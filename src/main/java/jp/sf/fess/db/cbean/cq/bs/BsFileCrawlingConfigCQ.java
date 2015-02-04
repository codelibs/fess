/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import jp.sf.fess.db.cbean.FileCrawlingConfigCB;
import jp.sf.fess.db.cbean.cq.FileAuthenticationCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.ciq.FileCrawlingConfigCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of FILE_CRAWLING_CONFIG.
 * @author DBFlute(AutoGenerator)
 */
public class BsFileCrawlingConfigCQ extends AbstractBsFileCrawlingConfigCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected FileCrawlingConfigCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsFileCrawlingConfigCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from FILE_CRAWLING_CONFIG) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public FileCrawlingConfigCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected FileCrawlingConfigCIQ xcreateCIQ() {
        final FileCrawlingConfigCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected FileCrawlingConfigCIQ xnewCIQ() {
        return new FileCrawlingConfigCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join FILE_CRAWLING_CONFIG on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public FileCrawlingConfigCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final FileCrawlingConfigCIQ inlineQuery = inline();
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

    public Map<String, FileAuthenticationCQ> getId_ExistsReferrer_FileAuthenticationList() {
        return xgetSQueMap("id_ExistsReferrer_FileAuthenticationList");
    }

    @Override
    public String keepId_ExistsReferrer_FileAuthenticationList(
            final FileAuthenticationCQ sq) {
        return xkeepSQue("id_ExistsReferrer_FileAuthenticationList", sq);
    }

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_ExistsReferrer_FileConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_ExistsReferrer_FileConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_ExistsReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue("id_ExistsReferrer_FileConfigToLabelTypeMappingList",
                sq);
    }

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_ExistsReferrer_FileConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_ExistsReferrer_FileConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_ExistsReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_ExistsReferrer_FileConfigToRoleTypeMappingList",
                sq);
    }

    public Map<String, FileAuthenticationCQ> getId_NotExistsReferrer_FileAuthenticationList() {
        return xgetSQueMap("id_NotExistsReferrer_FileAuthenticationList");
    }

    @Override
    public String keepId_NotExistsReferrer_FileAuthenticationList(
            final FileAuthenticationCQ sq) {
        return xkeepSQue("id_NotExistsReferrer_FileAuthenticationList", sq);
    }

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_NotExistsReferrer_FileConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_NotExistsReferrer_FileConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_NotExistsReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotExistsReferrer_FileConfigToLabelTypeMappingList", sq);
    }

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_NotExistsReferrer_FileConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_NotExistsReferrer_FileConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_NotExistsReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotExistsReferrer_FileConfigToRoleTypeMappingList", sq);
    }

    public Map<String, FileAuthenticationCQ> getId_SpecifyDerivedReferrer_FileAuthenticationList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_FileAuthenticationList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_FileAuthenticationList(
            final FileAuthenticationCQ sq) {
        return xkeepSQue("id_SpecifyDerivedReferrer_FileAuthenticationList", sq);
    }

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingList",
                sq);
    }

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList", sq);
    }

    public Map<String, FileAuthenticationCQ> getId_InScopeRelation_FileAuthenticationList() {
        return xgetSQueMap("id_InScopeRelation_FileAuthenticationList");
    }

    @Override
    public String keepId_InScopeRelation_FileAuthenticationList(
            final FileAuthenticationCQ sq) {
        return xkeepSQue("id_InScopeRelation_FileAuthenticationList", sq);
    }

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_InScopeRelation_FileConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_InScopeRelation_FileConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_InScopeRelation_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue("id_InScopeRelation_FileConfigToLabelTypeMappingList",
                sq);
    }

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_InScopeRelation_FileConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_InScopeRelation_FileConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_InScopeRelation_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_InScopeRelation_FileConfigToRoleTypeMappingList",
                sq);
    }

    public Map<String, FileAuthenticationCQ> getId_NotInScopeRelation_FileAuthenticationList() {
        return xgetSQueMap("id_NotInScopeRelation_FileAuthenticationList");
    }

    @Override
    public String keepId_NotInScopeRelation_FileAuthenticationList(
            final FileAuthenticationCQ sq) {
        return xkeepSQue("id_NotInScopeRelation_FileAuthenticationList", sq);
    }

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_NotInScopeRelation_FileConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_NotInScopeRelation_FileConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_NotInScopeRelation_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotInScopeRelation_FileConfigToLabelTypeMappingList", sq);
    }

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_NotInScopeRelation_FileConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_NotInScopeRelation_FileConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_NotInScopeRelation_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotInScopeRelation_FileConfigToRoleTypeMappingList", sq);
    }

    public Map<String, FileAuthenticationCQ> getId_QueryDerivedReferrer_FileAuthenticationList() {
        return xgetSQueMap("id_QueryDerivedReferrer_FileAuthenticationList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileAuthenticationList(
            final FileAuthenticationCQ sq) {
        return xkeepSQue("id_QueryDerivedReferrer_FileAuthenticationList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_FileAuthenticationListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_FileAuthenticationList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileAuthenticationListParameter(
            final Object pm) {
        return xkeepSQuePm("id_QueryDerivedReferrer_FileAuthenticationList", pm);
    }

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_QueryDerivedReferrer_FileConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_QueryDerivedReferrer_FileConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_QueryDerivedReferrer_FileConfigToLabelTypeMappingList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_FileConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameter(
            final Object pm) {
        return xkeepSQuePm(
                "id_QueryDerivedReferrer_FileConfigToLabelTypeMappingList", pm);
    }

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_QueryDerivedReferrer_FileConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_QueryDerivedReferrer_FileConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_QueryDerivedReferrer_FileConfigToRoleTypeMappingList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_FileConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameter(
            final Object pm) {
        return xkeepSQuePm(
                "id_QueryDerivedReferrer_FileConfigToRoleTypeMappingList", pm);
    }

    /**
     * Add order-by as ascend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _name;

    public ConditionValue getName() {
        if (_name == null) {
            _name = nCV();
        }
        return _name;
    }

    @Override
    protected ConditionValue getCValueName() {
        return getName();
    }

    /**
     * Add order-by as ascend. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Name_Asc() {
        regOBA("NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Name_Desc() {
        regOBD("NAME");
        return this;
    }

    protected ConditionValue _paths;

    public ConditionValue getPaths() {
        if (_paths == null) {
            _paths = nCV();
        }
        return _paths;
    }

    @Override
    protected ConditionValue getCValuePaths() {
        return getPaths();
    }

    /**
     * Add order-by as ascend. <br />
     * PATHS: {NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Paths_Asc() {
        regOBA("PATHS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * PATHS: {NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Paths_Desc() {
        regOBD("PATHS");
        return this;
    }

    protected ConditionValue _includedPaths;

    public ConditionValue getIncludedPaths() {
        if (_includedPaths == null) {
            _includedPaths = nCV();
        }
        return _includedPaths;
    }

    @Override
    protected ConditionValue getCValueIncludedPaths() {
        return getIncludedPaths();
    }

    /**
     * Add order-by as ascend. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_IncludedPaths_Asc() {
        regOBA("INCLUDED_PATHS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_IncludedPaths_Desc() {
        regOBD("INCLUDED_PATHS");
        return this;
    }

    protected ConditionValue _excludedPaths;

    public ConditionValue getExcludedPaths() {
        if (_excludedPaths == null) {
            _excludedPaths = nCV();
        }
        return _excludedPaths;
    }

    @Override
    protected ConditionValue getCValueExcludedPaths() {
        return getExcludedPaths();
    }

    /**
     * Add order-by as ascend. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_ExcludedPaths_Asc() {
        regOBA("EXCLUDED_PATHS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_ExcludedPaths_Desc() {
        regOBD("EXCLUDED_PATHS");
        return this;
    }

    protected ConditionValue _includedDocPaths;

    public ConditionValue getIncludedDocPaths() {
        if (_includedDocPaths == null) {
            _includedDocPaths = nCV();
        }
        return _includedDocPaths;
    }

    @Override
    protected ConditionValue getCValueIncludedDocPaths() {
        return getIncludedDocPaths();
    }

    /**
     * Add order-by as ascend. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_IncludedDocPaths_Asc() {
        regOBA("INCLUDED_DOC_PATHS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * INCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_IncludedDocPaths_Desc() {
        regOBD("INCLUDED_DOC_PATHS");
        return this;
    }

    protected ConditionValue _excludedDocPaths;

    public ConditionValue getExcludedDocPaths() {
        if (_excludedDocPaths == null) {
            _excludedDocPaths = nCV();
        }
        return _excludedDocPaths;
    }

    @Override
    protected ConditionValue getCValueExcludedDocPaths() {
        return getExcludedDocPaths();
    }

    /**
     * Add order-by as ascend. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_ExcludedDocPaths_Asc() {
        regOBA("EXCLUDED_DOC_PATHS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * EXCLUDED_DOC_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_ExcludedDocPaths_Desc() {
        regOBD("EXCLUDED_DOC_PATHS");
        return this;
    }

    protected ConditionValue _configParameter;

    public ConditionValue getConfigParameter() {
        if (_configParameter == null) {
            _configParameter = nCV();
        }
        return _configParameter;
    }

    @Override
    protected ConditionValue getCValueConfigParameter() {
        return getConfigParameter();
    }

    /**
     * Add order-by as ascend. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_ConfigParameter_Asc() {
        regOBA("CONFIG_PARAMETER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_ConfigParameter_Desc() {
        regOBD("CONFIG_PARAMETER");
        return this;
    }

    protected ConditionValue _depth;

    public ConditionValue getDepth() {
        if (_depth == null) {
            _depth = nCV();
        }
        return _depth;
    }

    @Override
    protected ConditionValue getCValueDepth() {
        return getDepth();
    }

    /**
     * Add order-by as ascend. <br />
     * DEPTH: {INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Depth_Asc() {
        regOBA("DEPTH");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DEPTH: {INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Depth_Desc() {
        regOBD("DEPTH");
        return this;
    }

    protected ConditionValue _maxAccessCount;

    public ConditionValue getMaxAccessCount() {
        if (_maxAccessCount == null) {
            _maxAccessCount = nCV();
        }
        return _maxAccessCount;
    }

    @Override
    protected ConditionValue getCValueMaxAccessCount() {
        return getMaxAccessCount();
    }

    /**
     * Add order-by as ascend. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_MaxAccessCount_Asc() {
        regOBA("MAX_ACCESS_COUNT");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_MaxAccessCount_Desc() {
        regOBD("MAX_ACCESS_COUNT");
        return this;
    }

    protected ConditionValue _numOfThread;

    public ConditionValue getNumOfThread() {
        if (_numOfThread == null) {
            _numOfThread = nCV();
        }
        return _numOfThread;
    }

    @Override
    protected ConditionValue getCValueNumOfThread() {
        return getNumOfThread();
    }

    /**
     * Add order-by as ascend. <br />
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_NumOfThread_Asc() {
        regOBA("NUM_OF_THREAD");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_NumOfThread_Desc() {
        regOBD("NUM_OF_THREAD");
        return this;
    }

    protected ConditionValue _intervalTime;

    public ConditionValue getIntervalTime() {
        if (_intervalTime == null) {
            _intervalTime = nCV();
        }
        return _intervalTime;
    }

    @Override
    protected ConditionValue getCValueIntervalTime() {
        return getIntervalTime();
    }

    /**
     * Add order-by as ascend. <br />
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_IntervalTime_Asc() {
        regOBA("INTERVAL_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_IntervalTime_Desc() {
        regOBD("INTERVAL_TIME");
        return this;
    }

    protected ConditionValue _boost;

    public ConditionValue getBoost() {
        if (_boost == null) {
            _boost = nCV();
        }
        return _boost;
    }

    @Override
    protected ConditionValue getCValueBoost() {
        return getBoost();
    }

    /**
     * Add order-by as ascend. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Boost_Asc() {
        regOBA("BOOST");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Boost_Desc() {
        regOBD("BOOST");
        return this;
    }

    protected ConditionValue _available;

    public ConditionValue getAvailable() {
        if (_available == null) {
            _available = nCV();
        }
        return _available;
    }

    @Override
    protected ConditionValue getCValueAvailable() {
        return getAvailable();
    }

    /**
     * Add order-by as ascend. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Available_Asc() {
        regOBA("AVAILABLE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_Available_Desc() {
        regOBD("AVAILABLE");
        return this;
    }

    protected ConditionValue _sortOrder;

    public ConditionValue getSortOrder() {
        if (_sortOrder == null) {
            _sortOrder = nCV();
        }
        return _sortOrder;
    }

    @Override
    protected ConditionValue getCValueSortOrder() {
        return getSortOrder();
    }

    /**
     * Add order-by as ascend. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_SortOrder_Asc() {
        regOBA("SORT_ORDER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_SortOrder_Desc() {
        regOBD("SORT_ORDER");
        return this;
    }

    protected ConditionValue _createdBy;

    public ConditionValue getCreatedBy() {
        if (_createdBy == null) {
            _createdBy = nCV();
        }
        return _createdBy;
    }

    @Override
    protected ConditionValue getCValueCreatedBy() {
        return getCreatedBy();
    }

    /**
     * Add order-by as ascend. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_CreatedBy_Asc() {
        regOBA("CREATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_CreatedBy_Desc() {
        regOBD("CREATED_BY");
        return this;
    }

    protected ConditionValue _createdTime;

    public ConditionValue getCreatedTime() {
        if (_createdTime == null) {
            _createdTime = nCV();
        }
        return _createdTime;
    }

    @Override
    protected ConditionValue getCValueCreatedTime() {
        return getCreatedTime();
    }

    /**
     * Add order-by as ascend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_CreatedTime_Asc() {
        regOBA("CREATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_CreatedTime_Desc() {
        regOBD("CREATED_TIME");
        return this;
    }

    protected ConditionValue _updatedBy;

    public ConditionValue getUpdatedBy() {
        if (_updatedBy == null) {
            _updatedBy = nCV();
        }
        return _updatedBy;
    }

    @Override
    protected ConditionValue getCValueUpdatedBy() {
        return getUpdatedBy();
    }

    /**
     * Add order-by as ascend. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("UPDATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_UpdatedBy_Desc() {
        regOBD("UPDATED_BY");
        return this;
    }

    protected ConditionValue _updatedTime;

    public ConditionValue getUpdatedTime() {
        if (_updatedTime == null) {
            _updatedTime = nCV();
        }
        return _updatedTime;
    }

    @Override
    protected ConditionValue getCValueUpdatedTime() {
        return getUpdatedTime();
    }

    /**
     * Add order-by as ascend. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("UPDATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("UPDATED_TIME");
        return this;
    }

    protected ConditionValue _deletedBy;

    public ConditionValue getDeletedBy() {
        if (_deletedBy == null) {
            _deletedBy = nCV();
        }
        return _deletedBy;
    }

    @Override
    protected ConditionValue getCValueDeletedBy() {
        return getDeletedBy();
    }

    /**
     * Add order-by as ascend. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_DeletedBy_Asc() {
        regOBA("DELETED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_DeletedBy_Desc() {
        regOBD("DELETED_BY");
        return this;
    }

    protected ConditionValue _deletedTime;

    public ConditionValue getDeletedTime() {
        if (_deletedTime == null) {
            _deletedTime = nCV();
        }
        return _deletedTime;
    }

    @Override
    protected ConditionValue getCValueDeletedTime() {
        return getDeletedTime();
    }

    /**
     * Add order-by as ascend. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_DeletedTime_Asc() {
        regOBA("DELETED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_DeletedTime_Desc() {
        regOBD("DELETED_TIME");
        return this;
    }

    protected ConditionValue _versionNo;

    public ConditionValue getVersionNo() {
        if (_versionNo == null) {
            _versionNo = nCV();
        }
        return _versionNo;
    }

    @Override
    protected ConditionValue getCValueVersionNo() {
        return getVersionNo();
    }

    /**
     * Add order-by as ascend. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_VersionNo_Asc() {
        regOBA("VERSION_NO");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsFileCrawlingConfigCQ addOrderBy_VersionNo_Desc() {
        regOBD("VERSION_NO");
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
    public BsFileCrawlingConfigCQ addSpecifiedDerivedOrderBy_Asc(
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
    public BsFileCrawlingConfigCQ addSpecifiedDerivedOrderBy_Desc(
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
    public Map<String, FileCrawlingConfigCQ> getScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final FileCrawlingConfigCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, FileCrawlingConfigCQ> getSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(final FileCrawlingConfigCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, FileCrawlingConfigCQ> getQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final FileCrawlingConfigCQ sq) {
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
    protected Map<String, FileCrawlingConfigCQ> _myselfExistsMap;

    public Map<String, FileCrawlingConfigCQ> getMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final FileCrawlingConfigCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, FileCrawlingConfigCQ> getMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    @Override
    public String keepMyselfInScope(final FileCrawlingConfigCQ sq) {
        return xkeepSQue("myselfInScope", sq);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return FileCrawlingConfigCB.class.getName();
    }

    protected String xCQ() {
        return FileCrawlingConfigCQ.class.getName();
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
