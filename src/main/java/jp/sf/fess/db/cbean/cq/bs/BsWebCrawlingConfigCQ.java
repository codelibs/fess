/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.db.cbean.cq.bs;

import java.util.Map;

import jp.sf.fess.db.cbean.WebCrawlingConfigCB;
import jp.sf.fess.db.cbean.cq.RequestHeaderCQ;
import jp.sf.fess.db.cbean.cq.WebAuthenticationCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.ciq.WebCrawlingConfigCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of WEB_CRAWLING_CONFIG.
 * @author DBFlute(AutoGenerator)
 */
public class BsWebCrawlingConfigCQ extends AbstractBsWebCrawlingConfigCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected WebCrawlingConfigCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsWebCrawlingConfigCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from WEB_CRAWLING_CONFIG) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public WebCrawlingConfigCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected WebCrawlingConfigCIQ xcreateCIQ() {
        final WebCrawlingConfigCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected WebCrawlingConfigCIQ xnewCIQ() {
        return new WebCrawlingConfigCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join WEB_CRAWLING_CONFIG on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public WebCrawlingConfigCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final WebCrawlingConfigCIQ inlineQuery = inline();
        inlineQuery.xsetOnClause(true);
        return inlineQuery;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    protected ConditionValue _id;

    public ConditionValue getId() {
        if (_id == null) {
            _id = nCV();
        }
        return _id;
    }

    @Override
    protected ConditionValue getCValueId() {
        return getId();
    }

    protected Map<String, RequestHeaderCQ> _id_ExistsReferrer_RequestHeaderListMap;

    public Map<String, RequestHeaderCQ> getId_ExistsReferrer_RequestHeaderList() {
        return _id_ExistsReferrer_RequestHeaderListMap;
    }

    @Override
    public String keepId_ExistsReferrer_RequestHeaderList(
            final RequestHeaderCQ subQuery) {
        if (_id_ExistsReferrer_RequestHeaderListMap == null) {
            _id_ExistsReferrer_RequestHeaderListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_RequestHeaderListMap.size() + 1);
        _id_ExistsReferrer_RequestHeaderListMap.put(key, subQuery);
        return "id_ExistsReferrer_RequestHeaderList." + key;
    }

    protected Map<String, WebAuthenticationCQ> _id_ExistsReferrer_WebAuthenticationListMap;

    public Map<String, WebAuthenticationCQ> getId_ExistsReferrer_WebAuthenticationList() {
        return _id_ExistsReferrer_WebAuthenticationListMap;
    }

    @Override
    public String keepId_ExistsReferrer_WebAuthenticationList(
            final WebAuthenticationCQ subQuery) {
        if (_id_ExistsReferrer_WebAuthenticationListMap == null) {
            _id_ExistsReferrer_WebAuthenticationListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_WebAuthenticationListMap.size() + 1);
        _id_ExistsReferrer_WebAuthenticationListMap.put(key, subQuery);
        return "id_ExistsReferrer_WebAuthenticationList." + key;
    }

    protected Map<String, WebConfigToBrowserTypeMappingCQ> _id_ExistsReferrer_WebConfigToBrowserTypeMappingListMap;

    public Map<String, WebConfigToBrowserTypeMappingCQ> getId_ExistsReferrer_WebConfigToBrowserTypeMappingList() {
        return _id_ExistsReferrer_WebConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_ExistsReferrer_WebConfigToBrowserTypeMappingList(
            final WebConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_ExistsReferrer_WebConfigToBrowserTypeMappingListMap == null) {
            _id_ExistsReferrer_WebConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_WebConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_ExistsReferrer_WebConfigToBrowserTypeMappingListMap.put(key,
                subQuery);
        return "id_ExistsReferrer_WebConfigToBrowserTypeMappingList." + key;
    }

    protected Map<String, WebConfigToLabelTypeMappingCQ> _id_ExistsReferrer_WebConfigToLabelTypeMappingListMap;

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_ExistsReferrer_WebConfigToLabelTypeMappingList() {
        return _id_ExistsReferrer_WebConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_ExistsReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ subQuery) {
        if (_id_ExistsReferrer_WebConfigToLabelTypeMappingListMap == null) {
            _id_ExistsReferrer_WebConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_WebConfigToLabelTypeMappingListMap.size() + 1);
        _id_ExistsReferrer_WebConfigToLabelTypeMappingListMap
                .put(key, subQuery);
        return "id_ExistsReferrer_WebConfigToLabelTypeMappingList." + key;
    }

    protected Map<String, WebConfigToRoleTypeMappingCQ> _id_ExistsReferrer_WebConfigToRoleTypeMappingListMap;

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_ExistsReferrer_WebConfigToRoleTypeMappingList() {
        return _id_ExistsReferrer_WebConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_ExistsReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ subQuery) {
        if (_id_ExistsReferrer_WebConfigToRoleTypeMappingListMap == null) {
            _id_ExistsReferrer_WebConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_WebConfigToRoleTypeMappingListMap.size() + 1);
        _id_ExistsReferrer_WebConfigToRoleTypeMappingListMap.put(key, subQuery);
        return "id_ExistsReferrer_WebConfigToRoleTypeMappingList." + key;
    }

    protected Map<String, RequestHeaderCQ> _id_NotExistsReferrer_RequestHeaderListMap;

    public Map<String, RequestHeaderCQ> getId_NotExistsReferrer_RequestHeaderList() {
        return _id_NotExistsReferrer_RequestHeaderListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_RequestHeaderList(
            final RequestHeaderCQ subQuery) {
        if (_id_NotExistsReferrer_RequestHeaderListMap == null) {
            _id_NotExistsReferrer_RequestHeaderListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_RequestHeaderListMap.size() + 1);
        _id_NotExistsReferrer_RequestHeaderListMap.put(key, subQuery);
        return "id_NotExistsReferrer_RequestHeaderList." + key;
    }

    protected Map<String, WebAuthenticationCQ> _id_NotExistsReferrer_WebAuthenticationListMap;

    public Map<String, WebAuthenticationCQ> getId_NotExistsReferrer_WebAuthenticationList() {
        return _id_NotExistsReferrer_WebAuthenticationListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_WebAuthenticationList(
            final WebAuthenticationCQ subQuery) {
        if (_id_NotExistsReferrer_WebAuthenticationListMap == null) {
            _id_NotExistsReferrer_WebAuthenticationListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_WebAuthenticationListMap.size() + 1);
        _id_NotExistsReferrer_WebAuthenticationListMap.put(key, subQuery);
        return "id_NotExistsReferrer_WebAuthenticationList." + key;
    }

    protected Map<String, WebConfigToBrowserTypeMappingCQ> _id_NotExistsReferrer_WebConfigToBrowserTypeMappingListMap;

    public Map<String, WebConfigToBrowserTypeMappingCQ> getId_NotExistsReferrer_WebConfigToBrowserTypeMappingList() {
        return _id_NotExistsReferrer_WebConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_WebConfigToBrowserTypeMappingList(
            final WebConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_NotExistsReferrer_WebConfigToBrowserTypeMappingListMap == null) {
            _id_NotExistsReferrer_WebConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_WebConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_NotExistsReferrer_WebConfigToBrowserTypeMappingListMap.put(key,
                subQuery);
        return "id_NotExistsReferrer_WebConfigToBrowserTypeMappingList." + key;
    }

    protected Map<String, WebConfigToLabelTypeMappingCQ> _id_NotExistsReferrer_WebConfigToLabelTypeMappingListMap;

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_NotExistsReferrer_WebConfigToLabelTypeMappingList() {
        return _id_NotExistsReferrer_WebConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ subQuery) {
        if (_id_NotExistsReferrer_WebConfigToLabelTypeMappingListMap == null) {
            _id_NotExistsReferrer_WebConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_WebConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_NotExistsReferrer_WebConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_NotExistsReferrer_WebConfigToLabelTypeMappingList." + key;
    }

    protected Map<String, WebConfigToRoleTypeMappingCQ> _id_NotExistsReferrer_WebConfigToRoleTypeMappingListMap;

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_NotExistsReferrer_WebConfigToRoleTypeMappingList() {
        return _id_NotExistsReferrer_WebConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ subQuery) {
        if (_id_NotExistsReferrer_WebConfigToRoleTypeMappingListMap == null) {
            _id_NotExistsReferrer_WebConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_WebConfigToRoleTypeMappingListMap
                        .size() + 1);
        _id_NotExistsReferrer_WebConfigToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_NotExistsReferrer_WebConfigToRoleTypeMappingList." + key;
    }

    protected Map<String, RequestHeaderCQ> _id_SpecifyDerivedReferrer_RequestHeaderListMap;

    public Map<String, RequestHeaderCQ> getId_SpecifyDerivedReferrer_RequestHeaderList() {
        return _id_SpecifyDerivedReferrer_RequestHeaderListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_RequestHeaderList(
            final RequestHeaderCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_RequestHeaderListMap == null) {
            _id_SpecifyDerivedReferrer_RequestHeaderListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_RequestHeaderListMap.size() + 1);
        _id_SpecifyDerivedReferrer_RequestHeaderListMap.put(key, subQuery);
        return "id_SpecifyDerivedReferrer_RequestHeaderList." + key;
    }

    protected Map<String, WebAuthenticationCQ> _id_SpecifyDerivedReferrer_WebAuthenticationListMap;

    public Map<String, WebAuthenticationCQ> getId_SpecifyDerivedReferrer_WebAuthenticationList() {
        return _id_SpecifyDerivedReferrer_WebAuthenticationListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_WebAuthenticationList(
            final WebAuthenticationCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_WebAuthenticationListMap == null) {
            _id_SpecifyDerivedReferrer_WebAuthenticationListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_WebAuthenticationListMap.size() + 1);
        _id_SpecifyDerivedReferrer_WebAuthenticationListMap.put(key, subQuery);
        return "id_SpecifyDerivedReferrer_WebAuthenticationList." + key;
    }

    protected Map<String, WebConfigToBrowserTypeMappingCQ> _id_SpecifyDerivedReferrer_WebConfigToBrowserTypeMappingListMap;

    public Map<String, WebConfigToBrowserTypeMappingCQ> getId_SpecifyDerivedReferrer_WebConfigToBrowserTypeMappingList() {
        return _id_SpecifyDerivedReferrer_WebConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_WebConfigToBrowserTypeMappingList(
            final WebConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_WebConfigToBrowserTypeMappingListMap == null) {
            _id_SpecifyDerivedReferrer_WebConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_WebConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_SpecifyDerivedReferrer_WebConfigToBrowserTypeMappingListMap.put(
                key, subQuery);
        return "id_SpecifyDerivedReferrer_WebConfigToBrowserTypeMappingList."
                + key;
    }

    protected Map<String, WebConfigToLabelTypeMappingCQ> _id_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingListMap;

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingList() {
        return _id_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingListMap == null) {
            _id_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingList."
                + key;
    }

    protected Map<String, WebConfigToRoleTypeMappingCQ> _id_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingListMap;

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList() {
        return _id_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingListMap == null) {
            _id_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingListMap
                        .size() + 1);
        _id_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList."
                + key;
    }

    protected Map<String, RequestHeaderCQ> _id_InScopeRelation_RequestHeaderListMap;

    public Map<String, RequestHeaderCQ> getId_InScopeRelation_RequestHeaderList() {
        return _id_InScopeRelation_RequestHeaderListMap;
    }

    @Override
    public String keepId_InScopeRelation_RequestHeaderList(
            final RequestHeaderCQ subQuery) {
        if (_id_InScopeRelation_RequestHeaderListMap == null) {
            _id_InScopeRelation_RequestHeaderListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_RequestHeaderListMap.size() + 1);
        _id_InScopeRelation_RequestHeaderListMap.put(key, subQuery);
        return "id_InScopeRelation_RequestHeaderList." + key;
    }

    protected Map<String, WebAuthenticationCQ> _id_InScopeRelation_WebAuthenticationListMap;

    public Map<String, WebAuthenticationCQ> getId_InScopeRelation_WebAuthenticationList() {
        return _id_InScopeRelation_WebAuthenticationListMap;
    }

    @Override
    public String keepId_InScopeRelation_WebAuthenticationList(
            final WebAuthenticationCQ subQuery) {
        if (_id_InScopeRelation_WebAuthenticationListMap == null) {
            _id_InScopeRelation_WebAuthenticationListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_WebAuthenticationListMap.size() + 1);
        _id_InScopeRelation_WebAuthenticationListMap.put(key, subQuery);
        return "id_InScopeRelation_WebAuthenticationList." + key;
    }

    protected Map<String, WebConfigToBrowserTypeMappingCQ> _id_InScopeRelation_WebConfigToBrowserTypeMappingListMap;

    public Map<String, WebConfigToBrowserTypeMappingCQ> getId_InScopeRelation_WebConfigToBrowserTypeMappingList() {
        return _id_InScopeRelation_WebConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_InScopeRelation_WebConfigToBrowserTypeMappingList(
            final WebConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_InScopeRelation_WebConfigToBrowserTypeMappingListMap == null) {
            _id_InScopeRelation_WebConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_WebConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_InScopeRelation_WebConfigToBrowserTypeMappingListMap.put(key,
                subQuery);
        return "id_InScopeRelation_WebConfigToBrowserTypeMappingList." + key;
    }

    protected Map<String, WebConfigToLabelTypeMappingCQ> _id_InScopeRelation_WebConfigToLabelTypeMappingListMap;

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_InScopeRelation_WebConfigToLabelTypeMappingList() {
        return _id_InScopeRelation_WebConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_InScopeRelation_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ subQuery) {
        if (_id_InScopeRelation_WebConfigToLabelTypeMappingListMap == null) {
            _id_InScopeRelation_WebConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_WebConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_InScopeRelation_WebConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_InScopeRelation_WebConfigToLabelTypeMappingList." + key;
    }

    protected Map<String, WebConfigToRoleTypeMappingCQ> _id_InScopeRelation_WebConfigToRoleTypeMappingListMap;

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_InScopeRelation_WebConfigToRoleTypeMappingList() {
        return _id_InScopeRelation_WebConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_InScopeRelation_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ subQuery) {
        if (_id_InScopeRelation_WebConfigToRoleTypeMappingListMap == null) {
            _id_InScopeRelation_WebConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_WebConfigToRoleTypeMappingListMap.size() + 1);
        _id_InScopeRelation_WebConfigToRoleTypeMappingListMap
                .put(key, subQuery);
        return "id_InScopeRelation_WebConfigToRoleTypeMappingList." + key;
    }

    protected Map<String, RequestHeaderCQ> _id_NotInScopeRelation_RequestHeaderListMap;

    public Map<String, RequestHeaderCQ> getId_NotInScopeRelation_RequestHeaderList() {
        return _id_NotInScopeRelation_RequestHeaderListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_RequestHeaderList(
            final RequestHeaderCQ subQuery) {
        if (_id_NotInScopeRelation_RequestHeaderListMap == null) {
            _id_NotInScopeRelation_RequestHeaderListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_RequestHeaderListMap.size() + 1);
        _id_NotInScopeRelation_RequestHeaderListMap.put(key, subQuery);
        return "id_NotInScopeRelation_RequestHeaderList." + key;
    }

    protected Map<String, WebAuthenticationCQ> _id_NotInScopeRelation_WebAuthenticationListMap;

    public Map<String, WebAuthenticationCQ> getId_NotInScopeRelation_WebAuthenticationList() {
        return _id_NotInScopeRelation_WebAuthenticationListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_WebAuthenticationList(
            final WebAuthenticationCQ subQuery) {
        if (_id_NotInScopeRelation_WebAuthenticationListMap == null) {
            _id_NotInScopeRelation_WebAuthenticationListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_WebAuthenticationListMap.size() + 1);
        _id_NotInScopeRelation_WebAuthenticationListMap.put(key, subQuery);
        return "id_NotInScopeRelation_WebAuthenticationList." + key;
    }

    protected Map<String, WebConfigToBrowserTypeMappingCQ> _id_NotInScopeRelation_WebConfigToBrowserTypeMappingListMap;

    public Map<String, WebConfigToBrowserTypeMappingCQ> getId_NotInScopeRelation_WebConfigToBrowserTypeMappingList() {
        return _id_NotInScopeRelation_WebConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_WebConfigToBrowserTypeMappingList(
            final WebConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_NotInScopeRelation_WebConfigToBrowserTypeMappingListMap == null) {
            _id_NotInScopeRelation_WebConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_WebConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_NotInScopeRelation_WebConfigToBrowserTypeMappingListMap.put(key,
                subQuery);
        return "id_NotInScopeRelation_WebConfigToBrowserTypeMappingList." + key;
    }

    protected Map<String, WebConfigToLabelTypeMappingCQ> _id_NotInScopeRelation_WebConfigToLabelTypeMappingListMap;

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_NotInScopeRelation_WebConfigToLabelTypeMappingList() {
        return _id_NotInScopeRelation_WebConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ subQuery) {
        if (_id_NotInScopeRelation_WebConfigToLabelTypeMappingListMap == null) {
            _id_NotInScopeRelation_WebConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_WebConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_NotInScopeRelation_WebConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_NotInScopeRelation_WebConfigToLabelTypeMappingList." + key;
    }

    protected Map<String, WebConfigToRoleTypeMappingCQ> _id_NotInScopeRelation_WebConfigToRoleTypeMappingListMap;

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_NotInScopeRelation_WebConfigToRoleTypeMappingList() {
        return _id_NotInScopeRelation_WebConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ subQuery) {
        if (_id_NotInScopeRelation_WebConfigToRoleTypeMappingListMap == null) {
            _id_NotInScopeRelation_WebConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_WebConfigToRoleTypeMappingListMap
                        .size() + 1);
        _id_NotInScopeRelation_WebConfigToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_NotInScopeRelation_WebConfigToRoleTypeMappingList." + key;
    }

    protected Map<String, RequestHeaderCQ> _id_QueryDerivedReferrer_RequestHeaderListMap;

    public Map<String, RequestHeaderCQ> getId_QueryDerivedReferrer_RequestHeaderList() {
        return _id_QueryDerivedReferrer_RequestHeaderListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_RequestHeaderList(
            final RequestHeaderCQ subQuery) {
        if (_id_QueryDerivedReferrer_RequestHeaderListMap == null) {
            _id_QueryDerivedReferrer_RequestHeaderListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_RequestHeaderListMap.size() + 1);
        _id_QueryDerivedReferrer_RequestHeaderListMap.put(key, subQuery);
        return "id_QueryDerivedReferrer_RequestHeaderList." + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_RequestHeaderListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_RequestHeaderListParameter() {
        return _id_QueryDerivedReferrer_RequestHeaderListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_RequestHeaderListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_RequestHeaderListParameterMap == null) {
            _id_QueryDerivedReferrer_RequestHeaderListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_RequestHeaderListParameterMap
                        .size() + 1);
        _id_QueryDerivedReferrer_RequestHeaderListParameterMap.put(key,
                parameterValue);
        return "id_QueryDerivedReferrer_RequestHeaderListParameter." + key;
    }

    protected Map<String, WebAuthenticationCQ> _id_QueryDerivedReferrer_WebAuthenticationListMap;

    public Map<String, WebAuthenticationCQ> getId_QueryDerivedReferrer_WebAuthenticationList() {
        return _id_QueryDerivedReferrer_WebAuthenticationListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebAuthenticationList(
            final WebAuthenticationCQ subQuery) {
        if (_id_QueryDerivedReferrer_WebAuthenticationListMap == null) {
            _id_QueryDerivedReferrer_WebAuthenticationListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_WebAuthenticationListMap.size() + 1);
        _id_QueryDerivedReferrer_WebAuthenticationListMap.put(key, subQuery);
        return "id_QueryDerivedReferrer_WebAuthenticationList." + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_WebAuthenticationListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_WebAuthenticationListParameter() {
        return _id_QueryDerivedReferrer_WebAuthenticationListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebAuthenticationListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_WebAuthenticationListParameterMap == null) {
            _id_QueryDerivedReferrer_WebAuthenticationListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_WebAuthenticationListParameterMap
                        .size() + 1);
        _id_QueryDerivedReferrer_WebAuthenticationListParameterMap.put(key,
                parameterValue);
        return "id_QueryDerivedReferrer_WebAuthenticationListParameter." + key;
    }

    protected Map<String, WebConfigToBrowserTypeMappingCQ> _id_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListMap;

    public Map<String, WebConfigToBrowserTypeMappingCQ> getId_QueryDerivedReferrer_WebConfigToBrowserTypeMappingList() {
        return _id_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToBrowserTypeMappingList(
            final WebConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListMap == null) {
            _id_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListMap.put(key,
                subQuery);
        return "id_QueryDerivedReferrer_WebConfigToBrowserTypeMappingList."
                + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListParameter() {
        return _id_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListParameterMap == null) {
            _id_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListParameterMap
                        .size() + 1);
        _id_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListParameterMap
                .put(key, parameterValue);
        return "id_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListParameter."
                + key;
    }

    protected Map<String, WebConfigToLabelTypeMappingCQ> _id_QueryDerivedReferrer_WebConfigToLabelTypeMappingListMap;

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_QueryDerivedReferrer_WebConfigToLabelTypeMappingList() {
        return _id_QueryDerivedReferrer_WebConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ subQuery) {
        if (_id_QueryDerivedReferrer_WebConfigToLabelTypeMappingListMap == null) {
            _id_QueryDerivedReferrer_WebConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_WebConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_QueryDerivedReferrer_WebConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_QueryDerivedReferrer_WebConfigToLabelTypeMappingList." + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameter() {
        return _id_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameterMap == null) {
            _id_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameterMap
                        .size() + 1);
        _id_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameterMap
                .put(key, parameterValue);
        return "id_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameter."
                + key;
    }

    protected Map<String, WebConfigToRoleTypeMappingCQ> _id_QueryDerivedReferrer_WebConfigToRoleTypeMappingListMap;

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_QueryDerivedReferrer_WebConfigToRoleTypeMappingList() {
        return _id_QueryDerivedReferrer_WebConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ subQuery) {
        if (_id_QueryDerivedReferrer_WebConfigToRoleTypeMappingListMap == null) {
            _id_QueryDerivedReferrer_WebConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_WebConfigToRoleTypeMappingListMap
                        .size() + 1);
        _id_QueryDerivedReferrer_WebConfigToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_QueryDerivedReferrer_WebConfigToRoleTypeMappingList." + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameter() {
        return _id_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameterMap == null) {
            _id_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameterMap
                        .size() + 1);
        _id_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameterMap
                .put(key, parameterValue);
        return "id_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameter."
                + key;
    }

    /** 
     * Add order-by as ascend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _name;

    public ConditionValue getName() {
        if (_name == null) {
            _name = nCV();
        }
        return _name;
    }

    @Override
    protected ConditionValue getCValueName() {
        return getName();
    }

    /** 
     * Add order-by as ascend. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Name_Asc() {
        regOBA("NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Name_Desc() {
        regOBD("NAME");
        return this;
    }

    protected ConditionValue _urls;

    public ConditionValue getUrls() {
        if (_urls == null) {
            _urls = nCV();
        }
        return _urls;
    }

    @Override
    protected ConditionValue getCValueUrls() {
        return getUrls();
    }

    /** 
     * Add order-by as ascend. <br />
     * URLS: {NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Urls_Asc() {
        regOBA("URLS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * URLS: {NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Urls_Desc() {
        regOBD("URLS");
        return this;
    }

    protected ConditionValue _includedUrls;

    public ConditionValue getIncludedUrls() {
        if (_includedUrls == null) {
            _includedUrls = nCV();
        }
        return _includedUrls;
    }

    @Override
    protected ConditionValue getCValueIncludedUrls() {
        return getIncludedUrls();
    }

    /** 
     * Add order-by as ascend. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_IncludedUrls_Asc() {
        regOBA("INCLUDED_URLS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_IncludedUrls_Desc() {
        regOBD("INCLUDED_URLS");
        return this;
    }

    protected ConditionValue _excludedUrls;

    public ConditionValue getExcludedUrls() {
        if (_excludedUrls == null) {
            _excludedUrls = nCV();
        }
        return _excludedUrls;
    }

    @Override
    protected ConditionValue getCValueExcludedUrls() {
        return getExcludedUrls();
    }

    /** 
     * Add order-by as ascend. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_ExcludedUrls_Asc() {
        regOBA("EXCLUDED_URLS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_ExcludedUrls_Desc() {
        regOBD("EXCLUDED_URLS");
        return this;
    }

    protected ConditionValue _includedDocUrls;

    public ConditionValue getIncludedDocUrls() {
        if (_includedDocUrls == null) {
            _includedDocUrls = nCV();
        }
        return _includedDocUrls;
    }

    @Override
    protected ConditionValue getCValueIncludedDocUrls() {
        return getIncludedDocUrls();
    }

    /** 
     * Add order-by as ascend. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_IncludedDocUrls_Asc() {
        regOBA("INCLUDED_DOC_URLS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_IncludedDocUrls_Desc() {
        regOBD("INCLUDED_DOC_URLS");
        return this;
    }

    protected ConditionValue _excludedDocUrls;

    public ConditionValue getExcludedDocUrls() {
        if (_excludedDocUrls == null) {
            _excludedDocUrls = nCV();
        }
        return _excludedDocUrls;
    }

    @Override
    protected ConditionValue getCValueExcludedDocUrls() {
        return getExcludedDocUrls();
    }

    /** 
     * Add order-by as ascend. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_ExcludedDocUrls_Asc() {
        regOBA("EXCLUDED_DOC_URLS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_ExcludedDocUrls_Desc() {
        regOBD("EXCLUDED_DOC_URLS");
        return this;
    }

    protected ConditionValue _configParameter;

    public ConditionValue getConfigParameter() {
        if (_configParameter == null) {
            _configParameter = nCV();
        }
        return _configParameter;
    }

    @Override
    protected ConditionValue getCValueConfigParameter() {
        return getConfigParameter();
    }

    /** 
     * Add order-by as ascend. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_ConfigParameter_Asc() {
        regOBA("CONFIG_PARAMETER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_ConfigParameter_Desc() {
        regOBD("CONFIG_PARAMETER");
        return this;
    }

    protected ConditionValue _depth;

    public ConditionValue getDepth() {
        if (_depth == null) {
            _depth = nCV();
        }
        return _depth;
    }

    @Override
    protected ConditionValue getCValueDepth() {
        return getDepth();
    }

    /** 
     * Add order-by as ascend. <br />
     * DEPTH: {INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Depth_Asc() {
        regOBA("DEPTH");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DEPTH: {INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Depth_Desc() {
        regOBD("DEPTH");
        return this;
    }

    protected ConditionValue _maxAccessCount;

    public ConditionValue getMaxAccessCount() {
        if (_maxAccessCount == null) {
            _maxAccessCount = nCV();
        }
        return _maxAccessCount;
    }

    @Override
    protected ConditionValue getCValueMaxAccessCount() {
        return getMaxAccessCount();
    }

    /** 
     * Add order-by as ascend. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_MaxAccessCount_Asc() {
        regOBA("MAX_ACCESS_COUNT");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_MaxAccessCount_Desc() {
        regOBD("MAX_ACCESS_COUNT");
        return this;
    }

    protected ConditionValue _userAgent;

    public ConditionValue getUserAgent() {
        if (_userAgent == null) {
            _userAgent = nCV();
        }
        return _userAgent;
    }

    @Override
    protected ConditionValue getCValueUserAgent() {
        return getUserAgent();
    }

    /** 
     * Add order-by as ascend. <br />
     * USER_AGENT: {NotNull, VARCHAR(200)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_UserAgent_Asc() {
        regOBA("USER_AGENT");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * USER_AGENT: {NotNull, VARCHAR(200)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_UserAgent_Desc() {
        regOBD("USER_AGENT");
        return this;
    }

    protected ConditionValue _numOfThread;

    public ConditionValue getNumOfThread() {
        if (_numOfThread == null) {
            _numOfThread = nCV();
        }
        return _numOfThread;
    }

    @Override
    protected ConditionValue getCValueNumOfThread() {
        return getNumOfThread();
    }

    /** 
     * Add order-by as ascend. <br />
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_NumOfThread_Asc() {
        regOBA("NUM_OF_THREAD");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_NumOfThread_Desc() {
        regOBD("NUM_OF_THREAD");
        return this;
    }

    protected ConditionValue _intervalTime;

    public ConditionValue getIntervalTime() {
        if (_intervalTime == null) {
            _intervalTime = nCV();
        }
        return _intervalTime;
    }

    @Override
    protected ConditionValue getCValueIntervalTime() {
        return getIntervalTime();
    }

    /** 
     * Add order-by as ascend. <br />
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_IntervalTime_Asc() {
        regOBA("INTERVAL_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_IntervalTime_Desc() {
        regOBD("INTERVAL_TIME");
        return this;
    }

    protected ConditionValue _boost;

    public ConditionValue getBoost() {
        if (_boost == null) {
            _boost = nCV();
        }
        return _boost;
    }

    @Override
    protected ConditionValue getCValueBoost() {
        return getBoost();
    }

    /** 
     * Add order-by as ascend. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Boost_Asc() {
        regOBA("BOOST");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Boost_Desc() {
        regOBD("BOOST");
        return this;
    }

    protected ConditionValue _available;

    public ConditionValue getAvailable() {
        if (_available == null) {
            _available = nCV();
        }
        return _available;
    }

    @Override
    protected ConditionValue getCValueAvailable() {
        return getAvailable();
    }

    /** 
     * Add order-by as ascend. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Available_Asc() {
        regOBA("AVAILABLE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Available_Desc() {
        regOBD("AVAILABLE");
        return this;
    }

    protected ConditionValue _sortOrder;

    public ConditionValue getSortOrder() {
        if (_sortOrder == null) {
            _sortOrder = nCV();
        }
        return _sortOrder;
    }

    @Override
    protected ConditionValue getCValueSortOrder() {
        return getSortOrder();
    }

    /** 
     * Add order-by as ascend. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_SortOrder_Asc() {
        regOBA("SORT_ORDER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_SortOrder_Desc() {
        regOBD("SORT_ORDER");
        return this;
    }

    protected ConditionValue _createdBy;

    public ConditionValue getCreatedBy() {
        if (_createdBy == null) {
            _createdBy = nCV();
        }
        return _createdBy;
    }

    @Override
    protected ConditionValue getCValueCreatedBy() {
        return getCreatedBy();
    }

    /** 
     * Add order-by as ascend. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_CreatedBy_Asc() {
        regOBA("CREATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_CreatedBy_Desc() {
        regOBD("CREATED_BY");
        return this;
    }

    protected ConditionValue _createdTime;

    public ConditionValue getCreatedTime() {
        if (_createdTime == null) {
            _createdTime = nCV();
        }
        return _createdTime;
    }

    @Override
    protected ConditionValue getCValueCreatedTime() {
        return getCreatedTime();
    }

    /** 
     * Add order-by as ascend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_CreatedTime_Asc() {
        regOBA("CREATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_CreatedTime_Desc() {
        regOBD("CREATED_TIME");
        return this;
    }

    protected ConditionValue _updatedBy;

    public ConditionValue getUpdatedBy() {
        if (_updatedBy == null) {
            _updatedBy = nCV();
        }
        return _updatedBy;
    }

    @Override
    protected ConditionValue getCValueUpdatedBy() {
        return getUpdatedBy();
    }

    /** 
     * Add order-by as ascend. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("UPDATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_UpdatedBy_Desc() {
        regOBD("UPDATED_BY");
        return this;
    }

    protected ConditionValue _updatedTime;

    public ConditionValue getUpdatedTime() {
        if (_updatedTime == null) {
            _updatedTime = nCV();
        }
        return _updatedTime;
    }

    @Override
    protected ConditionValue getCValueUpdatedTime() {
        return getUpdatedTime();
    }

    /** 
     * Add order-by as ascend. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("UPDATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("UPDATED_TIME");
        return this;
    }

    protected ConditionValue _deletedBy;

    public ConditionValue getDeletedBy() {
        if (_deletedBy == null) {
            _deletedBy = nCV();
        }
        return _deletedBy;
    }

    @Override
    protected ConditionValue getCValueDeletedBy() {
        return getDeletedBy();
    }

    /** 
     * Add order-by as ascend. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_DeletedBy_Asc() {
        regOBA("DELETED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_DeletedBy_Desc() {
        regOBD("DELETED_BY");
        return this;
    }

    protected ConditionValue _deletedTime;

    public ConditionValue getDeletedTime() {
        if (_deletedTime == null) {
            _deletedTime = nCV();
        }
        return _deletedTime;
    }

    @Override
    protected ConditionValue getCValueDeletedTime() {
        return getDeletedTime();
    }

    /** 
     * Add order-by as ascend. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_DeletedTime_Asc() {
        regOBA("DELETED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_DeletedTime_Desc() {
        regOBD("DELETED_TIME");
        return this;
    }

    protected ConditionValue _versionNo;

    public ConditionValue getVersionNo() {
        if (_versionNo == null) {
            _versionNo = nCV();
        }
        return _versionNo;
    }

    @Override
    protected ConditionValue getCValueVersionNo() {
        return getVersionNo();
    }

    /** 
     * Add order-by as ascend. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_VersionNo_Asc() {
        regOBA("VERSION_NO");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_VersionNo_Desc() {
        regOBD("VERSION_NO");
        return this;
    }

    // ===================================================================================
    //                                                             SpecifiedDerivedOrderBy
    //                                                             =======================
    /**
     * Add order-by for specified derived column as ascend.
     * <pre>
     * cb.specify().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime();
     *     }
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #FD4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addSpecifiedDerivedOrderBy_Asc(
            final String aliasName) {
        registerSpecifiedDerivedOrderBy_Asc(aliasName);
        return this;
    }

    /**
     * Add order-by for specified derived column as descend.
     * <pre>
     * cb.specify().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime();
     *     }
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #FD4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addSpecifiedDerivedOrderBy_Desc(
            final String aliasName) {
        registerSpecifiedDerivedOrderBy_Desc(aliasName);
        return this;
    }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    @Override
    protected void reflectRelationOnUnionQuery(
            final ConditionQuery baseQueryAsSuper,
            final ConditionQuery unionQueryAsSuper) {
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    protected Map<String, WebCrawlingConfigCQ> _scalarConditionMap;

    public Map<String, WebCrawlingConfigCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final WebCrawlingConfigCQ subQuery) {
        if (_scalarConditionMap == null) {
            _scalarConditionMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey" + (_scalarConditionMap.size() + 1);
        _scalarConditionMap.put(key, subQuery);
        return "scalarCondition." + key;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    protected Map<String, WebCrawlingConfigCQ> _specifyMyselfDerivedMap;

    public Map<String, WebCrawlingConfigCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final WebCrawlingConfigCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, WebCrawlingConfigCQ> _queryMyselfDerivedMap;

    public Map<String, WebCrawlingConfigCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final WebCrawlingConfigCQ subQuery) {
        if (_queryMyselfDerivedMap == null) {
            _queryMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_queryMyselfDerivedMap.size() + 1);
        _queryMyselfDerivedMap.put(key, subQuery);
        return "queryMyselfDerived." + key;
    }

    protected Map<String, Object> _qyeryMyselfDerivedParameterMap;

    public Map<String, Object> getQueryMyselfDerivedParameter() {
        return _qyeryMyselfDerivedParameterMap;
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object parameterValue) {
        if (_qyeryMyselfDerivedParameterMap == null) {
            _qyeryMyselfDerivedParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_qyeryMyselfDerivedParameterMap.size() + 1);
        _qyeryMyselfDerivedParameterMap.put(key, parameterValue);
        return "queryMyselfDerivedParameter." + key;
    }

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    protected Map<String, WebCrawlingConfigCQ> _myselfExistsMap;

    public Map<String, WebCrawlingConfigCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final WebCrawlingConfigCQ subQuery) {
        if (_myselfExistsMap == null) {
            _myselfExistsMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey" + (_myselfExistsMap.size() + 1);
        _myselfExistsMap.put(key, subQuery);
        return "myselfExists." + key;
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    protected Map<String, WebCrawlingConfigCQ> _myselfInScopeMap;

    public Map<String, WebCrawlingConfigCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final WebCrawlingConfigCQ subQuery) {
        if (_myselfInScopeMap == null) {
            _myselfInScopeMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey" + (_myselfInScopeMap.size() + 1);
        _myselfInScopeMap.put(key, subQuery);
        return "myselfInScope." + key;
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return WebCrawlingConfigCB.class.getName();
    }

    protected String xCQ() {
        return WebCrawlingConfigCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
