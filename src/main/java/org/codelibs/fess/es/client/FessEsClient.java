/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.client;

import static org.codelibs.core.stream.StreamUtil.stream;
import static org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.newConfigs;

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
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.exception.ResourceNotFoundRuntimeException;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.curl.CurlResponse;
import org.codelibs.elasticsearch.client.HttpClient;
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.Configs;
import org.codelibs.fess.Constants;
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
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocMap;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.Action;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteRequest.OpType;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkItemResponse.Failure;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.explain.ExplainRequest;
import org.elasticsearch.action.explain.ExplainRequestBuilder;
import org.elasticsearch.action.explain.ExplainResponse;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesRequest;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesRequestBuilder;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollRequestBuilder;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.termvectors.MultiTermVectorsRequest;
import org.elasticsearch.action.termvectors.MultiTermVectorsRequestBuilder;
import org.elasticsearch.action.termvectors.MultiTermVectorsResponse;
import org.elasticsearch.action.termvectors.TermVectorsRequest;
import org.elasticsearch.action.termvectors.TermVectorsRequestBuilder;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.InnerHitBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.threadpool.ThreadPool;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.di.exception.ContainerInitFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.BaseEncoding;

public class FessEsClient implements Client {
    private static final Logger logger = LoggerFactory.getLogger(FessEsClient.class);

    protected ElasticsearchClusterRunner runner;

    protected String httpAddress;

    protected Client client;

    protected Map<String, String> settings;

    protected String indexConfigPath = "fess_indices";

    protected List<String> indexConfigList = new ArrayList<>();

    protected Map<String, List<String>> configListMap = new HashMap<>();

    protected String scrollForSearch = "1m";

    protected int sizeForDelete = 100;

    protected String scrollForDelete = "1m";

    protected int maxConfigSyncStatusRetry = 10;

    protected int maxEsStatusRetry = 10;

    protected String clusterName = "elasticsearch";

    public void addIndexConfig(final String path) {
        indexConfigList.add(path);
    }

    public void addConfigFile(final String index, final String path) {
        List<String> list = configListMap.get(index);
        if (list == null) {
            list = new ArrayList<>();
            configListMap.put(index, list);
        }
        list.add(path);
    }

    public void setSettings(final Map<String, String> settings) {
        this.settings = settings;
    }

    public String getStatus() {
        return admin().cluster().prepareHealth().execute().actionGet(ComponentUtil.getFessConfig().getIndexHealthTimeout()).getStatus()
                .name();
    }

    public void setRunner(final ElasticsearchClusterRunner runner) {
        this.runner = runner;
    }

    public boolean isEmbedded() {
        return this.runner != null;
    }

    protected InetAddress getInetAddressByName(final String host) {
        try {
            return InetAddress.getByName(host);
        } catch (final UnknownHostException e) {
            throw new FessSystemException("Failed to resolve the hostname: " + host, e);
        }
    }

