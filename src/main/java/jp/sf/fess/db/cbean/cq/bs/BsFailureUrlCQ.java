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

import java.util.Map;

import jp.sf.fess.db.cbean.FailureUrlCB;
import jp.sf.fess.db.cbean.cq.FailureUrlCQ;
import jp.sf.fess.db.cbean.cq.FileCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.WebCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.ciq.FailureUrlCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
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
    public BsFailureUrlCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from FAILURE_URL) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
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
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
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
     * URL: {IX, NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_Url_Asc() {
        regOBA("URL");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * URL: {IX, NotNull, VARCHAR(4000)}
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
     * ERROR_NAME: {IX+, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_ErrorName_Asc() {
        regOBA("ERROR_NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ERROR_NAME: {IX+, VARCHAR(255)}
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
     * ERROR_COUNT: {IX+, NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_ErrorCount_Asc() {
        regOBA("ERROR_COUNT");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ERROR_COUNT: {IX+, NotNull, INTEGER(10)}
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
     * LAST_ACCESS_TIME: {IX+, NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_LastAccessTime_Asc() {
        regOBA("LAST_ACCESS_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * LAST_ACCESS_TIME: {IX+, NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_LastAccessTime_Desc() {
        regOBD("LAST_ACCESS_TIME");
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

    protected Map<String, WebCrawlingConfigCQ> _webConfigId_InScopeRelation_WebCrawlingConfigMap;

    public Map<String, WebCrawlingConfigCQ> getWebConfigId_InScopeRelation_WebCrawlingConfig() {
        return _webConfigId_InScopeRelation_WebCrawlingConfigMap;
    }

    @Override
    public String keepWebConfigId_InScopeRelation_WebCrawlingConfig(
            final WebCrawlingConfigCQ subQuery) {
        if (_webConfigId_InScopeRelation_WebCrawlingConfigMap == null) {
            _webConfigId_InScopeRelation_WebCrawlingConfigMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_webConfigId_InScopeRelation_WebCrawlingConfigMap.size() + 1);
        _webConfigId_InScopeRelation_WebCrawlingConfigMap.put(key, subQuery);
        return "webConfigId_InScopeRelation_WebCrawlingConfig." + key;
    }

    protected Map<String, WebCrawlingConfigCQ> _webConfigId_NotInScopeRelation_WebCrawlingConfigMap;

    public Map<String, WebCrawlingConfigCQ> getWebConfigId_NotInScopeRelation_WebCrawlingConfig() {
        return _webConfigId_NotInScopeRelation_WebCrawlingConfigMap;
    }

    @Override
    public String keepWebConfigId_NotInScopeRelation_WebCrawlingConfig(
            final WebCrawlingConfigCQ subQuery) {
        if (_webConfigId_NotInScopeRelation_WebCrawlingConfigMap == null) {
            _webConfigId_NotInScopeRelation_WebCrawlingConfigMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_webConfigId_NotInScopeRelation_WebCrawlingConfigMap.size() + 1);
        _webConfigId_NotInScopeRelation_WebCrawlingConfigMap.put(key, subQuery);
        return "webConfigId_NotInScopeRelation_WebCrawlingConfig." + key;
    }

    /** 
     * Add order-by as ascend. <br />
     * WEB_CONFIG_ID: {IX, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_WebConfigId_Asc() {
        regOBA("WEB_CONFIG_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * WEB_CONFIG_ID: {IX, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_WebConfigId_Desc() {
        regOBD("WEB_CONFIG_ID");
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

    protected Map<String, FileCrawlingConfigCQ> _fileConfigId_InScopeRelation_FileCrawlingConfigMap;

    public Map<String, FileCrawlingConfigCQ> getFileConfigId_InScopeRelation_FileCrawlingConfig() {
        return _fileConfigId_InScopeRelation_FileCrawlingConfigMap;
    }

    @Override
    public String keepFileConfigId_InScopeRelation_FileCrawlingConfig(
            final FileCrawlingConfigCQ subQuery) {
        if (_fileConfigId_InScopeRelation_FileCrawlingConfigMap == null) {
            _fileConfigId_InScopeRelation_FileCrawlingConfigMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_fileConfigId_InScopeRelation_FileCrawlingConfigMap.size() + 1);
        _fileConfigId_InScopeRelation_FileCrawlingConfigMap.put(key, subQuery);
        return "fileConfigId_InScopeRelation_FileCrawlingConfig." + key;
    }

    protected Map<String, FileCrawlingConfigCQ> _fileConfigId_NotInScopeRelation_FileCrawlingConfigMap;

    public Map<String, FileCrawlingConfigCQ> getFileConfigId_NotInScopeRelation_FileCrawlingConfig() {
        return _fileConfigId_NotInScopeRelation_FileCrawlingConfigMap;
    }

    @Override
    public String keepFileConfigId_NotInScopeRelation_FileCrawlingConfig(
            final FileCrawlingConfigCQ subQuery) {
        if (_fileConfigId_NotInScopeRelation_FileCrawlingConfigMap == null) {
            _fileConfigId_NotInScopeRelation_FileCrawlingConfigMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_fileConfigId_NotInScopeRelation_FileCrawlingConfigMap
                        .size() + 1);
        _fileConfigId_NotInScopeRelation_FileCrawlingConfigMap.put(key,
                subQuery);
        return "fileConfigId_NotInScopeRelation_FileCrawlingConfig." + key;
    }

    /** 
     * Add order-by as ascend. <br />
     * FILE_CONFIG_ID: {IX, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_FileConfigId_Asc() {
        regOBA("FILE_CONFIG_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * FILE_CONFIG_ID: {IX, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsFailureUrlCQ addOrderBy_FileConfigId_Desc() {
        regOBD("FILE_CONFIG_ID");
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
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #FD4747">aliasName</span>);
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
    protected void reflectRelationOnUnionQuery(
            final ConditionQuery baseQueryAsSuper,
            final ConditionQuery unionQueryAsSuper) {
        final FailureUrlCQ baseQuery = (FailureUrlCQ) baseQueryAsSuper;
        final FailureUrlCQ unionQuery = (FailureUrlCQ) unionQueryAsSuper;
        if (baseQuery.hasConditionQueryFileCrawlingConfig()) {
            unionQuery.queryFileCrawlingConfig().reflectRelationOnUnionQuery(
                    baseQuery.queryFileCrawlingConfig(),
                    unionQuery.queryFileCrawlingConfig());
        }
        if (baseQuery.hasConditionQueryWebCrawlingConfig()) {
            unionQuery.queryWebCrawlingConfig().reflectRelationOnUnionQuery(
                    baseQuery.queryWebCrawlingConfig(),
                    unionQuery.queryWebCrawlingConfig());
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

    protected FileCrawlingConfigCQ _conditionQueryFileCrawlingConfig;

    public FileCrawlingConfigCQ getConditionQueryFileCrawlingConfig() {
        if (_conditionQueryFileCrawlingConfig == null) {
            _conditionQueryFileCrawlingConfig = xcreateQueryFileCrawlingConfig();
            xsetupOuterJoinFileCrawlingConfig();
        }
        return _conditionQueryFileCrawlingConfig;
    }

    protected FileCrawlingConfigCQ xcreateQueryFileCrawlingConfig() {
        final String nrp = resolveNextRelationPath("FAILURE_URL",
                "fileCrawlingConfig");
        final String jan = resolveJoinAliasName(nrp, xgetNextNestLevel());
        final FileCrawlingConfigCQ cq = new FileCrawlingConfigCQ(this,
                xgetSqlClause(), jan, xgetNextNestLevel());
        cq.xsetBaseCB(_baseCB);
        cq.xsetForeignPropertyName("fileCrawlingConfig");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinFileCrawlingConfig() {
        final FileCrawlingConfigCQ cq = getConditionQueryFileCrawlingConfig();
        final Map<String, String> joinOnMap = newLinkedHashMapSized(4);
        joinOnMap.put("FILE_CONFIG_ID", "ID");
        registerOuterJoin(cq, joinOnMap, "fileCrawlingConfig");
    }

    public boolean hasConditionQueryFileCrawlingConfig() {
        return _conditionQueryFileCrawlingConfig != null;
    }

    /**
     * Get the condition-query for relation table. <br />
     * WEB_CRAWLING_CONFIG by my WEB_CONFIG_ID, named 'webCrawlingConfig'.
     * @return The instance of condition-query. (NotNull)
     */
    public WebCrawlingConfigCQ queryWebCrawlingConfig() {
        return getConditionQueryWebCrawlingConfig();
    }

    protected WebCrawlingConfigCQ _conditionQueryWebCrawlingConfig;

    public WebCrawlingConfigCQ getConditionQueryWebCrawlingConfig() {
        if (_conditionQueryWebCrawlingConfig == null) {
            _conditionQueryWebCrawlingConfig = xcreateQueryWebCrawlingConfig();
            xsetupOuterJoinWebCrawlingConfig();
        }
        return _conditionQueryWebCrawlingConfig;
    }

    protected WebCrawlingConfigCQ xcreateQueryWebCrawlingConfig() {
        final String nrp = resolveNextRelationPath("FAILURE_URL",
                "webCrawlingConfig");
        final String jan = resolveJoinAliasName(nrp, xgetNextNestLevel());
        final WebCrawlingConfigCQ cq = new WebCrawlingConfigCQ(this,
                xgetSqlClause(), jan, xgetNextNestLevel());
        cq.xsetBaseCB(_baseCB);
        cq.xsetForeignPropertyName("webCrawlingConfig");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinWebCrawlingConfig() {
        final WebCrawlingConfigCQ cq = getConditionQueryWebCrawlingConfig();
        final Map<String, String> joinOnMap = newLinkedHashMapSized(4);
        joinOnMap.put("WEB_CONFIG_ID", "ID");
        registerOuterJoin(cq, joinOnMap, "webCrawlingConfig");
    }

    public boolean hasConditionQueryWebCrawlingConfig() {
        return _conditionQueryWebCrawlingConfig != null;
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    protected Map<String, FailureUrlCQ> _scalarConditionMap;

    public Map<String, FailureUrlCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final FailureUrlCQ subQuery) {
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
    protected Map<String, FailureUrlCQ> _specifyMyselfDerivedMap;

    public Map<String, FailureUrlCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final FailureUrlCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, FailureUrlCQ> _queryMyselfDerivedMap;

    public Map<String, FailureUrlCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final FailureUrlCQ subQuery) {
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
    protected Map<String, FailureUrlCQ> _myselfExistsMap;

    public Map<String, FailureUrlCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final FailureUrlCQ subQuery) {
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
    protected Map<String, FailureUrlCQ> _myselfInScopeMap;

    public Map<String, FailureUrlCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final FailureUrlCQ subQuery) {
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
        return FailureUrlCB.class.getName();
    }

    protected String xCQ() {
        return FailureUrlCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
