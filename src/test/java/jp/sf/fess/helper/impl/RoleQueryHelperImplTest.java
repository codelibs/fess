/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

import java.util.List;

import javax.servlet.http.Cookie;

import jp.sf.fess.FessSystemException;
import jp.sf.fess.crypto.FessCipher;

import org.seasar.extension.unit.S2TestCase;

public class RoleQueryHelperImplTest extends S2TestCase {

    public FessCipher fessCipher;

    @Override
    protected String getRootDicon() throws Throwable {
        return "jp/sf/fess/helper/query.dicon";
    }

    public void test_buildByParameter() {
        final RoleQueryHelperImpl roleQueryHelperImpl = new RoleQueryHelperImpl();

        List<String> roleList;

        roleList = roleQueryHelperImpl.buildByParameter(getRequest());
        assertEquals(0, roleList.size());

        roleQueryHelperImpl.parameterKey = "fess1";

        getRequest().setParameter("aaa", "bbb");
        roleList = roleQueryHelperImpl.buildByParameter(getRequest());
        assertEquals(0, roleList.size());

        roleQueryHelperImpl.encryptedParameterValue = false;
        getRequest().setParameter("fess1", "xxx\nrole1,role2,role3");
        roleList = roleQueryHelperImpl.buildByParameter(getRequest());
        assertEquals(3, roleList.size());
        assertEquals("role1", roleList.get(0));
        assertEquals("role2", roleList.get(1));
        assertEquals("role3", roleList.get(2));

        roleQueryHelperImpl.parameterKey = "fess2";

        roleQueryHelperImpl.fessCipher = fessCipher;
        roleQueryHelperImpl.encryptedParameterValue = true;
        getRequest().setParameter("fess2",
                fessCipher.encryptoText("xxx\nrole1,role2,role3"));
        roleList = roleQueryHelperImpl.buildByParameter(getRequest());
        assertEquals(3, roleList.size());
        assertEquals("role1", roleList.get(0));
        assertEquals("role2", roleList.get(1));
        assertEquals("role3", roleList.get(2));

        getRequest().setParameter("fess2", "fail");
        try {
            roleList = roleQueryHelperImpl.buildByParameter(getRequest());
            fail();
        } catch (final FessSystemException e) {
            // ok
        }

        roleQueryHelperImpl.parameterKey = "fess3";

        roleQueryHelperImpl.encryptedParameterValue = false;
        roleList = roleQueryHelperImpl.buildByParameter(getRequest());
        assertEquals(0, roleList.size());

        roleQueryHelperImpl.parameterKey = "fess4";

        roleQueryHelperImpl.fessCipher = fessCipher;
        roleQueryHelperImpl.encryptedParameterValue = true;
        roleList = roleQueryHelperImpl.buildByParameter(getRequest());
        assertEquals(0, roleList.size());

    }

    public void test_buildByHeader() {
        final RoleQueryHelperImpl roleQueryHelperImpl = new RoleQueryHelperImpl();

        List<String> roleList;

        try {
            roleList = roleQueryHelperImpl.buildByHeader(getRequest());
            fail();
        } catch (final NullPointerException e) {
            //ok
        }

        roleQueryHelperImpl.headerKey = "fess1";

        getRequest().addHeader("aaa", "bbb");
        roleList = roleQueryHelperImpl.buildByHeader(getRequest());
        assertEquals(0, roleList.size());

        roleQueryHelperImpl.encryptedHeaderValue = false;
        getRequest().addHeader("fess1", "xxx\nrole1,role2,role3");
        roleList = roleQueryHelperImpl.buildByHeader(getRequest());
        assertEquals(3, roleList.size());
        assertEquals("role1", roleList.get(0));
        assertEquals("role2", roleList.get(1));
        assertEquals("role3", roleList.get(2));

        roleQueryHelperImpl.headerKey = "fess2";

        roleQueryHelperImpl.fessCipher = fessCipher;
        roleQueryHelperImpl.encryptedHeaderValue = true;
        getRequest().addHeader("fess2",
                fessCipher.encryptoText("xxx\nrole1,role2,role3"));
        roleList = roleQueryHelperImpl.buildByHeader(getRequest());
        assertEquals(3, roleList.size());
        assertEquals("role1", roleList.get(0));
        assertEquals("role2", roleList.get(1));
        assertEquals("role3", roleList.get(2));

        roleQueryHelperImpl.headerKey = "fess2x";
        getRequest().addHeader("fess2x", "fail");
        try {
            roleList = roleQueryHelperImpl.buildByHeader(getRequest());
            fail();
        } catch (final FessSystemException e) {
            // ok
        }

        roleQueryHelperImpl.headerKey = "fess3";

        roleQueryHelperImpl.encryptedHeaderValue = false;
        roleList = roleQueryHelperImpl.buildByHeader(getRequest());
        assertEquals(0, roleList.size());

        roleQueryHelperImpl.headerKey = "fess4";

        roleQueryHelperImpl.fessCipher = fessCipher;
        roleQueryHelperImpl.encryptedHeaderValue = true;
        roleList = roleQueryHelperImpl.buildByHeader(getRequest());
        assertEquals(0, roleList.size());
    }

