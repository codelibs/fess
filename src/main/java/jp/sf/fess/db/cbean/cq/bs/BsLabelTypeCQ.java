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

import jp.sf.fess.db.cbean.LabelTypeCB;
import jp.sf.fess.db.cbean.cq.DataConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.LabelTypeCQ;
import jp.sf.fess.db.cbean.cq.LabelTypeToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.ciq.LabelTypeCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of LABEL_TYPE.
 * @author DBFlute(AutoGenerator)
 */
public class BsLabelTypeCQ extends AbstractBsLabelTypeCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected LabelTypeCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsLabelTypeCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from LABEL_TYPE) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public LabelTypeCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected LabelTypeCIQ xcreateCIQ() {
        final LabelTypeCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected LabelTypeCIQ xnewCIQ() {
        return new LabelTypeCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join LABEL_TYPE on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public LabelTypeCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final LabelTypeCIQ inlineQuery = inline();
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

    protected Map<String, DataConfigToLabelTypeMappingCQ> _id_ExistsReferrer_DataConfigToLabelTypeMappingListMap;

    public Map<String, DataConfigToLabelTypeMappingCQ> getId_ExistsReferrer_DataConfigToLabelTypeMappingList() {
        return _id_ExistsReferrer_DataConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_ExistsReferrer_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ subQuery) {
        if (_id_ExistsReferrer_DataConfigToLabelTypeMappingListMap == null) {
            _id_ExistsReferrer_DataConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_DataConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_ExistsReferrer_DataConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_ExistsReferrer_DataConfigToLabelTypeMappingList." + key;
    }

    protected Map<String, FileConfigToLabelTypeMappingCQ> _id_ExistsReferrer_FileConfigToLabelTypeMappingListMap;

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_ExistsReferrer_FileConfigToLabelTypeMappingList() {
        return _id_ExistsReferrer_FileConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_ExistsReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ subQuery) {
        if (_id_ExistsReferrer_FileConfigToLabelTypeMappingListMap == null) {
            _id_ExistsReferrer_FileConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_FileConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_ExistsReferrer_FileConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_ExistsReferrer_FileConfigToLabelTypeMappingList." + key;
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

    protected Map<String, DataConfigToLabelTypeMappingCQ> _id_NotExistsReferrer_DataConfigToLabelTypeMappingListMap;

    public Map<String, DataConfigToLabelTypeMappingCQ> getId_NotExistsReferrer_DataConfigToLabelTypeMappingList() {
        return _id_NotExistsReferrer_DataConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ subQuery) {
        if (_id_NotExistsReferrer_DataConfigToLabelTypeMappingListMap == null) {
            _id_NotExistsReferrer_DataConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_DataConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_NotExistsReferrer_DataConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_NotExistsReferrer_DataConfigToLabelTypeMappingList." + key;
    }

    protected Map<String, FileConfigToLabelTypeMappingCQ> _id_NotExistsReferrer_FileConfigToLabelTypeMappingListMap;

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_NotExistsReferrer_FileConfigToLabelTypeMappingList() {
        return _id_NotExistsReferrer_FileConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ subQuery) {
        if (_id_NotExistsReferrer_FileConfigToLabelTypeMappingListMap == null) {
            _id_NotExistsReferrer_FileConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_FileConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_NotExistsReferrer_FileConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_NotExistsReferrer_FileConfigToLabelTypeMappingList." + key;
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

    protected Map<String, DataConfigToLabelTypeMappingCQ> _id_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingListMap;

    public Map<String, DataConfigToLabelTypeMappingCQ> getId_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingList() {
        return _id_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingListMap == null) {
            _id_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingList."
                + key;
    }

    protected Map<String, FileConfigToLabelTypeMappingCQ> _id_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingListMap;

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingList() {
        return _id_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingListMap == null) {
            _id_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingList."
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

    protected Map<String, DataConfigToLabelTypeMappingCQ> _id_InScopeRelation_DataConfigToLabelTypeMappingListMap;

    public Map<String, DataConfigToLabelTypeMappingCQ> getId_InScopeRelation_DataConfigToLabelTypeMappingList() {
        return _id_InScopeRelation_DataConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_InScopeRelation_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ subQuery) {
        if (_id_InScopeRelation_DataConfigToLabelTypeMappingListMap == null) {
            _id_InScopeRelation_DataConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_DataConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_InScopeRelation_DataConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_InScopeRelation_DataConfigToLabelTypeMappingList." + key;
    }

    protected Map<String, FileConfigToLabelTypeMappingCQ> _id_InScopeRelation_FileConfigToLabelTypeMappingListMap;

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_InScopeRelation_FileConfigToLabelTypeMappingList() {
        return _id_InScopeRelation_FileConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_InScopeRelation_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ subQuery) {
        if (_id_InScopeRelation_FileConfigToLabelTypeMappingListMap == null) {
            _id_InScopeRelation_FileConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_FileConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_InScopeRelation_FileConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_InScopeRelation_FileConfigToLabelTypeMappingList." + key;
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

    protected Map<String, DataConfigToLabelTypeMappingCQ> _id_NotInScopeRelation_DataConfigToLabelTypeMappingListMap;

    public Map<String, DataConfigToLabelTypeMappingCQ> getId_NotInScopeRelation_DataConfigToLabelTypeMappingList() {
        return _id_NotInScopeRelation_DataConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ subQuery) {
        if (_id_NotInScopeRelation_DataConfigToLabelTypeMappingListMap == null) {
            _id_NotInScopeRelation_DataConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_DataConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_NotInScopeRelation_DataConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_NotInScopeRelation_DataConfigToLabelTypeMappingList." + key;
    }

    protected Map<String, FileConfigToLabelTypeMappingCQ> _id_NotInScopeRelation_FileConfigToLabelTypeMappingListMap;

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_NotInScopeRelation_FileConfigToLabelTypeMappingList() {
        return _id_NotInScopeRelation_FileConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ subQuery) {
        if (_id_NotInScopeRelation_FileConfigToLabelTypeMappingListMap == null) {
            _id_NotInScopeRelation_FileConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_FileConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_NotInScopeRelation_FileConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_NotInScopeRelation_FileConfigToLabelTypeMappingList." + key;
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

    protected Map<String, DataConfigToLabelTypeMappingCQ> _id_QueryDerivedReferrer_DataConfigToLabelTypeMappingListMap;

    public Map<String, DataConfigToLabelTypeMappingCQ> getId_QueryDerivedReferrer_DataConfigToLabelTypeMappingList() {
        return _id_QueryDerivedReferrer_DataConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ subQuery) {
        if (_id_QueryDerivedReferrer_DataConfigToLabelTypeMappingListMap == null) {
            _id_QueryDerivedReferrer_DataConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_DataConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_QueryDerivedReferrer_DataConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_QueryDerivedReferrer_DataConfigToLabelTypeMappingList."
                + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_DataConfigToLabelTypeMappingListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_DataConfigToLabelTypeMappingListParameter() {
        return _id_QueryDerivedReferrer_DataConfigToLabelTypeMappingListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_DataConfigToLabelTypeMappingListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_DataConfigToLabelTypeMappingListParameterMap == null) {
            _id_QueryDerivedReferrer_DataConfigToLabelTypeMappingListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_DataConfigToLabelTypeMappingListParameterMap
                        .size() + 1);
        _id_QueryDerivedReferrer_DataConfigToLabelTypeMappingListParameterMap
                .put(key, parameterValue);
        return "id_QueryDerivedReferrer_DataConfigToLabelTypeMappingListParameter."
                + key;
    }

    protected Map<String, FileConfigToLabelTypeMappingCQ> _id_QueryDerivedReferrer_FileConfigToLabelTypeMappingListMap;

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_QueryDerivedReferrer_FileConfigToLabelTypeMappingList() {
        return _id_QueryDerivedReferrer_FileConfigToLabelTypeMappingListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ subQuery) {
        if (_id_QueryDerivedReferrer_FileConfigToLabelTypeMappingListMap == null) {
            _id_QueryDerivedReferrer_FileConfigToLabelTypeMappingListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_FileConfigToLabelTypeMappingListMap
                        .size() + 1);
        _id_QueryDerivedReferrer_FileConfigToLabelTypeMappingListMap.put(key,
                subQuery);
        return "id_QueryDerivedReferrer_FileConfigToLabelTypeMappingList."
                + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameter() {
        return _id_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameterMap == null) {
            _id_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameterMap
                        .size() + 1);
        _id_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameterMap
                .put(key, parameterValue);
        return "id_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameter."
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

    /**
     * Add order-by as ascend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_Id_Desc() {
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
    public BsLabelTypeCQ addOrderBy_Name_Asc() {
        regOBA("NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NAME: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_Name_Desc() {
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
    public BsLabelTypeCQ addOrderBy_Value_Asc() {
        regOBA("VALUE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_Value_Desc() {
        regOBD("VALUE");
        return this;
    }

    protected ConditionValue _includedPaths;

    public ConditionValue getIncludedPaths() {
        if (_includedPaths == null) {
            _includedPaths = nCV();
        }
        return _includedPaths;
    }

    @Override
    protected ConditionValue getCValueIncludedPaths() {
        return getIncludedPaths();
    }

    /**
     * Add order-by as ascend. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_IncludedPaths_Asc() {
        regOBA("INCLUDED_PATHS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_IncludedPaths_Desc() {
        regOBD("INCLUDED_PATHS");
        return this;
    }

    protected ConditionValue _excludedPaths;

    public ConditionValue getExcludedPaths() {
        if (_excludedPaths == null) {
            _excludedPaths = nCV();
        }
        return _excludedPaths;
    }

    @Override
    protected ConditionValue getCValueExcludedPaths() {
        return getExcludedPaths();
    }

    /**
     * Add order-by as ascend. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_ExcludedPaths_Asc() {
        regOBA("EXCLUDED_PATHS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_ExcludedPaths_Desc() {
        regOBD("EXCLUDED_PATHS");
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
    public BsLabelTypeCQ addOrderBy_SortOrder_Asc() {
        regOBA("SORT_ORDER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_SortOrder_Desc() {
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
    public BsLabelTypeCQ addOrderBy_CreatedBy_Asc() {
        regOBA("CREATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_CreatedBy_Desc() {
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
    public BsLabelTypeCQ addOrderBy_CreatedTime_Asc() {
        regOBA("CREATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_CreatedTime_Desc() {
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
    public BsLabelTypeCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("UPDATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_UpdatedBy_Desc() {
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
    public BsLabelTypeCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("UPDATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_UpdatedTime_Desc() {
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
    public BsLabelTypeCQ addOrderBy_DeletedBy_Asc() {
        regOBA("DELETED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_DeletedBy_Desc() {
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
    public BsLabelTypeCQ addOrderBy_DeletedTime_Asc() {
        regOBA("DELETED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_DeletedTime_Desc() {
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
    public BsLabelTypeCQ addOrderBy_VersionNo_Asc() {
        regOBA("VERSION_NO");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_VersionNo_Desc() {
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
    public BsLabelTypeCQ addSpecifiedDerivedOrderBy_Asc(final String aliasName) {
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
    public BsLabelTypeCQ addSpecifiedDerivedOrderBy_Desc(final String aliasName) {
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
    protected Map<String, LabelTypeCQ> _scalarConditionMap;

    public Map<String, LabelTypeCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final LabelTypeCQ subQuery) {
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
    protected Map<String, LabelTypeCQ> _specifyMyselfDerivedMap;

    public Map<String, LabelTypeCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final LabelTypeCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, LabelTypeCQ> _queryMyselfDerivedMap;

    public Map<String, LabelTypeCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final LabelTypeCQ subQuery) {
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
    protected Map<String, LabelTypeCQ> _myselfExistsMap;

    public Map<String, LabelTypeCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final LabelTypeCQ subQuery) {
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
    protected Map<String, LabelTypeCQ> _myselfInScopeMap;

    public Map<String, LabelTypeCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final LabelTypeCQ subQuery) {
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
        return LabelTypeCB.class.getName();
    }

    protected String xCQ() {
        return LabelTypeCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
