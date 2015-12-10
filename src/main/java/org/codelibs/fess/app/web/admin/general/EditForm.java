/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

/**
 * @author shinsuke
 * @author Shunji Makino
 */
public class EditForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 10)
    public String incrementalCrawling;

    @Required
    @Min(-1)
    @Max(1000)
    @ValidateTypeFailure
    public Integer dayForCleanup;

    @Required
    @Min(0)
    @Max(100)
    @ValidateTypeFailure
    public Integer crawlingThreadCount;

    @Size(max = 10)
    public String searchLog;

    @Size(max = 10)
    public String userInfo;

    @Size(max = 10)
    public String userFavorite;

    @Size(max = 10)
    public String webApiJson;

    @Size(max = 1000)
    public String defaultLabelValue;

    @Size(max = 10)
    public String appendQueryParameter;

    @Size(max = 10)
    public String supportedSearch;

    @Size(max = 1000)
    public String ignoreFailureType;

    @Required
    @Min(-1)
    @Max(10000)
    @ValidateTypeFailure
    public Integer failureCountThreshold;

    @Size(max = 10)
    public String hotSearchWord;

    @Required
    @Size(max = 20)
    public String csvFileEncoding;

    @Min(0)
    @Max(100000)
    @ValidateTypeFailure
    public Integer purgeSearchLogDay;

    @Min(0)
    @Max(100000)
    @ValidateTypeFailure
    public Integer purgeJobLogDay;

    @Min(0)
    @Max(100000)
    @ValidateTypeFailure
    public Integer purgeUserInfoDay;

    @Size(max = 1000)
    public String purgeByBots;

    @Size(max = 1000)
    public String notificationTo;

    @Size(max = 10)
    public String suggestSearchLog;

    @Size(max = 10)
    public String suggestDocuments;

    @Min(0)
    @Max(100000)
    @ValidateTypeFailure
    public Integer purgeSuggestSearchLogDay;

}
