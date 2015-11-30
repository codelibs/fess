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
import org.codelibs.fess.es.config.bsentity.dbmeta.SuggestElevateWordDbm;
import org.codelibs.fess.es.config.cbean.SuggestElevateWordCB;
import org.codelibs.fess.es.config.cbean.cq.SuggestElevateWordCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsSuggestElevateWordCQ;
import org.dbflute.cbean.ConditionQuery;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public class BsSuggestElevateWordCB extends EsAbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsSuggestElevateWordCQ _conditionQuery;
    protected HpSpecification _specification;

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======
    @Override
    public SuggestElevateWordDbm asDBMeta() {
        return SuggestElevateWordDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "suggest_elevate_word";
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
    public SuggestElevateWordCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsSuggestElevateWordCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (SuggestElevateWordCB) this;
    }

    @Override
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        acceptPK((String) primaryKeyMap.get("_id"));
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
    public BsSuggestElevateWordCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsSuggestElevateWordCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsSuggestElevateWordCQ createLocalCQ() {
        return new SuggestElevateWordCQ();
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

        public void columnBoost() {
            doColumn("boost");
        }

        public void columnCreatedBy() {
            doColumn("createdBy");
        }

        public void columnCreatedTime() {
            doColumn("createdTime");
        }

        public void columnReading() {
            doColumn("reading");
        }

        public void columnSuggestWord() {
            doColumn("suggestWord");
        }

        public void columnTargetLabel() {
            doColumn("targetLabel");
        }

        public void columnTargetRole() {
            doColumn("targetRole");
        }

        public void columnUpdatedBy() {
            doColumn("updatedBy");
        }

        public void columnUpdatedTime() {
            doColumn("updatedTime");
        }
    }
}
