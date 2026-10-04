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
package org.codelibs.fess.app.web.admin.tagtype;

import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.validation.CustomSize;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * The create form for Tag Type.
 */
public class CreateForm {

    /**
     * Creates a new CreateForm instance.
     */
    public CreateForm() {
    }

    /**
     * The CRUD mode for the form.
     */
    @ValidateTypeFailure
    public Integer crudMode;

    /**
     * The name of the tag, normalized when it is stored.
     */
    @Required
    @Size(max = 1000)
    public String name;

    /**
     * The user who owns the tag.
     */
    @Required
    @Size(max = 1000)
    public String owner;

    /**
     * The URLs of the tagged documents, one per line.
     */
    public String paths;

    /**
     * The permissions required to view this tag, one per line. The owner only when empty.
     */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String permissions;

    /**
     * The virtual host for the tag.
     */
    @Size(max = 1000)
    public String virtualHost;

    /**
     * The sort order for displaying this tag.
     */
    @Min(value = 0)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer sortOrder;

    /**
     * Initializes the form with default values.
     */
    public void initialize() {
        crudMode = CrudMode.CREATE;
        sortOrder = 0;
    }
}
