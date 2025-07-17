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
package org.codelibs.fess.app.web.admin.failureurl;

/**
 * The search form for Failure URL.
 */
public class SearchForm {

    /**
     * Default constructor for SearchForm.
     */
    public SearchForm() {
    }

    /**
     * The URL field for searching failure URLs.
     */
    public String url;

    /**
     * The minimum error count field for filtering failure URLs.
     */
    public String errorCountMin;

    /**
     * The maximum error count field for filtering failure URLs.
     */
    public String errorCountMax;

    /**
     * The error name field for searching failure URLs by error type.
     */
    public String errorName;

}
