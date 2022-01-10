/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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
package org.codelibs.fess.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.common.document.DocumentField;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.opensearch.search.aggregations.Aggregations;
import org.opensearch.search.fetch.subphase.highlight.HighlightField;

public class QueryResponseList implements List<Map<String, Object>> {

    private static final Logger logger = LogManager.getLogger(QueryResponseList.class);

    protected final List<Map<String, Object>> parent;

    /** The value of current page number. */
    protected int pageSize;

    /** The value of current page number. */
    protected int currentPageNumber;

    protected long allRecordCount;

    protected String allRecordCountRelation;

    protected int allPageCount;

    protected boolean existNextPage;

    protected boolean existPrevPage;

    protected long currentStartRecordNumber;

    protected long currentEndRecordNumber;

    protected List<String> pageNumberList;

    protected String searchQuery;

    protected long execTime;

    protected FacetResponse facetResponse;

    protected boolean partialResults = false;

    protected long queryTime;

    public QueryResponseList() {
        parent = new ArrayList<>();
    }

    // for testing
    protected QueryResponseList(final List<Map<String, Object>> parent) {
        this.parent = parent;
    }

    public void init(final OptionalEntity<SearchResponse> searchResponseOpt, final int start, final int pageSize) {
        searchResponseOpt.ifPresent(searchResponse -> {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final SearchHits searchHits = searchResponse.getHits();
            allRecordCount = searchHits.getTotalHits().value;
            allRecordCountRelation = searchHits.getTotalHits().relation.toString();
            queryTime = searchResponse.getTook().millis();

            if (searchResponse.getTotalShards() != searchResponse.getSuccessfulShards()) {
                partialResults = true;
            }

            // build highlighting fields
            final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
            final String hlPrefix = queryHelper.getHighlightPrefix();
            for (final SearchHit searchHit : searchHits.getHits()) {
                final Map<String, Object> docMap = parseSearchHit(fessConfig, hlPrefix, searchHit);

                if (fessConfig.isResultCollapsed()) {
                    final Map<String, SearchHits> innerHits = searchHit.getInnerHits();
                    if (innerHits != null) {
                        final SearchHits innerSearchHits = innerHits.get(fessConfig.getQueryCollapseInnerHitsName());
                        if (innerSearchHits != null) {
                            final long totalHits = innerSearchHits.getTotalHits().value;
                            if (totalHits > 1) {
                                docMap.put(fessConfig.getQueryCollapseInnerHitsName() + "_count", totalHits);
                                final DocumentField bitsField = searchHit.getFields().get(fessConfig.getIndexFieldContentMinhashBits());
                                if (bitsField != null && !bitsField.getValues().isEmpty()) {
                                    docMap.put(fessConfig.getQueryCollapseInnerHitsName() + "_hash", bitsField.getValues().get(0));
                                }
                                docMap.put(fessConfig.getQueryCollapseInnerHitsName(), StreamUtil.stream(innerSearchHits.getHits())
                                        .get(stream -> stream.map(v -> parseSearchHit(fessConfig, hlPrefix, v)).toArray(n -> new Map[n])));
                            }
                        }
                    }
                }

                parent.add(docMap);
            }

            // facet
            final Aggregations aggregations = searchResponse.getAggregations();
            if (aggregations != null) {
                facetResponse = new FacetResponse(aggregations);
            }

        });

        if (pageSize > 0) {
            calculatePageInfo(start, pageSize);
        }
    }

    protected Map<String, Object> parseSearchHit(final FessConfig fessConfig, final String hlPrefix, final SearchHit searchHit) {
        final Map<String, Object> docMap = new HashMap<>(32);
        if (searchHit.getSourceAsMap() == null) {
            searchHit.getFields().forEach((key, value) -> {
                docMap.put(key, value.getValue());
            });
        } else {
            docMap.putAll(searchHit.getSourceAsMap());
        }

        final ViewHelper viewHelper = ComponentUtil.getViewHelper();

        final Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
        try {
            if (highlightFields != null) {
                highlightFields.values().stream().forEach(highlightField -> {
                    final String text = viewHelper.createHighlightText(highlightField);
                    if (text != null) {
                        docMap.put(hlPrefix + highlightField.getName(), text);
                    }
                });
                if (Constants.TEXT_FRAGMENT_TYPE_HIGHLIGHT.equals(fessConfig.getQueryHighlightTextFragmentType())) {
                    docMap.put(Constants.TEXT_FRAGMENTS,
                            viewHelper.createTextFragmentsByHighlight(highlightFields.values().toArray(n -> new HighlightField[n])));
                }
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not create a highlighting value: {}", docMap, e);
            }
        }

        if (Constants.TEXT_FRAGMENT_TYPE_QUERY.equals(fessConfig.getQueryHighlightTextFragmentType())) {
            docMap.put(Constants.TEXT_FRAGMENTS, viewHelper.createTextFragmentsByQuery());
        }

        // ContentTitle
        if (viewHelper != null) {
            docMap.put(fessConfig.getResponseFieldContentTitle(), viewHelper.getContentTitle(docMap));
            docMap.put(fessConfig.getResponseFieldContentDescription(), viewHelper.getContentDescription(docMap));
            docMap.put(fessConfig.getResponseFieldUrlLink(), viewHelper.getUrlLink(docMap));
            docMap.put(fessConfig.getResponseFieldSitePath(), viewHelper.getSitePath(docMap));
        }

        if (!docMap.containsKey(Constants.SCORE)) {
            docMap.put(Constants.SCORE, searchHit.getScore());
        }

        if (!docMap.containsKey(fessConfig.getIndexFieldId())) {
            docMap.put(fessConfig.getIndexFieldId(), searchHit.getId());
        }
        return docMap;
    }

