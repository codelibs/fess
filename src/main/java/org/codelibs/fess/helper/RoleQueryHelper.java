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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * This class returns a list of a role from a request parameter,
 * a request header and a cookie. The format of the default value
 * is "[\d]+\nrole1,role2,role3", which you can encrypt.
 *
 *
 */
public class RoleQueryHelper {

    /**
     * Constructor.
     */
    public RoleQueryHelper() {
        super();
    }

    private static final Logger logger = LogManager.getLogger(RoleQueryHelper.class);

    /**
     * The key for user roles in the request attribute.
     */
    protected static final String USER_ROLES = "userRoles";

    /**
     * The cached cipher for encryption and decryption.
     */
    protected CachedCipher cipher;

    /**
     * The separator for values in the role string.
     */
    protected String valueSeparator = "\n";

    /**
     * The separator for roles in the role string.
     */
    protected String roleSeparator = ",";

    /**
     * The key for the request parameter that contains role information.
     */
    protected String parameterKey;

    /**
     * Whether the parameter value is encrypted.
     */
    protected boolean encryptedParameterValue = true;

    /**
     * The key for the request header that contains role information.
     */
    protected String headerKey;

    /**
     * Whether the header value is encrypted.
     */
    protected boolean encryptedHeaderValue = true;

    /**
     * The key for the cookie that stores role information.
     */
    protected String cookieKey;

    /**
     * Whether the cookie value is encrypted.
     */
    protected boolean encryptedCookieValue = true;

    /**
     * The maximum age of the role information in seconds.
     */
    protected long maxAge = 30 * 60; // sec

    /**
     * A map of cookie names to role names.
     */
    protected Map<String, String> cookieNameMap;

    /**
     * A list of default roles.
     */
    protected final List<String> defaultRoleList = new ArrayList<>();

    /**
     * Initializes the RoleQueryHelper.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing {}", this.getClass().getSimpleName());
        }
        stream(ComponentUtil.getFessConfig().getSearchDefaultPermissionsAsArray()).of(stream -> stream.forEach(name -> {
            defaultRoleList.add(name);
        }));
    }

    /**
     * Builds a set of roles from the request.
     * @param searchRequestType The type of the search request.
     * @return A set of roles.
     */
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
                                roleSet.addAll(fessConfig.getSearchGuestRoleList());
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

    /**
     * Processes the access token.
     * @param request The HTTP request.
     * @param roleSet The set of roles.
     * @param isApiRequest Whether the request is an API request.
     * @return true if the access token is processed, false otherwise.
     */
    protected boolean processAccessToken(final HttpServletRequest request, final Set<String> roleSet, final boolean isApiRequest) {
        if (isApiRequest) {
            return ComponentUtil.getComponent(AccessTokenService.class).getPermissions(request).map(p -> {
                p.forEach(roleSet::add);
                return true;
            }).orElse(false);
        }
        return false;
    }

    /**
     * Processes the request parameter.
     * @param request The HTTP request.
     * @param roleSet The set of roles.
     */
    protected void processParameter(final HttpServletRequest request, final Set<String> roleSet) {
        final String parameter = request.getParameter(parameterKey);
        if (logger.isDebugEnabled()) {
            logger.debug("{}:{}", parameterKey, parameter);
        }
        if (StringUtil.isNotEmpty(parameter)) {
            parseRoleSet(parameter, encryptedParameterValue, roleSet);
        }

    }

    /**
     * Processes the request header.
     * @param request The HTTP request.
     * @param roleSet The set of roles.
     */
    protected void processHeader(final HttpServletRequest request, final Set<String> roleSet) {

        final String parameter = request.getHeader(headerKey);
        if (logger.isDebugEnabled()) {
            logger.debug("{}:{}", headerKey, parameter);
        }
        if (StringUtil.isNotEmpty(parameter)) {
            parseRoleSet(parameter, encryptedHeaderValue, roleSet);
        }

    }

    /**
     * Processes the cookie.
     * @param request The HTTP request.
     * @param roleSet The set of roles.
     */
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

    /**
     * Builds roles from the cookie name mapping.
     * @param request The HTTP request.
     * @param roleSet The set of roles.
     */
    protected void buildByCookieNameMapping(final HttpServletRequest request, final Set<String> roleSet) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                addRoleFromCookieMapping(roleSet, cookie);
            }
        }

    }

    /**
     * Adds a role from the cookie mapping.
     * @param roleNameList The list of role names.
     * @param cookie The cookie.
     */
    protected void addRoleFromCookieMapping(final Set<String> roleNameList, final Cookie cookie) {
        final String roleName = cookieNameMap.get(cookie.getName());
        if (StringUtil.isNotBlank(roleName)) {
            roleNameList.add(roleName);
        }
    }

    /**
     * Parses the role set from a string.
     * @param value The string to parse.
     * @param encrypted Whether the string is encrypted.
     * @param roleSet The set of roles.
     */
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
                    logger.warn("Invalid role info: failed to parse timestamp from '{}'", rolesStr, e);
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

    /**
     * Gets the current time in milliseconds.
     * @return The current time in milliseconds.
     */
    protected long getCurrentTime() {
        return ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
    }

    /**
     * Adds a cookie name mapping.
     * @param cookieName The name of the cookie.
     * @param roleName The name of the role.
     */
    public void addCookieNameMapping(final String cookieName, final String roleName) {
        if (cookieNameMap == null) {
            cookieNameMap = new HashMap<>();
        }
        cookieNameMap.put(cookieName, roleName);
    }

    /**
     * Sets the cached cipher.
     * @param cipher The cached cipher.
     */
    public void setCipher(final CachedCipher cipher) {
        this.cipher = cipher;
    }

    /**
     * Sets the value separator.
     * @param valueSeparator The value separator.
     */
    public void setValueSeparator(final String valueSeparator) {
        this.valueSeparator = valueSeparator;
    }

    /**
     * Sets the role separator.
     * @param roleSeparator The role separator.
     */
    public void setRoleSeparator(final String roleSeparator) {
        this.roleSeparator = roleSeparator;
    }

    /**
     * Sets the parameter key.
     * @param parameterKey The parameter key.
     */
    public void setParameterKey(final String parameterKey) {
        this.parameterKey = parameterKey;
    }

    /**
     * Sets whether the parameter value is encrypted.
     * @param encryptedParameterValue Whether the parameter value is encrypted.
     */
    public void setEncryptedParameterValue(final boolean encryptedParameterValue) {
        this.encryptedParameterValue = encryptedParameterValue;
    }

    /**
     * Sets the header key.
     * @param headerKey The header key.
     */
    public void setHeaderKey(final String headerKey) {
        this.headerKey = headerKey;
    }

    /**
     * Sets whether the header value is encrypted.
     * @param encryptedHeaderValue Whether the header value is encrypted.
     */
    public void setEncryptedHeaderValue(final boolean encryptedHeaderValue) {
        this.encryptedHeaderValue = encryptedHeaderValue;
    }

    /**
     * Sets the cookie key.
     * @param cookieKey The cookie key.
     */
    public void setCookieKey(final String cookieKey) {
        this.cookieKey = cookieKey;
    }

    /**
     * Sets whether the cookie value is encrypted.
     * @param encryptedCookieValue Whether the cookie value is encrypted.
     */
    public void setEncryptedCookieValue(final boolean encryptedCookieValue) {
        this.encryptedCookieValue = encryptedCookieValue;
    }

    /**
     * Sets the maximum age of the role information in seconds.
     * @param maxAge The maximum age of the role information in seconds.
     */
    public void setMaxAge(final long maxAge) {
        this.maxAge = maxAge;
    }

}
