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
package org.codelibs.fess.app.web.admin.keymatch;

import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * Form class for editing key match configurations in the admin interface.
 * This form extends CreateForm to include fields necessary for updating existing key match entries,
 * including tracking information for optimistic locking and audit trails.
 * Key matches are used to promote specific documents when certain keywords are searched.
 */
public class EditForm extends CreateForm {

    /**
     * Creates a new EditForm instance.
     */
    public EditForm() {
        // Default constructor
    }

    /**
     * The unique identifier of the key match configuration being edited.
     * This is a required field for identifying which key match to update.
     */
    @Required
    @Size(max = 1000)
    public String id;

    /**
     * The username of the user who last updated this key match configuration.
     * Used for audit trail purposes to track who made changes.
     * Maximum length is 255 characters.
     */
    @Size(max = 255)
    public String updatedBy;

    /**
     * The timestamp when this key match configuration was last updated.
     * Stored as a long value representing milliseconds since epoch.
     * Used for audit trail and concurrency control.
     */
    @ValidateTypeFailure
    public Long updatedTime;

    /**
     * The version number of the key match configuration for optimistic locking.
     * This field is required to prevent concurrent modification conflicts
     * by ensuring the configuration hasn't been modified by another process.
     */
    @Required
    @ValidateTypeFailure
    public Integer versionNo;

}
