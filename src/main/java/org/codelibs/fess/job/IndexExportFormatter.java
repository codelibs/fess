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

import java.util.Map;
import java.util.Set;

/**
 * Strategy interface for formatting exported index documents.
 */
public interface IndexExportFormatter {

    /**
     * Returns the file extension for this format (e.g. ".html", ".json").
     *
     * @return the file extension including the leading dot
     */
    String getFileExtension();

    /**
     * Returns the default index file name for this format (e.g. "index.html", "index.json").
     *
     * @return the index file name
     */
    String getIndexFileName();

    /**
     * Formats a document source map into the target format string.
     *
     * @param source the document source map
     * @param excludeFields the set of field names to exclude from output
     * @return the formatted string
     */
    String format(Map<String, Object> source, Set<String> excludeFields);
}