    public void test_buildByCookie() {
        final RoleQueryHelperImpl roleQueryHelperImpl = new RoleQueryHelperImpl();

        List<String> roleList;
        Cookie cookie;

        roleList = roleQueryHelperImpl.buildByCookie(getRequest());
        assertEquals(0, roleList.size());

        cookie = new Cookie("aaa", "bbb");
        getRequest().addCookie(cookie);
        try {
            roleList = roleQueryHelperImpl.buildByCookie(getRequest());
            fail();
        } catch (final NullPointerException e) {
            // ok
        }

        roleQueryHelperImpl.cookieKey = "fess1";

        roleList = roleQueryHelperImpl.buildByCookie(getRequest());
        assertEquals(0, roleList.size());

        roleQueryHelperImpl.encryptedCookieValue = false;
        cookie = new Cookie("fess1", "xxx\nrole1,role2,role3");
        getRequest().addCookie(cookie);
        roleList = roleQueryHelperImpl.buildByCookie(getRequest());
        assertEquals(3, roleList.size());
        assertEquals("role1", roleList.get(0));
        assertEquals("role2", roleList.get(1));
        assertEquals("role3", roleList.get(2));

        roleQueryHelperImpl.cookieKey = "fess2";

        roleQueryHelperImpl.fessCipher = fessCipher;
        roleQueryHelperImpl.encryptedCookieValue = true;
        cookie = new Cookie("fess2",
                fessCipher.encryptoText("xxx\nrole1,role2,role3"));
        getRequest().addCookie(cookie);
        roleList = roleQueryHelperImpl.buildByCookie(getRequest());
        assertEquals(3, roleList.size());
        assertEquals("role1", roleList.get(0));
        assertEquals("role2", roleList.get(1));
        assertEquals("role3", roleList.get(2));

        roleQueryHelperImpl.cookieKey = "fess2x";

        roleQueryHelperImpl.fessCipher = fessCipher;
        roleQueryHelperImpl.encryptedCookieValue = true;
        cookie = new Cookie("fess2x", "fail");
        getRequest().addCookie(cookie);
        try {
            roleList = roleQueryHelperImpl.buildByCookie(getRequest());
            fail();
        } catch (final Exception e) {
            // ok 
        }

        roleQueryHelperImpl.cookieKey = "fess3";

        roleQueryHelperImpl.encryptedCookieValue = false;
        roleList = roleQueryHelperImpl.buildByCookie(getRequest());
        assertEquals(0, roleList.size());

        roleQueryHelperImpl.cookieKey = "fess4";

        roleQueryHelperImpl.fessCipher = fessCipher;
        roleQueryHelperImpl.encryptedCookieValue = true;
        roleList = roleQueryHelperImpl.buildByCookie(getRequest());
        assertEquals(0, roleList.size());
    }

    public void test_decodedRoleList() {

        final RoleQueryHelperImpl roleQueryHelperImpl = new RoleQueryHelperImpl();

        List<String> roleList;
        boolean encrypted;
        String value;

        encrypted = false;
        value = "";
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleList.size());

        encrypted = false;
        value = "role1";
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleList.size());

        encrypted = false;
        value = "role1,role2";
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleList.size());

        encrypted = false;
        value = "xxx\nrole1";
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(1, roleList.size());
        assertEquals("role1", roleList.get(0));

        encrypted = false;
        value = "xxx\nrole1,role2";
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(2, roleList.size());
        assertEquals("role1", roleList.get(0));
        assertEquals("role2", roleList.get(1));

        roleQueryHelperImpl.valueSeparator = "";

        encrypted = false;
        value = "";
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleList.size());

        encrypted = false;
        value = "role1";
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(1, roleList.size());
        assertEquals("role1", roleList.get(0));

        encrypted = false;
        value = "role1,role2";
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(2, roleList.size());
        assertEquals("role1", roleList.get(0));
        assertEquals("role2", roleList.get(1));

        encrypted = false;
        value = "role1,role2,role3";
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(3, roleList.size());
        assertEquals("role1", roleList.get(0));
        assertEquals("role2", roleList.get(1));
        assertEquals("role3", roleList.get(2));
    }

    public void test_decodedRoleList_withCipher() {

        final RoleQueryHelperImpl roleQueryHelperImpl = new RoleQueryHelperImpl();
        roleQueryHelperImpl.fessCipher = fessCipher;

        List<String> roleList;
        boolean encrypted;
        String value;

        encrypted = true;
        value = fessCipher.encryptoText("");
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleList.size());

        encrypted = true;
        value = fessCipher.encryptoText("role1");
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleList.size());

        encrypted = true;
        value = fessCipher.encryptoText("role1,role2");
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleList.size());

        encrypted = true;
        value = fessCipher.encryptoText("xxx\nrole1");
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(1, roleList.size());
        assertEquals("role1", roleList.get(0));

        encrypted = true;
        value = fessCipher.encryptoText("xxx\nrole1,role2");
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(2, roleList.size());
        assertEquals("role1", roleList.get(0));
        assertEquals("role2", roleList.get(1));

        roleQueryHelperImpl.valueSeparator = "";

        encrypted = true;
        value = fessCipher.encryptoText("");
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(0, roleList.size());

        encrypted = true;
        value = fessCipher.encryptoText("role1");
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(1, roleList.size());
        assertEquals("role1", roleList.get(0));

        encrypted = true;
        value = fessCipher.encryptoText("role1,role2");
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(2, roleList.size());
        assertEquals("role1", roleList.get(0));
        assertEquals("role2", roleList.get(1));

        encrypted = true;
        value = fessCipher.encryptoText("role1,role2,role3");
        roleList = roleQueryHelperImpl.decodedRoleList(value, encrypted);
        assertEquals(3, roleList.size());
        assertEquals("role1", roleList.get(0));
        assertEquals("role2", roleList.get(1));
        assertEquals("role3", roleList.get(2));
    }

}