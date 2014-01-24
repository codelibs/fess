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
import jp.sf.fess.db.cbean.DataConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.FileConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.LabelTypeToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.RoleTypeCB;
import jp.sf.fess.db.cbean.WebConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.cq.DataConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.LabelTypeToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.RoleTypeCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToRoleTypeMappingCQ;

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
 * The abstract condition-query of ROLE_TYPE.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsRoleTypeCQ extends AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsRoleTypeCQ(final ConditionQuery childQuery,
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
        return "ROLE_TYPE";
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
     * {exists (select ROLE_TYPE_ID from DATA_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'dataConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsDataConfigToRoleTypeMappingList</span>(new SubQuery&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void query(RoleTypeCB subCB) {
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
        registerExistsReferrer(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "dataConfigToRoleTypeMappingList");
    }

    public abstract String keepId_ExistsReferrer_DataConfigToRoleTypeMappingList(
            DataConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select ROLE_TYPE_ID from FILE_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'fileConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsFileConfigToRoleTypeMappingList</span>(new SubQuery&lt;FileConfigToRoleTypeMappingCB&gt;() {
     *     public void query(RoleTypeCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of FileConfigToRoleTypeMappingList for 'exists'. (NotNull)
     */
    public void existsFileConfigToRoleTypeMappingList(
            final SubQuery<FileConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<FileConfigToRoleTypeMappingCB>", subQuery);
        final FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_FileConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "fileConfigToRoleTypeMappingList");
    }

    public abstract String keepId_ExistsReferrer_FileConfigToRoleTypeMappingList(
            FileConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select ROLE_TYPE_ID from LABEL_TYPE_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * LABEL_TYPE_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'labelTypeToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsLabelTypeToRoleTypeMappingList</span>(new SubQuery&lt;LabelTypeToRoleTypeMappingCB&gt;() {
     *     public void query(RoleTypeCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of LabelTypeToRoleTypeMappingList for 'exists'. (NotNull)
     */
    public void existsLabelTypeToRoleTypeMappingList(
            final SubQuery<LabelTypeToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<LabelTypeToRoleTypeMappingCB>", subQuery);
        final LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_LabelTypeToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "labelTypeToRoleTypeMappingList");
    }

    public abstract String keepId_ExistsReferrer_LabelTypeToRoleTypeMappingList(
            LabelTypeToRoleTypeMappingCQ subQuery);

    /**
     * Set up ExistsReferrer (co-related sub-query). <br />
     * {exists (select ROLE_TYPE_ID from WEB_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'webConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">existsWebConfigToRoleTypeMappingList</span>(new SubQuery&lt;WebConfigToRoleTypeMappingCB&gt;() {
     *     public void query(RoleTypeCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of WebConfigToRoleTypeMappingList for 'exists'. (NotNull)
     */
    public void existsWebConfigToRoleTypeMappingList(
            final SubQuery<WebConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToRoleTypeMappingCB>", subQuery);
        final WebConfigToRoleTypeMappingCB cb = new WebConfigToRoleTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_ExistsReferrer_WebConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerExistsReferrer(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "webConfigToRoleTypeMappingList");
    }

    public abstract String keepId_ExistsReferrer_WebConfigToRoleTypeMappingList(
            WebConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select ROLE_TYPE_ID from DATA_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'dataConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsDataConfigToRoleTypeMappingList</span>(new SubQuery&lt;DataConfigToRoleTypeMappingCB&gt;() {
     *     public void query(RoleTypeCB subCB) {
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
        registerNotExistsReferrer(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "dataConfigToRoleTypeMappingList");
    }

    public abstract String keepId_NotExistsReferrer_DataConfigToRoleTypeMappingList(
            DataConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select ROLE_TYPE_ID from FILE_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'fileConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsFileConfigToRoleTypeMappingList</span>(new SubQuery&lt;FileConfigToRoleTypeMappingCB&gt;() {
     *     public void query(RoleTypeCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_FileConfigToRoleTypeMappingList for 'not exists'. (NotNull)
     */
    public void notExistsFileConfigToRoleTypeMappingList(
            final SubQuery<FileConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<FileConfigToRoleTypeMappingCB>", subQuery);
        final FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_FileConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "fileConfigToRoleTypeMappingList");
    }

    public abstract String keepId_NotExistsReferrer_FileConfigToRoleTypeMappingList(
            FileConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select ROLE_TYPE_ID from LABEL_TYPE_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * LABEL_TYPE_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'labelTypeToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsLabelTypeToRoleTypeMappingList</span>(new SubQuery&lt;LabelTypeToRoleTypeMappingCB&gt;() {
     *     public void query(RoleTypeCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_LabelTypeToRoleTypeMappingList for 'not exists'. (NotNull)
     */
    public void notExistsLabelTypeToRoleTypeMappingList(
            final SubQuery<LabelTypeToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<LabelTypeToRoleTypeMappingCB>", subQuery);
        final LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_LabelTypeToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "labelTypeToRoleTypeMappingList");
    }

    public abstract String keepId_NotExistsReferrer_LabelTypeToRoleTypeMappingList(
            LabelTypeToRoleTypeMappingCQ subQuery);

    /**
     * Set up NotExistsReferrer (co-related sub-query). <br />
     * {not exists (select ROLE_TYPE_ID from WEB_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'webConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">notExistsWebConfigToRoleTypeMappingList</span>(new SubQuery&lt;WebConfigToRoleTypeMappingCB&gt;() {
     *     public void query(RoleTypeCB subCB) {
     *         subCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param subQuery The sub-query of Id_NotExistsReferrer_WebConfigToRoleTypeMappingList for 'not exists'. (NotNull)
     */
    public void notExistsWebConfigToRoleTypeMappingList(
            final SubQuery<WebConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToRoleTypeMappingCB>", subQuery);
        final WebConfigToRoleTypeMappingCB cb = new WebConfigToRoleTypeMappingCB();
        cb.xsetupForExistsReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotExistsReferrer_WebConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotExistsReferrer(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "webConfigToRoleTypeMappingList");
    }

    public abstract String keepId_NotExistsReferrer_WebConfigToRoleTypeMappingList(
            WebConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select ROLE_TYPE_ID from DATA_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'dataConfigToRoleTypeMappingAsOne'.
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
        registerInScopeRelation(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "dataConfigToRoleTypeMappingList");
    }

    public abstract String keepId_InScopeRelation_DataConfigToRoleTypeMappingList(
            DataConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select ROLE_TYPE_ID from FILE_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'fileConfigToRoleTypeMappingAsOne'.
     * @param subQuery The sub-query of FileConfigToRoleTypeMappingList for 'in-scope'. (NotNull)
     */
    public void inScopeFileConfigToRoleTypeMappingList(
            final SubQuery<FileConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<FileConfigToRoleTypeMappingCB>", subQuery);
        final FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_FileConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "fileConfigToRoleTypeMappingList");
    }

    public abstract String keepId_InScopeRelation_FileConfigToRoleTypeMappingList(
            FileConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select ROLE_TYPE_ID from LABEL_TYPE_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * LABEL_TYPE_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'labelTypeToRoleTypeMappingAsOne'.
     * @param subQuery The sub-query of LabelTypeToRoleTypeMappingList for 'in-scope'. (NotNull)
     */
    public void inScopeLabelTypeToRoleTypeMappingList(
            final SubQuery<LabelTypeToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<LabelTypeToRoleTypeMappingCB>", subQuery);
        final LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_LabelTypeToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "labelTypeToRoleTypeMappingList");
    }

    public abstract String keepId_InScopeRelation_LabelTypeToRoleTypeMappingList(
            LabelTypeToRoleTypeMappingCQ subQuery);

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select ROLE_TYPE_ID from WEB_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'webConfigToRoleTypeMappingAsOne'.
     * @param subQuery The sub-query of WebConfigToRoleTypeMappingList for 'in-scope'. (NotNull)
     */
    public void inScopeWebConfigToRoleTypeMappingList(
            final SubQuery<WebConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToRoleTypeMappingCB>", subQuery);
        final WebConfigToRoleTypeMappingCB cb = new WebConfigToRoleTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_InScopeRelation_WebConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "webConfigToRoleTypeMappingList");
    }

    public abstract String keepId_InScopeRelation_WebConfigToRoleTypeMappingList(
            WebConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select ROLE_TYPE_ID from DATA_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'dataConfigToRoleTypeMappingAsOne'.
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
        registerNotInScopeRelation(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "dataConfigToRoleTypeMappingList");
    }

    public abstract String keepId_NotInScopeRelation_DataConfigToRoleTypeMappingList(
            DataConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select ROLE_TYPE_ID from FILE_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'fileConfigToRoleTypeMappingAsOne'.
     * @param subQuery The sub-query of FileConfigToRoleTypeMappingList for 'not in-scope'. (NotNull)
     */
    public void notInScopeFileConfigToRoleTypeMappingList(
            final SubQuery<FileConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<FileConfigToRoleTypeMappingCB>", subQuery);
        final FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_FileConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "fileConfigToRoleTypeMappingList");
    }

    public abstract String keepId_NotInScopeRelation_FileConfigToRoleTypeMappingList(
            FileConfigToRoleTypeMappingCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select ROLE_TYPE_ID from LABEL_TYPE_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * LABEL_TYPE_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'labelTypeToRoleTypeMappingAsOne'.
     * @param subQuery The sub-query of LabelTypeToRoleTypeMappingList for 'not in-scope'. (NotNull)
     */
    public void notInScopeLabelTypeToRoleTypeMappingList(
            final SubQuery<LabelTypeToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<LabelTypeToRoleTypeMappingCB>", subQuery);
        final LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_LabelTypeToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "labelTypeToRoleTypeMappingList");
    }

    public abstract String keepId_NotInScopeRelation_LabelTypeToRoleTypeMappingList(
            LabelTypeToRoleTypeMappingCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select ROLE_TYPE_ID from WEB_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'webConfigToRoleTypeMappingAsOne'.
     * @param subQuery The sub-query of WebConfigToRoleTypeMappingList for 'not in-scope'. (NotNull)
     */
    public void notInScopeWebConfigToRoleTypeMappingList(
            final SubQuery<WebConfigToRoleTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<WebConfigToRoleTypeMappingCB>", subQuery);
        final WebConfigToRoleTypeMappingCB cb = new WebConfigToRoleTypeMappingCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_NotInScopeRelation_WebConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "ID", "ROLE_TYPE_ID",
                subQueryPropertyName, "webConfigToRoleTypeMappingList");
    }

    public abstract String keepId_NotInScopeRelation_WebConfigToRoleTypeMappingList(
            WebConfigToRoleTypeMappingCQ subQuery);

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
                "ROLE_TYPE_ID", subQueryPropertyName,
                "dataConfigToRoleTypeMappingList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingList(
            DataConfigToRoleTypeMappingCQ subQuery);

    public void xsderiveFileConfigToRoleTypeMappingList(final String function,
            final SubQuery<FileConfigToRoleTypeMappingCB> subQuery,
            final String aliasName, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<FileConfigToRoleTypeMappingCB>", subQuery);
        final FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID",
                "ROLE_TYPE_ID", subQueryPropertyName,
                "fileConfigToRoleTypeMappingList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList(
            FileConfigToRoleTypeMappingCQ subQuery);

    public void xsderiveLabelTypeToRoleTypeMappingList(final String function,
            final SubQuery<LabelTypeToRoleTypeMappingCB> subQuery,
            final String aliasName, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<LabelTypeToRoleTypeMappingCB>", subQuery);
        final LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID",
                "ROLE_TYPE_ID", subQueryPropertyName,
                "labelTypeToRoleTypeMappingList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingList(
            LabelTypeToRoleTypeMappingCQ subQuery);

    public void xsderiveWebConfigToRoleTypeMappingList(final String function,
            final SubQuery<WebConfigToRoleTypeMappingCB> subQuery,
            final String aliasName, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<WebConfigToRoleTypeMappingCB>", subQuery);
        final WebConfigToRoleTypeMappingCB cb = new WebConfigToRoleTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        registerSpecifyDerivedReferrer(function, cb.query(), "ID",
                "ROLE_TYPE_ID", subQueryPropertyName,
                "webConfigToRoleTypeMappingList", aliasName, option);
    }

    public abstract String keepId_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList(
            WebConfigToRoleTypeMappingCQ subQuery);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from DATA_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * DATA_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'dataConfigToRoleTypeMappingAsOne'.
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
                "ROLE_TYPE_ID", subQueryPropertyName,
                "dataConfigToRoleTypeMappingList", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepId_QueryDerivedReferrer_DataConfigToRoleTypeMappingList(
            DataConfigToRoleTypeMappingCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_DataConfigToRoleTypeMappingListParameter(
            Object parameterValue);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from FILE_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'fileConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedFileConfigToRoleTypeMappingList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;FileConfigToRoleTypeMappingCB&gt;() {
     *     public void query(FileConfigToRoleTypeMappingCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<FileConfigToRoleTypeMappingCB> derivedFileConfigToRoleTypeMappingList() {
        return xcreateQDRFunctionFileConfigToRoleTypeMappingList();
    }

    protected HpQDRFunction<FileConfigToRoleTypeMappingCB> xcreateQDRFunctionFileConfigToRoleTypeMappingList() {
        return new HpQDRFunction<FileConfigToRoleTypeMappingCB>(
                new HpQDRSetupper<FileConfigToRoleTypeMappingCB>() {
                    @Override
                    public void setup(
                            final String function,
                            final SubQuery<FileConfigToRoleTypeMappingCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveFileConfigToRoleTypeMappingList(function,
                                subQuery, operand, value, option);
                    }
                });
    }

    public void xqderiveFileConfigToRoleTypeMappingList(final String function,
            final SubQuery<FileConfigToRoleTypeMappingCB> subQuery,
            final String operand, final Object value,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<FileConfigToRoleTypeMappingCB>", subQuery);
        final FileConfigToRoleTypeMappingCB cb = new FileConfigToRoleTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID",
                "ROLE_TYPE_ID", subQueryPropertyName,
                "fileConfigToRoleTypeMappingList", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingList(
            FileConfigToRoleTypeMappingCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameter(
            Object parameterValue);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from LABEL_TYPE_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * LABEL_TYPE_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'labelTypeToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedLabelTypeToRoleTypeMappingList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;LabelTypeToRoleTypeMappingCB&gt;() {
     *     public void query(LabelTypeToRoleTypeMappingCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<LabelTypeToRoleTypeMappingCB> derivedLabelTypeToRoleTypeMappingList() {
        return xcreateQDRFunctionLabelTypeToRoleTypeMappingList();
    }

    protected HpQDRFunction<LabelTypeToRoleTypeMappingCB> xcreateQDRFunctionLabelTypeToRoleTypeMappingList() {
        return new HpQDRFunction<LabelTypeToRoleTypeMappingCB>(
                new HpQDRSetupper<LabelTypeToRoleTypeMappingCB>() {
                    @Override
                    public void setup(
                            final String function,
                            final SubQuery<LabelTypeToRoleTypeMappingCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveLabelTypeToRoleTypeMappingList(function,
                                subQuery, operand, value, option);
                    }
                });
    }

    public void xqderiveLabelTypeToRoleTypeMappingList(final String function,
            final SubQuery<LabelTypeToRoleTypeMappingCB> subQuery,
            final String operand, final Object value,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<LabelTypeToRoleTypeMappingCB>", subQuery);
        final LabelTypeToRoleTypeMappingCB cb = new LabelTypeToRoleTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_LabelTypeToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID",
                "ROLE_TYPE_ID", subQueryPropertyName,
                "labelTypeToRoleTypeMappingList", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepId_QueryDerivedReferrer_LabelTypeToRoleTypeMappingList(
            LabelTypeToRoleTypeMappingCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListParameter(
            Object parameterValue);

    /**
     * Prepare for (Query)DerivedReferrer. <br />
     * {FOO &lt;= (select max(BAR) from WEB_CONFIG_TO_ROLE_TYPE_MAPPING where ...)} <br />
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'webConfigToRoleTypeMappingAsOne'.
     * <pre>
     * cb.query().<span style="color: #FD4747">derivedWebConfigToRoleTypeMappingList()</span>.<span style="color: #FD4747">max</span>(new SubQuery&lt;WebConfigToRoleTypeMappingCB&gt;() {
     *     public void query(WebConfigToRoleTypeMappingCB subCB) {
     *         subCB.specify().<span style="color: #FD4747">columnFoo...</span> <span style="color: #3F7E5E">// derived column by function</span>
     *         subCB.query().setBar... <span style="color: #3F7E5E">// referrer condition</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(123); <span style="color: #3F7E5E">// condition to derived column</span>
     * </pre>
     * @return The object to set up a function for referrer table. (NotNull)
     */
    public HpQDRFunction<WebConfigToRoleTypeMappingCB> derivedWebConfigToRoleTypeMappingList() {
        return xcreateQDRFunctionWebConfigToRoleTypeMappingList();
    }

    protected HpQDRFunction<WebConfigToRoleTypeMappingCB> xcreateQDRFunctionWebConfigToRoleTypeMappingList() {
        return new HpQDRFunction<WebConfigToRoleTypeMappingCB>(
                new HpQDRSetupper<WebConfigToRoleTypeMappingCB>() {
                    @Override
                    public void setup(
                            final String function,
                            final SubQuery<WebConfigToRoleTypeMappingCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveWebConfigToRoleTypeMappingList(function,
                                subQuery, operand, value, option);
                    }
                });
    }

    public void xqderiveWebConfigToRoleTypeMappingList(final String function,
            final SubQuery<WebConfigToRoleTypeMappingCB> subQuery,
            final String operand, final Object value,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<WebConfigToRoleTypeMappingCB>", subQuery);
        final WebConfigToRoleTypeMappingCB cb = new WebConfigToRoleTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingList(cb
                .query()); // for saving query-value.
        final String parameterPropertyName = keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameter(value);
        registerQueryDerivedReferrer(function, cb.query(), "ID",
                "ROLE_TYPE_ID", subQueryPropertyName,
                "webConfigToRoleTypeMappingList", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingList(
            WebConfigToRoleTypeMappingCQ subQuery);

    public abstract String keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameter(
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
     * NAME: {NotNull, VARCHAR(100)}
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
     * NAME: {NotNull, VARCHAR(100)}
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
     * NAME: {NotNull, VARCHAR(100)}
     * @param name The value of name as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_GreaterThan(final String name) {
        regName(CK_GT, fRES(name));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(100)}
     * @param name The value of name as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_LessThan(final String name) {
        regName(CK_LT, fRES(name));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(100)}
     * @param name The value of name as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_GreaterEqual(final String name) {
        regName(CK_GE, fRES(name));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * NAME: {NotNull, VARCHAR(100)}
     * @param name The value of name as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_LessEqual(final String name) {
        regName(CK_LE, fRES(name));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * NAME: {NotNull, VARCHAR(100)}
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
     * NAME: {NotNull, VARCHAR(100)}
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
     * NAME: {NotNull, VARCHAR(100)}
     * @param name The value of name as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setName_PrefixSearch(final String name) {
        setName_LikeSearch(name, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * NAME: {NotNull, VARCHAR(100)} <br />
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
     * NAME: {NotNull, VARCHAR(100)}
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
     * VALUE: {NotNull, VARCHAR(20)}
     * @param value The value of value as equal. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_Equal(final String value) {
        doSetValue_Equal(fRES(value));
    }

    protected void doSetValue_Equal(final String value) {
        regValue(CK_EQ, value);
    }

    /**
     * NotEqual(&lt;&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @param value The value of value as notEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_NotEqual(final String value) {
        doSetValue_NotEqual(fRES(value));
    }

    protected void doSetValue_NotEqual(final String value) {
        regValue(CK_NES, value);
    }

    /**
     * GreaterThan(&gt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @param value The value of value as greaterThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_GreaterThan(final String value) {
        regValue(CK_GT, fRES(value));
    }

    /**
     * LessThan(&lt;). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @param value The value of value as lessThan. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_LessThan(final String value) {
        regValue(CK_LT, fRES(value));
    }

    /**
     * GreaterEqual(&gt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @param value The value of value as greaterEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_GreaterEqual(final String value) {
        regValue(CK_GE, fRES(value));
    }

    /**
     * LessEqual(&lt;=). And NullOrEmptyIgnored, OnlyOnceRegistered. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @param value The value of value as lessEqual. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_LessEqual(final String value) {
        regValue(CK_LE, fRES(value));
    }

    /**
     * InScope {in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @param valueList The collection of value as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_InScope(final Collection<String> valueList) {
        doSetValue_InScope(valueList);
    }

    public void doSetValue_InScope(final Collection<String> valueList) {
        regINS(CK_INS, cTL(valueList), getCValueValue(), "VALUE");
    }

    /**
     * NotInScope {not in ('a', 'b')}. And NullOrEmptyIgnored, NullOrEmptyElementIgnored, SeveralRegistered. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @param valueList The collection of value as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_NotInScope(final Collection<String> valueList) {
        doSetValue_NotInScope(valueList);
    }

    public void doSetValue_NotInScope(final Collection<String> valueList) {
        regINS(CK_NINS, cTL(valueList), getCValueValue(), "VALUE");
    }

    /**
     * PrefixSearch {like 'xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @param value The value of value as prefixSearch. (NullAllowed: if null (or empty), no condition)
     */
    public void setValue_PrefixSearch(final String value) {
        setValue_LikeSearch(value, cLSOP());
    }

    /**
     * LikeSearch with various options. (versatile) {like '%xxx%' escape ...}. And NullOrEmptyIgnored, SeveralRegistered. <br />
     * VALUE: {NotNull, VARCHAR(20)} <br />
     * <pre>e.g. setValue_LikeSearch("xxx", new <span style="color: #FD4747">LikeSearchOption</span>().likeContain());</pre>
     * @param value The value of value as likeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of like-search. (NotNull)
     */
    public void setValue_LikeSearch(final String value,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_LS, fRES(value), getCValueValue(), "VALUE", likeSearchOption);
    }

    /**
     * NotLikeSearch with various options. (versatile) {not like 'xxx%' escape ...} <br />
     * And NullOrEmptyIgnored, SeveralRegistered. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @param value The value of value as notLikeSearch. (NullAllowed: if null (or empty), no condition)
     * @param likeSearchOption The option of not-like-search. (NotNull)
     */
    public void setValue_NotLikeSearch(final String value,
            final LikeSearchOption likeSearchOption) {
        regLSQ(CK_NLS, fRES(value), getCValueValue(), "VALUE", likeSearchOption);
    }

    protected void regValue(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueValue(), "VALUE");
    }

    abstract protected ConditionValue getCValueValue();

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
     * cb.query().<span style="color: #FD4747">scalar_Equal()</span>.max(new SubQuery&lt;RoleTypeCB&gt;() {
     *     public void query(RoleTypeCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<RoleTypeCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ.getOperand());
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;RoleTypeCB&gt;() {
     *     public void query(RoleTypeCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<RoleTypeCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;RoleTypeCB&gt;() {
     *     public void query(RoleTypeCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<RoleTypeCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessThan()</span>.max(new SubQuery&lt;RoleTypeCB&gt;() {
     *     public void query(RoleTypeCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<RoleTypeCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;RoleTypeCB&gt;() {
     *     public void query(RoleTypeCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<RoleTypeCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;RoleTypeCB&gt;() {
     *     public void query(RoleTypeCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<RoleTypeCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE.getOperand());
    }

    protected HpSSQFunction<RoleTypeCB> xcreateSSQFunction(final String operand) {
        return new HpSSQFunction<RoleTypeCB>(new HpSSQSetupper<RoleTypeCB>() {
            @Override
            public void setup(final String function,
                    final SubQuery<RoleTypeCB> subQuery,
                    final HpSSQOption<RoleTypeCB> option) {
                xscalarCondition(function, subQuery, operand, option);
            }
        });
    }

    protected void xscalarCondition(final String function,
            final SubQuery<RoleTypeCB> subQuery, final String operand,
            final HpSSQOption<RoleTypeCB> option) {
        assertObjectNotNull("subQuery<RoleTypeCB>", subQuery);
        final RoleTypeCB cb = xcreateScalarConditionCB();
        subQuery.query(cb);
        final String subQueryPropertyName = keepScalarCondition(cb.query()); // for saving query-value
        option.setPartitionByCBean(xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(function, cb.query(), subQueryPropertyName,
                operand, option);
    }

    public abstract String keepScalarCondition(RoleTypeCQ subQuery);

    protected RoleTypeCB xcreateScalarConditionCB() {
        final RoleTypeCB cb = new RoleTypeCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected RoleTypeCB xcreateScalarConditionPartitionByCB() {
        final RoleTypeCB cb = new RoleTypeCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String function,
            final SubQuery<RoleTypeCB> subQuery, final String aliasName,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<RoleTypeCB>", subQuery);
        final RoleTypeCB cb = new RoleTypeCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepSpecifyMyselfDerived(cb.query()); // for saving query-value.
        registerSpecifyMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", aliasName, option);
    }

    public abstract String keepSpecifyMyselfDerived(RoleTypeCQ subQuery);

    /**
     * Prepare for (Query)MyselfDerived (SubQuery).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<RoleTypeCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived();
    }

    protected HpQDRFunction<RoleTypeCB> xcreateQDRFunctionMyselfDerived() {
        return new HpQDRFunction<RoleTypeCB>(new HpQDRSetupper<RoleTypeCB>() {
            @Override
            public void setup(final String function,
                    final SubQuery<RoleTypeCB> subQuery, final String operand,
                    final Object value, final DerivedReferrerOption option) {
                xqderiveMyselfDerived(function, subQuery, operand, value,
                        option);
            }
        });
    }

    public void xqderiveMyselfDerived(final String function,
            final SubQuery<RoleTypeCB> subQuery, final String operand,
            final Object value, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<RoleTypeCB>", subQuery);
        final RoleTypeCB cb = new RoleTypeCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String parameterPropertyName = keepQueryMyselfDerivedParameter(value);
        registerQueryMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepQueryMyselfDerived(RoleTypeCQ subQuery);

    public abstract String keepQueryMyselfDerivedParameter(Object parameterValue);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfExists(final SubQuery<RoleTypeCB> subQuery) {
        assertObjectNotNull("subQuery<RoleTypeCB>", subQuery);
        final RoleTypeCB cb = new RoleTypeCB();
        cb.xsetupForMyselfExists(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfExists(cb.query()); // for saving query-value.
        registerMyselfExists(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfExists(RoleTypeCQ subQuery);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfInScope(final SubQuery<RoleTypeCB> subQuery) {
        assertObjectNotNull("subQuery<RoleTypeCB>", subQuery);
        final RoleTypeCB cb = new RoleTypeCB();
        cb.xsetupForMyselfInScope(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfInScope(cb.query()); // for saving query-value.
        registerMyselfInScope(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfInScope(RoleTypeCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCB() {
        return RoleTypeCB.class.getName();
    }

    protected String xabCQ() {
        return RoleTypeCQ.class.getName();
    }

    protected String xabLSO() {
        return LikeSearchOption.class.getName();
    }

    protected String xabSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
