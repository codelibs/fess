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

import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Tests that the time of the last password change survives a read and a write of a user document.
 * The document is rewritten whole, so a field that is not carried through is erased by every edit.
 */
public class UserBhvPasswordChangedAtTest extends UnitFessTestCase {

    private final UserBhv bhv = new UserBhv();

    @Test
    public void test_createEntity_readsTheTime_asNumber() {
        final Map<String, Object> source = new HashMap<>();
        source.put("name", "alice");
        source.put("password_changed_at", 1_700_000_000_000L);

        final User user = bhv.createEntity(source, User.class);

        assertEquals(Long.valueOf(1_700_000_000_000L), user.getPasswordChangedAt());
        assertFalse(user.getAttributes().containsKey("password_changed_at"));
    }

    @Test
    public void test_createEntity_readsTheTime_whenTheJsonParserGaveAnInteger() {
        final Map<String, Object> source = new HashMap<>();
        source.put("name", "alice");
        source.put("password_changed_at", Integer.valueOf(1234));

        assertEquals(Long.valueOf(1234L), bhv.createEntity(source, User.class).getPasswordChangedAt());
    }

    @Test
    public void test_createEntity_documentWithoutTheField() {
        final Map<String, Object> source = new HashMap<>();
        source.put("name", "alice");
        source.put("mail", "alice@example.com");

        final User user = bhv.createEntity(source, User.class);

        assertNull(user.getPasswordChangedAt());
        assertEquals("alice@example.com", user.getAttributes().get("mail"));
    }

    @Test
    public void test_toSource_writesTheTime_andOmitsItWhenThereIsNone() {
        final User user = new User();
        user.setName("alice");
        assertFalse(user.toSource().containsKey("password_changed_at"));

        user.setPasswordChangedAt(42L);
        assertEquals(Long.valueOf(42L), user.toSource().get("password_changed_at"));
    }

    @Test
    public void test_readThenWrite_keepsTheTime() {
        final Map<String, Object> source = new HashMap<>();
        source.put("name", "alice");
        source.put("password", "{bcrypt}x");
        source.put("password_changed_at", 99L);

        final Map<String, Object> written = bhv.createEntity(source, User.class).toSource();

        assertEquals(Long.valueOf(99L), written.get("password_changed_at"));
    }
}
