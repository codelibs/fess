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

public class PasswordHelperTest extends UnitFessTestCase {

    private PasswordHelper passwordHelper;

    private TestFessConfig testConfig;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        testConfig = new TestFessConfig();
        ComponentUtil.setFessConfig(testConfig);
        passwordHelper = new PasswordHelper();
        passwordHelper.fessConfig = testConfig;
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    @Test
    public void test_encode_matches_bcrypt() {
        final String encoded = passwordHelper.encode("secret");
        assertTrue(passwordHelper.matches("secret", encoded));
    }

    @Test
    public void test_encode_matches_wrongPassword() {
        final String encoded = passwordHelper.encode("secret");
        assertFalse(passwordHelper.matches("Secret", encoded));
        assertFalse(passwordHelper.matches("", encoded));
    }

    @Test
    public void test_encode_containsBcryptPrefix() {
        final String encoded = passwordHelper.encode("secret");
        assertTrue(encoded.startsWith("{bcrypt}$2a$"));
    }

    @Test
    public void test_encode_saltedUniqueness() {
        final String a = passwordHelper.encode("samepass");
        final String b = passwordHelper.encode("samepass");
        assertFalse(a.equals(b));
        assertTrue(passwordHelper.matches("samepass", a));
        assertTrue(passwordHelper.matches("samepass", b));
    }

    @Test
    public void test_matches_legacySha256() throws Exception {
        testConfig.digestAlgorithm = "sha256";
        final String legacy = hex("SHA-256", "hunter2");
        assertTrue(passwordHelper.matches("hunter2", legacy));
        assertFalse(passwordHelper.matches("wrong", legacy));
    }

    @Test
    public void test_matches_legacySha512() throws Exception {
        testConfig.digestAlgorithm = "sha512";
        final String legacy = hex("SHA-512", "hunter2");
        assertTrue(passwordHelper.matches("hunter2", legacy));
        assertFalse(passwordHelper.matches("wrong", legacy));
    }

    @Test
    public void test_matches_legacyMd5() throws Exception {
        testConfig.digestAlgorithm = "md5";
        final String legacy = hex("MD5", "hunter2");
        assertTrue(passwordHelper.matches("hunter2", legacy));
        assertFalse(passwordHelper.matches("wrong", legacy));
    }

    @Test
    public void test_matches_nullOrEmpty() {
        assertFalse(passwordHelper.matches(null, "{bcrypt}$2a$10$abc"));
        assertFalse(passwordHelper.matches("secret", null));
        assertFalse(passwordHelper.matches("secret", ""));
        assertFalse(passwordHelper.matches(null, null));
    }

    @Test
    public void test_matches_unknownPrefix() {
        assertFalse(passwordHelper.matches("secret", "{unknown}ffffff"));
        assertFalse(passwordHelper.matches("secret", "{noop}secret"));
    }

    @Test
    public void test_upgradeEncoding_legacyHash() {
        // hex SHA-256 of "hunter2"
        final String legacy = "f52fbd32b2b3b86ff88ef6c490628285f482af15ddcb29541f94bcf526a3f6c7";
        assertTrue(passwordHelper.upgradeEncoding(legacy));
    }

    @Test
    public void test_upgradeEncoding_newFormatSameCost() {
        final String encoded = passwordHelper.encode("secret");
        assertFalse(passwordHelper.upgradeEncoding(encoded));
    }

    @Test
    public void test_upgradeEncoding_newFormatOldCost() {
        testConfig.bcryptCost = 4;
        final String lowCost = passwordHelper.encode("secret");
        assertTrue(lowCost.startsWith("{bcrypt}$2a$04$"));
        // Now raise target cost above the stored hash's cost.
        testConfig.bcryptCost = 10;
        assertTrue(passwordHelper.upgradeEncoding(lowCost));
    }

