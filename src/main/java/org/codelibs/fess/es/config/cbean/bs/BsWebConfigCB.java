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
package org.codelibs.fess.es.config.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.config.allcommon.EsAbstractConditionBean;
import org.codelibs.fess.es.config.bsentity.dbmeta.WebConfigDbm;
import org.codelibs.fess.es.config.cbean.WebConfigCB;
import org.codelibs.fess.es.config.cbean.ca.WebConfigCA;
import org.codelibs.fess.es.config.cbean.ca.bs.BsWebConfigCA;
import org.codelibs.fess.es.config.cbean.cq.WebConfigCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsWebConfigCQ;
import org.dbflute.cbean.ConditionQuery;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.index.query.QueryBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public class BsWebConfigCB extends EsAbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsWebConfigCQ _conditionQuery;
    protected BsWebConfigCA _conditionAggregation;
    protected HpSpecification _specification;

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======
    @Override
    public WebConfigDbm asDBMeta() {
        return WebConfigDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "web_config";
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
    public WebConfigCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsWebConfigCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (WebConfigCB) this;
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
    public BsWebConfigCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsWebConfigCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsWebConfigCQ createLocalCQ() {
        return new WebConfigCQ();
    }

    // ===================================================================================
    //                                                                         Aggregation
    //                                                                         ===========
    public BsWebConfigCA aggregation() {
        assertAggregationPurpose();
        return doGetConditionAggregation();
    }

    protected BsWebConfigCA doGetConditionAggregation() {
        if (_conditionAggregation == null) {
            _conditionAggregation = createLocalCA();
        }
        return _conditionAggregation;
    }

    protected BsWebConfigCA createLocalCA() {
        return new WebConfigCA();
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

        public void columnAvailable() {
            doColumn("available");
        }

        public void columnBoost() {
            doColumn("boost");
        }

        public void columnConfigParameter() {
            doColumn("configParameter");
        }

        public void columnCreatedBy() {
            doColumn("createdBy");
        }

        public void columnCreatedTime() {
            doColumn("createdTime");
        }

        public void columnDepth() {
            doColumn("depth");
        }

        public void columnDescription() {
            doColumn("description");
        }

        public void columnExcludedDocUrls() {
            doColumn("excludedDocUrls");
        }

        public void columnExcludedUrls() {
            doColumn("excludedUrls");
        }

        public void columnIncludedDocUrls() {
            doColumn("includedDocUrls");
        }

        public void columnIncludedUrls() {
            doColumn("includedUrls");
        }

        public void columnIntervalTime() {
            doColumn("intervalTime");
        }

        public void columnMaxAccessCount() {
            doColumn("maxAccessCount");
        }

        public void columnName() {
            doColumn("name");
        }

        public void columnNumOfThread() {
            doColumn("numOfThread");
        }

        public void columnPermissions() {
            doColumn("permissions");
        }

        public void columnSortOrder() {
            doColumn("sortOrder");
        }

        public void columnTimeToLive() {
            doColumn("timeToLive");
        }

        public void columnUpdatedBy() {
            doColumn("updatedBy");
        }

        public void columnUpdatedTime() {
            doColumn("updatedTime");
        }

        public void columnUrls() {
            doColumn("urls");
        }

        public void columnUserAgent() {
            doColumn("userAgent");
        }

        public void columnVirtualHosts() {
            doColumn("virtualHosts");
        }
    }
}
