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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiErrorResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.util.ComponentUtil;
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

    private static final Logger logger = LogManager.getLogger(FessApiFailureHook.class);

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
        }
        return asJson(createFailureBean(Status.BAD_REQUEST, createMessage(resource, cause))).httpStatus(HTTP_BAD_REQUEST);
    }

    // ===================================================================================
    //                                                                      System Failure
    //                                                                      ==============
    @Override
    public OptionalThing<ApiResponse> handleClientException(final ApiFailureResource resource, final RuntimeException cause) {
        return OptionalThing.of(asJson(createFailureBean(Status.BAD_REQUEST, createMessage(resource, cause))));
    }

    @Override
    public OptionalThing<ApiResponse> handleServerException(final ApiFailureResource resource, final Throwable cause) {
        return OptionalThing.of(asJson(createFailureBean(Status.SYSTEM_ERROR, createMessage(resource, cause))));
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

    protected String createMessage(final ApiFailureResource resource, final Throwable cause) {
        if (!resource.getMessageList().isEmpty()) {
            return resource.getMessageList().stream().collect(Collectors.joining(" "));
        }

        if (cause == null) {
            return "Unknown error";
        }

        final Supplier<String> stacktraceString = () -> {
            final StringBuilder sb = new StringBuilder();
            if (StringUtil.isBlank(cause.getMessage())) {
                sb.append(cause.getClass().getName());
            } else {
                sb.append(cause.getMessage());
            }
            try (final StringWriter sw = new StringWriter(); final PrintWriter pw = new PrintWriter(sw)) {
                cause.printStackTrace(pw);
                pw.flush();
                sb.append(" [ ").append(sw.toString()).append(" ]");
            } catch (final IOException ignore) {}
            return sb.toString();
        };

        if (Constants.TRUE.equalsIgnoreCase(ComponentUtil.getFessConfig().getApiJsonResponseExceptionIncluded())) {
            return stacktraceString.get();
        }

        final String errorCode = UUID.randomUUID().toString();
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] {}", errorCode, stacktraceString.get().replace("\n", "\\n"));
        } else {
            logger.warn("[{}] {}", errorCode, cause.getMessage());
        }
        return "error_code:" + errorCode;
    }
}
