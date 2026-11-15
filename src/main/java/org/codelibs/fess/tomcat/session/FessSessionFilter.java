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
package org.codelibs.fess.tomcat.session;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * A filter that saves the sessions a request touched just before the response is committed.
 *
 * <p>{@link FessSessionValve} saves when the rest of the chain has returned. For a response that is written and closed
 * by the handler (every JSON endpoint: the writer is closed by the JSON mapper) Tomcat has already sent the response
 * to the client by then, so a client that sends its next request to another node at once, such as the single page
 * application right after a login, could find the session missing there. This filter wraps the response and saves the
 * sessions before the first byte is written, the buffer is flushed, or an error or a redirect is sent. The valve still
 * saves at the end of the request, which writes only what changed after that.</p>
 *
 * <p>Like the valve this class is shipped to Tomcat's class loader and must not reference any web application type.</p>
 */
public class FessSessionFilter implements Filter {

    private final FessSessionManager manager;

    /**
     * Creates a filter.
     *
     * @param manager the manager whose touched sessions are saved
     */
    public FessSessionFilter(final FessSessionManager manager) {
        this.manager = manager;
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof final HttpServletResponse httpResponse && !(response instanceof PreCommitResponse)) {
            chain.doFilter(request, new PreCommitResponse(httpResponse, manager::persistTouched));
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * A response that runs an action once, before it is first committed.
     */
    protected static class PreCommitResponse extends HttpServletResponseWrapper {

        private final Runnable beforeCommit;

        private boolean done;

        private PrintWriter writer;

        private ServletOutputStream outputStream;

        /**
         * Creates a response.
         *
         * @param response the response to wrap
         * @param beforeCommit the action to run once before the response is committed
         */
        protected PreCommitResponse(final HttpServletResponse response, final Runnable beforeCommit) {
            super(response);
            this.beforeCommit = beforeCommit;
        }

        /**
         * Runs the action if it has not run yet.
         */
        protected void commitNow() {
            if (!done) {
                done = true;
                beforeCommit.run();
            }
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (writer == null) {
                writer = new PreCommitWriter(super.getWriter());
            }
            return writer;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (outputStream == null) {
                outputStream = new PreCommitOutputStream(super.getOutputStream());
            }
            return outputStream;
        }

        @Override
        public void flushBuffer() throws IOException {
            commitNow();
            super.flushBuffer();
        }

        @Override
        public void sendError(final int sc) throws IOException {
            commitNow();
            super.sendError(sc);
        }

        @Override
        public void sendError(final int sc, final String msg) throws IOException {
            commitNow();
            super.sendError(sc, msg);
        }

        @Override
        public void sendRedirect(final String location) throws IOException {
            commitNow();
            super.sendRedirect(location);
        }

        private class PreCommitWriter extends PrintWriter {

            PreCommitWriter(final PrintWriter delegate) {
                super(delegate);
            }

            @Override
            public void write(final int c) {
                commitNow();
                super.write(c);
            }

            @Override
            public void write(final char[] buf, final int off, final int len) {
                commitNow();
                super.write(buf, off, len);
            }

            @Override
            public void write(final String s, final int off, final int len) {
                commitNow();
                super.write(s, off, len);
            }

            @Override
            public void println() {
                commitNow();
                super.println();
            }

            @Override
            public void flush() {
                commitNow();
                super.flush();
            }

            @Override
            public void close() {
                commitNow();
                super.close();
            }
        }

        private class PreCommitOutputStream extends ServletOutputStream {

            private final ServletOutputStream delegate;

            PreCommitOutputStream(final ServletOutputStream delegate) {
                this.delegate = delegate;
            }

            @Override
            public boolean isReady() {
                return delegate.isReady();
            }

            @Override
            public void setWriteListener(final WriteListener writeListener) {
                delegate.setWriteListener(writeListener);
            }

            @Override
            public void write(final int b) throws IOException {
                commitNow();
                delegate.write(b);
            }

            @Override
            public void write(final byte[] b, final int off, final int len) throws IOException {
                commitNow();
                delegate.write(b, off, len);
            }

            @Override
            public void flush() throws IOException {
                commitNow();
                delegate.flush();
            }

            @Override
            public void close() throws IOException {
                commitNow();
                delegate.close();
            }
        }
    }
}
