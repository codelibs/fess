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
package org.codelibs.fess.app.web.api.admin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.ActivityHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.validation.VaMessenger;

public class FessApiAdminActionTest extends UnitFessTestCase {

    @Test
    public void test_hookBefore_recordsAccessForATokenRequest() {
        final List<Map<String, String>> capturedLogs = new ArrayList<>();
        final FessApiAdminAction action = createAction(capturedLogs, OptionalThing.empty(), true);

        action.hookBefore(new TestActionRuntime("/api/admin/labeltype/setting", "post$setting"));

        assertEquals(1, capturedLogs.size());
        assertEquals("-", capturedLogs.get(0).get("user"));
        assertEquals("/api/admin/labeltype/setting", capturedLogs.get(0).get("path"));
        assertEquals("post$setting", capturedLogs.get(0).get("execute"));
    }

    @Test
    public void test_hookBefore_recordsTheLoggedInUser() {
        final List<Map<String, String>> capturedLogs = new ArrayList<>();
        final FessApiAdminAction action = createAction(capturedLogs, OptionalThing.of(new FessUserBean(new TestUser("admin"))), true);

        action.hookBefore(new TestActionRuntime("/api/admin/user/setting", "put$setting"));

        assertEquals(1, capturedLogs.size());
        assertEquals("admin", capturedLogs.get(0).get("user"));
        assertEquals("/api/admin/user/setting", capturedLogs.get(0).get("path"));
        assertEquals("put$setting", capturedLogs.get(0).get("execute"));
    }

    @Test
    public void test_godHandPrologue_recordsNothingForARejectedToken() {
        final List<Map<String, String>> capturedLogs = new ArrayList<>();
        ComponentUtil.register(new SystemHelper(), "systemHelper"); // ApiResult reads the product version
        final FessApiAdminAction action = createAction(capturedLogs, OptionalThing.empty(), false);

        assertNotNull(action.godHandPrologue(new TestActionRuntime("/api/admin/labeltype/setting", "post$setting")));

        assertEquals(0, capturedLogs.size());
    }

    @Test
    public void test_hookBefore_answersNotFoundForAnActionThatNeedsThePluginsWhenThereAreNone() throws Exception {
        for (final String type : new String[] { "vanilla", "aws", "cloud" }) {
            ComponentUtil.register(new SystemHelper(), "systemHelper"); // ApiResult reads the product version
            final List<Map<String, String>> capturedLogs = new ArrayList<>();
            final FessApiAdminAction action = createEngineAction(capturedLogs, type, true);

            final ActionResponse response = action.hookBefore(new TestActionRuntime("/api/admin/dict/synonym/settings", "post$setting"));

            assertTrue(type, response instanceof JsonResponse);
            final JsonResponse<?> jsonResponse = (JsonResponse<?>) response;
            assertEquals(type, Integer.valueOf(404), jsonResponse.getHttpStatus().get());
            final Object apiResponse = read(ApiResult.class, "response", jsonResponse.getJsonResult());
            assertEquals(type, Status.FAILED.getId(), read(ApiResult.ApiResponse.class, "status", apiResponse));
            assertTrue(type, ((String) read(ApiResult.ApiErrorResponse.class, "message", apiResponse)).contains("CodeLibs plugins"));
            // the request is audited before it is turned away
            assertEquals(type, 1, capturedLogs.size());
            assertEquals(type, "/api/admin/dict/synonym/settings", capturedLogs.get(0).get("path"));
        }
    }

    @Test
    public void test_hookBefore_runsAnActionThatNeedsThePluginsWhenTheyAreAvailable() {
        for (final String type : new String[] { "default", "opensearch", "" }) {
            final List<Map<String, String>> capturedLogs = new ArrayList<>();
            final FessApiAdminAction action = createEngineAction(capturedLogs, type, true);

            final ActionResponse response = action.hookBefore(new TestActionRuntime("/api/admin/dict/synonym/settings", "post$setting"));

            assertTrue(type, response.isUndefined());
            assertEquals(type, 1, capturedLogs.size());
        }
    }

