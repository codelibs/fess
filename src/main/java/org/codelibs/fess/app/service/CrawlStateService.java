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
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fesen.opensearch.action.get.GetResponse;
import org.codelibs.fesen.opensearch.action.search.SearchResponse;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.codelibs.fess.entity.CrawlProgress;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
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

    private static final String METADATA_FIELD = "metadata";

    private static final Pattern CONFIG_ID_PATTERN = Pattern.compile("[WFD][A-Za-z0-9_-]{1,100}");

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** The metadata the crawler wrote last, by config ID. */
    private final Map<String, Map<String, Object>> writtenMetadata = new ConcurrentHashMap<>();

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
     * Saves the number of documents processed so far by a running crawl.
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
        update(configId, changes);
    }

    /**
     * Saves the result of a crawl that completed normally and ends its progress. The count becomes the
     * base of the percentage of the next crawl, unless it is zero: a crawl that processed nothing is
     * more likely a failure than a site that became empty, and it must not erase the last good count.
     *
     * @param configId the crawl configuration ID
     * @param count the number of documents processed by the crawl
     */
    public void saveCompleted(final String configId, final long count) {
        final long now = System.currentTimeMillis();
        final Map<String, Object> changes = new LinkedHashMap<>();
        if (count > 0) {
            changes.put(LAST_COUNT, count);
        }
        changes.put(LAST_COMPLETED_TIME, now);
        changes.put(PROGRESS_COUNT, count);
        changes.put(PROGRESS_UPDATED_TIME, now);
        changes.put(PROGRESS_EXPIRED_TIME, 0L);
        update(configId, changes);
    }

    /**
     * Ends the progress of a crawl that did not complete, such as a stopped one. The count of the
     * previous completed crawl is kept.
     *
     * @param configId the crawl configuration ID
     */
    public void endProgress(final String configId) {
        final Map<String, Object> changes = new LinkedHashMap<>();
        changes.put(PROGRESS_EXPIRED_TIME, 0L);
        update(configId, changes);
    }

    /**
     * Merges the changes into the metadata of a crawl configuration and writes it with a partial update.
     *
     * @param configId the crawl configuration ID
     * @param changes the keys to set
     * @return true if the metadata was written
     */
    protected boolean update(final String configId, final Map<String, Object> changes) {
        if (!isValidConfigId(configId)) {
            logger.debug("Skipped saving a crawl state, the ID is not valid: configId={}", configId);
            return false;
        }
        try {
            Map<String, Object> state = writtenMetadata.get(configId);
            if (state == null) {
                // A read that fails must not be followed by a write: the keys it did not see would be lost.
                final Map<String, Object> stored = parseMetadata(fetchMetadata(getIndexName(configId), getDocumentId(configId)));
                state = writtenMetadata.putIfAbsent(configId, stored);
                if (state == null) {
                    state = stored;
                }
            }
            synchronized (state) {
                final Map<String, Object> merged = new LinkedHashMap<>(state);
                merged.putAll(changes);
                final String json = objectMapper.writeValueAsString(merged);
                if (json.length() > MAX_METADATA_LENGTH) {
                    reportFailure("size", "The metadata of the crawl state is too long: configId={}, length={}, max={}", configId,
                            json.length(), MAX_METADATA_LENGTH);
                    return false;
                }
                storeMetadata(getIndexName(configId), getDocumentId(configId), json);
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
     * @return the metadata string, or null if there is none
     */
    protected String fetchMetadata(final String index, final String id) {
        final GetResponse response = ComponentUtil.getSearchEngineClient()
                .prepareGet(index, id)
                .execute()
                .actionGet(ComponentUtil.getFessConfig().getIndexSearchTimeout());
        return response.isExists() && response.getSourceAsMap().get(METADATA_FIELD) instanceof final String metadata ? metadata : null;
    }

    /**
     * Sets the metadata string of a crawl configuration with a partial update of that one field.
     *
     * @param index the index of the crawl configuration
     * @param id the document ID of the crawl configuration
     * @param metadata the metadata string
     */
    protected void storeMetadata(final String index, final String id, final String metadata) {
        ComponentUtil.getSearchEngineClient()
                .prepareUpdate(index, id)
                .setRetryOnConflict(3)
                .setDoc(METADATA_FIELD, metadata)
                .execute()
                .actionGet(ComponentUtil.getFessConfig().getIndexIndexTimeout());
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
                if (getLong(metadata, PROGRESS_EXPIRED_TIME) > now) {
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
     * Lists the web, file and data store crawl configurations with their metadata.
     *
     * @return the crawl configurations
     */
    protected List<ConfigMetadata> listConfigMetadata() {
        final CrawlingConfigHelper helper = ComponentUtil.getCrawlingConfigHelper();
        final List<ConfigMetadata> list = new ArrayList<>();
        helper.getAllWebConfigList(true, true, false, null)
                .forEach(c -> list.add(new ConfigMetadata(c.getConfigId(), c.getName(), c.getMetadata())));
        helper.getAllFileConfigList(true, true, false, null)
                .forEach(c -> list.add(new ConfigMetadata(c.getConfigId(), c.getName(), c.getMetadata())));
        helper.getAllDataConfigList(true, true, false, null)
                .forEach(c -> list.add(new ConfigMetadata(c.getConfigId(), c.getName(), c.getMetadata())));
        return list;
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
        return metadata.get(key) instanceof final Number number ? number.longValue() : 0L;
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
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryBuilder query = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(fessConfig.getIndexFieldConfigId(), configId))
                .filter(QueryBuilders.termQuery(fessConfig.getIndexFieldSegment(), segment));
        try {
            return count(fessConfig.getIndexDocumentUpdateIndex(), query);
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
     * @return the number of documents
     */
    protected long count(final String index, final QueryBuilder query) {
        final SearchResponse response = ComponentUtil.getSearchEngineClient()
                .prepareSearch(index)
                .setSize(0)
                .setTrackTotalHits(true)
                .setQuery(query)
                .execute()
                .actionGet(ComponentUtil.getFessConfig().getIndexSearchTimeout());
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
