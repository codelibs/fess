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
package org.codelibs.fess.app.web.api.admin.webauth;

import java.lang.reflect.Field;

import org.codelibs.fess.app.service.WebAuthenticationService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.WebAuthentication;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ParameterUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.validation.exception.ValidationErrorException;

public class ApiAdminWebauthActionTest extends UnitFessTestCase {

    // An encrypted password that app.cipher.key cannot decrypt, such as one saved before the key was replaced.
    private static final String UNDECRYPTABLE_PASSWORD = "{cipher}00112233445566778899aabbccddeeff";

    private ApiAdminWebauthAction createAction(final String storedPassword) throws Exception {
        return createAction(new ApiAdminWebauthAction(), storedPassword);
    }

    private ApiAdminWebauthAction createAction(final ApiAdminWebauthAction action, final String storedPassword) throws Exception {
        final Field field = ApiAdminWebauthAction.class.getDeclaredField("webAuthService");
        field.setAccessible(true);
        field.set(action, new WebAuthenticationService() {
            @Override
            public OptionalEntity<WebAuthentication> getWebAuthentication(final String id) {
                if (!"1".equals(id)) {
                    return OptionalEntity.empty();
                }
                final WebAuthentication entity = new WebAuthentication();
                entity.setPassword(storedPassword);
                return OptionalEntity.of(entity);
            }
        });
        return action;
    }

    @Test
    public void test_createEditBody_doesNotReturnThePassword() throws Exception {
        final WebAuthentication entity = new WebAuthentication();
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

    // The body carries plain text, which the service encrypts on save: a stored encrypted password is
    // decrypted, so that it is encrypted once and a stored value is never taken for a submitted one.
    @Test
    public void test_keepStoredPassword_decryptsTheStoredPassword() throws Exception {
        final EditBody body = new EditBody();
        body.id = "1";

        createAction(ParameterUtil.encryptCredential("secret")).keepStoredPassword(body);

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

    // A blank password cannot keep a stored password that cannot be decrypted: the update is refused with a
    // validation error (HTTP 400), not a server error, and the body is not filled.
    @Test
    public void test_keepStoredPassword_refusesAnUndecryptablePassword() throws Exception {
        final ApiAdminWebauthAction action = createAction(injectAction(new ApiAdminWebauthAction()), UNDECRYPTABLE_PASSWORD);
        for (final String password : new String[] { null, "" }) {
            final EditBody body = new EditBody();
            body.id = "1";
            body.password = password;

            final ValidationErrorException e = catchValidationError(() -> action.keepStoredPassword(body));

            assertTrue(e.getMessages().hasMessageOf("password"));
            assertTrue(e.getMessages().hasMessageOf("password", "errors.could_not_decrypt_password"));
            assertApiHook(action, e);
            assertEquals(password, body.password);
        }
    }

    // A new password does not need the stored one, so an undecryptable stored password does not matter.
    @Test
    public void test_keepStoredPassword_keepsANewPasswordOverAnUndecryptablePassword() throws Exception {
        final EditBody body = new EditBody();
        body.id = "1";
        body.password = "changed";

        createAction(UNDECRYPTABLE_PASSWORD).keepStoredPassword(body);

        assertEquals("changed", body.password);
    }

    private interface Call {
        void run() throws Exception;
    }

    private ValidationErrorException catchValidationError(final Call call) throws Exception {
        try {
            call.run();
        } catch (final ValidationErrorException e) {
            return e;
        }
        fail("a validation error is expected");
        return null;
    }

    /**
     * The error is only turned into a 400 body when its hook is the one {@code throwValidationErrorApi()}
     * installs, so compare it with the hook of an error raised that way.
     */
    private void assertApiHook(final FessBaseAction action, final ValidationErrorException actual) throws Exception {
        final ValidationErrorException reference = catchValidationError(
                () -> action.throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance("_global")));
        assertTrue(reference.getErrorHook().getClass() == actual.getErrorHook().getClass(),
                "the error must carry the API failure hook, not a hook supplied by the action");
    }

    /**
     * Injects the framework fields that {@code throwValidationErrorApi()} needs.
     */
    private <T extends FessBaseAction> T injectAction(final T action) throws Exception {
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        suppressBindingOf(org.codelibs.fess.app.service.AccessTokenService.class);
        suppressBindingOf(WebAuthenticationService.class);
        suppressBindingOf(WebConfigService.class);
        inject(action);

        final SystemHelper systemHelper = new SystemHelper();
        final Field systemHelperField = FessBaseAction.class.getDeclaredField("systemHelper");
        systemHelperField.setAccessible(true);
        if (systemHelperField.get(action) == null) {
            systemHelperField.set(action, systemHelper);
        }
        ComponentUtil.register(systemHelper, "systemHelper");

        final Field fessConfigField = FessBaseAction.class.getDeclaredField("fessConfig");
        fessConfigField.setAccessible(true);
        if (fessConfigField.get(action) == null) {
            fessConfigField.set(action, ComponentUtil.getFessConfig());
        }
        return action;
    }
}
