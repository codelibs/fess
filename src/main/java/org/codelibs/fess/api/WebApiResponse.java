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
package org.codelibs.fess.api;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.output.ByteArrayOutputStream;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * Wrapper for HTTP servlet responses in web API context.
 * This class extends HttpServletResponseWrapper to provide custom response handling
 * for web API responses, including dummy output stream management.
 */
public class WebApiResponse extends HttpServletResponseWrapper {

    /**
     * Constructs a WebApiResponse with the specified response.
     *
     * @param response The original HTTP servlet response
     */
    public WebApiResponse(final HttpServletResponse response) {
        super(response);
    }

    /**
     * Gets a PrintWriter for writing response content.
     * Returns a dummy PrintWriter that writes to a ByteArrayOutputStream.
     *
     * @return A PrintWriter for response output
     * @throws IOException If an I/O error occurs
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        // dummy stream
        return new PrintWriter(new ByteArrayOutputStream());
    }

}
