/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * A response list that extends List functionality and includes pagination and search metadata.
 * This class wraps search results with pagination information, facet responses, and query statistics.
 * It implements the List interface to provide standard list operations while adding search-specific
 * functionality such as page navigation, record counts, and query execution times.
 */
public class QueryResponseList implements List<Map<String, Object>> {

    /** The underlying list that contains the actual search result documents. */
    protected final List<Map<String, Object>> parent;

    /** The starting position of the current page in the overall result set. */
    protected final int start;

    /** The offset value used for pagination calculations. */
    protected final int offset;

    /** The number of records per page. */
    protected final int pageSize;

    /** The current page number (1-based). */
    protected int currentPageNumber;

    /** The total number of records in the search result set. */
    protected long allRecordCount;

    /** The relation type for the total record count (e.g., "eq", "gte"). */
    protected String allRecordCountRelation;

    /** The total number of pages based on the page size and total record count. */
    protected int allPageCount;

    /** Flag indicating whether there is a next page available. */
    protected boolean existNextPage;

    /** Flag indicating whether there is a previous page available. */
    protected boolean existPrevPage;

    /** The record number of the first record on the current page (1-based). */
    protected long currentStartRecordNumber;

    /** The record number of the last record on the current page (1-based). */
    protected long currentEndRecordNumber;

    /** A list of page numbers for pagination display (typically a range around the current page). */
    protected List<String> pageNumberList;

    /** The search query string that was used to generate these results. */
    protected String searchQuery;

    /** The total execution time for the search request in milliseconds. */
    protected long execTime;

    /** The facet response containing aggregated search facets and their counts. */
    protected FacetResponse facetResponse;

    /** Flag indicating whether the search results are partial (not complete). */
    protected boolean partialResults = false;

    /** The time taken to execute the search query in milliseconds. */
    protected long queryTime;

    /**
     * Constructor for testing purposes.
     * Creates a QueryResponseList with minimal pagination information.
     *
     * @param documentList the list of documents to wrap
     * @param start the starting position of the current page
     * @param pageSize the number of records per page
     * @param offset the offset value for pagination
     */
    protected QueryResponseList(final List<Map<String, Object>> documentList, final int start, final int pageSize, final int offset) {
        parent = documentList;
        this.offset = offset;
        this.start = start;
        this.pageSize = pageSize;
    }

    /**
     * Main constructor that creates a QueryResponseList with complete search metadata.
     *
     * @param documentList the list of documents returned by the search
     * @param allRecordCount the total number of records in the search result set
     * @param allRecordCountRelation the relation type for the total record count
     * @param queryTime the time taken to execute the search query in milliseconds
     * @param partialResults flag indicating whether the results are partial
     * @param facetResponse the facet response containing aggregated search facets
     * @param start the starting position of the current page
     * @param pageSize the number of records per page
     * @param offset the offset value for pagination
     */
    public QueryResponseList(final List<Map<String, Object>> documentList, final long allRecordCount, final String allRecordCountRelation,
            final long queryTime, final boolean partialResults, final FacetResponse facetResponse, final int start, final int pageSize,
            final int offset) {
        this(documentList, start, pageSize, offset);
        this.allRecordCount = allRecordCount;
        this.allRecordCountRelation = allRecordCountRelation;
        this.queryTime = queryTime;
        this.partialResults = partialResults;
        this.facetResponse = facetResponse;
        if (pageSize > 0) {
            calculatePageInfo();
        }
    }

