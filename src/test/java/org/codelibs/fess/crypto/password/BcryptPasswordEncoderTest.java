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
package org.codelibs.fess.crypto.password;

import java.lang.reflect.Field;

import org.codelibs.fess.crypto.bcrypt.BCrypt;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class BcryptPasswordEncoderTest extends UnitFessTestCase {

    private BcryptPasswordEncoder encoder;

    private int cost = 10;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getAppPasswordBcryptCostAsInteger() {
                return Integer.valueOf(cost);
            }
        });
        encoder = new BcryptPasswordEncoder();
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    @Test
    public void test_capabilities() {
        assertEquals(72, encoder.getMaxPasswordBytes());
        assertTrue(encoder.isVerificationCostly());
    }

    @Test
    public void test_costIsClamped() {
        cost = 1;
        assertTrue(encoder.encode("secret").startsWith("$2a$04$"));
        cost = 99;
        assertEquals(30, encoder.resolveCost());
    }

    @Test
    public void test_needsUpgrade_lowerCostOnly() {
        cost = 4;
        final String low = encoder.encode("secret");
        assertFalse(encoder.needsUpgrade(low));
        cost = 5;
        assertTrue(encoder.needsUpgrade(low));
        // an unparseable cost is not a reason to re-encode
        assertFalse(encoder.needsUpgrade("$2a$"));
        assertFalse(encoder.needsUpgrade("garbage"));
    }

    @Test
    public void test_matches_isLenientAboutLength() {
        final String overlong = "a".repeat(80);
        final String hash = BCrypt.hashpw(overlong, BCrypt.gensalt(4));
        assertTrue(encoder.matches(overlong, hash));
        assertFalse(encoder.matches("a".repeat(71) + "b", hash));
        assertFalse(encoder.matches("secret", ""));
        assertFalse(encoder.matches("secret", "not a bcrypt hash"));
    }

    // Hashes written by other implementations (Python bcrypt for $2b$, Apache htpasswd -B for $2y$).
    private static final String PASSWORD_ASCII = "password";
    private static final String PASSWORD_ASCII_2 = "Passw0rd!x";
    private static final String PASSWORD_UTF8 = "p\u00e4ssw\u00f6rd\u65e5\u672c\u8a9e";
    private static final String PASSWORD_COST10 = "secret";

    private static final String HASH_2B_ASCII = "$2b$04$yPk3dip9vVghAiGk1hhBpedKK5XwXdIprS.TmgLADQUftkCMYgzCu";
    private static final String HASH_2B_ASCII_2 = "$2b$06$HGj5Xj.9ccA7w9yz479uo.IxX.hamAolMa2Fgtu13dUNP6LCh5byK";
    private static final String HASH_2B_UTF8 = "$2b$05$m9Zy5E9994Le0NfRCm/bUuoKxD01p46mPVbzaamv05THKSu1JIjwC";
    private static final String HASH_2B_COST10 = "$2b$10$52.JnWOVJp0SY1uuqu8oFedsRO7iwRn6SpUS7mYAYsFZqy8Z1pi0u";

    private static final String HASH_2Y_ASCII = "$2y$04$a0J.gcGLTR6mKJ0C2ZNKfeWdOXDdkEGjlG6TC3XN8t5aOZh3tBq1.";
    private static final String HASH_2Y_ASCII_2 = "$2y$06$RgJdZ1EVGrEZWZ0LTE9mfOjFO7NXzCLluf.4lyxKl3rPBg3OWcYjW";
    private static final String HASH_2Y_UTF8 = "$2y$05$gEmJNLB37J17SIe6sjAKEOG93.O4gZ6su.hMYdg.6MhPPX8dQ2q2.";
    private static final String HASH_2Y_COST10 = "$2y$10$K0jOpI2PuthXgKg.9vL55elfUpvkbw7ebuJxEOTqoCtPmVtoP.H1K";

    private static String oneCharOff(final String password) {
        return password.substring(0, password.length() - 1) + (password.endsWith("x") ? "y" : "x");
    }

    @Test
    public void test_matches_bcryptHashesWritten_with2bPrefix() {
        assertTrue(encoder.matches(PASSWORD_ASCII, HASH_2B_ASCII));
        assertTrue(encoder.matches(PASSWORD_ASCII_2, HASH_2B_ASCII_2));
        assertTrue(encoder.matches(PASSWORD_UTF8, HASH_2B_UTF8));
        assertTrue(encoder.matches(PASSWORD_COST10, HASH_2B_COST10));
    }

    @Test
    public void test_matches_bcryptHashesWritten_with2yPrefix() {
        assertTrue(encoder.matches(PASSWORD_ASCII, HASH_2Y_ASCII));
        assertTrue(encoder.matches(PASSWORD_ASCII_2, HASH_2Y_ASCII_2));
        assertTrue(encoder.matches(PASSWORD_UTF8, HASH_2Y_UTF8));
        assertTrue(encoder.matches(PASSWORD_COST10, HASH_2Y_COST10));
    }

    @Test
    public void test_matches_2bAnd2y_refuseAWrongPassword() {
        assertFalse(encoder.matches("Password", HASH_2B_ASCII));
        assertFalse(encoder.matches(oneCharOff(PASSWORD_ASCII_2), HASH_2B_ASCII_2));
        assertFalse(encoder.matches(oneCharOff(PASSWORD_UTF8), HASH_2B_UTF8));
        assertFalse(encoder.matches("", HASH_2B_ASCII));
        assertFalse(encoder.matches("Password", HASH_2Y_ASCII));
        assertFalse(encoder.matches(oneCharOff(PASSWORD_ASCII_2), HASH_2Y_ASCII_2));
        assertFalse(encoder.matches(oneCharOff(PASSWORD_UTF8), HASH_2Y_UTF8));
        assertFalse(encoder.matches("", HASH_2Y_ASCII));
        // the password of another hash
        assertFalse(encoder.matches(PASSWORD_ASCII_2, HASH_2B_ASCII));
        assertFalse(encoder.matches(PASSWORD_ASCII_2, HASH_2Y_ASCII));
    }

    @Test
    public void test_matches_2bAnd2y_agreeWith2aOfTheSameHash() {
        // the prefix is the only difference: the same hash text under $2a$ gives the same answer
        assertTrue(encoder.matches(PASSWORD_ASCII, "$2a$" + HASH_2B_ASCII.substring(4)));
        assertTrue(encoder.matches(PASSWORD_ASCII, "$2a$" + HASH_2Y_ASCII.substring(4)));
    }

    @Test
    public void test_matches_2xIsNotAccepted() {
        // $2x$ marks hashes made with the sign-extension bug of crypt_blowfish; they cannot be verified like $2a$
        assertFalse(encoder.matches(PASSWORD_ASCII, "$2x$" + HASH_2B_ASCII.substring(4)));
        assertFalse(encoder.matches(PASSWORD_ASCII, "$2x$" + HASH_2Y_ASCII.substring(4)));
    }

    @Test
    public void test_matches_malformedPrefixOrBody_isFalse() {
        assertFalse(encoder.matches(PASSWORD_ASCII, "$2c$" + HASH_2B_ASCII.substring(4)));
        assertFalse(encoder.matches(PASSWORD_ASCII, "$2B$" + HASH_2B_ASCII.substring(4)));
        assertFalse(encoder.matches(PASSWORD_ASCII, "$3b$" + HASH_2B_ASCII.substring(4)));
        assertFalse(encoder.matches(PASSWORD_ASCII, "$2b" + HASH_2B_ASCII.substring(4)));
        assertFalse(encoder.matches(PASSWORD_ASCII, "$2b$xx$yPk3dip9vVghAiGk1hhBpedKK5XwXdIprS.TmgLADQUftkCMYgzCu"));
    }

    @Test
    public void test_needsUpgrade_and_parseCost_workFor2bAnd2y() {
        cost = 4;
        assertFalse(encoder.needsUpgrade(HASH_2B_ASCII));
        assertFalse(encoder.needsUpgrade(HASH_2Y_ASCII));
        cost = 5;
        assertTrue(encoder.needsUpgrade(HASH_2B_ASCII));
        assertTrue(encoder.needsUpgrade(HASH_2Y_ASCII));
        assertFalse(encoder.needsUpgrade(HASH_2B_UTF8));
        assertEquals(6, encoder.parseCost(HASH_2B_ASCII_2));
        assertEquals(10, encoder.parseCost(HASH_2Y_COST10));
    }

    @Test
    public void test_encode_stillWrites2a() {
        assertTrue(encoder.encode("secret").startsWith("$2a$"));
    }

    @Test
    public void test_parseCost_malformed() {
        assertEquals(-1, encoder.parseCost(null));
        assertEquals(-1, encoder.parseCost(""));
        assertEquals(-1, encoder.parseCost("$2a$"));
        assertEquals(-1, encoder.parseCost("short"));
        assertEquals(-1, encoder.parseCost("$2a$xx$abcdefghijklmnopqrstuvwxyz0123456789012345678901234"));
        assertEquals(-1, encoder.parseCost("2a$10$abc")); // missing leading $
        assertEquals(-1, encoder.parseCost("$2a10$abc")); // missing second $
    }

    @Test
    public void test_parseCost_validHash() {
        assertEquals(10, encoder.parseCost(encoder.encode("secret")));
    }

    @Test
    public void test_applyTimingPadding_buildsOneDummyAtTheConfiguredCostAndReusesIt() throws Exception {
        cost = 4;
        encoder.applyTimingPadding();
        final String first = dummyHash();
        assertNotNull(first);
        assertTrue("dummy must use configured cost: " + first, first.startsWith("$2a$04$"));
        encoder.applyTimingPadding();
        assertSame(first, dummyHash(), "dummy hash must be cached across calls");
    }

    @Test
    public void test_applyTimingPadding_followsACostChange() throws Exception {
        cost = 4;
        encoder.applyTimingPadding();
        cost = 5;
        encoder.applyTimingPadding();
        assertTrue(dummyHash().startsWith("$2a$05$"));
    }

    private String dummyHash() throws Exception {
        final Field f = BcryptPasswordEncoder.class.getDeclaredField("dummyHash");
        f.setAccessible(true);
        return (String) f.get(encoder);
    }
}
