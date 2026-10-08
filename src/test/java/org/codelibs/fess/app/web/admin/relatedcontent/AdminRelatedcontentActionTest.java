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
package org.codelibs.fess.app.web.admin.relatedcontent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.app.service.RelatedContentService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.helper.RelatedContentHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.opensearch.config.exentity.RelatedContent;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

/**
 * A regex pattern that cannot be compiled is skipped when the related contents are loaded, so the
 * admin screen refuses to save it.
 */
public class AdminRelatedcontentActionTest extends UnitFessTestCase {

    private RecordingService service;

    private AdminRelatedcontentAction createAction() throws Exception {
        suppressBindingOf(FessLoginAssist.class);
        suppressBindingOf(RelatedContentService.class);
        ComponentUtil.register(new RelatedContentHelper(), "relatedContentHelper");
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        service = new RecordingService();
        final AdminRelatedcontentAction action = new AdminRelatedcontentAction();
        inject(action);
        setField(FessBaseAction.class, action, "systemHelper", ComponentUtil.getSystemHelper());
        setField(AdminRelatedcontentAction.class, action, "relatedContentService", service);
        mockTokenRequested(action.getClass());
        return action;
    }

    private static void setField(final Class<?> type, final Object target, final String name, final Object value) throws Exception {
        final Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static CreateForm createForm(final String term) {
        final CreateForm form = new CreateForm();
        form.crudMode = CrudMode.CREATE;
        form.term = term;
        form.content = "<p>x</p>";
        form.sortOrder = 99;
        return form;
    }

    private static EditForm editForm(final String term) {
        final EditForm form = new EditForm();
        form.crudMode = CrudMode.EDIT;
        form.id = "id1";
        form.versionNo = 1;
        form.term = term;
        form.content = "<p>x</p>";
        form.sortOrder = 99;
        return form;
    }

    @Test
    public void test_create_unparsableRegexIsRefused() throws Exception {
        final AdminRelatedcontentAction action = createAction();

        assertValidationError(() -> action.create(createForm("regex:("))).handle(data -> {
            data.requiredMessageOf("term", "errors.relatedcontent_invalid_regex");
        });
        assertTrue(service.stored.isEmpty());
    }

    @Test
    public void test_create_parsableRegexIsStored() throws Exception {
        final AdminRelatedcontentAction action = createAction();

        action.create(createForm("regex:.*(cancel|退会).*"));

        assertEquals(1, service.stored.size());
        assertEquals("regex:.*(cancel|退会).*", service.stored.get(0).getTerm());
    }

    @Test
    public void test_create_plainTermIsStored() throws Exception {
        final AdminRelatedcontentAction action = createAction();

        // a "(" in a term that is not a regex term is just text
        action.create(createForm("a(b"));

        assertEquals(1, service.stored.size());
    }

    @Test
    public void test_update_unparsableRegexIsRefused() throws Exception {
        final AdminRelatedcontentAction action = createAction();

        assertValidationError(() -> action.update(editForm("regex:["))).handle(data -> {
            data.requiredMessageOf("term", "errors.relatedcontent_invalid_regex");
        });
        assertTrue(service.stored.isEmpty());
    }

    @Test
    public void test_validateRelatedContent() {
        ComponentUtil.register(new RelatedContentHelper(), "relatedContentHelper");

        FessMessages messages = new FessMessages();
        validate(messages, "regex:(");
        assertTrue(messages.hasMessageOf("term"));

        messages = new FessMessages();
        validate(messages, "regex:(cancel|退会)");
        assertFalse(messages.hasMessageOf("term"));

        messages = new FessMessages();
        validate(messages, "regex:");
        assertFalse(messages.hasMessageOf("term"));
    }

    private static void validate(final FessMessages messages, final String term) {
        AdminRelatedcontentAction.validateRelatedContent(createForm(term), messages);
    }

    /** Records what is stored instead of writing to the index. */
    static class RecordingService extends RelatedContentService {
        final List<RelatedContent> stored = new ArrayList<>();

        @Override
        public void store(final RelatedContent relatedContent) {
            stored.add(relatedContent);
        }
    }
}
