/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
import org.codelibs.fess.es.config.bsentity.dbmeta.KeyMatchDbm;
import org.codelibs.fess.es.config.cbean.KeyMatchCB;
import org.codelibs.fess.es.config.cbean.ca.KeyMatchCA;
import org.codelibs.fess.es.config.cbean.ca.bs.BsKeyMatchCA;
import org.codelibs.fess.es.config.cbean.cq.KeyMatchCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsKeyMatchCQ;
import org.dbflute.cbean.ConditionQuery;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public class BsKeyMatchCB extends EsAbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsKeyMatchCQ _conditionQuery;
    protected BsKeyMatchCA _conditionAggregation;
    protected HpSpecification _specification;

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======
    @Override
    public KeyMatchDbm asDBMeta() {
        return KeyMatchDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "key_match";
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
    public KeyMatchCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsKeyMatchCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (KeyMatchCB) this;
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
    public BsKeyMatchCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsKeyMatchCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsKeyMatchCQ createLocalCQ() {
        return new KeyMatchCQ();
    }

    // ===================================================================================
    //                                                                         Aggregation
    //                                                                         ===========
    public BsKeyMatchCA aggregation() {
        assertAggregationPurpose();
        return doGetConditionAggregation();
    }

    protected BsKeyMatchCA doGetConditionAggregation() {
        if (_conditionAggregation == null) {
            _conditionAggregation = createLocalCA();
        }
        return _conditionAggregation;
    }

    protected BsKeyMatchCA createLocalCA() {
        return new KeyMatchCA();
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
        private List<String> columnList = new ArrayList<>();

        private void doColumn(String name) {
            columnList.add(name);
        }

        public void columnId() {
            doColumn("_id");
        }

        public void columnBoost() {
            doColumn("boost");
        }

        public void columnCreatedBy() {
            doColumn("createdBy");
        }

        public void columnCreatedTime() {
            doColumn("createdTime");
        }

        public void columnMaxSize() {
            doColumn("maxSize");
        }

        public void columnQuery() {
            doColumn("query");
        }

        public void columnTerm() {
            doColumn("term");
        }

        public void columnUpdatedBy() {
            doColumn("updatedBy");
        }

        public void columnUpdatedTime() {
            doColumn("updatedTime");
        }
    }
}
