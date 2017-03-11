/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codelibs.core.collection.LruHashMap;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;

public class UserInfoHelper {
    @Resource
    protected SearchLogHelper searchLogHelper;

    public int resultDocIdsCacheSize = 20;

    public String cookieName = "fsid";

    public String cookieDomain;

    public int cookieMaxAge = 30 * 24 * 60 * 60;// 1 month

    public String cookiePath;

    public Boolean cookieSecure;

    public String getUserCode() {
        final HttpServletRequest request = LaRequestUtil.getRequest();

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
            userCode = getId();
        }

        if (StringUtil.isNotBlank(userCode)) {
            updateUserSessionId(userCode);
        }
        return userCode;
    }

    protected String getUserCodeFromRequest(final HttpServletRequest request) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String userCode = request.getParameter(fessConfig.getUserCodeRequestParameter());
        if (StringUtil.isBlank(userCode)) {
            return null;
        }

        final int length = userCode.length();
        if (fessConfig.getUserCodeMinLengthAsInteger().intValue() > length
                || fessConfig.getUserCodeMaxLengthAsInteger().intValue() < length) {
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
        searchLogHelper.updateUserInfo(userCode);

        final HttpServletRequest request = LaRequestUtil.getRequest();
        request.setAttribute(Constants.USER_CODE, userCode);

        final Cookie cookie = new Cookie(cookieName, userCode);
        cookie.setMaxAge(cookieMaxAge);
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
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void storeQueryId(final String queryId, final List<Map<String, Object>> documentItems) {
        final HttpSession session = LaRequestUtil.getRequest().getSession(false);
        if (session != null) {
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
        }
    }

    public String[] getResultDocIds(final String queryId) {
        final HttpSession session = LaRequestUtil.getRequest().getSession(false);
        if (session != null) {
            final Map<String, String[]> resultUrlCache = getResultDocIdsCache(session);
            final String[] urls = resultUrlCache.get(queryId);
            if (urls != null) {
                return urls;
            }
        }
        return StringUtil.EMPTY_STRINGS;
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
}