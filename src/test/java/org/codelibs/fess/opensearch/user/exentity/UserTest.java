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
package org.codelibs.fess.opensearch.user.exentity;

import java.util.Arrays;
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link User}, covering how stored role and group ids are read back.
 */
public class UserTest extends UnitFessTestCase {

    /**
     * Role and group ids are Base64-encoded names, and they decode back to those names.
     */
    @Test
    public void test_getRoleNames_decodesStoredIds() {
        final User user = new User();
        user.setName("tester");
        user.setRoles(new String[] { "Z3Vlc3Q=", "YWRtaW4=" });
        user.setGroups(new String[] { "c2FsZXM=" });

        assertEquals(List.of("guest", "admin"), Arrays.asList(user.getRoleNames()));
        assertEquals(List.of("sales"), Arrays.asList(user.getGroupNames()));
    }

    /**
     * A role stored as a name rather than an id cannot be decoded. Reading it back skips it instead
     * of throwing, so the account can still be listed and repaired; without this every read of the
     * user fails and the account can no longer log in.
     */
    @Test
    public void test_getRoleNames_skipsAnIdThatIsNotBase64() {
        final User user = new User();
        user.setName("tester");
        user.setRoles(new String[] { "guest", "YWRtaW4=" });
        user.setGroups(new String[] { "sales" });

        assertEquals(List.of("admin"), Arrays.asList(user.getRoleNames()));
        assertEquals(List.of(), Arrays.asList(user.getGroupNames()));
    }

    /**
     * The permission list is what a login builds the search roles from, so it has to survive the
     * same broken value.
     */
    @Test
    public void test_getPermissions_skipsAnIdThatIsNotBase64() {
        final User user = new User();
        user.setName("tester");
        user.setRoles(new String[] { "guest", "YWRtaW4=" });
        user.setGroups(new String[] { "sales" });

        assertEquals(List.of("1tester", "Radmin"), Arrays.asList(user.getPermissions()));
    }
}
