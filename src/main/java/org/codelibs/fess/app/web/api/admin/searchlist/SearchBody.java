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
package org.codelibs.fess.app.web.api.admin.searchlist;

import org.codelibs.fess.app.web.admin.searchlist.ListForm;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

/**
 * Search request body for search list administration API.
 */
public class SearchBody extends ListForm {

    /**
     * Default constructor.
     */
    public SearchBody() {
        super();
    }

    // `size` is an alias of `num`.
    // `size` is prepared to be compatible with other Admin APIs
    /** Number of search results to retrieve (alias for num) */
    @ValidateTypeFailure
    public Integer size;

    @Override
    public void initialize() {
        if (size != null) {
            num = num == null || num < size ? size : num;
        }
        super.initialize();
    }

}
