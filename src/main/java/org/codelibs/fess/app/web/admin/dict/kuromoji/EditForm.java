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

package org.codelibs.fess.app.web.admin.dict.kuromoji;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author codelibs
 * @author Keiichi Watanabe
 */
public class EditForm implements Serializable {

    private static final long serialVersionUID = 1L;

    //@IntegerType
    public String pageNumber;

    public Map<String, String> searchParams = new HashMap<String, String>();

    //@Required
    public String dictId;

    //@IntegerType
    public int crudMode;

    public String getCurrentPageNumber() {
        return pageNumber;
    }

    //@Required(target = "confirmfromupdate,update,delete")
    //@LongType
    public String id;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    //@Maxbytelength(maxbytelength = 1000)
    public String token;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    //@Maxbytelength(maxbytelength = 1000)
    public String segmentation;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    //@Maxbytelength(maxbytelength = 1000)
    public String reading;

    //@Required(target = "confirmfromcreate,create,confirmfromupdate,update,delete")
    //@Maxbytelength(maxbytelength = 1000)
    public String pos;

    //@Required(target = "upload")
    //public FormFile userDictFile;

    public void initialize() {
        id = null;

    }
}
