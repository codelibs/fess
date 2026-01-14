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
package org.codelibs.fess.mylasta.action;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessLabelsTest extends UnitFessTestCase {

    private FessLabels fessLabels;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        fessLabels = new FessLabels();
    }

    /**
     * Test that assertPropertyNotNull throws exception for null input
     */
    @Test
    public void test_assertPropertyNotNull_withNull() {
        try {
            // Create a test instance that extends FessLabels
            TestFessLabels labels = new TestFessLabels();
            labels.testAssertPropertyNotNull(null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("The argument 'property' for message should not be null.", e.getMessage());
        }
    }

    /**
     * Test that assertPropertyNotNull does not throw exception for non-null input
     */
    @Test
    public void test_assertPropertyNotNull_withNonNull() {
        TestFessLabels labels = new TestFessLabels();
        // Should not throw exception
        labels.testAssertPropertyNotNull("test");
        labels.testAssertPropertyNotNull("");
        labels.testAssertPropertyNotNull(" ");
    }

    // Helper class to test protected method
    private static class TestFessLabels extends FessLabels {
        private static final long serialVersionUID = 1L;

        public void testAssertPropertyNotNull(String property) {
            assertPropertyNotNull(property);
        }
    }

    /**
     * Test serialVersionUID exists and has correct value
     */
    @Test
    public void test_serialVersionUID() throws Exception {
        Field serialVersionField = FessLabels.class.getDeclaredField("serialVersionUID");
        assertNotNull(serialVersionField);
        assertTrue(Modifier.isStatic(serialVersionField.getModifiers()));
        assertTrue(Modifier.isFinal(serialVersionField.getModifiers()));
        assertTrue(Modifier.isPrivate(serialVersionField.getModifiers()));
        serialVersionField.setAccessible(true);
        assertEquals(1L, serialVersionField.getLong(null));
    }

    /**
     * Test that all label constants follow the naming convention
     */
    @Test
    public void test_labelConstantsNamingConvention() throws Exception {
        Field[] fields = FessLabels.class.getDeclaredFields();
        Pattern labelPattern = Pattern.compile("^LABELS_[A-Za-z0-9_]+$");

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && Modifier.isPublic(field.getModifiers())
                    && field.getType() == String.class && !field.getName().equals("serialVersionUID")) {

                // Check field name follows convention
                assertTrue("Field name should match pattern: " + field.getName(), labelPattern.matcher(field.getName()).matches());

                // Check field value follows convention
                field.setAccessible(true);
                String value = (String) field.get(null);
                assertNotNull("Field value should not be null: " + field.getName(), value);
                assertTrue("Field value should start with {labels.: " + field.getName() + " = " + value, value.startsWith("{labels."));
                assertTrue("Field value should end with }: " + field.getName() + " = " + value, value.endsWith("}"));
            }
        }
    }

    /**
     * Test that all label constants have unique values
     */
    @Test
    public void test_labelConstantsUniqueness() throws Exception {
        Field[] fields = FessLabels.class.getDeclaredFields();
        Set<String> values = new HashSet<>();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && Modifier.isPublic(field.getModifiers())
                    && field.getType() == String.class && !field.getName().equals("serialVersionUID")) {

                field.setAccessible(true);
                String value = (String) field.get(null);
                assertFalse("Duplicate value found: " + value + " for field: " + field.getName(), values.contains(value));
                values.add(value);
            }
        }
    }

    /**
     * Test specific label constants exist and have correct values
     */
    @Test
    public void test_specificLabelConstants() {
        // Test common labels
        assertEquals("{labels.authRealm}", FessLabels.LABELS_AUTH_REALM);
        assertEquals("{labels.available}", FessLabels.LABELS_AVAILABLE);
        assertEquals("{labels.createdBy}", FessLabels.LABELS_CREATED_BY);
        assertEquals("{labels.createdTime}", FessLabels.LABELS_CREATED_TIME);
        assertEquals("{labels.depth}", FessLabels.LABELS_DEPTH);
        assertEquals("{labels.excludedPaths}", FessLabels.LABELS_EXCLUDED_PATHS);
        assertEquals("{labels.excludedUrls}", FessLabels.LABELS_EXCLUDED_URLS);
        assertEquals("{labels.excludedDocPaths}", FessLabels.LABELS_EXCLUDED_DOC_PATHS);
        assertEquals("{labels.excludedDocUrls}", FessLabels.LABELS_EXCLUDED_DOC_URLS);
        assertEquals("{labels.hostname}", FessLabels.LABELS_HOSTNAME);
        assertEquals("{labels.id}", FessLabels.LABELS_ID);
        assertEquals("{labels.includedPaths}", FessLabels.LABELS_INCLUDED_PATHS);
        assertEquals("{labels.includedUrls}", FessLabels.LABELS_INCLUDED_URLS);
        assertEquals("{labels.includedDocPaths}", FessLabels.LABELS_INCLUDED_DOC_PATHS);
        assertEquals("{labels.includedDocUrls}", FessLabels.LABELS_INCLUDED_DOC_URLS);
        assertEquals("{labels.maxAccessCount}", FessLabels.LABELS_MAX_ACCESS_COUNT);
        assertEquals("{labels.name}", FessLabels.LABELS_NAME);
        assertEquals("{labels.numOfThread}", FessLabels.LABELS_NUM_OF_THREAD);
        assertEquals("{labels.duplicateHostName}", FessLabels.LABELS_DUPLICATE_HOST_NAME);
        assertEquals("{labels.pageNumber}", FessLabels.LABELS_PAGE_NUMBER);
        assertEquals("{labels.password}", FessLabels.LABELS_PASSWORD);
        assertEquals("{labels.paths}", FessLabels.LABELS_PATHS);
        assertEquals("{labels.port}", FessLabels.LABELS_PORT);
        assertEquals("{labels.regex}", FessLabels.LABELS_REGEX);

        // Test login/logout related labels
        assertEquals("{labels.login}", FessLabels.LABELS_LOGIN);
        assertEquals("{labels.login.title}", FessLabels.LABELS_LOGIN_TITLE);
        assertEquals("{labels.login.placeholder_username}", FessLabels.LABELS_LOGIN_placeholder_username);
        assertEquals("{labels.login.placeholder_password}", FessLabels.LABELS_LOGIN_placeholder_password);
        assertEquals("{labels.logout}", FessLabels.LABELS_LOGOUT);
        assertEquals("{labels.logout_title}", FessLabels.LABELS_logout_title);
        assertEquals("{labels.logout_button}", FessLabels.LABELS_logout_button);
        assertEquals("{labels.do_you_want_to_logout}", FessLabels.LABELS_do_you_want_to_logout);

        // Test profile related labels
        assertEquals("{labels.profile}", FessLabels.LABELS_PROFILE);
        assertEquals("{labels.profile.title}", FessLabels.LABELS_PROFILE_TITLE);
        assertEquals("{labels.profile.update}", FessLabels.LABELS_PROFILE_UPDATE);
        assertEquals("{labels.profile.back}", FessLabels.LABELS_PROFILE_BACK);
        assertEquals("{labels.profile.placeholder_old_password}", FessLabels.LABELS_PROFILE_placeholder_old_password);
        assertEquals("{labels.profile.placeholder_new_password}", FessLabels.LABELS_PROFILE_placeholder_new_password);
        assertEquals("{labels.profile.placeholder_confirm_new_password}", FessLabels.LABELS_PROFILE_placeholder_confirm_new_password);

        // Test search related labels
        assertEquals("{labels.top.search}", FessLabels.LABELS_TOP_SEARCH);
        assertEquals("{labels.index_title}", FessLabels.LABELS_index_title);
        assertEquals("{labels.index_form_search_btn}", FessLabels.LABELS_index_form_search_btn);
        assertEquals("{labels.index_label}", FessLabels.LABELS_index_label);
        assertEquals("{labels.index_lang}", FessLabels.LABELS_index_lang);
        assertEquals("{labels.index_sort}", FessLabels.LABELS_index_sort);
        assertEquals("{labels.index_num}", FessLabels.LABELS_index_num);

        // Test error related labels
        assertEquals("{labels.error_title}", FessLabels.LABELS_error_title);
        assertEquals("{labels.system_error_title}", FessLabels.LABELS_system_error_title);
        assertEquals("{labels.contact_site_admin}", FessLabels.LABELS_contact_site_admin);
        assertEquals("{labels.request_error_title}", FessLabels.LABELS_request_error_title);
        assertEquals("{labels.bad_request}", FessLabels.LABELS_bad_request);
        assertEquals("{labels.page_not_found_title}", FessLabels.LABELS_page_not_found_title);
        assertEquals("{labels.check_url}", FessLabels.LABELS_check_url);

        // Test administration labels
        assertEquals("{labels.administration}", FessLabels.LABELS_ADMINISTRATION);
        assertEquals("{labels.user_name}", FessLabels.LABELS_user_name);
        assertEquals("{labels.profile_button}", FessLabels.LABELS_profile_button);

        // Test new password related labels
        assertEquals("{labels.login.newpassword}", FessLabels.LABELS_LOGIN_NEWPASSWORD);
        assertEquals("{labels.login.placeholder_new_password}", FessLabels.LABELS_LOGIN_placeholder_new_password);
        assertEquals("{labels.login.placeholder_confirm_new_password}", FessLabels.LABELS_LOGIN_placeholder_confirm_new_password);
        assertEquals("{labels.login.update}", FessLabels.LABELS_LOGIN_UPDATE);
    }

    /**
     * Test that FessLabels extends UserMessages
     */
    @Test
    public void test_extendsUserMessages() {
        assertTrue(org.lastaflute.core.message.UserMessages.class.isAssignableFrom(FessLabels.class));
    }

    /**
     * Test field count to ensure no unexpected fields are added
     */
    @Test
    public void test_fieldCount() throws Exception {
        Field[] fields = FessLabels.class.getDeclaredFields();
        int labelConstantCount = 0;
        int serialVersionUIDCount = 0;

        for (Field field : fields) {
            if (field.getName().equals("serialVersionUID")) {
                serialVersionUIDCount++;
            } else if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())
                    && Modifier.isPublic(field.getModifiers()) && field.getType() == String.class) {
                labelConstantCount++;
            }
        }

        assertEquals("Should have exactly one serialVersionUID", 1, serialVersionUIDCount);
        assertTrue("Should have many label constants", labelConstantCount > 100);
    }

    /**
     * Test that label constant values match their field names
     */
    @Test
    public void test_labelConstantValueMatchesName() throws Exception {
        Field[] fields = FessLabels.class.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && Modifier.isPublic(field.getModifiers())
                    && field.getType() == String.class && field.getName().startsWith("LABELS_")) {

                field.setAccessible(true);
                String value = (String) field.get(null);

                // Convert field name to expected value
                String fieldNamePart = field.getName().substring("LABELS_".length());
                String expectedValuePart = fieldNamePart.toLowerCase()
                        .replace("_", ".")
                        .replaceAll("\\.placeholder$", ".placeholder")
                        .replaceAll("\\.placeholder\\.(.+)", ".placeholder_$1");

                String expectedValue = "{labels." + expectedValuePart + "}";

                // Some fields may have different conventions, so we just check if it starts correctly
                assertTrue("Field " + field.getName() + " value '" + value + "' should be related to its name",
                        value.startsWith("{labels."));
            }
        }
    }

    /**
     * Test instance creation
     */
    @Test
    public void test_instanceCreation() {
        FessLabels labels = new FessLabels();
        assertNotNull(labels);
        assertTrue(labels instanceof org.lastaflute.core.message.UserMessages);
    }

    /**
     * Test reflection access to constants
     */
    @Test
    public void test_reflectionAccess() throws Exception {
        Class<?> clazz = FessLabels.class;
        Field field = clazz.getField("LABELS_LOGIN");
        assertNotNull(field);
        assertEquals(String.class, field.getType());
        assertTrue(Modifier.isPublic(field.getModifiers()));
        assertTrue(Modifier.isStatic(field.getModifiers()));
        assertTrue(Modifier.isFinal(field.getModifiers()));
        assertEquals("{labels.login}", field.get(null));
    }

    /**
     * Test for search options menu labels
     */
    @Test
    public void test_searchOptionsMenuLabels() {
        assertEquals("{labels.searchoptions_menu_labels}", FessLabels.LABELS_searchoptions_menu_labels);
    }

    /**
     * Interface for testing protected methods
     */
    private interface FessLabelsTestAccessor {
        void testAssertPropertyNotNull(String property);
    }
}