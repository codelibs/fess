/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.opensearch.user.bsentity.dbmeta;

import java.time.*;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.opensearch.user.exentity.Passkey;

import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

/**
 * @author ESFlute (using FreeGen)
 */
public class PasskeyDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final PasskeyDbm _instance = new PasskeyDbm();

    private PasskeyDbm() {
    }

    public static PasskeyDbm getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                       Current DBDef
    //                                                                       =============
    @Override
    public String getProjectName() {
        return null;
    }

    @Override
    public String getProjectPrefix() {
        return null;
    }

    @Override
    public String getGenerationGapBasePrefix() {
        return null;
    }

    @Override
    public DBDef getCurrentDBDef() {
        return null;
    }

    // ===================================================================================
    //                                                                    Property Gateway
    //                                                                    ================
    // -----------------------------------------------------
    //                                       Column Property
    //                                       ---------------
    protected final Map<String, PropertyGateway> _epgMap = newHashMap();
    {
        setupEpg(_epgMap, et -> ((Passkey) et).getBackupEligible(), (et, vl) -> ((Passkey) et).setBackupEligible(DfTypeUtil.toBoolean(vl)),
                "backupEligible");
        setupEpg(_epgMap, et -> ((Passkey) et).getBackupState(), (et, vl) -> ((Passkey) et).setBackupState(DfTypeUtil.toBoolean(vl)),
                "backupState");
        setupEpg(_epgMap, et -> ((Passkey) et).getCreatedAt(), (et, vl) -> ((Passkey) et).setCreatedAt(DfTypeUtil.toLong(vl)), "createdAt");
        setupEpg(_epgMap, et -> ((Passkey) et).getLabel(), (et, vl) -> ((Passkey) et).setLabel(DfTypeUtil.toString(vl)), "label");
        setupEpg(_epgMap, et -> ((Passkey) et).getLastUsedAt(), (et, vl) -> ((Passkey) et).setLastUsedAt(DfTypeUtil.toLong(vl)),
                "lastUsedAt");
        setupEpg(_epgMap, et -> ((Passkey) et).getPublicKey(), (et, vl) -> ((Passkey) et).setPublicKey(DfTypeUtil.toString(vl)),
                "publicKey");
        setupEpg(_epgMap, et -> ((Passkey) et).getSignCount(), (et, vl) -> ((Passkey) et).setSignCount(DfTypeUtil.toLong(vl)), "signCount");
        setupEpg(_epgMap, et -> ((Passkey) et).getTransports(), (et, vl) -> ((Passkey) et).setTransports((String[]) vl), "transports");
        setupEpg(_epgMap, et -> ((Passkey) et).getUserHandle(), (et, vl) -> ((Passkey) et).setUserHandle(DfTypeUtil.toString(vl)),
                "userHandle");
        setupEpg(_epgMap, et -> ((Passkey) et).getUserName(), (et, vl) -> ((Passkey) et).setUserName(DfTypeUtil.toString(vl)), "userName");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "passkey";
    protected final String _tableDispName = "passkey";
    protected final String _tablePropertyName = "Passkey";

    public String getTableDbName() {
        return _tableDbName;
    }

    @Override
    public String getTableDispName() {
        return _tableDispName;
    }

    @Override
    public String getTablePropertyName() {
        return _tablePropertyName;
    }

    @Override
    public TableSqlName getTableSqlName() {
        return null;
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnBackupEligible = cci("backupEligible", "backupEligible", null, null, Boolean.class, "backupEligible",
            null, false, false, false, "Boolean", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnBackupState = cci("backupState", "backupState", null, null, Boolean.class, "backupState", null, false,
            false, false, "Boolean", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedAt = cci("createdAt", "createdAt", null, null, Long.class, "createdAt", null, false, false,
            false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnLabel = cci("label", "label", null, null, String.class, "label", null, false, false, false, "keyword",
            0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnLastUsedAt = cci("lastUsedAt", "lastUsedAt", null, null, Long.class, "lastUsedAt", null, false, false,
            false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPublicKey = cci("publicKey", "publicKey", null, null, String.class, "publicKey", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnSignCount = cci("signCount", "signCount", null, null, Long.class, "signCount", null, false, false,
            false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnTransports = cci("transports", "transports", null, null, String[].class, "transports", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUserHandle = cci("userHandle", "userHandle", null, null, String.class, "userHandle", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUserName = cci("userName", "userName", null, null, String.class, "userName", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnBackupEligible() {
        return _columnBackupEligible;
    }

    public ColumnInfo columnBackupState() {
        return _columnBackupState;
    }

    public ColumnInfo columnCreatedAt() {
        return _columnCreatedAt;
    }

    public ColumnInfo columnLabel() {
        return _columnLabel;
    }

    public ColumnInfo columnLastUsedAt() {
        return _columnLastUsedAt;
    }

    public ColumnInfo columnPublicKey() {
        return _columnPublicKey;
    }

    public ColumnInfo columnSignCount() {
        return _columnSignCount;
    }

    public ColumnInfo columnTransports() {
        return _columnTransports;
    }

    public ColumnInfo columnUserHandle() {
        return _columnUserHandle;
    }

    public ColumnInfo columnUserName() {
        return _columnUserName;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnBackupEligible());
        ls.add(columnBackupState());
        ls.add(columnCreatedAt());
        ls.add(columnLabel());
        ls.add(columnLastUsedAt());
        ls.add(columnPublicKey());
        ls.add(columnSignCount());
        ls.add(columnTransports());
        ls.add(columnUserHandle());
        ls.add(columnUserName());
        return ls;
    }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    @Override
    public boolean hasPrimaryKey() {
        return false;
    }

    @Override
    public boolean hasCompoundPrimaryKey() {
        return false;
    }

    @Override
    protected UniqueInfo cpui() {
        return null;
    }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    @Override
    public String getEntityTypeName() {
        return "org.codelibs.fess.opensearch.user.exentity.Passkey";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.opensearch.user.cbean.PasskeyCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.opensearch.user.exbhv.PasskeyBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return Passkey.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new Passkey();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
    }

    @Override
    public void acceptAllColumnMap(Entity entity, Map<String, ? extends Object> allColumnMap) {
    }

    @Override
    public Map<String, Object> extractPrimaryKeyMap(Entity entity) {
        return null;
    }

    @Override
    public Map<String, Object> extractAllColumnMap(Entity entity) {
        return null;
    }
}
