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
package org.codelibs.fess.app.web.admin.dict.kuromoji;

import org.codelibs.fess.app.web.CrudMode;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * Form class for creating Kuromoji dictionary entries.
 * Kuromoji is a Japanese morphological analyzer and this form allows
 * administrators to add custom dictionary entries for better Japanese text analysis.
 *
 * @author shinsuke
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

    /** Token (word) to be added to the dictionary */
    @Required
    @Size(max = 1000)
    public String token;

    /** Segmentation information for the token */
    @Required
    @Size(max = 1000)
    public String segmentation;

    /** Reading (pronunciation) of the token in katakana */
    @Required
    @Size(max = 1000)
    public String reading;

    /** Part of speech tag for the token */
    @Required
    @Size(max = 1000)
    public String pos;

    /**
     * Initializes the form with default values for creating a new Kuromoji dictionary entry.
     */
    public void initialize() {
        crudMode = CrudMode.CREATE;
    }
}
