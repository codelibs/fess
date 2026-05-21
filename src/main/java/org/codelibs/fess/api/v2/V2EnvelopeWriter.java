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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Writes the unified {@code /api/v2} response envelope.
 *
 * <p>All responses share a common shape:</p>
 *
 * <pre>{@code
 * { "response": { "status": <int>, "version": "v2", ...payload-or-error... } }
 * }</pre>
 *
 * <p>{@code status} follows the same convention as the v1 JSON API
 * ({@code 0} = success, {@code 1} = user error, {@code 9} = system error)
 * so existing client SDKs that already branch on this integer continue
 * to work.</p>
 */
public final class V2EnvelopeWriter {

    /** Status value indicating a successful response. */
    public static final int STATUS_OK = 0;

    /** Status value indicating a client-side / user error. */
    public static final int STATUS_USER_ERROR = 1;

    /** Status value indicating an unexpected server-side failure. */
    public static final int STATUS_SYSTEM_ERROR = 9;

    /** API version stamped into every envelope. */
    public static final String VERSION = "v2";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String CONTENT_TYPE = "application/json; charset=UTF-8";

    private V2EnvelopeWriter() {
        // Utility class — no instances.
    }

    /**
     * Writes a successful v2 envelope wrapping the supplied payload.
     *
     * <p>Does not change the HTTP status (defaults to whatever the container set,
     * normally {@code 200 OK}). Sets the response content type to
     * {@code application/json; charset=UTF-8}.</p>
     *
     * @param res the HTTP response to write to
     * @param payload key/value fields to merge into the envelope; {@code null} is treated as empty
     * @throws IOException if writing to the response fails
     */
    public static void writeSuccess(final HttpServletResponse res, final Map<String, Object> payload) throws IOException {
        res.setContentType(CONTENT_TYPE);
        final Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("status", STATUS_OK);
        envelope.put("version", VERSION);
        if (payload != null) {
            envelope.putAll(payload);
        }
        final Map<String, Object> root = Map.of("response", envelope);
        MAPPER.writeValue(res.getWriter(), root);
    }

    /**
     * Writes an error v2 envelope and sets the HTTP status to the code's default.
     *
     * <p>The envelope's integer {@code status} is set to {@link #STATUS_SYSTEM_ERROR}
     * for {@link V2ErrorCode#INTERNAL_ERROR} and {@link #STATUS_USER_ERROR} otherwise.
     * A {@code null} message is normalised to the empty string so the wire shape stays
     * predictable.</p>
     *
     * @param res the HTTP response to write to
     * @param code the v2 error code; its default HTTP status is applied to the response
     * @param message a human-readable message safe to expose to API callers
     * @throws IOException if writing to the response fails
     */
    public static void writeError(final HttpServletResponse res, final V2ErrorCode code, final String message) throws IOException {
        res.setStatus(code.defaultHttpStatus());
        res.setContentType(CONTENT_TYPE);
        final Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", code.code());
        err.put("message", message == null ? "" : message);
        final int statusValue = code == V2ErrorCode.INTERNAL_ERROR ? STATUS_SYSTEM_ERROR : STATUS_USER_ERROR;
        final Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("status", statusValue);
        envelope.put("version", VERSION);
        envelope.put("error", err);
        final Map<String, Object> root = Map.of("response", envelope);
        MAPPER.writeValue(res.getWriter(), root);
    }
}
