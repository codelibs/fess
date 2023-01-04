/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.exception.InvalidAccessTokenException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;

public class AccessTokenHelperTest extends UnitFessTestCase {

    protected static final int NUM = 20;

    private AccessTokenHelper accessTokenHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        accessTokenHelper = new AccessTokenHelper();
    }

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

    public void test_getAccessTokenFromRequest_ok0() {
        final String token = accessTokenHelper.generateAccessToken();
        MockletHttpServletRequest req = getMockRequest();
        req.addHeader("Authorization", "Bearer " + token);
        assertEquals(token, accessTokenHelper.getAccessTokenFromRequest(req));
    }

    public void test_getAccessTokenFromRequest_ok1() {
        final String token = accessTokenHelper.generateAccessToken();
        MockletHttpServletRequest req = getMockRequest();
        req.addHeader("Authorization", token);
        assertEquals(token, accessTokenHelper.getAccessTokenFromRequest(req));
    }

    public void test_getAccessTokenFromRequest_ng0() {
        final String token = accessTokenHelper.generateAccessToken();
        MockletHttpServletRequest req = getMockRequest();
        assertNull(accessTokenHelper.getAccessTokenFromRequest(req));
    }

    public void test_getAccessTokenFromRequest_ng1() {
        final String token = "INVALID _TOKEN0";
        MockletHttpServletRequest req = getMockRequest();
        req.addHeader("Authorization", token);
        assertThrows(InvalidAccessTokenException.class, () -> accessTokenHelper.getAccessTokenFromRequest(req));
    }
}
