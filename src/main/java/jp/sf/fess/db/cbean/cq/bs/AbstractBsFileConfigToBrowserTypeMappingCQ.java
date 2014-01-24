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
import jp.sf.fess.db.cbean.BrowserTypeCB;
import jp.sf.fess.db.cbean.FileConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.cbean.FileCrawlingConfigCB;
import jp.sf.fess.db.cbean.cq.BrowserTypeCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileCrawlingConfigCQ;

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
 * The abstract condition-query of FILE_CONFIG_TO_BROWSER_TYPE_MAPPING.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsFileConfigToBrowserTypeMappingCQ extends
        AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsFileConfigToBrowserTypeMappingCQ(
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
        return "FILE_CONFIG_TO_BROWSER_TYPE_MAPPING";
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
     * FILE_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigId The value of fileConfigId as equal. (NullAllowed: if null, no condition)
     */
    public void setFileConfigId_Equal(final Long fileConfigId) {
        doSetFileConfigId_Equal(fileConfigId);
    }

    protected void doSetFileConfigId_Equal(final Long fileConfigId) {
        regFileConfigId(CK_EQ, fileConfigId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * FILE_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigId The value of fileConfigId as notEqual. (NullAllowed: if null, no condition)
     */
    public void setFileConfigId_NotEqual(final Long fileConfigId) {
        doSetFileConfigId_NotEqual(fileConfigId);
    }

    protected void doSetFileConfigId_NotEqual(final Long fileConfigId) {
        regFileConfigId(CK_NES, fileConfigId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * FILE_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigId The value of fileConfigId as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setFileConfigId_GreaterThan(final Long fileConfigId) {
        regFileConfigId(CK_GT, fileConfigId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * FILE_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigId The value of fileConfigId as lessThan. (NullAllowed: if null, no condition)
     */
    public void setFileConfigId_LessThan(final Long fileConfigId) {
        regFileConfigId(CK_LT, fileConfigId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * FILE_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigId The value of fileConfigId as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setFileConfigId_GreaterEqual(final Long fileConfigId) {
        regFileConfigId(CK_GE, fileConfigId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * FILE_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigId The value of fileConfigId as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setFileConfigId_LessEqual(final Long fileConfigId) {
        regFileConfigId(CK_LE, fileConfigId);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * FILE_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param minNumber The min number of fileConfigId. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of fileConfigId. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setFileConfigId_RangeOf(final Long minNumber,
            final Long maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueFileConfigId(), "FILE_CONFIG_ID",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * FILE_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigIdList The collection of fileConfigId as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setFileConfigId_InScope(final Collection<Long> fileConfigIdList) {
        doSetFileConfigId_InScope(fileConfigIdList);
    }

    protected void doSetFileConfigId_InScope(
            final Collection<Long> fileConfigIdList) {
        regINS(CK_INS, cTL(fileConfigIdList), getCValueFileConfigId(),
                "FILE_CONFIG_ID");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * FILE_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @param fileConfigIdList The collection of fileConfigId as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setFileConfigId_NotInScope(
            final Collection<Long> fileConfigIdList) {
        doSetFileConfigId_NotInScope(fileConfigIdList);
    }

    protected void doSetFileConfigId_NotInScope(
            final Collection<Long> fileConfigIdList) {
        regINS(CK_NINS, cTL(fileConfigIdList), getCValueFileConfigId(),
                "FILE_CONFIG_ID");
    }

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select FILE_CONFIG_ID from FILE_CRAWLING_CONFIG where ...)} <br />
     * FILE_CRAWLING_CONFIG by my FILE_CONFIG_ID, named 'fileCrawlingConfig'.
     * @param subQuery The sub-query of FileCrawlingConfig for 'in-scope'. (NotNull)
     */
    public void inScopeFileCrawlingConfig(
            final SubQuery<FileCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery<FileCrawlingConfigCB>", subQuery);
        final FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepFileConfigId_InScopeRelation_FileCrawlingConfig(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "FILE_CONFIG_ID", "ID",
                subQueryPropertyName, "fileCrawlingConfig");
    }

    public abstract String keepFileConfigId_InScopeRelation_FileCrawlingConfig(
            FileCrawlingConfigCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select FILE_CONFIG_ID from FILE_CRAWLING_CONFIG where ...)} <br />
     * FILE_CRAWLING_CONFIG by my FILE_CONFIG_ID, named 'fileCrawlingConfig'.
     * @param subQuery The sub-query of FileCrawlingConfig for 'not in-scope'. (NotNull)
     */
    public void notInScopeFileCrawlingConfig(
            final SubQuery<FileCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery<FileCrawlingConfigCB>", subQuery);
        final FileCrawlingConfigCB cb = new FileCrawlingConfigCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepFileConfigId_NotInScopeRelation_FileCrawlingConfig(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "FILE_CONFIG_ID", "ID",
                subQueryPropertyName, "fileCrawlingConfig");
    }

    public abstract String keepFileConfigId_NotInScopeRelation_FileCrawlingConfig(
            FileCrawlingConfigCQ subQuery);

    protected void regFileConfigId(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueFileConfigId(), "FILE_CONFIG_ID");
    }

    abstract protected ConditionValue getCValueFileConfigId();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * BROWSER_TYPE_ID: {IX, NotNull, BIGINT(19), FK to BROWSER_TYPE}
     * @param browserTypeId The value of browserTypeId as equal. (NullAllowed: if null, no condition)
     */
    public void setBrowserTypeId_Equal(final Long browserTypeId) {
        doSetBrowserTypeId_Equal(browserTypeId);
    }

    protected void doSetBrowserTypeId_Equal(final Long browserTypeId) {
        regBrowserTypeId(CK_EQ, browserTypeId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * BROWSER_TYPE_ID: {IX, NotNull, BIGINT(19), FK to BROWSER_TYPE}
     * @param browserTypeId The value of browserTypeId as notEqual. (NullAllowed: if null, no condition)
     */
    public void setBrowserTypeId_NotEqual(final Long browserTypeId) {
        doSetBrowserTypeId_NotEqual(browserTypeId);
    }

    protected void doSetBrowserTypeId_NotEqual(final Long browserTypeId) {
        regBrowserTypeId(CK_NES, browserTypeId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * BROWSER_TYPE_ID: {IX, NotNull, BIGINT(19), FK to BROWSER_TYPE}
     * @param browserTypeId The value of browserTypeId as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setBrowserTypeId_GreaterThan(final Long browserTypeId) {
        regBrowserTypeId(CK_GT, browserTypeId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * BROWSER_TYPE_ID: {IX, NotNull, BIGINT(19), FK to BROWSER_TYPE}
     * @param browserTypeId The value of browserTypeId as lessThan. (NullAllowed: if null, no condition)
     */
    public void setBrowserTypeId_LessThan(final Long browserTypeId) {
        regBrowserTypeId(CK_LT, browserTypeId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * BROWSER_TYPE_ID: {IX, NotNull, BIGINT(19), FK to BROWSER_TYPE}
     * @param browserTypeId The value of browserTypeId as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setBrowserTypeId_GreaterEqual(final Long browserTypeId) {
        regBrowserTypeId(CK_GE, browserTypeId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * BROWSER_TYPE_ID: {IX, NotNull, BIGINT(19), FK to BROWSER_TYPE}
     * @param browserTypeId The value of browserTypeId as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setBrowserTypeId_LessEqual(final Long browserTypeId) {
        regBrowserTypeId(CK_LE, browserTypeId);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * BROWSER_TYPE_ID: {IX, NotNull, BIGINT(19), FK to BROWSER_TYPE}
     * @param minNumber The min number of browserTypeId. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of browserTypeId. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setBrowserTypeId_RangeOf(final Long minNumber,
            final Long maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueBrowserTypeId(),
                "BROWSER_TYPE_ID", rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * BROWSER_TYPE_ID: {IX, NotNull, BIGINT(19), FK to BROWSER_TYPE}
     * @param browserTypeIdList The collection of browserTypeId as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setBrowserTypeId_InScope(
            final Collection<Long> browserTypeIdList) {
        doSetBrowserTypeId_InScope(browserTypeIdList);
    }

    protected void doSetBrowserTypeId_InScope(
            final Collection<Long> browserTypeIdList) {
        regINS(CK_INS, cTL(browserTypeIdList), getCValueBrowserTypeId(),
                "BROWSER_TYPE_ID");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * BROWSER_TYPE_ID: {IX, NotNull, BIGINT(19), FK to BROWSER_TYPE}
     * @param browserTypeIdList The collection of browserTypeId as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setBrowserTypeId_NotInScope(
            final Collection<Long> browserTypeIdList) {
        doSetBrowserTypeId_NotInScope(browserTypeIdList);
    }

    protected void doSetBrowserTypeId_NotInScope(
            final Collection<Long> browserTypeIdList) {
        regINS(CK_NINS, cTL(browserTypeIdList), getCValueBrowserTypeId(),
                "BROWSER_TYPE_ID");
    }

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select BROWSER_TYPE_ID from BROWSER_TYPE where ...)} <br />
     * BROWSER_TYPE by my BROWSER_TYPE_ID, named 'browserType'.
     * @param subQuery The sub-query of BrowserType for 'in-scope'. (NotNull)
     */
    public void inScopeBrowserType(final SubQuery<BrowserTypeCB> subQuery) {
        assertObjectNotNull("subQuery<BrowserTypeCB>", subQuery);
        final BrowserTypeCB cb = new BrowserTypeCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepBrowserTypeId_InScopeRelation_BrowserType(cb
                .query()); // for saving query-value.
        registerInScopeRelation(cb.query(), "BROWSER_TYPE_ID", "ID",
                subQueryPropertyName, "browserType");
    }

    public abstract String keepBrowserTypeId_InScopeRelation_BrowserType(
            BrowserTypeCQ subQuery);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select BROWSER_TYPE_ID from BROWSER_TYPE where ...)} <br />
     * BROWSER_TYPE by my BROWSER_TYPE_ID, named 'browserType'.
     * @param subQuery The sub-query of BrowserType for 'not in-scope'. (NotNull)
     */
    public void notInScopeBrowserType(final SubQuery<BrowserTypeCB> subQuery) {
        assertObjectNotNull("subQuery<BrowserTypeCB>", subQuery);
        final BrowserTypeCB cb = new BrowserTypeCB();
        cb.xsetupForInScopeRelation(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepBrowserTypeId_NotInScopeRelation_BrowserType(cb
                .query()); // for saving query-value.
        registerNotInScopeRelation(cb.query(), "BROWSER_TYPE_ID", "ID",
                subQueryPropertyName, "browserType");
    }

    public abstract String keepBrowserTypeId_NotInScopeRelation_BrowserType(
            BrowserTypeCQ subQuery);

    protected void regBrowserTypeId(final ConditionKey k, final Object v) {
        regQ(k, v, getCValueBrowserTypeId(), "BROWSER_TYPE_ID");
    }

    abstract protected ConditionValue getCValueBrowserTypeId();

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_Equal()</span>.max(new SubQuery&lt;FileConfigToBrowserTypeMappingCB&gt;() {
     *     public void query(FileConfigToBrowserTypeMappingCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FileConfigToBrowserTypeMappingCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ.getOperand());
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;FileConfigToBrowserTypeMappingCB&gt;() {
     *     public void query(FileConfigToBrowserTypeMappingCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FileConfigToBrowserTypeMappingCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;FileConfigToBrowserTypeMappingCB&gt;() {
     *     public void query(FileConfigToBrowserTypeMappingCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FileConfigToBrowserTypeMappingCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessThan()</span>.max(new SubQuery&lt;FileConfigToBrowserTypeMappingCB&gt;() {
     *     public void query(FileConfigToBrowserTypeMappingCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FileConfigToBrowserTypeMappingCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT.getOperand());
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;FileConfigToBrowserTypeMappingCB&gt;() {
     *     public void query(FileConfigToBrowserTypeMappingCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FileConfigToBrowserTypeMappingCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE.getOperand());
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #FD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;FileConfigToBrowserTypeMappingCB&gt;() {
     *     public void query(FileConfigToBrowserTypeMappingCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<FileConfigToBrowserTypeMappingCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE.getOperand());
    }

    protected HpSSQFunction<FileConfigToBrowserTypeMappingCB> xcreateSSQFunction(
            final String operand) {
        return new HpSSQFunction<FileConfigToBrowserTypeMappingCB>(
                new HpSSQSetupper<FileConfigToBrowserTypeMappingCB>() {
                    @Override
                    public void setup(
                            final String function,
                            final SubQuery<FileConfigToBrowserTypeMappingCB> subQuery,
                            final HpSSQOption<FileConfigToBrowserTypeMappingCB> option) {
                        xscalarCondition(function, subQuery, operand, option);
                    }
                });
    }

    protected void xscalarCondition(final String function,
            final SubQuery<FileConfigToBrowserTypeMappingCB> subQuery,
            final String operand,
            final HpSSQOption<FileConfigToBrowserTypeMappingCB> option) {
        assertObjectNotNull("subQuery<FileConfigToBrowserTypeMappingCB>",
                subQuery);
        final FileConfigToBrowserTypeMappingCB cb = xcreateScalarConditionCB();
        subQuery.query(cb);
        final String subQueryPropertyName = keepScalarCondition(cb.query()); // for saving query-value
        option.setPartitionByCBean(xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(function, cb.query(), subQueryPropertyName,
                operand, option);
    }

    public abstract String keepScalarCondition(
            FileConfigToBrowserTypeMappingCQ subQuery);

    protected FileConfigToBrowserTypeMappingCB xcreateScalarConditionCB() {
        final FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected FileConfigToBrowserTypeMappingCB xcreateScalarConditionPartitionByCB() {
        final FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String function,
            final SubQuery<FileConfigToBrowserTypeMappingCB> subQuery,
            final String aliasName, final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<FileConfigToBrowserTypeMappingCB>",
                subQuery);
        final FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepSpecifyMyselfDerived(cb.query()); // for saving query-value.
        registerSpecifyMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", aliasName, option);
    }

    public abstract String keepSpecifyMyselfDerived(
            FileConfigToBrowserTypeMappingCQ subQuery);

    /**
     * Prepare for (Query)MyselfDerived (SubQuery).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<FileConfigToBrowserTypeMappingCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived();
    }

    protected HpQDRFunction<FileConfigToBrowserTypeMappingCB> xcreateQDRFunctionMyselfDerived() {
        return new HpQDRFunction<FileConfigToBrowserTypeMappingCB>(
                new HpQDRSetupper<FileConfigToBrowserTypeMappingCB>() {
                    @Override
                    public void setup(
                            final String function,
                            final SubQuery<FileConfigToBrowserTypeMappingCB> subQuery,
                            final String operand, final Object value,
                            final DerivedReferrerOption option) {
                        xqderiveMyselfDerived(function, subQuery, operand,
                                value, option);
                    }
                });
    }

    public void xqderiveMyselfDerived(final String function,
            final SubQuery<FileConfigToBrowserTypeMappingCB> subQuery,
            final String operand, final Object value,
            final DerivedReferrerOption option) {
        assertObjectNotNull("subQuery<FileConfigToBrowserTypeMappingCB>",
                subQuery);
        final FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String parameterPropertyName = keepQueryMyselfDerivedParameter(value);
        registerQueryMyselfDerived(function, cb.query(), "ID", "ID",
                subQueryPropertyName, "myselfDerived", operand, value,
                parameterPropertyName, option);
    }

    public abstract String keepQueryMyselfDerived(
            FileConfigToBrowserTypeMappingCQ subQuery);

    public abstract String keepQueryMyselfDerivedParameter(Object parameterValue);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfExists(
            final SubQuery<FileConfigToBrowserTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<FileConfigToBrowserTypeMappingCB>",
                subQuery);
        final FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
        cb.xsetupForMyselfExists(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfExists(cb.query()); // for saving query-value.
        registerMyselfExists(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfExists(
            FileConfigToBrowserTypeMappingCQ subQuery);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (SubQuery).
     * @param subQuery The implementation of sub query. (NotNull)
     */
    public void myselfInScope(
            final SubQuery<FileConfigToBrowserTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery<FileConfigToBrowserTypeMappingCB>",
                subQuery);
        final FileConfigToBrowserTypeMappingCB cb = new FileConfigToBrowserTypeMappingCB();
        cb.xsetupForMyselfInScope(this);
        subQuery.query(cb);
        final String subQueryPropertyName = keepMyselfInScope(cb.query()); // for saving query-value.
        registerMyselfInScope(cb.query(), subQueryPropertyName);
    }

    public abstract String keepMyselfInScope(
            FileConfigToBrowserTypeMappingCQ subQuery);

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCB() {
        return FileConfigToBrowserTypeMappingCB.class.getName();
    }

    protected String xabCQ() {
        return FileConfigToBrowserTypeMappingCQ.class.getName();
    }

    protected String xabLSO() {
        return LikeSearchOption.class.getName();
    }

    protected String xabSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
