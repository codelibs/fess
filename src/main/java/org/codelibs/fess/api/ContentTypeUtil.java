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

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codelibs.core.lang.StringUtil;

/**
 * Utility class for determining MIME content types based on file extensions.
 * Provides a centralized mapping of file extensions to content types.
 * Thread-safe implementation using ConcurrentHashMap for runtime registration.
 */
public class ContentTypeUtil {

    /** Thread-safe map of file extensions to MIME types */
    private static final Map<String, String> CONTENT_TYPE_MAP = new ConcurrentHashMap<>();

    static {
        // Text formats
        CONTENT_TYPE_MAP.put(".txt", "text/plain");
        CONTENT_TYPE_MAP.put(".html", "text/html;charset=utf-8");
        CONTENT_TYPE_MAP.put(".css", "text/css");
        CONTENT_TYPE_MAP.put(".js", "text/javascript");
        CONTENT_TYPE_MAP.put(".json", "application/json");
        CONTENT_TYPE_MAP.put(".xml", "application/xml");

        // Font formats
        CONTENT_TYPE_MAP.put(".eot", "application/vnd.ms-fontobject");
        CONTENT_TYPE_MAP.put(".otf", "font/otf");
        CONTENT_TYPE_MAP.put(".ttf", "font/ttf");
        CONTENT_TYPE_MAP.put(".woff", "font/woff");
        CONTENT_TYPE_MAP.put(".woff2", "font/woff2");

        // Image formats
        CONTENT_TYPE_MAP.put(".ico", "image/vnd.microsoft.icon");
        CONTENT_TYPE_MAP.put(".svg", "image/svg+xml");
        CONTENT_TYPE_MAP.put(".png", "image/png");
        CONTENT_TYPE_MAP.put(".jpg", "image/jpeg");
        CONTENT_TYPE_MAP.put(".jpeg", "image/jpeg");
        CONTENT_TYPE_MAP.put(".gif", "image/gif");
        CONTENT_TYPE_MAP.put(".webp", "image/webp");

        // Document formats
        CONTENT_TYPE_MAP.put(".pdf", "application/pdf");
        CONTENT_TYPE_MAP.put(".zip", "application/zip");
        CONTENT_TYPE_MAP.put(".tar", "application/x-tar");
        CONTENT_TYPE_MAP.put(".gz", "application/gzip");
    }

    /** Private constructor to prevent instantiation */
    private ContentTypeUtil() {
        // Utility class
    }

    /**
     * Determines the content type from a file path.
     *
     * @param path The file path
     * @return The content type, or null if not found
     */
    public static String getContentType(final String path) {
        if (StringUtil.isBlank(path)) {
            return null;
        }

        final String lowerPath = path.toLowerCase(Locale.ROOT);

        // Handle directory paths
        if (lowerPath.endsWith("/")) {
            return CONTENT_TYPE_MAP.get(".html");
        }

        // Find the file extension
        final int dotIndex = lowerPath.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == lowerPath.length() - 1) {
            return null;
        }

        final String extension = lowerPath.substring(dotIndex);
        return CONTENT_TYPE_MAP.get(extension);
    }

    /**
     * Determines the content type from a file path, returning a default if not found.
     *
     * @param path The file path
     * @param defaultContentType The default content type to return if not found
     * @return The content type, or the default if not found
     */
    public static String getContentType(final String path, final String defaultContentType) {
        final String contentType = getContentType(path);
        return contentType != null ? contentType : defaultContentType;
    }

    /**
     * Checks if a file path has a known content type.
     *
     * @param path The file path
     * @return true if the content type is known, false otherwise
     */
    public static boolean hasContentType(final String path) {
        return getContentType(path) != null;
    }

    /**
     * Registers a custom content type mapping.
     *
     * @param extension The file extension (including the dot, e.g., ".custom")
     * @param contentType The MIME content type
     */
    public static void registerContentType(final String extension, final String contentType) {
        if (StringUtil.isNotBlank(extension) && StringUtil.isNotBlank(contentType)) {
            CONTENT_TYPE_MAP.put(extension.toLowerCase(Locale.ROOT), contentType);
        }
    }
}
