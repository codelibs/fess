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
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.api.v2.V2EnvelopeWriter;
import org.codelibs.fess.api.v2.V2ErrorCode;
import org.codelibs.fess.helper.SessionCsrfTokenManager;
import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.theme.Theme;
import org.codelibs.fess.theme.ThemeManifest;
import org.codelibs.fess.theme.ThemeRegistry;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Handles {@code GET /api/v2/ui/config}.
 *
 * <p>Returns the SPA bootstrap payload: site name, login requirement, supported
 * locales, the active theme descriptor, feature flags, paging defaults, and a
 * freshly-issued CSRF token. The endpoint is GET-only and CSRF-exempt because
 * it is the canonical way for an unauthenticated client to obtain a token in
 * the first place.</p>
 *
 * <p>The handler is defensive: a missing {@link ThemeRegistry} or any unexpected
 * exception during payload assembly is caught and emitted as a structured
 * envelope ({@code status:9, error.code:"internal_error"}) so the SPA always
 * sees a parseable response — never a leaked stack trace.</p>
 */
public class UiConfigHandler {

    private static final Logger logger = LogManager.getLogger(UiConfigHandler.class);

    /**
     * Default constructor. The handler is stateless and intended to be
     * instantiated once by the API manager and shared across concurrent requests.
     */
    public UiConfigHandler() {
        // no-op
    }

    /**
     * Processes one {@code /api/v2/ui/config} GET request.
     *
     * <p>Rejects non-{@code GET} methods with {@link V2ErrorCode#METHOD_NOT_ALLOWED}.
     * Otherwise assembles the SPA bootstrap payload — site name, login
     * requirement, supported locales, active theme descriptor, feature flags,
     * paging defaults, and (when {@code theme.api.csrf.required=true}) a
     * freshly-issued CSRF token bound to the session — and writes it via the
     * standard v2 envelope. Any unexpected exception is caught and emitted as
     * a structured {@code internal_error} envelope.</p>
     *
     * @param req the incoming HTTP request
     * @param res the HTTP response to write to
     * @throws IOException if writing the envelope fails
     */
    public void handle(final HttpServletRequest req, final HttpServletResponse res) throws IOException {
        if (!"GET".equalsIgnoreCase(req.getMethod())) {
            res.setHeader("Allow", "GET");
            V2EnvelopeWriter.writeError(res, V2ErrorCode.METHOD_NOT_ALLOWED, "method not allowed");
            return;
        }
        try {
            final FessConfig cfg = ComponentUtil.getFessConfig();

            // Resolve the active theme via the registry; the registry may be unregistered
            // in some environments (notably stripped-down unit harnesses), so absence is
            // treated as "no theme" rather than an error.
            final Map<String, Object> themePayload = new LinkedHashMap<>();
            Optional<Theme> activeTheme = Optional.empty();
            try {
                final VirtualHostHelper vhost = ComponentUtil.getVirtualHostHelper();
                final String vhostKey = vhost == null ? null : vhost.getVirtualHostKey();
                final ThemeRegistry registry = ComponentUtil.getComponent(ThemeRegistry.class);
                if (registry != null) {
                    activeTheme = registry.resolveActiveTheme(vhostKey);
                    activeTheme.ifPresent(t -> {
                        themePayload.put("name", t.getName());
                        themePayload.put("type", t.getType().name().toLowerCase());
                        t.getManifest().ifPresent(m -> {
                            themePayload.put("display_name", m.getDisplayName());
                            themePayload.put("version", m.getVersion());
                            themePayload.put("supported_locales", m.getSupportedLocales());
                        });
                    });
                }
            } catch (final Exception themeEx) {
                logger.warn("Theme resolution failed; emitting empty theme payload", themeEx);
                // fall through with empty themePayload — the "theme" key is always present.
            }

            // Site name resolution: prefer the active theme manifest's display_name when
            // a theme is bound to the current request; otherwise fall back to the literal
            // "Fess". We intentionally do not introduce a new fess_config key here.
            final String siteName = activeTheme.flatMap(Theme::getManifest).map(ThemeManifest::getDisplayName).orElse("Fess");

            // Feature flags mirror what the legacy JSPs branch on so the SPA can light up
            // the same set of UI affordances without round-tripping more endpoints.
            final Map<String, Object> features = new LinkedHashMap<>();
            features.put("user_favorite", cfg.isUserFavorite());
            features.put("popular_word", cfg.isWebApiPopularWord());
            features.put("suggest_search_log", cfg.isSuggestSearchLog());
            features.put("suggest_documents", cfg.isSuggestDocuments());
            features.put("login_required", cfg.isLoginRequired());

            // Server-wide supported language list, surfaced as plain JSON array of codes.
            final String[] langs = cfg.getSupportedLanguagesAsArray();

            // m-14: expose whether CSRF is required so the SPA can decide whether to
            // send the X-Fess-CSRF-Token header. When csrf_required=false the SPA MAY
            // omit the header; when true it MUST include it for all state-changing
            // requests. The csrf_token field is only populated when csrf_required=true
            // (empty string otherwise) to avoid issuing a session-bound token that the
            // SPA will never use.
            final boolean csrfRequired = cfg.isThemeApiCsrfRequired();
            String csrfToken = "";
            if (csrfRequired) {
                // Issue (or echo) a CSRF token so the SPA can immediately POST. Using
                // getSession(true) ensures the session exists even on the very first request.
                final HttpSession session = req.getSession(true);
                final SessionCsrfTokenManager csrf = ComponentUtil.getComponent(SessionCsrfTokenManager.class);
                csrfToken = csrf == null ? "" : csrf.issue(session);
            }

            final Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("site_name", siteName);
            payload.put("login_required", cfg.isLoginRequired());
            payload.put("locales", langs == null ? java.util.List.of() : Arrays.asList(langs));
            payload.put("theme", themePayload);
            payload.put("features", features);
            payload.put("page_size_default", cfg.getPagingSearchPageSizeAsInteger());
            payload.put("page_size_max", cfg.getPagingSearchPageMaxSizeAsInteger());
            payload.put("csrf_required", csrfRequired);
            payload.put("csrf_token", csrfToken);
            V2EnvelopeWriter.writeSuccess(res, payload);
        } catch (final Exception e) {
            V2EnvelopeWriter.writeInternalError(res, e, logger, "/api/v2/ui/config");
        }
    }
}
