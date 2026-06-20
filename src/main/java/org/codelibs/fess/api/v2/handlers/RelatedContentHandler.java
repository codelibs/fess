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
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.helper.RelatedContentHelper;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles {@code GET /api/v2/related-content}.
 *
 * <p>Returns related HTML content for the given {@code q} parameter by delegating to
 * {@link RelatedContentHelper#getRelatedContents(String)}. When multiple content items
 * are returned they are joined with a newline separator. When {@code q} is blank or
 * absent the response contains an empty {@code content} string. The response shape
 * is always a success envelope with {@code status:0}:</p>
 *
 * <pre>{@code
 * { "status": 0, "content": "<html>...</html>", "content_type": "html" }
 * }</pre>
 *
 * <p>This handler is GET-only; other methods are rejected with
 * {@link V2ErrorCode#METHOD_NOT_ALLOWED}.</p>
 */
public class RelatedContentHandler {

    private static final Logger logger = LogManager.getLogger(RelatedContentHandler.class);

    /**
     * Default constructor. The handler is stateless and intended to be
     * instantiated once by the API manager and shared across concurrent requests.
     */
    public RelatedContentHandler() {
        // no-op
    }

    /**
     * Processes one {@code GET /api/v2/related-content} request.
     *
     * <p>Reads the {@code q} query parameter, delegates to
     * {@link RelatedContentHelper#getRelatedContents(String)}, and writes the
     * result as a success envelope. Multiple content items are joined with a
     * newline. An empty or absent {@code q} yields an empty {@code content}
     * string rather than an error.</p>
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
            final String content;
            if (q == null || q.isBlank()) {
                content = StringUtil.EMPTY;
            } else {
                final RelatedContentHelper helper = ComponentUtil.getRelatedContentHelper();
                final String[] results = helper.getRelatedContents(q);
                content = results == null || results.length == 0 ? StringUtil.EMPTY : String.join("\n", results);
            }
            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("content", content);
            payload.put("content_type", "html");
            ComponentUtil.getV2EnvelopeWriter().writeSuccess(res, payload);
        } catch (final Exception e) {
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(res, e, logger, "/api/v2/related-content");
        }
    }
}
