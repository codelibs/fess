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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.function.LongSupplier;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fesen.opensearch.action.get.GetResponse;
import org.codelibs.fesen.opensearch.action.search.SearchResponse;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.codelibs.fess.entity.CrawlProgress;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exbhv.DataConfigBhv;
import org.codelibs.fess.opensearch.config.exbhv.FileConfigBhv;
import org.codelibs.fess.opensearch.config.exbhv.WebConfigBhv;
import org.codelibs.fess.opensearch.config.exentity.DataConfig;
import org.codelibs.fess.opensearch.config.exentity.FileConfig;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.LogUtil;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * Service for the crawl state that is kept in the {@code metadata} field of a crawl configuration
 * (web, file and data store): the number of documents of the previous completed crawl, and the
 * progress of the running one.
 *
 * <p>{@code metadata} is a JSON object in a string that the system manages. It is not in any admin
 * form or admin API body, so an administrator cannot change it, and saving a crawl configuration on
 * the admin screen keeps it: the screen loads the stored entity and copies the form over it.</p>
 *
 * <p>The crawler process is the only writer, and it writes with a partial update of that one field,
 * never a whole configuration, so {@code updatedTime} and {@code updatedBy} do not change and the
 * configuration caches are not invalidated. The crawler keeps the object it last wrote for each
 * configuration and merges each change into it, so every write carries all the keys.</p>
 *
 * <p>There is one race left. The admin screen writes the whole configuration back, and its load of
 * the stored entity and that write are a few milliseconds apart. A crawler write that falls between
 * them is overwritten with the older metadata. Another write follows within a renewal interval and
 * puts the whole object back, so the loss is one renewal of the progress. Only when the lost write is
 * the one that completes the crawl is the new previous count lost until the next completed crawl, and
 * the progress then shows as running until it expires.</p>
 *
 * <p>Every method that writes is failure-tolerant: a failure is reported once at WARN and then at
 * DEBUG, and never reaches the crawl.</p>
 */
public class CrawlStateService {

    private static final Logger logger = LogManager.getLogger(CrawlStateService.class);

    /** The metadata key of the number of documents of the previous completed crawl. */
    public static final String LAST_COUNT = "lastCount";

    /** The metadata key of the time the previous crawl completed. */
    public static final String LAST_COMPLETED_TIME = "lastCompletedTime";

    /** The metadata key of the number of documents the running crawl has processed. */
    public static final String PROGRESS_COUNT = "progressCount";

    /** The metadata key of the time the progress was written. */
    public static final String PROGRESS_UPDATED_TIME = "progressUpdatedTime";

    /** The metadata key of the time until which the progress counts as running. */
    public static final String PROGRESS_EXPIRED_TIME = "progressExpiredTime";

    /** The maximum length of the metadata string. */
    public static final int MAX_METADATA_LENGTH = 4096;

    /** The shortest time a written progress is shown as running, in milliseconds. */
    public static final long MIN_PROGRESS_TTL = 30000L;

    /** How long the periodic progress write may wait for the search engine. It runs off the crawl loop. */
    public static final String PROGRESS_TIMEOUT = "5s";

    /**
     * How long the write of the result of a crawl may wait for the search engine. It runs once per
     * crawl and configuration, in the crawl loop, and the previous count is not recoverable if it is lost.
     */
    public static final String RESULT_TIMEOUT = "30s";

    /** The furthest a progress may claim to be running ahead of now; later values are not trusted. */
    public static final long MAX_PROGRESS_AHEAD = 24L * 60 * 60 * 1000;

    private static final Set<String> KNOWN_KEYS =
            Set.of(LAST_COUNT, LAST_COMPLETED_TIME, PROGRESS_COUNT, PROGRESS_UPDATED_TIME, PROGRESS_EXPIRED_TIME);

    private static final String METADATA_FIELD = "metadata";

    private static final Pattern CONFIG_ID_PATTERN = Pattern.compile("[WFD][A-Za-z0-9_-]{1,100}");

    private final ObjectMapper objectMapper = new ObjectMapper();

    // The state below is static: a service is created anew for every lookup, and the crawl loop, the
    // writer thread and the data store threads must all see the same state.

    /** The metadata the crawler wrote last, by config ID. */
    private static final Map<String, Map<String, Object>> WRITTEN_METADATA = new ConcurrentHashMap<>();

    /** The config IDs whose crawl has ended in this process: a late progress write must not follow it. */
    private static final Set<String> ENDED_CONFIGS = ConcurrentHashMap.newKeySet();

