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
package org.codelibs.fess.app.web.admin.dict.mapping;

import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

/**
 * Form class for editing mapping dictionary entries in the admin interface.
 * This form extends CreateForm to include fields necessary for updating existing mapping dictionary entries.
 * Mapping dictionaries define field mappings and transformations during the indexing process.
 *
 */
public class EditForm extends CreateForm {

    /**
     * Creates a new EditForm instance.
     */
    public EditForm() {
        // Default constructor
    }

    /**
     * The unique identifier of the mapping dictionary entry being edited.
     * This is a required field for identifying which dictionary entry to update.
     */
    @Required
    @ValidateTypeFailure
    public Long id;

    /**
     * Returns a display-friendly identifier combining the dictionary ID and entry ID.
     * This method creates a composite identifier for UI display purposes.
     *
     * @return A string in the format "dictId:id" for display purposes
     */
    public String getDisplayId() {
        return dictId + ":" + id;
    }
}
