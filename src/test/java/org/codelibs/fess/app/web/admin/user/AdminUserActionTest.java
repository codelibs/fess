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
package org.codelibs.fess.app.web.admin.user;

import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Unit tests for {@link AdminUserAction}.
 * Tests user management action logic including password validation, form handling, and CRUD modes.
 */
public class AdminUserActionTest extends UnitFessTestCase {

    private AdminUserAction adminUserAction;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        adminUserAction = new AdminUserAction();
    }

    @Test
    public void test_defaultConstructor() {
        final AdminUserAction action = new AdminUserAction();
        assertNotNull(action);
    }

    @Test
    public void test_roleConstant() {
        assertEquals("admin-user", AdminUserAction.ROLE);
    }

    @Test
    public void test_createForm_initialize() {
        final CreateForm form = new CreateForm();
        assertNull(form.crudMode);
        assertNull(form.name);
        assertNull(form.password);
        assertNull(form.confirmPassword);
        assertNotNull(form.attributes);
        assertTrue(form.attributes.isEmpty());

        form.initialize();
        assertEquals(CrudMode.CREATE, form.crudMode.intValue());
    }

    @Test
    public void test_createForm_setName() {
        final CreateForm form = new CreateForm();
        form.name = "testuser";
        assertEquals("testuser", form.name);
    }

    @Test
    public void test_createForm_setPassword() {
        final CreateForm form = new CreateForm();
        form.password = "testpassword";
        form.confirmPassword = "testpassword";
        assertEquals("testpassword", form.password);
        assertEquals("testpassword", form.confirmPassword);
    }

    @Test
    public void test_createForm_setRolesAndGroups() {
        final CreateForm form = new CreateForm();
        form.roles = new String[] { "admin", "user" };
        form.groups = new String[] { "group1", "group2" };

        assertNotNull(form.roles);
        assertEquals(2, form.roles.length);
        assertEquals("admin", form.roles[0]);
        assertEquals("user", form.roles[1]);

        assertNotNull(form.groups);
        assertEquals(2, form.groups.length);
        assertEquals("group1", form.groups[0]);
        assertEquals("group2", form.groups[1]);
    }

    @Test
    public void test_createForm_attributes() {
        final CreateForm form = new CreateForm();
        form.attributes.put("department", "Engineering");
        form.attributes.put("location", "Tokyo");

        assertEquals(2, form.attributes.size());
        assertEquals("Engineering", form.attributes.get("department"));
        assertEquals("Tokyo", form.attributes.get("location"));
    }

    @Test
    public void test_editForm_extends_createForm() {
        final EditForm form = new EditForm();
        assertNull(form.id);
        assertNull(form.crudMode);
        assertNull(form.name);

        form.id = "user-id-123";
        form.name = "edituser";
        form.crudMode = CrudMode.EDIT;

        assertEquals("user-id-123", form.id);
        assertEquals("edituser", form.name);
        assertEquals(CrudMode.EDIT, form.crudMode.intValue());
    }

    @Test
    public void test_searchForm() {
        final SearchForm form = new SearchForm();
        assertNull(form.id);

        form.id = "search-id";
        assertEquals("search-id", form.id);
    }

    @Test
    public void test_resetPassword() {
        final CreateForm form = new CreateForm();
        form.password = "somepassword";
        form.confirmPassword = "somepassword";

        AdminUserAction.resetPassword(form);

        assertNull(form.password);
        assertNull(form.confirmPassword);
    }

    @Test
    public void test_createItem() {
        final Map<String, String> item = adminUserAction.createItem("Label", "value");

        assertNotNull(item);
        assertEquals(2, item.size());
        assertEquals("Label", item.get(Constants.ITEM_LABEL));
        assertEquals("value", item.get(Constants.ITEM_VALUE));
    }

    @Test
    public void test_createItem_emptyValues() {
        final Map<String, String> item = adminUserAction.createItem("", "");

        assertNotNull(item);
        assertEquals(2, item.size());
        assertEquals("", item.get(Constants.ITEM_LABEL));
        assertEquals("", item.get(Constants.ITEM_VALUE));
    }

    @Test
    public void test_crudMode_constants() {
        assertEquals(0, CrudMode.LIST);
        assertEquals(1, CrudMode.CREATE);
        assertEquals(2, CrudMode.EDIT);
        assertEquals(3, CrudMode.DELETE);
        assertEquals(4, CrudMode.DETAILS);
    }
}
