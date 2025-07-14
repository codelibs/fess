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
package org.codelibs.fess.app.web.admin.searchlist;

import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

/**
 * The edit form for Search List.
 * This form extends CreateForm and adds fields necessary for editing existing search list entries.
 */
public class EditForm extends CreateForm {

    /** Unique identifier for the search list entry */
    public String id;

    /** Sequence number for ordering search list entries */
    @ValidateTypeFailure
    public Long seqNo;

    /** Primary term value for search list configuration */
    @ValidateTypeFailure
    public Long primaryTerm;

    /**
     * Default constructor for EditForm.
     */
    public EditForm() {
        // Default constructor
    }
}
