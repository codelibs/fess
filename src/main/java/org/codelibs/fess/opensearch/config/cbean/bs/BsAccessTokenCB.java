/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.opensearch.config.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.opensearch.config.allcommon.EsAbstractConditionBean;
import org.codelibs.fess.opensearch.config.bsentity.dbmeta.AccessTokenDbm;
import org.codelibs.fess.opensearch.config.cbean.AccessTokenCB;
import org.codelibs.fess.opensearch.config.cbean.ca.AccessTokenCA;
import org.codelibs.fess.opensearch.config.cbean.ca.bs.BsAccessTokenCA;
import org.codelibs.fess.opensearch.config.cbean.cq.AccessTokenCQ;
import org.codelibs.fess.opensearch.config.cbean.cq.bs.BsAccessTokenCQ;
import org.dbflute.cbean.ConditionQuery;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.index.query.QueryBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public class BsAccessTokenCB extends EsAbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsAccessTokenCQ _conditionQuery;
    protected BsAccessTokenCA _conditionAggregation;
    protected HpSpecification _specification;

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======
    @Override
    public AccessTokenDbm asDBMeta() {
        return AccessTokenDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "access_token";
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
    public AccessTokenCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsAccessTokenCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (AccessTokenCB) this;
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
    public BsAccessTokenCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsAccessTokenCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsAccessTokenCQ createLocalCQ() {
        return new AccessTokenCQ();
    }

    // ===================================================================================
    //                                                                         Aggregation
    //                                                                         ===========
    public BsAccessTokenCA aggregation() {
        assertAggregationPurpose();
        return doGetConditionAggregation();
    }

    protected BsAccessTokenCA doGetConditionAggregation() {
        if (_conditionAggregation == null) {
            _conditionAggregation = createLocalCA();
        }
        return _conditionAggregation;
    }

    protected BsAccessTokenCA createLocalCA() {
        return new AccessTokenCA();
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

        public void columnCreatedBy() {
            doColumn("createdBy");
        }

        public void columnCreatedTime() {
            doColumn("createdTime");
        }

        public void columnExpiredTime() {
            doColumn("expiredTime");
        }

        public void columnName() {
            doColumn("name");
        }

        public void columnParameterName() {
            doColumn("parameter_name");
        }

        public void columnPermissions() {
            doColumn("permissions");
        }

        public void columnToken() {
            doColumn("token");
        }

        public void columnUpdatedBy() {
            doColumn("updatedBy");
        }

        public void columnUpdatedTime() {
            doColumn("updatedTime");
        }
    }
}
