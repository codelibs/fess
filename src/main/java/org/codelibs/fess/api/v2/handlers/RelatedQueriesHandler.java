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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.helper.RelatedQueryHelper;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles {@code GET /api/v2/related-queries}.
 *
 * <p>Returns related query terms for the given {@code q} parameter by delegating to
 * {@link RelatedQueryHelper#getRelatedQueries(String)}. When {@code q} is blank or
 * absent the response contains an empty {@code queries} array. The response shape
 * is always a success envelope with {@code status:0}:</p>
 *
 * <pre>{@code
 * { "status": 0, "queries": ["related1", "related2", ...] }
 * }</pre>
 *
 * <p>This handler is GET-only; other methods are rejected with
 * {@link V2ErrorCode#METHOD_NOT_ALLOWED}.</p>
 */
public class RelatedQueriesHandler {

    private static final Logger logger = LogManager.getLogger(RelatedQueriesHandler.class);

    /**
     * Default constructor. The handler is stateless and intended to be
     * instantiated once by the API manager and shared across concurrent requests.
     */
    public RelatedQueriesHandler() {
        // no-op
    }

    /**
     * Processes one {@code GET /api/v2/related-queries} request.
     *
     * <p>Reads the {@code q} query parameter, delegates to
     * {@link RelatedQueryHelper#getRelatedQueries(String)}, and writes the
     * result as a success envelope. An empty or absent {@code q} yields an
     * empty {@code queries} array rather than an error.</p>
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!"GET".equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", "GET");
            ComponentUtil.getV2EnvelopeWriter().writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        try {
            final String q = req.getParameter("q");
            final List<String> queries;
            if (q == null || q.isBlank()) {
                queries = List.of();
            } else {
                final RelatedQueryHelper helper = ComponentUtil.getRelatedQueryHelper();
                final String[] result = helper.getRelatedQueries(q);
                queries = result == null ? List.of() : Arrays.asList(result);
            }
            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("queries", queries);
            ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
        } catch (final Exception e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2/related-queries");
        }
    }
}
