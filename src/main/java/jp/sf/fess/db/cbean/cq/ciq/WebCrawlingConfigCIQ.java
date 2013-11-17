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

package jp.sf.fess.db.cbean.cq.ciq;

import java.util.Map;

import jp.sf.fess.db.cbean.WebCrawlingConfigCB;
import jp.sf.fess.db.cbean.cq.RequestHeaderCQ;
import jp.sf.fess.db.cbean.cq.WebAuthenticationCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.bs.AbstractBsWebCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.bs.BsWebCrawlingConfigCQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The condition-query for in-line of WEB_CRAWLING_CONFIG.
 * @author DBFlute(AutoGenerator)
 */
public class WebCrawlingConfigCIQ extends AbstractBsWebCrawlingConfigCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsWebCrawlingConfigCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public WebCrawlingConfigCIQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel, final BsWebCrawlingConfigCQ myCQ) {
        super(childQuery, sqlClause, aliasName, nestLevel);
        _myCQ = myCQ;
        _foreignPropertyName = _myCQ.xgetForeignPropertyName(); // accept foreign property name
        _relationPath = _myCQ.xgetRelationPath(); // accept relation path
        _inline = true;
    }

    // ===================================================================================
    //                                                             Override about Register
    //                                                             =======================
    @Override
    protected void reflectRelationOnUnionQuery(final ConditionQuery bq,
            final ConditionQuery uq) {
        final String msg = "InlineView must not need UNION method: " + bq
                + " : " + uq;
        throw new IllegalConditionBeanOperationException(msg);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(
            final ConditionKey k, final Object v, final ConditionValue cv,
            final String col) {
        regIQ(k, v, cv, col);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(
            final ConditionKey k, final Object v, final ConditionValue cv,
            final String col, final ConditionOption op) {
        regIQ(k, v, cv, col, op);
    }

    @Override
    protected void registerWhereClause(final String wc) {
        registerInlineWhereClause(wc);
    }

    @Override
    protected boolean isInScopeRelationSuppressLocalAliasName() {
        if (_onClause) {
            throw new IllegalConditionBeanOperationException(
                    "InScopeRelation on OnClause is unsupported.");
        }
        return true;
    }

    // ===================================================================================
    //                                                                Override about Query
    //                                                                ====================
    @Override
    protected ConditionValue getCValueId() {
        return _myCQ.getId();
    }

    @Override
    public String keepId_ExistsReferrer_RequestHeaderList(
            final RequestHeaderCQ sq) {
        throwIICBOE("ExistsReferrer");
        return null;
    }

    @Override
    public String keepId_ExistsReferrer_WebAuthenticationList(
            final WebAuthenticationCQ sq) {
        throwIICBOE("ExistsReferrer");
        return null;
    }

    @Override
    public String keepId_ExistsReferrer_WebConfigToBrowserTypeMappingList(
            final WebConfigToBrowserTypeMappingCQ sq) {
        throwIICBOE("ExistsReferrer");
        return null;
    }

    @Override
    public String keepId_ExistsReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        throwIICBOE("ExistsReferrer");
        return null;
    }

    @Override
    public String keepId_ExistsReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        throwIICBOE("ExistsReferrer");
        return null;
    }

    @Override
    public String keepId_NotExistsReferrer_RequestHeaderList(
            final RequestHeaderCQ sq) {
        throwIICBOE("NotExistsReferrer");
        return null;
    }

    @Override
    public String keepId_NotExistsReferrer_WebAuthenticationList(
            final WebAuthenticationCQ sq) {
        throwIICBOE("NotExistsReferrer");
        return null;
    }

    @Override
    public String keepId_NotExistsReferrer_WebConfigToBrowserTypeMappingList(
            final WebConfigToBrowserTypeMappingCQ sq) {
        throwIICBOE("NotExistsReferrer");
        return null;
    }

    @Override
    public String keepId_NotExistsReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        throwIICBOE("NotExistsReferrer");
        return null;
    }

    @Override
    public String keepId_NotExistsReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        throwIICBOE("NotExistsReferrer");
        return null;
    }

    @Override
    public String keepId_InScopeRelation_RequestHeaderList(
            final RequestHeaderCQ sq) {
        return _myCQ.keepId_InScopeRelation_RequestHeaderList(sq);
    }

    @Override
    public String keepId_InScopeRelation_WebAuthenticationList(
            final WebAuthenticationCQ sq) {
        return _myCQ.keepId_InScopeRelation_WebAuthenticationList(sq);
    }

    @Override
    public String keepId_InScopeRelation_WebConfigToBrowserTypeMappingList(
            final WebConfigToBrowserTypeMappingCQ sq) {
        return _myCQ
                .keepId_InScopeRelation_WebConfigToBrowserTypeMappingList(sq);
    }

    @Override
    public String keepId_InScopeRelation_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return _myCQ.keepId_InScopeRelation_WebConfigToLabelTypeMappingList(sq);
    }

    @Override
    public String keepId_InScopeRelation_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return _myCQ.keepId_InScopeRelation_WebConfigToRoleTypeMappingList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_RequestHeaderList(
            final RequestHeaderCQ sq) {
        return _myCQ.keepId_NotInScopeRelation_RequestHeaderList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_WebAuthenticationList(
            final WebAuthenticationCQ sq) {
        return _myCQ.keepId_NotInScopeRelation_WebAuthenticationList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_WebConfigToBrowserTypeMappingList(
            final WebConfigToBrowserTypeMappingCQ sq) {
        return _myCQ
                .keepId_NotInScopeRelation_WebConfigToBrowserTypeMappingList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return _myCQ
                .keepId_NotInScopeRelation_WebConfigToLabelTypeMappingList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return _myCQ
                .keepId_NotInScopeRelation_WebConfigToRoleTypeMappingList(sq);
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_RequestHeaderList(
            final RequestHeaderCQ sq) {
        throwIICBOE("(Specify)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_WebAuthenticationList(
            final WebAuthenticationCQ sq) {
        throwIICBOE("(Specify)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_WebConfigToBrowserTypeMappingList(
            final WebConfigToBrowserTypeMappingCQ sq) {
        throwIICBOE("(Specify)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        throwIICBOE("(Specify)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        throwIICBOE("(Specify)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_RequestHeaderList(
            final RequestHeaderCQ sq) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_RequestHeaderListParameter(
            final Object pv) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebAuthenticationList(
            final WebAuthenticationCQ sq) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebAuthenticationListParameter(
            final Object pv) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToBrowserTypeMappingList(
            final WebConfigToBrowserTypeMappingCQ sq) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToBrowserTypeMappingListParameter(
            final Object pv) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameter(
            final Object pv) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameter(
            final Object pv) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    protected ConditionValue getCValueName() {
        return _myCQ.getName();
    }

    @Override
    protected ConditionValue getCValueUrls() {
        return _myCQ.getUrls();
    }

    @Override
    protected ConditionValue getCValueIncludedUrls() {
        return _myCQ.getIncludedUrls();
    }

    @Override
    protected ConditionValue getCValueExcludedUrls() {
        return _myCQ.getExcludedUrls();
    }

    @Override
    protected ConditionValue getCValueIncludedDocUrls() {
        return _myCQ.getIncludedDocUrls();
    }

    @Override
    protected ConditionValue getCValueExcludedDocUrls() {
        return _myCQ.getExcludedDocUrls();
    }

    @Override
    protected ConditionValue getCValueConfigParameter() {
        return _myCQ.getConfigParameter();
    }

    @Override
    protected ConditionValue getCValueDepth() {
        return _myCQ.getDepth();
    }

    @Override
    protected ConditionValue getCValueMaxAccessCount() {
        return _myCQ.getMaxAccessCount();
    }

    @Override
    protected ConditionValue getCValueUserAgent() {
        return _myCQ.getUserAgent();
    }

    @Override
    protected ConditionValue getCValueNumOfThread() {
        return _myCQ.getNumOfThread();
    }

    @Override
    protected ConditionValue getCValueIntervalTime() {
        return _myCQ.getIntervalTime();
    }

    @Override
    protected ConditionValue getCValueBoost() {
        return _myCQ.getBoost();
    }

    @Override
    protected ConditionValue getCValueAvailable() {
        return _myCQ.getAvailable();
    }

    @Override
    protected ConditionValue getCValueSortOrder() {
        return _myCQ.getSortOrder();
    }

    @Override
    protected ConditionValue getCValueCreatedBy() {
        return _myCQ.getCreatedBy();
    }

    @Override
    protected ConditionValue getCValueCreatedTime() {
        return _myCQ.getCreatedTime();
    }

    @Override
    protected ConditionValue getCValueUpdatedBy() {
        return _myCQ.getUpdatedBy();
    }

    @Override
    protected ConditionValue getCValueUpdatedTime() {
        return _myCQ.getUpdatedTime();
    }

    @Override
    protected ConditionValue getCValueDeletedBy() {
        return _myCQ.getDeletedBy();
    }

    @Override
    protected ConditionValue getCValueDeletedTime() {
        return _myCQ.getDeletedTime();
    }

    @Override
    protected ConditionValue getCValueVersionNo() {
        return _myCQ.getVersionNo();
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    @Override
    public String keepScalarCondition(final WebCrawlingConfigCQ subQuery) {
        throwIICBOE("ScalarCondition");
        return null;
    }

    @Override
    public String keepSpecifyMyselfDerived(final WebCrawlingConfigCQ subQuery) {
        throwIICBOE("(Specify)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerived(final WebCrawlingConfigCQ subQuery) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object parameterValue) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepMyselfExists(final WebCrawlingConfigCQ subQuery) {
        throwIICBOE("MyselfExists");
        return null;
    }

    @Override
    public String keepMyselfInScope(final WebCrawlingConfigCQ subQuery) {
        throwIICBOE("MyselfInScope");
        return null;
    }

    protected void throwIICBOE(final String name) { // throwInlineIllegalConditionBeanOperationException()
        throw new IllegalConditionBeanOperationException(name
                + " at InlineView is unsupported.");
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xinCB() {
        return WebCrawlingConfigCB.class.getName();
    }

    protected String xinCQ() {
        return WebCrawlingConfigCQ.class.getName();
    }
}
