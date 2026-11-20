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
package org.codelibs.fess.tomcat.valve;

import java.io.IOException;
import java.io.Writer;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ErrorReportValve;

/**
 * This class is a valve to suppress error reports.
 * It extends {@link ErrorReportValve} and disables the display of
 * error reports and server information.
 *
 * <p>The page it writes is a short one of Fess, not the "HTTP Status 400 - Bad Request" page of the container. The
 * valve answers what no web application answers: a request the connector refuses before it is routed, such as one
 * whose request line and headers are longer than {@code tomcat.maxHttpHeaderSize} (8 KB by default). A search address
 * of about 8,000 characters or more is one; an application cannot reach it, so the page says what the limit is about.
 * It does not depend on any other class of Fess, which is why it can be written while the web application is not
 * (or cannot be) running.</p>
 */
public class SuppressErrorReportValve extends ErrorReportValve {
    /**
     * Constructs a new valve to suppress error reports.
     */
    public SuppressErrorReportValve() {
        setShowReport(false);
        setShowServerInfo(false);
    }

    @Override
    protected void report(final Request request, final Response response, final Throwable throwable) {
        final int status = response.getStatus();
        // The same conditions as the report of the container: an error, and nobody has written a body or reported it.
        if (status < 400 || response.getContentWritten() > 0 || !response.setErrorReported()) {
            return;
        }
        try {
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setHeader("Content-Security-Policy", "default-src 'none'");
            final Writer writer = response.getReporter();
            if (writer != null) {
                writer.write(page(status));
                response.finishResponse();
            }
        } catch (final IOException | IllegalStateException e) {
            // The client is gone or the response is already committed: there is nobody to tell.
        }
    }

    /**
     * Builds the page for a status.
     *
     * @param status the HTTP status of the response
     * @return the HTML of a short page that carries no script, stylesheet or link
     */
    static String page(final int status) {
        final String text = switch (status) {
        case 400 -> "Bad Request.";
        case 403 -> "Forbidden.";
        case 404 -> "Not Found.";
        case 405 -> "Method Not Allowed.";
        case 408 -> "Request Timeout.";
        case 413 -> "Content Too Large.";
        case 414 -> "URI Too Long.";
        case 431 -> "Request Header Fields Too Large.";
        case 503 -> "Service Unavailable.";
        default -> status < 500 ? "Bad Request." : "System Error.";
        };
        final String hint = status == 400
                ? "<p>The request could not be processed: the address or its headers may be too long, or the request is malformed.</p>\n"
                : "";
        return "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n<title>" + status + " " + text
                + "</title>\n</head>\n<body>\n<h1>" + status + " " + text + "</h1>\n" + hint + "</body>\n</html>\n";
    }
}
