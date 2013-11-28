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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.sf.fess.Constants;
import jp.sf.fess.crypto.FessCipher;
import jp.sf.fess.entity.LoginInfo;
import jp.sf.fess.helper.RoleQueryHelper;
import jp.sf.fess.helper.SystemHelper;

import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class returns a list of a role from a request parameter,
 * a request header and a cookie. The format of the default value
 * is "[\d]+\nrole1,role2,role3", which you can encrypt.
 * 
 * @author shinsuke
 *
 */
public class RoleQueryHelperImpl implements RoleQueryHelper, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory
            .getLogger(RoleQueryHelperImpl.class);

    public FessCipher fessCipher;

    public String valueSeparator = "\n";

    public String roleSeparator = ",";

    public String parameterKey;

    public boolean encryptedParameterValue = true;

    public String headerKey;

    public boolean encryptedHeaderValue = true;

    public String cookieKey;

    public boolean encryptedCookieValue = true;

    protected Map<String, String> cookieNameMap;

    public List<String> defaultRoleList;

    public SystemHelper systemHelper;

    /* (non-Javadoc)
     * @see jp.sf.fess.helper.impl.RoleQueryHelper#build()
     */
    @Override
    public List<String> build() {
        final List<String> roleList = new ArrayList<String>();
        final HttpServletRequest request = RequestUtil.getRequest();

        // request parameter
        if (request != null && StringUtil.isNotBlank(parameterKey)) {
            roleList.addAll(buildByParameter(request));
        }

        // request header
        if (request != null && StringUtil.isNotBlank(headerKey)) {
            roleList.addAll(buildByHeader(request));
        }

        // cookie
        if (request != null && StringUtil.isNotBlank(cookieKey)) {
            roleList.addAll(buildByCookie(request));
        }

        // cookie mapping
        if (cookieNameMap != null) {
            roleList.addAll(buildByCookieNameMapping(request));
        }

        // JAAS roles
        if (request != null) {
            final HttpSession session = request.getSession(false);
            if (session != null) {
                final LoginInfo loginInfo = (LoginInfo) session
                        .getAttribute(Constants.LOGIN_INFO);
                if (loginInfo != null) {
                    roleList.addAll(loginInfo.getRoleList());
                }
            }
        }

        if (defaultRoleList != null) {
            roleList.addAll(defaultRoleList);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("roleList: " + roleList);
        }

        return roleList;
    }

    protected List<String> buildByParameter(final HttpServletRequest request) {

        final String parameter = request.getParameter(parameterKey);
        if (logger.isDebugEnabled()) {
            logger.debug(parameterKey + ":" + parameter);
        }
        if (StringUtil.isNotEmpty(parameter)) {
            return decodedRoleList(parameter, encryptedParameterValue);
        }

        return Collections.emptyList();
    }

    protected List<String> buildByHeader(final HttpServletRequest request) {

        final String parameter = request.getHeader(headerKey);
        if (logger.isDebugEnabled()) {
            logger.debug(headerKey + ":" + parameter);
        }
        if (StringUtil.isNotEmpty(parameter)) {
            return decodedRoleList(parameter, encryptedHeaderValue);
        }

        return Collections.emptyList();

    }

    protected List<String> buildByCookie(final HttpServletRequest request) {

        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookieKey.equals(cookie.getName())) {
                    final String value = cookie.getValue();
                    if (logger.isDebugEnabled()) {
                        logger.debug(cookieKey + ":" + value);
                    }
                    if (StringUtil.isNotEmpty(value)) {
                        return decodedRoleList(value, encryptedCookieValue);
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    protected List<String> buildByCookieNameMapping(
            final HttpServletRequest request) {

        final List<String> roleNameList = new ArrayList<String>();
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                addRoleFromCookieMapping(roleNameList, cookie);
            }
        }

        return roleNameList;
    }

    protected void addRoleFromCookieMapping(final List<String> roleNameList,
            final Cookie cookie) {
        final String roleName = cookieNameMap.get(cookie.getName());
        if (StringUtil.isNotBlank(roleName)) {
            roleNameList.add(roleName);
        }
    }

    protected List<String> decodedRoleList(final String value,
            final boolean encrypted) {
        String rolesStr = value;
        if (encrypted && fessCipher != null) {
            rolesStr = fessCipher.decryptoText(rolesStr);
        }

        final List<String> roleList = new ArrayList<String>();
        if (valueSeparator.length() > 0) {
            final String[] values = rolesStr.split(valueSeparator);
            if (values.length > 1) {
                final String[] roles = values[1].split(roleSeparator);
                for (final String role : roles) {
                    if (StringUtil.isNotEmpty(role)) {
                        roleList.add(role);
                    }
                }
            }
        } else {
            final String[] roles = rolesStr.split(roleSeparator);
            for (final String role : roles) {
                if (StringUtil.isNotEmpty(role)) {
                    roleList.add(role);
                }
            }
        }
        return roleList;
    }

    public void addCookieNameMapping(final String cookieName,
            final String roleName) {
        if (cookieNameMap == null) {
            cookieNameMap = new HashMap<String, String>();
        }
        cookieNameMap.put(cookieName, roleName);
    }

}
