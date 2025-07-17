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

/**
 * Form class for editing design files in the admin interface.
 * This form handles the editing of template and design files used
 * for customizing the search interface appearance and layout.
 */
public class EditForm {

    /**
     * Creates a new EditForm instance.
     */
    public EditForm() {
        // Default constructor
    }

    /**
     * The name of the design file being edited.
     * This specifies which template or design file to modify.
     */
    public String fileName;

    /**
     * The content of the design file.
     * This contains the actual template code, CSS, or other design content
     * that will be saved to the specified file.
     */
    public String content;
}
