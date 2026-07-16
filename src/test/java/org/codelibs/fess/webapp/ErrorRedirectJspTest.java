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
package org.codelibs.fess.webapp;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the {@code /error/...} targets the container error page redirects to
 * actually resolve to existing action classes.
 *
 * <p>{@code redirect.jsp} is the {@code <error-page>} target for every mapped status code
 * (see {@code web.xml}). It hands control to LastaFlute by issuing a redirect to a URL such
 * as {@code /error/notfound/}. LastaFlute resolves that URL back to an action class by
 * convention, so a URL literal in the JSP that does not match an action class name is a
 * silent breakage: the redirect lands on nothing and falls through to the 404 page. No
 * compiler or existing test catches that mismatch, because one side is a string in a JSP
 * and the other is a Java class name.
 */
public class ErrorRedirectJspTest extends UnitFessTestCase {

    /** Package holding the error action classes that {@code /error/*} URLs resolve to. */
    private static final String ERROR_ACTION_PACKAGE = "org.codelibs.fess.app.web.error";

    private static final String REDIRECT_JSP_PATH = "src/main/webapp/WEB-INF/view/error/redirect.jsp";

    private String readJsp(final String path) throws Exception {
        final File file = new File(path);
        assertTrue(path + " should exist", file.exists());
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    /**
     * Derives the action class name LastaFlute resolves an {@code /error/<kind>/} URL to.
     *
     * <p>Mirrors {@code ActionPathResolver#buildActionName}, which composes the component
     * name as {@code pkg + classPrefix + "Action"} where {@code classPrefix} accumulates
     * {@code initCap} of each path segment. So {@code /error/notfound/} resolves to
     * {@code error_errorNotfoundAction}, i.e. the class
     * {@code org.codelibs.fess.app.web.error.ErrorNotfoundAction}.
     */
    private String toActionClassName(final String kind) {
        final String initCap = kind.substring(0, 1).toUpperCase(Locale.ROOT) + kind.substring(1);
        return ERROR_ACTION_PACKAGE + ".Error" + initCap + "Action";
    }

    private void assertActionExists(final String kind, final String source) {
        final String className = toActionClassName(kind);
        try {
            Class.forName(className);
        } catch (final ClassNotFoundException e) {
            fail(source + " redirects to /error/" + kind + "/ but no action class " + className
                    + " exists, so the redirect resolves to nothing and falls through to the 404 page");
        }
    }

    /**
     * Pins the specific mismatch that shipped: the bad-request branch must point at the
     * URL its action class is named for. The action was {@code ErrorBadrequrestAction}
     * (note the transposed "requrest"), so LastaFlute served it at {@code /error/badrequrest/}
     * while the JSP redirected genuine HTTP 400s to {@code /error/badrequest/} -- which
     * resolved to nothing, and every 400 rendered the 404 page instead.
     */
    @Test
    public void test_badRequestBranchTargetsTheBadRequestAction() throws Exception {
        final String jsp = readJsp(REDIRECT_JSP_PATH);
        assertTrue("the badRequest branch should redirect to /error/badrequest/", jsp.contains("/error/badrequest/"));
        assertActionExists("badrequest", REDIRECT_JSP_PATH);
    }
}
