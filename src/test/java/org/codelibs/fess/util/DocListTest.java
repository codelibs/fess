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

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class DocListTest extends UnitFessTestCase {
    @Test
    public void test_DocList() {
        DocList docList = new DocList();

        assertEquals(0, docList.getContentSize());
        assertEquals(0, docList.getProcessingTime());

        docList.addContentSize(1000);
        docList.addProcessingTime(999);
        assertEquals(1000, docList.getContentSize());
        assertEquals(999, docList.getProcessingTime());

        docList.clear();
        assertEquals(0, docList.getContentSize());
        assertEquals(0, docList.getProcessingTime());
    }

    @Test
    public void test_addContentSize_multiple() {
        DocList docList = new DocList();

        docList.addContentSize(100);
        assertEquals(100, docList.getContentSize());

        docList.addContentSize(200);
        assertEquals(300, docList.getContentSize());

        docList.addContentSize(50);
        assertEquals(350, docList.getContentSize());
    }

    @Test
    public void test_addProcessingTime_multiple() {
        DocList docList = new DocList();

        docList.addProcessingTime(10);
        assertEquals(10, docList.getProcessingTime());

        docList.addProcessingTime(20);
        assertEquals(30, docList.getProcessingTime());

        docList.addProcessingTime(5);
        assertEquals(35, docList.getProcessingTime());
    }

    @Test
    public void test_addContentSize_negative() {
        DocList docList = new DocList();

        docList.addContentSize(100);
        assertEquals(100, docList.getContentSize());

        docList.addContentSize(-30);
        assertEquals(70, docList.getContentSize());

        docList.addContentSize(-100);
        assertEquals(-30, docList.getContentSize());
    }

    @Test
    public void test_addProcessingTime_negative() {
        DocList docList = new DocList();

        docList.addProcessingTime(100);
        assertEquals(100, docList.getProcessingTime());

        docList.addProcessingTime(-30);
        assertEquals(70, docList.getProcessingTime());

        docList.addProcessingTime(-100);
        assertEquals(-30, docList.getProcessingTime());
    }

    @Test
    public void test_addContentSize_zero() {
        DocList docList = new DocList();

        docList.addContentSize(0);
        assertEquals(0, docList.getContentSize());

        docList.addContentSize(100);
        docList.addContentSize(0);
        assertEquals(100, docList.getContentSize());
    }

    @Test
    public void test_addProcessingTime_zero() {
        DocList docList = new DocList();

        docList.addProcessingTime(0);
        assertEquals(0, docList.getProcessingTime());

        docList.addProcessingTime(100);
        docList.addProcessingTime(0);
        assertEquals(100, docList.getProcessingTime());
    }

    @Test
    public void test_list_operations_with_metrics() {
        DocList docList = new DocList();

        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("id", "1");
        doc1.put("title", "Test Document 1");

        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("id", "2");
        doc2.put("title", "Test Document 2");

        docList.add(doc1);
        docList.add(doc2);

        assertEquals(2, docList.size());
        assertEquals(doc1, docList.get(0));
        assertEquals(doc2, docList.get(1));

        docList.addContentSize(500);
        docList.addProcessingTime(250);

        assertEquals(500, docList.getContentSize());
        assertEquals(250, docList.getProcessingTime());

        docList.remove(0);
        assertEquals(1, docList.size());
        assertEquals(doc2, docList.get(0));

        assertEquals(500, docList.getContentSize());
        assertEquals(250, docList.getProcessingTime());
    }

    @Test
    public void test_clear_with_elements() {
        DocList docList = new DocList();

        Map<String, Object> doc = new HashMap<>();
        doc.put("id", "1");
        docList.add(doc);

        docList.addContentSize(1000);
        docList.addProcessingTime(500);

        assertEquals(1, docList.size());
        assertEquals(1000, docList.getContentSize());
        assertEquals(500, docList.getProcessingTime());

        docList.clear();

        assertEquals(0, docList.size());
        assertEquals(0, docList.getContentSize());
        assertEquals(0, docList.getProcessingTime());
    }

    @Test
    public void test_toString_empty() {
        DocList docList = new DocList();
        String result = docList.toString();

        assertTrue(result.contains("contentSize=0"));
        assertTrue(result.contains("processingTime=0"));
        assertTrue(result.contains("elementData=[]"));
    }

    @Test
    public void test_toString_with_data() {
        DocList docList = new DocList();

        Map<String, Object> doc = new HashMap<>();
        doc.put("id", "1");
        docList.add(doc);

        docList.addContentSize(1000);
        docList.addProcessingTime(500);

        String result = docList.toString();

        assertTrue(result.contains("contentSize=1000"));
        assertTrue(result.contains("processingTime=500"));
        assertTrue(result.contains("elementData=["));
        assertTrue(result.contains("id=1"));
    }

    @Test
    public void test_serialization() {
        DocList docList = new DocList();

        assertTrue(docList instanceof java.io.Serializable);
    }

    @Test
    public void test_large_values() {
        DocList docList = new DocList();

        long largeValue = Long.MAX_VALUE - 1000;

        docList.addContentSize(largeValue);
        assertEquals(largeValue, docList.getContentSize());

        docList.addProcessingTime(largeValue);
        assertEquals(largeValue, docList.getProcessingTime());

        docList.addContentSize(1000);
        assertEquals(Long.MAX_VALUE, docList.getContentSize());

        docList.addProcessingTime(1000);
        assertEquals(Long.MAX_VALUE, docList.getProcessingTime());
    }

    @Test
    public void test_inheritance() {
        DocList docList = new DocList();

        assertTrue(docList instanceof java.util.ArrayList);
        assertTrue(docList instanceof java.util.List);
        assertTrue(docList instanceof java.util.Collection);
    }
}
