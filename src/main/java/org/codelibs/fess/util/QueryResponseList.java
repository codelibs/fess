/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import org.apache.commons.lang3.StringUtils;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryResponseList implements List<Map<String, Object>> {

    private static final Logger logger = LoggerFactory.getLogger(QueryResponseList.class);

    private final List<Map<String, Object>> parent;

    /** The value of current page number. */
    protected int pageSize;

    /** The value of current page number. */
    protected int currentPageNumber;

    protected long allRecordCount;

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
            final SearchHits searchHits = searchResponse.getHits();
            allRecordCount = searchHits.getTotalHits();
            queryTime = searchResponse.getTookInMillis();

            if (searchResponse.getTotalShards() != searchResponse.getSuccessfulShards()) {
                partialResults = true;
            }

            // build highlighting fields
                final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
                final String hlPrefix = queryHelper.getHighlightPrefix();
                for (final SearchHit searchHit : searchHits.getHits()) {
                    final Map<String, Object> docMap = new HashMap<>();
                    if (searchHit.getSource() == null) {
                        searchHit.getFields().forEach((key, value) -> {
                            docMap.put(key, value.getValue());
                        });
                    } else {
                        docMap.putAll(searchHit.getSource());
                    }

                    final Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                    try {
                        if (highlightFields != null) {
                            for (final Map.Entry<String, HighlightField> entry : highlightFields.entrySet()) {
                                final HighlightField highlightField = entry.getValue();
                                final Text[] fragments = highlightField.fragments();
                                if (fragments != null && fragments.length != 0) {
                                    final String[] texts = new String[fragments.length];
                                    for (int i = 0; i < fragments.length; i++) {
                                        texts[i] = fragments[i].string();
                                    }
                                    final String value = StringUtils.join(texts, "...");
                                    docMap.put(hlPrefix + highlightField.getName(), value);
                                }
                            }
                        }
                    } catch (final Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Could not create a highlighting value: " + docMap, e);
                        }
                    }

                    // ContentTitle
                    final ViewHelper viewHelper = ComponentUtil.getViewHelper();
                    if (viewHelper != null) {
                        final FessConfig fessConfig = ComponentUtil.getFessConfig();
                        docMap.put(fessConfig.getResponseFieldContentTitle(), viewHelper.getContentTitle(docMap));
                        docMap.put(fessConfig.getResponseFieldContentDescription(), viewHelper.getContentDescription(docMap));
                        docMap.put(fessConfig.getResponseFieldUrlLink(), viewHelper.getUrlLink(docMap));
                        docMap.put(fessConfig.getResponseFieldSitePath(), viewHelper.getSitePath(docMap));
                    }

                    parent.add(docMap);
                }

                // facet
                final Aggregations aggregations = searchResponse.getAggregations();
                if (aggregations != null) {
                    facetResponse = new FacetResponse(aggregations);
                }

            });

        calculatePageInfo(start, pageSize);
    }

    protected void calculatePageInfo(final int start, final int size) {
        pageSize = size;
        allPageCount = (int) ((allRecordCount - 1) / pageSize) + 1;
        existPrevPage = start > 0;
        existNextPage = start < (long) (allPageCount - 1) * (long) pageSize;
        currentPageNumber = start / pageSize + 1;
        currentStartRecordNumber = allRecordCount != 0 ? (currentPageNumber - 1) * pageSize + 1 : 0;
        currentEndRecordNumber = (long) currentPageNumber * pageSize;
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
