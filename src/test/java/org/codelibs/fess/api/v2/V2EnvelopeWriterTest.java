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

import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void test_writeError_sets_characterEncoding_to_UTF_8() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        V2EnvelopeWriter.writeError(res, V2ErrorCode.INVALID_REQUEST, "bad");
        assertEquals("UTF-8", res.characterEncoding);
    }

    @Test
    public void test_writeError_isNoop_when_response_already_committed() throws Exception {
        final CapturingResponse res = new CapturingResponse() {
            @Override
            public boolean isCommitted() {
                return true;
            }
        };
        V2EnvelopeWriter.writeError(res, V2ErrorCode.INTERNAL_ERROR, "should not appear");
        // If response is committed, nothing should be written to the body
        assertEquals("", res.body());
        // Status should not be changed from initial 200
        assertEquals(200, res.status);
    }

    @Test
    public void test_writeError_mapsStatusSystemError_for_5xx_codes() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        V2EnvelopeWriter.writeError(res, V2ErrorCode.INTERNAL_ERROR, "err");
        assertTrue(res.body().contains("\"status\":9"), res.body());
    }

    @Test
    public void test_writeError_mapsStatusUserError_for_4xx_codes() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        V2EnvelopeWriter.writeError(res, V2ErrorCode.NOT_FOUND, "not found");
        assertTrue(res.body().contains("\"status\":1"), res.body());

        final CapturingResponse res2 = new CapturingResponse();
        V2EnvelopeWriter.writeError(res2, V2ErrorCode.INVALID_REQUEST, "bad request");
        assertTrue(res2.body().contains("\"status\":1"), res2.body());
    }

    @Test
    public void test_writeSuccess_throwsOnReservedKey_status() {
        final CapturingResponse res = new CapturingResponse();
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("status", 99); // reserved — must be rejected
        assertThrows(IllegalStateException.class, () -> V2EnvelopeWriter.writeSuccess(res, payload),
                "payload containing 'status' should throw IllegalStateException");
    }

    @Test
    public void test_writeSuccess_throwsOnReservedKey_version() {
        final CapturingResponse res = new CapturingResponse();
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("version", "evil"); // reserved — must be rejected
        assertThrows(IllegalStateException.class, () -> V2EnvelopeWriter.writeSuccess(res, payload),
                "payload containing 'version' should throw IllegalStateException");
    }

    @Test
    public void test_writeSuccess_nullPayloadIsAllowed() throws Exception {
        // null payload is documented as "treated as empty"; must not throw.
        final CapturingResponse res = new CapturingResponse();
        V2EnvelopeWriter.writeSuccess(res, null);
        final String body = res.body();
        assertTrue(body.contains("\"status\":0"), body);
        assertTrue(body.contains("\"version\":\"v2\""), body);
    }

    @Test
    public void test_writeInternalError_does_not_leak_throwable_message() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        // Create a logger that does nothing (we just need to not throw)
        final org.apache.logging.log4j.Logger noopLogger = org.apache.logging.log4j.LogManager.getLogger("test-noop");
        final Exception secret = new RuntimeException("secret connection string: jdbc:postgresql://10.0.0.1:5432/prod");
        V2EnvelopeWriter.writeInternalError(res, secret, noopLogger, "/test");
        final String body = res.body();
        // Must contain the safe message, not the secret
        assertTrue(body.contains("internal error"), "expected 'internal error' in body: " + body);
        assertFalse(body.contains("secret connection string"), "should not leak exception message: " + body);
        assertFalse(body.contains("10.0.0.1"), "should not leak host details: " + body);
    }

    /** Minimal HttpServletResponse stub that captures setContentType/setStatus/getWriter output. */
    private static class CapturingResponse implements HttpServletResponse {
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        int status = 200;
        String contentType;
        String characterEncoding;

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
