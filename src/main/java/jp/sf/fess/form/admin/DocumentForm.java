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

package jp.sf.fess.form.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.struts.annotation.Required;

public class DocumentForm implements Serializable {

    private static final long serialVersionUID = 1L;

    public String currentServerForUpdate;

    public String currentServerForSelect;

    public String currentServerStatusForUpdate;

    public String currentServerStatusForSelect;

    public List<Map<String, String>> serverStatusList = new ArrayList<Map<String, String>>();

    @Required(target = "commit,optimize,delete")
    public String groupName;

    @Required(target = "delete")
    public String sessionId;

    @Required(target = "deleteByUrl,confirmByUrl")
    public String deleteUrl;

    @Required(target = "startSolrInstance,stopSolrInstance,reloadSolrInstance")
    public String solrInstanceName;

    @Required(target = "deleteSuggest")
    public String deleteSuggestType;
}
