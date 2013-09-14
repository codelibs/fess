/*
 * Copyright 2009-2013 the Fess Project and the Others.
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
import jp.sf.fess.annotation.UriType;

import org.seasar.struts.annotation.DateType;
import org.seasar.struts.annotation.IntRange;
import org.seasar.struts.annotation.IntegerType;
import org.seasar.struts.annotation.LongRange;
import org.seasar.struts.annotation.LongType;
import org.seasar.struts.annotation.Maxbytelength;
import org.seasar.struts.annotation.Required;

public abstract class BsFileCrawlingConfigForm {
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
    @UriType(protocols = "file:,smb:")
    @Maxbytelength(maxbytelength = 4000)
    public String paths;

    @Maxbytelength(maxbytelength = 4000)
    public String includedPaths;

    @Maxbytelength(maxbytelength = 4000)
    public String excludedPaths;

    @Maxbytelength(maxbytelength = 4000)
    public String includedDocPaths;

    @Maxbytelength(maxbytelength = 4000)
    public String excludedDocPaths;

    @Maxbytelength(maxbytelength = 4000)
    public String configParameter;

    @IntRange(min = 0, max = 2147483647)
    public String depth;

    @LongRange(min = 0, max = 9223372036854775807l)
    public String maxAccessCount;

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
    @IntegerType
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
        paths = null;
        includedPaths = null;
        excludedPaths = null;
        includedDocPaths = null;
        excludedDocPaths = null;
        configParameter = null;
        depth = null;
        maxAccessCount = null;
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

    }

}
