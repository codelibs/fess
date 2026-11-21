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

import org.codelibs.fess.crypto.bcrypt.BCrypt;
import org.codelibs.fess.crypto.password.PasswordEncoder;
import org.codelibs.fess.exception.FessConfigurationException;
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
    public void test_matches_bcryptHashWrittenByAnotherSystem_2bAnd2y() {
        // Python bcrypt ($2b$) and Apache htpasswd -B ($2y$), as stored by an import or a manual edit
        assertTrue(passwordHashHelper.matches("password", "{bcrypt}$2b$04$yPk3dip9vVghAiGk1hhBpedKK5XwXdIprS.TmgLADQUftkCMYgzCu"));
        assertTrue(passwordHashHelper.matches("password", "{bcrypt}$2y$04$a0J.gcGLTR6mKJ0C2ZNKfeWdOXDdkEGjlG6TC3XN8t5aOZh3tBq1."));
        assertFalse(passwordHashHelper.matches("passwort", "{bcrypt}$2b$04$yPk3dip9vVghAiGk1hhBpedKK5XwXdIprS.TmgLADQUftkCMYgzCu"));
        assertFalse(passwordHashHelper.matches("passwort", "{bcrypt}$2y$04$a0J.gcGLTR6mKJ0C2ZNKfeWdOXDdkEGjlG6TC3XN8t5aOZh3tBq1."));
        assertFalse(passwordHashHelper.matches("password", "{bcrypt}$2x$04$yPk3dip9vVghAiGk1hhBpedKK5XwXdIprS.TmgLADQUftkCMYgzCu"));
    }

    @Test
    public void test_matches_truncatedBcryptHash_2bAnd2y_isFalse() {
        assertFalse(passwordHashHelper.matches("password", "{bcrypt}$2b$"));
        assertFalse(passwordHashHelper.matches("password", "{bcrypt}$2b$04$"));
        assertFalse(passwordHashHelper.matches("password", "{bcrypt}$2y$04$a0J.gcGLTR6mKJ0C2ZNKfe"));
    }

    @Test
    public void test_upgradeEncoding_2bAnd2y_onlyWhenTheCostIsLower() {
        final String b = "{bcrypt}$2b$04$yPk3dip9vVghAiGk1hhBpedKK5XwXdIprS.TmgLADQUftkCMYgzCu";
        final String y = "{bcrypt}$2y$10$K0jOpI2PuthXgKg.9vL55elfUpvkbw7ebuJxEOTqoCtPmVtoP.H1K";
        testConfig.bcryptCost = 10;
        assertTrue(passwordHashHelper.upgradeEncoding(b));
        assertFalse(passwordHashHelper.upgradeEncoding(y));
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
    // password length limit of the algorithm
    // ---------------------------------------------------------------------

    @Test
    public void test_getMaxPasswordBytes_bcrypt() {
        assertEquals(72, passwordHashHelper.getMaxPasswordBytes());
    }

    @Test
    public void test_encode_acceptsUpTo72Bytes() {
        final String password = "a".repeat(72);
        assertTrue(passwordHashHelper.matches(password, passwordHashHelper.encode(password)));
        // 24 x 3 bytes
        final String multibyte = "\u3042".repeat(24);
        assertTrue(passwordHashHelper.matches(multibyte, passwordHashHelper.encode(multibyte)));
    }

    @Test
    public void test_encode_refusesOver72Bytes() {
        try {
            passwordHashHelper.encode("a".repeat(73));
            fail("encode() must refuse a password over 72 bytes");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    public void test_encode_refusesOver72BytesCountedInUtf8() {
        // 25 characters but 75 bytes
        try {
            passwordHashHelper.encode("\u3042".repeat(25));
            fail("encode() must count bytes, not characters");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    @Test
    public void test_encode_refusalDoesNotQuoteThePassword() {
        final String password = "x".repeat(40) + "SECRET" + "y".repeat(40);
        try {
            passwordHashHelper.encode(password);
            fail("encode() must refuse a password over 72 bytes");
        } catch (final IllegalArgumentException expected) {
            assertFalse(expected.getMessage(), expected.getMessage().contains("SECRET"));
            assertTrue(expected.getMessage(), expected.getMessage().contains("86"));
            assertTrue(expected.getMessage(), expected.getMessage().contains("72"));
        }
    }

    @Test
    public void test_matches_staysLenientForAHashMadeFromAnOverlongPassword() {
        // A password set over 72 bytes before 15.10: its owner must keep logging in.
        final String overlong = "a".repeat(80);
        final String legacyHash = "{bcrypt}" + BCrypt.hashpw(overlong, BCrypt.gensalt(4));
        assertTrue(passwordHashHelper.matches(overlong, legacyHash));
        // only the first 72 bytes are compared, which is why a new password must be refused
        assertTrue(passwordHashHelper.matches("a".repeat(72) + "another ending", legacyHash));
        assertFalse(passwordHashHelper.matches("a".repeat(71) + "b", legacyHash));
    }

    @Test
    public void test_exceedsMaxPasswordBytes() {
        assertFalse(passwordHashHelper.exceedsMaxPasswordBytes(null));
        assertFalse(passwordHashHelper.exceedsMaxPasswordBytes(""));
        assertFalse(passwordHashHelper.exceedsMaxPasswordBytes("a".repeat(72)));
        assertTrue(passwordHashHelper.exceedsMaxPasswordBytes("a".repeat(73)));
        // bytes, not characters: 24 Japanese characters are 72 bytes, 25 are 75
        assertFalse(passwordHashHelper.exceedsMaxPasswordBytes("\u3042".repeat(24)));
        assertTrue(passwordHashHelper.exceedsMaxPasswordBytes("\u3042".repeat(25)));
    }

    // ---------------------------------------------------------------------
    // init() validation, and an algorithm that cannot be resolved (nothing runs init() at start-up)
    // ---------------------------------------------------------------------

    @Test
    public void test_unresolvableAlgorithm_failsAtTheFirstHashWithAClearMessage_andVerifyingStillWorks() {
        final String bcryptHash = passwordHashHelper.encode("secret");
        testConfig.passwordAlgorithm = "gone";
        try {
            passwordHashHelper.encode("secret");
            fail("encode() must refuse a configured algorithm that has no component");
        } catch (final FessConfigurationException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("app.password.algorithm"));
            assertTrue(expected.getMessage(), expected.getMessage().contains("gonePasswordEncoder"));
        }
        try {
            passwordHashHelper.getMaxPasswordBytes();
            fail("the limit of an unresolvable algorithm is unknown");
        } catch (final FessConfigurationException expected) {
            // expected
        }
        // timing padding never throws, and a stored hash of a resolvable id still verifies
        passwordHashHelper.applyTimingPadding();
        assertTrue(passwordHashHelper.matches("secret", bcryptHash));
    }

    @Test
    public void test_init_failsFast_withUnknownAlgorithm() {
        testConfig.passwordAlgorithm = "unknown-algo";
        try {
            passwordHashHelper.init();
            fail("init() must throw FessConfigurationException for unknown algorithm");
        } catch (final FessConfigurationException expected) {
            assertTrue(
                    expected.getMessage().toLowerCase().contains("unsupported") || expected.getMessage().toLowerCase().contains("unknown"));
        }
    }

    @Test
    public void test_init_failsFast_withBlankAlgorithm() {
        testConfig.passwordAlgorithm = "";
        try {
            passwordHashHelper.init();
            fail("init() must throw FessConfigurationException for blank algorithm");
        } catch (final FessConfigurationException expected) {
            // expected
        }
        testConfig.passwordAlgorithm = "   ";
        try {
            passwordHashHelper.init();
            fail("init() must throw FessConfigurationException for whitespace-only algorithm");
        } catch (final FessConfigurationException expected) {
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
        // not be re-encoded solely due to cost parsing failure. The cost parser
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

    // ---------------------------------------------------------------------
    // the algorithm is a replaceable component: <id>PasswordEncoder
    // ---------------------------------------------------------------------

    private LimitlessEncoder registerLimitlessEncoder() {
        final LimitlessEncoder encoder = new LimitlessEncoder();
        // not in the container: ComponentUtil falls back to this map, and tearDown clears it
        ComponentUtil.register(encoder, "limitlessPasswordEncoder");
        return encoder;
    }

    @Test
    public void test_pluggable_encodeAndMatchesRoundTrip() {
        registerLimitlessEncoder();
        testConfig.passwordAlgorithm = "limitless";
        final String encoded = passwordHashHelper.encode("secret");
        assertEquals("{limitless}t9secret", encoded);
        assertTrue(passwordHashHelper.matches("secret", encoded));
        assertFalse(passwordHashHelper.matches("Secret", encoded));
    }

    @Test
    public void test_pluggable_configuredIdIsLowerCased() {
        registerLimitlessEncoder();
        testConfig.passwordAlgorithm = "LimitLess";
        assertTrue(passwordHashHelper.encode("secret").startsWith("{limitless}"));
    }

    @Test
    public void test_pluggable_existingBcryptHashStillVerifiesAndIsUpgraded() {
        final String bcryptHash = passwordHashHelper.encode("secret");
        assertTrue(bcryptHash.startsWith("{bcrypt}"));
        registerLimitlessEncoder();
        testConfig.passwordAlgorithm = "limitless";
        assertTrue(passwordHashHelper.matches("secret", bcryptHash));
        assertFalse(passwordHashHelper.matches("other", bcryptHash));
        // another id than the one for new passwords is always re-encoded
        assertTrue(passwordHashHelper.upgradeEncoding(bcryptHash));
        // the encoder decides for its own hashes
        assertFalse(passwordHashHelper.upgradeEncoding(passwordHashHelper.encode("secret")));
        assertTrue(passwordHashHelper.upgradeEncoding("{limitless}old:secret"));
    }

    @Test
    public void test_pluggable_upgradeEncodingHonoursTheSwitch() {
        registerLimitlessEncoder();
        testConfig.passwordAlgorithm = "limitless";
        testConfig.upgradeEnabled = false;
        assertFalse(passwordHashHelper.upgradeEncoding("{limitless}old:secret"));
    }

    @Test
    public void test_pluggable_encoderWithoutLimit() {
        registerLimitlessEncoder();
        testConfig.passwordAlgorithm = "limitless";
        assertTrue(passwordHashHelper.getMaxPasswordBytes() <= 0);
        final String longPassword = "a".repeat(500);
        assertFalse(passwordHashHelper.exceedsMaxPasswordBytes(longPassword));
        assertTrue(passwordHashHelper.matches(longPassword, passwordHashHelper.encode(longPassword)));
    }

    @Test
    public void test_pluggable_unknownIdKeepsTheOldBehaviour() {
        assertFalse(passwordHashHelper.matches("secret", "{limitless}t9secret"));
        assertFalse(passwordHashHelper.upgradeEncoding("{limitless}t9secret"));
        assertFalse(passwordHashHelper.isTimingSafeHash("{limitless}t9secret"));
        testConfig.passwordAlgorithm = "limitless";
        try {
            passwordHashHelper.init();
            fail("init() must refuse an algorithm without a PasswordEncoder component");
        } catch (final FessConfigurationException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("limitless"));
        }
    }

    @Test
    public void test_pluggable_aComponentThatIsNotAnEncoderIsNotAnEncoder() {
        ComponentUtil.register(new Object(), "bogusPasswordEncoder");
        assertFalse(passwordHashHelper.matches("secret", "{bogus}whatever"));
        testConfig.passwordAlgorithm = "bogus";
        try {
            passwordHashHelper.init();
            fail("init() must refuse a component that is not a PasswordEncoder");
        } catch (final FessConfigurationException expected) {
            // expected
        }
    }

    @Test
    public void test_pluggable_timingPaddingForACheapEncoder() {
        final LimitlessEncoder encoder = registerLimitlessEncoder();
        testConfig.passwordAlgorithm = "limitless";
        passwordHashHelper.init();
        passwordHashHelper.applyTimingPadding();
        assertEquals(0, encoder.paddings);
        assertFalse(passwordHashHelper.isTimingSafeHash("{limitless}t9secret"));
    }

    @Test
    public void test_pluggable_timingPaddingForACostlyEncoder() {
        final LimitlessEncoder encoder = registerLimitlessEncoder();
        encoder.costly = true;
        testConfig.passwordAlgorithm = "limitless";
        passwordHashHelper.applyTimingPadding();
        assertEquals(1, encoder.paddings);
        assertTrue(passwordHashHelper.isTimingSafeHash("{limitless}t9secret"));
        // never throws, even when the encoder does
        encoder.failPadding = true;
        passwordHashHelper.applyTimingPadding();
    }

    @Test
    public void test_applyTimingPadding_neverThrowsOnAMisconfiguredAlgorithm() {
        testConfig.passwordAlgorithm = "unknown-algo";
        passwordHashHelper.applyTimingPadding();
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

    /** An encoder that is not bcrypt: no limit, never costly, nothing to upgrade unless the hash says so. */
    private static class LimitlessEncoder implements PasswordEncoder {
        boolean costly;
        boolean failPadding;
        int paddings;

        @Override
        public String encode(final String rawPassword) {
            return "t9" + rawPassword;
        }

        @Override
        public boolean matches(final String rawPassword, final String encodedPassword) {
            return encode(rawPassword).equals(encodedPassword);
        }

        @Override
        public boolean needsUpgrade(final String encodedPassword) {
            return encodedPassword.startsWith("old:");
        }

        @Override
        public boolean isVerificationCostly() {
            return costly;
        }

        @Override
        public void applyTimingPadding() {
            paddings++;
            if (failPadding) {
                throw new IllegalStateException("padding failed");
            }
        }
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
