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
package org.codelibs.fess.app.web.api.admin.relatedcontent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.app.service.AccessTokenService;
import org.codelibs.fess.app.service.RelatedContentService;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.helper.RelatedContentHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.RelatedContent;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

/**
 * A regex pattern that cannot be compiled is skipped when the related contents are loaded, so the
 * admin API refuses to save it.
 */
public class ApiAdminRelatedcontentActionTest extends UnitFessTestCase {

    private RecordingService service;

    private ApiAdminRelatedcontentAction createAction() throws Exception {
        suppressBindingOf(FessLoginAssist.class);
        suppressBindingOf(RelatedContentService.class);
        suppressBindingOf(AccessTokenService.class);
        ComponentUtil.register(new RelatedContentHelper(), "relatedContentHelper");
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        service = new RecordingService();
        final ApiAdminRelatedcontentAction action = new ApiAdminRelatedcontentAction();
        inject(action);
        setField(FessBaseAction.class, action, "systemHelper", ComponentUtil.getSystemHelper());
        setField(ApiAdminRelatedcontentAction.class, action, "relatedContentService", service);
        return action;
    }

    private static void setField(final Class<?> type, final Object target, final String name, final Object value) throws Exception {
        final Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static CreateBody createBody(final String term) {
        final CreateBody body = new CreateBody();
        body.term = term;
        body.content = "<p>x</p>";
        body.sortOrder = 99;
        return body;
    }

    private static EditBody editBody(final String term) {
        final EditBody body = new EditBody();
        body.id = "id1";
        body.versionNo = 1;
        body.term = term;
        body.content = "<p>x</p>";
        body.sortOrder = 99;
        return body;
    }

    @Test
    public void test_post_unparsableRegexIsRefused() throws Exception {
        final ApiAdminRelatedcontentAction action = createAction();

        assertValidationError(() -> action.post$setting(createBody("regex:("))).handle(data -> {
            data.requiredMessageOf("term", "errors.relatedcontent_invalid_regex");
        });
        assertTrue(service.stored.isEmpty());
    }

    @Test
    public void test_post_parsableRegexIsStored() throws Exception {
        final ApiAdminRelatedcontentAction action = createAction();

        action.post$setting(createBody("regex:.*(cancel|退会).*"));

        assertEquals(1, service.stored.size());
        assertEquals("regex:.*(cancel|退会).*", service.stored.get(0).getTerm());
    }

    @Test
    public void test_put_unparsableRegexIsRefused() throws Exception {
        final ApiAdminRelatedcontentAction action = createAction();

        assertValidationError(() -> action.put$setting(editBody("regex:["))).handle(data -> {
            data.requiredMessageOf("term", "errors.relatedcontent_invalid_regex");
        });
        assertTrue(service.stored.isEmpty());
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
