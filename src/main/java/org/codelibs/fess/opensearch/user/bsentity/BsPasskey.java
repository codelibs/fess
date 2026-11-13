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
package org.codelibs.fess.opensearch.user.bsentity;

import java.time.*;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.opensearch.user.allcommon.EsAbstractEntity;
import org.codelibs.fess.opensearch.user.bsentity.dbmeta.PasskeyDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsPasskey extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** backupEligible */
    protected Boolean backupEligible;

    /** backupState */
    protected Boolean backupState;

    /** createdAt */
    protected Long createdAt;

    /** label */
    protected String label;

    /** lastUsedAt */
    protected Long lastUsedAt;

    /** publicKey */
    protected String publicKey;

    /** signCount */
    protected Long signCount;

    /** transports */
    protected String[] transports;

    /** userHandle */
    protected String userHandle;

    /** userName */
    protected String userName;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public PasskeyDbm asDBMeta() {
        return PasskeyDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "passkey";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (backupEligible != null) {
            addFieldToSource(sourceMap, "backupEligible", backupEligible);
        }
        if (backupState != null) {
            addFieldToSource(sourceMap, "backupState", backupState);
        }
        if (createdAt != null) {
            addFieldToSource(sourceMap, "createdAt", createdAt);
        }
        if (label != null) {
            addFieldToSource(sourceMap, "label", label);
        }
        if (lastUsedAt != null) {
            addFieldToSource(sourceMap, "lastUsedAt", lastUsedAt);
        }
        if (publicKey != null) {
            addFieldToSource(sourceMap, "publicKey", publicKey);
        }
        if (signCount != null) {
            addFieldToSource(sourceMap, "signCount", signCount);
        }
        if (transports != null) {
            addFieldToSource(sourceMap, "transports", transports);
        }
        if (userHandle != null) {
            addFieldToSource(sourceMap, "userHandle", userHandle);
        }
        if (userName != null) {
            addFieldToSource(sourceMap, "userName", userName);
        }
        return sourceMap;
    }

    protected void addFieldToSource(Map<String, Object> sourceMap, String field, Object value) {
        sourceMap.put(field, value);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    protected String doBuildColumnString(String dm) {
        StringBuilder sb = new StringBuilder();
        sb.append(dm).append(backupEligible);
        sb.append(dm).append(backupState);
        sb.append(dm).append(createdAt);
        sb.append(dm).append(label);
        sb.append(dm).append(lastUsedAt);
        sb.append(dm).append(publicKey);
        sb.append(dm).append(signCount);
        sb.append(dm).append(transports);
        sb.append(dm).append(userHandle);
        sb.append(dm).append(userName);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Boolean getBackupEligible() {
        checkSpecifiedProperty("backupEligible");
        return backupEligible;
    }

    public void setBackupEligible(Boolean value) {
        registerModifiedProperty("backupEligible");
        this.backupEligible = value;
    }

    public Boolean getBackupState() {
        checkSpecifiedProperty("backupState");
        return backupState;
    }

    public void setBackupState(Boolean value) {
        registerModifiedProperty("backupState");
        this.backupState = value;
    }

    public Long getCreatedAt() {
        checkSpecifiedProperty("createdAt");
        return createdAt;
    }

    public void setCreatedAt(Long value) {
        registerModifiedProperty("createdAt");
        this.createdAt = value;
    }

    public String getLabel() {
        checkSpecifiedProperty("label");
        return convertEmptyToNull(label);
    }

    public void setLabel(String value) {
        registerModifiedProperty("label");
        this.label = value;
    }

    public Long getLastUsedAt() {
        checkSpecifiedProperty("lastUsedAt");
        return lastUsedAt;
    }

    public void setLastUsedAt(Long value) {
        registerModifiedProperty("lastUsedAt");
        this.lastUsedAt = value;
    }

    public String getPublicKey() {
        checkSpecifiedProperty("publicKey");
        return convertEmptyToNull(publicKey);
    }

    public void setPublicKey(String value) {
        registerModifiedProperty("publicKey");
        this.publicKey = value;
    }

    public Long getSignCount() {
        checkSpecifiedProperty("signCount");
        return signCount;
    }

    public void setSignCount(Long value) {
        registerModifiedProperty("signCount");
        this.signCount = value;
    }

    public String[] getTransports() {
        checkSpecifiedProperty("transports");
        return transports;
    }

    public void setTransports(String[] value) {
        registerModifiedProperty("transports");
        this.transports = value;
    }

    public String getUserHandle() {
        checkSpecifiedProperty("userHandle");
        return convertEmptyToNull(userHandle);
    }

    public void setUserHandle(String value) {
        registerModifiedProperty("userHandle");
        this.userHandle = value;
    }

    public String getUserName() {
        checkSpecifiedProperty("userName");
        return convertEmptyToNull(userName);
    }

    public void setUserName(String value) {
        registerModifiedProperty("userName");
        this.userName = value;
    }
}
