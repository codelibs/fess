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
package org.codelibs.fess.app.web.api;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.FacetResponse;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.validation.VaMessenger;

public class ApiResult {

    protected ApiResponse response = null;

    public ApiResult(final ApiResponse response) {
        this.response = response;
    }

    public enum Status {
        OK(0), BAD_REQUEST(1), SYSTEM_ERROR(2), UNAUTHORIZED(3);

        private final int id;

        Status(final int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public static class ApiResponse {
        protected String version = ComponentUtil.getSystemHelper().getProductVersion();
        protected int status;

        public ApiResponse status(final Status status) {
            this.status = status.getId();
            return this;
        }

        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    public static class ApiUpdateResponse extends ApiResponse {
        protected String id;
        protected boolean created;

        public ApiUpdateResponse id(final String id) {
            this.id = id;
            return this;
        }

        public ApiUpdateResponse created(final boolean created) {
            this.created = created;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    public static class ApiDeleteResponse extends ApiResponse {
        protected long count = 1;

        public ApiDeleteResponse count(final long count) {
            this.count = count;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    public static class ApiConfigResponse extends ApiResponse {
        protected Object setting;

        public ApiConfigResponse setting(final Object setting) {
            this.setting = setting;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    public static class ApiConfigsResponse<T> extends ApiResponse {
        protected List<T> settings;
        protected long total = 0;

        public ApiConfigsResponse<T> settings(final List<T> settings) {
            this.settings = settings;
            this.total = settings.size();
            return this;
        }

        public ApiConfigsResponse<T> total(final long total) {
            this.total = total;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    public static class ApiDocResponse extends ApiResponse {
        protected Object doc;

        public ApiDocResponse doc(final Object doc) {
            this.doc = doc;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    public static class ApiDocsResponse extends ApiResponse {
        protected String queryId;
        protected List<Map<String, Object>> docs;
        protected String highlightParams;
        protected String execTime;
        protected int pageSize;
        protected int pageNumber;
        protected long recordCount;
        protected String recordCountRelation;
        protected int pageCount;
        protected boolean nextPage;
        protected boolean prevPage;
        protected long startRecordNumber;
        protected long endRecordNumber;
        protected List<String> pageNumbers;
        protected boolean partial;
        protected long queryTime;
        protected String searchQuery;
        protected long requestedTime;
        protected List<Map<String, Object>> facetField;
        protected List<Map<String, Object>> facetQuery;

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

    public static class ApiLogResponse extends ApiResponse {
        protected Object log;

        public ApiLogResponse log(final Object log) {
            this.log = log;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    public static class ApiLogsResponse<T> extends ApiResponse {
        protected List<T> logs;
        protected long total = 0;

        public ApiLogsResponse<T> logs(final List<T> logs) {
            this.logs = logs;
            return this;
        }

        public ApiLogsResponse<T> total(final long total) {
            this.total = total;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    public static class ApiLogFilesResponse extends ApiResponse {
        protected List<Map<String, Object>> files;
        protected long total = 0;

        public ApiLogFilesResponse files(final List<Map<String, Object>> files) {
            this.files = files;
            return this;
        }

        public ApiLogFilesResponse total(final long total) {
            this.total = total;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    public static class ApiBackupFilesResponse extends ApiResponse {
        protected List<Map<String, String>> files;
        protected long total = 0;

        public ApiBackupFilesResponse files(final List<Map<String, String>> files) {
            this.files = files;
            return this;
        }

        public ApiBackupFilesResponse total(final long total) {
            this.total = total;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    public static class ApiSystemInfoResponse extends ApiResponse {
        protected List<Map<String, String>> envProps;
        protected List<Map<String, String>> systemProps;
        protected List<Map<String, String>> fessProps;
        protected List<Map<String, String>> bugReportProps;

        public ApiSystemInfoResponse envProps(final List<Map<String, String>> envProps) {
            this.envProps = envProps;
            return this;
        }

        public ApiSystemInfoResponse systemProps(final List<Map<String, String>> systemProps) {
            this.systemProps = systemProps;
            return this;
        }

        public ApiSystemInfoResponse fessProps(final List<Map<String, String>> fessProps) {
            this.fessProps = fessProps;
            return this;
        }

        public ApiSystemInfoResponse bugReportProps(final List<Map<String, String>> bugReportProps) {
            this.bugReportProps = bugReportProps;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    public static class ApiErrorResponse extends ApiResponse {
        protected String message;

        public ApiErrorResponse message(final String message) {
            this.message = message;
            return this;
        }

        public ApiErrorResponse message(final VaMessenger<FessMessages> validationMessagesLambda) {
            final FessMessages messages = new FessMessages();
            validationMessagesLambda.message(messages);
            message = ComponentUtil.getMessageManager()
                    .toMessageList(LaRequestUtil.getOptionalRequest().map(HttpServletRequest::getLocale).orElse(Locale.ENGLISH), messages)
                    .stream().collect(Collectors.joining(" "));
            return this;
        }
    }

    public static class ApiPluginResponse extends ApiResponse {
        protected List<Map<String, String>> plugins;

        public ApiPluginResponse plugins(final List<Map<String, String>> plugins) {
            this.plugins = plugins;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    public static class ApiStorageResponse extends ApiResponse {
        protected List<Map<String, Object>> items;

        public ApiStorageResponse items(final List<Map<String, Object>> items) {
            this.items = items;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    public static class ApiStatsResponse extends ApiResponse {
        protected Map<String, Object> stats;

        public ApiStatsResponse stats(final Map<String, Object> stats) {
            this.stats = stats;
            return this;
        }

        @Override
        public ApiResult result() {
            return new ApiResult(this);
        }
    }
}
