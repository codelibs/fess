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
package org.codelibs.fess.exception;

/**
 * Exception thrown when requested content cannot be found.
 * Typically used when a document or resource is not available during crawling or indexing.
 */
public class ContentNotFoundException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ContentNotFoundException with the specified parent URL and URL.
     *
     * @param parentUrl the URL of the parent document
     * @param url the URL of the content that was not found
     */
    public ContentNotFoundException(final String parentUrl, final String url) {
        super("Not Found: " + url + " Parent: " + parentUrl, false, false);
    }

}
