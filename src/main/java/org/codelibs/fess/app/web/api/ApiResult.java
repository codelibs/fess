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
package org.codelibs.fess.app.web.api;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.util.ComponentUtil;
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

        private Status(final int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public static class ApiResponse {
        protected String version = Constants.WEB_API_VERSION;
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

    public static class ApiErrorResponse extends ApiResponse {
        protected String message;

        public ApiErrorResponse message(final String message) {
            this.message = message;
            return this;
        }

        public ApiErrorResponse message(final VaMessenger<FessMessages> validationMessagesLambda) {
            final FessMessages messages = new FessMessages();
            validationMessagesLambda.message(messages);
            message =
                    ComponentUtil.getMessageManager()
                            .toMessageList(LaRequestUtil.getOptionalRequest().map(r -> r.getLocale()).orElse(Locale.ENGLISH), messages)
                            .stream().collect(Collectors.joining(" "));
            return this;
        }
    }
}
