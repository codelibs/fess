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

import jakarta.servlet.http.HttpServletResponse;

/**
 * Common helper for Server-Sent Events (SSE) response headers.
 *
 * <p>Both the v1 chat API ({@code ChatApiManager}) and the v2 chat-stream handler
 * ({@code ChatStreamHandler}) emit {@code text/event-stream} responses and need the
 * same minimum set of response headers to interoperate correctly with browsers and
 * reverse proxies. This helper centralises that header configuration so the
 * baseline cannot drift between the two surfaces.</p>
 *
 * <p>The {@code X-Accel-Buffering: no} header is critical when running behind nginx
 * (or any reverse proxy that defaults to response buffering): without it the entire
 * stream is buffered until the connection closes, which defeats the purpose of SSE
 * and breaks the chat UX.</p>
 *
 * <p>This is a DI-managed singleton helper; obtain it through
 * {@link org.codelibs.fess.util.ComponentUtil#getSseResponseHelper()}.</p>
 */
public class SseResponseHelper {

    public SseResponseHelper() {
        // default constructor
    }

    /**
     * Applies the standard SSE response headers to the given response.
     *
     * <p>The following headers are set:</p>
     * <ul>
     *   <li>{@code Content-Type: text/event-stream; charset=UTF-8} (also sets character encoding)</li>
     *   <li>{@code Cache-Control: no-cache} — prevent intermediaries from caching the event stream</li>
     *   <li>{@code Connection: keep-alive} — keep the underlying TCP connection open for streaming</li>
     *   <li>{@code X-Accel-Buffering: no} — disable response buffering in nginx and similar reverse proxies</li>
     * </ul>
     *
     * <p>Must be called <strong>before</strong> any bytes are written to the response so the
     * headers are flushed with the response prelude.</p>
     *
     * @param response the HTTP response to configure; must not be {@code null}
     */
    public void applySseHeaders(final HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/event-stream; charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");
    }
}
