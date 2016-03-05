/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import static org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.newConfigs;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.Configs;
import org.codelibs.elasticsearch.runner.net.Curl;
import org.codelibs.elasticsearch.runner.net.CurlResponse;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.PingResponse;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.exception.SearchQueryException;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.StreamUtil;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.Action;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkItemResponse.Failure;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.exists.ExistsRequest;
import org.elasticsearch.action.exists.ExistsRequestBuilder;
import org.elasticsearch.action.exists.ExistsResponse;
import org.elasticsearch.action.explain.ExplainRequest;
import org.elasticsearch.action.explain.ExplainRequestBuilder;
import org.elasticsearch.action.explain.ExplainResponse;
import org.elasticsearch.action.fieldstats.FieldStatsRequest;
import org.elasticsearch.action.fieldstats.FieldStatsRequestBuilder;
import org.elasticsearch.action.fieldstats.FieldStatsResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.indexedscripts.delete.DeleteIndexedScriptRequest;
import org.elasticsearch.action.indexedscripts.delete.DeleteIndexedScriptRequestBuilder;
import org.elasticsearch.action.indexedscripts.delete.DeleteIndexedScriptResponse;
import org.elasticsearch.action.indexedscripts.get.GetIndexedScriptRequest;
import org.elasticsearch.action.indexedscripts.get.GetIndexedScriptRequestBuilder;
import org.elasticsearch.action.indexedscripts.get.GetIndexedScriptResponse;
import org.elasticsearch.action.indexedscripts.put.PutIndexedScriptRequest;
import org.elasticsearch.action.indexedscripts.put.PutIndexedScriptRequestBuilder;
import org.elasticsearch.action.indexedscripts.put.PutIndexedScriptResponse;
import org.elasticsearch.action.percolate.MultiPercolateRequest;
import org.elasticsearch.action.percolate.MultiPercolateRequestBuilder;
import org.elasticsearch.action.percolate.MultiPercolateResponse;
import org.elasticsearch.action.percolate.PercolateRequest;
import org.elasticsearch.action.percolate.PercolateRequestBuilder;
import org.elasticsearch.action.percolate.PercolateResponse;
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
import org.elasticsearch.action.suggest.SuggestRequest;
import org.elasticsearch.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
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
import org.elasticsearch.client.support.Headers;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.threadpool.ThreadPool;
import org.lastaflute.core.message.UserMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.BaseEncoding;

public class FessEsClient implements Client {
    private static final Logger logger = LoggerFactory.getLogger(FessEsClient.class);

    protected ElasticsearchClusterRunner runner;

    protected List<TransportAddress> transportAddressList = new ArrayList<>();

    protected Client client;

    protected Map<String, String> settings;

    protected String indexConfigPath = "fess_indices";

    protected List<String> indexConfigList = new ArrayList<>();

    protected Map<String, List<String>> configListMap = new HashMap<>();

    protected int sizeForDelete = 100;

    protected String scrollForDelete = "1m";

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

    public void addTransportAddress(final String host, final int port) {
        try {
            transportAddressList.add(new InetSocketTransportAddress(InetAddress.getByName(host), port));
        } catch (final UnknownHostException e) {
            throw new FessSystemException("Failed to resolve the hostname: " + host, e);
        }
    }

    @PostConstruct
    public void open() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final String transportAddressesValue = System.getProperty(Constants.FESS_ES_TRANSPORT_ADDRESSES);
        if (StringUtil.isNotBlank(transportAddressesValue)) {
            for (final String transportAddressValue : transportAddressesValue.split(",")) {
                final String[] addressPair = transportAddressValue.trim().split(":");
                if (addressPair.length < 3) {
                    final String host = addressPair[0];
                    int port = 9300;
                    if (addressPair.length == 2) {
                        port = Integer.parseInt(addressPair[1]);
                    }
                    addTransportAddress(host, port);
                } else {
                    logger.warn("Invalid address format: " + transportAddressValue);
                }
            }
        }

