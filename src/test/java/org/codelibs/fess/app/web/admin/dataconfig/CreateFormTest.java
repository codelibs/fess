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
package org.codelibs.fess.app.web.admin.dataconfig;

import org.codelibs.fess.Constants;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class CreateFormTest extends UnitFessTestCase {

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new SystemHelper(), "systemHelper");
    }

    @Test
    public void test_initialize_setsScriptType() {
        final CreateForm form = new CreateForm();
        form.initialize();
        assertNotNull(form.handlerParameter);
        assertTrue(form.handlerParameter.contains("script_type=" + Constants.DEFAULT_SCRIPT));
    }

    @Test
    public void test_initialize_setsScriptType_webConfig() {
        final org.codelibs.fess.app.web.admin.webconfig.CreateForm form = new org.codelibs.fess.app.web.admin.webconfig.CreateForm();
        form.initialize();
        assertNotNull(form.configParameter);
        assertTrue(form.configParameter.contains("config.script.type=" + Constants.DEFAULT_SCRIPT));
    }

    @Test
    public void test_initialize_setsScriptType_fileConfig() {
        final org.codelibs.fess.app.web.admin.fileconfig.CreateForm form = new org.codelibs.fess.app.web.admin.fileconfig.CreateForm();
        form.initialize();
        assertNotNull(form.configParameter);
        assertTrue(form.configParameter.contains("config.script.type=" + Constants.DEFAULT_SCRIPT));
    }
}
