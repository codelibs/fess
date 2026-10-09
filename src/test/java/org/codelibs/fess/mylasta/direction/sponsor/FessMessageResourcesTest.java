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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;

import org.codelibs.fess.filter.FessPrepareFilter;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.ruts.message.MessageResources;

public class FessMessageResourcesTest extends UnitFessTestCase {

    private static final String LABEL_KEY = "labels.search_cache_msg"; // in fess_label, which fess_message extends

    private static final String MESSAGE_KEY = "errors.invalid_query_parse_error"; // in fess_message

    private static final String[] LANGUAGES =
            { "ja", "de", "fr", "es", "it", "ko", "ru", "pt", "nl", "pl", "tr", "zh", "id", "hi", "ar", "bg", "ca", "cs", "da", "el" };

    @Test
    public void test_firstLookupsInParallel_findLabelsAndMessages() throws Exception {
        // A locale nothing has used yet is loaded by whichever request comes first, while the others
        // read it. Every round uses a locale of its own so that each round is a first use.
        final MessageResources resources = new FessMessageResources();
        final List<String> failures = new CopyOnWriteArrayList<>();
        for (int round = 0; round < LANGUAGES.length; round++) {
            final Locale locale = new Locale(LANGUAGES[round], "Z" + round);
            final int threads = 32;
            final CyclicBarrier start = new CyclicBarrier(threads);
            final List<Thread> list = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                final boolean label = i % 2 == 0;
                final String key = label ? LABEL_KEY : MESSAGE_KEY;
                final Thread thread = new Thread(() -> {
                    try {
                        start.await();
                        final String message =
                                label ? resources.getMessage(locale, key, new Object[] { "u", "c" }) : resources.getMessage(locale, key);
                        if (message == null) {
                            failures.add(locale + " " + key + ": not found");
                        }
                    } catch (final Exception e) {
                        failures.add(locale + " " + key + ": " + e);
                    }
                });
                list.add(thread);
                thread.start();
            }
            for (final Thread thread : list) {
                thread.join();
            }
        }
        assertTrue(failures.isEmpty(), failures.size() + " lookups failed: " + failures.subList(0, Math.min(5, failures.size())));
    }

    @Test
    public void test_labelsAndMessagesAreFoundThroughTheExtends() {
        final MessageResources resources = new FessMessageResources();
        final Locale locale = new Locale("de", "DE", "T");
        final String label = resources.getMessage(locale, LABEL_KEY, new Object[] { "http://example.com/", "2026-10-09" });
        assertTrue(label.contains("http://example.com/") && label.contains("2026-10-09"), label);
        assertNotNull(resources.getMessage(locale, MESSAGE_KEY));
        // the same bundle again, from the linked cache
        assertNotNull(resources.getMessage(locale, MESSAGE_KEY));
        assertNull(resources.getMessage(locale, "labels.no_such_key_zzz"));
    }

    @Test
    public void test_prepareFilterUsesFessMessageResources() {
        assertTrue(new FessPrepareFilter() {
            MessageResources create() {
                return newMessageResources();
            }
        }.create() instanceof FessMessageResources);
    }

    @Test
    public void test_webXmlDeclaresTheFessPrepareFilter() throws Exception {
        final String webXml = Files.readString(Paths.get("src/main/webapp/WEB-INF/web.xml"));
        assertTrue(webXml.contains("<filter-class>" + FessPrepareFilter.class.getName() + "</filter-class>"));
        assertFalse(webXml.contains("<filter-class>org.lastaflute.web.servlet.filter.LastaPrepareFilter</filter-class>"));
    }
}