    /** The config IDs that have a progress write queued or running. */
    private static final Set<String> PENDING_CONFIGS = ConcurrentHashMap.newKeySet();

    private static ExecutorService executor;

    /**
     * The identity and the stored metadata of a crawl configuration.
     *
     * @param configId the crawl configuration ID (W..., F... or D...)
     * @param name the name of the crawl configuration
     * @param metadata the stored metadata string, or null
     */
    public record ConfigMetadata(String configId, String name, String metadata) {
    }

    /**
     * Default constructor.
     */
    public CrawlStateService() {
        // Default constructor
    }

    /**
     * Checks whether a string can be the ID of a crawl configuration: W, F or D followed by the ID.
     *
     * @param configId the string to check
     * @return true if it is usable as a document ID
     */
    public static boolean isValidConfigId(final String configId) {
        return configId != null && CONFIG_ID_PATTERN.matcher(configId).matches();
    }

    /**
     * Returns how long a written progress is shown as running. A crawl that is terminated abnormally never
     * clears its progress, so the reader treats a progress that was not renewed in this time as not running.
     *
     * @param renewalInterval the interval at which the crawler renews the progress, in milliseconds
     * @return the time in milliseconds
     */
    public static long getProgressTtl(final long renewalInterval) {
        return Math.max(renewalInterval * 3, MIN_PROGRESS_TTL);
    }

    // ===================================================================================
    //                                                                               Write
    //                                                                               =====

    /**
     * Saves the number of documents processed so far by a running crawl. The call waits for the search
     * engine for at most {@value #PROGRESS_TIMEOUT} per request; the crawl loop uses
     * {@link #saveProgressAsync} instead.
     *
     * @param configId the crawl configuration ID
     * @param count the number of documents processed so far
     * @param ttl how long the progress is shown as running, in milliseconds
     */
    public void saveProgress(final String configId, final long count, final long ttl) {
        final long now = System.currentTimeMillis();
        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put(PROGRESS_COUNT, count);
        changes.put(PROGRESS_UPDATED_TIME, now);
        changes.put(PROGRESS_EXPIRED_TIME, now + ttl);
        update(configId, changes, PROGRESS_TIMEOUT, true);
    }

    /**
     * Saves the progress of a running crawl on a thread of its own, so that a slow search engine cannot
     * hold up the crawl loop that calls this. The count is taken on that thread too. A write is dropped
     * when the previous one of the same configuration has not finished, and one that is queued when the
     * crawl ends is dropped as well, because the next write would replace it anyway.
     *
     * @param configId the crawl configuration ID
     * @param count supplies the number of documents processed so far, or a negative number if it is unknown
     * @param ttl how long the progress is shown as running, in milliseconds
     */
    public void saveProgressAsync(final String configId, final LongSupplier count, final long ttl) {
        if (!isValidConfigId(configId) || ENDED_CONFIGS.contains(configId) || !PENDING_CONFIGS.add(configId)) {
            return;
        }
        try {
            execute(() -> {
                try {
                    final long value = count.getAsLong();
                    if (value >= 0) {
                        saveProgress(configId, value, ttl);
                    }
                } catch (final Exception e) {
                    reportFailure("progress", "Failed to save the crawl progress: configId={}", configId, e);
                } finally {
                    PENDING_CONFIGS.remove(configId);
                }
            });
        } catch (final RuntimeException e) {
            PENDING_CONFIGS.remove(configId);
            reportFailure("progress", "Failed to queue the crawl progress: configId={}", configId, e);
        }
    }

    /**
     * Runs a task on the thread that writes the progress.
     *
     * @param task the task
     */
    protected void execute(final Runnable task) {
        synchronized (CrawlStateService.class) {
            if (executor == null) {
                // one daemon thread for the process: it never keeps the crawler from exiting
                executor = Executors.newSingleThreadExecutor(r -> {
                    final Thread thread = new Thread(r, "CrawlStateWriter");
                    thread.setDaemon(true);
                    return thread;
                });
            }
            executor.execute(task);
        }
    }

    /**
     * Forgets what this process has written and which crawls have ended. Only for tests.
     */
    static void reset() {
        WRITTEN_METADATA.clear();
        ENDED_CONFIGS.clear();
        PENDING_CONFIGS.clear();
    }

