/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.app.service;

import static org.codelibs.fess.app.service.SearchLogAggregationReader.termsBuckets;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exbhv.BadWordBhv;
import org.codelibs.fess.opensearch.config.exbhv.RelatedQueryBhv;
import org.codelibs.fess.opensearch.config.exentity.BadWord;
import org.codelibs.fess.opensearch.config.exentity.RelatedQuery;
import org.codelibs.fess.opensearch.log.allcommon.EsPagingResultBean;
import org.codelibs.fess.opensearch.log.cbean.cq.bs.BsSearchLogCQ;
import org.codelibs.fess.opensearch.log.exbhv.SearchLogBhv;
import org.codelibs.fess.opensearch.log.exentity.SearchLog;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fesen.opensearch.search.aggregations.Aggregations;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.Terms;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import jakarta.annotation.Resource;

/**
 * Generates related queries from the query refinements recorded in the search log.
 *
 * <p>A refinement is a search B made by the same user session within a short interval after a
 * search A (e.g. a typo followed by its correction). For each frequently searched term A, the
 * follow-up searches B of the sessions that searched A are counted by distinct session, and the
 * most common ones become the related queries of A. Related queries are public and expand every
 * search for their term, so the generation is conservative: only plain queries are used, only
 * search logs whose roles are allowed by {@code suggest.search.log.permissions} are read, bad words
 * are excluded and terms that already have related queries are never changed.</p>
 */
public class RelatedQueryGenerationService {

    private static final Logger logger = LogManager.getLogger(RelatedQueryGenerationService.class);

    /** Guards against concurrent runs. Services are prototype components, so the flag is shared by all instances. */
    protected static final AtomicBoolean RUNNING = new AtomicBoolean(false);

    /** Maximum number of virtual hosts aggregated. */
    protected static final int VIRTUAL_HOST_SIZE = 100;

    /** Factor applied to the term size when aggregating candidate terms, to leave room for the ones filtered out. */
    protected static final int SEED_TERM_FACTOR = 3;

    /** Characters that make a search word a query expression rather than a plain query. */
    protected static final String RESERVED_CHARS = ":\"()\\*?{}[]~^!/";

    private static final String VIRTUAL_HOSTS = "virtualHosts";

    private static final String SEARCH_WORDS = "searchWords";

    private static final String KEY_SEPARATOR = "\n";

    /** Behavior for the search log index. */
    @Resource
    protected SearchLogBhv searchLogBhv;

    /** Behavior for the related query index. */
    @Resource
    protected RelatedQueryBhv relatedQueryBhv;

    /** Behavior for the bad word index. */
    @Resource
    protected BadWordBhv badWordBhv;

    /** Fess configuration. */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Default constructor.
     */
    public RelatedQueryGenerationService() {
        // nothing
    }

    // ===================================================================================
    //                                                                            Generate
    //                                                                            ========

    /**
     * Generates related queries from the search log and stores them. Terms that already have
     * related queries (for the same virtual host) are skipped, and no more entries are created than
     * the related query cache can load ({@code page.relatedquery.max.fetch.size}).
     *
     * @param username the user name recorded as the creator of the generated entries
     * @return the result; its status tells whether the generation ran
     */
    public GenerationResult generate(final String username) {
        if (!fessConfig.isSearchLog() || !fessConfig.isUserInfo()) {
            return new GenerationResult(Status.DISABLED, 0, 0);
        }
        if (!RUNNING.compareAndSet(false, true)) {
            return new GenerationResult(Status.IN_PROGRESS, 0, 0);
        }
        try {
            return doGenerate(username);
        } finally {
            RUNNING.set(false);
        }
    }