    @Test
    public void test_upgradeEncoding_disabled() {
        testConfig.upgradeEnabled = false;
        final String legacy = "f52fbd32b2b3b86ff88ef6c490628285f482af15ddcb29541f94bcf526a3f6c7";
        assertFalse(passwordHelper.upgradeEncoding(legacy));
        // Even when the stored cost is below target.
        testConfig.bcryptCost = 4;
        testConfig.upgradeEnabled = true;
        final String lowCost = passwordHelper.encode("secret");
        testConfig.bcryptCost = 10;
        testConfig.upgradeEnabled = false;
        assertFalse(passwordHelper.upgradeEncoding(lowCost));
    }

    @Test
    public void test_upgradeEncoding_nullOrEmpty() {
        assertFalse(passwordHelper.upgradeEncoding(null));
        assertFalse(passwordHelper.upgradeEncoding(""));
    }

    // ---------------------------------------------------------------------
    // init() fail-fast behaviour
    // ---------------------------------------------------------------------

    @Test
    public void test_init_failsFast_withUnknownAlgorithm() {
        testConfig.passwordAlgorithm = "unknown-algo";
        try {
            passwordHelper.init();
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
            passwordHelper.init();
            fail("init() must throw IllegalStateException for blank algorithm");
        } catch (final IllegalStateException expected) {
            // expected
        }
        testConfig.passwordAlgorithm = "   ";
        try {
            passwordHelper.init();
            fail("init() must throw IllegalStateException for whitespace-only algorithm");
        } catch (final IllegalStateException expected) {
            // expected
        }
    }

    @Test
    public void test_init_succeeds_withBcrypt() {
        // sanity: default config is valid
        passwordHelper.init();
    }

    // ---------------------------------------------------------------------
    // upgradeEncoding robustness against malformed prefixed values
    // ---------------------------------------------------------------------

    @Test
    public void test_upgradeEncoding_unknownPrefix_returnsFalse() {
        // Minor-sec-2 fix: a stored value carrying an unknown {id} prefix
        // cannot be verified via matches(), so it must NOT be up-hashed.
        assertFalse(passwordHelper.upgradeEncoding("{unknown}ffffff"));
        assertFalse(passwordHelper.upgradeEncoding("{noop}secret"));
    }

    @Test
    public void test_upgradeEncoding_emptyBraces() {
        // "{}..." - the encoder id between the braces is empty, which is not a
        // registered encoder. This must be treated as an unknown prefix, not
        // a legacy hash.
        assertFalse(passwordHelper.upgradeEncoding("{}ffffff"));
    }

    @Test
    public void test_upgradeEncoding_unclosedBrace_treatedAsLegacy() {
        // "{bcrypt" with no closing brace -> extractId() returns null -> legacy path
        // Legacy path: with upgrade enabled, legacy hashes are upgraded -> true
        assertTrue(passwordHelper.upgradeEncoding("{bcryptNoCloseBrace"));
    }

    @Test
    public void test_upgradeEncoding_malformedBcryptHash_sameId_noCostUpgrade() {
        // A value with the expected prefix but an unparseable cost field must
        // not be re-encoded solely due to cost parsing failure. parseBcryptCost
        // returns -1, so the cost-upgrade branch is skipped.
        assertFalse(passwordHelper.upgradeEncoding("{bcrypt}$2a$"));
        assertFalse(passwordHelper.upgradeEncoding("{bcrypt}garbage"));
    }

    // ---------------------------------------------------------------------
    // matches robustness
    // ---------------------------------------------------------------------

    @Test
    public void test_matches_prefixWithEmptyPayload() {
        // "{bcrypt}" with no payload - encoder.matches receives "" and returns false.
        assertFalse(passwordHelper.matches("secret", "{bcrypt}"));
    }

    @Test
    public void test_matches_prefixedUpperCase_treatedAsUnknown() {
        // Policy: prefix ids are case-sensitive. "{BCRYPT}" is unknown.
        // Real stored values from encode() are always lower-case ({bcrypt}).
        assertFalse(passwordHelper.matches("secret", "{BCRYPT}$2a$10$abcdefghijklmnopqrstuu"));
    }

