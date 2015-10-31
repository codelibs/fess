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
package org.codelibs.fess.app.web.admin.suggestelevateword;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.util.ComponentUtil;

/**
 * @author codelibs
 * @author Keiichi Watanabe
 */
public class SuggestElevateWordEditForm implements Serializable {

    private static final long serialVersionUID = 1L;

    //@Digits(integer=10, fraction=0)
    public String pageNumber;

    public Map<String, String> searchParams = new HashMap<String, String>();

    //@Digits(integer=10, fraction=0)
    public int crudMode;

    public String getCurrentPageNumber() {
        return pageNumber;
    }

    //@Required(target = "confirmfromupdate,update,delete")
    //@Maxbytelength(maxbytelength = 1000)
    public String id;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    public String suggestWord;

    public String reading;

    public String targetRole;

    public String targetLabel;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    //@FloatType
    public String boost;

    //@Required(target = "confirmfromupdate,update,delete")
    //@Maxbytelength(maxbytelength = 255)
    public String createdBy;

    //@Required(target = "confirmfromupdate,update,delete")
    //@Digits(integer=19, fraction=0)
    public String createdTime;

    //@Maxbytelength(maxbytelength = 255)
    public String updatedBy;

    //@Digits(integer=19, fraction=0)
    public String updatedTime;

    //@Required(target = "confirmfromupdate,update,delete")
    //@Digits(integer=10, fraction=0)
    public String versionNo;

    public void initialize() {
        id = null;
        suggestWord = null;
        reading = null;
        targetRole = null;
        targetLabel = null;
        boost = null;
        createdBy = "system";
        createdTime = Long.toString(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
        updatedBy = null;
        updatedTime = null;
        versionNo = null;
        boost = "100";
    }
}
