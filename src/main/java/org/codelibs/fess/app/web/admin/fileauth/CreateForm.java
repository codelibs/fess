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
package org.codelibs.fess.app.web.admin.fileauth;

import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * The create form for File Authentication.
 */
public class CreateForm {

    /**
     * Creates a new CreateForm instance.
     */
    public CreateForm() {
        // Default constructor
    }

    /** The CRUD operation mode for this form. */
    @ValidateTypeFailure
    public Integer crudMode;

    /** The hostname of the file server (maximum 100 characters). */
    @Size(max = 100)
    public String hostname;

    /** The port number of the file server (0 to 2147483647). */
    @Min(value = 0)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer port;

    /** The protocol scheme for file access (maximum 10 characters). */
    @Size(max = 10)
    public String protocolScheme;

    /** The username for file authentication (required, maximum 100 characters). */
    @Required
    @Size(max = 100)
    public String username;

    /** The password for file authentication (maximum 100 characters). */
    @Size(max = 100)
    public String password;

    /** Additional parameters for file authentication (maximum 1000 characters). */
    @Size(max = 1000)
    public String parameters;

    /** The ID of the associated file configuration (required, maximum 1000 characters). */
    @Required
    @Size(max = 1000)
    public String fileConfigId;

    /** The user who created this file authentication configuration (maximum 1000 characters). */
    @Size(max = 1000)
    public String createdBy;

    /** The timestamp when this file authentication configuration was created. */
    @ValidateTypeFailure
    public Long createdTime;

    /**
     * Initializes the form with default values for creation mode.
     */
    public void initialize() {
        crudMode = CrudMode.CREATE;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
    }

}