        if (transportAddressList.isEmpty()) {
            if (runner == null) {
                runner = new ElasticsearchClusterRunner();
                final Configs config = newConfigs().clusterName(fessConfig.getElasticsearchClusterName()).numOfNode(1).useLogger();
                final String esDir = System.getProperty("fess.es.dir");
                if (esDir != null) {
                    config.basePath(esDir);
                }
                runner.onBuild((number, settingsBuilder) -> {
                    final File pluginDir = new File(esDir, "plugins");
                    if (pluginDir.isDirectory()) {
                        settingsBuilder.put("path.plugins", pluginDir.getAbsolutePath());
                    } else {
                        settingsBuilder.put("path.plugins", new File(System.getProperty("user.dir"), "plugins").getAbsolutePath());
                    }
                    if (settings != null) {
                        settingsBuilder.put(settings);
                    }
                });
                runner.build(config);
            }
            client = runner.client();
            addTransportAddress("localhost", runner.node().settings().getAsInt("transport.tcp.port", 9300));
        } else {
            final Builder settingsBuilder = Settings.settingsBuilder();
            settingsBuilder.put("cluster.name", fessConfig.getElasticsearchClusterName());
            final Settings settings = settingsBuilder.build();
            final TransportClient transportClient = TransportClient.builder().settings(settings).build();
            for (final TransportAddress address : transportAddressList) {
                transportClient.addTransportAddress(address);
            }
            client = transportClient;
        }

        if (StringUtil.isBlank(transportAddressesValue)) {
            final StringBuilder buf = new StringBuilder();
            for (final TransportAddress transportAddress : transportAddressList) {
                if (transportAddress instanceof InetSocketTransportAddress) {
                    if (buf.length() > 0) {
                        buf.append(',');
                    }
                    final InetSocketTransportAddress inetTransportAddress = (InetSocketTransportAddress) transportAddress;
                    buf.append(inetTransportAddress.address().getHostName());
                    buf.append(':');
                    buf.append(inetTransportAddress.address().getPort());
                }
            }
            if (buf.length() > 0) {
                System.setProperty(Constants.FESS_ES_TRANSPORT_ADDRESSES, buf.toString());
            }
        }

        waitForYellowStatus();

        indexConfigList.forEach(configName -> {
            final String[] values = configName.split("/");
            if (values.length == 2) {
                final String configIndex = values[0];
                final String configType = values[1];
                boolean exists = false;
                try {
                    IndicesExistsResponse response =
                            client.admin().indices().prepareExists(configIndex).execute().actionGet(fessConfig.getIndexSearchTimeout());
                    exists = response.isExists();
                } catch (final Exception e) {
                    // ignore
            }
            if (!exists) {
                configListMap.getOrDefault(configIndex, Collections.emptyList()).forEach(
                        path -> {
                            String source = null;
                            final String filePath = indexConfigPath + "/" + configIndex + "/" + path;
                            try {
                                source = FileUtil.readUTF8(filePath);
                                try (CurlResponse response =
                                        Curl.post(org.codelibs.fess.util.ResourceUtil.getElasticsearchHttpUrl() + "/_configsync/file")
                                                .param("path", path).body(source).execute()) {
                                    if (response.getHttpStatusCode() == 200) {
                                        logger.info("Register " + path + " to " + configIndex);
                                    } else {
                                        logger.warn("Invalid request for " + path);
                                    }
                                }
                            } catch (final Exception e) {
                                logger.warn("Failed to register " + filePath, e);
                            }
                        });
                try (CurlResponse response =
                        Curl.post(org.codelibs.fess.util.ResourceUtil.getElasticsearchHttpUrl() + "/_configsync/flush").execute()) {
                    if (response.getHttpStatusCode() == 200) {
                        logger.info("Flushed config files.");
                    } else {
                        logger.warn("Failed to flush config files.");
                    }
                } catch (final Exception e) {
                    logger.warn("Failed to flush config files.", e);
                }

                String source = null;
                final String indexConfigFile = indexConfigPath + "/" + configIndex + ".json";
                try {
                    source = FileUtil.readUTF8(indexConfigFile);
                    final String dictionaryPath = System.getProperty("fess.dictionary.path", StringUtil.EMPTY);
                    source = source.replaceAll(Pattern.quote("${fess.dictionary.path}"), dictionaryPath);
                    final CreateIndexResponse indexResponse =
                            client.admin().indices().prepareCreate(configIndex).setSource(source).execute()
                                    .actionGet(fessConfig.getIndexIndicesTimeout());
                    if (indexResponse.isAcknowledged()) {
                        logger.info("Created " + configIndex + " index.");
                    } else if (logger.isDebugEnabled()) {
                        logger.debug("Failed to create " + configIndex + " index.");
                    }
                } catch (final IndexAlreadyExistsException e) {
                    // ignore
                } catch (final Exception e) {
                    logger.warn(indexConfigFile + " is not found.", e);
                }
            }

            final GetMappingsResponse getMappingsResponse =
                    client.admin().indices().prepareGetMappings(configIndex).setTypes(configType).execute()
                            .actionGet(fessConfig.getIndexIndicesTimeout());
            final ImmutableOpenMap<String, MappingMetaData> indexMappings = getMappingsResponse.mappings().get(configIndex);
            if (indexMappings == null || !indexMappings.containsKey(configType)) {
                String source = null;
                final String mappingFile = indexConfigPath + "/" + configIndex + "/" + configType + ".json";
                try {
                    source = FileUtil.readUTF8(mappingFile);
                } catch (final Exception e) {
                    logger.warn(mappingFile + " is not found.", e);
                }
                final PutMappingResponse putMappingResponse =
                        client.admin().indices().preparePutMapping(configIndex).setType(configType).setSource(source).execute()
                                .actionGet(fessConfig.getIndexIndicesTimeout());
                if (putMappingResponse.isAcknowledged()) {
                    logger.info("Created " + configIndex + "/" + configType + " mapping.");
                } else {
                    logger.warn("Failed to create " + configIndex + "/" + configType + " mapping.");
                }

                final String dataPath = indexConfigPath + "/" + configIndex + "/" + configType + ".bulk";
                if (ResourceUtil.isExist(dataPath)) {
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
                                                        client.prepareIndex(configIndex, configType, result.get("index").get("_id"))
                                                                .setSource(line);
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
            } else if (logger.isDebugEnabled()) {
                logger.debug(configIndex + "/" + configType + " mapping exists.");
            }
        } else {
            logger.warn("Invalid index config name: " + configName);
        }
    })  ;
    }

