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

import jp.sf.fess.db.cbean.RoleTypeCB;
import jp.sf.fess.db.cbean.cq.DataConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.LabelTypeToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.RoleTypeCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.bs.AbstractBsRoleTypeCQ;
import jp.sf.fess.db.cbean.cq.bs.BsRoleTypeCQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The condition-query for in-line of ROLE_TYPE.
 * @author DBFlute(AutoGenerator)
 */
public class RoleTypeCIQ extends AbstractBsRoleTypeCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsRoleTypeCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public RoleTypeCIQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel, final BsRoleTypeCQ myCQ) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
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
        throw new IllegalConditionBeanOperationException(
                "InlineView cannot use Union: " + bq + " : " + uq);
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
    public String keepId_ExistsReferrer_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        throwIICBOE("ExistsReferrer");
        return null;
    }

    @Override
    public String keepId_ExistsReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
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
    public String keepId_ExistsReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        throwIICBOE("ExistsReferrer");
        return null;
    }

    @Override
    public String keepId_NotExistsReferrer_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        throwIICBOE("NotExistsReferrer");
        return null;
    }

    @Override
    public String keepId_NotExistsReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
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
    public String keepId_NotExistsReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        throwIICBOE("NotExistsReferrer");
        return null;
    }

    @Override
    public String keepId_InScopeRelation_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        return _myCQ.keepId_InScopeRelation_DataConfigToRoleTypeMappingList(sq);
    }

    @Override
    public String keepId_InScopeRelation_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
        return _myCQ.keepId_InScopeRelation_FileConfigToRoleTypeMappingList(sq);
    }

    @Override
    public String keepId_InScopeRelation_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ sq) {
        return _myCQ.keepId_InScopeRelation_LabelTypeToRoleTypeMappingList(sq);
    }

    @Override
    public String keepId_InScopeRelation_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return _myCQ.keepId_InScopeRelation_WebConfigToRoleTypeMappingList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        return _myCQ
                .keepId_NotInScopeRelation_DataConfigToRoleTypeMappingList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
        return _myCQ
                .keepId_NotInScopeRelation_FileConfigToRoleTypeMappingList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ sq) {
        return _myCQ
                .keepId_NotInScopeRelation_LabelTypeToRoleTypeMappingList(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return _myCQ
                .keepId_NotInScopeRelation_WebConfigToRoleTypeMappingList(sq);
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        throwIICBOE("(Specify)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
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
    public String keepId_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        throwIICBOE("(Specify)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_DataConfigToRoleTypeMappingListParameter(
            final Object vl) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
        throwIICBOE("(Query)DerivedReferrer");
        return null;
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameter(
            final Object vl) {
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
            final Object vl) {
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
            final Object vl) {
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
            final String pp) {
        return null;
    }

    @Override
    public String keepScalarCondition(final RoleTypeCQ sq) {
        throwIICBOE("ScalarCondition");
        return null;
    }

    @Override
    public String keepSpecifyMyselfDerived(final RoleTypeCQ sq) {
        throwIICBOE("(Specify)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerived(final RoleTypeCQ sq) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object vl) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepMyselfExists(final RoleTypeCQ sq) {
        throwIICBOE("MyselfExists");
        return null;
    }

    @Override
    public String keepMyselfInScope(final RoleTypeCQ sq) {
        throwIICBOE("MyselfInScope");
        return null;
    }

    protected void throwIICBOE(final String name) {
        throw new IllegalConditionBeanOperationException(name
                + " at InlineView is unsupported.");
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xinCB() {
        return RoleTypeCB.class.getName();
    }

    protected String xinCQ() {
        return RoleTypeCQ.class.getName();
    }
}
