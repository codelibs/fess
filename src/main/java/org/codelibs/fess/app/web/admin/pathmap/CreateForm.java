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
package org.codelibs.fess.app.web.admin.pathmap;

import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * The create form for Path Map.
 */
public class CreateForm {

    /**
     * Creates a new CreateForm instance.
     */
    public CreateForm() {
    }

    /**
     * The CRUD operation mode (create, update, etc.).
     */
    @ValidateTypeFailure
    public Integer crudMode;

    /**
     * The regular expression pattern to match request paths.
     */
    @Required
    @Size(max = 1000)
    public String regex;

    /**
     * The replacement pattern for matched paths.
     */
    @Size(max = 1000)
    public String replacement;

    /**
     * The processing type for path mapping.
     */
    @Required
    public String processType;

    /**
     * The sort order for this path mapping (0-2147483647).
     */
    @Required
    @Min(value = 0)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer sortOrder;

    /**
     * The username who created this path mapping.
     */
    @Size(max = 1000)
    public String createdBy;

    /**
     * The timestamp when this path mapping was created.
     */
    @ValidateTypeFailure
    public Long createdTime;

    /**
     * The user agent string for this path mapping.
     */
    @Size(max = 1000)
    public String userAgent;

    /**
     * Initializes the form with default values for creating a new path mapping.
     */
    public void initialize() {
        crudMode = CrudMode.CREATE;
        sortOrder = 0;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
    }
}
