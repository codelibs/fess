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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codelibs.core.collection.LruHashMap;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.core.security.PrimaryCipher;
import org.lastaflute.web.login.TypicalUserBean;
import org.lastaflute.web.servlet.session.SessionManager;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Helper class for managing user information and session tracking in Fess search system.
 * This class handles user identification through cookies, session management, and query tracking.
 * It provides functionality for generating unique user codes, managing user sessions,
 * and tracking search result document IDs for analytics and personalization.
 *
 */
public class UserInfoHelper {

    /**
     * Default constructor for UserInfoHelper.
     */
    public UserInfoHelper() {
        // Default constructor
    }

    /** The session attribute key for storing user bean information */
    protected static final String USER_BEAN = "lastaflute.action.USER_BEAN.FessUserBean";

    /** The maximum size of the result document IDs cache */
    protected int resultDocIdsCacheSize = 20;

    /** The name of the cookie used for user identification */
    protected String cookieName = "fsid";

    /** The domain for the user identification cookie */
    protected String cookieDomain;

    /** The maximum age of the user identification cookie in seconds (default: 1 month) */
    protected int cookieMaxAge = 30 * 24 * 60 * 60;// 1 month

    /** The path for the user identification cookie */
    protected String cookiePath = "/";

    /** Whether the user identification cookie should be secure (HTTPS only) */
    protected Boolean cookieSecure;

    /** Whether the user identification cookie should be HTTP-only */
    protected boolean httpOnly = true;

    /**
     * Retrieves the user code for the current request.
     * The user code is used to uniquely identify users across sessions and requests.
     * It checks multiple sources in order: request attribute, request parameter, cookie, user bean, or generates a new one.
     *
     * @return the user code string, or null if no valid session exists
     */
    public String getUserCode() {
        return LaRequestUtil.getOptionalRequest().map(request -> {
            String userCode = (String) request.getAttribute(Constants.USER_CODE);
            if (StringUtil.isNotBlank(userCode)) {
                return userCode;
            }

            userCode = getUserCodeFromRequest(request);
            if (StringUtil.isNotBlank(userCode)) {
                return userCode;
            }

            if (!request.isRequestedSessionIdValid()) {
                return null;
            }

            userCode = getUserCodeFromCookie(request);
            if (StringUtil.isBlank(userCode)) {
                userCode = getUserCodeFromUserBean(request);
                if (StringUtil.isBlank(userCode)) {
                    userCode = getId();
                }
            }

            if (StringUtil.isNotBlank(userCode)) {
                updateUserSessionId(userCode);
            }
            return userCode;
        }).orElse(null);
    }

    /**
     * Extracts the user code from the user bean stored in the session.
     * This method retrieves the authenticated user information and creates an encrypted user code.
     *
     * @param request the HTTP servlet request
     * @return the user code from the user bean, or null if not found or invalid
     */
    protected String getUserCodeFromUserBean(final HttpServletRequest request) {
        final SessionManager sessionManager = ComponentUtil.getComponent(SessionManager.class);
        String userCode = sessionManager.getAttribute(USER_BEAN, TypicalUserBean.class)
                .filter(u -> !Constants.EMPTY_USER_ID.equals(u.getUserId()))
                .map(u -> u.getUserId().toString())
                .orElse(StringUtil.EMPTY);
        if (StringUtil.isBlank(userCode)) {
            return null;
        }

        userCode = createUserCodeFromUserId(userCode);
        request.setAttribute(Constants.USER_CODE, userCode);
        deleteUserCodeFromCookie(request);
        return userCode;
    }

