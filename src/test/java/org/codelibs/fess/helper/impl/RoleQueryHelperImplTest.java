/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.helper.impl;

import java.util.Set;

import javax.servlet.http.Cookie;

import org.codelibs.core.crypto.CachedCipher;
import org.codelibs.core.exception.IllegalBlockSizeRuntimeException;
import org.codelibs.fess.unit.UnitFessTestCase;

public class RoleQueryHelperImplTest extends UnitFessTestCase {
    public RoleQueryHelperImpl roleQueryHelperImpl;
    public CachedCipher cipher;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        roleQueryHelperImpl = new RoleQueryHelperImpl();
        cipher = new CachedCipher();
        cipher.setKey("1234567890123456");
    }

    public void test_buildByParameter() {
        final RoleQueryHelperImpl roleQueryHelperImpl = new RoleQueryHelperImpl();

        Set<String> roleSet;

        roleSet = roleQueryHelperImpl.buildByParameter(getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.parameterKey = "fess1";

        getMockRequest().setParameter("aaa", "bbb");
        roleSet = roleQueryHelperImpl.buildByParameter(getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.encryptedParameterValue = false;
        getMockRequest().setParameter("fess1", "xxx\nrole1,role2,role3");
        roleSet = roleQueryHelperImpl.buildByParameter(getMockRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        roleQueryHelperImpl.parameterKey = "fess2";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedParameterValue = true;
        getMockRequest().setParameter("fess2", cipher.encryptoText("xxx\nrole1,role2,role3"));
        roleSet = roleQueryHelperImpl.buildByParameter(getMockRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        getMockRequest().setParameter("fess2", "fail");
        try {
            roleSet = roleQueryHelperImpl.buildByParameter(getMockRequest());
            fail();
        } catch (final IllegalBlockSizeRuntimeException e) {
            // ok
        }

        roleQueryHelperImpl.parameterKey = "fess3";

        roleQueryHelperImpl.encryptedParameterValue = false;
        roleSet = roleQueryHelperImpl.buildByParameter(getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.parameterKey = "fess4";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedParameterValue = true;
        roleSet = roleQueryHelperImpl.buildByParameter(getMockRequest());
        assertEquals(0, roleSet.size());

    }

    public void test_buildByHeader() {
        final RoleQueryHelperImpl roleQueryHelperImpl = new RoleQueryHelperImpl();

        Set<String> roleSet;

        try {
            roleSet = roleQueryHelperImpl.buildByHeader(getMockRequest());
            fail();
        } catch (final NullPointerException e) {
            //ok
        }

        roleQueryHelperImpl.headerKey = "fess1";

        getMockRequest().addHeader("aaa", "bbb");
        roleSet = roleQueryHelperImpl.buildByHeader(getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.encryptedHeaderValue = false;
        getMockRequest().addHeader("fess1", "xxx\nrole1,role2,role3");
        roleSet = roleQueryHelperImpl.buildByHeader(getMockRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        roleQueryHelperImpl.headerKey = "fess2";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedHeaderValue = true;
        getMockRequest().addHeader("fess2", cipher.encryptoText("xxx\nrole1,role2,role3"));
        roleSet = roleQueryHelperImpl.buildByHeader(getMockRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        roleQueryHelperImpl.headerKey = "fess2x";
        getMockRequest().addHeader("fess2x", "fail");
        try {
            roleSet = roleQueryHelperImpl.buildByHeader(getMockRequest());
            fail();
        } catch (final IllegalBlockSizeRuntimeException e) {
            // ok
        }

        roleQueryHelperImpl.headerKey = "fess3";

        roleQueryHelperImpl.encryptedHeaderValue = false;
        roleSet = roleQueryHelperImpl.buildByHeader(getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.headerKey = "fess4";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedHeaderValue = true;
        roleSet = roleQueryHelperImpl.buildByHeader(getMockRequest());
        assertEquals(0, roleSet.size());
    }

    public void test_buildByCookie() {
        final RoleQueryHelperImpl roleQueryHelperImpl = new RoleQueryHelperImpl();

        Set<String> roleSet;
        Cookie cookie;

        roleSet = roleQueryHelperImpl.buildByCookie(getMockRequest());
        assertEquals(0, roleSet.size());

        cookie = new Cookie("aaa", "bbb");
        getMockRequest().addCookie(cookie);
        try {
            roleSet = roleQueryHelperImpl.buildByCookie(getMockRequest());
            fail();
        } catch (final NullPointerException e) {
            // ok
        }

        roleQueryHelperImpl.cookieKey = "fess1";

        roleSet = roleQueryHelperImpl.buildByCookie(getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.encryptedCookieValue = false;
        cookie = new Cookie("fess1", "xxx\nrole1,role2,role3");
        getMockRequest().addCookie(cookie);
        roleSet = roleQueryHelperImpl.buildByCookie(getMockRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        roleQueryHelperImpl.cookieKey = "fess2";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedCookieValue = true;
        cookie = new Cookie("fess2", cipher.encryptoText("xxx\nrole1,role2,role3"));
        getMockRequest().addCookie(cookie);
        roleSet = roleQueryHelperImpl.buildByCookie(getMockRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        roleQueryHelperImpl.cookieKey = "fess2x";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedCookieValue = true;
        cookie = new Cookie("fess2x", "fail");
        getMockRequest().addCookie(cookie);
        try {
            roleSet = roleQueryHelperImpl.buildByCookie(getMockRequest());
            fail();
        } catch (final Exception e) {
            // ok
        }

        roleQueryHelperImpl.cookieKey = "fess3";

        roleQueryHelperImpl.encryptedCookieValue = false;
        roleSet = roleQueryHelperImpl.buildByCookie(getMockRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.cookieKey = "fess4";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedCookieValue = true;
        roleSet = roleQueryHelperImpl.buildByCookie(getMockRequest());
        assertEquals(0, roleSet.size());
    }

    public void test_decodedRoleList() {

        final RoleQueryHelperImpl roleQueryHelperImpl = new RoleQueryHelperImpl();

        Set<String> roleSet;
        boolean encrypted;
        String value;

        encrypted = false;
        value = "";
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = false;
        value = "role1";
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = false;
        value = "role1,role2";
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = false;
        value = "xxx\nrole1";
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(1, roleSet.size());
        assertTrue(roleSet.contains("role1"));

        encrypted = false;
        value = "xxx\nrole1,role2";
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(2, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));

        roleQueryHelperImpl.valueSeparator = "";

        encrypted = false;
        value = "";
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = false;
        value = "role1";
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(1, roleSet.size());
        assertTrue(roleSet.contains("role1"));

        encrypted = false;
        value = "role1,role2";
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(2, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));

        encrypted = false;
        value = "role1,role2,role3";
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));
    }

    public void test_decodedRoleList_withCipher() {

        final RoleQueryHelperImpl roleQueryHelperImpl = new RoleQueryHelperImpl();
        roleQueryHelperImpl.cipher = cipher;

        Set<String> roleSet;
        boolean encrypted;
        String value;

        encrypted = true;
        value = cipher.encryptoText("");
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = true;
        value = cipher.encryptoText("role1");
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = true;
        value = cipher.encryptoText("role1,role2");
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = true;
        value = cipher.encryptoText("xxx\nrole1");
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(1, roleSet.size());
        assertTrue(roleSet.contains("role1"));

        encrypted = true;
        value = cipher.encryptoText("xxx\nrole1,role2");
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(2, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));

        roleQueryHelperImpl.valueSeparator = "";

        encrypted = true;
        value = cipher.encryptoText("");
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleSet.size());

        encrypted = true;
        value = cipher.encryptoText("role1");
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(1, roleSet.size());
        assertTrue(roleSet.contains("role1"));

        encrypted = true;
        value = cipher.encryptoText("role1,role2");
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(2, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));

        encrypted = true;
        value = cipher.encryptoText("role1,role2,role3");
        roleSet = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));
    }

}