    /**
     * Runs the generation.
     *
     * @param username the user name recorded as the creator of the generated entries
     * @return the result
     */
    protected GenerationResult doGenerate(final String username) {
        final long startTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        final Settings settings = loadSettings();
        final LocalDateTime from = ComponentUtil.getSystemHelper().getCurrentTimeAsLocalDateTime().minusDays(settings.days());

        final List<RelatedQuery> existingList = fetchExistingRelatedQueries();
        final Set<String> existingKeys = new HashSet<>();
        for (final RelatedQuery entity : existingList) {
            if (StringUtil.isNotBlank(entity.getTerm())) {
                existingKeys.add(toKey(entity.getVirtualHost(), entity.getTerm()));
            }
        }
        final int capacity = fessConfig.getPageRelatedqueryMaxFetchSizeAsInteger() - existingList.size();
        if (capacity <= 0) {
            logger.info("Skipped related query generation because the related query limit is reached: existing={}, capacity={}",
                    existingList.size(), capacity);
            return new GenerationResult(Status.GENERATED, 0, 0);
        }

        final List<String> badWords = loadBadWords();
        final SeedSelection selection = selectSeeds(fetchSeeds(from, settings), existingKeys, badWords, settings);

        final List<RelatedQuery> entityList = new ArrayList<>();
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        int fewSessions = 0;
        int noQueries = 0;
        for (final Seed seed : selection.seeds()) {
            if (entityList.size() >= capacity) {
                break;
            }
            final Map<String, List<LocalDateTime>> seedTimes = groupSeedTimes(fetchSeedLogs(seed, from, settings), settings);
            if (seedTimes.size() < settings.minSessions()) {
                fewSessions++;
                continue;
            }
            final List<String> queries =
                    collectRelated(seed.normalized(), seedTimes, fetchFollowLogs(seed, seedTimes, settings), badWords, settings);
            if (queries.isEmpty()) {
                noQueries++;
                continue;
            }
            entityList.add(createRelatedQuery(seed, queries, username, now));
        }

        if (!entityList.isEmpty()) {
            insert(entityList);
        }

        logger.info(
                "Generated related queries from search logs: created={}, skippedExisting={}, seeds={}, fewSessions={}, noQueries={}, capacity={}, elapsed={}ms",
                entityList.size(), selection.skippedExisting(), selection.seeds().size(), fewSessions, noQueries, capacity,
                ComponentUtil.getSystemHelper().getCurrentTimeAsLong() - startTime);
        return new GenerationResult(Status.GENERATED, entityList.size(), selection.skippedExisting());
    }

    // ===================================================================================
    //                                                                         Data Access
    //                                                                         ===========

    /**
     * Loads the generation settings from the Fess configuration.
     *
     * @return the settings
     */
    protected Settings loadSettings() {
        return new Settings(fessConfig.getRelatedQueryGenerateDaysAsInteger(), fessConfig.getRelatedQueryGenerateTermSizeAsInteger(),
                fessConfig.getRelatedQueryGenerateQuerySizeAsInteger(), fessConfig.getRelatedQueryGenerateMinSessionsAsInteger(),
                Duration.ofMinutes(fessConfig.getRelatedQueryGenerateSessionIntervalAsInteger()),
                fessConfig.getRelatedQueryGenerateSeedLogSizeAsInteger(), fessConfig.getRelatedQueryGenerateSeedSessionSizeAsInteger(),
                fessConfig.getRelatedQueryGenerateLogFetchSizeAsInteger(), fessConfig.getRelatedQueryGenerateQueryMinLengthAsInteger(),
                fessConfig.getRelatedQueryGenerateQueryMaxLengthAsInteger());
    }

    /**
     * Returns the related queries that already exist, as loaded by the related query cache.
     *
     * @return the existing related queries
     */
    protected List<RelatedQuery> fetchExistingRelatedQueries() {
        return ComponentUtil.getRelatedQueryHelper().getAvailableRelatedQueryList();
    }

    /**
     * Loads the bad words, trimmed and lower-cased.
     *
     * @return the bad words
     */
    protected List<String> loadBadWords() {
        final List<String> list = new ArrayList<>();
        for (final BadWord badWord : badWordBhv.selectList(cb -> {
            cb.query().matchAll();
            cb.fetchFirst(fessConfig.getPageBadWordMaxFetchSizeAsInteger());
        })) {
            final String word = badWord.getSuggestWord();
            if (StringUtil.isNotBlank(word)) {
                list.add(word.trim().toLowerCase(Locale.ROOT));
            }
        }
        return list;
    }

