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
package org.codelibs.fess.api.v2.handlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the {@code /api/v2/documents/all} endpoint — streaming scroll search.
 *
 * <p>Mirrors v1's {@code SearchApiManager#processScrollSearchRequest} but emits
 * NDJSON ({@code application/x-ndjson; charset=UTF-8}) where each line is a
 * {@code {"data":{...}}} envelope wrapping one filtered document. The wrapper
 * adds a stable "data" key per line so v2 callers can branch on the same field
 * name they get back from the non-streaming {@code /search} endpoint without
 * special-casing the scroll path.</p>
 *
 * <p>Unlike the other v2 handlers this one does <em>not</em> emit a JSON
 * envelope on the success path — the response body is a stream of documents,
 * not a single object. Error paths still go through {@link V2EnvelopeWriter}
 * so client SDKs see structured failures in the usual shape.</p>
 *
 * <p><strong>Wire contract — mid-stream error terminator (MJ-22):</strong>
 * When an exception is thrown after at least one NDJSON line has been written,
 * the handler emits a final NDJSON record:
 * {@code {"error":{"code":"internal_error","message":"stream error"}}\n}
 * and then flushes. Clients MUST check whether the final line contains an
 * {@code "error"} key to distinguish "stream complete" from "server crashed
 * mid-stream". If the final-line write itself fails, the stream terminates
 * without a terminator — this is unavoidable and the client must treat
 * unexpected EOF as an error.</p>
 *
 * <p><strong>Referer allowlist (MJ-21):</strong> v1's {@code SearchApiManager}
 * enforced {@code isAcceptedSearchReferer} (~line 860) as a browser-driven
 * scraping defence. v2 omits this check deliberately: idempotent GETs carry
 * no side-effects that require Referer gating. CSRF protection covers
 * state-changing endpoints. See {@link SearchHandler} for the same rationale.</p>
 *
 * <p>The handler is stateless; the manager holds a single shared instance and
 * dispatches concurrent requests through it without locking. {@link #MAPPER}
 * is a {@code static final} instance because {@code ObjectMapper} is thread-safe
 * after configuration (m-11).</p>
 */
public class ScrollSearchHandler {

    private static final Logger logger = LogManager.getLogger(ScrollSearchHandler.class);

    private static final String NDJSON_CONTENT_TYPE = "application/x-ndjson; charset=UTF-8";

    /**
     * Default constructor. The handler is stateless and intended to be
     * instantiated once by the API manager and shared across concurrent requests.
     */
    public ScrollSearchHandler() {
        // no-op
    }

    /**
     * Shared, thread-safe {@link ObjectMapper} instance (m-11).
     * {@code ObjectMapper} is safe to share after construction; hoisting here
     * avoids per-request allocation while keeping each write session independent.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Processes one {@code /api/v2/documents/all} request.
     *
     * <p>Rejects non-{@code GET} methods and feature-disabled cases with
     * {@link V2ErrorCode#INVALID_REQUEST}. On the success path the NDJSON
     * content-type is set <em>before</em> the first write so chunked clients
     * see the type at the start of the stream; the type is not reset between
     * lines.</p>
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response to write to
     * @throws IOException if writing the envelope or any NDJSON line fails
     */
    public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "GET");
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isApiSearchScroll()) {
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.INVALID_REQUEST, "scroll search is not available");
            return;
        }
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, Constants.SEARCH_LOG_ACCESS_TYPE_JSON);
        // Tracks whether at least one NDJSON line was flushed. If an error occurs after
        // the first write, we cannot emit a JSON error envelope without corrupting the
        // in-flight NDJSON stream — so we just log and return.
        final boolean[] wroteAnyLine = { false };
        try {
            final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
            final QueryFieldConfig queryFieldConfig = ComponentUtil.getQueryFieldConfig();
            response.setContentType(NDJSON_CONTENT_TYPE);
            final PrintWriter writer = response.getWriter();
            final V2JsonRequestParams params = new V2JsonRequestParams(request, fessConfig);
            final long count = searchHelper.scrollSearch(params, doc -> {
                final Map<String, Object> filtered = filterDoc(doc, queryFieldConfig);
                final Map<String, Object> line = new LinkedHashMap<>();
                line.put("data", filtered);
                try {
                    // Serialize to a String first, then write. Passing the servlet writer to
                    // MAPPER.writeValue(Writer, …) closes it after the first line — Jackson's
                    // JsonGenerator.Feature.AUTO_CLOSE_TARGET defaults to true, so the generator's
                    // close() closes the underlying writer. Once closed, PrintWriter silently
                    // swallows every subsequent write (it never throws, only sets an error flag),
                    // so only the first document would reach the client while the scroll still
                    // iterates all N matching documents.
                    writer.write(MAPPER.writeValueAsString(line));
                    writer.write('\n');
                    wroteAnyLine[0] = true;
                } catch (final IOException e) {
                    throw new UncheckedIOException(e);
                }
                return true;
            }, OptionalThing.empty());
            response.flushBuffer();
            if (logger.isDebugEnabled()) {
                logger.debug("Loaded {} documents", count);
            }
        } catch (final InvalidRequestParameterException e) {
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.INVALID_REQUEST, e.getMessage());
        } catch (final InvalidQueryException | ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("invalid /api/v2/documents/all request", e);
            }
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.INVALID_REQUEST, e.getMessage());
        } catch (final UncheckedIOException e) {
            // Surface the underlying IOException so the servlet container can log/respond.
            throw e.getCause();
        } catch (final Exception e) {
            if (wroteAnyLine[0]) {
                // Response body is partial NDJSON — we cannot emit a JSON error envelope
                // without corrupting the stream. Per the wire contract (MJ-22), emit a
                // final NDJSON error-terminator line so clients can distinguish
                // "stream complete" from "server crashed mid-stream".
                logger.warn("/api/v2/documents/all failed after partial write", e);
                try {
                    final PrintWriter w = response.getWriter();
                    final Map<String, Object> errLine = new LinkedHashMap<>();
                    final Map<String, Object> errBody = new LinkedHashMap<>();
                    errBody.put("code", "internal_error");
                    errBody.put("message", "stream error");
                    errLine.put("error", errBody);
                    // Serialize first (see the success-path comment above): writeValue(Writer,…)
                    // would close the writer before the trailing newline could be written.
                    w.write(MAPPER.writeValueAsString(errLine));
                    w.write('\n');
                    response.flushBuffer();
                } catch (final Exception flushEx) {
                    logger.warn("/api/v2/documents/all: could not write error terminator", flushEx);
                }
            } else {
                ComponentUtil.getV2EnvelopeWriter().writeInternalError(response, e, logger, "/api/v2/documents/all");
            }
        }
    }

    /**
     * Filters a single document down to fields {@link QueryFieldConfig#isApiResponseField}
     * marks as safe for API exposure, mirroring {@code SearchHandler#filterDocuments}.
     *
     * <p>Drops blank keys and {@code null} values so the wire payload size stays
     * stable across versions.</p>
     *
     * @param doc the raw document map (never {@code null})
     * @param cfg the active query-field configuration
     * @return a new, order-preserved map of fields safe to emit
     */
    private Map<String, Object> filterDoc(final Map<String, Object> doc, final QueryFieldConfig cfg) {
        final Map<String, Object> filtered = new LinkedHashMap<>();
        for (final Map.Entry<String, Object> e : doc.entrySet()) {
            final String name = e.getKey();
            if (StringUtil.isNotBlank(name) && e.getValue() != null && cfg.isApiResponseField(name)) {
                filtered.put(name, e.getValue());
            }
        }
        return filtered;
    }
}
