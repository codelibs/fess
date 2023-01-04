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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.crypto.CachedCipher;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.service.AccessTokenService;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.exception.InvalidAccessTokenException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.login.LoginManager;
import org.lastaflute.web.servlet.request.RequestManager;
import org.lastaflute.web.util.LaRequestUtil;

/**
 * This class returns a list of a role from a request parameter,
 * a request header and a cookie. The format of the default value
 * is "[\d]+\nrole1,role2,role3", which you can encrypt.
 *
 * @author shinsuke
 *
 */
public class RoleQueryHelper {

    private static final Logger logger = LogManager.getLogger(RoleQueryHelper.class);

    protected static final String USER_ROLES = "userRoles";

    protected CachedCipher cipher;

    protected String valueSeparator = "\n";

    protected String roleSeparator = ",";

    protected String parameterKey;

    protected boolean encryptedParameterValue = true;

    protected String headerKey;

    protected boolean encryptedHeaderValue = true;

    protected String cookieKey;

    protected boolean encryptedCookieValue = true;

    protected long maxAge = 30 * 60; // sec

    protected Map<String, String> cookieNameMap;

    protected final List<String> defaultRoleList = new ArrayList<>();

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        stream(ComponentUtil.getFessConfig().getSearchDefaultPermissionsAsArray()).of(stream -> stream.forEach(name -> {
            defaultRoleList.add(name);
        }));
    }

    public Set<String> build(final SearchRequestType searchRequestType) {
        final Set<String> roleSet = new HashSet<>();
        final HttpServletRequest request = LaRequestUtil.getOptionalRequest().orElse(null);
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final boolean isApiRequest =
                !SearchRequestType.SEARCH.equals(searchRequestType) && !SearchRequestType.ADMIN_SEARCH.equals(searchRequestType);

        if (request != null) {
            @SuppressWarnings("unchecked")
            final Set<String> list = (Set<String>) request.getAttribute(USER_ROLES);
            if (list != null) {
                return list;
            }

            // request parameter
            if (StringUtil.isNotBlank(parameterKey)) {
                processParameter(request, roleSet);
            }

            // request header
            if (StringUtil.isNotBlank(headerKey)) {
                processHeader(request, roleSet);
            }

            // cookie
            if (StringUtil.isNotBlank(cookieKey)) {
                processCookie(request, roleSet);
            }

            // cookie mapping
            if (cookieNameMap != null) {
                buildByCookieNameMapping(request, roleSet);
            }

            final boolean hasAccessToken = processAccessToken(request, roleSet, isApiRequest);

            final RequestManager requestManager = ComponentUtil.getRequestManager();
            try {
                requestManager.findUserBean(FessUserBean.class)
                        .ifPresent(fessUserBean -> stream(fessUserBean.getPermissions()).of(stream -> stream.forEach(roleSet::add)))
                        .orElse(() -> {
                            if (isApiRequest && ComponentUtil.getFessConfig().getApiAccessTokenRequiredAsBoolean()) {
                                throw new InvalidAccessTokenException("invalid_token", "Access token is requried.");
                            }
                            if (!hasAccessToken || roleSet.isEmpty()) {
                                roleSet.addAll(fessConfig.getSearchGuestPermissionList());
                            }
                        });
            } catch (final RuntimeException e) {
                try {
                    requestManager.findLoginManager(FessUserBean.class).ifPresent(LoginManager::logout);
                } catch (final Exception e1) {
                    // ignore
                }
                throw e;
            }
        }

        if (defaultRoleList != null) {
            roleSet.addAll(defaultRoleList);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("roleSet: {}", roleSet);
        }

        if (request != null) {
            request.setAttribute(USER_ROLES, roleSet);
        }
        return roleSet;
    }

    protected boolean processAccessToken(final HttpServletRequest request, final Set<String> roleSet, final boolean isApiRequest) {
        if (isApiRequest) {
            return ComponentUtil.getComponent(AccessTokenService.class).getPermissions(request).map(p -> {
                p.forEach(roleSet::add);
                return true;
            }).orElse(false);
        }
        return false;
    }

    protected void processParameter(final HttpServletRequest request, final Set<String> roleSet) {
        final String parameter = request.getParameter(parameterKey);
        if (logger.isDebugEnabled()) {
            logger.debug("{}:{}", parameterKey, parameter);
        }
        if (StringUtil.isNotEmpty(parameter)) {
            parseRoleSet(parameter, encryptedParameterValue, roleSet);
        }

    }

    protected void processHeader(final HttpServletRequest request, final Set<String> roleSet) {

        final String parameter = request.getHeader(headerKey);
        if (logger.isDebugEnabled()) {
            logger.debug("{}:{}", headerKey, parameter);
        }
        if (StringUtil.isNotEmpty(parameter)) {
            parseRoleSet(parameter, encryptedHeaderValue, roleSet);
        }

    }

    protected void processCookie(final HttpServletRequest request, final Set<String> roleSet) {

        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookieKey.equals(cookie.getName())) {
                    final String value = cookie.getValue();
                    if (logger.isDebugEnabled()) {
                        logger.debug("{}:{}", cookieKey, value);
                    }
                    if (StringUtil.isNotEmpty(value)) {
                        parseRoleSet(value, encryptedCookieValue, roleSet);
                    }
                }
            }
        }

    }

    protected void buildByCookieNameMapping(final HttpServletRequest request, final Set<String> roleSet) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                addRoleFromCookieMapping(roleSet, cookie);
            }
        }

    }

    protected void addRoleFromCookieMapping(final Set<String> roleNameList, final Cookie cookie) {
        final String roleName = cookieNameMap.get(cookie.getName());
        if (StringUtil.isNotBlank(roleName)) {
            roleNameList.add(roleName);
        }
    }

    protected void parseRoleSet(final String value, final boolean encrypted, final Set<String> roleSet) {
        String rolesStr = value;
        if (encrypted && cipher != null) {
            try {
                rolesStr = cipher.decryptoText(rolesStr);
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to decrypt {}", rolesStr, e);
                }
                return;
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("role: original: {}, decrypto: {}", value, rolesStr);
        }

        if (valueSeparator.length() > 0) {
            final String[] values = rolesStr.split(valueSeparator);
            if (maxAge > 0) {
                try {
                    final long time = getCurrentTime() / 1000 - Long.parseLong(values[0]);
                    if (time > maxAge || time < 0) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("role info is expired: {} > {}", time, maxAge);
                        }
                        return;
                    }
                } catch (final NumberFormatException e) {
                    logger.warn("Invalid role info: {}", rolesStr, e);
                    return;
                }
            }
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
    }

    protected long getCurrentTime() {
        return ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
    }

    public void addCookieNameMapping(final String cookieName, final String roleName) {
        if (cookieNameMap == null) {
            cookieNameMap = new HashMap<>();
        }
        cookieNameMap.put(cookieName, roleName);
    }

    public void setCipher(final CachedCipher cipher) {
        this.cipher = cipher;
    }

    public void setValueSeparator(final String valueSeparator) {
        this.valueSeparator = valueSeparator;
    }

    public void setRoleSeparator(final String roleSeparator) {
        this.roleSeparator = roleSeparator;
    }

    public void setParameterKey(final String parameterKey) {
        this.parameterKey = parameterKey;
    }

    public void setEncryptedParameterValue(final boolean encryptedParameterValue) {
        this.encryptedParameterValue = encryptedParameterValue;
    }

    public void setHeaderKey(final String headerKey) {
        this.headerKey = headerKey;
    }

    public void setEncryptedHeaderValue(final boolean encryptedHeaderValue) {
        this.encryptedHeaderValue = encryptedHeaderValue;
    }

    public void setCookieKey(final String cookieKey) {
        this.cookieKey = cookieKey;
    }

    public void setEncryptedCookieValue(final boolean encryptedCookieValue) {
        this.encryptedCookieValue = encryptedCookieValue;
    }

    public void setMaxAge(final long maxAge) {
        this.maxAge = maxAge;
    }

}
