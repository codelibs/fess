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
package org.codelibs.fess.app.web.admin.dataconfig;

import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.validation.CustomSize;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Form class for creating data store configurations.
 * Data configs allow administrators to set up crawling of various data sources
 * including databases, CSV files, and other structured data sources.
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

    /** Configuration name for identifying this data source */
    @Required
    @Size(max = 200)
    public String name;

    /** Optional description of this data configuration */
    @Size(max = 1000)
    public String description;

    /** Handler class name for processing this data source */
    @Required
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String handlerName;

    /** Parameters passed to the data handler */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String handlerParameter;

    /** Script for custom data processing logic */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String handlerScript;

    /** Boost value for documents from this data source */
    @Required
    @ValidateTypeFailure
    public Float boost;

    /** Whether this configuration is enabled (true/false) */
    @Required
    @Size(max = 5)
    public String available;

    /** Access permissions for documents from this data source */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String permissions;

    /** Virtual hosts where this configuration applies */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String virtualHosts;

    /** Sort order for displaying configurations */
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
     * Initializes the form with default values for creating a new data configuration.
     */
    public void initialize() {
        crudMode = CrudMode.CREATE;
        boost = 1.0f;
        sortOrder = 0;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        permissions = ComponentUtil.getFessConfig().getSearchDefaultDisplayPermission();
    }
}
