/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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
package org.codelibs.fess.helper;

import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalThing;
import org.opensearch.common.collect.Map;

public class ActivityHelperTest extends UnitFessTestCase {

    private ActivityHelper activityHelper;

    private ThreadLocal<String> localLogMsg = new ThreadLocal<>();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        activityHelper = new ActivityHelper() {
            @Override
            protected void log(final StringBuilder buf) {
                localLogMsg.set(buf.toString());
            }
        };
    }

    public void test_login() {
        activityHelper.login(OptionalThing.empty());
        assertEquals("action:LOGIN\tuser:-\tpermissions:-", localLogMsg.get());

        activityHelper.login(createUser("testuser", new String[0]));
        assertEquals("action:LOGIN\tuser:testuser\tpermissions:-", localLogMsg.get());

        activityHelper.login(createUser("testuser", new String[] { "111", "222" }));
        assertEquals("action:LOGIN\tuser:testuser\tpermissions:111|222", localLogMsg.get());
    }

    public void test_loginFailure() {
        activityHelper.loginFailure(OptionalThing.empty());
        assertEquals("action:LOGIN_FAILURE", localLogMsg.get());
    }

    public void test_logout() {
        activityHelper.logout(OptionalThing.empty());
        assertEquals("action:LOGOUT\tuser:-\tpermissions:-", localLogMsg.get());

        activityHelper.logout(createUser("testuser", new String[0]));
        assertEquals("action:LOGOUT\tuser:testuser\tpermissions:-", localLogMsg.get());

        activityHelper.logout(createUser("testuser", new String[] { "111", "222" }));
        assertEquals("action:LOGOUT\tuser:testuser\tpermissions:111|222", localLogMsg.get());
    }

    public void test_access() {
        activityHelper.access(OptionalThing.empty(), "/", "aaa");
        assertEquals("action:ACCESS\tuser:-\tpath:/\texecute:aaa", localLogMsg.get());

        activityHelper.access(createUser("testuser", new String[0]), "/aaa", "bbb");
        assertEquals("action:ACCESS\tuser:testuser\tpath:/aaa\texecute:bbb", localLogMsg.get());

        activityHelper.access(createUser("testuser", new String[] { "111", "222" }), "/aaa/bbb", "ccc");
        assertEquals("action:ACCESS\tuser:testuser\tpath:/aaa/bbb\texecute:ccc", localLogMsg.get());
    }

    public void test_permissionChanged() {
        activityHelper.permissionChanged(OptionalThing.empty());
        assertEquals("action:UPDATE_PERMISSION\tuser:-\tpermissions:-", localLogMsg.get());

        activityHelper.permissionChanged(createUser("testuser", new String[0]));
        assertEquals("action:UPDATE_PERMISSION\tuser:testuser\tpermissions:-", localLogMsg.get());

        activityHelper.permissionChanged(createUser("testuser", new String[] { "111", "222" }));
        assertEquals("action:UPDATE_PERMISSION\tuser:testuser\tpermissions:111|222", localLogMsg.get());
    }

    public void test_print() {
        activityHelper.print("aaa", OptionalThing.empty(), Map.of());
        assertEquals("action:AAA\tuser:-", localLogMsg.get());

        activityHelper.print("aaa bbb", createUser("testuser", new String[0]), Map.of("111", "222"));
        assertEquals("action:AAA BBB\tuser:testuser\t111:222", localLogMsg.get());

        activityHelper.print("ccc", createUser("testuser", new String[] { "111", "222" }), Map.of("111", "222", "333", "444"));
        assertEquals("action:CCC\tuser:testuser\t111:222\t333:444", localLogMsg.get());
    }

    OptionalThing<FessUserBean> createUser(String name, String[] permissions) {
        return OptionalThing.of(new FessUserBean(new TestUser(name, permissions)));
    }

    static class TestUser implements FessUser {

        private static final long serialVersionUID = 1L;

        private String name;

        private String[] permissions;

        TestUser(String name, String[] permissions) {
            this.name = name;
            this.permissions = permissions;

        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String[] getRoleNames() {
            return new String[0];
        }

        @Override
        public String[] getGroupNames() {
            return new String[0];
        }

        @Override
        public String[] getPermissions() {
            return permissions;
        }

    }
}
