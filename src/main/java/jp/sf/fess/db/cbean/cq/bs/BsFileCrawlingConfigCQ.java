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

import jp.sf.fess.db.cbean.FileCrawlingConfigCB;
import jp.sf.fess.db.cbean.cq.FileAuthenticationCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.ciq.FileCrawlingConfigCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of FILE_CRAWLING_CONFIG.
 * @author DBFlute(AutoGenerator)
 */
public class BsFileCrawlingConfigCQ extends AbstractBsFileCrawlingConfigCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected FileCrawlingConfigCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsFileCrawlingConfigCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from FILE_CRAWLING_CONFIG) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public FileCrawlingConfigCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected FileCrawlingConfigCIQ xcreateCIQ() {
        final FileCrawlingConfigCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected FileCrawlingConfigCIQ xnewCIQ() {
        return new FileCrawlingConfigCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join FILE_CRAWLING_CONFIG on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public FileCrawlingConfigCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final FileCrawlingConfigCIQ inlineQuery = inline();
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

    protected Map<String, FileAuthenticationCQ> _id_ExistsReferrer_FileAuthenticationListMap;

    public Map<String, FileAuthenticationCQ> getId_ExistsReferrer_FileAuthenticationList() {
        return _id_ExistsReferrer_FileAuthenticationListMap;
    }

    @Override
    public String keepId_ExistsReferrer_FileAuthenticationList(
            final FileAuthenticationCQ subQuery) {
        if (_id_ExistsReferrer_FileAuthenticationListMap == null) {
            _id_ExistsReferrer_FileAuthenticationListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_ExistsReferrer_FileAuthenticationListMap.size() + 1);
        _id_ExistsReferrer_FileAuthenticationListMap.put(key, subQuery);
        return "id_ExistsReferrer_FileAuthenticationList." + key;
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

    protected Map<String, FileAuthenticationCQ> _id_NotExistsReferrer_FileAuthenticationListMap;

    public Map<String, FileAuthenticationCQ> getId_NotExistsReferrer_FileAuthenticationList() {
        return _id_NotExistsReferrer_FileAuthenticationListMap;
    }

    @Override
    public String keepId_NotExistsReferrer_FileAuthenticationList(
            final FileAuthenticationCQ subQuery) {
        if (_id_NotExistsReferrer_FileAuthenticationListMap == null) {
            _id_NotExistsReferrer_FileAuthenticationListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotExistsReferrer_FileAuthenticationListMap.size() + 1);
        _id_NotExistsReferrer_FileAuthenticationListMap.put(key, subQuery);
        return "id_NotExistsReferrer_FileAuthenticationList." + key;
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

    protected Map<String, FileAuthenticationCQ> _id_SpecifyDerivedReferrer_FileAuthenticationListMap;

    public Map<String, FileAuthenticationCQ> getId_SpecifyDerivedReferrer_FileAuthenticationList() {
        return _id_SpecifyDerivedReferrer_FileAuthenticationListMap;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_FileAuthenticationList(
            final FileAuthenticationCQ subQuery) {
        if (_id_SpecifyDerivedReferrer_FileAuthenticationListMap == null) {
            _id_SpecifyDerivedReferrer_FileAuthenticationListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_SpecifyDerivedReferrer_FileAuthenticationListMap.size() + 1);
        _id_SpecifyDerivedReferrer_FileAuthenticationListMap.put(key, subQuery);
        return "id_SpecifyDerivedReferrer_FileAuthenticationList." + key;
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

    protected Map<String, FileAuthenticationCQ> _id_InScopeRelation_FileAuthenticationListMap;

    public Map<String, FileAuthenticationCQ> getId_InScopeRelation_FileAuthenticationList() {
        return _id_InScopeRelation_FileAuthenticationListMap;
    }

    @Override
    public String keepId_InScopeRelation_FileAuthenticationList(
            final FileAuthenticationCQ subQuery) {
        if (_id_InScopeRelation_FileAuthenticationListMap == null) {
            _id_InScopeRelation_FileAuthenticationListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_InScopeRelation_FileAuthenticationListMap.size() + 1);
        _id_InScopeRelation_FileAuthenticationListMap.put(key, subQuery);
        return "id_InScopeRelation_FileAuthenticationList." + key;
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

    protected Map<String, FileAuthenticationCQ> _id_NotInScopeRelation_FileAuthenticationListMap;

    public Map<String, FileAuthenticationCQ> getId_NotInScopeRelation_FileAuthenticationList() {
        return _id_NotInScopeRelation_FileAuthenticationListMap;
    }

    @Override
    public String keepId_NotInScopeRelation_FileAuthenticationList(
            final FileAuthenticationCQ subQuery) {
        if (_id_NotInScopeRelation_FileAuthenticationListMap == null) {
            _id_NotInScopeRelation_FileAuthenticationListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_NotInScopeRelation_FileAuthenticationListMap.size() + 1);
        _id_NotInScopeRelation_FileAuthenticationListMap.put(key, subQuery);
        return "id_NotInScopeRelation_FileAuthenticationList." + key;
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

    protected Map<String, FileAuthenticationCQ> _id_QueryDerivedReferrer_FileAuthenticationListMap;

    public Map<String, FileAuthenticationCQ> getId_QueryDerivedReferrer_FileAuthenticationList() {
        return _id_QueryDerivedReferrer_FileAuthenticationListMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileAuthenticationList(
            final FileAuthenticationCQ subQuery) {
        if (_id_QueryDerivedReferrer_FileAuthenticationListMap == null) {
            _id_QueryDerivedReferrer_FileAuthenticationListMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_id_QueryDerivedReferrer_FileAuthenticationListMap.size() + 1);
        _id_QueryDerivedReferrer_FileAuthenticationListMap.put(key, subQuery);
        return "id_QueryDerivedReferrer_FileAuthenticationList." + key;
    }

    protected Map<String, Object> _id_QueryDerivedReferrer_FileAuthenticationListParameterMap;

    public Map<String, Object> getId_QueryDerivedReferrer_FileAuthenticationListParameter() {
        return _id_QueryDerivedReferrer_FileAuthenticationListParameterMap;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileAuthenticationListParameter(
            final Object parameterValue) {
        if (_id_QueryDerivedReferrer_FileAuthenticationListParameterMap == null) {
            _id_QueryDerivedReferrer_FileAuthenticationListParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_id_QueryDerivedReferrer_FileAuthenticationListParameterMap
                        .size() + 1);
        _id_QueryDerivedReferrer_FileAuthenticationListParameterMap.put(key,
                parameterValue);
        return "id_QueryDerivedReferrer_FileAuthenticationListParameter." + key;
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

    /** 
     * Add order-by as ascend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Id_Desc() {
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
    public BsFileCrawlingConfigCQ addOrderBy_Name_Asc() {
        regOBA("NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Name_Desc() {
        regOBD("NAME");
        return this;
    }

    protected ConditionValue _paths;

    public ConditionValue getPaths() {
        if (_paths == null) {
            _paths = nCV();
        }
        return _paths;
    }

    @Override
    protected ConditionValue getCValuePaths() {
        return getPaths();
    }

    /** 
     * Add order-by as ascend. <br />
     * PATHS: {NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Paths_Asc() {
        regOBA("PATHS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * PATHS: {NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Paths_Desc() {
        regOBD("PATHS");
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
    public BsFileCrawlingConfigCQ addOrderBy_IncludedPaths_Asc() {
        regOBA("INCLUDED_PATHS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_IncludedPaths_Desc() {
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
    public BsFileCrawlingConfigCQ addOrderBy_ExcludedPaths_Asc() {
        regOBA("EXCLUDED_PATHS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_ExcludedPaths_Desc() {
        regOBD("EXCLUDED_PATHS");
        return this;
    }

    protected ConditionValue _includedDocPaths;

    public ConditionValue getIncludedDocPaths() {
        if (_includedDocPaths == null) {
            _includedDocPaths = nCV();
        }
        return _includedDocPaths;
    }

    @Override
    protected ConditionValue getCValueIncludedDocPaths() {
        return getIncludedDocPaths();
    }

    /** 
     * Add order-by as ascend. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_IncludedDocPaths_Asc() {
        regOBA("INCLUDED_DOC_PATHS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_IncludedDocPaths_Desc() {
        regOBD("INCLUDED_DOC_PATHS");
        return this;
    }

    protected ConditionValue _excludedDocPaths;

    public ConditionValue getExcludedDocPaths() {
        if (_excludedDocPaths == null) {
            _excludedDocPaths = nCV();
        }
        return _excludedDocPaths;
    }

    @Override
    protected ConditionValue getCValueExcludedDocPaths() {
        return getExcludedDocPaths();
    }

    /** 
     * Add order-by as ascend. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_ExcludedDocPaths_Asc() {
        regOBA("EXCLUDED_DOC_PATHS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_ExcludedDocPaths_Desc() {
        regOBD("EXCLUDED_DOC_PATHS");
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
    public BsFileCrawlingConfigCQ addOrderBy_ConfigParameter_Asc() {
        regOBA("CONFIG_PARAMETER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_ConfigParameter_Desc() {
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
    public BsFileCrawlingConfigCQ addOrderBy_Depth_Asc() {
        regOBA("DEPTH");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DEPTH: {INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Depth_Desc() {
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
    public BsFileCrawlingConfigCQ addOrderBy_MaxAccessCount_Asc() {
        regOBA("MAX_ACCESS_COUNT");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_MaxAccessCount_Desc() {
        regOBD("MAX_ACCESS_COUNT");
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
    public BsFileCrawlingConfigCQ addOrderBy_NumOfThread_Asc() {
        regOBA("NUM_OF_THREAD");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_NumOfThread_Desc() {
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
    public BsFileCrawlingConfigCQ addOrderBy_IntervalTime_Asc() {
        regOBA("INTERVAL_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_IntervalTime_Desc() {
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
    public BsFileCrawlingConfigCQ addOrderBy_Boost_Asc() {
        regOBA("BOOST");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Boost_Desc() {
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
    public BsFileCrawlingConfigCQ addOrderBy_Available_Asc() {
        regOBA("AVAILABLE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Available_Desc() {
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
    public BsFileCrawlingConfigCQ addOrderBy_SortOrder_Asc() {
        regOBA("SORT_ORDER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_SortOrder_Desc() {
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
    public BsFileCrawlingConfigCQ addOrderBy_CreatedBy_Asc() {
        regOBA("CREATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_CreatedBy_Desc() {
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
    public BsFileCrawlingConfigCQ addOrderBy_CreatedTime_Asc() {
        regOBA("CREATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_CreatedTime_Desc() {
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
    public BsFileCrawlingConfigCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("UPDATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_UpdatedBy_Desc() {
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
    public BsFileCrawlingConfigCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("UPDATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_UpdatedTime_Desc() {
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
    public BsFileCrawlingConfigCQ addOrderBy_DeletedBy_Asc() {
        regOBA("DELETED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_DeletedBy_Desc() {
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
    public BsFileCrawlingConfigCQ addOrderBy_DeletedTime_Asc() {
        regOBA("DELETED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_DeletedTime_Desc() {
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
    public BsFileCrawlingConfigCQ addOrderBy_VersionNo_Asc() {
        regOBA("VERSION_NO");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_VersionNo_Desc() {
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
    public BsFileCrawlingConfigCQ addSpecifiedDerivedOrderBy_Asc(
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
    public BsFileCrawlingConfigCQ addSpecifiedDerivedOrderBy_Desc(
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
    protected Map<String, FileCrawlingConfigCQ> _scalarConditionMap;

    public Map<String, FileCrawlingConfigCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final FileCrawlingConfigCQ subQuery) {
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
    protected Map<String, FileCrawlingConfigCQ> _specifyMyselfDerivedMap;

    public Map<String, FileCrawlingConfigCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final FileCrawlingConfigCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, FileCrawlingConfigCQ> _queryMyselfDerivedMap;

    public Map<String, FileCrawlingConfigCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final FileCrawlingConfigCQ subQuery) {
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
    protected Map<String, FileCrawlingConfigCQ> _myselfExistsMap;

    public Map<String, FileCrawlingConfigCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final FileCrawlingConfigCQ subQuery) {
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
    protected Map<String, FileCrawlingConfigCQ> _myselfInScopeMap;

    public Map<String, FileCrawlingConfigCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final FileCrawlingConfigCQ subQuery) {
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
        return FileCrawlingConfigCB.class.getName();
    }

    protected String xCQ() {
        return FileCrawlingConfigCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
