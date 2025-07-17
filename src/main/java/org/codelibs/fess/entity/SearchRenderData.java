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
package org.codelibs.fess.entity;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.util.FacetResponse;

/**
 * Data container for search results rendering.
 *
 * This class holds all the data needed to render search results in the UI,
 * including the actual search results, pagination information, facet data,
 * execution timing, and highlighting parameters.
 */
public class SearchRenderData {

    /** List of search result documents. */
    protected List<Map<String, Object>> documentItems;

    /** Facet response containing aggregated search facets. */
    protected FacetResponse facetResponse;

    /** Additional highlight parameters to append to URLs. */
    protected String appendHighlightParams;

    /** Formatted execution time for the search request. */
    protected String execTime;

    /** Number of results per page. */
    protected int pageSize;

    /** Current page number being displayed. */
    protected int currentPageNumber;

    /** Total number of records matching the search query. */
    protected long allRecordCount;

    /** Relation type for the record count (e.g., "eq", "gte"). */
    protected String allRecordCountRelation;

    /** Total number of pages based on record count and page size. */
    protected int allPageCount;

    /** Flag indicating whether a next page exists. */
    protected boolean existNextPage;

    /** Flag indicating whether a previous page exists. */
    protected boolean existPrevPage;

    /** Starting record number for the current page. */
    protected long currentStartRecordNumber;

    /** Ending record number for the current page. */
    protected long currentEndRecordNumber;

    /** List of page numbers for pagination navigation. */
    protected List<String> pageNumberList;

    /** Flag indicating whether the results are partial due to timeout or other issues. */
    protected boolean partialResults;

    /** The actual search query executed against the search engine. */
    protected String searchQuery;

    /** Time taken to execute the search query in milliseconds. */
    protected long queryTime;

    /** Timestamp when the search request was made. */
    protected long requestedTime;

    /** Unique identifier for this search query session. */
    protected String queryId;

    /**
     * Default constructor for creating a new SearchRenderData instance.
     */
    public SearchRenderData() {
        // Default constructor
    }

    /**
     * Sets the list of search result documents.
     *
     * @param documentItems The list of search result documents
     */
    public void setDocumentItems(final List<Map<String, Object>> documentItems) {
        this.documentItems = documentItems;
    }

    /**
     * Sets the facet response containing aggregated search facets.
     *
     * @param facetResponse The facet response
     */
    public void setFacetResponse(final FacetResponse facetResponse) {
        this.facetResponse = facetResponse;
    }

    /**
     * Sets additional highlight parameters to append to URLs.
     *
     * @param appendHighlightParams The highlight parameters string
     */
    public void setAppendHighlightParams(final String appendHighlightParams) {
        this.appendHighlightParams = appendHighlightParams;
    }

    /**
     * Sets the formatted execution time for the search request.
     *
     * @param execTime The formatted execution time string
     */
    public void setExecTime(final String execTime) {
        this.execTime = execTime;
    }