    private void waitForYellowStatus() {
        final ClusterHealthResponse response =
                client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute()
                        .actionGet(ComponentUtil.getFessConfig().getIndexHealthTimeout());
        if (logger.isDebugEnabled()) {
            logger.debug("Elasticsearch Cluster Status: " + response.getStatus());
        }
    }

    @Override
    @PreDestroy
    public void close() {
        try {
            client.admin().indices().prepareFlush().setForce(true).execute()
                    .actionGet(ComponentUtil.getFessConfig().getIndexIndicesTimeout());
        } catch (Exception e) {
            logger.warn("Failed to flush indices.", e);
        }
        try {
            client.close();
        } catch (final ElasticsearchException e) {
            logger.warn("Failed to close Client: " + client, e);
        }
    }

    public int deleteByQuery(final String index, final String type, final QueryBuilder queryBuilder) {

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        SearchResponse response =
                client.prepareSearch(index).setTypes(type).setScroll(scrollForDelete).setSize(sizeForDelete)
                        .addField(fessConfig.getIndexFieldId()).setQuery(queryBuilder).execute()
                        .actionGet(fessConfig.getIndexScrollSearchTimeoutTimeout());

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
            }
            count += hits.length;
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

    public <T> T get(final String index, final String type, final String id, final SearchCondition<GetRequestBuilder> condition,
            final SearchResult<T, GetRequestBuilder, GetResponse> searchResult) {
        final long startTime = System.currentTimeMillis();

        GetResponse response = null;
        final GetRequestBuilder requestBuilder = client.prepareGet(index, type, id);
        if (condition.build(requestBuilder)) {

            if (ComponentUtil.hasQueryHelper()) {
                final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
                for (final Map.Entry<String, String[]> entry : queryHelper.getQueryRequestHeaderMap().entrySet()) {
                    requestBuilder.putHeader(entry.getKey(), entry.getValue());
                }

                final Set<Entry<String, String[]>> paramSet = queryHelper.getRequestParameterSet();
                if (!paramSet.isEmpty()) {
                    for (final Map.Entry<String, String[]> entry : paramSet) {
                        requestBuilder.putHeader(entry.getKey(), entry.getValue());
                    }
                }
            }

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

            if (ComponentUtil.hasQueryHelper()) {
                final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
                if (queryHelper.getTimeAllowed() >= 0) {
                    searchRequestBuilder.setTimeout(TimeValue.timeValueMillis(queryHelper.getTimeAllowed()));
                }

                for (final Map.Entry<String, String[]> entry : queryHelper.getQueryRequestHeaderMap().entrySet()) {
                    searchRequestBuilder.putHeader(entry.getKey(), entry.getValue());
                }

                final Set<Entry<String, String[]>> paramSet = queryHelper.getRequestParameterSet();
                if (!paramSet.isEmpty()) {
                    for (final Map.Entry<String, String[]> entry : paramSet) {
                        searchRequestBuilder.putHeader(entry.getKey(), entry.getValue());
                    }
                }
            }

            try {
                searchResponse = searchRequestBuilder.execute().actionGet(ComponentUtil.getFessConfig().getIndexSearchTimeout());
            } catch (final SearchPhaseExecutionException e) {
                throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryParseError(UserMessages.GLOBAL_PROPERTY_KEY),
                        "Invalid query: " + searchRequestBuilder, e);
            }
        }
        final long execTime = System.currentTimeMillis() - startTime;

