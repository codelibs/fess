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
package org.codelibs.fess.app.web.admin.badword;

import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * The create form for Bad Word.
 */
public class CreateForm {

    /**
     * Creates a new CreateForm instance.
     */
    public CreateForm() {
        // Default constructor
    }

    /**
     * The CRUD mode for form processing (create, update, delete operations).
     */
    @ValidateTypeFailure
    public Integer crudMode;

    /**
     * The bad word to be filtered from search suggestions.
     */
    @Required
    @Pattern(regexp = "[^\\s]+")
    public String suggestWord;

    /**
     * The username of the user who created this bad word entry.
     */
    @Size(max = 1000)
    public String createdBy;

    /**
     * The timestamp when this bad word entry was created.
     */
    @ValidateTypeFailure
    public Long createdTime;

    /**
     * Initializes the form with default values including current user and timestamp.
     */
    public void initialize() {
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
    }
}
