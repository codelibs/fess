package org.codelibs.fess.client;

import static org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.newConfigs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.codec.Charsets;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.Configs;
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
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ShardOperationFailedException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.BaseEncoding;

public class SearchClient {
    private static final Logger logger = LoggerFactory.getLogger(SearchClient.class);

    protected ElasticsearchClusterRunner runner;

    protected List<TransportAddress> transportAddressList = new ArrayList<>();

    protected Client client;

    protected String index = "fess";

    protected String type = "doc";

    protected String clusterName = "elasticsearch";

    protected Map<String, String> settings;

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @InitMethod
    public void open() {
        String transportAddressesValue = System.getProperty(Constants.FESS_ES_TRANSPORT_ADDRESSES);
        if (StringUtil.isNotBlank(transportAddressesValue)) {
            for (String transportAddressValue : transportAddressesValue.split(",")) {
                String[] addressPair = transportAddressValue.trim().split(":");
                if (addressPair.length < 3) {
                    String host = addressPair[0];
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
                Configs config = newConfigs().clusterName(clusterName).numOfNode(1);
                String esDir = System.getProperty("fess.es.dir");
                if (esDir != null) {
                    config.basePath(esDir);
                }
                runner.onBuild(new ElasticsearchClusterRunner.Builder() {
                    @Override
                    public void build(final int number, final Builder settingsBuilder) {
                        if (settings != null) {
                            settingsBuilder.put(settings);
                        }
                    }
                });
                runner.build(config);
            }
            client = runner.client();
            addTransportAddress("localhost", runner.node().settings().getAsInt("transport.tcp.port", 9300));
        } else {
            Builder settingsBuilder = ImmutableSettings.settingsBuilder();
            settingsBuilder.put("cluster.name", clusterName);
            Settings settings = settingsBuilder.build();
            TransportClient transportClient = new TransportClient(settings);
            for (TransportAddress address : transportAddressList) {
                transportClient.addTransportAddress(address);
            }
            client = transportClient;
        }

        if (StringUtil.isBlank(transportAddressesValue)) {
            StringBuilder buf = new StringBuilder();
            for (TransportAddress transportAddress : transportAddressList) {
                if (transportAddress instanceof InetSocketTransportAddress) {
                    if (buf.length() > 0) {
                        buf.append(',');
                    }
                    InetSocketTransportAddress inetTransportAddress = (InetSocketTransportAddress) transportAddress;
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

        if (!isIndexExists()) {
            createIndex();
            waitForYellowStatus();
        }
    }

    private void createIndex() {
        String source = FileUtil.readText("json/index.json");
        CreateIndexResponse response = client.admin().indices().prepareCreate(index).setSource(source).execute().actionGet();
        if (!response.isAcknowledged()) {
            logger.warn("Failed to create {0}.", index);
        }
    }

    private boolean isIndexExists() {
        IndicesExistsResponse response = client.admin().indices().prepareExists(index).execute().actionGet();
        return response.isExists();
    }

    private void waitForYellowStatus() {
        ClusterHealthResponse response = client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
        if (logger.isDebugEnabled()) {
            logger.debug("Elasticsearch Cluster Status: {0}", response.getStatus());
        }
    }

    @DestroyMethod
    public void close() {
        try {
            client.close();
        } catch (ElasticsearchException e) {
            logger.warn("Failed to close Client: " + client, e);
        }
    }

    public void deleteByQuery(QueryBuilder queryBuilder) {
        try {
            // TODO replace with deleting bulk ids with scroll/scan
            client.prepareDeleteByQuery(index).setQuery(queryBuilder).execute().actionGet().forEach(res -> {
                ShardOperationFailedException[] failures = res.getFailures();
                if (failures.length > 0) {
                    StringBuilder buf = new StringBuilder(200);
                    buf.append("Failed to delete documents in some shards.");
                    for (ShardOperationFailedException failure : failures) {
                        buf.append('\n').append(failure.toString());
                    }
                    throw new SearchException(buf.toString());
                }
            });
        } catch (ElasticsearchException e) {
            throw new SearchException("Failed to delete documents.", e);
        }
    }

    public <T> T search(SearchCondition condition, SearchResult<T> searchResult) {
        final long startTime = System.currentTimeMillis();

        SearchResponse searchResponse = null;
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
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

    public Optional<Map<String, Object>> getDocument(final SearchCondition condition) {
        return search(condition, (queryBuilder, execTime, searchResponse) -> {
            return searchResponse.map(response -> {
                SearchHit[] hits = response.getHits().hits();
                if (hits.length > 0) {
                    return hits[0].getSource();
                }
                return null;
            });
        });
    }

    public Optional<List<Map<String, Object>>> getDocumentList(final SearchCondition condition) {
        return search(condition, (searchRequestBuilder, execTime, searchResponse) -> {
            List<Map<String, Object>> list = new ArrayList<>();
            searchResponse.ifPresent(response -> {
                response.getHits().forEach(hit -> {
                    list.add(hit.getSource());
                });
            });
            return Optional.of(list);
        });
    }

    public boolean update(String id, String field, Object value) {
        try {
            return client.prepareUpdate(index, type, id).setDoc(field, value).execute().actionGet().isCreated();
        } catch (ElasticsearchException e) {
            throw new SearchException("Failed to set " + value + " to " + field + " for doc " + id, e);
        }
    }

    public void refresh() {
        client.admin().indices().prepareRefresh(index).execute(new ActionListener<RefreshResponse>() {
            @Override
            public void onResponse(RefreshResponse response) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Refreshed " + index + "/" + type + ".");
                }
            }

            @Override
            public void onFailure(Throwable e) {
                logger.error("Failed to refresh " + index + "/" + type + ".", e);
            }
        });

    }

    public void flush() {
        client.admin().indices().prepareFlush(index).execute(new ActionListener<FlushResponse>() {

            @Override
            public void onResponse(FlushResponse response) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Flushed " + index + "/" + type + ".");
                }
            }

            @Override
            public void onFailure(Throwable e) {
                logger.error("Failed to flush " + index + "/" + type + ".", e);
            }
        });

    }

    public void optimize() {
        client.admin().indices().prepareOptimize(index).execute(new ActionListener<OptimizeResponse>() {

            @Override
            public void onResponse(OptimizeResponse response) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Optimzed " + index + "/" + type + ".");
                }
            }

            @Override
            public void onFailure(Throwable e) {
                logger.error("Failed to optimze " + index + "/" + type + ".", e);
            }
        });
    }

    public PingResponse ping() {
        try {
            ClusterHealthResponse response = client.admin().cluster().prepareHealth().execute().actionGet();
            return new PingResponse(response);
        } catch (ElasticsearchException e) {
            throw new SearchException("Failed to process a ping request.", e);
        }
    }

    public void addAll(List<Map<String, Object>> docList) {
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (Map<String, Object> doc : docList) {
            bulkRequestBuilder.add(client.prepareIndex(index, type).setSource(doc));
        }
        BulkResponse response = bulkRequestBuilder.execute().actionGet();
        String failureMessage = response.buildFailureMessage();
        if (StringUtil.isNotBlank(failureMessage)) {
            throw new SearchException(failureMessage);
        }
    }

    public static class SearchConditionBuilder {
        private SearchRequestBuilder searchRequestBuilder;
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
                    FieldSortBuilder fieldSort = SortBuilders.fieldSort(sortField.getField());
                    if (Constants.DESC.equals(sortField.getOrder())) {
                        fieldSort.order(SortOrder.DESC);
                    } else {
                        fieldSort.order(SortOrder.ASC);
                    }
                    searchRequestBuilder.addSort(fieldSort);
                }
            } else if (ComponentUtil.getQueryHelper().hasDefaultSortFields()) {
                for (final SortField sortField : ComponentUtil.getQueryHelper().getDefaultSortFields()) {
                    FieldSortBuilder fieldSort = SortBuilders.fieldSort(sortField.getField());
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
                            String encodedField = BaseEncoding.base64().encode(f.getBytes(Charsets.UTF_8));
                            TermsBuilder termsBuilder = AggregationBuilders.terms(Constants.FACET_FIELD_PREFIX + encodedField).field(f);
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
                            FilterAggregationBuilder filterBuilder =
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
                for (String filterQuery : searchQuery.getFilterQueries()) {
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

    public interface SearchCondition {
        boolean build(SearchRequestBuilder searchRequestBuilder);
    }

    public interface SearchResult<T> {
        T build(SearchRequestBuilder searchRequestBuilder, long execTime, Optional<SearchResponse> searchResponse);
    }
}
