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

package org.codelibs.fess.app.web.admin.failureurl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.util.ComponentUtil;

/**
 * @author codelibs
 * @author Keiichi Watanabe
 */
public class FailureUrlEditForm implements Serializable {

    private static final long serialVersionUID = 1L;

    public String webConfigName;

    public String fileConfigName;

    //@IntegerType
    public String pageNumber;

    public Map<String, String> searchParams = new HashMap<String, String>();

    //@IntegerType 
    // TODO necessary?
    public int crudMode;

    public String getCurrentPageNumber() {
        return pageNumber;
    }

    //@Required(target = "confirmfromupdate,update,delete")
    //@Maxbytelength(maxbytelength = 1000)
    public String id;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    public String url;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    public String threadName;

    public String errorName;

    public String errorLog;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    //@IntegerType
    public String errorCount;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    //@DateType(datePattern = Constants.DEFAULT_DATETIME_FORMAT)
    public String lastAccessTime;

    //@Maxbytelength(maxbytelength = 1000)
    public String configId;

    public void initialize() {
        id = null;
        url = null;
        threadName = null;
        errorName = null;
        errorLog = null;
        errorCount = null;
        lastAccessTime = null;
        configId = null;
    }

}
