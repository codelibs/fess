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

import jp.sf.fess.db.cbean.FailureUrlCB;
import jp.sf.fess.db.cbean.cq.FailureUrlCQ;
import jp.sf.fess.db.cbean.cq.ciq.FailureUrlCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of FAILURE_URL.
 * @author DBFlute(AutoGenerator)
 */
public class BsFailureUrlCQ extends AbstractBsFailureUrlCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected FailureUrlCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsFailureUrlCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from FAILURE_URL) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public FailureUrlCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected FailureUrlCIQ xcreateCIQ() {
        final FailureUrlCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected FailureUrlCIQ xnewCIQ() {
        return new FailureUrlCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join FAILURE_URL on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public FailureUrlCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final FailureUrlCIQ inlineQuery = inline();
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
    public BsFailureUrlCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _url;

    public ConditionValue getUrl() {
        if (_url == null) {
            _url = nCV();
        }
        return _url;
    }

    @Override
    protected ConditionValue getCValueUrl() {
        return getUrl();
    }

    /**
     * Add order-by as ascend. <br />
     * URL: {IX+, NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_Url_Asc() {
        regOBA("URL");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * URL: {IX+, NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_Url_Desc() {
        regOBD("URL");
        return this;
    }

    protected ConditionValue _threadName;

    public ConditionValue getThreadName() {
        if (_threadName == null) {
            _threadName = nCV();
        }
        return _threadName;
    }

    @Override
    protected ConditionValue getCValueThreadName() {
        return getThreadName();
    }

    /**
     * Add order-by as ascend. <br />
     * THREAD_NAME: {NotNull, VARCHAR(30)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_ThreadName_Asc() {
        regOBA("THREAD_NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * THREAD_NAME: {NotNull, VARCHAR(30)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_ThreadName_Desc() {
        regOBD("THREAD_NAME");
        return this;
    }

    protected ConditionValue _errorName;

    public ConditionValue getErrorName() {
        if (_errorName == null) {
            _errorName = nCV();
        }
        return _errorName;
    }

    @Override
    protected ConditionValue getCValueErrorName() {
        return getErrorName();
    }

    /**
     * Add order-by as ascend. <br />
     * ERROR_NAME: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_ErrorName_Asc() {
        regOBA("ERROR_NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ERROR_NAME: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_ErrorName_Desc() {
        regOBD("ERROR_NAME");
        return this;
    }

    protected ConditionValue _errorLog;

    public ConditionValue getErrorLog() {
        if (_errorLog == null) {
            _errorLog = nCV();
        }
        return _errorLog;
    }

    @Override
    protected ConditionValue getCValueErrorLog() {
        return getErrorLog();
    }

    /**
     * Add order-by as ascend. <br />
     * ERROR_LOG: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_ErrorLog_Asc() {
        regOBA("ERROR_LOG");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ERROR_LOG: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_ErrorLog_Desc() {
        regOBD("ERROR_LOG");
        return this;
    }

    protected ConditionValue _errorCount;

    public ConditionValue getErrorCount() {
        if (_errorCount == null) {
            _errorCount = nCV();
        }
        return _errorCount;
    }

    @Override
    protected ConditionValue getCValueErrorCount() {
        return getErrorCount();
    }

    /**
     * Add order-by as ascend. <br />
     * ERROR_COUNT: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_ErrorCount_Asc() {
        regOBA("ERROR_COUNT");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ERROR_COUNT: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_ErrorCount_Desc() {
        regOBD("ERROR_COUNT");
        return this;
    }

    protected ConditionValue _lastAccessTime;

    public ConditionValue getLastAccessTime() {
        if (_lastAccessTime == null) {
            _lastAccessTime = nCV();
        }
        return _lastAccessTime;
    }

    @Override
    protected ConditionValue getCValueLastAccessTime() {
        return getLastAccessTime();
    }

    /**
     * Add order-by as ascend. <br />
     * LAST_ACCESS_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_LastAccessTime_Asc() {
        regOBA("LAST_ACCESS_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * LAST_ACCESS_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_LastAccessTime_Desc() {
        regOBD("LAST_ACCESS_TIME");
        return this;
    }

    protected ConditionValue _configId;

    public ConditionValue getConfigId() {
        if (_configId == null) {
            _configId = nCV();
        }
        return _configId;
    }

    @Override
    protected ConditionValue getCValueConfigId() {
        return getConfigId();
    }

    /**
     * Add order-by as ascend. <br />
     * CONFIG_ID: {IX, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_ConfigId_Asc() {
        regOBA("CONFIG_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CONFIG_ID: {IX, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_ConfigId_Desc() {
        regOBD("CONFIG_ID");
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
    public BsFailureUrlCQ addSpecifiedDerivedOrderBy_Asc(final String aliasName) {
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
    public BsFailureUrlCQ addSpecifiedDerivedOrderBy_Desc(final String aliasName) {
        registerSpecifiedDerivedOrderBy_Desc(aliasName);
        return this;
    }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    @Override
    public void reflectRelationOnUnionQuery(final ConditionQuery bqs,
            final ConditionQuery uqs) {
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
    public Map<String, FailureUrlCQ> getScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final FailureUrlCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, FailureUrlCQ> getSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(final FailureUrlCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, FailureUrlCQ> getQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final FailureUrlCQ sq) {
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
    protected Map<String, FailureUrlCQ> _myselfExistsMap;

    public Map<String, FailureUrlCQ> getMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final FailureUrlCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, FailureUrlCQ> getMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    @Override
    public String keepMyselfInScope(final FailureUrlCQ sq) {
        return xkeepSQue("myselfInScope", sq);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return FailureUrlCB.class.getName();
    }

    protected String xCQ() {
        return FailureUrlCQ.class.getName();
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
