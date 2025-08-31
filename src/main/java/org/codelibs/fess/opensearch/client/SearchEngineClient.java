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
package org.codelibs.fess.opensearch.client;

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;
import static org.codelibs.opensearch.runner.OpenSearchRunner.newConfigs;
import static org.opensearch.core.action.ActionListener.wrap;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.exception.ResourceNotFoundRuntimeException;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fesen.client.EngineInfo;
import org.codelibs.fesen.client.HttpClient;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.PingResponse;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.exception.SearchQueryException;
import org.codelibs.fess.helper.DocumentHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.util.BooleanFunction;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocMap;
import org.codelibs.fess.util.SearchEngineUtil;
import org.codelibs.fess.util.SystemUtil;
import org.codelibs.opensearch.runner.OpenSearchRunner;
import org.codelibs.opensearch.runner.OpenSearchRunner.Configs;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.di.exception.ContainerInitFailureException;
import org.opensearch.OpenSearchException;
import org.opensearch.OpenSearchStatusException;
import org.opensearch.action.ActionRequest;
import org.opensearch.action.ActionType;
import org.opensearch.action.DocWriteRequest.OpType;
import org.opensearch.action.DocWriteResponse.Result;
import org.opensearch.action.admin.cluster.health.ClusterHealthResponse;
import org.opensearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.opensearch.action.admin.indices.create.CreateIndexResponse;
import org.opensearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.opensearch.action.admin.indices.flush.FlushResponse;
import org.opensearch.action.admin.indices.get.GetIndexResponse;
import org.opensearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.opensearch.action.admin.indices.refresh.RefreshResponse;
import org.opensearch.action.admin.indices.segments.IndicesSegmentResponse;
import org.opensearch.action.admin.indices.segments.PitSegmentsRequest;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkRequestBuilder;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.delete.DeleteRequestBuilder;
import org.opensearch.action.delete.DeleteResponse;
import org.opensearch.action.explain.ExplainRequest;
import org.opensearch.action.explain.ExplainRequestBuilder;
import org.opensearch.action.explain.ExplainResponse;
import org.opensearch.action.fieldcaps.FieldCapabilitiesRequest;
import org.opensearch.action.fieldcaps.FieldCapabilitiesRequestBuilder;
import org.opensearch.action.fieldcaps.FieldCapabilitiesResponse;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetRequestBuilder;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.get.MultiGetRequest;
import org.opensearch.action.get.MultiGetRequestBuilder;
import org.opensearch.action.get.MultiGetResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexRequestBuilder;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.ClearScrollRequest;
import org.opensearch.action.search.ClearScrollRequestBuilder;
import org.opensearch.action.search.ClearScrollResponse;
import org.opensearch.action.search.CreatePitRequest;
import org.opensearch.action.search.CreatePitResponse;
import org.opensearch.action.search.DeletePitRequest;
import org.opensearch.action.search.DeletePitResponse;
import org.opensearch.action.search.GetAllPitNodesRequest;
import org.opensearch.action.search.GetAllPitNodesResponse;
import org.opensearch.action.search.MultiSearchRequest;
import org.opensearch.action.search.MultiSearchRequestBuilder;
import org.opensearch.action.search.MultiSearchResponse;
import org.opensearch.action.search.SearchPhaseExecutionException;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.search.SearchScrollRequest;
import org.opensearch.action.search.SearchScrollRequestBuilder;
import org.opensearch.action.support.WriteRequest.RefreshPolicy;
import org.opensearch.action.support.clustermanager.AcknowledgedResponse;
import org.opensearch.action.termvectors.MultiTermVectorsRequest;
import org.opensearch.action.termvectors.MultiTermVectorsRequestBuilder;
import org.opensearch.action.termvectors.MultiTermVectorsResponse;
import org.opensearch.action.termvectors.TermVectorsRequest;
import org.opensearch.action.termvectors.TermVectorsRequestBuilder;
import org.opensearch.action.termvectors.TermVectorsResponse;
import org.opensearch.action.update.UpdateRequest;
import org.opensearch.action.update.UpdateRequestBuilder;
import org.opensearch.action.update.UpdateResponse;
import org.opensearch.cluster.metadata.MappingMetadata;
import org.opensearch.common.action.ActionFuture;
import org.opensearch.common.document.DocumentField;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.settings.Settings.Builder;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.core.action.ActionListener;
import org.opensearch.core.action.ActionResponse;
import org.opensearch.core.rest.RestStatus;
import org.opensearch.index.query.InnerHitBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.reindex.UpdateByQueryRequest;
import org.opensearch.script.Script;
import org.opensearch.script.ScriptType;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.opensearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.opensearch.search.collapse.CollapseBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.opensearch.threadpool.ThreadPool;
import org.opensearch.transport.client.AdminClient;
import org.opensearch.transport.client.Client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.BaseEncoding;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Client for interacting with OpenSearch search engine.
 * Provides document indexing, searching, and administrative operations.
 */
public class SearchEngineClient implements Client {

    /**
     * Default constructor.
     */
    public SearchEngineClient() {
        // Default constructor
    }

    private static final Logger logger = LogManager.getLogger(SearchEngineClient.class);

    private static final String DOC_INDEX = "fess";

    private static final String LOG_INDEX_PREFIX = "fess_log";

    private static final String USER_INDEX_PREFIX = "fess_user";

    private static final String CONFIG_INDEX_PREFIX = "fess_config";

    /** OpenSearch runner for managing the embedded search engine */
    protected OpenSearchRunner runner;

    /** OpenSearch client for executing operations */
    protected Client client;

    /** Configuration settings for the search engine */
    protected Map<String, String> settings;

    /** Path to index configuration resources */
    protected String indexConfigPath = "fess_indices";

    /** List of index configuration files to load */
    protected List<String> indexConfigList = new ArrayList<>();

    /** Map of configuration types to their respective configuration files */
    protected Map<String, List<String>> configListMap = new HashMap<>();

    /** Scroll timeout for search operations */
    protected String scrollForSearch = "1m";

    /** Batch size for delete operations */
    protected int sizeForDelete = 100;

    /** Scroll timeout for delete operations */
    protected String scrollForDelete = "1m";

    /** Batch size for update operations */
    protected int sizeForUpdate = 100;

    /** Scroll timeout for update operations */
    protected String scrollForUpdate = "1m";

    /** Maximum retry attempts for configuration synchronization status checks */
    protected int maxConfigSyncStatusRetry = 10;

    /** Maximum retry attempts for search engine status checks */
    protected int maxEsStatusRetry = 60;

    /** Name of the search engine cluster */
    protected String clusterName = "fesen";

    /** List of rewrite rules for document settings */
    protected final List<UnaryOperator<String>> docSettingRewriteRuleList = new ArrayList<>();

    /** List of rewrite rules for document mappings */
    protected final List<UnaryOperator<String>> docMappingRewriteRuleList = new ArrayList<>();

    /** Whether to use pipelines for document processing */
    protected boolean usePipeline = false;

    /**
     * Adds an index configuration file path to be loaded.
     *
     * @param path path to the index configuration file
     */
    public void addIndexConfig(final String path) {
        indexConfigList.add(path);
    }

    /**
     * Adds a configuration file for a specific index.
     *
     * @param index the index name
     * @param path  path to the configuration file
     */
    public void addConfigFile(final String index, final String path) {
        configListMap.computeIfAbsent(index, k -> new ArrayList<>()).add(path);
    }

    /**
     * Sets the configuration settings for the search engine.
     *
     * @param settings map of configuration key-value pairs
     */
    public void setSettings(final Map<String, String> settings) {
        this.settings = settings;
    }

    /**
     * Gets the current cluster health status.
     *
     * @return the cluster health status name
     */
    public String getStatus() {
        return admin().cluster()
                .prepareHealth()
                .execute()
                .actionGet(ComponentUtil.getFessConfig().getIndexHealthTimeout())
                .getStatus()
                .name();
    }

    /**
     * Sets the OpenSearch runner for embedded mode.
     *
     * @param runner the OpenSearch runner instance
     */
    public void setRunner(final OpenSearchRunner runner) {
        this.runner = runner;
    }

    /**
     * Checks if the search engine is running in embedded mode.
     *
     * @return true if running in embedded mode, false otherwise
     */
    public boolean isEmbedded() {
        return runner != null;
    }

    /**
     * Enables the use of ingest pipelines for document processing.
     */
    public void usePipeline() {
        usePipeline = true;
    }

    /**
     * Resolves a hostname to an InetAddress.
     *
     * @param host the hostname to resolve
     * @return the resolved InetAddress
     * @throws FessSystemException if hostname resolution fails
     */
    protected InetAddress getInetAddressByName(final String host) {
        try {
            return InetAddress.getByName(host);
        } catch (final UnknownHostException e) {
            throw new FessSystemException("Failed to resolve the hostname: " + host, e);
        }
    }

    /**
     * Initializes the search engine client and configures indices.
     * Called automatically after dependency injection is complete.
     */
    @PostConstruct
    public void open() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        if (StringUtil.isNotBlank(fessConfig.getIndexDictionaryPrefix())) {
            String dictionaryPath = System.getProperty("fess.dictionary.path", StringUtil.EMPTY);
            if (StringUtil.isBlank(dictionaryPath)) {
                System.setProperty("fess.dictionary.path", fessConfig.getIndexDictionaryPrefix() + "/");
            } else {
                if (!dictionaryPath.endsWith("/")) {
                    dictionaryPath = dictionaryPath + "/";
                }
                System.setProperty("fess.dictionary.path", dictionaryPath + fessConfig.getIndexDictionaryPrefix() + "/");
            }
        }

        String httpAddress = SystemUtil.getSearchEngineHttpAddress();
        if (StringUtil.isBlank(httpAddress) && runner == null) {
            switch (fessConfig.getFesenType()) {
            case Constants.FESEN_TYPE_CLOUD:
            case Constants.FESEN_TYPE_AWS:
                httpAddress = org.codelibs.fess.util.ResourceUtil.getFesenHttpUrl();
                break;
            default:
                runner = new OpenSearchRunner();
                final Configs config = newConfigs().clusterName(clusterName).numOfNode(1).useLogger();
                final String esDir = System.getProperty("fess.es.dir");
                if (esDir != null) {
                    config.basePath(esDir);
                }
                config.disableESLogger();
                runner.onBuild((number, settingsBuilder) -> {
                    final File moduleDir = new File(esDir, "modules");
                    if (moduleDir.isDirectory()) {
                        settingsBuilder.put("path.modules", moduleDir.getAbsolutePath());
                    } else {
                        settingsBuilder.put("path.modules", new File(System.getProperty("user.dir"), "modules").getAbsolutePath());
                    }
                    final File pluginDir = new File(esDir, "plugins");
                    if (pluginDir.isDirectory()) {
                        settingsBuilder.put("path.plugins", pluginDir.getAbsolutePath());
                    } else {
                        settingsBuilder.put("path.plugins", new File(System.getProperty("user.dir"), "plugins").getAbsolutePath());
                    }
                    if (settings != null) {
                        settingsBuilder.putProperties(settings, s -> s);
                    }
                });
                runner.build(config);

                final int port = runner.node().settings().getAsInt("http.port", 9200);
                httpAddress = "http://localhost:" + port;
                logger.warn("Embedded OpenSearch is running. This configuration is not recommended for production use.");
                break;
            }
        }
        client = createHttpClient(fessConfig, httpAddress);