    @Test
    public void test_hookBefore_runsAnActionThatDoesNotNeedThePluginsOnAnyEngine() {
        for (final String type : new String[] { "default", "vanilla", "aws", "cloud" }) {
            final FessApiAdminAction action = createEngineAction(new ArrayList<>(), type, false);

            assertTrue(type, action.hookBefore(new TestActionRuntime("/api/admin/general", "post$setting")).isUndefined());
        }
    }

    @Test
    public void test_requiresEnginePlugins_isFalseByDefault() {
        assertFalse(createAction(new ArrayList<>(), OptionalThing.empty(), true).requiresEnginePlugins());
    }

    @Test
    public void test_godHandPrologue_rejectsADeniedTokenBeforeTheEnginePluginGuard() {
        // the guard is in hookBefore, after the token check: a caller without access gets the 401
        // of the prologue whatever the engine is
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final FessApiAdminAction action = createEngineAction(new ArrayList<>(), "vanilla", true);

        final ActionResponse response = action.godHandPrologue(new TestActionRuntime("/api/admin/dict/synonym/settings", "post$setting"));

        assertEquals(Integer.valueOf(401), ((JsonResponse<?>) response).getHttpStatus().get());
    }

    private static Object read(final Class<?> owner, final String name, final Object target) throws Exception {
        final Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    private FessApiAdminAction createEngineAction(final List<Map<String, String>> capturedLogs, final String type,
            final boolean requiresEnginePlugins) {
        final ActivityHelper spyActivityHelper = new ActivityHelper() {
            @Override
            public void access(final OptionalThing<FessUserBean> user, final String path, final String execute) {
                final Map<String, String> log = new LinkedHashMap<>();
                log.put("path", path);
                log.put("execute", execute);
                capturedLogs.add(log);
            }
        };
        return new FessApiAdminAction() {
            {
                activityHelper = spyActivityHelper;
                viewHelper = new ViewHelper();
                fessConfig = new FessConfig.SimpleImpl() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getSearchEngineType() {
                        return type;
                    }
                };
            }

            @Override
            protected boolean isAccessAllowed() {
                return false;
            }

            @Override
            protected boolean requiresEnginePlugins() {
                return requiresEnginePlugins;
            }

            @Override
            protected OptionalThing<FessUserBean> getUserBean() {
                return OptionalThing.empty();
            }

            @Override
            protected String getMessage(final VaMessenger<FessMessages> validationMessagesLambda) {
                return "unauthorized";
            }
        };
    }

    private FessApiAdminAction createAction(final List<Map<String, String>> capturedLogs, final OptionalThing<FessUserBean> userBean,
            final boolean accessAllowed) {
        final ActivityHelper spyActivityHelper = new ActivityHelper() {
            @Override
            public void access(final OptionalThing<FessUserBean> user, final String path, final String execute) {
                final Map<String, String> log = new LinkedHashMap<>();
                log.put("user", user.map(FessUserBean::getUserId).orElse("-"));
                log.put("path", path);
                log.put("execute", execute);
                capturedLogs.add(log);
            }
        };
        return new FessApiAdminAction() {
            {
                activityHelper = spyActivityHelper;
                viewHelper = new ViewHelper();
            }

            @Override
            protected boolean isAccessAllowed() {
                return accessAllowed;
            }

            @Override
            protected OptionalThing<FessUserBean> getUserBean() {
                return userBean;
            }

            @Override
            protected String getMessage(final VaMessenger<FessMessages> validationMessagesLambda) {
                return "unauthorized";
            }
        };
    }

    static class TestActionRuntime extends ActionRuntime {

        private final Method executeMethod;

        TestActionRuntime(final String requestPath, final String executeName) {
            super(requestPath, null, null);
            try {
                executeMethod = DummyAction.class.getMethod(executeName);
            } catch (final NoSuchMethodException e) {
                throw new IllegalArgumentException(executeName, e);
            }
        }

        @Override
        public Method getExecuteMethod() {
            return executeMethod;
        }
    }

    public static class DummyAction {
        public void post$setting() {
        }

        public void put$setting() {
        }
    }

    static class TestUser implements FessUser {

        private static final long serialVersionUID = 1L;

        private final String name;

        TestUser(final String name) {
            this.name = name;
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
            return new String[0];
        }
    }
}
