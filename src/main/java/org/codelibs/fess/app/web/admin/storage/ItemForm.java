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
package org.codelibs.fess.app.web.admin.storage;

import org.lastaflute.web.ruts.multipart.MultipartFormFile;

import jakarta.validation.constraints.Size;

/**
 * Form class for storage item operations in the admin interface.
 * This form handles file upload and management operations for storage items.
 */
public class ItemForm {

    /** The path of the storage item */
    public String path;

    /**
     * Default constructor.
     */
    public ItemForm() {
        // Default constructor
    }

    /** The name of the storage item, limited to 100 characters */
    @Size(max = 100)
    public String name;

    /** The file to be uploaded for the storage item */
    public MultipartFormFile uploadFile;
}
