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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class SseResponseHelperTest extends UnitFessTestCase {

    @Test
    public void test_applySseHeaders_setsAllFiveHeaders() {
        // F2: ensure the centralised SSE header set matches the previously inlined
        // 5-line block in v1 ChatApiManager and v2 ChatStreamHandler exactly.
        final CapturingResponse res = new CapturingResponse();
        new SseResponseHelper().applySseHeaders(res);
        // Content-Type and charset
        assertEquals("text/event-stream; charset=UTF-8", res.contentType);
        assertEquals("UTF-8", res.characterEncoding);
        // The remaining three plain headers
        assertEquals("no-cache", res.headers.get("Cache-Control"));
        assertEquals("keep-alive", res.headers.get("Connection"));
        // Critical for nginx reverse-proxy SSE behaviour — buffering must stay disabled.
        assertEquals("no", res.headers.get("X-Accel-Buffering"));
    }

    @Test
    public void test_applySseHeaders_isIdempotent() {
        // F2: calling twice (e.g. due to a re-dispatch retry) must yield the same final
        // header values; setHeader semantics mean each call overwrites the previous.
        final CapturingResponse res = new CapturingResponse();
        new SseResponseHelper().applySseHeaders(res);
        new SseResponseHelper().applySseHeaders(res);
        assertEquals("text/event-stream; charset=UTF-8", res.contentType);
        assertEquals("UTF-8", res.characterEncoding);
        assertEquals("no-cache", res.headers.get("Cache-Control"));
        assertEquals("keep-alive", res.headers.get("Connection"));
        assertEquals("no", res.headers.get("X-Accel-Buffering"));
    }

    /** Minimal HttpServletResponse stub capturing only the calls the helper makes. */
    private static class CapturingResponse implements HttpServletResponse {
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        int status = 200;
        String contentType;
        String characterEncoding;
        final Map<String, String> headers = new HashMap<>();

        @Override
        public void setStatus(final int sc) {
            this.status = sc;
        }

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public void setContentType(final String type) {
            this.contentType = type;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }

        @Override
        public String getCharacterEncoding() {
            return characterEncoding != null ? characterEncoding : "UTF-8";
        }

        @Override
        public void setCharacterEncoding(final String s) {
            this.characterEncoding = s;
        }

        @Override
        public jakarta.servlet.ServletOutputStream getOutputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setContentLength(final int len) {
        }

        @Override
        public void setContentLengthLong(final long len) {
        }

        @Override
        public void setBufferSize(final int size) {
        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void flushBuffer() {
        }

        @Override
        public void resetBuffer() {
        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {
        }

        @Override
        public void setLocale(final Locale loc) {
        }

        @Override
        public Locale getLocale() {
            return Locale.ROOT;
        }

        @Override
        public void addCookie(final Cookie cookie) {
        }

        @Override
        public boolean containsHeader(final String name) {
            return headers.containsKey(name);
        }

        @Override
        public String encodeURL(final String url) {
            return url;
        }

        @Override
        public String encodeRedirectURL(final String url) {
            return url;
        }

        @Override
        public void sendError(final int sc, final String msg) {
        }

        @Override
        public void sendError(final int sc) {
        }

        @Override
        public void sendRedirect(final String location) {
        }

        @Override
        public void sendRedirect(final String location, final int sc) {
        }

        @Override
        public void sendRedirect(final String location, final boolean clearBuffer) {
        }

        @Override
        public void sendRedirect(final String location, final int sc, final boolean clearBuffer) {
        }

        @Override
        public void setDateHeader(final String name, final long date) {
        }

        @Override
        public void addDateHeader(final String name, final long date) {
        }

        @Override
        public void setHeader(final String name, final String value) {
            headers.put(name, value);
        }

        @Override
        public void addHeader(final String name, final String value) {
            headers.put(name, value);
        }

        @Override
        public void setIntHeader(final String name, final int value) {
            headers.put(name, Integer.toString(value));
        }

        @Override
        public void addIntHeader(final String name, final int value) {
            headers.put(name, Integer.toString(value));
        }

        @Override
        public String getHeader(final String name) {
            return headers.get(name);
        }

        @Override
        public Collection<String> getHeaders(final String name) {
            final String v = headers.get(name);
            return v == null ? Collections.emptyList() : Collections.singletonList(v);
        }

        @Override
        public Collection<String> getHeaderNames() {
            return new java.util.ArrayList<>(headers.keySet());
        }
    }
}
