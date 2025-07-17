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
package org.codelibs.fess.app.web.admin.accesstoken;

import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.validation.CustomSize;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Form class for creating access tokens in the admin interface.
 * This form handles the creation of API access tokens with configurable permissions and expiration dates.
 */
public class CreateForm {

    /**
     * Creates a new CreateForm instance.
     */
    public CreateForm() {
        // Default constructor
    }

    /**
     * The CRUD operation mode for this form.
     * Indicates whether this is a create, read, update, or delete operation.
     */
    @ValidateTypeFailure
    public Integer crudMode;

    /**
     * The name of the access token.
     * This is a required field with a maximum length of 1000 characters.
     */
    @Required
    @Size(max = 1000)
    public String name;

    /**
     * The actual access token string.
     * This is the token value that will be used for authentication.
     * Maximum length is 10000 characters.
     */
    @Size(max = 10000)
    public String token;

    /**
     * The permissions associated with this access token.
     * Defines what operations and resources this token can access.
     */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String permissions;

    /**
     * The parameter name for the access token.
     * This field specifies how the token should be passed in API requests.
     * Maximum length is 10000 characters.
     */
    @Size(max = 10000)
    public String parameterName;

    /**
     * The expiration date and time for the access token.
     * Must be in ISO 8601 format: YYYY-MM-DDTHH:MM:SS
     */
    @Pattern(regexp = "[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]T[0-9][0-9]:[0-9][0-9]:[0-9][0-9]")
    public String expires;

    /**
     * The username of the user who created this access token.
     * Maximum length is 1000 characters.
     */
    @Size(max = 1000)
    public String createdBy;

    /**
     * The timestamp when this access token was created.
     * Stored as a long value representing milliseconds since epoch.
     */
    @ValidateTypeFailure
    public Long createdTime;

    /**
     * Initializes the form with default values for creation.
     * Sets the CRUD mode to CREATE and populates created by and created time fields
     * with current user and timestamp information.
     */
    public void initialize() {
        crudMode = CrudMode.CREATE;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
    }
}