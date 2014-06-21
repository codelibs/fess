/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import jp.sf.fess.helper.QueryHelper;
import jp.sf.fess.helper.ViewHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.codelibs.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryResponseList implements List<Map<String, Object>> {
    private static final String PARTIAL_RESULTS = "partialResults";

    private static final String MORE_LIKE_THIS = "moreLikeThis";

    private static final String DOC_VALUES = "docValues";

    private static final String ID_FIELD = "id";

    private static final Logger logger = LoggerFactory
            .getLogger(QueryResponseList.class);

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

    protected String solrQuery;

    protected long execTime;

    protected FacetResponse facetResponse;

    protected MoreLikeThisResponse moreLikeThisResponse;

    protected boolean partialResults = false;

    protected int queryTime;

    protected long searchTime;

    public QueryResponseList() {
        parent = new ArrayList<Map<String, Object>>();
    }

    // for testing
    protected QueryResponseList(final List<Map<String, Object>> parent) {
        this.parent = parent;
    }

    public void init(final QueryResponse queryResponse, final int pageSize) {
        long start = 0;
        long numFound = 0;
        if (queryResponse != null) {
            final SolrDocumentList sdList = queryResponse.getResults();
            start = sdList.getStart();
            numFound = sdList.getNumFound();
            queryTime = queryResponse.getQTime();
            searchTime = queryResponse.getElapsedTime();

            final Object partialResultsValue = queryResponse
                    .getResponseHeader().get(PARTIAL_RESULTS);
            if (partialResultsValue != null
                    && ((Boolean) partialResultsValue).booleanValue()) {
                partialResults = true;
            }

            // build highlighting fields
            final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
            final String hlPrefix = queryHelper.getHighlightingPrefix();
            for (final SolrDocument solrDocMap : sdList) {
                final Map<String, Object> docMap = new HashMap<String, Object>();
                docMap.putAll(solrDocMap);

                try {
                    final Object idValue = docMap.get(ID_FIELD);
                    if (queryResponse.getHighlighting().get(idValue) != null) {
                        for (final String hf : queryHelper
                                .getHighlightingFields()) {
                            final List<String> highlightSnippets = queryResponse
                                    .getHighlighting().get(idValue).get(hf);
                            String value = null;
                            if (highlightSnippets != null
                                    && !highlightSnippets.isEmpty()) {
                                value = StringUtils.join(highlightSnippets,
                                        "...");
                                docMap.put(hlPrefix + hf, value);
                            }
                        }
                    }
                } catch (final Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Could not create a highlighting value: "
                                + docMap, e);
                    }
                }

                // ContentTitle
                final ViewHelper viewHelper = ComponentUtil.getViewHelper();
                if (viewHelper != null) {
                    docMap.put("contentTitle",
                            viewHelper.getContentTitle(docMap));
                    docMap.put("contentDescription",
                            viewHelper.getContentDescription(docMap));
                    docMap.put("urlLink", viewHelper.getUrlLink(docMap));
                }

                parent.add(docMap);
            }

            // facet
            final List<FacetField> facetFields = queryResponse.getFacetFields();
            final Map<String, Integer> facetQueryMap = queryResponse
                    .getFacetQuery();
            if (facetFields != null || facetQueryMap != null) {
                facetResponse = new FacetResponse(facetFields, facetQueryMap);
            }

            // mlt
            final Object moreLikeThisMap = queryResponse.getResponse().get(
                    MORE_LIKE_THIS);
            if (moreLikeThisMap instanceof SimpleOrderedMap) {
                moreLikeThisResponse = new MoreLikeThisResponse();
                final int size = ((SimpleOrderedMap<?>) moreLikeThisMap).size();
                for (int i = 0; i < size; i++) {
                    final String id = ((SimpleOrderedMap<?>) moreLikeThisMap)
                            .getName(i);
                    final Object docList = ((SimpleOrderedMap<?>) moreLikeThisMap)
                            .getVal(i);
                    if (StringUtil.isNotBlank(id)
                            && docList instanceof SolrDocumentList) {
                        final List<Map<String, Object>> docMapList = new ArrayList<Map<String, Object>>(
                                ((SolrDocumentList) docList).size());
                        for (final SolrDocument solrDoc : (SolrDocumentList) docList) {
                            final Map<String, Object> docMap = new HashMap<String, Object>();
                            docMap.putAll(solrDoc);
                            docMapList.add(docMap);
                        }
                        moreLikeThisResponse.put(id, docMapList);
                    }
                }
            }

            // docValues
            final Object docValuesObj = queryResponse.getResponse().get(
                    DOC_VALUES);
            if (docValuesObj instanceof SimpleOrderedMap) {
                @SuppressWarnings("unchecked")
                final SimpleOrderedMap<List<Long>> docValuesMap = (SimpleOrderedMap<List<Long>>) docValuesObj;
                for (int i = 0; i < docValuesMap.size(); i++) {
                    final String name = docValuesMap.getName(i);
                    final List<Long> valueList = docValuesMap.getVal(i);
                    for (int j = 0; j < valueList.size() && j < parent.size(); j++) {
                        parent.get(j).put(name, valueList.get(j));
                    }
                }
            }
        }
        calculatePageInfo(start, pageSize, numFound);
    }

    protected void calculatePageInfo(final long start, final int size,
            final long numFound) {
        pageSize = size;
        allRecordCount = numFound;
        allPageCount = (int) ((allRecordCount - 1) / pageSize) + 1;
        existPrevPage = start > 0;
        existNextPage = start < (long) (allPageCount - 1) * (long) pageSize;
        currentPageNumber = (int) (start / pageSize) + 1;
        currentStartRecordNumber = numFound != 0 ? (currentPageNumber - 1)
                * pageSize + 1 : 0;
        currentEndRecordNumber = currentPageNumber * pageSize;
        currentEndRecordNumber = allRecordCount < currentEndRecordNumber ? allRecordCount
                : currentEndRecordNumber;

        final int pageRangeSize = 5;
        int startPageRangeSize = currentPageNumber - pageRangeSize;
        if (startPageRangeSize < 1) {
            startPageRangeSize = 1;
        }
        int endPageRangeSize = currentPageNumber + pageRangeSize;
        if (endPageRangeSize > allPageCount) {
            endPageRangeSize = allPageCount;
        }
        pageNumberList = new ArrayList<String>();
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
    public boolean addAll(final int index,
            final Collection<? extends Map<String, Object>> c) {
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
    public Map<String, Object> set(final int index,
            final Map<String, Object> element) {
        return parent.set(index, element);
    }

    @Override
    public int size() {
        return parent.size();
    }

    @Override
    public List<Map<String, Object>> subList(final int fromIndex,
            final int toIndex) {
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

    public String getSolrQuery() {
        return solrQuery;
    }

    public void setSolrQuery(final String solrQuery) {
        this.solrQuery = solrQuery;
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

    public MoreLikeThisResponse getMoreLikeThisResponse() {
        return moreLikeThisResponse;
    }

    public boolean isPartialResults() {
        return partialResults;
    }

    public int getQueryTime() {
        return queryTime;
    }

    public long getSearchTime() {
        return searchTime;
    }

}
