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
package org.codelibs.fess.app.web.api.admin.dict;

import java.lang.reflect.Field;

import org.codelibs.fess.app.service.CharMappingService;
import org.codelibs.fess.app.service.SynonymService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.admin.dict.mapping.ApiAdminDictMappingAction;
import org.codelibs.fess.app.web.api.admin.dict.synonym.ApiAdminDictSynonymAction;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.validation.exception.ValidationErrorException;

/**
 * An entry the synonym or mapping file cannot hold is refused with a validation error that the
 * API failure hook turns into a 400 response.
 *
 * <p>The error has to be raised with {@code throwValidationErrorApi()}. The admin screens report
 * the same violation with a hook that renders their edit page, and the hook the API actions pass
 * to the shared entry builders only throws a second validation error when LastaFlute runs it, so
 * an entry such as {@code "検索,サーチ"} used to end as a 500 system error instead.</p>
 */
public class ApiAdminDictEntryValidationTest extends UnitFessTestCase {

    @Test
    public void test_synonym_post$setting_refusesACommaInAnInputWithTheApiHook() throws Exception {
        final ApiAdminDictSynonymAction action = createSynonymAction();
        final org.codelibs.fess.app.web.api.admin.dict.synonym.CreateBody body =
                new org.codelibs.fess.app.web.api.admin.dict.synonym.CreateBody();
        body.crudMode = CrudMode.CREATE;
        body.inputs = "検索,サーチ";
        body.outputs = "検索\nサーチ\nリサーチ";

        final ValidationErrorException e = catchValidationError(() -> action.post$setting("synonym-id", body));

        assertTrue(e.getMessages().hasMessageOf("inputs"), "the error must name the offending property");
        assertApiHook(action, e);
    }

    @Test
    public void test_synonym_put$setting_refusesACommaInAnOutputWithTheApiHook() throws Exception {
        final ApiAdminDictSynonymAction action = createSynonymAction();
        final org.codelibs.fess.app.web.api.admin.dict.synonym.EditBody body =
                new org.codelibs.fess.app.web.api.admin.dict.synonym.EditBody();
        body.id = 1L;
        body.crudMode = CrudMode.EDIT;
        body.inputs = "検索";
        body.outputs = "検索,サーチ,リサーチ";

        final ValidationErrorException e = catchValidationError(() -> action.put$setting("synonym-id", body));

        assertTrue(e.getMessages().hasMessageOf("outputs"), "the error must name the offending property");
        assertApiHook(action, e);
    }

    @Test
    public void test_mapping_post$setting_refusesACommaInAnInputWithTheApiHook() throws Exception {
        final ApiAdminDictMappingAction action = createMappingAction();
        final org.codelibs.fess.app.web.api.admin.dict.mapping.CreateBody body =
                new org.codelibs.fess.app.web.api.admin.dict.mapping.CreateBody();
        body.crudMode = CrudMode.CREATE;
        body.inputs = "a,b";
        body.output = "c";

        final ValidationErrorException e = catchValidationError(() -> action.post$setting("mapping-id", body));

        assertTrue(e.getMessages().hasMessageOf("inputs"), "the error must name the offending property");
        assertApiHook(action, e);
    }

    @Test
    public void test_mapping_put$setting_refusesTheRuleSeparatorInAnInputWithTheApiHook() throws Exception {
        final ApiAdminDictMappingAction action = createMappingAction();
        final org.codelibs.fess.app.web.api.admin.dict.mapping.EditBody body =
                new org.codelibs.fess.app.web.api.admin.dict.mapping.EditBody();
        body.id = 1L;
        body.crudMode = CrudMode.EDIT;
        body.inputs = "a=>b";
        body.output = "c";

        final ValidationErrorException e = catchValidationError(() -> action.put$setting("mapping-id", body));

        assertTrue(e.getMessages().hasMessageOf("inputs"), "the error must name the offending property");
        assertApiHook(action, e);
    }

    // ===================================================================================
    //                                                                             Helpers
    //                                                                             =======

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

    private ApiAdminDictSynonymAction createSynonymAction() throws Exception {
        suppressBindingOf(SynonymService.class);
        final ApiAdminDictSynonymAction action = injectAction(new ApiAdminDictSynonymAction());
        setField(ApiAdminDictSynonymAction.class, action, "synonymService", new SynonymService());
        return action;
    }

    private ApiAdminDictMappingAction createMappingAction() throws Exception {
        suppressBindingOf(CharMappingService.class);
        final ApiAdminDictMappingAction action = injectAction(new ApiAdminDictMappingAction());
        setField(ApiAdminDictMappingAction.class, action, "charMappingService", new CharMappingService() {
            @Override
            public boolean containsInput(final String dictId, final String input, final Long excludeId) {
                return false;
            }
        });
        return action;
    }

    /**
     * Injects the framework fields that {@code validateApi()} and {@code throwValidationErrorApi()} need.
     */
    private <T extends FessBaseAction> T injectAction(final T action) throws Exception {
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        suppressBindingOf(org.codelibs.fess.app.service.AccessTokenService.class);
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

    private static void setField(final Class<?> type, final Object target, final String name, final Object value) throws Exception {
        final Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
