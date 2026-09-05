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
package org.codelibs.fess.filter;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.lastaflute.core.direction.FwAssistantDirector;
import org.lastaflute.web.servlet.filter.RequestLoggingFilter;
import org.lastaflute.web.servlet.filter.hook.FilterHook;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Asserts that a request ending as a 500 leaves exactly one WARN line behind.
 *
 * <p>The exception is delivered the way {@link RequestLoggingFilter} delivers it in production:
 * through {@code processServerErrorCallback}, which is what {@code sendInternalServerError} calls
 * on the 500 path and only there.</p>
 */
public class ServerErrorLoggingFilterTest extends UnitFessTestCase {

    @Override
    protected String prepareMockServletPath() {
        return "/search/advance";
    }

    /** Calls the protected callback dispatch of the real logging filter. */
    private static class ExposedLoggingFilter extends RequestLoggingFilter {
        void fireServerError(final HttpServletRequest request, final HttpServletResponse response, final Throwable cause) {
            processServerErrorCallback(request, response, cause);
        }
    }

    @Test
    public void test_serverError_logsOneWarnLineWithPathAndException() throws Exception {
        final LogCapturingAppender appender = LogCapturingAppender.attach(ServerErrorLoggingFilter.class);
        try {
            final HttpServletRequest request = getMockRequest();
            final HttpServletResponse response = getMockResponse();
            getMockRequest().setMethod("GET");
            final ExposedLoggingFilter loggingFilter = new ExposedLoggingFilter();
            final IllegalStateException cause = new IllegalStateException("lonely validator annotation");

            new ServerErrorLoggingFilter().doFilter(request, response,
                    (req, res) -> loggingFilter.fireServerError(request, response, cause));

            final List<String> warnings = appender.warnings();
            assertEquals("expected exactly one WARN line, got " + warnings, 1, warnings.size());
            final String line = warnings.get(0);
            assertTrue(line + " must name the request path", line.contains(request.getRequestURI()));
            assertTrue(line + " must name the request method", line.contains("GET"));
            assertTrue(line + " must name the exception class", line.contains(IllegalStateException.class.getName()));
            assertTrue(line + " must carry the exception message", line.contains("lonely validator annotation"));
            // The stack trace belongs to debug: a WARN carrying the throwable would print it.
            assertNull(appender.eventsAt(Level.WARN).get(0).getThrown(), "WARN line must not carry a stack trace");
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_noServerError_logsNothing() throws Exception {
        final LogCapturingAppender appender = LogCapturingAppender.attach(ServerErrorLoggingFilter.class);
        try {
            new ServerErrorLoggingFilter().doFilter(getMockRequest(), getMockResponse(), (req, res) -> {
                // an ordinary request: a 4xx or a redirect never reaches the server error callback
            });
            assertEquals("a request that did not fail must log nothing, got " + appender.events(), 0, appender.events().size());
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_registeredOutsideTheLoggingFilter() throws Exception {
        final List<FilterHook> hooks =
                ComponentUtil.getComponent(FwAssistantDirector.class).assistWebDirection().assistOutsideFilterHookList();
        assertEquals("expected one outside filter hook, got " + hooks, 1, hooks.size());
        final LogCapturingAppender appender = LogCapturingAppender.attach(ServerErrorLoggingFilter.class);
        try {
            final ExposedLoggingFilter loggingFilter = new ExposedLoggingFilter();
            hooks.get(0)
                    .hook(getMockRequest(), getMockResponse(),
                            (req, res) -> loggingFilter.fireServerError(req, res, new IllegalStateException("registered hook")));
            assertEquals("the registered hook must produce the WARN line, got " + appender.events(), 1, appender.warnings().size());
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_handlerDetachedAfterRequest() throws Exception {
        final HttpServletRequest request = getMockRequest();
        final HttpServletResponse response = getMockResponse();
        final ExposedLoggingFilter loggingFilter = new ExposedLoggingFilter();
        new ServerErrorLoggingFilter().doFilter(request, response, (req, res) -> {
            // nothing failed during the request
        });
        final LogCapturingAppender appender = LogCapturingAppender.attach(ServerErrorLoggingFilter.class);
        try {
            loggingFilter.fireServerError(request, response, new IllegalStateException("after the request"));
            assertEquals("the handler must not outlive the request, got " + appender.events(), 0, appender.events().size());
        } finally {
            appender.detach();
        }
    }
}