    @PostConstruct
    public void open() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        String httpAddress = System.getProperty(Constants.FESS_ES_HTTP_ADDRESS);
        if (StringUtil.isBlank(httpAddress)) {
            if (runner == null) {
                runner = new ElasticsearchClusterRunner();
                final Configs config = newConfigs().clusterName(clusterName).numOfNode(1).useLogger();
                final String esDir = System.getProperty("fess.es.dir");
                if (esDir != null) {
                    config.basePath(esDir);
                }
                config.disableESLogger();
                runner.onBuild((number, settingsBuilder) -> {
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
            }
            final int port = runner.node().settings().getAsInt("http.port", 9200);
            httpAddress = "http://localhost:" + port;
            logger.warn("Embedded Elasticsearch is running. This configuration is not recommended for production use.");
        }
        client = createHttpClient(fessConfig, httpAddress);

        if (StringUtil.isNotBlank(httpAddress)) {
            System.setProperty(Constants.FESS_ES_HTTP_ADDRESS, httpAddress);
        }

        waitForYellowStatus(fessConfig);

        indexConfigList.forEach(configName -> {
            final String[] values = configName.split("/");
            if (values.length == 2) {
                final String configIndex = values[0];
                final String configType = values[1];

                final boolean isFessIndex = configIndex.equals("fess");
                final String indexName;
                if (isFessIndex) {
                    final boolean exists = existsIndex(fessConfig.getIndexDocumentUpdateIndex());
                    if (!exists) {
                        indexName = generateNewIndexName(configIndex);
                        createIndex(configIndex, indexName);
                        createAlias(configIndex, indexName);
                    } else {
                        client.admin().cluster().prepareHealth(fessConfig.getIndexDocumentUpdateIndex()).setWaitForYellowStatus().execute()
                                .actionGet(fessConfig.getIndexIndicesTimeout());
                        final GetIndexResponse response =
                                client.admin().indices().prepareGetIndex().addIndices(fessConfig.getIndexDocumentUpdateIndex()).execute()
                                        .actionGet(fessConfig.getIndexIndicesTimeout());
                        final String[] indices = response.indices();
                        if (indices.length == 1) {
                            indexName = indices[0];
                        } else {
                            indexName = configIndex;
                        }
                    }
                } else {
                    if (configIndex.startsWith(".fess_config")) {
                        final String name = fessConfig.getIndexConfigIndex();
                        indexName = configIndex.replaceFirst(Pattern.quote(".fess_config"), name);
                    } else if (configIndex.startsWith(".fess_user")) {
                        final String name = fessConfig.getIndexUserIndex();
                        indexName = configIndex.replaceFirst(Pattern.quote(".fess_config"), name);
                    } else if (configIndex.startsWith("fess_log")) {
                        final String name = fessConfig.getIndexLogIndex();
                        indexName = configIndex.replaceFirst(Pattern.quote(".fess_config"), name);
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
                logger.warn("Invalid index config name: " + configName);
            }
        });
    }

    protected Client createHttpClient(final FessConfig fessConfig, final String host) {
        final Settings settings = Settings.builder().putList("http.hosts", host).build();
        return new HttpClient(settings, null);
    }

    public boolean existsIndex(final String indexName) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        boolean exists = false;
        try {
            final IndicesExistsResponse response =
                    client.admin().indices().prepareExists(indexName).execute().actionGet(fessConfig.getIndexSearchTimeout());
            exists = response.isExists();
        } catch (final Exception e) {
            // ignore
        }
        return exists;
    }

    public boolean reindex(final String fromIndex, final String toIndex, final boolean waitForCompletion) {
        final String source = "{\"source\":{\"index\":\"" + fromIndex + "\"},\"dest\":{\"index\":\"" + toIndex + "\"}}";
        try (CurlResponse response =
                ComponentUtil.getCurlHelper().post("/_reindex").param("wait_for_completion", Boolean.toString(waitForCompletion))
                        .body(source).execute()) {
            if (response.getHttpStatusCode() == 200) {
                return true;
            } else {
                logger.warn("Failed to reindex from " + fromIndex + " to " + toIndex);
            }
        } catch (final IOException e) {
            logger.warn("Failed to reindex from " + fromIndex + " to " + toIndex, e);
        }
        return false;
    }

    public boolean createIndex(final String index, final String indexName) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return createIndex(index, indexName, fessConfig.getIndexNumberOfShards(), fessConfig.getIndexAutoExpandReplicas(), true);
    }

    public boolean createIndex(final String index, final String indexName, final String numberOfShards, final String autoExpandReplicas,
            final boolean uploadConfig) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        if (uploadConfig) {
            waitForConfigSyncStatus();
            sendConfigFiles(index);
        }

        final String indexConfigFile = indexConfigPath + "/" + index + ".json";
        try {
            String source = FileUtil.readUTF8(indexConfigFile);
            String dictionaryPath = System.getProperty("fess.dictionary.path", StringUtil.EMPTY);
            if (StringUtil.isNotBlank(dictionaryPath) && !dictionaryPath.endsWith("/")) {
                dictionaryPath = dictionaryPath + "/";
            }
            source = source.replaceAll(Pattern.quote("${fess.dictionary.path}"), dictionaryPath);
            source = source.replaceAll(Pattern.quote("${fess.index.codec}"), fessConfig.getIndexCodec());
            source = source.replaceAll(Pattern.quote("${fess.index.number_of_shards}"), numberOfShards);
            source = source.replaceAll(Pattern.quote("${fess.index.auto_expand_replicas}"), autoExpandReplicas);
            final CreateIndexResponse indexResponse =
                    client.admin().indices().prepareCreate(indexName).setSource(source, XContentType.JSON).execute()
                            .actionGet(fessConfig.getIndexIndicesTimeout());
            if (indexResponse.isAcknowledged()) {
                logger.info("Created " + indexName + " index.");
                return true;
            } else if (logger.isDebugEnabled()) {
                logger.debug("Failed to create " + indexName + " index.");
            }
        } catch (final Exception e) {
            logger.warn(indexConfigFile + " is not found.", e);
        }

        return false;
    }

