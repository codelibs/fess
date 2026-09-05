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
package org.codelibs.fess.app.web.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.validation.VaErrorHook;
import org.lastaflute.web.validation.VaMore;
import org.lastaflute.web.validation.ValidationSuccess;

/**
 * The chat screens sit alongside the other search-side screens, so login.required has to close
 * them too. It did not: every sibling action consulted the gate and this one did not, which left
 * the page and the session reset reachable without signing in.
 */
public class ChatActionTest extends UnitFessTestCase {

    /**
     * The chat page redirects to login while login.required is set and nobody is signed in, before
     * it looks at anything else.
     */
    @Test
    public void test_index_redirectsToLoginWhenLoginIsRequired() {
        final AtomicInteger redirects = new AtomicInteger(0);
        final ChatAction action = gated(redirects);

        assertNull(action.index());
        assertEquals(1, redirects.get());
    }

    /**
     * Clearing a session is a write, so it is gated the same way. Otherwise anyone could drop
     * another visitor's chat session on a site that requires a login.
     */
    @Test
    public void test_clear_redirectsToLoginWhenLoginIsRequired() {
        final AtomicInteger redirects = new AtomicInteger(0);
        final ChatAction action = gated(redirects);

        final ChatForm form = new ChatForm();
        form.sessionId = "s1";
        assertNull(action.clear(form));
        assertEquals(1, redirects.get());
    }

    /**
     * ChatForm carries validator annotations, so the framework insists that clear() call
     * validate() on every path out of the method, and it reports a method that never did once the
     * method has already returned. The call has to come before the login gate, because a call
     * placed after it is skipped again on the path that redirects to login.
     */
    @Test
    public void test_clear_validatesBeforeTheLoginGate() {
        final List<String> calls = new ArrayList<>();
        final ChatAction action = gated(new AtomicInteger(0), calls);

        final ChatForm form = new ChatForm();
        form.sessionId = "s1";
        assertNull(action.clear(form));
        assertEquals("validate redirectToLogin", String.join(" ", calls));
    }

    /**
     * An action that reports login as required and counts how often it sends the visitor there.
     * Nothing else is wired, so a call that reaches past the gate fails loudly.
     */
    private ChatAction gated(final AtomicInteger redirects) {
        return gated(redirects, new ArrayList<>());
    }

    /**
     * The same action, recording what it does in order. validate() is recorded rather than run,
     * since the real one wants a request and a message resource behind it.
     */
    private ChatAction gated(final AtomicInteger redirects, final List<String> calls) {
        return new ChatAction() {
            @Override
            public ValidationSuccess validate(final Object form, final VaMore<FessMessages> moreValidationLambda,
                    final VaErrorHook validationErrorLambda) {
                calls.add("validate");
                return new ValidationSuccess(new FessMessages());
            }

            @Override
            protected boolean isLoginRequired() {
                return true;
            }

            @Override
            protected HtmlResponse redirectToLogin() {
                calls.add("redirectToLogin");
                redirects.incrementAndGet();
                return null;
            }
        };
    }
}
