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
package org.codelibs.fess.helper;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.login.credential.LoginCredential;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class ActivityHelperTest extends UnitFessTestCase {

    private ActivityHelper activityHelper;

    private ThreadLocal<String> localLogMsg = new ThreadLocal<>();

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
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

    @Test
    public void test_login() {
        activityHelper.useEcsFormat = false;
        activityHelper.login(OptionalThing.empty());
        assertEquals("action:LOGIN\tuser:-\tpermissions:-", localLogMsg.get());

        activityHelper.login(createUser("testuser", new String[0]));
        assertEquals("action:LOGIN\tuser:testuser\tpermissions:-", localLogMsg.get());

        activityHelper.login(createUser("testuser", new String[] { "111", "222" }));
        assertEquals("action:LOGIN\tuser:testuser\tpermissions:111|222", localLogMsg.get());
    }

    @Test
    public void test_loginFailure() {
        activityHelper.useEcsFormat = false;
        activityHelper.loginFailure(OptionalThing.empty());
        assertEquals("action:LOGIN_FAILURE", localLogMsg.get());
    }

    @Test
    public void test_logout() {
        activityHelper.useEcsFormat = false;
        activityHelper.logout(OptionalThing.empty());
        assertEquals("action:LOGOUT\tuser:-\tpermissions:-", localLogMsg.get());

        activityHelper.logout(createUser("testuser", new String[0]));
        assertEquals("action:LOGOUT\tuser:testuser\tpermissions:-", localLogMsg.get());

        activityHelper.logout(createUser("testuser", new String[] { "111", "222" }));
        assertEquals("action:LOGOUT\tuser:testuser\tpermissions:111|222", localLogMsg.get());
    }

    @Test
    public void test_access() {
        activityHelper.useEcsFormat = false;
        activityHelper.access(OptionalThing.empty(), "/", "aaa");
        assertEquals("action:ACCESS\tuser:-\tpath:/\texecute:aaa", localLogMsg.get());

        activityHelper.access(createUser("testuser", new String[0]), "/aaa", "bbb");
        assertEquals("action:ACCESS\tuser:testuser\tpath:/aaa\texecute:bbb", localLogMsg.get());

        activityHelper.access(createUser("testuser", new String[] { "111", "222" }), "/aaa/bbb", "ccc");
        assertEquals("action:ACCESS\tuser:testuser\tpath:/aaa/bbb\texecute:ccc", localLogMsg.get());
    }

    @Test
    public void test_permissionChanged() {
        activityHelper.useEcsFormat = false;
        activityHelper.permissionChanged(OptionalThing.empty());
        assertEquals("action:UPDATE_PERMISSION\tuser:-\tpermissions:-", localLogMsg.get());

        activityHelper.permissionChanged(createUser("testuser", new String[0]));
        assertEquals("action:UPDATE_PERMISSION\tuser:testuser\tpermissions:-", localLogMsg.get());

        activityHelper.permissionChanged(createUser("testuser", new String[] { "111", "222" }));
        assertEquals("action:UPDATE_PERMISSION\tuser:testuser\tpermissions:111|222", localLogMsg.get());
    }

    @Test
    public void test_print() {
        activityHelper.useEcsFormat = false;
        activityHelper.print("aaa", OptionalThing.empty(), Map.of());
        assertEquals("action:AAA\tuser:-", localLogMsg.get());

        activityHelper.print("aaa bbb", createUser("testuser", new String[0]), Map.of("111", "222"));
        assertEquals("action:AAA BBB\tuser:testuser\t111:222", localLogMsg.get());

        activityHelper.print("ccc", createUser("testuser", new String[] { "111", "222" }), Map.of("111", "222", "333", "444"));
        assertEquals("action:CCC\tuser:testuser\t111:222\t333:444", localLogMsg.get());
    }

    @Test
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

    @Test
    public void test_loginFailure_ecs() {
        activityHelper.useEcsFormat = true;
        activityHelper.loginFailure(OptionalThing.empty());
        assertEquals(
                "{\"@timestamp\":\"2022-01-01T00:00:00.000Z\",\"log.level\":\"INFO\",\"ecs.version\":\"1.2.0\",\"service.name\":\"fess\",\"event.dataset\":\"app\",\"process.thread.name\":\"main\",\"log.logger\":\"org.codelibs.fess.helper.ActivityHelperTest$1\",\"labels.action\":\"LOGIN_FAILURE\"}",
                localLogMsg.get());
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    // ===== Script Execution Audit Log Tests =====

    @Test
    public void test_normalizeScript_null() {
        assertEquals("-", activityHelper.normalizeScript(null));
    }

    @Test
    public void test_normalizeScript_empty() {
        assertEquals("", activityHelper.normalizeScript(""));
    }

    @Test
    public void test_normalizeScript_normal() {
        assertEquals("return 1 + 2", activityHelper.normalizeScript("return 1 + 2"));
    }

    @Test
    public void test_normalizeScript_withControlCharacters() {
        assertEquals("line1 line2 line3", activityHelper.normalizeScript("line1\nline2\rline3"));
        assertEquals("tab1_tab2", activityHelper.normalizeScript("tab1\ttab2"));
        // \t -> _, \n -> space, \r -> space, \n -> space
        assertEquals("mixed_   ", activityHelper.normalizeScript("mixed\t\n\r\n"));
    }

    @Test
    public void test_normalizeScript_longScript() {
        // Create a script longer than 1000 characters
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1100; i++) {
            sb.append("a");
        }
        String longScript = sb.toString();

        String result = activityHelper.normalizeScript(longScript);

        // Should be truncated to 1000 characters (997 + "...")
        assertEquals(1000, result.length());
        assertTrue(result.endsWith("..."));
    }

    @Test
    public void test_normalizeScript_exactlyMaxLength() {
        // Create a script of exactly 1000 characters
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("b");
        }
        String exactScript = sb.toString();

        String result = activityHelper.normalizeScript(exactScript);

        // Should not be truncated
        assertEquals(1000, result.length());
        assertFalse(result.endsWith("..."));
    }

    @Test
    public void test_normalizeScript_lessThanMaxLength() {
        // Create a script of 999 characters
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 999; i++) {
            sb.append("c");
        }
        String shortScript = sb.toString();

        String result = activityHelper.normalizeScript(shortScript);

        // Should not be truncated
        assertEquals(999, result.length());
        assertFalse(result.endsWith("..."));
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

    @Test
    public void test_setLoggerName() {
        String originalLoggerName = activityHelper.loggerName;
        activityHelper.setLoggerName("test.logger");
        assertEquals("test.logger", activityHelper.loggerName);
        activityHelper.setLoggerName(originalLoggerName);
    }

    @Test
    public void test_setPermissionSeparator() {
        activityHelper.setPermissionSeparator(",");
        activityHelper.useEcsFormat = false;
        activityHelper.login(createUser("testuser", new String[] { "111", "222" }));
        assertEquals("action:LOGIN\tuser:testuser\tpermissions:111,222", localLogMsg.get());
    }

    @Test
    public void test_setEcsVersion() {
        activityHelper.setEcsVersion("2.0.0");
        activityHelper.useEcsFormat = true;
        activityHelper.login(OptionalThing.empty());
        assertTrue(localLogMsg.get().contains("\"ecs.version\":\"2.0.0\""));
    }

    @Test
    public void test_setEcsServiceName() {
        activityHelper.setEcsServiceName("test-service");
        activityHelper.useEcsFormat = true;
        activityHelper.login(OptionalThing.empty());
        assertTrue(localLogMsg.get().contains("\"service.name\":\"test-service\""));
    }

    @Test
    public void test_setEcsEventDataset() {
        activityHelper.setEcsEventDataset("test-dataset");
        activityHelper.useEcsFormat = true;
        activityHelper.login(OptionalThing.empty());
        assertTrue(localLogMsg.get().contains("\"event.dataset\":\"test-dataset\""));
    }

    @Test
    public void test_setEnvMap() {
        Map<String, String> testEnv = new HashMap<>();
        testEnv.put("TEST_KEY", "TEST_VALUE");
        activityHelper.setEnvMap(testEnv);
        assertEquals(testEnv, activityHelper.getEnvMap());
    }

    @Test
    public void test_getEnvMap_withoutCustomMap() {
        activityHelper.setEnvMap(null);
        Map<String, String> envMap = activityHelper.getEnvMap();
        assertNotNull(envMap);
    }

    @Test
    public void test_init_withDockerEnvironment() {
        Map<String, String> dockerEnv = new HashMap<>();
        dockerEnv.put("FESS_APP_TYPE", "docker");
        activityHelper.setEnvMap(dockerEnv);
        activityHelper.init();
        assertTrue(activityHelper.useEcsFormat);
    }

    @Test
    public void test_init_withNonDockerEnvironment() {
        Map<String, String> nonDockerEnv = new HashMap<>();
        nonDockerEnv.put("FESS_APP_TYPE", "standalone");
        activityHelper.setEnvMap(nonDockerEnv);
        activityHelper.init();
        assertFalse(activityHelper.useEcsFormat);
    }

    @Test
    public void test_loginFailure_withCredential() {
        activityHelper.useEcsFormat = false;
        LoginCredential credential = new TestLoginCredential();
        activityHelper.loginFailure(OptionalThing.of(credential));
        String result = localLogMsg.get();
        assertTrue(result.contains("action:LOGIN_FAILURE"));
        assertTrue(result.contains("class:TestLoginCredential"));
    }

    @Test
    public void test_print_withTabReplacement() {
        activityHelper.useEcsFormat = false;
        Map<String, String> params = new HashMap<>();
        params.put("key\twith\ttabs", "value\twith\ttabs");
        activityHelper.print("test\taction", OptionalThing.empty(), params);
        String result = localLogMsg.get();
        assertTrue(result.contains("action:TEST_ACTION"));
        assertTrue(result.contains("key\twith\ttabs:value_with_tabs"));
    }

    @Test
    public void test_print_parameterSorting() {
        activityHelper.useEcsFormat = false;
        Map<String, String> params = new HashMap<>();
        params.put("z_param", "z_value");
        params.put("a_param", "a_value");
        params.put("m_param", "m_value");
        activityHelper.print("test", OptionalThing.empty(), params);
        String result = localLogMsg.get();
        assertTrue(result.indexOf("a_param") < result.indexOf("m_param"));
        assertTrue(result.indexOf("m_param") < result.indexOf("z_param"));
    }

    @Test
    public void test_printByEcs_escaping() {
        activityHelper.useEcsFormat = true;
        Map<String, String> params = new HashMap<>();
        params.put("quotes", "text with \"quotes\"");
        params.put("backslash", "text\\with\\backslash");
        activityHelper.print("test", OptionalThing.empty(), params);
        String result = localLogMsg.get();
        assertTrue(result.contains("\\\"quotes\\\""));
        assertTrue(result.contains("text\\\\with\\\\backslash"));
    }

    static class TestLoginCredential implements LoginCredential {
        @Override
        public String toString() {
            return "TestLoginCredential";
        }
    }
}
