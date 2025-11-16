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

/**
 * Constants used across the API layer.
 * Centralizes magic strings and configuration values for better maintainability.
 */
public final class ApiConstants {

    /** Private constructor to prevent instantiation */
    private ApiConstants() {
        // Constants class
    }

    // Request attribute keys
    /** Request attribute key for storing API format type */
    public static final String API_FORMAT_TYPE = "apiFormatType";

    /** Request attribute key for storing document ID */
    public static final String DOC_ID_FIELD = "doc_id";

    // Response field names
    /** JSON response field name for messages */
    public static final String MESSAGE_FIELD = "message";

    /** JSON response field name for results */
    public static final String RESULT_FIELD = "result";

    /** JSON response field name for error codes */
    public static final String ERROR_CODE_FIELD = "error_code";

    /** JSON response field name for data */
    public static final String DATA_FIELD = "data";

    /** JSON response field name for record count */
    public static final String RECORD_COUNT_FIELD = "record_count";

    /** JSON response field name for query */
    public static final String QUERY_FIELD = "q";

    /** JSON response field name for query ID */
    public static final String QUERY_ID_FIELD = "query_id";

    // HTTP methods
    /** HTTP GET method */
    public static final String HTTP_METHOD_GET = "GET";

    /** HTTP POST method */
    public static final String HTTP_METHOD_POST = "POST";

    /** HTTP PUT method */
    public static final String HTTP_METHOD_PUT = "PUT";

    /** HTTP DELETE method */
    public static final String HTTP_METHOD_DELETE = "DELETE";

    /** HTTP PATCH method */
    public static final String HTTP_METHOD_PATCH = "PATCH";

    // MIME types
    /** MIME type for JSON */
    public static final String MIME_TYPE_JSON = "application/json";

    /** MIME type for JSONP */
    public static final String MIME_TYPE_JAVASCRIPT = "application/javascript";

    /** MIME type for NDJSON (newline-delimited JSON) */
    public static final String MIME_TYPE_NDJSON = "application/x-ndjson";

    /** MIME type for text */
    public static final String MIME_TYPE_TEXT = "text/plain";

    // API paths
    /** API v1 path prefix */
    public static final String API_V1_PREFIX = "/api/v1";

    /** Admin server path prefix */
    public static final String ADMIN_SERVER_PREFIX = "/admin/server_";

    // Default values
    /** Default number of suggestions to return */
    public static final int DEFAULT_SUGGEST_NUM = 10;

    /** Default result value for created resources */
    public static final String RESULT_CREATED = "created";

    /** Default result value for updated resources */
    public static final String RESULT_UPDATED = "updated";

    /** Default result value for deleted resources */
    public static final String RESULT_DELETED = "deleted";

    // Error messages
    /** Error message for unsupported operations */
    public static final String ERROR_UNSUPPORTED_OPERATION = "Unsupported operation.";

    /** Error message for not found resources */
    public static final String ERROR_NOT_FOUND = "Not found.";

    /** Error message for invalid referer */
    public static final String ERROR_INVALID_REFERER = "Referer is invalid.";

    /** Error message for method not allowed */
    public static final String ERROR_METHOD_NOT_ALLOWED = " is not allowed.";

    /** Error message for no user session */
    public static final String ERROR_NO_USER_SESSION = "No user session.";

    /** Error message for invalid session */
    public static final String ERROR_INVALID_SESSION = "Invalid session.";

    /** Error message for invalid access token */
    public static final String ERROR_INVALID_ACCESS_TOKEN = "Invalid access token.";

    /** Error message for unauthorized access */
    public static final String ERROR_UNAUTHORIZED_ACCESS = "Unauthorized access: ";
}
