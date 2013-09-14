/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

import jp.sf.fess.db.cbean.FileConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.cq.FileConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.LabelTypeCQ;
import jp.sf.fess.db.cbean.cq.ciq.FileConfigToLabelTypeMappingCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of FILE_CONFIG_TO_LABEL_TYPE_MAPPING.
 * @author DBFlute(AutoGenerator)
 */
public class BsFileConfigToLabelTypeMappingCQ extends
        AbstractBsFileConfigToLabelTypeMappingCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected FileConfigToLabelTypeMappingCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsFileConfigToLabelTypeMappingCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from FILE_CONFIG_TO_LABEL_TYPE_MAPPING) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public FileConfigToLabelTypeMappingCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected FileConfigToLabelTypeMappingCIQ xcreateCIQ() {
        final FileConfigToLabelTypeMappingCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected FileConfigToLabelTypeMappingCIQ xnewCIQ() {
        return new FileConfigToLabelTypeMappingCIQ(xgetReferrerQuery(),
                xgetSqlClause(), xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join FILE_CONFIG_TO_LABEL_TYPE_MAPPING on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public FileConfigToLabelTypeMappingCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final FileConfigToLabelTypeMappingCIQ inlineQuery = inline();
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

    /** 
     * Add order-by as ascend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsFileConfigToLabelTypeMappingCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsFileConfigToLabelTypeMappingCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _fileConfigId;

    public ConditionValue getFileConfigId() {
        if (_fileConfigId == null) {
            _fileConfigId = nCV();
        }
        return _fileConfigId;
    }

    @Override
    protected ConditionValue getCValueFileConfigId() {
        return getFileConfigId();
    }

    protected Map<String, FileCrawlingConfigCQ> _fileConfigId_InScopeRelation_FileCrawlingConfigMap;

    public Map<String, FileCrawlingConfigCQ> getFileConfigId_InScopeRelation_FileCrawlingConfig() {
        return _fileConfigId_InScopeRelation_FileCrawlingConfigMap;
    }

    @Override
    public String keepFileConfigId_InScopeRelation_FileCrawlingConfig(
            final FileCrawlingConfigCQ subQuery) {
        if (_fileConfigId_InScopeRelation_FileCrawlingConfigMap == null) {
            _fileConfigId_InScopeRelation_FileCrawlingConfigMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_fileConfigId_InScopeRelation_FileCrawlingConfigMap.size() + 1);
        _fileConfigId_InScopeRelation_FileCrawlingConfigMap.put(key, subQuery);
        return "fileConfigId_InScopeRelation_FileCrawlingConfig." + key;
    }

    protected Map<String, FileCrawlingConfigCQ> _fileConfigId_NotInScopeRelation_FileCrawlingConfigMap;

    public Map<String, FileCrawlingConfigCQ> getFileConfigId_NotInScopeRelation_FileCrawlingConfig() {
        return _fileConfigId_NotInScopeRelation_FileCrawlingConfigMap;
    }

    @Override
    public String keepFileConfigId_NotInScopeRelation_FileCrawlingConfig(
            final FileCrawlingConfigCQ subQuery) {
        if (_fileConfigId_NotInScopeRelation_FileCrawlingConfigMap == null) {
            _fileConfigId_NotInScopeRelation_FileCrawlingConfigMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_fileConfigId_NotInScopeRelation_FileCrawlingConfigMap
                        .size() + 1);
        _fileConfigId_NotInScopeRelation_FileCrawlingConfigMap.put(key,
                subQuery);
        return "fileConfigId_NotInScopeRelation_FileCrawlingConfig." + key;
    }

    /** 
     * Add order-by as ascend. <br />
     * FILE_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsFileConfigToLabelTypeMappingCQ addOrderBy_FileConfigId_Asc() {
        regOBA("FILE_CONFIG_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * FILE_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsFileConfigToLabelTypeMappingCQ addOrderBy_FileConfigId_Desc() {
        regOBD("FILE_CONFIG_ID");
        return this;
    }

    protected ConditionValue _labelTypeId;

    public ConditionValue getLabelTypeId() {
        if (_labelTypeId == null) {
            _labelTypeId = nCV();
        }
        return _labelTypeId;
    }

    @Override
    protected ConditionValue getCValueLabelTypeId() {
        return getLabelTypeId();
    }

    protected Map<String, LabelTypeCQ> _labelTypeId_InScopeRelation_LabelTypeMap;

    public Map<String, LabelTypeCQ> getLabelTypeId_InScopeRelation_LabelType() {
        return _labelTypeId_InScopeRelation_LabelTypeMap;
    }

    @Override
    public String keepLabelTypeId_InScopeRelation_LabelType(
            final LabelTypeCQ subQuery) {
        if (_labelTypeId_InScopeRelation_LabelTypeMap == null) {
            _labelTypeId_InScopeRelation_LabelTypeMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_labelTypeId_InScopeRelation_LabelTypeMap.size() + 1);
        _labelTypeId_InScopeRelation_LabelTypeMap.put(key, subQuery);
        return "labelTypeId_InScopeRelation_LabelType." + key;
    }

    protected Map<String, LabelTypeCQ> _labelTypeId_NotInScopeRelation_LabelTypeMap;

    public Map<String, LabelTypeCQ> getLabelTypeId_NotInScopeRelation_LabelType() {
        return _labelTypeId_NotInScopeRelation_LabelTypeMap;
    }

    @Override
    public String keepLabelTypeId_NotInScopeRelation_LabelType(
            final LabelTypeCQ subQuery) {
        if (_labelTypeId_NotInScopeRelation_LabelTypeMap == null) {
            _labelTypeId_NotInScopeRelation_LabelTypeMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_labelTypeId_NotInScopeRelation_LabelTypeMap.size() + 1);
        _labelTypeId_NotInScopeRelation_LabelTypeMap.put(key, subQuery);
        return "labelTypeId_NotInScopeRelation_LabelType." + key;
    }

    /** 
     * Add order-by as ascend. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @return this. (NotNull)
     */
    public BsFileConfigToLabelTypeMappingCQ addOrderBy_LabelTypeId_Asc() {
        regOBA("LABEL_TYPE_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @return this. (NotNull)
     */
    public BsFileConfigToLabelTypeMappingCQ addOrderBy_LabelTypeId_Desc() {
        regOBD("LABEL_TYPE_ID");
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
    public BsFileConfigToLabelTypeMappingCQ addSpecifiedDerivedOrderBy_Asc(
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
    public BsFileConfigToLabelTypeMappingCQ addSpecifiedDerivedOrderBy_Desc(
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
        final FileConfigToLabelTypeMappingCQ baseQuery = (FileConfigToLabelTypeMappingCQ) baseQueryAsSuper;
        final FileConfigToLabelTypeMappingCQ unionQuery = (FileConfigToLabelTypeMappingCQ) unionQueryAsSuper;
        if (baseQuery.hasConditionQueryLabelType()) {
            unionQuery.queryLabelType().reflectRelationOnUnionQuery(
                    baseQuery.queryLabelType(), unionQuery.queryLabelType());
        }
        if (baseQuery.hasConditionQueryFileCrawlingConfig()) {
            unionQuery.queryFileCrawlingConfig().reflectRelationOnUnionQuery(
                    baseQuery.queryFileCrawlingConfig(),
                    unionQuery.queryFileCrawlingConfig());
        }
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    /**
     * Get the condition-query for relation table. <br />
     * LABEL_TYPE by my LABEL_TYPE_ID, named 'labelType'.
     * @return The instance of condition-query. (NotNull)
     */
    public LabelTypeCQ queryLabelType() {
        return getConditionQueryLabelType();
    }

    protected LabelTypeCQ _conditionQueryLabelType;

    public LabelTypeCQ getConditionQueryLabelType() {
        if (_conditionQueryLabelType == null) {
            _conditionQueryLabelType = xcreateQueryLabelType();
            xsetupOuterJoinLabelType();
        }
        return _conditionQueryLabelType;
    }

    protected LabelTypeCQ xcreateQueryLabelType() {
        final String nrp = resolveNextRelationPath(
                "FILE_CONFIG_TO_LABEL_TYPE_MAPPING", "labelType");
        final String jan = resolveJoinAliasName(nrp, xgetNextNestLevel());
        final LabelTypeCQ cq = new LabelTypeCQ(this, xgetSqlClause(), jan,
                xgetNextNestLevel());
        cq.xsetBaseCB(_baseCB);
        cq.xsetForeignPropertyName("labelType");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinLabelType() {
        final LabelTypeCQ cq = getConditionQueryLabelType();
        final Map<String, String> joinOnMap = newLinkedHashMapSized(4);
        joinOnMap.put("LABEL_TYPE_ID", "ID");
        registerOuterJoin(cq, joinOnMap, "labelType");
    }

    public boolean hasConditionQueryLabelType() {
        return _conditionQueryLabelType != null;
    }

    /**
     * Get the condition-query for relation table. <br />
     * FILE_CRAWLING_CONFIG by my FILE_CONFIG_ID, named 'fileCrawlingConfig'.
     * @return The instance of condition-query. (NotNull)
     */
    public FileCrawlingConfigCQ queryFileCrawlingConfig() {
        return getConditionQueryFileCrawlingConfig();
    }

    protected FileCrawlingConfigCQ _conditionQueryFileCrawlingConfig;

    public FileCrawlingConfigCQ getConditionQueryFileCrawlingConfig() {
        if (_conditionQueryFileCrawlingConfig == null) {
            _conditionQueryFileCrawlingConfig = xcreateQueryFileCrawlingConfig();
            xsetupOuterJoinFileCrawlingConfig();
        }
        return _conditionQueryFileCrawlingConfig;
    }

    protected FileCrawlingConfigCQ xcreateQueryFileCrawlingConfig() {
        final String nrp = resolveNextRelationPath(
                "FILE_CONFIG_TO_LABEL_TYPE_MAPPING", "fileCrawlingConfig");
        final String jan = resolveJoinAliasName(nrp, xgetNextNestLevel());
        final FileCrawlingConfigCQ cq = new FileCrawlingConfigCQ(this,
                xgetSqlClause(), jan, xgetNextNestLevel());
        cq.xsetBaseCB(_baseCB);
        cq.xsetForeignPropertyName("fileCrawlingConfig");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinFileCrawlingConfig() {
        final FileCrawlingConfigCQ cq = getConditionQueryFileCrawlingConfig();
        final Map<String, String> joinOnMap = newLinkedHashMapSized(4);
        joinOnMap.put("FILE_CONFIG_ID", "ID");
        registerOuterJoin(cq, joinOnMap, "fileCrawlingConfig");
    }

    public boolean hasConditionQueryFileCrawlingConfig() {
        return _conditionQueryFileCrawlingConfig != null;
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    protected Map<String, FileConfigToLabelTypeMappingCQ> _scalarConditionMap;

    public Map<String, FileConfigToLabelTypeMappingCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(
            final FileConfigToLabelTypeMappingCQ subQuery) {
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
    protected Map<String, FileConfigToLabelTypeMappingCQ> _specifyMyselfDerivedMap;

    public Map<String, FileConfigToLabelTypeMappingCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(
            final FileConfigToLabelTypeMappingCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, FileConfigToLabelTypeMappingCQ> _queryMyselfDerivedMap;

    public Map<String, FileConfigToLabelTypeMappingCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(
            final FileConfigToLabelTypeMappingCQ subQuery) {
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
    protected Map<String, FileConfigToLabelTypeMappingCQ> _myselfExistsMap;

    public Map<String, FileConfigToLabelTypeMappingCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final FileConfigToLabelTypeMappingCQ subQuery) {
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
    protected Map<String, FileConfigToLabelTypeMappingCQ> _myselfInScopeMap;

    public Map<String, FileConfigToLabelTypeMappingCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(
            final FileConfigToLabelTypeMappingCQ subQuery) {
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
        return FileConfigToLabelTypeMappingCB.class.getName();
    }

    protected String xCQ() {
        return FileConfigToLabelTypeMappingCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
