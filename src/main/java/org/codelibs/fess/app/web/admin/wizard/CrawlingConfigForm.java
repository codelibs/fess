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
package org.codelibs.fess.app.web.admin.wizard;

import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Form class for crawling configuration wizard in the admin interface.
 * Contains validation constraints for creating new crawling configurations.
 */
public class CrawlingConfigForm {

    /**
     * Creates a new form instance.
     */
    public CrawlingConfigForm() {
        // Default constructor
    }

    /**
     * Name of the crawling configuration.
     */
    @Required
    @Size(max = 200)
    public String crawlingConfigName;

    /**
     * Path or URL to be crawled by this configuration.
     */
    @Required
    @Size(max = 1000)
    public String crawlingConfigPath;

    /**
     * Maximum depth for crawling (how many levels deep to follow links).
     */
    @Min(value = 0)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer depth;

    /**
     * Maximum number of pages/documents to access during crawling.
     */
    @Min(value = 0)
    @Max(value = 9223372036854775807L)
    @ValidateTypeFailure
    public Long maxAccessCount;

}
