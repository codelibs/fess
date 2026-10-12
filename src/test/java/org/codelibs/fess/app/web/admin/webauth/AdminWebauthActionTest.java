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

import org.codelibs.fess.app.service.WebAuthenticationService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.direction.FessProp;
import org.codelibs.fess.opensearch.config.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.opensearch.config.exbhv.WebAuthenticationBhv;
import org.codelibs.fess.opensearch.config.exentity.WebAuthentication;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ParameterUtil;
import org.codelibs.fesen.opensearch.action.index.IndexRequestBuilder;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class AdminWebauthActionTest extends UnitFessTestCase {

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

    // The admin API keeps the stored value for an update without a password, and a restore carries the
    // stored value: a value that already has the prefix is not encrypted again.
    @Test
    public void test_update_prefixedValueIsNotEncryptedTwice() {
        storedValue = ParameterUtil.encryptCredential("s3cret");
        assertEquals(storedValue, update(storedValue));
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
}
