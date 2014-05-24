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

import jp.sf.fess.db.cbean.JobLogCB;
import jp.sf.fess.db.cbean.cq.JobLogCQ;
import jp.sf.fess.db.cbean.cq.ciq.JobLogCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of JOB_LOG.
 * @author DBFlute(AutoGenerator)
 */
public class BsJobLogCQ extends AbstractBsJobLogCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected JobLogCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsJobLogCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from JOB_LOG) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public JobLogCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected JobLogCIQ xcreateCIQ() {
        final JobLogCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected JobLogCIQ xnewCIQ() {
        return new JobLogCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join JOB_LOG on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public JobLogCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final JobLogCIQ inlineQuery = inline();
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
    public BsJobLogCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _jobName;

    public ConditionValue getJobName() {
        if (_jobName == null) {
            _jobName = nCV();
        }
        return _jobName;
    }

    @Override
    protected ConditionValue getCValueJobName() {
        return getJobName();
    }

    /**
     * Add order-by as ascend. <br />
     * JOB_NAME: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_JobName_Asc() {
        regOBA("JOB_NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * JOB_NAME: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_JobName_Desc() {
        regOBD("JOB_NAME");
        return this;
    }

    protected ConditionValue _jobStatus;

    public ConditionValue getJobStatus() {
        if (_jobStatus == null) {
            _jobStatus = nCV();
        }
        return _jobStatus;
    }

    @Override
    protected ConditionValue getCValueJobStatus() {
        return getJobStatus();
    }

    /**
     * Add order-by as ascend. <br />
     * JOB_STATUS: {NotNull, VARCHAR(10)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_JobStatus_Asc() {
        regOBA("JOB_STATUS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * JOB_STATUS: {NotNull, VARCHAR(10)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_JobStatus_Desc() {
        regOBD("JOB_STATUS");
        return this;
    }

    protected ConditionValue _target;

    public ConditionValue getTarget() {
        if (_target == null) {
            _target = nCV();
        }
        return _target;
    }

    @Override
    protected ConditionValue getCValueTarget() {
        return getTarget();
    }

    /**
     * Add order-by as ascend. <br />
     * TARGET: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_Target_Asc() {
        regOBA("TARGET");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * TARGET: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_Target_Desc() {
        regOBD("TARGET");
        return this;
    }

    protected ConditionValue _scriptType;

    public ConditionValue getScriptType() {
        if (_scriptType == null) {
            _scriptType = nCV();
        }
        return _scriptType;
    }

    @Override
    protected ConditionValue getCValueScriptType() {
        return getScriptType();
    }

    /**
     * Add order-by as ascend. <br />
     * SCRIPT_TYPE: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_ScriptType_Asc() {
        regOBA("SCRIPT_TYPE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SCRIPT_TYPE: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_ScriptType_Desc() {
        regOBD("SCRIPT_TYPE");
        return this;
    }

    protected ConditionValue _scriptData;

    public ConditionValue getScriptData() {
        if (_scriptData == null) {
            _scriptData = nCV();
        }
        return _scriptData;
    }

    @Override
    protected ConditionValue getCValueScriptData() {
        return getScriptData();
    }

    /**
     * Add order-by as ascend. <br />
     * SCRIPT_DATA: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_ScriptData_Asc() {
        regOBA("SCRIPT_DATA");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SCRIPT_DATA: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_ScriptData_Desc() {
        regOBD("SCRIPT_DATA");
        return this;
    }

    protected ConditionValue _scriptResult;

    public ConditionValue getScriptResult() {
        if (_scriptResult == null) {
            _scriptResult = nCV();
        }
        return _scriptResult;
    }

    @Override
    protected ConditionValue getCValueScriptResult() {
        return getScriptResult();
    }

    /**
     * Add order-by as ascend. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_ScriptResult_Asc() {
        regOBA("SCRIPT_RESULT");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SCRIPT_RESULT: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_ScriptResult_Desc() {
        regOBD("SCRIPT_RESULT");
        return this;
    }

    protected ConditionValue _startTime;

    public ConditionValue getStartTime() {
        if (_startTime == null) {
            _startTime = nCV();
        }
        return _startTime;
    }

    @Override
    protected ConditionValue getCValueStartTime() {
        return getStartTime();
    }

    /**
     * Add order-by as ascend. <br />
     * START_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_StartTime_Asc() {
        regOBA("START_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * START_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_StartTime_Desc() {
        regOBD("START_TIME");
        return this;
    }

    protected ConditionValue _endTime;

    public ConditionValue getEndTime() {
        if (_endTime == null) {
            _endTime = nCV();
        }
        return _endTime;
    }

    @Override
    protected ConditionValue getCValueEndTime() {
        return getEndTime();
    }

    /**
     * Add order-by as ascend. <br />
     * END_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_EndTime_Asc() {
        regOBA("END_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * END_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsJobLogCQ addOrderBy_EndTime_Desc() {
        regOBD("END_TIME");
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
    public BsJobLogCQ addSpecifiedDerivedOrderBy_Asc(final String aliasName) {
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
    public BsJobLogCQ addSpecifiedDerivedOrderBy_Desc(final String aliasName) {
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
    protected Map<String, JobLogCQ> _scalarConditionMap;

    public Map<String, JobLogCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final JobLogCQ subQuery) {
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
    protected Map<String, JobLogCQ> _specifyMyselfDerivedMap;

    public Map<String, JobLogCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final JobLogCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, JobLogCQ> _queryMyselfDerivedMap;

    public Map<String, JobLogCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final JobLogCQ subQuery) {
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
    protected Map<String, JobLogCQ> _myselfExistsMap;

    public Map<String, JobLogCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final JobLogCQ subQuery) {
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
    protected Map<String, JobLogCQ> _myselfInScopeMap;

    public Map<String, JobLogCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final JobLogCQ subQuery) {
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
        return JobLogCB.class.getName();
    }

    protected String xCQ() {
        return JobLogCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
