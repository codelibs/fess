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

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.CsvWriter;

public class CsvUtilTest extends UnitFessTestCase {

    @Test
    public void test_escapeFormula_triggers() {
        assertEquals("'=1+1", CsvUtil.escapeFormula("=1+1"));
        assertEquals("'+1", CsvUtil.escapeFormula("+1"));
        assertEquals("'@SUM(A1)", CsvUtil.escapeFormula("@SUM(A1)"));
        assertEquals("'\tcmd", CsvUtil.escapeFormula("\tcmd"));
        assertEquals("'\rcmd", CsvUtil.escapeFormula("\rcmd"));
    }

    @Test
    public void test_escapeFormula_minus() {
        assertEquals("'-foo", CsvUtil.escapeFormula("-foo"));
        assertEquals("'-2+3+cmd|' /C calc'!A0", CsvUtil.escapeFormula("-2+3+cmd|' /C calc'!A0"));
    }

    @Test
    public void test_escapeFormula_onlyTheFirstCharacterCounts() {
        assertEquals("a=b", CsvUtil.escapeFormula("a=b"));
        assertEquals("foo +bar", CsvUtil.escapeFormula("foo +bar"));
        assertEquals(" =1", CsvUtil.escapeFormula(" =1"));
        assertEquals("'=1", CsvUtil.escapeFormula("'=1")); // a quote is no trigger
    }

    @Test
    public void test_escapeFormula_nullAndEmpty() {
        assertNull(CsvUtil.escapeFormula(null));
        assertEquals("", CsvUtil.escapeFormula(""));
    }

    @Test
    public void test_toCell() {
        assertEquals("", CsvUtil.toCell(null));
        assertEquals("'=1", CsvUtil.toCell("=1"));
        assertEquals("'-foo", CsvUtil.toCell("-foo"));
        assertEquals("-5", CsvUtil.toCell(-5));
        assertEquals("0.25", CsvUtil.toCell(0.25));
        assertEquals("12", CsvUtil.toCell(12L));
    }

    @Test
    public void test_createCsvConfig() throws IOException {
        final CsvConfig cfg = CsvUtil.createCsvConfig();
        assertEquals(',', cfg.getSeparator());
        assertEquals('"', cfg.getQuote());
        assertEquals('"', cfg.getEscape());
        assertFalse(cfg.isEscapeDisabled());
        assertFalse(cfg.isQuoteDisabled());
        final StringWriter out = new StringWriter();
        try (CsvWriter writer = new CsvWriter(out, cfg)) {
            writer.writeValues(List.of("a,b", "say \"hi\""));
        }
        assertEquals("\"a,b\",\"say \"\"hi\"\"\"", out.toString().trim());
    }
}
