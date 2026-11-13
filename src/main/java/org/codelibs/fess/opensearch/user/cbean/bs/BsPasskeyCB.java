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
package org.codelibs.fess.opensearch.user.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.opensearch.user.allcommon.EsAbstractConditionBean;
import org.codelibs.fess.opensearch.user.bsentity.dbmeta.PasskeyDbm;
import org.codelibs.fess.opensearch.user.cbean.PasskeyCB;
import org.codelibs.fess.opensearch.user.cbean.cq.PasskeyCQ;
import org.codelibs.fess.opensearch.user.cbean.cq.bs.BsPasskeyCQ;
import org.codelibs.fess.opensearch.user.cbean.ca.PasskeyCA;
import org.codelibs.fess.opensearch.user.cbean.ca.bs.BsPasskeyCA;
import org.dbflute.cbean.ConditionQuery;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public class BsPasskeyCB extends EsAbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsPasskeyCQ _conditionQuery;
    protected BsPasskeyCA _conditionAggregation;
    protected HpSpecification _specification;

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======
    @Override
    public PasskeyDbm asDBMeta() {
        return PasskeyDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "passkey";
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
    public PasskeyCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsPasskeyCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (PasskeyCB) this;
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
    public BsPasskeyCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsPasskeyCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsPasskeyCQ createLocalCQ() {
        return new PasskeyCQ();
    }

    // ===================================================================================
    //                                                                         Aggregation
    //                                                                         ===========
    public BsPasskeyCA aggregation() {
        assertAggregationPurpose();
        return doGetConditionAggregation();
    }

    protected BsPasskeyCA doGetConditionAggregation() {
        if (_conditionAggregation == null) {
            _conditionAggregation = createLocalCA();
        }
        return _conditionAggregation;
    }

    protected BsPasskeyCA createLocalCA() {
        return new PasskeyCA();
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

        public void columnBackupEligible() {
            doColumn("backupEligible");
        }

        public void columnBackupState() {
            doColumn("backupState");
        }

        public void columnCreatedAt() {
            doColumn("createdAt");
        }

        public void columnLabel() {
            doColumn("label");
        }

        public void columnLastUsedAt() {
            doColumn("lastUsedAt");
        }

        public void columnPublicKey() {
            doColumn("publicKey");
        }

        public void columnSignCount() {
            doColumn("signCount");
        }

        public void columnTransports() {
            doColumn("transports");
        }

        public void columnUserHandle() {
            doColumn("userHandle");
        }

        public void columnUserName() {
            doColumn("userName");
        }
    }
}
