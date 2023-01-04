/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

public class UserInfoHelper {
    protected static final String USER_BEAN = "lastaflute.action.USER_BEAN.FessUserBean";

    protected int resultDocIdsCacheSize = 20;

    protected String cookieName = "fsid";

    protected String cookieDomain;

    protected int cookieMaxAge = 30 * 24 * 60 * 60;// 1 month

    protected String cookiePath = "/";

    protected Boolean cookieSecure;

    protected boolean httpOnly = true;

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

    protected String getUserCodeFromUserBean(final HttpServletRequest request) {
        final SessionManager sessionManager = ComponentUtil.getComponent(SessionManager.class);
        String userCode = sessionManager.getAttribute(USER_BEAN, TypicalUserBean.class)
                .filter(u -> !Constants.EMPTY_USER_ID.equals(u.getUserId())).map(u -> u.getUserId().toString()).orElse(StringUtil.EMPTY);
        if (StringUtil.isBlank(userCode)) {
            return null;
        }

        userCode = createUserCodeFromUserId(userCode);
        request.setAttribute(Constants.USER_CODE, userCode);
        deleteUserCodeFromCookie(request);
        return userCode;
    }

    protected String createUserCodeFromUserId(String userCode) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final PrimaryCipher cipher = ComponentUtil.getPrimaryCipher();
        userCode = cipher.encrypt(userCode);
        if (fessConfig.isValidUserCode(userCode)) {
            return userCode;
        }
        return null;
    }

    public void deleteUserCodeFromCookie(final HttpServletRequest request) {
        final String cookieValue = getUserCodeFromCookie(request);
        if (cookieValue != null) {
            updateCookie(StringUtil.EMPTY, 0);
        }
    }

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

    protected String getId() {
        return UUID.randomUUID().toString().replace("-", StringUtil.EMPTY);
    }

    protected void updateUserSessionId(final String userCode) {
        ComponentUtil.getSearchLogHelper().getUserInfo(userCode);

        LaRequestUtil.getOptionalRequest().ifPresent(req -> req.setAttribute(Constants.USER_CODE, userCode));

        updateCookie(userCode, cookieMaxAge);
    }

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
        if (cookieSecure != null) {
            cookie.setSecure(cookieSecure);
        }
        LaResponseUtil.getResponse().addCookie(cookie);
    }

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

    private Map<String, String[]> getResultDocIdsCache(final HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<String, String[]> resultDocIdsCache = (Map<String, String[]>) session.getAttribute(Constants.RESULT_DOC_ID_CACHE);
        if (resultDocIdsCache == null) {
            resultDocIdsCache = new LruHashMap<>(resultDocIdsCacheSize);
            session.setAttribute(Constants.RESULT_DOC_ID_CACHE, resultDocIdsCache);
        }
        return resultDocIdsCache;
    }

    public void setResultDocIdsCacheSize(final int resultDocIdsCacheSize) {
        this.resultDocIdsCacheSize = resultDocIdsCacheSize;
    }

    public void setCookieName(final String cookieName) {
        this.cookieName = cookieName;
    }

    public void setCookieDomain(final String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    public void setCookieMaxAge(final int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    public void setCookiePath(final String cookiePath) {
        this.cookiePath = cookiePath;
    }

    public void setCookieSecure(final Boolean cookieSecure) {
        this.cookieSecure = cookieSecure;
    }

    public void setCookieHttpOnly(final boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
}