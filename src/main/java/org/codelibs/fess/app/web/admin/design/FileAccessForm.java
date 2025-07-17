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

import org.lastaflute.web.validation.Required;

/**
 * Form class for file access operations in the admin design interface.
 * This form handles file name validation for accessing design files.
 */
public class FileAccessForm {

    /** The name of the file to access (required) */
    @Required
    public String fileName;

    /**
     * Default constructor for file access form.
     * Creates a new instance with default values.
     */
    public FileAccessForm() {
        // Default constructor
    }
}
