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
import org.codelibs.fess.es.log.bsentity.dbmeta.SearchLogDbm;
import org.codelibs.fess.es.log.cbean.SearchLogCB;
import org.codelibs.fess.es.log.cbean.ca.SearchLogCA;
import org.codelibs.fess.es.log.cbean.ca.bs.BsSearchLogCA;
import org.codelibs.fess.es.log.cbean.cq.SearchLogCQ;
import org.codelibs.fess.es.log.cbean.cq.bs.BsSearchLogCQ;
import org.dbflute.cbean.ConditionQuery;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.index.query.QueryBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public class BsSearchLogCB extends EsAbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsSearchLogCQ _conditionQuery;
    protected BsSearchLogCA _conditionAggregation;
    protected HpSpecification _specification;

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======
    @Override
    public SearchLogDbm asDBMeta() {
        return SearchLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "search_log";
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
    public SearchLogCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsSearchLogCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (SearchLogCB) this;
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
    public BsSearchLogCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsSearchLogCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsSearchLogCQ createLocalCQ() {
        return new SearchLogCQ();
    }

    // ===================================================================================
    //                                                                         Aggregation
    //                                                                         ===========
    public BsSearchLogCA aggregation() {
        assertAggregationPurpose();
        return doGetConditionAggregation();
    }

    protected BsSearchLogCA doGetConditionAggregation() {
        if (_conditionAggregation == null) {
            _conditionAggregation = createLocalCA();
        }
        return _conditionAggregation;
    }

    protected BsSearchLogCA createLocalCA() {
        return new SearchLogCA();
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

        public void columnAccessType() {
            doColumn("accessType");
        }

        public void columnClientIp() {
            doColumn("clientIp");
        }

        public void columnHitCount() {
            doColumn("hitCount");
        }

        public void columnHitCountRelation() {
            doColumn("hitCountRelation");
        }

        public void columnLanguages() {
            doColumn("languages");
        }

        public void columnQueryId() {
            doColumn("queryId");
        }

        public void columnQueryOffset() {
            doColumn("queryOffset");
        }

        public void columnQueryPageSize() {
            doColumn("queryPageSize");
        }

        public void columnQueryTime() {
            doColumn("queryTime");
        }

        public void columnReferer() {
            doColumn("referer");
        }

        public void columnRequestedAt() {
            doColumn("requestedAt");
        }

        public void columnResponseTime() {
            doColumn("responseTime");
        }

        public void columnRoles() {
            doColumn("roles");
        }

        public void columnSearchWord() {
            doColumn("searchWord");
        }

        public void columnUser() {
            doColumn("user");
        }

        public void columnUserAgent() {
            doColumn("userAgent");
        }

        public void columnUserInfoId() {
            doColumn("userInfoId");
        }

        public void columnUserSessionId() {
            doColumn("userSessionId");
        }

        public void columnVirtualHost() {
            doColumn("virtualHost");
        }
    }
}
