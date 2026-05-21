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
package org.codelibs.fess.helper;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

public class SessionCsrfTokenManagerTest extends UnitFessTestCase {

    @Test
    public void test_issue_returnsStableTokenWithinSession() {
        final SessionCsrfTokenManager m = new SessionCsrfTokenManager();
        final HttpSession session = mockSession();

        final String t1 = m.issue(session);
        final String t2 = m.issue(session);
        assertEquals(t1, t2);
        assertNotNull(t1);
        assertTrue(t1.length() >= 32);
    }

    @Test
    public void test_verify_succeedsForMatching() {
        final SessionCsrfTokenManager m = new SessionCsrfTokenManager();
        final HttpSession session = mockSession();

        final String t = m.issue(session);
        assertTrue(m.verify(session, t));
        assertFalse(m.verify(session, "wrong"));
        assertFalse(m.verify(session, null));
    }

    @Test
    public void test_rotate_changesToken() {
        final SessionCsrfTokenManager m = new SessionCsrfTokenManager();
        final HttpSession session = mockSession();

        final String before = m.issue(session);
        m.rotate(session);
        final String after = m.issue(session);
        assertNotEquals(before, after);
    }

    @Test
    public void test_verify_rejectsNullToken() {
        final SessionCsrfTokenManager m = new SessionCsrfTokenManager();
        final HttpSession session = mockSession();
        // Even with no token stored yet, a null provided value must short-circuit to false.
        assertFalse(m.verify(session, null));
        // And once a token IS issued, a null provided value still returns false.
        m.issue(session);
        assertFalse(m.verify(session, null));
    }

    @Test
    public void test_verify_rejectsEmptyToken() {
        final SessionCsrfTokenManager m = new SessionCsrfTokenManager();
        final HttpSession session = mockSession();
        // Empty token must be rejected before any session attribute lookup.
        assertFalse(m.verify(session, ""));
        m.issue(session);
        assertFalse(m.verify(session, ""));
    }

    @Test
    public void test_verify_rejectsWrongTokenOfSameLength() {
        final SessionCsrfTokenManager m = new SessionCsrfTokenManager();
        final HttpSession session = mockSession();
        final String issued = m.issue(session);
        // Construct a same-length but different string to exercise the constant-time
        // path (length-equal branch) without depending on timing.
        final char[] chars = issued.toCharArray();
        // Flip every character to a guaranteed-different one in the urlsafe base64 alphabet
        // (A-Z, a-z, 0-9, '-', '_').
        for (int i = 0; i < chars.length; i++) {
            chars[i] = chars[i] == 'A' ? 'B' : 'A';
        }
        final String wrong = new String(chars);
        assertEquals(issued.length(), wrong.length());
        assertNotEquals(issued, wrong);
        assertFalse(m.verify(session, wrong));
    }

    @Test
    public void test_rotate_invalidatesPreviousToken() {
        final SessionCsrfTokenManager m = new SessionCsrfTokenManager();
        final HttpSession session = mockSession();
        final String a = m.issue(session);
        assertTrue(m.verify(session, a));
        m.rotate(session);
        // Previous token must no longer verify.
        assertFalse(m.verify(session, a));
        // A freshly issued token must verify; it must differ from the previous one.
        final String b = m.issue(session);
        assertNotEquals(a, b);
        assertTrue(m.verify(session, b));
    }

    private static HttpSession mockSession() {
        return new StubHttpSession();
    }

    /**
     * Minimal Map-backed stub of {@link HttpSession} used only for the
     * attribute get/set semantics that {@link SessionCsrfTokenManager}
     * exercises. All other methods throw {@link UnsupportedOperationException}.
     */
    private static class StubHttpSession implements HttpSession {
        private final Map<String, Object> store = new HashMap<>();

        @Override
        public Object getAttribute(final String name) {
            return store.get(name);
        }

        @Override
        public void setAttribute(final String name, final Object value) {
            if (value == null) {
                store.remove(name);
            } else {
                store.put(name, value);
            }
        }

        @Override
        public void removeAttribute(final String name) {
            store.remove(name);
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return Collections.enumeration(store.keySet());
        }

        @Override
        public long getCreationTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLastAccessedTime() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ServletContext getServletContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setMaxInactiveInterval(final int interval) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getMaxInactiveInterval() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void invalidate() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isNew() {
            throw new UnsupportedOperationException();
        }
    }
}
