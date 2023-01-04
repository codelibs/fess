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
package org.codelibs.fess.util;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.lastaflute.web.response.render.RenderData;

public class RenderDataUtilTest extends UnitFessTestCase {

    public void test_register() {
        RenderData data = new RenderData();

        RenderDataUtil.register(data, "key1", null);
        assertNull(data.getDataMap().get("key1"));

        RenderDataUtil.register(data, "key5", "");
        assertEquals(data.getDataMap().get("key5"), "");

    }

}
