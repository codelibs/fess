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
package org.codelibs.fess.app.web.admin.tagtype;

import java.util.List;

import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.tagtype.TagTypeAdminTestSupport.Env;
import org.codelibs.fess.entity.TagChange;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Covers the tag admin screen: the tag types it writes and the changes of the tag field of the
 * documents it queues.
 */
public class AdminTagtypeActionTest extends UnitFessTestCase {

    private Env env;

    private AdminTagtypeAction createAction() throws Exception {
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        suppressBindingOf(org.codelibs.fess.app.service.TagTypeService.class);
        suppressBindingOf(org.codelibs.fess.app.service.RoleTypeService.class);
        env = new Env();
        final AdminTagtypeAction action = new AdminTagtypeAction();
        inject(action);
        final java.lang.reflect.Field sysField = org.codelibs.fess.app.web.base.FessBaseAction.class.getDeclaredField("systemHelper");
        sysField.setAccessible(true);
        sysField.set(action, org.codelibs.fess.util.ComponentUtil.getSystemHelper());
        final java.lang.reflect.Field configField = org.codelibs.fess.app.web.base.FessBaseAction.class.getDeclaredField("fessConfig");
        configField.setAccessible(true);
        configField.set(action, env.fessConfig);
        final java.lang.reflect.Field serviceField = AdminTagtypeAction.class.getDeclaredField("tagTypeService");
        serviceField.setAccessible(true);
        serviceField.set(action, env.service);
        mockTokenRequested(action.getClass());
        return action;
    }

    private static CreateForm createForm(final String name, final String owner, final String paths) {
        final CreateForm form = new CreateForm();
        form.crudMode = CrudMode.CREATE;
        form.name = name;
        form.owner = owner;
        form.paths = paths;
        form.sortOrder = 0;
        return form;
    }

    private static EditForm editForm(final TagType stored, final String name, final String owner, final String paths) {
        final EditForm form = new EditForm();
        form.crudMode = CrudMode.EDIT;
        form.id = stored.getId();
        form.seqNo = stored.getSeqNo();
        form.primaryTerm = stored.getPrimaryTerm();
        form.name = name;
        form.owner = owner;
        form.paths = paths;
        form.sortOrder = 0;
        return form;
    }

    // ===================================================================================
    //                                                                              create
    //                                                                              ======

    @Test
    public void test_create_storesNormalizedTagAndQueuesAddForEachPath() throws Exception {
        final AdminTagtypeAction action = createAction();

        action.create(createForm("  Foo　Bar ", " alice ", "http://a/\n\n  http://b/  \nhttp://a/\n"));

        final TagType stored = env.stored("Foo Bar", "alice");
        assertNotNull(stored);
        assertEquals(List.of("http://a/", "http://b/"), List.of(stored.getPaths()));
        assertEquals(List.of("1alice"), List.of(stored.getPermissions()));
        final String value = env.value("Foo Bar", "alice");
        assertEquals(List.of(TagChange.add(value, "http://a/"), TagChange.add(value, "http://b/")), env.helper.changes);
    }

    @Test
    public void test_create_keepsGivenPermissions() throws Exception {
        final AdminTagtypeAction action = createAction();
        final CreateForm form = createForm("foo", "alice", "");
        form.permissions = "{user}alice\n{role}guest";

        action.create(form);

        assertEquals(List.of("1alice", "Rguest"), List.of(env.stored("foo", "alice").getPermissions()));
    }

    @Test
    public void test_create_featureDisabledQueuesNothing() throws Exception {
        final AdminTagtypeAction action = createAction();
        env.fessConfig.enabled = false;

        action.create(createForm("foo", "alice", "http://a/"));

        assertNotNull(env.stored("foo", "alice"));
        assertTrue(env.helper.changes.isEmpty());
    }

    @Test
    public void test_create_invalidNameIsRefused() throws Exception {
        final AdminTagtypeAction action = createAction();

        assertValidationError(() -> action.create(createForm("a\u0007b", "alice", ""))).handle(data -> {
            data.requiredMessageOf("name", "errors.tagtype_invalid_name");
        });
        assertTrue(env.service.store.isEmpty());
    }

    @Test
    public void test_create_tooManyPathsIsRefused() throws Exception {
        final AdminTagtypeAction action = createAction();
        env.fessConfig.maxPaths = 2;

        assertValidationError(() -> action.create(createForm("foo", "alice", "http://a/\nhttp://b/\nhttp://c/"))).handle(data -> {
            data.requiredMessageOf("paths", "errors.tagtype_too_many_paths");
        });
        assertTrue(env.service.store.isEmpty());
    }

    @Test
    public void test_create_existingTagIsRefused() throws Exception {
        final AdminTagtypeAction action = createAction();
        env.put("foo", "alice", "http://a/");

        assertValidationError(() -> action.create(createForm("foo", "alice", "http://b/"))).handle(data -> {
            data.requiredMessageOf("_global", "errors.tagtype_already_exists");
        });
        assertEquals(List.of("http://a/"), List.of(env.stored("foo", "alice").getPaths()));
        assertTrue(env.helper.changes.isEmpty());
    }

    // ===================================================================================
    //                                                                              update
    //                                                                              ======

    @Test
    public void test_update_queuesAddAndRemoveForChangedPaths() throws Exception {
        final AdminTagtypeAction action = createAction();
        final TagType stored = env.put("foo", "alice", "http://a/", "http://b/");

        action.update(editForm(stored, "foo", "alice", "http://b/\nhttp://c/"));

        assertEquals(List.of("http://b/", "http://c/"), List.of(env.stored("foo", "alice").getPaths()));
        final String value = env.value("foo", "alice");
        assertEquals(List.of(TagChange.add(value, "http://c/"), TagChange.remove(value, "http://a/")), env.helper.changes);
    }