    /**
     * Saves the result of a crawl that completed normally and ends its progress. The count becomes the
     * base of the percentage of the next crawl, unless it is zero: a crawl that processed nothing is
     * more likely a failure than a site that became empty, and it must not erase the last good count.
     * It is written on the calling thread and may wait for the search engine for at most
     * {@value #RESULT_TIMEOUT} per request: it happens once per crawl, and a progress write that was
     * still queued is dropped so that it cannot follow it.
     *
     * @param configId the crawl configuration ID
     * @param count the number of documents processed by the crawl
     */
    public void saveCompleted(final String configId, final long count) {
        markEnded(configId);
        final long now = System.currentTimeMillis();
        final Map<String, Object> changes = new LinkedHashMap<>();
        if (count > 0) {
            changes.put(LAST_COUNT, count);
        }
        changes.put(LAST_COMPLETED_TIME, now);
        changes.put(PROGRESS_COUNT, count);
        changes.put(PROGRESS_UPDATED_TIME, now);
        changes.put(PROGRESS_EXPIRED_TIME, 0L);
        update(configId, changes, RESULT_TIMEOUT, false);
    }

    /**
     * Ends the progress of a crawl that did not complete, such as a stopped one. The count of the
     * previous completed crawl is kept.
     *
     * @param configId the crawl configuration ID
     */
    public void endProgress(final String configId) {
        markEnded(configId);
        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put(PROGRESS_EXPIRED_TIME, 0L);
        update(configId, changes, RESULT_TIMEOUT, false);
    }

    private void markEnded(final String configId) {
        if (isValidConfigId(configId)) {
            ENDED_CONFIGS.add(configId);
        }
    }

    /**
     * Merges the changes into the metadata of a crawl configuration and writes it with a partial update.
     * When the merged object is too long, the keys this service does not know are dropped, so that a
     * stray key cannot stop the writes of the progress for good.
     *
     * @param configId the crawl configuration ID
     * @param changes the keys to set
     * @param timeout how long a request may wait for the search engine
     * @param skipIfEnded true to write nothing if the crawl of the configuration has ended in this process
     * @return true if the metadata was written
     */
    protected boolean update(final String configId, final Map<String, Object> changes, final String timeout, final boolean skipIfEnded) {
        if (!isValidConfigId(configId)) {
            logger.debug("Skipped saving a crawl state, the ID is not valid: configId={}", configId);
            return false;
        }
        try {
            Map<String, Object> state = WRITTEN_METADATA.get(configId);
            if (state == null) {
                // A read that fails must not be followed by a write: the keys it did not see would be lost.
                final Map<String, Object> stored = parseMetadata(fetchMetadata(getIndexName(configId), getDocumentId(configId), timeout));
                state = WRITTEN_METADATA.putIfAbsent(configId, stored);
                if (state == null) {
                    state = stored;
                }
            }
            synchronized (state) {
                if (skipIfEnded && ENDED_CONFIGS.contains(configId)) {
                    return false;
                }
                Map<String, Object> merged = new LinkedHashMap<>(state);
                merged.putAll(changes);
                String json = objectMapper.writeValueAsString(merged);
                if (json.length() > MAX_METADATA_LENGTH) {
                    merged.entrySet()
                            .removeIf(e -> !KNOWN_KEYS.contains(e.getKey())
                                    || !(e.getValue() instanceof Long || e.getValue() instanceof Integer));
                    json = objectMapper.writeValueAsString(merged);
                    if (json.length() > MAX_METADATA_LENGTH) {
                        reportFailure("size", "The metadata of the crawl state is too long: configId={}, length={}, max={}", configId,
                                json.length(), MAX_METADATA_LENGTH);
                        return false;
                    }
                }
                storeMetadata(getIndexName(configId), getDocumentId(configId), json, timeout);
                state.clear();
                state.putAll(merged);
                return true;
            }
        } catch (final Exception e) {
            reportFailure("update", "Failed to save the crawl state: configId={}", configId, e);
        }
        return false;
    }

    /**
     * Reads the metadata string of a crawl configuration.
     *
     * @param index the index of the crawl configuration
     * @param id the document ID of the crawl configuration
     * @param timeout how long the request may wait for the search engine
     * @return the metadata string, or null if there is none
     */
    protected String fetchMetadata(final String index, final String id, final String timeout) {
        final GetResponse response = ComponentUtil.getSearchEngineClient().prepareGet(index, id).execute().actionGet(timeout);
        return response.isExists() && response.getSourceAsMap().get(METADATA_FIELD) instanceof final String metadata ? metadata : null;
    }

