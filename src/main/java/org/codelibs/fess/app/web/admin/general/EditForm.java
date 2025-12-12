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
package org.codelibs.fess.app.web.admin.general;

import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Form class for editing general system settings in the admin interface.
 * This form handles global configuration settings that affect the entire Fess system,
 * including crawling behavior, authentication, logging, and various system parameters.
 *
 */
public class EditForm {

    /**
     * Creates a new EditForm instance.
     */
    public EditForm() {
        // Default constructor
    }

    /**
     * Enable or disable incremental crawling.
     * When enabled, only new or modified documents are crawled.
     */
    @Size(max = 10)
    public String incrementalCrawling;

    /**
     * Number of days to keep crawled documents before cleanup.
     * Set to -1 to disable automatic cleanup.
     */
    @Required
    @Min(-1)
    @Max(1000)
    @ValidateTypeFailure
    public Integer dayForCleanup;

    /**
     * Number of threads to use for crawling operations.
     * Higher values increase crawling speed but consume more resources.
     */
    @Required
    @Min(0)
    @Max(100)
    @ValidateTypeFailure
    public Integer crawlingThreadCount;

    /**
     * Enable or disable search query logging.
     * When enabled, user search queries are logged for analysis.
     */
    @Size(max = 10)
    public String searchLog;

    /**
     * Enable or disable user information tracking.
     * When enabled, user information is stored and tracked.
     */
    @Size(max = 10)
    public String userInfo;

    /**
     * Enable or disable user favorite functionality.
     * When enabled, users can save favorite search results.
     */
    @Size(max = 10)
    public String userFavorite;

    /**
     * Enable or disable JSON Web API.
     * When enabled, search results can be retrieved via JSON API.
     */
    @Size(max = 10)
    public String webApiJson;

    /**
     * Application-specific value for custom configurations.
     * This field can be used to store custom application settings.
     */
    @Size(max = 10000)
    public String appValue;

    /**
     * Default label value to use when no specific label is selected.
     * This affects which documents are included in search results by default.
     */
    @Size(max = 1000)
    public String defaultLabelValue;

    /**
     * Default sort order for search results.
     * Defines how search results are ordered when no specific sort is requested.
     */
    @Size(max = 1000)
    public String defaultSortValue;

    /**
     * Virtual host configuration for multi-tenant setups.
     * Allows different search configurations based on the request host.
     */
    @Size(max = 10000)
    public String virtualHostValue;

    /**
     * Enable or disable appending query parameters to search URLs.
     * When enabled, additional parameters are added to search result URLs.
     */
    @Size(max = 10)
    public String appendQueryParameter;

    /**
     * Enable or disable login requirement for search access.
     * When enabled, users must authenticate before performing searches.
     */
    @Size(max = 10)
    public String loginRequired;

    /**
     * Enable or disable result collapsing for similar documents.
     * When enabled, similar search results are grouped together.
     */
    @Size(max = 10)
    public String resultCollapsed;

    /**
     * Enable or disable display of login link in the search interface.
     * When enabled, a login link is shown to unauthenticated users.
     */
    @Size(max = 10)
    public String loginLink;

    /**
     * Enable or disable thumbnail generation for documents.
     * When enabled, thumbnails are generated for supported file types.
     */
    @Size(max = 10)
    public String thumbnail;

    /**
     * Types of crawling failures to ignore during crawling operations.
     * Specified failure types will not be logged or counted as errors.
     */
    @Size(max = 1000)
    public String ignoreFailureType;

    /**
     * Threshold for failure count before stopping crawling of a URL.
     * Set to -1 to disable the threshold check.
     */
    @Required
    @Min(-1)
    @Max(10000)
    @ValidateTypeFailure
    public Integer failureCountThreshold;

    /**
     * Enable or disable popular word tracking and display.
     * When enabled, frequently searched terms are tracked and displayed.
     */
    @Size(max = 10)
    public String popularWord;

    /**
     * Character encoding to use for CSV file exports.
     * This setting affects the encoding of downloaded CSV files.
     */
    @Required
    @Size(max = 20)
    public String csvFileEncoding;

    /**
     * Number of days to keep search logs before purging.
     * Set to -1 to disable automatic purging of search logs.
     */
    @Min(-1)
    @Max(100000)
    @ValidateTypeFailure
    public Integer purgeSearchLogDay;

    /**
     * Number of days to keep job logs before purging.
     * Set to -1 to disable automatic purging of job logs.
     */
    @Min(-1)
    @Max(100000)
    @ValidateTypeFailure
    public Integer purgeJobLogDay;

