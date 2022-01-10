/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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
package org.codelibs.fess.util;

import org.codelibs.fess.exception.WebApiException;
import org.lastaflute.web.util.LaRequestUtil;

public final class WebApiUtil {

    private static final String WEB_API_EXCEPTION = "webApiException";

    private WebApiUtil() {
    }

    public static void setObject(final String name, final Object value) {
        LaRequestUtil.getRequest().setAttribute(name, value);
    }

    public static <T> T getObject(final String name) {
        return (T) LaRequestUtil.getRequest().getAttribute(name);
    }

    public static void setError(final int statusCode, final String message) {
        LaRequestUtil.getRequest().setAttribute(WEB_API_EXCEPTION, new WebApiException(statusCode, message));
    }

    public static void setError(final int statusCode, final Exception e) {
        LaRequestUtil.getRequest().setAttribute(WEB_API_EXCEPTION, new WebApiException(statusCode, e));
    }

    public static void validate() {
        final WebApiException e = (WebApiException) LaRequestUtil.getRequest().getAttribute(WEB_API_EXCEPTION);
        if (e != null) {
            throw e;
        }
    }

}
