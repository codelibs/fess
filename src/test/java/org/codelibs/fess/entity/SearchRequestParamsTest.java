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
package org.codelibs.fess.entity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.servlet.request.SimpleRequestManager;

import jakarta.servlet.http.HttpServletRequest;

public class SearchRequestParamsTest extends UnitFessTestCase {

    private SearchRequestParams searchRequestParams;
    private TestSearchRequestParams testParams;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        testParams = new TestSearchRequestParams();
        searchRequestParams = testParams;
    }

    // Test for hasConditionQuery method
    public void test_hasConditionQuery_withQuery() {
        // Test with query parameter
        testParams.conditions.put(SearchRequestParams.AS_Q, new String[] { "test query" });
        assertTrue(searchRequestParams.hasConditionQuery());
    }

    public void test_hasConditionQuery_withEmptyQuery() {
        // Test with empty query parameter
        testParams.conditions.put(SearchRequestParams.AS_Q, new String[] { "" });
        assertFalse(searchRequestParams.hasConditionQuery());
    }

    public void test_hasConditionQuery_withBlankQuery() {
        // Test with blank query parameter
        testParams.conditions.put(SearchRequestParams.AS_Q, new String[] { "   " });
        assertFalse(searchRequestParams.hasConditionQuery());
    }

    public void test_hasConditionQuery_withMultipleEmptyQueries() {
        // Test with multiple empty strings
        testParams.conditions.put(SearchRequestParams.AS_Q, new String[] { "", "  ", null });
        assertFalse(searchRequestParams.hasConditionQuery());
    }

    public void test_hasConditionQuery_withExactPhraseQuery() {
        // Test with exact phrase query
        testParams.conditions.put(SearchRequestParams.AS_EPQ, new String[] { "exact phrase" });
        assertTrue(searchRequestParams.hasConditionQuery());
    }

    public void test_hasConditionQuery_withOrQuery() {
        // Test with OR query
        testParams.conditions.put(SearchRequestParams.AS_OQ, new String[] { "option1 option2" });
        assertTrue(searchRequestParams.hasConditionQuery());
    }

    public void test_hasConditionQuery_withNegativeQuery() {
        // Test with negative query
        testParams.conditions.put(SearchRequestParams.AS_NQ, new String[] { "exclude this" });
        assertTrue(searchRequestParams.hasConditionQuery());
    }

    public void test_hasConditionQuery_withTimestamp() {
        // Test with timestamp
        testParams.conditions.put(SearchRequestParams.AS_TIMESTAMP, new String[] { "2024-01-01" });
        assertTrue(searchRequestParams.hasConditionQuery());
    }

    public void test_hasConditionQuery_withSiteSearch() {
        // Test with site search
        testParams.conditions.put(SearchRequestParams.AS_SITESEARCH, new String[] { "example.com" });
        assertTrue(searchRequestParams.hasConditionQuery());
    }

    public void test_hasConditionQuery_withFileType() {
        // Test with file type
        testParams.conditions.put(SearchRequestParams.AS_FILETYPE, new String[] { "pdf" });
        assertTrue(searchRequestParams.hasConditionQuery());
    }

    public void test_hasConditionQuery_withMultipleConditions() {
        // Test with multiple conditions
        testParams.conditions.put(SearchRequestParams.AS_Q, new String[] { "search term" });
        testParams.conditions.put(SearchRequestParams.AS_FILETYPE, new String[] { "doc" });
        testParams.conditions.put(SearchRequestParams.AS_NQ, new String[] { "exclude" });
        assertTrue(searchRequestParams.hasConditionQuery());
    }

    public void test_hasConditionQuery_withNoConditions() {
        // Test with no conditions
        assertFalse(searchRequestParams.hasConditionQuery());
    }

    public void test_hasConditionQuery_withNullValues() {
        // Test with null values
        testParams.conditions.put(SearchRequestParams.AS_Q, null);
        assertFalse(searchRequestParams.hasConditionQuery());
    }

    public void test_hasConditionQuery_withEmptyArray() {
        // Test with empty array
        testParams.conditions.put(SearchRequestParams.AS_Q, new String[] {});
        assertFalse(searchRequestParams.hasConditionQuery());
    }

    // Test for isEmptyArray method
    public void test_isEmptyArray_withNull() {
        assertTrue(searchRequestParams.isEmptyArray(null));
    }

    public void test_isEmptyArray_withEmptyArray() {
        assertTrue(searchRequestParams.isEmptyArray(new String[] {}));
    }

    public void test_isEmptyArray_withBlankStrings() {
        assertTrue(searchRequestParams.isEmptyArray(new String[] { "", "  ", "\t", "\n" }));
    }

    public void test_isEmptyArray_withNullElements() {
        assertTrue(searchRequestParams.isEmptyArray(new String[] { null, null }));
    }

    public void test_isEmptyArray_withMixedBlankAndNull() {
        assertTrue(searchRequestParams.isEmptyArray(new String[] { null, "", "  " }));
    }

    public void test_isEmptyArray_withNonEmptyString() {
        assertFalse(searchRequestParams.isEmptyArray(new String[] { "value" }));
    }

    public void test_isEmptyArray_withMixedEmptyAndNonEmpty() {
        assertFalse(searchRequestParams.isEmptyArray(new String[] { "", "value", "  " }));
    }

    // Test for simplifyArray static method
    public void test_simplifyArray_withDuplicates() {
        String[] input = { "a", "b", "a", "c", "b" };
        String[] result = SearchRequestParams.simplifyArray(input);
        assertEquals(3, result.length);
        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
        assertEquals("c", result[2]);
    }

    public void test_simplifyArray_withBlanks() {
        String[] input = { "a", "", "b", "  ", null, "c" };
        String[] result = SearchRequestParams.simplifyArray(input);
        assertEquals(3, result.length);
        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
        assertEquals("c", result[2]);
    }

    public void test_simplifyArray_withAllBlanks() {
        String[] input = { "", "  ", null };
        String[] result = SearchRequestParams.simplifyArray(input);
        assertEquals(0, result.length);
    }

    public void test_simplifyArray_withNoDuplicates() {
        String[] input = { "a", "b", "c" };
        String[] result = SearchRequestParams.simplifyArray(input);
        assertEquals(3, result.length);
        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
        assertEquals("c", result[2]);
    }

    public void test_simplifyArray_withSingleElement() {
        String[] input = { "single" };
        String[] result = SearchRequestParams.simplifyArray(input);
        assertEquals(1, result.length);
        assertEquals("single", result[0]);
    }

    public void test_simplifyArray_withEmpty() {
        String[] input = {};
        String[] result = SearchRequestParams.simplifyArray(input);
        assertEquals(0, result.length);
    }

    // Test for getParamValueArray static method
    public void test_getParamValueArray() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameterValues("test", new String[] { "value1", "value2", "value1" });
        String[] result = SearchRequestParams.getParamValueArray(request, "test");
        assertEquals(2, result.length);
        assertEquals("value1", result[0]);
        assertEquals("value2", result[1]);
    }

    public void test_getParamValueArray_withNullParam() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String[] result = SearchRequestParams.getParamValueArray(request, "nonexistent");
        assertEquals(0, result.length);
    }

    public void test_getParamValueArray_withBlankValues() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameterValues("test", new String[] { "value", "", "  ", null });
        String[] result = SearchRequestParams.getParamValueArray(request, "test");
        assertEquals(1, result.length);
        assertEquals("value", result[0]);
    }

    // Test for createFacetInfo method
    public void test_createFacetInfo_withFieldsAndQueries() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameterValues("facet.field", new String[] { "field1", "field2" });
        request.setParameterValues("facet.query", new String[] { "query1", "query2" });
        request.setParameter("facet.size", "100");
        request.setParameter("facet.minDocCount", "5");
        request.setParameter("facet.sort", "count");
        request.setParameter("facet.missing", "other");

        FacetInfo facetInfo = searchRequestParams.createFacetInfo(request);
        assertNotNull(facetInfo);
        assertEquals(2, facetInfo.field.length);
        assertEquals("field1", facetInfo.field[0]);
        assertEquals("field2", facetInfo.field[1]);
        assertEquals(2, facetInfo.query.length);
        assertEquals("query1", facetInfo.query[0]);
        assertEquals("query2", facetInfo.query[1]);
        assertEquals(Integer.valueOf(100), facetInfo.size);
        assertEquals(Long.valueOf(5), facetInfo.minDocCount);
        assertEquals("count", facetInfo.sort);
        assertEquals("other", facetInfo.missing);
    }

    public void test_createFacetInfo_withOnlyFields() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameterValues("facet.field", new String[] { "field1" });

        FacetInfo facetInfo = searchRequestParams.createFacetInfo(request);
        assertNotNull(facetInfo);
        assertEquals(1, facetInfo.field.length);
        assertEquals("field1", facetInfo.field[0]);
        assertEquals(0, facetInfo.query.length);
        assertNull(facetInfo.size);
        assertNull(facetInfo.minDocCount);
    }

    public void test_createFacetInfo_withOnlyQueries() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameterValues("facet.query", new String[] { "query1" });

        FacetInfo facetInfo = searchRequestParams.createFacetInfo(request);
        assertNotNull(facetInfo);
        assertEquals(0, facetInfo.field.length);
        assertEquals(1, facetInfo.query.length);
        assertEquals("query1", facetInfo.query[0]);
    }

    public void test_createFacetInfo_withNoFieldsOrQueries() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        FacetInfo facetInfo = searchRequestParams.createFacetInfo(request);
        assertNull(facetInfo);
    }

    public void test_createFacetInfo_withBlankParameters() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameterValues("facet.field", new String[] { "field1" });
        request.setParameter("facet.size", "");
        request.setParameter("facet.minDocCount", "  ");

        FacetInfo facetInfo = searchRequestParams.createFacetInfo(request);
        assertNotNull(facetInfo);
        assertNull(facetInfo.size);
        assertNull(facetInfo.minDocCount);
    }

    // Test for createGeoInfo method
    public void test_createGeoInfo() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        GeoInfo geoInfo = searchRequestParams.createGeoInfo(request);
        assertNotNull(geoInfo);
    }

    // Test for getTrackTotalHits method
    public void test_getTrackTotalHits() {
        assertNull(searchRequestParams.getTrackTotalHits());
    }

    // Test for getMinScore method
    public void test_getMinScore() {
        assertNull(searchRequestParams.getMinScore());
    }

    // Test for getResponseFields method
    public void test_getResponseFields() {
        // Setup mock QueryFieldConfig
        QueryFieldConfig queryFieldConfig = new QueryFieldConfig() {
            @Override
            public String[] getResponseFields() {
                return new String[] { "field1", "field2", "field3" };
            }
        };
        ComponentUtil.register(queryFieldConfig, "queryFieldConfig");

        String[] responseFields = searchRequestParams.getResponseFields();
        assertNotNull(responseFields);
        assertEquals(3, responseFields.length);
        assertEquals("field1", responseFields[0]);
        assertEquals("field2", responseFields[1]);
        assertEquals("field3", responseFields[2]);
    }

    // Test implementation of abstract SearchRequestParams
    private static class TestSearchRequestParams extends SearchRequestParams {
        String query = "";
        Map<String, String[]> fields = new HashMap<>();
        Map<String, String[]> conditions = new HashMap<>();
        String[] languages = new String[0];
        GeoInfo geoInfo = null;
        FacetInfo facetInfo = null;
        HighlightInfo highlightInfo = null;
        String sort = "";
        int startPosition = 0;
        int pageSize = 10;
        int offset = 0;
        String[] extraQueries = new String[0];
        Map<String, Object> attributes = new HashMap<>();
        Locale locale = Locale.ENGLISH;
        SearchRequestType type = SearchRequestType.SEARCH;
        String similarDocHash = "";

        @Override
        public String getQuery() {
            return query;
        }

        @Override
        public Map<String, String[]> getFields() {
            return fields;
        }

        @Override
        public Map<String, String[]> getConditions() {
            return conditions;
        }

        @Override
        public String[] getLanguages() {
            return languages;
        }

        @Override
        public GeoInfo getGeoInfo() {
            return geoInfo;
        }

        @Override
        public FacetInfo getFacetInfo() {
            return facetInfo;
        }

        @Override
        public HighlightInfo getHighlightInfo() {
            return highlightInfo;
        }

        @Override
        public String getSort() {
            return sort;
        }

        @Override
        public int getStartPosition() {
            return startPosition;
        }

        @Override
        public int getPageSize() {
            return pageSize;
        }

        @Override
        public int getOffset() {
            return offset;
        }

        @Override
        public String[] getExtraQueries() {
            return extraQueries;
        }

        @Override
        public Object getAttribute(String name) {
            return attributes.get(name);
        }

        @Override
        public Locale getLocale() {
            return locale;
        }

        @Override
        public SearchRequestType getType() {
            return type;
        }

        @Override
        public String getSimilarDocHash() {
            return similarDocHash;
        }
    }

    // Mock HttpServletRequest for testing
    private static class MockHttpServletRequest implements HttpServletRequest {
        private Map<String, String[]> parameters = new HashMap<>();
        private Map<String, String> singleParameters = new HashMap<>();

        public void setParameterValues(String name, String[] values) {
            parameters.put(name, values);
        }

        public void setParameter(String name, String value) {
            singleParameters.put(name, value);
        }

        @Override
        public String[] getParameterValues(String name) {
            return parameters.get(name);
        }

        @Override
        public String getParameter(String name) {
            return singleParameters.get(name);
        }

        // All other methods throw UnsupportedOperationException
        @Override
        public Object getAttribute(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.util.Enumeration<String> getAttributeNames() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getCharacterEncoding() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCharacterEncoding(String env) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getContentLength() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getContentLengthLong() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getContentType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.ServletInputStream getInputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.util.Enumeration<String> getParameterNames() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> paramMap = new HashMap<>(parameters);
            for (Map.Entry<String, String> entry : singleParameters.entrySet()) {
                if (!paramMap.containsKey(entry.getKey())) {
                    paramMap.put(entry.getKey(), new String[] { entry.getValue() });
                }
            }
            return paramMap;
        }

        @Override
        public String getProtocol() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getScheme() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getServerName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getServerPort() {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.io.BufferedReader getReader() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRemoteAddr() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRemoteHost() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAttribute(String name, Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeAttribute(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Locale getLocale() {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.util.Enumeration<Locale> getLocales() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isSecure() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.RequestDispatcher getRequestDispatcher(String path) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getRemotePort() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getLocalName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getLocalAddr() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getLocalPort() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.ServletContext getServletContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.AsyncContext startAsync() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.AsyncContext startAsync(jakarta.servlet.ServletRequest servletRequest,
                jakarta.servlet.ServletResponse servletResponse) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isAsyncStarted() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isAsyncSupported() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.AsyncContext getAsyncContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.DispatcherType getDispatcherType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRequestId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getProtocolRequestId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.ServletConnection getServletConnection() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getAuthType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.http.Cookie[] getCookies() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getDateHeader(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getHeader(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.util.Enumeration<String> getHeaders(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.util.Enumeration<String> getHeaderNames() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getIntHeader(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getMethod() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getPathInfo() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getPathTranslated() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getContextPath() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getQueryString() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRemoteUser() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isUserInRole(String role) {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.security.Principal getUserPrincipal() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRequestedSessionId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRequestURI() {
            throw new UnsupportedOperationException();
        }

        @Override
        public StringBuffer getRequestURL() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getServletPath() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.http.HttpSession getSession(boolean create) {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.http.HttpSession getSession() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String changeSessionId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean authenticate(jakarta.servlet.http.HttpServletResponse response) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void login(String username, String password) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void logout() {
            throw new UnsupportedOperationException();
        }

        @Override
        public java.util.Collection<jakarta.servlet.http.Part> getParts() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.http.Part getPart(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends jakarta.servlet.http.HttpUpgradeHandler> T upgrade(Class<T> handlerClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.http.HttpServletMapping getHttpServletMapping() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.http.PushBuilder newPushBuilder() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<String, String> getTrailerFields() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isTrailerFieldsReady() {
            throw new UnsupportedOperationException();
        }
    }
}