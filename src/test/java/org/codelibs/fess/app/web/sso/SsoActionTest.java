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
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.codelibs.fess.exception.SsoMessageException;
import org.codelibs.fess.exception.SsoProcessException;
import org.codelibs.fess.exception.SsoStateException;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.sso.SsoManager;
import org.codelibs.fess.sso.SsoResponseType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.lastaflute.core.message.UserMessages;
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
            assertEquals(1, appender.warnings().size());
            assertNull(appender.warnings().get(0).getThrown(), "the rejected request must not be logged with a stack trace");
            assertTrue(appender.warningMessages().get(0), appender.warningMessages().get(0).contains("not a logout callback"));
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
            assertEquals(1, appender.warnings().size());
            assertNotNull(appender.warnings().get(0).getThrown(), "a genuine failure must keep its stack trace");
            assertEquals(1, action.savedErrors.size());
        } finally {
            appender.detach();
        }
    }

    /**
     * SsoAction with the seams that need the DI container replaced: the message stores and the
     * action path resolver are not available to a plain unit test.
     */
    private static class TestableSsoAction extends SsoAction {
        final List<String> savedErrors = new CopyOnWriteArrayList<>();
        final List<String> savedInfos = new CopyOnWriteArrayList<>();

        @Override
        protected void saveError(final VaMessenger<FessMessages> validationMessagesLambda) {
            savedErrors.add(String.valueOf(validationMessagesLambda));
        }

        @Override
        protected void saveInfo(final VaMessenger<FessMessages> validationMessagesLambda) {
            savedInfos.add(String.valueOf(validationMessagesLambda));
        }

        @Override
        protected HtmlResponse redirect(final Class<?> actionType) {
            return REDIRECT_TO_LOGIN;
        }
    }

    /**
     * Minimal in-memory log4j2 appender for asserting on emitted log messages.
     * Mirrors {@code SamlAuthenticatorTest.LogCapturingAppender}, but also keeps the throwable.
     */
    static final class LogCapturingAppender extends AbstractAppender {
        private final List<LogEvent> events = new CopyOnWriteArrayList<>();
        private final Logger boundLogger;

        private LogCapturingAppender(final Logger logger) {
            super("LogCapturingAppender-" + UUID.randomUUID(), null, null, true, Property.EMPTY_ARRAY);
            this.boundLogger = logger;
        }

        static LogCapturingAppender attach(final Class<?> targetClass) {
            final Logger logger = (Logger) LogManager.getLogger(targetClass);
            final LogCapturingAppender appender = new LogCapturingAppender(logger);
            appender.start();
            logger.addAppender(appender);
            return appender;
        }

        void detach() {
            boundLogger.removeAppender(this);
            stop();
        }

        @Override
        public void append(final LogEvent event) {
            events.add(event.toImmutable());
        }

        List<LogEvent> warnings() {
            return events.stream().filter(e -> e.getLevel() == Level.WARN).toList();
        }

        List<String> warningMessages() {
            return warnings().stream().map(e -> e.getMessage().getFormattedMessage()).toList();
        }
    }
}
