package org.codelibs.fess.client;

import static org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.newConfigs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
import org.codelibs.fess.util.QueryResponseList;
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

    public void setRunner(ElasticsearchClusterRunner runner) {
        this.runner = runner;
    }

    public void addTransportAddress(String host, int port) {
        transportAddressList.add(new InetSocketTransportAddress(host, port));
    }

    @InitMethod
    public void open() {
        if (transportAddressList.isEmpty()) {
            if (runner == null) {
                runner = new ElasticsearchClusterRunner();
                Configs config = newConfigs().clusterName("fess-" + UUID.randomUUID().toString()).numOfNode(1);
                String esDir = System.getProperty("fess.es.dir");
                if (esDir != null) {
                    config.basePath(esDir);
                }
                runner.build(config);
            }
            client = runner.client();
        } else {
            TransportClient transportClient = new TransportClient();
            for (TransportAddress address : transportAddressList) {
                transportClient.addTransportAddress(address);
            }
            client = transportClient;
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
        SearchRequestBuilder queryRequestBuilder = client.prepareSearch(index);
        if (condition.build(queryRequestBuilder)) {

            if (ComponentUtil.getQueryHelper().getTimeAllowed() >= 0) {
                queryRequestBuilder.setTimeout(TimeValue.timeValueMillis(ComponentUtil.getQueryHelper().getTimeAllowed()));
            }

            final Set<Entry<String, String[]>> paramSet = ComponentUtil.getQueryHelper().getRequestParameterSet();
            if (!paramSet.isEmpty()) {
                for (final Map.Entry<String, String[]> entry : paramSet) {
                    queryRequestBuilder.putHeader(entry.getKey(), entry.getValue());
                }
            }

            searchResponse = queryRequestBuilder.execute().actionGet();
        }
        final long execTime = System.currentTimeMillis() - startTime;

        return searchResult.build(queryRequestBuilder, execTime, Optional.ofNullable(searchResponse));
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
        return search(condition, (queryRequestBuilder, execTime, searchResponse) -> {
            List<Map<String, Object>> list = new ArrayList<>();
            searchResponse.ifPresent(response -> {
                response.getHits().forEach(hit -> {
                    list.add(hit.getSource());
                });
            });
            return Optional.of(list);
        });
    }

    // TODO 
    public List<Map<String, Object>> getDocumentList(final String query, final int start, final int rows, final FacetInfo facetInfo,
            final GeoInfo geoInfo, final String[] responseFields) {
        return getDocumentList(query, start, rows, facetInfo, geoInfo, responseFields, true);
    }

    // TODO 
    public List<Map<String, Object>> getDocumentList(final String query, final int start, final int rows, final FacetInfo facetInfo,
            final GeoInfo geoInfo, final String[] responseFields, final boolean forUser) {
        if (start > ComponentUtil.getQueryHelper().getMaxSearchResultOffset()) {
            throw new ResultOffsetExceededException("The number of result size is exceeded.");
        }

        final long startTime = System.currentTimeMillis();

        SearchResponse searchResponse = null;
        SearchRequestBuilder queryRequestBuilder = client.prepareSearch(index);
        final SearchQuery searchQuery = ComponentUtil.getQueryHelper().build(query, forUser);
        final String q = searchQuery.getQuery();
        if (StringUtil.isNotBlank(q)) {

            BoolFilterBuilder boolFilterBuilder = null;

            // fields
            queryRequestBuilder.addFields(responseFields);
            // query
            QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(q);
            queryRequestBuilder.setFrom(start).setSize(rows);
            for (final Map.Entry<String, String[]> entry : ComponentUtil.getQueryHelper().getQueryParamMap().entrySet()) {
                queryRequestBuilder.putHeader(entry.getKey(), entry.getValue());
            }
            // filter query
            if (searchQuery.hasFilterQueries()) {
                if (boolFilterBuilder == null) {
                    boolFilterBuilder = FilterBuilders.boolFilter();
                }
                for (String filterQuery : searchQuery.getFilterQueries()) {
                    boolFilterBuilder.must(FilterBuilders.queryFilter(QueryBuilders.queryStringQuery(filterQuery)));
                }
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
                    queryRequestBuilder.addSort(fieldSort);
                }
            } else if (ComponentUtil.getQueryHelper().hasDefaultSortFields()) {
                for (final SortField sortField : ComponentUtil.getQueryHelper().getDefaultSortFields()) {
                    FieldSortBuilder fieldSort = SortBuilders.fieldSort(sortField.getField());
                    if (Constants.DESC.equals(sortField.getOrder())) {
                        fieldSort.order(SortOrder.DESC);
                    } else {
                        fieldSort.order(SortOrder.ASC);
                    }
                    queryRequestBuilder.addSort(fieldSort);
                }
            }
            // highlighting
            if (ComponentUtil.getQueryHelper().getHighlightingFields() != null
                    && ComponentUtil.getQueryHelper().getHighlightingFields().length != 0) {
                for (final String hf : ComponentUtil.getQueryHelper().getHighlightingFields()) {
                    queryRequestBuilder.addHighlightedField(hf, ComponentUtil.getQueryHelper().getHighlightSnippetSize());
                }
            }
            // geo
            if (geoInfo != null && geoInfo.isAvailable()) {
                if (boolFilterBuilder == null) {
                    boolFilterBuilder = FilterBuilders.boolFilter();
                }
                boolFilterBuilder.must(geoInfo.toFilterBuilder());
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
                            queryRequestBuilder.addAggregation(termsBuilder);
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
                            queryRequestBuilder.addAggregation(filterBuilder);
                        } else {
                            throw new FessSolrQueryException("Invalid facet query: " + facetQuery);
                        }
                    }
                }
            }

            if (ComponentUtil.getQueryHelper().getTimeAllowed() >= 0) {
                queryRequestBuilder.setTimeout(TimeValue.timeValueMillis(ComponentUtil.getQueryHelper().getTimeAllowed()));
            }
            final Set<Entry<String, String[]>> paramSet = ComponentUtil.getQueryHelper().getRequestParameterSet();
            if (!paramSet.isEmpty()) {
                for (final Map.Entry<String, String[]> entry : paramSet) {
                    queryRequestBuilder.putHeader(entry.getKey(), entry.getValue());
                }
            }

            if (boolFilterBuilder != null) {
                queryBuilder = QueryBuilders.filteredQuery(queryBuilder, boolFilterBuilder);
            }

            searchResponse = queryRequestBuilder.setQuery(queryBuilder).execute().actionGet();
        }
        final long execTime = System.currentTimeMillis() - startTime;

        final QueryResponseList queryResponseList = ComponentUtil.getQueryResponseList();
        queryResponseList.init(searchResponse, start, rows);
        queryResponseList.setSearchQuery(q);
        if (queryRequestBuilder != null) {
            queryResponseList.setSolrQuery(queryRequestBuilder.toString());
        }
        queryResponseList.setExecTime(execTime);
        return queryResponseList;
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

    public interface SearchCondition {
        boolean build(SearchRequestBuilder queryRequestBuilder);
    }

    public interface SearchResult<T> {
        T build(SearchRequestBuilder queryRequestBuilder, long execTime, Optional<SearchResponse> searchResponse);
    }
}
