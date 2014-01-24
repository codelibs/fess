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

package jp.sf.fess.crud.form.admin;

import java.util.HashMap;
import java.util.Map;

import jp.sf.fess.Constants;
import jp.sf.fess.annotation.CronExpression;

import org.seasar.struts.annotation.DateType;
import org.seasar.struts.annotation.IntRange;
import org.seasar.struts.annotation.IntegerType;
import org.seasar.struts.annotation.LongType;
import org.seasar.struts.annotation.Maxbytelength;
import org.seasar.struts.annotation.Required;

public abstract class BsScheduledJobForm {
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
    @DateType(datePattern = Constants.DEFAULT_DATETIME_FORMAT)
    public String createdTime;

    public String updatedBy;

    @DateType(datePattern = Constants.DEFAULT_DATETIME_FORMAT)
    public String updatedTime;

    public String deletedBy;

    @DateType(datePattern = Constants.DEFAULT_DATETIME_FORMAT)
    public String deletedTime;

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
        createdBy = null;
        createdTime = null;
        updatedBy = null;
        updatedTime = null;
        deletedBy = null;
        deletedTime = null;
        versionNo = null;

    }

}
