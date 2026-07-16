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
package org.codelibs.fess.app.web.admin.general;

import java.lang.reflect.Method;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class AdminGeneralActionTest extends UnitFessTestCase {

    @Test
    public void test_isResultCollapsedEditable_cloud() throws Exception {
        assertFalse(invokeIsResultCollapsedEditable("cloud"));
    }

    @Test
    public void test_isResultCollapsedEditable_aws() throws Exception {
        assertFalse(invokeIsResultCollapsedEditable("aws"));
    }

    @Test
    public void test_isResultCollapsedEditable_default() throws Exception {
        assertTrue(invokeIsResultCollapsedEditable("default"));
    }

    @Test
    public void test_isResultCollapsedEditable_other() throws Exception {
        assertTrue(invokeIsResultCollapsedEditable("unknown"));
    }

    private boolean invokeIsResultCollapsedEditable(final String fesenType) throws Exception {
        final FessConfig fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getSearchEngineType() {
                return fesenType;
            }
        };
        final Method method = AdminGeneralAction.class.getDeclaredMethod("isResultCollapsedEditable", FessConfig.class);
        method.setAccessible(true);
        return ((Boolean) method.invoke(null, fessConfig)).booleanValue();
    }
}
