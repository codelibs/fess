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
package org.codelibs.fess.app.web.admin.dict.synonym;

import org.codelibs.fess.app.web.CrudMode;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * Form class for creating new synonym dictionary entries.
 * This form handles the creation of synonym mappings that expand
 * search queries to include related terms.
 *
 * @author shinsuke
 */
public class CreateForm {

    /**
     * Creates a new CreateForm instance.
     */
    public CreateForm() {
    }

    /** The dictionary ID to which this synonym entry belongs */
    @Required
    public String dictId;

    /** The CRUD operation mode for form processing */
    @ValidateTypeFailure
    public Integer crudMode;

    /** The input terms that should be considered synonymous */
    @Required
    @Size(max = 1000)
    public String inputs;

    /** The output synonyms that should be matched for the input terms */
    @Required
    @Size(max = 1000)
    public String outputs;

    /**
     * Initializes the form with default values for creating a new synonym entry.
     */
    public void initialize() {
        crudMode = CrudMode.CREATE;
    }
}
