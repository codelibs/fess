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

import org.apache.logging.log4j.Logger;
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
     * Reserved keys that the envelope always owns and that a caller-supplied payload must
     * not contain. If a payload map contains either key the envelope's {@code status} integer
     * or {@code version} string would be silently overwritten, breaking the wire contract for
     * every client that branches on those fields.
     *
     * <p>Call sites that accidentally include these keys represent a programming error, so
     * the method throws {@link IllegalStateException} rather than silently discarding or
     * overriding the value — this surfaces the mistake at development/test time rather than
     * in production.</p>
     */
    private static final java.util.Set<String> RESERVED_KEYS = java.util.Set.of("status", "version");

    /**
     * Writes a successful v2 envelope wrapping the supplied payload.
     *
     * <p>Does not change the HTTP status (defaults to whatever the container set,
     * normally {@code 200 OK}). Sets the response content type to
     * {@code application/json; charset=UTF-8}.</p>
     *
     * <p><strong>Reserved keys:</strong> the payload must not contain the keys
     * {@code "status"} or {@code "version"}; both are owned by the envelope itself.
     * Passing a map that contains either key will throw {@link IllegalStateException}
     * so the mistake is caught at development/test time.</p>
     *
     * @param res the HTTP response to write to
     * @param payload key/value fields to merge into the envelope; {@code null} is treated as empty
     * @throws IOException if writing to the response fails
     * @throws IllegalStateException if {@code payload} contains a reserved envelope key
     *         ({@code "status"} or {@code "version"})
     */
    public static void writeSuccess(final HttpServletResponse res, final Map<String, Object> payload) throws IOException {
        if (payload != null) {
            for (final String reserved : RESERVED_KEYS) {
                if (payload.containsKey(reserved)) {
                    throw new IllegalStateException(
                            "payload must not contain reserved envelope key '" + reserved + "': use a different key name");
                }
            }
        }
        res.setCharacterEncoding("UTF-8");
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
        if (res.isCommitted()) {
            return;
        }
        res.setStatus(code.defaultHttpStatus());
        res.setCharacterEncoding("UTF-8");
        res.setContentType(CONTENT_TYPE);
        final Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", code.code());
        err.put("message", message == null ? "" : message);
        final int statusValue = code.defaultHttpStatus() >= 500 ? STATUS_SYSTEM_ERROR : STATUS_USER_ERROR;
        final Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("status", statusValue);
        envelope.put("version", VERSION);
        envelope.put("error", err);
        final Map<String, Object> root = Map.of("response", envelope);
        MAPPER.writeValue(res.getWriter(), root);
    }

    /**
     * Convenience method for internal error paths.
     *
     * <p>Safety policy: the cause message is never written to the wire. Instead,
     * it is logged via the caller-supplied logger so the detail is available in
     * server logs without leaking connection strings, stack fragments or other
     * server-internal information to API consumers. If the response is already
     * committed (e.g. SSE or NDJSON handlers that flushed before the failure),
     * the method returns silently to avoid corrupting the in-flight body.</p>
     *
     * @param res the HTTP response to write to
     * @param cause the cause to log (may be null; only logged, never written to wire)
     * @param logger the caller's logger to use for WARN-level logging
     * @param contextTag a brief tag identifying the call site (e.g. "/api/v2/health")
     * @throws IOException if writing the envelope fails
     */
    public static void writeInternalError(final HttpServletResponse res, final Throwable cause, final Logger logger,
            final String contextTag) throws IOException {
        if (cause != null) {
            logger.warn("v2 internal error: {}", contextTag, cause);
        } else {
            logger.warn("v2 internal error: {}", contextTag);
        }
        if (res.isCommitted()) {
            return;
        }
        writeError(res, V2ErrorCode.INTERNAL_ERROR, "internal error");
    }
}