    /**
     * Aggregates the most searched words per virtual host. Only searches with a user session are
     * counted and admin searches are excluded. Search logs without a virtual host are counted
     * for the default virtual host (an empty key).
     *
     * @param from the start of the period
     * @param settings the settings
     * @return the search words and their counts per virtual host key, most searched first
     */
    protected Map<String, List<WordCount>> fetchSeeds(final LocalDateTime from, final Settings settings) {
        final EsPagingResultBean<SearchLog> result = (EsPagingResultBean<SearchLog>) searchLogBhv.selectPage(cb -> {
            cb.fetchFirst(0);
            cb.query().setRequestedAt_GreaterEqual(from);
            cb.query().setUserSessionId_Exists();
            cb.query().setAccessType_NotTerm(Constants.SEARCH_LOG_ACCESS_TYPE_ADMIN);
            cb.aggregation().setVirtualHost_Terms(VIRTUAL_HOSTS, op -> {
                applyTermsSize(op, VIRTUAL_HOST_SIZE);
                op.missing(StringUtil.EMPTY);
            }, ca -> ca.setSearchWord_Terms(SEARCH_WORDS, op -> {
                applyTermsSize(op, settings.termSize() * SEED_TERM_FACTOR);
                op.minDocCount(settings.minSessions());
            }, null));
        });
        final Aggregations aggs = result.getAggregations();
        final Map<String, List<WordCount>> seeds = new LinkedHashMap<>();
        for (final Terms.Bucket hostBucket : termsBuckets(aggs, VIRTUAL_HOSTS)) {
            final List<WordCount> words = seeds.computeIfAbsent(toHostKey(hostBucket.getKeyAsString()), k -> new ArrayList<>());
            for (final Terms.Bucket wordBucket : termsBuckets(hostBucket.getAggregations(), SEARCH_WORDS)) {
                words.add(new WordCount(wordBucket.getKeyAsString(), wordBucket.getDocCount()));
            }
        }
        return seeds;
    }

    /**
     * Fetches the most recent search logs of a seed term that have a user session.
     *
     * @param seed the seed term
     * @param from the start of the period
     * @param settings the settings
     * @return the search logs, most recent first
     */
    protected List<SearchLog> fetchSeedLogs(final Seed seed, final LocalDateTime from, final Settings settings) {
        return searchLogBhv.selectList(cb -> {
            cb.query().setSearchWord_InScope(seed.searchWords());
            setupVirtualHost(cb.query(), seed.virtualHost());
            cb.query().setRequestedAt_GreaterEqual(from);
            cb.query().setUserSessionId_Exists();
            cb.query().setAccessType_NotTerm(Constants.SEARCH_LOG_ACCESS_TYPE_ADMIN);
            cb.query().addOrderBy_RequestedAt_Desc();
            cb.specify().columnUserSessionId();
            cb.specify().columnRequestedAt();
            cb.specify().columnRoles();
            cb.fetchFirst(settings.seedLogSize());
        });
    }

    /**
     * Fetches the searches with hits that the sessions of a seed term made between the first seed
     * search and the end of the interval after the last one.
     *
     * @param seed the seed term
     * @param seedTimes the times of the seed searches per session
     * @param settings the settings
     * @return the search logs
     */
    protected List<SearchLog> fetchFollowLogs(final Seed seed, final Map<String, List<LocalDateTime>> seedTimes, final Settings settings) {
        LocalDateTime min = null;
        LocalDateTime max = null;
        for (final List<LocalDateTime> times : seedTimes.values()) {
            for (final LocalDateTime time : times) {
                if (min == null || time.isBefore(min)) {
                    min = time;
                }
                if (max == null || time.isAfter(max)) {
                    max = time;
                }
            }
        }
        if (min == null) {
            return new ArrayList<>();
        }
        final LocalDateTime start = min;
        final LocalDateTime end = max.plus(settings.window());
        return searchLogBhv.selectList(cb -> {
            cb.query().setUserSessionId_InScope(seedTimes.keySet());
            setupVirtualHost(cb.query(), seed.virtualHost());
            cb.query().setRequestedAt_GreaterThan(start);
            cb.query().setRequestedAt_LessEqual(end);
            cb.query().setHitCount_GreaterThan(0L);
            cb.query().setAccessType_NotTerm(Constants.SEARCH_LOG_ACCESS_TYPE_ADMIN);
            cb.query().not(q -> q.setSearchWord_InScope(seed.searchWords()));
            cb.query().addOrderBy_RequestedAt_Asc();
            cb.specify().columnSearchWord();
            cb.specify().columnUserSessionId();
            cb.specify().columnRequestedAt();
            cb.specify().columnRoles();
            cb.specify().columnHitCount();
            cb.fetchFirst(settings.logFetchSize());
        });
    }

    /**
     * Stores the generated related queries and reloads the related query cache.
     *
     * @param entityList the related queries to store
     */
    protected void insert(final List<RelatedQuery> entityList) {
        relatedQueryBhv.batchInsert(entityList, op -> op.setRefreshPolicy(Constants.TRUE));
        ComponentUtil.getRelatedQueryHelper().update();
    }