        return searchResult.build(searchRequestBuilder, execTime, OptionalEntity.ofNullable(searchResponse, () -> {}));
    }

    public OptionalEntity<Map<String, Object>> getDocument(final String index, final String type,
            final SearchCondition<SearchRequestBuilder> condition) {
        return getDocument(
                index,
                type,
                condition,
                (response, hit) -> {
                    final FessConfig fessConfig = ComponentUtil.getFessConfig();
                    final Map<String, Object> source = hit.getSource();
                    if (source != null) {
                        final Map<String, Object> docMap = new HashMap<>(source);
                        docMap.put(fessConfig.getIndexFieldId(), hit.getId());
                        return docMap;
                    }
                    final Map<String, SearchHitField> fields = hit.getFields();
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

    public <T> OptionalEntity<T> getDocument(final String index, final String type, final SearchCondition<SearchRequestBuilder> condition,
            final EntityCreator<T, SearchResponse, SearchHit> creator) {
        return search(index, type, condition, (queryBuilder, execTime, searchResponse) -> {
            return searchResponse.map(response -> {
                final SearchHit[] hits = response.getHits().hits();
                if (hits.length > 0) {
                    return creator.build(response, hits[0]);
                }
                return null;
            });
        });
    }

    public OptionalEntity<Map<String, Object>> getDocument(final String index, final String type, final String id,
            final SearchCondition<GetRequestBuilder> condition) {
        return getDocument(
                index,
                type,
                id,
                condition,
                (response, result) -> {
                    final FessConfig fessConfig = ComponentUtil.getFessConfig();
                    final Map<String, Object> source = response.getSource();
                    if (source != null) {
                        final Map<String, Object> docMap = new HashMap<>(source);
                        docMap.put(fessConfig.getIndexFieldId(), response.getId());
                        return docMap;
                    }
                    final Map<String, GetField> fields = response.getFields();
                    if (fields != null) {
                        final Map<String, Object> docMap =
                                fields.entrySet().stream()
                                        .collect(Collectors.toMap(e -> e.getKey(), e -> (Object) e.getValue().getValues()));
                        docMap.put(fessConfig.getIndexFieldId(), response.getId());
                        return docMap;
                    }
                    return null;

                });
    }

    public <T> OptionalEntity<T> getDocument(final String index, final String type, final String id,
            final SearchCondition<GetRequestBuilder> condition, final EntityCreator<T, GetResponse, GetResponse> creator) {
        return get(index, type, id, condition, (queryBuilder, execTime, getResponse) -> {
            return getResponse.map(response -> {
                return creator.build(response, response);
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
                    final Map<String, Object> source = hit.getSource();
                    if (source != null) {
                        final Map<String, Object> docMap = new HashMap<>(source);
                        docMap.put(fessConfig.getIndexFieldId(), hit.getId());
                        return docMap;
                    }
                    final Map<String, SearchHitField> fields = hit.getFields();
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

    public <T> List<T> getDocumentList(final String index, final String type, final SearchCondition<SearchRequestBuilder> condition,
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
            return client.prepareUpdate(index, type, id).setDoc(field, value).execute()
                    .actionGet(ComponentUtil.getFessConfig().getIndexIndexTimeout()).isCreated();
        } catch (final ElasticsearchException e) {
            throw new FessEsClientException("Failed to set " + value + " to " + field + " for doc " + id, e);
        }
    }

    public void refresh(final String index) {
        client.admin().indices().prepareRefresh(index).execute(new ActionListener<RefreshResponse>() {
            @Override
            public void onResponse(final RefreshResponse response) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Refreshed " + index + ".");
                }
            }

            @Override
            public void onFailure(final Throwable e) {
                logger.error("Failed to refresh " + index + ".", e);
            }
        });

    }

    public void flush(final String index) {
        client.admin().indices().prepareFlush(index).execute(new ActionListener<FlushResponse>() {

            @Override
            public void onResponse(final FlushResponse response) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Flushed " + index + ".");
                }
            }

            @Override
            public void onFailure(final Throwable e) {
                logger.error("Failed to flush " + index + ".", e);
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

    public void addAll(final String index, final String type, final List<Map<String, Object>> docList) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (final Map<String, Object> doc : docList) {
            final Object id = doc.remove(fessConfig.getIndexFieldId());
            bulkRequestBuilder.add(client.prepareIndex(index, type, id.toString()).setSource(doc));
        }
        final BulkResponse response = bulkRequestBuilder.execute().actionGet(ComponentUtil.getFessConfig().getIndexBulkTimeout());
        if (response.hasFailures()) {
            if (logger.isDebugEnabled()) {
                @SuppressWarnings("rawtypes")
                final List<ActionRequest> requests = bulkRequestBuilder.request().requests();
                final BulkItemResponse[] items = response.getItems();
                if (requests.size() == items.length) {
                    for (int i = 0; i < requests.size(); i++) {
                        final BulkItemResponse resp = items[i];
                        if (resp.isFailed() && resp.getFailure() != null) {
                            @SuppressWarnings("rawtypes")
                            final ActionRequest req = requests.get(i);
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
        private boolean administrativeAccess = false;

        public static SearchConditionBuilder builder(final SearchRequestBuilder searchRequestBuilder) {
            return new SearchConditionBuilder(searchRequestBuilder);
        }

        SearchConditionBuilder(final SearchRequestBuilder searchRequestBuilder) {
            this.searchRequestBuilder = searchRequestBuilder;
        }

        public SearchConditionBuilder query(final String query) {
            this.query = query;
            return this;
        }

        public SearchConditionBuilder administrativeAccess(final boolean administrativeAccess) {
            this.administrativeAccess = administrativeAccess;
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

        public SearchConditionBuilder facetInfo(final FacetInfo facetInfo) {
            this.facetInfo = facetInfo;
            return this;
        }

        public boolean build() {
            if (StringUtil.isBlank(query)) {
                return false;
            }

            final QueryHelper queryHelper = ComponentUtil.getQueryHelper();

            if (offset > queryHelper.getMaxSearchResultOffset()) {
                throw new ResultOffsetExceededException("The number of result size is exceeded.");
            }

            final QueryContext queryContext = queryHelper.build(query, context -> {
                if (administrativeAccess) {
                    context.skipRoleQuery();
                }
                // geo
                    if (geoInfo != null && geoInfo.isAvailable()) {
                        context.addQuery(boolQuery -> {
                            boolQuery.filter(geoInfo.toQueryBuilder());
                        });
                    }
                });

            searchRequestBuilder.setFrom(offset).setSize(size);

            if (responseFields != null) {
                searchRequestBuilder.addFields(responseFields);
            }

            // sort
            queryContext.sortBuilders().forEach(sortBuilder -> searchRequestBuilder.addSort(sortBuilder));

            // highlighting
            queryHelper.highlightedFields().forEach(
                    hf -> searchRequestBuilder.addHighlightedField(hf, queryHelper.getHighlightFragmentSize()));

            // facets
            if (facetInfo != null) {
                StreamUtil.of(facetInfo.field).forEach(f -> {
                    if (queryHelper.isFacetField(f)) {
                        final String encodedField = BaseEncoding.base64().encode(f.getBytes(StandardCharsets.UTF_8));
                        final TermsBuilder termsBuilder = AggregationBuilders.terms(Constants.FACET_FIELD_PREFIX + encodedField).field(f);
                        if ("term".equals(facetInfo.sort)) {
                            termsBuilder.order(Order.term(true));
                        } else if ("count".equals(facetInfo.sort)) {
                            termsBuilder.order(Order.count(true));
                        }
                        if (facetInfo.limit != null) {
                            termsBuilder.size(Integer.parseInt(facetInfo.limit));
                        }
                        if (facetInfo.minCount != null) {
                            termsBuilder.minDocCount(Long.parseLong(facetInfo.minCount));
                        }
                        if (facetInfo.missing != null) {
                            termsBuilder.missing(facetInfo.missing);
                        }
                        searchRequestBuilder.addAggregation(termsBuilder);
                    } else {
                        throw new SearchQueryException("Invalid facet field: " + f);
                    }
                });
                StreamUtil.of(facetInfo.query).forEach(
                        fq -> {
                            final QueryContext facetContext = new QueryContext(fq, false);
                            queryHelper.buildBaseQuery(facetContext, c -> {});
                            final String encodedFacetQuery = BaseEncoding.base64().encode(fq.getBytes(StandardCharsets.UTF_8));
                            final FilterAggregationBuilder filterBuilder =
                                    AggregationBuilders.filter(Constants.FACET_QUERY_PREFIX + encodedFacetQuery).filter(
                                            facetContext.getQueryBuilder());
                            searchRequestBuilder.addAggregation(filterBuilder);
                        });
            }

            searchRequestBuilder.setQuery(queryContext.getQueryBuilder());
            if (logger.isDebugEnabled()) {
                logger.debug("Query: " + searchRequestBuilder);
            }

            return true;
        }
    }

    public boolean store(final String index, final String type, final Object obj) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Map<String, Object> source = BeanUtil.copyBeanToNewMap(obj);
        final String id = (String) source.remove(fessConfig.getIndexFieldId());
        final Long version = (Long) source.remove("version");
        IndexResponse response;
        try {
            if (id == null) {
                // create
                response =
                        client.prepareIndex(index, type).setSource(source).setRefresh(true).setOpType(OpType.CREATE).execute()
                                .actionGet(fessConfig.getIndexIndexTimeout());
            } else {
                // create or update
                response =
                        client.prepareIndex(index, type, id).setSource(source).setRefresh(true).setOpType(OpType.INDEX).setVersion(version)
                                .execute().actionGet(fessConfig.getIndexIndexTimeout());
            }
            return response.isCreated();
        } catch (final ElasticsearchException e) {
            throw new FessEsClientException("Failed to store: " + obj, e);
        }
    }

    public boolean delete(final String index, final String type, final String id, final long version) {
        try {
            final DeleteRequestBuilder builder = client.prepareDelete(index, type, id).setRefresh(true);
            if (version > 0) {
                builder.setVersion(version);
            }
            final DeleteResponse response = builder.execute().actionGet(ComponentUtil.getFessConfig().getIndexDeleteTimeout());
            return response.isFound();
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
    public PutIndexedScriptRequestBuilder preparePutIndexedScript() {
        return client.preparePutIndexedScript();
    }

    @Override
    public PutIndexedScriptRequestBuilder preparePutIndexedScript(final String scriptLang, final String id, final String source) {
        return client.preparePutIndexedScript(scriptLang, id, source);
    }

    @Override
    public void deleteIndexedScript(final DeleteIndexedScriptRequest request, final ActionListener<DeleteIndexedScriptResponse> listener) {
        client.deleteIndexedScript(request, listener);
    }

    @Override
    public ActionFuture<DeleteIndexedScriptResponse> deleteIndexedScript(final DeleteIndexedScriptRequest request) {
        return client.deleteIndexedScript(request);
    }

    @Override
    public DeleteIndexedScriptRequestBuilder prepareDeleteIndexedScript() {
        return client.prepareDeleteIndexedScript();
    }

    @Override
    public DeleteIndexedScriptRequestBuilder prepareDeleteIndexedScript(final String scriptLang, final String id) {
        return client.prepareDeleteIndexedScript(scriptLang, id);
    }

    @Override
    public void putIndexedScript(final PutIndexedScriptRequest request, final ActionListener<PutIndexedScriptResponse> listener) {
        client.putIndexedScript(request, listener);
    }

    @Override
    public ActionFuture<PutIndexedScriptResponse> putIndexedScript(final PutIndexedScriptRequest request) {
        return client.putIndexedScript(request);
    }

    @Override
    public GetIndexedScriptRequestBuilder prepareGetIndexedScript() {
        return client.prepareGetIndexedScript();
    }

    @Override
    public GetIndexedScriptRequestBuilder prepareGetIndexedScript(final String scriptLang, final String id) {
        return client.prepareGetIndexedScript(scriptLang, id);
    }

    @Override
    public void getIndexedScript(final GetIndexedScriptRequest request, final ActionListener<GetIndexedScriptResponse> listener) {
        client.getIndexedScript(request, listener);
    }

    @Override
    public ActionFuture<GetIndexedScriptResponse> getIndexedScript(final GetIndexedScriptRequest request) {
        return client.getIndexedScript(request);
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

    @SuppressWarnings("deprecation")
    @Override
    public ActionFuture<CountResponse> count(final CountRequest request) {
        return client.count(request);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void count(final CountRequest request, final ActionListener<CountResponse> listener) {
        client.count(request, listener);
    }

    @SuppressWarnings("deprecation")
    @Override
    public CountRequestBuilder prepareCount(final String... indices) {
        return client.prepareCount(indices);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionFuture<ExistsResponse> exists(final ExistsRequest request) {
        return client.exists(request);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void exists(final ExistsRequest request, final ActionListener<ExistsResponse> listener) {
        client.exists(request, listener);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ExistsRequestBuilder prepareExists(final String... indices) {
        return client.prepareExists(indices);
    }

    @Override
    public ActionFuture<SuggestResponse> suggest(final SuggestRequest request) {
        return client.suggest(request);
    }

    @Override
    public void suggest(final SuggestRequest request, final ActionListener<SuggestResponse> listener) {
        client.suggest(request, listener);
    }

    @Override
    public SuggestRequestBuilder prepareSuggest(final String... indices) {
        return client.prepareSuggest(indices);
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
    public ActionFuture<PercolateResponse> percolate(final PercolateRequest request) {
        return client.percolate(request);
    }

    @Override
    public void percolate(final PercolateRequest request, final ActionListener<PercolateResponse> listener) {
        client.percolate(request, listener);
    }

    @Override
    public PercolateRequestBuilder preparePercolate() {
        return client.preparePercolate();
    }

    @Override
    public ActionFuture<MultiPercolateResponse> multiPercolate(final MultiPercolateRequest request) {
        return client.multiPercolate(request);
    }

    @Override
    public void multiPercolate(final MultiPercolateRequest request, final ActionListener<MultiPercolateResponse> listener) {
        client.multiPercolate(request, listener);
    }

    @Override
    public MultiPercolateRequestBuilder prepareMultiPercolate() {
        return client.prepareMultiPercolate();
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
    public FieldStatsRequestBuilder prepareFieldStats() {
        return client.prepareFieldStats();
    }

    @Override
    public ActionFuture<FieldStatsResponse> fieldStats(final FieldStatsRequest request) {
        return client.fieldStats(request);
    }

    @Override
    public void fieldStats(final FieldStatsRequest request, final ActionListener<FieldStatsResponse> listener) {
        client.fieldStats(request, listener);
    }

    @Override
    public Settings settings() {
        return client.settings();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> ActionFuture<Response> execute(
            final Action<Request, Response, RequestBuilder> action, final Request request) {
        return client.execute(action, request);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> void execute(
            final Action<Request, Response, RequestBuilder> action, final Request request, final ActionListener<Response> listener) {
        client.execute(action, request, listener);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> RequestBuilder prepareExecute(
            final Action<Request, Response, RequestBuilder> action) {
        return client.prepareExecute(action);
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

    @Override
    public Headers headers() {
        return client.headers();
    }

    public void setSizeForDelete(final int sizeForDelete) {
        this.sizeForDelete = sizeForDelete;
    }

    public void setScrollForDelete(final String scrollForDelete) {
        this.scrollForDelete = scrollForDelete;
    }

}
