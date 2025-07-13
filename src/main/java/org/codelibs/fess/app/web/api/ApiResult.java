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
package org.codelibs.fess.app.web.api;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.FacetResponse;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.validation.VaMessenger;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This class represents the base response structure for API results.
 * It encapsulates the API response and provides methods to build different types of API responses.
 */
public class ApiResult {

    /**
     * The API response object.
     */
    protected ApiResponse response = null;

    /**
     * Constructs an ApiResult with the specified ApiResponse.
     * @param response The API response object.
     */
    public ApiResult(final ApiResponse response) {
        this.response = response;
    }

    /**
     * Represents the status of an API response.
     */
    public enum Status {
        /** Successful response status. */
        OK(0),
        /** Bad request status indicating client error. */
        BAD_REQUEST(1),
        /** System error status indicating server error. */
        SYSTEM_ERROR(2),
        /** Unauthorized status indicating authentication failure. */
        UNAUTHORIZED(3),
        /** General failure status. */
        FAILED(9);

        private final int id;

        Status(final int id) {
            this.id = id;
        }

        /**
         * Gets the numeric ID of the status.
         * @return The numeric ID of the status.
         */
        public int getId() {
            return id;
        }
    }

    /**
     * Represents the base API response structure.
     */
    public static class ApiResponse {
        /** The version of the product. */
        protected String version = ComponentUtil.getSystemHelper().getProductVersion();
        /** The status code of the response. */
        protected int status;

        /**
         * Default constructor for ApiResponse.
         */
        public ApiResponse() {
            // Default constructor
        }

        /**
         * Sets the status of the response.
         * @param status The status to set.
         * @return This ApiResponse instance.
         */
        public ApiResponse status(final Status status) {
            this.status = status.getId();
            return this;
        }

        /**
         * Returns a new ApiResult instance with this ApiResponse.
         * @return A new ApiResult instance.
         */
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    /**
     * Represents an API response for an update operation.
     */
    public static class ApiUpdateResponse extends ApiResponse {
        /** The ID of the updated item. */
        protected String id;
        /** Whether the item was created (true) or updated (false). */
        protected boolean created;

        /**
         * Default constructor for ApiUpdateResponse.
         */
        public ApiUpdateResponse() {
            // Default constructor
        }

