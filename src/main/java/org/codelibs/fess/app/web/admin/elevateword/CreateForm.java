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
package org.codelibs.fess.app.web.admin.elevateword;

import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.validation.CustomSize;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * Form class for creating new elevate word configuration entries.
 * This form handles the creation of elevate word rules that boost
 * specific documents in search results when certain keywords are matched.
 */
public class CreateForm {

    /**
     * Creates a new CreateForm instance.
     */
    public CreateForm() {
        // Default constructor
    }

    /** The label type IDs associated with this elevate word entry */
    public String[] labelTypeIds;

    /** The CRUD operation mode for form processing */
    @ValidateTypeFailure
    public Integer crudMode;

    /** The word that should trigger document elevation in search results */
    @Required
    public String suggestWord;

    /** The reading/pronunciation of the suggest word for language analysis */
    public String reading;

    /** The target label for filtering documents to be elevated */
    public String targetLabel;

    /** The permission settings for accessing this elevate word configuration */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String permissions;

    /** The boost score multiplier applied to elevated documents */
    @Required
    @ValidateTypeFailure
    public Float boost;

    /** The username of who created this elevate word entry */
    @Size(max = 1000)
    public String createdBy;

    /** The timestamp when this elevate word entry was created */
    @ValidateTypeFailure
    public Long createdTime;

    /**
     * Initializes the form with default values for creating a new elevate word entry.
     */
    public void initialize() {
        boost = 100.0f;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        permissions = ComponentUtil.getFessConfig().getSearchDefaultDisplayPermission();
    }
}
