package org.codelibs.fess.entity;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.util.FacetResponse;

public class SearchRenderData {

    private List<Map<String, Object>> documentItems;

    private FacetResponse facetResponse;

    private String appendHighlightParams;

    private String execTime;

    private int pageSize;

    private int currentPageNumber;

    private long allRecordCount;

    private int allPageCount;

    private boolean existNextPage;

    private boolean existPrevPage;

    private long currentStartRecordNumber;

    private long currentEndRecordNumber;

    private List<String> pageNumberList;

    private boolean partialResults;

    private String searchQuery;

    private long queryTime;

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

}
