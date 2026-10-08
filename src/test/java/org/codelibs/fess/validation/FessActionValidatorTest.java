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
package org.codelibs.fess.validation;

import java.lang.reflect.Field;
import java.util.Locale;

import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.core.message.UserMessage;
import org.lastaflute.core.message.supplier.UserMessagesCreator;
import org.lastaflute.web.servlet.request.RequestManager;
import org.lastaflute.web.validation.ActionValidator;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.exception.ValidationErrorException;

import jakarta.validation.groups.Default;

public class FessActionValidatorTest extends UnitFessTestCase {

    @Test
    public void test_constructor() {
        // Test basic class structure without complex mocking
        assertEquals("FessActionValidator should be in correct package", "org.codelibs.fess.validation.FessActionValidator",
                FessActionValidator.class.getName());
    }

    @Test
    public void test_inheritance() {
        assertTrue("FessActionValidator should extend ActionValidator", ActionValidator.class.isAssignableFrom(FessActionValidator.class));
    }

    @Test
    public void test_constructorExists() {
        // Verify constructor exists with expected parameters
        try {
            FessActionValidator.class.getConstructor(RequestManager.class, UserMessagesCreator.class, Class[].class);
            assertTrue("Constructor with required parameters exists", true);
        } catch (final NoSuchMethodException e) {
            fail("Constructor with RequestManager, UserMessagesCreator, and Class[] parameters should exist");
        }
    }

    @Test
    public void test_classStructure() {
        // Verify the class is generic
        final java.lang.reflect.TypeVariable<?>[] typeParameters = FessActionValidator.class.getTypeParameters();
        assertEquals("Should have one type parameter", 1, typeParameters.length);
        assertEquals("Type parameter should be MESSAGES", "MESSAGES", typeParameters[0].getName());
    }

    @Test
    public void test_packageStructure() {
        final Package pkg = FessActionValidator.class.getPackage();
        assertNotNull(pkg, "Package should not be null");
        assertEquals("Should be in correct package", "org.codelibs.fess.validation", pkg.getName());
    }

    @Test
    public void test_isPublicClass() {
        assertTrue("FessActionValidator should be public", java.lang.reflect.Modifier.isPublic(FessActionValidator.class.getModifiers()));
    }

    public static class NameForm {
        @Required
        public String name;
    }

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        resetSharedValidator();
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        resetSharedValidator();
        super.tearDown(testInfo);
    }

    // ActionValidator keeps one Hibernate Validator for every instance, bound to the first one built.
    // Drop it so that this test builds it from the request manager of its own container.
    private void resetSharedValidator() throws Exception {
        final Field field = ActionValidator.class.getDeclaredField("cachedValidator");
        field.setAccessible(true);
        field.set(null, null);
    }

    private String validateName(final Locale locale) {
        final RequestManager requestManager = ComponentUtil.getRequestManager();
        requestManager.saveUserLocaleToSession(locale);
        // a validator per request, as SystemHelper.createValidator builds it
        final FessActionValidator<FessMessages> validator =
                new FessActionValidator<>(requestManager, FessMessages::new, new Class<?>[] { Default.class });
        try {
            validator.validate(new NameForm(), messages -> {}, () -> null);
        } catch (final ValidationErrorException e) {
            final UserMessage message = e.getMessages().silentAccessByIteratorOf("name").next();
            return message.getMessageKey();
        }
        throw new AssertionError("The blank name should be rejected: locale=" + locale);
    }

    @Test
    public void test_validationMessage_followsRequestLocale() {
        // Hibernate Validator caches a resolved sentence by its template and its own default locale,
        // so the sentence a request gets must not depend on the locale that reported it first.
        assertEquals("Nom est requis.", validateName(Locale.FRENCH));
        assertEquals("Name is required.", validateName(Locale.ENGLISH));
        assertEquals("名前 が必要です。", validateName(Locale.JAPANESE));
        assertEquals("Name ist erforderlich.", validateName(Locale.GERMAN));
        assertEquals("Nom est requis.", validateName(Locale.FRENCH));
    }
}
