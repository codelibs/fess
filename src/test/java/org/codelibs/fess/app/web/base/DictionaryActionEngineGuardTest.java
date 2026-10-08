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
package org.codelibs.fess.app.web.base;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codelibs.fess.app.web.admin.dict.AdminDictAction;
import org.codelibs.fess.app.web.admin.dict.kuromoji.AdminDictKuromojiAction;
import org.codelibs.fess.app.web.admin.dict.mapping.AdminDictMappingAction;
import org.codelibs.fess.app.web.admin.dict.protwords.AdminDictProtwordsAction;
import org.codelibs.fess.app.web.admin.dict.stemmeroverride.AdminDictStemmeroverrideAction;
import org.codelibs.fess.app.web.admin.dict.stopwords.AdminDictStopwordsAction;
import org.codelibs.fess.app.web.admin.dict.synonym.AdminDictSynonymAction;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.app.web.api.admin.dict.ApiAdminDictAction;
import org.codelibs.fess.app.web.api.admin.dict.kuromoji.ApiAdminDictKuromojiAction;
import org.codelibs.fess.app.web.api.admin.dict.mapping.ApiAdminDictMappingAction;
import org.codelibs.fess.app.web.api.admin.dict.protwords.ApiAdminDictProtwordsAction;
import org.codelibs.fess.app.web.api.admin.dict.stemmeroverride.ApiAdminDictStemmeroverrideAction;
import org.codelibs.fess.app.web.api.admin.dict.stopwords.ApiAdminDictStopwordsAction;
import org.codelibs.fess.app.web.api.admin.dict.synonym.ApiAdminDictSynonymAction;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.helper.ActivityHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.LastaAction;
import org.lastaflute.web.path.ActionPathResolver;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

/**
 * Tests that every dictionary screen and API, which exist only for the CodeLibs plugins of the
 * search engine, turns requests away when the search engine runs without them.
 */
public class DictionaryActionEngineGuardTest extends UnitFessTestCase {

    private static final List<Class<? extends FessAdminAction>> HTML_ACTIONS =
            List.of(AdminDictAction.class, AdminDictKuromojiAction.class, AdminDictMappingAction.class, AdminDictProtwordsAction.class,
                    AdminDictStemmeroverrideAction.class, AdminDictStopwordsAction.class, AdminDictSynonymAction.class);

    private static final List<Class<? extends FessApiAdminAction>> API_ACTIONS = List.of(ApiAdminDictAction.class,
            ApiAdminDictKuromojiAction.class, ApiAdminDictMappingAction.class, ApiAdminDictProtwordsAction.class,
            ApiAdminDictStemmeroverrideAction.class, ApiAdminDictStopwordsAction.class, ApiAdminDictSynonymAction.class);

    private static final Path HTML_SOURCE_DIR = Paths.get("src/main/java/org/codelibs/fess/app/web/admin/dict");

    private static final Path API_SOURCE_DIR = Paths.get("src/main/java/org/codelibs/fess/app/web/api/admin/dict");

    @Test
    public void test_everyDictionaryActionIsCovered() throws IOException {
        // a new dictionary action must be added to the lists above, and so to the guard
        assertEquals(classNamesUnder(HTML_SOURCE_DIR),
                HTML_ACTIONS.stream().map(Class::getName).collect(Collectors.toCollection(TreeSet::new)));
        assertEquals(classNamesUnder(API_SOURCE_DIR),
                API_ACTIONS.stream().map(Class::getName).collect(Collectors.toCollection(TreeSet::new)));
    }

    @Test
    public void test_htmlActions_redirectToTheAdminTopWhenThereAreNoPlugins() throws Exception {
        for (final Class<? extends FessAdminAction> actionClass : HTML_ACTIONS) {
            for (final String type : new String[] { "vanilla", "aws", "cloud" }) {
                final ActionResponse response = hookBeforeOfHtmlAction(actionClass, type);

                final String name = actionClass.getSimpleName() + "/" + type;
                assertTrue(name, response instanceof HtmlResponse);
                assertTrue(name, ((HtmlResponse) response).isRedirectTo());
                assertEquals(name, "/admin/", ((HtmlResponse) response).getRoutingPath());
            }
        }
    }

