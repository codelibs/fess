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

import java.util.Locale;
import java.util.stream.Collectors;

import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.validation.VaMessenger;

public class ApiResult {

    protected ApiResponse response = null;

    public ApiResult(ApiResponse response) {
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

        public ApiResponse status(Status status) {
            this.status = status.getId();
            return this;
        }

        public ApiResult result() {
            return new ApiResult(this);
        }
    }

    public static class ApiErrorResponse extends ApiResponse {
        protected String message;

        public ApiErrorResponse message(String message) {
            this.message = message;
            return this;
        }

        public ApiErrorResponse message(VaMessenger<FessMessages> validationMessagesLambda) {
            FessMessages messages = new FessMessages();
            validationMessagesLambda.message(messages);
            message =
                    ComponentUtil.getMessageManager()
                            .toMessageList(LaRequestUtil.getOptionalRequest().map(r -> r.getLocale()).orElse(Locale.ENGLISH), messages)
                            .stream().collect(Collectors.joining(" "));
            return this;
        }
    }
}
