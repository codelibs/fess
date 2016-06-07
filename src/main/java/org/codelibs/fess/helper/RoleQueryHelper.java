/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.codelibs.core.crypto.CachedCipher;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.servlet.request.RequestManager;
import org.lastaflute.web.util.LaRequestUtil;
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
public class RoleQueryHelper {

    private static final Logger logger = LoggerFactory.getLogger(RoleQueryHelper.class);

    public CachedCipher cipher;

    public String valueSeparator = "\n";

    public String roleSeparator = ",";

    public String parameterKey;

    public boolean encryptedParameterValue = true;

    public String headerKey;

    public boolean encryptedHeaderValue = true;

    public String cookieKey;

    public boolean encryptedCookieValue = true;

    protected Map<String, String> cookieNameMap;

    private final List<String> defaultRoleList = new ArrayList<>();

    @PostConstruct
    public void init() {
        stream(ComponentUtil.getFessConfig().getSearchDefaultPermissionsAsArray()).of(stream -> stream.forEach(name -> {
            defaultRoleList.add(name);
        }));
    }

    public Set<String> build() {
        final Set<String> roleList = new HashSet<>();
        final HttpServletRequest request = LaRequestUtil.getOptionalRequest().orElse(null);

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

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final RequestManager requestManager = ComponentUtil.getRequestManager();
        requestManager.findUserBean(FessUserBean.class)
                .ifPresent(fessUserBean -> stream(fessUserBean.getPermissions()).of(stream -> stream.forEach(roleList::add)))
                .orElse(() -> roleList.addAll(fessConfig.getSearchGuestPermissionList()));

        if (defaultRoleList != null) {
            roleList.addAll(defaultRoleList);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("roleList: " + roleList);
        }

        return roleList;
    }

    protected Set<String> buildByParameter(final HttpServletRequest request) {

        final String parameter = request.getParameter(parameterKey);
        if (logger.isDebugEnabled()) {
            logger.debug(parameterKey + ":" + parameter);
        }
        if (StringUtil.isNotEmpty(parameter)) {
            return decodedRoleList(parameter, encryptedParameterValue);
        }

        return Collections.emptySet();
    }

    protected Set<String> buildByHeader(final HttpServletRequest request) {

        final String parameter = request.getHeader(headerKey);
        if (logger.isDebugEnabled()) {
            logger.debug(headerKey + ":" + parameter);
        }
        if (StringUtil.isNotEmpty(parameter)) {
            return decodedRoleList(parameter, encryptedHeaderValue);
        }

        return Collections.emptySet();

    }

    protected Set<String> buildByCookie(final HttpServletRequest request) {

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

        return Collections.emptySet();
    }

    protected Set<String> buildByCookieNameMapping(final HttpServletRequest request) {

        final Set<String> roleNameSet = new HashSet<>();
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                addRoleFromCookieMapping(roleNameSet, cookie);
            }
        }

        return roleNameSet;
    }

    protected void addRoleFromCookieMapping(final Set<String> roleNameList, final Cookie cookie) {
        final String roleName = cookieNameMap.get(cookie.getName());
        if (StringUtil.isNotBlank(roleName)) {
            roleNameList.add(roleName);
        }
    }

    protected Set<String> decodedRoleList(final String value, final boolean encrypted) {
        String rolesStr = value;
        if (encrypted && cipher != null) {
            rolesStr = cipher.decryptoText(rolesStr);
        }

        final Set<String> roleSet = new HashSet<>();
        if (valueSeparator.length() > 0) {
            final String[] values = rolesStr.split(valueSeparator);
            if (values.length > 1) {
                final String[] roles = values[1].split(roleSeparator);
                for (final String role : roles) {
                    if (StringUtil.isNotEmpty(role)) {
                        roleSet.add(role);
                    }
                }
            }
        } else {
            final String[] roles = rolesStr.split(roleSeparator);
            for (final String role : roles) {
                if (StringUtil.isNotEmpty(role)) {
                    roleSet.add(role);
                }
            }
        }
        return roleSet;
    }

    public void addCookieNameMapping(final String cookieName, final String roleName) {
        if (cookieNameMap == null) {
            cookieNameMap = new HashMap<>();
        }
        cookieNameMap.put(cookieName, roleName);
    }

}