    @Test
    public void test_htmlActions_runWhenThePluginsAreAvailable() throws Exception {
        for (final Class<? extends FessAdminAction> actionClass : HTML_ACTIONS) {
            assertTrue(actionClass.getSimpleName(), hookBeforeOfHtmlAction(actionClass, "default").isUndefined());
        }
    }

    @Test
    public void test_apiActions_answerNotFoundWhenThereAreNoPlugins() throws Exception {
        for (final Class<? extends FessApiAdminAction> actionClass : API_ACTIONS) {
            for (final String type : new String[] { "vanilla", "aws", "cloud" }) {
                final ActionResponse response = hookBeforeOfApiAction(actionClass, type);

                final String name = actionClass.getSimpleName() + "/" + type;
                assertTrue(name, response instanceof JsonResponse);
                assertEquals(name, Integer.valueOf(404), ((JsonResponse<?>) response).getHttpStatus().get());
            }
        }
    }

    @Test
    public void test_apiActions_runWhenThePluginsAreAvailable() throws Exception {
        for (final Class<? extends FessApiAdminAction> actionClass : API_ACTIONS) {
            assertTrue(actionClass.getSimpleName(), hookBeforeOfApiAction(actionClass, "default").isUndefined());
        }
    }

    private ActionResponse hookBeforeOfHtmlAction(final Class<? extends FessAdminAction> actionClass, final String type) throws Exception {
        final FessAdminAction action = actionClass.getDeclaredConstructor().newInstance();
        // the field that resolves the redirect; injecting the whole action would need the dictionary services
        final Field resolverField = LastaAction.class.getDeclaredField("actionPathResolver");
        resolverField.setAccessible(true);
        resolverField.set(action, getComponent(ActionPathResolver.class));
        wire(action, type);
        return action.hookBefore(new GuardActionRuntime());
    }

    private ActionResponse hookBeforeOfApiAction(final Class<? extends FessApiAdminAction> actionClass, final String type)
            throws Exception {
        ComponentUtil.register(new SystemHelper(), "systemHelper"); // ApiResult reads the product version
        final FessApiAdminAction action = actionClass.getDeclaredConstructor().newInstance();
        wire(action, type);
        return action.hookBefore(new GuardActionRuntime());
    }

    private static void wire(final FessBaseAction action, final String type) {
        action.fessLoginAssist = new FessLoginAssist() {
            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.empty();
            }
        };
        action.activityHelper = new ActivityHelper() {
            @Override
            public void access(final OptionalThing<FessUserBean> user, final String path, final String execute) {
                // not audited here
            }
        };
        action.viewHelper = new ViewHelper();
        action.fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getSearchEngineType() {
                return type;
            }
        };
    }

    private static Set<String> classNamesUnder(final Path sourceDir) throws IOException {
        final String prefix = Paths.get("src/main/java").toString() + "/";
        try (Stream<Path> files = Files.walk(sourceDir)) {
            return files.map(Path::toString)
                    .filter(name -> name.endsWith("Action.java"))
                    .map(name -> name.substring(prefix.length(), name.length() - ".java".length()).replace('/', '.'))
                    .collect(Collectors.toCollection(TreeSet::new));
        }
    }

    static class GuardActionRuntime extends ActionRuntime {

        private final Method executeMethod;

        GuardActionRuntime() {
            super("/admin/dict/", null, null);
            try {
                executeMethod = DummyAction.class.getMethod("index");
            } catch (final NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public Method getExecuteMethod() {
            return executeMethod;
        }
    }

    public static class DummyAction {
        public void index() {
        }
    }
}
