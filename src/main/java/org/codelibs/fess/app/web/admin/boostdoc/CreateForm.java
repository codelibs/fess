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
package org.codelibs.fess.app.web.admin.boostdoc;

import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Form class for creating boost document configurations.
 * Boost documents allow administrators to define URL patterns and boost expressions
 * to influence search result rankings for specific documents.
 */
public class CreateForm {

    /**
     * Creates a new CreateForm instance.
     */
    public CreateForm() {
        // Default constructor
    }

    /** CRUD operation mode (CREATE, EDIT, etc.) */
    @ValidateTypeFailure
    public Integer crudMode;

    /** URL expression pattern to match documents for boosting */
    @Required
    @Size(max = 10000)
    public String urlExpr;

    /** Boost expression to apply to matching documents */
    @Required
    @Size(max = 10000)
    public String boostExpr;

    /** Sort order for displaying boost configurations */
    @Required
    @Min(value = 0)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer sortOrder;

    /** User who created this configuration */
    @Size(max = 1000)
    public String createdBy;

    /** Timestamp when this configuration was created */
    @ValidateTypeFailure
    public Long createdTime;

    /**
     * Initializes the form with default values for creating a new boost document configuration.
     */
    public void initialize() {
        crudMode = CrudMode.CREATE;
        sortOrder = 0;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
    }
}
