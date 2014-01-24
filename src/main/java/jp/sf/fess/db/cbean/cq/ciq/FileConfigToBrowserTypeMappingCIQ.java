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

import jp.sf.fess.db.cbean.FileConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.cbean.cq.BrowserTypeCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.bs.AbstractBsFileConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.bs.BsFileConfigToBrowserTypeMappingCQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.ckey.ConditionKey;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The condition-query for in-line of FILE_CONFIG_TO_BROWSER_TYPE_MAPPING.
 * @author DBFlute(AutoGenerator)
 */
public class FileConfigToBrowserTypeMappingCIQ extends
        AbstractBsFileConfigToBrowserTypeMappingCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsFileConfigToBrowserTypeMappingCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public FileConfigToBrowserTypeMappingCIQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel, final BsFileConfigToBrowserTypeMappingCQ myCQ) {
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
    protected ConditionValue getCValueFileConfigId() {
        return _myCQ.getFileConfigId();
    }

    @Override
    public String keepFileConfigId_InScopeRelation_FileCrawlingConfig(
            final FileCrawlingConfigCQ sq) {
        return _myCQ.keepFileConfigId_InScopeRelation_FileCrawlingConfig(sq);
    }

    @Override
    public String keepFileConfigId_NotInScopeRelation_FileCrawlingConfig(
            final FileCrawlingConfigCQ sq) {
        return _myCQ.keepFileConfigId_NotInScopeRelation_FileCrawlingConfig(sq);
    }

    @Override
    protected ConditionValue getCValueBrowserTypeId() {
        return _myCQ.getBrowserTypeId();
    }

    @Override
    public String keepBrowserTypeId_InScopeRelation_BrowserType(
            final BrowserTypeCQ sq) {
        return _myCQ.keepBrowserTypeId_InScopeRelation_BrowserType(sq);
    }

    @Override
    public String keepBrowserTypeId_NotInScopeRelation_BrowserType(
            final BrowserTypeCQ sq) {
        return _myCQ.keepBrowserTypeId_NotInScopeRelation_BrowserType(sq);
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    @Override
    public String keepScalarCondition(
            final FileConfigToBrowserTypeMappingCQ subQuery) {
        throwIICBOE("ScalarCondition");
        return null;
    }

    @Override
    public String keepSpecifyMyselfDerived(
            final FileConfigToBrowserTypeMappingCQ subQuery) {
        throwIICBOE("(Specify)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerived(
            final FileConfigToBrowserTypeMappingCQ subQuery) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object parameterValue) {
        throwIICBOE("(Query)MyselfDerived");
        return null;
    }

    @Override
    public String keepMyselfExists(
            final FileConfigToBrowserTypeMappingCQ subQuery) {
        throwIICBOE("MyselfExists");
        return null;
    }

    @Override
    public String keepMyselfInScope(
            final FileConfigToBrowserTypeMappingCQ subQuery) {
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
        return FileConfigToBrowserTypeMappingCB.class.getName();
    }

    protected String xinCQ() {
        return FileConfigToBrowserTypeMappingCQ.class.getName();
    }
}
