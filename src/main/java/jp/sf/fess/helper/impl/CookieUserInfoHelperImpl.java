/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.helper.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.sf.fess.Constants;
import jp.sf.fess.helper.SearchLogHelper;
import jp.sf.fess.helper.UserInfoHelper;

import org.codelibs.core.util.StringUtil;
import org.codelibs.robot.util.LruHashMap;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ResponseUtil;

public class CookieUserInfoHelperImpl implements UserInfoHelper {

    @Resource
    protected SearchLogHelper searchLogHelper;

    public int resultDocIdsCacheSize = 20;

    public String cookieName = "fsid";

    public String cookieDomain;

    public int cookieMaxAge = 30 * 24 * 60 * 60;// 1 month

    public String cookiePath;

    public Boolean cookieSecure;

    /* (non-Javadoc)
     * @see jp.sf.fess.helper.impl.UserInfoHelper#getUserCode()
     */
    @Override
    public String getUserCode() {
        final HttpServletRequest request = RequestUtil.getRequest();

        String userCode = (String) request.getAttribute(Constants.USER_CODE);

        if (StringUtil.isBlank(userCode)) {
            userCode = getUserCodeFromCookie(request);
        }

        if (!request.isRequestedSessionIdValid()) {
            return null;
        }

        if (StringUtil.isBlank(userCode)) {
            userCode = getId();
        }

        if (StringUtil.isNotBlank(userCode)) {
            updateUserSessionId(userCode);
        }
        return userCode;
    }

    protected String getId() {
        return UUID.randomUUID().toString().replace("-", StringUtil.EMPTY);
    }

    protected void updateUserSessionId(final String userCode) {
        searchLogHelper.updateUserInfo(userCode);

        final HttpServletRequest request = RequestUtil.getRequest();
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
        ResponseUtil.getResponse().addCookie(cookie);
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

    @Override
    public String generateQueryId(final String query,
            final List<Map<String, Object>> documentItems) {
        final HttpSession session = RequestUtil.getRequest().getSession(false);
        if (session != null) {
            final String queryId = getId();

            final List<String> docIdList = new ArrayList<String>();
            for (final Map<String, Object> map : documentItems) {
                final Object docId = map.get(Constants.DOC_ID);
                if (docId != null && docId.toString().length() > 0) {
                    docIdList.add(docId.toString());
                }
            }

            if (!docIdList.isEmpty()) {
                final Map<String, String[]> resultDocIdsCache = getResultDocIdsCache(session);
                resultDocIdsCache.put(queryId,
                        docIdList.toArray(new String[docIdList.size()]));
                return queryId;
            }
        }
        return StringUtil.EMPTY;
    }

    @Override
    public String[] getResultDocIds(final String queryId) {
        final HttpSession session = RequestUtil.getRequest().getSession(false);
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
        Map<String, String[]> resultDocIdsCache = (Map<String, String[]>) session
                .getAttribute(Constants.RESULT_DOC_ID_CACHE);
        if (resultDocIdsCache == null) {
            resultDocIdsCache = new LruHashMap<String, String[]>(
                    resultDocIdsCacheSize);
            session.setAttribute(Constants.RESULT_DOC_ID_CACHE,
                    resultDocIdsCache);
        }
        return resultDocIdsCache;
    }
}
