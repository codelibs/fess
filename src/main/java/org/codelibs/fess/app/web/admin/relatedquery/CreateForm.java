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
package org.codelibs.fess.app.web.admin.relatedquery;

import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * The create form for managing related queries.
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
     * The search term for which related queries are shown.
     */
    @Required
    @Size(max = 10000)
    public String term;

    /**
     * The related queries to be suggested (one per line).
     */
    @Required
    @Size(max = 10000)
    public String queries;

    /**
     * The virtual host for which these related queries apply.
     */
    @Size(max = 1000)
    public String virtualHost;

    /**
     * The username who created these related queries.
     */
    @Size(max = 1000)
    public String createdBy;

    /**
     * The timestamp when these related queries were created.
     */
    @ValidateTypeFailure
    public Long createdTime;

    /**
     * Initializes the form with default values for creating new related queries.
     */
    public void initialize() {
        crudMode = CrudMode.CREATE;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
    }
}
