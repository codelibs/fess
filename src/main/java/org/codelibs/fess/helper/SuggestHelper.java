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
package org.codelibs.fess.helper;

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.config.exbhv.BadWordBhv;
import org.codelibs.fess.opensearch.config.exbhv.ElevateWordBhv;
import org.codelibs.fess.opensearch.config.exentity.BadWord;
import org.codelibs.fess.opensearch.config.exentity.ElevateWord;
import org.codelibs.fess.opensearch.log.exbhv.SearchLogBhv;
import org.codelibs.fess.opensearch.log.exentity.SearchLog;
import org.codelibs.fess.suggest.Suggester;
import org.codelibs.fess.suggest.constants.FieldNames;
import org.codelibs.fess.suggest.entity.SuggestItem;
import org.codelibs.fess.suggest.exception.SuggestSettingsException;
import org.codelibs.fess.suggest.index.SuggestDeleteResponse;
import org.codelibs.fess.suggest.index.contents.document.ESSourceReader;
import org.codelibs.fess.suggest.settings.SuggestSettings;
import org.codelibs.fess.suggest.settings.SuggestSettingsBuilder;
import org.codelibs.fess.suggest.util.SuggestUtil;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.common.lucene.search.function.CombineFunction;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.opensearch.index.query.functionscore.ScoreFunctionBuilders;
import org.opensearch.search.sort.SortBuilders;

import jakarta.annotation.PostConstruct;

/**
 * Helper class for managing the suggest feature.
 * This class provides methods for initializing the suggester, indexing data from
 * various sources, and managing elevate words and bad words.
 */
public class SuggestHelper {
    /**
     * Constructs a new suggest helper.
     */
    public SuggestHelper() {
        // do nothing
    }

    private static final Logger logger = LogManager.getLogger(SuggestHelper.class);

    /** The separator for text content. */
    protected static final String TEXT_SEP = " ";

    /** The suggester instance for handling suggest operations. */
    protected Suggester suggester;

    /** The Fess configuration for accessing system settings. */
    protected FessConfig fessConfig;

    /** The set of field names for content. */
    protected final Set<String> contentFieldNameSet = new HashSet<>();

    /** The set of field names for tags. */
    protected final Set<String> tagFieldNameSet = new HashSet<>();

    /** The set of field names for roles. */
    protected final Set<String> roleFieldNameSet = new HashSet<>();

    /** The list of content field names. */
    protected List<String> contentFieldList;

    /** The popular word helper for handling popular words. */
    protected PopularWordHelper popularWordHelper = null;

    /** The interval for storing search logs. */
    protected long searchStoreInterval = 1; // min

