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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codelibs.fess.Constants;
import org.codelibs.fess.opensearch.config.exentity.PathMapping;
import org.codelibs.fess.unit.UnitFessTestCase;

public class PathMappingHelperTest extends UnitFessTestCase {

    public PathMappingHelper pathMappingHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        pathMappingHelper = new PathMappingHelper();
        pathMappingHelper.init();
    }

    public void test_setPathMappingList() {
        final String sessionId = "test";
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();

        assertNull(pathMappingHelper.getPathMappingList(sessionId));
        assertNull(pathMappingHelper.getPathMappingList(sessionId + "1"));
        pathMappingHelper.setPathMappingList(sessionId, pathMappingList);
        assertNotNull(pathMappingHelper.getPathMappingList(sessionId));
        assertNull(pathMappingHelper.getPathMappingList(sessionId + "1"));
        pathMappingHelper.removePathMappingList(sessionId);
        assertNull(pathMappingHelper.getPathMappingList(sessionId));
        assertNull(pathMappingHelper.getPathMappingList(sessionId + "1"));

    }

    public void test_replaceUrl() {
        final String sessionId = "test";
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        final PathMapping pathMapping = new PathMapping();
        pathMapping.setRegex("file:///home/");
        pathMapping.setReplacement("http://localhost/");
        pathMappingList.add(pathMapping);

        pathMappingHelper.setPathMappingList(sessionId, pathMappingList);

        final String url = "file:///home/user/";
        assertEquals("http://localhost/user/", pathMappingHelper.replaceUrl(sessionId, url));
    }

    public void test_replaceUrls() {
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        final PathMapping pathMapping = new PathMapping();
        pathMapping.setRegex("file:///home/");
        pathMapping.setReplacement("http://localhost/");
        pathMappingList.add(pathMapping);

        pathMappingHelper.cachedPathMappingList = pathMappingList;

        String text = "\"file:///home/\"";
        assertEquals("\"http://localhost/\"", pathMappingHelper.replaceUrls(text));

        text = "\"file:///home/user/\"";
        assertEquals("\"http://localhost/user/\"", pathMappingHelper.replaceUrls(text));

        text = "\"aaafile:///home/user/\"";
        assertEquals("\"aaahttp://localhost/user/\"", pathMappingHelper.replaceUrls(text));

        text = "aaa\"file:///home/user/\"bbb";
        assertEquals("aaa\"http://localhost/user/\"bbb", pathMappingHelper.replaceUrls(text));
    }

    public void test_setPathMappingList_withNullSessionId() {
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        pathMappingHelper.setPathMappingList(null, pathMappingList);
        assertNull(pathMappingHelper.getPathMappingList("test"));
    }

    public void test_setPathMappingList_withNullList() {
        final String sessionId = "test";
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        pathMappingHelper.setPathMappingList(sessionId, pathMappingList);
        assertNotNull(pathMappingHelper.getPathMappingList(sessionId));

        pathMappingHelper.setPathMappingList(sessionId, null);
        assertNull(pathMappingHelper.getPathMappingList(sessionId));
    }

    public void test_removePathMappingList_withNullSessionId() {
        pathMappingHelper.removePathMappingList(null);
    }

    public void test_getPathMappingList_withNullSessionId() {
        assertNull(pathMappingHelper.getPathMappingList(null));
    }

    public void test_replaceUrl_withNullSessionId() {
        final String url = "file:///home/user/";
        assertEquals(url, pathMappingHelper.replaceUrl((String) null, url));
    }

    public void test_replaceUrl_withNonExistentSessionId() {
        final String url = "file:///home/user/";
        assertEquals(url, pathMappingHelper.replaceUrl("nonexistent", url));
    }

    public void test_replaceUrl_withEmptyList() {
        final String sessionId = "test";
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        pathMappingHelper.setPathMappingList(sessionId, pathMappingList);

        final String url = "file:///home/user/";
        assertEquals(url, pathMappingHelper.replaceUrl(sessionId, url));
    }

    public void test_replaceUrl_withMultiplePathMappings() {
        final String sessionId = "test";
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();

        final PathMapping pathMapping1 = new PathMapping();
        pathMapping1.setRegex("file:///home/");
        pathMapping1.setReplacement("http://localhost/");
        pathMappingList.add(pathMapping1);

        final PathMapping pathMapping2 = new PathMapping();
        pathMapping2.setRegex("http://localhost/");
        pathMapping2.setReplacement("https://server/");
        pathMappingList.add(pathMapping2);

        pathMappingHelper.setPathMappingList(sessionId, pathMappingList);

        final String url = "file:///home/user/";
        assertEquals("https://server/user/", pathMappingHelper.replaceUrl(sessionId, url));
    }

    public void test_replaceUrls_withNullReplacement() {
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        final PathMapping pathMapping = new PathMapping();
        pathMapping.setRegex("file:///home/");
        pathMapping.setReplacement(null);
        pathMappingList.add(pathMapping);

        pathMappingHelper.cachedPathMappingList = pathMappingList;

        String text = "\"file:///home/user/\"";
        assertEquals("\"user/\"", pathMappingHelper.replaceUrls(text));
    }

    public void test_replaceUrls_withEmptyReplacement() {
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        final PathMapping pathMapping = new PathMapping();
        pathMapping.setRegex("file:///home/");
        pathMapping.setReplacement("");
        pathMappingList.add(pathMapping);

        pathMappingHelper.cachedPathMappingList = pathMappingList;

        String text = "\"file:///home/user/\"";
        assertEquals("\"user/\"", pathMappingHelper.replaceUrls(text));
    }

    public void test_replaceUrls_withNullCachedList() {
        pathMappingHelper.cachedPathMappingList = null;
        String text = "\"file:///home/user/\"";
        assertEquals(text, pathMappingHelper.replaceUrls(text));
    }

    public void test_replaceUrl_withNullCachedList() {
        pathMappingHelper.cachedPathMappingList = null;
        String url = "file:///home/user/";
        String result = pathMappingHelper.replaceUrl(url);
        assertNotNull(result);
    }

    public void test_replaceUrls_withMultipleQuotedStrings() {
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        final PathMapping pathMapping = new PathMapping();
        pathMapping.setRegex("file:///home/");
        pathMapping.setReplacement("http://localhost/");
        pathMappingList.add(pathMapping);

        pathMappingHelper.cachedPathMappingList = pathMappingList;

        String text = "\"file:///home/user1/\" and \"file:///home/user2/\"";
        assertEquals("\"http://localhost/user1/\" and \"http://localhost/user2/\"", pathMappingHelper.replaceUrls(text));
    }

    public void test_replaceUrls_withNoQuotes() {
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        final PathMapping pathMapping = new PathMapping();
        pathMapping.setRegex("file:///home/");
        pathMapping.setReplacement("http://localhost/");
        pathMappingList.add(pathMapping);

        pathMappingHelper.cachedPathMappingList = pathMappingList;

        String text = "file:///home/user/";
        assertEquals("file:///home/user/", pathMappingHelper.replaceUrls(text));
    }

    public void test_getProcessTypeList_crawlerMode() {
        System.setProperty("lasta.env", Constants.EXECUTE_TYPE_CRAWLER);
        try {
            final List<String> ptList = pathMappingHelper.getProcessTypeList();
            assertEquals(1, ptList.size());
            assertEquals(Constants.PROCESS_TYPE_REPLACE, ptList.get(0));
        } finally {
            System.clearProperty("lasta.env");
        }
    }

    public void test_getProcessTypeList_defaultMode() {
        System.clearProperty("lasta.env");
        final List<String> ptList = pathMappingHelper.getProcessTypeList();
        assertEquals(2, ptList.size());
        assertTrue(ptList.contains(Constants.PROCESS_TYPE_DISPLAYING));
        assertTrue(ptList.contains(Constants.PROCESS_TYPE_BOTH));
    }

    public void test_getProcessTypeList_nonCrawlerMode() {
        System.setProperty("lasta.env", "other");
        try {
            final List<String> ptList = pathMappingHelper.getProcessTypeList();
            assertEquals(2, ptList.size());
            assertTrue(ptList.contains(Constants.PROCESS_TYPE_DISPLAYING));
            assertTrue(ptList.contains(Constants.PROCESS_TYPE_BOTH));
        } finally {
            System.clearProperty("lasta.env");
        }
    }

    public void test_createPathMatcher_encodeUrl() {
        final Pattern pattern = Pattern.compile("test");
        final Matcher matcher = pattern.matcher("test");
        final BiFunction<String, Matcher, String> pathMatcher = pathMappingHelper.createPathMatcher(matcher, "function:encodeUrl");

        String result = pathMatcher.apply("http://example.com/test path", matcher);
        assertEquals("http://example.com/test+path", result);
    }

    public void test_createPathMatcher_normalReplacement() {
        final Pattern pattern = Pattern.compile("test");
        final Matcher matcher = pattern.matcher("test");
        final BiFunction<String, Matcher, String> pathMatcher = pathMappingHelper.createPathMatcher(matcher, "replacement");

        String result = pathMatcher.apply("test", matcher);
        assertEquals("replacement", result);
    }

    public void test_load_withoutPathMappingBhv() {
        final PathMappingHelper helper = new PathMappingHelper();
        int result = helper.load();
        assertEquals(0, result);
    }

    public void test_init_called() {
        final PathMappingHelper helper = new PathMappingHelper();
        helper.init();
        assertNotNull(helper.cachedPathMappingList);
    }

    public void test_replaceUrl_withEmptyUrl() {
        final String sessionId = "test";
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        final PathMapping pathMapping = new PathMapping();
        pathMapping.setRegex("file:///home/");
        pathMapping.setReplacement("http://localhost/");
        pathMappingList.add(pathMapping);

        pathMappingHelper.setPathMappingList(sessionId, pathMappingList);

        final String url = "";
        assertEquals("", pathMappingHelper.replaceUrl(sessionId, url));
    }

    public void test_replaceUrls_withEmptyText() {
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        final PathMapping pathMapping = new PathMapping();
        pathMapping.setRegex("file:///home/");
        pathMapping.setReplacement("http://localhost/");
        pathMappingList.add(pathMapping);

        pathMappingHelper.cachedPathMappingList = pathMappingList;

        String text = "";
        assertEquals("", pathMappingHelper.replaceUrls(text));
    }

    public void test_replaceUrl_displayMode() {
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        final PathMapping pathMapping = new PathMapping();
        pathMapping.setRegex("file:///home/");
        pathMapping.setReplacement("http://localhost/");
        pathMappingList.add(pathMapping);

        pathMappingHelper.cachedPathMappingList = pathMappingList;

        final String url = "file:///home/user/";
        assertEquals("http://localhost/user/", pathMappingHelper.replaceUrl(url));
    }

    public void test_replaceUrl_displayMode_withEmptyUrl() {
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        final PathMapping pathMapping = new PathMapping();
        pathMapping.setRegex("file:///home/");
        pathMapping.setReplacement("http://localhost/");
        pathMappingList.add(pathMapping);

        pathMappingHelper.cachedPathMappingList = pathMappingList;

        final String url = "";
        assertEquals("", pathMappingHelper.replaceUrl(url));
    }

    public void test_replaceUrl_withComplexRegex() {
        final String sessionId = "test";
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        final PathMapping pathMapping = new PathMapping();
        pathMapping.setRegex("file:///(\\w+)/");
        pathMapping.setReplacement("http://localhost/$1/");
        pathMappingList.add(pathMapping);

        pathMappingHelper.setPathMappingList(sessionId, pathMappingList);

        final String url = "file:///home/user/";
        assertEquals("http://localhost/home/user/", pathMappingHelper.replaceUrl(sessionId, url));
    }

    public void test_replaceUrls_withSpecialCharacters() {
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        final PathMapping pathMapping = new PathMapping();
        pathMapping.setRegex("file:///home/");
        pathMapping.setReplacement("http://localhost/");
        pathMappingList.add(pathMapping);

        pathMappingHelper.cachedPathMappingList = pathMappingList;

        String text = "\"file:///home/user with spaces/\"";
        assertEquals("\"http://localhost/user with spaces/\"", pathMappingHelper.replaceUrls(text));
    }

    public void test_replaceUrl_withSpecialCharacters() {
        final String sessionId = "test";
        final List<PathMapping> pathMappingList = new ArrayList<PathMapping>();
        final PathMapping pathMapping = new PathMapping();
        pathMapping.setRegex("file:///home/");
        pathMapping.setReplacement("http://localhost/");
        pathMappingList.add(pathMapping);

        pathMappingHelper.setPathMappingList(sessionId, pathMappingList);

        final String url = "file:///home/user with spaces/";
        assertEquals("http://localhost/user with spaces/", pathMappingHelper.replaceUrl(sessionId, url));
    }
}