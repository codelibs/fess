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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.util.List;

import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.api.ApiFailureHook;
import org.lastaflute.web.api.ApiFailureResource;
import org.lastaflute.web.login.exception.LoginUnauthorizedException;
import org.lastaflute.web.response.ApiResponse;
import org.lastaflute.web.response.JsonResponse;

/**
 * @author jflute
 */
public class FessApiFailureHook implements ApiFailureHook { // #change_it for handling API failure

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final int HTTP_BAD_REQUEST = 400;
    protected static final int HTTP_UNAUTHORIZED = 401;

    // ===================================================================================
    //                                                                    Business Failure
    //                                                                    ================
    @Override
    public ApiResponse handleValidationError(final ApiFailureResource resource) {
        return asJson(createFailureBean(resource)).httpStatus(HTTP_BAD_REQUEST);
    }

    @Override
    public ApiResponse handleApplicationException(final ApiFailureResource resource, final RuntimeException cause) {
        final int status;
        if (cause instanceof LoginUnauthorizedException) {
            status = HTTP_UNAUTHORIZED; // unauthorized
        } else {
            status = HTTP_BAD_REQUEST; // bad request
        }
        return asJson(createFailureBean(resource)).httpStatus(status);
    }

    // ===================================================================================
    //                                                                      System Failure
    //                                                                      ==============
    @Override
    public OptionalThing<ApiResponse> handleClientException(final ApiFailureResource resource, final RuntimeException cause) {
        return OptionalThing.empty(); // means empty body (HTTP status will be automatically sent)
    }

    @Override
    public OptionalThing<ApiResponse> handleServerException(final ApiFailureResource resource, final Throwable cause) {
        return OptionalThing.empty(); // means empty body (HTTP status will be automatically sent)
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected JsonResponse<TooSimpleFailureBean> asJson(final TooSimpleFailureBean bean) {
        return new JsonResponse<>(bean);
    }

    protected TooSimpleFailureBean createFailureBean(final ApiFailureResource resource) {
        return new TooSimpleFailureBean(resource.getMessageList());
    }

    public static class TooSimpleFailureBean {

        public final String notice = "[Attension] tentative JSON so you should change it: " + FessApiFailureHook.class;

        public final List<String> messageList;

        public TooSimpleFailureBean(final List<String> messageList) {
            this.messageList = messageList;
        }
    }
}
