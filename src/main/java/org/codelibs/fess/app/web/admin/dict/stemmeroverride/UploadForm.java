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
package org.codelibs.fess.app.web.admin.dict.stemmeroverride;

import org.lastaflute.web.ruts.multipart.MultipartFormFile;
import org.lastaflute.web.validation.Required;

/**
 * Form for uploading stemmer override files to the Fess search engine.
 * Stemmer override allows custom rules to override the default stemming behavior for specific words.
 * This form is used in the admin interface to upload custom stemmer override dictionary files.
 */
public class UploadForm {

    /**
     * The dictionary ID that identifies which stemmer override dictionary configuration to update.
     * This ID corresponds to a specific stemmer override dictionary instance in the system.
     */
    @Required
    public String dictId;

    /**
     * The multipart file containing the stemmer override rules to be uploaded.
     * This file should contain mappings that override default stemming for specific words.
     */
    @Required
    public MultipartFormFile stemmerOverrideFile;

    /**
     * Default constructor for UploadForm.
     * Creates a new instance with default values.
     */
    public UploadForm() {
        // Default constructor
    }

}