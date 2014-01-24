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

package jp.sf.fess.db.cbean.cq.ciq;

import java.util.Map;

import jp.sf.fess.db.cbean.LabelTypeCB;
import jp.sf.fess.db.cbean.cq.DataConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.LabelTypeCQ;
import jp.sf.fess.db.cbean.cq.LabelTypeToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.bs.AbstractBsLabelTypeCQ;
import jp.sf.fess.db.cbean.cq.bs.BsLabelTypeCQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The condition-query for in-line of LABEL_TYPE.
 * @author DBFlute(AutoGenerator)
 */
public class LabelTypeCIQ extends AbstractBsLabelTypeCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsLabelTypeCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public LabelTypeCIQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel, final BsLabelTypeCQ myCQ) {
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
    public String keepId_ExistsReferrer_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ sq) {
        throwIICBOE("ExistsReferrer");
        return null;
    }

    @Override
    public String keepId_ExistsReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        throwIICBOE("ExistsReferrer");
        return null;
    }

    @Override
    public String keepId_ExistsReferrer_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ sq) {
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
    public String keepId_NotExistsReferrer_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ sq) {
        throwIICBOE("NotExistsReferrer");
        return null;
    }

    @Override
    public String keepId_NotExistsReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        throwIICBOE("NotExistsReferrer");
        return null;
    }

    @Override
    public String keepId_NotExistsReferrer_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ sq) {
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
    public String keepId_InScopeRelation_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ sq) {
        return _myCQ
                .keepId_InScopeRelation_DataConfigToLabelTypeMappingList(sq);
    }

    @Override
    public String keepId_InScopeRelation_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        return _myCQ
                .keepId_InScopeRelation_FileConfigToLabelTypeMappingList(sq);
    }

    @Override
    public String keepId_InScopeRelation_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ sq) {
        return _myCQ.keepId_InScopeRelation_LabelTypeToRoleTypeMappingList(sq);
    }

    @Override
    public String keepId_InScopeRelation_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return _myCQ.keepId_InScopeRelation_WebConfigToLabelTypeMappingList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ sq) {
        return _myCQ
                .keepId_NotInScopeRelation_DataConfigToLabelTypeMappingList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        return _myCQ
                .keepId_NotInScopeRelation_FileConfigToLabelTypeMappingList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ sq) {
        return _myCQ
                .keepId_NotInScopeRelation_LabelTypeToRoleTypeMappingList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return _myCQ
                .keepId_NotInScopeRelation_WebConfigToLabelTypeMappingList(sq);
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ sq) {
        throwIICBOE("(Specify)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        throwIICBOE("(Specify)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ sq) {
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
    public String keepId_QueryDerivedReferrer_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ sq) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_DataConfigToLabelTypeMappingListParameter(
            final Object pv) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameter(
            final Object pv) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ sq) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListParameter(
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
    protected ConditionValue getCValueName() {
        return _myCQ.getName();
    }

    @Override
    protected ConditionValue getCValueValue() {
        return _myCQ.getValue();
    }

    @Override
    protected ConditionValue getCValueIncludedPaths() {
        return _myCQ.getIncludedPaths();
    }

    @Override
    protected ConditionValue getCValueExcludedPaths() {
        return _myCQ.getExcludedPaths();
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
    public String keepScalarCondition(final LabelTypeCQ subQuery) {
        throwIICBOE("ScalarCondition");
        return null;
    }

    @Override
    public String keepSpecifyMyselfDerived(final LabelTypeCQ subQuery) {
        throwIICBOE("(Specify)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerived(final LabelTypeCQ subQuery) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object parameterValue) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepMyselfExists(final LabelTypeCQ subQuery) {
        throwIICBOE("MyselfExists");
        return null;
    }

    @Override
    public String keepMyselfInScope(final LabelTypeCQ subQuery) {
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
        return LabelTypeCB.class.getName();
    }

    protected String xinCQ() {
        return LabelTypeCQ.class.getName();
    }
}
