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

/**
 * Static decision table for the {@code login.required} gate on the {@code /api/v2} surface.
 *
 * <p>{@code login.required} promises that search requires a login. The v1 actions honor it
 * ({@code SearchAction}, {@code GoAction}, {@code CacheAction}, {@code ThumbnailAction},
 * {@code OsddAction}, {@code HelpAction} and {@code RootAction} all redirect to the login page),
 * but on the v2 surface only {@code /api/v2/cache/{docId}} did. Every other v2 endpoint answered
 * anonymously, so {@code GET /api/v2/search} returned every document carrying the guest role —
 * and {@code role.search.default.display.permissions} seeds exactly that role into each crawl
 * configuration created from the admin UI, so in a default installation that is the whole
 * corpus.</p>
 *
 * <p><strong>Default policy: a login is REQUIRED for every sub-path.</strong> Exemptions are
 * listed explicitly in {@link #requiresLogin}, so an endpoint added to
 * {@code SearchApiV2Manager.process} without a corresponding entry here inherits the safe
 * default rather than being silently reachable by anonymous callers.</p>
 *
 * <p>The exempt set is the minimum a browser needs to render the login page and authenticate:
 * the health probe, the three anonymous-capable auth endpoints and the UI configuration the
 * single-page application reads before it knows whether anyone is signed in. Note that this
 * table describes the policy only; it applies solely when {@code login.required} is enabled.</p>
 *
 * <p><strong>Maintenance contract:</strong> whenever a new endpoint is added to
 * {@code SearchApiV2Manager.process}, decide whether it is reachable before login and extend
 * {@code LoginRequirementCompleteCoverageTest} to pin that decision.</p>
 */
public class LoginRequirement {

    /**
     * Creates a login requirement evaluator. Registered as the DI component
     * {@code v2LoginRequirement} and obtained via
     * {@link org.codelibs.fess.util.ComponentUtil#getV2LoginRequirement()}.
     */
    public LoginRequirement() {
        // default constructor
    }

    /**
     * Returns whether the given v2 sub-path may only be served to an authenticated user
     * while {@code login.required} is enabled.
     *
     * <p>A {@code null} sub-path is treated as unknown and therefore gated, matching the
     * secure default applied to every path that is not explicitly exempt.</p>
     *
     * @param subPath the v2 sub-path (e.g. {@code /search}, {@code /auth/login})
     * @return {@code true} if the request must carry an authenticated user
     */
    public boolean requiresLogin(final String subPath) {
        if (subPath == null) {
            return true;
        }
        return switch (subPath) {
        // Liveness/readiness probing must stay reachable: monitoring runs without a session.
        case "/health" -> false;
        // The single-page application calls these before it can know whether anyone is signed
        // in. /auth/me answers "nobody" for an anonymous caller, and /ui/config carries the
        // login_required flag the page needs in order to show the login form at all.
        case "/auth/me", "/auth/login", "/auth/logout", "/ui/config" -> false;
        // Secure default: everything else — search, scroll, suggest, labels, popular words,
        // related queries/content, chat, click, favorites, cache and any endpoint added later
        // — is served only to an authenticated user.
        default -> true;
        };
    }
}
