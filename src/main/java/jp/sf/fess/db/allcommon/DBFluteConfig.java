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

package jp.sf.fess.db.allcommon;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.QLog;
import org.seasar.dbflute.XLog;
import org.seasar.dbflute.bhv.core.supplement.SequenceCacheKeyGenerator;
import org.seasar.dbflute.cbean.cipher.GearedCipherManager;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseCreator;
import org.seasar.dbflute.dbmeta.name.SqlNameFilter;
import org.seasar.dbflute.exception.IllegalDBFluteConfigAccessException;
import org.seasar.dbflute.jdbc.DataSourceHandler;
import org.seasar.dbflute.jdbc.NotClosingConnectionWrapper;
import org.seasar.dbflute.jdbc.PhysicalConnectionDigger;
import org.seasar.dbflute.jdbc.SQLExceptionDigger;
import org.seasar.dbflute.jdbc.StatementConfig;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.outsidesql.factory.OutsideSqlExecutorFactory;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypes;
import org.seasar.dbflute.s2dao.valuetype.plugin.OracleAgent;
import org.seasar.dbflute.s2dao.valuetype.plugin.OracleArrayType;
import org.seasar.dbflute.s2dao.valuetype.plugin.OracleDateType;
import org.seasar.dbflute.s2dao.valuetype.plugin.OracleStructType;
import org.seasar.dbflute.twowaysql.DisplaySqlBuilder;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.extension.dbcp.ConnectionWrapper;
import org.seasar.framework.exception.SQLRuntimeException;

/**
 * @author DBFlute(AutoGenerator)
 */
