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

import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.util.ComponentUtil;

public class AccessTokenHelper {

    protected Random random = new SecureRandom();

    public String generateAccessToken() {
        return RandomStringUtils.random(ComponentUtil.getFessConfig().getApiAccessTokenLengthAsInteger().intValue(), 0, 0, true, true,
                null, random);
    }

    public String getAccessTokenFromRequest(final HttpServletRequest request) {
        final String token = request.getHeader("Authorization");
        if (token != null) {
            return token;
        }
        final String name = ComponentUtil.getFessConfig().getApiAccessTokenRequestParameter();
        if (StringUtil.isBlank(name)) {
            return null;
        }
        return request.getParameter(name);
    }

    public void setRandom(final Random random) {
        this.random = random;
    }
}
