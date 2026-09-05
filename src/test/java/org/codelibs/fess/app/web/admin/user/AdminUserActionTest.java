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
import org.codelibs.fess.app.service.GroupService;
import org.codelibs.fess.app.service.RoleService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.opensearch.user.exentity.Group;
import org.codelibs.fess.opensearch.user.exentity.Role;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
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

    /**
     * The password policy is shared with the REST API, so a password the screens refuse cannot be
     * set through an API call instead. A blacklisted password is one such case.
     */
    @Test
    public void test_verifyPasswordPolicy_rejectsABlacklistedPassword() {
        assertTrue(policyErrorOf("admin").contains("password"));
    }

    /**
     * A password shorter than password.min.length is refused too.
     */
    @Test
    public void test_verifyPasswordPolicy_rejectsATooShortPassword() {
        assertTrue(policyErrorOf("x").contains("password"));
    }

    /**
     * A password that satisfies the policy reports nothing, and the form keeps it.
     */
    @Test
    public void test_verifyPasswordPolicy_acceptsACompliantPassword() {
        assertEquals("", policyErrorOf("Str0ng-Passw0rd!"));
    }

    /**
     * An edit that leaves the password empty keeps the stored one, so a blank value is not a policy
     * violation at this point.
     */
    @Test
    public void test_verifyPasswordPolicy_ignoresABlankPassword() {
        assertEquals("", policyErrorOf(null));
        assertEquals("", policyErrorOf(""));
    }

    /**
     * Roles are stored by id, and a user entity Base64-decodes those ids to read them back. A role
     * name is a natural thing for an API caller to send and is not an id, so it has to be refused
     * here; storing it leaves an account that can no longer log in.
     */
    @Test
    public void test_verifyRolesAndGroups_rejectsARoleName() {
        final String reported = roleAndGroupErrorOf(new String[] { "guest" }, null);
        assertTrue(reported.contains("roles"));
        assertTrue(reported.contains("errors.invalid_role_or_group_id"));
    }

    /**
     * Groups have the same shape and the same failure, so they are checked the same way.
     */
    @Test
    public void test_verifyRolesAndGroups_rejectsAGroupName() {
        final String reported = roleAndGroupErrorOf(null, new String[] { "sales" });
        assertTrue(reported.contains("groups"));
        assertTrue(reported.contains("errors.invalid_role_or_group_id"));
    }

    /**
     * An id that is valid Base64 but names no stored role is refused too: it would give the account
     * a permission for a role that does not exist.
     */
    @Test
    public void test_verifyRolesAndGroups_rejectsAnUnknownId() {
        assertTrue(roleAndGroupErrorOf(new String[] { "bm9ib2R5" }, null).contains("errors.invalid_role_or_group_id"));
    }

    /**
     * The ids the screens offer resolve, so nothing is reported for them.
     */
    @Test
    public void test_verifyRolesAndGroups_acceptsStoredIds() {
        assertEquals("", roleAndGroupErrorOf(new String[] { "Z3Vlc3Q=" }, new String[] { "c2FsZXM=" }));
    }

    /**
     * A user with no roles and no groups is allowed, as it always was.
     */
    @Test
    public void test_verifyRolesAndGroups_acceptsNothingAssigned() {
        assertEquals("", roleAndGroupErrorOf(null, null));
    }

    /**
     * Runs the shared role and group check over one set of ids and returns what it reported, or an
     * empty string when it reported nothing. Only "Z3Vlc3Q=" resolves as a role and only "c2FsZXM="
     * as a group.
     */
    private String roleAndGroupErrorOf(final String[] roles, final String[] groups) {
        final CreateForm form = new CreateForm();
        form.crudMode = CrudMode.CREATE;
        form.name = "tester";
        form.roles = roles;
        form.groups = groups;
        final RoleService roleService = new RoleService() {
            @Override
            public OptionalEntity<Role> getRole(final String id) {
                return "Z3Vlc3Q=".equals(id) ? OptionalEntity.of(new Role()) : OptionalEntity.empty();
            }
        };
        final GroupService groupService = new GroupService() {
            @Override
            public OptionalEntity<Group> getGroup(final String id) {
                return "c2FsZXM=".equals(id) ? OptionalEntity.of(new Group()) : OptionalEntity.empty();
            }
        };
        final StringBuilder reported = new StringBuilder();
        AdminUserAction.verifyRolesAndGroups(form, roleService, groupService, messenger -> {
            final FessMessages messages = new FessMessages();
            messenger.message(messages);
            reported.append(messages.toString());
        });
        return reported.toString();
    }

    /**
     * Runs the shared policy check over one password and returns what it reported, or an empty
     * string when it reported nothing.
     */
    private String policyErrorOf(final String password) {
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final CreateForm form = new CreateForm();
        form.crudMode = CrudMode.CREATE;
        form.password = password;
        form.confirmPassword = password;
        final StringBuilder reported = new StringBuilder();
        AdminUserAction.verifyPasswordPolicy(form, messenger -> {
            final FessMessages messages = new FessMessages();
            messenger.message(messages);
            reported.append(messages.toString());
        });
        return reported.toString();
    }
}
