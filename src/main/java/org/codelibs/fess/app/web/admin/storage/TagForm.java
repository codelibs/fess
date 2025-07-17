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

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

import jakarta.validation.constraints.Size;

/**
 * Form class for handling tag operations in storage management.
 * This form represents tag data including path, name, and associated metadata.
 */
public class TagForm {

    /**
     * The file or directory path associated with this tag.
     */
    @Required
    public String path;

    /**
     * The name of the tag with a maximum length of 100 characters.
     */
    @Required
    @Size(max = 100)
    public String name;

    /**
     * A map containing additional tag metadata as key-value pairs.
     */
    public Map<String, String> tags = new HashMap<>();

    /**
     * Default constructor for TagForm.
     */
    public TagForm() {
        // Default constructor
    }

}
