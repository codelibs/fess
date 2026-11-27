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
package org.codelibs.fess.app.web.admin.webconfig;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codelibs.fess.app.pager.WebConfigPager;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.app.service.RoleTypeService;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.opensearch.config.cbean.WebConfigCB;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lastaflute.core.message.UserMessage;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.servlet.session.SessionManager;

import jakarta.annotation.Resource;

/**
 * A list filter that the search engine cannot run is refused by the list query, which runs in the
 * {@code renderWith} callback of the list screen after the action method has returned. The list
 * screen is shown again with the error, and the filter does not stay in the pager of the session.
 */
public class AdminWebconfigActionTest extends UnitFessTestCase {

    /** "*xxx...*": the search engine refuses to determinize it. */
    private static final String TOO_COMPLEX = "x".repeat(400);

    @Resource
    private SessionManager sessionManager;

    private WebConfigPager pager;

    private ListQueryService service;

    private AdminWebconfigAction createAction() throws Exception {
        suppressBindingOf(FessLoginAssist.class);
        suppressBindingOf(WebConfigService.class);
        suppressBindingOf(ScheduledJobService.class);
        suppressBindingOf(RoleTypeService.class);
        suppressBindingOf(LabelTypeService.class);
        final AdminWebconfigAction action = new AdminWebconfigAction();
        inject(action);
        pager = new WebConfigPager();
        service = new ListQueryService();
        setField(AdminWebconfigAction.class, action, "webConfigPager", pager);
        setField(AdminWebconfigAction.class, action, "webConfigService", service);
        setField(FessBaseAction.class, action, "viewHelper", new ViewHelper());
        // the monologue resolves the login manager, which is not used for this exception
        setField(FessBaseAction.class, action, "fessLoginAssist", new FessLoginAssist());
        return action;
    }

    private static void setField(final Class<?> type, final Object target, final String name, final Object value) throws Exception {
        final Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static SearchForm searchForm(final String name) {
        final SearchForm form = new SearchForm();
        form.name = name;
        return form;
    }

    /** Runs the renderWith callbacks the way LastaFlute does after the action method. */
    private static void render(final HtmlResponse response) {
        final RenderData data = new RenderData();
        response.getRegistrationList().forEach(reg -> reg.register(data));
    }

    @Test
    public void test_search_tooComplexFilterIsRefusedAfterTheActionMethod() throws Exception {
        final AdminWebconfigAction action = createAction();

        // the action method itself returns: a try/catch in it would never see the exception
        final HtmlResponse response = action.search(searchForm(TOO_COMPLEX));

        assertEquals(TOO_COMPLEX, pager.name);
        Assertions.assertThrows(InvalidQueryException.class, () -> render(response));
    }

    @Test
    public void test_godHandMonologue_showsTheListWithTheErrorAndClearsTheFilter() throws Exception {
        final AdminWebconfigAction action = createAction();
        final HtmlResponse searched = action.search(searchForm(TOO_COMPLEX));
        final InvalidQueryException cause = Assertions.assertThrows(InvalidQueryException.class, () -> render(searched));
        final ActionRuntime runtime = new FailedRuntime("/admin/webconfig/search/");
        runtime.manageFailureCause(cause);

        final ActionResponse response = action.godHandMonologue(runtime);

        assertTrue("the list screen is shown instead of the error page", response.isDefined());
        final HtmlResponse html = (HtmlResponse) response;
        assertTrue(html.isForwardTo());
        assertEquals("/admin/webconfig/admin_webconfig.jsp", html.getRoutingPath());
        assertEquals(List.of("errors.invalid_query_parse_error"), messageKeys(sessionManager.errors().get().get()));
        // the filter is gone from the session, so the list (and every later visit to it) runs
        assertNull(pager.name);
        render(html);
        assertEquals(1, service.queried);
    }

    @Test
    public void test_godHandMonologue_leavesAnotherFailureAlone() throws Exception {
        final AdminWebconfigAction action = createAction();
        pager.name = "web";
        final ActionRuntime runtime = new FailedRuntime("/admin/webconfig/search/");
        runtime.manageFailureCause(new IllegalStateException("not a filter"));

        // undefined: LastaFlute rethrows the exception, which ends as the error page
        assertTrue(action.godHandMonologue(runtime).isUndefined());

        assertEquals("web", pager.name);
        assertFalse(sessionManager.errors().get().isPresent());
    }

    @Test
    public void test_search_aFilterTheSearchEngineCanRunIsKept() throws Exception {
        final AdminWebconfigAction action = createAction();

        render(action.search(searchForm("x".repeat(100))));

        assertEquals("x".repeat(100), pager.name);
        assertEquals(1, service.queried);
    }

    private static List<String> messageKeys(final UserMessages messages) {
        final List<String> keys = new ArrayList<>();
        for (final Iterator<UserMessage> ite = messages.silentAccessByIteratorOf(UserMessages.GLOBAL_PROPERTY_KEY); ite.hasNext();) {
            keys.add(ite.next().getMessageKey());
        }
        return keys;
    }

    /** Builds the list condition as the real service does, without the search engine. */
    private static class ListQueryService extends WebConfigService {
        int queried;

        @Override
        public List<WebConfig> getWebConfigList(final WebConfigPager webConfigPager) {
            setupListCondition(new WebConfigCB(), webConfigPager);
            queried++;
            return new ArrayList<>();
        }
    }

    /** A runtime of an HTML execute method, without the method itself. */
    private static class FailedRuntime extends ActionRuntime {
        FailedRuntime(final String requestPath) {
            super(requestPath, null, null);
        }

        @Override
        public boolean isApiExecute() {
            return false;
        }

        @Override
        public String toString() {
            return "runtime:{" + getRequestPath() + "}";
        }
    }
}
