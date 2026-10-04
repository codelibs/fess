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

import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Size;

/**
 * Form class for editing tag types in the admin interface. The sequence number and primary term
 * of the tag type as it was read are sent back, so that a change made since then by its owner or
 * another administrator is reported instead of overwritten.
 */
public class EditForm extends CreateForm {

    /**
     * Creates a new EditForm instance.
     */
    public EditForm() {
    }

    /**
     * The unique identifier of the tag type being edited.
     */
    @Required
    @Size(max = 1000)
    public String id;

    /**
     * The sequence number of the tag type as it was read.
     */
    @Required
    @ValidateTypeFailure
    public Long seqNo;

    /**
     * The primary term of the tag type as it was read.
     */
    @Required
    @ValidateTypeFailure
    public Long primaryTerm;

}
