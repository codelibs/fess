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

import org.codelibs.fess.opensearch.user.exentity.Group;
import org.codelibs.fess.opensearch.user.exentity.Role;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Tests that an attribute without a value, as the admin API stores for {@code "attributes":{"gidNumber":null}},
 * is neither written to a user, group or role document nor breaks the read of one that already holds it.
 * Such a document made every list of its kind, and the admin user form, fail with a NullPointerException.
 */
public class UserGroupRoleBhvNullAttributeTest extends UnitFessTestCase {

    private static Map<String, Object> sourceWithNull(final String name) {
        final Map<String, Object> source = new HashMap<>();
        source.put("name", name);
        source.put("gidNumber", null);
        source.put("mail", "x@example.com");
        return source;
    }

    private static Map<String, String> attributesWithNull() {
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("gidNumber", null);
        attributes.put("mail", "x@example.com");
        return attributes;
    }

    @Test
    public void test_createEntity_user_skipsNullAttribute() {
        final User user = new UserBhv().createEntity(sourceWithNull("alice"), User.class);

        assertEquals("alice", user.getName());
        assertFalse(user.getAttributes().containsKey("gidNumber"));
        assertEquals("x@example.com", user.getAttributes().get("mail"));
    }

    @Test
    public void test_createEntity_group_skipsNullAttribute() {
        final Group group = new GroupBhv().createEntity(sourceWithNull("sales"), Group.class);

        assertEquals("sales", group.getName());
        assertFalse(group.getAttributes().containsKey("gidNumber"));
        assertEquals("x@example.com", group.getAttributes().get("mail"));
    }

    @Test
    public void test_createEntity_role_skipsNullAttribute() {
        final Role role = new RoleBhv().createEntity(sourceWithNull("eng"), Role.class);

        assertEquals("eng", role.getName());
        assertFalse(role.getAttributes().containsKey("gidNumber"));
        assertEquals("x@example.com", role.getAttributes().get("mail"));
    }

    @Test
    public void test_toSource_user_leavesOutNullAttribute() {
        final User user = new User();
        user.setName("alice");
        user.setAttributes(attributesWithNull());

        final Map<String, Object> source = user.toSource();

        assertFalse(source.containsKey("gidNumber"));
        assertEquals("x@example.com", source.get("mail"));
    }

    @Test
    public void test_toSource_group_leavesOutNullAttribute() {
        final Group group = new Group();
        group.setName("sales");
        group.setAttributes(attributesWithNull());

        final Map<String, Object> source = group.toSource();

        assertFalse(source.containsKey("gidNumber"));
        assertEquals("sales", source.get("name"));
        assertEquals("x@example.com", source.get("mail"));
    }

    @Test
    public void test_toSource_role_leavesOutNullAttribute() {
        final Role role = new Role();
        role.setName("eng");
        role.setAttributes(attributesWithNull());

        final Map<String, Object> source = role.toSource();

        assertFalse(source.containsKey("gidNumber"));
        assertEquals("eng", source.get("name"));
        assertEquals("x@example.com", source.get("mail"));
    }
}
