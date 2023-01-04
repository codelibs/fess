/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import javax.validation.constraints.Size;

import org.lastaflute.web.validation.Required;

/**
 * @author shinsuke
 */
public class MailForm {

    public String incrementalCrawling;

    public String dayForCleanup;

    public String crawlingThreadCount;

    public String searchLog;

    public String userInfo;

    public String userFavorite;

    public String webApiJson;

    public String defaultLabelValue;

    public String appendQueryParameter;

    public String loginRequired;

    public String ignoreFailureType;

    public String failureCountThreshold;

    public String popularWord;

    public String csvFileEncoding;

    public String purgeSearchLogDay;

    public String purgeJobLogDay;

    public String purgeUserInfoDay;

    public String purgeByBots;

    @Required
    @Size(max = 1000)
    public String notificationTo;

    public String suggestSearchLog;

    public String suggestDocuments;

    public String purgeSuggestSearchLogDay;

}
