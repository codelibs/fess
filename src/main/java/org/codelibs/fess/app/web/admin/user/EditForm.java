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
package org.codelibs.fess.app.web.admin.user;

import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * Form class for editing users in the admin interface.
 * This form extends CreateForm to include fields necessary for updating existing user entries,
 * including tracking information for optimistic locking.
 * Users represent individual accounts that can access and search within the system.
 *
 */
public class EditForm extends CreateForm {

    /**
     * Creates a new EditForm instance.
     */
    public EditForm() {
        super();
    }

    /**
     * The unique identifier of the user being edited.
     * This is a required field for identifying which user to update.
     */
    @Required
    @Size(max = 1000)
    public String id;

    /**
     * The version number of the user for optimistic locking.
     * This field is required to prevent concurrent modification conflicts
     * by ensuring the user hasn't been modified by another process.
     */
    @Required
    @ValidateTypeFailure
    public Integer versionNo;

}