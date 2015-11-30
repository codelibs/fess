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
package org.codelibs.fess.es.config.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.config.allcommon.EsAbstractConditionBean;
import org.codelibs.fess.es.config.bsentity.dbmeta.CrawlingSessionInfoDbm;
import org.codelibs.fess.es.config.cbean.CrawlingSessionInfoCB;
import org.codelibs.fess.es.config.cbean.cq.CrawlingSessionInfoCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsCrawlingSessionInfoCQ;
import org.dbflute.cbean.ConditionQuery;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public class BsCrawlingSessionInfoCB extends EsAbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsCrawlingSessionInfoCQ _conditionQuery;
    protected HpSpecification _specification;

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======
    @Override
    public CrawlingSessionInfoDbm asDBMeta() {
        return CrawlingSessionInfoDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "crawling_session_info";
    }

    @Override
    public boolean hasSpecifiedColumn() {
        return _specification != null;
    }

    @Override
    public ConditionQuery localCQ() {
        return doGetConditionQuery();
    }

    // ===================================================================================
    //                                                                         Primary Key
    //                                                                         ===========
    public CrawlingSessionInfoCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsCrawlingSessionInfoCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (CrawlingSessionInfoCB) this;
    }

    @Override
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        acceptPK((String)primaryKeyMap.get("_id"));
    }

    // ===================================================================================
    //                                                                               Build
    //                                                                               =====
    @Override
    public CountRequestBuilder build(CountRequestBuilder builder) {
        if (_conditionQuery != null) {
            QueryBuilder queryBuilder = _conditionQuery.getQuery();
            if (queryBuilder != null) {
                builder.setQuery(queryBuilder);
            }
        }
        return builder;
    }

    @Override
    public SearchRequestBuilder build(SearchRequestBuilder builder) {
        if (_conditionQuery != null) {
            QueryBuilder queryBuilder = _conditionQuery.getQuery();
            if (queryBuilder != null) {
                builder.setQuery(queryBuilder);
            }
            _conditionQuery.getFieldSortBuilderList().forEach(sort -> {
                builder.addSort(sort);
            });
        }

        if (_specification != null) {
            builder.setFetchSource(_specification.columnList.toArray(new String[_specification.columnList.size()]), null);
        }

        return builder;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public BsCrawlingSessionInfoCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsCrawlingSessionInfoCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsCrawlingSessionInfoCQ createLocalCQ() {
        return new CrawlingSessionInfoCQ();
    }

    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    public HpSpecification specify() {
        assertSpecifyPurpose();
        if (_specification == null) {
            _specification = new HpSpecification();
        }
        return _specification;
    }

    protected void assertQueryPurpose() {
    }

    protected void assertSpecifyPurpose() {
    }

    public static class HpSpecification {
        private List<String> columnList = new ArrayList<>();

        private void doColumn(String name) {
            columnList.add(name);
        }

        public void columnId() {
            doColumn("_id");
        }

        public void columnCrawlingSessionId() {
            doColumn("crawlingSessionId");
        }
        public void columnCreatedTime() {
            doColumn("createdTime");
        }
        public void columnKey() {
            doColumn("key");
        }
        public void columnValue() {
            doColumn("value");
        }
    }
}
