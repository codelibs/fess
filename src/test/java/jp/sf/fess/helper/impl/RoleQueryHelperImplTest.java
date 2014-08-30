/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.helper.impl;

import java.util.Set;

import javax.servlet.http.Cookie;

import org.codelibs.core.crypto.CachedCipher;
import org.codelibs.core.exception.IllegalBlockSizeRuntimeException;
import org.seasar.extension.unit.S2TestCase;

public class RoleQueryHelperImplTest extends S2TestCase {

    public CachedCipher cipher;

    @Override
    protected String getRootDicon() throws Throwable {
        return "jp/sf/fess/helper/query.dicon";
    }

    public void test_buildByParameter() {
        final RoleQueryHelperImpl roleQueryHelperImpl = new RoleQueryHelperImpl();

        Set<String> roleSet;

        roleSet = roleQueryHelperImpl.buildByParameter(getRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.parameterKey = "fess1";

        getRequest().setParameter("aaa", "bbb");
        roleSet = roleQueryHelperImpl.buildByParameter(getRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.encryptedParameterValue = false;
        getRequest().setParameter("fess1", "xxx\nrole1,role2,role3");
        roleSet = roleQueryHelperImpl.buildByParameter(getRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        roleQueryHelperImpl.parameterKey = "fess2";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedParameterValue = true;
        getRequest().setParameter("fess2",
                cipher.encryptoText("xxx\nrole1,role2,role3"));
        roleSet = roleQueryHelperImpl.buildByParameter(getRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        getRequest().setParameter("fess2", "fail");
        try {
            roleSet = roleQueryHelperImpl.buildByParameter(getRequest());
            fail();
        } catch (final IllegalBlockSizeRuntimeException e) {
            // ok
        }

        roleQueryHelperImpl.parameterKey = "fess3";

        roleQueryHelperImpl.encryptedParameterValue = false;
        roleSet = roleQueryHelperImpl.buildByParameter(getRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.parameterKey = "fess4";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedParameterValue = true;
        roleSet = roleQueryHelperImpl.buildByParameter(getRequest());
        assertEquals(0, roleSet.size());

    }

    public void test_buildByHeader() {
        final RoleQueryHelperImpl roleQueryHelperImpl = new RoleQueryHelperImpl();

        Set<String> roleSet;

        try {
            roleSet = roleQueryHelperImpl.buildByHeader(getRequest());
            fail();
        } catch (final NullPointerException e) {
            //ok
        }

        roleQueryHelperImpl.headerKey = "fess1";

        getRequest().addHeader("aaa", "bbb");
        roleSet = roleQueryHelperImpl.buildByHeader(getRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.encryptedHeaderValue = false;
        getRequest().addHeader("fess1", "xxx\nrole1,role2,role3");
        roleSet = roleQueryHelperImpl.buildByHeader(getRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        roleQueryHelperImpl.headerKey = "fess2";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedHeaderValue = true;
        getRequest().addHeader("fess2",
                cipher.encryptoText("xxx\nrole1,role2,role3"));
        roleSet = roleQueryHelperImpl.buildByHeader(getRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        roleQueryHelperImpl.headerKey = "fess2x";
        getRequest().addHeader("fess2x", "fail");
        try {
            roleSet = roleQueryHelperImpl.buildByHeader(getRequest());
            fail();
        } catch (final IllegalBlockSizeRuntimeException e) {
            // ok
        }

        roleQueryHelperImpl.headerKey = "fess3";

        roleQueryHelperImpl.encryptedHeaderValue = false;
        roleSet = roleQueryHelperImpl.buildByHeader(getRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.headerKey = "fess4";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedHeaderValue = true;
        roleSet = roleQueryHelperImpl.buildByHeader(getRequest());
        assertEquals(0, roleSet.size());
    }

    public void test_buildByCookie() {
        final RoleQueryHelperImpl roleQueryHelperImpl = new RoleQueryHelperImpl();

        Set<String> roleSet;
        Cookie cookie;

        roleSet = roleQueryHelperImpl.buildByCookie(getRequest());
        assertEquals(0, roleSet.size());

        cookie = new Cookie("aaa", "bbb");
        getRequest().addCookie(cookie);
        try {
            roleSet = roleQueryHelperImpl.buildByCookie(getRequest());
            fail();
        } catch (final NullPointerException e) {
            // ok
        }

        roleQueryHelperImpl.cookieKey = "fess1";

        roleSet = roleQueryHelperImpl.buildByCookie(getRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.encryptedCookieValue = false;
        cookie = new Cookie("fess1", "xxx\nrole1,role2,role3");
        getRequest().addCookie(cookie);
        roleSet = roleQueryHelperImpl.buildByCookie(getRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        roleQueryHelperImpl.cookieKey = "fess2";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedCookieValue = true;
        cookie = new Cookie("fess2",
                cipher.encryptoText("xxx\nrole1,role2,role3"));
        getRequest().addCookie(cookie);
        roleSet = roleQueryHelperImpl.buildByCookie(getRequest());
        assertEquals(3, roleSet.size());
        assertTrue(roleSet.contains("role1"));
        assertTrue(roleSet.contains("role2"));
        assertTrue(roleSet.contains("role3"));

        roleQueryHelperImpl.cookieKey = "fess2x";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedCookieValue = true;
        cookie = new Cookie("fess2x", "fail");
        getRequest().addCookie(cookie);
        try {
            roleSet = roleQueryHelperImpl.buildByCookie(getRequest());
            fail();
        } catch (final Exception e) {
            // ok
        }

        roleQueryHelperImpl.cookieKey = "fess3";

        roleQueryHelperImpl.encryptedCookieValue = false;
        roleSet = roleQueryHelperImpl.buildByCookie(getRequest());
        assertEquals(0, roleSet.size());

        roleQueryHelperImpl.cookieKey = "fess4";

        roleQueryHelperImpl.cipher = cipher;
        roleQueryHelperImpl.encryptedCookieValue = true;
        roleSet = roleQueryHelperImpl.buildByCookie(getRequest());
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