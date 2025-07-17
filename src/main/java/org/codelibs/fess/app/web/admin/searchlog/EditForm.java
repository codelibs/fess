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
package org.codelibs.fess.app.web.admin.searchlog;

import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * The edit form for Search Log.
 * This form handles the editing of search log entries in the administration interface.
 */
public class EditForm {

    /** CRUD operation mode indicator */
    @ValidateTypeFailure
    public int crudMode;

    /** Type of the search log entry */
    @Required
    @Size(max = 10)
    public String logType;

    /** Unique identifier for the search log entry */
    @Required
    @Size(max = 1000)
    public String id;

    /**
     * Default constructor for EditForm.
     */
    public EditForm() {
        // Default constructor
    }

    /**
     * Initializes the form by resetting all fields to their default values.
     */
    public void initialize() {
        id = null;
        logType = null;
    }
}
