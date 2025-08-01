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
package org.codelibs.fess.app.web.api.admin.duplicatehost;

import org.codelibs.fess.app.web.api.admin.BaseSearchBody;

/**
 * Search request body for duplicate host administration.
 * Extends BaseSearchBody with duplicate host-specific search parameters.
 */
public class SearchBody extends BaseSearchBody {

    /** The regular host name to search for. */
    public String regularName;

    /** The duplicate host name to search for. */
    public String duplicateHostName;

    /**
     * Default constructor for SearchBody.
     */
    public SearchBody() {
        super();
    }
}
