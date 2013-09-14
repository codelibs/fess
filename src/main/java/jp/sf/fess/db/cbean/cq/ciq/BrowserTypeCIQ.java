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

import jp.sf.fess.db.cbean.BrowserTypeCB;
import jp.sf.fess.db.cbean.cq.BrowserTypeCQ;
import jp.sf.fess.db.cbean.cq.DataConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.bs.AbstractBsBrowserTypeCQ;
import jp.sf.fess.db.cbean.cq.bs.BsBrowserTypeCQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The condition-query for in-line of BROWSER_TYPE.
 * @author DBFlute(AutoGenerator)
 */
public class BrowserTypeCIQ extends AbstractBsBrowserTypeCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsBrowserTypeCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BrowserTypeCIQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel, final BsBrowserTypeCQ myCQ) {
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
    public String keepId_ExistsReferrer_DataConfigToBrowserTypeMappingList(
            final DataConfigToBrowserTypeMappingCQ sq) {
        throwIICBOE("ExistsReferrer");
        return null;
    }

    @Override
    public String keepId_ExistsReferrer_FileConfigToBrowserTypeMappingList(
            final FileConfigToBrowserTypeMappingCQ sq) {
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
    public String keepId_NotExistsReferrer_DataConfigToBrowserTypeMappingList(
            final DataConfigToBrowserTypeMappingCQ sq) {
        throwIICBOE("NotExistsReferrer");
        return null;
    }

    @Override
    public String keepId_NotExistsReferrer_FileConfigToBrowserTypeMappingList(
            final FileConfigToBrowserTypeMappingCQ sq) {
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
    public String keepId_InScopeRelation_DataConfigToBrowserTypeMappingList(
            final DataConfigToBrowserTypeMappingCQ sq) {
        return _myCQ
                .keepId_InScopeRelation_DataConfigToBrowserTypeMappingList(sq);
    }

    @Override
    public String keepId_InScopeRelation_FileConfigToBrowserTypeMappingList(
            final FileConfigToBrowserTypeMappingCQ sq) {
        return _myCQ
                .keepId_InScopeRelation_FileConfigToBrowserTypeMappingList(sq);
    }

    @Override
    public String keepId_InScopeRelation_WebConfigToBrowserTypeMappingList(
            final WebConfigToBrowserTypeMappingCQ sq) {
        return _myCQ
                .keepId_InScopeRelation_WebConfigToBrowserTypeMappingList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_DataConfigToBrowserTypeMappingList(
            final DataConfigToBrowserTypeMappingCQ sq) {
        return _myCQ
                .keepId_NotInScopeRelation_DataConfigToBrowserTypeMappingList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_FileConfigToBrowserTypeMappingList(
            final FileConfigToBrowserTypeMappingCQ sq) {
        return _myCQ
                .keepId_NotInScopeRelation_FileConfigToBrowserTypeMappingList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_WebConfigToBrowserTypeMappingList(
            final WebConfigToBrowserTypeMappingCQ sq) {
        return _myCQ
                .keepId_NotInScopeRelation_WebConfigToBrowserTypeMappingList(sq);
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_DataConfigToBrowserTypeMappingList(
            final DataConfigToBrowserTypeMappingCQ sq) {
        throwIICBOE("(Specify)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_FileConfigToBrowserTypeMappingList(
            final FileConfigToBrowserTypeMappingCQ sq) {
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
    public String keepId_QueryDerivedReferrer_DataConfigToBrowserTypeMappingList(
            final DataConfigToBrowserTypeMappingCQ sq) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_DataConfigToBrowserTypeMappingListParameter(
            final Object pv) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToBrowserTypeMappingList(
            final FileConfigToBrowserTypeMappingCQ sq) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToBrowserTypeMappingListParameter(
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
    protected ConditionValue getCValueName() {
        return _myCQ.getName();
    }

    @Override
    protected ConditionValue getCValueValue() {
        return _myCQ.getValue();
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
    public String keepScalarCondition(final BrowserTypeCQ subQuery) {
        throwIICBOE("ScalarCondition");
        return null;
    }

    @Override
    public String keepSpecifyMyselfDerived(final BrowserTypeCQ subQuery) {
        throwIICBOE("(Specify)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerived(final BrowserTypeCQ subQuery) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object parameterValue) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepMyselfExists(final BrowserTypeCQ subQuery) {
        throwIICBOE("MyselfExists");
        return null;
    }

    @Override
    public String keepMyselfInScope(final BrowserTypeCQ subQuery) {
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
        return BrowserTypeCB.class.getName();
    }

    protected String xinCQ() {
        return BrowserTypeCQ.class.getName();
    }
}
