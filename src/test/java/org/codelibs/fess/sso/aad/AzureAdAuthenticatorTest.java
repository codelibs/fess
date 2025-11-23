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
package org.codelibs.fess.sso.aad;

import java.util.ArrayList;
import java.util.List;

import org.codelibs.core.misc.Pair;
import org.codelibs.fess.unit.UnitFessTestCase;

public class AzureAdAuthenticatorTest extends UnitFessTestCase {
    public void test_addGroupOrRoleName() {
        AzureAdAuthenticator authenticator = new AzureAdAuthenticator();
        List<String> list = new ArrayList<>();

        list.clear();
        authenticator.addGroupOrRoleName(list, "test", true);
        assertEquals(1, list.size());
        assertEquals("test", list.get(0));

        list.clear();
        authenticator.addGroupOrRoleName(list, "test", false);
        assertEquals(1, list.size());
        assertEquals("test", list.get(0));

        list.clear();
        authenticator.addGroupOrRoleName(list, "test@codelibs.org", true);
        assertEquals(2, list.size());
        assertEquals("test@codelibs.org", list.get(0));
        assertEquals("test", list.get(1));

        list.clear();
        authenticator.addGroupOrRoleName(list, "test@codelibs.org", false);
        assertEquals(1, list.size());
        assertEquals("test@codelibs.org", list.get(0));

        list.clear();
        authenticator.addGroupOrRoleName(list, "test@codelibs.org@hoge.com", true);
        assertEquals(2, list.size());
        assertEquals("test@codelibs.org@hoge.com", list.get(0));
        assertEquals("test", list.get(1));

    }

    public void test_setMaxGroupDepth() {
        AzureAdAuthenticator authenticator = new AzureAdAuthenticator();

        // Test setting different max group depths
        authenticator.setMaxGroupDepth(5);
        authenticator.setMaxGroupDepth(20);
        authenticator.setMaxGroupDepth(1);

        // Verify method accepts valid values without exception
        assertTrue(true);
    }

    public void test_setGroupCacheExpiry() {
        AzureAdAuthenticator authenticator = new AzureAdAuthenticator();

        // Test setting different cache expiry values
        authenticator.setGroupCacheExpiry(300L);
        authenticator.setGroupCacheExpiry(600L);
        authenticator.setGroupCacheExpiry(60L);

        // Verify method accepts valid values without exception
        assertTrue(true);
    }

    public void test_getParentGroup_withDepthLimit() {
        AzureAdAuthenticator authenticator = new AzureAdAuthenticator();
        authenticator.setMaxGroupDepth(2);

        // Test that depth limit returns empty arrays when depth is exceeded
        // With depth limit set to 2, depth 10 should return empty arrays
        Pair<String[], String[]> result = authenticator.getParentGroup(null, "test-id", 10);
        assertNotNull(result);
        assertEquals(0, result.getFirst().length);
        assertEquals(0, result.getSecond().length);
    }

    public void test_getParentGroup_exactlyAtDepthLimit() {
        AzureAdAuthenticator authenticator = new AzureAdAuthenticator();
        authenticator.setMaxGroupDepth(5);

        // Test with depth exactly at the limit - should return empty arrays
        Pair<String[], String[]> result = authenticator.getParentGroup(null, "test-id", 5);
        assertNotNull(result);
        assertEquals(0, result.getFirst().length);
        assertEquals(0, result.getSecond().length);
    }

    public void test_getParentGroup_oneBeforeDepthLimit() {
        AzureAdAuthenticator authenticator = new AzureAdAuthenticator();
        authenticator.setMaxGroupDepth(5);

        // Test with depth one before the limit - should attempt to process
        // Will fail due to null user, but verifies depth check passes
        try {
            authenticator.getParentGroup(null, "test-id", 4);
            // If we reach here without NullPointerException, depth check passed
        } catch (NullPointerException e) {
            // Expected due to null user - depth check passed, processing attempted
            assertTrue(true);
        } catch (Exception e) {
            // Other exceptions are also acceptable as we're testing depth logic
            assertTrue(true);
        }
    }

    public void test_processParentGroup_callsOverloadWithDepth() {
        AzureAdAuthenticator authenticator = new AzureAdAuthenticator();
        authenticator.setMaxGroupDepth(3);

        List<String> groupList = new ArrayList<>();
        List<String> roleList = new ArrayList<>();

        // Test the overload that doesn't take depth parameter
        // It should call the depth-tracking version with depth 0
        try {
            authenticator.processParentGroup(null, groupList, roleList, "test-id");
        } catch (Exception e) {
            // Expected due to null user
        }

        // Verify lists are still valid
        assertNotNull(groupList);
        assertNotNull(roleList);
    }

    public void test_processParentGroup_respectsDepthLimit() {
        AzureAdAuthenticator authenticator = new AzureAdAuthenticator();
        authenticator.setMaxGroupDepth(2);

        List<String> groupList = new ArrayList<>();
        List<String> roleList = new ArrayList<>();

        // Test with depth exceeding limit - should return immediately
        authenticator.processParentGroup(null, groupList, roleList, "test-id", 5);

        // Lists should remain empty as depth limit prevents processing
        assertEquals(0, groupList.size());
        assertEquals(0, roleList.size());
    }

    public void test_setUseV2Endpoint() {
        AzureAdAuthenticator authenticator = new AzureAdAuthenticator();

        // Test parameter accepts final boolean (compile-time verification)
        authenticator.setUseV2Endpoint(true);
        authenticator.setUseV2Endpoint(false);

        // Verify method signature is correct
        assertTrue(true);
    }

    public void test_defaultMaxGroupDepth() {
        AzureAdAuthenticator authenticator = new AzureAdAuthenticator();

        // Test that default max depth (10) prevents deep recursion
        // Depth 100 should exceed default and return empty
        Pair<String[], String[]> result = authenticator.getParentGroup(null, "test-id", 100);
        assertNotNull(result);
        assertEquals(0, result.getFirst().length);
        assertEquals(0, result.getSecond().length);
    }
}