    public void addMapping(final String index, final String docType, final String indexName) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final GetMappingsResponse getMappingsResponse =
                client.admin().indices().prepareGetMappings(indexName).execute().actionGet(fessConfig.getIndexIndicesTimeout());
        final ImmutableOpenMap<String, MappingMetaData> indexMappings = getMappingsResponse.mappings().get(indexName);
        if (indexMappings == null || !indexMappings.containsKey(docType)) {
            String source = null;
            final String mappingFile = indexConfigPath + "/" + index + "/" + docType + ".json";
            try {
                source = FileUtil.readUTF8(mappingFile);
            } catch (final Exception e) {
                logger.warn(mappingFile + " is not found.", e);
            }
            try {
                final AcknowledgedResponse putMappingResponse =
                        client.admin().indices().preparePutMapping(indexName).setType(docType).setSource(source, XContentType.JSON)
                                .execute().actionGet(fessConfig.getIndexIndicesTimeout());
                if (putMappingResponse.isAcknowledged()) {
                    logger.info("Created " + indexName + "/" + docType + " mapping.");
                } else {
                    logger.warn("Failed to create " + indexName + "/" + docType + " mapping.");
                }

                final String dataPath = indexConfigPath + "/" + index + "/" + docType + ".bulk";
                if (ResourceUtil.isExist(dataPath)) {
                    insertBulkData(fessConfig, indexName, docType, dataPath);
                }
            } catch (final Exception e) {
                logger.warn("Failed to create " + indexName + "/" + docType + " mapping.", e);
            }
        } else if (logger.isDebugEnabled()) {
            logger.debug(indexName + "/" + docType + " mapping exists.");
        }
    }

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

    protected void createAlias(final String index, final String createdIndexName) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        // alias
        final String aliasConfigDirPath = indexConfigPath + "/" + index + "/alias";
        try {
            final File aliasConfigDir = ResourceUtil.getResourceAsFile(aliasConfigDirPath);
            if (aliasConfigDir.isDirectory()) {
                stream(aliasConfigDir.listFiles((dir, name) -> name.endsWith(".json"))).of(
                        stream -> stream.forEach(f -> {
                            final String aliasName = f.getName().replaceFirst(".json$", "");
                            String source = FileUtil.readUTF8(f);
                            if (source.trim().equals("{}")) {
                                source = null;
                            }
                            final AcknowledgedResponse response =
                                    client.admin().indices().prepareAliases().addAlias(createdIndexName, aliasName, source).execute()
                                            .actionGet(fessConfig.getIndexIndicesTimeout());
                            if (response.isAcknowledged()) {
                                logger.info("Created " + aliasName + " alias for " + createdIndexName);
                            } else if (logger.isDebugEnabled()) {
                                logger.debug("Failed to create " + aliasName + " alias for " + createdIndexName);
                            }
                        }));
            }
        } catch (final ResourceNotFoundRuntimeException e) {
            // ignore
        } catch (final Exception e) {
            logger.warn(aliasConfigDirPath + " is not found.", e);
        }
    }

    protected void sendConfigFiles(final String index) {
        configListMap.getOrDefault(index, Collections.emptyList()).forEach(
                path -> {
                    String source = null;
                    final String filePath = indexConfigPath + "/" + index + "/" + path;
                    try {
                        source = FileUtil.readUTF8(filePath);
                        try (CurlResponse response =
                                ComponentUtil.getCurlHelper().post("/_configsync/file").param("path", path).body(source).execute()) {
                            if (response.getHttpStatusCode() == 200) {
                                logger.info("Register " + path + " to " + index);
                            } else {
                                if (response.getContentException() != null) {
                                    logger.warn("Invalid request for " + path + ".", response.getContentException());
                                } else {
                                    logger.warn("Invalid request for " + path + ". The response is " + response.getContentAsString());
                                }
                            }
                        }
                    } catch (final Exception e) {
                        logger.warn("Failed to register " + filePath, e);
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

    protected String generateNewIndexName(final String configIndex) {
        return configIndex + "." + new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    protected void insertBulkData(final FessConfig fessConfig, final String configIndex, final String configType, final String dataPath) {
        try {
            final BulkRequestBuilder builder = client.prepareBulk();
            final ObjectMapper mapper = new ObjectMapper();
            Arrays.stream(FileUtil.readUTF8(dataPath).split("\n")).reduce(
                    (prev, line) -> {
                        try {
                            if (StringUtil.isBlank(prev)) {
                                final Map<String, Map<String, String>> result =
                                        mapper.readValue(line, new TypeReference<Map<String, Map<String, String>>>() {
                                        });
                                if (result.keySet().contains("index")) {
                                    return line;
                                } else if (result.keySet().contains("update")) {
                                    return line;
                                } else if (result.keySet().contains("delete")) {
                                    return StringUtil.EMPTY;
                                }
                            } else {
                                final Map<String, Map<String, String>> result =
                                        mapper.readValue(prev, new TypeReference<Map<String, Map<String, String>>>() {
                                        });
                                if (result.keySet().contains("index")) {
                                    final IndexRequestBuilder requestBuilder =
                                            client.prepareIndex(configIndex, configType, result.get("index").get("_id")).setSource(line,
                                                    XContentType.JSON);
                                    builder.add(requestBuilder);
                                }
                            }
                        } catch (final Exception e) {
                            logger.warn("Failed to parse " + dataPath);
                        }
                        return StringUtil.EMPTY;
                    });
            final BulkResponse response = builder.execute().actionGet(fessConfig.getIndexBulkTimeout());
            if (response.hasFailures()) {
                logger.warn("Failed to register " + dataPath + ": " + response.buildFailureMessage());
            }
        } catch (final Exception e) {
            logger.warn("Failed to create " + configIndex + "/" + configType + " mapping.");
        }
    }

    protected void waitForYellowStatus(final FessConfig fessConfig) {
        Exception cause = null;
        for (int i = 0; i < maxEsStatusRetry; i++) {
            try {
                final ClusterHealthResponse response =
                        client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute()
                                .actionGet(fessConfig.getIndexHealthTimeout());
                if (logger.isDebugEnabled()) {
                    logger.debug("Elasticsearch Cluster Status: " + response.getStatus());
                }
                return;
            } catch (final Exception e) {
                cause = e;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to access to Elasticsearch:" + i, cause);
            }
            try {
                Thread.sleep(1000L);
            } catch (final InterruptedException e) {
                // ignore
            }
        }
        final String message =
                "Elasticsearch (" + System.getProperty(Constants.FESS_ES_HTTP_ADDRESS)
                        + ") is not available. Check the state of your Elasticsearch cluster (" + clusterName + ").";
        throw new ContainerInitFailureException(message, cause);
    }

    protected void waitForConfigSyncStatus() {
        FessSystemException cause = null;
        for (int i = 0; i < maxConfigSyncStatusRetry; i++) {
            try (CurlResponse response = ComponentUtil.getCurlHelper().get("/_configsync/wait").param("status", "green").execute()) {
                final int httpStatusCode = response.getHttpStatusCode();
                if (httpStatusCode == 200) {
                    logger.info("ConfigSync is ready.");
                    return;
                } else {
                    final String message = "Configsync is not available. HTTP Status is " + httpStatusCode;
                    if (response.getContentException() != null) {
                        throw new FessSystemException(message, response.getContentException());
                    } else {
                        throw new FessSystemException(message);
                    }
                }
            } catch (final Exception e) {
                cause = new FessSystemException("Configsync is not available.", e);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to access to configsync:" + i, cause);
            }
            try {
                Thread.sleep(1000L);
            } catch (final InterruptedException e) {
                // ignore
            }
        }
        throw cause;
    }

    @Override
    @PreDestroy
    public void close() {
        if (runner != null) {
            try {
                client.admin().indices().prepareFlush().setForce(true).execute()
                        .actionGet(ComponentUtil.getFessConfig().getIndexIndicesTimeout());
            } catch (final Exception e) {
                logger.warn("Failed to flush indices.", e);
            }
        }
        try {
            client.close();
        } catch (final ElasticsearchException e) {
            logger.warn("Failed to close Client: " + client, e);
        }
    }

    public long deleteByQuery(final String index, final String type, final QueryBuilder queryBuilder) {

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        SearchResponse response =
                client.prepareSearch(index).setTypes(type).setScroll(scrollForDelete).setSize(sizeForDelete)
                        .setFetchSource(new String[] { fessConfig.getIndexFieldId() }, null).setQuery(queryBuilder)
                        .setPreference(Constants.SEARCH_PREFERENCE_LOCAL).execute().actionGet(fessConfig.getIndexScrollSearchTimeout());

        int count = 0;
        String scrollId = response.getScrollId();
        while (scrollId != null) {
            final SearchHits searchHits = response.getHits();
            final SearchHit[] hits = searchHits.getHits();
            if (hits.length == 0) {
                scrollId = null;
                break;
            }

            final BulkRequestBuilder bulkRequest = client.prepareBulk();
            for (final SearchHit hit : hits) {
                bulkRequest.add(client.prepareDelete(index, type, hit.getId()));
                count++;
            }
            final BulkResponse bulkResponse = bulkRequest.execute().actionGet(fessConfig.getIndexBulkTimeout());
            if (bulkResponse.hasFailures()) {
                throw new IllegalBehaviorStateException(bulkResponse.buildFailureMessage());
            }

            response =
                    client.prepareSearchScroll(scrollId).setScroll(scrollForDelete).execute().actionGet(fessConfig.getIndexBulkTimeout());
            scrollId = response.getScrollId();
        }
        return count;
    }

    protected <T> T get(final String index, final String type, final String id, final SearchCondition<GetRequestBuilder> condition,
            final SearchResult<T, GetRequestBuilder, GetResponse> searchResult) {
        final long startTime = System.currentTimeMillis();

        GetResponse response = null;
        final GetRequestBuilder requestBuilder = client.prepareGet(index, type, id);
        if (condition.build(requestBuilder)) {
            response = requestBuilder.execute().actionGet(ComponentUtil.getFessConfig().getIndexSearchTimeout());
        }
        final long execTime = System.currentTimeMillis() - startTime;

        return searchResult.build(requestBuilder, execTime, OptionalEntity.ofNullable(response, () -> {}));
    }

    public <T> T search(final String index, final String type, final SearchCondition<SearchRequestBuilder> condition,
            final SearchResult<T, SearchRequestBuilder, SearchResponse> searchResult) {
        final long startTime = System.currentTimeMillis();

        SearchResponse searchResponse = null;
        final SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type);
        if (condition.build(searchRequestBuilder)) {

            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final long queryTimeout = fessConfig.getQueryTimeoutAsInteger().longValue();
            if (queryTimeout >= 0) {
                searchRequestBuilder.setTimeout(TimeValue.timeValueMillis(queryTimeout));
            }

            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Query DSL:\n" + searchRequestBuilder.toString());
                }
                searchResponse = searchRequestBuilder.execute().actionGet(ComponentUtil.getFessConfig().getIndexSearchTimeout());
            } catch (final SearchPhaseExecutionException e) {
                throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryParseError(UserMessages.GLOBAL_PROPERTY_KEY),
                        "Invalid query: " + searchRequestBuilder, e);
            }
        }
        final long execTime = System.currentTimeMillis() - startTime;

        return searchResult.build(searchRequestBuilder, execTime, OptionalEntity.ofNullable(searchResponse, () -> {}));
    }

    public <T> long scrollSearch(final String index, final String type, final SearchCondition<SearchRequestBuilder> condition,
            final EntityCreator<T, SearchResponse, SearchHit> creator, final Function<T, Boolean> cursor) {
        long count = 0;

        final SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type).setScroll(scrollForSearch);
        if (condition.build(searchRequestBuilder)) {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();

            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Query DSL:\n" + searchRequestBuilder.toString());
                }
                SearchResponse response = searchRequestBuilder.execute().actionGet(ComponentUtil.getFessConfig().getIndexSearchTimeout());

                String scrollId = response.getScrollId();
                while (scrollId != null) {
                    final SearchHits searchHits = response.getHits();
                    final SearchHit[] hits = searchHits.getHits();
                    if (hits.length == 0) {
                        scrollId = null;
                        break;
                    }

                    for (final SearchHit hit : hits) {
                        count++;
                        if (!cursor.apply(creator.build(response, hit))) {
                            scrollId = null;
                            break;
                        }
                    }

                    response =
                            client.prepareSearchScroll(scrollId).setScroll(scrollForDelete).execute()
                                    .actionGet(fessConfig.getIndexBulkTimeout());
                    scrollId = response.getScrollId();
                }
            } catch (final SearchPhaseExecutionException e) {
                throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryParseError(UserMessages.GLOBAL_PROPERTY_KEY),
                        "Invalid query: " + searchRequestBuilder, e);
            }
        }

        return count;
    }

    public OptionalEntity<Map<String, Object>> getDocument(final String index, final String type,
            final SearchCondition<SearchRequestBuilder> condition) {
        return getDocument(
                index,
                type,
                condition,
                (response, hit) -> {
                    final FessConfig fessConfig = ComponentUtil.getFessConfig();
                    final Map<String, Object> source = hit.getSourceAsMap();
                    if (source != null) {
                        final Map<String, Object> docMap = new HashMap<>(source);
                        docMap.put(fessConfig.getIndexFieldId(), hit.getId());
                        docMap.put(fessConfig.getIndexFieldVersion(), hit.getVersion());
                        return docMap;
                    }
                    final Map<String, DocumentField> fields = hit.getFields();
                    if (fields != null) {
                        final Map<String, Object> docMap =
                                fields.entrySet().stream()
                                        .collect(Collectors.toMap(e -> e.getKey(), e -> (Object) e.getValue().getValues()));
                        docMap.put(fessConfig.getIndexFieldId(), hit.getId());
                        docMap.put(fessConfig.getIndexFieldVersion(), hit.getVersion());
                        return docMap;
                    }
                    return null;
                });
    }

    protected <T> OptionalEntity<T> getDocument(final String index, final String type,
            final SearchCondition<SearchRequestBuilder> condition, final EntityCreator<T, SearchResponse, SearchHit> creator) {
        return search(index, type, searchRequestBuilder -> {
            searchRequestBuilder.setVersion(true);
            return condition.build(searchRequestBuilder);
        }, (queryBuilder, execTime, searchResponse) -> {
            return searchResponse.map(response -> {
                final SearchHit[] hits = response.getHits().getHits();
                if (hits.length > 0) {
                    return creator.build(response, hits[0]);
                }
                return null;
            });
        });
    }

    public List<Map<String, Object>> getDocumentList(final String index, final String type,
            final SearchCondition<SearchRequestBuilder> condition) {
        return getDocumentList(
                index,
                type,
                condition,
                (response, hit) -> {
                    final FessConfig fessConfig = ComponentUtil.getFessConfig();
                    final Map<String, Object> source = hit.getSourceAsMap();
                    if (source != null) {
                        final Map<String, Object> docMap = new HashMap<>(source);
                        docMap.put(fessConfig.getIndexFieldId(), hit.getId());
                        return docMap;
                    }
                    final Map<String, DocumentField> fields = hit.getFields();
                    if (fields != null) {
                        final Map<String, Object> docMap =
                                fields.entrySet().stream()
                                        .collect(Collectors.toMap(e -> e.getKey(), e -> (Object) e.getValue().getValues()));
                        docMap.put(fessConfig.getIndexFieldId(), hit.getId());
                        return docMap;
                    }
                    return null;
                });
    }

    protected <T> List<T> getDocumentList(final String index, final String type, final SearchCondition<SearchRequestBuilder> condition,
            final EntityCreator<T, SearchResponse, SearchHit> creator) {
        return search(index, type, condition, (searchRequestBuilder, execTime, searchResponse) -> {
            final List<T> list = new ArrayList<>();
            searchResponse.ifPresent(response -> {
                response.getHits().forEach(hit -> {
                    list.add(creator.build(response, hit));
                });
            });
            return list;
        });
    }

    public boolean update(final String index, final String type, final String id, final String field, final Object value) {
        try {
            final Result result =
                    client.prepareUpdate(index, type, id).setDoc(field, value).execute()
                            .actionGet(ComponentUtil.getFessConfig().getIndexIndexTimeout()).getResult();
            return result == Result.CREATED || result == Result.UPDATED;
        } catch (final ElasticsearchException e) {
            throw new FessEsClientException("Failed to set " + value + " to " + field + " for doc " + id, e);
        }
    }

    public void refresh(final String... indices) {
        client.admin().indices().prepareRefresh(indices).execute(new ActionListener<RefreshResponse>() {
            @Override
            public void onResponse(final RefreshResponse response) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Refreshed " + stream(indices).get(stream -> stream.collect(Collectors.joining(", "))) + ".");
                }
            }

            @Override
            public void onFailure(final Exception e) {
                logger.error("Failed to refresh " + stream(indices).get(stream -> stream.collect(Collectors.joining(", "))) + ".", e);
            }
        });

    }

    public void flush(final String... indices) {
        client.admin().indices().prepareFlush(indices).execute(new ActionListener<FlushResponse>() {

            @Override
            public void onResponse(final FlushResponse response) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Flushed " + stream(indices).get(stream -> stream.collect(Collectors.joining(", "))) + ".");
                }
            }

            @Override
            public void onFailure(final Exception e) {
                logger.error("Failed to flush " + stream(indices).get(stream -> stream.collect(Collectors.joining(", "))) + ".", e);
            }
        });

    }

    public PingResponse ping() {
        try {
            final ClusterHealthResponse response =
                    client.admin().cluster().prepareHealth().execute().actionGet(ComponentUtil.getFessConfig().getIndexHealthTimeout());
            return new PingResponse(response);
        } catch (final ElasticsearchException e) {
            throw new FessEsClientException("Failed to process a ping request.", e);
        }
    }

    public void addAll(final String index, final String type, final List<Map<String, Object>> docList,
            final BiConsumer<Map<String, Object>, IndexRequestBuilder> options) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (final Map<String, Object> doc : docList) {
            final Object id = doc.remove(fessConfig.getIndexFieldId());
            final IndexRequestBuilder builder = client.prepareIndex(index, type, id.toString()).setSource(new DocMap(doc));
            options.accept(doc, builder);
            bulkRequestBuilder.add(builder);
        }
        final BulkResponse response = bulkRequestBuilder.execute().actionGet(ComponentUtil.getFessConfig().getIndexBulkTimeout());
        if (response.hasFailures()) {
            if (logger.isDebugEnabled()) {
                @SuppressWarnings("rawtypes")
                final List<DocWriteRequest<?>> requests = bulkRequestBuilder.request().requests();
                final BulkItemResponse[] items = response.getItems();
                if (requests.size() == items.length) {
                    for (int i = 0; i < requests.size(); i++) {
                        final BulkItemResponse resp = items[i];
                        if (resp.isFailed() && resp.getFailure() != null) {
                            final DocWriteRequest<?> req = requests.get(i);
                            final Failure failure = resp.getFailure();
                            logger.debug("Failed Request: " + req + "\n=>" + failure.getMessage());
                        }
                    }
                }
            }
            throw new FessEsClientException(response.buildFailureMessage());
        }
    }

    public static class SearchConditionBuilder {
        private final SearchRequestBuilder searchRequestBuilder;
        private String query;
        private String[] responseFields;
        private int offset = Constants.DEFAULT_START_COUNT;
        private int size = Constants.DEFAULT_PAGE_SIZE;
        private GeoInfo geoInfo;
        private FacetInfo facetInfo;
        private HighlightInfo highlightInfo;
        private String similarDocHash;
        private SearchRequestType searchRequestType = SearchRequestType.SEARCH;
        private boolean isScroll = false;

        public static SearchConditionBuilder builder(final SearchRequestBuilder searchRequestBuilder) {
            return new SearchConditionBuilder(searchRequestBuilder);
        }

        SearchConditionBuilder(final SearchRequestBuilder searchRequestBuilder) {
            this.searchRequestBuilder = searchRequestBuilder;
        }

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

        public SearchConditionBuilder query(final String query) {
            this.query = query;
            return this;
        }

        public SearchConditionBuilder searchRequestType(final SearchRequestType searchRequestType) {
            this.searchRequestType = searchRequestType;
            return this;
        }

        public SearchConditionBuilder responseFields(final String[] responseFields) {
            this.responseFields = responseFields;
            return this;
        }

        public SearchConditionBuilder offset(final int offset) {
            this.offset = offset;
            return this;
        }

        public SearchConditionBuilder size(final int size) {
            this.size = size;
            return this;
        }

        public SearchConditionBuilder geoInfo(final GeoInfo geoInfo) {
            this.geoInfo = geoInfo;
            return this;
        }

        public SearchConditionBuilder highlightInfo(final HighlightInfo highlightInfo) {
            this.highlightInfo = highlightInfo;
            return this;
        }

        public SearchConditionBuilder similarDocHash(final String similarDocHash) {
            if (StringUtil.isNotBlank(similarDocHash)) {
                this.similarDocHash = similarDocHash;
            }
            return this;
        }

        public SearchConditionBuilder facetInfo(final FacetInfo facetInfo) {
            this.facetInfo = facetInfo;
            return this;
        }

        public SearchConditionBuilder scroll() {
            this.isScroll = true;
            return this;
        }

        public boolean build() {
            if (StringUtil.isBlank(query)) {
                return false;
            }

            final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
            final FessConfig fessConfig = ComponentUtil.getFessConfig();

            if (offset > fessConfig.getQueryMaxSearchResultOffsetAsInteger()) {
                throw new ResultOffsetExceededException("The number of result size is exceeded.");
            }

            final QueryContext queryContext =
                    queryHelper.build(searchRequestType, query, context -> {
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
                            context.addQuery(boolQuery -> {
                                boolQuery.filter(geoInfo.toQueryBuilder());
                            });
                        }
                    });

            searchRequestBuilder.setFrom(offset).setSize(size);

            if (responseFields != null) {
                searchRequestBuilder.setFetchSource(responseFields, null);
            }

            // rescorer
            stream(queryHelper.getRescorers(condition())).of(stream -> stream.forEach(searchRequestBuilder::addRescorer));

            // sort
            queryContext.sortBuilders().forEach(sortBuilder -> searchRequestBuilder.addSort(sortBuilder));

            // highlighting
            if (highlightInfo != null) {
                final HighlightBuilder highlightBuilder = new HighlightBuilder();
                queryHelper.highlightedFields(stream -> stream.forEach(hf -> highlightBuilder.field(new HighlightBuilder.Field(hf)
                        .highlighterType(highlightInfo.getType()).fragmentSize(highlightInfo.getFragmentSize())
                        .numOfFragments(highlightInfo.getNumOfFragments()))));
                searchRequestBuilder.highlighter(highlightBuilder);
            }

            // facets
            if (facetInfo != null) {
                stream(facetInfo.field).of(
                        stream -> stream.forEach(f -> {
                            if (queryHelper.isFacetField(f)) {
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
                            } else {
                                throw new SearchQueryException("Invalid facet field: " + f);
                            }
                        }));
                stream(facetInfo.query).of(
                        stream -> stream.forEach(fq -> {
                            final QueryContext facetContext = new QueryContext(fq, false);
                            queryHelper.buildBaseQuery(facetContext, c -> {});
                            final String encodedFacetQuery = BaseEncoding.base64().encode(fq.getBytes(StandardCharsets.UTF_8));
                            final FilterAggregationBuilder filterBuilder =
                                    AggregationBuilders.filter(Constants.FACET_QUERY_PREFIX + encodedFacetQuery,
                                            facetContext.getQueryBuilder());
                            searchRequestBuilder.addAggregation(filterBuilder);
                        }));
            }

            if (!SearchRequestType.ADMIN_SEARCH.equals(searchRequestType) && !isScroll && fessConfig.isResultCollapsed()
                    && similarDocHash == null) {
                searchRequestBuilder.setCollapse(getCollapseBuilder(fessConfig));
            }

            searchRequestBuilder.setQuery(queryContext.getQueryBuilder());
            return true;
        }

        protected CollapseBuilder getCollapseBuilder(final FessConfig fessConfig) {
            final InnerHitBuilder innerHitBuilder =
                    new InnerHitBuilder().setName(fessConfig.getQueryCollapseInnerHitsName()).setSize(
                            fessConfig.getQueryCollapseInnerHitsSizeAsInteger());
            fessConfig.getQueryCollapseInnerHitsSortBuilders().ifPresent(
                    builders -> stream(builders).of(stream -> stream.forEach(innerHitBuilder::addSort)));
            return new CollapseBuilder(fessConfig.getIndexFieldContentMinhashBits()).setMaxConcurrentGroupRequests(
                    fessConfig.getQueryCollapseMaxConcurrentGroupResultsAsInteger()).setInnerHits(innerHitBuilder);
        }
    }

    public boolean store(final String index, final String type, final Object obj) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        @SuppressWarnings("unchecked")
        final Map<String, Object> source = obj instanceof Map ? (Map<String, Object>) obj : BeanUtil.copyBeanToNewMap(obj);
        final String id = (String) source.remove(fessConfig.getIndexFieldId());
        final Number version = (Number) source.remove(fessConfig.getIndexFieldVersion());
        IndexResponse response;
        try {
            if (id == null) {
                // TODO throw Exception in next release
                // create
                response =
                        client.prepareIndex(index, type).setSource(new DocMap(source)).setRefreshPolicy(RefreshPolicy.IMMEDIATE)
                                .setOpType(OpType.CREATE).execute().actionGet(fessConfig.getIndexIndexTimeout());
            } else {
                // create or update
                final IndexRequestBuilder builder =
                        client.prepareIndex(index, type, id).setSource(new DocMap(source)).setRefreshPolicy(RefreshPolicy.IMMEDIATE)
                                .setOpType(OpType.INDEX);
                if (version != null && version.longValue() > 0) {
                    builder.setVersion(version.longValue());
                }
                response = builder.execute().actionGet(fessConfig.getIndexIndexTimeout());
            }
            final Result result = response.getResult();
            return result == Result.CREATED || result == Result.UPDATED;
        } catch (final ElasticsearchException e) {
            throw new FessEsClientException("Failed to store: " + obj, e);
        }
    }

    public boolean delete(final String index, final String type, final String id, final long version) {
        try {
            final DeleteRequestBuilder builder = client.prepareDelete(index, type, id).setRefreshPolicy(RefreshPolicy.IMMEDIATE);
            if (version > 0) {
                builder.setVersion(version);
            }
            final DeleteResponse response = builder.execute().actionGet(ComponentUtil.getFessConfig().getIndexDeleteTimeout());
            return response.getResult() == Result.DELETED;
        } catch (final ElasticsearchException e) {
            throw new FessEsClientException("Failed to delete: " + index + "/" + type + "/" + id + "/" + version, e);
        }
    }

    public void setIndexConfigPath(final String indexConfigPath) {
        this.indexConfigPath = indexConfigPath;
    }

    public interface SearchCondition<B> {
        boolean build(B requestBuilder);
    }

    public interface SearchResult<T, B, R> {
        T build(B requestBuilder, long execTime, OptionalEntity<R> response);
    }

    public interface EntityCreator<T, R, H> {
        T build(R response, H hit);
    }

    public void setClusterName(final String clusterName) {
        this.clusterName = clusterName;
    }

    //
    // Elasticsearch Client
    //

    @Override
    public ThreadPool threadPool() {
        return client.threadPool();
    }

    @Override
    public AdminClient admin() {
        return client.admin();
    }

    @Override
    public ActionFuture<IndexResponse> index(final IndexRequest request) {
        return client.index(request);
    }

    @Override
    public void index(final IndexRequest request, final ActionListener<IndexResponse> listener) {
        client.index(request, listener);
    }

    @Override
    public IndexRequestBuilder prepareIndex() {
        return client.prepareIndex();
    }

    @Override
    public ActionFuture<UpdateResponse> update(final UpdateRequest request) {
        return client.update(request);
    }

    @Override
    public void update(final UpdateRequest request, final ActionListener<UpdateResponse> listener) {
        client.update(request, listener);
    }

    @Override
    public UpdateRequestBuilder prepareUpdate() {
        return client.prepareUpdate();
    }

    @Override
    public UpdateRequestBuilder prepareUpdate(final String index, final String type, final String id) {
        return client.prepareUpdate(index, type, id);
    }

    @Override
    public IndexRequestBuilder prepareIndex(final String index, final String type) {
        return client.prepareIndex(index, type);
    }

    @Override
    public IndexRequestBuilder prepareIndex(final String index, final String type, final String id) {
        return client.prepareIndex(index, type, id);
    }

    @Override
    public ActionFuture<DeleteResponse> delete(final DeleteRequest request) {
        return client.delete(request);
    }

    @Override
    public void delete(final DeleteRequest request, final ActionListener<DeleteResponse> listener) {
        client.delete(request, listener);
    }

    @Override
    public DeleteRequestBuilder prepareDelete() {
        return client.prepareDelete();
    }

    @Override
    public DeleteRequestBuilder prepareDelete(final String index, final String type, final String id) {
        return client.prepareDelete(index, type, id);
    }

    @Override
    public ActionFuture<BulkResponse> bulk(final BulkRequest request) {
        return client.bulk(request);
    }

    @Override
    public void bulk(final BulkRequest request, final ActionListener<BulkResponse> listener) {
        client.bulk(request, listener);
    }

    @Override
    public BulkRequestBuilder prepareBulk() {
        return client.prepareBulk();
    }

    @Override
    public ActionFuture<GetResponse> get(final GetRequest request) {
        return client.get(request);
    }

    @Override
    public void get(final GetRequest request, final ActionListener<GetResponse> listener) {
        client.get(request, listener);
    }

    @Override
    public GetRequestBuilder prepareGet() {
        return client.prepareGet();
    }

    @Override
    public GetRequestBuilder prepareGet(final String index, final String type, final String id) {
        return client.prepareGet(index, type, id);
    }

    @Override
    public ActionFuture<MultiGetResponse> multiGet(final MultiGetRequest request) {
        return client.multiGet(request);
    }

    @Override
    public void multiGet(final MultiGetRequest request, final ActionListener<MultiGetResponse> listener) {
        client.multiGet(request, listener);
    }

    @Override
    public MultiGetRequestBuilder prepareMultiGet() {
        return client.prepareMultiGet();
    }

    @Override
    public ActionFuture<SearchResponse> search(final SearchRequest request) {
        return client.search(request);
    }

    @Override
    public void search(final SearchRequest request, final ActionListener<SearchResponse> listener) {
        client.search(request, listener);
    }

    @Override
    public SearchRequestBuilder prepareSearch(final String... indices) {
        return client.prepareSearch(indices);
    }

    @Override
    public ActionFuture<SearchResponse> searchScroll(final SearchScrollRequest request) {
        return client.searchScroll(request);
    }

    @Override
    public void searchScroll(final SearchScrollRequest request, final ActionListener<SearchResponse> listener) {
        client.searchScroll(request, listener);
    }

    @Override
    public SearchScrollRequestBuilder prepareSearchScroll(final String scrollId) {
        return client.prepareSearchScroll(scrollId);
    }

    @Override
    public ActionFuture<MultiSearchResponse> multiSearch(final MultiSearchRequest request) {
        return client.multiSearch(request);
    }

    @Override
    public void multiSearch(final MultiSearchRequest request, final ActionListener<MultiSearchResponse> listener) {
        client.multiSearch(request, listener);
    }

    @Override
    public MultiSearchRequestBuilder prepareMultiSearch() {
        return client.prepareMultiSearch();
    }

    @Override
    public ExplainRequestBuilder prepareExplain(final String index, final String type, final String id) {
        return client.prepareExplain(index, type, id);
    }

    @Override
    public ActionFuture<ExplainResponse> explain(final ExplainRequest request) {
        return client.explain(request);
    }

    @Override
    public void explain(final ExplainRequest request, final ActionListener<ExplainResponse> listener) {
        client.explain(request, listener);
    }

    @Override
    public ClearScrollRequestBuilder prepareClearScroll() {
        return client.prepareClearScroll();
    }

    @Override
    public ActionFuture<ClearScrollResponse> clearScroll(final ClearScrollRequest request) {
        return client.clearScroll(request);
    }

    @Override
    public void clearScroll(final ClearScrollRequest request, final ActionListener<ClearScrollResponse> listener) {
        client.clearScroll(request, listener);
    }

    @Override
    public Settings settings() {
        return client.settings();
    }

    @Override
    public ActionFuture<TermVectorsResponse> termVectors(final TermVectorsRequest request) {
        return client.termVectors(request);
    }

    @Override
    public void termVectors(final TermVectorsRequest request, final ActionListener<TermVectorsResponse> listener) {
        client.termVectors(request, listener);
    }

    @Override
    public TermVectorsRequestBuilder prepareTermVectors() {
        return client.prepareTermVectors();
    }

    @Override
    public TermVectorsRequestBuilder prepareTermVectors(final String index, final String type, final String id) {
        return client.prepareTermVectors(index, type, id);
    }

    @Override
    @Deprecated
    public ActionFuture<TermVectorsResponse> termVector(final TermVectorsRequest request) {
        return client.termVector(request);
    }

    @Override
    @Deprecated
    public void termVector(final TermVectorsRequest request, final ActionListener<TermVectorsResponse> listener) {
        client.termVector(request, listener);
    }

    @Override
    @Deprecated
    public TermVectorsRequestBuilder prepareTermVector() {
        return client.prepareTermVector();
    }

    @Override
    @Deprecated
    public TermVectorsRequestBuilder prepareTermVector(final String index, final String type, final String id) {
        return client.prepareTermVector(index, type, id);
    }

    @Override
    public ActionFuture<MultiTermVectorsResponse> multiTermVectors(final MultiTermVectorsRequest request) {
        return client.multiTermVectors(request);
    }

    @Override
    public void multiTermVectors(final MultiTermVectorsRequest request, final ActionListener<MultiTermVectorsResponse> listener) {
        client.multiTermVectors(request, listener);
    }

    @Override
    public MultiTermVectorsRequestBuilder prepareMultiTermVectors() {
        return client.prepareMultiTermVectors();
    }

    public void setSizeForDelete(final int sizeForDelete) {
        this.sizeForDelete = sizeForDelete;
    }

    public void setScrollForDelete(final String scrollForDelete) {
        this.scrollForDelete = scrollForDelete;
    }

    public void setScrollForSearch(final String scrollForSearch) {
        this.scrollForSearch = scrollForSearch;
    }

    public void setMaxConfigSyncStatusRetry(final int maxConfigSyncStatusRetry) {
        this.maxConfigSyncStatusRetry = maxConfigSyncStatusRetry;
    }

    public void setMaxEsStatusRetry(final int maxEsStatusRetry) {
        this.maxEsStatusRetry = maxEsStatusRetry;
    }

    @Override
    public Client filterWithHeader(final Map<String, String> headers) {
        return client.filterWithHeader(headers);
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> ActionFuture<Response> execute(
            final Action<Request, Response, RequestBuilder> action, final Request request) {
        return client.execute(action, request);
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> void execute(
            final Action<Request, Response, RequestBuilder> action, final Request request, final ActionListener<Response> listener) {
        client.execute(action, request, listener);
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> RequestBuilder prepareExecute(
            final Action<Request, Response, RequestBuilder> action) {
        return client.prepareExecute(action);
    }

    @Override
    public FieldCapabilitiesRequestBuilder prepareFieldCaps(final String... indices) {
        return client.prepareFieldCaps(indices);
    }

    @Override
    public ActionFuture<FieldCapabilitiesResponse> fieldCaps(final FieldCapabilitiesRequest request) {
        return client.fieldCaps(request);
    }

    @Override
    public void fieldCaps(final FieldCapabilitiesRequest request, final ActionListener<FieldCapabilitiesResponse> listener) {
        client.fieldCaps(request, listener);
    }

    @Override
    public BulkRequestBuilder prepareBulk(String globalIndex, String globalType) {
        return client.prepareBulk(globalIndex, globalType);
    }
}
