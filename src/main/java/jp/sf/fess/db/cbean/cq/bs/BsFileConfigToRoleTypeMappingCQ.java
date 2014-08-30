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

import jp.sf.fess.db.cbean.FileConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.cq.FileConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.RoleTypeCQ;
import jp.sf.fess.db.cbean.cq.ciq.FileConfigToRoleTypeMappingCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of FILE_CONFIG_TO_ROLE_TYPE_MAPPING.
 * @author DBFlute(AutoGenerator)
 */
public class BsFileConfigToRoleTypeMappingCQ extends
        AbstractBsFileConfigToRoleTypeMappingCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected FileConfigToRoleTypeMappingCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsFileConfigToRoleTypeMappingCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from FILE_CONFIG_TO_ROLE_TYPE_MAPPING) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public FileConfigToRoleTypeMappingCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected FileConfigToRoleTypeMappingCIQ xcreateCIQ() {
        final FileConfigToRoleTypeMappingCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected FileConfigToRoleTypeMappingCIQ xnewCIQ() {
        return new FileConfigToRoleTypeMappingCIQ(xgetReferrerQuery(),
                xgetSqlClause(), xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join FILE_CONFIG_TO_ROLE_TYPE_MAPPING on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public FileConfigToRoleTypeMappingCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final FileConfigToRoleTypeMappingCIQ inlineQuery = inline();
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
    public BsFileConfigToRoleTypeMappingCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsFileConfigToRoleTypeMappingCQ addOrderBy_Id_Desc() {
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

    public Map<String, FileCrawlingConfigCQ> getFileConfigId_InScopeRelation_FileCrawlingConfig() {
        return xgetSQueMap("fileConfigId_InScopeRelation_FileCrawlingConfig");
    }

    @Override
    public String keepFileConfigId_InScopeRelation_FileCrawlingConfig(
            final FileCrawlingConfigCQ sq) {
        return xkeepSQue("fileConfigId_InScopeRelation_FileCrawlingConfig", sq);
    }

    public Map<String, FileCrawlingConfigCQ> getFileConfigId_NotInScopeRelation_FileCrawlingConfig() {
        return xgetSQueMap("fileConfigId_NotInScopeRelation_FileCrawlingConfig");
    }

    @Override
    public String keepFileConfigId_NotInScopeRelation_FileCrawlingConfig(
            final FileCrawlingConfigCQ sq) {
        return xkeepSQue("fileConfigId_NotInScopeRelation_FileCrawlingConfig",
                sq);
    }

    /**
     * Add order-by as ascend. <br />
     * FILE_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsFileConfigToRoleTypeMappingCQ addOrderBy_FileConfigId_Asc() {
        regOBA("FILE_CONFIG_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * FILE_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsFileConfigToRoleTypeMappingCQ addOrderBy_FileConfigId_Desc() {
        regOBD("FILE_CONFIG_ID");
        return this;
    }

    protected ConditionValue _roleTypeId;

    public ConditionValue getRoleTypeId() {
        if (_roleTypeId == null) {
            _roleTypeId = nCV();
        }
        return _roleTypeId;
    }

    @Override
    protected ConditionValue getCValueRoleTypeId() {
        return getRoleTypeId();
    }

    public Map<String, RoleTypeCQ> getRoleTypeId_InScopeRelation_RoleType() {
        return xgetSQueMap("roleTypeId_InScopeRelation_RoleType");
    }

    @Override
    public String keepRoleTypeId_InScopeRelation_RoleType(final RoleTypeCQ sq) {
        return xkeepSQue("roleTypeId_InScopeRelation_RoleType", sq);
    }

    public Map<String, RoleTypeCQ> getRoleTypeId_NotInScopeRelation_RoleType() {
        return xgetSQueMap("roleTypeId_NotInScopeRelation_RoleType");
    }

    @Override
    public String keepRoleTypeId_NotInScopeRelation_RoleType(final RoleTypeCQ sq) {
        return xkeepSQue("roleTypeId_NotInScopeRelation_RoleType", sq);
    }

    /**
     * Add order-by as ascend. <br />
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @return this. (NotNull)
     */
    public BsFileConfigToRoleTypeMappingCQ addOrderBy_RoleTypeId_Asc() {
        regOBA("ROLE_TYPE_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @return this. (NotNull)
     */
    public BsFileConfigToRoleTypeMappingCQ addOrderBy_RoleTypeId_Desc() {
        regOBD("ROLE_TYPE_ID");
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
     * }, <span style="color: #DD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #DD4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #DD4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsFileConfigToRoleTypeMappingCQ addSpecifiedDerivedOrderBy_Asc(
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
     * }, <span style="color: #DD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #DD4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #DD4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsFileConfigToRoleTypeMappingCQ addSpecifiedDerivedOrderBy_Desc(
            final String aliasName) {
        registerSpecifiedDerivedOrderBy_Desc(aliasName);
        return this;
    }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    @Override
    public void reflectRelationOnUnionQuery(final ConditionQuery bqs,
            final ConditionQuery uqs) {
        final FileConfigToRoleTypeMappingCQ bq = (FileConfigToRoleTypeMappingCQ) bqs;
        final FileConfigToRoleTypeMappingCQ uq = (FileConfigToRoleTypeMappingCQ) uqs;
        if (bq.hasConditionQueryFileCrawlingConfig()) {
            uq.queryFileCrawlingConfig().reflectRelationOnUnionQuery(
                    bq.queryFileCrawlingConfig(), uq.queryFileCrawlingConfig());
        }
        if (bq.hasConditionQueryRoleType()) {
            uq.queryRoleType().reflectRelationOnUnionQuery(bq.queryRoleType(),
                    uq.queryRoleType());
        }
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    /**
     * Get the condition-query for relation table. <br />
     * FILE_CRAWLING_CONFIG by my FILE_CONFIG_ID, named 'fileCrawlingConfig'.
     * @return The instance of condition-query. (NotNull)
     */
    public FileCrawlingConfigCQ queryFileCrawlingConfig() {
        return getConditionQueryFileCrawlingConfig();
    }

    public FileCrawlingConfigCQ getConditionQueryFileCrawlingConfig() {
        final String prop = "fileCrawlingConfig";
        if (!xhasQueRlMap(prop)) {
            xregQueRl(prop, xcreateQueryFileCrawlingConfig());
            xsetupOuterJoinFileCrawlingConfig();
        }
        return xgetQueRlMap(prop);
    }

    protected FileCrawlingConfigCQ xcreateQueryFileCrawlingConfig() {
        final String nrp = xresolveNRP("FILE_CONFIG_TO_ROLE_TYPE_MAPPING",
                "fileCrawlingConfig");
        final String jan = xresolveJAN(nrp, xgetNNLvl());
        return xinitRelCQ(new FileCrawlingConfigCQ(this, xgetSqlClause(), jan,
                xgetNNLvl()), _baseCB, "fileCrawlingConfig", nrp);
    }

    protected void xsetupOuterJoinFileCrawlingConfig() {
        xregOutJo("fileCrawlingConfig");
    }

    public boolean hasConditionQueryFileCrawlingConfig() {
        return xhasQueRlMap("fileCrawlingConfig");
    }

    /**
     * Get the condition-query for relation table. <br />
     * ROLE_TYPE by my ROLE_TYPE_ID, named 'roleType'.
     * @return The instance of condition-query. (NotNull)
     */
    public RoleTypeCQ queryRoleType() {
        return getConditionQueryRoleType();
    }

    public RoleTypeCQ getConditionQueryRoleType() {
        final String prop = "roleType";
        if (!xhasQueRlMap(prop)) {
            xregQueRl(prop, xcreateQueryRoleType());
            xsetupOuterJoinRoleType();
        }
        return xgetQueRlMap(prop);
    }

    protected RoleTypeCQ xcreateQueryRoleType() {
        final String nrp = xresolveNRP("FILE_CONFIG_TO_ROLE_TYPE_MAPPING",
                "roleType");
        final String jan = xresolveJAN(nrp, xgetNNLvl());
        return xinitRelCQ(new RoleTypeCQ(this, xgetSqlClause(), jan,
                xgetNNLvl()), _baseCB, "roleType", nrp);
    }

    protected void xsetupOuterJoinRoleType() {
        xregOutJo("roleType");
    }

    public boolean hasConditionQueryRoleType() {
        return xhasQueRlMap("roleType");
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    public Map<String, FileConfigToRoleTypeMappingCQ> getScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, FileConfigToRoleTypeMappingCQ> getSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(
            final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, FileConfigToRoleTypeMappingCQ> getQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("queryMyselfDerived", sq);
    }

    public Map<String, Object> getQueryMyselfDerivedParameter() {
        return xgetSQuePmMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object pm) {
        return xkeepSQuePm("queryMyselfDerived", pm);
    }

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    protected Map<String, FileConfigToRoleTypeMappingCQ> _myselfExistsMap;

    public Map<String, FileConfigToRoleTypeMappingCQ> getMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, FileConfigToRoleTypeMappingCQ> getMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    @Override
    public String keepMyselfInScope(final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("myselfInScope", sq);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return FileConfigToRoleTypeMappingCB.class.getName();
    }

    protected String xCQ() {
        return FileConfigToRoleTypeMappingCQ.class.getName();
    }

    protected String xCHp() {
        return HpCalculator.class.getName();
    }

    protected String xCOp() {
        return ConditionOption.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
