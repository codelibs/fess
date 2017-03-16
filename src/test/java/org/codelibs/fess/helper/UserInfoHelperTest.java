/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;

public class UserInfoHelperTest extends UnitFessTestCase {

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
}
