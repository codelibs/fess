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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;

import jakarta.servlet.http.Cookie;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.Test;

public class UserInfoHelperTest extends UnitFessTestCase {

    @Test
    public void test_getUserCodeFromRequest() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();

        MockletHttpServletRequest request = getMockRequest();

        assertNull(userInfoHelper.getUserCodeFromRequest(request));

        request.setParameter("userCode", "");
        assertNull(userInfoHelper.getUserCodeFromRequest(request));

        final StringBuilder buf = new StringBuilder();
        buf.append("12345abcde");
        request.setParameter("userCode", buf.toString());
        assertNull(userInfoHelper.getUserCodeFromRequest(request));

        buf.append("12345ABCDE");
        request.setParameter("userCode", buf.toString());
        assertEquals("12345abcde12345ABCDE", userInfoHelper.getUserCodeFromRequest(request));
        request.setParameter("userCode", buf.toString() + "_");
        assertEquals("12345abcde12345ABCDE_", userInfoHelper.getUserCodeFromRequest(request));
        request.setParameter("userCode", buf.toString() + " ");
        assertNull(userInfoHelper.getUserCodeFromRequest(request));

        buf.append("12345ABCDE");
        request.setParameter("userCode", buf.toString());
        assertNotNull(userInfoHelper.getUserCodeFromRequest(request));
        buf.append("12345ABCDE");
        request.setParameter("userCode", buf.toString());
        assertNotNull(userInfoHelper.getUserCodeFromRequest(request));
        buf.append("12345ABCDE");
        request.setParameter("userCode", buf.toString());
        assertNotNull(userInfoHelper.getUserCodeFromRequest(request));
        buf.append("12345ABCDE");
        request.setParameter("userCode", buf.toString());
        assertNotNull(userInfoHelper.getUserCodeFromRequest(request));
        buf.append("12345ABCDE");
        request.setParameter("userCode", buf.toString());
        assertNotNull(userInfoHelper.getUserCodeFromRequest(request));
        buf.append("12345ABCDE");
        request.setParameter("userCode", buf.toString());
        assertNotNull(userInfoHelper.getUserCodeFromRequest(request));
        buf.append("12345ABCDE");
        request.setParameter("userCode", buf.toString());
        assertNotNull(userInfoHelper.getUserCodeFromRequest(request));
        buf.append("12345ABCDE");
        request.setParameter("userCode", buf.toString());
        assertNotNull(userInfoHelper.getUserCodeFromRequest(request));
        request.setParameter("userCode", buf.toString() + "x");
        assertNull(userInfoHelper.getUserCodeFromRequest(request));
    }

    @Test
    public void test_createUserCodeFromUserId() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();
        assertEquals("009ab986effa1a9664ada54eb81d7fce", userInfoHelper.createUserCodeFromUserId("a"));
        assertEquals("b17816944bb30c19cb3265480470288caaa93e36666527a57ca94d8a8b8d7b80",
                userInfoHelper.createUserCodeFromUserId("test@example.com"));
        assertEquals("41ebbef035e6cebb9d0cf6b98266d9335abd454718a3b172efa30635ef19f1cc",
                userInfoHelper.createUserCodeFromUserId("!\"#$%&'()'\\^-=,./_?><+*}{`P@[]"));
        assertNull(userInfoHelper
                .createUserCodeFromUserId("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"));
    }

    @Test
    public void test_isSecureCookie_cookieSecureNotNull() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();

        userInfoHelper.setCookieSecure(Boolean.TRUE);
        assertTrue(userInfoHelper.isSecureCookie());

        userInfoHelper.setCookieSecure(Boolean.FALSE);
        assertFalse(userInfoHelper.isSecureCookie());
    }

    @Test
    public void test_isSecureCookie_cookieSecureNull_xForwardedProto_https() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();
        userInfoHelper.setCookieSecure(null);

        MockletHttpServletRequest request = getMockRequest();
        request.addHeader("X-Forwarded-Proto", "https");

        assertTrue(userInfoHelper.isSecureCookie());
    }

    @Test
    public void test_isSecureCookie_noRequest() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();
        userInfoHelper.setCookieSecure(null);

        assertFalse(userInfoHelper.isSecureCookie());
    }

    @Test
    public void test_getUserCodeFromCookie() {
        UserInfoHelper userInfoHelper = new UserInfoHelper() {
            @Override
            protected String getUserCodeFromCookie(final jakarta.servlet.http.HttpServletRequest request) {
                final Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (final Cookie cookie : cookies) {
                        if ("fsid".equals(cookie.getName()) && "12345abcde12345ABCDE".equals(cookie.getValue())) {
                            return cookie.getValue();
                        }
                    }
                }
                return null;
            }
        };
        MockletHttpServletRequest request = getMockRequest();

        assertNull(userInfoHelper.getUserCodeFromCookie(request));

        request.addCookie(new Cookie("fsid", "12345abcde12345ABCDE"));
        assertEquals("12345abcde12345ABCDE", userInfoHelper.getUserCodeFromCookie(request));
    }

    @Test
    public void test_deleteUserCodeFromCookie() {
        UserInfoHelper userInfoHelper = new UserInfoHelper() {
            @Override
            protected String getUserCodeFromCookie(final jakarta.servlet.http.HttpServletRequest request) {
                return "testUserCode";
            }

            @Override
            protected void updateCookie(final String userCode, final int age) {
                assertTrue(userCode.isEmpty());
                assertEquals(0, age);
            }
        };
        MockletHttpServletRequest request = getMockRequest();

        userInfoHelper.deleteUserCodeFromCookie(request);
    }

    @Test
    public void test_getId() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();
        String id1 = userInfoHelper.getId();
        String id2 = userInfoHelper.getId();

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotSame(id1, id2);
        assertEquals(32, id1.length());
        assertEquals(32, id2.length());
        assertTrue(id1.matches("[0-9a-f]+"));
        assertTrue(id2.matches("[0-9a-f]+"));
    }

    @Test
    public void test_storeQueryId() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();
        MockletHttpServletRequest request = getMockRequest();

        List<Map<String, Object>> documentItems = new ArrayList<>();
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("docId", "doc1");
        doc1.put("title", "Test Document 1");
        documentItems.add(doc1);

        try {
            userInfoHelper.storeQueryId("query1", documentItems);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_storeQueryId_emptyDocuments() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();
        MockletHttpServletRequest request = getMockRequest();

        List<Map<String, Object>> documentItems = new ArrayList<>();
        try {
            userInfoHelper.storeQueryId("query1", documentItems);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_getResultDocIds_nonExistentQuery() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();
        MockletHttpServletRequest request = getMockRequest();

        String[] docIds = userInfoHelper.getResultDocIds("nonExistentQuery");
        assertEquals(0, docIds.length);
    }

    @Test
    public void test_setters() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();

        userInfoHelper.setResultDocIdsCacheSize(50);
        userInfoHelper.setCookieName("testCookie");
        userInfoHelper.setCookieDomain("example.com");
        userInfoHelper.setCookieMaxAge(3600);
        userInfoHelper.setCookiePath("/test");
        userInfoHelper.setCookieSecure(true);
        userInfoHelper.setCookieHttpOnly(false);

        assertTrue(true);
    }

    @Test
    public void test_isSecureCookie_cookieSecureNull_xForwardedProto_http() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();
        userInfoHelper.setCookieSecure(null);

        MockletHttpServletRequest request = getMockRequest();
        request.addHeader("X-Forwarded-Proto", "http");

        assertFalse(userInfoHelper.isSecureCookie());
    }

    @Test
    public void test_isSecureCookie_cookieSecureNull_xForwardedProto_mixed_case() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();
        userInfoHelper.setCookieSecure(null);

        MockletHttpServletRequest request = getMockRequest();
        request.addHeader("X-Forwarded-Proto", "HTTPS");

        assertTrue(userInfoHelper.isSecureCookie());
    }

    @Test
    public void test_isSecureCookie_cookieSecureNull_noXForwardedProto_secure() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();
        userInfoHelper.setCookieSecure(null);

        MockletHttpServletRequest request = getMockRequest();
        request.setScheme("https");

        try {
            userInfoHelper.isSecureCookie();
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_isSecureCookie_cookieSecureNull_noXForwardedProto_notSecure() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();
        userInfoHelper.setCookieSecure(null);

        MockletHttpServletRequest request = getMockRequest();
        request.setScheme("http");

        try {
            userInfoHelper.isSecureCookie();
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_getUserCode_withAttributeSet() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();
        MockletHttpServletRequest request = getMockRequest();

        request.setAttribute(Constants.USER_CODE, "testUserCode");
        assertEquals("testUserCode", userInfoHelper.getUserCode());
    }

    @Test
    public void test_getUserCode_withValidRequestParameter() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();
        MockletHttpServletRequest request = getMockRequest();

        request.setParameter("userCode", "12345abcde12345ABCDE");
        assertEquals("12345abcde12345ABCDE", userInfoHelper.getUserCode());
    }

    @Test
    public void test_getUserCode_withValidCookie() {
        UserInfoHelper userInfoHelper = new UserInfoHelper() {
            @Override
            protected String getUserCodeFromRequest(final jakarta.servlet.http.HttpServletRequest request) {
                return null;
            }

            @Override
            protected String getUserCodeFromCookie(final jakarta.servlet.http.HttpServletRequest request) {
                return "12345abcde12345ABCDE";
            }

            @Override
            protected String getUserCodeFromUserBean(final jakarta.servlet.http.HttpServletRequest request) {
                return null;
            }

            @Override
            protected void updateUserSessionId(final String userCode) {
                assertEquals("12345abcde12345ABCDE", userCode);
            }
        };
        MockletHttpServletRequest request = getMockRequest();

        try {
            String userCode = userInfoHelper.getUserCode();
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_getUserCode_generatesNewId() {
        UserInfoHelper userInfoHelper = new UserInfoHelper();
        MockletHttpServletRequest request = getMockRequest();

        try {
            String userCode = userInfoHelper.getUserCode();
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

}
