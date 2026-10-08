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
package org.codelibs.fess.app.web.admin.docreport;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.app.service.DocumentReportService;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.response.HtmlResponse;

public class AdminDocreportActionTest extends UnitFessTestCase {

    @Test
    public void test_onlineHelpName() {
        // the name of docreport-guide.rst in fess-docs
        assertEquals("docreport", ComponentUtil.getFessConfig().getOnlineHelpNameDocreport());
    }

    @Test
    public void test_index_showsTheDuplicateTabWhenDuplicatesCanBeReported() throws Exception {
        final List<String> shown = new ArrayList<>();
        final DuplicateForm form = new DuplicateForm();
        form.url = "smb://server/";

        createAction(true, shown).index(form);

        assertEquals(List.of("duplicate:smb://server/"), shown);
    }

    @Test
    public void test_index_landsOnTheDormantTabWhenDuplicatesCannotBeReported() throws Exception {
        // the duplicate tab is hidden, and the index has no content signature to report from
        final List<String> shown = new ArrayList<>();
        final DuplicateForm form = new DuplicateForm();
        form.url = "smb://server/";

        createAction(false, shown).index(form);

        assertEquals(List.of("dormant:smb://server/"), shown);
    }

    private AdminDocreportAction createAction(final boolean duplicateReportAvailable, final List<String> shown) throws Exception {
        final AdminDocreportAction action = new AdminDocreportAction() {
            @Override
            public HtmlResponse duplicate(final DuplicateForm form) {
                shown.add("duplicate:" + form.url);
                return HtmlResponse.undefined();
            }

            @Override
            public HtmlResponse dormant(final DormantForm form) {
                shown.add("dormant:" + form.url);
                return HtmlResponse.undefined();
            }
        };
        final Field field = AdminDocreportAction.class.getDeclaredField("documentReportService");
        field.setAccessible(true);
        field.set(action, new DocumentReportService() {
            @Override
            public boolean isDuplicateReportAvailable() {
                return duplicateReportAvailable;
            }
        });
        return action;
    }

    @Test
    public void test_parseDays() {
        assertEquals(365, AdminDocreportAction.parseDays(null, 365));
        assertEquals(365, AdminDocreportAction.parseDays("", 365));
        assertEquals(30, AdminDocreportAction.parseDays(" 30 ", 365));
        assertEquals(1, AdminDocreportAction.parseDays("1", 365));
        assertEquals(36500, AdminDocreportAction.parseDays("36500", 365));
        assertEquals(365, AdminDocreportAction.parseDays("0", 365));
        assertEquals(365, AdminDocreportAction.parseDays("-5", 365));
        assertEquals(365, AdminDocreportAction.parseDays("36501", 365));
        assertEquals(365, AdminDocreportAction.parseDays("abc", 365));
        assertEquals(365, AdminDocreportAction.parseDays("99999999999", 365));
    }

    @Test
    public void test_parsePage() {
        assertEquals(1, AdminDocreportAction.parsePage(null));
        assertEquals(1, AdminDocreportAction.parsePage("x"));
        assertEquals(1, AdminDocreportAction.parsePage("0"));
        assertEquals(1, AdminDocreportAction.parsePage("-3"));
        assertEquals(4, AdminDocreportAction.parsePage("4"));
    }

    @Test
    public void test_normalizeUrl() {
        assertNull(AdminDocreportAction.normalizeUrl(null));
        assertNull(AdminDocreportAction.normalizeUrl("  "));
        assertEquals("smb://server/", AdminDocreportAction.normalizeUrl(" smb://server/ "));
    }

    @Test
    public void test_buildQuery() {
        assertEquals("", AdminDocreportAction.buildQuery(null, null, false));
        assertEquals("url=smb%3A%2F%2Fserver%2Fa+b%26c", AdminDocreportAction.buildQuery("smb://server/a b&c", null, false));
        assertEquals("days=30", AdminDocreportAction.buildQuery(null, 30, false));
        assertEquals("url=x&days=30&unclicked=true", AdminDocreportAction.buildQuery("x", 30, true));
        assertEquals("unclicked=true", AdminDocreportAction.buildQuery(null, null, true));
    }
}