public class DBFluteConfig {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DBFluteConfig.class);

    /** Singleton instance. */
    private static final DBFluteConfig _instance = new DBFluteConfig();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                         Configuration
    //                                         -------------
    // condition-bean or parameter-bean
    protected boolean _pagingCountLater = true;

    protected boolean _pagingCountLeastJoin = true;

    protected boolean _innerJoinAutoDetect = true;

    protected boolean _thatsBadTimingDetect = false;

    protected boolean _emptyStringQueryAllowed;

    protected boolean _emptyStringParameterAllowed;

    protected boolean _invalidQueryChecked;

    protected boolean _disableSelectIndex;

    protected boolean _checkCountBeforeQueryUpdate = false;

    // logging
    protected boolean _queryLogLevelInfo;

    protected boolean _executeStatusLogLevelInfo;

    protected String _logDateFormat;

    protected String _logTimestampFormat;

    // environment
    protected StatementConfig _defaultStatementConfig;

    protected Integer _cursorSelectFetchSize;

    protected DataSourceHandler _dataSourceHandler;

    protected PhysicalConnectionDigger _physicalConnectionDigger;

    protected SQLExceptionDigger _sqlExceptionDigger;

    protected String _outsideSqlPackage;

    protected boolean _useSqlLogRegistry = false;

    // extension
    protected SequenceCacheKeyGenerator _sequenceCacheKeyGenerator;

    protected SqlClauseCreator _sqlClauseCreator;

    protected SqlNameFilter _tableSqlNameFilter;

    protected OutsideSqlExecutorFactory _outsideSqlExecutorFactory;

    protected GearedCipherManager _gearedCipherManager;

    // internal
    protected boolean _internalDebug;

    // -----------------------------------------------------
    //                                   Database Dependency
    //                                   -------------------

    // -----------------------------------------------------
    //                                                  Lock
    //                                                  ----
    protected boolean _locked = true; // at first locked

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     */
    private DBFluteConfig() { // adjusts default settings
        _physicalConnectionDigger = new ImplementedPhysicalConnectionDigger();
        _sqlExceptionDigger = new ImplementedSQLExceptionDigger();

        if (isCurrentDBDef(DBDef.Oracle)) {
            // date formatting has two points:
            //   o the DATE type of Oracle has seconds
            //   o it uses a date literal of Oracle
            _logDateFormat = "timestamp $df:{yyyy-MM-dd HH:mm:ss}";
            _logTimestampFormat = "timestamp $df:{"
                    + DisplaySqlBuilder.DEFAULT_TIMESTAMP_FORMAT + "}";
        }
    }

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    /**
     * Get singleton instance.
     * @return Singleton instance. (NotNull)
     */
    public static DBFluteConfig getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                       Paging Select
    //                                                                       =============
    public boolean isPagingCountLater() {
        return _pagingCountLater;
    }

    public void setPagingCountLater(final boolean pagingCountLater) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting pagingCountLater: " + pagingCountLater);
        }
        _pagingCountLater = pagingCountLater;
    }

    public boolean isPagingCountLeastJoin() {
        return _pagingCountLeastJoin;
    }

    public void setPagingCountLeastJoin(final boolean pagingCountLeastJoin) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting pagingCountLeastJoin: "
                    + pagingCountLeastJoin);
        }
        _pagingCountLeastJoin = pagingCountLeastJoin;
    }

    // ===================================================================================
    //                                                              Inner Join Auto Detect
    //                                                              ======================
    public boolean isInnerJoinAutoDetect() {
        return _innerJoinAutoDetect;
    }

    public void setInnerJoinAutoDetect(final boolean innerJoinAutoDetect) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting innerJoinAutoDetect: " + innerJoinAutoDetect);
        }
        _innerJoinAutoDetect = innerJoinAutoDetect;
    }

    // ===================================================================================
    //                                                            That's-Bad-Timing Detect
    //                                                            ========================
    public boolean isThatsBadTimingDetect() {
        return _thatsBadTimingDetect;
    }

    public void setThatsBadTimingDetect(final boolean thatsBadTimingDetect) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting thatsBadTimingDetect: "
                    + thatsBadTimingDetect);
        }
        _thatsBadTimingDetect = thatsBadTimingDetect;
    }

    // ===================================================================================
    //                                                                       Invalid Query
    //                                                                       =============
    public boolean isEmptyStringQueryAllowed() {
        return _emptyStringQueryAllowed;
    }

    /**
     * Set whether an empty string for query is allowed or not. {default: false}<br />
     * This configuration is only for ConditionBean.
     * @param emptyStringQueryAllowed Determination.
     */
    public void setEmptyStringQueryAllowed(final boolean emptyStringQueryAllowed) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting emptyStringQueryAllowed: "
                    + emptyStringQueryAllowed);
        }
        _emptyStringQueryAllowed = emptyStringQueryAllowed;
    }

    public boolean isEmptyStringParameterAllowed() {
        return _emptyStringParameterAllowed;
    }

    /**
     * Set whether an empty string for parameter is allowed or not. {default: false}<br />
     * This configuration is only for ParameterBean.
     * @param emptyStringParameterAllowed Determination.
     */
    public void setEmptyStringParameterAllowed(
            final boolean emptyStringParameterAllowed) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting emptyStringParameterAllowed: "
                    + emptyStringParameterAllowed);
        }
        _emptyStringParameterAllowed = emptyStringParameterAllowed;
    }

    public boolean isInvalidQueryChecked() {
        return _invalidQueryChecked;
    }

    /**
     * Set whether an invalid query is checked or not. {default: false}<br />
     * This configuration is only for ConditionBean.
     * @param invalidQueryChecked Determination.
     */
    public void setInvalidQueryChecked(final boolean invalidQueryChecked) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting invalidQueryChecked: " + invalidQueryChecked);
        }
        _invalidQueryChecked = invalidQueryChecked;
    }

    // ===================================================================================
    //                                                                        Select Index
    //                                                                        ============
    public boolean isDisableSelectIndex() {
        return _disableSelectIndex;
    }

    public void setDisableSelectIndex(final boolean disableSelectIndex) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting disableSelectIndex: " + disableSelectIndex);
        }
        _disableSelectIndex = disableSelectIndex;
    }

    // ===================================================================================
    //                                                                        Query Update
    //                                                                        ============
    public boolean isCheckCountBeforeQueryUpdate() {
        return _checkCountBeforeQueryUpdate;
    }

    public void setCheckCountBeforeQueryUpdate(
            final boolean checkCountBeforeQueryUpdate) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting checkCountBeforeQueryUpdate: "
                    + checkCountBeforeQueryUpdate);
        }
        _checkCountBeforeQueryUpdate = checkCountBeforeQueryUpdate;
    }

    // ===================================================================================
    //                                                                Query Log Level Info
    //                                                                ====================
    public void setQueryLogLevelInfo(final boolean queryLogLevelInfo) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting queryLogLevelInfo: " + queryLogLevelInfo);
        }
        QLog.unlock();
        QLog.setQueryLogLevelInfo(queryLogLevelInfo);
        QLog.lock();
    }

    // ===================================================================================
    //                                                       Execute Status Log Level Info
    //                                                       =============================
    public void setExecuteStatusLogLevelInfo(
            final boolean executeStatusLogLevelInfo) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting executeStatusLogLevelInfo: "
                    + executeStatusLogLevelInfo);
        }
        XLog.unlock();
        XLog.setExecuteStatusLogLevelInfo(executeStatusLogLevelInfo);
        XLog.lock();
    }

    // ===================================================================================
    //                                                                          Log Format
    //                                                                          ==========
    public String getLogDateFormat() {
        return _logDateFormat;
    }

    public void setLogDateFormat(final String logDateFormat) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting logDateFormat: " + logDateFormat);
        }
        _logDateFormat = logDateFormat;
    }

    public String getLogTimestampFormat() {
        return _logTimestampFormat;
    }

    public void setLogTimestampFormat(final String logTimestampFormat) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting logTimestampFormat: " + logTimestampFormat);
        }
        _logTimestampFormat = logTimestampFormat;
    }

    // ===================================================================================
    //                                                             Default StatementConfig
    //                                                             =======================
    public StatementConfig getDefaultStatementConfig() {
        return _defaultStatementConfig;
    }

    public void setDefaultStatementConfig(
            final StatementConfig defaultStatementConfig) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting defaultStatementConfig: "
                    + defaultStatementConfig);
        }
        _defaultStatementConfig = defaultStatementConfig;
    }

    // ===================================================================================
    //                                                              CursorSelect FetchSize
    //                                                              ======================
    public Integer getCursorSelectFetchSize() {
        return _cursorSelectFetchSize;
    }

    public void setCursorSelectFetchSize(final Integer cursorSelectFetchSize) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting cursorSelectFetchSize: "
                    + cursorSelectFetchSize);
        }
        _cursorSelectFetchSize = cursorSelectFetchSize;
    }

    // [DBFlute-0.9.0]
    // ===================================================================================
    //                                                                  DataSource Handler
    //                                                                  ==================
    /**
     * @return The handler of data source. (NullAllowed)
     */
    public DataSourceHandler getDataSourceHandler() {
        return _dataSourceHandler;
    }

    /**
     * @param dataSourceHandler The handler of data source. (NullAllowed)
     */
    public void setDataSourceHandler(final DataSourceHandler dataSourceHandler) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting dataSourceHandler: " + dataSourceHandler);
        }
        _dataSourceHandler = dataSourceHandler;
    }

    // [DBFlute-0.9.7.6]
    // ===================================================================================
    //                                                           PhysicalConnection Digger
    //                                                           =========================
    /**
     * @return The digger of physical connection. (NotNull: has a default instance)
     */
    public PhysicalConnectionDigger getPhysicalConnectionDigger() {
        return _physicalConnectionDigger;
    }

    /**
     * @param physicalConnectionDigger The digger of physical connection. (NotNull)
     */
    public void setPhysicalConnectionDigger(
            final PhysicalConnectionDigger physicalConnectionDigger) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting physicalConnectionDigger: "
                    + physicalConnectionDigger);
        }
        if (physicalConnectionDigger == null) {
            throw new IllegalArgumentException(
                    "The argument 'physicalConnectionDigger' should not be null.");
        }
        _physicalConnectionDigger = physicalConnectionDigger;
    }

    // [DBFlute-0.9.7.8]
    // ===================================================================================
    //                                                                 SQLException Digger
    //                                                                 ===================
    /**
     * @return The digger of SQLException. (NotNull: has a default instance)
     */
    public SQLExceptionDigger getSQLExceptionDigger() {
        return _sqlExceptionDigger;
    }

    /**
     * @param sqlExceptionDigger The digger of SQLException. (NotNull)
     */
    public void setSQLExceptionDigger(
            final SQLExceptionDigger sqlExceptionDigger) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting sqlExceptionDigger: " + sqlExceptionDigger);
        }
        if (sqlExceptionDigger == null) {
            throw new IllegalArgumentException(
                    "The argument 'sqlExceptionDigger' should not be null.");
        }
        _sqlExceptionDigger = sqlExceptionDigger;
    }

    // ===================================================================================
    //                                                                  OutsideSql Package
    //                                                                  ==================
    /**
     * @return The package of outside SQL. (NullAllowed)
     */
    public String getOutsideSqlPackage() {
        return _outsideSqlPackage;
    }

    /**
     * @param outsideSqlPackage The package of outside SQL. (NullAllowed)
     */
    public void setOutsideSqlPackage(final String outsideSqlPackage) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting outsideSqlPackage: " + outsideSqlPackage);
        }
        _outsideSqlPackage = outsideSqlPackage;
    }

    // [DBFlute-0.8.2]
    // ===================================================================================
    //                                                                    SQL Log Registry
    //                                                                    ================
    public boolean isUseSqlLogRegistry() {
        return _useSqlLogRegistry;
    }

    public void setUseSqlLogRegistry(final boolean useSqlLogRegistry) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting useSqlLogRegistry: " + useSqlLogRegistry);
        }
        _useSqlLogRegistry = useSqlLogRegistry;
    }

    // [DBFlute-0.9.6.4]
    // ===================================================================================
    //                                                                      Sequence Cache
    //                                                                      ==============
    /**
     * @return The key generator of sequence cache. (NullAllowed)
     */
    public SequenceCacheKeyGenerator getSequenceCacheKeyGenerator() {
        return _sequenceCacheKeyGenerator;
    }

    /**
     * @param sequenceCacheKeyGenerator The key generator of sequence cache. (NullAllowed)
     */
    public void setSequenceCacheKeyGenerator(
            final SequenceCacheKeyGenerator sequenceCacheKeyGenerator) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting sequenceCacheKeyGenerator: "
                    + sequenceCacheKeyGenerator);
        }
        _sequenceCacheKeyGenerator = sequenceCacheKeyGenerator;
    }

    // [DBFlute-0.9.6.9]
    // ===================================================================================
    //                                                                   SqlClause Creator
    //                                                                   =================
    /**
     * @return The creator of SQL clause. (NullAllowed)
     */
    public SqlClauseCreator getSqlClauseCreator() {
        return _sqlClauseCreator;
    }

    /**
     * @param sqlClauseCreator The creator of SQL clause. (NullAllowed)
     */
    public void setSqlClauseCreator(final SqlClauseCreator sqlClauseCreator) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting sqlClauseCreator: " + sqlClauseCreator);
        }
        _sqlClauseCreator = sqlClauseCreator;
    }

    // [DBFlute-0.9.7.6]
    // ===================================================================================
    //                                                                 TableSqlName Filter
    //                                                                 ===================
    /**
     * @return The SQL name filter for table. (NullAllowed)
     */
    public SqlNameFilter getTableSqlNameFilter() {
        return _tableSqlNameFilter;
    }

    /**
     * Set the SQL name filter for table. <br />
     * This setting should be called before container's initialization.
     * (its exact meaning is: before class loading of DBMeta for table)
     * @param tableSqlNameFilter The SQL name filter for table. (NullAllowed)
     */
    public void setTableSqlNameFilter(final SqlNameFilter tableSqlNameFilter) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting tableSqlNameFilter: " + tableSqlNameFilter);
        }
        _tableSqlNameFilter = tableSqlNameFilter;
    }

    // [DBFlute-0.9.7.0]
    // ===================================================================================
    //                                                                 OutsideSql Executor
    //                                                                 ===================
    public OutsideSqlExecutorFactory getOutsideSqlExecutorFactory() {
        return _outsideSqlExecutorFactory;
    }

    public void setOutsideSqlExecutorFactory(
            final OutsideSqlExecutorFactory outsideSqlExecutorFactory) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting outsideSqlExecutorFactory: "
                    + outsideSqlExecutorFactory);
        }
        _outsideSqlExecutorFactory = outsideSqlExecutorFactory;
    }

    // [DBFlute-0.9.7.0]
    // ===================================================================================
    //                                                               Geared Cipher Manager
    //                                                               =====================
    public GearedCipherManager getGearedCipherManager() {
        return _gearedCipherManager;
    }

    public void setGearedCipherManager(
            final GearedCipherManager gearedCipherManager) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting gearedCipherManager: " + gearedCipherManager);
        }
        _gearedCipherManager = gearedCipherManager;
    }

    // ===================================================================================
    //                                                                 Database Dependency
    //                                                                 ===================

    // ===================================================================================
    //                                                                      Internal Debug
    //                                                                      ==============
    public boolean isInternalDebug() {
        return _internalDebug;
    }

    public void setInternalDebug(final boolean internalDebug) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting internalDebug: " + internalDebug);
        }
        _internalDebug = internalDebug;
    }

    // ===================================================================================
    //                                                                          Value Type
    //                                                                          ==========
    /**
     * Register the basic value type. <br />
     * This setting is shared per DBMS in the same class loader.
     * @param keyType The type as key. (NotNull)
     * @param valueType The basic value type. (NotNull)
     */
    public void registerBasicValueType(final Class<?> keyType,
            final ValueType valueType) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Registering basic valueType: " + keyType + " = "
                    + valueType);
        }
        TnValueTypes.registerBasicValueType(currentDBDef(), keyType, valueType);
    }

    public void removeBasicValueType(final Class<?> keyType) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Removing basic valueType: " + keyType);
        }
        TnValueTypes.removeBasicValueType(currentDBDef(), keyType);
    }

    /**
     * Register the plug-in value type. <br />
     * This setting is shared per DBMS in the same class loader.
     * @param keyName The name as key. (NotNull)
     * @param valueType The plug-in value type. (NotNull)
     */
    public void registerPluginValueType(final String keyName,
            final ValueType valueType) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Registering plug-in valueType: " + keyName + " = "
                    + valueType);
        }
        TnValueTypes
                .registerPluginValueType(currentDBDef(), keyName, valueType);
    }

    public void removePluginValueType(final String keyName) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Removing plug-in valueType: keyName=" + keyName);
        }
        TnValueTypes.removePluginValueType(currentDBDef(), keyName);
    }

    // ===================================================================================
    //                                                                  Configuration Lock
    //                                                                  ==================
    public boolean isLocked() {
        return _locked;
    }

    public void lock() {
        if (_locked) {
            return;
        }
        if (_log.isInfoEnabled()) {
            _log.info("...Locking the configuration of DBFlute");
        }
        _locked = true;
    }

    public void unlock() {
        if (!_locked) {
            return;
        }
        if (_log.isInfoEnabled()) {
            _log.info("...Unlocking the configuration of DBFlute");
        }
        _locked = false;
    }

    protected void assertNotLocked() {
        if (!isLocked()) {
            return;
        }
        final String msg = "The configuration of DBFlute is locked.";
        throw new IllegalDBFluteConfigAccessException(msg);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected DBDef currentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
    }

    protected boolean isCurrentDBDef(final DBDef currentDBDef) {
        return DBCurrent.getInstance().isCurrentDBDef(currentDBDef);
    }

    // ===================================================================================
    //                                                                   Implemented Class
    //                                                                   =================
    // -----------------------------------------------------
    //                                                Spring
    //                                                ------
    public static class SpringTransactionalDataSourceHandler implements
            DataSourceHandler {

        @Override
        public Connection getConnection(final DataSource ds)
                throws SQLException {
            final Connection conn = getConnectionFromUtils(ds);
            if (isConnectionTransactional(conn, ds)) {
                return new NotClosingConnectionWrapper(conn);
            } else {
                return conn;
            }
        }

        public Connection getConnectionFromUtils(final DataSource ds) {
            throw new IllegalStateException(
                    "This method is only for Spring Framework.");
        }

        public boolean isConnectionTransactional(final Connection conn,
                final DataSource ds) {
            throw new IllegalStateException(
                    "This method is only for Spring Framework.");
        }
    }

    // -----------------------------------------------------
    //                                                Oracle
    //                                                ------
    public static class ImplementedOracleAgent implements OracleAgent {

        @Override
        public Object toOracleDate(final Timestamp timestamp) {
            throw new UnsupportedOperationException(
                    "This method is only for Oracle.");
        }

        @Override
        public Object toOracleArray(final Connection conn,
                final String arrayTypeName, final Object arrayValue)
                throws SQLException {
            throw new UnsupportedOperationException(
                    "This method is only for Oracle.");
        }

        @Override
        public Object toStandardArray(final Object oracleArray)
                throws SQLException {
            throw new UnsupportedOperationException(
                    "This method is only for Oracle.");
        }

        @Override
        public boolean isOracleArray(final Object obj) {
            throw new UnsupportedOperationException(
                    "This method is only for Oracle.");
        }

        @Override
        public Object toOracleStruct(final Connection conn,
                final String structTypeName, final Object[] attrs)
                throws SQLException {
            throw new UnsupportedOperationException(
                    "This method is only for Oracle.");
        }

        @Override
        public Object[] toStandardStructAttributes(final Object oracleStruct)
                throws SQLException {
            throw new UnsupportedOperationException(
                    "This method is only for Oracle.");
        }

        @Override
        public boolean isOracleStruct(final Object obj) {
            throw new UnsupportedOperationException(
                    "This method is only for Oracle.");
        }

        @Override
        public PhysicalConnectionDigger getPhysicalConnectionDigger() {
            return DBFluteConfig.getInstance().getPhysicalConnectionDigger();
        }
    }

    public static class ImplementedOracleDateType extends OracleDateType {

        @Override
        protected OracleAgent createOracleAgent() {
            return new ImplementedOracleAgent();
        }
    }

    public static class ImplementedOracleArrayType extends OracleArrayType {

        public ImplementedOracleArrayType(final String arrayTypeName,
                final Class<?> elementType) {
            super(arrayTypeName, elementType);
        }

        @Override
        protected OracleAgent createOracleAgent() {
            return new ImplementedOracleAgent();
        }
    }

    public static class ImplementedOracleStructType extends OracleStructType {

        public ImplementedOracleStructType(final String structTypeName,
                final Class<?> entityType) {
            super(structTypeName, entityType);
        }

        @Override
        protected OracleAgent createOracleAgent() {
            return new ImplementedOracleAgent();
        }
    }

    // -----------------------------------------------------
    //                                   Physical Connection
    //                                   -------------------
    public static class ImplementedPhysicalConnectionDigger implements
            PhysicalConnectionDigger {

        @Override
        public Connection digUp(final Connection conn) throws SQLException {
            Connection digged = unwrap(conn);
            digged = resolveS2DBCP(digged);
            digged = resolveCommonsDBCP(digged);
            return digged;
        }

        protected Connection unwrap(final Connection conn) {
            if (conn instanceof NotClosingConnectionWrapper) {
                return ((NotClosingConnectionWrapper) conn)
                        .getActualConnection();
            }
            return conn;
        }

        protected Connection resolveS2DBCP(final Connection conn) {
            if (conn instanceof ConnectionWrapper) {
                return ((ConnectionWrapper) conn).getPhysicalConnection();
            }
            return conn;
        }

        protected Connection resolveCommonsDBCP(final Connection conn) {
            Connection resolved = conn;
            if ("org.apache.commons.dbcp.PoolingDataSource$PoolGuardConnectionWrapper"
                    .equals(resolved.getClass().getName())) {
                resolved = getFieldConnection(resolved, "delegate");
            }
            if ("org.apache.commons.dbcp.PoolableConnection".equals(resolved
                    .getClass().getName())) {
                resolved = getFieldConnection(resolved, "_conn");
            }
            return resolved;
        }

        protected Connection getFieldConnection(final Connection conn,
                final String fieldName) {
            final Field field = DfReflectionUtil.getWholeField(conn.getClass(),
                    fieldName);
            return (Connection) DfReflectionUtil.getValueForcedly(field, conn);
        }
    }

    // -----------------------------------------------------
    //                                          SQLException
    //                                          ------------
    public static class ImplementedSQLExceptionDigger implements
            SQLExceptionDigger {

        @Override
        public SQLException digUp(final Throwable cause) {
            SQLException found = resolveS2DBCP(cause);
            if (found != null) {
                return found;
            }
            found = resolveDefault(cause);
            if (found != null) {
                return found;
            }
            return null;
        }

        protected SQLException resolveS2DBCP(final Throwable cause) {
            if (cause instanceof SQLRuntimeException) {
                final Throwable nestedCause = ((SQLRuntimeException) cause)
                        .getCause();
                if (nestedCause instanceof SQLException) {
                    return (SQLException) nestedCause;
                }
            }
            return null;
        }

        protected SQLException resolveDefault(final Throwable cause) {
            final Throwable nestedCause = cause.getCause();
            if (nestedCause instanceof SQLException) {
                return (SQLException) nestedCause;
            }
            return null;
        }
    }
}
