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
package org.codelibs.fess.app.web.admin.searchlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.validation.VaMessenger;

public class AdminSearchlistActionTest extends UnitFessTestCase {

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexAdminRequiredFields() {
                return "url,title,role,boost";
            }

            @Override
            public String getIndexAdminArrayFields() {
                return "lang,role,label,anchor,virtual_host";
            }

            @Override
            public String getIndexAdminDateFields() {
                return "expires,created,timestamp,last_modified";
            }

            @Override
            public String getIndexAdminIntegerFields() {
                return "";
            }

            @Override
            public String getIndexAdminLongFields() {
                return "content_length,favorite_count,click_count";
            }

            @Override
            public String getIndexAdminFloatFields() {
                return "boost";
            }

            @Override
            public String getIndexAdminDoubleFields() {
                return "";
            }
        });
    }

    // ===================================================================================
    //                                                                           Constants
    //                                                                           =========
    @Test
    public void test_roleConstant() {
        assertEquals("admin-searchlist", AdminSearchlistAction.ROLE);
    }

    // ===================================================================================
    //                                                                              Forms
    //                                                                           =========
    @Test
    public void test_createForm_initialize() {
        final CreateForm form = new CreateForm();
        assertNull(form.crudMode);
        assertNull(form.doc);
        assertNull(form.q);

        form.initialize();
        assertEquals(CrudMode.CREATE, form.crudMode.intValue());
    }

    @Test
    public void test_editForm_extends_createForm() {
        final EditForm form = new EditForm();
        assertNull(form.id);
        assertNull(form.seqNo);
        assertNull(form.primaryTerm);
        assertNull(form.crudMode);

        form.id = "test-id";
        form.seqNo = 1L;
        form.primaryTerm = 1L;
        form.crudMode = CrudMode.EDIT;

        assertEquals("test-id", form.id);
        assertEquals(1L, form.seqNo.longValue());
        assertEquals(1L, form.primaryTerm.longValue());
        assertEquals(CrudMode.EDIT, form.crudMode.intValue());
    }

    @Test
    public void test_deleteForm_fields() {
        final DeleteForm form = new DeleteForm();
        assertNull(form.q);
        assertNull(form.docId);

        form.q = "test query";
        form.docId = "doc-123";

        assertEquals("test query", form.q);
        assertEquals("doc-123", form.docId);
    }

    @Test
    public void test_listForm_defaults() {
        final ListForm form = new ListForm();
        assertNull(form.q);
        assertNull(form.sort);
        assertNull(form.start);
        assertNull(form.num);
        assertNull(form.pn);
    }

    // ===================================================================================
    //                                                                     validateFields
    //                                                                           =========
    @Test
    public void test_validateFields_validDoc() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "https://example.com");
        doc.put("title", "Test Title");
        doc.put("role", "Rguest");
        doc.put("boost", "1.0");

        final List<String> errors = new ArrayList<>();
        AdminSearchlistAction.validateFields(doc, messages -> {
            errors.add("error");
        });

        assertTrue(errors.isEmpty());
    }

    @Test
    public void test_validateFields_missingRequiredFields() {
        final Map<String, Object> doc = new HashMap<>();
        // url, title, role, boost are all missing

        final List<VaMessenger<FessMessages>> errorMessages = new ArrayList<>();
        AdminSearchlistAction.validateFields(doc, errorMessages::add);

        assertFalse(errorMessages.isEmpty());

        // Verify error messages use field names without doc. prefix
        final FessMessages messages = new FessMessages();
        errorMessages.forEach(m -> m.message(messages));

        assertTrue(messages.hasMessageOf("doc.url"));
        assertTrue(messages.hasMessageOf("doc.title"));
        assertTrue(messages.hasMessageOf("doc.role"));
        assertTrue(messages.hasMessageOf("doc.boost"));
    }

    @Test
    public void test_validateFields_requiredFieldsPartiallyMissing() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "https://example.com");
        doc.put("title", "Test Title");
        // role and boost are missing

        final List<VaMessenger<FessMessages>> errorMessages = new ArrayList<>();
        AdminSearchlistAction.validateFields(doc, errorMessages::add);

        assertFalse(errorMessages.isEmpty());

        final FessMessages messages = new FessMessages();
        errorMessages.forEach(m -> m.message(messages));

        assertFalse(messages.hasMessageOf("doc.url"));
        assertFalse(messages.hasMessageOf("doc.title"));
        assertTrue(messages.hasMessageOf("doc.role"));
        assertTrue(messages.hasMessageOf("doc.boost"));
    }

    @Test
    public void test_validateFields_invalidFloatField() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "https://example.com");
        doc.put("title", "Test Title");
        doc.put("role", "Rguest");
        doc.put("boost", "not-a-float");

        final List<VaMessenger<FessMessages>> errorMessages = new ArrayList<>();
        AdminSearchlistAction.validateFields(doc, errorMessages::add);

        assertFalse(errorMessages.isEmpty());

        final FessMessages messages = new FessMessages();
        errorMessages.forEach(m -> m.message(messages));

        assertTrue(messages.hasMessageOf("doc.boost"));
    }

    @Test
    public void test_validateFields_invalidLongField() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "https://example.com");
        doc.put("title", "Test Title");
        doc.put("role", "Rguest");
        doc.put("boost", "1.0");
        doc.put("content_length", "not-a-number");

        final List<VaMessenger<FessMessages>> errorMessages = new ArrayList<>();
        AdminSearchlistAction.validateFields(doc, errorMessages::add);

        assertFalse(errorMessages.isEmpty());

        final FessMessages messages = new FessMessages();
        errorMessages.forEach(m -> m.message(messages));

        assertTrue(messages.hasMessageOf("doc.content_length"));
    }

    @Test
    public void test_validateFields_invalidDateField() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "https://example.com");
        doc.put("title", "Test Title");
        doc.put("role", "Rguest");
        doc.put("boost", "1.0");
        doc.put("created", "invalid-date");

        final List<VaMessenger<FessMessages>> errorMessages = new ArrayList<>();
        AdminSearchlistAction.validateFields(doc, errorMessages::add);

        assertFalse(errorMessages.isEmpty());

        final FessMessages messages = new FessMessages();
        errorMessages.forEach(m -> m.message(messages));

        assertTrue(messages.hasMessageOf("doc.created"));
    }

    @Test
    public void test_validateFields_validDateField() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "https://example.com");
        doc.put("title", "Test Title");
        doc.put("role", "Rguest");
        doc.put("boost", "1.0");
        doc.put("created", "2025-01-01T00:00:00.000Z");

        final List<String> errors = new ArrayList<>();
        AdminSearchlistAction.validateFields(doc, messages -> {
            errors.add("error");
        });

        assertTrue(errors.isEmpty());
    }

    @Test
    public void test_validateFields_validLongField() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "https://example.com");
        doc.put("title", "Test Title");
        doc.put("role", "Rguest");
        doc.put("boost", "1.0");
        doc.put("content_length", "12345");
        doc.put("click_count", "100");

        final List<String> errors = new ArrayList<>();
        AdminSearchlistAction.validateFields(doc, messages -> {
            errors.add("error");
        });

        assertTrue(errors.isEmpty());
    }

    @Test
    public void test_validateFields_multipleInvalidFields() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "https://example.com");
        doc.put("title", "Test Title");
        doc.put("role", "Rguest");
        doc.put("boost", "not-a-float");
        doc.put("content_length", "not-a-long");
        doc.put("created", "not-a-date");

        final List<VaMessenger<FessMessages>> errorMessages = new ArrayList<>();
        AdminSearchlistAction.validateFields(doc, errorMessages::add);

        assertTrue(errorMessages.size() >= 3);
    }

    @Test
    public void test_validateFields_emptyStringRequiredFields() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "");
        doc.put("title", "");
        doc.put("role", "");
        doc.put("boost", "");

        final List<VaMessenger<FessMessages>> errorMessages = new ArrayList<>();
        AdminSearchlistAction.validateFields(doc, errorMessages::add);

        assertFalse(errorMessages.isEmpty());

        final FessMessages messages = new FessMessages();
        errorMessages.forEach(m -> m.message(messages));

        assertTrue(messages.hasMessageOf("doc.url"));
        assertTrue(messages.hasMessageOf("doc.title"));
        assertTrue(messages.hasMessageOf("doc.role"));
        assertTrue(messages.hasMessageOf("doc.boost"));
    }

    // ===================================================================================
    //                                                          Validation Message Format
    //                                                                           =========
    @Test
    public void test_validateFields_errorMessageHasNoDocPrefix() {
        final Map<String, Object> doc = new HashMap<>();
        // leave required fields empty

        final List<VaMessenger<FessMessages>> errorMessages = new ArrayList<>();
        AdminSearchlistAction.validateFields(doc, errorMessages::add);

        assertFalse(errorMessages.isEmpty());

        final FessMessages messages = new FessMessages();
        errorMessages.forEach(m -> m.message(messages));

        // The property key should be "doc.url" (for JSP binding)
        assertTrue(messages.hasMessageOf("doc.url"));

        // Verify messages exist at the doc.* property keys
        assertTrue(messages.hasMessageOf("doc.url"), "Required field 'url' should have error at 'doc.url' property");
        assertTrue(messages.hasMessageOf("doc.title"), "Required field 'title' should have error at 'doc.title' property");
        assertTrue(messages.hasMessageOf("doc.role"), "Required field 'role' should have error at 'doc.role' property");
        assertTrue(messages.hasMessageOf("doc.boost"), "Required field 'boost' should have error at 'doc.boost' property");
    }

    @Test
    public void test_validateFields_floatErrorMessagePropertyKey() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "https://example.com");
        doc.put("title", "Test Title");
        doc.put("role", "Rguest");
        doc.put("boost", "invalid");

        final List<VaMessenger<FessMessages>> errorMessages = new ArrayList<>();
        AdminSearchlistAction.validateFields(doc, errorMessages::add);

        final FessMessages messages = new FessMessages();
        errorMessages.forEach(m -> m.message(messages));

        assertTrue(messages.hasMessageOf("doc.boost"), "Float validation error should be at 'doc.boost' property");
    }

    @Test
    public void test_validateFields_longErrorMessagePropertyKey() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "https://example.com");
        doc.put("title", "Test Title");
        doc.put("role", "Rguest");
        doc.put("boost", "1.0");
        doc.put("content_length", "abc");

        final List<VaMessenger<FessMessages>> errorMessages = new ArrayList<>();
        AdminSearchlistAction.validateFields(doc, errorMessages::add);

        final FessMessages messages = new FessMessages();
        errorMessages.forEach(m -> m.message(messages));

        assertTrue(messages.hasMessageOf("doc.content_length"), "Long validation error should be at 'doc.content_length' property");
    }

    @Test
    public void test_validateFields_dateErrorMessagePropertyKey() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "https://example.com");
        doc.put("title", "Test Title");
        doc.put("role", "Rguest");
        doc.put("boost", "1.0");
        doc.put("created", "bad-date");

        final List<VaMessenger<FessMessages>> errorMessages = new ArrayList<>();
        AdminSearchlistAction.validateFields(doc, errorMessages::add);

        final FessMessages messages = new FessMessages();
        errorMessages.forEach(m -> m.message(messages));

        assertTrue(messages.hasMessageOf("doc.created"), "Date validation error should be at 'doc.created' property");
    }

    // ===================================================================================
    //                                                                  STANDARD_EDIT_FIELDS
    //                                                                           =========
    @Test
    public void test_standardEditFields_containsExpectedFields() throws Exception {
        final java.lang.reflect.Field field = AdminSearchlistAction.class.getDeclaredField("STANDARD_EDIT_FIELDS");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        final java.util.Set<String> standardFields = (java.util.Set<String>) field.get(null);

        assertTrue(standardFields.contains("url"));
        assertTrue(standardFields.contains("title"));
        assertTrue(standardFields.contains("role"));
        assertTrue(standardFields.contains("boost"));
        assertTrue(standardFields.contains("label"));
        assertTrue(standardFields.contains("lang"));
        assertTrue(standardFields.contains("mimetype"));
        assertTrue(standardFields.contains("content_length"));
        assertTrue(standardFields.contains("created"));
        assertTrue(standardFields.contains("timestamp"));
        assertTrue(standardFields.contains("last_modified"));
        assertTrue(standardFields.contains("expires"));
        assertTrue(standardFields.contains("virtual_host"));
        assertTrue(standardFields.contains("doc_id"));
    }

    @Test
    public void test_standardEditFields_doesNotContainCustomFields() throws Exception {
        final java.lang.reflect.Field field = AdminSearchlistAction.class.getDeclaredField("STANDARD_EDIT_FIELDS");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        final java.util.Set<String> standardFields = (java.util.Set<String>) field.get(null);

        assertFalse(standardFields.contains("custom_field"));
        assertFalse(standardFields.contains("department"));
        assertFalse(standardFields.contains("author"));
    }

    // ===================================================================================
    //                                                                  registerExtraFields
    //                                                                           =========
    @Test
    public void test_registerExtraFields_noExtraFields() throws Exception {
        final AdminSearchlistAction action = new AdminSearchlistAction();

        // Set currentForm via reflection
        final CreateForm form = new CreateForm();
        form.doc = new HashMap<>();
        form.doc.put("url", "https://example.com");
        form.doc.put("title", "Test");

        final java.lang.reflect.Field currentFormField = AdminSearchlistAction.class.getDeclaredField("currentForm");
        currentFormField.setAccessible(true);
        currentFormField.set(action, form);

        // Set fessConfig via reflection
        final java.lang.reflect.Field fessConfigField = findField(action.getClass(), "fessConfig");
        fessConfigField.setAccessible(true);
        fessConfigField.set(action, ComponentUtil.getFessConfig());

        // Call registerExtraFields via reflection
        final java.lang.reflect.Method method = AdminSearchlistAction.class.getDeclaredMethod("registerExtraFields", RenderData.class);
        method.setAccessible(true);

        final RenderData renderData = new RenderData();
        method.invoke(action, renderData);

        @SuppressWarnings("unchecked")
        final List<String> extraFieldNames = (List<String>) renderData.getDataMap().get("extraFieldNames");
        assertTrue(extraFieldNames == null || extraFieldNames.isEmpty());
    }

    @Test
    public void test_registerExtraFields_withCustomFields() throws Exception {
        final AdminSearchlistAction action = new AdminSearchlistAction();

        // Set currentForm via reflection
        final CreateForm form = new CreateForm();
        form.doc = new HashMap<>();
        form.doc.put("url", "https://example.com");
        form.doc.put("title", "Test");
        form.doc.put("custom_metadata", "custom value");
        form.doc.put("department", "Engineering");

        final java.lang.reflect.Field currentFormField = AdminSearchlistAction.class.getDeclaredField("currentForm");
        currentFormField.setAccessible(true);
        currentFormField.set(action, form);

        final java.lang.reflect.Field fessConfigField = findField(action.getClass(), "fessConfig");
        fessConfigField.setAccessible(true);
        fessConfigField.set(action, ComponentUtil.getFessConfig());

        final java.lang.reflect.Method method = AdminSearchlistAction.class.getDeclaredMethod("registerExtraFields", RenderData.class);
        method.setAccessible(true);

        final RenderData renderData = new RenderData();
        method.invoke(action, renderData);

        @SuppressWarnings("unchecked")
        final List<String> extraFieldNames = (List<String>) renderData.getDataMap().get("extraFieldNames");
        assertNotNull(extraFieldNames);
        assertEquals(2, extraFieldNames.size());
        assertTrue(extraFieldNames.contains("custom_metadata"));
        assertTrue(extraFieldNames.contains("department"));

        @SuppressWarnings("unchecked")
        final Map<String, String> extraFieldTypes = (Map<String, String>) renderData.getDataMap().get("extraFieldTypes");
        assertNotNull(extraFieldTypes);
        assertEquals("text", extraFieldTypes.get("custom_metadata"));
        assertEquals("text", extraFieldTypes.get("department"));
    }

    @Test
    public void test_registerExtraFields_sortedOrder() throws Exception {
        final AdminSearchlistAction action = new AdminSearchlistAction();

        final CreateForm form = new CreateForm();
        form.doc = new HashMap<>();
        form.doc.put("url", "https://example.com");
        form.doc.put("zzz_field", "z");
        form.doc.put("aaa_field", "a");
        form.doc.put("mmm_field", "m");

        final java.lang.reflect.Field currentFormField = AdminSearchlistAction.class.getDeclaredField("currentForm");
        currentFormField.setAccessible(true);
        currentFormField.set(action, form);

        final java.lang.reflect.Field fessConfigField = findField(action.getClass(), "fessConfig");
        fessConfigField.setAccessible(true);
        fessConfigField.set(action, ComponentUtil.getFessConfig());

        final java.lang.reflect.Method method = AdminSearchlistAction.class.getDeclaredMethod("registerExtraFields", RenderData.class);
        method.setAccessible(true);

        final RenderData renderData = new RenderData();
        method.invoke(action, renderData);

        @SuppressWarnings("unchecked")
        final List<String> extraFieldNames = (List<String>) renderData.getDataMap().get("extraFieldNames");
        assertNotNull(extraFieldNames);
        assertEquals(3, extraFieldNames.size());
        assertEquals("aaa_field", extraFieldNames.get(0));
        assertEquals("mmm_field", extraFieldNames.get(1));
        assertEquals("zzz_field", extraFieldNames.get(2));
    }

    @Test
    public void test_registerExtraFields_nullCurrentForm() throws Exception {
        final AdminSearchlistAction action = new AdminSearchlistAction();

        final java.lang.reflect.Field fessConfigField = findField(action.getClass(), "fessConfig");
        fessConfigField.setAccessible(true);
        fessConfigField.set(action, ComponentUtil.getFessConfig());

        final java.lang.reflect.Method method = AdminSearchlistAction.class.getDeclaredMethod("registerExtraFields", RenderData.class);
        method.setAccessible(true);

        final RenderData renderData = new RenderData();
        method.invoke(action, renderData);

        assertNull(renderData.getDataMap().get("extraFieldNames"));
    }

    @Test
    public void test_registerExtraFields_nullDoc() throws Exception {
        final AdminSearchlistAction action = new AdminSearchlistAction();

        final CreateForm form = new CreateForm();
        form.doc = null;

        final java.lang.reflect.Field currentFormField = AdminSearchlistAction.class.getDeclaredField("currentForm");
        currentFormField.setAccessible(true);
        currentFormField.set(action, form);

        final java.lang.reflect.Field fessConfigField = findField(action.getClass(), "fessConfig");
        fessConfigField.setAccessible(true);
        fessConfigField.set(action, ComponentUtil.getFessConfig());

        final java.lang.reflect.Method method = AdminSearchlistAction.class.getDeclaredMethod("registerExtraFields", RenderData.class);
        method.setAccessible(true);

        final RenderData renderData = new RenderData();
        method.invoke(action, renderData);

        assertNull(renderData.getDataMap().get("extraFieldNames"));
    }

    @Test
    public void test_registerExtraFields_fieldTypeDetection() throws Exception {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexAdminArrayFields() {
                return "custom_array";
            }

            @Override
            public String getIndexAdminDateFields() {
                return "custom_date";
            }

            @Override
            public String getIndexAdminLongFields() {
                return "custom_long";
            }

            @Override
            public String getIndexAdminFloatFields() {
                return "custom_float";
            }

            @Override
            public String getIndexAdminIntegerFields() {
                return "";
            }

            @Override
            public String getIndexAdminDoubleFields() {
                return "";
            }

            @Override
            public String getIndexAdminRequiredFields() {
                return "";
            }
        });

        final AdminSearchlistAction action = new AdminSearchlistAction();

        final CreateForm form = new CreateForm();
        form.doc = new HashMap<>();
        form.doc.put("custom_array", "val1\nval2");
        form.doc.put("custom_date", "2025-01-01T00:00:00.000Z");
        form.doc.put("custom_long", "12345");
        form.doc.put("custom_float", "1.5");
        form.doc.put("custom_text", "hello");

        final java.lang.reflect.Field currentFormField = AdminSearchlistAction.class.getDeclaredField("currentForm");
        currentFormField.setAccessible(true);
        currentFormField.set(action, form);

        final java.lang.reflect.Field fessConfigField = findField(action.getClass(), "fessConfig");
        fessConfigField.setAccessible(true);
        fessConfigField.set(action, ComponentUtil.getFessConfig());

        final java.lang.reflect.Method method = AdminSearchlistAction.class.getDeclaredMethod("registerExtraFields", RenderData.class);
        method.setAccessible(true);

        final RenderData renderData = new RenderData();
        method.invoke(action, renderData);

        @SuppressWarnings("unchecked")
        final Map<String, String> extraFieldTypes = (Map<String, String>) renderData.getDataMap().get("extraFieldTypes");
        assertNotNull(extraFieldTypes);
        assertEquals("array", extraFieldTypes.get("custom_array"));
        assertEquals("date", extraFieldTypes.get("custom_date"));
        assertEquals("number", extraFieldTypes.get("custom_long"));
        assertEquals("number", extraFieldTypes.get("custom_float"));
        assertEquals("text", extraFieldTypes.get("custom_text"));
    }

    // ===================================================================================
    //                                                                           Helpers
    //                                                                           =========
    private java.lang.reflect.Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (final NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new RuntimeException("Field not found: " + fieldName);
    }

}