    @Test
    public void test_matches_md5_regressionNoException() {
        // md5 match path emits a WARN log; we only assert the functional
        // contract (matches returns true without throwing) because log
        // capturing is not wired up in this test harness.
        testConfig.digestAlgorithm = "md5";
        try {
            final String legacy = hex("MD5", "hunter2");
            assertTrue(passwordHelper.matches("hunter2", legacy));
        } catch (final Exception e) {
            fail("matches() must not throw: " + e);
        }
    }

    // ---------------------------------------------------------------------
    // parseBcryptCost
    // ---------------------------------------------------------------------

    @Test
    public void test_parseBcryptCost_malformed() {
        assertEquals(-1, passwordHelper.parseBcryptCost(null));
        assertEquals(-1, passwordHelper.parseBcryptCost(""));
        assertEquals(-1, passwordHelper.parseBcryptCost("$2a$"));
        assertEquals(-1, passwordHelper.parseBcryptCost("short"));
        assertEquals(-1, passwordHelper.parseBcryptCost("$2a$xx$abcdefghijklmnopqrstuvwxyz0123456789012345678901234"));
        assertEquals(-1, passwordHelper.parseBcryptCost("2a$10$abc")); // missing leading $
        assertEquals(-1, passwordHelper.parseBcryptCost("$2a10$abc")); // missing second $
    }

    @Test
    public void test_parseBcryptCost_validHash() {
        // Cost must be parseable from a real BCrypt encode output.
        final String encoded = passwordHelper.encode("secret");
        final String payload = encoded.substring("{bcrypt}".length());
        assertEquals(10, passwordHelper.parseBcryptCost(payload));
    }

    // ---------------------------------------------------------------------
    // applyTimingPadding / isTimingSafeHash
    // ---------------------------------------------------------------------

    @Test
    public void test_applyTimingPadding_doesNotThrow() {
        // Smoke test: applyTimingPadding must never surface exceptions to the
        // caller, even under repeated invocation.
        for (int i = 0; i < 3; i++) {
            passwordHelper.applyTimingPadding();
        }
    }

    @Test
    public void test_applyTimingPadding_initBuildsDummyEagerly() {
        // init() is expected to eagerly generate the dummy hash so the first
        // login does not pay the generation cost. Assert it is cached.
        passwordHelper.init();
        final String first;
        final String second;
        try {
            final java.lang.reflect.Field f = PasswordHelper.class.getDeclaredField("dummyBcryptHash");
            f.setAccessible(true);
            first = (String) f.get(passwordHelper);
            assertNotNull("init() must populate dummyBcryptHash eagerly", first);
            assertTrue("dummy must be bcrypt-formatted: " + first, first.startsWith("{bcrypt}$2a$"));
            // Second access must reuse the cached reference.
            passwordHelper.applyTimingPadding();
            second = (String) f.get(passwordHelper);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
        assertSame(first, second, "dummy hash must be cached across calls");
    }

    @Test
    public void test_applyTimingPadding_reflectsConfiguredCost() {
        testConfig.bcryptCost = 4;
        passwordHelper.init();
        try {
            final java.lang.reflect.Field f = PasswordHelper.class.getDeclaredField("dummyBcryptHash");
            f.setAccessible(true);
            final String dummy = (String) f.get(passwordHelper);
            assertTrue("dummy must use configured cost: " + dummy, dummy.startsWith("{bcrypt}$2a$04$"));
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void test_isTimingSafeHash() {
        assertTrue(passwordHelper.isTimingSafeHash("{bcrypt}$2a$10$abcdefghijklmnopqrstuu"));
        assertFalse(passwordHelper.isTimingSafeHash(null));
        assertFalse(passwordHelper.isTimingSafeHash(""));
        assertFalse(passwordHelper.isTimingSafeHash("f52fbd32b2b3b86ff88ef6c490628285"));
        assertFalse(passwordHelper.isTimingSafeHash("{unknown}whatever"));
        assertFalse(passwordHelper.isTimingSafeHash("{sha256}abcd"));
        // Case-sensitivity: "{BCRYPT}" is not registered.
        assertFalse(passwordHelper.isTimingSafeHash("{BCRYPT}$2a$10$abc"));
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
