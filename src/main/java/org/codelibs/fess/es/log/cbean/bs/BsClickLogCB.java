/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.log.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.log.allcommon.EsAbstractConditionBean;
import org.codelibs.fess.es.log.bsentity.dbmeta.ClickLogDbm;
import org.codelibs.fess.es.log.cbean.ClickLogCB;
import org.codelibs.fess.es.log.cbean.ca.ClickLogCA;
import org.codelibs.fess.es.log.cbean.ca.bs.BsClickLogCA;
import org.codelibs.fess.es.log.cbean.cq.ClickLogCQ;
import org.codelibs.fess.es.log.cbean.cq.bs.BsClickLogCQ;
import org.dbflute.cbean.ConditionQuery;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.index.query.QueryBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public class BsClickLogCB extends EsAbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsClickLogCQ _conditionQuery;
    protected BsClickLogCA _conditionAggregation;
    protected HpSpecification _specification;

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======
    @Override
    public ClickLogDbm asDBMeta() {
        return ClickLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "click_log";
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
    public ClickLogCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsClickLogCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (ClickLogCB) this;
    }

    @Override
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        acceptPK((String) primaryKeyMap.get("_id"));
    }

    // ===================================================================================
    //                                                                               Build
    //                                                                               =====

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

        if (_conditionAggregation != null) {
            _conditionAggregation.getAggregationBuilderList().forEach(builder::addAggregation);
        }

        if (_specification != null) {
            builder.setFetchSource(_specification.columnList.toArray(new String[_specification.columnList.size()]), null);
        }

        return builder;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public BsClickLogCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsClickLogCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsClickLogCQ createLocalCQ() {
        return new ClickLogCQ();
    }

    // ===================================================================================
    //                                                                         Aggregation
    //                                                                         ===========
    public BsClickLogCA aggregation() {
        assertAggregationPurpose();
        return doGetConditionAggregation();
    }

    protected BsClickLogCA doGetConditionAggregation() {
        if (_conditionAggregation == null) {
            _conditionAggregation = createLocalCA();
        }
        return _conditionAggregation;
    }

    protected BsClickLogCA createLocalCA() {
        return new ClickLogCA();
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

    protected void assertAggregationPurpose() {
    }

    protected void assertSpecifyPurpose() {
    }

    public static class HpSpecification {
        protected List<String> columnList = new ArrayList<>();

        public void doColumn(String name) {
            columnList.add(name);
        }

        public void columnId() {
            doColumn("_id");
        }

        public void columnUrlId() {
            doColumn("urlId");
        }

        public void columnDocId() {
            doColumn("docId");
        }

        public void columnOrder() {
            doColumn("order");
        }

        public void columnQueryId() {
            doColumn("queryId");
        }

        public void columnQueryRequestedAt() {
            doColumn("queryRequestedAt");
        }

        public void columnRequestedAt() {
            doColumn("requestedAt");
        }

        public void columnUrl() {
            doColumn("url");
        }

        public void columnUserSessionId() {
            doColumn("userSessionId");
        }
    }
}
