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

package jp.sf.fess.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import jp.sf.fess.db.bsbhv.BsSearchFieldLogBhv;
import jp.sf.fess.db.exbhv.SearchFieldLogBhv;
import jp.sf.fess.db.exbhv.pmbean.GroupedFieldNamePmb;
import jp.sf.fess.db.exentity.customize.GroupedFieldName;

import org.seasar.dbflute.cbean.ListResultBean;
import org.seasar.framework.container.annotation.tiger.InitMethod;

public class SearchFieldLogService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected SearchFieldLogBhv searchFieldLogBhv;

    private String[] groupedFieldNames;

    @InitMethod
    public void init() {
        updateFieldLabels();
    }

    public void updateFieldLabels() {
        final GroupedFieldNamePmb pmb = new GroupedFieldNamePmb();
        final String path = BsSearchFieldLogBhv.PATH_selectGroupedFieldName;
        final ListResultBean<GroupedFieldName> list = searchFieldLogBhv
                .outsideSql().selectList(path, pmb, GroupedFieldName.class);
        final List<String> groupedFieldNameList = new ArrayList<String>();
        for (final GroupedFieldName e : list) {
            final String name = e.getName();
            if (!"query".equals(name) && !"solrQuery".equals(name)) {
                groupedFieldNameList.add(name);
            }
        }
        groupedFieldNames = groupedFieldNameList
                .toArray(new String[groupedFieldNameList.size()]);
    }

    public String[] getGroupedFieldNames() {
        return groupedFieldNames;
    }
}
