/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.codelibs.core.collection.Maps;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.sai.internal.ir.debug.ObjectSizeCalculator;

import com.google.common.collect.Lists;

public class MemoryUtilTest extends UnitFessTestCase {

    public void test_byteCountToDisplaySize() {
        assertEquals("0bytes", MemoryUtil.byteCountToDisplaySize(0L));
        assertEquals("999bytes", MemoryUtil.byteCountToDisplaySize(999L));
        assertEquals("1000bytes", MemoryUtil.byteCountToDisplaySize(1000L));
        assertEquals("1.024KB", MemoryUtil.byteCountToDisplaySize(FileUtils.ONE_KB));
        assertEquals("999.999KB", MemoryUtil.byteCountToDisplaySize(999_999L));
        assertEquals("1000KB", MemoryUtil.byteCountToDisplaySize(1000_000L));
        assertEquals("1.024MB", MemoryUtil.byteCountToDisplaySize(FileUtils.ONE_MB));
        assertEquals("976.562MB", MemoryUtil.byteCountToDisplaySize(999_999_999L));
        assertEquals("976.562MB", MemoryUtil.byteCountToDisplaySize(1000_000_000L));
        assertEquals("1.024GB", MemoryUtil.byteCountToDisplaySize(FileUtils.ONE_GB));
        assertEquals("953.674GB", MemoryUtil.byteCountToDisplaySize(999_999_999_999L));
        assertEquals("953.674GB", MemoryUtil.byteCountToDisplaySize(1000_000_000_000L));
        assertEquals("1.024TB", MemoryUtil.byteCountToDisplaySize(FileUtils.ONE_TB));
        assertEquals("931.322TB", MemoryUtil.byteCountToDisplaySize(999_999_999_999_999L));
        assertEquals("931.322TB", MemoryUtil.byteCountToDisplaySize(1000_000_000_000_000L));
        assertEquals("1.024PB", MemoryUtil.byteCountToDisplaySize(FileUtils.ONE_PB));
        assertEquals("909.494PB", MemoryUtil.byteCountToDisplaySize(999_999_999_999_999_999L));
        assertEquals("909.494PB", MemoryUtil.byteCountToDisplaySize(1000_000_000_000_000_000L));
        assertEquals("1.024EB", MemoryUtil.byteCountToDisplaySize(FileUtils.ONE_EB));
    }

    public void test_getUsedMemory() {
        assertTrue(MemoryUtil.getUsedMemory() >= 0);
    }

    public void test_sizeOf() throws Exception {
        // System.out.println("size: " + getObjectSize(""));
        assertEquals(24L, MemoryUtil.sizeOf(Integer.MAX_VALUE));
        assertEquals(24L, MemoryUtil.sizeOf(Long.MAX_VALUE));
        assertEquals(24L, MemoryUtil.sizeOf(Short.MAX_VALUE));
        assertEquals(24L, MemoryUtil.sizeOf(Float.MAX_VALUE));
        assertEquals(24L, MemoryUtil.sizeOf(Double.MAX_VALUE));
        assertEquals(24L, MemoryUtil.sizeOf(Byte.MAX_VALUE));
        assertEquals(16L, MemoryUtil.sizeOf(Boolean.TRUE));
        assertEquals(32L, MemoryUtil.sizeOf(new Date()));
        assertEquals(80L, MemoryUtil.sizeOf(LocalDateTime.now()));
        assertEquals(2128L, MemoryUtil.sizeOf(ZonedDateTime.now()));
        assertEquals(66L, MemoryUtil.sizeOf("1234567890"));
        assertEquals(76L, MemoryUtil.sizeOf("12345678901234567890"));
        assertEquals(66L, MemoryUtil.sizeOf(new String[] { "1234567890" }));
        assertEquals(132L, MemoryUtil.sizeOf(new String[] { "1234567890", "1234567890" }));
        assertEquals(132L, MemoryUtil.sizeOf(Lists.asList("1234567890", new String[] { "1234567890" })));
        assertEquals(132L, MemoryUtil.sizeOf(Maps.map("1234567890", "1234567890").$()));
    }

    private long getObjectSize(Object value) {
        System.setProperty("java.vm.name", "Java HotSpot(TM) ");
        return ObjectSizeCalculator.getObjectSize(value);
    }
}