    /**
     * Sets the metadata string of a crawl configuration with a partial update of that one field.
     *
     * @param index the index of the crawl configuration
     * @param id the document ID of the crawl configuration
     * @param metadata the metadata string
     * @param timeout how long the request may wait for the search engine
     */
    protected void storeMetadata(final String index, final String id, final String metadata, final String timeout) {
        ComponentUtil.getSearchEngineClient()
                .prepareUpdate(index, id)
                .setRetryOnConflict(3)
                .setDoc(METADATA_FIELD, metadata)
                .execute()
                .actionGet(timeout);
    }

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====

    /**
     * Returns the progress of the crawls that are running now: those whose progress was renewed within
     * its time to live. The percentage is relative to the previous completed crawl of the same configuration.
     *
     * @return the running crawls ordered by name, or an empty list if there is none or the state cannot be read
     */
    public List<CrawlProgress> getRunningProgress() {
        final List<CrawlProgress> list = new ArrayList<>();
        try {
            final long now = System.currentTimeMillis();
            for (final ConfigMetadata config : listConfigMetadata()) {
                final Map<String, Object> metadata = parseMetadata(config.metadata());
                final long expired = getLong(metadata, PROGRESS_EXPIRED_TIME);
                // a time far ahead is not one this service wrote, and would show a crawl that never ends
                if (expired > now && expired <= now + MAX_PROGRESS_AHEAD) {
                    list.add(new CrawlProgress(config.configId(), config.name() != null ? config.name() : config.configId(),
                            getLong(metadata, PROGRESS_COUNT), getLong(metadata, LAST_COUNT), getLong(metadata, PROGRESS_UPDATED_TIME)));
                }
            }
        } catch (final Exception e) {
            reportFailure("read", "Failed to read the crawl state.", e);
            return new ArrayList<>();
        }
        list.sort(Comparator.comparing(CrawlProgress::getName).thenComparing(CrawlProgress::getConfigId));
        return list;
    }

