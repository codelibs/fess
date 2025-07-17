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
package org.codelibs.fess.app.web.admin.dict.stemmeroverride;

import org.codelibs.fess.app.web.CrudMode;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * Form class for creating new stemmer override dictionary entries.
 * This form handles the creation of stemmer override rules that modify
 * the stemming behavior for specific words.
 *
 */
public class CreateForm {

    /**
     * Creates a new CreateForm instance.
     */
    public CreateForm() {
    }

    /** The dictionary ID to which this stemmer override entry belongs */
    @Required
    public String dictId;

    /** The CRUD operation mode for form processing */
    @ValidateTypeFailure
    public Integer crudMode;

    /** The input word that should be stemmed differently */
    @Required
    @Size(max = 1000)
    public String input;

    /** The desired stem output for the input word */
    @Required
    @Size(max = 1000)
    public String output;

    /**
     * Initializes the form with default values for creating a new stemmer override entry.
     */
    public void initialize() {
        crudMode = CrudMode.CREATE;
    }
}