    /**
     * Checks whether a search log may become public vocabulary, with the same policy as the
     * suggest index ({@code suggest.search.log.permissions}).
     *
     * @param searchLog the search log
     * @return true if all roles of the search log are allowed
     */
    protected boolean isAllowed(final SearchLog searchLog) {
        return fessConfig.isValidSearchLogPermissions(searchLog.getRoles());
    }

    private void setupVirtualHost(final BsSearchLogCQ cq, final String virtualHost) {
        if (StringUtil.isNotBlank(virtualHost)) {
            cq.setVirtualHost_Term(virtualHost);
        } else {
            // the default virtual host is logged as an empty string; older logs may not have it at all
            cq.bool((must, should, mustNot, filter) -> {
                should.setVirtualHost_InScope(List.of(StringUtil.EMPTY));
                should.not(q -> q.setVirtualHost_Exists());
            }, op -> op.minimumShouldMatch(1));
        }
    }

    private void applyTermsSize(final TermsAggregationBuilder op, final int size) {
        op.size(size);
        final Integer shardSize = fessConfig.getSearchlogAggShardSizeAsInteger();
        if (shardSize != null && shardSize >= 0) {
            op.shardSize(shardSize);
        }
    }

    // ===================================================================================
    //                                                                         Pure Helper
    //                                                                         ===========

    /**
     * Selects the seed terms: plain, not bad and not existing search words, grouped by their
     * normalized form, at most {@code termSize} per virtual host.
     *
     * @param candidates the search words and their counts per virtual host key, most searched first
     * @param existingKeys the keys of the existing related queries (see {@link #toKey(String, String)})
     * @param badWords the bad words
     * @param settings the settings
     * @return the selected seeds and the number of existing terms skipped
     */
    protected SeedSelection selectSeeds(final Map<String, List<WordCount>> candidates, final Set<String> existingKeys,
            final List<String> badWords, final Settings settings) {
        final List<Seed> seeds = new ArrayList<>();
        int skippedExisting = 0;
        for (final Map.Entry<String, List<WordCount>> entry : candidates.entrySet()) {
            final String virtualHost = toHostKey(entry.getKey());
            final Map<String, List<String>> variants = new LinkedHashMap<>();
            final Map<String, String> displays = new HashMap<>();
            final Set<String> skipped = new HashSet<>();
            for (final WordCount candidate : entry.getValue()) {
                final String word = candidate.word();
                if (!isPlainQuery(word, settings)) {
                    continue;
                }
                final String display = StringUtils.normalizeSpace(word);
                final String normalized = display.toLowerCase(Locale.ROOT);
                if (isBadWord(normalized, badWords) || skipped.contains(normalized)) {
                    continue;
                }
                if (existingKeys.contains(toKey(virtualHost, normalized)) || existingKeys.contains(toKey(virtualHost, word))) {
                    skipped.add(normalized);
                    skippedExisting++;
                    continue;
                }
                final List<String> list = variants.get(normalized);
                if (list != null) {
                    list.add(word);
                } else if (variants.size() < settings.termSize()) {
                    final List<String> newList = new ArrayList<>();
                    newList.add(word);
                    variants.put(normalized, newList);
                    displays.put(normalized, display);
                }
            }
            variants.forEach((normalized, words) -> seeds.add(new Seed(virtualHost, displays.get(normalized), normalized, words)));
        }
        return new SeedSelection(seeds, skippedExisting);
    }

    /**
     * Groups the times of the seed searches by session, keeping the most recent sessions.
     * Search logs whose roles are not allowed are ignored.
     *
     * @param seedLogs the seed search logs, most recent first
     * @param settings the settings
     * @return the seed search times per session
     */
    protected Map<String, List<LocalDateTime>> groupSeedTimes(final List<SearchLog> seedLogs, final Settings settings) {
        final Map<String, List<LocalDateTime>> seedTimes = new LinkedHashMap<>();
        for (final SearchLog searchLog : seedLogs) {
            final String sessionId = searchLog.getUserSessionId();
            final LocalDateTime requestedAt = searchLog.getRequestedAt();
            if (StringUtil.isBlank(sessionId) || requestedAt == null || !isAllowed(searchLog)) {
                continue;
            }
            final List<LocalDateTime> times = seedTimes.get(sessionId);
            if (times != null) {
                times.add(requestedAt);
            } else if (seedTimes.size() < settings.seedSessionSize()) {
                final List<LocalDateTime> newTimes = new ArrayList<>();
                newTimes.add(requestedAt);
                seedTimes.put(sessionId, newTimes);
            }
        }
        return seedTimes;
    }

