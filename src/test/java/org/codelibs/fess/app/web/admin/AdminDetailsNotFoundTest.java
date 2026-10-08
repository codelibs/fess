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
package org.codelibs.fess.app.web.admin;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.codelibs.fess.app.service.RoleTypeService;
import org.codelibs.fess.app.service.StopwordsService;
import org.codelibs.fess.app.service.TagTypeService;
import org.codelibs.fess.app.web.admin.dict.stopwords.AdminDictStopwordsAction;
import org.codelibs.fess.app.web.admin.tagtype.AdminTagtypeAction;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.response.HtmlResponse;

/**
 * The details pages answer an unknown id with the list and the message "the data could not be
 * found", not with HTTP 500.
 *
 * <p>LastaFlute creates the form given to {@code useForm} while the response is written, after the
 * action method has returned, and reports anything thrown by the setup of the form as a failure to
 * create the form. The id therefore has to be looked up, and the validation error thrown, by the
 * action method itself.</p>
 */
public class AdminDetailsNotFoundTest extends UnitFessTestCase {

    private static final Path ADMIN_DIR = Paths.get("src/main/java/org/codelibs/fess/app/web/admin");

    private static final Pattern SETUP = Pattern.compile("op\\.setup\\(");

    @Test
    public void test_noActionThrowsAValidationErrorInTheSetupOfAForm() throws Exception {
        final List<String> inSetup = new ArrayList<>();
        int setups = 0;
        try (Stream<Path> paths = Files.walk(ADMIN_DIR)) {
            for (final Path java : paths.filter(p -> p.getFileName().toString().matches("Admin\\w+Action\\.java")).toList()) {
                final String source = Files.readString(java, StandardCharsets.UTF_8);
                final Matcher matcher = SETUP.matcher(source);
                while (matcher.find()) {
                    setups++;
                    final int open = source.indexOf('{', matcher.end());
                    final int close = closingBrace(source, open);
                    if (source.substring(open, close).contains("throwValidationError")) {
                        inSetup.add(java.getFileName() + ":" + (source.substring(0, matcher.start()).split("\n", -1).length));
                    }
                }
            }
        }
        assertTrue("no form setup found under " + ADMIN_DIR, setups > 50);
        assertEquals("throwValidationError belongs in the action method, not in the setup of a form: " + inSetup, 0, inSetup.size());
    }

    @Test
    public void test_tagtypeDetailsThrowsTheValidationErrorForAnUnknownId() throws Exception {
        final AdminTagtypeAction action = injected(new AdminTagtypeAction(), TagTypeService.class, RoleTypeService.class);
        setField(action, "tagTypeService", new TagTypeService() {
            @Override
            public OptionalEntity<TagType> getTagType(final String id) {
                return OptionalEntity.empty();
            }
        });

        assertValidationError(() -> action.details(4, "nosuchid")).handle(data -> {
            data.requiredMessageOf("_global", "errors.crud_could_not_find_crud_table");
        });
    }

    @Test
    public void test_tagtypeDetailsShowsAKnownId() throws Exception {
        final AdminTagtypeAction action = injected(new AdminTagtypeAction(), TagTypeService.class, RoleTypeService.class);
        setField(action, "tagTypeService", new TagTypeService() {
            @Override
            public OptionalEntity<TagType> getTagType(final String id) {
                final TagType tagType = new TagType();
                tagType.setName("known");
                return OptionalEntity.of(tagType);
            }
        });

        final HtmlResponse response = action.details(4, "known");

        assertNotNull(response);
        assertTrue(response.isForwardTo());
    }

    @Test
    public void test_dictionaryDetailsThrowsTheValidationErrorForAnUnknownItem() throws Exception {
        final AdminDictStopwordsAction action = injected(new AdminDictStopwordsAction(), StopwordsService.class);
        setField(action, "stopwordsService", new StopwordsService() {
            @Override
            public OptionalEntity<org.codelibs.fess.dict.stopwords.StopwordsItem> getStopwordsItem(final String dictId, final long id) {
                return OptionalEntity.empty();
            }
        });

        assertValidationError(() -> action.details("dict1", 4, 123L)).handle(data -> {
            data.requiredMessageOf("_global", "errors.crud_could_not_find_crud_table");
        });
    }

    private <ACTION extends FessAdminAction> ACTION injected(final ACTION action, final Class<?>... servicesToStub) throws Exception {
        // these need a search engine behind them; the tests wire stubs of the ones they use
        suppressBindingOf(FessLoginAssist.class);
        for (final Class<?> service : servicesToStub) {
            suppressBindingOf(service);
        }
        inject(action);
        // systemHelper is defined in fess.xml, which the unit test container does not load; the validator needs it
        final Field systemHelper = FessBaseAction.class.getDeclaredField("systemHelper");
        systemHelper.setAccessible(true);
        if (systemHelper.get(action) == null) {
            systemHelper.set(action, new SystemHelper());
        }
        return action;
    }

    private static void setField(final Object target, final String name, final Object value) throws Exception {
        final Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static int closingBrace(final String source, final int open) {
        int depth = 0;
        for (int i = open; i < source.length(); i++) {
            if (source.charAt(i) == '{') {
                depth++;
            } else if (source.charAt(i) == '}' && --depth == 0) {
                return i;
            }
        }
        throw new IllegalStateException("no closing brace for " + open);
    }
}
