/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package jp.sf.fess.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.ResultOffsetExceededException;
import jp.sf.fess.entity.FacetInfo;
import jp.sf.fess.entity.FieldAnalysisResponse;
import jp.sf.fess.entity.GeoInfo;
import jp.sf.fess.entity.MoreLikeThisInfo;
import jp.sf.fess.entity.PingResponse;
import jp.sf.fess.entity.SearchQuery;
import jp.sf.fess.entity.SearchQuery.SortField;
import jp.sf.fess.helper.FieldHelper;
import jp.sf.fess.helper.QueryHelper;
import jp.sf.fess.helper.RoleQueryHelper;
import jp.sf.fess.solr.FessSolrQueryException;
import jp.sf.fess.util.ComponentUtil;
import jp.sf.fess.util.QueryResponseList;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.request.FieldAnalysisRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;
import org.codelibs.core.util.StringUtil;
import org.codelibs.solr.lib.SolrGroup;
import org.codelibs.solr.lib.SolrGroupManager;
import org.codelibs.solr.lib.policy.QueryType;

public class SearchService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected SolrGroupManager solrGroupManager;

    @Resource
    protected QueryHelper queryHelper;

    @Resource
    protected RoleQueryHelper roleQueryHelper;

    public PingResponse ping() {
        final SolrGroup solrGroup = solrGroupManager
                .getSolrGroup(QueryType.QUERY);
        return new PingResponse(solrGroup.ping());
    }

    public Map<String, Object> getDocument(final String query) {
        return getDocument(query, queryHelper.getResponseFields(), null);
    }

    public Map<String, Object> getDocument(final String query,
            final String[] responseFields, final String[] docValuesFields) {
        final List<Map<String, Object>> docList = getDocumentList(query, 0, 1,
                null, null, null, responseFields, docValuesFields);
        if (!docList.isEmpty()) {
            return docList.get(0);
        }
        return null;
    }

    public List<Map<String, Object>> getDocumentListByDocIds(
            final String[] docIds, final String[] responseFields,
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
            buf.append(fieldHelper.docIdField).append(':').append(docIds[i]);
        }
        return getDocumentList(buf.toString(), 0, pageSize, null, null, null,
                responseFields, docValuesFields);
    }

    public List<Map<String, Object>> getDocumentList(final String query,
            final int start, final int rows, final FacetInfo facetInfo,
            final GeoInfo geoInfo, final MoreLikeThisInfo mltInfo,
            final String[] responseFields, final String[] docValuesFields) {
        return getDocumentList(query, start, rows, facetInfo, geoInfo, mltInfo,
                responseFields, docValuesFields, true);
    }

    public List<Map<String, Object>> getDocumentList(final String query,
            final int start, final int rows, final FacetInfo facetInfo,
            final GeoInfo geoInfo, final MoreLikeThisInfo mltInfo,
            final String[] responseFields, final String[] docValuesFields,
            final boolean forUser) {
        if (start > queryHelper.getMaxSearchResultOffset()) {
            throw new ResultOffsetExceededException(
                    "The number of result size is exceeded.");
        }

        final long startTime = System.currentTimeMillis();

        final SolrGroup solrGroup = solrGroupManager
                .getSolrGroup(QueryType.QUERY);

        QueryResponse queryResponse = null;
        final SolrQuery solrQuery = new SolrQuery();
        final SearchQuery searchQuery = queryHelper.build(query, forUser);
        final String q = searchQuery.getQuery();
        if (StringUtil.isNotBlank(q)) {
            // fields
            solrQuery.setFields(responseFields);
            // query
            solrQuery.setQuery(q);
            solrQuery.setStart(start);
            solrQuery.setRows(rows);
            solrQuery.set("mm", searchQuery.getMinimumShouldMatch());
            solrQuery.set("defType", searchQuery.getDefType());
            for (final Map.Entry<String, String[]> entry : queryHelper
                    .getQueryParamMap().entrySet()) {
                solrQuery.set(entry.getKey(), entry.getValue());
            }
            // filter query
            if (searchQuery.hasFilterQueries()) {
                solrQuery.addFilterQuery(searchQuery.getFilterQueries());
            }
            // sort
            final SortField[] sortFields = searchQuery.getSortFields();
            if (sortFields.length != 0) {
                for (final SortField sortField : sortFields) {
                    solrQuery
                            .addSort(
                                    sortField.getField(),
                                    Constants.DESC.equals(sortField.getOrder()) ? SolrQuery.ORDER.desc
                                            : SolrQuery.ORDER.asc);
                }
            } else if (queryHelper.hasDefaultSortFields()) {
                for (final SortField sortField : queryHelper
                        .getDefaultSortFields()) {
                    solrQuery
                            .addSort(
                                    sortField.getField(),
                                    Constants.DESC.equals(sortField.getOrder()) ? SolrQuery.ORDER.desc
                                            : SolrQuery.ORDER.asc);
                }
            }
            // highlighting
            if (queryHelper.getHighlightingFields() != null
                    && queryHelper.getHighlightingFields().length != 0) {
                for (final String hf : queryHelper.getHighlightingFields()) {
                    solrQuery.addHighlightField(hf);
                }
                solrQuery.setHighlightSnippets(queryHelper
                        .getHighlightSnippetSize());
            }
            // shards
            if (queryHelper.getShards() != null) {
                solrQuery.setParam("shards", queryHelper.getShards());
            }
            // geo
            if (geoInfo != null && geoInfo.isAvailable()) {
                solrQuery.addFilterQuery(geoInfo.toGeoQueryString());
                final String additionalGeoQuery = queryHelper
                        .getAdditionalGeoQuery();
                if (StringUtil.isNotBlank(additionalGeoQuery)) {
                    solrQuery.addFilterQuery(additionalGeoQuery);
                }
            }
            // facets
            if (facetInfo != null) {
                solrQuery.setFacet(true);
                if (facetInfo.field != null) {
                    for (final String f : facetInfo.field) {
                        if (queryHelper.isFacetField(f)) {
                            solrQuery.addFacetField(f);
                        } else {
                            throw new FessSolrQueryException("EFESS0002",
                                    new Object[] { f });
                        }
                    }
                }
                if (facetInfo.query != null) {
                    for (final String fq : facetInfo.query) {
                        final String facetQuery = queryHelper
                                .buildFacetQuery(fq);
                        if (StringUtil.isNotBlank(facetQuery)) {
                            solrQuery.addFacetQuery(facetQuery);
                        } else {
                            throw new FessSolrQueryException("EFESS0003",
                                    new Object[] { fq, facetQuery });
                        }
                    }
                }
                if (facetInfo.limit != null) {
                    solrQuery.setFacetLimit(Integer.parseInt(facetInfo.limit));
                }
                if (facetInfo.minCount != null) {
                    solrQuery.setFacetMinCount(Integer
                            .parseInt(facetInfo.minCount));
                }
                if (facetInfo.missing != null) {
                    solrQuery.setFacetMissing(Boolean
                            .parseBoolean(facetInfo.missing));
                }
                if (facetInfo.prefix != null) {
                    solrQuery.setFacetPrefix(facetInfo.prefix);
                }
                if (facetInfo.sort != null
                        && queryHelper.isFacetSortValue(facetInfo.sort)) {
                    solrQuery.setFacetSort(facetInfo.sort);
                }
            }
            // mlt
            if (mltInfo != null) {
                final String mltField = queryHelper
                        .getMoreLikeThisField(mltInfo.field);
                if (mltField != null) {
                    solrQuery.set("mlt", true);
                    if (mltInfo.count != null) {
                        solrQuery.set("mlt.count",
                                Integer.parseInt(mltInfo.count));
                    }
                    solrQuery.set("mlt.fl", mltField);
                }
            }

            if (queryHelper.getTimeAllowed() >= 0) {
                solrQuery.setTimeAllowed(queryHelper.getTimeAllowed());
            }
            final Set<Entry<String, String[]>> paramSet = queryHelper
                    .getRequestParameterSet();
            if (!paramSet.isEmpty()) {
                for (final Map.Entry<String, String[]> entry : paramSet) {
                    solrQuery.set(entry.getKey(), entry.getValue());
                }
            }

            if (docValuesFields != null) {
                for (final String docValuesField : docValuesFields) {
                    solrQuery.add(Constants.DCF, docValuesField);
                }
            }

            queryResponse = solrGroup.query(solrQuery, SolrRequest.METHOD.POST);
        }
        final long execTime = System.currentTimeMillis() - startTime;

        final QueryResponseList queryResponseList = ComponentUtil
                .getQueryResponseList();
        queryResponseList.init(queryResponse, rows);
        queryResponseList.setSearchQuery(q);
        queryResponseList.setSolrQuery(solrQuery.toString());
        queryResponseList.setExecTime(execTime);
        return queryResponseList;
    }

    public FieldAnalysisResponse getFieldAnalysisResponse(
            final String[] fieldNames, final String fieldValue) {
        final FieldAnalysisRequest request = new FieldAnalysisRequest();

        for (final String fieldName : fieldNames) {
            if (!queryHelper.isAnalysisFieldName(fieldName)) {
                throw new FessSolrQueryException("EFESS0001",
                        new Object[] { fieldName });
            }
            request.addFieldName(fieldName);
        }
        request.setFieldValue(fieldValue);

        final long startTime = System.currentTimeMillis();

        final SolrGroup solrGroup = solrGroupManager
                .getSolrGroup(QueryType.REQUEST);

        final NamedList<Object> response = solrGroup.request(request);

        final long execTime = System.currentTimeMillis() - startTime;

        final FieldAnalysisResponse fieldAnalysisResponse = new FieldAnalysisResponse(
                response);
        fieldAnalysisResponse.setExecTime(execTime);
        return fieldAnalysisResponse;
    }
}