    protected void calculatePageInfo(final int start, final int size) {
        pageSize = size;
        allPageCount = (int) ((allRecordCount - 1) / pageSize) + 1;
        existPrevPage = start > 0;
        existNextPage = start < (long) (allPageCount - 1) * (long) pageSize;
        currentPageNumber = start / pageSize + 1;
        if (existNextPage && size() < pageSize) {
            // collapsing
            existNextPage = false;
            allPageCount = currentPageNumber;
        }
        currentStartRecordNumber = allRecordCount != 0 ? start + 1 : 0;
        currentEndRecordNumber = currentStartRecordNumber + pageSize - 1;
        currentEndRecordNumber = allRecordCount < currentEndRecordNumber ? allRecordCount : currentEndRecordNumber;

        final int pageRangeSize = 5;
        int startPageRangeSize = currentPageNumber - pageRangeSize;
        if (startPageRangeSize < 1) {
            startPageRangeSize = 1;
        }
        int endPageRangeSize = currentPageNumber + pageRangeSize;
        if (endPageRangeSize > allPageCount) {
            endPageRangeSize = allPageCount;
        }
        pageNumberList = new ArrayList<>();
        for (int i = startPageRangeSize; i <= endPageRangeSize; i++) {
            pageNumberList.add(String.valueOf(i));
        }
    }

    @Override
    public boolean add(final Map<String, Object> e) {
        return parent.add(e);
    }

    @Override
    public void add(final int index, final Map<String, Object> element) {
        parent.add(index, element);
    }

    @Override
    public boolean addAll(final Collection<? extends Map<String, Object>> c) {
        return parent.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends Map<String, Object>> c) {
        return parent.addAll(index, c);
    }

    @Override
    public void clear() {
        parent.clear();
    }

    @Override
    public boolean contains(final Object o) {
        return parent.contains(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return parent.containsAll(c);
    }

    @Override
    public boolean equals(final Object o) {
        return parent.equals(o);
    }

    @Override
    public Map<String, Object> get(final int index) {
        return parent.get(index);
    }

    @Override
    public int hashCode() {
        return parent.hashCode();
    }

    @Override
    public int indexOf(final Object o) {
        return parent.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return parent.isEmpty();
    }

    @Override
    public Iterator<Map<String, Object>> iterator() {
        return parent.iterator();
    }

    @Override
    public int lastIndexOf(final Object o) {
        return parent.lastIndexOf(o);
    }

    @Override
    public ListIterator<Map<String, Object>> listIterator() {
        return parent.listIterator();
    }

    @Override
    public ListIterator<Map<String, Object>> listIterator(final int index) {
        return parent.listIterator(index);
    }

    @Override
    public Map<String, Object> remove(final int index) {
        return parent.remove(index);
    }

    @Override
    public boolean remove(final Object o) {
        return parent.remove(o);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return parent.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return parent.retainAll(c);
    }

    @Override
    public Map<String, Object> set(final int index, final Map<String, Object> element) {
        return parent.set(index, element);
    }

    @Override
    public int size() {
        return parent.size();
    }

    @Override
    public List<Map<String, Object>> subList(final int fromIndex, final int toIndex) {
        return parent.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return parent.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return parent.toArray(a);
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public long getAllRecordCount() {
        return allRecordCount;
    }

    public String getAllRecordCountRelation() {
        return allRecordCountRelation;
    }

    public int getAllPageCount() {
        return allPageCount;
    }

    public boolean isExistNextPage() {
        return existNextPage;
    }

    public boolean isExistPrevPage() {
        return existPrevPage;
    }

    public long getCurrentStartRecordNumber() {
        return currentStartRecordNumber;
    }

    public long getCurrentEndRecordNumber() {
        return currentEndRecordNumber;
    }

    public List<String> getPageNumberList() {
        return pageNumberList;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(final String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public long getExecTime() {
        return execTime;
    }

    public void setExecTime(final long execTime) {
        this.execTime = execTime;
    }

    public FacetResponse getFacetResponse() {
        return facetResponse;
    }

    public boolean isPartialResults() {
        return partialResults;
    }

    public long getQueryTime() {
        return queryTime;
    }

}
