package org.codelibs.fess.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.codec.Charsets;
import org.codelibs.core.util.StringUtil;
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.codelibs.fess.Constants;
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.ResultOffsetExceededException;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.PingResponse;
import org.codelibs.fess.entity.SearchQuery;
import org.codelibs.fess.entity.SearchQuery.SortField;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.RoleQueryHelper;
import org.codelibs.fess.solr.FessSolrQueryException;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.BaseEncoding;

// TODO rename to SearchClient
public class SearchClient {
    private static final Logger logger = LoggerFactory.getLogger(SearchClient.class);

    @Resource
    protected SearchClient searchClient;

    @Resource
    protected QueryHelper queryHelper;

    @Resource
    protected RoleQueryHelper roleQueryHelper;
    protected ElasticsearchClusterRunner runner;
    protected List<TransportAddress> transportAddressList = new ArrayList<>();
    protected Client client;
    protected String index;
    protected String type;

    @InitMethod
    public void open() {
        if (transportAddressList.isEmpty()) {
            if (runner == null) {
                throw new FessSystemException("No elasticsearch instance.");
            }
            client = runner.client();
        } else {
            TransportClient transportClient = new TransportClient();
            for (TransportAddress address : transportAddressList) {
                transportClient.addTransportAddress(address);
            }
            client = transportClient;
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

    public void addTransportAddress(String host, int port) {
        transportAddressList.add(new InetSocketTransportAddress(host, port));
    }

    public void deleteByQuery(QueryBuilder queryBuilder) {
        DeleteByQueryResponse response = client.prepareDeleteByQuery(index).setQuery(queryBuilder).execute().actionGet();
        // TODO
    }

    public void deleteByQuery(String field, String value) {
        // TODO Auto-generated method stub

    }

    public Map<String, Object> getDocument(final String query) {
        return getDocument(query, queryHelper.getResponseFields());
    }

    public Map<String, Object> getDocument(final String query, final String[] responseFields) {
        final List<Map<String, Object>> docList = getDocumentList(query, 0, 1, null, null, responseFields);
        if (!docList.isEmpty()) {
            return docList.get(0);
        }
        return null;
    }

    public List<Map<String, Object>> getDocumentListByDocIds(final String[] docIds, final String[] responseFields,
            final String[] docValuesFields, final int pageSize) {
        if (docIds == null || docIds.length == 0) {
            return Collections.emptyList();
        }
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        final StringBuilder buf = new StringBuilder(1000);
        for (int i = 0; i < docIds.length; i++) {
            if (i != 0) {
                buf.append(" OR ");
            }
            buf.append(fieldHelper.docIdField + ":").append(docIds[i]);
        }
        return getDocumentList(buf.toString(), 0, pageSize, null, null, responseFields);
    }

    public List<Map<String, Object>> getDocumentList(final String query, final int start, final int rows, final FacetInfo facetInfo,
            final GeoInfo geoInfo, final String[] responseFields) {
        return getDocumentList(query, start, rows, facetInfo, geoInfo, responseFields, true);
    }

    public List<Map<String, Object>> getDocumentList(final String query, final int start, final int rows, final FacetInfo facetInfo,
            final GeoInfo geoInfo, final String[] responseFields, final boolean forUser) {
        if (start > queryHelper.getMaxSearchResultOffset()) {
            throw new ResultOffsetExceededException("The number of result size is exceeded.");
        }

        final long startTime = System.currentTimeMillis();

        SearchResponse searchResponse = null;
        SearchRequestBuilder queryRequestBuilder = client.prepareSearch(index);
        final SearchQuery searchQuery = queryHelper.build(query, forUser);
        final String q = searchQuery.getQuery();
        if (StringUtil.isNotBlank(q)) {

            BoolFilterBuilder boolFilterBuilder = null;

            // fields
            queryRequestBuilder.addFields(responseFields);
            // query
            QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(q);
            queryRequestBuilder.setFrom(start).setSize(rows);
            for (final Map.Entry<String, String[]> entry : queryHelper.getQueryParamMap().entrySet()) {
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
            } else if (queryHelper.hasDefaultSortFields()) {
                for (final SortField sortField : queryHelper.getDefaultSortFields()) {
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
            if (queryHelper.getHighlightingFields() != null && queryHelper.getHighlightingFields().length != 0) {
                for (final String hf : queryHelper.getHighlightingFields()) {
                    queryRequestBuilder.addHighlightedField(hf, queryHelper.getHighlightSnippetSize());
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
                        if (queryHelper.isFacetField(f)) {
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
                        final String facetQuery = queryHelper.buildFacetQuery(fq);
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

            if (queryHelper.getTimeAllowed() >= 0) {
                queryRequestBuilder.setTimeout(TimeValue.timeValueMillis(queryHelper.getTimeAllowed()));
            }
            final Set<Entry<String, String[]>> paramSet = queryHelper.getRequestParameterSet();
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

    // TODO search
    public SearchResponse query(QueryBuilder queryBuilder, AbstractAggregationBuilder aggregationBuilder, SortBuilder sortBuilder) {
        SearchRequestBuilder query = client.prepareSearch(index).setQuery(queryBuilder);
        if (aggregationBuilder != null) {
            query.addAggregation(aggregationBuilder);
        }
        if (sortBuilder != null) {
            query.addSort(sortBuilder);
        }
        return query.execute().actionGet();
    }

    public void update(String id, String field, Object value) {
        UpdateResponse response = client.prepareUpdate(index, type, id).setDoc(field, value).execute().actionGet();
        // TODO

    }

    public void refresh() {
        client.admin().indices().prepareRefresh(index).execute(new ActionListener<RefreshResponse>() {
            @Override
            public void onResponse(RefreshResponse response) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onFailure(Throwable e) {
                // TODO Auto-generated method stub       
            }
        });

    }

    public void flush() {
        client.admin().indices().prepareFlush(index).execute(new ActionListener<FlushResponse>() {

            @Override
            public void onResponse(FlushResponse response) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailure(Throwable e) {
                // TODO Auto-generated method stub

            }
        });

    }

    public void optimize() {
        client.admin().indices().prepareOptimize(index).execute(new ActionListener<OptimizeResponse>() {

            @Override
            public void onResponse(OptimizeResponse response) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailure(Throwable e) {
                // TODO Auto-generated method stub

            }
        });
    }

    public PingResponse ping() {
        ClusterHealthResponse response = client.admin().cluster().prepareHealth().execute().actionGet();
        return new PingResponse(response);

    }

    public void addAll(List<Map<String, Object>> docList) {
        // TODO Auto-generated method stub

    }

}
