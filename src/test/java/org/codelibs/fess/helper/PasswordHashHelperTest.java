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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class PasswordHashHelperTest extends UnitFessTestCase {

    private PasswordHashHelper passwordHashHelper;

    private TestFessConfig testConfig;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        testConfig = new TestFessConfig();
        ComponentUtil.setFessConfig(testConfig);
        passwordHashHelper = new PasswordHashHelper();
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    @Test
    public void test_encode_matches_bcrypt() {
        final String encoded = passwordHashHelper.encode("secret");
        assertTrue(passwordHashHelper.matches("secret", encoded));
    }

    @Test
    public void test_encode_matches_wrongPassword() {
        final String encoded = passwordHashHelper.encode("secret");
        assertFalse(passwordHashHelper.matches("Secret", encoded));
        assertFalse(passwordHashHelper.matches("", encoded));
    }

    @Test
    public void test_encode_containsBcryptPrefix() {
        final String encoded = passwordHashHelper.encode("secret");
        assertTrue(encoded.startsWith("{bcrypt}$2a$"));
    }

    @Test
    public void test_encode_saltedUniqueness() {
        final String a = passwordHashHelper.encode("samepass");
        final String b = passwordHashHelper.encode("samepass");
        assertFalse(a.equals(b));
        assertTrue(passwordHashHelper.matches("samepass", a));
        assertTrue(passwordHashHelper.matches("samepass", b));
    }

    @Test
    public void test_matches_legacySha256() throws Exception {
        testConfig.digestAlgorithm = "sha256";
        final String legacy = hex("SHA-256", "hunter2");
        assertTrue(passwordHashHelper.matches("hunter2", legacy));
        assertFalse(passwordHashHelper.matches("wrong", legacy));
    }

    @Test
    public void test_matches_legacySha512() throws Exception {
        testConfig.digestAlgorithm = "sha512";
        final String legacy = hex("SHA-512", "hunter2");
        assertTrue(passwordHashHelper.matches("hunter2", legacy));
        assertFalse(passwordHashHelper.matches("wrong", legacy));
    }

    @Test
    public void test_matches_legacyMd5() throws Exception {
        testConfig.digestAlgorithm = "md5";
        final String legacy = hex("MD5", "hunter2");
        assertTrue(passwordHashHelper.matches("hunter2", legacy));
        assertFalse(passwordHashHelper.matches("wrong", legacy));
    }

    @Test
    public void test_matches_nullOrEmpty() {
        assertFalse(passwordHashHelper.matches(null, "{bcrypt}$2a$10$abc"));
        assertFalse(passwordHashHelper.matches("secret", null));
        assertFalse(passwordHashHelper.matches("secret", ""));
        assertFalse(passwordHashHelper.matches(null, null));
    }

    @Test
    public void test_matches_unknownPrefix() {
        assertFalse(passwordHashHelper.matches("secret", "{unknown}ffffff"));
        assertFalse(passwordHashHelper.matches("secret", "{noop}secret"));
    }

    @Test
    public void test_upgradeEncoding_legacyHash() {
        // hex SHA-256 of "hunter2"
        final String legacy = "f52fbd32b2b3b86ff88ef6c490628285f482af15ddcb29541f94bcf526a3f6c7";
        assertTrue(passwordHashHelper.upgradeEncoding(legacy));
    }

    @Test
    public void test_upgradeEncoding_newFormatSameCost() {
        final String encoded = passwordHashHelper.encode("secret");
        assertFalse(passwordHashHelper.upgradeEncoding(encoded));
    }

    @Test
    public void test_upgradeEncoding_newFormatOldCost() {
        testConfig.bcryptCost = 4;
        final String lowCost = passwordHashHelper.encode("secret");
        assertTrue(lowCost.startsWith("{bcrypt}$2a$04$"));
        // Now raise target cost above the stored hash's cost.
        testConfig.bcryptCost = 10;
        assertTrue(passwordHashHelper.upgradeEncoding(lowCost));
    }

    @Test
    public void test_upgradeEncoding_disabled() {
        testConfig.upgradeEnabled = false;
        final String legacy = "f52fbd32b2b3b86ff88ef6c490628285f482af15ddcb29541f94bcf526a3f6c7";
        assertFalse(passwordHashHelper.upgradeEncoding(legacy));
        // Even when the stored cost is below target.
        testConfig.bcryptCost = 4;
        testConfig.upgradeEnabled = true;
        final String lowCost = passwordHashHelper.encode("secret");
        testConfig.bcryptCost = 10;
        testConfig.upgradeEnabled = false;
        assertFalse(passwordHashHelper.upgradeEncoding(lowCost));
    }

    @Test
    public void test_upgradeEncoding_nullOrEmpty() {
        assertFalse(passwordHashHelper.upgradeEncoding(null));
        assertFalse(passwordHashHelper.upgradeEncoding(""));
    }

    // ---------------------------------------------------------------------
    // init() fail-fast behaviour
    // ---------------------------------------------------------------------

    @Test
    public void test_init_failsFast_withUnknownAlgorithm() {
        testConfig.passwordAlgorithm = "unknown-algo";
        try {
            passwordHashHelper.init();
            fail("init() must throw IllegalStateException for unknown algorithm");
        } catch (final IllegalStateException expected) {
            assertTrue(
                    expected.getMessage().toLowerCase().contains("unsupported") || expected.getMessage().toLowerCase().contains("unknown"));
        }
    }

    @Test
    public void test_init_failsFast_withBlankAlgorithm() {
        testConfig.passwordAlgorithm = "";
        try {
            passwordHashHelper.init();
            fail("init() must throw IllegalStateException for blank algorithm");
        } catch (final IllegalStateException expected) {
            // expected
        }
        testConfig.passwordAlgorithm = "   ";
        try {
            passwordHashHelper.init();
            fail("init() must throw IllegalStateException for whitespace-only algorithm");
        } catch (final IllegalStateException expected) {
            // expected
        }
    }

    @Test
    public void test_init_succeeds_withBcrypt() {
        // sanity: default config is valid
        passwordHashHelper.init();
    }

    // ---------------------------------------------------------------------
    // upgradeEncoding robustness against malformed prefixed values
    // ---------------------------------------------------------------------

    @Test
    public void test_upgradeEncoding_unknownPrefix_returnsFalse() {
        // Minor-sec-2 fix: a stored value carrying an unknown {id} prefix
        // cannot be verified via matches(), so it must NOT be up-hashed.
        assertFalse(passwordHashHelper.upgradeEncoding("{unknown}ffffff"));
        assertFalse(passwordHashHelper.upgradeEncoding("{noop}secret"));
    }

    @Test
    public void test_upgradeEncoding_emptyBraces() {
        // "{}..." - the encoder id between the braces is empty, which is not a
        // registered encoder. This must be treated as an unknown prefix, not
        // a legacy hash.
        assertFalse(passwordHashHelper.upgradeEncoding("{}ffffff"));
    }

    @Test
    public void test_upgradeEncoding_unclosedBrace_treatedAsLegacy() {
        // "{bcrypt" with no closing brace -> extractId() returns null -> legacy path
        // Legacy path: with upgrade enabled, legacy hashes are upgraded -> true
        assertTrue(passwordHashHelper.upgradeEncoding("{bcryptNoCloseBrace"));
    }

    @Test
    public void test_upgradeEncoding_malformedBcryptHash_sameId_noCostUpgrade() {
        // A value with the expected prefix but an unparseable cost field must
        // not be re-encoded solely due to cost parsing failure. parseBcryptCost
        // returns -1, so the cost-upgrade branch is skipped.
        assertFalse(passwordHashHelper.upgradeEncoding("{bcrypt}$2a$"));
        assertFalse(passwordHashHelper.upgradeEncoding("{bcrypt}garbage"));
    }

    // ---------------------------------------------------------------------
    // matches robustness
    // ---------------------------------------------------------------------

    @Test
    public void test_matches_prefixWithEmptyPayload() {
        // "{bcrypt}" with no payload - encoder.matches receives "" and returns false.
        assertFalse(passwordHashHelper.matches("secret", "{bcrypt}"));
    }

    @Test
    public void test_matches_prefixedUpperCase_treatedAsUnknown() {
        // Policy: prefix ids are case-sensitive. "{BCRYPT}" is unknown.
        // Real stored values from encode() are always lower-case ({bcrypt}).
        assertFalse(passwordHashHelper.matches("secret", "{BCRYPT}$2a$10$abcdefghijklmnopqrstuu"));
    }

    @Test
    public void test_matches_md5_regressionNoException() {
        // md5 match path emits a WARN log; we only assert the functional
        // contract (matches returns true without throwing) because log
        // capturing is not wired up in this test harness.
        testConfig.digestAlgorithm = "md5";
        try {
            final String legacy = hex("MD5", "hunter2");
            assertTrue(passwordHashHelper.matches("hunter2", legacy));
        } catch (final Exception e) {
            fail("matches() must not throw: " + e);
        }
    }

    // ---------------------------------------------------------------------
    // parseBcryptCost
    // ---------------------------------------------------------------------

    @Test
    public void test_parseBcryptCost_malformed() {
        assertEquals(-1, passwordHashHelper.parseBcryptCost(null));
        assertEquals(-1, passwordHashHelper.parseBcryptCost(""));
        assertEquals(-1, passwordHashHelper.parseBcryptCost("$2a$"));
        assertEquals(-1, passwordHashHelper.parseBcryptCost("short"));
        assertEquals(-1, passwordHashHelper.parseBcryptCost("$2a$xx$abcdefghijklmnopqrstuvwxyz0123456789012345678901234"));
        assertEquals(-1, passwordHashHelper.parseBcryptCost("2a$10$abc")); // missing leading $
        assertEquals(-1, passwordHashHelper.parseBcryptCost("$2a10$abc")); // missing second $
    }

    @Test
    public void test_parseBcryptCost_validHash() {
        // Cost must be parseable from a real BCrypt encode output.
        final String encoded = passwordHashHelper.encode("secret");
        final String payload = encoded.substring("{bcrypt}".length());
        assertEquals(10, passwordHashHelper.parseBcryptCost(payload));
    }

    // ---------------------------------------------------------------------
    // applyTimingPadding / isTimingSafeHash
    // ---------------------------------------------------------------------

    @Test
    public void test_applyTimingPadding_doesNotThrow() {
        // Smoke test: applyTimingPadding must never surface exceptions to the
        // caller, even under repeated invocation.
        for (int i = 0; i < 3; i++) {
            passwordHashHelper.applyTimingPadding();
        }
    }

    @Test
    public void test_applyTimingPadding_initBuildsDummyEagerly() {
        // init() is expected to eagerly generate the dummy hash so the first
        // login does not pay the generation cost. Assert it is cached.
        passwordHashHelper.init();
        final String first;
        final String second;
        try {
            final java.lang.reflect.Field f = PasswordHashHelper.class.getDeclaredField("dummyBcryptHash");
            f.setAccessible(true);
            first = (String) f.get(passwordHashHelper);
            assertNotNull("init() must populate dummyBcryptHash eagerly", first);
            assertTrue("dummy must be bcrypt-formatted: " + first, first.startsWith("{bcrypt}$2a$"));
            // Second access must reuse the cached reference.
            passwordHashHelper.applyTimingPadding();
            second = (String) f.get(passwordHashHelper);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
        assertSame(first, second, "dummy hash must be cached across calls");
    }

    @Test
    public void test_applyTimingPadding_reflectsConfiguredCost() {
        testConfig.bcryptCost = 4;
        passwordHashHelper.init();
        try {
            final java.lang.reflect.Field f = PasswordHashHelper.class.getDeclaredField("dummyBcryptHash");
            f.setAccessible(true);
            final String dummy = (String) f.get(passwordHashHelper);
            assertTrue("dummy must use configured cost: " + dummy, dummy.startsWith("{bcrypt}$2a$04$"));
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void test_isTimingSafeHash() {
        assertTrue(passwordHashHelper.isTimingSafeHash("{bcrypt}$2a$10$abcdefghijklmnopqrstuu"));
        assertFalse(passwordHashHelper.isTimingSafeHash(null));
        assertFalse(passwordHashHelper.isTimingSafeHash(""));
        assertFalse(passwordHashHelper.isTimingSafeHash("f52fbd32b2b3b86ff88ef6c490628285"));
        assertFalse(passwordHashHelper.isTimingSafeHash("{unknown}whatever"));
        assertFalse(passwordHashHelper.isTimingSafeHash("{sha256}abcd"));
        // Case-sensitivity: "{BCRYPT}" is not registered.
        assertFalse(passwordHashHelper.isTimingSafeHash("{BCRYPT}$2a$10$abc"));
    }

    private static String hex(final String jca, final String input) throws Exception {
        final MessageDigest md = MessageDigest.getInstance(jca);
        final byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        final StringBuilder sb = new StringBuilder(digest.length * 2);
        for (final byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    /** Minimal FessConfig override for this test. */
    private static class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        String passwordAlgorithm = "bcrypt";
        int bcryptCost = 10;
        boolean upgradeEnabled = true;
        String digestAlgorithm = "sha256";

        @Override
        public String getAppPasswordAlgorithm() {
            return passwordAlgorithm;
        }

        @Override
        public Integer getAppPasswordBcryptCostAsInteger() {
            return Integer.valueOf(bcryptCost);
        }

        @Override
        public String getAppPasswordBcryptCost() {
            return Integer.toString(bcryptCost);
        }

        @Override
        public boolean isAppPasswordUpgradeEnabled() {
            return upgradeEnabled;
        }

        @Override
        public String getAppPasswordUpgradeEnabled() {
            return Boolean.toString(upgradeEnabled);
        }

        @Override
        public String getAppDigestAlgorithm() {
            return digestAlgorithm;
        }
    }
}
