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
package org.codelibs.fess.app.web.api.admin.user;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.helper.PasswordHashHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.ldap.LdapManager;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Covers the version check of {@code PUT /api/admin/user/setting}: the body must carry the version of
 * the user it was read from, as the edit form of the admin screen does.
 */
public class ApiAdminUserActionTest extends UnitFessTestCase {

    private static final String ID = "YWxpY2U=";

    private final List<User> stored = new ArrayList<>();

    private final List<String> encoded = new ArrayList<>();

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        ComponentUtil.register(new LdapManager() {
            @Override
            public void validateUserAttributes(final Class<?> type, final Map<String, String> attributes, final Consumer<String> consumer) {
                // the attributes are not what is under test
            }
        }, "ldapManager");
        ComponentUtil.register(new PasswordHashHelper() {
            @Override
            public String encode(final String plain) {
                encoded.add(plain);
                return "{test}" + plain;
            }
        }, "passwordHashHelper");
    }

    private ApiAdminUserAction createAction(final long storedVersion) throws Exception {
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        suppressBindingOf(org.codelibs.fess.app.service.AccessTokenService.class);
        suppressBindingOf(UserService.class);
        suppressBindingOf(org.codelibs.fess.app.service.RoleService.class);
        suppressBindingOf(org.codelibs.fess.app.service.GroupService.class);
        final ApiAdminUserAction action = new ApiAdminUserAction();
        inject(action);
        setField(action, FessBaseAction.class, "systemHelper", ComponentUtil.getSystemHelper());
        setField(action, FessBaseAction.class, "fessConfig", ComponentUtil.getFessConfig());
        final UserService userService = new UserService() {
            @Override
            public OptionalEntity<User> getUser(final String id) {
                final User user = new User();
                user.setId(ID);
                user.setName("alice");
                user.setVersionNo(storedVersion);
                return OptionalEntity.of(user);
            }

            @Override
            public void store(final User user) {
                stored.add(user);
            }
        };
        setField(action, ApiAdminUserAction.class, "userService", userService);
        // the shared edit logic of the admin screen reads the user through the container
        ComponentUtil.register(userService, UserService.class.getCanonicalName());
        return action;
    }

    private static void setField(final Object target, final Class<?> owner, final String name, final Object value) throws Exception {
        final Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static EditBody bodyOfVersion(final Integer version) {
        final EditBody body = new EditBody();
        body.id = ID;
        body.name = "alice";
        body.versionNo = version;
        return body;
    }

    @Test
    public void test_put_staleVersionIsRefusedAndNothingIsStored() throws Exception {
        final ApiAdminUserAction action = createAction(4);

        assertValidationError(() -> action.put$setting(bodyOfVersion(3))).handle(data -> {
            data.requiredMessageOf("_global", "errors.user_changed_concurrently");
        });
        assertTrue(stored.isEmpty());
    }

    @Test
    public void test_put_staleVersionIsRefusedBeforeThePasswordIsEncoded() throws Exception {
        final ApiAdminUserAction action = createAction(4);
        final EditBody body = bodyOfVersion(3);
        body.password = "newPassword1";
        body.confirmPassword = "newPassword1";

        assertValidationError(() -> action.put$setting(body)).handle(data -> {
            data.requiredMessageOf("_global", "errors.user_changed_concurrently");
        });
        assertTrue(encoded.isEmpty());
        assertTrue(stored.isEmpty());
    }

    @Test
    public void test_put_currentVersionIsStored() throws Exception {
        final ApiAdminUserAction action = createAction(4);

        action.put$setting(bodyOfVersion(4));

        assertEquals(1, stored.size());
        assertEquals("alice", stored.get(0).getName());
        assertTrue(encoded.isEmpty());
    }
}
