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

import jakarta.validation.constraints.Size;

/**
 * The mail form for General settings.
 */
public class MailForm {

    /**
     * Default constructor.
     */
    public MailForm() {
        // Default constructor
    }

    /** The incremental crawling setting. */
    public String incrementalCrawling;

    /** The day for cleanup setting. */
    public String dayForCleanup;

    /** The crawling thread count setting. */
    public String crawlingThreadCount;

    /** The search log setting. */
    public String searchLog;

    /** The user info setting. */
    public String userInfo;

    /** The user favorite setting. */
    public String userFavorite;

    /** The web API JSON setting. */
    public String webApiJson;

    /** The default label value setting. */
    public String defaultLabelValue;

    /** The append query parameter setting. */
    public String appendQueryParameter;

    /** The login required setting. */
    public String loginRequired;

    /** The ignore failure type setting. */
    public String ignoreFailureType;

    /** The failure count threshold setting. */
    public String failureCountThreshold;

    /** The popular word setting. */
    public String popularWord;

    /** The CSV file encoding setting. */
    public String csvFileEncoding;

    /** The purge search log day setting. */
    public String purgeSearchLogDay;

    /** The purge job log day setting. */
    public String purgeJobLogDay;

    /** The purge user info day setting. */
    public String purgeUserInfoDay;

    /** The purge by bots setting. */
    public String purgeByBots;

    /** The notification recipient setting. */
    @Required
    @Size(max = 1000)
    public String notificationTo;

    /** The suggest search log setting. */
    public String suggestSearchLog;

    /** The suggest documents setting. */
    public String suggestDocuments;

    /** The purge suggest search log day setting. */
    public String purgeSuggestSearchLogDay;

}
