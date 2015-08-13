package org.codelibs.fess.client;

import static org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.newConfigs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.codec.Charsets;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.Configs;
import org.codelibs.elasticsearch.runner.net.Curl;
import org.codelibs.elasticsearch.runner.net.CurlResponse;
import org.codelibs.fess.Constants;
import org.codelibs.fess.ResultOffsetExceededException;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.PingResponse;
import org.codelibs.fess.entity.SearchQuery;
import org.codelibs.fess.entity.SearchQuery.SortField;
import org.codelibs.fess.solr.FessSolrQueryException;
import org.codelibs.fess.util.ComponentUtil;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.Action;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.ShardOperationFailedException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequest;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
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
import org.elasticsearch.action.mlt.MoreLikeThisRequest;
import org.elasticsearch.action.mlt.MoreLikeThisRequestBuilder;
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
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.action.suggest.SuggestRequest;
import org.elasticsearch.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.action.termvector.MultiTermVectorsRequest;
import org.elasticsearch.action.termvector.MultiTermVectorsRequestBuilder;
import org.elasticsearch.action.termvector.MultiTermVectorsResponse;
import org.elasticsearch.action.termvector.TermVectorRequest;
import org.elasticsearch.action.termvector.TermVectorRequestBuilder;
import org.elasticsearch.action.termvector.TermVectorResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.threadpool.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.BaseEncoding;

public class FessEsClient implements Client {
    private static final Logger logger = LoggerFactory.getLogger(FessEsClient.class);

    protected ElasticsearchClusterRunner runner;

    protected List<TransportAddress> transportAddressList = new ArrayList<>();

    protected Client client;

    protected String clusterName = "elasticsearch";

    protected Map<String, String> settings;

    protected String indexConfigPath = "fess_indices";

    protected List<String> indexConfigList = new ArrayList<>();

    protected Map<String, List<String>> configListMap = new HashMap<>();

    public void addIndexConfig(String path) {
        indexConfigList.add(path);
    }

