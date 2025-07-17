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
package org.codelibs.fess.app.web.admin.design;

import org.lastaflute.web.ruts.multipart.MultipartFormFile;
import org.lastaflute.web.validation.Required;

import jakarta.validation.constraints.Pattern;

/**
 * Form for uploading design files to customize the appearance of the Fess search interface.
 * This form is used in the admin interface to upload CSS, JSP, and other design-related files
 * that customize the look and feel of the search application.
 */
public class UploadForm {

    /**
     * The multipart file containing design resources to be uploaded.
     * This can include CSS files, JSP templates, images, or other design assets.
     */
    @Required
    public MultipartFormFile designFile;

    /**
     * The name for the design file being uploaded.
     * Must not contain invalid file system characters like backslash, colon, asterisk, etc.
     */
    @Pattern(regexp = "^[^\\\\|/|:|\\*|?|\"|<|>|\\|]+$", message = "{errors.design_file_name_is_invalid}")
    public String designFileName;

    /**
     * Default constructor for UploadForm.
     * Creates a new instance with default values.
     */
    public UploadForm() {
        // Default constructor
    }
}
