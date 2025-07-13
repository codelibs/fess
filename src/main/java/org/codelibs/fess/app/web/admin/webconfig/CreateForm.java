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
package org.codelibs.fess.app.web.admin.webconfig;

import org.codelibs.core.lang.StringUtil;
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
 * The create form for Web Config.
 *
 * @author shinsuke
 */
public class CreateForm {

    /**
     * Default constructor.
     */
    public CreateForm() {
        // Empty constructor
    }

    /**
     * The label type IDs associated with this web configuration.
     */
    public String[] labelTypeIds;

    /**
     * The CRUD mode for the form.
     */
    @ValidateTypeFailure
    public Integer crudMode;

    /**
     * The name of the web configuration.
     */
    @Required
    @Size(max = 200)
    public String name;

    /**
     * The description of the web configuration.
     */
    @Size(max = 1000)
    public String description;

    /**
     * The URLs to be crawled by this web configuration.
     */
    @Required
    @UriType(protocolType = ProtocolType.WEB)
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String urls;

    /**
     * URL patterns to include during crawling.
     */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String includedUrls;

    /**
     * URL patterns to exclude during crawling.
     */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String excludedUrls;

    /**
     * Document URL patterns to include in search index.
     */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String includedDocUrls;

    /**
     * Document URL patterns to exclude from search index.
     */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String excludedDocUrls;

    /**
     * Additional configuration parameters for the crawler.
     */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String configParameter;

    /**
     * The maximum crawling depth from the starting URLs.
     */
    @Min(value = 0)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer depth;

    /**
     * The maximum number of URLs to access during crawling.
     */
    @Min(value = 0)
    @Max(value = 9223372036854775807L)
    @ValidateTypeFailure
    public Long maxAccessCount;

    /**
     * The user agent string to use during crawling.
     */
    @Required
    @Size(max = 200)
    public String userAgent;

    /**
     * The number of crawler threads to use.
     */
    @Required
    @Min(value = 1)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer numOfThread;

    /**
     * The interval time between requests in milliseconds.
     */
    @Required
    @Min(value = 0)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer intervalTime;

    /**
     * The boost value for documents from this web configuration.
     */
    @Required
    @ValidateTypeFailure
    public Float boost;

    /**
     * Whether this web configuration is available for crawling.
     */
    @Required
    @Size(max = 5)
    public String available;

    /**
     * Permissions required to access documents from this configuration.
     */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String permissions;

    /**
     * Virtual host names for this web configuration.
     */
    @CustomSize(maxKey = "form.admin.max.input.size")
    public String virtualHosts;

    /**
     * The sort order for this web configuration.
     */
    @Required
    @Min(value = 0)
    @Max(value = 2147483647)
    @ValidateTypeFailure
    public Integer sortOrder;

    /**
     * The user who created this web configuration.
     */
    @Size(max = 1000)
    public String createdBy;

    /**
     * The timestamp when this web configuration was created.
     */
    @ValidateTypeFailure
    public Long createdTime;

    /**
     * Initializes the form with default values for creating a new web configuration.
     */
    public void initialize() {
        crudMode = CrudMode.CREATE;
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        includedUrls = fessConfig.getCrawlerDocumentHtmlDefaultIncludeIndexPatterns();
        excludedUrls = fessConfig.getCrawlerDocumentHtmlDefaultExcludeIndexPatterns();
        includedDocUrls = fessConfig.getCrawlerDocumentHtmlDefaultIncludeSearchPatterns();
        excludedDocUrls = fessConfig.getCrawlerDocumentHtmlDefaultExcludeSearchPatterns();
        boost = 1.0f;
        if (StringUtil.isBlank(userAgent)) {
            userAgent = fessConfig.getUserAgentName();
        }
        numOfThread = Constants.DEFAULT_NUM_OF_THREAD_FOR_WEB;
        intervalTime = Constants.DEFAULT_INTERVAL_TIME_FOR_WEB;
        sortOrder = 0;
        createdBy = ComponentUtil.getSystemHelper().getUsername();
        createdTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        permissions = fessConfig.getSearchDefaultDisplayPermission();
    }
}
