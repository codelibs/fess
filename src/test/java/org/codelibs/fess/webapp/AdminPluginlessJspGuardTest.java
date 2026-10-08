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
package org.codelibs.fess.webapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Guards the admin screens that hide what needs the CodeLibs plugins on a search engine without
 * them ({@code fesenPluginless}).
 *
 * <p>A JSP is not compiled by the build and no unit test renders it, so a guard that is dropped,
 * moved, or closed in the wrong place goes unnoticed until an administrator of a plain OpenSearch
 * clicks a button that cannot work. Each screen below must keep its plugin-dependent control
 * inside a {@code <c:if test="${not fesenPluginless}">} block, and must not show it anywhere
 * else.</p>
 */
public class AdminPluginlessJspGuardTest {

    private static final String GUARD = "<c:if test=\"${not fesenPluginless}\">";

    private static final String VIEW_DIR = "src/main/webapp/WEB-INF/view/admin/";

    private static String read(final String jsp) throws Exception {
        return new String(Files.readAllBytes(Paths.get(VIEW_DIR + jsp)), StandardCharsets.UTF_8);
    }

    /** The text between each {@link #GUARD} and its closing tag, nested {@code c:if} elements included. */
    private static List<String> guardedBlocks(final String source) {
        final List<String> blocks = new ArrayList<>();
        int from = 0;
        while (true) {
            final int start = source.indexOf(GUARD, from);
            if (start < 0) {
                return blocks;
            }
            int depth = 1;
            int i = start + GUARD.length();
            while (depth > 0) {
                final int open = source.indexOf("<c:if", i);
                final int close = source.indexOf("</c:if>", i);
                assertTrue(close >= 0, "unclosed " + GUARD);
                if (open >= 0 && open < close) {
                    depth++;
                    i = open + "<c:if".length();
                } else {
                    depth--;
                    i = close + "</c:if>".length();
                }
            }
            blocks.add(source.substring(start + GUARD.length(), i - "</c:if>".length()));
            from = i;
        }
    }

    private static int count(final String source, final String text) {
        int count = 0;
        for (int i = source.indexOf(text); i >= 0; i = source.indexOf(text, i + text.length())) {
            count++;
        }
        return count;
    }

    /** Asserts that {@code marker} is in the page and that every occurrence of it is inside a guarded block. */
    private static void assertOnlyInsideAGuard(final String jsp, final String source, final String marker) {
        final int total = count(source, marker);
        final int guarded = guardedBlocks(source).stream().mapToInt(block -> count(block, marker)).sum();
        assertTrue(total > 0, jsp + ": " + marker + " is not on the page");
        assertEquals(total, guarded, jsp + ": every " + marker + " must sit inside a fesenPluginless guard");
    }

    @Test
    public void test_maintenance_hidesTheDictionaryResetAndTheDocumentIndexReload() throws Exception {
        final String jsp = "maintenance/admin_maintenance.jsp";
        final String source = read(jsp);

        assertEquals(2, guardedBlocks(source).size(), jsp);
        // the checkbox is hidden with its row, not disabled
        assertOnlyInsideAGuard(jsp, source, "key=\"labels.reset_dictionaries\"");
        assertOnlyInsideAGuard(jsp, source, "property=\"resetDictionaries\"");
        // the POST button of the reload card; the card title goes with it
        assertOnlyInsideAGuard(jsp, source, "name=\"reloadDocIndex\"");
        assertOnlyInsideAGuard(jsp, source, "key=\"labels.reload_doc_index\"");
        // what works without the plugins stays visible
        assertTrue(source.contains("name=\"reindexOnly\""), jsp);
        assertTrue(guardedBlocks(source).stream().noneMatch(block -> block.contains("name=\"reindexOnly\"")), jsp);
    }

    @Test
    public void test_docreport_hidesTheDuplicatesTab() throws Exception {
        final String jsp = "docreport/admin_docreport.jsp";
        final String source = read(jsp);

        assertEquals(1, guardedBlocks(source).size(), jsp);
        assertOnlyInsideAGuard(jsp, source, "fe:url('/admin/docreport/duplicate')");
        assertOnlyInsideAGuard(jsp, source, "key=\"labels.docreport_tab_duplicate\"");
        // the dormant tab is what a plugin-less engine lands on
        assertTrue(source.contains("fe:url('/admin/docreport/dormant')"), jsp);
        assertTrue(guardedBlocks(source).stream().noneMatch(block -> block.contains("/admin/docreport/dormant")), jsp);
    }

    @Test
    public void test_general_hidesTheResultCollapsedCheckbox() throws Exception {
        final String jsp = "general/admin_general.jsp";
        final String source = read(jsp);

        assertEquals(1, guardedBlocks(source).size(), jsp);
        assertOnlyInsideAGuard(jsp, source, "key=\"labels.result_collapsed\"");
        assertOnlyInsideAGuard(jsp, source, "property=\"resultCollapsed\"");
    }
}
