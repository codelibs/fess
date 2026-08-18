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
package org.codelibs.fess.app.web.api;

import org.codelibs.fess.app.web.api.ApiResult.ApiErrorResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.validation.VaMessenger;

import jakarta.servlet.http.HttpServletResponse;

public class FessApiActionTest extends UnitFessTestCase {

    private static final String UNAUTHORIZED_MESSAGE = "Unauthorized request.";

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        // the result bean stamps the product version through this helper
        ComponentUtil.register(new SystemHelper(), "systemHelper");
    }

    /**
     * Stands in for a concrete API action so that the prologue can be driven without a
     * container. The message lookup is stubbed out because the behaviour under test is
     * the response itself, not the wording of the message.
     */
    private static class TestApiAction extends FessApiAction {

        @Override
        protected boolean isAccessAllowed() {
            return false;
        }

        @Override
        protected String getMessage(final VaMessenger<FessMessages> validationMessagesLambda) {
            return UNAUTHORIZED_MESSAGE;
        }
    }

    private ActionResponse prologueOfDeniedRequest() {
        return new TestApiAction().godHandPrologue(null);
    }

    /**
     * A denied request must carry 401. Leaving the status unset makes the container send
     * 200, so a client that only inspects the HTTP status cannot tell a rejected call
     * from a successful one.
     */
    @Test
    public void test_godHandPrologue_deniedUsesUnauthorizedStatus() {
        final ActionResponse response = prologueOfDeniedRequest();

        assertTrue(response instanceof JsonResponse);
        final JsonResponse<?> jsonResponse = (JsonResponse<?>) response;
        assertTrue(jsonResponse.getHttpStatus().isPresent());
        assertEquals(Integer.valueOf(HttpServletResponse.SC_UNAUTHORIZED), jsonResponse.getHttpStatus().get());
    }

    /**
     * The response body is the published part of the contract, so the status code change
     * must leave it alone. Clients reading response.status keep seeing UNAUTHORIZED.
     */
    @Test
    public void test_godHandPrologue_deniedKeepsResponseBody() {
        final Object bean = ((JsonResponse<?>) prologueOfDeniedRequest()).getJsonBean();

        assertTrue(bean instanceof ApiResult);
        final ApiResult result = (ApiResult) bean;
        assertEquals(Status.UNAUTHORIZED.getId(), result.response.status);
        assertTrue(result.response instanceof ApiErrorResponse);
        assertEquals(UNAUTHORIZED_MESSAGE, ((ApiErrorResponse) result.response).message);
    }
}
