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
     *
     * <p>The Authorization header is read as a Fess access token only when it is addressed to us:
     * either a bare token with no scheme, or the {@code Bearer} scheme, whose name RFC 7235 makes
     * case-insensitive. A header naming any other scheme -- the {@code Negotiate} of a SPNEGO
     * client, a {@code Basic} or {@code Digest} credential -- belongs to that scheme's own
     * handler, so this request simply carries no access token and the method returns null.
     * Reporting those as a malformed access token is what made every such API request a 500.</p>
     *
     * <p>What is thrown is a header that is ours and unusable: {@code Bearer} with no credential
     * after it, or with more than one word after it. Neither message quotes the header, because
     * the part after the scheme is the access token itself and these messages are logged.</p>
     *
     * @param request The request.
     * @return The access token, or null when the request carries none.
     */
    public String getAccessTokenFromRequest(final HttpServletRequest request) {
        final String authzHeader = request.getHeader("Authorization");
        if (authzHeader != null) {
            final String[] values = authzHeader.trim().split(" ");
            if (values.length == 1) {
                if (BEARER.equalsIgnoreCase(values[0])) {
                    throw new InvalidAccessTokenException("invalid_request", "The Bearer scheme is missing its credential.");
                }
                return values[0];
            }
            if (!BEARER.equalsIgnoreCase(values[0])) {
                return null;
            }
            if (values.length == 2) {
                return values[1];
            }
            throw new InvalidAccessTokenException("invalid_request", "The Bearer credential is not a single token.");
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
