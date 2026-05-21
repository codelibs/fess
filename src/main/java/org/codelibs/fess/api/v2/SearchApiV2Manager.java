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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.api.BaseApiManager;
import org.codelibs.fess.entity.PingResponse;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Web API manager for the {@code /api/v2} surface.
 *
 * <p>This skeleton registers itself under the {@code /api/v2} path prefix and
 * dispatches by sub-path to specific handlers. The {@code /health} endpoint is
 * wired up here; later batches add search, suggest, auth, and theme endpoints.</p>
 *
 * <p>All responses use {@link V2EnvelopeWriter} so the wire shape stays uniform:
 * a top-level {@code {"response": {...}}} envelope with {@code status} and
 * {@code version} fields plus either a payload or an {@code error} object.</p>
 */
public class SearchApiV2Manager extends BaseApiManager {

    private static final Logger logger = LogManager.getLogger(SearchApiV2Manager.class);

    /**
     * Constructor — pins the path prefix to {@code /api/v2}.
     */
    public SearchApiV2Manager() {
        setPathPrefix("/api/v2");
    }

    /**
     * Registers this manager with the global {@link org.codelibs.fess.api.WebApiManagerFactory}
     * once Lasta DI finishes injecting dependencies.
     */
    @PostConstruct
    public void register() {
        if (logger.isInfoEnabled()) {
            logger.info("Loaded {}", this.getClass().getSimpleName());
        }
        ComponentUtil.getWebApiManagerFactory().add(this);
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        if (!ComponentUtil.getFessConfig().isWebApiJson()) {
            return false;
        }
        final String servletPath = request.getServletPath();
        return servletPath != null && servletPath.startsWith(pathPrefix);
    }

    @Override
    public void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final String sub = subPath(request);
        try {
            switch (sub) {
            case "/health" -> handleHealth(response);
            default -> V2EnvelopeWriter.writeError(response, V2ErrorCode.NOT_FOUND, "endpoint not found: " + sub);
            }
        } catch (final Exception e) {
            logger.warn("/api/v2 handler failed for {}", sub, e);
            V2EnvelopeWriter.writeError(response, V2ErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    /**
     * Extracts the portion of the servlet path after the {@code /api/v2} prefix.
     *
     * @param request the incoming HTTP request
     * @return the sub-path (e.g. {@code "/health"}) or an empty string when the
     *         request targets the prefix root itself
     */
    String subPath(final HttpServletRequest request) {
        final String p = request.getServletPath();
        if (p == null) {
            return "";
        }
        return p.length() > pathPrefix.length() ? p.substring(pathPrefix.length()) : "";
    }

    /**
     * Returns a structured snapshot of the search engine's cluster health.
     *
     * <p>Mirrors the v1 ping endpoint, but emits the v2 envelope shape and exposes
     * just the high-level fields most useful to monitoring tooling. If the engine
     * is unreachable we still emit a structured error envelope rather than letting
     * the exception escape.</p>
     *
     * @param response the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    private void handleHealth(final HttpServletResponse response) throws IOException {
        try {
            final PingResponse ping = ComponentUtil.getSearchEngineClient().ping();
            final Map<String, Object> engine = new LinkedHashMap<>();
            engine.put("cluster_name", ping.getClusterName());
            engine.put("status", ping.getClusterStatus());
            engine.put("ping_status", ping.getStatus());
            V2EnvelopeWriter.writeSuccess(response, Map.of("engine", engine));
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process /api/v2/health.", e);
            }
            V2EnvelopeWriter.writeError(response, V2ErrorCode.INTERNAL_ERROR, "engine unreachable: " + e.getMessage());
        }
    }

    @Override
    protected void writeHeaders(final HttpServletResponse response) {
        // v2 envelopes are written through V2EnvelopeWriter which sets its own Content-Type;
        // no additional response headers are required here for the skeleton.
    }
}
