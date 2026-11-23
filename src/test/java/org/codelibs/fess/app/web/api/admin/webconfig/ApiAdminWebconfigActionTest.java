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
package org.codelibs.fess.app.web.api.admin.webconfig;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.helper.ProtocolHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.core.json.JsonMappingOption.JsonFieldNaming;
import org.lastaflute.core.json.engine.GsonJsonEngine;
import org.lastaflute.core.json.engine.RealJsonEngine;

/**
 * A setting that the admin API receives without a permission gets the default one, as the create screen
 * prefills it: documents of a setting without a permission are visible to nobody. A PUT that omits the
 * permission keeps the stored one; a PUT stores what it sends (blank included), so that the body of a GET can be sent back unchanged.
 */
public class ApiAdminWebconfigActionTest extends UnitFessTestCase {

    private static final String ID = "id1";

    private final List<WebConfig> stored = new ArrayList<>();

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        final SystemHelper sysHelper = new SystemHelper();
        ComponentUtil.register(sysHelper, "systemHelper");
        ComponentUtil.register(new PermissionHelper() {
            {
                systemHelper = sysHelper;
            }
        }, "permissionHelper");
        ComponentUtil.register(new ProtocolHelper() {
            @Override
            public String[] getWebProtocols() {
                return new String[] { "http:" };
            }
        }, "protocolHelper");
    }

    private ApiAdminWebconfigAction createAction() throws Exception {
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        suppressBindingOf(org.codelibs.fess.app.service.AccessTokenService.class);
        suppressBindingOf(WebConfigService.class);
        final ApiAdminWebconfigAction action = new ApiAdminWebconfigAction();
        inject(action);
        setField(action, FessBaseAction.class, "systemHelper", ComponentUtil.getSystemHelper());
        setField(action, FessBaseAction.class, "fessConfig", ComponentUtil.getFessConfig());
        final WebConfigService service = new WebConfigService() {
            @Override
            public OptionalEntity<WebConfig> getWebConfig(final String id) {
                final WebConfig entity = new WebConfig();
                entity.setId(ID);
                entity.setPermissions(new String[] { "Rkeep" });
                return OptionalEntity.of(entity);
            }

            @Override
            public void store(final WebConfig entity) {
                stored.add(entity);
            }
        };
        setField(action, ApiAdminWebconfigAction.class, "webConfigService", service);
        // the shared logic of the admin screen reads the setting through the container
        ComponentUtil.register(service, WebConfigService.class.getCanonicalName());
        return action;
    }

    private static void setField(final Object target, final Class<?> owner, final String name, final Object value) throws Exception {
        final Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private CreateBody createBody(final String permissions) {
        final CreateBody body = new CreateBody();
        fill(body, permissions);
        return body;
    }

    private EditBody editBody(final String permissions) {
        final EditBody body = new EditBody();
        body.id = ID;
        body.versionNo = 1;
        fill(body, permissions);
        return body;
    }

    private void fill(final org.codelibs.fess.app.web.admin.webconfig.CreateForm body, final String permissions) {
        body.name = "n";
        body.urls = "http://localhost/";
        body.userAgent = "ua";
        body.numOfThread = 1;
        body.intervalTime = 0;
        body.boost = 1.0f;
        body.available = "T";
        body.sortOrder = 0;
        body.permissions = permissions;
    }

    private String[] storedPermissions() {
        assertEquals(1, stored.size());
        return stored.get(0).getPermissions();
    }

    @Test
    public void test_post_missingPermissionsGetTheDefault() throws Exception {
        createAction().post$setting(createBody(null));

        assertEquals(List.of("R" + "guest"), List.of(storedPermissions()));
    }

    @Test
    public void test_post_blankPermissionsGetTheDefault() throws Exception {
        createAction().post$setting(createBody("  "));

        assertEquals(List.of("R" + "guest"), List.of(storedPermissions()));
    }

    @Test
    public void test_post_permissionsOfTheCallerAreKept() throws Exception {
        createAction().post$setting(createBody("{role}sales"));

        assertEquals(List.of("Rsales"), List.of(storedPermissions()));
    }

    @Test
    public void test_put_missingPermissionsKeepTheStoredOnes() throws Exception {
        createAction().put$setting(editBody(null));

        assertEquals(List.of("Rkeep"), List.of(storedPermissions()));
    }

    @Test
    public void test_put_blankPermissionsAreStoredAsSent() throws Exception {
        createAction().put$setting(editBody("  "));

        assertEquals(0, storedPermissions().length);
    }

    /**
     * The body of a GET for a setting without a permission has an empty "permissions"; sent back by a PUT
     * it must not turn into the default.
     */
    @Test
    public void test_put_bodyOfAGetOfASettingWithoutPermissionIsStoredUnchanged() throws Exception {
        final ApiAdminWebconfigAction action = createAction();
        final WebConfig withoutPermission = new WebConfig();
        withoutPermission.setPermissions(new String[0]);
        final String fromGet = action.createEditBody(withoutPermission).permissions;
        assertEquals("", fromGet);

        action.put$setting(editBody(fromGet));

        assertEquals(0, storedPermissions().length);
    }

    @Test
    public void test_put_permissionsOfTheCallerAreKept() throws Exception {
        createAction().put$setting(editBody("{role}sales"));

        assertEquals(List.of("Rsales"), List.of(storedPermissions()));
    }

    /**
     * The request body is bound by the JSON engine of the application (see FessJsonResourceProvider): a
     * missing "permissions" must stay null, so that a PUT keeps the stored permission, and an empty or blank
     * one must stay as sent, so that it can clear the permission.
     */
    @Test
    public void test_json_omittedPermissionsStayNullAndBlankStaysBlank() throws Exception {
        final RealJsonEngine json = new GsonJsonEngine(builder -> {}, option -> option.asFieldNaming(JsonFieldNaming.CAMEL_TO_LOWER_SNAKE));
        assertNull(json.fromJson("{\"id\":\"id1\"}", EditBody.class).permissions);
        assertNull(json.fromJson("{\"id\":\"id1\",\"permissions\":null}", EditBody.class).permissions);
        assertEquals("", json.fromJson("{\"id\":\"id1\",\"permissions\":\"\"}", EditBody.class).permissions);
        assertEquals(" ", json.fromJson("{\"id\":\"id1\",\"permissions\":\" \"}", EditBody.class).permissions);
    }
}
