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
package org.codelibs.fess.app.web.api.admin.fileauth;

import java.lang.reflect.Field;

import org.codelibs.fess.app.service.FileAuthenticationService;
import org.codelibs.fess.opensearch.config.exentity.FileAuthentication;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;

public class ApiAdminFileauthActionTest extends UnitFessTestCase {

    private ApiAdminFileauthAction createAction(final String storedPassword) throws Exception {
        final ApiAdminFileauthAction action = new ApiAdminFileauthAction();
        final Field field = ApiAdminFileauthAction.class.getDeclaredField("fileAuthService");
        field.setAccessible(true);
        field.set(action, new FileAuthenticationService() {
            @Override
            public OptionalEntity<FileAuthentication> getFileAuthentication(final String id) {
                if (!"1".equals(id)) {
                    return OptionalEntity.empty();
                }
                final FileAuthentication entity = new FileAuthentication();
                entity.setPassword(storedPassword);
                return OptionalEntity.of(entity);
            }
        });
        return action;
    }

    @Test
    public void test_createEditBody_doesNotReturnThePassword() throws Exception {
        final FileAuthentication entity = new FileAuthentication();
        entity.setUsername("user");
        entity.setPassword("secret");

        final EditBody body = createAction("secret").createEditBody(entity);

        assertEquals("user", body.username);
        assertNull(body.password);
    }

    @Test
    public void test_keepStoredPassword_fillsAnOmittedPassword() throws Exception {
        final EditBody body = new EditBody();
        body.id = "1";

        createAction("secret").keepStoredPassword(body);

        assertEquals("secret", body.password);
    }

    @Test
    public void test_keepStoredPassword_fillsABlankPassword() throws Exception {
        final EditBody body = new EditBody();
        body.id = "1";
        body.password = "";

        createAction("secret").keepStoredPassword(body);

        assertEquals("secret", body.password);
    }

    @Test
    public void test_keepStoredPassword_keepsANewPassword() throws Exception {
        final EditBody body = new EditBody();
        body.id = "1";
        body.password = "changed";

        createAction("secret").keepStoredPassword(body);

        assertEquals("changed", body.password);
    }

    @Test
    public void test_keepStoredPassword_ignoresAnUnknownId() throws Exception {
        final EditBody body = new EditBody();
        body.id = "2";

        createAction("secret").keepStoredPassword(body);

        assertNull(body.password);
    }
}
