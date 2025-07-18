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

import org.lastaflute.web.ruts.multipart.MultipartFormFile;
import org.lastaflute.web.validation.Required;

/**
 * Form for uploading elevate word files to the Fess search engine.
 * Elevate words are terms that should be promoted or given higher ranking in search results.
 * This form is used in the admin interface to upload elevate word configuration files.
 */
public class UploadForm {

    /**
     * The multipart file containing the elevate word configurations to be uploaded.
     * This file should contain definitions for words or documents that should be elevated in search results.
     */
    @Required
    public MultipartFormFile elevateWordFile;

    /**
     * Default constructor for UploadForm.
     * Creates a new instance with default values.
     */
    public UploadForm() {
        // Default constructor
    }

}
