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
package org.codelibs.fess.app.web.api.admin.dict;

import org.codelibs.fess.app.web.api.admin.BaseSearchBody;
import org.lastaflute.web.validation.Required;

/**
 * Base class for dictionary search request body objects in admin API.
 * Extends BaseSearchBody with dictionary-specific parameters.
 */
public class BaseSearchDictBody extends BaseSearchBody {
    /** The dictionary ID for the search operation. */
    @Required
    public String dictId;

    /**
     * Default constructor for BaseSearchDictBody.
     */
    public BaseSearchDictBody() {
        // Default constructor
    }
}
