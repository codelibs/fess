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
package org.codelibs.fess.crypto.bcrypt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the vendored jBCrypt implementation at
 * {@link org.codelibs.fess.crypto.bcrypt.BCrypt}. These tests intentionally
 * avoid any Fess runtime dependencies so that the bcrypt primitive can be
 * verified in isolation.
 */
public class BCryptTest {

    /** Known-answer vector generated with this very implementation and pinned
     *  here so regressions in {@link BCrypt#hashpw(String, String)} are
     *  detected. The fixed salt keeps the hash deterministic. */
    private static final String KNOWN_PASSWORD = "password";
    private static final String KNOWN_SALT = "$2a$10$abcdefghijklmnopqrstuu";
    private static final String KNOWN_HASH = "$2a$10$abcdefghijklmnopqrstuu5Lo0g67CiD3M4RpN1BmBb4Crp5w7dbK";

    @Test
    public void testGensaltAndRoundTrip() {
        final String salt = BCrypt.gensalt();
        assertTrue(salt.startsWith("$2a$10$"), "default gensalt should be $2a$10$ (Spring v5.8 compatible)");
        final String hash = BCrypt.hashpw("my-secret-pass", salt);
        assertTrue(hash.startsWith("$2a$10$"), "hash should preserve the salt prefix");
        assertTrue(BCrypt.checkpw("my-secret-pass", hash));
        assertFalse(BCrypt.checkpw("wrong-password", hash));
    }

    @Test
    public void testGensaltIncludesLogRounds() {
        assertTrue(BCrypt.gensalt(4).startsWith("$2a$04$"));
        assertTrue(BCrypt.gensalt(12).startsWith("$2a$12$"));
    }

    @Test
    public void testKnownAnswerHashpw() {
        // Deterministic check - same salt must always produce the same hash.
        assertEquals(KNOWN_HASH, BCrypt.hashpw(KNOWN_PASSWORD, KNOWN_SALT));
        assertTrue(BCrypt.checkpw(KNOWN_PASSWORD, KNOWN_HASH));
        assertFalse(BCrypt.checkpw("not-the-password", KNOWN_HASH));
    }

    @Test
    public void testDifferentSaltsYieldDifferentHashes() {
        final String pw = "same-password";
        final String h1 = BCrypt.hashpw(pw, BCrypt.gensalt(4));
        final String h2 = BCrypt.hashpw(pw, BCrypt.gensalt(4));
        assertNotEquals(h1, h2, "bcrypt must include a per-call salt");
        assertTrue(BCrypt.checkpw(pw, h1));
        assertTrue(BCrypt.checkpw(pw, h2));
    }

    @Test
    public void testCostParameters() {
        for (final int cost : new int[] { 4, 6, 8, 10, 12 }) {
            final String salt = BCrypt.gensalt(cost);
            final String expectedPrefix = String.format("$2a$%02d$", cost);
            assertTrue(salt.startsWith(expectedPrefix), "gensalt(" + cost + ") should produce prefix " + expectedPrefix);
            final String hash = BCrypt.hashpw("cost-test", salt);
            assertTrue(hash.startsWith(expectedPrefix));
            assertTrue(BCrypt.checkpw("cost-test", hash));
        }
    }

    @Test
    public void testInvalidCostRejected() {
        // gensalt only range-checks the upper bound on emit; the lower bound
        // (< 4) is enforced when the hash is actually computed.
        assertThrows(IllegalArgumentException.class, () -> BCrypt.gensalt(31));
        assertThrows(IllegalArgumentException.class, () -> BCrypt.hashpw("x", "$2a$03$abcdefghijklmnopqrstuu"));
        assertThrows(IllegalArgumentException.class, () -> BCrypt.hashpw("x", "$2a$31$abcdefghijklmnopqrstuu"));
    }

    @Test
    public void testInvalidSaltRejected() {
        assertThrows(IllegalArgumentException.class, () -> BCrypt.hashpw("x", "not-a-salt"));
        assertThrows(IllegalArgumentException.class, () -> BCrypt.hashpw("x", "$1$abc$def"));
    }

    /**
     * BCrypt is defined to silently truncate input at 72 bytes. This is an
     * inherent limitation of the original algorithm and is the same behavior
     * exposed by Spring Security's {@code BCryptPasswordEncoder}. We pin the
     * behavior here so any accidental change to the primitive is caught.
     *
     * <p>Consequence: two passwords that share the first 72 bytes will match
     * against the same hash. Password policies in Fess should keep the
     * allowed maximum below 72 bytes, or pre-hash if longer is ever needed.
     */
    @Test
    public void testKnownTruncationAt72Bytes() {
        final StringBuilder base = new StringBuilder();
        for (int i = 0; i < 72; i++) {
            base.append('a');
        }
        final String salt = BCrypt.gensalt(4);
        final String hash72 = BCrypt.hashpw(base.toString(), salt);
        // Appending arbitrary characters beyond 72 bytes still matches.
        assertTrue(BCrypt.checkpw(base.toString() + "X", hash72),
                "BCrypt truncates at 72 bytes - trailing bytes beyond index 72 are ignored");
        assertTrue(BCrypt.checkpw(base.toString() + "different-tail", hash72),
                "BCrypt truncates at 72 bytes - trailing bytes beyond index 72 are ignored");
    }
}
