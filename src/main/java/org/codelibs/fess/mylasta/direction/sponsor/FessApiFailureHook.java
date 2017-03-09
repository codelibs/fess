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

import java.util.stream.Collectors;

import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiErrorResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
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
        return asJson(createFailureBean(Status.BAD_REQUEST, createMessage(resource, null))).httpStatus(HTTP_BAD_REQUEST);
    }

    @Override
    public ApiResponse handleApplicationException(final ApiFailureResource resource, final RuntimeException cause) {
        if (cause instanceof LoginUnauthorizedException) {
            return asJson(createFailureBean(Status.UNAUTHORIZED, "Unauthorized request.")).httpStatus(HTTP_UNAUTHORIZED);
        } else {
            return asJson(createFailureBean(Status.BAD_REQUEST, createMessage(resource, cause))).httpStatus(HTTP_BAD_REQUEST);
        }
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
    protected JsonResponse<ApiResult> asJson(final ApiResult bean) {
        return new JsonResponse<>(bean);
    }

    protected ApiResult createFailureBean(final Status status, final String message) {
        return new ApiErrorResponse().message(message).status(status).result();
    }

    protected String createMessage(final ApiFailureResource resource, final RuntimeException cause) {
        if (!resource.getMessageList().isEmpty()) {
            return resource.getMessageList().stream().collect(Collectors.joining(" "));
        }
        if (cause != null) {
            return cause.getMessage();
        }
        return "Unknown error";
    }

}
