/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.sf.fess.Constants;
import jp.sf.fess.db.exentity.UserInfo;
import jp.sf.fess.helper.UserInfoHelper;
import jp.sf.fess.service.UserInfoService;

import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.UUID;
import org.seasar.robot.util.LruHashMap;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ResponseUtil;

public class CookieUserInfoHelperImpl implements UserInfoHelper {

    @Resource
    protected UserInfoService userInfoService;

    public int userInfoCacheSize = 1000;

    public int resultUrlCacheSize = 10;

    public String cookieName = "fsid";

    public String cookieDomain;

    public int cookieMaxAge = 30 * 24 * 60 * 60;// 1 month

    public String cookiePath;

    public Boolean cookieSecure;

    public long userCheckInterval = 5 * 60 * 1000;// 5 min

    private Map<String, Long> userInfoCache;

    @InitMethod
    public void init() {
        userInfoCache = new LruHashMap<String, Long>(userInfoCacheSize);
    }

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

        if (StringUtil.isBlank(userCode)) {
            userCode = getId();
        }

        if (StringUtil.isNotBlank(userCode)) {
            updateUserSessionId(userCode);
        }
        return userCode;
    }

    protected String getId() {
        return UUID.create();
    }

    protected void updateUserSessionId(final String userCode) {
        final long current = System.currentTimeMillis();
        final Long time = userInfoCache.get(userCode);
        if (time == null || current - time.longValue() > userCheckInterval) {
            UserInfo userInfo = userInfoService.getUserInfo(userCode);
            if (userInfo == null) {
                final Timestamp now = new Timestamp(current);
                userInfo = new UserInfo();
                userInfo.setCode(userCode);
                userInfo.setCreatedTime(now);
                userInfo.setUpdatedTime(now);
                userInfoService.store(userInfo);
            }
            userInfoCache.put(userCode, current);
        }

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
            String queryId = query.hashCode() + session.getId()
                    + System.currentTimeMillis();
            queryId = Integer.toString(queryId.hashCode());

            final List<String> urlList = new ArrayList<String>();
            for (final Map<String, Object> map : documentItems) {
                final Object url = map.get("url");
                if (url != null && url.toString().length() > 0) {
                    urlList.add(url.toString());
                }
            }

            if (!urlList.isEmpty()) {
                final Map<String, String[]> resultUrlCache = getResultUrlCache(session);
                resultUrlCache.put(queryId,
                        urlList.toArray(new String[urlList.size()]));
                return queryId;
            }
        }
        return Constants.EMPTY_STRING;
    }

    @Override
    public String[] getResultUrls(final String queryId) {
        final HttpSession session = RequestUtil.getRequest().getSession(false);
        if (session != null) {
            final Map<String, String[]> resultUrlCache = getResultUrlCache(session);
            final String[] urls = resultUrlCache.get(queryId);
            if (urls != null) {
                return urls;
            }
        }
        return new String[0];
    }

    private Map<String, String[]> getResultUrlCache(final HttpSession session) {
        Map<String, String[]> resultUrlCache = (Map<String, String[]>) session
                .getAttribute(Constants.RESULT_URL_CACHE);
        if (resultUrlCache == null) {
            resultUrlCache = new LruHashMap<String, String[]>(
                    resultUrlCacheSize);
            session.setAttribute(Constants.RESULT_URL_CACHE, resultUrlCache);
        }
        return resultUrlCache;
    }
}