    /**
     * Initializes the SuggestHelper.
     * This method sets up the suggester, configures field names, and initializes
     * the connection to the search engine.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        fessConfig = ComponentUtil.getFessConfig();
        split(fessConfig.getSuggestFieldContents(), ",")
                .of(stream -> stream.filter(StringUtil::isNotBlank).forEach(contentFieldNameSet::add));
        split(fessConfig.getSuggestFieldTags(), ",").of(stream -> stream.filter(StringUtil::isNotBlank).forEach(tagFieldNameSet::add));
        split(fessConfig.getSuggestFieldRoles(), ",").of(stream -> stream.filter(StringUtil::isNotBlank).forEach(roleFieldNameSet::add));
        contentFieldList = Arrays.asList(stream(fessConfig.getSuggestFieldContents()).get(stream -> stream.toArray(n -> new String[n])));

        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        searchEngineClient.admin().cluster().prepareHealth().setWaitForYellowStatus().execute()
                .actionGet(fessConfig.getIndexHealthTimeout());

        final SuggestSettingsBuilder settingsBuilder = SuggestSettings.builder();
        settingsBuilder.addInitialSettings("search_engine.type", fessConfig.getFesenType());
        settingsBuilder.bulkTimeout(fessConfig.getIndexBulkTimeout());
        settingsBuilder.clusterTimeout(fessConfig.getIndexHealthTimeout());
        settingsBuilder.indexTimeout(fessConfig.getIndexIndexTimeout());
        settingsBuilder.indicesTimeout(fessConfig.getIndexIndicesTimeout());
        settingsBuilder.searchTimeout(fessConfig.getIndexSearchTimeout());
        settingsBuilder.setSettingsIndexName(fessConfig.getIndexDocumentSuggestIndex() + "_suggest");
        suggester = Suggester.builder().settings(settingsBuilder).build(searchEngineClient, fessConfig.getIndexDocumentSuggestIndex());
        if (ComponentUtil.hasPopularWordHelper()) {
            popularWordHelper = ComponentUtil.getPopularWordHelper();
        }
        try {
            suggester.settings().array().delete(SuggestSettings.DefaultKeys.SUPPORTED_FIELDS);
            split(fessConfig.getSuggestFieldIndexContents(), ",").of(stream -> stream.filter(StringUtil::isNotBlank).forEach(field -> {
                try {
                    suggester.settings().array().add(SuggestSettings.DefaultKeys.SUPPORTED_FIELDS, field);
                } catch (final SuggestSettingsException e) {
                    logger.warn("Failed to add {}", field, e);
                }
            }));
            suggester.createIndexIfNothing();
        } catch (final Exception e) {
            logger.warn("Failed to initialize Suggester.", e);
        }
    }

    /**
     * Gets the suggester instance.
     *
     * @return The suggester instance.
     */
    public Suggester suggester() {
        return suggester;
    }

    /**
     * Stores search logs in the suggest index.
     */
    public void storeSearchLog() {
        final SearchLogBhv searchLogBhv = ComponentUtil.getComponent(SearchLogBhv.class);

        searchLogBhv.selectBulk(cb -> {
            final String from = LocalDateTime.now().minusDays(fessConfig.getPurgeSuggestSearchLogDay()).format(DateTimeFormatter.ISO_DATE);
            cb.query().addQuery(QueryBuilders.rangeQuery("requestedAt").gte(from));
            cb.query().addOrderBy_RequestedAt_Asc();
            cb.specify().columnAccessType();
            cb.specify().columnUserSessionId();
            cb.specify().columnHitCount();
            cb.specify().columnClientIp();
            cb.specify().columnSearchWord();
            cb.specify().columnRequestedAt();
            cb.specify().columnHitCount();
            cb.specify().columnVirtualHost();
            cb.specify().columnLanguages();
            cb.specify().columnRoles();
            cb.specify().doColumn("searchField.*");
        }, this::indexFromSearchLog);
    }

