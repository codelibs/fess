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

import jp.sf.fess.db.cbean.WebCrawlingConfigCB;
import jp.sf.fess.db.cbean.cq.RequestHeaderCQ;
import jp.sf.fess.db.cbean.cq.WebAuthenticationCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.ciq.WebCrawlingConfigCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of WEB_CRAWLING_CONFIG.
 * @author DBFlute(AutoGenerator)
 */
public class BsWebCrawlingConfigCQ extends AbstractBsWebCrawlingConfigCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected WebCrawlingConfigCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsWebCrawlingConfigCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from WEB_CRAWLING_CONFIG) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public WebCrawlingConfigCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected WebCrawlingConfigCIQ xcreateCIQ() {
        final WebCrawlingConfigCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected WebCrawlingConfigCIQ xnewCIQ() {
        return new WebCrawlingConfigCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join WEB_CRAWLING_CONFIG on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public WebCrawlingConfigCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final WebCrawlingConfigCIQ inlineQuery = inline();
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

    public Map<String, RequestHeaderCQ> getId_ExistsReferrer_RequestHeaderList() {
        return xgetSQueMap("id_ExistsReferrer_RequestHeaderList");
    }

    @Override
    public String keepId_ExistsReferrer_RequestHeaderList(
            final RequestHeaderCQ sq) {
        return xkeepSQue("id_ExistsReferrer_RequestHeaderList", sq);
    }

    public Map<String, WebAuthenticationCQ> getId_ExistsReferrer_WebAuthenticationList() {
        return xgetSQueMap("id_ExistsReferrer_WebAuthenticationList");
    }

    @Override
    public String keepId_ExistsReferrer_WebAuthenticationList(
            final WebAuthenticationCQ sq) {
        return xkeepSQue("id_ExistsReferrer_WebAuthenticationList", sq);
    }

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_ExistsReferrer_WebConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_ExistsReferrer_WebConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_ExistsReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue("id_ExistsReferrer_WebConfigToLabelTypeMappingList",
                sq);
    }

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_ExistsReferrer_WebConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_ExistsReferrer_WebConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_ExistsReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_ExistsReferrer_WebConfigToRoleTypeMappingList", sq);
    }

    public Map<String, RequestHeaderCQ> getId_NotExistsReferrer_RequestHeaderList() {
        return xgetSQueMap("id_NotExistsReferrer_RequestHeaderList");
    }

    @Override
    public String keepId_NotExistsReferrer_RequestHeaderList(
            final RequestHeaderCQ sq) {
        return xkeepSQue("id_NotExistsReferrer_RequestHeaderList", sq);
    }

    public Map<String, WebAuthenticationCQ> getId_NotExistsReferrer_WebAuthenticationList() {
        return xgetSQueMap("id_NotExistsReferrer_WebAuthenticationList");
    }

    @Override
    public String keepId_NotExistsReferrer_WebAuthenticationList(
            final WebAuthenticationCQ sq) {
        return xkeepSQue("id_NotExistsReferrer_WebAuthenticationList", sq);
    }

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_NotExistsReferrer_WebConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_NotExistsReferrer_WebConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_NotExistsReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotExistsReferrer_WebConfigToLabelTypeMappingList", sq);
    }

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_NotExistsReferrer_WebConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_NotExistsReferrer_WebConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_NotExistsReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_NotExistsReferrer_WebConfigToRoleTypeMappingList",
                sq);
    }

    public Map<String, RequestHeaderCQ> getId_SpecifyDerivedReferrer_RequestHeaderList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_RequestHeaderList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_RequestHeaderList(
            final RequestHeaderCQ sq) {
        return xkeepSQue("id_SpecifyDerivedReferrer_RequestHeaderList", sq);
    }

    public Map<String, WebAuthenticationCQ> getId_SpecifyDerivedReferrer_WebAuthenticationList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_WebAuthenticationList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_WebAuthenticationList(
            final WebAuthenticationCQ sq) {
        return xkeepSQue("id_SpecifyDerivedReferrer_WebAuthenticationList", sq);
    }

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingList", sq);
    }

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList", sq);
    }

    public Map<String, RequestHeaderCQ> getId_InScopeRelation_RequestHeaderList() {
        return xgetSQueMap("id_InScopeRelation_RequestHeaderList");
    }

    @Override
    public String keepId_InScopeRelation_RequestHeaderList(
            final RequestHeaderCQ sq) {
        return xkeepSQue("id_InScopeRelation_RequestHeaderList", sq);
    }

    public Map<String, WebAuthenticationCQ> getId_InScopeRelation_WebAuthenticationList() {
        return xgetSQueMap("id_InScopeRelation_WebAuthenticationList");
    }

    @Override
    public String keepId_InScopeRelation_WebAuthenticationList(
            final WebAuthenticationCQ sq) {
        return xkeepSQue("id_InScopeRelation_WebAuthenticationList", sq);
    }

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_InScopeRelation_WebConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_InScopeRelation_WebConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_InScopeRelation_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue("id_InScopeRelation_WebConfigToLabelTypeMappingList",
                sq);
    }

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_InScopeRelation_WebConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_InScopeRelation_WebConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_InScopeRelation_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_InScopeRelation_WebConfigToRoleTypeMappingList",
                sq);
    }

    public Map<String, RequestHeaderCQ> getId_NotInScopeRelation_RequestHeaderList() {
        return xgetSQueMap("id_NotInScopeRelation_RequestHeaderList");
    }

    @Override
    public String keepId_NotInScopeRelation_RequestHeaderList(
            final RequestHeaderCQ sq) {
        return xkeepSQue("id_NotInScopeRelation_RequestHeaderList", sq);
    }

    public Map<String, WebAuthenticationCQ> getId_NotInScopeRelation_WebAuthenticationList() {
        return xgetSQueMap("id_NotInScopeRelation_WebAuthenticationList");
    }

    @Override
    public String keepId_NotInScopeRelation_WebAuthenticationList(
            final WebAuthenticationCQ sq) {
        return xkeepSQue("id_NotInScopeRelation_WebAuthenticationList", sq);
    }

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_NotInScopeRelation_WebConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_NotInScopeRelation_WebConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_NotInScopeRelation_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotInScopeRelation_WebConfigToLabelTypeMappingList", sq);
    }

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_NotInScopeRelation_WebConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_NotInScopeRelation_WebConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_NotInScopeRelation_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotInScopeRelation_WebConfigToRoleTypeMappingList", sq);
    }

    public Map<String, RequestHeaderCQ> getId_QueryDerivedReferrer_RequestHeaderList() {
        return xgetSQueMap("id_QueryDerivedReferrer_RequestHeaderList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_RequestHeaderList(
            final RequestHeaderCQ sq) {
        return xkeepSQue("id_QueryDerivedReferrer_RequestHeaderList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_RequestHeaderListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_RequestHeaderList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_RequestHeaderListParameter(
            final Object pm) {
        return xkeepSQuePm("id_QueryDerivedReferrer_RequestHeaderList", pm);
    }

    public Map<String, WebAuthenticationCQ> getId_QueryDerivedReferrer_WebAuthenticationList() {
        return xgetSQueMap("id_QueryDerivedReferrer_WebAuthenticationList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebAuthenticationList(
            final WebAuthenticationCQ sq) {
        return xkeepSQue("id_QueryDerivedReferrer_WebAuthenticationList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_WebAuthenticationListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_WebAuthenticationList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebAuthenticationListParameter(
            final Object pm) {
        return xkeepSQuePm("id_QueryDerivedReferrer_WebAuthenticationList", pm);
    }

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_QueryDerivedReferrer_WebConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_QueryDerivedReferrer_WebConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_QueryDerivedReferrer_WebConfigToLabelTypeMappingList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_WebConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameter(
            final Object pm) {
        return xkeepSQuePm(
                "id_QueryDerivedReferrer_WebConfigToLabelTypeMappingList", pm);
    }

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_QueryDerivedReferrer_WebConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_QueryDerivedReferrer_WebConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_QueryDerivedReferrer_WebConfigToRoleTypeMappingList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_WebConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameter(
            final Object pm) {
        return xkeepSQuePm(
                "id_QueryDerivedReferrer_WebConfigToRoleTypeMappingList", pm);
    }

    /**
     * Add order-by as ascend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Id_Desc() {
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
    public BsWebCrawlingConfigCQ addOrderBy_Name_Asc() {
        regOBA("NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Name_Desc() {
        regOBD("NAME");
        return this;
    }

    protected ConditionValue _urls;

    public ConditionValue getUrls() {
        if (_urls == null) {
            _urls = nCV();
        }
        return _urls;
    }

    @Override
    protected ConditionValue getCValueUrls() {
        return getUrls();
    }

    /**
     * Add order-by as ascend. <br />
     * URLS: {NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Urls_Asc() {
        regOBA("URLS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * URLS: {NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Urls_Desc() {
        regOBD("URLS");
        return this;
    }

    protected ConditionValue _includedUrls;

    public ConditionValue getIncludedUrls() {
        if (_includedUrls == null) {
            _includedUrls = nCV();
        }
        return _includedUrls;
    }

    @Override
    protected ConditionValue getCValueIncludedUrls() {
        return getIncludedUrls();
    }

    /**
     * Add order-by as ascend. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_IncludedUrls_Asc() {
        regOBA("INCLUDED_URLS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * INCLUDED_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_IncludedUrls_Desc() {
        regOBD("INCLUDED_URLS");
        return this;
    }

    protected ConditionValue _excludedUrls;

    public ConditionValue getExcludedUrls() {
        if (_excludedUrls == null) {
            _excludedUrls = nCV();
        }
        return _excludedUrls;
    }

    @Override
    protected ConditionValue getCValueExcludedUrls() {
        return getExcludedUrls();
    }

    /**
     * Add order-by as ascend. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_ExcludedUrls_Asc() {
        regOBA("EXCLUDED_URLS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * EXCLUDED_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_ExcludedUrls_Desc() {
        regOBD("EXCLUDED_URLS");
        return this;
    }

    protected ConditionValue _includedDocUrls;

    public ConditionValue getIncludedDocUrls() {
        if (_includedDocUrls == null) {
            _includedDocUrls = nCV();
        }
        return _includedDocUrls;
    }

    @Override
    protected ConditionValue getCValueIncludedDocUrls() {
        return getIncludedDocUrls();
    }

    /**
     * Add order-by as ascend. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_IncludedDocUrls_Asc() {
        regOBA("INCLUDED_DOC_URLS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_IncludedDocUrls_Desc() {
        regOBD("INCLUDED_DOC_URLS");
        return this;
    }

    protected ConditionValue _excludedDocUrls;

    public ConditionValue getExcludedDocUrls() {
        if (_excludedDocUrls == null) {
            _excludedDocUrls = nCV();
        }
        return _excludedDocUrls;
    }

    @Override
    protected ConditionValue getCValueExcludedDocUrls() {
        return getExcludedDocUrls();
    }

    /**
     * Add order-by as ascend. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_ExcludedDocUrls_Asc() {
        regOBA("EXCLUDED_DOC_URLS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_ExcludedDocUrls_Desc() {
        regOBD("EXCLUDED_DOC_URLS");
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
    public BsWebCrawlingConfigCQ addOrderBy_ConfigParameter_Asc() {
        regOBA("CONFIG_PARAMETER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_ConfigParameter_Desc() {
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
    public BsWebCrawlingConfigCQ addOrderBy_Depth_Asc() {
        regOBA("DEPTH");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DEPTH: {INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Depth_Desc() {
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
    public BsWebCrawlingConfigCQ addOrderBy_MaxAccessCount_Asc() {
        regOBA("MAX_ACCESS_COUNT");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_MaxAccessCount_Desc() {
        regOBD("MAX_ACCESS_COUNT");
        return this;
    }

    protected ConditionValue _userAgent;

    public ConditionValue getUserAgent() {
        if (_userAgent == null) {
            _userAgent = nCV();
        }
        return _userAgent;
    }

    @Override
    protected ConditionValue getCValueUserAgent() {
        return getUserAgent();
    }

    /**
     * Add order-by as ascend. <br />
     * USER_AGENT: {NotNull, VARCHAR(200)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_UserAgent_Asc() {
        regOBA("USER_AGENT");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * USER_AGENT: {NotNull, VARCHAR(200)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_UserAgent_Desc() {
        regOBD("USER_AGENT");
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
    public BsWebCrawlingConfigCQ addOrderBy_NumOfThread_Asc() {
        regOBA("NUM_OF_THREAD");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_NumOfThread_Desc() {
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
    public BsWebCrawlingConfigCQ addOrderBy_IntervalTime_Asc() {
        regOBA("INTERVAL_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_IntervalTime_Desc() {
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
    public BsWebCrawlingConfigCQ addOrderBy_Boost_Asc() {
        regOBA("BOOST");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Boost_Desc() {
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
    public BsWebCrawlingConfigCQ addOrderBy_Available_Asc() {
        regOBA("AVAILABLE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_Available_Desc() {
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
    public BsWebCrawlingConfigCQ addOrderBy_SortOrder_Asc() {
        regOBA("SORT_ORDER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_SortOrder_Desc() {
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
    public BsWebCrawlingConfigCQ addOrderBy_CreatedBy_Asc() {
        regOBA("CREATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_CreatedBy_Desc() {
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
    public BsWebCrawlingConfigCQ addOrderBy_CreatedTime_Asc() {
        regOBA("CREATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_CreatedTime_Desc() {
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
    public BsWebCrawlingConfigCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("UPDATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_UpdatedBy_Desc() {
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
    public BsWebCrawlingConfigCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("UPDATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_UpdatedTime_Desc() {
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
    public BsWebCrawlingConfigCQ addOrderBy_DeletedBy_Asc() {
        regOBA("DELETED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_DeletedBy_Desc() {
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
    public BsWebCrawlingConfigCQ addOrderBy_DeletedTime_Asc() {
        regOBA("DELETED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_DeletedTime_Desc() {
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
    public BsWebCrawlingConfigCQ addOrderBy_VersionNo_Asc() {
        regOBA("VERSION_NO");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsWebCrawlingConfigCQ addOrderBy_VersionNo_Desc() {
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
    public BsWebCrawlingConfigCQ addSpecifiedDerivedOrderBy_Asc(
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
    public BsWebCrawlingConfigCQ addSpecifiedDerivedOrderBy_Desc(
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
    public Map<String, WebCrawlingConfigCQ> getScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final WebCrawlingConfigCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, WebCrawlingConfigCQ> getSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(final WebCrawlingConfigCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, WebCrawlingConfigCQ> getQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final WebCrawlingConfigCQ sq) {
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
    protected Map<String, WebCrawlingConfigCQ> _myselfExistsMap;

    public Map<String, WebCrawlingConfigCQ> getMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final WebCrawlingConfigCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, WebCrawlingConfigCQ> getMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    @Override
    public String keepMyselfInScope(final WebCrawlingConfigCQ sq) {
        return xkeepSQue("myselfInScope", sq);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return WebCrawlingConfigCB.class.getName();
    }

    protected String xCQ() {
        return WebCrawlingConfigCQ.class.getName();
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
