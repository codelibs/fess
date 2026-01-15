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
package org.codelibs.fess.api.chat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Unit tests for {@link ChatApiManager}.
 * Tests the RAG chat API endpoints including SSE streaming.
 */
public class ChatApiManagerTest extends UnitFessTestCase {

    private ChatApiManager chatApiManager;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        chatApiManager = new ChatApiManager();
    }

    @Test
    public void test_defaultConstructor() {
        final ChatApiManager manager = new ChatApiManager();
        assertNotNull(manager);
    }

    @Test
    public void test_matches_ragChatDisabled() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isRagChatEnabled() {
                return false;
            }
        });

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/chat");

        assertFalse(chatApiManager.matches(request));
    }

    @Test
    public void test_matches_ragChatEnabled_validPath() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isRagChatEnabled() {
                return true;
            }
        });

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/chat");

        assertTrue(chatApiManager.matches(request));
    }

    @Test
    public void test_matches_ragChatEnabled_streamPath() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isRagChatEnabled() {
                return true;
            }
        });

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/chat/stream");

        assertTrue(chatApiManager.matches(request));
    }

    @Test
    public void test_matches_ragChatEnabled_invalidPath() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isRagChatEnabled() {
                return true;
            }
        });

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/v1/search");

        assertFalse(chatApiManager.matches(request));
    }

    @Test
    public void test_createSuccessResponse_basic() {
        final Map<String, Object> response = chatApiManager.createSuccessResponse("session-123", "Hello, world!", null);

        assertEquals("ok", response.get("status"));
        assertEquals("session-123", response.get("sessionId"));
        assertEquals("Hello, world!", response.get("content"));
        assertFalse(response.containsKey("sources"));
    }

    @Test
    public void test_createSuccessResponse_withSources() {
        final List<ChatSource> sources = new ArrayList<>();
        final ChatSource source1 = new ChatSource();
        source1.setUrl("https://example.com/page1");
        source1.setTitle("Page 1");
        sources.add(source1);

        final Map<String, Object> response = chatApiManager.createSuccessResponse("session-123", "Response content", sources);

        assertEquals("ok", response.get("status"));
        assertEquals("session-123", response.get("sessionId"));
        assertEquals("Response content", response.get("content"));
        assertTrue(response.containsKey("sources"));
        assertEquals(sources, response.get("sources"));
    }

    @Test
    public void test_createSuccessResponse_nullContent() {
        final Map<String, Object> response = chatApiManager.createSuccessResponse("session-123", null, null);

        assertEquals("ok", response.get("status"));
        assertEquals("session-123", response.get("sessionId"));
        assertNull(response.get("content"));
    }

    @Test
    public void test_createSuccessResponse_emptySessionId() {
        final Map<String, Object> response = chatApiManager.createSuccessResponse("", "Content", null);

        assertEquals("ok", response.get("status"));
        assertEquals("", response.get("sessionId"));
        assertEquals("Content", response.get("content"));
    }

    @Test
    public void test_createErrorResponse_basic() {
        final Map<String, Object> response = chatApiManager.createErrorResponse("Something went wrong");

        assertEquals("error", response.get("status"));
        assertEquals("Something went wrong", response.get("message"));
    }

    @Test
    public void test_createErrorResponse_emptyMessage() {
        final Map<String, Object> response = chatApiManager.createErrorResponse("");

        assertEquals("error", response.get("status"));
        assertEquals("", response.get("message"));
    }

    @Test
    public void test_createErrorResponse_nullMessage() {
        final Map<String, Object> response = chatApiManager.createErrorResponse(null);

        assertEquals("error", response.get("status"));
        assertNull(response.get("message"));
    }

    @Test
    public void test_sendSseEvent_basic() {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);

        chatApiManager.sendSseEvent(writer, "test", Map.of("key", "value"));

        final String output = stringWriter.toString();
        assertTrue(output.contains("event: test"));
        assertTrue(output.contains("data:"));
        assertTrue(output.contains("\"key\":\"value\""));
    }

    @Test
    public void test_sendSseEvent_session() {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);

        chatApiManager.sendSseEvent(writer, "session", Map.of("sessionId", "abc-123"));

        final String output = stringWriter.toString();
        assertTrue(output.contains("event: session"));
        assertTrue(output.contains("\"sessionId\":\"abc-123\""));
    }

    @Test
    public void test_sendSseEvent_phase() {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);

        chatApiManager.sendSseEvent(writer, "phase", Map.of("phase", "search", "status", "start", "message", "Searching documents"));

        final String output = stringWriter.toString();
        assertTrue(output.contains("event: phase"));
        assertTrue(output.contains("\"phase\":\"search\""));
        assertTrue(output.contains("\"status\":\"start\""));
        assertTrue(output.contains("\"message\":\"Searching documents\""));
    }

    @Test
    public void test_sendSseEvent_chunk() {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);

        chatApiManager.sendSseEvent(writer, "chunk", Map.of("content", "Hello "));

        final String output = stringWriter.toString();
        assertTrue(output.contains("event: chunk"));
        assertTrue(output.contains("\"content\":\"Hello \""));
    }

    @Test
    public void test_sendSseEvent_error() {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);

        chatApiManager.sendSseEvent(writer, "error", Map.of("phase", "generation", "message", "Model unavailable"));

        final String output = stringWriter.toString();
        assertTrue(output.contains("event: error"));
        assertTrue(output.contains("\"phase\":\"generation\""));
        assertTrue(output.contains("\"message\":\"Model unavailable\""));
    }

    @Test
    public void test_sendSseEvent_done() {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);

        chatApiManager.sendSseEvent(writer, "done", Map.of("sessionId", "session-123"));

        final String output = stringWriter.toString();
        assertTrue(output.contains("event: done"));
        assertTrue(output.contains("\"sessionId\":\"session-123\""));
    }

    @Test
    public void test_sendSseEvent_done_withHtmlContent() {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);

        chatApiManager.sendSseEvent(writer, "done", Map.of("sessionId", "session-123", "htmlContent", "<p>Response</p>"));

        final String output = stringWriter.toString();
        assertTrue(output.contains("event: done"));
        assertTrue(output.contains("\"sessionId\":\"session-123\""));
        assertTrue(output.contains("htmlContent"));
    }

    @Test
    public void test_sendSseEvent_specialCharacters() {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);

        chatApiManager.sendSseEvent(writer, "chunk", Map.of("content", "Special chars: \"quotes\" and \\ backslash"));

        final String output = stringWriter.toString();
        assertTrue(output.contains("event: chunk"));
        // JSON should properly escape special characters
        assertTrue(output.contains("data:"));
    }

    @Test
    public void test_sendSseEvent_unicode() {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);

        chatApiManager.sendSseEvent(writer, "chunk", Map.of("content", "Unicode: 日本語 中文"));

        final String output = stringWriter.toString();
        assertTrue(output.contains("event: chunk"));
        assertTrue(output.contains("日本語") || output.contains("\\u")); // Either raw or escaped
    }

    @Test
    public void test_sendSseEvent_emptyMap() {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);

        chatApiManager.sendSseEvent(writer, "test", Map.of());

        final String output = stringWriter.toString();
        assertTrue(output.contains("event: test"));
        assertTrue(output.contains("data: {}"));
    }

    @Test
    public void test_sendSseEvent_nestedData() {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);

        final Map<String, Object> nestedData = Map.of("outer", Map.of("inner", "value"));
        chatApiManager.sendSseEvent(writer, "test", nestedData);

        final String output = stringWriter.toString();
        assertTrue(output.contains("event: test"));
        assertTrue(output.contains("inner"));
        assertTrue(output.contains("value"));
    }

    @Test
    public void test_sendSseEvent_listData() {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);

        final List<String> items = List.of("item1", "item2", "item3");
        chatApiManager.sendSseEvent(writer, "test", Map.of("items", items));

        final String output = stringWriter.toString();
        assertTrue(output.contains("event: test"));
        assertTrue(output.contains("["));
        assertTrue(output.contains("item1"));
        assertTrue(output.contains("item2"));
        assertTrue(output.contains("item3"));
    }

    @Test
    public void test_chatSource_properties() {
        final ChatSource source = new ChatSource();

        assertNull(source.getUrl());
        assertNull(source.getTitle());
        assertNull(source.getSnippet());

        source.setUrl("https://example.com");
        source.setTitle("Example Page");
        source.setSnippet("This is a snippet...");

        assertEquals("https://example.com", source.getUrl());
        assertEquals("Example Page", source.getTitle());
        assertEquals("This is a snippet...", source.getSnippet());
    }

    @Test
    public void test_createSuccessResponse_emptySources() {
        final List<ChatSource> sources = new ArrayList<>();
        final Map<String, Object> response = chatApiManager.createSuccessResponse("session-123", "Content", sources);

        assertEquals("ok", response.get("status"));
        assertTrue(response.containsKey("sources"));
        assertEquals(sources, response.get("sources"));
    }

    /**
     * Simple mock HttpServletRequest for testing.
     */
    private static class MockHttpServletRequest extends jakarta.servlet.http.HttpServletRequestWrapper {
        private String servletPath;
        private String method = "POST";

        public MockHttpServletRequest() {
            super(new MockServletRequest());
        }

        @Override
        public String getServletPath() {
            return servletPath;
        }

        public void setServletPath(String servletPath) {
            this.servletPath = servletPath;
        }

        @Override
        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }
    }

    /**
     * Minimal ServletRequest implementation for mock wrapper.
     */
    private static class MockServletRequest implements jakarta.servlet.http.HttpServletRequest {
        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public java.util.Enumeration<String> getAttributeNames() {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String env) {
        }

        @Override
        public int getContentLength() {
            return 0;
        }

        @Override
        public long getContentLengthLong() {
            return 0;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public jakarta.servlet.ServletInputStream getInputStream() {
            return null;
        }

        @Override
        public String getParameter(String name) {
            return null;
        }

        @Override
        public java.util.Enumeration<String> getParameterNames() {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public String[] getParameterValues(String name) {
            return null;
        }

        @Override
        public java.util.Map<String, String[]> getParameterMap() {
            return java.util.Collections.emptyMap();
        }

        @Override
        public String getProtocol() {
            return null;
        }

        @Override
        public String getScheme() {
            return null;
        }

        @Override
        public String getServerName() {
            return null;
        }

        @Override
        public int getServerPort() {
            return 0;
        }

        @Override
        public java.io.BufferedReader getReader() {
            return null;
        }

        @Override
        public String getRemoteAddr() {
            return null;
        }

        @Override
        public String getRemoteHost() {
            return null;
        }

        @Override
        public void setAttribute(String name, Object o) {
        }

        @Override
        public void removeAttribute(String name) {
        }

        @Override
        public java.util.Locale getLocale() {
            return null;
        }

        @Override
        public java.util.Enumeration<java.util.Locale> getLocales() {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public jakarta.servlet.RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public String getLocalAddr() {
            return null;
        }

        @Override
        public int getLocalPort() {
            return 0;
        }

        @Override
        public jakarta.servlet.ServletContext getServletContext() {
            return null;
        }

        @Override
        public jakarta.servlet.AsyncContext startAsync() {
            return null;
        }

        @Override
        public jakarta.servlet.AsyncContext startAsync(jakarta.servlet.ServletRequest servletRequest,
                jakarta.servlet.ServletResponse servletResponse) {
            return null;
        }

        @Override
        public boolean isAsyncStarted() {
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            return false;
        }

        @Override
        public jakarta.servlet.AsyncContext getAsyncContext() {
            return null;
        }

        @Override
        public jakarta.servlet.DispatcherType getDispatcherType() {
            return null;
        }

        @Override
        public String getRequestId() {
            return null;
        }

        @Override
        public String getProtocolRequestId() {
            return null;
        }

        @Override
        public jakarta.servlet.ServletConnection getServletConnection() {
            return null;
        }

        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public jakarta.servlet.http.Cookie[] getCookies() {
            return null;
        }

        @Override
        public long getDateHeader(String name) {
            return 0;
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

        @Override
        public java.util.Enumeration<String> getHeaders(String name) {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public java.util.Enumeration<String> getHeaderNames() {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public int getIntHeader(String name) {
            return 0;
        }

        @Override
        public String getMethod() {
            return "POST";
        }

        @Override
        public String getPathInfo() {
            return null;
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getContextPath() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public java.security.Principal getUserPrincipal() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public String getRequestURI() {
            return null;
        }

        @Override
        public StringBuffer getRequestURL() {
            return null;
        }

        @Override
        public String getServletPath() {
            return null;
        }

        @Override
        public jakarta.servlet.http.HttpSession getSession(boolean create) {
            return null;
        }

        @Override
        public jakarta.servlet.http.HttpSession getSession() {
            return null;
        }

        @Override
        public String changeSessionId() {
            return null;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        public boolean authenticate(jakarta.servlet.http.HttpServletResponse response) {
            return false;
        }

        @Override
        public void login(String username, String password) {
        }

        @Override
        public void logout() {
        }

        @Override
        public java.util.Collection<jakarta.servlet.http.Part> getParts() {
            return java.util.Collections.emptyList();
        }

        @Override
        public jakarta.servlet.http.Part getPart(String name) {
            return null;
        }

        @Override
        public <T extends jakarta.servlet.http.HttpUpgradeHandler> T upgrade(Class<T> handlerClass) {
            return null;
        }
    }
}
