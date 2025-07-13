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
package org.codelibs.fess.app.web.api.admin.documents;

import java.util.List;
import java.util.Map;

/**
 * Request body for bulk document operations containing a list of documents.
 */
public class BulkBody {

    /**
     * Creates a new instance of BulkBody.
     */
    public BulkBody() {
        // Default constructor
    }

    /**
     * List of documents to be processed in bulk operations.
     */
    public List<Map<String, Object>> documents;

}
