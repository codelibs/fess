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
import java.util.List;
import java.util.Map;

import org.codelibs.fess.db.exentity.ClickLog;
import org.seasar.struts.annotation.DateType;
import org.seasar.struts.annotation.IntegerType;
import org.seasar.struts.annotation.LongType;
import org.seasar.struts.annotation.Required;

public class SearchLogForm implements Serializable {

    private static final long serialVersionUID = 1L;

    public String csvEncoding;

    public List<ClickLog> clickLogList;

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

    public String searchWord;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @DateType
    public String requestedTime;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @IntegerType
    public String responseTime;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @LongType
    public String hitCount;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @IntegerType
    public String queryOffset;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    @IntegerType
    public String queryPageSize;

    public String userAgent;

    public String referer;

    public String clientIp;

    public String userSessionId;

    @Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    public String accessType;

    @LongType
    public String userId;

    public void initialize() {
        id = null;
        searchWord = null;
        requestedTime = null;
        responseTime = null;
        hitCount = null;
        queryOffset = null;
        queryPageSize = null;
        userAgent = null;
        referer = null;
        clientIp = null;
        userSessionId = null;
        accessType = null;
        userId = null;
    }
}
