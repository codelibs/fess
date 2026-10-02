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
package org.codelibs.fess.opensearch.log.cbean.bs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.opensearch.log.allcommon.EsAbstractConditionBean;
import org.codelibs.fess.opensearch.log.bsentity.dbmeta.ChatLogDbm;
import org.codelibs.fess.opensearch.log.cbean.ChatLogCB;
import org.codelibs.fess.opensearch.log.cbean.ca.ChatLogCA;
import org.codelibs.fess.opensearch.log.cbean.ca.bs.BsChatLogCA;
import org.codelibs.fess.opensearch.log.cbean.cq.ChatLogCQ;
import org.codelibs.fess.opensearch.log.cbean.cq.bs.BsChatLogCQ;
import org.dbflute.cbean.ConditionQuery;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public class BsChatLogCB extends EsAbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsChatLogCQ _conditionQuery;
    protected BsChatLogCA _conditionAggregation;
    protected HpSpecification _specification;

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======
    @Override
    public ChatLogDbm asDBMeta() {
        return ChatLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "chat_log";
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
    public ChatLogCB acceptPK(String id) {
        assertObjectNotNull("id", id);
        BsChatLogCB cb = this;
        cb.query().docMeta().setId_Equal(id);
        return (ChatLogCB) this;
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
    public BsChatLogCQ query() {
        assertQueryPurpose();
        return doGetConditionQuery();
    }

    protected BsChatLogCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected BsChatLogCQ createLocalCQ() {
        return new ChatLogCQ();
    }

    // ===================================================================================
    //                                                                         Aggregation
    //                                                                         ===========
    public BsChatLogCA aggregation() {
        assertAggregationPurpose();
        return doGetConditionAggregation();
    }

    protected BsChatLogCA doGetConditionAggregation() {
        if (_conditionAggregation == null) {
            _conditionAggregation = createLocalCA();
        }
        return _conditionAggregation;
    }

    protected BsChatLogCA createLocalCA() {
        return new ChatLogCA();
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

        public void columnChatSessionId() {
            doColumn("chatSessionId");
        }

        public void columnChatType() {
            doColumn("chatType");
        }

        public void columnCompletionTokens() {
            doColumn("completionTokens");
        }

        public void columnErrorCode() {
            doColumn("errorCode");
        }

        public void columnIntent() {
            doColumn("intent");
        }

        public void columnLlmCalls() {
            doColumn("llmCalls");
        }

        public void columnLlmName() {
            doColumn("llmName");
        }

        public void columnModel() {
            doColumn("model");
        }

        public void columnPromptTokens() {
            doColumn("promptTokens");
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

        public void columnSearchQueryId() {
            doColumn("searchQueryId");
        }

        public void columnSourceCount() {
            doColumn("sourceCount");
        }

        public void columnStatus() {
            doColumn("status");
        }

        public void columnTotalTokens() {
            doColumn("totalTokens");
        }

        public void columnUser() {
            doColumn("user");
        }

        public void columnUserSessionId() {
            doColumn("userSessionId");
        }

        public void columnVirtualHost() {
            doColumn("virtualHost");
        }
    }
}
