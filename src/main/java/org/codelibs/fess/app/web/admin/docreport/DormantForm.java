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
package org.codelibs.fess.app.web.admin.docreport;

import jakarta.validation.constraints.Size;

/**
 * The GET filter form of the dormant tab of the document report.
 */
public class DormantForm {

    /**
     * Default constructor for DormantForm.
     */
    public DormantForm() {
    }

    /**
     * Only documents whose URL starts with this prefix; blank means all documents.
     */
    @Size(max = 1000)
    public String url;

    /**
     * The number of days since the last modification; blank or invalid means docreport.dormant.days.
     */
    @Size(max = 10)
    public String days;

    /**
     * "true" to keep only the documents that have never been opened from a search result.
     */
    @Size(max = 10)
    public String unclicked;

    /**
     * The page number, starting at 1.
     */
    @Size(max = 10)
    public String pn;
}
