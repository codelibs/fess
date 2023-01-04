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
package org.codelibs.fess.entity;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.util.FacetResponse;

public class SearchRenderData {

    protected List<Map<String, Object>> documentItems;

    protected FacetResponse facetResponse;

    protected String appendHighlightParams;

    protected String execTime;

    protected int pageSize;

    protected int currentPageNumber;

    protected long allRecordCount;

    protected String allRecordCountRelation;

    protected int allPageCount;

    protected boolean existNextPage;

    protected boolean existPrevPage;

    protected long currentStartRecordNumber;

    protected long currentEndRecordNumber;

    protected List<String> pageNumberList;

    protected boolean partialResults;

    protected String searchQuery;

    protected long queryTime;

    protected long requestedTime;

    protected String queryId;

    public void setDocumentItems(final List<Map<String, Object>> documentItems) {
        this.documentItems = documentItems;
    }

    public void setFacetResponse(final FacetResponse facetResponse) {
        this.facetResponse = facetResponse;
    }

    public void setAppendHighlightParams(final String appendHighlightParams) {
        this.appendHighlightParams = appendHighlightParams;
    }

    public void setExecTime(final String execTime) {
        this.execTime = execTime;
    }

    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    public void setCurrentPageNumber(final int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public void setAllRecordCount(final long allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    public void setAllRecordCountRelation(final String allRecordCountRelation) {
        this.allRecordCountRelation = allRecordCountRelation;
    }

    public void setAllPageCount(final int allPageCount) {
        this.allPageCount = allPageCount;
    }

    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    public void setExistPrevPage(final boolean existPrevPage) {
        this.existPrevPage = existPrevPage;
    }

    public void setCurrentStartRecordNumber(final long currentStartRecordNumber) {
        this.currentStartRecordNumber = currentStartRecordNumber;
    }

    public void setCurrentEndRecordNumber(final long currentEndRecordNumber) {
        this.currentEndRecordNumber = currentEndRecordNumber;
    }

    public void setPageNumberList(final List<String> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }

    public void setPartialResults(final boolean partialResults) {
        this.partialResults = partialResults;
    }

    public void setQueryTime(final long queryTime) {
        this.queryTime = queryTime;
    }

    public void setSearchQuery(final String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void setRequestedTime(final long requestedTime) {
        this.requestedTime = requestedTime;
    }

    public void setQueryId(final String queryId) {
        this.queryId = queryId;
    }

    public List<Map<String, Object>> getDocumentItems() {
        return documentItems;
    }

    public FacetResponse getFacetResponse() {
        return facetResponse;
    }

    public String getAppendHighlightParams() {
        return appendHighlightParams;
    }

    public String getExecTime() {
        return execTime;
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

    public boolean isPartialResults() {
        return partialResults;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public long getQueryTime() {
        return queryTime;
    }

    public long getRequestedTime() {
        return requestedTime;
    }

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
