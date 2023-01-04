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

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.codelibs.core.crypto.CachedCipher;
import org.codelibs.fess.unit.UnitFessTestCase;

public class RoleQueryHelperTest extends UnitFessTestCase {
    public CachedCipher cipher;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        cipher = new CachedCipher();
        cipher.setKey("1234567890123456");
    }

    private Set<String> buildByParameter(final RoleQueryHelper roleQueryHelperImpl, final HttpServletRequest request) {
        Set<String> roleSet = new HashSet<>();
        roleQueryHelperImpl.processParameter(request, roleSet);
        return roleSet;
    }

    private Set<String> buildByHeader(final RoleQueryHelper roleQueryHelperImpl, final HttpServletRequest request) {
        Set<String> roleSet = new HashSet<>();
        roleQueryHelperImpl.processHeader(request, roleSet);
        return roleSet;
    }

    private Set<String> buildByCookie(final RoleQueryHelper roleQueryHelperImpl, final HttpServletRequest request) {
        Set<String> roleSet = new HashSet<>();
        roleQueryHelperImpl.processCookie(request, roleSet);
        return roleSet;
    }

    private Set<String> decodedRoleList(final RoleQueryHelper roleQueryHelperImpl, final String value, final boolean encrypted) {
        Set<String> roleSet = new HashSet<>();
        roleQueryHelperImpl.parseRoleSet(value, encrypted, roleSet);
        return roleSet;
    }

    public void test_buildByParameter() {
        final RoleQueryHelper roleQueryHelperImpl = new RoleQueryHelper() {
            protected long getCurrentTime() {
                return System.currentTimeMillis();
            }
        };

        Set<String> roleSet;

        roleSet = buildByParameter(roleQueryHelperImpl, getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.parameterKey = "fess1";

        getMockRequest().setParameter("aaa", "bbb");
        roleSet = buildByParameter(roleQueryHelperImpl, getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.encryptedParameterValue = false;
        getMockRequest().setParameter("fess1", System.currentTimeMillis() / 1000 + "\nrole1,role2,role3");
        roleSet = buildByParameter(roleQueryHelperImpl, getMockRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        roleQueryHelperImpl.parameterKey = "fess2";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedParameterValue = true;
        getMockRequest().setParameter("fess2", cipher.encryptoText(System.currentTimeMillis() / 1000 + "\nrole1,role2,role3"));
        roleSet = buildByParameter(roleQueryHelperImpl, getMockRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        getMockRequest().setParameter("fess2", "fail");
        roleSet = buildByParameter(roleQueryHelperImpl, getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.parameterKey = "fess3";

        roleQueryHelperImpl.encryptedParameterValue = false;
        roleSet = buildByParameter(roleQueryHelperImpl, getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.parameterKey = "fess4";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedParameterValue = true;
        roleSet = buildByParameter(roleQueryHelperImpl, getMockRequest());
        assertEquals(0, roleSet.size());

    }

    public void test_buildByHeader() {
        final RoleQueryHelper roleQueryHelperImpl = new RoleQueryHelper() {
            protected long getCurrentTime() {
                return System.currentTimeMillis();
            }
        };

        Set<String> roleSet;

        try {
            roleSet = buildByHeader(roleQueryHelperImpl, getMockRequest());
            fail();
        } catch (final NullPointerException e) {
            //ok
        }

        roleQueryHelperImpl.headerKey = "fess1";

        getMockRequest().addHeader("aaa", "bbb");
        roleSet = buildByHeader(roleQueryHelperImpl, getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.encryptedHeaderValue = false;
        getMockRequest().addHeader("fess1", System.currentTimeMillis() / 1000 + "\nrole1,role2,role3");
        roleSet = buildByHeader(roleQueryHelperImpl, getMockRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        roleQueryHelperImpl.headerKey = "fess2";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedHeaderValue = true;
        getMockRequest().addHeader("fess2", cipher.encryptoText(System.currentTimeMillis() / 1000 + "\nrole1,role2,role3"));
        roleSet = buildByHeader(roleQueryHelperImpl, getMockRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        roleQueryHelperImpl.headerKey = "fess2x";
        getMockRequest().addHeader("fess2x", "fail");
        roleSet = buildByHeader(roleQueryHelperImpl, getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.headerKey = "fess3";

        roleQueryHelperImpl.encryptedHeaderValue = false;
        roleSet = buildByHeader(roleQueryHelperImpl, getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.headerKey = "fess4";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedHeaderValue = true;
        roleSet = buildByHeader(roleQueryHelperImpl, getMockRequest());
        assertEquals(0, roleSet.size());
    }

    public void test_buildByCookie() {
        final RoleQueryHelper roleQueryHelperImpl = new RoleQueryHelper() {
            protected long getCurrentTime() {
                return System.currentTimeMillis();
            }
        };

        Set<String> roleSet;
        Cookie cookie;

        roleSet = buildByCookie(roleQueryHelperImpl, getMockRequest());
        assertEquals(0, roleSet.size());

        cookie = new Cookie("aaa", "bbb");
        getMockRequest().addCookie(cookie);
        try {
            roleSet = buildByCookie(roleQueryHelperImpl, getMockRequest());
            fail();
        } catch (final NullPointerException e) {
            // ok
        }

        roleQueryHelperImpl.cookieKey = "fess1";

        roleSet = buildByCookie(roleQueryHelperImpl, getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.encryptedCookieValue = false;
        cookie = new Cookie("fess1", System.currentTimeMillis() / 1000 + "\nrole1,role2,role3");
        getMockRequest().addCookie(cookie);
        roleSet = buildByCookie(roleQueryHelperImpl, getMockRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        roleQueryHelperImpl.cookieKey = "fess2";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedCookieValue = true;
        cookie = new Cookie("fess2", cipher.encryptoText(System.currentTimeMillis() / 1000 + "\nrole1,role2,role3"));
        getMockRequest().addCookie(cookie);
        roleSet = buildByCookie(roleQueryHelperImpl, getMockRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        roleQueryHelperImpl.cookieKey = "fess2x";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedCookieValue = true;
        cookie = new Cookie("fess2x", "fail");
        getMockRequest().addCookie(cookie);
        roleSet = buildByCookie(roleQueryHelperImpl, getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.cookieKey = "fess3";

        roleQueryHelperImpl.encryptedCookieValue = false;
        roleSet = buildByCookie(roleQueryHelperImpl, getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.cookieKey = "fess4";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedCookieValue = true;
        roleSet = buildByCookie(roleQueryHelperImpl, getMockRequest());
        assertEquals(0, roleSet.size());
    }

    public void test_decodedRoleList() {
        final RoleQueryHelper roleQueryHelperImpl = new RoleQueryHelper() {
            protected long getCurrentTime() {
                return System.currentTimeMillis();
            }
        };

        Set<String> roleSet;
        boolean encrypted;
        String value;

        encrypted = false;
        value = "";
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = false;
        value = "role1";
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = false;
        value = "role1,role2";
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = false;
        value = System.currentTimeMillis() / 1000 + "\nrole1";
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(1, roleSet.size());
        assertTrue(roleSet.contains("role1"));

        encrypted = false;
        value = System.currentTimeMillis() / 1000 + "\nrole1,role2";
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(2, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));

        roleQueryHelperImpl.valueSeparator = "";

        encrypted = false;
        value = "";
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = false;
        value = "role1";
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(1, roleSet.size());
        assertTrue(roleSet.contains("role1"));

        encrypted = false;
        value = "role1,role2";
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(2, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));

        encrypted = false;
        value = "role1,role2,role3";
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));
    }

    public void test_decodedRoleList_withCipher() {
        final RoleQueryHelper roleQueryHelperImpl = new RoleQueryHelper() {
            protected long getCurrentTime() {
                return System.currentTimeMillis();
            }
        };
        roleQueryHelperImpl.cipher = cipher;

        Set<String> roleSet;
        boolean encrypted;
        String value;

        encrypted = true;
        value = cipher.encryptoText("");
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = true;
        value = cipher.encryptoText("role1");
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = true;
        value = cipher.encryptoText("role1,role2");
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = true;
        value = cipher.encryptoText(System.currentTimeMillis() / 1000 + "\nrole1");
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(1, roleSet.size());
        assertTrue(roleSet.contains("role1"));

        encrypted = true;
        value = cipher.encryptoText(System.currentTimeMillis() / 1000 + "\nrole1,role2");
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(2, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));

        roleQueryHelperImpl.valueSeparator = "";

        encrypted = true;
        value = cipher.encryptoText("");
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = true;
        value = cipher.encryptoText("role1");
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(1, roleSet.size());
        assertTrue(roleSet.contains("role1"));

        encrypted = true;
        value = cipher.encryptoText("role1,role2");
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(2, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));

        encrypted = true;
        value = cipher.encryptoText("role1,role2,role3");
        roleSet = decodedRoleList(roleQueryHelperImpl, value, encrypted);
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));
    }

}
