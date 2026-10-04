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
import org.codelibs.fess.opensearch.config.bsentity.dbmeta.TagTypeDbm;
import org.codelibs.fess.opensearch.config.cbean.TagTypeCB;
import org.codelibs.fess.opensearch.config.cbean.ca.TagTypeCA;
import org.codelibs.fess.opensearch.config.cbean.ca.bs.BsTagTypeCA;
import org.codelibs.fess.opensearch.config.cbean.cq.TagTypeCQ;
import org.codelibs.fess.opensearch.config.cbean.cq.bs.BsTagTypeCQ;
import org.dbflute.cbean.ConditionQuery;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public class BsTagTypeCB extends EsAbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsTagTypeCQ _conditionQuery;
    protected BsTagTypeCA _conditionAggregation;
    protected HpSpecification _specification;

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======
    @Override
    public TagTypeDbm asDBMeta() {
        return TagTypeDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "tag_type";
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
    public TagTypeCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsTagTypeCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (TagTypeCB) this;
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
    public BsTagTypeCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsTagTypeCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsTagTypeCQ createLocalCQ() {
        return new TagTypeCQ();
    }

    // ===================================================================================
    //                                                                         Aggregation
    //                                                                         ===========
    public BsTagTypeCA aggregation() {
        assertAggregationPurpose();
        return doGetConditionAggregation();
    }

    protected BsTagTypeCA doGetConditionAggregation() {
        if (_conditionAggregation == null) {
            _conditionAggregation = createLocalCA();
        }
        return _conditionAggregation;
    }

    protected BsTagTypeCA createLocalCA() {
        return new TagTypeCA();
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

        public void columnName() {
            doColumn("name");
        }

        public void columnOwner() {
            doColumn("owner");
        }

        public void columnPaths() {
            doColumn("paths");
        }

        public void columnPermissions() {
            doColumn("permissions");
        }

        public void columnSortOrder() {
            doColumn("sortOrder");
        }

        public void columnUpdatedBy() {
            doColumn("updatedBy");
        }

        public void columnUpdatedTime() {
            doColumn("updatedTime");
        }

        public void columnVirtualHost() {
            doColumn("virtualHost");
        }
    }
}
