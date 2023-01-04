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

import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.exception.InvalidAccessTokenException;
import org.codelibs.fess.util.ComponentUtil;

public class AccessTokenHelper {

    protected Random random = new SecureRandom();

    public String generateAccessToken() {
        return RandomStringUtils.random(ComponentUtil.getFessConfig().getApiAccessTokenLengthAsInteger(), 0, 0, true, true, null, random);
    }

    public String getAccessTokenFromRequest(final HttpServletRequest request) {
        final String token = request.getHeader("Authorization");
        if (token != null) {
            final String[] values = token.trim().split(" ");
            if (values.length == 2 && "Bearer".equals(values[0])) {
                return values[1];
            }
            if (values.length == 1) {
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

    public void setRandom(final Random random) {
        this.random = random;
    }
}