    @Test
    public void test_update_renameReplacesTagAndQueuesRenameBeforePathChanges() throws Exception {
        final AdminTagtypeAction action = createAction();
        final TagType stored = env.put("foo", "alice", "http://a/", "http://b/");

        action.update(editForm(stored, "bar", "alice", "http://a/\nhttp://c/"));

        assertNull(env.stored("foo", "alice"));
        final TagType renamed = env.stored("bar", "alice");
        assertNotNull(renamed);
        assertEquals(List.of("http://a/", "http://c/"), List.of(renamed.getPaths()));
        assertEquals("admin", renamed.getCreatedBy());
        final String newValue = env.value("bar", "alice");
        assertEquals(List.of(TagChange.rename(env.value("foo", "alice"), newValue), TagChange.add(newValue, "http://c/"),
                TagChange.remove(newValue, "http://b/")), env.helper.changes);
    }

    @Test
    public void test_update_ownerChangeIsARename() throws Exception {
        final AdminTagtypeAction action = createAction();
        final TagType stored = env.put("foo", "alice", "http://a/");

        action.update(editForm(stored, "foo", "bob", "http://a/"));

        assertNull(env.stored("foo", "alice"));
        assertNotNull(env.stored("foo", "bob"));
        assertEquals(List.of(TagChange.rename(env.value("foo", "alice"), env.value("foo", "bob"))), env.helper.changes);
    }

    @Test
    public void test_update_ownerChangeMovesTheOwnerPermission() throws Exception {
        final AdminTagtypeAction action = createAction();
        final TagType stored = env.put("foo", "alice", "http://a/");
        final EditForm form = editForm(stored, "foo", "bob", "http://a/");
        // as the edit form shows them: the old owner, the sharing role and a role an administrator added
        form.permissions = "{user}alice\n{role}guest\n{group}sales";

        action.update(form);

        assertEquals(List.of("1bob", "Rguest", "2sales"), List.of(env.stored("foo", "bob").getPermissions()));
    }

    @Test
    public void test_update_renameToExistingTagIsRefused() throws Exception {
        final AdminTagtypeAction action = createAction();
        final TagType stored = env.put("foo", "alice", "http://a/");
        env.put("bar", "alice", "http://b/");

        assertValidationError(() -> action.update(editForm(stored, "bar", "alice", "http://a/"))).handle(data -> {
            data.requiredMessageOf("_global", "errors.tagtype_already_exists");
        });
        assertNotNull(env.stored("foo", "alice"));
        assertEquals(List.of("http://b/"), List.of(env.stored("bar", "alice").getPaths()));
        assertTrue(env.helper.changes.isEmpty());
    }

    @Test
    public void test_update_tagChangedSinceReadIsRefused() throws Exception {
        final AdminTagtypeAction action = createAction();
        final TagType stored = env.put("foo", "alice", "http://a/");
        // the owner adds a URL after the administrator opened the form
        final TagType changed = env.service.getTagType(stored.getId()).get();
        changed.setPaths(new String[] { "http://a/", "http://x/" });
        env.service.update(changed);

        assertValidationError(() -> action.update(editForm(stored, "foo", "alice", "http://a/\nhttp://b/"))).handle(data -> {
            data.requiredMessageOf("_global", "errors.tagtype_changed_concurrently");
        });
        assertEquals(List.of("http://a/", "http://x/"), List.of(env.stored("foo", "alice").getPaths()));
        assertTrue(env.helper.changes.isEmpty());
    }

    @Test
    public void test_update_renameOfTagChangedSinceReadIsRefusedAndRolledBack() throws Exception {
        final AdminTagtypeAction action = createAction();
        final TagType stored = env.put("foo", "alice", "http://a/");
        final TagType changed = env.service.getTagType(stored.getId()).get();
        changed.setPaths(new String[] { "http://a/", "http://x/" });
        env.service.update(changed);

        assertValidationError(() -> action.update(editForm(stored, "bar", "alice", "http://a/"))).handle(data -> {
            data.requiredMessageOf("_global", "errors.tagtype_changed_concurrently");
        });
        assertNotNull(env.stored("foo", "alice"));
        assertNull(env.stored("bar", "alice"));
        assertTrue(env.helper.changes.isEmpty());
    }

    // ===================================================================================
    //                                                                              delete
    //                                                                              ======

    @Test
    public void test_delete_removesTagAndQueuesDelete() throws Exception {
        final AdminTagtypeAction action = createAction();
        final TagType stored = env.put("foo", "alice", "http://a/");
        final EditForm form = editForm(stored, "foo", "alice", null);
        form.crudMode = CrudMode.DETAILS;

        action.delete(form);

        assertNull(env.stored("foo", "alice"));
        assertEquals(List.of(TagChange.delete(env.value("foo", "alice"))), env.helper.changes);
    }

    @Test
    public void test_delete_tagChangedSinceReadIsRefused() throws Exception {
        final AdminTagtypeAction action = createAction();
        final TagType stored = env.put("foo", "alice", "http://a/");
        env.service.update(env.service.getTagType(stored.getId()).get());
        final EditForm form = editForm(stored, "foo", "alice", null);
        form.crudMode = CrudMode.DETAILS;

        assertValidationError(() -> action.delete(form)).handle(data -> {
            data.requiredMessageOf("_global", "errors.tagtype_changed_concurrently");
        });
        assertNotNull(env.stored("foo", "alice"));
        assertTrue(env.helper.changes.isEmpty());
    }
}
