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
import org.codelibs.fess.api.v2.handlers.LogoutHandler;
import org.codelibs.fess.api.v2.handlers.MeHandler;
import org.codelibs.fess.api.v2.handlers.PasswordChangeHandler;
import org.codelibs.fess.api.v2.handlers.ScrollSearchHandler;
import org.codelibs.fess.api.v2.handlers.SearchHandler;
import org.codelibs.fess.api.v2.handlers.UiConfigHandler;

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
        final SearchApiV2Manager m = new SearchApiV2Manager();
        m.searchHandler = new SearchHandler();
        m.scrollSearchHandler = new ScrollSearchHandler();
        m.favoriteGetHandler = new FavoriteGetHandler();
        m.favoritePostHandler = new FavoritePostHandler();
        m.meHandler = new MeHandler();
        m.logoutHandler = new LogoutHandler();
        m.passwordChangeHandler = new PasswordChangeHandler();
        m.uiConfigHandler = new UiConfigHandler();
        m.clickHandler = new ClickHandler();
        m.chatHandler = new ChatHandler();
        m.chatStreamHandler = new ChatStreamHandler();
        m.chatSessionClearHandler = new ChatSessionClearHandler();
        m.cacheHandler = new CacheHandler();
        m.loginHandler = new LoginHandler();
        return m;
    }
}