        if (StringUtil.isNotBlank(httpAddress)) {
            System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, httpAddress);
        }

        waitForYellowStatus(fessConfig);

        indexConfigList.forEach(configName -> {
            final String[] values = configName.split("/");
            if (values.length == 2) {
                final String configIndex = values[0];
                final String configType = values[1];

                final boolean isFessIndex = DOC_INDEX.equals(configIndex);
                final String indexName;
                if (isFessIndex) {
                    final boolean exists = existsIndex(fessConfig.getIndexDocumentUpdateIndex());
                    if (!exists) {
                        indexName = generateNewIndexName(configIndex);
                        createIndex(configIndex, indexName);
                        createAlias(configIndex, indexName);
                    } else {
                        client.admin()
                                .cluster()
                                .prepareHealth(fessConfig.getIndexDocumentUpdateIndex())
                                .setWaitForYellowStatus()
                                .execute()
                                .actionGet(fessConfig.getIndexIndicesTimeout());
                        final GetIndexResponse response = client.admin()
                                .indices()
                                .prepareGetIndex()
                                .addIndices(fessConfig.getIndexDocumentUpdateIndex())
                                .execute()
                                .actionGet(fessConfig.getIndexIndicesTimeout());
                        final String[] indices = response.indices();
                        if (indices.length == 1) {
                            indexName = indices[0];
                        } else {
                            indexName = configIndex;
                        }
                    }
                } else {
                    if (configIndex.startsWith(CONFIG_INDEX_PREFIX)) {
                        final String name = fessConfig.getIndexConfigIndex();
                        indexName = configIndex.replaceFirst(Pattern.quote(CONFIG_INDEX_PREFIX), name);
                    } else if (configIndex.startsWith(USER_INDEX_PREFIX)) {
                        final String name = fessConfig.getIndexUserIndex();
                        indexName = configIndex.replaceFirst(Pattern.quote(USER_INDEX_PREFIX), name);
                    } else if (configIndex.startsWith(LOG_INDEX_PREFIX)) {
                        final String name = fessConfig.getIndexLogIndex();
                        indexName = configIndex.replaceFirst(Pattern.quote(LOG_INDEX_PREFIX), name);
                    } else {
                        throw new FessSystemException("Unknown config index: " + configIndex);
                    }
                    final boolean exists = existsIndex(indexName);
                    if (!exists) {
                        createIndex(configIndex, indexName);
                        createAlias(configIndex, indexName);
                    }
                }

                addMapping(configIndex, configType, indexName);
            } else {
                logger.warn("Invalid index config name: {}", configName);
            }
        });
    }

    /**
     * Creates an HTTP client for connecting to the search engine.
     *
     * @param fessConfig the Fess configuration
     * @param host       the search engine host address
     * @return the configured HTTP client
     */
    protected Client createHttpClient(final FessConfig fessConfig, final String host) {
        final String[] hosts =
                split(host, ",").get(stream -> stream.map(String::trim).filter(StringUtil::isNotEmpty).toArray(n -> new String[n]));
        final Builder builder = Settings.builder()
                .putList("http.hosts", hosts)
                .put("processors", fessConfig.availableProcessors())
                .put("http.heartbeat_interval", fessConfig.getFesenHeartbeatInterval());
        final String username = fessConfig.getFesenUsername();
        final String password = fessConfig.getFesenPassword();
        if (StringUtil.isNotBlank(username) && StringUtil.isNotBlank(password)) {
            builder.put(Constants.FESEN_USERNAME, username);
            builder.put(Constants.FESEN_PASSWORD, password);
        }
        final String authorities = fessConfig.getFesenHttpSslCertificateAuthorities();
        if (StringUtil.isNotBlank(authorities)) {
            builder.put("http.ssl.certificate_authorities", authorities);
        }
        return new HttpClient(builder.build(), null);
    }

    /**
     * Checks if an index exists in the search engine.
     *
     * @param indexName the name of the index to check
     * @return true if the index exists, false otherwise
     */
    public boolean existsIndex(final String indexName) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        boolean exists = false;
        try {
            final IndicesExistsResponse response =
                    client.admin().indices().prepareExists(indexName).execute().actionGet(fessConfig.getIndexSearchTimeout());
            exists = response.isExists();
        } catch (final Exception e) {
            logger.debug("Failed to check {} index status.", indexName, e);
        }
        return exists;
    }

    /**
     * Copies documents from one index to another with optional transformation.
     *
     * @param fromIndex        the source index name
     * @param toIndex          the destination index name
     * @param waitForCompletion whether to wait for the operation to complete
     * @return true if the copy operation was successful, false otherwise
     */
    public boolean copyDocIndex(final String fromIndex, final String toIndex, final boolean waitForCompletion) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String source = fessConfig.getIndexReindexBody()//
                .replace("__SOURCE_INDEX__", fromIndex)//
                .replace("__SIZE__", fessConfig.getIndexReindexSize())//
                .replace("__DEST_INDEX__", toIndex)//
                .replace("__SCRIPT_SOURCE__", ComponentUtil.getLanguageHelper().getReindexScriptSource());
        return reindex(fromIndex, toIndex, source, waitForCompletion);
    }

    /**
     * Reindexes documents from one index to another.
     *
     * @param fromIndex        the source index name
     * @param toIndex          the destination index name
     * @param waitForCompletion whether to wait for the operation to complete
     * @return true if the reindex operation was successful, false otherwise
     */
    public boolean reindex(final String fromIndex, final String toIndex, final boolean waitForCompletion) {
        final String template = """
                {"source":{"index":"__SOURCE_INDEX__","size":__SIZE__},"dest":{"index":"__DEST_INDEX__"}}
                """;
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String source = template //
                .replace("__SOURCE_INDEX__", fromIndex)//
                .replace("__SIZE__", fessConfig.getIndexReindexSize())//
                .replace("__DEST_INDEX__", toIndex);
        return reindex(fromIndex, toIndex, source, waitForCompletion);
    }

    /**
     * Performs a reindex operation with custom source configuration.
     *
     * @param fromIndex        the source index name
     * @param toIndex          the destination index name
     * @param source           the reindex configuration JSON
     * @param waitForCompletion whether to wait for the operation to complete
     * @return true if the reindex operation was successful, false otherwise
     */
    protected boolean reindex(final String fromIndex, final String toIndex, final String source, final boolean waitForCompletion) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String refresh = StringUtil.isNotBlank(fessConfig.getIndexReindexRefresh()) ? fessConfig.getIndexReindexRefresh() : null;
        final String requestsPerSecond = getReindexRequestsPerSecound(fessConfig);
        final String scroll = StringUtil.isNotBlank(fessConfig.getIndexReindexScroll()) ? fessConfig.getIndexReindexScroll() : null;
        final String maxDocs = StringUtil.isNotBlank(fessConfig.getIndexReindexMaxDocs()) ? fessConfig.getIndexReindexMaxDocs() : null;
        try (CurlResponse response = ComponentUtil.getCurlHelper()
                .post("/_reindex")
                .param("refresh", refresh)
                .param("requests_per_second", requestsPerSecond)
                .param("scroll", scroll)
                .param("max_docs", maxDocs)
                .param("wait_for_completion", Boolean.toString(waitForCompletion))
                .body(source)
                .execute()) {
            if (response.getHttpStatusCode() == 200) {
                return true;
            }
            logger.warn("Failed to reindex from {} to {}", fromIndex, toIndex);
        } catch (final IOException e) {
            logger.warn("Failed to reindex from {} to {}", fromIndex, toIndex, e);
        }
        return false;
    }

    /**
     * Calculates the requests per second setting for reindex operations.
     *
     * @param fessConfig the Fess configuration
     * @return the requests per second value, or null for no limit
     */
    protected String getReindexRequestsPerSecound(final FessConfig fessConfig) {
        if (StringUtil.isBlank(fessConfig.getIndexReindexRequestsPerSecond())) {
            return null;
        }
        final String value = fessConfig.getIndexReindexRequestsPerSecond();
        if ("adaptive".equalsIgnoreCase(value)) {
            if (fessConfig.availableProcessors() >= 4) {
                return null;
            }
            final String requestsPerSecond = String.valueOf(fessConfig.getIndexReindexSizeAsInteger() * fessConfig.availableProcessors());
            logger.info("Set requests_per_second to {}", requestsPerSecond);
            return requestsPerSecond;
        }
        return value;
    }

    /**
     * Creates a new index with default settings.
     *
     * @param index     the index configuration name
     * @param indexName the actual index name to create
     * @return true if the index was created successfully, false otherwise
     */
    public boolean createIndex(final String index, final String indexName) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return createIndex(index, indexName, fessConfig.getIndexNumberOfShards(), fessConfig.getIndexAutoExpandReplicas(), true);
    }

    /**
     * Creates a new index with specified settings.
     *
     * @param index              the index configuration name
     * @param indexName          the actual index name to create
     * @param numberOfShards     the number of primary shards
     * @param autoExpandReplicas the auto expand replicas setting
     * @param uploadConfig       whether to upload configuration files
     * @return true if the index was created successfully, false otherwise
     */
    public boolean createIndex(final String index, final String indexName, final String numberOfShards, final String autoExpandReplicas,
            final boolean uploadConfig) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final String fesenType = fessConfig.getFesenType();
        if (uploadConfig) {
            switch (fesenType) {
            case Constants.FESEN_TYPE_CLOUD:
            case Constants.FESEN_TYPE_AWS:
                // nothing
                break;
            default:
                waitForConfigSyncStatus();
                sendConfigFiles(index);
                break;
            }
        }

        final String indexConfigFile = getResourcePath(indexConfigPath, fesenType, "/" + index + ".json");
        try {
            final String source = readIndexSetting(fesenType, indexConfigFile, numberOfShards, autoExpandReplicas);
            final CreateIndexResponse indexResponse = client.admin()
                    .indices()
                    .prepareCreate(indexName)
                    .setSource(source, XContentType.JSON)
                    .execute()
                    .actionGet(fessConfig.getIndexIndicesTimeout());
            if (indexResponse.isAcknowledged()) {
                logger.info("Created {} index.", indexName);
                return true;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to create {} index.", indexName);
            }
        } catch (final Exception e) {
            logger.warn("{} is not found.", indexConfigFile, e);
        }

        return false;
    }

    /**
     * Deletes an index from the search engine.
     *
     * @param indexName the name of the index to delete
     * @return true if the index was deleted successfully, false otherwise
     */
    public boolean deleteIndex(final String indexName) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        try {
            final AcknowledgedResponse response =
                    client.admin().indices().prepareDelete(indexName).execute().actionGet(fessConfig.getIndexIndicesTimeout());
            return response.isAcknowledged();
        } catch (final Exception e) {
            logger.debug("Failed to delete {}.", indexName, e);
        }
        return false;
    }

    /**
     * Reads and processes index settings from configuration file.
     *
     * @param fesenType          the search engine type
     * @param indexConfigFile    the path to the index configuration file
     * @param numberOfShards     the number of primary shards
     * @param autoExpandReplicas the auto expand replicas setting
     * @return the processed index settings JSON
     */
    protected String readIndexSetting(final String fesenType, final String indexConfigFile, final String numberOfShards,
            final String autoExpandReplicas) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        String source = FileUtil.readUTF8(indexConfigFile);
        String dictionaryPath = System.getProperty("fess.dictionary.path", StringUtil.EMPTY);
        if (StringUtil.isNotBlank(dictionaryPath) && !dictionaryPath.endsWith("/")) {
            dictionaryPath = dictionaryPath + "/";
        }
        source = source.replaceAll(Pattern.quote("${fess.dictionary.path}"), dictionaryPath)//
                .replaceAll(Pattern.quote("${fess.index.codec}"), fessConfig.getIndexCodec())//
                .replaceAll(Pattern.quote("${fess.index.number_of_shards}"), numberOfShards)//
                .replaceAll(Pattern.quote("${fess.index.auto_expand_replicas}"), autoExpandReplicas);
        for (final UnaryOperator<String> rule : docSettingRewriteRuleList) {
            source = rule.apply(source);
        }
        return source;
    }

    /**
     * Adds a rewrite rule for document settings.
     *
     * @param rule the rewrite rule to apply to document settings
     */
    public void addDocumentSettingRewriteRule(final UnaryOperator<String> rule) {
        docSettingRewriteRuleList.add(rule);
    }

    /**
     * Gets the resource path for configuration files, checking type-specific variants first.
     *
     * @param basePath the base path for resources
     * @param type     the search engine type
     * @param path     the relative path to the resource
     * @return the full resource path
     */
    protected String getResourcePath(final String basePath, final String type, final String path) {
        final String target = basePath + "/_" + type + path;
        if (ResourceUtil.getResourceNoException(target) != null) {
            return target;
        }
        return basePath + path;
    }

    /**
     * Adds field mappings to an index.
     *
     * @param index     the index configuration name
     * @param docType   the document type
     * @param indexName the actual index name
     */
    public void addMapping(final String index, final String docType, final String indexName) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final GetMappingsResponse getMappingsResponse =
                client.admin().indices().prepareGetMappings(indexName).execute().actionGet(fessConfig.getIndexIndicesTimeout());
        final Map<String, MappingMetadata> indexMappings = getMappingsResponse.mappings();
        if (indexMappings == null || !indexMappings.containsKey("properties")) {
            String source = null;
            final String mappingFile = getResourcePath(indexConfigPath, fessConfig.getFesenType(), "/" + index + "/" + docType + ".json");
            try {
                source = FileUtil.readUTF8(mappingFile);
                if (DOC_INDEX.equals(index)) {
                    for (final UnaryOperator<String> rule : docMappingRewriteRuleList) {
                        source = rule.apply(source);
                    }
                }
            } catch (final Exception e) {
                logger.warn("{} is not found.", mappingFile, e);
            }
            try {
                final AcknowledgedResponse putMappingResponse = client.admin()
                        .indices()
                        .preparePutMapping(indexName)
                        .setSource(source, XContentType.JSON)
                        .execute()
                        .actionGet(fessConfig.getIndexIndicesTimeout());
                if (putMappingResponse.isAcknowledged()) {
                    logger.info("Created {}/{} mapping.", indexName, docType);
                } else {
                    logger.warn("Failed to create {}/{} mapping.", indexName, docType);
                }

                final String dataPath = getResourcePath(indexConfigPath, fessConfig.getFesenType(), "/" + index + "/" + docType + ".bulk");
                if (ResourceUtil.isExist(dataPath)) {
                    insertBulkData(fessConfig, indexName, dataPath);
                }
                split(fessConfig.getAppExtensionNames(), ",").of(stream -> stream.filter(StringUtil::isNotBlank).forEach(name -> {
                    final String bulkPath =
                            getResourcePath(indexConfigPath, fessConfig.getFesenType(), "/" + index + "/" + docType + "_" + name + ".bulk");
                    if (ResourceUtil.isExist(bulkPath)) {
                        insertBulkData(fessConfig, indexName, bulkPath);
                    }
                }));
            } catch (final Exception e) {
                logger.warn("Failed to create {}/{} mapping.", indexName, docType, e);
            }
        } else if (logger.isDebugEnabled()) {
            logger.debug("{}/{} mapping exists.", indexName, docType);
        }
    }

    /**
     * Adds a rewrite rule for document mappings.
     *
     * @param rule the rewrite rule to apply to document mappings
     */
    public void addDocumentMappingRewriteRule(final UnaryOperator<String> rule) {
        docMappingRewriteRuleList.add(rule);
    }

    /**
     * Updates index aliases to point to a new index.
     *
     * @param newIndex the new index to point aliases to
     * @return true if the alias update was successful, false otherwise
     */
    public boolean updateAlias(final String newIndex) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String updateAlias = fessConfig.getIndexDocumentUpdateIndex();
        final String searchAlias = fessConfig.getIndexDocumentSearchIndex();
        final GetIndexResponse response1 =
                client.admin().indices().prepareGetIndex().addIndices(updateAlias).execute().actionGet(fessConfig.getIndexIndicesTimeout());
        final String[] updateIndices = response1.indices();
        final GetIndexResponse response2 =
                client.admin().indices().prepareGetIndex().addIndices(searchAlias).execute().actionGet(fessConfig.getIndexIndicesTimeout());
        final String[] searchIndices = response2.indices();

        final IndicesAliasesRequestBuilder builder =
                client.admin().indices().prepareAliases().addAlias(newIndex, updateAlias).addAlias(newIndex, searchAlias);
        for (final String index : updateIndices) {
            builder.removeAlias(index, updateAlias);
        }
        for (final String index : searchIndices) {
            builder.removeAlias(index, searchAlias);
        }
        final AcknowledgedResponse response = builder.execute().actionGet(fessConfig.getIndexIndicesTimeout());
        return response.isAcknowledged();
    }

    /**
     * Creates aliases for a newly created index.
     *
     * @param index            the index configuration name
     * @param createdIndexName the actual index name that was created
     */
    protected void createAlias(final String index, final String createdIndexName) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        // alias
        final String aliasConfigDirPath = getResourcePath(indexConfigPath, fessConfig.getFesenType(), "/" + index + "/alias");
        try {
            final File aliasConfigDir = ResourceUtil.getResourceAsFile(aliasConfigDirPath);
            if (aliasConfigDir.isDirectory()) {
                stream(aliasConfigDir.listFiles((dir, name) -> name.endsWith(".json"))).of(stream -> stream.forEach(f -> {
                    String aliasName = f.getName().replaceFirst(".json$", "");
                    if (index.equals(DOC_INDEX)) {
                        if ("fess.search".equals(aliasName)) {
                            aliasName = fessConfig.getIndexDocumentSearchIndex();
                        } else if ("fess.update".equals(aliasName)) {
                            aliasName = fessConfig.getIndexDocumentUpdateIndex();
                        }
                    } else if (index.startsWith(CONFIG_INDEX_PREFIX)) {
                        final String name = fessConfig.getIndexConfigIndex();
                        if ("fess_basic_config".equals(aliasName) && !CONFIG_INDEX_PREFIX.equals(name)) {
                            aliasName = aliasName.replaceFirst("fess_basic_config", "basic_" + name);
                        } else {
                            aliasName = aliasName.replaceFirst(Pattern.quote(CONFIG_INDEX_PREFIX), name);
                        }
                    } else if (index.startsWith(USER_INDEX_PREFIX)) {
                        final String name = fessConfig.getIndexUserIndex();
                        aliasName = aliasName.replaceFirst(Pattern.quote(USER_INDEX_PREFIX), name);
                    } else if (index.startsWith(LOG_INDEX_PREFIX)) {
                        final String name = fessConfig.getIndexLogIndex();
                        aliasName = aliasName.replaceFirst(Pattern.quote(LOG_INDEX_PREFIX), name);
                    }
                    String source = FileUtil.readUTF8(f);
                    if ("{}".equals(source.trim())) {
                        source = null;
                    }
                    final AcknowledgedResponse response = client.admin()
                            .indices()
                            .prepareAliases()
                            .addAlias(createdIndexName, aliasName, source)
                            .execute()
                            .actionGet(fessConfig.getIndexIndicesTimeout());
                    if (response.isAcknowledged()) {
                        logger.info("Created {} alias for {}", aliasName, createdIndexName);
                    } else if (logger.isDebugEnabled()) {
                        logger.debug("Failed to create {} alias for {}", aliasName, createdIndexName);
                    }
                }));
            }
        } catch (final ResourceNotFoundRuntimeException e) {
            // ignore
        } catch (final Exception e) {
            logger.warn("{} is not found.", aliasConfigDirPath, e);
        }
    }

    /**
     * Sends configuration files to the search engine for an index.
     *
     * @param index the index configuration name
     */
    protected void sendConfigFiles(final String index) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        configListMap.getOrDefault(index, Collections.emptyList()).forEach(path -> {
            String source = null;
            final String filePath = indexConfigPath + "/" + index + "/" + path;
            final String dictionaryPath;
            if (StringUtil.isNotBlank(fessConfig.getIndexDictionaryPrefix())) {
                dictionaryPath = fessConfig.getIndexDictionaryPrefix() + "/" + path;
            } else {
                dictionaryPath = path;
            }
            try {
                source = FileUtil.readUTF8(filePath);
                try (CurlResponse response =
                        ComponentUtil.getCurlHelper().post("/_configsync/file").param("path", dictionaryPath).body(source).execute()) {
                    if (response.getHttpStatusCode() == 200) {
                        logger.info("Register {} to {}", path, index);
                    } else if (response.getContentException() != null) {
                        logger.warn("Invalid request for {}.", path, response.getContentException());
                    } else {
                        logger.warn("Invalid request for {}. The response is {}", path, response.getContentAsString());
                    }
                }
            } catch (final Exception e) {
                logger.warn("Failed to register {}", filePath, e);
            }
        });
        try (CurlResponse response = ComponentUtil.getCurlHelper().post("/_configsync/flush").execute()) {
            if (response.getHttpStatusCode() == 200) {
                logger.info("Flushed config files.");
            } else {
                logger.warn("Failed to flush config files.");
            }
        } catch (final Exception e) {
            logger.warn("Failed to flush config files.", e);
        }
    }

    /**
     * Flushes configuration files to the search engine and executes a callback.
     *
     * @param callback the callback to execute after flushing
     */
    public void flushConfigFiles(final Runnable callback) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final String fesenType = fessConfig.getFesenType();
        switch (fesenType) {
        case Constants.FESEN_TYPE_CLOUD:
        case Constants.FESEN_TYPE_AWS:
            if (logger.isDebugEnabled()) {
                logger.debug("Skipped configsync flush: {}", fesenType);
            }
            callback.run();
            break;
        default:
            ComponentUtil.getCurlHelper().post("/_configsync/flush").execute(response -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("Flushed config files: {} => {}", fesenType, response.getContentAsString());
                }
                callback.run();
            }, e -> {
                logger.warn("Failed to flush config files.", e);
                callback.run();
            });
            break;
        }
    }

    /**
     * Generates a new index name with timestamp suffix.
     *
     * @param configIndex the base index configuration name
     * @return the generated index name with timestamp
     */
    protected String generateNewIndexName(final String configIndex) {
        return configIndex + "." + new SimpleDateFormat(Constants.DOCUMENT_INDEX_SUFFIX_PATTERN).format(new Date());
    }

    /**
     * Inserts bulk data from a file into an index.
     *
     * @param fessConfig  the Fess configuration
     * @param configIndex the target index name
     * @param dataPath    the path to the bulk data file
     */
    protected void insertBulkData(final FessConfig fessConfig, final String configIndex, final String dataPath) {
        try {
            final BulkRequestBuilder builder = client.prepareBulk();
            final ObjectMapper mapper = new ObjectMapper();
            final String userIndex = fessConfig.getIndexUserIndex() + ".user";
            Arrays.stream(FileUtil.readUTF8(dataPath).split("\n"))
                    .map(line -> line//
                            .replace("\"_index\":\"fess_config.", "\"_index\":\"" + fessConfig.getIndexConfigIndex() + ".")//
                            .replace("\"_index\":\"fess_user.", "\"_index\":\"" + fessConfig.getIndexUserIndex() + ".")//
                            .replace("\"_index\":\"fess_log.", "\"_index\":\"" + fessConfig.getIndexLogIndex() + "."))
                    .reduce((prev, line) -> {
                        try {
                            if (StringUtil.isBlank(prev)) {
                                final Map<String, Map<String, String>> result =
                                        mapper.readValue(line, new TypeReference<Map<String, Map<String, String>>>() {
                                        });
                                if (result.containsKey("index") || result.containsKey("update")) {
                                    return line;
                                }
                                if (result.containsKey("delete")) {
                                    return StringUtil.EMPTY;
                                }
                            } else {
                                final Map<String, Map<String, String>> result =
                                        mapper.readValue(prev, new TypeReference<Map<String, Map<String, String>>>() {
                                        });
                                if (result.containsKey("index")) {
                                    String source = line;
                                    if (userIndex.equals(configIndex)) {
                                        source = source.replace("${fess.index.initial_password}",
                                                ComponentUtil.getComponent(FessLoginAssist.class)
                                                        .encryptPassword(fessConfig.getIndexUserInitialPassword()));
                                    }
                                    final IndexRequestBuilder requestBuilder = client.prepareIndex()
                                            .setIndex(configIndex)
                                            .setId(result.get("index").get("_id"))
                                            .setSource(source, XContentType.JSON);
                                    builder.add(requestBuilder);
                                }
                            }
                        } catch (final Exception e) {
                            logger.warn("Failed to parse {}", dataPath, e);
                        }
                        return StringUtil.EMPTY;
                    });
            final BulkResponse response = builder.execute().actionGet(fessConfig.getIndexBulkTimeout());
            if (response.hasFailures()) {
                logger.warn("Failed to register {}: {}", dataPath, response.buildFailureMessage());
            }
        } catch (final Exception e) {
            logger.warn("Failed to create {} mapping.", configIndex, e);
        }
    }

    /**
     * Waits for the search engine cluster to reach yellow or green status.
     *
     * @param fessConfig the Fess configuration
     * @throws ContainerInitFailureException if the cluster doesn't become available
     */
    protected void waitForYellowStatus(final FessConfig fessConfig) {
        Exception cause = null;
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final long startTime = systemHelper.getCurrentTimeAsLong();
        for (int i = 0; i < maxEsStatusRetry; i++) {
            try {
                final ClusterHealthResponse response = client.admin()
                        .cluster()
                        .prepareHealth()
                        .setWaitForYellowStatus()
                        .execute()
                        .actionGet(fessConfig.getIndexHealthTimeout());
                if (logger.isDebugEnabled()) {
                    logger.debug("Fesen Cluster Status: {}", response.getStatus());
                }
                return;
            } catch (final Exception e) {
                cause = e;
            }
            if (cause instanceof OpenSearchStatusException) {
                final RestStatus status = ((OpenSearchStatusException) cause).status();
                switch (status) {
                case UNAUTHORIZED:
                    logger.warn("[{}] Unauthorized access: {}", i, SystemUtil.getSearchEngineHttpAddress(), cause);
                    break;
                default:
                    logger.debug("[{}][{}] Failed to access to Fesen ({})", i, status, SystemUtil.getSearchEngineHttpAddress(), cause);
                    break;
                }
            } else if (logger.isDebugEnabled()) {
                logger.debug("[{}] Failed to access to Fesen ({})", i, SystemUtil.getSearchEngineHttpAddress(), cause);
            }
            ThreadUtil.sleep(1000L);
        }
        final String message =
                "Fesen (" + SystemUtil.getSearchEngineHttpAddress() + ") is not available. Check the state of your Fesen cluster ("
                        + clusterName + ") in " + (systemHelper.getCurrentTimeAsLong() - startTime) + "ms.";
        throw new ContainerInitFailureException(message, cause);
    }

    /**
     * Waits for the configuration synchronization service to become available.
     *
     * @throws FessSystemException if ConfigSync doesn't become available
     */
    protected void waitForConfigSyncStatus() {
        FessSystemException cause = null;
        for (int i = 0; i < maxConfigSyncStatusRetry; i++) {
            try (CurlResponse response = ComponentUtil.getCurlHelper().get("/_configsync/wait").param("status", "green").execute()) {
                final int httpStatusCode = response.getHttpStatusCode();
                if (httpStatusCode == 200) {
                    logger.info("ConfigSync is ready.");
                    return;
                }
                final String message = "Configsync is not available. HTTP Status is " + httpStatusCode;
                if (response.getContentException() != null) {
                    throw new FessSystemException(message, response.getContentException());
                }
                throw new FessSystemException(message);
            } catch (final Exception e) {
                cause = new FessSystemException("Configsync is not available.", e);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to access to configsync:{}", i, cause);
            }
            ThreadUtil.sleep(1000L);
        }
        throw cause;
    }

    @Override
    @PreDestroy
    public void close() {
        if (runner != null) {
            try {
                client.admin()
                        .indices()
                        .prepareFlush()
                        .setForce(true)
                        .execute()
                        .actionGet(ComponentUtil.getFessConfig().getIndexIndicesTimeout());
            } catch (final Exception e) {
                logger.warn("Failed to flush indices.", e);
            }
        }
        try {
            client.close();
        } catch (final OpenSearchException e) {
            logger.warn("Failed to close Client: {}", client, e);
        }
    }

    /**
     * Updates documents in an index using a query.
     *
     * @param index   the index name
     * @param option  function to customize the search request
     * @param builder function to build update requests from search hits
     * @return the number of documents processed
     */
    public long updateByQuery(final String index, final Function<SearchRequestBuilder, SearchRequestBuilder> option,
            final BiFunction<UpdateRequestBuilder, SearchHit, UpdateRequestBuilder> builder) {

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        SearchResponse response = option.apply(client.prepareSearch(index)
                .setScroll(scrollForUpdate)
                .setSize(sizeForUpdate)
                .setPreference(Constants.SEARCH_PREFERENCE_LOCAL)).execute().actionGet(fessConfig.getIndexScrollSearchTimeout());

        int count = 0;
        String scrollId = response.getScrollId();
        try {
            while (scrollId != null) {
                final SearchHits searchHits = response.getHits();
                final SearchHit[] hits = searchHits.getHits();
                if (hits.length == 0) {
                    break;
                }

                final BulkRequestBuilder bulkRequest = client.prepareBulk();
                for (final SearchHit hit : hits) {
                    final UpdateRequestBuilder requestBuilder =
                            builder.apply(client.prepareUpdate().setIndex(index).setId(hit.getId()), hit);
                    if (requestBuilder != null) {
                        bulkRequest.add(requestBuilder);
                    }
                    count++;
                }
                final BulkResponse bulkResponse = bulkRequest.execute().actionGet(fessConfig.getIndexBulkTimeout());
                if (bulkResponse.hasFailures()) {
                    throw new IllegalBehaviorStateException(bulkResponse.buildFailureMessage());
                }

                response = client.prepareSearchScroll(scrollId)
                        .setScroll(scrollForUpdate)
                        .execute()
                        .actionGet(fessConfig.getIndexBulkTimeout());
                if (!scrollId.equals(response.getScrollId())) {
                    deleteScrollContext(scrollId);
                }
                scrollId = response.getScrollId();
            }
        } finally {
            deleteScrollContext(scrollId);
        }
        return count;
    }

    /**
     * Deletes documents in an index matching a query.
     *
     * @param index        the index name
     * @param queryBuilder the query to match documents for deletion
     * @return the number of documents deleted
     */
    public long deleteByQuery(final String index, final QueryBuilder queryBuilder) {

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        SearchResponse response = client.prepareSearch(index)
                .setScroll(scrollForDelete)
                .setSize(sizeForDelete)
                .setFetchSource(new String[] { fessConfig.getIndexFieldId() }, null)
                .setQuery(queryBuilder)
                .setPreference(Constants.SEARCH_PREFERENCE_LOCAL)
                .execute()
                .actionGet(fessConfig.getIndexScrollSearchTimeout());

        int count = 0;
        String scrollId = response.getScrollId();
        try {
            while (scrollId != null) {
                final SearchHits searchHits = response.getHits();
                final SearchHit[] hits = searchHits.getHits();
                if (hits.length == 0) {
                    break;
                }

                final BulkRequestBuilder bulkRequest = client.prepareBulk();
                for (final SearchHit hit : hits) {
                    bulkRequest.add(client.prepareDelete().setIndex(index).setId(hit.getId()));
                    count++;
                }
                final BulkResponse bulkResponse = bulkRequest.execute().actionGet(fessConfig.getIndexBulkTimeout());
                if (bulkResponse.hasFailures()) {
                    throw new IllegalBehaviorStateException(bulkResponse.buildFailureMessage());
                }

                response = client.prepareSearchScroll(scrollId)
                        .setScroll(scrollForDelete)
                        .execute()
                        .actionGet(fessConfig.getIndexBulkTimeout());
                if (!scrollId.equals(response.getScrollId())) {
                    deleteScrollContext(scrollId);
                }
                scrollId = response.getScrollId();
            }
        } finally {
            deleteScrollContext(scrollId);
        }
        return count;
    }

    /**
     * Deletes a scroll context to free resources.
     *
     * @param scrollId the scroll ID to delete
     */
    protected void deleteScrollContext(final String scrollId) {
        if (scrollId != null) {
            client.prepareClearScroll()
                    .addScrollId(scrollId)
                    .execute(wrap(res -> {}, e -> logger.warn("Failed to clear the scroll context.", e)));
        }
    }

    /**
     * Retrieves a document by ID with custom conditions and result processing.
     *
     * @param <T>          the result type
     * @param index        the index name
     * @param id           the document ID
     * @param condition    the search condition
     * @param searchResult the result processor
     * @return the processed result
     */
    protected <T> T get(final String index, final String id, final SearchCondition<GetRequestBuilder> condition,
            final SearchResult<T, GetRequestBuilder, GetResponse> searchResult) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final long startTime = systemHelper.getCurrentTimeAsLong();

        GetResponse response = null;
        final GetRequestBuilder requestBuilder = client.prepareGet(index, id);
        if (condition.build(requestBuilder)) {
            response = requestBuilder.execute().actionGet(ComponentUtil.getFessConfig().getIndexSearchTimeout());
        }
        final long execTime = systemHelper.getCurrentTimeAsLong() - startTime;

        return searchResult.build(requestBuilder, execTime, OptionalEntity.ofNullable(response, () -> {}));
    }

    /**
     * Performs a search with custom conditions and result processing.
     *
     * @param <T>          the result type
     * @param index        the index name
     * @param condition    the search condition
     * @param searchResult the result processor
     * @return the processed search result
     * @throws InvalidQueryException if the query is invalid
     */
    public <T> T search(final String index, final SearchCondition<SearchRequestBuilder> condition,
            final SearchResult<T, SearchRequestBuilder, SearchResponse> searchResult) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final long startTime = systemHelper.getCurrentTimeAsLong();

        SearchResponse searchResponse = null;
        final SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        if (condition.build(searchRequestBuilder)) {

            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final long queryTimeout = fessConfig.getQueryTimeoutAsInteger().longValue();
            if (queryTimeout >= 0) {
                searchRequestBuilder.setTimeout(TimeValue.timeValueMillis(queryTimeout));
            }

            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Query DSL: {}", searchRequestBuilder);
                }
                searchResponse = searchRequestBuilder.execute().actionGet(ComponentUtil.getFessConfig().getIndexSearchTimeout());
            } catch (final SearchPhaseExecutionException e) {
                throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryParseError(UserMessages.GLOBAL_PROPERTY_KEY),
                        "Invalid query: " + searchRequestBuilder, e);
            } catch (final OpenSearchException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Cannot process {}", searchRequestBuilder, e);
                }
                throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryCannotProcess(UserMessages.GLOBAL_PROPERTY_KEY),
                        "Failed query: " + searchRequestBuilder, e);
            }
        }
        final long execTime = systemHelper.getCurrentTimeAsLong() - startTime;

        return searchResult.build(searchRequestBuilder, execTime, OptionalEntity.ofNullable(searchResponse, () -> {}));
    }

    /**
     * Performs a scroll search with default entity creation.
     *
     * @param index     the index name
     * @param condition the search condition
     * @param cursor    the cursor function to process each hit
     * @return the number of documents processed
     * @throws InvalidQueryException if the query is invalid
     */
    public long scrollSearch(final String index, final SearchCondition<SearchRequestBuilder> condition,
            final BooleanFunction<Map<String, Object>> cursor) {
        return scrollSearch(index, condition, getDefaultEntityCreator(), cursor);
    }

    /**
     * Performs a scroll search with custom entity creation.
     *
     * @param <T>       the entity type
     * @param index     the index name
     * @param condition the search condition
     * @param creator   the entity creator
     * @param cursor    the cursor function to process each entity
     * @return the number of documents processed
     * @throws InvalidQueryException if the query is invalid
     */
    public <T> long scrollSearch(final String index, final SearchCondition<SearchRequestBuilder> condition,
            final EntityCreator<T, SearchResponse, SearchHit> creator, final BooleanFunction<T> cursor) {
        long count = 0;

        final SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setScroll(scrollForSearch);
        if (condition.build(searchRequestBuilder)) {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();

            String scrollId = null;
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Query DSL: {}", searchRequestBuilder);
                }
                SearchResponse response = searchRequestBuilder.execute().actionGet(ComponentUtil.getFessConfig().getIndexSearchTimeout());

                scrollId = response.getScrollId();
                while (scrollId != null) {
                    final SearchHits searchHits = response.getHits();
                    final SearchHit[] hits = searchHits.getHits();
                    if (hits.length == 0) {
                        break;
                    }

                    for (final SearchHit hit : hits) {
                        count++;
                        if (!cursor.apply(creator.build(response, hit))) {
                            break;
                        }
                    }

                    response = client.prepareSearchScroll(scrollId)
                            .setScroll(scrollForDelete)
                            .execute()
                            .actionGet(fessConfig.getIndexBulkTimeout());
                    if (!scrollId.equals(response.getScrollId())) {
                        deleteScrollContext(scrollId);
                    }
                    scrollId = response.getScrollId();
                }
            } catch (final SearchPhaseExecutionException e) {
                throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryParseError(UserMessages.GLOBAL_PROPERTY_KEY),
                        "Invalid query: " + searchRequestBuilder, e);
            } finally {
                deleteScrollContext(scrollId);
            }
        }

        return count;
    }

    /**
     * Retrieves a single document matching the search condition.
     *
     * @param index     the index name
     * @param condition the search condition
     * @return an optional containing the document if found
     */
    public OptionalEntity<Map<String, Object>> getDocument(final String index, final SearchCondition<SearchRequestBuilder> condition) {
        return getDocument(index, condition, (response, hit) -> {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final Map<String, Object> source = hit.getSourceAsMap();
            if (source != null) {
                final Map<String, Object> docMap = new HashMap<>(source);
                docMap.put(fessConfig.getIndexFieldId(), hit.getId());
                docMap.put(fessConfig.getIndexFieldVersion(), hit.getVersion());
                docMap.put(fessConfig.getIndexFieldSeqNo(), hit.getSeqNo());
                docMap.put(fessConfig.getIndexFieldPrimaryTerm(), hit.getPrimaryTerm());
                return docMap;
            }
            final Map<String, DocumentField> fields = hit.getFields();
            if (fields != null) {
                final Map<String, Object> docMap = fields.entrySet()
                        .stream()
                        .collect(Collectors.toMap(Entry<String, DocumentField>::getKey, e -> (Object) e.getValue().getValues()));
                docMap.put(fessConfig.getIndexFieldId(), hit.getId());
                docMap.put(fessConfig.getIndexFieldVersion(), hit.getVersion());
                docMap.put(fessConfig.getIndexFieldSeqNo(), hit.getSeqNo());
                docMap.put(fessConfig.getIndexFieldPrimaryTerm(), hit.getPrimaryTerm());
                return docMap;
            }
            return null;
        });
    }

    /**
     * Retrieves a single document with custom entity creation.
     *
     * @param <T>       the entity type
     * @param index     the index name
     * @param condition the search condition
     * @param creator   the entity creator
     * @return an optional containing the entity if found
     */
    protected <T> OptionalEntity<T> getDocument(final String index, final SearchCondition<SearchRequestBuilder> condition,
            final EntityCreator<T, SearchResponse, SearchHit> creator) {
        return search(index, searchRequestBuilder -> {
            searchRequestBuilder.setVersion(true);
            return condition.build(searchRequestBuilder);
        }, (queryBuilder, execTime, searchResponse) -> searchResponse.map(response -> {
            final SearchHit[] hits = response.getHits().getHits();
            if (hits.length > 0) {
                return creator.build(response, hits[0]);
            }
            return null;
        }));
    }

    /**
     * Retrieves a list of documents matching the search condition.
     *
     * @param index     the index name
     * @param condition the search condition
     * @return a list of documents
     */
    public List<Map<String, Object>> getDocumentList(final String index, final SearchCondition<SearchRequestBuilder> condition) {
        return getDocumentList(index, condition, getDefaultEntityCreator());
    }

    /**
     * Gets the default entity creator for converting search hits to maps.
     *
     * @return the default entity creator
     */
    protected EntityCreator<Map<String, Object>, SearchResponse, SearchHit> getDefaultEntityCreator() {
        return (response, hit) -> {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final Map<String, Object> source = hit.getSourceAsMap();
            if (source != null) {
                final Map<String, Object> docMap = new HashMap<>(source);
                docMap.put(fessConfig.getIndexFieldId(), hit.getId());
                return docMap;
            }
            final Map<String, DocumentField> fields = hit.getFields();
            if (fields != null) {
                final Map<String, Object> docMap = fields.entrySet()
                        .stream()
                        .collect(Collectors.toMap(Entry<String, DocumentField>::getKey, e -> (Object) e.getValue().getValues()));
                docMap.put(fessConfig.getIndexFieldId(), hit.getId());
                return docMap;
            }
            return null;
        };
    }

    /**
     * Retrieves a list of documents with custom entity creation.
     *
     * @param <T>       the entity type
     * @param index     the index name
     * @param condition the search condition
     * @param creator   the entity creator
     * @return a list of entities
     */
    protected <T> List<T> getDocumentList(final String index, final SearchCondition<SearchRequestBuilder> condition,
            final EntityCreator<T, SearchResponse, SearchHit> creator) {
        return search(index, condition, (searchRequestBuilder, execTime, searchResponse) -> {
            final List<T> list = new ArrayList<>();
            searchResponse.ifPresent(response -> response.getHits().forEach(hit -> {
                list.add(creator.build(response, hit));
            }));
            return list;
        });
    }

    /**
     * Updates a specific field in a document.
     *
     * @param index the index name
     * @param id    the document ID
     * @param field the field name to update
     * @param value the new field value
     * @return true if the update was successful, false otherwise
     * @throws SearchEngineClientException if the update fails
     */
    public boolean update(final String index, final String id, final String field, final Object value) {
        // Using ingest pipelines with doc_as_upsert is not supported.
        if (usePipeline) {
            return updateByIdWithScript(index, id, field, value);
        }
        try {
            final Result result = client.prepareUpdate()
                    .setIndex(index)
                    .setId(id)
                    .setDoc(field, value)
                    .execute()
                    .actionGet(ComponentUtil.getFessConfig().getIndexIndexTimeout())
                    .getResult();
            return result == Result.CREATED || result == Result.UPDATED;
        } catch (final OpenSearchException e) {
            throw new SearchEngineClientException("[" + index + "] Failed to set " + value + " to " + field + " for doc " + id, e);
        }
    }

    /**
     * Updates a document by ID using a script when pipelines are enabled.
     *
     * @param index the index name
     * @param id    the document ID
     * @param field the field name to update
     * @param value the new field value
     * @return true if the update was successful, false otherwise
     * @throws SearchEngineClientException if the update fails
     */
    protected boolean updateByIdWithScript(final String index, final String id, final String field, final Object value) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final UpdateByQueryRequest request = new UpdateByQueryRequest(index).setQuery(QueryBuilders.idsQuery().addIds(id))
                .setScript(new Script(ScriptType.INLINE, "painless",
                        "ctx._source[params.f]=params.v;" + ComponentUtil.getLanguageHelper().getReindexScriptSource(),
                        Map.of("f", field, "v", value)));
        try {
            final String source = SearchEngineUtil.getXContentString(request, XContentType.JSON);
            if (logger.isDebugEnabled()) {
                logger.debug("update script by id: {}", source);
            }
            final String refresh = StringUtil.isNotBlank(fessConfig.getIndexReindexRefresh()) ? fessConfig.getIndexReindexRefresh() : null;
            try (CurlResponse response = ComponentUtil.getCurlHelper()
                    .post("/" + index + "/_update_by_query")
                    .param("refresh", refresh)
                    .param("max_docs", "1")
                    .body(source)
                    .execute()) {
                if (response.getHttpStatusCode() == 200) {
                    return true;
                }
                return false;
            }
        } catch (final IOException e) {
            throw new SearchEngineClientException("[" + index + "] Failed to set " + value + " to " + field + " for doc " + id, e);
        }
    }

    /**
     * Refreshes the specified indices to make recent changes visible for search.
     *
     * @param indices the indices to refresh
     */
    public void refresh(final String... indices) {
        client.admin().indices().prepareRefresh(indices).execute(new ActionListener<RefreshResponse>() {
            @Override
            public void onResponse(final RefreshResponse response) {
                if (logger.isDebugEnabled()) {
                    logger.debug(() -> "Refreshed " + stream(indices).get(stream -> stream.collect(Collectors.joining(", "))));
                }
            }

            @Override
            public void onFailure(final Exception e) {
                logger.error(() -> "Failed to refresh " + stream(indices).get(stream -> stream.collect(Collectors.joining(", "))), e);
            }
        });

    }

    /**
     * Flushes the specified indices to ensure data is written to disk.
     *
     * @param indices the indices to flush
     */
    public void flush(final String... indices) {
        client.admin().indices().prepareFlush(indices).execute(new ActionListener<FlushResponse>() {

            @Override
            public void onResponse(final FlushResponse response) {
                if (logger.isDebugEnabled()) {
                    logger.debug(() -> "Flushed " + stream(indices).get(stream -> stream.collect(Collectors.joining(", "))));
                }
            }

            @Override
            public void onFailure(final Exception e) {
                logger.error(() -> "Failed to flush " + stream(indices).get(stream -> stream.collect(Collectors.joining(", "))), e);
            }
        });

    }

    /**
     * Pings the search engine cluster to check connectivity and health.
     *
     * @return the ping response with cluster information
     * @throws SearchEngineClientException if the ping fails
     */
    public PingResponse ping() {
        try {
            final ClusterHealthResponse response =
                    client.admin().cluster().prepareHealth().execute().actionGet(ComponentUtil.getFessConfig().getIndexHealthTimeout());
            return new PingResponse(response);
        } catch (final OpenSearchException e) {
            throw new SearchEngineClientException("Failed to process a ping request.", e);
        }
    }

    /**
     * Adds multiple documents to the specified index in bulk.
     *
     * @param index   the target index
     * @param docList list of documents to add
     * @param options callback for customizing index request options
     * @return the bulk response
     */
    public BulkResponse addAll(final String index, final List<Map<String, Object>> docList,
            final BiConsumer<Map<String, Object>, IndexRequestBuilder> options) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (final Map<String, Object> doc : docList) {
            final Object id = doc.remove(fessConfig.getIndexFieldId());
            final IndexRequestBuilder builder = client.prepareIndex().setIndex(index).setId(id.toString()).setSource(new DocMap(doc));
            options.accept(doc, builder);
            bulkRequestBuilder.add(builder);
        }
        return bulkRequestBuilder.execute().actionGet(ComponentUtil.getFessConfig().getIndexBulkTimeout());
    }

    /**
     * Builder class for constructing search conditions and parameters.
     */
    public static class SearchConditionBuilder {
        /** The search request builder being configured */
        protected final SearchRequestBuilder searchRequestBuilder;
        /** The search query string */
        protected String query;
        /** Fields to include in the response */
        protected String[] responseFields;
        /** Search result offset (number of results to skip) */
        protected int offset = Constants.DEFAULT_START_COUNT;
        /** Maximum number of results to return */
        protected int size = Constants.DEFAULT_PAGE_SIZE;
        /** Geographic search information */
        protected GeoInfo geoInfo;
        /** Facet configuration for aggregations */
        protected FacetInfo facetInfo;
        /** Highlighting configuration */
        protected HighlightInfo highlightInfo;
        /** Hash of document for similarity search */
        protected String similarDocHash;
        /** Type of search request */
        protected SearchRequestType searchRequestType = SearchRequestType.SEARCH;
        /** Whether scroll mode is enabled for large result sets */
        protected boolean isScroll = false;
        /** Track total hits configuration */
        protected String trackTotalHits = null;
        /** Minimum score threshold for results */
        protected Float minScore = null;

        /**
         * Creates a new SearchConditionBuilder instance.
         *
         * @param searchRequestBuilder the search request builder to configure
         * @return a new SearchConditionBuilder instance
         */
        public static SearchConditionBuilder builder(final SearchRequestBuilder searchRequestBuilder) {
            return new SearchConditionBuilder(searchRequestBuilder);
        }

        /**
         * Constructor for SearchConditionBuilder.
         *
         * @param searchRequestBuilder the search request builder to configure
         */
        SearchConditionBuilder(final SearchRequestBuilder searchRequestBuilder) {
            this.searchRequestBuilder = searchRequestBuilder;
        }

        /**
         * Gets the current search condition as a map.
         *
         * @return a map containing the search condition parameters
         */
        public Map<String, Object> condition() {
            final Map<String, Object> params = new HashMap<>();
            params.put("query", query);
            params.put("responseFields", responseFields);
            params.put("offset", offset);
            params.put("size", size);
            // TODO support rescorer(convert to map)
            // params.put("geoInfo", geoInfo);
            // params.put("facetInfo", facetInfo);
            params.put("similarDocHash", similarDocHash);
            return params;
        }

        /**
         * Sets the search query string.
         *
         * @param query the query string
         * @return this builder for method chaining
         */
        public SearchConditionBuilder query(final String query) {
            this.query = query;
            return this;
        }

        /**
         * Sets the search request type.
         *
         * @param searchRequestType the search request type
         * @return this builder for method chaining
         */
        public SearchConditionBuilder searchRequestType(final SearchRequestType searchRequestType) {
            this.searchRequestType = searchRequestType;
            return this;
        }

        /**
         * Sets the fields to include in the response.
         *
         * @param responseFields the fields to include in the response
         * @return this builder for method chaining
         */
        public SearchConditionBuilder responseFields(final String[] responseFields) {
            this.responseFields = responseFields;
            return this;
        }

        /**
         * Sets the search result offset.
         *
         * @param offset the number of results to skip
         * @return this builder for method chaining
         */
        public SearchConditionBuilder offset(final int offset) {
            this.offset = offset;
            return this;
        }

        /**
         * Sets the maximum number of results to return.
         *
         * @param size the maximum number of results
         * @return this builder for method chaining
         */
        public SearchConditionBuilder size(final int size) {
            this.size = size;
            return this;
        }

        /**
         * Sets the geographic search information.
         *
         * @param geoInfo the geographic search information
         * @return this builder for method chaining
         */
        public SearchConditionBuilder geoInfo(final GeoInfo geoInfo) {
            this.geoInfo = geoInfo;
            return this;
        }

        /**
         * Sets the highlighting information.
         *
         * @param highlightInfo the highlighting configuration
         * @return this builder for method chaining
         */
        public SearchConditionBuilder highlightInfo(final HighlightInfo highlightInfo) {
            this.highlightInfo = highlightInfo;
            return this;
        }

        /**
         * Sets the similar document hash for similarity search.
         *
         * @param similarDocHash the hash of the document to find similar documents to
         * @return this builder for method chaining
         */
        public SearchConditionBuilder similarDocHash(final String similarDocHash) {
            if (StringUtil.isNotBlank(similarDocHash)) {
                this.similarDocHash = similarDocHash;
            }
            return this;
        }

        /**
         * Sets the facet information for aggregations.
         *
         * @param facetInfo the facet configuration
         * @return this builder for method chaining
         */
        public SearchConditionBuilder facetInfo(final FacetInfo facetInfo) {
            this.facetInfo = facetInfo;
            return this;
        }

        /**
         * Enables scroll mode for large result sets.
         *
         * @return this builder for method chaining
         */
        public SearchConditionBuilder scroll() {
            isScroll = true;
            return this;
        }

        /**
         * Sets the track total hits configuration.
         *
         * @param trackTotalHits the track total hits setting
         * @return this builder for method chaining
         */
        public SearchConditionBuilder trackTotalHits(final String trackTotalHits) {
            this.trackTotalHits = trackTotalHits;
            return this;
        }

        /**
         * Sets the minimum score threshold for results.
         *
         * @param minScore the minimum score threshold
         * @return this builder for method chaining
         */
        public SearchConditionBuilder minScore(final Float minScore) {
            this.minScore = minScore;
            return this;
        }

        /**
         * Builds the search request with all configured parameters.
         *
         * @return true if the build was successful, false if the query is blank
         * @throws ResultOffsetExceededException if the offset exceeds the maximum allowed
         * @throws SearchQueryException if facet fields are invalid
         */
        public boolean build() {
            if (StringUtil.isBlank(query)) {
                return false;
            }

            final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
            final QueryFieldConfig queryFieldConfig = ComponentUtil.getQueryFieldConfig();
            final FessConfig fessConfig = ComponentUtil.getFessConfig();

            if (offset > fessConfig.getQueryMaxSearchResultOffsetAsInteger()) {
                throw new ResultOffsetExceededException("The number of result size is exceeded.");
            }

            final QueryContext queryContext = buildQueryContext(queryHelper, queryFieldConfig, fessConfig);

            searchRequestBuilder.setFrom(offset).setSize(size);

            buildTrackTotalHits(fessConfig);
            buildMinScore(fessConfig);

            if (responseFields != null) {
                searchRequestBuilder.setFetchSource(responseFields, null);
            }

            // rescorer
            buildRescorer(queryHelper, queryFieldConfig, fessConfig);

            // sort
            buildSort(queryContext, queryFieldConfig, fessConfig);

            // highlighting
            if (highlightInfo != null) {
                buildHighlighter(queryHelper, queryFieldConfig, fessConfig);
            }

            // facets
            if (facetInfo != null) {
                buildFacet(queryHelper, queryFieldConfig, fessConfig);
            }

            if (!SearchRequestType.ADMIN_SEARCH.equals(searchRequestType) && !isScroll && fessConfig.isResultCollapsed()
                    && similarDocHash == null) {
                searchRequestBuilder.setCollapse(getCollapseBuilder(fessConfig));
            }

            searchRequestBuilder.setQuery(queryContext.getQueryBuilder());
            return true;
        }

        /**
         * Builds the minimum score configuration.
         *
         * @param fessConfig the Fess configuration
         */
        protected void buildMinScore(final FessConfig fessConfig) {
            if (minScore != null) {
                searchRequestBuilder.setMinScore(minScore);
            }
        }

        /**
         * Builds the track total hits configuration.
         *
         * @param fessConfig the Fess configuration
         */
        protected void buildTrackTotalHits(final FessConfig fessConfig) {
            if (isScroll) {
                return;
            }
            if (StringUtil.isNotBlank(trackTotalHits)) {
                if (Constants.TRUE.equalsIgnoreCase(trackTotalHits) || Constants.FALSE.equalsIgnoreCase(trackTotalHits)) {
                    searchRequestBuilder.setTrackTotalHits(Boolean.parseBoolean(trackTotalHits));
                    return;
                }
                try {
                    searchRequestBuilder.setTrackTotalHitsUpTo(Integer.parseInt(trackTotalHits));
                    return;
                } catch (final NumberFormatException e) {
                    // ignore
                }
            }
            final Object trackTotalHitsValue = fessConfig.getQueryTrackTotalHitsValue();
            if (trackTotalHitsValue instanceof Boolean) {
                searchRequestBuilder.setTrackTotalHits((Boolean) trackTotalHitsValue);
            } else if (trackTotalHitsValue instanceof Number) {
                searchRequestBuilder.setTrackTotalHitsUpTo(((Number) trackTotalHitsValue).intValue());
            }
        }

        /**
         * Builds the facet aggregations.
         *
         * @param queryHelper the query helper
         * @param queryFieldConfig the query field configuration
         * @param fessConfig the Fess configuration
         * @throws SearchQueryException if facet fields are invalid
         */
        protected void buildFacet(final QueryHelper queryHelper, final QueryFieldConfig queryFieldConfig, final FessConfig fessConfig) {
            stream(facetInfo.field).of(stream -> stream.forEach(f -> {
                if (!queryFieldConfig.isFacetField(f)) {
                    throw new SearchQueryException("Invalid facet field: " + f);
                }
                final String encodedField = BaseEncoding.base64().encode(f.getBytes(StandardCharsets.UTF_8));
                final TermsAggregationBuilder termsBuilder =
                        AggregationBuilders.terms(Constants.FACET_FIELD_PREFIX + encodedField).field(f);
                termsBuilder.order(facetInfo.getBucketOrder());
                if (facetInfo.size != null) {
                    termsBuilder.size(facetInfo.size);
                }
                if (facetInfo.minDocCount != null) {
                    termsBuilder.minDocCount(facetInfo.minDocCount);
                }
                if (facetInfo.missing != null) {
                    termsBuilder.missing(facetInfo.missing);
                }
                searchRequestBuilder.addAggregation(termsBuilder);
            }));
            stream(facetInfo.query).of(stream -> stream.forEach(fq -> {
                final QueryContext facetContext = new QueryContext(fq, false);
                queryHelper.buildBaseQuery(facetContext, c -> {});
                final String encodedFacetQuery = BaseEncoding.base64().encode(fq.getBytes(StandardCharsets.UTF_8));
                final FilterAggregationBuilder filterBuilder =
                        AggregationBuilders.filter(Constants.FACET_QUERY_PREFIX + encodedFacetQuery, facetContext.getQueryBuilder());
                searchRequestBuilder.addAggregation(filterBuilder);
            }));
        }

        /**
         * Builds the highlighting configuration.
         *
         * @param queryHelper the query helper
         * @param queryFieldConfig the query field configuration
         * @param fessConfig the Fess configuration
         */
        protected void buildHighlighter(final QueryHelper queryHelper, final QueryFieldConfig queryFieldConfig,
                final FessConfig fessConfig) {
            final String highlighterType = highlightInfo.getType();
            final int fragmentSize = highlightInfo.getFragmentSize();
            final int numOfFragments = highlightInfo.getNumOfFragments();
            final int fragmentOffset = highlightInfo.getFragmentOffset();
            final char[] boundaryChars = fessConfig.getQueryHighlightBoundaryCharsAsArray();
            final int boundaryMaxScan = fessConfig.getQueryHighlightBoundaryMaxScanAsInteger();
            final String boundaryScannerType = fessConfig.getQueryHighlightBoundaryScanner();
            final boolean forceSource = fessConfig.isQueryHighlightForceSource();
            final String fragmenter = fessConfig.getQueryHighlightFragmenter();
            final int noMatchSize = fessConfig.getQueryHighlightNoMatchSizeAsInteger();
            final String order = fessConfig.getQueryHighlightOrder();
            final int phraseLimit = fessConfig.getQueryHighlightPhraseLimitAsInteger();
            final String encoder = fessConfig.getQueryHighlightEncoder();
            final HighlightBuilder highlightBuilder = new HighlightBuilder();
            queryFieldConfig.highlightedFields(
                    stream -> stream.forEach(hf -> highlightBuilder.field(new HighlightBuilder.Field(hf).highlighterType(highlighterType)
                            .fragmentSize(fragmentSize)
                            .numOfFragments(numOfFragments)
                            .boundaryChars(boundaryChars)
                            .boundaryMaxScan(boundaryMaxScan)
                            .boundaryScannerType(boundaryScannerType)
                            .forceSource(forceSource)
                            .fragmenter(fragmenter)
                            .fragmentOffset(fragmentOffset)
                            .noMatchSize(noMatchSize)
                            .order(order)
                            .phraseLimit(phraseLimit)).encoder(encoder)));
            searchRequestBuilder.highlighter(highlightBuilder);
        }

        /**
         * Builds the sort configuration.
         *
         * @param queryContext the query context
         * @param queryFieldConfig the query field configuration
         * @param fessConfig the Fess configuration
         */
        protected void buildSort(final QueryContext queryContext, final QueryFieldConfig queryFieldConfig, final FessConfig fessConfig) {
            queryContext.sortBuilders().forEach(sortBuilder -> searchRequestBuilder.addSort(sortBuilder));
        }

        /**
         * Builds the rescorer configuration.
         *
         * @param queryHelper the query helper
         * @param queryFieldConfig the query field configuration
         * @param fessConfig the Fess configuration
         */
        protected void buildRescorer(final QueryHelper queryHelper, final QueryFieldConfig queryFieldConfig, final FessConfig fessConfig) {
            stream(queryHelper.getRescorers(condition())).of(stream -> stream.forEach(searchRequestBuilder::addRescorer));
        }

        /**
         * Builds the query context with all search parameters.
         *
         * @param queryHelper the query helper
         * @param queryFieldConfig the query field configuration
         * @param fessConfig the Fess configuration
         * @return the built query context
         */
        protected QueryContext buildQueryContext(final QueryHelper queryHelper, final QueryFieldConfig queryFieldConfig,
                final FessConfig fessConfig) {
            return queryHelper.build(searchRequestType, query, context -> {
                if (SearchRequestType.ADMIN_SEARCH.equals(searchRequestType)) {
                    context.skipRoleQuery();
                } else if (similarDocHash != null) {
                    final DocumentHelper documentHelper = ComponentUtil.getDocumentHelper();
                    context.addQuery(boolQuery -> {
                        boolQuery.filter(QueryBuilders.termQuery(fessConfig.getIndexFieldContentMinhashBits(),
                                documentHelper.decodeSimilarDocHash(similarDocHash)));
                    });
                }

                if (geoInfo != null && geoInfo.toQueryBuilder() != null) {
                    context.addQuery(boolQuery -> boolQuery.filter(geoInfo.toQueryBuilder()));
                }
            });
        }

        /**
         * Gets the collapse builder for result grouping.
         *
         * @param fessConfig the Fess configuration
         * @return the collapse builder
         */
        protected CollapseBuilder getCollapseBuilder(final FessConfig fessConfig) {
            final InnerHitBuilder innerHitBuilder = new InnerHitBuilder().setName(fessConfig.getQueryCollapseInnerHitsName())
                    .setSize(fessConfig.getQueryCollapseInnerHitsSizeAsInteger());
            fessConfig.getQueryCollapseInnerHitsSortBuilders()
                    .ifPresent(builders -> stream(builders).of(stream -> stream.forEach(innerHitBuilder::addSort)));
            return new CollapseBuilder(fessConfig.getIndexFieldContentMinhashBits())
                    .setMaxConcurrentGroupRequests(fessConfig.getQueryCollapseMaxConcurrentGroupResultsAsInteger())
                    .setInnerHits(innerHitBuilder);
        }
    }

    /**
     * Stores a document in the specified index.
     *
     * @param index the index name
     * @param obj   the document object to store
     * @return true if the document was stored successfully, false otherwise
     * @throws SearchEngineClientException if the store operation fails
     */
    public boolean store(final String index, final Object obj) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        @SuppressWarnings("unchecked")
        final Map<String, Object> source = obj instanceof Map ? (Map<String, Object>) obj : BeanUtil.copyBeanToNewMap(obj);
        final String id = (String) source.remove(fessConfig.getIndexFieldId());
        source.remove(fessConfig.getIndexFieldVersion());
        final Number seqNo = (Number) source.remove(fessConfig.getIndexFieldSeqNo());
        final Number primaryTerm = (Number) source.remove(fessConfig.getIndexFieldPrimaryTerm());
        IndexResponse response;
        try {
            if (id == null) {
                // TODO throw Exception in next release
                // create
                response = client.prepareIndex()
                        .setIndex(index)
                        .setSource(new DocMap(source))
                        .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
                        .setOpType(OpType.CREATE)
                        .execute()
                        .actionGet(fessConfig.getIndexIndexTimeout());
            } else {
                // create or update
                final IndexRequestBuilder builder = client.prepareIndex()
                        .setIndex(index)
                        .setId(id)
                        .setSource(new DocMap(source))
                        .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
                        .setOpType(OpType.INDEX);
                if (seqNo != null) {
                    builder.setIfSeqNo(seqNo.longValue());
                }
                if (primaryTerm != null) {
                    builder.setIfPrimaryTerm(primaryTerm.longValue());
                }
                response = builder.execute().actionGet(fessConfig.getIndexIndexTimeout());
            }
            final Result result = response.getResult();
            return result == Result.CREATED || result == Result.UPDATED;
        } catch (final OpenSearchException e) {
            throw new SearchEngineClientException("Failed to store: " + obj, e);
        }
    }

    /**
     * Deletes a document from the specified index.
     *
     * @param index the index name
     * @param id    the document ID
     * @return true if the document was deleted successfully, false otherwise
     */
    public boolean delete(final String index, final String id) {
        return delete(index, id, null, null);
    }

    /**
     * Deletes a document from the specified index with optimistic concurrency control.
     *
     * @param index       the index name
     * @param id          the document ID
     * @param seqNo       the sequence number for optimistic concurrency control
     * @param primaryTerm the primary term for optimistic concurrency control
     * @return true if the document was deleted successfully, false otherwise
     * @throws SearchEngineClientException if the delete operation fails
     */
    public boolean delete(final String index, final String id, final Number seqNo, final Number primaryTerm) {
        try {
            final DeleteRequestBuilder builder = client.prepareDelete().setIndex(index).setId(id).setRefreshPolicy(RefreshPolicy.IMMEDIATE);
            if (seqNo != null) {
                builder.setIfSeqNo(seqNo.longValue());
            }
            if (primaryTerm != null) {
                builder.setIfPrimaryTerm(primaryTerm.longValue());
            }
            final DeleteResponse response = builder.execute().actionGet(ComponentUtil.getFessConfig().getIndexDeleteTimeout());
            return response.getResult() == Result.DELETED;
        } catch (final OpenSearchException e) {
            throw new SearchEngineClientException("Failed to delete: " + index + "/" + id + "@" + seqNo + ":" + primaryTerm, e);
        }
    }

    /**
     * Sets the path to index configuration resources.
     *
     * @param indexConfigPath the path to index configuration resources
     */
    public void setIndexConfigPath(final String indexConfigPath) {
        this.indexConfigPath = indexConfigPath;
    }

    /**
     * Interface for defining search condition logic.
     *
     * @param <B> the type of request builder
     */
    public interface SearchCondition<B> {
        /**
         * Builds the search condition into the request builder.
         *
         * @param requestBuilder the request builder to configure
         * @return true if the condition was successfully built, false otherwise
         */
        boolean build(B requestBuilder);
    }

    /**
     * Interface for building search results from response data.
     *
     * @param <T> the result type
     * @param <B> the request builder type
     * @param <R> the response type
     */
    public interface SearchResult<T, B, R> {
        /**
         * Builds a result object from the request builder, execution time, and response.
         *
         * @param requestBuilder the request builder that was executed
         * @param execTime       the execution time in milliseconds
         * @param response       the optional response from the search engine
         * @return the built result object
         */
        T build(B requestBuilder, long execTime, OptionalEntity<R> response);
    }

    /**
     * Interface for creating entities from search response hits.
     *
     * @param <T> the entity type
     * @param <R> the response type
     * @param <H> the hit type
     */
    public interface EntityCreator<T, R, H> {
        /**
         * Creates an entity from a search response and hit.
         *
         * @param response the search response
         * @param hit      the individual search hit
         * @return the created entity
         */
        T build(R response, H hit);
    }

    /**
     * Sets the name of the search engine cluster.
     *
     * @param clusterName the cluster name
     */
    public void setClusterName(final String clusterName) {
        this.clusterName = clusterName;
    }

    /**
     * Gets information about the search engine.
     *
     * @return the engine information
     * @throws SearchEngineClientException if the client is not an HttpClient
     */
    public EngineInfo getEngineInfo() {
        if (client instanceof final HttpClient httpClient) {
            return httpClient.getEngineInfo();
        }
        throw new SearchEngineClientException("client is not HttpClient.");
    }

    //
    // Fesen Client
    //

    /**
     * Gets the thread pool used by the client.
     *
     * @return the thread pool
     */
    @Override
    public ThreadPool threadPool() {
        return client.threadPool();
    }

    /**
     * Gets the admin client for cluster and index administration.
     *
     * @return the admin client
     */
    @Override
    public AdminClient admin() {
        return client.admin();
    }

    /**
     * Indexes a document asynchronously.
     *
     * @param request the index request
     * @return a future for the index response
     */
    @Override
    public ActionFuture<IndexResponse> index(final IndexRequest request) {
        return client.index(request);
    }

    /**
     * Indexes a document asynchronously with a callback.
     *
     * @param request  the index request
     * @param listener the response listener
     */
    @Override
    public void index(final IndexRequest request, final ActionListener<IndexResponse> listener) {
        client.index(request, listener);
    }

    /**
     * Prepares an index request builder.
     *
     * @return the index request builder
     */
    @Override
    public IndexRequestBuilder prepareIndex() {
        return client.prepareIndex();
    }

    /**
     * Updates a document asynchronously.
     *
     * @param request the update request
     * @return a future for the update response
     */
    @Override
    public ActionFuture<UpdateResponse> update(final UpdateRequest request) {
        return client.update(request);
    }

    /**
     * Updates a document asynchronously with a callback.
     *
     * @param request  the update request
     * @param listener the response listener
     */
    @Override
    public void update(final UpdateRequest request, final ActionListener<UpdateResponse> listener) {
        client.update(request, listener);
    }

    /**
     * Prepares an update request builder.
     *
     * @return the update request builder
     */
    @Override
    public UpdateRequestBuilder prepareUpdate() {
        return client.prepareUpdate();
    }

    /**
     * Prepares an update request builder for a specific document.
     *
     * @param index the index name
     * @param id    the document ID
     * @return the update request builder
     */
    @Override
    public UpdateRequestBuilder prepareUpdate(final String index, final String id) {
        return client.prepareUpdate(index, id);
    }

    /**
     * Prepares an index request builder for a specific index.
     *
     * @param index the index name
     * @return the index request builder
     */
    @Override
    public IndexRequestBuilder prepareIndex(final String index) {
        return client.prepareIndex(index);
    }

    /**
     * Deletes a document asynchronously.
     *
     * @param request the delete request
     * @return a future for the delete response
     */
    @Override
    public ActionFuture<DeleteResponse> delete(final DeleteRequest request) {
        return client.delete(request);
    }

    /**
     * Deletes a document asynchronously with a callback.
     *
     * @param request  the delete request
     * @param listener the response listener
     */
    @Override
    public void delete(final DeleteRequest request, final ActionListener<DeleteResponse> listener) {
        client.delete(request, listener);
    }

    /**
     * Prepares a delete request builder.
     *
     * @return the delete request builder
     */
    @Override
    public DeleteRequestBuilder prepareDelete() {
        return client.prepareDelete();
    }

    /**
     * Prepares a delete request builder for a specific document.
     *
     * @param index the index name
     * @param id    the document ID
     * @return the delete request builder
     */
    @Override
    public DeleteRequestBuilder prepareDelete(final String index, final String id) {
        return client.prepareDelete(index, id);
    }

    /**
     * Executes a bulk request asynchronously.
     *
     * @param request the bulk request
     * @return a future for the bulk response
     */
    @Override
    public ActionFuture<BulkResponse> bulk(final BulkRequest request) {
        return client.bulk(request);
    }

    /**
     * Executes a bulk request asynchronously with a callback.
     *
     * @param request  the bulk request
     * @param listener the response listener
     */
    @Override
    public void bulk(final BulkRequest request, final ActionListener<BulkResponse> listener) {
        client.bulk(request, listener);
    }

    /**
     * Prepares a bulk request builder.
     *
     * @return the bulk request builder
     */
    @Override
    public BulkRequestBuilder prepareBulk() {
        return client.prepareBulk();
    }

    /**
     * Gets a document asynchronously.
     *
     * @param request the get request
     * @return a future for the get response
     */
    @Override
    public ActionFuture<GetResponse> get(final GetRequest request) {
        return client.get(request);
    }

    /**
     * Gets a document asynchronously with a callback.
     *
     * @param request  the get request
     * @param listener the response listener
     */
    @Override
    public void get(final GetRequest request, final ActionListener<GetResponse> listener) {
        client.get(request, listener);
    }

    /**
     * Prepares a get request builder.
     *
     * @return the get request builder
     */
    @Override
    public GetRequestBuilder prepareGet() {
        return client.prepareGet();
    }

    /**
     * Prepares a get request builder for a specific document.
     *
     * @param index the index name
     * @param id    the document ID
     * @return the get request builder
     */
    @Override
    public GetRequestBuilder prepareGet(final String index, final String id) {
        return client.prepareGet(index, id);
    }

    /**
     * Gets multiple documents asynchronously.
     *
     * @param request the multi-get request
     * @return a future for the multi-get response
     */
    @Override
    public ActionFuture<MultiGetResponse> multiGet(final MultiGetRequest request) {
        return client.multiGet(request);
    }

    /**
     * Gets multiple documents asynchronously with a callback.
     *
     * @param request  the multi-get request
     * @param listener the response listener
     */
    @Override
    public void multiGet(final MultiGetRequest request, final ActionListener<MultiGetResponse> listener) {
        client.multiGet(request, listener);
    }

    /**
     * Prepares a multi-get request builder.
     *
     * @return the multi-get request builder
     */
    @Override
    public MultiGetRequestBuilder prepareMultiGet() {
        return client.prepareMultiGet();
    }

    /**
     * Executes a search request asynchronously.
     *
     * @param request the search request
     * @return a future for the search response
     */
    @Override
    public ActionFuture<SearchResponse> search(final SearchRequest request) {
        return client.search(request);
    }

    /**
     * Executes a search request asynchronously with a callback.
     *
     * @param request  the search request
     * @param listener the response listener
     */
    @Override
    public void search(final SearchRequest request, final ActionListener<SearchResponse> listener) {
        client.search(request, listener);
    }

    /**
     * Prepares a search request builder for specific indices.
     *
     * @param indices the indices to search
     * @return the search request builder
     */
    @Override
    public SearchRequestBuilder prepareSearch(final String... indices) {
        return client.prepareSearch(indices);
    }

    /**
     * Prepares a stream search request builder for specific indices.
     *
     * @param indices the indices to search
     * @return the search request builder
     */
    @Override
    public SearchRequestBuilder prepareStreamSearch(final String... indices) {
        return client.prepareStreamSearch(indices);
    }

    /**
     * Executes a search scroll request asynchronously.
     *
     * @param request the search scroll request
     * @return a future for the search response
     */
    @Override
    public ActionFuture<SearchResponse> searchScroll(final SearchScrollRequest request) {
        return client.searchScroll(request);
    }

    /**
     * Executes a search scroll request asynchronously with a callback.
     *
     * @param request  the search scroll request
     * @param listener the response listener
     */
    @Override
    public void searchScroll(final SearchScrollRequest request, final ActionListener<SearchResponse> listener) {
        client.searchScroll(request, listener);
    }

    /**
     * Prepares a search scroll request builder.
     *
     * @param scrollId the scroll ID
     * @return the search scroll request builder
     */
    @Override
    public SearchScrollRequestBuilder prepareSearchScroll(final String scrollId) {
        return client.prepareSearchScroll(scrollId);
    }

    /**
     * Executes a multi-search request asynchronously.
     *
     * @param request the multi-search request
     * @return a future for the multi-search response
     */
    @Override
    public ActionFuture<MultiSearchResponse> multiSearch(final MultiSearchRequest request) {
        return client.multiSearch(request);
    }

    /**
     * Executes a multi-search request asynchronously with a callback.
     *
     * @param request  the multi-search request
     * @param listener the response listener
     */
    @Override
    public void multiSearch(final MultiSearchRequest request, final ActionListener<MultiSearchResponse> listener) {
        client.multiSearch(request, listener);
    }

    /**
     * Prepares a multi-search request builder.
     *
     * @return the multi-search request builder
     */
    @Override
    public MultiSearchRequestBuilder prepareMultiSearch() {
        return client.prepareMultiSearch();
    }

    /**
     * Prepares an explain request builder for a specific document.
     *
     * @param index the index name
     * @param id    the document ID
     * @return the explain request builder
     */
    @Override
    public ExplainRequestBuilder prepareExplain(final String index, final String id) {
        return client.prepareExplain(index, id);
    }

    /**
     * Executes an explain request asynchronously.
     *
     * @param request the explain request
     * @return a future for the explain response
     */
    @Override
    public ActionFuture<ExplainResponse> explain(final ExplainRequest request) {
        return client.explain(request);
    }

    /**
     * Executes an explain request asynchronously with a callback.
     *
     * @param request  the explain request
     * @param listener the response listener
     */
    @Override
    public void explain(final ExplainRequest request, final ActionListener<ExplainResponse> listener) {
        client.explain(request, listener);
    }

    /**
     * Prepares a clear scroll request builder.
     *
     * @return the clear scroll request builder
     */
    @Override
    public ClearScrollRequestBuilder prepareClearScroll() {
        return client.prepareClearScroll();
    }

    /**
     * Clears scroll contexts asynchronously.
     *
     * @param request the clear scroll request
     * @return a future for the clear scroll response
     */
    @Override
    public ActionFuture<ClearScrollResponse> clearScroll(final ClearScrollRequest request) {
        return client.clearScroll(request);
    }

    /**
     * Clears scroll contexts asynchronously with a callback.
     *
     * @param request  the clear scroll request
     * @param listener the response listener
     */
    @Override
    public void clearScroll(final ClearScrollRequest request, final ActionListener<ClearScrollResponse> listener) {
        client.clearScroll(request, listener);
    }

    /**
     * Gets the client settings.
     *
     * @return the client settings
     */
    @Override
    public Settings settings() {
        return client.settings();
    }

    /**
     * Gets term vectors for a document asynchronously.
     *
     * @param request the term vectors request
     * @return a future for the term vectors response
     */
    @Override
    public ActionFuture<TermVectorsResponse> termVectors(final TermVectorsRequest request) {
        return client.termVectors(request);
    }

    /**
     * Gets term vectors for a document asynchronously with a callback.
     *
     * @param request  the term vectors request
     * @param listener the response listener
     */
    @Override
    public void termVectors(final TermVectorsRequest request, final ActionListener<TermVectorsResponse> listener) {
        client.termVectors(request, listener);
    }

    /**
     * Prepares a term vectors request builder.
     *
     * @return the term vectors request builder
     */
    @Override
    public TermVectorsRequestBuilder prepareTermVectors() {
        return client.prepareTermVectors();
    }

    /**
     * Prepares a term vectors request builder for a specific document.
     *
     * @param index the index name
     * @param id    the document ID
     * @return the term vectors request builder
     */
    @Override
    public TermVectorsRequestBuilder prepareTermVectors(final String index, final String id) {
        return client.prepareTermVectors(index, id);
    }

    /**
     * Gets term vectors for multiple documents asynchronously.
     *
     * @param request the multi-term vectors request
     * @return a future for the multi-term vectors response
     */
    @Override
    public ActionFuture<MultiTermVectorsResponse> multiTermVectors(final MultiTermVectorsRequest request) {
        return client.multiTermVectors(request);
    }

    /**
     * Gets term vectors for multiple documents asynchronously with a callback.
     *
     * @param request  the multi-term vectors request
     * @param listener the response listener
     */
    @Override
    public void multiTermVectors(final MultiTermVectorsRequest request, final ActionListener<MultiTermVectorsResponse> listener) {
        client.multiTermVectors(request, listener);
    }

    /**
     * Prepares a multi-term vectors request builder.
     *
     * @return the multi-term vectors request builder
     */
    @Override
    public MultiTermVectorsRequestBuilder prepareMultiTermVectors() {
        return client.prepareMultiTermVectors();
    }

    /**
     * Sets the batch size for update operations.
     *
     * @param sizeForUpdate the batch size for updates
     */
    public void setSizeForUpdate(final int sizeForUpdate) {
        this.sizeForUpdate = sizeForUpdate;
    }

    /**
     * Sets the scroll timeout for update operations.
     *
     * @param scrollForUpdate the scroll timeout string
     */
    public void setScrollForUpdate(final String scrollForUpdate) {
        this.scrollForUpdate = scrollForUpdate;
    }

    /**
     * Sets the batch size for delete operations.
     *
     * @param sizeForDelete the batch size for deletes
     */
    public void setSizeForDelete(final int sizeForDelete) {
        this.sizeForDelete = sizeForDelete;
    }

    /**
     * Sets the scroll timeout for delete operations.
     *
     * @param scrollForDelete the scroll timeout string
     */
    public void setScrollForDelete(final String scrollForDelete) {
        this.scrollForDelete = scrollForDelete;
    }

    /**
     * Sets the scroll timeout for search operations.
     *
     * @param scrollForSearch the scroll timeout string
     */
    public void setScrollForSearch(final String scrollForSearch) {
        this.scrollForSearch = scrollForSearch;
    }

    /**
     * Sets the maximum retry attempts for configuration synchronization status checks.
     *
     * @param maxConfigSyncStatusRetry the maximum retry attempts
     */
    public void setMaxConfigSyncStatusRetry(final int maxConfigSyncStatusRetry) {
        this.maxConfigSyncStatusRetry = maxConfigSyncStatusRetry;
    }

    /**
     * Sets the maximum retry attempts for search engine status checks.
     *
     * @param maxEsStatusRetry the maximum retry attempts
     */
    public void setMaxEsStatusRetry(final int maxEsStatusRetry) {
        this.maxEsStatusRetry = maxEsStatusRetry;
    }

    /**
     * Creates a client filtered with additional headers.
     *
     * @param headers the headers to add to requests
     * @return the filtered client
     */
    @Override
    public Client filterWithHeader(final Map<String, String> headers) {
        return client.filterWithHeader(headers);
    }

    /**
     * Executes an action asynchronously.
     *
     * @param <Request>  the request type
     * @param <Response> the response type
     * @param action     the action to execute
     * @param request    the action request
     * @return a future for the action response
     */
    @Override
    public <Request extends ActionRequest, Response extends ActionResponse> ActionFuture<Response> execute(
            final ActionType<Response> action, final Request request) {
        return client.execute(action, request);
    }

    /**
     * Executes an action asynchronously with a callback.
     *
     * @param <Request>  the request type
     * @param <Response> the response type
     * @param action     the action to execute
     * @param request    the action request
     * @param listener   the response listener
     */
    @Override
    public <Request extends ActionRequest, Response extends ActionResponse> void execute(final ActionType<Response> action,
            final Request request, final ActionListener<Response> listener) {
        client.execute(action, request, listener);
    }

    /**
     * Prepares a field capabilities request builder.
     *
     * @param indices the indices to check field capabilities for
     * @return the field capabilities request builder
     */
    @Override
    public FieldCapabilitiesRequestBuilder prepareFieldCaps(final String... indices) {
        return client.prepareFieldCaps(indices);
    }

    /**
     * Gets field capabilities asynchronously.
     *
     * @param request the field capabilities request
     * @return a future for the field capabilities response
     */
    @Override
    public ActionFuture<FieldCapabilitiesResponse> fieldCaps(final FieldCapabilitiesRequest request) {
        return client.fieldCaps(request);
    }

    /**
     * Gets field capabilities asynchronously with a callback.
     *
     * @param request  the field capabilities request
     * @param listener the response listener
     */
    @Override
    public void fieldCaps(final FieldCapabilitiesRequest request, final ActionListener<FieldCapabilitiesResponse> listener) {
        client.fieldCaps(request, listener);
    }

    /**
     * Prepares a bulk request builder with a global index.
     *
     * @param globalIndex the global index for all operations
     * @return the bulk request builder
     */
    @Override
    public BulkRequestBuilder prepareBulk(final String globalIndex) {
        return client.prepareBulk(globalIndex);
    }

    /**
     * Creates a point-in-time context asynchronously.
     *
     * @param createPITRequest the create PIT request
     * @param listener         the response listener
     */
    @Override
    public void createPit(final CreatePitRequest createPITRequest, final ActionListener<CreatePitResponse> listener) {
        client.createPit(createPITRequest, listener);
    }

    /**
     * Deletes point-in-time contexts asynchronously.
     *
     * @param deletePITRequest the delete PITs request
     * @param listener         the response listener
     */
    @Override
    public void deletePits(final DeletePitRequest deletePITRequest, final ActionListener<DeletePitResponse> listener) {
        client.deletePits(deletePITRequest, listener);
    }

    /**
     * Gets all point-in-time contexts asynchronously.
     *
     * @param getAllPitNodesRequest the get all PITs request
     * @param listener              the response listener
     */
    @Override
    public void getAllPits(final GetAllPitNodesRequest getAllPitNodesRequest, final ActionListener<GetAllPitNodesResponse> listener) {
        client.getAllPits(getAllPitNodesRequest, listener);
    }

    /**
     * Gets point-in-time segments information asynchronously.
     *
     * @param pitSegmentsRequest the PIT segments request
     * @param listener           the response listener
     */
    @Override
    public void pitSegments(final PitSegmentsRequest pitSegmentsRequest, final ActionListener<IndicesSegmentResponse> listener) {
        client.pitSegments(pitSegmentsRequest, listener);
    }

    /**
     * Searches a view asynchronously (not implemented).
     *
     * @param request  the search view request
     * @param listener the response listener
     * @throws UnsupportedOperationException always thrown as this operation is not implemented
     */
    @Override
    public void searchView(org.opensearch.action.admin.indices.view.SearchViewAction.Request request,
            ActionListener<SearchResponse> listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Searches a view asynchronously (not implemented).
     *
     * @param request the search view request
     * @return never returns as this operation is not implemented
     * @throws UnsupportedOperationException always thrown as this operation is not implemented
     */
    @Override
    public ActionFuture<SearchResponse> searchView(org.opensearch.action.admin.indices.view.SearchViewAction.Request request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Lists view names asynchronously (not implemented).
     *
     * @param request  the list view names request
     * @param listener the response listener
     * @throws UnsupportedOperationException always thrown as this operation is not implemented
     */
    @Override
    public void listViewNames(org.opensearch.action.admin.indices.view.ListViewNamesAction.Request request,
            ActionListener<org.opensearch.action.admin.indices.view.ListViewNamesAction.Response> listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Lists view names asynchronously (not implemented).
     *
     * @param request the list view names request
     * @return never returns as this operation is not implemented
     * @throws UnsupportedOperationException always thrown as this operation is not implemented
     */
    @Override
    public ActionFuture<org.opensearch.action.admin.indices.view.ListViewNamesAction.Response> listViewNames(
            org.opensearch.action.admin.indices.view.ListViewNamesAction.Request request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
