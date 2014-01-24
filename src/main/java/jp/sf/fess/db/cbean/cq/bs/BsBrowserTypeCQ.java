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

import jp.sf.fess.db.cbean.BrowserTypeCB;
import jp.sf.fess.db.cbean.cq.BrowserTypeCQ;
import jp.sf.fess.db.cbean.cq.DataConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.ciq.BrowserTypeCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of BROWSER_TYPE.
 * @author DBFlute(AutoGenerator)
 */
public class BsBrowserTypeCQ extends AbstractBsBrowserTypeCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BrowserTypeCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsBrowserTypeCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from BROWSER_TYPE) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public BrowserTypeCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected BrowserTypeCIQ xcreateCIQ() {
        final BrowserTypeCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected BrowserTypeCIQ xnewCIQ() {
        return new BrowserTypeCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join BROWSER_TYPE on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public BrowserTypeCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final BrowserTypeCIQ inlineQuery = inline();
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

    protected Map<String, DataConfigToBrowserTypeMappingCQ> _id_ExistsReferrer_DataConfigToBrowserTypeMappingListMap;

    public Map<String, DataConfigToBrowserTypeMappingCQ> getId_ExistsReferrer_DataConfigToBrowserTypeMappingList() {
        return _id_ExistsReferrer_DataConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_ExistsReferrer_DataConfigToBrowserTypeMappingList(
            final DataConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_ExistsReferrer_DataConfigToBrowserTypeMappingListMap == null) {
            _id_ExistsReferrer_DataConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_DataConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_ExistsReferrer_DataConfigToBrowserTypeMappingListMap.put(key,
                subQuery);
        return "id_ExistsReferrer_DataConfigToBrowserTypeMappingList." + key;
    }

    protected Map<String, FileConfigToBrowserTypeMappingCQ> _id_ExistsReferrer_FileConfigToBrowserTypeMappingListMap;

    public Map<String, FileConfigToBrowserTypeMappingCQ> getId_ExistsReferrer_FileConfigToBrowserTypeMappingList() {
        return _id_ExistsReferrer_FileConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_ExistsReferrer_FileConfigToBrowserTypeMappingList(
            final FileConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_ExistsReferrer_FileConfigToBrowserTypeMappingListMap == null) {
            _id_ExistsReferrer_FileConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_FileConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_ExistsReferrer_FileConfigToBrowserTypeMappingListMap.put(key,
                subQuery);
        return "id_ExistsReferrer_FileConfigToBrowserTypeMappingList." + key;
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

    protected Map<String, DataConfigToBrowserTypeMappingCQ> _id_NotExistsReferrer_DataConfigToBrowserTypeMappingListMap;

    public Map<String, DataConfigToBrowserTypeMappingCQ> getId_NotExistsReferrer_DataConfigToBrowserTypeMappingList() {
        return _id_NotExistsReferrer_DataConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_DataConfigToBrowserTypeMappingList(
            final DataConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_NotExistsReferrer_DataConfigToBrowserTypeMappingListMap == null) {
            _id_NotExistsReferrer_DataConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_DataConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_NotExistsReferrer_DataConfigToBrowserTypeMappingListMap.put(key,
                subQuery);
        return "id_NotExistsReferrer_DataConfigToBrowserTypeMappingList." + key;
    }

    protected Map<String, FileConfigToBrowserTypeMappingCQ> _id_NotExistsReferrer_FileConfigToBrowserTypeMappingListMap;

    public Map<String, FileConfigToBrowserTypeMappingCQ> getId_NotExistsReferrer_FileConfigToBrowserTypeMappingList() {
        return _id_NotExistsReferrer_FileConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_FileConfigToBrowserTypeMappingList(
            final FileConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_NotExistsReferrer_FileConfigToBrowserTypeMappingListMap == null) {
            _id_NotExistsReferrer_FileConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_FileConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_NotExistsReferrer_FileConfigToBrowserTypeMappingListMap.put(key,
                subQuery);
        return "id_NotExistsReferrer_FileConfigToBrowserTypeMappingList." + key;
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

    protected Map<String, DataConfigToBrowserTypeMappingCQ> _id_SpecifyDerivedReferrer_DataConfigToBrowserTypeMappingListMap;

    public Map<String, DataConfigToBrowserTypeMappingCQ> getId_SpecifyDerivedReferrer_DataConfigToBrowserTypeMappingList() {
        return _id_SpecifyDerivedReferrer_DataConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_DataConfigToBrowserTypeMappingList(
            final DataConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_DataConfigToBrowserTypeMappingListMap == null) {
            _id_SpecifyDerivedReferrer_DataConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_DataConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_SpecifyDerivedReferrer_DataConfigToBrowserTypeMappingListMap.put(
                key, subQuery);
        return "id_SpecifyDerivedReferrer_DataConfigToBrowserTypeMappingList."
                + key;
    }

    protected Map<String, FileConfigToBrowserTypeMappingCQ> _id_SpecifyDerivedReferrer_FileConfigToBrowserTypeMappingListMap;

    public Map<String, FileConfigToBrowserTypeMappingCQ> getId_SpecifyDerivedReferrer_FileConfigToBrowserTypeMappingList() {
        return _id_SpecifyDerivedReferrer_FileConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_FileConfigToBrowserTypeMappingList(
            final FileConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_FileConfigToBrowserTypeMappingListMap == null) {
            _id_SpecifyDerivedReferrer_FileConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_FileConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_SpecifyDerivedReferrer_FileConfigToBrowserTypeMappingListMap.put(
                key, subQuery);
        return "id_SpecifyDerivedReferrer_FileConfigToBrowserTypeMappingList."
                + key;
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

    protected Map<String, DataConfigToBrowserTypeMappingCQ> _id_InScopeRelation_DataConfigToBrowserTypeMappingListMap;

    public Map<String, DataConfigToBrowserTypeMappingCQ> getId_InScopeRelation_DataConfigToBrowserTypeMappingList() {
        return _id_InScopeRelation_DataConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_InScopeRelation_DataConfigToBrowserTypeMappingList(
            final DataConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_InScopeRelation_DataConfigToBrowserTypeMappingListMap == null) {
            _id_InScopeRelation_DataConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_DataConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_InScopeRelation_DataConfigToBrowserTypeMappingListMap.put(key,
                subQuery);
        return "id_InScopeRelation_DataConfigToBrowserTypeMappingList." + key;
    }

    protected Map<String, FileConfigToBrowserTypeMappingCQ> _id_InScopeRelation_FileConfigToBrowserTypeMappingListMap;

    public Map<String, FileConfigToBrowserTypeMappingCQ> getId_InScopeRelation_FileConfigToBrowserTypeMappingList() {
        return _id_InScopeRelation_FileConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_InScopeRelation_FileConfigToBrowserTypeMappingList(
            final FileConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_InScopeRelation_FileConfigToBrowserTypeMappingListMap == null) {
            _id_InScopeRelation_FileConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_FileConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_InScopeRelation_FileConfigToBrowserTypeMappingListMap.put(key,
                subQuery);
        return "id_InScopeRelation_FileConfigToBrowserTypeMappingList." + key;
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

    protected Map<String, DataConfigToBrowserTypeMappingCQ> _id_NotInScopeRelation_DataConfigToBrowserTypeMappingListMap;

    public Map<String, DataConfigToBrowserTypeMappingCQ> getId_NotInScopeRelation_DataConfigToBrowserTypeMappingList() {
        return _id_NotInScopeRelation_DataConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_DataConfigToBrowserTypeMappingList(
            final DataConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_NotInScopeRelation_DataConfigToBrowserTypeMappingListMap == null) {
            _id_NotInScopeRelation_DataConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_DataConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_NotInScopeRelation_DataConfigToBrowserTypeMappingListMap.put(key,
                subQuery);
        return "id_NotInScopeRelation_DataConfigToBrowserTypeMappingList."
                + key;
    }

    protected Map<String, FileConfigToBrowserTypeMappingCQ> _id_NotInScopeRelation_FileConfigToBrowserTypeMappingListMap;

    public Map<String, FileConfigToBrowserTypeMappingCQ> getId_NotInScopeRelation_FileConfigToBrowserTypeMappingList() {
        return _id_NotInScopeRelation_FileConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_FileConfigToBrowserTypeMappingList(
            final FileConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_NotInScopeRelation_FileConfigToBrowserTypeMappingListMap == null) {
            _id_NotInScopeRelation_FileConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_FileConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_NotInScopeRelation_FileConfigToBrowserTypeMappingListMap.put(key,
                subQuery);
        return "id_NotInScopeRelation_FileConfigToBrowserTypeMappingList."
                + key;
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

    protected Map<String, DataConfigToBrowserTypeMappingCQ> _id_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListMap;

    public Map<String, DataConfigToBrowserTypeMappingCQ> getId_QueryDerivedReferrer_DataConfigToBrowserTypeMappingList() {
        return _id_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_DataConfigToBrowserTypeMappingList(
            final DataConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListMap == null) {
            _id_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListMap.put(key,
                subQuery);
        return "id_QueryDerivedReferrer_DataConfigToBrowserTypeMappingList."
                + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListParameter() {
        return _id_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListParameterMap == null) {
            _id_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListParameterMap
                        .size() + 1);
        _id_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListParameterMap
                .put(key, parameterValue);
        return "id_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListParameter."
                + key;
    }

    protected Map<String, FileConfigToBrowserTypeMappingCQ> _id_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListMap;

    public Map<String, FileConfigToBrowserTypeMappingCQ> getId_QueryDerivedReferrer_FileConfigToBrowserTypeMappingList() {
        return _id_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToBrowserTypeMappingList(
            final FileConfigToBrowserTypeMappingCQ subQuery) {
        if (_id_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListMap == null) {
            _id_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListMap
                        .size() + 1);
        _id_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListMap.put(key,
                subQuery);
        return "id_QueryDerivedReferrer_FileConfigToBrowserTypeMappingList."
                + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListParameter() {
        return _id_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListParameterMap == null) {
            _id_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListParameterMap
                        .size() + 1);
        _id_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListParameterMap
                .put(key, parameterValue);
        return "id_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListParameter."
                + key;
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

    /** 
     * Add order-by as ascend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsBrowserTypeCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsBrowserTypeCQ addOrderBy_Id_Desc() {
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
     * NAME: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsBrowserTypeCQ addOrderBy_Name_Asc() {
        regOBA("NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NAME: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsBrowserTypeCQ addOrderBy_Name_Desc() {
        regOBD("NAME");
        return this;
    }

    protected ConditionValue _value;

    public ConditionValue getValue() {
        if (_value == null) {
            _value = nCV();
        }
        return _value;
    }

    @Override
    protected ConditionValue getCValueValue() {
        return getValue();
    }

    /** 
     * Add order-by as ascend. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsBrowserTypeCQ addOrderBy_Value_Asc() {
        regOBA("VALUE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsBrowserTypeCQ addOrderBy_Value_Desc() {
        regOBD("VALUE");
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
    public BsBrowserTypeCQ addOrderBy_SortOrder_Asc() {
        regOBA("SORT_ORDER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsBrowserTypeCQ addOrderBy_SortOrder_Desc() {
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
    public BsBrowserTypeCQ addOrderBy_CreatedBy_Asc() {
        regOBA("CREATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsBrowserTypeCQ addOrderBy_CreatedBy_Desc() {
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
    public BsBrowserTypeCQ addOrderBy_CreatedTime_Asc() {
        regOBA("CREATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsBrowserTypeCQ addOrderBy_CreatedTime_Desc() {
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
    public BsBrowserTypeCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("UPDATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsBrowserTypeCQ addOrderBy_UpdatedBy_Desc() {
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
    public BsBrowserTypeCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("UPDATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsBrowserTypeCQ addOrderBy_UpdatedTime_Desc() {
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
    public BsBrowserTypeCQ addOrderBy_DeletedBy_Asc() {
        regOBA("DELETED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsBrowserTypeCQ addOrderBy_DeletedBy_Desc() {
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
    public BsBrowserTypeCQ addOrderBy_DeletedTime_Asc() {
        regOBA("DELETED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsBrowserTypeCQ addOrderBy_DeletedTime_Desc() {
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
    public BsBrowserTypeCQ addOrderBy_VersionNo_Asc() {
        regOBA("VERSION_NO");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsBrowserTypeCQ addOrderBy_VersionNo_Desc() {
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
    public BsBrowserTypeCQ addSpecifiedDerivedOrderBy_Asc(final String aliasName) {
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
    public BsBrowserTypeCQ addSpecifiedDerivedOrderBy_Desc(
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
    protected Map<String, BrowserTypeCQ> _scalarConditionMap;

    public Map<String, BrowserTypeCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final BrowserTypeCQ subQuery) {
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
    protected Map<String, BrowserTypeCQ> _specifyMyselfDerivedMap;

    public Map<String, BrowserTypeCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final BrowserTypeCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, BrowserTypeCQ> _queryMyselfDerivedMap;

    public Map<String, BrowserTypeCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final BrowserTypeCQ subQuery) {
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
    protected Map<String, BrowserTypeCQ> _myselfExistsMap;

    public Map<String, BrowserTypeCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final BrowserTypeCQ subQuery) {
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
    protected Map<String, BrowserTypeCQ> _myselfInScopeMap;

    public Map<String, BrowserTypeCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final BrowserTypeCQ subQuery) {
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
        return BrowserTypeCB.class.getName();
    }

    protected String xCQ() {
        return BrowserTypeCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
