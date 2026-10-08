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
package org.codelibs.fess.app.web.admin;

import org.codelibs.fess.app.web.admin.accesstoken.AdminAccesstokenAction;
import org.codelibs.fess.app.web.admin.dict.AdminDictAction;
import org.codelibs.fess.app.web.admin.scheduler.AdminSchedulerAction;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

public class AdminActionTest extends UnitFessTestCase {

    private static final String[] PLUGINLESS_TYPES = { "vanilla", "aws", "cloud" };

    private static final String[] PLUGIN_TYPES = { "default", "opensearch" };

    private static void useSearchEngineType(final String type) {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getSearchEngineType() {
                return type;
            }
        });
    }

    private static FessUserBean userWithRoles(final String... roles) {
        return new FessUserBean(new FessUser() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getName() {
                return "taro";
            }

            @Override
            public String[] getRoleNames() {
                return roles;
            }

            @Override
            public String[] getGroupNames() {
                return new String[0];
            }

            @Override
            public String[] getPermissions() {
                return new String[0];
            }
        });
    }

    @Test
    public void test_getAdminActionClass_dictionaryOnlyRoleLandsOnTheDictionaryWhenThePluginsAreAvailable() {
        for (final String type : PLUGIN_TYPES) {
            useSearchEngineType(type);

            assertEquals(type, AdminDictAction.class, AdminAction.getAdminActionClass(userWithRoles("admin-dict")));
            assertEquals(type, AdminDictAction.class, AdminAction.getAdminActionClass(userWithRoles("admin-dict-view")));
        }
    }

    @Test
    public void test_getAdminActionClass_dictionaryOnlyRoleIsNotRoutedToTheDictionaryWithoutThePlugins() {
        // the dictionary actions send everyone back to the admin top, which would route here again
        for (final String type : PLUGINLESS_TYPES) {
            useSearchEngineType(type);

            assertNull(AdminAction.getAdminActionClass(userWithRoles("admin-dict")), type);
            assertNull(AdminAction.getAdminActionClass(userWithRoles("admin-dict-view")), type);
        }
    }

    @Test
    public void test_getAdminActionClass_skipsOnlyTheDictionaryWithoutThePlugins() {
        // admin-dict is checked before admin-accesstoken, and admin-scheduler before admin-dict
        for (final String type : PLUGINLESS_TYPES) {
            useSearchEngineType(type);

            assertEquals(type, AdminAccesstokenAction.class,
                    AdminAction.getAdminActionClass(userWithRoles("admin-dict", "admin-accesstoken")));
            assertEquals(type, AdminSchedulerAction.class, AdminAction.getAdminActionClass(userWithRoles("admin-dict", "admin-scheduler")));
        }
        useSearchEngineType("default");
        assertEquals(AdminDictAction.class, AdminAction.getAdminActionClass(userWithRoles("admin-dict", "admin-accesstoken")));
    }

    @Test
    public void test_getAdminActionClass_userWithoutAdminRolesNeedsNoSearchEngineType() {
        // hasRoles comes first, so the configuration is not read for a user without a matching role
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getSearchEngineType() {
                throw new AssertionError("must not be read");
            }
        });

        assertNull(AdminAction.getAdminActionClass(userWithRoles("guest")));
    }
}
