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
package org.codelibs.fess.app.web.api.admin.boostdoc;

import org.codelibs.fess.app.web.api.admin.BaseSearchBody;

/**
 * Search request body for boost document administration.
 * Extends BaseSearchBody with boost document-specific search parameters.
 */
public class SearchBody extends BaseSearchBody {

    /** The URL expression pattern to search for in boost documents. */
    public String urlExpr;

    /** The boost expression to search for in boost documents. */
    public String boostExpr;

    /**
     * Default constructor for SearchBody.
     */
    public SearchBody() {
        // Default constructor
    }
}