    /**
     * Number of days to keep user information before purging.
     * Set to -1 to disable automatic purging of user information.
     */
    @Min(-1)
    @Max(100000)
    @ValidateTypeFailure
    public Integer purgeUserInfoDay;

    /**
     * Bot user agents whose search logs should be purged.
     * Search logs from these bots will be automatically removed.
     */
    @Size(max = 10000)
    public String purgeByBots;

    /**
     * Email addresses to receive system notifications.
     * Multiple addresses can be specified, separated by commas.
     */
    @Size(max = 1000)
    public String notificationTo;

    /**
     * Enable or disable search suggestions based on search logs.
     * When enabled, suggestions are generated from previous searches.
     */
    @Size(max = 10)
    public String suggestSearchLog;

    /**
     * Enable or disable search suggestions based on document content.
     * When enabled, suggestions are generated from indexed documents.
     */
    @Size(max = 10)
    public String suggestDocuments;

    /**
     * Number of days to keep suggestion search logs before purging.
     * Set to 0 to disable purging of suggestion search logs.
     */
    @Min(0)
    @Max(100000)
    @ValidateTypeFailure
    public Integer purgeSuggestSearchLogDay;

    /**
     * LDAP server URL for authentication.
     * Used when LDAP authentication is enabled.
     */
    @Size(max = 1000)
    public String ldapProviderUrl;

    /**
     * LDAP security principal for binding to the LDAP server.
     * Used for authenticating with the LDAP server.
     */
    @Size(max = 1000)
    public String ldapSecurityPrincipal;

    /**
     * LDAP admin security principal for administrative operations.
     * Used for admin-level operations on the LDAP server.
     */
    @Size(max = 1000)
    public String ldapAdminSecurityPrincipal;

    /**
     * LDAP admin security credentials (password) for administrative operations.
     * Used in conjunction with the admin security principal.
     */
    @Size(max = 1000)
    public String ldapAdminSecurityCredentials;

    /**
     * LDAP base DN (Distinguished Name) for user searches.
     * Defines the root of the LDAP directory tree for user lookups.
     */
    @Size(max = 1000)
    public String ldapBaseDn;

    /**
     * LDAP filter for finding user accounts.
     * Defines the search filter used to locate user accounts in LDAP.
     */
    @Size(max = 1000)
    public String ldapAccountFilter;

    /**
     * LDAP filter for finding groups.
     * Defines the search filter used to locate groups in LDAP.
     */
    @Size(max = 1000)
    public String ldapGroupFilter;

    /**
     * LDAP attribute name for group membership.
     * Specifies which LDAP attribute contains group membership information.
     */
    @Size(max = 100)
    public String ldapMemberofAttribute;

    /**
     * Notification message displayed on the login page.
     * This message is shown to users on the authentication page.
     */
    @Size(max = 3000)
    public String notificationLogin;

    /**
     * Notification message displayed on the search top page.
     * This message is shown to users on the main search page.
     */
    @Size(max = 3000)
    public String notificationSearchTop;

    /**
     * System log level for controlling log verbosity.
     * Controls the level of detail in system log messages.
     */
    @Size(max = 10)
    public String logLevel;

    /**
     * Storage service endpoint URL for cloud storage integration.
     * Used for storing files in cloud storage services like S3.
     */
    @Size(max = 1000)
    public String storageEndpoint;

    /**
     * Access key for cloud storage authentication.
     * Used to authenticate with cloud storage services.
     */
    @Size(max = 1000)
    public String storageAccessKey;

    /**
     * Secret key for cloud storage authentication.
     * Used in conjunction with the access key for cloud storage.
     */
    @Size(max = 1000)
    public String storageSecretKey;

    /**
     * Storage bucket name for cloud storage operations.
     * Specifies which bucket to use for storing files in cloud storage.
     */
    @Size(max = 1000)
    public String storageBucket;

    /**
     * Storage type for cloud storage (s3, gcs, auto).
     * Determines which storage client to use.
     */
    @Size(max = 20)
    public String storageType;

    /**
     * Storage region for S3.
     * AWS region where the S3 bucket is located.
     */
    @Size(max = 100)
    public String storageRegion;

    /**
     * GCS project ID.
     * Google Cloud project ID for GCS storage.
     */
    @Size(max = 200)
    public String storageProjectId;

    /**
     * Storage credentials file path for GCS.
     * Path to the service account credentials JSON file.
     */
    @Size(max = 1000)
    public String storageCredentialsPath;
}
