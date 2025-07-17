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
package org.codelibs.fess.app.web.admin.dict.protwords;

import org.codelibs.fess.app.web.CrudMode;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * Form class for creating protected words dictionary entries.
 * Protected words are terms that should not be modified or analyzed during
 * text processing, preserving their original form in search indexes.
 *
 */
public class CreateForm {

    /**
     * Creates a new CreateForm instance.
     */
    public CreateForm() {
        // Default constructor
    }

    /** Dictionary identifier */
    @Required
    public String dictId;

    /** CRUD operation mode (CREATE, EDIT, etc.) */
    @ValidateTypeFailure
    public Integer crudMode;

    /** Word or phrase to be protected from analysis */
    @Required
    @Size(max = 1000)
    public String input;

    /**
     * Initializes the form with default values for creating a new protected words entry.
     */
    public void initialize() {
        crudMode = CrudMode.CREATE;
    }
}