    public void addConfigFile(String index, String path) {
        List<String> list = configListMap.get(index);
        if (list == null) {
            list = new ArrayList<>();
            configListMap.put(index, list);
        }
        list.add(path);
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public void setRunner(ElasticsearchClusterRunner runner) {
        this.runner = runner;
    }

    public void addTransportAddress(String host, int port) {
        transportAddressList.add(new InetSocketTransportAddress(host, port));
    }

    @PostConstruct
    public void open() {
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
                final Configs config = newConfigs().clusterName(clusterName).numOfNode(1);
                final String esDir = System.getProperty("fess.es.dir");
                if (esDir != null) {
                    config.basePath(esDir);
                }
                runner.onBuild((number, settingsBuilder) -> {
                    if (settings != null) {
                        settingsBuilder.put(settings);
                    }
                    settingsBuilder.put("path.plugins", System.getProperty("user.dir") + "/plugins");
                });
                runner.build(config);
            }
            client = runner.client();
            addTransportAddress("localhost", runner.node().settings().getAsInt("transport.tcp.port", 9300));
        } else {
            final Builder settingsBuilder = ImmutableSettings.settingsBuilder();
            settingsBuilder.put("cluster.name", clusterName);
            final Settings settings = settingsBuilder.build();
            final TransportClient transportClient = new TransportClient(settings);
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
                    client.prepareExists(configIndex).execute().actionGet();
                    exists = true;
                } catch (final IndexMissingException e) {
                    // ignore
            }
            if (!exists) {
                if (runner != null) {// TODO replace with url
                    configListMap.getOrDefault(configIndex, Collections.emptyList()).forEach(
                            path -> {
                                String source = null;
                                final String filePath = indexConfigPath + "/" + configIndex + "/" + path;
                                try {
                                    source = FileUtil.readUTF8(filePath);
                                    try (CurlResponse response =
                                            Curl.post(runner.node(), "_configsync/file").param("path", path).body(source).execute()) {
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
                }

                try {
                    String source = null;
                    final String indexConfigFile = indexConfigPath + "/" + configIndex + ".json";
                    try {
                        source = FileUtil.readUTF8(indexConfigFile);
                    } catch (final Exception e) {
                        logger.warn(indexConfigFile + " is not found.", e);
                    }
                    final CreateIndexResponse indexResponse =
                            client.admin().indices().prepareCreate(configIndex).setSource(source).execute().actionGet();
                    if (indexResponse.isAcknowledged()) {
                        logger.info("Created " + configIndex + " index.");
                    } else if (logger.isDebugEnabled()) {
                        logger.debug("Failed to create " + configIndex + " index.");
                    }
                } catch (final IndexAlreadyExistsException e) {
                    // ignore
                }
            }

            final GetMappingsResponse getMappingsResponse =
                    client.admin().indices().prepareGetMappings(configIndex).setTypes(configType).execute().actionGet();
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
                        client.admin().indices().preparePutMapping(configIndex).setType(configType).setSource(source).execute().actionGet();
                if (putMappingResponse.isAcknowledged()) {
                    logger.info("Created " + configIndex + "/" + configType + " mapping.");
                } else {
                    logger.warn("Failed to create " + configIndex + "/" + configType + " mapping.");
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
        final ClusterHealthResponse response = client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
        if (logger.isDebugEnabled()) {
            logger.debug("Elasticsearch Cluster Status: {0}", response.getStatus());
        }
    }

    @Override
    @PreDestroy
    public void close() {
        try {
            client.close();
        } catch (final ElasticsearchException e) {
            logger.warn("Failed to close Client: " + client, e);
        }
    }

    public void deleteByQuery(String index, String type, QueryBuilder queryBuilder) {
        try {
            // TODO replace with deleting bulk ids with scroll/scan
            client.prepareDeleteByQuery(index).setQuery(queryBuilder).setTypes(type).execute().actionGet().forEach(res -> {
                final ShardOperationFailedException[] failures = res.getFailures();
                if (failures.length > 0) {
                    final StringBuilder buf = new StringBuilder(200);
                    buf.append("Failed to delete documents in some shards.");
                    for (final ShardOperationFailedException failure : failures) {
                        buf.append('\n').append(failure.toString());
                    }
                    throw new FessEsClientException(buf.toString());
                }
            });
        } catch (final ElasticsearchException e) {
            throw new FessEsClientException("Failed to delete documents.", e);
        }
    }

    public <T> T search(String index, String type, SearchCondition condition, SearchResult<T> searchResult) {
        final long startTime = System.currentTimeMillis();

        SearchResponse searchResponse = null;
        final SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type);
        if (condition.build(searchRequestBuilder)) {

            if (ComponentUtil.getQueryHelper().getTimeAllowed() >= 0) {
                searchRequestBuilder.setTimeout(TimeValue.timeValueMillis(ComponentUtil.getQueryHelper().getTimeAllowed()));
            }

            for (final Map.Entry<String, String[]> entry : ComponentUtil.getQueryHelper().getQueryParamMap().entrySet()) {
                searchRequestBuilder.putHeader(entry.getKey(), entry.getValue());
            }

            final Set<Entry<String, String[]>> paramSet = ComponentUtil.getQueryHelper().getRequestParameterSet();
            if (!paramSet.isEmpty()) {
                for (final Map.Entry<String, String[]> entry : paramSet) {
                    searchRequestBuilder.putHeader(entry.getKey(), entry.getValue());
                }
            }

            searchResponse = searchRequestBuilder.execute().actionGet();
        }
        final long execTime = System.currentTimeMillis() - startTime;

        return searchResult.build(searchRequestBuilder, execTime, Optional.ofNullable(searchResponse));
    }

    public Optional<Map<String, Object>> getDocument(String index, String type, final SearchCondition condition) {
        return getDocument(index, type, condition, (response, hit) -> {
            return hit.getSource();
        });
    }

    public <T> Optional<T> getDocument(String index, String type, final SearchCondition condition, EntityCreator<T> creator) {
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

    public List<Map<String, Object>> getDocumentList(String index, String type, final SearchCondition condition) {
        return getDocumentList(index, type, condition, (response, hit) -> {
            return hit.getSource();
        });
    }

    public <T> List<T> getDocumentList(String index, String type, final SearchCondition condition, EntityCreator<T> creator) {
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

    public boolean update(String index, String type, String id, String field, Object value) {
        try {
            return client.prepareUpdate(index, type, id).setDoc(field, value).execute().actionGet().isCreated();
        } catch (final ElasticsearchException e) {
            throw new FessEsClientException("Failed to set " + value + " to " + field + " for doc " + id, e);
        }
    }

    public void refresh(String index) {
        client.admin().indices().prepareRefresh(index).execute(new ActionListener<RefreshResponse>() {
            @Override
            public void onResponse(RefreshResponse response) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Refreshed " + index + ".");
                }
            }

            @Override
            public void onFailure(Throwable e) {
                logger.error("Failed to refresh " + index + ".", e);
            }
        });

    }

    public void flush(String index) {
        client.admin().indices().prepareFlush(index).execute(new ActionListener<FlushResponse>() {

            @Override
            public void onResponse(FlushResponse response) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Flushed " + index + ".");
                }
            }

            @Override
            public void onFailure(Throwable e) {
                logger.error("Failed to flush " + index + ".", e);
            }
        });

    }

    public void optimize(String index) {
        client.admin().indices().prepareOptimize(index).execute(new ActionListener<OptimizeResponse>() {

            @Override
            public void onResponse(OptimizeResponse response) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Optimzed " + index + ".");
                }
            }

            @Override
            public void onFailure(Throwable e) {
                logger.error("Failed to optimze " + index + ".", e);
            }
        });
    }

    public PingResponse ping() {
        try {
            final ClusterHealthResponse response = client.admin().cluster().prepareHealth().execute().actionGet();
            return new PingResponse(response);
        } catch (final ElasticsearchException e) {
            throw new FessEsClientException("Failed to process a ping request.", e);
        }
    }

    public void addAll(String index, String type, List<Map<String, Object>> docList) {
        final BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (final Map<String, Object> doc : docList) {
            bulkRequestBuilder.add(client.prepareIndex(index, type).setSource(doc));
        }
        final BulkResponse response = bulkRequestBuilder.execute().actionGet();
        final String failureMessage = response.buildFailureMessage();
        if (StringUtil.isNotBlank(failureMessage)) {
            throw new FessEsClientException(failureMessage);
        }
    }

    public static class SearchConditionBuilder {
        private final SearchRequestBuilder searchRequestBuilder;
        private String query;
        private boolean administrativeAccess = false;
        private String[] responseFields;
        private int offset = Constants.DEFAULT_START_COUNT;
        private int size = Constants.DEFAULT_PAGE_SIZE;
        private GeoInfo geoInfo;
        private FacetInfo facetInfo;

        public static SearchConditionBuilder builder(SearchRequestBuilder searchRequestBuilder) {
            return new SearchConditionBuilder(searchRequestBuilder);
        }

        SearchConditionBuilder(SearchRequestBuilder searchRequestBuilder) {
            this.searchRequestBuilder = searchRequestBuilder;
        }

        public SearchConditionBuilder query(String query) {
            this.query = query;
            return this;
        }

        public SearchConditionBuilder administrativeAccess() {
            this.administrativeAccess = true;
            return this;
        }

        public SearchConditionBuilder responseFields(String[] responseFields) {
            this.responseFields = responseFields;
            return this;
        }

        public SearchConditionBuilder offset(int offset) {
            this.offset = offset;
            return this;
        }

        public SearchConditionBuilder size(int size) {
            this.size = size;
            return this;
        }

        public SearchConditionBuilder geoInfo(GeoInfo geoInfo) {
            this.geoInfo = geoInfo;
            return this;
        }

        public SearchConditionBuilder facetInfo(FacetInfo facetInfo) {
            this.facetInfo = facetInfo;
            return this;
        }

        public boolean build() {
            if (offset > ComponentUtil.getQueryHelper().getMaxSearchResultOffset()) {
                throw new ResultOffsetExceededException("The number of result size is exceeded.");
            }

            final SearchQuery searchQuery = ComponentUtil.getQueryHelper().build(query, administrativeAccess);
            final String q = searchQuery.getQuery();
            if (StringUtil.isBlank(q)) {
                return false;
            }

            searchRequestBuilder.setFrom(offset).setSize(size);

            if (responseFields != null) {
                searchRequestBuilder.addFields(responseFields);
            }

            // sort
            final SortField[] sortFields = searchQuery.getSortFields();
            if (sortFields.length != 0) {
                for (final SortField sortField : sortFields) {
                    final FieldSortBuilder fieldSort = SortBuilders.fieldSort(sortField.getField());
                    if (Constants.DESC.equals(sortField.getOrder())) {
                        fieldSort.order(SortOrder.DESC);
                    } else {
                        fieldSort.order(SortOrder.ASC);
                    }
                    searchRequestBuilder.addSort(fieldSort);
                }
            } else if (ComponentUtil.getQueryHelper().hasDefaultSortFields()) {
                for (final SortField sortField : ComponentUtil.getQueryHelper().getDefaultSortFields()) {
                    final FieldSortBuilder fieldSort = SortBuilders.fieldSort(sortField.getField());
                    if (Constants.DESC.equals(sortField.getOrder())) {
                        fieldSort.order(SortOrder.DESC);
                    } else {
                        fieldSort.order(SortOrder.ASC);
                    }
                    searchRequestBuilder.addSort(fieldSort);
                }
            }
            // highlighting
            if (ComponentUtil.getQueryHelper().getHighlightingFields() != null
                    && ComponentUtil.getQueryHelper().getHighlightingFields().length != 0) {
                for (final String hf : ComponentUtil.getQueryHelper().getHighlightingFields()) {
                    searchRequestBuilder.addHighlightedField(hf, ComponentUtil.getQueryHelper().getHighlightSnippetSize());
                }
            }

            // facets
            if (facetInfo != null) {
                if (facetInfo.field != null) {
                    for (final String f : facetInfo.field) {
                        if (ComponentUtil.getQueryHelper().isFacetField(f)) {
                            final String encodedField = BaseEncoding.base64().encode(f.getBytes(Charsets.UTF_8));
                            final TermsBuilder termsBuilder =
                                    AggregationBuilders.terms(Constants.FACET_FIELD_PREFIX + encodedField).field(f);
                            // TODO order
                            if (facetInfo.limit != null) {
                                // TODO
                                termsBuilder.size(Integer.parseInt(facetInfo.limit));
                            }
                            searchRequestBuilder.addAggregation(termsBuilder);
                        } else {
                            throw new FessSolrQueryException("Invalid facet field: " + f);
                        }
                    }
                }
                if (facetInfo.query != null) {
                    for (final String fq : facetInfo.query) {
                        final String facetQuery = ComponentUtil.getQueryHelper().buildFacetQuery(fq);
                        if (StringUtil.isNotBlank(facetQuery)) {
                            final String encodedFacetQuery = BaseEncoding.base64().encode(facetQuery.getBytes(Charsets.UTF_8));
                            final FilterAggregationBuilder filterBuilder =
                                    AggregationBuilders.filter(Constants.FACET_QUERY_PREFIX + encodedFacetQuery).filter(
                                            FilterBuilders.queryFilter(QueryBuilders.queryStringQuery(facetQuery)));
                            // TODO order
                            if (facetInfo.limit != null) {
                                // TODO
                                //    filterBuilder.size(Integer.parseInt(facetInfo .limit));
                            }
                            searchRequestBuilder.addAggregation(filterBuilder);
                        } else {
                            throw new FessSolrQueryException("Invalid facet query: " + facetQuery);
                        }
                    }
                }
            }

            BoolFilterBuilder boolFilterBuilder = null;

            // query
            QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(q);
            // filter query
            if (searchQuery.hasFilterQueries()) {
                if (boolFilterBuilder == null) {
                    boolFilterBuilder = FilterBuilders.boolFilter();
                }
                for (final String filterQuery : searchQuery.getFilterQueries()) {
                    boolFilterBuilder.must(FilterBuilders.queryFilter(QueryBuilders.queryStringQuery(filterQuery)));
                }
            }
            // geo
            if (geoInfo != null && geoInfo.isAvailable()) {
                if (boolFilterBuilder == null) {
                    boolFilterBuilder = FilterBuilders.boolFilter();
                }
                boolFilterBuilder.must(geoInfo.toFilterBuilder());
            }

            if (boolFilterBuilder != null) {
                queryBuilder = QueryBuilders.filteredQuery(queryBuilder, boolFilterBuilder);
            }

            searchRequestBuilder.setQuery(queryBuilder);

            return true;
        }
    }

    public boolean store(String index, String type, Object obj) {
        final Map<String, Object> source = BeanUtil.copyBeanToNewMap(obj);
        final String id = (String) source.remove("id");
        final Long version = (Long) source.remove("version");
        IndexResponse response;
        try {
            if (id == null) {
                // create
                response =
                        client.prepareIndex(index, type).setSource(source).setRefresh(true).setOpType(OpType.CREATE).execute().actionGet();
            } else {
                // update
                response =
                        client.prepareIndex(index, type, id).setSource(source).setRefresh(true).setOpType(OpType.INDEX).setVersion(version)
                                .execute().actionGet();
            }
            return response.isCreated();
        } catch (final ElasticsearchException e) {
            throw new FessEsClientException("Failed to store: " + obj, e);
        }
    }

    public boolean delete(String index, String type, String id, long version) {
        try {
            final DeleteResponse response =
                    client.prepareDelete(index, type, id).setVersion(version).setRefresh(true).execute().actionGet();
            return response.isFound();
        } catch (final ElasticsearchException e) {
            throw new FessEsClientException("Failed to delete: " + index + "/" + type + "/" + id + "/" + version, e);
        }
    }

    public void setIndexConfigPath(String indexConfigPath) {
        this.indexConfigPath = indexConfigPath;
    }

    public interface SearchCondition {
        boolean build(SearchRequestBuilder searchRequestBuilder);
    }

    public interface SearchResult<T> {
        T build(SearchRequestBuilder searchRequestBuilder, long execTime, Optional<SearchResponse> searchResponse);
    }

    public interface EntityCreator<T> {
        T build(SearchResponse response, SearchHit hit);
    }

    //
    // Elasticsearch Client
    //

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder, Client>> ActionFuture<Response> execute(
            Action<Request, Response, RequestBuilder, Client> action, Request request) {
        return client.execute(action, request);
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder, Client>> void execute(
            Action<Request, Response, RequestBuilder, Client> action, Request request, ActionListener<Response> listener) {
        client.execute(action, request, listener);
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder, Client>> RequestBuilder prepareExecute(
            Action<Request, Response, RequestBuilder, Client> action) {
        return client.prepareExecute(action);
    }

    @Override
    public ThreadPool threadPool() {
        return client.threadPool();
    }

    @Override
    public AdminClient admin() {
        return client.admin();
    }

    @Override
    public ActionFuture<IndexResponse> index(IndexRequest request) {
        return client.index(request);
    }

    @Override
    public void index(IndexRequest request, ActionListener<IndexResponse> listener) {
        client.index(request, listener);
    }

    @Override
    public IndexRequestBuilder prepareIndex() {
        return client.prepareIndex();
    }

    @Override
    public ActionFuture<UpdateResponse> update(UpdateRequest request) {
        return client.update(request);
    }

    @Override
    public void update(UpdateRequest request, ActionListener<UpdateResponse> listener) {
        client.update(request, listener);
    }

    @Override
    public UpdateRequestBuilder prepareUpdate() {
        return client.prepareUpdate();
    }

    @Override
    public UpdateRequestBuilder prepareUpdate(String index, String type, String id) {
        return client.prepareUpdate(index, type, id);
    }

    @Override
    public IndexRequestBuilder prepareIndex(String index, String type) {
        return client.prepareIndex(index, type);
    }

    @Override
    public IndexRequestBuilder prepareIndex(String index, String type, String id) {
        return client.prepareIndex(index, type, id);
    }

    @Override
    public ActionFuture<DeleteResponse> delete(DeleteRequest request) {
        return client.delete(request);
    }

    @Override
    public void delete(DeleteRequest request, ActionListener<DeleteResponse> listener) {
        client.delete(request, listener);
    }

    @Override
    public DeleteRequestBuilder prepareDelete() {
        return client.prepareDelete();
    }

    @Override
    public DeleteRequestBuilder prepareDelete(String index, String type, String id) {
        return client.prepareDelete(index, type, id);
    }

    @Override
    public ActionFuture<BulkResponse> bulk(BulkRequest request) {
        return client.bulk(request);
    }

    @Override
    public void bulk(BulkRequest request, ActionListener<BulkResponse> listener) {
        client.bulk(request, listener);
    }

    @Override
    public BulkRequestBuilder prepareBulk() {
        return client.prepareBulk();
    }

    @Override
    public ActionFuture<DeleteByQueryResponse> deleteByQuery(DeleteByQueryRequest request) {
        return client.deleteByQuery(request);
    }

    @Override
    public void deleteByQuery(DeleteByQueryRequest request, ActionListener<DeleteByQueryResponse> listener) {
        client.deleteByQuery(request, listener);
    }

    @Override
    public DeleteByQueryRequestBuilder prepareDeleteByQuery(String... indices) {
        return client.prepareDeleteByQuery(indices);
    }

    @Override
    public ActionFuture<GetResponse> get(GetRequest request) {
        return client.get(request);
    }

    @Override
    public void get(GetRequest request, ActionListener<GetResponse> listener) {
        client.get(request, listener);
    }

    @Override
    public GetRequestBuilder prepareGet() {
        return client.prepareGet();
    }

    @Override
    public GetRequestBuilder prepareGet(String index, String type, String id) {
        return client.prepareGet(index, type, id);
    }

    @Override
    public PutIndexedScriptRequestBuilder preparePutIndexedScript() {
        return client.preparePutIndexedScript();
    }

    @Override
    public PutIndexedScriptRequestBuilder preparePutIndexedScript(String scriptLang, String id, String source) {
        return client.preparePutIndexedScript(scriptLang, id, source);
    }

    @Override
    public void deleteIndexedScript(DeleteIndexedScriptRequest request, ActionListener<DeleteIndexedScriptResponse> listener) {
        client.deleteIndexedScript(request, listener);
    }

    @Override
    public ActionFuture<DeleteIndexedScriptResponse> deleteIndexedScript(DeleteIndexedScriptRequest request) {
        return client.deleteIndexedScript(request);
    }

    @Override
    public DeleteIndexedScriptRequestBuilder prepareDeleteIndexedScript() {
        return client.prepareDeleteIndexedScript();
    }

    @Override
    public DeleteIndexedScriptRequestBuilder prepareDeleteIndexedScript(String scriptLang, String id) {
        return client.prepareDeleteIndexedScript(scriptLang, id);
    }

    @Override
    public void putIndexedScript(PutIndexedScriptRequest request, ActionListener<PutIndexedScriptResponse> listener) {
        client.putIndexedScript(request, listener);
    }

    @Override
    public ActionFuture<PutIndexedScriptResponse> putIndexedScript(PutIndexedScriptRequest request) {
        return client.putIndexedScript(request);
    }

    @Override
    public GetIndexedScriptRequestBuilder prepareGetIndexedScript() {
        return client.prepareGetIndexedScript();
    }

    @Override
    public GetIndexedScriptRequestBuilder prepareGetIndexedScript(String scriptLang, String id) {
        return client.prepareGetIndexedScript(scriptLang, id);
    }

    @Override
    public void getIndexedScript(GetIndexedScriptRequest request, ActionListener<GetIndexedScriptResponse> listener) {
        client.getIndexedScript(request, listener);
    }

    @Override
    public ActionFuture<GetIndexedScriptResponse> getIndexedScript(GetIndexedScriptRequest request) {
        return client.getIndexedScript(request);
    }

    @Override
    public ActionFuture<MultiGetResponse> multiGet(MultiGetRequest request) {
        return client.multiGet(request);
    }

    @Override
    public void multiGet(MultiGetRequest request, ActionListener<MultiGetResponse> listener) {
        client.multiGet(request, listener);
    }

    @Override
    public MultiGetRequestBuilder prepareMultiGet() {
        return client.prepareMultiGet();
    }

    @Override
    public ActionFuture<CountResponse> count(CountRequest request) {
        return client.count(request);
    }

    @Override
    public void count(CountRequest request, ActionListener<CountResponse> listener) {
        client.count(request, listener);
    }

    @Override
    public CountRequestBuilder prepareCount(String... indices) {
        return client.prepareCount(indices);
    }

    @Override
    public ActionFuture<ExistsResponse> exists(ExistsRequest request) {
        return client.exists(request);
    }

    @Override
    public void exists(ExistsRequest request, ActionListener<ExistsResponse> listener) {
        client.exists(request, listener);
    }

    @Override
    public ExistsRequestBuilder prepareExists(String... indices) {
        return client.prepareExists(indices);
    }

    @Override
    public ActionFuture<SuggestResponse> suggest(SuggestRequest request) {
        return client.suggest(request);
    }

    @Override
    public void suggest(SuggestRequest request, ActionListener<SuggestResponse> listener) {
        client.suggest(request, listener);
    }

    @Override
    public SuggestRequestBuilder prepareSuggest(String... indices) {
        return client.prepareSuggest(indices);
    }

    @Override
    public ActionFuture<SearchResponse> search(SearchRequest request) {
        return client.search(request);
    }

    @Override
    public void search(SearchRequest request, ActionListener<SearchResponse> listener) {
        client.search(request, listener);
    }

    @Override
    public SearchRequestBuilder prepareSearch(String... indices) {
        return client.prepareSearch(indices);
    }

    @Override
    public ActionFuture<SearchResponse> searchScroll(SearchScrollRequest request) {
        return client.searchScroll(request);
    }

    @Override
    public void searchScroll(SearchScrollRequest request, ActionListener<SearchResponse> listener) {
        client.searchScroll(request, listener);
    }

    @Override
    public SearchScrollRequestBuilder prepareSearchScroll(String scrollId) {
        return client.prepareSearchScroll(scrollId);
    }

    @Override
    public ActionFuture<MultiSearchResponse> multiSearch(MultiSearchRequest request) {
        return client.multiSearch(request);
    }

    @Override
    public void multiSearch(MultiSearchRequest request, ActionListener<MultiSearchResponse> listener) {
        client.multiSearch(request, listener);
    }

    @Override
    public MultiSearchRequestBuilder prepareMultiSearch() {
        return client.prepareMultiSearch();
    }

    @Override
    public ActionFuture<SearchResponse> moreLikeThis(MoreLikeThisRequest request) {
        return client.moreLikeThis(request);
    }

    @Override
    public void moreLikeThis(MoreLikeThisRequest request, ActionListener<SearchResponse> listener) {
        client.moreLikeThis(request, listener);
    }

    @Override
    public MoreLikeThisRequestBuilder prepareMoreLikeThis(String index, String type, String id) {
        return client.prepareMoreLikeThis(index, type, id);
    }

    @Override
    public ActionFuture<TermVectorResponse> termVector(TermVectorRequest request) {
        return client.termVector(request);
    }

    @Override
    public void termVector(TermVectorRequest request, ActionListener<TermVectorResponse> listener) {
        client.termVector(request, listener);
    }

    @Override
    public TermVectorRequestBuilder prepareTermVector() {
        return client.prepareTermVector();
    }

    @Override
    public TermVectorRequestBuilder prepareTermVector(String index, String type, String id) {
        return client.prepareTermVector(index, type, id);
    }

    @Override
    public ActionFuture<MultiTermVectorsResponse> multiTermVectors(MultiTermVectorsRequest request) {
        return client.multiTermVectors(request);
    }

    @Override
    public void multiTermVectors(MultiTermVectorsRequest request, ActionListener<MultiTermVectorsResponse> listener) {
        client.multiTermVectors(request, listener);
    }

    @Override
    public MultiTermVectorsRequestBuilder prepareMultiTermVectors() {
        return client.prepareMultiTermVectors();
    }

    @Override
    public ActionFuture<PercolateResponse> percolate(PercolateRequest request) {
        return client.percolate(request);
    }

    @Override
    public void percolate(PercolateRequest request, ActionListener<PercolateResponse> listener) {
        client.percolate(request, listener);
    }

    @Override
    public PercolateRequestBuilder preparePercolate() {
        return client.preparePercolate();
    }

    @Override
    public ActionFuture<MultiPercolateResponse> multiPercolate(MultiPercolateRequest request) {
        return client.multiPercolate(request);
    }

    @Override
    public void multiPercolate(MultiPercolateRequest request, ActionListener<MultiPercolateResponse> listener) {
        client.multiPercolate(request, listener);
    }

    @Override
    public MultiPercolateRequestBuilder prepareMultiPercolate() {
        return client.prepareMultiPercolate();
    }

    @Override
    public ExplainRequestBuilder prepareExplain(String index, String type, String id) {
        return client.prepareExplain(index, type, id);
    }

    @Override
    public ActionFuture<ExplainResponse> explain(ExplainRequest request) {
        return client.explain(request);
    }

    @Override
    public void explain(ExplainRequest request, ActionListener<ExplainResponse> listener) {
        client.explain(request, listener);
    }

    @Override
    public ClearScrollRequestBuilder prepareClearScroll() {
        return client.prepareClearScroll();
    }

    @Override
    public ActionFuture<ClearScrollResponse> clearScroll(ClearScrollRequest request) {
        return client.clearScroll(request);
    }

    @Override
    public void clearScroll(ClearScrollRequest request, ActionListener<ClearScrollResponse> listener) {
        client.clearScroll(request, listener);
    }

    @Override
    public FieldStatsRequestBuilder prepareFieldStats() {
        return client.prepareFieldStats();
    }

    @Override
    public ActionFuture<FieldStatsResponse> fieldStats(FieldStatsRequest request) {
        return client.fieldStats(request);
    }

    @Override
    public void fieldStats(FieldStatsRequest request, ActionListener<FieldStatsResponse> listener) {
        client.fieldStats(request, listener);
    }

    @Override
    public Settings settings() {
        return client.settings();
    }

}
