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
package org.codelibs.fess.helper;

import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalThing;

public class ActivityHelperTest extends UnitFessTestCase {

    private ActivityHelper activityHelper;

    private ThreadLocal<String> localLogMsg = new ThreadLocal<>();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        activityHelper = new ActivityHelper() {
            @Override
            protected void printByLtsv(final Map<String, String> valueMap) {
                valueMap.remove("time");
                valueMap.remove("ip");
                super.printByLtsv(valueMap);
            }

            @Override
            protected void printByEcs(final Map<String, String> valueMap) {
                valueMap.put("time", "2022-01-01T00:00:00.000Z");
                valueMap.remove("ip");
                super.printByEcs(valueMap);
            }

            @Override
            protected void printLog(final String message) {
                localLogMsg.set(message);
            }

            @Override
            protected String getClientIp() {
                return StringUtil.EMPTY;
            }
        };
    }

    public void test_login() {
        activityHelper.useEcsFormat = false;
        activityHelper.login(OptionalThing.empty());
        assertEquals("action:LOGIN\tuser:-\tpermissions:-", localLogMsg.get());

        activityHelper.login(createUser("testuser", new String[0]));
        assertEquals("action:LOGIN\tuser:testuser\tpermissions:-", localLogMsg.get());

        activityHelper.login(createUser("testuser", new String[] { "111", "222" }));
        assertEquals("action:LOGIN\tuser:testuser\tpermissions:111|222", localLogMsg.get());
    }

    public void test_loginFailure() {
        activityHelper.useEcsFormat = false;
        activityHelper.loginFailure(OptionalThing.empty());
        assertEquals("action:LOGIN_FAILURE", localLogMsg.get());
    }

    public void test_logout() {
        activityHelper.useEcsFormat = false;
        activityHelper.logout(OptionalThing.empty());
        assertEquals("action:LOGOUT\tuser:-\tpermissions:-", localLogMsg.get());

        activityHelper.logout(createUser("testuser", new String[0]));
        assertEquals("action:LOGOUT\tuser:testuser\tpermissions:-", localLogMsg.get());

        activityHelper.logout(createUser("testuser", new String[] { "111", "222" }));
        assertEquals("action:LOGOUT\tuser:testuser\tpermissions:111|222", localLogMsg.get());
    }

    public void test_access() {
        activityHelper.useEcsFormat = false;
        activityHelper.access(OptionalThing.empty(), "/", "aaa");
        assertEquals("action:ACCESS\tuser:-\tpath:/\texecute:aaa", localLogMsg.get());

        activityHelper.access(createUser("testuser", new String[0]), "/aaa", "bbb");
        assertEquals("action:ACCESS\tuser:testuser\tpath:/aaa\texecute:bbb", localLogMsg.get());

        activityHelper.access(createUser("testuser", new String[] { "111", "222" }), "/aaa/bbb", "ccc");
        assertEquals("action:ACCESS\tuser:testuser\tpath:/aaa/bbb\texecute:ccc", localLogMsg.get());
    }

    public void test_permissionChanged() {
        activityHelper.useEcsFormat = false;
        activityHelper.permissionChanged(OptionalThing.empty());
        assertEquals("action:UPDATE_PERMISSION\tuser:-\tpermissions:-", localLogMsg.get());

        activityHelper.permissionChanged(createUser("testuser", new String[0]));
        assertEquals("action:UPDATE_PERMISSION\tuser:testuser\tpermissions:-", localLogMsg.get());

        activityHelper.permissionChanged(createUser("testuser", new String[] { "111", "222" }));
        assertEquals("action:UPDATE_PERMISSION\tuser:testuser\tpermissions:111|222", localLogMsg.get());
    }

    public void test_print() {
        activityHelper.useEcsFormat = false;
        activityHelper.print("aaa", OptionalThing.empty(), Map.of());
        assertEquals("action:AAA\tuser:-", localLogMsg.get());

        activityHelper.print("aaa bbb", createUser("testuser", new String[0]), Map.of("111", "222"));
        assertEquals("action:AAA BBB\tuser:testuser\t111:222", localLogMsg.get());

        activityHelper.print("ccc", createUser("testuser", new String[] { "111", "222" }), Map.of("111", "222", "333", "444"));
        assertEquals("action:CCC\tuser:testuser\t111:222\t333:444", localLogMsg.get());
    }

    public void test_login_ecs() {
        activityHelper.useEcsFormat = true;
        activityHelper.login(OptionalThing.empty());
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"LOGIN\",\"labels.user\":\"-\",\"labels.permissions\":\"-\"}",
                localLogMsg.get());

        activityHelper.login(createUser("testuser", new String[0]));
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"LOGIN\",\"labels.user\":\"testuser\",\"labels.permissions\":\"-\"}",
                localLogMsg.get());

        activityHelper.login(createUser("testuser", new String[] { "111", "222" }));
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"LOGIN\",\"labels.user\":\"testuser\",\"labels.permissions\":\"111|222\"}",
                localLogMsg.get());
    }

    public void test_loginFailure_ecs() {
        activityHelper.useEcsFormat = true;
        activityHelper.loginFailure(OptionalThing.empty());
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"LOGIN_FAILURE\"}",
                localLogMsg.get());
    }

    public void test_logout_ecs() {
        activityHelper.useEcsFormat = true;
        activityHelper.logout(OptionalThing.empty());
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"LOGOUT\",\"labels.user\":\"-\",\"labels.permissions\":\"-\"}",
                localLogMsg.get());

        activityHelper.logout(createUser("testuser", new String[0]));
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"LOGOUT\",\"labels.user\":\"testuser\",\"labels.permissions\":\"-\"}",
                localLogMsg.get());

        activityHelper.logout(createUser("testuser", new String[] { "111", "222" }));
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"LOGOUT\",\"labels.user\":\"testuser\",\"labels.permissions\":\"111|222\"}",
                localLogMsg.get());
    }

    public void test_access_ecs() {
        activityHelper.useEcsFormat = true;
        activityHelper.access(OptionalThing.empty(), "/", "aaa");
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"ACCESS\",\"labels.user\":\"-\",\"labels.path\":\"\\/\",\"labels.execute\":\"aaa\"}",
                localLogMsg.get());

        activityHelper.access(createUser("testuser", new String[0]), "/aaa", "bbb");
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"ACCESS\",\"labels.user\":\"testuser\",\"labels.path\":\"\\/aaa\",\"labels.execute\":\"bbb\"}",
                localLogMsg.get());

        activityHelper.access(createUser("testuser", new String[] { "111", "222" }), "/aaa/bbb", "ccc");
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"ACCESS\",\"labels.user\":\"testuser\",\"labels.path\":\"\\/aaa\\/bbb\",\"labels.execute\":\"ccc\"}",
                localLogMsg.get());
    }

    public void test_permissionChanged_ecs() {
        activityHelper.useEcsFormat = true;
        activityHelper.permissionChanged(OptionalThing.empty());
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"UPDATE_PERMISSION\",\"labels.user\":\"-\",\"labels.permissions\":\"-\"}",
                localLogMsg.get());

        activityHelper.permissionChanged(createUser("testuser", new String[0]));
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"UPDATE_PERMISSION\",\"labels.user\":\"testuser\",\"labels.permissions\":\"-\"}",
                localLogMsg.get());

        activityHelper.permissionChanged(createUser("testuser", new String[] { "111", "222" }));
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"UPDATE_PERMISSION\",\"labels.user\":\"testuser\",\"labels.permissions\":\"111|222\"}",
                localLogMsg.get());
    }

    public void test_print_ecs() {
        activityHelper.useEcsFormat = true;
        activityHelper.print("aaa", OptionalThing.empty(), Map.of());
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"AAA\",\"labels.user\":\"-\"}",
                localLogMsg.get());

        activityHelper.print("aaa bbb", createUser("testuser", new String[0]), Map.of("111", "222"));
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"AAA BBB\",\"labels.user\":\"testuser\",\"labels.111\":\"222\"}",
                localLogMsg.get());

        activityHelper.print("ccc", createUser("testuser", new String[] { "111", "222" }), Map.of("111", "222", "333", "444"));
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"CCC\",\"labels.user\":\"testuser\",\"labels.111\":\"222\",\"labels.333\":\"444\"}",
                localLogMsg.get());
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
