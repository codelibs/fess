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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.LastaWebKey;

import jakarta.servlet.http.HttpServletRequest;

public class FessRequestManagerTest extends UnitFessTestCase {

    /** Records what the manager keeps on the request; the session save of LastaFlute is not reached in these cases. */
    private static class TestRequestManager extends FessRequestManager {
        private final String path;
        private final HttpServletRequest request;
        private final Map<String, Object> attributes = new HashMap<>();

        TestRequestManager(final String path, final HttpServletRequest request) {
            this.path = path;
            this.request = request;
        }

        @Override
        public String getRequestPath() {
            return path;
        }

        @Override
        public HttpServletRequest getRequest() {
            return request;
        }

        @Override
        public void setAttribute(final String key, final Object value) {
            attributes.put(key, value);
        }
    }

    @Test
    public void test_apiRequestWithoutSession_keepsLocaleOnRequest() {
        final MockletHttpServletRequest request = getMockRequest();
        final TestRequestManager manager = new TestRequestManager("/api/admin/badword/settings", request);
        assertTrue(manager.isApiRequest());
        assertFalse(manager.hasSession());
        manager.saveUserLocaleToSession(Locale.JAPANESE);
        assertEquals(Locale.JAPANESE, manager.attributes.get(LastaWebKey.USER_LOCALE_KEY));
        assertNull(request.getSession(false));
    }

    @Test
    public void test_hasSession() {
        final MockletHttpServletRequest request = getMockRequest();
        final TestRequestManager manager = new TestRequestManager("/api/admin/badword/settings", request);
        assertFalse(manager.hasSession());
        request.getSession(true);
        assertTrue(manager.hasSession());
    }

    @Test
    public void test_isApiRequest() {
        assertTrue(new TestRequestManager("/api/v1/x", getMockRequest()).isApiRequest());
        assertFalse(new TestRequestManager("/apiv2", getMockRequest()).isApiRequest());
        assertFalse(new TestRequestManager("/login/", getMockRequest()).isApiRequest());
        assertFalse(new TestRequestManager(null, getMockRequest()).isApiRequest());
    }
}
