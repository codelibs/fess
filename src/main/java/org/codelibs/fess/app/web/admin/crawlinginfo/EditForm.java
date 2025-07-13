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
package org.codelibs.fess.app.web.admin.crawlinginfo;

import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * Form class for editing crawling information in the admin interface.
 * This form handles the editing of crawling session data, which tracks
 * the status and metadata of web crawling operations.
 *
 */
public class EditForm {

    /**
     * Creates a new EditForm instance.
     */
    public EditForm() {
        // Default constructor
    }

    /**
     * The CRUD operation mode for this form.
     * Indicates whether this is a create, read, update, or delete operation.
     */
    @ValidateTypeFailure
    public int crudMode;

    /**
     * The unique identifier of the crawling information entry being edited.
     * This is a required field for identifying which crawling info to update.
     */
    @Required
    @Size(max = 1000)
    public String id;

    /**
     * The session identifier of the crawling session.
     * This is a required field that identifies the specific crawling session.
     * Maximum length is 20 characters.
     */
    @Required
    @Size(max = 20)
    public String sessionId;

    /**
     * The name or description of the crawling session.
     * This is an optional descriptive field with a maximum length of 20 characters.
     */
    @Size(max = 20)
    public String name;

    /**
     * The expiration time for the crawling session.
     * This field indicates when the crawling session should expire or be cleaned up.
     */
    public String expiredTime;

    /**
     * The timestamp when this crawling session was created.
     * Stored as a long value representing milliseconds since epoch.
     */
    @ValidateTypeFailure
    public Long createdTime;

    /**
     * Initializes the form with default null values.
     * This method resets all fields to their default state for creating a new entry.
     */
    public void initialize() {

        id = null;
        sessionId = null;
        name = null;
        expiredTime = null;
        createdTime = null;

    }
}