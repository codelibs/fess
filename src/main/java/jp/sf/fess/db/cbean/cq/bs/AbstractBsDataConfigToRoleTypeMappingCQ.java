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

import java.util.Collection;

import jp.sf.fess.db.allcommon.DBMetaInstanceHandler;
import jp.sf.fess.db.cbean.DataConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.DataCrawlingConfigCB;
import jp.sf.fess.db.cbean.RoleTypeCB;
import jp.sf.fess.db.cbean.cq.DataConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.DataCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.RoleTypeCQ;

import org.seasar.dbflute.cbean.AbstractConditionQuery;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.chelper.HpQDRFunction;
import org.seasar.dbflute.cbean.chelper.HpQDRSetupper;
import org.seasar.dbflute.cbean.chelper.HpSSQFunction;
import org.seasar.dbflute.cbean.chelper.HpSSQOption;
import org.seasar.dbflute.cbean.chelper.HpSSQSetupper;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.DerivedReferrerOption;
import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.cbean.coption.RangeOfOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMetaProvider;

/**
 * The abstract condition-query of DATA_CONFIG_TO_ROLE_TYPE_MAPPING.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsDataConfigToRoleTypeMappingCQ extends
        AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsDataConfigToRoleTypeMappingCQ(
            final ConditionQuery childQuery, final SqlClause sqlClause,
            final String aliasName, final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                     DBMeta Provider
    //                                                                     ===============
    @Override
    protected DBMetaProvider xgetDBMetaProvider() {
        return DBMetaInstanceHandler.getProvider();
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    @Override
    public String getTableDbName() {
        return "DATA_CONFIG_TO_ROLE_TYPE_MAPPING";
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as equal. (NullAllowed: if null, no condition)
     */
    public void setId_Equal(final Long id) {
        doSetId_Equal(id);
    }

    protected void doSetId_Equal(final Long id) {
        regId(CK_EQ, id);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as notEqual. (NullAllowed: if null, no condition)
     */
    public void setId_NotEqual(final Long id) {
        doSetId_NotEqual(id);
    }

    protected void doSetId_NotEqual(final Long id) {
        regId(CK_NES, id);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setId_GreaterThan(final Long id) {
        regId(CK_GT, id);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as lessThan. (NullAllowed: if null, no condition)
     */
    public void setId_LessThan(final Long id) {
        regId(CK_LT, id);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setId_GreaterEqual(final Long id) {
        regId(CK_GE, id);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param id The value of id as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setId_LessEqual(final Long id) {
        regId(CK_LE, id);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param minNumber The min number of id. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of id. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setId_RangeOf(final Long minNumber, final Long maxNumber,
            final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueId(), "ID", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param idList The collection of id as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setId_InScope(final Collection<Long> idList) {
        doSetId_InScope(idList);
    }

    protected void doSetId_InScope(final Collection<Long> idList) {
        regINS(CK_INS, cTL(idList), getCValueId(), "ID");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @param idList The collection of id as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setId_NotInScope(final Collection<Long> idList) {
        doSetId_NotInScope(idList);
    }

    protected void doSetId_NotInScope(final Collection<Long> idList) {
        regINS(CK_NINS, cTL(idList), getCValueId(), "ID");
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     */
    public void setId_IsNull() {
        regId(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     */
    public void setId_IsNotNull() {
        regId(CK_ISNN, DOBJ);
    }

    protected void regId(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueId(), "ID");
    }

    abstract protected ConditionValue getCValueId();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG}
     * @param dataConfigId The value of dataConfigId as equal. (NullAllowed: if null, no condition)
     */
    public void setDataConfigId_Equal(final Long dataConfigId) {
        doSetDataConfigId_Equal(dataConfigId);
    }

    protected void doSetDataConfigId_Equal(final Long dataConfigId) {
        regDataConfigId(CK_EQ, dataConfigId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG}
     * @param dataConfigId The value of dataConfigId as notEqual. (NullAllowed: if null, no condition)
     */
    public void setDataConfigId_NotEqual(final Long dataConfigId) {
        doSetDataConfigId_NotEqual(dataConfigId);
    }

    protected void doSetDataConfigId_NotEqual(final Long dataConfigId) {
        regDataConfigId(CK_NES, dataConfigId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG}
     * @param dataConfigId The value of dataConfigId as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setDataConfigId_GreaterThan(final Long dataConfigId) {
        regDataConfigId(CK_GT, dataConfigId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG}
     * @param dataConfigId The value of dataConfigId as lessThan. (NullAllowed: if null, no condition)
     */
    public void setDataConfigId_LessThan(final Long dataConfigId) {
        regDataConfigId(CK_LT, dataConfigId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG}
     * @param dataConfigId The value of dataConfigId as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setDataConfigId_GreaterEqual(final Long dataConfigId) {
        regDataConfigId(CK_GE, dataConfigId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG}
     * @param dataConfigId The value of dataConfigId as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setDataConfigId_LessEqual(final Long dataConfigId) {
        regDataConfigId(CK_LE, dataConfigId);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG}
     * @param minNumber The min number of dataConfigId. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of dataConfigId. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setDataConfigId_RangeOf(final Long minNumber,
            final Long maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueDataConfigId(), "DATA_CONFIG_ID",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG}
     * @param dataConfigIdList The collection of dataConfigId as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setDataConfigId_InScope(final Collection<Long> dataConfigIdList) {
        doSetDataConfigId_InScope(dataConfigIdList);
    }

    protected void doSetDataConfigId_InScope(
            final Collection<Long> dataConfigIdList) {
        regINS(CK_INS, cTL(dataConfigIdList), getCValueDataConfigId(),
                "DATA_CONFIG_ID");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG}
     * @param dataConfigIdList The collection of dataConfigId as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setDataConfigId_NotInScope(
            final Collection<Long> dataConfigIdList) {
        doSetDataConfigId_NotInScope(dataConfigIdList);
    }

    protected void doSetDataConfigId_NotInScope(
            final Collection<Long> dataConfigIdList) {
        regINS(CK_NINS, cTL(dataConfigIdList), getCValueDataConfigId(),
                "DATA_CONFIG_ID");
    }

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select DATA_CONFIG_ID from DATA_CRAWLING_CONFIG where ...)} <br />
     * DATA_CRAWLING_CONFIG by my DATA_CONFIG_ID, named 'dataCrawlingConfig'.
     * @param subQuery The sub-query of DataCrawlingConfig for 'in-scope'. (NotNull)
     */
    public void inScopeDataCrawlingConfig(
            final SubQuery<DataCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery<DataCrawlingConfigCB>", subQuery);
        final DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepDataConfigId_InScopeRelation_DataCrawlingConfig(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "DATA_CONFIG_ID", "ID",
                subQueryPropertyName, "dataCrawlingConfig");
    }

    public abstract String keepDataConfigId_InScopeRelation_DataCrawlingConfig(
            DataCrawlingConfigCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select DATA_CONFIG_ID from DATA_CRAWLING_CONFIG where ...)} <br />
     * DATA_CRAWLING_CONFIG by my DATA_CONFIG_ID, named 'dataCrawlingConfig'.
     * @param subQuery The sub-query of DataCrawlingConfig for 'not in-scope'. (NotNull)
     */
    public void notInScopeDataCrawlingConfig(
            final SubQuery<DataCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery<DataCrawlingConfigCB>", subQuery);
        final DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepDataConfigId_NotInScopeRelation_DataCrawlingConfig(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "DATA_CONFIG_ID", "ID",
                subQueryPropertyName, "dataCrawlingConfig");
    }

    public abstract String keepDataConfigId_NotInScopeRelation_DataCrawlingConfig(
            DataCrawlingConfigCQ subQuery);

    protected void regDataConfigId(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueDataConfigId(), "DATA_CONFIG_ID");
    }

    abstract protected ConditionValue getCValueDataConfigId();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @param roleTypeId The value of roleTypeId as equal. (NullAllowed: if null, no condition)
     */
    public void setRoleTypeId_Equal(final Long roleTypeId) {
        doSetRoleTypeId_Equal(roleTypeId);
    }

    protected void doSetRoleTypeId_Equal(final Long roleTypeId) {
        regRoleTypeId(CK_EQ, roleTypeId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @param roleTypeId The value of roleTypeId as notEqual. (NullAllowed: if null, no condition)
     */
    public void setRoleTypeId_NotEqual(final Long roleTypeId) {
        doSetRoleTypeId_NotEqual(roleTypeId);
    }

    protected void doSetRoleTypeId_NotEqual(final Long roleTypeId) {
        regRoleTypeId(CK_NES, roleTypeId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @param roleTypeId The value of roleTypeId as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setRoleTypeId_GreaterThan(final Long roleTypeId) {
        regRoleTypeId(CK_GT, roleTypeId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @param roleTypeId The value of roleTypeId as lessThan. (NullAllowed: if null, no condition)
     */
    public void setRoleTypeId_LessThan(final Long roleTypeId) {
        regRoleTypeId(CK_LT, roleTypeId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @param roleTypeId The value of roleTypeId as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setRoleTypeId_GreaterEqual(final Long roleTypeId) {
        regRoleTypeId(CK_GE, roleTypeId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @param roleTypeId The value of roleTypeId as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setRoleTypeId_LessEqual(final Long roleTypeId) {
        regRoleTypeId(CK_LE, roleTypeId);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @param minNumber The min number of roleTypeId. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of roleTypeId. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setRoleTypeId_RangeOf(final Long minNumber,
            final Long maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueRoleTypeId(), "ROLE_TYPE_ID",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @param roleTypeIdList The collection of roleTypeId as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setRoleTypeId_InScope(final Collection<Long> roleTypeIdList) {
        doSetRoleTypeId_InScope(roleTypeIdList);
    }

    protected void doSetRoleTypeId_InScope(final Collection<Long> roleTypeIdList) {
        regINS(CK_INS, cTL(roleTypeIdList), getCValueRoleTypeId(),
                "ROLE_TYPE_ID");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @param roleTypeIdList The collection of roleTypeId as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setRoleTypeId_NotInScope(final Collection<Long> roleTypeIdList) {
        doSetRoleTypeId_NotInScope(roleTypeIdList);
    }

    protected void doSetRoleTypeId_NotInScope(
            final Collection<Long> roleTypeIdList) {
        regINS(CK_NINS, cTL(roleTypeIdList), getCValueRoleTypeId(),
                "ROLE_TYPE_ID");
    }

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select ROLE_TYPE_ID from ROLE_TYPE where ...)} <br />
     * ROLE_TYPE by my ROLE_TYPE_ID, named 'roleType'.
     * @param subQuery The sub-query of RoleType for 'in-scope'. (NotNull)
     */
    public void inScopeRoleType(final SubQuery<RoleTypeCB> subQuery) {
        assertObjectNotNull("subQuery<RoleTypeCB>", subQuery);
        final RoleTypeCB cb = new RoleTypeCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepRoleTypeId_InScopeRelation_RoleType(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ROLE_TYPE_ID", "ID",
                subQueryPropertyName, "roleType");
    }

    public abstract String keepRoleTypeId_InScopeRelation_RoleType(
            RoleTypeCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select ROLE_TYPE_ID from ROLE_TYPE where ...)} <br />
     * ROLE_TYPE by my ROLE_TYPE_ID, named 'roleType'.
     * @param subQuery The sub-query of RoleType for 'not in-scope'. (NotNull)
     */
    public void notInScopeRoleType(final SubQuery<RoleTypeCB> subQuery) {
        assertObjectNotNull("subQuery<RoleTypeCB>", subQuery);
        final RoleTypeCB cb = new RoleTypeCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepRoleTypeId_NotInScopeRelation_RoleType(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ROLE_TYPE_ID", "ID",
                subQueryPropertyName, "roleType");
    }

    public abstract String keepRoleTypeId_NotInScopeRelation_RoleType(
            RoleTypeCQ subQuery);

    protected void regRoleTypeId(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueRoleTypeId(), "ROLE_TYPE_ID");
    }

    abstract protected ConditionValue getCValueRoleTypeId();

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_Equal()</span>.max(new SubQuery&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void query(DataConfigToRoleTypeMappingCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<DataConfigToRoleTypeMappingCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ.getOperand());
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void query(DataConfigToRoleTypeMappingCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<DataConfigToRoleTypeMappingCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void query(DataConfigToRoleTypeMappingCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<DataConfigToRoleTypeMappingCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessThan()</span>.max(new SubQuery&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void query(DataConfigToRoleTypeMappingCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<DataConfigToRoleTypeMappingCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void query(DataConfigToRoleTypeMappingCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<DataConfigToRoleTypeMappingCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void query(DataConfigToRoleTypeMappingCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<DataConfigToRoleTypeMappingCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE.getOperand());
    }

    protected HpSSQFunction<DataConfigToRoleTypeMappingCB> xcreateSSQFunction(
            final String operand) {
        return new HpSSQFunction<DataConfigToRoleTypeMappingCB>(
                new HpSSQSetupper<DataConfigToRoleTypeMappingCB>() {
                    @Override
                    public void setup(
                            final String function,
                            final SubQuery<DataConfigToRoleTypeMappingCB> subQuery,
                            final HpSSQOption<DataConfigToRoleTypeMappingCB> option) {
                        xscalarCondition(function, subQuery, operand, option);
                    }
                });
    }

    protected void xscalarCondition(final String function,
            final SubQuery<DataConfigToRoleTypeMappingCB> subQuery,
            final String operand,
            final HpSSQOption<DataConfigToRoleTypeMappingCB> option) {
        assertObjectNotNull("subQuery<DataConfigToRoleTypeMappingCB>", subQuery);
        final DataConfigToRoleTypeMappingCB cb = xcreateScalarConditionCB();
        subQuery.query(cb);
        final String subQueryPropertyName = keepScalarCondition(cb.query()); // for saving query-value
        option.setPartitionByCBean(xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(function, cb.query(), subQueryPropertyName,
                operand, option);
    }

    public abstract String keepScalarCondition(
            DataConfigToRoleTypeMappingCQ subQuery);

    protected DataConfigToRoleTypeMappingCB xcreateScalarConditionCB() {
        final DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected DataConfigToRoleTypeMappingCB xcreateScalarConditionPartitionByCB() {
        final DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String function,
            final SubQuery<DataConfigToRoleTypeMappingCB> subQuery,
            final String aliasName, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<DataConfigToRoleTypeMappingCB>", subQuery);
        final DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepSpecifyMyselfDerived(cb.query()); // for saving query-value.
        registerSpecifyMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", aliasName, option);
    }

    public abstract String keepSpecifyMyselfDerived(
            DataConfigToRoleTypeMappingCQ subQuery);

    /**
     * Prepare for (Query)MyselfDerived (SubQuery).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<DataConfigToRoleTypeMappingCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived();
    }

    protected HpQDRFunction<DataConfigToRoleTypeMappingCB> xcreateQDRFunctionMyselfDerived() {
        return new HpQDRFunction<DataConfigToRoleTypeMappingCB>(
                new HpQDRSetupper<DataConfigToRoleTypeMappingCB>() {
                    @Override
                    public void setup(
                            final String function,
                            final SubQuery<DataConfigToRoleTypeMappingCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveMyselfDerived(function, subQuery, operand,
                                value, option);
                    }
                });
    }

    public void xqderiveMyselfDerived(final String function,
            final SubQuery<DataConfigToRoleTypeMappingCB> subQuery,
            final String operand, final Object value,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<DataConfigToRoleTypeMappingCB>", subQuery);
        final DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String parameterPropertyName = keepQueryMyselfDerivedParameter(value);
        registerQueryMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepQueryMyselfDerived(
            DataConfigToRoleTypeMappingCQ subQuery);

    public abstract String keepQueryMyselfDerivedParameter(Object parameterValue);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfExists(
            final SubQuery<DataConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<DataConfigToRoleTypeMappingCB>", subQuery);
        final DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
        cb.xsetupForMyselfExists(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfExists(cb.query()); // for saving query-value.
        registerMyselfExists(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfExists(
            DataConfigToRoleTypeMappingCQ subQuery);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfInScope(
            final SubQuery<DataConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<DataConfigToRoleTypeMappingCB>", subQuery);
        final DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
        cb.xsetupForMyselfInScope(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfInScope(cb.query()); // for saving query-value.
        registerMyselfInScope(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfInScope(
            DataConfigToRoleTypeMappingCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCB() {
        return DataConfigToRoleTypeMappingCB.class.getName();
    }

    protected String xabCQ() {
        return DataConfigToRoleTypeMappingCQ.class.getName();
    }

    protected String xabLSO() {
        return LikeSearchOption.class.getName();
    }

    protected String xabSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
