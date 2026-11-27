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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.util.Locale;

import org.lastaflute.web.servlet.request.SimpleRequestManager;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The request manager of Fess.
 * <p>
 * LastaFlute resolves the user locale before every action and saves it to the HTTP session, which creates a
 * session when the request has none. A call of the REST API under {@code /api/} is authenticated by its access
 * token and carries no session, so each call, including one that is refused with HTTP 401, made a new session
 * (a {@code Set-Cookie: JSESSIONID} on every response, and one stored session per call when the sessions are kept
 * in the search engine). For such a request the locale is kept for the request only: it is resolved again from the
 * request on the next call anyway. A request that already has a session, and every screen, keeps the locale in the
 * session as before.
 * </p>
 */
public class FessRequestManager extends SimpleRequestManager {

    /** The path prefix of the REST APIs that are served by actions and authenticated per request. */
    protected static final String API_PATH_PREFIX = "/api/";

    /**
     * Default constructor.
     */
    public FessRequestManager() {
        super();
    }

    @Override
    public void saveUserLocaleToSession(final Locale locale) {
        if (isApiRequest() && !hasSession()) {
            setAttribute(getReqeustUserLocaleKey(), locale);
            return;
        }
        super.saveUserLocaleToSession(locale);
    }

    /**
     * Tells whether the current request is a call of the REST API.
     *
     * @return true if the request path starts with {@code /api/}
     */
    protected boolean isApiRequest() {
        final String path = getRequestPath();
        return path != null && path.startsWith(API_PATH_PREFIX);
    }

    /**
     * Tells whether the current request already has an HTTP session, without creating one.
     *
     * @return true if a session exists
     */
    protected boolean hasSession() {
        final HttpServletRequest request = getRequest();
        return request != null && request.getSession(false) != null;
    }
}
