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

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codelibs.fess.crawler.entity.ResponseData;

/**
 * What the crawler knew about a document when it could not be crawled: its size, its MIME type and
 * the HTTP status of its response. Every component is optional and null when it is unknown.
 *
 * <p>The values come from the remote server, so they are normalized when the record is created:
 * a non-positive status and a negative size are unknown, and a MIME type is reduced to a lower-case
 * {@code type/subtype} token of at most {@link #MAX_MIME_TYPE_LENGTH} characters, or dropped.</p>
 *
 * @param contentLength the size in bytes, or null
 * @param mimeType the media type without parameters, or null
 * @param httpStatusCode the HTTP status code, or null
 */
public record FailureUrlDetails(Long contentLength, String mimeType, Integer httpStatusCode) {

    /** The longest MIME type that is stored. */
    public static final int MAX_MIME_TYPE_LENGTH = 100;

    private static final Pattern MIME_TYPE = Pattern.compile("[a-z0-9][a-z0-9._+-]*/[a-z0-9][a-z0-9._+-]*");

    private static final Pattern LENGTH_IN_MESSAGE = Pattern.compile("\\((\\d{1,18}) bytes?\\)");

    /**
     * Normalizes the components.
     *
     * @param contentLength the size in bytes, negative when unknown
     * @param mimeType the Content-Type or MIME type
     * @param httpStatusCode the HTTP status code, not positive when unknown
     */
    public FailureUrlDetails {
        contentLength = contentLength != null && contentLength >= 0 ? contentLength : null;
        mimeType = normalizeMimeType(mimeType);
        httpStatusCode = httpStatusCode != null && httpStatusCode > 0 ? httpStatusCode : null;
    }

    /**
     * Takes the details of a response. The HTTP status is kept only for a response of an http or https URL.
     *
     * @param responseData the response of the crawler, may be null
     * @return the details, or null when there is no response
     */
    public static FailureUrlDetails of(final ResponseData responseData) {
        if (responseData == null) {
            return null;
        }
        // the clients of the other protocols set 200 for every file they can read: it is not a status of the server
        final String url = responseData.getUrl();
        final boolean http = url != null && (url.regionMatches(true, 0, "http:", 0, 5) || url.regionMatches(true, 0, "https:", 0, 6));
        // 0 is the default of the response, not a size: an empty document has no content to fail on
        return new FailureUrlDetails(responseData.getContentLength() > 0 ? responseData.getContentLength() : null,
                responseData.getMimeType(), http ? responseData.getHttpStatusCode() : null);
    }

    /**
     * Reads the size from the message of a rejection for being too large, such as
     * {@code The content length (2048 byte) is over 1024 byte.}, which is all a client gives when it
     * rejects a document before it returns a response.
     *
     * @param message the message of the exception, may be null
     * @return the details with the size, or null when the message has none
     */
    public static FailureUrlDetails ofMaxLengthMessage(final String message) {
        if (message == null) {
            return null;
        }
        final Matcher matcher = LENGTH_IN_MESSAGE.matcher(message);
        if (!matcher.find()) {
            return null;
        }
        return new FailureUrlDetails(Long.valueOf(matcher.group(1)), null, null);
    }

    /**
     * Reduces a Content-Type to its media type, which is what a report can show safely.
     *
     * @param value the Content-Type or MIME type, may be null
     * @return the lower-case {@code type/subtype}, or null when it is blank, too long or not a media type
     */
    public static String normalizeMimeType(final String value) {
        if (value == null) {
            return null;
        }
        final int parameters = value.indexOf(';');
        final String token = (parameters >= 0 ? value.substring(0, parameters) : value).trim().toLowerCase(Locale.ROOT);
        if (token.isEmpty() || token.length() > MAX_MIME_TYPE_LENGTH || !MIME_TYPE.matcher(token).matches()) {
            return null;
        }
        return token;
    }
}
