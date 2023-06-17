/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

public class QueryResponseList implements List<Map<String, Object>> {

    protected final List<Map<String, Object>> parent;

    protected final int start;

    protected final int offset;

    /** The value of current page number. */
    protected final int pageSize;

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

    // for testing
    protected QueryResponseList(final List<Map<String, Object>> documentList, final int start, final int pageSize, final int offset) {
        this.parent = documentList;
        this.offset = offset;
        this.start = start;
        this.pageSize = pageSize;
    }

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

    public int getStart() {
        return start;
    }

    public int getOffset() {
        return offset;
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
