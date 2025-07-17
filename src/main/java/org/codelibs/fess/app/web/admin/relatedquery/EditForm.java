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
package org.codelibs.fess.app.web.admin.relatedquery;

import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * The edit form for Related Query.
 * This form extends CreateForm and adds fields necessary for editing existing related query entries.
 */
public class EditForm extends CreateForm {

    /** Unique identifier for the related query entry */
    @Required
    @Size(max = 1000)
    public String id;

    /** Username of the user who last updated this entry */
    @Size(max = 1000)
    public String updatedBy;

    /** Timestamp when this entry was last updated */
    @ValidateTypeFailure
    public Long updatedTime;

    /** Version number for optimistic locking */
    @Required
    @ValidateTypeFailure
    public Integer versionNo;

    /**
     * Default constructor for EditForm.
     */
    public EditForm() {
        super();
    }

}
