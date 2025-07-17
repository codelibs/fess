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
package org.codelibs.fess.app.web.admin.duplicatehost;

import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Form class for creating new duplicate host configuration entries.
 * This form handles the creation of duplicate host mappings that redirect
 * crawling from duplicate hostnames to the regular canonical hostname.
 */
public class CreateForm {

    /**
     * Creates a new CreateForm instance.
     */
    public CreateForm() {
    }

    /** The CRUD operation mode for form processing */
    @ValidateTypeFailure
    public Integer crudMode;

    /** The regular canonical hostname that should be used */
    @Required
    @Size(max = 1000)
    public String regularName;

    /** The duplicate hostname that should be redirected to the regular name */
    @Required
    @Size(max = 1000)
    public String duplicateHostName;

    /** The sort order for displaying this duplicate host entry */
    @Required
    @Min(value = 0)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer sortOrder;

    /** The username of who created this duplicate host entry */
    @Size(max = 1000)
    public String createdBy;

    /** The timestamp when this duplicate host entry was created */
    @ValidateTypeFailure
    public Long createdTime;

    /**
     * Initializes the form with default values for creating a new duplicate host entry.
     */
    public void initialize() {
        crudMode = CrudMode.CREATE;
        sortOrder = 0;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
    }
}
