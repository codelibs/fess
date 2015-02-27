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
import java.util.HashMap;
import java.util.Map;

import jp.sf.fess.Constants;

import org.codelibs.core.util.StringUtil;
import org.seasar.struts.annotation.IntegerType;

public class StatsForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @IntegerType
    public String pageNumber;

    public Map<String, String> searchParams = new HashMap<String, String>();

    public StatsForm() {
        final String reportType = searchParams.get(Constants.STATS_REPORT_TYPE);
        if (StringUtil.isBlank(reportType)) {
            searchParams.put(Constants.STATS_REPORT_TYPE, "searchWord");
        }
    }

    public String getCurrentPageNumber() {
        return pageNumber;
    }

    public void initialize() {
        // nothing
    }
}
