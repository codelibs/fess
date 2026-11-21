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
