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
package org.codelibs.fess.opensearch.user.exbhv;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.exentity.Passkey;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

/**
 * The passkey index follows the configured user index name, like the role and group indexes.
 */
public class PasskeyBhvTest extends UnitFessTestCase {

    @Test
    public void test_asEsIndex_usesConfiguredUserIndex() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexUserIndex() {
                return "custom_user";
            }
        });
        assertEquals("custom_user.passkey", new PasskeyBhv().asEsIndex());
    }

    @Test
    public void test_createEntity_readsAllFields() {
        final Map<String, Object> source = new HashMap<>();
        source.put("userName", "alice");
        source.put("userHandle", "handle");
        source.put("publicKey", "key");
        source.put("signCount", 3); // OpenSearch returns small long values as Integer
        source.put("transports", new String[] { "internal", "hybrid" });
        source.put("backupEligible", true);
        source.put("backupState", false);
        source.put("label", "laptop");
        source.put("createdAt", 1_700_000_000_000L);
        source.put("lastUsedAt", 1_700_000_001_000L);

        final Passkey passkey = new PasskeyBhv() {
            Passkey read(final Map<String, Object> map) {
                return createEntity(map, Passkey.class);
            }
        }.read(source);

        assertEquals("alice", passkey.getUserName());
        assertEquals("handle", passkey.getUserHandle());
        assertEquals("key", passkey.getPublicKey());
        assertEquals(Long.valueOf(3), passkey.getSignCount());
        assertEquals(2, passkey.getTransports().length);
        assertTrue(passkey.getBackupEligible());
        assertFalse(passkey.getBackupState());
        assertEquals("laptop", passkey.getLabel());
        assertEquals(Long.valueOf(1_700_000_000_000L), passkey.getCreatedAt());
        assertEquals(Long.valueOf(1_700_000_001_000L), passkey.getLastUsedAt());
    }

    @Test
    public void test_toString_omitsPublicKey() {
        final Passkey passkey = new Passkey();
        passkey.setUserName("alice");
        passkey.setPublicKey("secret-key-material");
        assertFalse(passkey.toString().contains("secret-key-material"));
    }
}
