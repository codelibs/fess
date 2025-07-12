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

import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.exception.InvalidAccessTokenException;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The helper for access token.
 */
public class AccessTokenHelper {

    /**
     * Default constructor.
     */
    public AccessTokenHelper() {
        // nothing
    }

    /**
     * The bearer string.
     */
    protected static final String BEARER = "Bearer";

    /**
     * The random instance.
     */
    protected Random random = new SecureRandom();

    /**
     * Generate the access token.
     * @return The access token.
     */
    public String generateAccessToken() {
        return RandomStringUtils.random(ComponentUtil.getFessConfig().getApiAccessTokenLengthAsInteger(), 0, 0, true, true, null, random);
    }

    /**
     * Get the access token from the request.
     * @param request The request.
     * @return The access token.
     */
    public String getAccessTokenFromRequest(final HttpServletRequest request) {
        final String token = request.getHeader("Authorization");
        if (token != null) {
            final String[] values = token.trim().split(" ");
            if (values.length == 2 && BEARER.equals(values[0])) {
                return values[1];
            }
            if (values.length == 1 && !BEARER.equals(values[0])) {
                return values[0];
            }
            throw new InvalidAccessTokenException("invalid_request", "Invalid format: " + token);
        }
        final String name = ComponentUtil.getFessConfig().getApiAccessTokenRequestParameter();
        if (StringUtil.isNotBlank(name)) {
            return request.getParameter(name);
        }
        return null;
    }

    /**
     * Set the random instance.
     * @param random The random instance.
     */
    public void setRandom(final Random random) {
        this.random = random;
    }
}
