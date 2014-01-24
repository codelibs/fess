/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.form.admin;

import java.io.Serializable;

import org.seasar.struts.annotation.IntRange;
import org.seasar.struts.annotation.LongRange;
import org.seasar.struts.annotation.Maxbytelength;
import org.seasar.struts.annotation.Required;

public class CrawlForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Maxbytelength(maxbytelength = 10)
    public String diffCrawling;

    @Maxbytelength(maxbytelength = 10)
    public String useAclAsRole;

    @Maxbytelength(maxbytelength = 10)
    public String serverRotation;

    @Required
    @IntRange(min = -1, max = 1000)
    public String dayForCleanup;

    @Required
    @LongRange(min = 0, max = Long.MAX_VALUE)
    public String commitPerCount;

    @Required
    @LongRange(min = 0, max = 100)
    public String crawlingThreadCount;

    @Maxbytelength(maxbytelength = 1000)
    public String mobileTranscoder;

    @Maxbytelength(maxbytelength = 10)
    public String searchLog;

    @Maxbytelength(maxbytelength = 10)
    public String userInfo;

    @Maxbytelength(maxbytelength = 10)
    public String userFavorite;

    @Maxbytelength(maxbytelength = 10)
    public String webApiXml;

    @Maxbytelength(maxbytelength = 10)
    public String webApiJson;

    @Maxbytelength(maxbytelength = 10)
    public String webApiSuggest;

    @Maxbytelength(maxbytelength = 10)
    public String webApiAnalysis;

    @Maxbytelength(maxbytelength = 1000)
    public String defaultLabelValue;

    @Maxbytelength(maxbytelength = 10)
    public String appendQueryParameter;

    @Maxbytelength(maxbytelength = 10)
    public String supportedSearch;

    @Maxbytelength(maxbytelength = 1000)
    public String ignoreFailureType;

    @IntRange(min = -1, max = 10000)
    public String failureCountThreshold;

    @Maxbytelength(maxbytelength = 10)
    public String hotSearchWord;

    @Required
    @Maxbytelength(maxbytelength = 20)
    public String csvFileEncoding;

    @IntRange(min = 0, max = 100000)
    public String purgeSearchLogDay;

    @IntRange(min = 0, max = 100000)
    public String purgeJobLogDay;

    @IntRange(min = 0, max = 100000)
    public String purgeUserInfoDay;

    @Maxbytelength(maxbytelength = 1000)
    public String purgeByBots;

    @Maxbytelength(maxbytelength = 1000)
    public String notificationTo;
}
