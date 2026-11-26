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
package org.codelibs.fess.app.web.admin.webauth;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.app.service.WebAuthenticationService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.direction.FessProp;
import org.codelibs.fess.opensearch.config.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.opensearch.config.exbhv.WebAuthenticationBhv;
import org.codelibs.fess.opensearch.config.exentity.WebAuthentication;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ParameterUtil;
import org.codelibs.fesen.opensearch.action.index.IndexRequestBuilder;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.web.token.TokenErrorHook;
import org.lastaflute.web.validation.exception.ValidationErrorException;

public class AdminWebauthActionTest extends UnitFessTestCase {

    // An encrypted password that app.cipher.key cannot decrypt, such as one saved before the key was replaced
    // or restored from the backup of another installation.
    private static final String UNDECRYPTABLE_PASSWORD = "{cipher}00112233445566778899aabbccddeeff";

    private final List<String> storedPasswords = new ArrayList<>();

    private String storedValue;

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        FessProp.propMap.clear();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getAppEncryptPropertyPattern() {
                return ".*password|.*key|.*secret";
            }
        });
        ComponentUtil.register(new SystemHelper() {
            @Override
            public String getUsername() {
                return "admin";
            }
        }, "systemHelper");
        storedPasswords.clear();
        final WebAuthenticationService service = new WebAuthenticationService() {
            @Override
            public OptionalEntity<WebAuthentication> getWebAuthentication(final String id) {
                final WebAuthentication entity = new WebAuthentication();
                entity.setId(id);
                entity.setUsername("user");
                entity.setPassword(storedValue);
                return OptionalEntity.of(entity);
            }
        };
        final Field field = WebAuthenticationService.class.getDeclaredField("webAuthenticationBhv");
        field.setAccessible(true);
        field.set(service, new WebAuthenticationBhv() {
            @Override
            public void insertOrUpdate(final WebAuthentication entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                storedPasswords.add(entity.getPassword());
            }
        });
        ComponentUtil.register(service, WebAuthenticationService.class.getCanonicalName());
    }

    private String update(final String submittedPassword) {
        final EditForm form = new EditForm();
        form.crudMode = CrudMode.EDIT;
        form.id = "1";
        form.username = "user";
        form.password = submittedPassword;
        final WebAuthentication entity = AdminWebauthAction.getWebAuthentication(form).get();
        ComponentUtil.getComponent(WebAuthenticationService.class).store(entity);
        assertEquals(1, storedPasswords.size());
        return storedPasswords.get(0);
    }

    private WebAuthentication stored() {
        return ComponentUtil.getComponent(WebAuthenticationService.class).getWebAuthentication("1").get();
    }

    // The screens work with the plain text: the edit and details pages get the decrypted password.
    @Test
    public void test_copyToForm_formGetsPlainTextPassword() {
        storedValue = ParameterUtil.encryptCredential("s3cret");
        final EditForm form = new EditForm();
        AdminWebauthAction.copyToForm(stored(), form, false);
        assertEquals("s3cret", form.password);
        assertEquals("user", form.username);

        final EditForm detailsForm = new EditForm();
        AdminWebauthAction.copyToForm(stored(), detailsForm, true);
        assertEquals("s3cret", detailsForm.password);
        assertEquals("user", detailsForm.username);
    }

    // A setting saved before the password was encrypted is shown as it is.
    @Test
    public void test_copyToForm_legacyPlainTextPasswordIsShownAsIs() {
        storedValue = "legacy";
        final EditForm form = new EditForm();
        AdminWebauthAction.copyToForm(stored(), form, false);
        assertEquals("legacy", form.password);
    }

    @Test
    public void test_copyToForm_noPassword() {
        storedValue = null;
        final EditForm form = new EditForm();
        AdminWebauthAction.copyToForm(stored(), form, true);
        assertNull(form.password);
    }

    // Saving a setting without touching the password submits the plain text the page showed. It is
    // encrypted once, and the stored value is what it was.
    @Test
    public void test_update_unchangedResubmissionIsEncryptedOnce() {
        storedValue = ParameterUtil.encryptCredential("s3cret");
        final EditForm form = new EditForm();
        AdminWebauthAction.copyToForm(stored(), form, false);

        final String saved = update(form.password);

        assertEquals(storedValue, saved);
        assertEquals("s3cret", ParameterUtil.decryptCredential(saved, null, "password"));
    }

    // A value that looks like an encrypted one, such as the ciphertext of another setting, is a submitted
    // password like any other: it is stored as the literal text, and the details page shows that text,
    // not the secret behind it.
    @Test
    public void test_update_submittedCipherValueIsStoredAsLiteralText() {
        storedValue = ParameterUtil.encryptCredential("s3cret");
        final String submitted = ParameterUtil.encryptCredential("other secret");

        final String saved = update(submitted);

        assertFalse(submitted.equals(saved));
        assertEquals(submitted, ParameterUtil.decryptCredential(saved, null, "password"));
        storedValue = saved;
        final EditForm form = new EditForm();
        AdminWebauthAction.copyToForm(stored(), form, true);
        assertEquals(submitted, form.password);
    }

    // A password typed into the field is encrypted.
    @Test
    public void test_update_newPasswordIsEncrypted() {
        storedValue = ParameterUtil.encryptCredential("s3cret");
        final String saved = update("changed");
        assertTrue(saved, saved.startsWith("{cipher}"));
        assertEquals("changed", ParameterUtil.decryptCredential(saved, null, "password"));
    }

    // A setting saved before the password was encrypted is encrypted by the next save.
    @Test
    public void test_update_legacyPlainTextPasswordIsEncrypted() {
        storedValue = "legacy";
        final String saved = update("legacy");
        assertTrue(saved, saved.startsWith("{cipher}"));
        assertEquals("legacy", ParameterUtil.decryptCredential(saved, null, "password"));
    }

    // The edit and details pages open when the stored password cannot be decrypted. The password is blank,
    // not the encrypted value, so that the admin can enter it again and the encrypted value is never encrypted
    // a second time.
    @Test
    public void test_copyToForm_undecryptablePasswordIsShownBlank() {
        storedValue = UNDECRYPTABLE_PASSWORD;
        final EditForm form = new EditForm();
        AdminWebauthAction.copyToForm(stored(), form, false);
        assertEquals("", form.password);
        assertEquals("user", form.username);

        final EditForm detailsForm = new EditForm();
        AdminWebauthAction.copyToForm(stored(), detailsForm, true);
        assertEquals("", detailsForm.password);
        assertEquals("user", detailsForm.username);
    }

    // The list page shows a setting whose password cannot be decrypted; the row leaves out the crawler configuration,
    // whose getter decrypts the password.
    @Test
    public void test_toListItems_undecryptablePasswordDoesNotFailTheList() {
        storedValue = UNDECRYPTABLE_PASSWORD;
        final WebConfig webConfig = new WebConfig();
        webConfig.setName("site");
        ComponentUtil.register(new WebConfigService() {
            @Override
            public OptionalEntity<WebConfig> getWebConfig(final String id) {
                return OptionalEntity.of(webConfig);
            }
        }, WebConfigService.class.getCanonicalName());
        final List<Map<String, Object>> rows = AdminWebauthAction.toListItems(List.of(stored()));
        assertEquals(1, rows.size());
        assertEquals("user", rows.get(0).get("username"));
        assertFalse(rows.get(0).containsKey("webAuthenticationConfig"));
        // the list shows the name of the web config of the setting
        assertSame(webConfig, rows.get(0).get("webConfig"));
    }

    // A blank password submitted while the stored password can be decrypted clears the password, as it did before.
    @Test
    public void test_update_blankPasswordOverDecryptablePasswordIsSaved() {
        storedValue = ParameterUtil.encryptCredential("s3cret");
        final EditForm form = new EditForm();
        form.id = "1";
        form.password = "";
        assertFalse(AdminWebauthAction.isUndecryptablePasswordBlanked(form));
        assertNull(update(""));
        storedPasswords.clear();
        assertNull(update(null));
    }

    // Saving the edit page with the blank password while the stored password cannot be decrypted is refused, so
    // the stored password is not replaced unless the admin enters a new one.
    @Test
    public void test_update_blankPasswordOverUndecryptablePasswordIsRefused() {
        storedValue = UNDECRYPTABLE_PASSWORD;
        for (final String password : new String[] { null, "", " " }) {
            final EditForm form = new EditForm();
            form.crudMode = CrudMode.EDIT;
            form.id = "1";
            form.password = password;
            assertTrue(AdminWebauthAction.isUndecryptablePasswordBlanked(form));
        }
        assertTrue(storedPasswords.isEmpty());
        assertEquals(UNDECRYPTABLE_PASSWORD, stored().getPassword());
    }

    // A password typed into the field replaces a stored password that cannot be decrypted, encrypted once.
    @Test
    public void test_update_newPasswordReplacesUndecryptablePassword() {
        storedValue = UNDECRYPTABLE_PASSWORD;
        final EditForm form = new EditForm();
        form.id = "1";
        form.password = "changed";
        assertFalse(AdminWebauthAction.isUndecryptablePasswordBlanked(form));

        final String saved = update("changed");

        assertTrue(saved, saved.startsWith("{cipher}"));
        assertEquals("changed", ParameterUtil.decryptCredential(saved, null, "password"));
    }

    @Test
    public void test_copyToForm_returnsWhetherThePasswordWasDecrypted() {
        storedValue = ParameterUtil.encryptCredential("s3cret");
        assertTrue(AdminWebauthAction.copyToForm(stored(), new EditForm(), false));
        storedValue = UNDECRYPTABLE_PASSWORD;
        assertFalse(AdminWebauthAction.copyToForm(stored(), new EditForm(), false));
    }

    // update() itself: saving the edit page with the blank password while the stored password cannot be
    // decrypted is refused with a validation error on the password field, and nothing is stored.
    @Test
    public void test_updateAction_blankPasswordOverUndecryptablePasswordIsRefused() throws Exception {
        storedValue = UNDECRYPTABLE_PASSWORD;
        final AdminWebauthAction action = createUpdateAction();
        for (final String password : new String[] { null, "" }) {
            final EditForm form = createEditForm(password);
            try {
                action.update(form);
                fail("a blank password must not replace a stored password that cannot be decrypted");
            } catch (final ValidationErrorException e) {
                assertTrue(e.getMessages().hasMessageOf("password", "errors.could_not_decrypt_password"));
            }
        }
        assertTrue(storedPasswords.isEmpty());
    }

    // update() itself: a password typed into the field replaces a stored password that cannot be decrypted.
    @Test
    public void test_updateAction_newPasswordReplacesUndecryptablePassword() throws Exception {
        storedValue = UNDECRYPTABLE_PASSWORD;

        createUpdateAction().update(createEditForm("changed"));

        assertEquals(1, storedPasswords.size());
        final String saved = storedPasswords.get(0);
        assertTrue(saved, saved.startsWith("{cipher}"));
        assertEquals("changed", ParameterUtil.decryptCredential(saved, null, "password"));
    }

    private EditForm createEditForm(final String password) {
        final EditForm form = new EditForm();
        form.crudMode = CrudMode.EDIT;
        form.id = "1";
        form.versionNo = 1;
        form.username = "user";
        form.password = password;
        form.webConfigId = "webconfig";
        return form;
    }

    /**
     * Creates the action with the fields that update() needs. The token of the edit page is not under test, so
     * verifyToken() accepts the request.
     */
    private AdminWebauthAction createUpdateAction() throws Exception {
        final AdminWebauthAction action = new AdminWebauthAction() {
            @Override
            protected void verifyToken(final TokenErrorHook errorResponseLambda) {
                // accepted
            }
        };
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        suppressBindingOf(org.codelibs.fess.app.service.AccessTokenService.class);
        suppressBindingOf(WebAuthenticationService.class);
        suppressBindingOf(org.codelibs.fess.app.service.WebConfigService.class);
        suppressBindingOf(org.codelibs.fess.app.pager.WebAuthPager.class);
        inject(action);
        final Field serviceField = AdminWebauthAction.class.getDeclaredField("webAuthenticationService");
        serviceField.setAccessible(true);
        serviceField.set(action, ComponentUtil.getComponent(WebAuthenticationService.class));
        final Field systemHelperField = FessBaseAction.class.getDeclaredField("systemHelper");
        systemHelperField.setAccessible(true);
        systemHelperField.set(action, ComponentUtil.getSystemHelper());
        final Field fessConfigField = FessBaseAction.class.getDeclaredField("fessConfig");
        fessConfigField.setAccessible(true);
        fessConfigField.set(action, ComponentUtil.getFessConfig());
        return action;
    }
}
