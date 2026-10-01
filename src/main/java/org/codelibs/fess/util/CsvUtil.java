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
package org.codelibs.fess.util;

import com.orangesignal.csv.CsvConfig;

/**
 * Helpers shared by the CSV downloads: the writer configuration and the guard that keeps a
 * spreadsheet from running a cell as a formula.
 */
public final class CsvUtil {

    /**
     * The leading characters a spreadsheet reads as the start of a formula or a command:
     * {@code =}, {@code +}, {@code -}, {@code @}, tab and carriage return.
     */
    public static final String FORMULA_TRIGGERS = "=+-@\t\r";

    private CsvUtil() {
    }

    /**
     * Creates the CSV configuration the Fess downloads use: comma separated, double-quoted, with the
     * quote character escaped by doubling it.
     *
     * @return a new configuration
     */
    public static CsvConfig createCsvConfig() {
        final CsvConfig cfg = new CsvConfig(',', '"', '"');
        cfg.setEscapeDisabled(false);
        cfg.setQuoteDisabled(false);
        return cfg;
    }

    /**
     * Prefixes a single quote to a text that starts with one of {@link #FORMULA_TRIGGERS}, so a
     * spreadsheet shows it as text instead of running it as a formula.
     *
     * @param text the cell text, may be null
     * @return the text, safe to write as a cell; null is returned as is
     */
    public static String escapeFormula(final String text) {
        if (text != null && !text.isEmpty() && FORMULA_TRIGGERS.indexOf(text.charAt(0)) >= 0) {
            return "'" + text;
        }
        return text;
    }

    /**
     * Converts a value to the text of a cell: null is an empty cell, a text is guarded with
     * {@link #escapeFormula(String)} and anything else (a number) is written as is.
     *
     * @param value the cell value, may be null
     * @return the cell text
     */
    public static String toCell(final Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof final String text) {
            return escapeFormula(text);
        }
        return value.toString();
    }
}
