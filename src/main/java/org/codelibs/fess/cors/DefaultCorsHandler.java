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
package org.codelibs.fess.cors;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

public class DefaultCorsHandler extends CorsHandler {

    @PostConstruct
    public void register() {
        final CorsHandlerFactory factory = ComponentUtil.getCorsHandlerFactory();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        fessConfig.getApiCorsAllowOriginList().forEach(s -> factory.add(s, this));
    }

    @Override
    public void process(final String origin, final ServletRequest request, final ServletResponse response) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        httpResponse.addHeader(ACCESS_CONTROL_ALLOW_METHODS, fessConfig.getApiCorsAllowMethods());
        httpResponse.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, fessConfig.getApiCorsAllowHeaders());
        httpResponse.addHeader(ACCESS_CONTROL_MAX_AGE, fessConfig.getApiCorsMaxAge());
        httpResponse.addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, fessConfig.getApiCorsAllowCredentials());
    }

}
