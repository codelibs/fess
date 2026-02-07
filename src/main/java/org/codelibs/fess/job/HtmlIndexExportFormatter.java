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
package org.codelibs.fess.job;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Formatter that outputs index documents as HTML files.
 */
public class HtmlIndexExportFormatter implements IndexExportFormatter {

    /**
     * Creates a new HtmlIndexExportFormatter instance.
     */
    public HtmlIndexExportFormatter() {
        // default constructor
    }

    @Override
    public String getFileExtension() {
        return ".html";
    }

    @Override
    public String getIndexFileName() {
        return "index.html";
    }

    @Override
    public String format(final Map<String, Object> source, final Set<String> excludeFields) {
        final String title = escapeHtml(getStringValue(source, "title"));
        final String content = escapeHtml(getStringValue(source, "content"));
        final String lang = escapeHtml(getStringValue(source, "lang"));

        final StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"").append(lang).append("\">\n");
        html.append("<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<title>").append(title).append("</title>\n");

        for (final Map.Entry<String, Object> entry : source.entrySet()) {
            final String field = entry.getKey();
            if ("title".equals(field) || "content".equals(field) || "lang".equals(field)) {
                continue;
            }
            if (excludeFields.contains(field)) {
                continue;
            }

            final Object value = entry.getValue();
            if (value instanceof Collection) {
                for (final Object item : (Collection<?>) value) {
                    html.append("<meta name=\"fess:")
                            .append(escapeHtml(field))
                            .append("\" content=\"")
                            .append(escapeHtml(String.valueOf(item)))
                            .append("\">\n");
                }
            } else if (value != null) {
                html.append("<meta name=\"fess:")
                        .append(escapeHtml(field))
                        .append("\" content=\"")
                        .append(escapeHtml(String.valueOf(value)))
                        .append("\">\n");
            }
        }

        html.append("</head>\n");
        html.append("<body>\n");
        html.append(content).append("\n");
        html.append("</body>\n");
        html.append("</html>\n");

        return html.toString();
    }

    private String getStringValue(final Map<String, Object> source, final String key) {
        final Object value = source.get(key);
        return value != null ? value.toString() : "";
    }

    private String escapeHtml(final String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}
