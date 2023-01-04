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
package org.codelibs.fess.exception;

public class WebApiException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    private final int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public WebApiException(final int statusCode, final String message, final Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public WebApiException(final int statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public WebApiException(final int statusCode, final Exception e) {
        this(statusCode, e.getMessage(), e);
    }
}
