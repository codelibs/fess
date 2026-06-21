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

    /**
     * Default constructor used by the DI container.
     */
    public HealthHandler() {
        // default constructor
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
            final PingResponse ping = ComponentUtil.getSearchEngineClient().ping();
            final String clusterStatus = ping.getClusterStatus();
            final Map<String, Object> engine = new LinkedHashMap<>();
            engine.put("cluster_name", ping.getClusterName());
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
            ComponentUtil.getV2EnvelopeWriter().writeInternalError(response, e, logger, "/api/v2/health");
        }
    }
}
