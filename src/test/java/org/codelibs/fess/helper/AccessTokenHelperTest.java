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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.codelibs.fess.exception.InvalidAccessTokenException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class AccessTokenHelperTest extends UnitFessTestCase {

    protected static final int NUM = 20;

    private AccessTokenHelper accessTokenHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        accessTokenHelper = new AccessTokenHelper();
    }

    @Test
    public void test_generateAccessToken() {
        List<String> tokens = new ArrayList<String>();
        for (int i = 0; i < NUM; i++) {
            tokens.add(accessTokenHelper.generateAccessToken());
        }
        for (int i = 0; i < NUM; i++) {
            assertFalse(tokens.get(i).isEmpty());
            for (int j = i + 1; j < NUM; j++) {
                assertFalse(tokens.get(i).equals(tokens.get(j)));
            }
        }
    }

    @Test
    public void test_getAccessTokenFromRequest_ok0() {
        final String token = accessTokenHelper.generateAccessToken();
        MockletHttpServletRequest req = getMockRequest();
        req.addHeader("Authorization", "Bearer " + token);
        assertEquals(token, accessTokenHelper.getAccessTokenFromRequest(req));
    }

    @Test
    public void test_getAccessTokenFromRequest_ok1() {
        final String token = accessTokenHelper.generateAccessToken();
        MockletHttpServletRequest req = getMockRequest();
        req.addHeader("Authorization", token);
        assertEquals(token, accessTokenHelper.getAccessTokenFromRequest(req));
    }

    @Test
    public void test_getAccessTokenFromRequest_bad0() {
        MockletHttpServletRequest req = getMockRequest();
        assertNull(accessTokenHelper.getAccessTokenFromRequest(req));
    }

    @Test
    public void test_getAccessTokenFromRequest_bad1() {
        final String token = "INVALID _TOKEN0";
        MockletHttpServletRequest req = getMockRequest();
        req.addHeader("Authorization", token);
        try {
            accessTokenHelper.getAccessTokenFromRequest(req);
            fail();
        } catch (InvalidAccessTokenException e) {
            // ok
        }
    }

    @Test
    public void test_getAccessTokenFromRequest_bad2() {
        final String token = "Bearer";
        MockletHttpServletRequest req = getMockRequest();
        req.addHeader("Authorization", token);
        try {
            accessTokenHelper.getAccessTokenFromRequest(req);
            fail();
        } catch (InvalidAccessTokenException e) {
            // ok
        }
    }

    @Test
    public void test_getAccessTokenFromRequest_emptyHeader() {
        MockletHttpServletRequest req = getMockRequest();
        req.addHeader("Authorization", "");
        assertEquals("", accessTokenHelper.getAccessTokenFromRequest(req));
    }

    @Test
    public void test_getAccessTokenFromRequest_whitespaceHeader() {
        MockletHttpServletRequest req = getMockRequest();
        req.addHeader("Authorization", "   ");
        assertEquals("", accessTokenHelper.getAccessTokenFromRequest(req));
    }

    @Test
    public void test_getAccessTokenFromRequest_multipleSpaces() {
        final String token = accessTokenHelper.generateAccessToken();
        MockletHttpServletRequest req = getMockRequest();
        req.addHeader("Authorization", "Bearer  " + token);
        try {
            accessTokenHelper.getAccessTokenFromRequest(req);
            fail();
        } catch (InvalidAccessTokenException e) {
            // ok
        }
    }

    @Test
    public void test_getAccessTokenFromRequest_threeElements() {
        MockletHttpServletRequest req = getMockRequest();
        req.addHeader("Authorization", "Bearer token extra");
        try {
            accessTokenHelper.getAccessTokenFromRequest(req);
            fail();
        } catch (InvalidAccessTokenException e) {
            // ok
        }
    }

    @Test
    public void test_getAccessTokenFromRequest_bearerWithTrailingSpaces() {
        final String token = accessTokenHelper.generateAccessToken();
        MockletHttpServletRequest req = getMockRequest();
        req.addHeader("Authorization", "Bearer " + token + "   ");
        assertEquals(token, accessTokenHelper.getAccessTokenFromRequest(req));
    }

    @Test
    public void test_getAccessTokenFromRequest_noHeaderNoParameter() {
        MockletHttpServletRequest req = getMockRequest();
        assertNull(accessTokenHelper.getAccessTokenFromRequest(req));
    }

    @Test
    public void test_setRandom() {
        final Random customRandom = new Random(12345L);
        accessTokenHelper.setRandom(customRandom);

        final String token1 = accessTokenHelper.generateAccessToken();

        accessTokenHelper.setRandom(new Random(12345L));
        final String token2 = accessTokenHelper.generateAccessToken();

        assertEquals(token1, token2);
    }

    @Test
    public void test_generateAccessToken_withCustomRandom() {
        final Random customRandom = new Random(0L);
        accessTokenHelper.setRandom(customRandom);

        final String token = accessTokenHelper.generateAccessToken();
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void test_getAccessTokenFromRequest_caseInsensitiveBearer() {
        final String token = accessTokenHelper.generateAccessToken();
        MockletHttpServletRequest req = getMockRequest();
        req.addHeader("Authorization", "bearer " + token);
        try {
            accessTokenHelper.getAccessTokenFromRequest(req);
            fail();
        } catch (InvalidAccessTokenException e) {
            // ok
        }
    }

    @Test
    public void test_getAccessTokenFromRequest_invalidBearerFormat() {
        MockletHttpServletRequest req = getMockRequest();
        req.addHeader("Authorization", "InvalidBearer token");
        try {
            accessTokenHelper.getAccessTokenFromRequest(req);
            fail();
        } catch (InvalidAccessTokenException e) {
            // ok
        }
    }
}
