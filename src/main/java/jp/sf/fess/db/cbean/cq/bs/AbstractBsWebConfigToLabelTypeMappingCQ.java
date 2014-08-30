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
import java.util.List;

import jp.sf.fess.db.allcommon.DBMetaInstanceHandler;
import jp.sf.fess.db.cbean.LabelTypeCB;
import jp.sf.fess.db.cbean.WebConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.WebCrawlingConfigCB;
import jp.sf.fess.db.cbean.cq.LabelTypeCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebCrawlingConfigCQ;

import org.seasar.dbflute.cbean.AbstractConditionQuery;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.ManualOrderBean;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.chelper.HpQDRFunction;
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
 * The abstract condition-query of WEB_CONFIG_TO_LABEL_TYPE_MAPPING.
 * @author DBFlute(AutoGenerator)
 */
public abstract class AbstractBsWebConfigToLabelTypeMappingCQ extends
        AbstractConditionQuery {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AbstractBsWebConfigToLabelTypeMappingCQ(
            final ConditionQuery referrerQuery, final SqlClause sqlClause,
            final String aliasName, final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
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
        return "WEB_CONFIG_TO_LABEL_TYPE_MAPPING";
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

    protected void regId(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueId(), "ID");
    }

    protected abstract ConditionValue getCValueId();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigId The value of webConfigId as equal. (NullAllowed: if null, no condition)
     */
    public void setWebConfigId_Equal(final Long webConfigId) {
        doSetWebConfigId_Equal(webConfigId);
    }

    protected void doSetWebConfigId_Equal(final Long webConfigId) {
        regWebConfigId(CK_EQ, webConfigId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigId The value of webConfigId as notEqual. (NullAllowed: if null, no condition)
     */
    public void setWebConfigId_NotEqual(final Long webConfigId) {
        doSetWebConfigId_NotEqual(webConfigId);
    }

    protected void doSetWebConfigId_NotEqual(final Long webConfigId) {
        regWebConfigId(CK_NES, webConfigId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigId The value of webConfigId as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setWebConfigId_GreaterThan(final Long webConfigId) {
        regWebConfigId(CK_GT, webConfigId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigId The value of webConfigId as lessThan. (NullAllowed: if null, no condition)
     */
    public void setWebConfigId_LessThan(final Long webConfigId) {
        regWebConfigId(CK_LT, webConfigId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigId The value of webConfigId as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setWebConfigId_GreaterEqual(final Long webConfigId) {
        regWebConfigId(CK_GE, webConfigId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigId The value of webConfigId as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setWebConfigId_LessEqual(final Long webConfigId) {
        regWebConfigId(CK_LE, webConfigId);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * WEB_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param minNumber The min number of webConfigId. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of webConfigId. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setWebConfigId_RangeOf(final Long minNumber,
            final Long maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueWebConfigId(), "WEB_CONFIG_ID",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * WEB_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigIdList The collection of webConfigId as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setWebConfigId_InScope(final Collection<Long> webConfigIdList) {
        doSetWebConfigId_InScope(webConfigIdList);
    }

    protected void doSetWebConfigId_InScope(
            final Collection<Long> webConfigIdList) {
        regINS(CK_INS, cTL(webConfigIdList), getCValueWebConfigId(),
                "WEB_CONFIG_ID");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * WEB_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @param webConfigIdList The collection of webConfigId as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setWebConfigId_NotInScope(final Collection<Long> webConfigIdList) {
        doSetWebConfigId_NotInScope(webConfigIdList);
    }

    protected void doSetWebConfigId_NotInScope(
            final Collection<Long> webConfigIdList) {
        regINS(CK_NINS, cTL(webConfigIdList), getCValueWebConfigId(),
                "WEB_CONFIG_ID");
    }

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select WEB_CONFIG_ID from WEB_CRAWLING_CONFIG where ...)} <br />
     * WEB_CRAWLING_CONFIG by my WEB_CONFIG_ID, named 'webCrawlingConfig'.
     * @param subQuery The sub-query of WebCrawlingConfig for 'in-scope'. (NotNull)
     */
    public void inScopeWebCrawlingConfig(
            final SubQuery<WebCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepWebConfigId_InScopeRelation_WebCrawlingConfig(cb
                .query());
        registerInScopeRelation(cb.query(), "WEB_CONFIG_ID", "ID", pp,
                "webCrawlingConfig");
    }

    public abstract String keepWebConfigId_InScopeRelation_WebCrawlingConfig(
            WebCrawlingConfigCQ sq);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select WEB_CONFIG_ID from WEB_CRAWLING_CONFIG where ...)} <br />
     * WEB_CRAWLING_CONFIG by my WEB_CONFIG_ID, named 'webCrawlingConfig'.
     * @param subQuery The sub-query of WebCrawlingConfig for 'not in-scope'. (NotNull)
     */
    public void notInScopeWebCrawlingConfig(
            final SubQuery<WebCrawlingConfigCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final WebCrawlingConfigCB cb = new WebCrawlingConfigCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepWebConfigId_NotInScopeRelation_WebCrawlingConfig(cb
                .query());
        registerNotInScopeRelation(cb.query(), "WEB_CONFIG_ID", "ID", pp,
                "webCrawlingConfig");
    }

    public abstract String keepWebConfigId_NotInScopeRelation_WebCrawlingConfig(
            WebCrawlingConfigCQ sq);

    protected void regWebConfigId(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueWebConfigId(), "WEB_CONFIG_ID");
    }

    protected abstract ConditionValue getCValueWebConfigId();

    /**
     * Equal(=). And NullIgnored, OnlyOnceRegistered. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @param labelTypeId The value of labelTypeId as equal. (NullAllowed: if null, no condition)
     */
    public void setLabelTypeId_Equal(final Long labelTypeId) {
        doSetLabelTypeId_Equal(labelTypeId);
    }

    protected void doSetLabelTypeId_Equal(final Long labelTypeId) {
        regLabelTypeId(CK_EQ, labelTypeId);
    }

    /**
     * NotEqual(&lt;&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @param labelTypeId The value of labelTypeId as notEqual. (NullAllowed: if null, no condition)
     */
    public void setLabelTypeId_NotEqual(final Long labelTypeId) {
        doSetLabelTypeId_NotEqual(labelTypeId);
    }

    protected void doSetLabelTypeId_NotEqual(final Long labelTypeId) {
        regLabelTypeId(CK_NES, labelTypeId);
    }

    /**
     * GreaterThan(&gt;). And NullIgnored, OnlyOnceRegistered. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @param labelTypeId The value of labelTypeId as greaterThan. (NullAllowed: if null, no condition)
     */
    public void setLabelTypeId_GreaterThan(final Long labelTypeId) {
        regLabelTypeId(CK_GT, labelTypeId);
    }

    /**
     * LessThan(&lt;). And NullIgnored, OnlyOnceRegistered. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @param labelTypeId The value of labelTypeId as lessThan. (NullAllowed: if null, no condition)
     */
    public void setLabelTypeId_LessThan(final Long labelTypeId) {
        regLabelTypeId(CK_LT, labelTypeId);
    }

    /**
     * GreaterEqual(&gt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @param labelTypeId The value of labelTypeId as greaterEqual. (NullAllowed: if null, no condition)
     */
    public void setLabelTypeId_GreaterEqual(final Long labelTypeId) {
        regLabelTypeId(CK_GE, labelTypeId);
    }

    /**
     * LessEqual(&lt;=). And NullIgnored, OnlyOnceRegistered. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @param labelTypeId The value of labelTypeId as lessEqual. (NullAllowed: if null, no condition)
     */
    public void setLabelTypeId_LessEqual(final Long labelTypeId) {
        regLabelTypeId(CK_LE, labelTypeId);
    }

    /**
     * RangeOf with various options. (versatile) <br />
     * {(default) minNumber &lt;= column &lt;= maxNumber} <br />
     * And NullIgnored, OnlyOnceRegistered. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @param minNumber The min number of labelTypeId. (NullAllowed: if null, no from-condition)
     * @param maxNumber The max number of labelTypeId. (NullAllowed: if null, no to-condition)
     * @param rangeOfOption The option of range-of. (NotNull)
     */
    public void setLabelTypeId_RangeOf(final Long minNumber,
            final Long maxNumber, final RangeOfOption rangeOfOption) {
        regROO(minNumber, maxNumber, getCValueLabelTypeId(), "LABEL_TYPE_ID",
                rangeOfOption);
    }

    /**
     * InScope {in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @param labelTypeIdList The collection of labelTypeId as inScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setLabelTypeId_InScope(final Collection<Long> labelTypeIdList) {
        doSetLabelTypeId_InScope(labelTypeIdList);
    }

    protected void doSetLabelTypeId_InScope(
            final Collection<Long> labelTypeIdList) {
        regINS(CK_INS, cTL(labelTypeIdList), getCValueLabelTypeId(),
                "LABEL_TYPE_ID");
    }

    /**
     * NotInScope {not in (1, 2)}. And NullIgnored, NullElementIgnored, SeveralRegistered. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @param labelTypeIdList The collection of labelTypeId as notInScope. (NullAllowed: if null (or empty), no condition)
     */
    public void setLabelTypeId_NotInScope(final Collection<Long> labelTypeIdList) {
        doSetLabelTypeId_NotInScope(labelTypeIdList);
    }

    protected void doSetLabelTypeId_NotInScope(
            final Collection<Long> labelTypeIdList) {
        regINS(CK_NINS, cTL(labelTypeIdList), getCValueLabelTypeId(),
                "LABEL_TYPE_ID");
    }

    /**
     * Set up InScopeRelation (sub-query). <br />
     * {in (select LABEL_TYPE_ID from LABEL_TYPE where ...)} <br />
     * LABEL_TYPE by my LABEL_TYPE_ID, named 'labelType'.
     * @param subQuery The sub-query of LabelType for 'in-scope'. (NotNull)
     */
    public void inScopeLabelType(final SubQuery<LabelTypeCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final LabelTypeCB cb = new LabelTypeCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepLabelTypeId_InScopeRelation_LabelType(cb.query());
        registerInScopeRelation(cb.query(), "LABEL_TYPE_ID", "ID", pp,
                "labelType");
    }

    public abstract String keepLabelTypeId_InScopeRelation_LabelType(
            LabelTypeCQ sq);

    /**
     * Set up NotInScopeRelation (sub-query). <br />
     * {not in (select LABEL_TYPE_ID from LABEL_TYPE where ...)} <br />
     * LABEL_TYPE by my LABEL_TYPE_ID, named 'labelType'.
     * @param subQuery The sub-query of LabelType for 'not in-scope'. (NotNull)
     */
    public void notInScopeLabelType(final SubQuery<LabelTypeCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final LabelTypeCB cb = new LabelTypeCB();
        cb.xsetupForInScopeRelation(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepLabelTypeId_NotInScopeRelation_LabelType(cb
                .query());
        registerNotInScopeRelation(cb.query(), "LABEL_TYPE_ID", "ID", pp,
                "labelType");
    }

    public abstract String keepLabelTypeId_NotInScopeRelation_LabelType(
            LabelTypeCQ sq);

    protected void regLabelTypeId(final ConditionKey ky, final Object vl) {
        regQ(ky, vl, getCValueLabelTypeId(), "LABEL_TYPE_ID");
    }

    protected abstract ConditionValue getCValueLabelTypeId();

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO = (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_Equal()</span>.max(new SubQuery&lt;WebConfigToLabelTypeMappingCB&gt;() {
     *     public void query(WebConfigToLabelTypeMappingCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<WebConfigToLabelTypeMappingCB> scalar_Equal() {
        return xcreateSSQFunction(CK_EQ, WebConfigToLabelTypeMappingCB.class);
    }

    /**
     * Prepare ScalarCondition as equal. <br />
     * {where FOO &lt;&gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_NotEqual()</span>.max(new SubQuery&lt;WebConfigToLabelTypeMappingCB&gt;() {
     *     public void query(WebConfigToLabelTypeMappingCB subCB) {
     *         subCB.specify().setXxx... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setYyy...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<WebConfigToLabelTypeMappingCB> scalar_NotEqual() {
        return xcreateSSQFunction(CK_NES, WebConfigToLabelTypeMappingCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterThan. <br />
     * {where FOO &gt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_GreaterThan()</span>.max(new SubQuery&lt;WebConfigToLabelTypeMappingCB&gt;() {
     *     public void query(WebConfigToLabelTypeMappingCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<WebConfigToLabelTypeMappingCB> scalar_GreaterThan() {
        return xcreateSSQFunction(CK_GT, WebConfigToLabelTypeMappingCB.class);
    }

    /**
     * Prepare ScalarCondition as lessThan. <br />
     * {where FOO &lt; (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_LessThan()</span>.max(new SubQuery&lt;WebConfigToLabelTypeMappingCB&gt;() {
     *     public void query(WebConfigToLabelTypeMappingCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<WebConfigToLabelTypeMappingCB> scalar_LessThan() {
        return xcreateSSQFunction(CK_LT, WebConfigToLabelTypeMappingCB.class);
    }

    /**
     * Prepare ScalarCondition as greaterEqual. <br />
     * {where FOO &gt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_GreaterEqual()</span>.max(new SubQuery&lt;WebConfigToLabelTypeMappingCB&gt;() {
     *     public void query(WebConfigToLabelTypeMappingCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<WebConfigToLabelTypeMappingCB> scalar_GreaterEqual() {
        return xcreateSSQFunction(CK_GE, WebConfigToLabelTypeMappingCB.class);
    }

    /**
     * Prepare ScalarCondition as lessEqual. <br />
     * {where FOO &lt;= (select max(BAR) from ...)
     * <pre>
     * cb.query().<span style="color: #DD4747">scalar_LessEqual()</span>.max(new SubQuery&lt;WebConfigToLabelTypeMappingCB&gt;() {
     *     public void query(WebConfigToLabelTypeMappingCB subCB) {
     *         subCB.specify().setFoo... <span style="color: #3F7E5E">// derived column for function</span>
     *         subCB.query().setBar...
     *     }
     * });
     * </pre>
     * @return The object to set up a function. (NotNull)
     */
    public HpSSQFunction<WebConfigToLabelTypeMappingCB> scalar_LessEqual() {
        return xcreateSSQFunction(CK_LE, WebConfigToLabelTypeMappingCB.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xscalarCondition(final String fn,
            final SubQuery<CB> sq, final String rd, final HpSSQOption<CB> op) {
        assertObjectNotNull("subQuery", sq);
        final WebConfigToLabelTypeMappingCB cb = xcreateScalarConditionCB();
        sq.query((CB) cb);
        final String pp = keepScalarCondition(cb.query()); // for saving query-value
        op.setPartitionByCBean((CB) xcreateScalarConditionPartitionByCB()); // for using partition-by
        registerScalarCondition(fn, cb.query(), pp, rd, op);
    }

    public abstract String keepScalarCondition(WebConfigToLabelTypeMappingCQ sq);

    protected WebConfigToLabelTypeMappingCB xcreateScalarConditionCB() {
        final WebConfigToLabelTypeMappingCB cb = newMyCB();
        cb.xsetupForScalarCondition(this);
        return cb;
    }

    protected WebConfigToLabelTypeMappingCB xcreateScalarConditionPartitionByCB() {
        final WebConfigToLabelTypeMappingCB cb = newMyCB();
        cb.xsetupForScalarConditionPartitionBy(this);
        return cb;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public void xsmyselfDerive(final String fn,
            final SubQuery<WebConfigToLabelTypeMappingCB> sq, final String al,
            final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final WebConfigToLabelTypeMappingCB cb = new WebConfigToLabelTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        try {
            lock();
            sq.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepSpecifyMyselfDerived(cb.query());
        final String pk = "ID";
        registerSpecifyMyselfDerived(fn, cb.query(), pk, pk, pp,
                "myselfDerived", al, op);
    }

    public abstract String keepSpecifyMyselfDerived(
            WebConfigToLabelTypeMappingCQ sq);

    /**
     * Prepare for (Query)MyselfDerived (correlated sub-query).
     * @return The object to set up a function for myself table. (NotNull)
     */
    public HpQDRFunction<WebConfigToLabelTypeMappingCB> myselfDerived() {
        return xcreateQDRFunctionMyselfDerived(WebConfigToLabelTypeMappingCB.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <CB extends ConditionBean> void xqderiveMyselfDerived(
            final String fn, final SubQuery<CB> sq, final String rd,
            final Object vl, final DerivedReferrerOption op) {
        assertObjectNotNull("subQuery", sq);
        final WebConfigToLabelTypeMappingCB cb = new WebConfigToLabelTypeMappingCB();
        cb.xsetupForDerivedReferrer(this);
        sq.query((CB) cb);
        final String pk = "ID";
        final String sqpp = keepQueryMyselfDerived(cb.query()); // for saving query-value.
        final String prpp = keepQueryMyselfDerivedParameter(vl);
        registerQueryMyselfDerived(fn, cb.query(), pk, pk, sqpp,
                "myselfDerived", rd, vl, prpp, op);
    }

    public abstract String keepQueryMyselfDerived(
            WebConfigToLabelTypeMappingCQ sq);

    public abstract String keepQueryMyselfDerivedParameter(Object vl);

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    /**
     * Prepare for MyselfExists (correlated sub-query).
     * @param subQuery The implementation of sub-query. (NotNull)
     */
    public void myselfExists(
            final SubQuery<WebConfigToLabelTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final WebConfigToLabelTypeMappingCB cb = new WebConfigToLabelTypeMappingCB();
        cb.xsetupForMyselfExists(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepMyselfExists(cb.query());
        registerMyselfExists(cb.query(), pp);
    }

    public abstract String keepMyselfExists(WebConfigToLabelTypeMappingCQ sq);

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    /**
     * Prepare for MyselfInScope (sub-query).
     * @param subQuery The implementation of sub-query. (NotNull)
     */
    public void myselfInScope(
            final SubQuery<WebConfigToLabelTypeMappingCB> subQuery) {
        assertObjectNotNull("subQuery", subQuery);
        final WebConfigToLabelTypeMappingCB cb = new WebConfigToLabelTypeMappingCB();
        cb.xsetupForMyselfInScope(this);
        try {
            lock();
            subQuery.query(cb);
        } finally {
            unlock();
        }
        final String pp = keepMyselfInScope(cb.query());
        registerMyselfInScope(cb.query(), pp);
    }

    public abstract String keepMyselfInScope(WebConfigToLabelTypeMappingCQ sq);

    /**
     * Order along manual ordering information.
     * <pre>
     * MemberCB cb = new MemberCB();
     * ManualOrderBean mob = new ManualOrderBean();
     * mob.<span style="color: #DD4747">when_GreaterEqual</span>(priorityDate); <span style="color: #3F7E5E">// e.g. 2000/01/01</span>
     * cb.query().addOrderBy_Birthdate_Asc().<span style="color: #DD4747">withManualOrder(mob)</span>;
     * <span style="color: #3F7E5E">// order by </span>
     * <span style="color: #3F7E5E">//   case</span>
     * <span style="color: #3F7E5E">//     when BIRTHDATE &gt;= '2000/01/01' then 0</span>
     * <span style="color: #3F7E5E">//     else 1</span>
     * <span style="color: #3F7E5E">//   end asc, ...</span>
     *
     * MemberCB cb = new MemberCB();
     * ManualOrderBean mob = new ManualOrderBean();
     * mob.<span style="color: #DD4747">when_Equal</span>(CDef.MemberStatus.Withdrawal);
     * mob.<span style="color: #DD4747">when_Equal</span>(CDef.MemberStatus.Formalized);
     * mob.<span style="color: #DD4747">when_Equal</span>(CDef.MemberStatus.Provisional);
     * cb.query().addOrderBy_MemberStatusCode_Asc().<span style="color: #DD4747">withManualOrder(mob)</span>;
     * <span style="color: #3F7E5E">// order by </span>
     * <span style="color: #3F7E5E">//   case</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'WDL' then 0</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'FML' then 1</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'PRV' then 2</span>
     * <span style="color: #3F7E5E">//     else 3</span>
     * <span style="color: #3F7E5E">//   end asc, ...</span>
     * </pre>
     * <p>This function with Union is unsupported!</p>
     * <p>The order values are bound (treated as bind parameter).</p>
     * @param mob The bean of manual order containing order values. (NotNull)
     */
    public void withManualOrder(final ManualOrderBean mob) { // is user public!
        xdoWithManualOrder(mob);
    }

    // ===================================================================================
    //                                                                          Compatible
    //                                                                          ==========
    /**
     * Order along the list of manual values. #beforejava8 <br />
     * This function with Union is unsupported! <br />
     * The order values are bound (treated as bind parameter).
     * <pre>
     * MemberCB cb = new MemberCB();
     * List&lt;CDef.MemberStatus&gt; orderValueList = new ArrayList&lt;CDef.MemberStatus&gt;();
     * orderValueList.add(CDef.MemberStatus.Withdrawal);
     * orderValueList.add(CDef.MemberStatus.Formalized);
     * orderValueList.add(CDef.MemberStatus.Provisional);
     * cb.query().addOrderBy_MemberStatusCode_Asc().<span style="color: #DD4747">withManualOrder(orderValueList)</span>;
     * <span style="color: #3F7E5E">// order by </span>
     * <span style="color: #3F7E5E">//   case</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'WDL' then 0</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'FML' then 1</span>
     * <span style="color: #3F7E5E">//     when MEMBER_STATUS_CODE = 'PRV' then 2</span>
     * <span style="color: #3F7E5E">//     else 3</span>
     * <span style="color: #3F7E5E">//   end asc, ...</span>
     * </pre>
     * @param orderValueList The list of order values for manual ordering. (NotNull)
     */
    public void withManualOrder(final List<? extends Object> orderValueList) { // is user public!
        assertObjectNotNull("withManualOrder(orderValueList)", orderValueList);
        final ManualOrderBean manualOrderBean = new ManualOrderBean();
        manualOrderBean.acceptOrderValueList(orderValueList);
        withManualOrder(manualOrderBean);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    protected WebConfigToLabelTypeMappingCB newMyCB() {
        return new WebConfigToLabelTypeMappingCB();
    }

    // very internal (for suppressing warn about 'Not Use Import')
    protected String xabCQ() {
        return WebConfigToLabelTypeMappingCQ.class.getName();
    }

    protected String xabLSO() {
        return LikeSearchOption.class.getName();
    }

    protected String xabSSQS() {
        return HpSSQSetupper.class.getName();
    }
}