    /**
     * Sets the number of results per page.
     *
     * @param pageSize The page size
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Sets the current page number being displayed.
     *
     * @param currentPageNumber The current page number
     */
    public void setCurrentPageNumber(final int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    /**
     * Sets the total number of records matching the search query.
     *
     * @param allRecordCount The total record count
     */
    public void setAllRecordCount(final long allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    /**
     * Sets the relation type for the record count (e.g., "eq", "gte").
     *
     * @param allRecordCountRelation The record count relation
     */
    public void setAllRecordCountRelation(final String allRecordCountRelation) {
        this.allRecordCountRelation = allRecordCountRelation;
    }

    /**
     * Sets the total number of pages based on record count and page size.
     *
     * @param allPageCount The total page count
     */
    public void setAllPageCount(final int allPageCount) {
        this.allPageCount = allPageCount;
    }

    /**
     * Sets whether a next page exists.
     *
     * @param existNextPage true if a next page exists
     */
    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    /**
     * Sets whether a previous page exists.
     *
     * @param existPrevPage true if a previous page exists
     */
    public void setExistPrevPage(final boolean existPrevPage) {
        this.existPrevPage = existPrevPage;
    }

    /**
     * Sets the starting record number for the current page.
     *
     * @param currentStartRecordNumber The starting record number
     */
    public void setCurrentStartRecordNumber(final long currentStartRecordNumber) {
        this.currentStartRecordNumber = currentStartRecordNumber;
    }

    /**
     * Sets the ending record number for the current page.
     *
     * @param currentEndRecordNumber The ending record number
     */
    public void setCurrentEndRecordNumber(final long currentEndRecordNumber) {
        this.currentEndRecordNumber = currentEndRecordNumber;
    }

    /**
     * Sets the list of page numbers for pagination navigation.
     *
     * @param pageNumberList The page number list
     */
    public void setPageNumberList(final List<String> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }

    /**
     * Sets whether the results are partial due to timeout or other issues.
     *
     * @param partialResults true if results are partial
     */
    public void setPartialResults(final boolean partialResults) {
        this.partialResults = partialResults;
    }

    /**
     * Sets the time taken to execute the search query in milliseconds.
     *
     * @param queryTime The query execution time in milliseconds
     */
    public void setQueryTime(final long queryTime) {
        this.queryTime = queryTime;
    }

    /**
     * Sets the actual search query executed against the search engine.
     *
     * @param searchQuery The search query string
     */
    public void setSearchQuery(final String searchQuery) {
        this.searchQuery = searchQuery;
    }

    /**
     * Sets the timestamp when the search request was made.
     *
     * @param requestedTime The request timestamp
     */
    public void setRequestedTime(final long requestedTime) {
        this.requestedTime = requestedTime;
    }

    /**
     * Sets the unique identifier for this search query session.
     *
     * @param queryId The query identifier
     */
    public void setQueryId(final String queryId) {
        this.queryId = queryId;
    }

    /**
     * Gets the list of search result documents.
     *
     * @return The list of search result documents
     */
    public List<Map<String, Object>> getDocumentItems() {
        return documentItems;
    }

    /**
     * Gets the facet response containing aggregated search facets.
     *
     * @return The facet response
     */
    public FacetResponse getFacetResponse() {
        return facetResponse;
    }

    /**
     * Gets additional highlight parameters to append to URLs.
     *
     * @return The highlight parameters string
     */
    public String getAppendHighlightParams() {
        return appendHighlightParams;
    }

    /**
     * Gets the formatted execution time for the search request.
     *
     * @return The formatted execution time string
     */
    public String getExecTime() {
        return execTime;
    }

    /**
     * Gets the number of results per page.
     *
     * @return The page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Gets the current page number being displayed.
     *
     * @return The current page number
     */
    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    /**
     * Gets the total number of records matching the search query.
     *
     * @return The total record count
     */
    public long getAllRecordCount() {
        return allRecordCount;
    }

    /**
     * Gets the relation type for the record count (e.g., "eq", "gte").
     *
     * @return The record count relation
     */
    public String getAllRecordCountRelation() {
        return allRecordCountRelation;
    }

    /**
     * Gets the total number of pages based on record count and page size.
     *
     * @return The total page count
     */
    public int getAllPageCount() {
        return allPageCount;
    }

    /**
     * Checks whether a next page exists.
     *
     * @return true if a next page exists
     */
    public boolean isExistNextPage() {
        return existNextPage;
    }

    /**
     * Checks whether a previous page exists.
     *
     * @return true if a previous page exists
     */
    public boolean isExistPrevPage() {
        return existPrevPage;
    }

    /**
     * Gets the starting record number for the current page.
     *
     * @return The starting record number
     */
    public long getCurrentStartRecordNumber() {
        return currentStartRecordNumber;
    }

    /**
     * Gets the ending record number for the current page.
     *
     * @return The ending record number
     */
    public long getCurrentEndRecordNumber() {
        return currentEndRecordNumber;
    }

    /**
     * Gets the list of page numbers for pagination navigation.
     *
     * @return The page number list
     */
    public List<String> getPageNumberList() {
        return pageNumberList;
    }

    /**
     * Checks whether the results are partial due to timeout or other issues.
     *
     * @return true if results are partial
     */
    public boolean isPartialResults() {
        return partialResults;
    }

    /**
     * Gets the actual search query executed against the search engine.
     *
     * @return The search query string
     */
    public String getSearchQuery() {
        return searchQuery;
    }

    /**
     * Gets the time taken to execute the search query in milliseconds.
     *
     * @return The query execution time in milliseconds
     */
    public long getQueryTime() {
        return queryTime;
    }

    /**
     * Gets the timestamp when the search request was made.
     *
     * @return The request timestamp
     */
    public long getRequestedTime() {
        return requestedTime;
    }

    /**
     * Gets the unique identifier for this search query session.
     *
     * @return The query identifier
     */
    public String getQueryId() {
        return queryId;
    }

    @Override
    public String toString() {
        return "SearchRenderData [documentItems=" + documentItems + ", facetResponse=" + facetResponse + ", appendHighlightParams="
                + appendHighlightParams + ", execTime=" + execTime + ", pageSize=" + pageSize + ", currentPageNumber=" + currentPageNumber
                + ", allRecordCount=" + allRecordCount + ", allRecordCountRelation=" + allRecordCountRelation + ", allPageCount="
                + allPageCount + ", existNextPage=" + existNextPage + ", existPrevPage=" + existPrevPage + ", currentStartRecordNumber="
                + currentStartRecordNumber + ", currentEndRecordNumber=" + currentEndRecordNumber + ", pageNumberList=" + pageNumberList
                + ", partialResults=" + partialResults + ", searchQuery=" + searchQuery + ", queryTime=" + queryTime + ", requestedTime="
                + requestedTime + ", queryId=" + queryId + "]";
    }

}
