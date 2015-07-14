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
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.CronExpression;
import org.codelibs.fess.util.ComponentUtil;
import org.seasar.struts.annotation.IntRange;
import org.seasar.struts.annotation.IntegerType;
import org.seasar.struts.annotation.LongType;
import org.seasar.struts.annotation.Maxbytelength;
import org.seasar.struts.annotation.Required;

public class ScheduledJobForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @IntegerType
    public String pageNumber;

    public Map<String, String> searchParams = new HashMap<String, String>();

    @IntegerType
    public int crudMode;

    public String getCurrentPageNumber() {
        return pageNumber;
    }

    @Required(target = "confirmfromupdate,update,delete")
    @Maxbytelength(maxbytelength = 1000)
    public String id;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @Maxbytelength(maxbytelength = 100)
    public String name;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @Maxbytelength(maxbytelength = 100)
    public String target;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @Maxbytelength(maxbytelength = 100)
    @CronExpression
    public String cronExpression;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @Maxbytelength(maxbytelength = 100)
    public String scriptType;

    @Maxbytelength(maxbytelength = 4000)
    public String scriptData;

    // ignore
    public String crawler;

    // ignore
    public String jobLogging;

    // ignore
    public String available;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @IntegerType
    @IntRange(min = 0, max = 2147483647)
    public String sortOrder;

    @Required(target = "confirmfromupdate,update,delete")
    public String createdBy;

    @Required(target = "confirmfromupdate,update,delete")
    @LongType
    public String createdTime;

    public String updatedBy;

    @LongType
    public String updatedTime;

    @Required(target = "confirmfromupdate,update,delete")
    @IntegerType
    public String versionNo;

    public void initialize() {
        id = null;
        name = null;
        target = null;
        cronExpression = null;
        scriptType = null;
        scriptData = null;
        crawler = null;
        jobLogging = null;
        available = null;
        sortOrder = null;
        createdBy = "system";
        createdTime = Long.toString(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
        updatedBy = null;
        updatedTime = null;
        versionNo = null;
        target = Constants.DEFAULT_JOB_TARGET;
        cronExpression = Constants.DEFAULT_CRON_EXPRESSION;
        scriptType = Constants.DEFAULT_JOB_SCRIPT_TYPE;
        sortOrder = "0";
    }
}
