/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.common;

import javax.sql.DataSource;

import org.dbflute.bhv.core.InvokerAssistant;
import org.dbflute.bhv.core.context.ResourceParameter;
import org.dbflute.bhv.core.supplement.SequenceCacheHandler;
import org.dbflute.bhv.exception.BehaviorExceptionThrower;
import org.dbflute.bhv.exception.SQLExceptionHandlerFactory;
import org.dbflute.cbean.cipher.GearedCipherManager;
import org.dbflute.cbean.sqlclause.SqlClauseCreator;
import org.dbflute.dbmeta.DBMetaProvider;
import org.dbflute.dbway.DBDef;
import org.dbflute.jdbc.SQLExceptionDigger;
import org.dbflute.jdbc.StatementConfig;
import org.dbflute.jdbc.StatementFactory;
import org.dbflute.optional.RelationOptionalFactory;
import org.dbflute.outsidesql.OutsideSqlOption;
import org.dbflute.outsidesql.factory.OutsideSqlExecutorFactory;
import org.dbflute.s2dao.jdbc.TnResultSetHandlerFactory;
import org.dbflute.s2dao.metadata.TnBeanMetaDataFactory;
import org.dbflute.twowaysql.factory.SqlAnalyzerFactory;

public class ImplementedInvokerAssistant implements InvokerAssistant {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected static final String[] DEFAULT_CLIENT_INVOKE_NAMES = { "Page", "Action", "Controller", "ControllerImpl", "Task", "Test" };

    protected static final String[] DEFAULT_BYPASS_INVOKE_NAMES =
            { "Service", "ServiceImpl", "Facade", "FacadeImpl", "Logic", "LogicImpl" };

    @Override
    public DBDef assistCurrentDBDef() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataSource assistDataSource() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DBMetaProvider assistDBMetaProvider() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SqlClauseCreator assistSqlClauseCreator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementFactory assistStatementFactory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TnBeanMetaDataFactory assistBeanMetaDataFactory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TnResultSetHandlerFactory assistResultSetHandlerFactory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RelationOptionalFactory assistRelationOptionalFactory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SqlAnalyzerFactory assistSqlAnalyzerFactory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OutsideSqlOption assistFirstOutsideSqlOption(final String tableDbName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OutsideSqlExecutorFactory assistOutsideSqlExecutorFactory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SQLExceptionDigger assistSQLExceptionDigger() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SQLExceptionHandlerFactory assistSQLExceptionHandlerFactory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SequenceCacheHandler assistSequenceCacheHandler() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String assistSqlFileEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementConfig assistDefaultStatementConfig() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BehaviorExceptionThrower assistBehaviorExceptionThrower() {
        return new BehaviorExceptionThrower();
    }

    @Override
    public GearedCipherManager assistGearedCipherManager() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResourceParameter assistResourceParameter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] assistClientInvokeNames() {
        return DEFAULT_CLIENT_INVOKE_NAMES;
    }

    @Override
    public String[] assistByPassInvokeNames() {
        return DEFAULT_BYPASS_INVOKE_NAMES;
    }

    @Override
    public void toBeDisposable(final DisposableProcess callerProcess) {
        // TODO Auto-generated method stub

    }

}
