/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import org.codelibs.fess.unit.UnitFessTestCase;

public class DocListTest extends UnitFessTestCase {
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
}
