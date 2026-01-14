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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.util.Locale;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.jdbc.ClassificationMeta;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.db.dbflute.exception.ProvidedClassificationNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessListedClassificationProviderTest extends UnitFessTestCase {

    private FessListedClassificationProvider provider;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        provider = new FessListedClassificationProvider();
    }

    @Test
    public void test_provide_notFound() {
        // Test with various classification names that should not be found
        assertProvideThrowsException("TestClassification");
        assertProvideThrowsException("NonExistent");
        assertProvideThrowsException("");
        assertProvideThrowsException("Some.Classification");
        assertProvideThrowsException("Project.TestClassification");
    }

    @Test
    public void test_provide_nullClassificationName() {
        // Test with null classification name
        try {
            provider.provide(null);
            fail("Should throw exception for null classification name");
        } catch (ProvidedClassificationNotFoundException e) {
            // expected
            assertTrue(e.getMessage().contains("Not found the classification: null"));
        } catch (NullPointerException e) {
            // Also acceptable if null is not handled explicitly
        }
    }

    @Test
    public void test_findOnMainSchema_alwaysReturnsNull() {
        // Since the implementation always returns null (no DBFlute classification used),
        // test that it consistently returns null for any input
        assertNull(invokeMethod(provider, "findOnMainSchema", "TestClassification"));
        assertNull(invokeMethod(provider, "findOnMainSchema", ""));
        assertNull(invokeMethod(provider, "findOnMainSchema", "Some.Classification"));
        assertNull(invokeMethod(provider, "findOnMainSchema", "Project.TestClassification"));
    }

    @Test
    public void test_determineAlias_returnsEmpty() {
        // Test with various locales
        OptionalThing<String> result = provider.determineAlias(Locale.ENGLISH);
        assertFalse(result.isPresent());

        result = provider.determineAlias(Locale.JAPANESE);
        assertFalse(result.isPresent());

        result = provider.determineAlias(Locale.GERMAN);
        assertFalse(result.isPresent());

        result = provider.determineAlias(null);
        assertFalse(result.isPresent());
    }

    @Test
    public void test_determineAlias_multipleLocales() {
        // Test with various locale configurations
        Locale[] testLocales = { Locale.US, Locale.UK, Locale.CANADA, Locale.FRANCE, Locale.CHINA, Locale.KOREA, new Locale("es", "ES"),
                new Locale("pt", "BR") };

        for (Locale locale : testLocales) {
            OptionalThing<String> result = provider.determineAlias(locale);
            assertFalse("Should return empty for locale: " + locale, result.isPresent());
        }
    }

    @Test
    public void test_provide_withSpecialCharacters() {
        // Test classification names with special characters
        assertProvideThrowsException("Test@Classification");
        assertProvideThrowsException("Test#Classification");
        assertProvideThrowsException("Test$Classification");
        assertProvideThrowsException("Test%Classification");
        assertProvideThrowsException("Test&Classification");
        assertProvideThrowsException("Test*Classification");
        assertProvideThrowsException("Test(Classification)");
        assertProvideThrowsException("Test[Classification]");
        assertProvideThrowsException("Test{Classification}");
        assertProvideThrowsException("Test|Classification");
        assertProvideThrowsException("Test\\Classification");
        assertProvideThrowsException("Test/Classification");
        assertProvideThrowsException("Test<Classification>");
        assertProvideThrowsException("Test\"Classification\"");
        assertProvideThrowsException("Test'Classification'");
    }

    @Test
    public void test_provide_withWhitespace() {
        // Test classification names with whitespace
        assertProvideThrowsException(" ");
        assertProvideThrowsException("  ");
        assertProvideThrowsException("\t");
        assertProvideThrowsException("\n");
        assertProvideThrowsException("\r");
        assertProvideThrowsException(" TestClassification");
        assertProvideThrowsException("TestClassification ");
        assertProvideThrowsException(" TestClassification ");
        assertProvideThrowsException("Test Classification");
        assertProvideThrowsException("Test\tClassification");
        assertProvideThrowsException("Test\nClassification");
    }

    @Test
    public void test_provide_withNumericValues() {
        // Test classification names with numeric values
        assertProvideThrowsException("123");
        assertProvideThrowsException("0");
        assertProvideThrowsException("-1");
        assertProvideThrowsException("Test123");
        assertProvideThrowsException("123Test");
        assertProvideThrowsException("Test123Classification");
    }

    @Test
    public void test_provide_withLongClassificationName() {
        // Test with very long classification name
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longName.append("VeryLongClassificationName");
        }
        assertProvideThrowsException(longName.toString());
    }

    @Test
    public void test_provide_withUnicodeCharacters() {
        // Test classification names with Unicode characters
        assertProvideThrowsException("テスト分類");
        assertProvideThrowsException("测试分类");
        assertProvideThrowsException("테스트분류");
        assertProvideThrowsException("тестклассификация");
        assertProvideThrowsException("🔥TestClassification");
        assertProvideThrowsException("Test😀Classification");
    }

    // Helper method to assert that provide() throws ProvidedClassificationNotFoundException
    private void assertProvideThrowsException(String classificationName) {
        try {
            provider.provide(classificationName);
            fail("Should throw ProvidedClassificationNotFoundException for: " + classificationName);
        } catch (ProvidedClassificationNotFoundException e) {
            // expected
            assertTrue(e.getMessage().contains("Not found the classification: " + classificationName));
        }
    }

    // Helper method to invoke protected method using reflection
    private ClassificationMeta invokeMethod(Object obj, String methodName, String param) {
        try {
            java.lang.reflect.Method method = obj.getClass().getDeclaredMethod(methodName, String.class);
            method.setAccessible(true);
            return (ClassificationMeta) method.invoke(obj, param);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}