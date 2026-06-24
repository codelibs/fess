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

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.cors.CorsMatchType;
import org.codelibs.fess.cors.CorsResolution;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.OriginUtil;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Baseline same-origin / allow-listed-origin decision for the v2 CSRF Origin
 * check. Registered as a Lasta DI component ({@code originValidator}) so the
 * policy is replaceable; the trusted self-origin set is resolved through the
 * injected {@link TargetOriginResolver} and the CORS allow list is consulted via
 * {@link ComponentUtil}.
 */
public class OriginValidator {

    private static final Logger logger = LogManager.getLogger(OriginValidator.class);

    /** Resolves the canonical self-origin set used for the same-origin comparison. */
    @Resource
    protected TargetOriginResolver targetOriginResolver;

    /**
     * Default constructor. The validator is stateless and intended to be
     * instantiated once by the DI container and shared across concurrent requests.
     */
    public OriginValidator() {
        // no-op
    }

    /**
     * Validates the source origin of a state-changing request.
     *
     * <p>Algorithm:</p>
     * <ol>
     * <li>Resolve the trusted self-origin set via {@link TargetOriginResolver#resolve}.</li>
     * <li>Read the {@code Origin} header. If non-blank, it is the source — the
     * {@code Referer} is NOT consulted as a fallback (a same-origin Referer must
     * not rescue a cross-site Origin). If {@code Origin} is blank/absent, read
     * {@code Referer} as the source.</li>
     * <li>Canonicalize the source via {@link OriginUtil#canonicalize}. If the
     * result is {@code null} (both headers absent or unparseable) → allow
     * (non-browser API client compatibility; browser-driven cross-site state
     * changes always carry an Origin).</li>
     * <li>If the trusted self-origin set contains the canonical source → allow.</li>
     * <li>If the CORS allow list resolves the source to an {@link CorsMatchType#EXACT}
     * match → allow. {@code resolve()} is used (never {@code get()}) so a {@code "*"}
     * wildcard fallback is never silently trusted.</li>
     * <li>Otherwise (cross-site, {@code Origin: null}, malformed value) → reject.</li>
     * </ol>
     *
     * @param request the request; only the {@code Origin}/{@code Referer} headers are read
     * @return {@code true} if the request may proceed, {@code false} if it is blocked as cross-site
     */
    public boolean isAllowed(final HttpServletRequest request) {
        final Set<String> trustedSameOrigins = targetOriginResolver.resolve(request);

        final String origin = request.getHeader("Origin");
        final String rawSource;
        final boolean fromOrigin;
        if (StringUtil.isNotBlank(origin)) {
            // Origin present (non-blank): do NOT fall back to Referer. A same-origin
            // Referer must not rescue a cross-site Origin.
            rawSource = origin;
            fromOrigin = true;
        } else {
            // A blank/absent Origin is treated as absent; consult the Referer.
            rawSource = request.getHeader("Referer");
            fromOrigin = false;
        }

        final String source = OriginUtil.canonicalize(rawSource);
        if (source == null) {
            if (fromOrigin) {
                // A non-blank Origin that fails to canonicalize is a present but
                // malformed/opaque origin ("null", multi-value, control chars,
                // unparseable). This is a cross-site signal from a real browser, not
                // a missing header — reject.
                if (logger.isDebugEnabled()) {
                    logger.debug("v2 origin check: blocked request with present but unusable Origin header");
                }
                return false;
            }
            // Origin absent/blank and Referer absent or unparseable. Browser-driven
            // cross-site state changes always carry an Origin, so a missing source is
            // a non-browser API client; allow for compatibility.
            if (logger.isDebugEnabled()) {
                logger.debug("v2 origin check: no usable source origin; allowing (non-browser client)");
            }
            return true;
        }

        if (trustedSameOrigins.contains(source)) {
            return true;
        }

        final CorsResolution resolution = ComponentUtil.getCorsHandlerFactory().resolve(source);
        if (resolution != null && resolution.getMatchType() == CorsMatchType.EXACT) {
            // Explicit allow-listed origin (credentialed SPA). WILDCARD is never trusted.
            return true;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("v2 origin check: blocked cross-site request (source present, not trusted)");
        }
        return false;
    }
}
