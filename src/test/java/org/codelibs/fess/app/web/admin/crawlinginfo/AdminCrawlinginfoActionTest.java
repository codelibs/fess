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
package org.codelibs.fess.app.web.admin.crawlinginfo;

import java.util.List;

import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class AdminCrawlinginfoActionTest extends UnitFessTestCase {

    @Test
    public void test_deleteall_requiresTheEditRole() throws Exception {
        final Secured secured = AdminCrawlinginfoAction.class.getMethod("deleteall").getAnnotation(Secured.class);
        assertEquals(List.of(AdminCrawlinginfoAction.ROLE), List.of(secured.value()));
    }

    @Test
    public void test_viewRoleStillOpensTheList() throws Exception {
        final Secured secured = AdminCrawlinginfoAction.class.getMethod("index").getAnnotation(Secured.class);
        assertEquals(List.of(AdminCrawlinginfoAction.ROLE, AdminCrawlinginfoAction.ROLE + "-view"), List.of(secured.value()));
    }
}
