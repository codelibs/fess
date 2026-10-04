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
package org.codelibs.fess.app.web.api.admin.tagtype;

import java.util.List;

import org.codelibs.fess.app.web.admin.tagtype.TagTypeAdminTestSupport.Env;
import org.codelibs.fess.entity.TagChange;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Covers the tag admin API: the tag types it writes and the changes of the tag field of the
 * documents it queues.
 */
public class ApiAdminTagtypeActionTest extends UnitFessTestCase {

    private Env env;

    private ApiAdminTagtypeAction createAction() throws Exception {
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        suppressBindingOf(org.codelibs.fess.app.service.TagTypeService.class);
        suppressBindingOf(org.codelibs.fess.app.service.RoleTypeService.class);
        suppressBindingOf(org.codelibs.fess.app.service.AccessTokenService.class);
        env = new Env();
        final ApiAdminTagtypeAction action = new ApiAdminTagtypeAction();
        inject(action);
        final java.lang.reflect.Field sysField = org.codelibs.fess.app.web.base.FessBaseAction.class.getDeclaredField("systemHelper");
        sysField.setAccessible(true);
        sysField.set(action, org.codelibs.fess.util.ComponentUtil.getSystemHelper());
        final java.lang.reflect.Field configField = org.codelibs.fess.app.web.base.FessBaseAction.class.getDeclaredField("fessConfig");
        configField.setAccessible(true);
        configField.set(action, env.fessConfig);
        final java.lang.reflect.Field serviceField = ApiAdminTagtypeAction.class.getDeclaredField("tagTypeService");
        serviceField.setAccessible(true);
        serviceField.set(action, env.service);
        return action;
    }

    private static EditBody editBody(final TagType stored, final String name, final String owner, final String paths) {
        final EditBody body = new EditBody();
        body.id = stored.getId();
        body.seqNo = stored.getSeqNo();
        body.primaryTerm = stored.getPrimaryTerm();
        body.name = name;
        body.owner = owner;
        body.paths = paths;
        body.sortOrder = 0;
        return body;
    }

    @Test
    public void test_post_createsTagAndQueuesAdd() throws Exception {
        final ApiAdminTagtypeAction action = createAction();
        final CreateBody body = new CreateBody();
        body.name = "foo";
        body.owner = "alice";
        body.paths = "http://a/";
        body.sortOrder = 0;

        action.post$setting(body);

        assertNotNull(env.stored("foo", "alice"));
        assertEquals(List.of(TagChange.add(env.value("foo", "alice"), "http://a/")), env.helper.changes);
    }

    @Test
    public void test_get_returnsPathsAndVersion() throws Exception {
        final ApiAdminTagtypeAction action = createAction();
        final TagType stored = env.put("foo", "alice", "http://a/", "http://b/");

        final EditBody body = action.createEditBody(env.service.getTagType(stored.getId()).get());

        assertEquals(stored.getId(), body.id);
        assertEquals("http://a/\nhttp://b/", body.paths);
        assertEquals("{user}alice", body.permissions);
        assertEquals(Long.valueOf(0L), body.seqNo);
        assertEquals(Long.valueOf(1L), body.primaryTerm);
    }

    @Test
    public void test_put_renameQueuesRename() throws Exception {
        final ApiAdminTagtypeAction action = createAction();
        final TagType stored = env.put("foo", "alice", "http://a/");

        action.put$setting(editBody(stored, "bar", "alice", "http://a/"));

        assertNull(env.stored("foo", "alice"));
        assertNotNull(env.stored("bar", "alice"));
        assertEquals(List.of(TagChange.rename(env.value("foo", "alice"), env.value("bar", "alice"))), env.helper.changes);
    }

    @Test
    public void test_put_ownerChangeMovesTheOwnerPermission() throws Exception {
        final ApiAdminTagtypeAction action = createAction();
        final TagType stored = env.put("foo", "alice", "http://a/");
        final EditBody body = editBody(stored, "foo", "bob", "http://a/");
        body.permissions = "{user}alice\n{role}guest\n{group}sales";

        action.put$setting(body);

        assertEquals(List.of("1bob", "Rguest", "2sales"), List.of(env.stored("foo", "bob").getPermissions()));
    }

    @Test
    public void test_put_staleVersionIsRefused() throws Exception {
        final ApiAdminTagtypeAction action = createAction();
        final TagType stored = env.put("foo", "alice", "http://a/");
        env.service.update(env.service.getTagType(stored.getId()).get());

        assertValidationError(() -> action.put$setting(editBody(stored, "foo", "alice", "http://b/"))).handle(data -> {
            data.requiredMessageOf("_global", "errors.tagtype_changed_concurrently");
        });
        assertEquals(List.of("http://a/"), List.of(env.stored("foo", "alice").getPaths()));
        assertTrue(env.helper.changes.isEmpty());
    }

    @Test
    public void test_delete_removesTagAndQueuesDelete() throws Exception {
        final ApiAdminTagtypeAction action = createAction();
        final TagType stored = env.put("foo", "alice", "http://a/");

        action.delete$setting(stored.getId());

        assertNull(env.stored("foo", "alice"));
        assertEquals(List.of(TagChange.delete(env.value("foo", "alice"))), env.helper.changes);
    }
}
