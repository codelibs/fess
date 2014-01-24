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

import java.util.Collection;

import jp.sf.fess.db.allcommon.DBMetaInstanceHandler;
import jp.sf.fess.db.cbean.DataConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.cbean.DataConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.DataConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.DataCrawlingConfigCB;
import jp.sf.fess.db.cbean.cq.DataConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.DataConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.DataConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.DataCrawlingConfigCQ;

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
import org.seasar.dbflute.cbean.coption.FromToOption;
import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.cbean.coption.RangeOfOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMetaProvider;

/**
 * The abstract condition-query of DATA_CRAWLING_CONFIG.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsDataCrawlingConfigCQ extends
        AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsDataCrawlingConfigCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
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
        return "DATA_CRAWLING_CONFIG";
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
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select DATA_CONFIG_ID from DATA_CONFIG_TO_BROWSER_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_BROWSER_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToBrowserTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsDataConfigToBrowserTypeMappingList</span>(new SubQuery&lt;DataConfigToBrowserTypeMappingCB&gt;() {
     *     public void query(DataCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of DataConfigToBrowserTypeMappingList for 'exists'. (NotNull)
     */
    public void existsDataConfigToBrowserTypeMappingList(
            final SubQuery<DataConfigToBrowserTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<DataConfigToBrowserTypeMappingCB>",
                subQuery);
        final DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_DataConfigToBrowserTypeMappingList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "DATA_CONFIG_ID",
                subQueryPropertyName, "dataConfigToBrowserTypeMappingList");
    }

    public abstract String keepId_ExistsReferrer_DataConfigToBrowserTypeMappingList(
            DataConfigToBrowserTypeMappingCQ subQuery);

    /**
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select DATA_CONFIG_ID from DATA_CONFIG_TO_LABEL_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_LABEL_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToLabelTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsDataConfigToLabelTypeMappingList</span>(new SubQuery&lt;DataConfigToLabelTypeMappingCB&gt;() {
     *     public void query(DataCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of DataConfigToLabelTypeMappingList for 'exists'. (NotNull)
     */
    public void existsDataConfigToLabelTypeMappingList(
            final SubQuery<DataConfigToLabelTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<DataConfigToLabelTypeMappingCB>",
                subQuery);
        final DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_DataConfigToLabelTypeMappingList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "DATA_CONFIG_ID",
                subQueryPropertyName, "dataConfigToLabelTypeMappingList");
    }

    public abstract String keepId_ExistsReferrer_DataConfigToLabelTypeMappingList(
            DataConfigToLabelTypeMappingCQ subQuery);

    /**
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select DATA_CONFIG_ID from DATA_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_ROLE_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsDataConfigToRoleTypeMappingList</span>(new SubQuery&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void query(DataCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of DataConfigToRoleTypeMappingList for 'exists'. (NotNull)
     */
    public void existsDataConfigToRoleTypeMappingList(
            final SubQuery<DataConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<DataConfigToRoleTypeMappingCB>", subQuery);
        final DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_DataConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "DATA_CONFIG_ID",
                subQueryPropertyName, "dataConfigToRoleTypeMappingList");
    }

    public abstract String keepId_ExistsReferrer_DataConfigToRoleTypeMappingList(
            DataConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select DATA_CONFIG_ID from DATA_CONFIG_TO_BROWSER_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_BROWSER_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToBrowserTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsDataConfigToBrowserTypeMappingList</span>(new SubQuery&lt;DataConfigToBrowserTypeMappingCB&gt;() {
     *     public void query(DataCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_DataConfigToBrowserTypeMappingList for 'not exists'. (NotNull)
     */
    public void notExistsDataConfigToBrowserTypeMappingList(
            final SubQuery<DataConfigToBrowserTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<DataConfigToBrowserTypeMappingCB>",
                subQuery);
        final DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_DataConfigToBrowserTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "DATA_CONFIG_ID",
                subQueryPropertyName, "dataConfigToBrowserTypeMappingList");
    }

    public abstract String keepId_NotExistsReferrer_DataConfigToBrowserTypeMappingList(
            DataConfigToBrowserTypeMappingCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select DATA_CONFIG_ID from DATA_CONFIG_TO_LABEL_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_LABEL_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToLabelTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsDataConfigToLabelTypeMappingList</span>(new SubQuery&lt;DataConfigToLabelTypeMappingCB&gt;() {
     *     public void query(DataCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_DataConfigToLabelTypeMappingList for 'not exists'. (NotNull)
     */
    public void notExistsDataConfigToLabelTypeMappingList(
            final SubQuery<DataConfigToLabelTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<DataConfigToLabelTypeMappingCB>",
                subQuery);
        final DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_DataConfigToLabelTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "DATA_CONFIG_ID",
                subQueryPropertyName, "dataConfigToLabelTypeMappingList");
    }

    public abstract String keepId_NotExistsReferrer_DataConfigToLabelTypeMappingList(
            DataConfigToLabelTypeMappingCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select DATA_CONFIG_ID from DATA_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_ROLE_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsDataConfigToRoleTypeMappingList</span>(new SubQuery&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void query(DataCrawlingConfigCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_DataConfigToRoleTypeMappingList for 'not exists'. (NotNull)
     */
    public void notExistsDataConfigToRoleTypeMappingList(
            final SubQuery<DataConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<DataConfigToRoleTypeMappingCB>", subQuery);
        final DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_DataConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "DATA_CONFIG_ID",
                subQueryPropertyName, "dataConfigToRoleTypeMappingList");
    }

    public abstract String keepId_NotExistsReferrer_DataConfigToRoleTypeMappingList(
            DataConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select DATA_CONFIG_ID from DATA_CONFIG_TO_BROWSER_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_BROWSER_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToBrowserTypeMappingAsOne'.
     * @param subQuery The sub-query of DataConfigToBrowserTypeMappingList for 'in-scope'. (NotNull)
     */
    public void inScopeDataConfigToBrowserTypeMappingList(
            final SubQuery<DataConfigToBrowserTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<DataConfigToBrowserTypeMappingCB>",
                subQuery);
        final DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_DataConfigToBrowserTypeMappingList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "DATA_CONFIG_ID",
                subQueryPropertyName, "dataConfigToBrowserTypeMappingList");
    }

    public abstract String keepId_InScopeRelation_DataConfigToBrowserTypeMappingList(
            DataConfigToBrowserTypeMappingCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select DATA_CONFIG_ID from DATA_CONFIG_TO_LABEL_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_LABEL_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToLabelTypeMappingAsOne'.
     * @param subQuery The sub-query of DataConfigToLabelTypeMappingList for 'in-scope'. (NotNull)
     */
    public void inScopeDataConfigToLabelTypeMappingList(
            final SubQuery<DataConfigToLabelTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<DataConfigToLabelTypeMappingCB>",
                subQuery);
        final DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_DataConfigToLabelTypeMappingList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "DATA_CONFIG_ID",
                subQueryPropertyName, "dataConfigToLabelTypeMappingList");
    }

    public abstract String keepId_InScopeRelation_DataConfigToLabelTypeMappingList(
            DataConfigToLabelTypeMappingCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select DATA_CONFIG_ID from DATA_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_ROLE_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToRoleTypeMappingAsOne'.
     * @param subQuery The sub-query of DataConfigToRoleTypeMappingList for 'in-scope'. (NotNull)
     */
    public void inScopeDataConfigToRoleTypeMappingList(
            final SubQuery<DataConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<DataConfigToRoleTypeMappingCB>", subQuery);
        final DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_DataConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "DATA_CONFIG_ID",
                subQueryPropertyName, "dataConfigToRoleTypeMappingList");
    }

    public abstract String keepId_InScopeRelation_DataConfigToRoleTypeMappingList(
            DataConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select DATA_CONFIG_ID from DATA_CONFIG_TO_BROWSER_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_BROWSER_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToBrowserTypeMappingAsOne'.
     * @param subQuery The sub-query of DataConfigToBrowserTypeMappingList for 'not in-scope'. (NotNull)
     */
    public void notInScopeDataConfigToBrowserTypeMappingList(
            final SubQuery<DataConfigToBrowserTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<DataConfigToBrowserTypeMappingCB>",
                subQuery);
        final DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_DataConfigToBrowserTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "DATA_CONFIG_ID",
                subQueryPropertyName, "dataConfigToBrowserTypeMappingList");
    }

    public abstract String keepId_NotInScopeRelation_DataConfigToBrowserTypeMappingList(
            DataConfigToBrowserTypeMappingCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select DATA_CONFIG_ID from DATA_CONFIG_TO_LABEL_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_LABEL_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToLabelTypeMappingAsOne'.
     * @param subQuery The sub-query of DataConfigToLabelTypeMappingList for 'not in-scope'. (NotNull)
     */
    public void notInScopeDataConfigToLabelTypeMappingList(
            final SubQuery<DataConfigToLabelTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<DataConfigToLabelTypeMappingCB>",
                subQuery);
        final DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_DataConfigToLabelTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "DATA_CONFIG_ID",
                subQueryPropertyName, "dataConfigToLabelTypeMappingList");
    }

    public abstract String keepId_NotInScopeRelation_DataConfigToLabelTypeMappingList(
            DataConfigToLabelTypeMappingCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select DATA_CONFIG_ID from DATA_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_ROLE_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToRoleTypeMappingAsOne'.
     * @param subQuery The sub-query of DataConfigToRoleTypeMappingList for 'not in-scope'. (NotNull)
     */
    public void notInScopeDataConfigToRoleTypeMappingList(
            final SubQuery<DataConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<DataConfigToRoleTypeMappingCB>", subQuery);
        final DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_DataConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "DATA_CONFIG_ID",
                subQueryPropertyName, "dataConfigToRoleTypeMappingList");
    }

    public abstract String keepId_NotInScopeRelation_DataConfigToRoleTypeMappingList(
            DataConfigToRoleTypeMappingCQ subQuery);

    public void xsderiveDataConfigToBrowserTypeMappingList(
            final String function,
            final SubQuery<DataConfigToBrowserTypeMappingCB> subQuery,
            final String aliasName, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<DataConfigToBrowserTypeMappingCB>",
                subQuery);
        final DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_DataConfigToBrowserTypeMappingList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID",
                "DATA_CONFIG_ID", subQueryPropertyName,
                "dataConfigToBrowserTypeMappingList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_DataConfigToBrowserTypeMappingList(
            DataConfigToBrowserTypeMappingCQ subQuery);

    public void xsderiveDataConfigToLabelTypeMappingList(final String function,
            final SubQuery<DataConfigToLabelTypeMappingCB> subQuery,
            final String aliasName, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<DataConfigToLabelTypeMappingCB>",
                subQuery);
        final DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID",
                "DATA_CONFIG_ID", subQueryPropertyName,
                "dataConfigToLabelTypeMappingList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingList(
            DataConfigToLabelTypeMappingCQ subQuery);

    public void xsderiveDataConfigToRoleTypeMappingList(final String function,
            final SubQuery<DataConfigToRoleTypeMappingCB> subQuery,
            final String aliasName, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<DataConfigToRoleTypeMappingCB>", subQuery);
        final DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID",
                "DATA_CONFIG_ID", subQueryPropertyName,
                "dataConfigToRoleTypeMappingList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingList(
            DataConfigToRoleTypeMappingCQ subQuery);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from DATA_CONFIG_TO_BROWSER_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_BROWSER_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToBrowserTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedDataConfigToBrowserTypeMappingList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;DataConfigToBrowserTypeMappingCB&gt;() {
     *     public void query(DataConfigToBrowserTypeMappingCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<DataConfigToBrowserTypeMappingCB> derivedDataConfigToBrowserTypeMappingList() {
        return xcreateQDRFunctionDataConfigToBrowserTypeMappingList();
    }

    protected HpQDRFunction<DataConfigToBrowserTypeMappingCB> xcreateQDRFunctionDataConfigToBrowserTypeMappingList() {
        return new HpQDRFunction<DataConfigToBrowserTypeMappingCB>(
                new HpQDRSetupper<DataConfigToBrowserTypeMappingCB>() {
                    @Override
                    public void setup(
                            final String function,
                            final SubQuery<DataConfigToBrowserTypeMappingCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveDataConfigToBrowserTypeMappingList(function,
                                subQuery, operand, value, option);
                    }
                });
    }

    public void xqderiveDataConfigToBrowserTypeMappingList(
            final String function,
            final SubQuery<DataConfigToBrowserTypeMappingCB> subQuery,
            final String operand, final Object value,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<DataConfigToBrowserTypeMappingCB>",
                subQuery);
        final DataConfigToBrowserTypeMappingCB cb = new DataConfigToBrowserTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_DataConfigToBrowserTypeMappingList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID",
                "DATA_CONFIG_ID", subQueryPropertyName,
                "dataConfigToBrowserTypeMappingList", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepId_QueryDerivedReferrer_DataConfigToBrowserTypeMappingList(
            DataConfigToBrowserTypeMappingCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListParameter(
            Object parameterValue);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from DATA_CONFIG_TO_LABEL_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_LABEL_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToLabelTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedDataConfigToLabelTypeMappingList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;DataConfigToLabelTypeMappingCB&gt;() {
     *     public void query(DataConfigToLabelTypeMappingCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<DataConfigToLabelTypeMappingCB> derivedDataConfigToLabelTypeMappingList() {
        return xcreateQDRFunctionDataConfigToLabelTypeMappingList();
    }

    protected HpQDRFunction<DataConfigToLabelTypeMappingCB> xcreateQDRFunctionDataConfigToLabelTypeMappingList() {
        return new HpQDRFunction<DataConfigToLabelTypeMappingCB>(
                new HpQDRSetupper<DataConfigToLabelTypeMappingCB>() {
                    @Override
                    public void setup(
                            final String function,
                            final SubQuery<DataConfigToLabelTypeMappingCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveDataConfigToLabelTypeMappingList(function,
                                subQuery, operand, value, option);
                    }
                });
    }

    public void xqderiveDataConfigToLabelTypeMappingList(final String function,
            final SubQuery<DataConfigToLabelTypeMappingCB> subQuery,
            final String operand, final Object value,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<DataConfigToLabelTypeMappingCB>",
                subQuery);
        final DataConfigToLabelTypeMappingCB cb = new DataConfigToLabelTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_DataConfigToLabelTypeMappingList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_DataConfigToLabelTypeMappingListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID",
                "DATA_CONFIG_ID", subQueryPropertyName,
                "dataConfigToLabelTypeMappingList", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepId_QueryDerivedReferrer_DataConfigToLabelTypeMappingList(
            DataConfigToLabelTypeMappingCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_DataConfigToLabelTypeMappingListParameter(
            Object parameterValue);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from DATA_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_ROLE_TYPE_MAPPING by DATA_CONFIG_ID, named 'dataConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedDataConfigToRoleTypeMappingList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void query(DataConfigToRoleTypeMappingCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<DataConfigToRoleTypeMappingCB> derivedDataConfigToRoleTypeMappingList() {
        return xcreateQDRFunctionDataConfigToRoleTypeMappingList();
    }

    protected HpQDRFunction<DataConfigToRoleTypeMappingCB> xcreateQDRFunctionDataConfigToRoleTypeMappingList() {
        return new HpQDRFunction<DataConfigToRoleTypeMappingCB>(
                new HpQDRSetupper<DataConfigToRoleTypeMappingCB>() {
                    @Override
                    public void setup(
                            final String function,
                            final SubQuery<DataConfigToRoleTypeMappingCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveDataConfigToRoleTypeMappingList(function,
                                subQuery, operand, value, option);
                    }
                });
    }

    public void xqderiveDataConfigToRoleTypeMappingList(final String function,
            final SubQuery<DataConfigToRoleTypeMappingCB> subQuery,
            final String operand, final Object value,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<DataConfigToRoleTypeMappingCB>", subQuery);
        final DataConfigToRoleTypeMappingCB cb = new DataConfigToRoleTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_DataConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_DataConfigToRoleTypeMappingListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID",
                "DATA_CONFIG_ID", subQueryPropertyName,
                "dataConfigToRoleTypeMappingList", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepId_QueryDerivedReferrer_DataConfigToRoleTypeMappingList(
            DataConfigToRoleTypeMappingCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_DataConfigToRoleTypeMappingListParameter(
            Object parameterValue);

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
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_Equal(final String name) {
        doSetName_Equal(fRES(name));
    }

    protected void doSetName_Equal(final String name) {
        regName(CK_EQ, name);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_NotEqual(final String name) {
        doSetName_NotEqual(fRES(name));
    }

    protected void doSetName_NotEqual(final String name) {
        regName(CK_NES, name);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_GreaterThan(final String name) {
        regName(CK_GT, fRES(name));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_LessThan(final String name) {
        regName(CK_LT, fRES(name));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_GreaterEqual(final String name) {
        regName(CK_GE, fRES(name));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_LessEqual(final String name) {
        regName(CK_LE, fRES(name));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param nameList The collection of name as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_InScope(final Collection<String> nameList) {
        doSetName_InScope(nameList);
    }

    public void doSetName_InScope(final Collection<String> nameList) {
        regINS(CK_INS, cTL(nameList), getCValueName(), "NAME");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param nameList The collection of name as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_NotInScope(final Collection<String> nameList) {
        doSetName_NotInScope(nameList);
    }

    public void doSetName_NotInScope(final Collection<String> nameList) {
        regINS(CK_NINS, cTL(nameList), getCValueName(), "NAME");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_PrefixSearch(final String name) {
        setName_LikeSearch(name, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)} <br />
     * <pre>e.g. setName_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param name The value of name as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setName_LikeSearch(final String name,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(name), getCValueName(), "NAME", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @param name The value of name as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setName_NotLikeSearch(final String name,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(name), getCValueName(), "NAME", likeSearchOption);
    }

    protected void regName(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueName(), "NAME");
    }

    abstract protected ConditionValue getCValueName();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_NAME: {NotNull, VARCHAR(200)}
     * @param handlerName The value of handlerName as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerName_Equal(final String handlerName) {
        doSetHandlerName_Equal(fRES(handlerName));
    }

    protected void doSetHandlerName_Equal(final String handlerName) {
        regHandlerName(CK_EQ, handlerName);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_NAME: {NotNull, VARCHAR(200)}
     * @param handlerName The value of handlerName as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerName_NotEqual(final String handlerName) {
        doSetHandlerName_NotEqual(fRES(handlerName));
    }

    protected void doSetHandlerName_NotEqual(final String handlerName) {
        regHandlerName(CK_NES, handlerName);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_NAME: {NotNull, VARCHAR(200)}
     * @param handlerName The value of handlerName as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerName_GreaterThan(final String handlerName) {
        regHandlerName(CK_GT, fRES(handlerName));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_NAME: {NotNull, VARCHAR(200)}
     * @param handlerName The value of handlerName as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerName_LessThan(final String handlerName) {
        regHandlerName(CK_LT, fRES(handlerName));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_NAME: {NotNull, VARCHAR(200)}
     * @param handlerName The value of handlerName as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerName_GreaterEqual(final String handlerName) {
        regHandlerName(CK_GE, fRES(handlerName));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_NAME: {NotNull, VARCHAR(200)}
     * @param handlerName The value of handlerName as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerName_LessEqual(final String handlerName) {
        regHandlerName(CK_LE, fRES(handlerName));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * HANDLER_NAME: {NotNull, VARCHAR(200)}
     * @param handlerNameList The collection of handlerName as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerName_InScope(final Collection<String> handlerNameList) {
        doSetHandlerName_InScope(handlerNameList);
    }

    public void doSetHandlerName_InScope(
            final Collection<String> handlerNameList) {
        regINS(CK_INS, cTL(handlerNameList), getCValueHandlerName(),
                "HANDLER_NAME");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * HANDLER_NAME: {NotNull, VARCHAR(200)}
     * @param handlerNameList The collection of handlerName as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerName_NotInScope(
            final Collection<String> handlerNameList) {
        doSetHandlerName_NotInScope(handlerNameList);
    }

    public void doSetHandlerName_NotInScope(
            final Collection<String> handlerNameList) {
        regINS(CK_NINS, cTL(handlerNameList), getCValueHandlerName(),
                "HANDLER_NAME");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * HANDLER_NAME: {NotNull, VARCHAR(200)}
     * @param handlerName The value of handlerName as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerName_PrefixSearch(final String handlerName) {
        setHandlerName_LikeSearch(handlerName, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * HANDLER_NAME: {NotNull, VARCHAR(200)} <br />
     * <pre>e.g. setHandlerName_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param handlerName The value of handlerName as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setHandlerName_LikeSearch(final String handlerName,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(handlerName), getCValueHandlerName(),
                "HANDLER_NAME", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * HANDLER_NAME: {NotNull, VARCHAR(200)}
     * @param handlerName The value of handlerName as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setHandlerName_NotLikeSearch(final String handlerName,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(handlerName), getCValueHandlerName(),
                "HANDLER_NAME", likeSearchOption);
    }

    protected void regHandlerName(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueHandlerName(), "HANDLER_NAME");
    }

    abstract protected ConditionValue getCValueHandlerName();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)}
     * @param handlerParameter The value of handlerParameter as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerParameter_Equal(final String handlerParameter) {
        doSetHandlerParameter_Equal(fRES(handlerParameter));
    }

    protected void doSetHandlerParameter_Equal(final String handlerParameter) {
        regHandlerParameter(CK_EQ, handlerParameter);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)}
     * @param handlerParameter The value of handlerParameter as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerParameter_NotEqual(final String handlerParameter) {
        doSetHandlerParameter_NotEqual(fRES(handlerParameter));
    }

    protected void doSetHandlerParameter_NotEqual(final String handlerParameter) {
        regHandlerParameter(CK_NES, handlerParameter);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)}
     * @param handlerParameter The value of handlerParameter as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerParameter_GreaterThan(final String handlerParameter) {
        regHandlerParameter(CK_GT, fRES(handlerParameter));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)}
     * @param handlerParameter The value of handlerParameter as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerParameter_LessThan(final String handlerParameter) {
        regHandlerParameter(CK_LT, fRES(handlerParameter));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)}
     * @param handlerParameter The value of handlerParameter as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerParameter_GreaterEqual(final String handlerParameter) {
        regHandlerParameter(CK_GE, fRES(handlerParameter));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)}
     * @param handlerParameter The value of handlerParameter as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerParameter_LessEqual(final String handlerParameter) {
        regHandlerParameter(CK_LE, fRES(handlerParameter));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)}
     * @param handlerParameterList The collection of handlerParameter as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerParameter_InScope(
            final Collection<String> handlerParameterList) {
        doSetHandlerParameter_InScope(handlerParameterList);
    }

    public void doSetHandlerParameter_InScope(
            final Collection<String> handlerParameterList) {
        regINS(CK_INS, cTL(handlerParameterList), getCValueHandlerParameter(),
                "HANDLER_PARAMETER");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)}
     * @param handlerParameterList The collection of handlerParameter as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerParameter_NotInScope(
            final Collection<String> handlerParameterList) {
        doSetHandlerParameter_NotInScope(handlerParameterList);
    }

    public void doSetHandlerParameter_NotInScope(
            final Collection<String> handlerParameterList) {
        regINS(CK_NINS, cTL(handlerParameterList), getCValueHandlerParameter(),
                "HANDLER_PARAMETER");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)}
     * @param handlerParameter The value of handlerParameter as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerParameter_PrefixSearch(final String handlerParameter) {
        setHandlerParameter_LikeSearch(handlerParameter, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)} <br />
     * <pre>e.g. setHandlerParameter_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param handlerParameter The value of handlerParameter as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setHandlerParameter_LikeSearch(final String handlerParameter,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(handlerParameter), getCValueHandlerParameter(),
                "HANDLER_PARAMETER", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)}
     * @param handlerParameter The value of handlerParameter as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setHandlerParameter_NotLikeSearch(
            final String handlerParameter,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(handlerParameter), getCValueHandlerParameter(),
                "HANDLER_PARAMETER", likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)}
     */
    public void setHandlerParameter_IsNull() {
        regHandlerParameter(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)}
     */
    public void setHandlerParameter_IsNullOrEmpty() {
        regHandlerParameter(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)}
     */
    public void setHandlerParameter_IsNotNull() {
        regHandlerParameter(CK_ISNN, DOBJ);
    }

    protected void regHandlerParameter(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueHandlerParameter(), "HANDLER_PARAMETER");
    }

    abstract protected ConditionValue getCValueHandlerParameter();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)}
     * @param handlerScript The value of handlerScript as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerScript_Equal(final String handlerScript) {
        doSetHandlerScript_Equal(fRES(handlerScript));
    }

    protected void doSetHandlerScript_Equal(final String handlerScript) {
        regHandlerScript(CK_EQ, handlerScript);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)}
     * @param handlerScript The value of handlerScript as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerScript_NotEqual(final String handlerScript) {
        doSetHandlerScript_NotEqual(fRES(handlerScript));
    }

    protected void doSetHandlerScript_NotEqual(final String handlerScript) {
        regHandlerScript(CK_NES, handlerScript);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)}
     * @param handlerScript The value of handlerScript as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerScript_GreaterThan(final String handlerScript) {
        regHandlerScript(CK_GT, fRES(handlerScript));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)}
     * @param handlerScript The value of handlerScript as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerScript_LessThan(final String handlerScript) {
        regHandlerScript(CK_LT, fRES(handlerScript));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)}
     * @param handlerScript The value of handlerScript as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerScript_GreaterEqual(final String handlerScript) {
        regHandlerScript(CK_GE, fRES(handlerScript));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)}
     * @param handlerScript The value of handlerScript as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerScript_LessEqual(final String handlerScript) {
        regHandlerScript(CK_LE, fRES(handlerScript));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)}
     * @param handlerScriptList The collection of handlerScript as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerScript_InScope(
            final Collection<String> handlerScriptList) {
        doSetHandlerScript_InScope(handlerScriptList);
    }

    public void doSetHandlerScript_InScope(
            final Collection<String> handlerScriptList) {
        regINS(CK_INS, cTL(handlerScriptList), getCValueHandlerScript(),
                "HANDLER_SCRIPT");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)}
     * @param handlerScriptList The collection of handlerScript as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerScript_NotInScope(
            final Collection<String> handlerScriptList) {
        doSetHandlerScript_NotInScope(handlerScriptList);
    }

    public void doSetHandlerScript_NotInScope(
            final Collection<String> handlerScriptList) {
        regINS(CK_NINS, cTL(handlerScriptList), getCValueHandlerScript(),
                "HANDLER_SCRIPT");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)}
     * @param handlerScript The value of handlerScript as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setHandlerScript_PrefixSearch(final String handlerScript) {
        setHandlerScript_LikeSearch(handlerScript, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)} <br />
     * <pre>e.g. setHandlerScript_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param handlerScript The value of handlerScript as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setHandlerScript_LikeSearch(final String handlerScript,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(handlerScript), getCValueHandlerScript(),
                "HANDLER_SCRIPT", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)}
     * @param handlerScript The value of handlerScript as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setHandlerScript_NotLikeSearch(final String handlerScript,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(handlerScript), getCValueHandlerScript(),
                "HANDLER_SCRIPT", likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)}
     */
    public void setHandlerScript_IsNull() {
        regHandlerScript(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)}
     */
    public void setHandlerScript_IsNullOrEmpty() {
        regHandlerScript(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)}
     */
    public void setHandlerScript_IsNotNull() {
        regHandlerScript(CK_ISNN, DOBJ);
    }

    protected void regHandlerScript(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueHandlerScript(), "HANDLER_SCRIPT");
    }

    abstract protected ConditionValue getCValueHandlerScript();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boost The value of boost as equal. (NullAllowed: if null, no condition)
     */
    public void setBoost_Equal(final java.math.BigDecimal boost) {
        doSetBoost_Equal(boost);
    }

    protected void doSetBoost_Equal(final java.math.BigDecimal boost) {
        regBoost(CK_EQ, boost);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boost The value of boost as notEqual. (NullAllowed: if null, no condition)
     */
    public void setBoost_NotEqual(final java.math.BigDecimal boost) {
        doSetBoost_NotEqual(boost);
    }

    protected void doSetBoost_NotEqual(final java.math.BigDecimal boost) {
        regBoost(CK_NES, boost);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boost The value of boost as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setBoost_GreaterThan(final java.math.BigDecimal boost) {
        regBoost(CK_GT, boost);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boost The value of boost as lessThan. (NullAllowed: if null, no condition)
     */
    public void setBoost_LessThan(final java.math.BigDecimal boost) {
        regBoost(CK_LT, boost);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boost The value of boost as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setBoost_GreaterEqual(final java.math.BigDecimal boost) {
        regBoost(CK_GE, boost);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boost The value of boost as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setBoost_LessEqual(final java.math.BigDecimal boost) {
        regBoost(CK_LE, boost);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param minNumber The min number of boost. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of boost. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setBoost_RangeOf(final java.math.BigDecimal minNumber,
            final java.math.BigDecimal maxNumber,
            final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueBoost(), "BOOST", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boostList The collection of boost as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setBoost_InScope(
            final Collection<java.math.BigDecimal> boostList) {
        doSetBoost_InScope(boostList);
    }

    protected void doSetBoost_InScope(
            final Collection<java.math.BigDecimal> boostList) {
        regINS(CK_INS, cTL(boostList), getCValueBoost(), "BOOST");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @param boostList The collection of boost as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setBoost_NotInScope(
            final Collection<java.math.BigDecimal> boostList) {
        doSetBoost_NotInScope(boostList);
    }

    protected void doSetBoost_NotInScope(
            final Collection<java.math.BigDecimal> boostList) {
        regINS(CK_NINS, cTL(boostList), getCValueBoost(), "BOOST");
    }

    protected void regBoost(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueBoost(), "BOOST");
    }

    abstract protected ConditionValue getCValueBoost();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_Equal(final String available) {
        doSetAvailable_Equal(fRES(available));
    }

    protected void doSetAvailable_Equal(final String available) {
        regAvailable(CK_EQ, available);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_NotEqual(final String available) {
        doSetAvailable_NotEqual(fRES(available));
    }

    protected void doSetAvailable_NotEqual(final String available) {
        regAvailable(CK_NES, available);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_GreaterThan(final String available) {
        regAvailable(CK_GT, fRES(available));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_LessThan(final String available) {
        regAvailable(CK_LT, fRES(available));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_GreaterEqual(final String available) {
        regAvailable(CK_GE, fRES(available));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_LessEqual(final String available) {
        regAvailable(CK_LE, fRES(available));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param availableList The collection of available as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_InScope(final Collection<String> availableList) {
        doSetAvailable_InScope(availableList);
    }

    public void doSetAvailable_InScope(final Collection<String> availableList) {
        regINS(CK_INS, cTL(availableList), getCValueAvailable(), "AVAILABLE");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param availableList The collection of available as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_NotInScope(final Collection<String> availableList) {
        doSetAvailable_NotInScope(availableList);
    }

    public void doSetAvailable_NotInScope(final Collection<String> availableList) {
        regINS(CK_NINS, cTL(availableList), getCValueAvailable(), "AVAILABLE");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setAvailable_PrefixSearch(final String available) {
        setAvailable_LikeSearch(available, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)} <br />
     * <pre>e.g. setAvailable_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param available The value of available as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setAvailable_LikeSearch(final String available,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(available), getCValueAvailable(), "AVAILABLE",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @param available The value of available as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setAvailable_NotLikeSearch(final String available,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(available), getCValueAvailable(), "AVAILABLE",
                likeSearchOption);
    }

    protected void regAvailable(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueAvailable(), "AVAILABLE");
    }

    abstract protected ConditionValue getCValueAvailable();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrder The value of sortOrder as equal. (NullAllowed: if null, no condition)
     */
    public void setSortOrder_Equal(final Integer sortOrder) {
        doSetSortOrder_Equal(sortOrder);
    }

    protected void doSetSortOrder_Equal(final Integer sortOrder) {
        regSortOrder(CK_EQ, sortOrder);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrder The value of sortOrder as notEqual. (NullAllowed: if null, no condition)
     */
    public void setSortOrder_NotEqual(final Integer sortOrder) {
        doSetSortOrder_NotEqual(sortOrder);
    }

    protected void doSetSortOrder_NotEqual(final Integer sortOrder) {
        regSortOrder(CK_NES, sortOrder);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrder The value of sortOrder as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setSortOrder_GreaterThan(final Integer sortOrder) {
        regSortOrder(CK_GT, sortOrder);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrder The value of sortOrder as lessThan. (NullAllowed: if null, no condition)
     */
    public void setSortOrder_LessThan(final Integer sortOrder) {
        regSortOrder(CK_LT, sortOrder);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrder The value of sortOrder as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setSortOrder_GreaterEqual(final Integer sortOrder) {
        regSortOrder(CK_GE, sortOrder);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrder The value of sortOrder as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setSortOrder_LessEqual(final Integer sortOrder) {
        regSortOrder(CK_LE, sortOrder);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param minNumber The min number of sortOrder. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of sortOrder. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setSortOrder_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueSortOrder(), "SORT_ORDER",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrderList The collection of sortOrder as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setSortOrder_InScope(final Collection<Integer> sortOrderList) {
        doSetSortOrder_InScope(sortOrderList);
    }

    protected void doSetSortOrder_InScope(
            final Collection<Integer> sortOrderList) {
        regINS(CK_INS, cTL(sortOrderList), getCValueSortOrder(), "SORT_ORDER");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @param sortOrderList The collection of sortOrder as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setSortOrder_NotInScope(final Collection<Integer> sortOrderList) {
        doSetSortOrder_NotInScope(sortOrderList);
    }

    protected void doSetSortOrder_NotInScope(
            final Collection<Integer> sortOrderList) {
        regINS(CK_NINS, cTL(sortOrderList), getCValueSortOrder(), "SORT_ORDER");
    }

    protected void regSortOrder(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueSortOrder(), "SORT_ORDER");
    }

    abstract protected ConditionValue getCValueSortOrder();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_Equal(final String createdBy) {
        doSetCreatedBy_Equal(fRES(createdBy));
    }

    protected void doSetCreatedBy_Equal(final String createdBy) {
        regCreatedBy(CK_EQ, createdBy);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_NotEqual(final String createdBy) {
        doSetCreatedBy_NotEqual(fRES(createdBy));
    }

    protected void doSetCreatedBy_NotEqual(final String createdBy) {
        regCreatedBy(CK_NES, createdBy);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_GreaterThan(final String createdBy) {
        regCreatedBy(CK_GT, fRES(createdBy));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_LessThan(final String createdBy) {
        regCreatedBy(CK_LT, fRES(createdBy));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_GreaterEqual(final String createdBy) {
        regCreatedBy(CK_GE, fRES(createdBy));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_LessEqual(final String createdBy) {
        regCreatedBy(CK_LE, fRES(createdBy));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdByList The collection of createdBy as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_InScope(final Collection<String> createdByList) {
        doSetCreatedBy_InScope(createdByList);
    }

    public void doSetCreatedBy_InScope(final Collection<String> createdByList) {
        regINS(CK_INS, cTL(createdByList), getCValueCreatedBy(), "CREATED_BY");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdByList The collection of createdBy as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_NotInScope(final Collection<String> createdByList) {
        doSetCreatedBy_NotInScope(createdByList);
    }

    public void doSetCreatedBy_NotInScope(final Collection<String> createdByList) {
        regINS(CK_NINS, cTL(createdByList), getCValueCreatedBy(), "CREATED_BY");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setCreatedBy_PrefixSearch(final String createdBy) {
        setCreatedBy_LikeSearch(createdBy, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)} <br />
     * <pre>e.g. setCreatedBy_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param createdBy The value of createdBy as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setCreatedBy_LikeSearch(final String createdBy,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(createdBy), getCValueCreatedBy(), "CREATED_BY",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @param createdBy The value of createdBy as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setCreatedBy_NotLikeSearch(final String createdBy,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(createdBy), getCValueCreatedBy(), "CREATED_BY",
                likeSearchOption);
    }

    protected void regCreatedBy(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueCreatedBy(), "CREATED_BY");
    }

    abstract protected ConditionValue getCValueCreatedBy();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param createdTime The value of createdTime as equal. (NullAllowed: if null, no condition)
     */
    public void setCreatedTime_Equal(final java.sql.Timestamp createdTime) {
        regCreatedTime(CK_EQ, createdTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param createdTime The value of createdTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setCreatedTime_GreaterThan(final java.sql.Timestamp createdTime) {
        regCreatedTime(CK_GT, createdTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param createdTime The value of createdTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setCreatedTime_LessThan(final java.sql.Timestamp createdTime) {
        regCreatedTime(CK_LT, createdTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param createdTime The value of createdTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setCreatedTime_GreaterEqual(final java.sql.Timestamp createdTime) {
        regCreatedTime(CK_GE, createdTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @param createdTime The value of createdTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setCreatedTime_LessEqual(final java.sql.Timestamp createdTime) {
        regCreatedTime(CK_LE, createdTime);
    }

    /**
     * FromTo with various options. (versatile) {(default) fromDatetime &lt;= column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * <pre>e.g. setCreatedTime_FromTo(fromDate, toDate, new <span style="color: #FD4747">FromToOption</span>().compareAsDate());</pre>
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of createdTime. (NullAllowed: if null, no from-condition)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of createdTime. (NullAllowed: if null, no to-condition)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setCreatedTime_FromTo(final java.util.Date fromDatetime,
            final java.util.Date toDatetime, final FromToOption fromToOption) {
        regFTQ(fromDatetime != null ? new java.sql.Timestamp(
                fromDatetime.getTime()) : null,
                toDatetime != null ? new java.sql.Timestamp(toDatetime
                        .getTime()) : null, getCValueCreatedTime(),
                "CREATED_TIME", fromToOption);
    }

    /**
     * DateFromTo. (Date means yyyy/MM/dd) {fromDate &lt;= column &lt; toDate + 1 day} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #FD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of createdTime. (NullAllowed: if null, no from-condition)
     * @param toDate The to-date(yyyy/MM/dd) of createdTime. (NullAllowed: if null, no to-condition)
     */
    public void setCreatedTime_DateFromTo(final java.util.Date fromDate,
            final java.util.Date toDate) {
        setCreatedTime_FromTo(fromDate, toDate,
                new FromToOption().compareAsDate());
    }

    protected void regCreatedTime(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueCreatedTime(), "CREATED_TIME");
    }

    abstract protected ConditionValue getCValueCreatedTime();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_Equal(final String updatedBy) {
        doSetUpdatedBy_Equal(fRES(updatedBy));
    }

    protected void doSetUpdatedBy_Equal(final String updatedBy) {
        regUpdatedBy(CK_EQ, updatedBy);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_NotEqual(final String updatedBy) {
        doSetUpdatedBy_NotEqual(fRES(updatedBy));
    }

    protected void doSetUpdatedBy_NotEqual(final String updatedBy) {
        regUpdatedBy(CK_NES, updatedBy);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_GreaterThan(final String updatedBy) {
        regUpdatedBy(CK_GT, fRES(updatedBy));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_LessThan(final String updatedBy) {
        regUpdatedBy(CK_LT, fRES(updatedBy));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_GreaterEqual(final String updatedBy) {
        regUpdatedBy(CK_GE, fRES(updatedBy));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_LessEqual(final String updatedBy) {
        regUpdatedBy(CK_LE, fRES(updatedBy));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedByList The collection of updatedBy as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_InScope(final Collection<String> updatedByList) {
        doSetUpdatedBy_InScope(updatedByList);
    }

    public void doSetUpdatedBy_InScope(final Collection<String> updatedByList) {
        regINS(CK_INS, cTL(updatedByList), getCValueUpdatedBy(), "UPDATED_BY");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedByList The collection of updatedBy as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_NotInScope(final Collection<String> updatedByList) {
        doSetUpdatedBy_NotInScope(updatedByList);
    }

    public void doSetUpdatedBy_NotInScope(final Collection<String> updatedByList) {
        regINS(CK_NINS, cTL(updatedByList), getCValueUpdatedBy(), "UPDATED_BY");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setUpdatedBy_PrefixSearch(final String updatedBy) {
        setUpdatedBy_LikeSearch(updatedBy, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)} <br />
     * <pre>e.g. setUpdatedBy_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param updatedBy The value of updatedBy as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setUpdatedBy_LikeSearch(final String updatedBy,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(updatedBy), getCValueUpdatedBy(), "UPDATED_BY",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @param updatedBy The value of updatedBy as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setUpdatedBy_NotLikeSearch(final String updatedBy,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(updatedBy), getCValueUpdatedBy(), "UPDATED_BY",
                likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     */
    public void setUpdatedBy_IsNull() {
        regUpdatedBy(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     */
    public void setUpdatedBy_IsNullOrEmpty() {
        regUpdatedBy(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * UPDATED_BY: {VARCHAR(255)}
     */
    public void setUpdatedBy_IsNotNull() {
        regUpdatedBy(CK_ISNN, DOBJ);
    }

    protected void regUpdatedBy(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueUpdatedBy(), "UPDATED_BY");
    }

    abstract protected ConditionValue getCValueUpdatedBy();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @param updatedTime The value of updatedTime as equal. (NullAllowed: if null, no condition)
     */
    public void setUpdatedTime_Equal(final java.sql.Timestamp updatedTime) {
        regUpdatedTime(CK_EQ, updatedTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @param updatedTime The value of updatedTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setUpdatedTime_GreaterThan(final java.sql.Timestamp updatedTime) {
        regUpdatedTime(CK_GT, updatedTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @param updatedTime The value of updatedTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setUpdatedTime_LessThan(final java.sql.Timestamp updatedTime) {
        regUpdatedTime(CK_LT, updatedTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @param updatedTime The value of updatedTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setUpdatedTime_GreaterEqual(final java.sql.Timestamp updatedTime) {
        regUpdatedTime(CK_GE, updatedTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @param updatedTime The value of updatedTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setUpdatedTime_LessEqual(final java.sql.Timestamp updatedTime) {
        regUpdatedTime(CK_LE, updatedTime);
    }

    /**
     * FromTo with various options. (versatile) {(default) fromDatetime &lt;= column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * <pre>e.g. setUpdatedTime_FromTo(fromDate, toDate, new <span style="color: #FD4747">FromToOption</span>().compareAsDate());</pre>
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of updatedTime. (NullAllowed: if null, no from-condition)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of updatedTime. (NullAllowed: if null, no to-condition)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setUpdatedTime_FromTo(final java.util.Date fromDatetime,
            final java.util.Date toDatetime, final FromToOption fromToOption) {
        regFTQ(fromDatetime != null ? new java.sql.Timestamp(
                fromDatetime.getTime()) : null,
                toDatetime != null ? new java.sql.Timestamp(toDatetime
                        .getTime()) : null, getCValueUpdatedTime(),
                "UPDATED_TIME", fromToOption);
    }

    /**
     * DateFromTo. (Date means yyyy/MM/dd) {fromDate &lt;= column &lt; toDate + 1 day} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #FD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of updatedTime. (NullAllowed: if null, no from-condition)
     * @param toDate The to-date(yyyy/MM/dd) of updatedTime. (NullAllowed: if null, no to-condition)
     */
    public void setUpdatedTime_DateFromTo(final java.util.Date fromDate,
            final java.util.Date toDate) {
        setUpdatedTime_FromTo(fromDate, toDate,
                new FromToOption().compareAsDate());
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     */
    public void setUpdatedTime_IsNull() {
        regUpdatedTime(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     */
    public void setUpdatedTime_IsNotNull() {
        regUpdatedTime(CK_ISNN, DOBJ);
    }

    protected void regUpdatedTime(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueUpdatedTime(), "UPDATED_TIME");
    }

    abstract protected ConditionValue getCValueUpdatedTime();

    /**
     * Equal(=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_Equal(final String deletedBy) {
        doSetDeletedBy_Equal(fRES(deletedBy));
    }

    protected void doSetDeletedBy_Equal(final String deletedBy) {
        regDeletedBy(CK_EQ, deletedBy);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_NotEqual(final String deletedBy) {
        doSetDeletedBy_NotEqual(fRES(deletedBy));
    }

    protected void doSetDeletedBy_NotEqual(final String deletedBy) {
        regDeletedBy(CK_NES, deletedBy);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_GreaterThan(final String deletedBy) {
        regDeletedBy(CK_GT, fRES(deletedBy));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_LessThan(final String deletedBy) {
        regDeletedBy(CK_LT, fRES(deletedBy));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_GreaterEqual(final String deletedBy) {
        regDeletedBy(CK_GE, fRES(deletedBy));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_LessEqual(final String deletedBy) {
        regDeletedBy(CK_LE, fRES(deletedBy));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedByList The collection of deletedBy as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_InScope(final Collection<String> deletedByList) {
        doSetDeletedBy_InScope(deletedByList);
    }

    public void doSetDeletedBy_InScope(final Collection<String> deletedByList) {
        regINS(CK_INS, cTL(deletedByList), getCValueDeletedBy(), "DELETED_BY");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedByList The collection of deletedBy as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_NotInScope(final Collection<String> deletedByList) {
        doSetDeletedBy_NotInScope(deletedByList);
    }

    public void doSetDeletedBy_NotInScope(final Collection<String> deletedByList) {
        regINS(CK_NINS, cTL(deletedByList), getCValueDeletedBy(), "DELETED_BY");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setDeletedBy_PrefixSearch(final String deletedBy) {
        setDeletedBy_LikeSearch(deletedBy, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * DELETED_BY: {VARCHAR(255)} <br />
     * <pre>e.g. setDeletedBy_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param deletedBy The value of deletedBy as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setDeletedBy_LikeSearch(final String deletedBy,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(deletedBy), getCValueDeletedBy(), "DELETED_BY",
                likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @param deletedBy The value of deletedBy as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setDeletedBy_NotLikeSearch(final String deletedBy,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(deletedBy), getCValueDeletedBy(), "DELETED_BY",
                likeSearchOption);
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     */
    public void setDeletedBy_IsNull() {
        regDeletedBy(CK_ISN, DOBJ);
    }

    /**
     * IsNullOrEmpty {is null or empty}. And OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     */
    public void setDeletedBy_IsNullOrEmpty() {
        regDeletedBy(CK_ISNOE, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * DELETED_BY: {VARCHAR(255)}
     */
    public void setDeletedBy_IsNotNull() {
        regDeletedBy(CK_ISNN, DOBJ);
    }

    protected void regDeletedBy(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueDeletedBy(), "DELETED_BY");
    }

    abstract protected ConditionValue getCValueDeletedBy();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @param deletedTime The value of deletedTime as equal. (NullAllowed: if null, no condition)
     */
    public void setDeletedTime_Equal(final java.sql.Timestamp deletedTime) {
        regDeletedTime(CK_EQ, deletedTime);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @param deletedTime The value of deletedTime as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setDeletedTime_GreaterThan(final java.sql.Timestamp deletedTime) {
        regDeletedTime(CK_GT, deletedTime);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @param deletedTime The value of deletedTime as lessThan. (NullAllowed: if null, no condition)
     */
    public void setDeletedTime_LessThan(final java.sql.Timestamp deletedTime) {
        regDeletedTime(CK_LT, deletedTime);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @param deletedTime The value of deletedTime as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setDeletedTime_GreaterEqual(final java.sql.Timestamp deletedTime) {
        regDeletedTime(CK_GE, deletedTime);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @param deletedTime The value of deletedTime as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setDeletedTime_LessEqual(final java.sql.Timestamp deletedTime) {
        regDeletedTime(CK_LE, deletedTime);
    }

    /**
     * FromTo with various options. (versatile) {(default) fromDatetime &lt;= column &lt;= toDatetime} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * <pre>e.g. setDeletedTime_FromTo(fromDate, toDate, new <span style="color: #FD4747">FromToOption</span>().compareAsDate());</pre>
     * @param fromDatetime The from-datetime(yyyy/MM/dd HH:mm:ss.SSS) of deletedTime. (NullAllowed: if null, no from-condition)
     * @param toDatetime The to-datetime(yyyy/MM/dd HH:mm:ss.SSS) of deletedTime. (NullAllowed: if null, no to-condition)
     * @param fromToOption The option of from-to. (NotNull)
     */
    public void setDeletedTime_FromTo(final java.util.Date fromDatetime,
            final java.util.Date toDatetime, final FromToOption fromToOption) {
        regFTQ(fromDatetime != null ? new java.sql.Timestamp(
                fromDatetime.getTime()) : null,
                toDatetime != null ? new java.sql.Timestamp(toDatetime
                        .getTime()) : null, getCValueDeletedTime(),
                "DELETED_TIME", fromToOption);
    }

    /**
     * DateFromTo. (Date means yyyy/MM/dd) {fromDate &lt;= column &lt; toDate + 1 day} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * <pre>
     * e.g. from:{2007/04/10 08:24:53} to:{2007/04/16 14:36:29}
     *  column &gt;= '2007/04/10 00:00:00' and column <span style="color: #FD4747">&lt; '2007/04/17 00:00:00'</span>
     * </pre>
     * @param fromDate The from-date(yyyy/MM/dd) of deletedTime. (NullAllowed: if null, no from-condition)
     * @param toDate The to-date(yyyy/MM/dd) of deletedTime. (NullAllowed: if null, no to-condition)
     */
    public void setDeletedTime_DateFromTo(final java.util.Date fromDate,
            final java.util.Date toDate) {
        setDeletedTime_FromTo(fromDate, toDate,
                new FromToOption().compareAsDate());
    }

    /**
     * IsNull {is null}. And OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     */
    public void setDeletedTime_IsNull() {
        regDeletedTime(CK_ISN, DOBJ);
    }

    /**
     * IsNotNull {is not null}. And OnlyOnceRegistered. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     */
    public void setDeletedTime_IsNotNull() {
        regDeletedTime(CK_ISNN, DOBJ);
    }

    protected void regDeletedTime(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueDeletedTime(), "DELETED_TIME");
    }

    abstract protected ConditionValue getCValueDeletedTime();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNo The value of versionNo as equal. (NullAllowed: if null, no condition)
     */
    public void setVersionNo_Equal(final Integer versionNo) {
        doSetVersionNo_Equal(versionNo);
    }

    protected void doSetVersionNo_Equal(final Integer versionNo) {
        regVersionNo(CK_EQ, versionNo);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNo The value of versionNo as notEqual. (NullAllowed: if null, no condition)
     */
    public void setVersionNo_NotEqual(final Integer versionNo) {
        doSetVersionNo_NotEqual(versionNo);
    }

    protected void doSetVersionNo_NotEqual(final Integer versionNo) {
        regVersionNo(CK_NES, versionNo);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNo The value of versionNo as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setVersionNo_GreaterThan(final Integer versionNo) {
        regVersionNo(CK_GT, versionNo);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNo The value of versionNo as lessThan. (NullAllowed: if null, no condition)
     */
    public void setVersionNo_LessThan(final Integer versionNo) {
        regVersionNo(CK_LT, versionNo);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNo The value of versionNo as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setVersionNo_GreaterEqual(final Integer versionNo) {
        regVersionNo(CK_GE, versionNo);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNo The value of versionNo as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setVersionNo_LessEqual(final Integer versionNo) {
        regVersionNo(CK_LE, versionNo);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param minNumber The min number of versionNo. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of versionNo. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setVersionNo_RangeOf(final Integer minNumber,
            final Integer maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueVersionNo(), "VERSION_NO",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNoList The collection of versionNo as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setVersionNo_InScope(final Collection<Integer> versionNoList) {
        doSetVersionNo_InScope(versionNoList);
    }

    protected void doSetVersionNo_InScope(
            final Collection<Integer> versionNoList) {
        regINS(CK_INS, cTL(versionNoList), getCValueVersionNo(), "VERSION_NO");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @param versionNoList The collection of versionNo as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setVersionNo_NotInScope(final Collection<Integer> versionNoList) {
        doSetVersionNo_NotInScope(versionNoList);
    }

    protected void doSetVersionNo_NotInScope(
            final Collection<Integer> versionNoList) {
        regINS(CK_NINS, cTL(versionNoList), getCValueVersionNo(), "VERSION_NO");
    }

    protected void regVersionNo(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueVersionNo(), "VERSION_NO");
    }

    abstract protected ConditionValue getCValueVersionNo();

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_Equal()</span>.max(new SubQuery&lt;DataCrawlingConfigCB&gt;() {
     *     public void query(DataCrawlingConfigCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<DataCrawlingConfigCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ.getOperand());
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;DataCrawlingConfigCB&gt;() {
     *     public void query(DataCrawlingConfigCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<DataCrawlingConfigCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;DataCrawlingConfigCB&gt;() {
     *     public void query(DataCrawlingConfigCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<DataCrawlingConfigCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessThan()</span>.max(new SubQuery&lt;DataCrawlingConfigCB&gt;() {
     *     public void query(DataCrawlingConfigCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<DataCrawlingConfigCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;DataCrawlingConfigCB&gt;() {
     *     public void query(DataCrawlingConfigCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<DataCrawlingConfigCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;DataCrawlingConfigCB&gt;() {
     *     public void query(DataCrawlingConfigCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<DataCrawlingConfigCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE.getOperand());
    }

    protected HpSSQFunction<DataCrawlingConfigCB> xcreateSSQFunction(
            final String operand) {
        return new HpSSQFunction<DataCrawlingConfigCB>(
                new HpSSQSetupper<DataCrawlingConfigCB>() {
                    @Override
                    public void setup(final String function,
                            final SubQuery<DataCrawlingConfigCB> subQuery,
                            final HpSSQOption<DataCrawlingConfigCB> option) {
                        xscalarCondition(function, subQuery, operand, option);
                    }
                });
    }

    protected void xscalarCondition(final String function,
            final SubQuery<DataCrawlingConfigCB> subQuery,
            final String operand, final HpSSQOption<DataCrawlingConfigCB> option) {
        assertObjectNotNull("subQuery<DataCrawlingConfigCB>", subQuery);
        final DataCrawlingConfigCB cb = xcreateScalarConditionCB();
        subQuery.query(cb);
        final String subQueryPropertyName = keepScalarCondition(cb.query()); // for saving query-value
        option.setPartitionByCBean(xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(function, cb.query(), subQueryPropertyName,
                operand, option);
    }

    public abstract String keepScalarCondition(DataCrawlingConfigCQ subQuery);

    protected DataCrawlingConfigCB xcreateScalarConditionCB() {
        final DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected DataCrawlingConfigCB xcreateScalarConditionPartitionByCB() {
        final DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String function,
            final SubQuery<DataCrawlingConfigCB> subQuery,
            final String aliasName, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<DataCrawlingConfigCB>", subQuery);
        final DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepSpecifyMyselfDerived(cb.query()); // for saving query-value.
        registerSpecifyMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", aliasName, option);
    }

    public abstract String keepSpecifyMyselfDerived(
            DataCrawlingConfigCQ subQuery);

    /**
     * Prepare for (Query)MyselfDerived (SubQuery).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<DataCrawlingConfigCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived();
    }

    protected HpQDRFunction<DataCrawlingConfigCB> xcreateQDRFunctionMyselfDerived() {
        return new HpQDRFunction<DataCrawlingConfigCB>(
                new HpQDRSetupper<DataCrawlingConfigCB>() {
                    @Override
                    public void setup(final String function,
                            final SubQuery<DataCrawlingConfigCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveMyselfDerived(function, subQuery, operand,
                                value, option);
                    }
                });
    }

    public void xqderiveMyselfDerived(final String function,
            final SubQuery<DataCrawlingConfigCB> subQuery,
            final String operand, final Object value,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<DataCrawlingConfigCB>", subQuery);
        final DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String parameterPropertyName = keepQueryMyselfDerivedParameter(value);
        registerQueryMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepQueryMyselfDerived(DataCrawlingConfigCQ subQuery);

    public abstract String keepQueryMyselfDerivedParameter(Object parameterValue);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfExists(final SubQuery<DataCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery<DataCrawlingConfigCB>", subQuery);
        final DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
        cb.xsetupForMyselfExists(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfExists(cb.query()); // for saving query-value.
        registerMyselfExists(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfExists(DataCrawlingConfigCQ subQuery);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfInScope(final SubQuery<DataCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery<DataCrawlingConfigCB>", subQuery);
        final DataCrawlingConfigCB cb = new DataCrawlingConfigCB();
        cb.xsetupForMyselfInScope(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfInScope(cb.query()); // for saving query-value.
        registerMyselfInScope(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfInScope(DataCrawlingConfigCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCB() {
        return DataCrawlingConfigCB.class.getName();
    }

    protected String xabCQ() {
        return DataCrawlingConfigCQ.class.getName();
    }

    protected String xabLSO() {
        return LikeSearchOption.class.getName();
    }

    protected String xabSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