    /**
     * Collects the related queries of a seed term: the follow-up searches made by a session within
     * the interval after one of its seed searches, counted by distinct session. A follow-up search
     * counts only if it has hits, its roles are allowed, it is a plain query, it differs from the
     * seed term (ignoring case and spaces) and it contains no bad word. Queries searched by fewer
     * than {@code minSessions} sessions are dropped; the rest are ordered by the number of sessions
     * (descending) and then alphabetically, and each is returned in its most frequent spelling.
     *
     * @param seedNormalized the normalized seed term
     * @param seedTimes the seed search times per session
     * @param followLogs the follow-up search logs
     * @param badWords the bad words
     * @param settings the settings
     * @return the related queries, at most {@code querySize}
     */
    protected List<String> collectRelated(final String seedNormalized, final Map<String, List<LocalDateTime>> seedTimes,
            final List<SearchLog> followLogs, final List<String> badWords, final Settings settings) {
        final Map<String, Set<String>> sessionsByQuery = new HashMap<>();
        final Map<String, Map<String, Integer>> spellingsByQuery = new HashMap<>();
        for (final SearchLog searchLog : followLogs) {
            final String sessionId = searchLog.getUserSessionId();
            final List<LocalDateTime> times = sessionId == null ? null : seedTimes.get(sessionId);
            final LocalDateTime requestedAt = searchLog.getRequestedAt();
            final Long hitCount = searchLog.getHitCount();
            if (times == null || requestedAt == null || hitCount == null || hitCount <= 0 || !isAllowed(searchLog)) {
                continue;
            }
            final String word = searchLog.getSearchWord();
            if (!isPlainQuery(word, settings)) {
                continue;
            }
            final String display = StringUtils.normalizeSpace(word);
            final String normalized = display.toLowerCase(Locale.ROOT);
            if (normalized.equals(seedNormalized) || isBadWord(normalized, badWords)
                    || !isWithinInterval(times, requestedAt, settings.window())) {
                continue;
            }
            sessionsByQuery.computeIfAbsent(normalized, k -> new HashSet<>()).add(sessionId);
            spellingsByQuery.computeIfAbsent(normalized, k -> new HashMap<>()).merge(display, 1, Integer::sum);
        }

        record Candidate(String query, int sessions) {
        }
        final List<Candidate> candidates = new ArrayList<>();
        sessionsByQuery.forEach((normalized, sessions) -> {
            if (sessions.size() >= settings.minSessions()) {
                candidates.add(new Candidate(selectSpelling(spellingsByQuery.get(normalized)), sessions.size()));
            }
        });
        candidates.sort(Comparator.comparingInt(Candidate::sessions).reversed().thenComparing(Candidate::query));
        return candidates.stream().limit(settings.querySize()).map(Candidate::query).toList();
    }

    /**
     * Creates a related query entity.
     *
     * @param seed the seed term
     * @param queries the related queries
     * @param username the creator
     * @param now the current time
     * @return the entity
     */
    protected RelatedQuery createRelatedQuery(final Seed seed, final List<String> queries, final String username, final long now) {
        final RelatedQuery entity = new RelatedQuery();
        entity.setTerm(seed.term());
        entity.setQueries(queries.toArray(new String[queries.size()]));
        entity.setVirtualHost(seed.virtualHost());
        entity.setCreatedBy(username);
        entity.setCreatedTime(now);
        entity.setUpdatedBy(username);
        entity.setUpdatedTime(now);
        return entity;
    }

