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

package org.codelibs.fess.app.web.admin.fileconfig;

import java.io.Serializable;

import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.UriType;
import org.codelibs.fess.util.ComponentUtil;

/**
 * @author codelibs
 * @author Keiichi Watanabe
 */
public class FileConfigEditForm implements Serializable {

    private static final long serialVersionUID = 1L;

    public String[] roleTypeIds;

    public String[] labelTypeIds;

    //@IntegerType
    public int crudMode;

    //@Required(target = "confirmfromupdate,update,delete")
    //@Maxbytelength(maxbytelength = 1000)
    public String id;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    //@Maxbytelength(maxbytelength = 200)
    public String name;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @UriType(protocols = "file:,smb:")
    //@Maxbytelength(maxbytelength = 4000)
    public String paths;

    //@Maxbytelength(maxbytelength = 4000)
    public String includedPaths;

    //@Maxbytelength(maxbytelength = 4000)
    public String excludedPaths;

    //@Maxbytelength(maxbytelength = 4000)
    public String includedDocPaths;

    //@Maxbytelength(maxbytelength = 4000)
    public String excludedDocPaths;

    //@Maxbytelength(maxbytelength = 4000)
    public String configParameter;

    //@IntRange(min = 0, max = 2147483647)
    public String depth;

    //@LongRange(min = 0, max = 9223372036854775807l)
    public String maxAccessCount;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    //@IntRange(min = 0, max = 2147483647)
    public String numOfThread;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    //@IntRange(min = 0, max = 2147483647)
    public String intervalTime;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    //@IntRange(min = 0, max = 2147483647)
    public String boost;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    //@Maxbytelength(maxbytelength = 5)
    public String available;

    //@Required(target = "confirmfromupdate,update,delete")
    //@IntegerType
    //@IntRange(min = 0, max = 2147483647)
    public String sortOrder;

    //@Required(target = "confirmfromupdate,update,delete")
    //@Maxbytelength(maxbytelength = 255)
    public String createdBy;

    //@Required(target = "confirmfromupdate,update,delete")
    //@LongType
    public String createdTime;

    //@Maxbytelength(maxbytelength = 255)
    public String updatedBy;

    //@LongType
    public String updatedTime;

    //@Required(target = "confirmfromupdate,update,delete")
    //@IntegerType
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
        createdBy = "system";
        createdTime = Long.toString(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
        updatedBy = null;
        updatedTime = null;
        versionNo = null;
        sortOrder = "0";
        numOfThread = Integer.toString(Constants.DEFAULT_NUM_OF_THREAD_FOR_FS);
        intervalTime = Integer.toString(Constants.DEFAULT_INTERVAL_TIME_FOR_FS);
    }
}
