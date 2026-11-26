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

import org.codelibs.fess.opensearch.user.cbean.GroupCB;
import org.codelibs.fess.opensearch.user.exbhv.GroupBhv;
import org.codelibs.fess.opensearch.user.exentity.Group;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.junit.jupiter.api.Test;

public class GroupServiceTest extends UnitFessTestCase {

    /** The behavior that holds the stored groups and only answers a cursor, never a page. */
    private static class StubBhv extends GroupBhv {
        final List<Group> rows = new ArrayList<>();

        final AtomicReference<GroupCB> cursorCb = new AtomicReference<>();

        @Override
        public void selectCursor(final CBCall<GroupCB> cbLambda, final EntityRowHandler<Group> entityLambda) {
            final GroupCB cb = new GroupCB();
            cbLambda.callback(cb);
            cursorCb.set(cb);
            for (final Group row : rows) {
                entityLambda.handle(row);
            }
        }
    }

    @Test
    public void test_getAvailableGroupList_returnsEveryGroup() {
        final StubBhv bhv = new StubBhv();
        // more than the 1,000 that one page holds (page.group.max.fetch.size)
        for (int i = 0; i < 1500; i++) {
            final Group group = new Group();
            group.setName(String.format("group-%04d", i));
            bhv.rows.add(group);
        }
        final GroupService service = new GroupService();
        service.groupBhv = bhv;

        final List<Group> list = service.getAvailableGroupList();

        assertEquals(1500, list.size());
        assertEquals("group-0000", list.get(0).getName());
        assertEquals("group-1499", list.get(1499).getName());
        assertNotNull(bhv.cursorCb.get());
    }

    @Test
    public void test_getAvailableGroupList_noGroup() {
        final GroupService service = new GroupService();
        service.groupBhv = new StubBhv();

        assertTrue(service.getAvailableGroupList().isEmpty());
    }
}