    /**
     * Creates an encrypted user code from a user ID.
     * The user ID is encrypted using the primary cipher and validated against the configuration.
     *
     * @param userCode the raw user ID to encrypt
     * @return the encrypted and validated user code, or null if invalid
     */
    protected String createUserCodeFromUserId(String userCode) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final PrimaryCipher cipher = ComponentUtil.getPrimaryCipher();
        userCode = cipher.encrypt(userCode);
        if (fessConfig.isValidUserCode(userCode)) {
            return userCode;
        }
        return null;
    }

    /**
     * Deletes the user code cookie from the client browser.
     * This method removes the user identification cookie by setting it to an empty value with zero max age.
     *
     * @param request the HTTP servlet request
     */
    public void deleteUserCodeFromCookie(final HttpServletRequest request) {
        final String cookieValue = getUserCodeFromCookie(request);
        if (cookieValue != null) {
            updateCookie(StringUtil.EMPTY, 0);
        }
    }

    /**
     * Extracts the user code from request parameters.
     * This method looks for the user code in the request parameters and validates it.
     *
     * @param request the HTTP servlet request
     * @return the user code from request parameters, or null if not found or invalid
     */
    protected String getUserCodeFromRequest(final HttpServletRequest request) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String userCode = request.getParameter(fessConfig.getUserCodeRequestParameter());
        if (StringUtil.isBlank(userCode)) {
            return null;
        }

        if (fessConfig.isValidUserCode(userCode)) {
            request.setAttribute(Constants.USER_CODE, userCode);
            return userCode;
        }
        return null;
    }

    /**
     * Generates a new unique identifier for user tracking.
     * Creates a UUID and removes hyphens to create a clean identifier string.
     *
     * @return a new unique identifier string
     */
    protected String getId() {
        return UUID.randomUUID().toString().replace("-", StringUtil.EMPTY);
    }

    /**
     * Updates the user session with the provided user code.
     * This method registers the user info with the search log helper and updates the cookie.
     *
     * @param userCode the user code to associate with the session
     */
    protected void updateUserSessionId(final String userCode) {
        ComponentUtil.getSearchLogHelper().getUserInfo(userCode);

        LaRequestUtil.getOptionalRequest().ifPresent(req -> req.setAttribute(Constants.USER_CODE, userCode));

        updateCookie(userCode, cookieMaxAge);
    }

    /**
     * Updates the user identification cookie with the specified user code and max age.
     * Configures the cookie with security settings including domain, path, secure flag, and HTTP-only flag.
     *
     * @param userCode the user code to store in the cookie
     * @param age the maximum age of the cookie in seconds
     */
    protected void updateCookie(final String userCode, final int age) {
        final Cookie cookie = new Cookie(cookieName, userCode);
        cookie.setMaxAge(age);
        cookie.setHttpOnly(httpOnly);
        if (StringUtil.isNotBlank(cookieDomain)) {
            cookie.setDomain(cookieDomain);
        }
        if (StringUtil.isNotBlank(cookiePath)) {
            cookie.setPath(cookiePath);
        }
        cookie.setSecure(isSecureCookie());
        LaResponseUtil.getResponse().addCookie(cookie);
    }

    /**
     * Determines whether the user identification cookie should be marked as secure.
     * Checks the configured secure setting or examines request headers to detect HTTPS.
     *
     * @return true if the cookie should be secure, false otherwise
     */
    protected boolean isSecureCookie() {
        if (cookieSecure != null) {
            return cookieSecure;
        }

        return LaRequestUtil.getOptionalRequest().map(req -> {
            String forwardedProto = req.getHeader("X-Forwarded-Proto");
            if ("https".equalsIgnoreCase(forwardedProto)) {
                return true;
            }
            return req.isSecure();
        }).orElse(false);
    }

    /**
     * Extracts the user code from the user identification cookie.
     * Searches through all request cookies to find the user identification cookie and validates its value.
     *
     * @param request the HTTP servlet request
     * @return the user code from the cookie, or null if not found or invalid
     */
    protected String getUserCodeFromCookie(final HttpServletRequest request) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName()) && fessConfig.isValidUserCode(cookie.getValue())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Stores the document IDs associated with a search query for tracking purposes.
     * This method caches the document IDs returned for a specific query to enable click tracking and analytics.
     *
     * @param queryId the unique identifier for the search query
     * @param documentItems the list of document maps containing search results
     */
    public void storeQueryId(final String queryId, final List<Map<String, Object>> documentItems) {
        LaRequestUtil.getOptionalRequest().map(req -> req.getSession(false)).ifPresent(session -> {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();

            final List<String> docIdList = new ArrayList<>();
            for (final Map<String, Object> map : documentItems) {
                final Object docId = map.get(fessConfig.getIndexFieldDocId());
                if (docId != null && docId.toString().length() > 0) {
                    docIdList.add(docId.toString());
                }
            }

            if (!docIdList.isEmpty()) {
                final Map<String, String[]> resultDocIdsCache = getResultDocIdsCache(session);
                resultDocIdsCache.put(queryId, docIdList.toArray(new String[docIdList.size()]));
            }
        });
    }

    /**
     * Retrieves the document IDs associated with a specific query ID.
     * Used for tracking which documents were displayed for a particular search query.
     *
     * @param queryId the unique identifier for the search query
     * @return an array of document IDs, or an empty array if not found
     */
    public String[] getResultDocIds(final String queryId) {
        return LaRequestUtil.getOptionalRequest().map(req -> req.getSession(false)).map(session -> {
            final Map<String, String[]> resultUrlCache = getResultDocIdsCache(session);
            final String[] urls = resultUrlCache.get(queryId);
            if (urls != null) {
                return urls;
            }
            return StringUtil.EMPTY_STRINGS;
        }).orElse(StringUtil.EMPTY_STRINGS);
    }

    /**
     * Retrieves or creates the result document IDs cache from the session.
     * The cache is implemented as an LRU map to limit memory usage.
     *
     * @param session the HTTP session
     * @return the result document IDs cache map
     */
    private Map<String, String[]> getResultDocIdsCache(final HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<String, String[]> resultDocIdsCache = (Map<String, String[]>) session.getAttribute(Constants.RESULT_DOC_ID_CACHE);
        if (resultDocIdsCache == null) {
            resultDocIdsCache = new LruHashMap<>(resultDocIdsCacheSize);
            session.setAttribute(Constants.RESULT_DOC_ID_CACHE, resultDocIdsCache);
        }
        return resultDocIdsCache;
    }

    /**
     * Sets the maximum size of the result document IDs cache.
     *
     * @param resultDocIdsCacheSize the maximum number of entries in the cache
     */
    public void setResultDocIdsCacheSize(final int resultDocIdsCacheSize) {
        this.resultDocIdsCacheSize = resultDocIdsCacheSize;
    }

    /**
     * Sets the name of the user identification cookie.
     *
     * @param cookieName the name to use for the user identification cookie
     */
    public void setCookieName(final String cookieName) {
        this.cookieName = cookieName;
    }

    /**
     * Sets the domain for the user identification cookie.
     *
     * @param cookieDomain the domain to use for the user identification cookie
     */
    public void setCookieDomain(final String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    /**
     * Sets the maximum age of the user identification cookie in seconds.
     *
     * @param cookieMaxAge the maximum age in seconds
     */
    public void setCookieMaxAge(final int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    /**
     * Sets the path for the user identification cookie.
     *
     * @param cookiePath the path to use for the user identification cookie
     */
    public void setCookiePath(final String cookiePath) {
        this.cookiePath = cookiePath;
    }

    /**
     * Sets whether the user identification cookie should be marked as secure.
     *
     * @param cookieSecure true if the cookie should be secure (HTTPS only), false otherwise, or null for auto-detection
     */
    public void setCookieSecure(final Boolean cookieSecure) {
        this.cookieSecure = cookieSecure;
    }

    /**
     * Sets whether the user identification cookie should be HTTP-only.
     *
     * @param httpOnly true if the cookie should be HTTP-only (not accessible via JavaScript), false otherwise
     */
    public void setCookieHttpOnly(final boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
}