        /**
         * Sets the ID of the updated item.
         * @param id The ID to set.
         * @return This ApiUpdateResponse instance.
         */
        public ApiUpdateResponse id(final String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets whether the item was created or updated.
         * @param created True if created, false if updated.
         * @return This ApiUpdateResponse instance.
         */
        public ApiUpdateResponse created(final boolean created) {
            this.created = created;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    /**
     * Represents an API response for a delete operation.
     */
    public static class ApiDeleteResponse extends ApiResponse {
        /**
         * Constructs an empty ApiDeleteResponse.
         */
        public ApiDeleteResponse() {
            // NOP
        }

        /**
         * The number of deleted items.
         */
        protected long count = 1;

        /**
         * Sets the count of deleted items.
         * @param count The number of deleted items.
         * @return The ApiDeleteResponse instance.
         */
        public ApiDeleteResponse count(final long count) {
            this.count = count;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    /**
     * Represents an API response for configuration settings.
     */
    public static class ApiConfigResponse extends ApiResponse {
        /**
         * Constructs an empty ApiConfigResponse.
         */
        public ApiConfigResponse() {
            // NOP
        }

        /**
         * The configuration setting object.
         */
        protected Object setting;

        /**
         * Sets the configuration setting object.
         * @param setting The configuration setting object.
         * @return The ApiConfigResponse instance.
         */
        public ApiConfigResponse setting(final Object setting) {
            this.setting = setting;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    /**
     * Represents an API response for a list of configuration settings.
     * @param <T> the type of the configuration settings
     */
    public static class ApiConfigsResponse<T> extends ApiResponse {
        /**
         * Constructs an empty ApiConfigsResponse.
         */
        public ApiConfigsResponse() {
            // NOP
        }

        /**
         * The list of configuration settings.
         */
        protected List<T> settings;
        /**
         * The total number of configuration settings.
         */
        protected long total = 0;

        /**
         * Sets the list of configuration settings and updates the total count.
         * @param settings The list of configuration settings.
         * @return The ApiConfigsResponse instance.
         */
        public ApiConfigsResponse<T> settings(final List<T> settings) {
            this.settings = settings;
            total = settings.size();
            return this;
        }

        /**
         * Sets the total number of configuration settings.
         * @param total The total number of configuration settings.
         * @return The ApiConfigsResponse instance.
         */
        public ApiConfigsResponse<T> total(final long total) {
            this.total = total;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    /**
     * Represents an API response for a single document.
     */
    public static class ApiDocResponse extends ApiResponse {
        /**
         * Constructs an empty ApiDocResponse.
         */
        public ApiDocResponse() {
            // NOP
        }

        /**
         * The document object.
         */
        protected Object doc;

        /**
         * Sets the document object.
         * @param doc The document object.
         * @return The ApiDocResponse instance.
         */
        public ApiDocResponse doc(final Object doc) {
            this.doc = doc;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    /**
     * Represents an API response for search results, including document list, pagination, and facet information.
     */
    public static class ApiDocsResponse extends ApiResponse {
        /**
         * The ID of the search query.
         */
        protected String queryId;

        /**
         * Default constructor for ApiDocsResponse.
         */
        public ApiDocsResponse() {
            // Default constructor
        }

        /**
         * The list of documents returned in the search results.
         */
        protected List<Map<String, Object>> docs;
        /**
         * Parameters for highlighting search results.
         */
        protected String highlightParams;
        /**
         * The execution time of the search query.
         */
        protected String execTime;
        /**
         * The page size of the search results.
         */
        protected int pageSize;
        /**
         * The current page number of the search results.
         */
        protected int pageNumber;
        /**
         * The total number of records found.
         */
        protected long recordCount;
        /**
         * The relation of the record count (e.g., "eq" for exact, "gte" for greater than or equal to).
         */
        protected String recordCountRelation;
        /**
         * The total number of pages in the search results.
         */
        protected int pageCount;
        /**
         * Indicates if there is a next page of search results.
         */
        protected boolean nextPage;
        /**
         * Indicates if there is a previous page of search results.
         */
        protected boolean prevPage;
        /**
         * The starting record number for the current page of search results.
         */
        protected long startRecordNumber;
        /**
         * The ending record number for the current page of search results.
         */
        protected long endRecordNumber;
        /**
         * The list of page numbers for pagination.
         */
        protected List<String> pageNumbers;
        /**
         * Indicates if the search results are partial.
         */
        protected boolean partial;
        /**
         * The time taken for the search query in milliseconds.
         */
        protected long queryTime;
        /**
         * The search query string.
         */
        protected String searchQuery;
        /**
         * The time when the search request was made.
         */
        protected long requestedTime;
        /**
         * The list of facet fields and their values.
         */
        protected List<Map<String, Object>> facetField;
        /**
         * The list of facet queries and their counts.
         */
        protected List<Map<String, Object>> facetQuery;

        /**
         * Populates this response with search render data.
         * @param data The search render data to populate from.
         * @return This ApiDocsResponse instance.
         */
        public ApiDocsResponse renderData(final SearchRenderData data) {
            queryId = data.getQueryId();
            docs = data.getDocumentItems();
            highlightParams = data.getAppendHighlightParams();
            execTime = data.getExecTime();
            pageSize = data.getPageSize();
            pageNumber = data.getCurrentPageNumber();
            recordCount = data.getAllRecordCount();
            recordCountRelation = data.getAllRecordCountRelation();
            pageCount = data.getAllPageCount();
            nextPage = data.isExistNextPage();
            prevPage = data.isExistPrevPage();
            startRecordNumber = data.getCurrentStartRecordNumber();
            endRecordNumber = data.getCurrentEndRecordNumber();
            pageNumbers = data.getPageNumberList();
            partial = data.isPartialResults();
            queryTime = data.getQueryTime();
            searchQuery = data.getSearchQuery();
            requestedTime = data.getRequestedTime();
            final FacetResponse facetResponse = data.getFacetResponse();
            if (facetResponse != null && facetResponse.hasFacetResponse()) {
                // facet field
                if (facetResponse.getFieldList() != null) {
                    facetField = facetResponse.getFieldList().stream().map(field -> {
                        final Map<String, Object> fieldMap = new HashMap<>(2, 1f);
                        fieldMap.put("name", field.getName());
                        fieldMap.put("result", field.getValueCountMap().entrySet().stream().map(e -> {
                            final Map<String, Object> valueCount = new HashMap<>(2, 1f);
                            valueCount.put("value", e.getKey());
                            valueCount.put("count", e.getValue());
                            return valueCount;
                        }).collect(Collectors.toList()));
                        return fieldMap;
                    }).collect(Collectors.toList());
                }
                // facet q
                if (facetResponse.getQueryCountMap() != null) {
                    facetQuery = facetResponse.getQueryCountMap().entrySet().stream().map(e -> {
                        final Map<String, Object> valueCount = new HashMap<>(2, 1f);
                        valueCount.put("value", e.getKey());
                        valueCount.put("count", e.getValue());
                        return valueCount;
                    }).collect(Collectors.toList());

                }
            }
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    /**
     * Represents an API response for a log entry.
     */
    public static class ApiLogResponse extends ApiResponse {
        /** The log entry object. */
        protected Object log;

        /**
         * Default constructor for ApiLogResponse.
         */
        public ApiLogResponse() {
            // Default constructor
        }

        /**
         * Sets the log entry object.
         * @param log The log entry object.
         * @return This ApiLogResponse instance.
         */
        public ApiLogResponse log(final Object log) {
            this.log = log;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    /**
     * Represents an API response for a list of logs.
     * @param <T> the type of the logs
     */
    public static class ApiLogsResponse<T> extends ApiResponse {
        /** The list of log entries. */
        protected List<T> logs;

        /**
         * Default constructor for ApiLogsResponse.
         */
        public ApiLogsResponse() {
            // Default constructor
        }

        /**
         * The total number of logs.
         */
        protected long total = 0;

        /**
         * Sets the list of log entries.
         * @param logs The list of log entries.
         * @return This ApiLogsResponse instance.
         */
        public ApiLogsResponse<T> logs(final List<T> logs) {
            this.logs = logs;
            return this;
        }

        /**
         * Sets the total number of logs.
         * @param total The total number of logs.
         * @return This ApiLogsResponse instance.
         */
        public ApiLogsResponse<T> total(final long total) {
            this.total = total;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    /**
     * Represents an API response containing a list of log files.
     */
    public static class ApiLogFilesResponse extends ApiResponse {
        /** The list of log files. */
        protected List<Map<String, Object>> files;

        /**
         * Default constructor for ApiLogFilesResponse.
         */
        public ApiLogFilesResponse() {
            // Default constructor
        }

        /**
         * The total number of log files.
         */
        protected long total = 0;

        /**
         * Sets the list of log files.
         * @param files The list of log files.
         * @return This ApiLogFilesResponse instance.
         */
        public ApiLogFilesResponse files(final List<Map<String, Object>> files) {
            this.files = files;
            return this;
        }

        /**
         * Sets the total number of log files.
         * @param total The total number of log files.
         * @return This ApiLogFilesResponse instance.
         */
        public ApiLogFilesResponse total(final long total) {
            this.total = total;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    /**
     * Represents an API response containing a list of backup files.
     */
    public static class ApiBackupFilesResponse extends ApiResponse {
        /**
         * The list of backup files, where each file is represented by a map of strings.
         */
        protected List<Map<String, String>> files;
        /**
         * The total number of backup files.
         */
        protected long total = 0;

        /**
         * Constructs an empty ApiBackupFilesResponse.
         */
        public ApiBackupFilesResponse() {
            // NOP
        }

        /**
         * Sets the list of backup files.
         * @param files The list of backup files, where each file is represented by a map of strings.
         * @return The ApiBackupFilesResponse instance.
         */
        public ApiBackupFilesResponse files(final List<Map<String, String>> files) {
            this.files = files;
            return this;
        }

        /**
         * Sets the total number of backup files.
         * @param total The total number of backup files.
         * @return The ApiBackupFilesResponse instance.
         */
        public ApiBackupFilesResponse total(final long total) {
            this.total = total;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    /**
     * Represents an API response containing system information.
     */
    public static class ApiSystemInfoResponse extends ApiResponse {
        /** Environment properties. */
        protected List<Map<String, String>> envProps;

        /**
         * Default constructor for ApiSystemInfoResponse.
         */
        public ApiSystemInfoResponse() {
            // Default constructor
        }

        /** System properties. */
        protected List<Map<String, String>> systemProps;
        /** Fess-specific properties. */
        protected List<Map<String, String>> fessProps;
        /** Bug report properties. */
        protected List<Map<String, String>> bugReportProps;

        /**
         * Sets the environment properties.
         * @param envProps The environment properties.
         * @return This ApiSystemInfoResponse instance.
         */
        public ApiSystemInfoResponse envProps(final List<Map<String, String>> envProps) {
            this.envProps = envProps;
            return this;
        }

        /**
         * Sets the system properties.
         * @param systemProps The system properties.
         * @return This ApiSystemInfoResponse instance.
         */
        public ApiSystemInfoResponse systemProps(final List<Map<String, String>> systemProps) {
            this.systemProps = systemProps;
            return this;
        }

        /**
         * Sets the Fess-specific properties.
         * @param fessProps The Fess-specific properties.
         * @return This ApiSystemInfoResponse instance.
         */
        public ApiSystemInfoResponse fessProps(final List<Map<String, String>> fessProps) {
            this.fessProps = fessProps;
            return this;
        }

        /**
         * Sets the bug report properties.
         * @param bugReportProps The bug report properties.
         * @return This ApiSystemInfoResponse instance.
         */
        public ApiSystemInfoResponse bugReportProps(final List<Map<String, String>> bugReportProps) {
            this.bugReportProps = bugReportProps;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    /**
     * Represents an API response for an error.
     */
    public static class ApiErrorResponse extends ApiResponse {
        /** The error message. */
        protected String message;

        /**
         * Default constructor for ApiErrorResponse.
         */
        public ApiErrorResponse() {
            // Default constructor
        }

        /**
         * Sets the error message.
         * @param message The error message.
         * @return This ApiErrorResponse instance.
         */
        public ApiErrorResponse message(final String message) {
            this.message = message;
            return this;
        }

        /**
         * Sets the error message from validation messages.
         * @param validationMessagesLambda Lambda function to process validation messages.
         * @return This ApiErrorResponse instance.
         */
        public ApiErrorResponse message(final VaMessenger<FessMessages> validationMessagesLambda) {
            final FessMessages messages = new FessMessages();
            validationMessagesLambda.message(messages);
            message = ComponentUtil.getMessageManager()
                    .toMessageList(LaRequestUtil.getOptionalRequest().map(HttpServletRequest::getLocale).orElse(Locale.ENGLISH), messages)
                    .stream().collect(Collectors.joining(" "));
            return this;
        }
    }

    /**
     * Represents an API response for plugin information.
     */
    public static class ApiPluginResponse extends ApiResponse {
        /** The list of plugins. */
        protected List<Map<String, String>> plugins;

        /**
         * Default constructor for ApiPluginResponse.
         */
        public ApiPluginResponse() {
            // Default constructor
        }

        /**
         * Sets the list of plugins.
         * @param plugins The list of plugins.
         * @return This ApiPluginResponse instance.
         */
        public ApiPluginResponse plugins(final List<Map<String, String>> plugins) {
            this.plugins = plugins;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    /**
     * Represents an API response for storage-related operations, typically containing a list of items.
     */
    public static class ApiStorageResponse extends ApiResponse {
        /** The list of storage items. */
        protected List<Map<String, Object>> items;

        /**
         * Default constructor for ApiStorageResponse.
         */
        public ApiStorageResponse() {
            // Default constructor
        }

        /**
         * Sets the list of storage items.
         * @param items The list of storage items.
         * @return This ApiStorageResponse instance.
         */
        public ApiStorageResponse items(final List<Map<String, Object>> items) {
            this.items = items;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    /**
     * Represents an API response containing statistical information.
     */
    public static class ApiStatsResponse extends ApiResponse {
        /** The statistical data. */
        protected Map<String, Object> stats;

        /**
         * Default constructor for ApiStatsResponse.
         */
        public ApiStatsResponse() {
            // Default constructor
        }

        /**
         * Sets the statistical data.
         * @param stats The statistical data.
         * @return This ApiStatsResponse instance.
         */
        public ApiStatsResponse stats(final Map<String, Object> stats) {
            this.stats = stats;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    /**
     * Represents an API response for bulk operations, containing a list of processed items.
     */
    public static class ApiBulkResponse extends ApiResponse {
        /**
         * Constructs an empty ApiBulkResponse.
         */
        public ApiBulkResponse() {
            // NOP
        }

        /**
         * The list of items processed in the bulk operation.
         */
        protected List<Map<String, Object>> items;

        /**
         * Sets the list of items processed in the bulk operation.
         * @param items The list of items, where each item is represented by a map.
         * @return The ApiBulkResponse instance.
         */
        public ApiBulkResponse items(final List<Map<String, Object>> items) {
            this.items = items;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }
}
