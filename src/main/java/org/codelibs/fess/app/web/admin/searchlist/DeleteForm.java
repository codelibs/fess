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
package org.codelibs.fess.app.web.admin.searchlist;

import org.lastaflute.web.validation.Required;

import jakarta.validation.constraints.Size;

/**
 * Form class for deleting documents from search results in the admin interface.
 * Contains query and document ID fields for targeted document deletion.
 */
public class DeleteForm {

    /**
     * Creates a new instance of DeleteForm.
     * This constructor initializes the form for deleting documents from search results
     * in the admin interface with validation rules for query and document identification.
     */
    public DeleteForm() {
        // Default constructor with explicit documentation
    }

    /**
     * The search query used to find the document.
     * Optional field with maximum length of 1000 characters.
     */
    @Size(max = 1000)
    public String q;

    /**
     * The document ID of the document to delete.
     * Required field for identifying the specific document.
     */
    @Required
    public String docId;

}