    /**
     * Checks whether a search word is a plain query typed by a user rather than a query built by
     * Fess (related query expansion, label filters, sort) or a query expression: it must not
     * contain reserved characters, operators ({@code AND}, {@code OR}, {@code NOT}, {@code &&},
     * {@code ||}) or tokens starting with {@code +} or {@code -}, and its length (in code points,
     * after normalizing spaces) must be within the configured range.
     *
     * @param word the search word
     * @param settings the settings
     * @return true if the search word is a plain query
     */
    protected boolean isPlainQuery(final String word, final Settings settings) {
        if (word == null) {
            return false;
        }
        final String value = StringUtils.normalizeSpace(word);
        if (StringUtil.isBlank(value)) {
            return false;
        }
        final int length = value.codePointCount(0, value.length());
        if (length < settings.minLength() || length > settings.maxLength()) {
            return false;
        }
        if (StringUtils.containsAny(value, RESERVED_CHARS) || value.contains("&&") || value.contains("||")) {
            return false;
        }
        for (final String token : value.split(" ")) {
            if (token.startsWith("-") || token.startsWith("+") || "AND".equals(token) || "OR".equals(token) || "NOT".equals(token)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether a text contains a bad word (case-insensitive substring match, as in the suggest index).
     *
     * @param text the text
     * @param badWords the trimmed and lower-cased bad words
     * @return true if the text contains a bad word
     */
    protected static boolean isBadWord(final String text, final Collection<String> badWords) {
        final String value = text.toLowerCase(Locale.ROOT);
        for (final String badWord : badWords) {
            if (value.contains(badWord)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a time falls within the interval after one of the seed times:
     * {@code seed < time <= seed + window}.
     *
     * @param seedTimes the seed times
     * @param time the time
     * @param window the interval
     * @return true if the time is within the interval
     */
    protected static boolean isWithinInterval(final List<LocalDateTime> seedTimes, final LocalDateTime time, final Duration window) {
        for (final LocalDateTime seedTime : seedTimes) {
            if (time.isAfter(seedTime) && !time.isAfter(seedTime.plus(window))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the most frequent spelling, the lexicographically smallest one on a tie.
     *
     * @param spellings the spellings and their counts
     * @return the spelling
     */
    protected static String selectSpelling(final Map<String, Integer> spellings) {
        String selected = null;
        int max = 0;
        for (final Map.Entry<String, Integer> entry : spellings.entrySet()) {
            final int count = entry.getValue();
            if (selected == null || count > max || count == max && entry.getKey().compareTo(selected) < 0) {
                selected = entry.getKey();
                max = count;
            }
        }
        return selected;
    }

    /**
     * Returns the key of a term, in the same form as the related query cache: the virtual host key
     * (empty for the default host) and the lower-cased term.
     *
     * @param virtualHost the virtual host
     * @param term the term
     * @return the key
     */
    protected static String toKey(final String virtualHost, final String term) {
        return toHostKey(virtualHost) + KEY_SEPARATOR + term.toLowerCase(Locale.ROOT);
    }

    /**
     * Returns the virtual host key, empty for the default host.
     *
     * @param virtualHost the virtual host
     * @return the key
     */
    protected static String toHostKey(final String virtualHost) {
        return StringUtil.isBlank(virtualHost) ? StringUtil.EMPTY : virtualHost;
    }

    // ===================================================================================
    //                                                                        Result/Model
    //                                                                        ============

    /**
     * The status of a generation request.
     */
    public enum Status {
        /** The generation ran. */
        GENERATED,
        /** The search log or the user info is disabled, so there are no sessions to read. */
        DISABLED,
        /** Another generation is running. */
        IN_PROGRESS
    }

    /**
     * The result of a generation request.
     *
     * @param status the status
     * @param created the number of related queries created
     * @param skipped the number of candidate terms skipped because they already have related queries
     */
    public record GenerationResult(Status status, int created, int skipped) {
    }

    /**
     * The generation settings.
     *
     * @param days the number of days of search logs read
     * @param termSize the maximum number of terms per virtual host
     * @param querySize the maximum number of related queries per term
     * @param minSessions the minimum number of distinct sessions
     * @param window the interval after a search in which a follow-up search counts
     * @param seedLogSize the maximum number of search logs read per term
     * @param seedSessionSize the maximum number of sessions read per term
     * @param logFetchSize the maximum number of follow-up search logs read per term
     * @param minLength the minimum length of a term or query
     * @param maxLength the maximum length of a term or query
     */
    public record Settings(int days, int termSize, int querySize, int minSessions, Duration window, int seedLogSize, int seedSessionSize,
            int logFetchSize, int minLength, int maxLength) {
    }

    /**
     * A search word and its number of searches.
     *
     * @param word the search word
     * @param count the number of searches
     */
    public record WordCount(String word, long count) {
    }

    /**
     * A seed term.
     *
     * @param virtualHost the virtual host key (empty for the default host)
     * @param term the term to store (its most searched spelling, spaces normalized)
     * @param normalized the normalized term (spaces normalized, lower-cased)
     * @param searchWords the search words of the term as logged
     */
    public record Seed(String virtualHost, String term, String normalized, List<String> searchWords) {
    }

    /**
     * The selected seeds.
     *
     * @param seeds the seeds
     * @param skippedExisting the number of terms skipped because they already have related queries
     */
    public record SeedSelection(List<Seed> seeds, int skippedExisting) {
    }
}
