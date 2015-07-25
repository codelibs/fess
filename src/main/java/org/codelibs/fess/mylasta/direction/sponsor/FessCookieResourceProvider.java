/*
 * Copyright 2014-2015 the original author or authors.
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
package org.codelibs.fess.mylasta.direction.sponsor;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.lastaflute.core.security.InvertibleCryptographer;
import org.lastaflute.web.servlet.cookie.CookieResourceProvider;

/**
 * @author jflute
 */
public class FessCookieResourceProvider implements CookieResourceProvider {

    protected final FessConfig harborConfig;
    protected final InvertibleCryptographer cookieCipher;

    public FessCookieResourceProvider(FessConfig harborConfig, InvertibleCryptographer cookieCipher) {
        this.harborConfig = harborConfig;
        this.cookieCipher = cookieCipher;
    }

    public String provideDefaultPath() {
        return harborConfig.getCookieDefaultPath();
    }

    public Integer provideDefaultExpire() {
        return harborConfig.getCookieDefaultExpireAsInteger();
    }

    public InvertibleCryptographer provideCipher() {
        return cookieCipher;
    }
}
