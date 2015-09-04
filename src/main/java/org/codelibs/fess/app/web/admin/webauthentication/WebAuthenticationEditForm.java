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

package org.codelibs.fess.app.web.admin.webauthentication;

import java.io.Serializable;

import org.codelibs.fess.util.ComponentUtil;

/**
 * @author codelibs
 * @author jflute
 */
public class WebAuthenticationEditForm implements Serializable {

    private static final long serialVersionUID = 1L;

    //@IntegerType
    public int crudMode;

    //@Required(target = "confirmfromupdate,update,delete")
    //@Maxbytelength(maxbytelength = 1000)
    public String id;

    //@Maxbytelength(maxbytelength = 100)
    public String hostname;

    //@IntRange(min = -1, max = 2147483647)
    public String port;

    //@Maxbytelength(maxbytelength = 100)
    public String authRealm;

    //@Maxbytelength(maxbytelength = 10)
    public String protocolScheme;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    //@Maxbytelength(maxbytelength = 100)
    public String username;

    //@Maxbytelength(maxbytelength = 100)
    public String password;

    //@Maxbytelength(maxbytelength = 1000)
    public String parameters;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    //@Maxbytelength(maxbytelength = 1000)
    public String webConfigId;

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
        hostname = null;
        port = null;
        authRealm = null;
        protocolScheme = null;
        username = null;
        password = null;
        parameters = null;
        webConfigId = null;
        createdBy = "system";
        createdTime = Long.toString(ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
        updatedBy = null;
        updatedTime = null;
        versionNo = null;
    }
}
