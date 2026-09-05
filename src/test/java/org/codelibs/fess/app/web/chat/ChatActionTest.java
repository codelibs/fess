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

import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.response.HtmlResponse;

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
     * An action that reports login as required and counts how often it sends the visitor there.
     * Nothing else is wired, so a call that reaches past the gate fails loudly.
     */
    private ChatAction gated(final AtomicInteger redirects) {
        return new ChatAction() {
            @Override
            protected boolean isLoginRequired() {
                return true;
            }

            @Override
            protected HtmlResponse redirectToLogin() {
                redirects.incrementAndGet();
                return null;
            }
        };
    }
}
