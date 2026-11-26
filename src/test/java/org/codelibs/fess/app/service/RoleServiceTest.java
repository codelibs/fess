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
package org.codelibs.fess.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.opensearch.user.cbean.RoleCB;
import org.codelibs.fess.opensearch.user.exbhv.RoleBhv;
import org.codelibs.fess.opensearch.user.exentity.Role;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.junit.jupiter.api.Test;

public class RoleServiceTest extends UnitFessTestCase {

    /** The behavior that holds the stored roles and only answers a cursor, never a page. */
    private static class StubBhv extends RoleBhv {
        final List<Role> rows = new ArrayList<>();

        final AtomicReference<RoleCB> cursorCb = new AtomicReference<>();

        @Override
        public void selectCursor(final CBCall<RoleCB> cbLambda, final EntityRowHandler<Role> entityLambda) {
            final RoleCB cb = new RoleCB();
            cbLambda.callback(cb);
            cursorCb.set(cb);
            for (final Role row : rows) {
                entityLambda.handle(row);
            }
        }
    }

    @Test
    public void test_getAvailableRoleList_returnsEveryRole() {
        final StubBhv bhv = new StubBhv();
        // more than the 1,000 that one page holds (page.role.max.fetch.size)
        for (int i = 0; i < 1500; i++) {
            final Role role = new Role();
            role.setName(String.format("role-%04d", i));
            bhv.rows.add(role);
        }
        final RoleService service = new RoleService();
        service.roleBhv = bhv;

        final List<Role> list = service.getAvailableRoleList();

        assertEquals(1500, list.size());
        assertEquals("role-0000", list.get(0).getName());
        assertEquals("role-1499", list.get(1499).getName());
        assertNotNull(bhv.cursorCb.get());
    }

    @Test
    public void test_getAvailableRoleList_noRole() {
        final RoleService service = new RoleService();
        service.roleBhv = new StubBhv();

        assertTrue(service.getAvailableRoleList().isEmpty());
    }
}
