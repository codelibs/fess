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
package org.codelibs.fess.app.web.sso;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.entity.RequestParameter;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;

import org.apache.logging.log4j.Level;
import org.codelibs.fess.exception.SsoMessageException;
import org.codelibs.fess.exception.SsoProcessException;
import org.codelibs.fess.exception.SsoStateException;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.sso.SsoManager;
import org.codelibs.fess.sso.SsoResponseType;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.UrlChain;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.validation.VaMessenger;

import jakarta.servlet.http.HttpServletResponse;

public class SsoActionTest extends UnitFessTestCase {

    /** Marker returned instead of the real redirect, which needs the action path resolver. */
    private static final HtmlResponse REDIRECT_TO_LOGIN = HtmlResponse.fromRedirectPathAsIs("/login/");

    /**
     * Registers an SsoManager whose responses always fail with the given exception, and returns
     * an action wired to it.
     */
    private TestableSsoAction createAction(final SsoMessageException failure) {
        ComponentUtil.register(new SsoManager() {
            @Override
            public ActionResponse getResponse(final SsoResponseType responseType) {
                throw failure;
            }
        }, "ssoManager");
        return new TestableSsoAction();
    }

    private static SsoMessageException messageException(final String msg, final Throwable cause) {
        return new SsoMessageException(messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, msg),
                "Failed.", cause);
    }

    // ===================================================================================
    //                                                                            metadata
    //                                                                            ========

    @Test
    public void test_metadata_failureIsNotReportedAsAnHtmlLoginPage() throws Exception {
        // the IdP and automation fetch this endpoint; a redirect to the human login page would
        // report a misconfigured SP as 302 -> 200 text/html
        final TestableSsoAction action = createAction(messageException("sp_acs_not_found", new SsoProcessException("sp_acs_not_found")));

        final ActionResponse response = action.metadata();

        assertTrue("no HTTP status was set: " + response, response.getHttpStatus().isPresent());
        assertEquals(Integer.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), response.getHttpStatus().get());
        assertTrue(String.valueOf(response), response instanceof HtmlResponse);
        assertTrue(((HtmlResponse) response).isReturnAsEmptyBody());
        assertTrue(action.savedErrors + "/" + action.savedInfos, action.savedErrors.isEmpty() && action.savedInfos.isEmpty());
    }

    @Test
    public void test_metadata_informationalMessageStillRedirects() throws Exception {
        // an SsoMessageException without a cause carries information for a human, not a failure
        final TestableSsoAction action = createAction(messageException("info", null));

        final ActionResponse response = action.metadata();

        assertSame(REDIRECT_TO_LOGIN, response);
        assertEquals(1, action.savedInfos.size());
        assertTrue(action.savedErrors.toString(), action.savedErrors.isEmpty());
    }

    // ===================================================================================
    //                                                                              logout
    //                                                                              ======

    @Test
    public void test_logout_rejectedRequestIsLoggedWithoutAStackTrace() throws Exception {
        // /sso/logout is anonymous, so a request that is not a logout callback must not cost a
        // stack trace: an unauthenticated client could otherwise fill the log at will
        final TestableSsoAction action =
                createAction(messageException("not a logout callback", new SsoStateException("not a logout callback")));
        final LogCapturingAppender appender = LogCapturingAppender.attach(SsoAction.class);
        try {
            final ActionResponse response = action.logout();

            assertSame(REDIRECT_TO_LOGIN, response);
            assertEquals(1, appender.eventsAt(Level.WARN).size());
            assertNull(appender.eventsAt(Level.WARN).get(0).getThrown(), "the rejected request must not be logged with a stack trace");
            assertTrue(appender.warnings().get(0), appender.warnings().get(0).contains("not a logout callback"));
            assertEquals(1, action.savedErrors.size());
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_logout_genuineFailureKeepsItsStackTrace() throws Exception {
        final TestableSsoAction action = createAction(messageException("boom", new SsoProcessException("boom")));
        final LogCapturingAppender appender = LogCapturingAppender.attach(SsoAction.class);
        try {
            final ActionResponse response = action.logout();

            assertSame(REDIRECT_TO_LOGIN, response);
            assertEquals(1, appender.eventsAt(Level.WARN).size());
            assertNotNull(appender.eventsAt(Level.WARN).get(0).getThrown(), "a genuine failure must keep its stack trace");
            assertEquals(1, action.savedErrors.size());
        } finally {
            appender.detach();
        }
    }

    // ===================================================================================
    //                                                       Restoring the stored search query
    //                                                       =================================

    /**
     * Builds an action whose stored search parameters are exactly the given ones.
     */
    private TestableSsoAction actionWithStoredParameters(final RequestParameter... parameters) {
        return actionWithStoredParameters(4096, parameters);
    }

    // ===================================================================================
    //                                                                               index
    //                                                                               =====

    @Test
    public void test_index_keepsTheSearchAndRestartsOnTheBareEndpoint() {
        final List<String> calls = new CopyOnWriteArrayList<>();
        registerSsoManagerRecording(calls);
        final TestableSsoAction action = new TestableSsoAction();
        action.anonymous(calls, true);

        action.index();

        assertEquals("the handshake must not start on a URL carrying the query", List.of("store"), calls);
        assertEquals(List.of(SsoAction.class), action.redirectTargets);
    }

    @Test
    public void test_index_withoutASearchStartsTheHandshake() {
        final List<String> calls = new CopyOnWriteArrayList<>();
        registerSsoManagerRecording(calls);
        final TestableSsoAction action = new TestableSsoAction();
        action.anonymous(calls, false);

        action.index();

        assertEquals(List.of("credential"), calls);
    }

    @Test
    public void test_index_loggedInWithNothingStoredLandsWhereALoginWould() {
        final TestableSsoAction action = actionWithStoredParameters();
        action.loggedIn();

        action.index();

        assertEquals(1, action.landingPages, "the second leg of a POST login must land as the login would");
        assertEquals(List.of(), action.redirectTargets);
    }

    @Test
    public void test_index_loggedInRestoresTheStoredSearch() {
        final TestableSsoAction action = actionWithStoredParameters(new RequestParameter("q", new String[] { "saml" }));
        action.loggedIn();

        action.index();

        assertEquals(List.of("q", "saml"), action.redirectParams);
        assertEquals(0, action.landingPages);
    }

    @Test
    public void test_redirectAfterLogin_postWithNothingStoredFinishesOnAGet() {
        // The SAML HTTP-POST binding: the IdP posts cross-site, so the SameSite=Lax cookie holding
        // the search is not on this request even when it exists.
        final TestableSsoAction action = actionWithStoredParameters();
        action.post = true;

        action.redirectAfterLogin();

        assertEquals(List.of(SsoAction.class), action.redirectTargets);
        assertEquals(0, action.landingPages);
    }

    @Test
    public void test_redirectAfterLogin_getWithNothingStoredLandsByUser() {
        // A login completed on a GET (SPNEGO, an OpenID Connect callback) already carried the
        // cookie, so there is nothing more to wait for.
        final TestableSsoAction action = actionWithStoredParameters();

        action.redirectAfterLogin();

        assertEquals(List.of(), action.redirectTargets);
        assertEquals(1, action.landingPages);
    }

    @Test
    public void test_redirectAfterLogin_postThatCarriesTheSearchRestoresItAtOnce() {
        final TestableSsoAction action = actionWithStoredParameters(new RequestParameter("q", new String[] { "saml" }));
        action.post = true;

        action.redirectAfterLogin();

        assertEquals(List.of("q", "saml"), action.redirectParams);
        assertEquals(List.of(), action.redirectTargets);
    }

    private static void registerSsoManagerRecording(final List<String> calls) {
        ComponentUtil.register(new SsoManager() {
            @Override
            public LoginCredential getLoginCredential() {
                calls.add("credential");
                return null;
            }

            @Override
            public boolean available() {
                return false;
            }
        }, "ssoManager");
    }

    /** @param maxRestoredLength the value of cookie.search.parameter.max.restored.length */
    private TestableSsoAction actionWithStoredParameters(final int maxRestoredLength, final RequestParameter... parameters) {
        final TestableSsoAction action = new TestableSsoAction();
        action.storeParameters(maxRestoredLength, parameters);
        return action;
    }

    @Test
    public void test_redirectToSearchPage_restoresAQueryThatFits() {
        final TestableSsoAction action = actionWithStoredParameters(new RequestParameter("q", new String[] { "kerberos" }));

        assertTrue(action.redirectToSearchPage().isPresent());
        assertEquals(List.of("q", "kerberos"), action.redirectParams);
    }

    @Test
    public void test_redirectToSearchPage_dropsAQueryTooLongToRedirectWith() {
        // cookie.search.parameter.max.length bounds the COMPRESSED cookie, so it is no bound at
        // all on the URL built from it: percent-encoding a CJK query multiplies its length by
        // nine. A query the search page accepts can therefore produce a Location header the
        // container refuses to write, and the login answers 500 instead of succeeding.
        final StringBuilder cjk = new StringBuilder();
        while (cjk.length() < 850) {
            cjk.append('\u691c');
        }
        final TestableSsoAction action = actionWithStoredParameters(new RequestParameter("q", new String[] { cjk.toString() }));

        assertFalse(action.redirectToSearchPage().isPresent(), "the login must not be lost to the query it was going to restore");
    }

    @Test
    public void test_redirectToSearchPage_dropsTheWholeRestoreRatherThanHalfOfIt() {
        // A partial restore would run a DIFFERENT search from the one the user asked for -- the
        // paging and sorting parameters would be gone while q stayed. Nothing is restored.
        final StringBuilder cjk = new StringBuilder();
        while (cjk.length() < 850) {
            cjk.append('\u691c');
        }
        final TestableSsoAction action = actionWithStoredParameters(new RequestParameter("q", new String[] { cjk.toString() }),
                new RequestParameter("num", new String[] { "20" }));

        assertFalse(action.redirectToSearchPage().isPresent());
        assertTrue(action.redirectParams.isEmpty());
    }

    @Test
    public void test_redirectToSearchPage_honoursTheConfiguredBound() {
        // The bound is a configuration key because the container bound it protects against is one
        // too (tomcat.maxHttpHeaderSize). A deployment that raised that has to be able to raise
        // this, or the query it can now carry is still dropped.
        final StringBuilder cjk = new StringBuilder();
        while (cjk.length() < 850) {
            cjk.append('\u691c');
        }
        final RequestParameter q = new RequestParameter("q", new String[] { cjk.toString() });

        assertFalse(actionWithStoredParameters(4096, q).redirectToSearchPage().isPresent(), "the shipped bound drops it");
        assertTrue(actionWithStoredParameters(65536, q).redirectToSearchPage().isPresent(), "a raised bound restores it");
    }

    @Test
    public void test_redirectToSearchPage_fallsBackWhenTheBoundIsBlank() {
        // getAsInteger answers null for a blank value, and this is read on the login path: a blank
        // key must fall back to the shipped default, not throw.
        final TestableSsoAction action = new TestableSsoAction();
        action.storeParametersWithNoConfiguredBound(new RequestParameter("q", new String[] { "kerberos" }));

        assertTrue(action.redirectToSearchPage().isPresent());
    }

    @Test
    public void test_redirectToSearchPage_encodesTheNameAsWellAsTheValue() {
        // Both halves reach this point from a client-supplied cookie. Encoding only the value
        // leaves an '&' in the name splitting into a query parameter of its own once the redirect
        // URL is assembled.
        final TestableSsoAction action = actionWithStoredParameters(new RequestParameter("q&injected=1&x", new String[] { "ok" }));

        assertTrue(action.redirectToSearchPage().isPresent());
        assertEquals(List.of("q%26injected%3D1%26x", "ok"), action.redirectParams);
    }

    @Test
    public void test_redirectToSearchPage_leavesAnOrdinaryNameReadable() {
        // The falsification: encoding must not disturb the names the mechanism actually stores.
        final TestableSsoAction action = actionWithStoredParameters(new RequestParameter("num", new String[] { "20" }));

        assertTrue(action.redirectToSearchPage().isPresent());
        assertEquals(List.of("num", "20"), action.redirectParams);
    }

    @Test
    public void test_redirectToSearchPage_withNothingStored() {
        assertFalse(actionWithStoredParameters().redirectToSearchPage().isPresent());
    }

    /**
     * SsoAction with the seams that need the DI container replaced: the message stores and the
     * action path resolver are not available to a plain unit test.
     */
    private static class TestableSsoAction extends SsoAction {
        final List<String> savedErrors = new CopyOnWriteArrayList<>();
        final List<String> savedInfos = new CopyOnWriteArrayList<>();
        final List<Object> redirectParams = new CopyOnWriteArrayList<>();

        @Override
        protected HtmlResponse redirectWith(final Class<?> actionType, final UrlChain moreUrl) {
            redirectParams.addAll(List.of(moreUrl.getParamsOnGet()));
            return REDIRECT_TO_LOGIN;
        }

        /** searchHelper and fessConfig are protected in another package, so they are set here. */
        void storeParameters(final int maxRestoredLength, final RequestParameter... parameters) {
            searchHelper = new SearchHelper() {
                @Override
                public RequestParameter[] getSearchParameters() {
                    return parameters;
                }
            };
            fessConfig = new FessConfig.SimpleImpl() {
                private static final long serialVersionUID = 1L;

                @Override
                public Integer getCookieSearchParameterMaxRestoredLengthAsInteger() {
                    return maxRestoredLength;
                }
            };
        }

        /** The same seams, with the bound left blank so the fallback is what answers. */
        void storeParametersWithNoConfiguredBound(final RequestParameter... parameters) {
            storeParameters(4096, parameters);
            fessConfig = new FessConfig.SimpleImpl() {
                private static final long serialVersionUID = 1L;

                @Override
                public Integer getCookieSearchParameterMaxRestoredLengthAsInteger() {
                    return null;
                }
            };
        }

        @Override
        protected void saveError(final VaMessenger<FessMessages> validationMessagesLambda) {
            savedErrors.add(String.valueOf(validationMessagesLambda));
        }

        @Override
        protected void saveInfo(final VaMessenger<FessMessages> validationMessagesLambda) {
            savedInfos.add(String.valueOf(validationMessagesLambda));
        }

        final List<Class<?>> redirectTargets = new CopyOnWriteArrayList<>();

        @Override
        protected HtmlResponse redirect(final Class<?> actionType) {
            redirectTargets.add(actionType);
            return REDIRECT_TO_LOGIN;
        }

        boolean post;

        int landingPages;

        @Override
        protected boolean isPostRequest() {
            return post;
        }

        @Override
        protected HtmlResponse getHtmlResponse() {
            landingPages++;
            return REDIRECT_TO_LOGIN;
        }

        /** A saved user; what it is does not matter to where it lands here. */
        void loggedIn() {
            fessLoginAssist = new FessLoginAssist() {
                @Override
                public OptionalThing<FessUserBean> getSavedUserBean() {
                    return OptionalThing.of(new FessUserBean(null));
                }
            };
        }

        /** No saved user, and a search helper that records when the query is stored. */
        void anonymous(final List<String> calls, final boolean carriesSearch) {
            fessLoginAssist = new FessLoginAssist() {
                @Override
                public OptionalThing<FessUserBean> getSavedUserBean() {
                    return OptionalThing.empty();
                }
            };
            searchHelper = new SearchHelper() {
                @Override
                public boolean hasRequiredSearchParameters() {
                    return carriesSearch;
                }

                @Override
                public void storeSearchParameters() {
                    calls.add("store");
                }
            };
        }
    }
}
