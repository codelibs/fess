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
package org.codelibs.fess.app.web.base;

import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.response.HtmlResponse;

public class FessLoginActionTest extends UnitFessTestCase {

    /**
     * LoginAction.changePassword lands here once the forced password change succeeds. A user with
     * no admin role must reach the search page at the root: the JSP profile page it used to land
     * on is gone.
     */
    @Test
    public void test_redirectByUser_userWithoutAdminRolesLandsOnTheRoot() {
        final List<Class<?>> redirects = new ArrayList<>();
        final FessLoginAction action = new FessLoginAction() {
            {
                fessConfig = new FessConfig.SimpleImpl() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String[] getAuthenticationAdminRolesAsArray() {
                        return new String[] { "admin" };
                    }
                };
                systemHelper = new SystemHelper();
            }

            @Override
            protected HtmlResponse redirect(final Class<?> actionType) {
                redirects.add(actionType);
                return HtmlResponse.undefined();
            }
        };

        final HtmlResponse response = action.redirectByUser(new FessUserBean(new FessAdminActionTest.TestUser("taro", new String[0])));

        assertEquals("/", response.getRoutingPath());
        assertTrue(response.isRedirectTo());
        assertEquals(0, redirects.size());
    }
}