    /**
     * Indexes suggest data from a list of search logs.
     *
     * @param searchLogList The list of search logs to index.
     */
    public void indexFromSearchLog(final List<SearchLog> searchLogList) {
        final Map<String, LocalDateTime> duplicateSessionMap = new HashMap<>();
        searchLogList.stream().forEach(searchLog -> {
            if (searchLog.getHitCount() == null
                    || searchLog.getHitCount().longValue() < fessConfig.getSuggestMinHitCountAsInteger().longValue()) {
                return;
            }

            final String sessionId;
            if (searchLog.getUserSessionId() != null) {
                sessionId = searchLog.getUserSessionId();
            } else if (Constants.SEARCH_LOG_ACCESS_TYPE_WEB.equals(searchLog.getAccessType())) {
                sessionId = searchLog.getClientIp();
            } else {
                sessionId = searchLog.getClientIp() + '_' + searchLog.getSearchWord();
            }

            final LocalDateTime requestedAt = searchLog.getRequestedAt();
            if (sessionId == null || duplicateSessionMap.containsKey(sessionId)
                    && duplicateSessionMap.get(sessionId).plusMinutes(searchStoreInterval).isAfter(requestedAt)) {
                return;
            }

            final StringBuilder sb = new StringBuilder(100);
            final List<String> fields = new ArrayList<>();
            final List<String> tags = new ArrayList<>();
            final List<String> roles = new ArrayList<>();

            for (final Pair<String, String> searchFieldLog : searchLog.getSearchFieldLogList()) {
                final String name = searchFieldLog.getFirst();
                if (contentFieldNameSet.contains(name)) {
                    if (sb.length() > 0) {
                        sb.append(TEXT_SEP);
                    }
                    sb.append(searchFieldLog.getSecond());
                    fields.add(name);
                } else if (tagFieldNameSet.contains(name)) {
                    tags.add(searchFieldLog.getSecond());
                } else if (roleFieldNameSet.contains(name)) {
                    roles.add(searchFieldLog.getSecond());
                }
            }

            final String virtualHost = searchLog.getVirtualHost();
            if (virtualHost != null) {
                tags.add(virtualHost);
            }

            if (sb.length() > 0) {
                final String[] langs = searchLog.getLanguages() == null ? new String[] {} : searchLog.getLanguages().split(",");
                stream(searchLog.getRoles()).of(stream -> stream.forEach(role -> roles.add(role)));
                if (fessConfig.isValidSearchLogPermissions(roles.toArray(new String[roles.size()]))) {
                    suggester.indexer().indexFromSearchWord(sb.toString(), fields.toArray(new String[fields.size()]),
                            tags.toArray(new String[tags.size()]), roles.toArray(new String[roles.size()]), 1, langs);
                    duplicateSessionMap.put(sessionId, requestedAt);
                }
            }
        });
        refresh();
    }

