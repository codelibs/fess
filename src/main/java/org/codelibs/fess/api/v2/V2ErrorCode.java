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
package org.codelibs.fess.api.v2;

/**
 * Canonical error codes for the /api/v2 response envelope.
 *
 * <p>Each constant carries a stable wire code (snake_case string) and a default
 * HTTP status. The wire code is what API consumers branch on; the HTTP status
 * is a sensible default that individual handlers may override before writing
 * the envelope if a more specific code is appropriate.</p>
 */
public enum V2ErrorCode {

    /** Malformed or otherwise unprocessable request. */
    INVALID_REQUEST("invalid_request", 400),

    /** The endpoint requires authentication and none was supplied. */
    AUTH_REQUIRED("auth_required", 401),

    /** The caller is authenticated but not permitted to perform the action. */
    FORBIDDEN("forbidden", 403),

    /** The requested resource or endpoint does not exist. */
    NOT_FOUND("not_found", 404),

    /** The HTTP method is not supported for this endpoint. */
    CONFLICT("conflict", 409),

    /** The HTTP method is not supported for this endpoint. */
    METHOD_NOT_ALLOWED("method_not_allowed", 405),

    /** The caller has exceeded a rate or quota threshold. */
    RATE_LIMITED("rate_limited", 429),

    /** Unhandled server-side failure. */
    INTERNAL_ERROR("internal_error", 500);

    private final String code;
    private final int httpStatus;

    V2ErrorCode(final String code, final int httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

    /**
     * Returns the stable wire code emitted in the {@code error.code} field of the v2 envelope.
     *
     * @return the snake_case wire code
     */
    public String code() {
        return code;
    }

    /**
     * Returns the default HTTP status code associated with this error.
     *
     * @return the default HTTP status code
     */
    public int defaultHttpStatus() {
        return httpStatus;
    }
}
