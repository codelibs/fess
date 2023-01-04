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

import org.codelibs.fess.crawler.exception.CrawlingAccessException;

public class DataStoreCrawlingException extends CrawlingAccessException {

    private static final long serialVersionUID = 1L;

    private final String url;

    private final boolean abort;

    public DataStoreCrawlingException(final String url, final String message, final Exception e) {
        this(url, message, e, false);
    }

    public DataStoreCrawlingException(final String url, final String message, final Exception e, final boolean abort) {
        super(message, e);
        this.url = url;
        this.abort = abort;
    }

    public String getUrl() {
        return url;
    }

    public boolean aborted() {
        return abort;
    }
}
