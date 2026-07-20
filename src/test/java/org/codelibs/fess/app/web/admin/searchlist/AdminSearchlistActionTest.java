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
import org.codelibs.fess.helper.ContentChunkConstants;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
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

            @Override
            public String getIndexFieldId() {
                return "_id";
            }

            @Override
            public String getIndexFieldVersion() {
                return "_version";
            }

            @Override
            public String getIndexFieldSeqNo() {
                return "_seq_no";
            }

            @Override
            public String getIndexFieldPrimaryTerm() {
                return "_primary_term";
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
        assertNotNull(extraFieldNames);
        // Config-defined field "anchor" is not in STANDARD_EDIT_FIELDS, so it appears as extra
        assertTrue(extraFieldNames.contains("anchor"));
        // Standard field "url" should not appear
        assertFalse(extraFieldNames.contains("url"));
        assertFalse(extraFieldNames.contains("title"));
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
        assertTrue(extraFieldNames.contains("custom_metadata"));
        assertTrue(extraFieldNames.contains("department"));
        // Config-defined "anchor" also appears as extra field
        assertTrue(extraFieldNames.contains("anchor"));

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
        assertTrue(extraFieldNames.contains("aaa_field"));
        assertTrue(extraFieldNames.contains("mmm_field"));
        assertTrue(extraFieldNames.contains("zzz_field"));
        // Verify sorted order among the custom fields
        final int aIdx = extraFieldNames.indexOf("aaa_field");
        final int mIdx = extraFieldNames.indexOf("mmm_field");
        final int zIdx = extraFieldNames.indexOf("zzz_field");
        assertTrue(aIdx < mIdx);
        assertTrue(mIdx < zIdx);
    }

    @Test
    public void test_registerExtraFields_nullDoc_showsConfigFields() throws Exception {
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

        // Config fields that are not in STANDARD_EDIT_FIELDS should still appear
        @SuppressWarnings("unchecked")
        final List<String> extraFieldNames = (List<String>) renderData.getDataMap().get("extraFieldNames");
        assertNotNull(extraFieldNames);
        assertTrue(extraFieldNames.contains("anchor"));
    }

    @Test
    public void test_registerExtraFields_excludesReservedFields() throws Exception {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexAdminArrayFields() {
                return "";
            }

            @Override
            public String getIndexAdminDateFields() {
                return "";
            }

            @Override
            public String getIndexAdminLongFields() {
                return "";
            }

            @Override
            public String getIndexAdminFloatFields() {
                return "";
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

            @Override
            public String getIndexFieldId() {
                return "_id";
            }

            @Override
            public String getIndexFieldVersion() {
                return "_version";
            }

            @Override
            public String getIndexFieldSeqNo() {
                return "_seq_no";
            }

            @Override
            public String getIndexFieldPrimaryTerm() {
                return "_primary_term";
            }
        });

        final AdminSearchlistAction action = new AdminSearchlistAction();

        final CreateForm form = new CreateForm();
        form.doc = new HashMap<>();
        form.doc.put("url", "https://example.com");
        form.doc.put("custom_field", "value");
        form.doc.put("_id", "abc123");
        form.doc.put("_version", "1");
        form.doc.put("_seq_no", "5");
        form.doc.put("_primary_term", "1");

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
        assertTrue(extraFieldNames.contains("custom_field"));
        assertFalse(extraFieldNames.contains("_id"), "Reserved field _id should be excluded");
        assertFalse(extraFieldNames.contains("_version"), "Reserved field _version should be excluded");
        assertFalse(extraFieldNames.contains("_seq_no"), "Reserved field _seq_no should be excluded");
        assertFalse(extraFieldNames.contains("_primary_term"), "Reserved field _primary_term should be excluded");
    }

    @Test
    public void test_registerExtraFields_configFieldsAppearedWithoutDoc() throws Exception {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexAdminArrayFields() {
                return "custom_tags";
            }

            @Override
            public String getIndexAdminDateFields() {
                return "custom_publish_date";
            }

            @Override
            public String getIndexAdminLongFields() {
                return "";
            }

            @Override
            public String getIndexAdminFloatFields() {
                return "";
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

            @Override
            public String getIndexFieldId() {
                return "_id";
            }

            @Override
            public String getIndexFieldVersion() {
                return "_version";
            }

            @Override
            public String getIndexFieldSeqNo() {
                return "_seq_no";
            }

            @Override
            public String getIndexFieldPrimaryTerm() {
                return "_primary_term";
            }
        });

        final AdminSearchlistAction action = new AdminSearchlistAction();

        // Empty doc (create mode) — no custom fields in document
        final CreateForm form = new CreateForm();
        form.doc = new HashMap<>();

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
        assertTrue(extraFieldNames.contains("custom_tags"), "Config-defined array field should appear even without doc data");
        assertTrue(extraFieldNames.contains("custom_publish_date"), "Config-defined date field should appear even without doc data");

        @SuppressWarnings("unchecked")
        final Map<String, String> extraFieldTypes = (Map<String, String>) renderData.getDataMap().get("extraFieldTypes");
        assertEquals("array", extraFieldTypes.get("custom_tags"));
        assertEquals("date", extraFieldTypes.get("custom_publish_date"));
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

            @Override
            public String getIndexFieldId() {
                return "_id";
            }

            @Override
            public String getIndexFieldVersion() {
                return "_version";
            }

            @Override
            public String getIndexFieldSeqNo() {
                return "_seq_no";
            }

            @Override
            public String getIndexFieldPrimaryTerm() {
                return "_primary_term";
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
    //                                          System-managed chunk fields / content read-only
    //                                                                           =========
    @Test
    public void test_registerExtraFields_excludesSystemManagedChunkFields() throws Exception {
        final AdminSearchlistAction action = new AdminSearchlistAction();

        final CreateForm form = new CreateForm();
        form.doc = new HashMap<>();
        form.doc.put("url", "https://example.com");
        form.doc.put("custom_field", "value");
        form.doc.put("content_chunk_vector", new ArrayList<>());
        form.doc.put("content_chunk_status", "done");
        form.doc.put("content_chunk_retry_count", 1);

        setCurrentFormAndConfig(action, form);
        final RenderData renderData = invokeRegisterExtraFields(action);

        @SuppressWarnings("unchecked")
        final List<String> extraFieldNames = (List<String>) renderData.getDataMap().get("extraFieldNames");
        assertNotNull(extraFieldNames);
        assertTrue(extraFieldNames.contains("custom_field"), "a normal custom field must still be editable");
        assertFalse(extraFieldNames.contains("content_chunk_vector"), "content_chunk_vector must be hidden from the editable walk");
        assertFalse(extraFieldNames.contains("content_chunk_status"), "content_chunk_status must be hidden from the editable walk");
        assertFalse(extraFieldNames.contains("content_chunk_retry_count"),
                "content_chunk_retry_count must be hidden from the editable walk");
    }

    @Test
    public void test_registerContentReadOnly_listContent_marksReadOnlyWithJoinedDisplay() throws Exception {
        final AdminSearchlistAction action = new AdminSearchlistAction();

        final CreateForm form = new CreateForm();
        form.doc = new HashMap<>();
        form.doc.put("url", "https://example.com");
        form.doc.put("content", List.of("chunk-a", "chunk-b"));

        setCurrentFormAndConfig(action, form);
        final RenderData renderData = invokeRegisterExtraFields(action);

        assertEquals("chunked (List) content must be rendered read-only", Boolean.TRUE, renderData.getDataMap().get("contentReadOnly"));
        assertEquals("read-only content must be joined for readable display, not rendered via List.toString()", "chunk-a\nchunk-b",
                renderData.getDataMap().get("contentDisplay"));
    }

    @Test
    public void test_registerContentReadOnly_stringContent_staysEditable() throws Exception {
        final AdminSearchlistAction action = new AdminSearchlistAction();

        final CreateForm form = new CreateForm();
        form.doc = new HashMap<>();
        form.doc.put("url", "https://example.com");
        form.doc.put("content", "a normal single string of content");

        setCurrentFormAndConfig(action, form);
        final RenderData renderData = invokeRegisterExtraFields(action);

        assertEquals("plain string content must remain editable", Boolean.FALSE, renderData.getDataMap().get("contentReadOnly"));
        assertNull(renderData.getDataMap().get("contentDisplay"), "no read-only display value should be produced for editable content");
    }

    @Test
    public void test_registerContentReadOnly_nullDoc_staysEditable() throws Exception {
        final AdminSearchlistAction action = new AdminSearchlistAction();

        final CreateForm form = new CreateForm();
        form.doc = null;

        setCurrentFormAndConfig(action, form);
        final RenderData renderData = invokeRegisterExtraFields(action);

        assertEquals("with no document (create mode) content must remain editable", Boolean.FALSE,
                renderData.getDataMap().get("contentReadOnly"));
    }

    // ===================================================================================
    //                                          stripSystemManagedFields (server-side enforcement)
    //                                                                           =========
    // The JSP's disabled/read-only rendering (registerContentReadOnly/registerExtraFields, tested
    // above) only prevents a normal browser form submit -- a direct POST or API call can still
    // smuggle these keys into the submitted map. These tests close that gap: first at the shared
    // helper level, then by driving the full create()/update() action methods (not just the
    // rendering helpers) to prove the strip is actually wired into the write path.

    @Test
    public void test_stripSystemManagedFields_removesSystemFieldsUnconditionally() {
        final Map<String, Object> entity = new HashMap<>(); // fresh entity: content is not a List
        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "https://example.com");
        doc.put(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD, new ArrayList<>());
        doc.put(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD, "done");
        doc.put(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD, 2);
        doc.put("content", "still editable when the fetched entity isn't already chunked");

        AdminSearchlistAction.stripSystemManagedFields(entity, doc);

        assertFalse(doc.containsKey(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD), "content_chunk_vector must always be stripped");
        assertFalse(doc.containsKey(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD), "content_chunk_status must always be stripped");
        assertFalse(doc.containsKey(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD),
                "content_chunk_retry_count must always be stripped");
        assertTrue(doc.containsKey("content"), "content must survive when the fetched entity is not already chunked");
        assertEquals("https://example.com", doc.get("url"));
    }

    @Test
    public void test_stripSystemManagedFields_alsoStripsContentWhenFetchedEntityIsChunked() {
        final Map<String, Object> entity = new HashMap<>();
        entity.put("content", List.of("chunk-a", "chunk-b")); // already-chunked signal
        final Map<String, Object> doc = new HashMap<>();
        doc.put("content", "attacker-supplied replacement content");
        doc.put(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD, "failed");

        AdminSearchlistAction.stripSystemManagedFields(entity, doc);

        assertFalse(doc.containsKey("content"), "content must be stripped when the FETCHED entity is already chunked");
        assertFalse(doc.containsKey(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
    }

    @Test
    public void test_stripSystemManagedFields_decisionKeyedOffFetchedEntityNotClientDoc() {
        // The client's OWN doc.content is a plain string (not a List) here -- confirming the strip
        // decision is keyed off the FETCHED entity's content, exactly as the review required, not
        // the (attacker-controlled) client-supplied value.
        final Map<String, Object> entity = new HashMap<>();
        entity.put("content", List.of("chunk-a"));
        final Map<String, Object> doc = new HashMap<>();
        doc.put("content", "a plain string, not a List");

        AdminSearchlistAction.stripSystemManagedFields(entity, doc);

        assertFalse(doc.containsKey("content"));
    }

    @Test
    public void test_stripSystemManagedFields_nullDoc_noop() {
        AdminSearchlistAction.stripSystemManagedFields(new HashMap<>(), null); // must not throw
    }

    // ===================================================================================
    //                                     update()/create() full action drive
    //                                                                           =========

    @Test
    public void test_update_alreadyChunkedDocument_stripsSystemFieldsAndPreservesChunkArray() throws Exception {
        final FessConfig testConfig = buildFullFessConfig();
        final FakeSearchEngineClient client = new FakeSearchEngineClient();
        final Map<String, Object> fetchedEntity = new HashMap<>();
        fetchedEntity.put("doc_id", "chunked-doc-1");
        fetchedEntity.put("_id", "chunked-doc-1-original-id");
        fetchedEntity.put("url", "https://example.com/chunked");
        fetchedEntity.put("content", new ArrayList<>(List.of("chunk-a", "chunk-b"))); // already-chunked signal
        fetchedEntity.put(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD, ContentChunkConstants.STATUS_DONE);
        fetchedEntity.put(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD, List.of(Map.of("vector", List.of(0.1, 0.2))));
        fetchedEntity.put("_seq_no", 5L);
        fetchedEntity.put("_primary_term", 1L);
        client.documentToReturn = fetchedEntity;

        final AdminSearchlistAction action = createInjectedAction(client, testConfig);
        mockTokenRequested(action.getClass());

        final EditForm form = new EditForm();
        form.crudMode = CrudMode.EDIT;
        form.doc = new HashMap<>();
        form.doc.put("doc_id", "chunked-doc-1");
        form.doc.put("url", "https://example.com/chunked");
        form.doc.put("title", "Title");
        form.doc.put("role", "Rguest");
        form.doc.put("boost", "1.0");
        // A direct POST/API call attempting to overwrite already-chunked content and smuggle
        // system-managed fields -- exactly what the JSP's disabled/hidden rendering cannot stop.
        form.doc.put("content", "attacker-supplied replacement content");
        form.doc.put(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD, "failed");
        form.doc.put(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD, new ArrayList<>());
        form.doc.put(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD, 99);
        form.q = "test-query";

        action.update(form);

        final Map<String, Object> stored = client.lastStoredDoc;
        assertNotNull(stored, "update() must have reached the store() call");
        org.junit.jupiter.api.Assertions.assertEquals(List.of("chunk-a", "chunk-b"), stored.get("content"),
                "the original chunk array must survive; the client's replacement string must be dropped");
        org.junit.jupiter.api.Assertions.assertEquals(ContentChunkConstants.STATUS_DONE,
                stored.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD),
                "the fetched entity's own status must survive; the client's smuggled value must be dropped");
        @SuppressWarnings("unchecked")
        final List<Object> storedVector = (List<Object>) stored.get(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD);
        assertFalse(storedVector.isEmpty(),
                "the fetched entity's own vector must survive; the client's smuggled empty list must be dropped");
        assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD),
                "the client's smuggled retry_count must never reach storage (the fetched entity had none either)");
    }

    @Test
    public void test_create_freshEntity_stripsSystemFieldsButKeepsClientContentString() throws Exception {
        final FessConfig testConfig = buildFullFessConfig();
        final FakeSearchEngineClient client = new FakeSearchEngineClient();
        // getDoc() for CrudMode.CREATE builds a brand-new empty entity and never fetches, so
        // client.documentToReturn is irrelevant here.

        final AdminSearchlistAction action = createInjectedAction(client, testConfig);
        mockTokenRequested(action.getClass());

        final CreateForm form = new CreateForm();
        form.crudMode = CrudMode.CREATE;
        form.doc = new HashMap<>();
        form.doc.put("url", "https://example.com/new");
        form.doc.put("title", "Title");
        form.doc.put("role", "Rguest");
        form.doc.put("boost", "1.0");
        form.doc.put("content", "a brand-new document's own content");
        // An attempt to smuggle system-managed fields into a brand-new document too.
        form.doc.put(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD, "done");
        form.doc.put(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD, List.of(Map.of("vector", List.of(9.9))));

        action.create(form);

        final Map<String, Object> stored = client.lastStoredDoc;
        assertNotNull(stored, "create() must have reached the store() call");
        org.junit.jupiter.api.Assertions.assertEquals("a brand-new document's own content", stored.get("content"),
                "a fresh (non-chunked) entity must keep the client's own content string");
        assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD),
                "system-managed fields must never be smuggled into a brand-new document either");
        assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD));
    }

    /**
     * Wires an {@link AdminSearchlistAction} through UTFlute's {@code inject()} (framework fields:
     * sessionManager/requestManager/etc., needed by {@code validate()}/{@code verifyToken()}/
     * {@code saveInfo()}/{@code redirectWith()}), then directly wires the fess-specific
     * collaborators that {@code fess.xml} (not loaded by the unit test container) would otherwise
     * provide: {@code systemHelper}, a single consistent {@code fessConfig} instance (used both via
     * this reflection-set field AND {@link ComponentUtil#setFessConfig}, since {@code getDoc()}/
     * {@code validateFields()} read the latter statically), and the fake {@code searchEngineClient}
     * (both as this action's {@code @Resource} field AND via {@link ComponentUtil#register}, since
     * {@code getDoc()} resolves it statically too). Mirrors {@code AdminThemeActionTest}'s
     * {@code createInjectedAction} pattern.
     */
    private AdminSearchlistAction createInjectedAction(final FakeSearchEngineClient client, final FessConfig testConfig) throws Exception {
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        final AdminSearchlistAction action = new AdminSearchlistAction();
        inject(action);

        final org.codelibs.fess.helper.SystemHelper systemHelperInstance = new org.codelibs.fess.helper.SystemHelper();
        final java.lang.reflect.Field sysField = org.codelibs.fess.app.web.base.FessBaseAction.class.getDeclaredField("systemHelper");
        sysField.setAccessible(true);
        if (sysField.get(action) == null) {
            sysField.set(action, systemHelperInstance);
        }
        // getDoc()'s CrudMode.CREATE branch resolves SystemHelper statically (ComponentUtil.getSystemHelper()),
        // not via this action's @Resource field -- register it there too.
        ComponentUtil.register(systemHelperInstance, "systemHelper");

        final java.lang.reflect.Field fessConfigField = org.codelibs.fess.app.web.base.FessBaseAction.class.getDeclaredField("fessConfig");
        fessConfigField.setAccessible(true);
        fessConfigField.set(action, testConfig);
        ComponentUtil.setFessConfig(testConfig);

        final java.lang.reflect.Field clientField = AdminSearchlistAction.class.getDeclaredField("searchEngineClient");
        clientField.setAccessible(true);
        clientField.set(action, client);
        ComponentUtil.register(client, "searchEngineClient");

        // create()/update() resolve CrawlingInfoHelper statically (ComponentUtil.getCrawlingInfoHelper())
        // to compute generateId(entity); its generateId() has no dependency beyond FessConfig, so a
        // plain instance is sufficient here.
        ComponentUtil.register(new org.codelibs.fess.helper.CrawlingInfoHelper(), "crawlingInfoHelper");

        return action;
    }

    /**
     * A {@link FessConfig.SimpleImpl} with every getter this test's exercised code paths
     * (update()/create(), getDoc(), validateFields(), convertToStorableDoc(), generateId()) touch
     * explicitly overridden -- an unoverridden {@code SimpleImpl} getter NPEs (it reads a
     * {@code null} backing {@code Properties} object), so this must be kept in sync with whatever
     * those production code paths read.
     */
    private FessConfig buildFullFessConfig() {
        return new FessConfig.SimpleImpl() {
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

            @Override
            public String getIndexFieldId() {
                return "_id";
            }

            @Override
            public String getIndexFieldVersion() {
                return "_version";
            }

            @Override
            public String getIndexFieldSeqNo() {
                return "_seq_no";
            }

            @Override
            public String getIndexFieldPrimaryTerm() {
                return "_primary_term";
            }

            @Override
            public String getIndexFieldDocId() {
                return "doc_id";
            }

            @Override
            public String getIndexFieldUrl() {
                return "url";
            }

            @Override
            public String getIndexFieldRole() {
                return "role";
            }

            @Override
            public String getIndexFieldVirtualHost() {
                return "virtual_host";
            }

            @Override
            public String getIndexDocumentUpdateIndex() {
                return "fess.update";
            }

            @Override
            public String getIndexIdDigestAlgorithm() {
                return "SHA-512";
            }
        };
    }

    /**
     * A capturing {@link SearchEngineClient} fake for driving {@code update()}/{@code create()}
     * end to end. {@link #delete} is a harmless no-op recorder: {@code update()}'s id-changed
     * branch (triggered whenever the test's placeholder {@code _id} doesn't match the real
     * generateId() hash) calls it, but this test's assertions only care about the final
     * {@link #store}d document, not the delete-of-the-old-id side effect.
     */
    private static final class FakeSearchEngineClient extends SearchEngineClient {
        Map<String, Object> documentToReturn;
        Map<String, Object> lastStoredDoc;
        int deleteCallCount = 0;

        @Override
        public OptionalEntity<Map<String, Object>> getDocument(final String index,
                final org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition<org.opensearch.action.search.SearchRequestBuilder> condition) {
            return documentToReturn == null ? OptionalEntity.empty() : OptionalEntity.of(new HashMap<>(documentToReturn));
        }

        @Override
        public boolean store(final String index, final Object obj) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> map = (Map<String, Object>) obj;
            lastStoredDoc = map;
            return true;
        }

        @Override
        public boolean delete(final String index, final String id, final Number seqNo, final Number primaryTerm) {
            deleteCallCount++;
            return true;
        }
    }

    // ===================================================================================
    //                                                                           Helpers
    //                                                                           =========
    private void setCurrentFormAndConfig(final AdminSearchlistAction action, final CreateForm form) throws Exception {
        final java.lang.reflect.Field currentFormField = AdminSearchlistAction.class.getDeclaredField("currentForm");
        currentFormField.setAccessible(true);
        currentFormField.set(action, form);

        final java.lang.reflect.Field fessConfigField = findField(action.getClass(), "fessConfig");
        fessConfigField.setAccessible(true);
        fessConfigField.set(action, ComponentUtil.getFessConfig());
    }

    private RenderData invokeRegisterExtraFields(final AdminSearchlistAction action) throws Exception {
        final java.lang.reflect.Method method = AdminSearchlistAction.class.getDeclaredMethod("registerExtraFields", RenderData.class);
        method.setAccessible(true);
        final RenderData renderData = new RenderData();
        method.invoke(action, renderData);
        return renderData;
    }

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
