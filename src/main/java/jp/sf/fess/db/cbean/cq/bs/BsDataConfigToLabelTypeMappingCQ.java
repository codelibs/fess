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

import jp.sf.fess.db.cbean.DataConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.cq.DataConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.DataCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.LabelTypeCQ;
import jp.sf.fess.db.cbean.cq.ciq.DataConfigToLabelTypeMappingCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of DATA_CONFIG_TO_LABEL_TYPE_MAPPING.
 * @author DBFlute(AutoGenerator)
 */
public class BsDataConfigToLabelTypeMappingCQ extends
        AbstractBsDataConfigToLabelTypeMappingCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataConfigToLabelTypeMappingCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsDataConfigToLabelTypeMappingCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from DATA_CONFIG_TO_LABEL_TYPE_MAPPING) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public DataConfigToLabelTypeMappingCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected DataConfigToLabelTypeMappingCIQ xcreateCIQ() {
        final DataConfigToLabelTypeMappingCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected DataConfigToLabelTypeMappingCIQ xnewCIQ() {
        return new DataConfigToLabelTypeMappingCIQ(xgetReferrerQuery(),
                xgetSqlClause(), xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join DATA_CONFIG_TO_LABEL_TYPE_MAPPING on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public DataConfigToLabelTypeMappingCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final DataConfigToLabelTypeMappingCIQ inlineQuery = inline();
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
    public BsDataConfigToLabelTypeMappingCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsDataConfigToLabelTypeMappingCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _dataConfigId;

    public ConditionValue getDataConfigId() {
        if (_dataConfigId == null) {
            _dataConfigId = nCV();
        }
        return _dataConfigId;
    }

    @Override
    protected ConditionValue getCValueDataConfigId() {
        return getDataConfigId();
    }

    public Map<String, DataCrawlingConfigCQ> getDataConfigId_InScopeRelation_DataCrawlingConfig() {
        return xgetSQueMap("dataConfigId_InScopeRelation_DataCrawlingConfig");
    }

    @Override
    public String keepDataConfigId_InScopeRelation_DataCrawlingConfig(
            final DataCrawlingConfigCQ sq) {
        return xkeepSQue("dataConfigId_InScopeRelation_DataCrawlingConfig", sq);
    }

    public Map<String, DataCrawlingConfigCQ> getDataConfigId_NotInScopeRelation_DataCrawlingConfig() {
        return xgetSQueMap("dataConfigId_NotInScopeRelation_DataCrawlingConfig");
    }

    @Override
    public String keepDataConfigId_NotInScopeRelation_DataCrawlingConfig(
            final DataCrawlingConfigCQ sq) {
        return xkeepSQue("dataConfigId_NotInScopeRelation_DataCrawlingConfig",
                sq);
    }

    /**
     * Add order-by as ascend. <br />
     * DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsDataConfigToLabelTypeMappingCQ addOrderBy_DataConfigId_Asc() {
        regOBA("DATA_CONFIG_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsDataConfigToLabelTypeMappingCQ addOrderBy_DataConfigId_Desc() {
        regOBD("DATA_CONFIG_ID");
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

    public Map<String, LabelTypeCQ> getLabelTypeId_InScopeRelation_LabelType() {
        return xgetSQueMap("labelTypeId_InScopeRelation_LabelType");
    }

    @Override
    public String keepLabelTypeId_InScopeRelation_LabelType(final LabelTypeCQ sq) {
        return xkeepSQue("labelTypeId_InScopeRelation_LabelType", sq);
    }

    public Map<String, LabelTypeCQ> getLabelTypeId_NotInScopeRelation_LabelType() {
        return xgetSQueMap("labelTypeId_NotInScopeRelation_LabelType");
    }

    @Override
    public String keepLabelTypeId_NotInScopeRelation_LabelType(
            final LabelTypeCQ sq) {
        return xkeepSQue("labelTypeId_NotInScopeRelation_LabelType", sq);
    }

    /**
     * Add order-by as ascend. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @return this. (NotNull)
     */
    public BsDataConfigToLabelTypeMappingCQ addOrderBy_LabelTypeId_Asc() {
        regOBA("LABEL_TYPE_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @return this. (NotNull)
     */
    public BsDataConfigToLabelTypeMappingCQ addOrderBy_LabelTypeId_Desc() {
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
     * }, <span style="color: #DD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #DD4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #DD4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsDataConfigToLabelTypeMappingCQ addSpecifiedDerivedOrderBy_Asc(
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
    public BsDataConfigToLabelTypeMappingCQ addSpecifiedDerivedOrderBy_Desc(
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
        final DataConfigToLabelTypeMappingCQ bq = (DataConfigToLabelTypeMappingCQ) bqs;
        final DataConfigToLabelTypeMappingCQ uq = (DataConfigToLabelTypeMappingCQ) uqs;
        if (bq.hasConditionQueryDataCrawlingConfig()) {
            uq.queryDataCrawlingConfig().reflectRelationOnUnionQuery(
                    bq.queryDataCrawlingConfig(), uq.queryDataCrawlingConfig());
        }
        if (bq.hasConditionQueryLabelType()) {
            uq.queryLabelType().reflectRelationOnUnionQuery(
                    bq.queryLabelType(), uq.queryLabelType());
        }
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    /**
     * Get the condition-query for relation table. <br />
     * DATA_CRAWLING_CONFIG by my DATA_CONFIG_ID, named 'dataCrawlingConfig'.
     * @return The instance of condition-query. (NotNull)
     */
    public DataCrawlingConfigCQ queryDataCrawlingConfig() {
        return getConditionQueryDataCrawlingConfig();
    }

    public DataCrawlingConfigCQ getConditionQueryDataCrawlingConfig() {
        final String prop = "dataCrawlingConfig";
        if (!xhasQueRlMap(prop)) {
            xregQueRl(prop, xcreateQueryDataCrawlingConfig());
            xsetupOuterJoinDataCrawlingConfig();
        }
        return xgetQueRlMap(prop);
    }

    protected DataCrawlingConfigCQ xcreateQueryDataCrawlingConfig() {
        final String nrp = xresolveNRP("DATA_CONFIG_TO_LABEL_TYPE_MAPPING",
                "dataCrawlingConfig");
        final String jan = xresolveJAN(nrp, xgetNNLvl());
        return xinitRelCQ(new DataCrawlingConfigCQ(this, xgetSqlClause(), jan,
                xgetNNLvl()), _baseCB, "dataCrawlingConfig", nrp);
    }

    protected void xsetupOuterJoinDataCrawlingConfig() {
        xregOutJo("dataCrawlingConfig");
    }

    public boolean hasConditionQueryDataCrawlingConfig() {
        return xhasQueRlMap("dataCrawlingConfig");
    }

    /**
     * Get the condition-query for relation table. <br />
     * LABEL_TYPE by my LABEL_TYPE_ID, named 'labelType'.
     * @return The instance of condition-query. (NotNull)
     */
    public LabelTypeCQ queryLabelType() {
        return getConditionQueryLabelType();
    }

    public LabelTypeCQ getConditionQueryLabelType() {
        final String prop = "labelType";
        if (!xhasQueRlMap(prop)) {
            xregQueRl(prop, xcreateQueryLabelType());
            xsetupOuterJoinLabelType();
        }
        return xgetQueRlMap(prop);
    }

    protected LabelTypeCQ xcreateQueryLabelType() {
        final String nrp = xresolveNRP("DATA_CONFIG_TO_LABEL_TYPE_MAPPING",
                "labelType");
        final String jan = xresolveJAN(nrp, xgetNNLvl());
        return xinitRelCQ(new LabelTypeCQ(this, xgetSqlClause(), jan,
                xgetNNLvl()), _baseCB, "labelType", nrp);
    }

    protected void xsetupOuterJoinLabelType() {
        xregOutJo("labelType");
    }

    public boolean hasConditionQueryLabelType() {
        return xhasQueRlMap("labelType");
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    public Map<String, DataConfigToLabelTypeMappingCQ> getScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final DataConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, DataConfigToLabelTypeMappingCQ> getSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(
            final DataConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, DataConfigToLabelTypeMappingCQ> getQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final DataConfigToLabelTypeMappingCQ sq) {
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
    protected Map<String, DataConfigToLabelTypeMappingCQ> _myselfExistsMap;

    public Map<String, DataConfigToLabelTypeMappingCQ> getMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final DataConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, DataConfigToLabelTypeMappingCQ> getMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    @Override
    public String keepMyselfInScope(final DataConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue("myselfInScope", sq);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return DataConfigToLabelTypeMappingCB.class.getName();
    }

    protected String xCQ() {
        return DataConfigToLabelTypeMappingCQ.class.getName();
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
