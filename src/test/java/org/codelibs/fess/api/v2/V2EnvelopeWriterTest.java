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
package org.codelibs.fess.api.v2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletResponse;

public class V2EnvelopeWriterTest extends UnitFessTestCase {

    @Test
    public void test_writeSuccess_minimalEnvelope() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("k", "v");
        V2EnvelopeWriter.writeSuccess(res, payload);
        final String body = res.body();
        assertTrue(body.contains("\"status\":0"), body);
        assertTrue(body.contains("\"version\":\"v2\""), body);
        assertTrue(body.contains("\"k\":\"v\""), body);
        assertEquals("application/json; charset=UTF-8", res.contentType);
    }

    @Test
    public void test_writeError_setsStatusAndCode() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        V2EnvelopeWriter.writeError(res, V2ErrorCode.AUTH_REQUIRED, "login required");
        final String body = res.body();
        assertEquals(401, res.status);
        assertTrue(body.contains("\"status\":1"), body);
        assertTrue(body.contains("\"code\":\"auth_required\""), body);
        assertTrue(body.contains("\"message\":\"login required\""), body);
    }

    @Test
    public void test_writeError_notFoundSetsHttpStatus404() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        V2EnvelopeWriter.writeError(res, V2ErrorCode.NOT_FOUND, "no");
        assertEquals(404, res.status);
        assertTrue(res.body().contains("\"code\":\"not_found\""));
    }

    @Test
    public void test_writeError_forbiddenSetsHttpStatus403() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        V2EnvelopeWriter.writeError(res, V2ErrorCode.FORBIDDEN, "no permission");
        assertEquals(403, res.status);
        assertTrue(res.body().contains("\"code\":\"forbidden\""));
    }

    /** Minimal HttpServletResponse stub that captures setContentType/setStatus/getWriter output. */
    private static class CapturingResponse implements HttpServletResponse {
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        int status = 200;
        String contentType;

        String body() {
            writer.flush();
            return sw.toString();
        }

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

        // Unused methods — implement to satisfy interface. Throw UnsupportedOperationException
        // for anything the test does not exercise, return sane defaults otherwise.
        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public void setCharacterEncoding(final String s) {
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
        public void setLocale(final java.util.Locale loc) {
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }

        @Override
        public void addCookie(final jakarta.servlet.http.Cookie cookie) {
        }

        @Override
        public boolean containsHeader(final String name) {
            return false;
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
        }

        @Override
        public void addHeader(final String name, final String value) {
        }

        @Override
        public void setIntHeader(final String name, final int value) {
        }

        @Override
        public void addIntHeader(final String name, final int value) {
        }

        @Override
        public String getHeader(final String name) {
            return null;
        }

        @Override
        public java.util.Collection<String> getHeaders(final String name) {
            return java.util.Collections.emptyList();
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return java.util.Collections.emptyList();
        }
    }
}
