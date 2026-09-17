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
package org.codelibs.fess.api.v2.handlers;

import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.entity.FessUser.PermissionState;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class UserPayloadsTest extends UnitFessTestCase {

    @Test
    public void test_permissionState_reportsTheUsersState() {
        final UserPayloads payloads = new UserPayloads();
        assertEquals("PENDING", payloads.permissionState(new FessUserBean(user(PermissionState.PENDING))));
        assertEquals("FAILED", payloads.permissionState(new FessUserBean(user(PermissionState.FAILED))));
        assertEquals("RESOLVED", payloads.permissionState(new FessUserBean(user(PermissionState.RESOLVED))));
    }

    @Test
    public void test_permissionState_withoutAStateCountsAsResolved() {
        final UserPayloads payloads = new UserPayloads();
        assertEquals("RESOLVED", payloads.permissionState(null));
        assertEquals("RESOLVED", payloads.permissionState(FessUserBean.empty()));
        // A session deserialized from before the field existed carries a null state.
        assertEquals("RESOLVED", payloads.permissionState(new FessUserBean(user(null))));
    }

    @Test
    public void test_toJson_includesPermissionState() {
        assertEquals("FAILED", new UserPayloads().toJson(new FessUserBean(user(PermissionState.FAILED))).get("permission_state"));
    }

    private static FessUser user(final PermissionState state) {
        return new FessUser() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getName() {
                return "dave";
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

            @Override
            public PermissionState getPermissionState() {
                return state;
            }
        };
    }
}