    /**
     * Lists the web, file and data store crawl configurations with their metadata. The configurations
     * that can run come first, so that the disabled ones, which use up the cap of each type, are the ones
     * left out when the cap is reached.
     *
     * @return the crawl configurations
     */
    protected List<ConfigMetadata> listConfigMetadata() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final List<ConfigMetadata> list = new ArrayList<>();
        final int web = fessConfig.getPageWebConfigMaxFetchSizeAsInteger();
        final List<WebConfig> webConfigs = selectWebConfigs(web);
        warnIfCapped(webConfigs.size(), web, FessConfig.PAGE_WEB_CONFIG_MAX_FETCH_SIZE);
        webConfigs.forEach(c -> list.add(new ConfigMetadata(c.getConfigId(), c.getName(), c.getMetadata())));
        final int file = fessConfig.getPageFileConfigMaxFetchSizeAsInteger();
        final List<FileConfig> fileConfigs = selectFileConfigs(file);
        warnIfCapped(fileConfigs.size(), file, FessConfig.PAGE_FILE_CONFIG_MAX_FETCH_SIZE);
        fileConfigs.forEach(c -> list.add(new ConfigMetadata(c.getConfigId(), c.getName(), c.getMetadata())));
        final int data = fessConfig.getPageDataConfigMaxFetchSizeAsInteger();
        final List<DataConfig> dataConfigs = selectDataConfigs(data);
        warnIfCapped(dataConfigs.size(), data, FessConfig.PAGE_DATA_CONFIG_MAX_FETCH_SIZE);
        dataConfigs.forEach(c -> list.add(new ConfigMetadata(c.getConfigId(), c.getName(), c.getMetadata())));
        return list;
    }

    private void warnIfCapped(final int size, final int max, final String key) {
        if (max > 0 && size >= max) {
            // The wording is about this list only: a configuration that is left out here can still be crawled.
            LogUtil.warnOnce(logger, key,
                    "{}={} limits how many crawl configurations of a type are checked for a running crawl, and that many were found. Crawls of further configurations are not listed in Crawling Info. Increase {} to list more.",
                    key, max, key);
        }
    }

    /**
     * Selects web configurations, the ones that can run first.
     *
     * @param max the most to select
     * @return the web configurations
     */
    protected List<WebConfig> selectWebConfigs(final int max) {
        return ComponentUtil.getComponent(WebConfigBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_Available_Desc();
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
            cb.fetchFirst(max);
        });
    }

    /**
     * Selects file configurations, the ones that can run first.
     *
     * @param max the most to select
     * @return the file configurations
     */
    protected List<FileConfig> selectFileConfigs(final int max) {
        return ComponentUtil.getComponent(FileConfigBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_Available_Desc();
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
            cb.fetchFirst(max);
        });
    }

    /**
     * Selects data store configurations, the ones that can run first.
     *
     * @param max the most to select
     * @return the data store configurations
     */
    protected List<DataConfig> selectDataConfigs(final int max) {
        return ComponentUtil.getComponent(DataConfigBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_Available_Desc();
            cb.query().addOrderBy_SortOrder_Asc();
            cb.query().addOrderBy_Name_Asc();
            cb.fetchFirst(max);
        });
    }

    /**
     * Parses a metadata string. A value that is missing, too long or not a JSON object is taken as empty.
     *
     * @param metadata the metadata string, or null
     * @return the metadata as a map the caller can change
     */
    protected Map<String, Object> parseMetadata(final String metadata) {
        if (metadata == null || metadata.isBlank() || metadata.length() > MAX_METADATA_LENGTH) {
            return new LinkedHashMap<>();
        }
        try {
            return new LinkedHashMap<>(objectMapper.readValue(metadata, new TypeReference<Map<String, Object>>() {
            }));
        } catch (final Exception e) {
            logger.debug("Ignored the metadata of a crawl configuration, it is not a JSON object.", e);
            return new LinkedHashMap<>();
        }
    }

    private static long getLong(final Map<String, Object> metadata, final String key) {
        // Only whole numbers that fit a long: longValue() of a larger or fractional number is not what was stored.
        return metadata.get(key) instanceof final Integer number ? number.longValue()
                : metadata.get(key) instanceof final Long number ? number.longValue() : 0L;
    }

    /**
     * Counts the documents of the document index that a crawl of a data store configuration has indexed
     * in a session. A data store creates no access result to count, as the web and file crawls do.
     *
     * @param configId the crawl configuration ID
     * @param segment the session ID the documents are indexed with
     * @return the number of documents, or -1 if the count failed
     */
    public long countDocuments(final String configId, final String segment) {
        return countDocuments(configId, segment, RESULT_TIMEOUT);
    }

    /**
     * Counts the documents of the document index that a crawl of a data store configuration has indexed
     * in a session, waiting for the search engine for at most the given time.
     *
     * @param configId the crawl configuration ID
     * @param segment the session ID the documents are indexed with
     * @param timeout how long the request may wait for the search engine
     * @return the number of documents, or -1 if the count failed
     */
    public long countDocuments(final String configId, final String segment, final String timeout) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(fessConfig.getIndexFieldConfigId(), configId))
                .filter(QueryBuilders.termQuery(fessConfig.getIndexFieldSegment(), segment));
        try {
            return count(fessConfig.getIndexDocumentUpdateIndex(), query, timeout);
        } catch (final Exception e) {
            reportFailure("count", "Failed to count the documents of a crawl: configId={}", configId, e);
            return -1;
        }
    }

    /**
     * Counts the documents of an index that match a query.
     *
     * @param index the index name
     * @param query the query
     * @param timeout how long the request may wait for the search engine
     * @return the number of documents
     */
    protected long count(final String index, final QueryBuilder query, final String timeout) {
        final SearchResponse response = ComponentUtil.getSearchEngineClient()
                .prepareSearch(index)
                .setSize(0)
                .setTrackTotalHits(true)
                .setQuery(query)
                .execute()
                .actionGet(timeout);
        return response.getHits().getTotalHits().value();
    }

    // ===================================================================================
    //                                                                              Helper
    //                                                                              ======

    /**
     * Returns the name of the index of a crawl configuration, adjusted for the configured index prefix.
     *
     * @param configId the crawl configuration ID
     * @return the index name
     */
    protected String getIndexName(final String configId) {
        final String name = switch (configId.charAt(0)) {
        case 'W' -> "fess_config.web_config";
        case 'F' -> "fess_config.file_config";
        default -> "fess_config.data_config";
        };
        return name.replaceFirst("fess_config", ComponentUtil.getFessConfig().getIndexConfigIndex());
    }

    /**
     * Returns the document ID of a crawl configuration in its index.
     *
     * @param configId the crawl configuration ID
     * @return the document ID, which is the config ID without its type letter
     */
    protected String getDocumentId(final String configId) {
        return configId.substring(1);
    }

    private void reportFailure(final String key, final String message, final Object... params) {
        // Written on every renewal of every running crawl, so only the first failure is a warning.
        if (!LogUtil.warnOnce(logger, key, message, params)) {
            logger.debug(message, params);
        }
    }
}
