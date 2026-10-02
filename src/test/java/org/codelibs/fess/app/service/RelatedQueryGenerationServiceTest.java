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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.codelibs.fess.app.service.RelatedQueryGenerationService.GenerationResult;
import org.codelibs.fess.app.service.RelatedQueryGenerationService.Seed;
import org.codelibs.fess.app.service.RelatedQueryGenerationService.SeedSelection;
import org.codelibs.fess.app.service.RelatedQueryGenerationService.Settings;
import org.codelibs.fess.app.service.RelatedQueryGenerationService.Status;
import org.codelibs.fess.app.service.RelatedQueryGenerationService.WordCount;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exentity.RelatedQuery;
import org.codelibs.fess.opensearch.log.exentity.SearchLog;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class RelatedQueryGenerationServiceTest extends UnitFessTestCase {

    private static final String GUEST = "Rguest";

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 9, 20, 12, 0);

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 1, 12, 0);

    private MockFessConfig fessConfig;

    private TestService service;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        fessConfig = new MockFessConfig();
        ComponentUtil.setFessConfig(fessConfig);
        ComponentUtil.register(new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return 1_000_000L;
            }

            @Override
            public LocalDateTime getCurrentTimeAsLocalDateTime() {
                return NOW;
            }
        }, "systemHelper");
        service = new TestService();
        service.fessConfig = fessConfig;
        RelatedQueryGenerationService.RUNNING.set(false);
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        RelatedQueryGenerationService.RUNNING.set(false);
        super.tearDown(testInfo);
    }

    // ===================================================================================
    //                                                                         Plain Query
    //                                                                         ===========

    @Test
    public void test_isPlainQuery_accepted() {
        final Settings settings = settings();
        for (final String word : List.of("java", "java tutorial", "Java   Tutorial", "c++", "java or python", "not found", "AT&T", "e-mail",
                "全文検索", "𠮷野家")) {
            assertTrue(service.isPlainQuery(word, settings), word);
        }
    }

    @Test
    public void test_isPlainQuery_rejected() {
        final Settings settings = settings();
        final List<String> words = new ArrayList<>(
                List.of("", "   ", "a", "x".repeat(51), "title:java", "\"java tutorial\"", "(java OR python)", "java OR python",
                        "java AND python", "NOT java", "-java", "java +tutorial", "java*", "jav?", "a && b", "a || b", "!java",
                        "java sort:score", "java label:\"news\"", "java\\:x", "{a TO b}", "[a TO b]", "java~", "java^2", "/jav.*/"));
        words.add(null);
        for (final String word : words) {
            assertFalse(service.isPlainQuery(word, settings), String.valueOf(word));
        }
    }

    @Test
    public void test_isPlainQuery_lengthInCodePoints() {
        final Settings settings = new Settings(30, 100, 5, 3, Duration.ofMinutes(10), 1000, 200, 2000, 2, 3);
        assertTrue(service.isPlainQuery("𠮷野家", settings));
        assertFalse(service.isPlainQuery("𠮷野家a", settings));
        assertTrue(service.isPlainQuery("a  b", settings), "length is measured after normalizing spaces");
    }

    // ===================================================================================
    //                                                                         Pure Helper
    //                                                                         ===========

    @Test
    public void test_isWithinInterval() {
        final Duration window = Duration.ofMinutes(10);
        final List<LocalDateTime> seeds = List.of(BASE, BASE.plusHours(1));
        assertFalse(RelatedQueryGenerationService.isWithinInterval(seeds, BASE, window), "same time");
        assertFalse(RelatedQueryGenerationService.isWithinInterval(seeds, BASE.minusSeconds(1), window), "before");
        assertTrue(RelatedQueryGenerationService.isWithinInterval(seeds, BASE.plusSeconds(1), window));
        assertTrue(RelatedQueryGenerationService.isWithinInterval(seeds, BASE.plusMinutes(10), window), "end is inclusive");
        assertFalse(RelatedQueryGenerationService.isWithinInterval(seeds, BASE.plusMinutes(10).plusSeconds(1), window));
        assertTrue(RelatedQueryGenerationService.isWithinInterval(seeds, BASE.plusMinutes(65), window), "second seed");
    }

    @Test
    public void test_selectSpelling() {
        final Map<String, Integer> spellings = new HashMap<>();
        spellings.put("Java", 2);
        spellings.put("java", 3);
        assertEquals("java", RelatedQueryGenerationService.selectSpelling(spellings));
        spellings.put("JAVA", 3);
        assertEquals("tie -> lexicographic", "JAVA", RelatedQueryGenerationService.selectSpelling(spellings));
    }

    @Test
    public void test_isBadWord() {
        assertTrue(RelatedQueryGenerationService.isBadWord("Free Casino Bonus", List.of("casino")));
        assertFalse(RelatedQueryGenerationService.isBadWord("java", List.of("casino")));
        assertFalse(RelatedQueryGenerationService.isBadWord("java", List.of()));
    }

    @Test
    public void test_toKey() {
        assertEquals("\njava", RelatedQueryGenerationService.toKey(null, "Java"));
        assertEquals("\njava", RelatedQueryGenerationService.toKey(" ", "JAVA"));
        assertEquals("host1\njava", RelatedQueryGenerationService.toKey("host1", "java"));
    }

    // ===================================================================================
    //                                                                        Select Seeds
    //                                                                        ============

    @Test
    public void test_selectSeeds() {
        final Map<String, List<WordCount>> candidates = new LinkedHashMap<>();
        candidates.put("",
                List.of(new WordCount("Java", 10), new WordCount("(java OR jdk)", 9), new WordCount("java", 8),
                        new WordCount("casino tips", 7), new WordCount("python", 6), new WordCount("Python", 5), new WordCount("rust", 4),
                        new WordCount("golang", 3)));
        candidates.put("host1", List.of(new WordCount("java", 5)));
        final Settings settings = new Settings(30, 3, 5, 3, Duration.ofMinutes(10), 1000, 200, 2000, 2, 50);

        final SeedSelection selection = service.selectSeeds(candidates, Set.of("\npython"), List.of("casino"), settings);

        assertEquals(1, selection.skippedExisting(), "python and Python count once");
        final List<Seed> seeds = selection.seeds();
        assertEquals(4, seeds.size());
        assertEquals(new Seed("", "Java", "java", List.of("Java", "java")), seeds.get(0));
        assertEquals("rust", seeds.get(1).term());
        assertEquals("golang", seeds.get(2).term());
        assertEquals(new Seed("host1", "java", "java", List.of("java")), seeds.get(3));
    }

    @Test
    public void test_selectSeeds_termSizePerVirtualHost() {
        final Map<String, List<WordCount>> candidates = new LinkedHashMap<>();
        candidates.put("", List.of(new WordCount("a1", 9), new WordCount("a2", 8), new WordCount("a3", 7)));
        final Settings settings = new Settings(30, 2, 5, 3, Duration.ofMinutes(10), 1000, 200, 2000, 2, 50);
        final List<String> terms = service.selectSeeds(candidates, Set.of(), List.of(), settings).seeds().stream().map(Seed::term).toList();
        assertEquals(List.of("a1", "a2"), terms);
    }

    // ===================================================================================
    //                                                                     Group Seed Logs
    //                                                                     ===============

    @Test
    public void test_groupSeedTimes() {
        final Settings settings = new Settings(30, 100, 5, 3, Duration.ofMinutes(10), 1000, 2, 2000, 2, 50);
        final List<SearchLog> logs = List.of(log("s1", "java", 30), log("s2", "java", 20), log("s1", "java", 15),
                logWithRoles("s3", "java", 12, "Radmin"), log("s4", "java", 10), log(null, "java", 5));
        final Map<String, List<LocalDateTime>> times = service.groupSeedTimes(logs, settings);
        assertEquals(List.of("s1", "s2"), new ArrayList<>(times.keySet()));
        assertEquals(List.of(BASE.plusMinutes(30), BASE.plusMinutes(15)), times.get("s1"));
    }

    // ===================================================================================
    //                                                                     Collect Related
    //                                                                     ===============

    @Test
    public void test_collectRelated_distinctSessionsAndOrder() {
        final Map<String, List<LocalDateTime>> seedTimes = seedTimes("s1", "s2", "s3", "s4");
        final List<SearchLog> follow = new ArrayList<>();
        // "javascript": 4 sessions, s1 searches it twice
        for (final String s : List.of("s1", "s1", "s2", "s3", "s4")) {
            follow.add(log(s, "javascript", 2));
        }
        // "java tutorial" and "jdk": 3 sessions each -> alphabetical
        for (final String s : List.of("s1", "s2", "s3")) {
            follow.add(log(s, "jdk", 3));
            follow.add(log(s, "java tutorial", 4));
        }
        // "kotlin": 2 sessions -> below minSessions
        follow.add(log("s1", "kotlin", 5));
        follow.add(log("s2", "kotlin", 5));
        // "scala": one session many times -> below minSessions
        for (int i = 0; i < 5; i++) {
            follow.add(log("s4", "scala", 1 + i));
        }

        assertEquals(List.of("javascript", "java tutorial", "jdk"),
                service.collectRelated("java", seedTimes, follow, List.of(), settings()));

        final Settings two = new Settings(30, 100, 2, 3, Duration.ofMinutes(10), 1000, 200, 2000, 2, 50);
        assertEquals(List.of("javascript", "java tutorial"), service.collectRelated("java", seedTimes, follow, List.of(), two));
    }

    @Test
    public void test_collectRelated_windowAndOrder() {
        final Map<String, List<LocalDateTime>> seedTimes = seedTimes("s1", "s2", "s3");
        final List<SearchLog> follow = new ArrayList<>();
        follow.add(log("s1", "after", 10));
        follow.add(log("s2", "after", 1));
        follow.add(log("s3", "after", 0)); // same time as the seed: not after it
        follow.add(log("s1", "late", 11));
        follow.add(log("s2", "late", 11));
        follow.add(log("s3", "late", 11));
        follow.add(log("s1", "before", -1));
        follow.add(log("s2", "before", -1));
        follow.add(log("s3", "before", -1));
        follow.add(log("s9", "other", 1)); // not a seed session
        follow.add(log("s9", "other", 1));
        follow.add(log("s9", "other", 1));
        assertEquals(List.of(), service.collectRelated("java", seedTimes, follow, List.of(), settings()));

        follow.add(log("s3", "after", 5));
        assertEquals(List.of("after"), service.collectRelated("java", seedTimes, follow, List.of(), settings()));
    }

    @Test
    public void test_collectRelated_filters() {
        final Map<String, List<LocalDateTime>> seedTimes = seedTimes("s1", "s2", "s3");
        final List<SearchLog> follow = new ArrayList<>();
        for (final String s : List.of("s1", "s2", "s3")) {
            follow.add(log(s, "JAVA", 1)); // same as the seed ignoring case
            follow.add(log(s, " java ", 1)); // same as the seed ignoring spaces
            follow.add(logWithRoles(s, "secret project", 2, GUEST, "Rsecret")); // a role is not allowed
            follow.add(logWithRoles(s, "no roles", 2)); // empty roles: allowed, as in the suggest index
            follow.add(logWithHits(s, "zero hits", 2, 0L));
            follow.add(log(s, "java sort:score", 2)); // built by Fess
            follow.add(log(s, "casino tips", 2)); // bad word
            follow.add(log(s, "ok", 3));
        }
        final SearchLog nullRoles = log("s1", "null roles", 2);
        nullRoles.setRoles(null);
        follow.add(nullRoles);
        assertEquals(List.of("no roles", "ok"), service.collectRelated("java", seedTimes, follow, List.of("casino"), settings()));
    }

    @Test
    public void test_collectRelated_mostFrequentSpelling() {
        final Map<String, List<LocalDateTime>> seedTimes = seedTimes("s1", "s2", "s3");
        final List<SearchLog> follow = new ArrayList<>();
        follow.add(log("s1", "JavaScript", 1));
        follow.add(log("s2", "javascript", 1));
        follow.add(log("s3", "javascript", 1));
        follow.add(log("s3", "JavaScript  ", 2));
        follow.add(log("s3", "JAVASCRIPT", 3));
        follow.add(log("s1", "JavaScript", 4));
        assertEquals(List.of("JavaScript"), service.collectRelated("java", seedTimes, follow, List.of(), settings()));
    }

    // ===================================================================================
    //                                                                            Generate
    //                                                                            ========

    @Test
    public void test_generate() {
        refinements("", "jvaa", "java", "s1", "s2", "s3");
        refinements("", "jvaa", "jdk", "s1", "s2");
        service.logs.add(log("s9", "jvaa", 100)); // a seed search with no refinement
        // admin searches are not read
        service.logs.add(logWithAccessType("a1", "jvaa", 0, "admin"));
        service.logs.add(logWithAccessType("a1", "jdk", 1, "admin"));
        service.logs.add(logWithAccessType("a2", "jvaa", 0, "admin"));
        service.logs.add(logWithAccessType("a2", "jdk", 1, "admin"));

        final GenerationResult result = service.generate("admin");

        assertEquals(Status.GENERATED, result.status());
        assertEquals(1, result.created());
        assertEquals(0, result.skipped());
        assertEquals(1, service.inserted.size());
        final RelatedQuery entity = service.inserted.get(0);
        assertEquals("jvaa", entity.getTerm());
        assertEquals(List.of("java"), Arrays.asList(entity.getQueries()));
        assertEquals("", hostKey(entity));
        assertEquals("admin", entity.getCreatedBy());
        assertEquals("admin", entity.getUpdatedBy());
        assertEquals(1_000_000L, entity.getCreatedTime().longValue());
        assertEquals(1_000_000L, entity.getUpdatedTime().longValue());
        assertFalse(RelatedQueryGenerationService.RUNNING.get());
    }

    @Test
    public void test_generate_virtualHostIsolation() {
        refinements("host1", "jvaa", "java", "h1", "h2", "h3");
        refinements("", "jvaa", "javascript", "d1", "d2", "d3");
        // the same session ids in another virtual host must not mix
        refinements("host2", "jvaa", "kotlin", "d1", "d2");

        final GenerationResult result = service.generate("admin");

        assertEquals(2, result.created());
        final Map<String, List<String>> byHost = service.inserted.stream()
                .collect(Collectors.toMap(RelatedQueryGenerationServiceTest::hostKey, e -> Arrays.asList(e.getQueries())));
        assertEquals(List.of("java"), byHost.get("host1"));
        assertEquals(List.of("javascript"), byHost.get(""));
        assertFalse(byHost.containsKey("host2"));
    }

    @Test
    public void test_generate_missingVirtualHostIsDefault() {
        refinements("", "jvaa", "java", "s1", "s2");
        final SearchLog seed = log("s3", "jvaa", 0);
        seed.setVirtualHost(null);
        final SearchLog follow = log("s3", "java", 1);
        follow.setVirtualHost(null);
        service.logs.add(seed);
        service.logs.add(follow);

        final GenerationResult result = service.generate("admin");

        assertEquals(1, result.created());
        assertEquals("", hostKey(service.inserted.get(0)));
    }

    @Test
    public void test_generate_existingTermsSkipped() {
        refinements("", "jvaa", "java", "s1", "s2", "s3");
        refinements("", "pyhton", "python", "s1", "s2", "s3");
        refinements("host1", "pyhton", "python", "s1", "s2", "s3");
        service.existing.add(relatedQuery("JVAA", "", "java8"));
        service.existing.add(relatedQuery("pyhton", "host1", "py"));

        final GenerationResult result = service.generate("admin");

        assertEquals(1, result.created());
        assertEquals(2, result.skipped());
        assertEquals("pyhton", service.inserted.get(0).getTerm());
        assertEquals("", hostKey(service.inserted.get(0)));
    }

    @Test
    public void test_generate_capacity() {
        refinements("", "jvaa", "java", "s1", "s2", "s3", "s4");
        refinements("", "pyhton", "python", "s1", "s2", "s3");
        fessConfig.maxFetchSize = 3;
        service.existing.add(relatedQuery("a", "", "b"));
        service.existing.add(relatedQuery("c", "", "d"));

        final GenerationResult result = service.generate("admin");

        assertEquals(1, result.created());
        assertEquals("the first term with related queries", "jvaa", service.inserted.get(0).getTerm());

        service.inserted.clear();
        service.insertCount = 0;
        service.existing.add(relatedQuery("e", "", "f"));
        final GenerationResult full = service.generate("admin");
        assertEquals(Status.GENERATED, full.status());
        assertEquals(0, full.created());
        assertEquals(0, service.inserted.size());
        assertEquals(0, service.insertCount);
    }

    @Test
    public void test_generate_nothingToInsert() {
        refinements("", "jvaa", "java", "s1", "s2");
        final GenerationResult result = service.generate("admin");
        assertEquals(Status.GENERATED, result.status());
        assertEquals(0, result.created());
        assertEquals(0, service.insertCount);
    }

    @Test
    public void test_generate_disabled() {
        refinements("", "jvaa", "java", "s1", "s2", "s3");
        fessConfig.searchLog = false;
        assertEquals(Status.DISABLED, service.generate("admin").status());
        fessConfig.searchLog = true;
        fessConfig.userInfo = false;
        assertEquals(Status.DISABLED, service.generate("admin").status());
        assertEquals(0, service.insertCount);
        assertEquals(0, service.seedFetchCount);
    }

    @Test
    public void test_generate_inProgress() {
        refinements("", "jvaa", "java", "s1", "s2", "s3");
        RelatedQueryGenerationService.RUNNING.set(true);
        assertEquals(Status.IN_PROGRESS, service.generate("admin").status());
        assertEquals(0, service.seedFetchCount);
        assertTrue(RelatedQueryGenerationService.RUNNING.get(), "the flag of the running generation is kept");

        RelatedQueryGenerationService.RUNNING.set(false);
        assertEquals(Status.GENERATED, service.generate("admin").status());
    }

    @Test
    public void test_generate_flagResetOnFailure() {
        refinements("", "jvaa", "java", "s1", "s2", "s3");
        service.failOnInsert = true;
        try {
            service.generate("admin");
            fail("expected a failure");
        } catch (final IllegalStateException e) {
            // expected
        }
        assertFalse(RelatedQueryGenerationService.RUNNING.get());
    }

    // ===================================================================================
    //                                                                        Test Support
    //                                                                        ============

    private static Settings settings() {
        return new Settings(30, 100, 5, 3, Duration.ofMinutes(10), 1000, 200, 2000, 2, 50);
    }

    /** Sessions that searched "java" at BASE. */
    private static Map<String, List<LocalDateTime>> seedTimes(final String... sessions) {
        final Map<String, List<LocalDateTime>> map = new LinkedHashMap<>();
        for (final String s : sessions) {
            map.put(s, new ArrayList<>(List.of(BASE)));
        }
        return map;
    }

    /** Each session searches seed at BASE and then follow one minute later. */
    private void refinements(final String virtualHost, final String seed, final String follow, final String... sessions) {
        for (final String s : sessions) {
            final SearchLog seedLog = log(s, seed, 0);
            seedLog.setVirtualHost(virtualHost);
            seedLog.setHitCount(0L);
            service.logs.add(seedLog);
            final SearchLog followLog = log(s, follow, 1);
            followLog.setVirtualHost(virtualHost);
            service.logs.add(followLog);
        }
    }

    private static SearchLog log(final String session, final String word, final int minutes) {
        return logWithRoles(session, word, minutes, GUEST);
    }

    private static SearchLog logWithRoles(final String session, final String word, final int minutes, final String... roles) {
        final SearchLog log = new SearchLog();
        log.setUserSessionId(session);
        log.setSearchWord(word);
        log.setRequestedAt(BASE.plusMinutes(minutes));
        log.setHitCount(10L);
        log.setRoles(roles);
        log.setVirtualHost("");
        log.setAccessType("web");
        return log;
    }

    private static SearchLog logWithHits(final String session, final String word, final int minutes, final Long hits) {
        final SearchLog log = log(session, word, minutes);
        log.setHitCount(hits);
        return log;
    }

    private static SearchLog logWithAccessType(final String session, final String word, final int minutes, final String accessType) {
        final SearchLog log = log(session, word, minutes);
        log.setAccessType(accessType);
        return log;
    }

    private static RelatedQuery relatedQuery(final String term, final String virtualHost, final String... queries) {
        final RelatedQuery entity = new RelatedQuery();
        entity.setTerm(term);
        entity.setVirtualHost(virtualHost);
        entity.setQueries(queries);
        return entity;
    }

    private static String hostKey(final SearchLog log) {
        return RelatedQueryGenerationService.toHostKey(log.getVirtualHost());
    }

    private static String hostKey(final RelatedQuery entity) {
        return RelatedQueryGenerationService.toHostKey(entity.getVirtualHost());
    }

    /**
     * Replaces the OpenSearch access with an in-memory search log that applies the same
     * conditions as the queries of the service.
     */
    static class TestService extends RelatedQueryGenerationService {
        final List<SearchLog> logs = new ArrayList<>();
        final List<RelatedQuery> existing = new ArrayList<>();
        final List<RelatedQuery> inserted = new ArrayList<>();
        int insertCount;
        int seedFetchCount;
        boolean failOnInsert;

        @Override
        protected List<RelatedQuery> fetchExistingRelatedQueries() {
            return existing;
        }

        @Override
        protected List<String> loadBadWords() {
            return List.of("casino");
        }

        @Override
        protected Map<String, List<WordCount>> fetchSeeds(final LocalDateTime from, final Settings settings) {
            seedFetchCount++;
            final Map<String, Map<String, Long>> counts = new LinkedHashMap<>();
            for (final SearchLog log : logs) {
                if (log.getUserSessionId() != null && !log.getRequestedAt().isBefore(from) && !"admin".equals(log.getAccessType())) {
                    counts.computeIfAbsent(hostKey(log), k -> new HashMap<>()).merge(log.getSearchWord(), 1L, Long::sum);
                }
            }
            final Map<String, List<WordCount>> seeds = new LinkedHashMap<>();
            counts.forEach((host, words) -> seeds.put(host,
                    words.entrySet()
                            .stream()
                            .filter(e -> e.getValue() >= settings.minSessions())
                            .sorted(Map.Entry.<String, Long> comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                            .limit(settings.termSize() * SEED_TERM_FACTOR)
                            .map(e -> new WordCount(e.getKey(), e.getValue()))
                            .toList()));
            return seeds;
        }

        @Override
        protected List<SearchLog> fetchSeedLogs(final Seed seed, final LocalDateTime from, final Settings settings) {
            return logs.stream()
                    .filter(log -> seed.searchWords().contains(log.getSearchWord()) && hostKey(log).equals(seed.virtualHost())
                            && !log.getRequestedAt().isBefore(from) && log.getUserSessionId() != null
                            && !"admin".equals(log.getAccessType()))
                    .sorted(Comparator.comparing(SearchLog::getRequestedAt).reversed())
                    .limit(settings.seedLogSize())
                    .toList();
        }

        @Override
        protected List<SearchLog> fetchFollowLogs(final Seed seed, final Map<String, List<LocalDateTime>> seedTimes,
                final Settings settings) {
            final LocalDateTime min = seedTimes.values().stream().flatMap(List::stream).min(Comparator.naturalOrder()).orElseThrow();
            final LocalDateTime max =
                    seedTimes.values().stream().flatMap(List::stream).max(Comparator.naturalOrder()).orElseThrow().plus(settings.window());
            return logs.stream()
                    .filter(log -> seedTimes.containsKey(log.getUserSessionId()) && hostKey(log).equals(seed.virtualHost())
                            && log.getRequestedAt().isAfter(min) && !log.getRequestedAt().isAfter(max) && log.getHitCount() > 0
                            && !"admin".equals(log.getAccessType()) && !seed.searchWords().contains(log.getSearchWord()))
                    .limit(settings.logFetchSize())
                    .toList();
        }

        @Override
        protected void insert(final List<RelatedQuery> entityList) {
            insertCount++;
            if (failOnInsert) {
                throw new IllegalStateException("insert failed");
            }
            inserted.addAll(entityList);
        }
    }

    static class MockFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        boolean searchLog = true;
        boolean userInfo = true;
        int maxFetchSize = 5000;

        @Override
        public boolean isSearchLog() {
            return searchLog;
        }

        @Override
        public boolean isUserInfo() {
            return userInfo;
        }

        @Override
        public Integer getPageRelatedqueryMaxFetchSizeAsInteger() {
            return maxFetchSize;
        }

        @Override
        public boolean isValidSearchLogPermissions(final String[] permissions) {
            return permissions != null && Arrays.stream(permissions).allMatch(GUEST::equals);
        }

        @Override
        public Integer getRelatedQueryGenerateDaysAsInteger() {
            return 30;
        }

        @Override
        public Integer getRelatedQueryGenerateTermSizeAsInteger() {
            return 100;
        }

        @Override
        public Integer getRelatedQueryGenerateQuerySizeAsInteger() {
            return 5;
        }

        @Override
        public Integer getRelatedQueryGenerateMinSessionsAsInteger() {
            return 3;
        }

        @Override
        public Integer getRelatedQueryGenerateSessionIntervalAsInteger() {
            return 10;
        }

        @Override
        public Integer getRelatedQueryGenerateSeedLogSizeAsInteger() {
            return 1000;
        }

        @Override
        public Integer getRelatedQueryGenerateSeedSessionSizeAsInteger() {
            return 200;
        }

        @Override
        public Integer getRelatedQueryGenerateLogFetchSizeAsInteger() {
            return 2000;
        }

        @Override
        public Integer getRelatedQueryGenerateQueryMinLengthAsInteger() {
            return 2;
        }

        @Override
        public Integer getRelatedQueryGenerateQueryMaxLengthAsInteger() {
            return 50;
        }
    }
}
