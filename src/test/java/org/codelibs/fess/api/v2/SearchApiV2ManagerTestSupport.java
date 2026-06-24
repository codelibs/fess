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

import org.codelibs.fess.api.v2.handlers.CacheHandler;
import org.codelibs.fess.api.v2.handlers.ChatHandler;
import org.codelibs.fess.api.v2.handlers.ChatSessionClearHandler;
import org.codelibs.fess.api.v2.handlers.ChatStreamHandler;
import org.codelibs.fess.api.v2.handlers.ClickHandler;
import org.codelibs.fess.api.v2.handlers.FavoriteGetHandler;
import org.codelibs.fess.api.v2.handlers.FavoritePostHandler;
import org.codelibs.fess.api.v2.handlers.LoginHandler;
import org.codelibs.fess.api.v2.handlers.FavoritesListHandler;
import org.codelibs.fess.api.v2.handlers.HealthHandler;
import org.codelibs.fess.api.v2.handlers.LabelsHandler;
import org.codelibs.fess.api.v2.handlers.LogoutHandler;
import org.codelibs.fess.api.v2.handlers.MeHandler;
import org.codelibs.fess.api.v2.handlers.OriginValidator;
import org.codelibs.fess.api.v2.handlers.PasswordChangeHandler;
import org.codelibs.fess.api.v2.handlers.PopularWordsHandler;
import org.codelibs.fess.api.v2.handlers.ScrollSearchHandler;
import org.codelibs.fess.api.v2.handlers.SearchHandler;
import org.codelibs.fess.api.v2.handlers.SuggestWordsHandler;
import org.codelibs.fess.api.v2.handlers.TargetOriginResolver;
import org.codelibs.fess.api.v2.handlers.UiConfigHandler;
import org.codelibs.fess.cors.CorsHandlerFactory;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Test support factory for {@link SearchApiV2Manager}.
 *
 * <p>Since the handler fields in {@code SearchApiV2Manager} are pure {@code @Resource}
 * injection points (no inline {@code = new XxxHandler()} defaults), unit tests that
 * construct the manager directly via {@code new SearchApiV2Manager()} will have
 * {@code null} handler fields. This helper mirrors what the Lasta DI container does
 * at runtime by setting all 14 handler fields to freshly constructed instances.</p>
 *
 * <p>Tests that need to override a specific handler (e.g. to inject a stub) should
 * call {@link #newManagerWithHandlers()} and then assign the field directly — the
 * helper sets the default first and the test's own assignment overrides it.</p>
 */
final class SearchApiV2ManagerTestSupport {

    private SearchApiV2ManagerTestSupport() {
    }

    /**
     * Creates a {@link SearchApiV2Manager} with all 14 handler fields set to freshly
     * constructed instances. This mirrors the Lasta DI container injection for unit
     * tests that construct the manager directly, since {@code SearchApiV2Manager}'s
     * handler fields are now pure {@code @Resource} injection points with no inline
     * defaults.
     *
     * @return a fully initialised {@code SearchApiV2Manager} ready for unit testing
     */
    static SearchApiV2Manager newManagerWithHandlers() {
        // The v2 CSRF Origin layer (SearchApiV2Manager.process) resolves the CORS
        // allow list via ComponentUtil.getCorsHandlerFactory() for unsafe methods.
        // The unit container does not auto-register it, so mirror the DI wiring with
        // an empty factory (no EXACT/wildcard entries) unless a test registered its own.
        if (!ComponentUtil.hasComponent("corsHandlerFactory")) {
            ComponentUtil.register(new CorsHandlerFactory(), "corsHandlerFactory");
        }
        final SearchApiV2Manager m = new SearchApiV2Manager();
        m.searchHandler = new SearchHandler();
        m.scrollSearchHandler = new ScrollSearchHandler();
        m.favoriteGetHandler = new FavoriteGetHandler();
        m.favoritePostHandler = new FavoritePostHandler();
        m.favoritesListHandler = new FavoritesListHandler();
        m.meHandler = new MeHandler();
        m.logoutHandler = new LogoutHandler();
        m.passwordChangeHandler = new PasswordChangeHandler();
        m.uiConfigHandler = new UiConfigHandler();
        m.clickHandler = new ClickHandler();
        m.chatHandler = new ChatHandler();
        m.chatStreamHandler = new ChatStreamHandler();
        m.chatSessionClearHandler = new ChatSessionClearHandler();
        m.cacheHandler = new CacheHandler();
        m.healthHandler = new HealthHandler();
        m.suggestWordsHandler = new SuggestWordsHandler();
        m.labelsHandler = new LabelsHandler();
        m.popularWordsHandler = new PopularWordsHandler();
        m.loginHandler = new LoginHandler();
        // The Origin layer (SearchApiV2Manager.process) calls originValidator.isAllowed(...)
        // for unsafe methods. Mirror the DI wiring: a real OriginValidator whose
        // targetOriginResolver reconstructs the same-origin set from the servlet request
        // (server.origins empty + untrusted peer in the unit harness).
        m.originValidator = newOriginValidator();
        return m;
    }

    /**
     * Builds an {@link OriginValidator} with a {@link TargetOriginResolver} injected,
     * mirroring the Lasta DI {@code @Resource} wiring for unit tests that construct the
     * validator outside the container.
     *
     * @return a fully wired {@code OriginValidator}
     */
    static OriginValidator newOriginValidator() {
        final OriginValidator validator = new OriginValidator();
        try {
            final java.lang.reflect.Field field = OriginValidator.class.getDeclaredField("targetOriginResolver");
            field.setAccessible(true);
            field.set(validator, new TargetOriginResolver());
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to wire TargetOriginResolver into OriginValidator", e);
        }
        return validator;
    }
}
