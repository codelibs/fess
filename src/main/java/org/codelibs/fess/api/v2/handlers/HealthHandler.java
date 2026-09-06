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
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.entity.PingResponse;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the {@code /api/v2/health} endpoint.
 */
public class HealthHandler {

    private static final Logger logger = LogManager.getLogger(HealthHandler.class);

    /** The engine status reported when the search engine cannot be reached at all. */
    protected static final String UNAVAILABLE_STATUS = "UNAVAILABLE";

    /** The ping status reported when there was no response to read one from. */
    protected static final int PING_STATUS_ERROR = 1;

    /**
     * Default constructor used by the DI container.
     */
    public HealthHandler() {
        // default constructor
    }

    /**
     * Asks the search engine for its cluster health. Separated so a test can drive the
     * unreachable path without a search engine.
     *
     * @return the ping response
     */
    protected PingResponse ping() {
        return ComponentUtil.getSearchEngineClient().ping();
    }

    /**
     * Processes one health check request.
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "GET");
            ComponentUtil.getV2EnvelopeWriter().writeError(response, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        try {
            final PingResponse ping = ping();
            final String clusterStatus = ping.getClusterStatus();
            final Map<String, Object> engine = new LinkedHashMap<>();
            // The OpenSearch cluster name is intentionally omitted from this anonymous endpoint
            // to avoid leaking deployment metadata; only the coarse status and ping_status are
            // surfaced. Operators that need cluster details use the authenticated admin API.
            engine.put("status", clusterStatus);
            engine.put("ping_status", ping.getStatus());
            if ("red".equalsIgnoreCase(clusterStatus)) {
                ComponentUtil.getV2EnvelopeWriter()
                        .writeErrorWithDetails(response, V2ErrorCode.SERVICE_UNAVAILABLE, "search engine cluster is red",
                                Map.of("engine", engine));
            } else {
                ComponentUtil.getV2EnvelopeWriter().writeSuccess(response, Map.of("engine", engine));
            }
        } catch (final Exception e) {
            // The engine could not be reached at all. Fess cannot answer a search either way,
            // so this is reported like a red cluster rather than as an internal error: the
            // documented response set for this endpoint has no 500, and a monitoring client
            // reading error.details.engine has nothing to show when the snapshot is dropped.
            logger.warn("/api/v2/health: the search engine is not reachable", e);
            final Map<String, Object> engine = new LinkedHashMap<>();
            engine.put("status", UNAVAILABLE_STATUS);
            engine.put("ping_status", PING_STATUS_ERROR);
            ComponentUtil.getV2EnvelopeWriter()
                    .writeErrorWithDetails(response, V2ErrorCode.SERVICE_UNAVAILABLE, "search engine is not reachable",
                            Map.of("engine", engine));
        }
    }
}
