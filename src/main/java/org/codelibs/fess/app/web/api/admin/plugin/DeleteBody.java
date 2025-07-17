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
package org.codelibs.fess.app.web.api.admin.plugin;

import org.codelibs.fess.app.web.admin.plugin.DeleteForm;

/**
 * Request body class for plugin deletion API endpoint.
 * Extends the standard plugin DeleteForm to provide JSON request body functionality
 * for REST API operations.
 */
public class DeleteBody extends DeleteForm {

    /**
     * Creates a new instance of DeleteBody.
     * This constructor initializes the request body for plugin deletion API operations,
     * extending the standard plugin DeleteForm with JSON request body functionality.
     */
    public DeleteBody() {
        super();
    }
}
