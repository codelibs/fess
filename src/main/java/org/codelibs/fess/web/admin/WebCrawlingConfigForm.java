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

package org.codelibs.fess.web.admin;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.UriType;
import org.codelibs.fess.util.ComponentUtil;
import org.seasar.struts.annotation.DateType;
import org.seasar.struts.annotation.IntRange;
import org.seasar.struts.annotation.IntegerType;
import org.seasar.struts.annotation.LongRange;
import org.seasar.struts.annotation.LongType;
import org.seasar.struts.annotation.Maxbytelength;
import org.seasar.struts.annotation.Required;

public class WebCrawlingConfigForm implements Serializable {

    private static final long serialVersionUID = 1L;

    public String[] roleTypeIds;

    public String[] labelTypeIds;

    @IntegerType
    public String pageNumber;

    public Map<String, String> searchParams = new HashMap<String, String>();

    @IntegerType
    public int crudMode;

    public String getCurrentPageNumber() {
        return pageNumber;
    }

    @Required(target = "confirmfromupdate,update,delete")
    @LongType
    public String id;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @Maxbytelength(maxbytelength = 200)
    public String name;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @UriType(protocols = "http:,https:")
    @Maxbytelength(maxbytelength = 4000)
    public String urls;

    @Maxbytelength(maxbytelength = 4000)
    public String includedUrls;

    @Maxbytelength(maxbytelength = 4000)
    public String excludedUrls;

    @Maxbytelength(maxbytelength = 4000)
    public String includedDocUrls;

    @Maxbytelength(maxbytelength = 4000)
    public String excludedDocUrls;

    @Maxbytelength(maxbytelength = 4000)
    public String configParameter;

    @IntRange(min = 0, max = 2147483647)
    public String depth;

    @LongRange(min = 0, max = 9223372036854775807l)
    public String maxAccessCount;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @Maxbytelength(maxbytelength = 200)
    public String userAgent;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @IntRange(min = 0, max = 2147483647)
    public String numOfThread;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @IntRange(min = 0, max = 2147483647)
    public String intervalTime;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @IntRange(min = 0, max = 2147483647)
    public String boost;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @Maxbytelength(maxbytelength = 1)
    public String available;

    @Required(target = "confirmfromupdate,update,delete")
    @IntRange(min = 0, max = 2147483647)
    public String sortOrder;

    @Required(target = "confirmfromupdate,update,delete")
    @Maxbytelength(maxbytelength = 255)
    public String createdBy;

    @Required(target = "confirmfromupdate,update,delete")
    @DateType(datePattern = Constants.DEFAULT_DATETIME_FORMAT)
    public String createdTime;

    @Maxbytelength(maxbytelength = 255)
    public String updatedBy;

    @DateType(datePattern = Constants.DEFAULT_DATETIME_FORMAT)
    public String updatedTime;

    @Maxbytelength(maxbytelength = 255)
    public String deletedBy;

    @DateType(datePattern = Constants.DEFAULT_DATETIME_FORMAT)
    public String deletedTime;

    @Required(target = "confirmfromupdate,update,delete")
    @IntegerType
    public String versionNo;

    public void initialize() {
        id = null;
        name = null;
        urls = null;
        includedUrls = null;
        excludedUrls = null;
        includedDocUrls = null;
        excludedDocUrls = null;
        configParameter = null;
        depth = null;
        maxAccessCount = null;
        userAgent = null;
        numOfThread = null;
        intervalTime = null;
        boost = "1";
        available = null;
        sortOrder = null;
        createdBy = null;
        createdTime = null;
        updatedBy = null;
        updatedTime = null;
        deletedBy = null;
        deletedTime = null;
        versionNo = null;
        sortOrder = "0";
        userAgent = ComponentUtil.getUserAgentName();
        if (StringUtil.isBlank(userAgent)) {
            userAgent = "Fess Robot/1.0";
        }
        numOfThread = Integer.toString(Constants.DEFAULT_NUM_OF_THREAD_FOR_WEB);
        intervalTime = Integer.toString(Constants.DEFAULT_INTERVAL_TIME_FOR_WEB);
        // Temporary data
        createdBy = "system";
        final SimpleDateFormat sdf = new SimpleDateFormat(Constants.DEFAULT_DATETIME_FORMAT);
        createdTime = sdf.format(new Date());

    }
}
