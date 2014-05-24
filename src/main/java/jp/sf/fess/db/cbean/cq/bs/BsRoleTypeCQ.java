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

import jp.sf.fess.db.cbean.RoleTypeCB;
import jp.sf.fess.db.cbean.cq.DataConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.LabelTypeToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.RoleTypeCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.ciq.RoleTypeCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of ROLE_TYPE.
 * @author DBFlute(AutoGenerator)
 */
public class BsRoleTypeCQ extends AbstractBsRoleTypeCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected RoleTypeCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsRoleTypeCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from ROLE_TYPE) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public RoleTypeCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected RoleTypeCIQ xcreateCIQ() {
        final RoleTypeCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected RoleTypeCIQ xnewCIQ() {
        return new RoleTypeCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join ROLE_TYPE on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public RoleTypeCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final RoleTypeCIQ inlineQuery = inline();
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

    protected Map<String, DataConfigToRoleTypeMappingCQ> _id_ExistsReferrer_DataConfigToRoleTypeMappingListMap;

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_ExistsReferrer_DataConfigToRoleTypeMappingList() {
        return _id_ExistsReferrer_DataConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_ExistsReferrer_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ subQuery) {
        if (_id_ExistsReferrer_DataConfigToRoleTypeMappingListMap == null) {
            _id_ExistsReferrer_DataConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_DataConfigToRoleTypeMappingListMap.size() + 1);
        _id_ExistsReferrer_DataConfigToRoleTypeMappingListMap
                .put(key, subQuery);
        return "id_ExistsReferrer_DataConfigToRoleTypeMappingList." + key;
    }

    protected Map<String, FileConfigToRoleTypeMappingCQ> _id_ExistsReferrer_FileConfigToRoleTypeMappingListMap;

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_ExistsReferrer_FileConfigToRoleTypeMappingList() {
        return _id_ExistsReferrer_FileConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_ExistsReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ subQuery) {
        if (_id_ExistsReferrer_FileConfigToRoleTypeMappingListMap == null) {
            _id_ExistsReferrer_FileConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_FileConfigToRoleTypeMappingListMap.size() + 1);
        _id_ExistsReferrer_FileConfigToRoleTypeMappingListMap
                .put(key, subQuery);
        return "id_ExistsReferrer_FileConfigToRoleTypeMappingList." + key;
    }

    protected Map<String, LabelTypeToRoleTypeMappingCQ> _id_ExistsReferrer_LabelTypeToRoleTypeMappingListMap;

    public Map<String, LabelTypeToRoleTypeMappingCQ> getId_ExistsReferrer_LabelTypeToRoleTypeMappingList() {
        return _id_ExistsReferrer_LabelTypeToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_ExistsReferrer_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ subQuery) {
        if (_id_ExistsReferrer_LabelTypeToRoleTypeMappingListMap == null) {
            _id_ExistsReferrer_LabelTypeToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_LabelTypeToRoleTypeMappingListMap.size() + 1);
        _id_ExistsReferrer_LabelTypeToRoleTypeMappingListMap.put(key, subQuery);
        return "id_ExistsReferrer_LabelTypeToRoleTypeMappingList." + key;
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

    protected Map<String, DataConfigToRoleTypeMappingCQ> _id_NotExistsReferrer_DataConfigToRoleTypeMappingListMap;

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_NotExistsReferrer_DataConfigToRoleTypeMappingList() {
        return _id_NotExistsReferrer_DataConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ subQuery) {
        if (_id_NotExistsReferrer_DataConfigToRoleTypeMappingListMap == null) {
            _id_NotExistsReferrer_DataConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_DataConfigToRoleTypeMappingListMap
                        .size() + 1);
        _id_NotExistsReferrer_DataConfigToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_NotExistsReferrer_DataConfigToRoleTypeMappingList." + key;
    }

    protected Map<String, FileConfigToRoleTypeMappingCQ> _id_NotExistsReferrer_FileConfigToRoleTypeMappingListMap;

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_NotExistsReferrer_FileConfigToRoleTypeMappingList() {
        return _id_NotExistsReferrer_FileConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ subQuery) {
        if (_id_NotExistsReferrer_FileConfigToRoleTypeMappingListMap == null) {
            _id_NotExistsReferrer_FileConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_FileConfigToRoleTypeMappingListMap
                        .size() + 1);
        _id_NotExistsReferrer_FileConfigToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_NotExistsReferrer_FileConfigToRoleTypeMappingList." + key;
    }

    protected Map<String, LabelTypeToRoleTypeMappingCQ> _id_NotExistsReferrer_LabelTypeToRoleTypeMappingListMap;

    public Map<String, LabelTypeToRoleTypeMappingCQ> getId_NotExistsReferrer_LabelTypeToRoleTypeMappingList() {
        return _id_NotExistsReferrer_LabelTypeToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ subQuery) {
        if (_id_NotExistsReferrer_LabelTypeToRoleTypeMappingListMap == null) {
            _id_NotExistsReferrer_LabelTypeToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_LabelTypeToRoleTypeMappingListMap
                        .size() + 1);
        _id_NotExistsReferrer_LabelTypeToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_NotExistsReferrer_LabelTypeToRoleTypeMappingList." + key;
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

    protected Map<String, DataConfigToRoleTypeMappingCQ> _id_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingListMap;

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingList() {
        return _id_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingListMap == null) {
            _id_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingListMap
                        .size() + 1);
        _id_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingList."
                + key;
    }

    protected Map<String, FileConfigToRoleTypeMappingCQ> _id_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingListMap;

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList() {
        return _id_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingListMap == null) {
            _id_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingListMap
                        .size() + 1);
        _id_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList."
                + key;
    }

    protected Map<String, LabelTypeToRoleTypeMappingCQ> _id_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingListMap;

    public Map<String, LabelTypeToRoleTypeMappingCQ> getId_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingList() {
        return _id_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingListMap == null) {
            _id_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingListMap
                        .size() + 1);
        _id_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingList."
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

    protected Map<String, DataConfigToRoleTypeMappingCQ> _id_InScopeRelation_DataConfigToRoleTypeMappingListMap;

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_InScopeRelation_DataConfigToRoleTypeMappingList() {
        return _id_InScopeRelation_DataConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_InScopeRelation_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ subQuery) {
        if (_id_InScopeRelation_DataConfigToRoleTypeMappingListMap == null) {
            _id_InScopeRelation_DataConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_DataConfigToRoleTypeMappingListMap
                        .size() + 1);
        _id_InScopeRelation_DataConfigToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_InScopeRelation_DataConfigToRoleTypeMappingList." + key;
    }

    protected Map<String, FileConfigToRoleTypeMappingCQ> _id_InScopeRelation_FileConfigToRoleTypeMappingListMap;

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_InScopeRelation_FileConfigToRoleTypeMappingList() {
        return _id_InScopeRelation_FileConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_InScopeRelation_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ subQuery) {
        if (_id_InScopeRelation_FileConfigToRoleTypeMappingListMap == null) {
            _id_InScopeRelation_FileConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_FileConfigToRoleTypeMappingListMap
                        .size() + 1);
        _id_InScopeRelation_FileConfigToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_InScopeRelation_FileConfigToRoleTypeMappingList." + key;
    }

    protected Map<String, LabelTypeToRoleTypeMappingCQ> _id_InScopeRelation_LabelTypeToRoleTypeMappingListMap;

    public Map<String, LabelTypeToRoleTypeMappingCQ> getId_InScopeRelation_LabelTypeToRoleTypeMappingList() {
        return _id_InScopeRelation_LabelTypeToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_InScopeRelation_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ subQuery) {
        if (_id_InScopeRelation_LabelTypeToRoleTypeMappingListMap == null) {
            _id_InScopeRelation_LabelTypeToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_LabelTypeToRoleTypeMappingListMap.size() + 1);
        _id_InScopeRelation_LabelTypeToRoleTypeMappingListMap
                .put(key, subQuery);
        return "id_InScopeRelation_LabelTypeToRoleTypeMappingList." + key;
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

    protected Map<String, DataConfigToRoleTypeMappingCQ> _id_NotInScopeRelation_DataConfigToRoleTypeMappingListMap;

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_NotInScopeRelation_DataConfigToRoleTypeMappingList() {
        return _id_NotInScopeRelation_DataConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ subQuery) {
        if (_id_NotInScopeRelation_DataConfigToRoleTypeMappingListMap == null) {
            _id_NotInScopeRelation_DataConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_DataConfigToRoleTypeMappingListMap
                        .size() + 1);
        _id_NotInScopeRelation_DataConfigToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_NotInScopeRelation_DataConfigToRoleTypeMappingList." + key;
    }

    protected Map<String, FileConfigToRoleTypeMappingCQ> _id_NotInScopeRelation_FileConfigToRoleTypeMappingListMap;

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_NotInScopeRelation_FileConfigToRoleTypeMappingList() {
        return _id_NotInScopeRelation_FileConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ subQuery) {
        if (_id_NotInScopeRelation_FileConfigToRoleTypeMappingListMap == null) {
            _id_NotInScopeRelation_FileConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_FileConfigToRoleTypeMappingListMap
                        .size() + 1);
        _id_NotInScopeRelation_FileConfigToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_NotInScopeRelation_FileConfigToRoleTypeMappingList." + key;
    }

    protected Map<String, LabelTypeToRoleTypeMappingCQ> _id_NotInScopeRelation_LabelTypeToRoleTypeMappingListMap;

    public Map<String, LabelTypeToRoleTypeMappingCQ> getId_NotInScopeRelation_LabelTypeToRoleTypeMappingList() {
        return _id_NotInScopeRelation_LabelTypeToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ subQuery) {
        if (_id_NotInScopeRelation_LabelTypeToRoleTypeMappingListMap == null) {
            _id_NotInScopeRelation_LabelTypeToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_LabelTypeToRoleTypeMappingListMap
                        .size() + 1);
        _id_NotInScopeRelation_LabelTypeToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_NotInScopeRelation_LabelTypeToRoleTypeMappingList." + key;
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

    protected Map<String, DataConfigToRoleTypeMappingCQ> _id_QueryDerivedReferrer_DataConfigToRoleTypeMappingListMap;

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_QueryDerivedReferrer_DataConfigToRoleTypeMappingList() {
        return _id_QueryDerivedReferrer_DataConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ subQuery) {
        if (_id_QueryDerivedReferrer_DataConfigToRoleTypeMappingListMap == null) {
            _id_QueryDerivedReferrer_DataConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_DataConfigToRoleTypeMappingListMap
                        .size() + 1);
        _id_QueryDerivedReferrer_DataConfigToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_QueryDerivedReferrer_DataConfigToRoleTypeMappingList." + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_DataConfigToRoleTypeMappingListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_DataConfigToRoleTypeMappingListParameter() {
        return _id_QueryDerivedReferrer_DataConfigToRoleTypeMappingListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_DataConfigToRoleTypeMappingListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_DataConfigToRoleTypeMappingListParameterMap == null) {
            _id_QueryDerivedReferrer_DataConfigToRoleTypeMappingListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_DataConfigToRoleTypeMappingListParameterMap
                        .size() + 1);
        _id_QueryDerivedReferrer_DataConfigToRoleTypeMappingListParameterMap
                .put(key, parameterValue);
        return "id_QueryDerivedReferrer_DataConfigToRoleTypeMappingListParameter."
                + key;
    }

    protected Map<String, FileConfigToRoleTypeMappingCQ> _id_QueryDerivedReferrer_FileConfigToRoleTypeMappingListMap;

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_QueryDerivedReferrer_FileConfigToRoleTypeMappingList() {
        return _id_QueryDerivedReferrer_FileConfigToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ subQuery) {
        if (_id_QueryDerivedReferrer_FileConfigToRoleTypeMappingListMap == null) {
            _id_QueryDerivedReferrer_FileConfigToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_FileConfigToRoleTypeMappingListMap
                        .size() + 1);
        _id_QueryDerivedReferrer_FileConfigToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_QueryDerivedReferrer_FileConfigToRoleTypeMappingList." + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameter() {
        return _id_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameterMap == null) {
            _id_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameterMap
                        .size() + 1);
        _id_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameterMap
                .put(key, parameterValue);
        return "id_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameter."
                + key;
    }

    protected Map<String, LabelTypeToRoleTypeMappingCQ> _id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListMap;

    public Map<String, LabelTypeToRoleTypeMappingCQ> getId_QueryDerivedReferrer_LabelTypeToRoleTypeMappingList() {
        return _id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ subQuery) {
        if (_id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListMap == null) {
            _id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListMap
                        .size() + 1);
        _id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListMap.put(key,
                subQuery);
        return "id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingList." + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListParameter() {
        return _id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListParameterMap == null) {
            _id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListParameterMap
                        .size() + 1);
        _id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListParameterMap
                .put(key, parameterValue);
        return "id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListParameter."
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
    public BsRoleTypeCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_Id_Desc() {
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
    public BsRoleTypeCQ addOrderBy_Name_Asc() {
        regOBA("NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NAME: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_Name_Desc() {
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
    public BsRoleTypeCQ addOrderBy_Value_Asc() {
        regOBA("VALUE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_Value_Desc() {
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
    public BsRoleTypeCQ addOrderBy_SortOrder_Asc() {
        regOBA("SORT_ORDER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_SortOrder_Desc() {
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
    public BsRoleTypeCQ addOrderBy_CreatedBy_Asc() {
        regOBA("CREATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_CreatedBy_Desc() {
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
    public BsRoleTypeCQ addOrderBy_CreatedTime_Asc() {
        regOBA("CREATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_CreatedTime_Desc() {
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
    public BsRoleTypeCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("UPDATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_UpdatedBy_Desc() {
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
    public BsRoleTypeCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("UPDATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_UpdatedTime_Desc() {
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
    public BsRoleTypeCQ addOrderBy_DeletedBy_Asc() {
        regOBA("DELETED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_DeletedBy_Desc() {
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
    public BsRoleTypeCQ addOrderBy_DeletedTime_Asc() {
        regOBA("DELETED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_DeletedTime_Desc() {
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
    public BsRoleTypeCQ addOrderBy_VersionNo_Asc() {
        regOBA("VERSION_NO");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_VersionNo_Desc() {
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
    public BsRoleTypeCQ addSpecifiedDerivedOrderBy_Asc(final String aliasName) {
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
    public BsRoleTypeCQ addSpecifiedDerivedOrderBy_Desc(final String aliasName) {
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
    protected Map<String, RoleTypeCQ> _scalarConditionMap;

    public Map<String, RoleTypeCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final RoleTypeCQ subQuery) {
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
    protected Map<String, RoleTypeCQ> _specifyMyselfDerivedMap;

    public Map<String, RoleTypeCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final RoleTypeCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, RoleTypeCQ> _queryMyselfDerivedMap;

    public Map<String, RoleTypeCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final RoleTypeCQ subQuery) {
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
    protected Map<String, RoleTypeCQ> _myselfExistsMap;

    public Map<String, RoleTypeCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final RoleTypeCQ subQuery) {
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
    protected Map<String, RoleTypeCQ> _myselfInScopeMap;

    public Map<String, RoleTypeCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final RoleTypeCQ subQuery) {
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
        return RoleTypeCB.class.getName();
    }

    protected String xCQ() {
        return RoleTypeCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
