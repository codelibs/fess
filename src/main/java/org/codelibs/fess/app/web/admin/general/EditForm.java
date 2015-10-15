/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

/**
 * @author shinsuke
 * @author Shunji Makino
 */
public class EditForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 10)
    public String diffCrawling;

    @Size(max = 10)
    public String useAclAsRole;

    @Size(max = 10)
    public String serverRotation;

    @Required
    @Max(1000)
    @Min(-1)
    public Integer dayForCleanup;

    @Required
    @Max(100)
    @Min(0)
    public String crawlingThreadCount;

    @Size(max = 10)
    public String searchLog;

    @Size(max = 10)
    public String userInfo;

    @Size(max = 10)
    public String userFavorite;

    @Size(max = 10)
    public String webApiXml;

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
    @Max(10000)
    @Min(-1)
    public Integer failureCountThreshold;

    @Size(max = 10)
    public String hotSearchWord;

    @Required
    @Size(max = 20)
    public String csvFileEncoding;

    @Max(100000)
    @Min(0)
    public String purgeSearchLogDay;

    @Max(100000)
    @Min(0)
    public String purgeJobLogDay;

    @Max(100000)
    @Min(0)
    public String purgeUserInfoDay;

    @Size(max = 1000)
    public String purgeByBots;

    @Size(max = 1000)
    public String notificationTo;

    @Size(max = 10)
    public String suggestSearchLog;

    @Max(100000)
    @Min(0)
    public String purgeSuggestSearchLogDay;

    @Size(max = 1000)
    public String esHttpUrl;
}