    /**
     * Indexes suggest data from documents.
     *
     * @param success A callback for successful indexing.
     * @param error   A callback for indexing errors.
     */
    public void indexFromDocuments(final Consumer<Boolean> success, final Consumer<Throwable> error) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final long interval = fessConfig.getSuggestUpdateRequestIntervalAsInteger().longValue();
        final int docPerReq = fessConfig.getSuggestUpdateDocPerRequestAsInteger();
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        suggester.indexer().indexFromDocument(() -> {
            final ESSourceReader reader = new ESSourceReader(ComponentUtil.getSearchEngineClient(), suggester.settings(),
                    fessConfig.getIndexDocumentSearchIndex());
            reader.setScrollSize(fessConfig.getSuggestSourceReaderScrollSizeAsInteger());
            reader.setLimitDocNumPercentage(fessConfig.getSuggestUpdateContentsLimitNumPercentage());
            reader.setLimitNumber(fessConfig.getSuggestUpdateContentsLimitNumAsInteger());
            reader.setLimitOfDocumentSize(fessConfig.getSuggestUpdateContentsLimitDocSizeAsInteger());

            final List<FunctionScoreQueryBuilder.FilterFunctionBuilder> flist = new ArrayList<>();
            flist.add(new FunctionScoreQueryBuilder.FilterFunctionBuilder(ScoreFunctionBuilders.randomFunction()
                    .seed(ComponentUtil.getSystemHelper().getCurrentTimeAsLong()).setField(fessConfig.getIndexFieldDocId())));
            reader.setQuery(QueryBuilders
                    .functionScoreQuery(QueryBuilders.matchAllQuery(),
                            flist.toArray(new FunctionScoreQueryBuilder.FilterFunctionBuilder[flist.size()]))
                    .boostMode(CombineFunction.MULTIPLY));
            reader.addSort(SortBuilders.fieldSort(fessConfig.getIndexFieldClickCount()));
            reader.addSort(SortBuilders.scoreSort());
            return reader;
        }, docPerReq, () -> {
            systemHelper.calibrateCpuLoad();
            ThreadUtil.sleep(interval);
        }).then(response -> {
            refresh();
            success.accept(true);
        }).error(t -> error.accept(t));
    }

    /**
     * Purges old suggest data from documents.
     *
     * @param time The timestamp to purge data before.
     */
    public void purgeDocumentSuggest(final LocalDateTime time) {
        final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder
                .must(QueryBuilders.rangeQuery(FieldNames.TIMESTAMP).lt(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

        boolQueryBuilder.must(QueryBuilders.termQuery(FieldNames.KINDS, SuggestItem.Kind.DOCUMENT.toString()));
        boolQueryBuilder.mustNot(QueryBuilders.termQuery(FieldNames.KINDS, SuggestItem.Kind.QUERY.toString()));
        boolQueryBuilder.mustNot(QueryBuilders.termQuery(FieldNames.KINDS, SuggestItem.Kind.USER.toString()));

        SuggestUtil.deleteByQuery(ComponentUtil.getSearchEngineClient(), suggester.settings(), suggester.getIndex(), boolQueryBuilder);
    }

    /**
     * Purges old suggest data from search logs.
     *
     * @param time The timestamp to purge data before.
     */
    public void purgeSearchlogSuggest(final LocalDateTime time) {
        final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder
                .must(QueryBuilders.rangeQuery(FieldNames.TIMESTAMP).lt(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

        boolQueryBuilder.mustNot(QueryBuilders.termQuery(FieldNames.KINDS, SuggestItem.Kind.DOCUMENT.toString()));
        boolQueryBuilder.must(QueryBuilders.termQuery(FieldNames.KINDS, SuggestItem.Kind.QUERY.toString()));
        boolQueryBuilder.mustNot(QueryBuilders.termQuery(FieldNames.KINDS, SuggestItem.Kind.USER.toString()));

        SuggestUtil.deleteByQuery(ComponentUtil.getSearchEngineClient(), suggester.settings(), suggester.getIndex(), boolQueryBuilder);
    }

    /**
     * Gets the total number of words in the suggest index.
     *
     * @return The total number of words.
     */
    public long getAllWordsNum() {
        return suggester.getAllWordsNum();
    }

    /**
     * Gets the number of words from documents in the suggest index.
     *
     * @return The number of document words.
     */
    public long getDocumentWordsNum() {
        return suggester.getDocumentWordsNum();
    }

    /**
     * Gets the number of words from queries in the suggest index.
     *
     * @return The number of query words.
     */
    public long getQueryWordsNum() {
        return suggester.getQueryWordsNum();
    }

    /**
     * Deletes all words from the suggest index.
     *
     * @return true if the operation was successful, false otherwise.
     */
    public boolean deleteAllWords() {
        final SuggestDeleteResponse response = suggester.indexer().deleteAll();
        if (response.hasError()) {
            logger.warn("Failed to delete all words.", response.getErrors().get(0));
            return false;
        }
        refresh();
        return true;
    }

    /**
     * Deletes all document-based words from the suggest index.
     *
     * @return true if the operation was successful, false otherwise.
     */
    public boolean deleteDocumentWords() {
        final SuggestDeleteResponse response = suggester.indexer().deleteDocumentWords();
        if (response.hasError()) {
            logger.warn("Failed to delete document words.", response.getErrors().get(0));
            return false;
        }
        refresh();
        return true;
    }

    /**
     * Deletes all query-based words from the suggest index.
     *
     * @return true if the operation was successful, false otherwise.
     */
    public boolean deleteQueryWords() {
        final SuggestDeleteResponse response = suggester.indexer().deleteQueryWords();
        if (response.hasError()) {
            logger.warn("Failed to delete query words.", response.getErrors().get(0));
            return false;
        }
        refresh();
        return true;
    }

    /**
     * Stores all elevate words in the suggest index.
     *
     * @param apply true to apply the changes immediately.
     */
    public void storeAllElevateWords(final boolean apply) {
        deleteAllElevateWord(apply);

        final List<ElevateWord> list = ComponentUtil.getComponent(ElevateWordBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageElevateWordMaxFetchSizeAsInteger());
        });

        for (final ElevateWord elevateWord : list) {
            addElevateWord(elevateWord.getSuggestWord(), elevateWord.getReading(), elevateWord.getLabelTypeValues(),
                    elevateWord.getPermissions(), elevateWord.getBoost(), apply);
        }
        refresh();
    }

    /**
     * Deletes all elevate words from the suggest index.
     *
     * @param apply true to apply the changes immediately.
     */
    public void deleteAllElevateWord(final boolean apply) {
        final List<ElevateWord> list = ComponentUtil.getComponent(ElevateWordBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageElevateWordMaxFetchSizeAsInteger());
        });

        for (final ElevateWord elevateWord : list) {
            suggester.indexer().deleteElevateWord(elevateWord.getSuggestWord(), apply);
        }
        refresh();
    }

    /**
     * Deletes a specific elevate word from the suggest index.
     *
     * @param word  The elevate word to delete.
     * @param apply true to apply the changes immediately.
     */
    public void deleteElevateWord(final String word, final boolean apply) {
        suggester.indexer().deleteElevateWord(word, apply);
        refresh();
    }

    /**
     * Adds an elevate word to the suggest index.
     *
     * @param word        The elevate word.
     * @param reading     The reading of the word.
     * @param tags        The tags associated with the word.
     * @param permissions The permissions for the word.
     * @param boost       The boost value for the word.
     * @param apply       true to apply the changes immediately.
     */
    public void addElevateWord(final String word, final String reading, final String[] tags, final String[] permissions, final Float boost,
            final boolean apply) {
        final String[] readings;
        if (StringUtil.isBlank(reading)) {
            readings = word.replace("　", TEXT_SEP).replaceAll(TEXT_SEP + "+", TEXT_SEP).split(TEXT_SEP);
        } else {
            readings = reading.replace("　", TEXT_SEP).replaceAll(TEXT_SEP + "+", TEXT_SEP).split(TEXT_SEP);
        }

        final List<String> labelList = new ArrayList<>();
        if (tags != null) {
            Collections.addAll(labelList, tags);
        }
        final List<String> roleList = new ArrayList<>();
        if (permissions != null) {
            Collections.addAll(roleList, permissions);
        }

        suggester.indexer().addElevateWord(new org.codelibs.fess.suggest.entity.ElevateWord(word, boost, Arrays.asList(readings),
                contentFieldList, labelList, roleList), apply);

        refresh();
    }

    /**
     * Deletes all bad words from the suggest index.
     */
    protected void deleteAllBadWords() {
        suggester.settings().badword().deleteAll();
    }

    /**
     * Stores all bad words in the suggest index.
     *
     * @param apply true to apply the changes immediately.
     */
    public void storeAllBadWords(final boolean apply) {
        deleteAllBadWords();
        final List<BadWord> list = ComponentUtil.getComponent(BadWordBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageBadWordMaxFetchSizeAsInteger());
        });
        for (final BadWord badWord : list) {
            final String word = badWord.getSuggestWord();
            suggester.indexer().addBadWord(word, apply);
        }
        refresh();
    }

    /**
     * Adds a bad word to the suggest index.
     *
     * @param badWord The bad word to add.
     * @param apply   true to apply the changes immediately.
     */
    public void addBadWord(final String badWord, final boolean apply) {
        suggester.indexer().addBadWord(badWord, apply);
        refresh();
    }

    /**
     * Deletes a bad word from the suggest index.
     *
     * @param badWord The bad word to delete.
     */
    public void deleteBadWord(final String badWord) {
        suggester.indexer().deleteBadWord(badWord);
        refresh();
    }

    /**
     * Refreshes the suggest index.
     */
    public synchronized void refresh() {
        suggester.refresh();
        if (popularWordHelper != null) {
            popularWordHelper.clearCache();
        }
    }

    /**
     * Sets the interval for storing search logs.
     *
     * @param searchStoreInterval The search store interval in minutes.
     */
    public void setSearchStoreInterval(final long searchStoreInterval) {
        this.searchStoreInterval = searchStoreInterval;
    }
}