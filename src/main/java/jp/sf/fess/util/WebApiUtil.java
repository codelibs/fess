/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.util;

import jp.sf.fess.WebApiException;

import org.seasar.struts.util.RequestUtil;

public final class WebApiUtil {

    private static final String WEB_API_EXCEPTION = "webApiException";

    private WebApiUtil() {
    }

    public static void setObject(final String name, final Object value) {
        RequestUtil.getRequest().setAttribute(name, value);
    }

    public static <T> T getObject(final String name) {
        @SuppressWarnings("unchecked")
        final T value = (T) RequestUtil.getRequest().getAttribute(name);
        return value;
    }

    public static void setError(final int statusCode, final String message) {
        RequestUtil.getRequest().setAttribute(WEB_API_EXCEPTION,
                new WebApiException(statusCode, message));
    }

    public static void setError(final int statusCode, final Exception e) {
        RequestUtil.getRequest().setAttribute(WEB_API_EXCEPTION,
                new WebApiException(statusCode, e));
    }

    public static void validate() {
        final WebApiException e = (WebApiException) RequestUtil.getRequest()
                .getAttribute(WEB_API_EXCEPTION);
        if (e != null) {
            throw e;
        }
    }

}
