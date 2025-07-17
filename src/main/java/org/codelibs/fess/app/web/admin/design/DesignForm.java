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

/**
 * Form class for design file management in the admin interface.
 * Handles upload and management of design templates and CSS files.
 */
public class DesignForm {

    /**
     * Creates a new instance of DesignForm.
     * This constructor initializes the form for design file management
     * in the admin interface, handling upload and management of templates and CSS files.
     */
    public DesignForm() {
        // Default constructor with explicit documentation
    }

    /**
     * The multipart file containing the design content to upload.
     */
    public MultipartFormFile designFile;

    /**
     * The name of the design file being uploaded.
     */
    public String designFileName;

    /**
     * The target file name for the design file.
     */
    public String fileName;
}
