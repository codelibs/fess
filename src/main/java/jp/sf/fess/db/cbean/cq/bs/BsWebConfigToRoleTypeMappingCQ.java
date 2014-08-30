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

import jp.sf.fess.db.cbean.WebConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.cq.RoleTypeCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.ciq.WebConfigToRoleTypeMappingCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of WEB_CONFIG_TO_ROLE_TYPE_MAPPING.
 * @author DBFlute(AutoGenerator)
 */
public class BsWebConfigToRoleTypeMappingCQ extends
        AbstractBsWebConfigToRoleTypeMappingCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected WebConfigToRoleTypeMappingCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsWebConfigToRoleTypeMappingCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from WEB_CONFIG_TO_ROLE_TYPE_MAPPING) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public WebConfigToRoleTypeMappingCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected WebConfigToRoleTypeMappingCIQ xcreateCIQ() {
        final WebConfigToRoleTypeMappingCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected WebConfigToRoleTypeMappingCIQ xnewCIQ() {
        return new WebConfigToRoleTypeMappingCIQ(xgetReferrerQuery(),
                xgetSqlClause(), xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join WEB_CONFIG_TO_ROLE_TYPE_MAPPING on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public WebConfigToRoleTypeMappingCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final WebConfigToRoleTypeMappingCIQ inlineQuery = inline();
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
    public BsWebConfigToRoleTypeMappingCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsWebConfigToRoleTypeMappingCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _webConfigId;

    public ConditionValue getWebConfigId() {
        if (_webConfigId == null) {
            _webConfigId = nCV();
        }
        return _webConfigId;
    }

    @Override
    protected ConditionValue getCValueWebConfigId() {
        return getWebConfigId();
    }

    public Map<String, WebCrawlingConfigCQ> getWebConfigId_InScopeRelation_WebCrawlingConfig() {
        return xgetSQueMap("webConfigId_InScopeRelation_WebCrawlingConfig");
    }

    @Override
    public String keepWebConfigId_InScopeRelation_WebCrawlingConfig(
            final WebCrawlingConfigCQ sq) {
        return xkeepSQue("webConfigId_InScopeRelation_WebCrawlingConfig", sq);
    }

    public Map<String, WebCrawlingConfigCQ> getWebConfigId_NotInScopeRelation_WebCrawlingConfig() {
        return xgetSQueMap("webConfigId_NotInScopeRelation_WebCrawlingConfig");
    }

    @Override
    public String keepWebConfigId_NotInScopeRelation_WebCrawlingConfig(
            final WebCrawlingConfigCQ sq) {
        return xkeepSQue("webConfigId_NotInScopeRelation_WebCrawlingConfig", sq);
    }

    /**
     * Add order-by as ascend. <br />
     * WEB_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsWebConfigToRoleTypeMappingCQ addOrderBy_WebConfigId_Asc() {
        regOBA("WEB_CONFIG_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * WEB_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsWebConfigToRoleTypeMappingCQ addOrderBy_WebConfigId_Desc() {
        regOBD("WEB_CONFIG_ID");
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
    public BsWebConfigToRoleTypeMappingCQ addOrderBy_RoleTypeId_Asc() {
        regOBA("ROLE_TYPE_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @return this. (NotNull)
     */
    public BsWebConfigToRoleTypeMappingCQ addOrderBy_RoleTypeId_Desc() {
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
    public BsWebConfigToRoleTypeMappingCQ addSpecifiedDerivedOrderBy_Asc(
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
    public BsWebConfigToRoleTypeMappingCQ addSpecifiedDerivedOrderBy_Desc(
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
        final WebConfigToRoleTypeMappingCQ bq = (WebConfigToRoleTypeMappingCQ) bqs;
        final WebConfigToRoleTypeMappingCQ uq = (WebConfigToRoleTypeMappingCQ) uqs;
        if (bq.hasConditionQueryRoleType()) {
            uq.queryRoleType().reflectRelationOnUnionQuery(bq.queryRoleType(),
                    uq.queryRoleType());
        }
        if (bq.hasConditionQueryWebCrawlingConfig()) {
            uq.queryWebCrawlingConfig().reflectRelationOnUnionQuery(
                    bq.queryWebCrawlingConfig(), uq.queryWebCrawlingConfig());
        }
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
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
        final String nrp = xresolveNRP("WEB_CONFIG_TO_ROLE_TYPE_MAPPING",
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

    /**
     * Get the condition-query for relation table. <br />
     * WEB_CRAWLING_CONFIG by my WEB_CONFIG_ID, named 'webCrawlingConfig'.
     * @return The instance of condition-query. (NotNull)
     */
    public WebCrawlingConfigCQ queryWebCrawlingConfig() {
        return getConditionQueryWebCrawlingConfig();
    }

    public WebCrawlingConfigCQ getConditionQueryWebCrawlingConfig() {
        final String prop = "webCrawlingConfig";
        if (!xhasQueRlMap(prop)) {
            xregQueRl(prop, xcreateQueryWebCrawlingConfig());
            xsetupOuterJoinWebCrawlingConfig();
        }
        return xgetQueRlMap(prop);
    }

    protected WebCrawlingConfigCQ xcreateQueryWebCrawlingConfig() {
        final String nrp = xresolveNRP("WEB_CONFIG_TO_ROLE_TYPE_MAPPING",
                "webCrawlingConfig");
        final String jan = xresolveJAN(nrp, xgetNNLvl());
        return xinitRelCQ(new WebCrawlingConfigCQ(this, xgetSqlClause(), jan,
                xgetNNLvl()), _baseCB, "webCrawlingConfig", nrp);
    }

    protected void xsetupOuterJoinWebCrawlingConfig() {
        xregOutJo("webCrawlingConfig");
    }

    public boolean hasConditionQueryWebCrawlingConfig() {
        return xhasQueRlMap("webCrawlingConfig");
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    public Map<String, WebConfigToRoleTypeMappingCQ> getScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, WebConfigToRoleTypeMappingCQ> getSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, WebConfigToRoleTypeMappingCQ> getQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final WebConfigToRoleTypeMappingCQ sq) {
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
    protected Map<String, WebConfigToRoleTypeMappingCQ> _myselfExistsMap;

    public Map<String, WebConfigToRoleTypeMappingCQ> getMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, WebConfigToRoleTypeMappingCQ> getMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    @Override
    public String keepMyselfInScope(final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("myselfInScope", sq);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return WebConfigToRoleTypeMappingCB.class.getName();
    }

    protected String xCQ() {
        return WebConfigToRoleTypeMappingCQ.class.getName();
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
