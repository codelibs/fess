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
package org.codelibs.fess.app.web.admin.dict.kuromoji;

import org.lastaflute.web.validation.Required;

/**
 * The download form for Kuromoji dictionary.
 * This form is used for downloading Kuromoji dictionary files from the admin interface.
 *
 */
public class DownloadForm {

    /**
     * Dictionary ID for identifying the Kuromoji dictionary to download.
     */
    @Required
    public String dictId;

    /**
     * Default constructor.
     */
    public DownloadForm() {
        // Use of default constructor
    }
}