    /**
     * Calculates pagination information based on the current parameters.
     * This method computes page counts, navigation flags, record numbers, and page number lists.
     */
    protected void calculatePageInfo() {
        int startWithOffset = start - offset;
        if (startWithOffset < 0) {
            startWithOffset = 0;
        }
        allPageCount = (int) ((allRecordCount - 1) / pageSize) + 1;
        existPrevPage = startWithOffset > 0;
        existNextPage = startWithOffset < (long) (allPageCount - 1) * (long) pageSize;
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

    /**
     * Gets the starting position of the current page in the overall result set.
     *
     * @return the start position (0-based)
     */
    public int getStart() {
        return start;
    }

    /**
     * Gets the offset value used for pagination calculations.
     *
     * @return the offset value
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Gets the number of records per page.
     *
     * @return the page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Gets the current page number (1-based).
     *
     * @return the current page number
     */
    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    /**
     * Gets the total number of records in the search result set.
     *
     * @return the total record count
     */
    public long getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Gets the relation type for the total record count.
     *
     * @return the relation type (e.g., "eq" for exact count, "gte" for greater than or equal)
     */
    public String getAllRecordCountRelation() {
        return allRecordCountRelation;
    }

    /**
     * Gets the total number of pages based on the page size and total record count.
     *
     * @return the total page count
     */
    public int getAllPageCount() {
        return allPageCount;
    }

    /**
     * Checks whether there is a next page available.
     *
     * @return true if a next page exists, false otherwise
     */
    public boolean isExistNextPage() {
        return existNextPage;
    }

    /**
     * Checks whether there is a previous page available.
     *
     * @return true if a previous page exists, false otherwise
     */
    public boolean isExistPrevPage() {
        return existPrevPage;
    }

    /**
     * Gets the record number of the first record on the current page (1-based).
     *
     * @return the starting record number of the current page
     */
    public long getCurrentStartRecordNumber() {
        return currentStartRecordNumber;
    }

    /**
     * Gets the record number of the last record on the current page (1-based).
     *
     * @return the ending record number of the current page
     */
    public long getCurrentEndRecordNumber() {
        return currentEndRecordNumber;
    }

    /**
     * Gets a list of page numbers for pagination display.
     * Typically returns a range of page numbers around the current page.
     *
     * @return a list of page numbers as strings
     */
    public List<String> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Gets the search query string that was used to generate these results.
     *
     * @return the search query string
     */
    public String getSearchQuery() {
        return searchQuery;
    }

    /**
     * Sets the search query string that was used to generate these results.
     *
     * @param searchQuery the search query string
     */
    public void setSearchQuery(final String searchQuery) {
        this.searchQuery = searchQuery;
    }

    /**
     * Gets the total execution time for the search request in milliseconds.
     *
     * @return the execution time in milliseconds
     */
    public long getExecTime() {
        return execTime;
    }

    /**
     * Sets the total execution time for the search request in milliseconds.
     *
     * @param execTime the execution time in milliseconds
     */
    public void setExecTime(final long execTime) {
        this.execTime = execTime;
    }

    /**
     * Gets the facet response containing aggregated search facets and their counts.
     *
     * @return the facet response, or null if no facets were requested
     */
    public FacetResponse getFacetResponse() {
        return facetResponse;
    }

    /**
     * Checks whether the search results are partial (not complete).
     *
     * @return true if the results are partial, false if complete
     */
    public boolean isPartialResults() {
        return partialResults;
    }

    /**
     * Gets the time taken to execute the search query in milliseconds.
     *
     * @return the query execution time in milliseconds
     */
    public long getQueryTime() {
        return queryTime;
    }

    @Override
    public String toString() {
        return "QueryResponseList [parent=" + parent + ", start=" + start + ", offset=" + offset + ", pageSize=" + pageSize
                + ", currentPageNumber=" + currentPageNumber + ", allRecordCount=" + allRecordCount + ", allRecordCountRelation="
                + allRecordCountRelation + ", allPageCount=" + allPageCount + ", existNextPage=" + existNextPage + ", existPrevPage="
                + existPrevPage + ", currentStartRecordNumber=" + currentStartRecordNumber + ", currentEndRecordNumber="
                + currentEndRecordNumber + ", pageNumberList=" + pageNumberList + ", searchQuery=" + searchQuery + ", execTime=" + execTime
                + ", facetResponse=" + facetResponse + ", partialResults=" + partialResults + ", queryTime=" + queryTime + "]";
    }

}
