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
package org.codelibs.fess.app.web.admin.fileconfig;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.validation.CustomSize;
import org.codelibs.fess.validation.UriType;
import org.codelibs.fess.validation.UriTypeValidator.ProtocolType;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * The create form for File Config.
 *
 */
public class CreateForm {

    /**
     * Creates a new CreateForm instance.
     */
    public CreateForm() {
    }

    /** The IDs of label types associated with this file configuration. */
    public String[] labelTypeIds;

    /** The CRUD operation mode for this form. */
    @ValidateTypeFailure
    public Integer crudMode;

    /** The name of the file configuration (required, maximum 200 characters). */
    @Required
    @Size(max = 200)
    public String name;

    /** The description of the file configuration (maximum 1000 characters). */
    @Size(max = 1000)
    public String description;

    /** The file paths to crawl (required, must be valid file URIs). */
    @Required
    @UriType(protocolType = ProtocolType.FILE)
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String paths;

    /** The paths to include during crawling (pattern-based). */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String includedPaths;

    /** The paths to exclude during crawling (pattern-based). */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String excludedPaths;

    /** The document paths to include in search results (pattern-based). */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String includedDocPaths;

    /** The document paths to exclude from search results (pattern-based). */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String excludedDocPaths;

    /** Additional configuration parameters for the file crawler. */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String configParameter;

    /** The maximum crawling depth (0 to 2147483647, 0 means unlimited). */
    @Min(value = 0)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer depth;

    /** The maximum number of documents to access during crawling (0 means unlimited). */
    @Min(value = 0)
    @Max(value = 9223372036854775807L)
    @ValidateTypeFailure
    public Long maxAccessCount;

    /** The number of threads to use for crawling (required, 1 to 2147483647). */
    @Required
    @Min(value = 1)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer numOfThread;

    /** The interval time between crawl requests in milliseconds (required, 0 to 2147483647). */
    @Required
    @Min(value = 0)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer intervalTime;

    /** The boost value for search ranking (required). */
    @Required
    @ValidateTypeFailure
    public Float boost;

    /** Whether this configuration is available for crawling (required, maximum 5 characters). */
    @Required
    @Size(max = 5)
    public String available;

    /** The permissions required to access documents from this configuration. */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String permissions;

    /** The virtual hosts associated with this file configuration. */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String virtualHosts;

    /** The sort order for this configuration (required, 0 to 2147483647). */
    @Required
    @Min(value = 0)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer sortOrder;

    /** The user who created this file configuration (maximum 1000 characters). */
    @Size(max = 1000)
    public String createdBy;

    /** The timestamp when this file configuration was created. */
    @ValidateTypeFailure
    public Long createdTime;

    /**
     * Initializes the form with default values for creation mode.
     */
    public void initialize() {
        crudMode = CrudMode.CREATE;
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        includedPaths = fessConfig.getCrawlerDocumentFileDefaultIncludeIndexPatterns();
        excludedPaths = fessConfig.getCrawlerDocumentFileDefaultExcludeIndexPatterns();
        includedDocPaths = fessConfig.getCrawlerDocumentFileDefaultIncludeSearchPatterns();
        excludedDocPaths = fessConfig.getCrawlerDocumentFileDefaultExcludeSearchPatterns();
        boost = 1.0f;
        numOfThread = Constants.DEFAULT_NUM_OF_THREAD_FOR_FS;
        intervalTime = Constants.DEFAULT_INTERVAL_TIME_FOR_FS;
        sortOrder = 0;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        permissions = fessConfig.getSearchDefaultDisplayPermission();
    }
}
