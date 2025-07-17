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
package org.codelibs.fess.app.web.api.admin.storage;

import org.lastaflute.web.ruts.multipart.MultipartFormFile;
import org.lastaflute.web.validation.Required;

/**
 * Form for uploading files to the storage system via the admin API.
 * This form is used in the admin API interface to upload files to the storage backend
 * with optional path specification for file organization.
 */
public class UploadForm {

    /**
     * The storage path where the file should be uploaded.
     * If not specified, the file will be stored in the default location.
     */
    public String path;

    /**
     * The multipart file to be uploaded to the storage system.
     * This file will be stored in the specified path or default location.
     */
    @Required
    public MultipartFormFile file;

    /**
     * Default constructor for UploadForm.
     * Creates a new instance with default values.
     */
    public UploadForm() {
        // Default constructor
    }

}
