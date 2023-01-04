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
package org.codelibs.fess.dict.stemmeroverride;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.unit.UnitFessTestCase;

public class StemmerOverrideFileTest extends UnitFessTestCase {
    private StemmerOverrideFile stemmerOverrideFile;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        stemmerOverrideFile = new StemmerOverrideFile("1", "dummy", new Date());
        List<StemmerOverrideItem> itemList = new ArrayList<>();
        itemList.add(new StemmerOverrideItem(1, "aaa", "a"));
        itemList.add(new StemmerOverrideItem(2, "bbb", "b"));
        itemList.add(new StemmerOverrideItem(3, "ccc", "c"));
        stemmerOverrideFile.stemmerOverrideItemList = itemList;
    }

    public void test_selectList() {
        final PagingList<StemmerOverrideItem> itemList1 = stemmerOverrideFile.selectList(0, 20); // error occurs
        assertEquals(3, itemList1.size());
        assertEquals(3, itemList1.getAllRecordCount());
        assertEquals(1, itemList1.getCurrentPageNumber());
        assertEquals(20, itemList1.getPageSize());

        final PagingList<StemmerOverrideItem> itemList2 = stemmerOverrideFile.selectList(2, 2);
        assertEquals(1, itemList2.size());
        assertEquals(3, itemList2.getAllRecordCount());
        assertEquals(2, itemList2.getCurrentPageNumber());
        assertEquals(2, itemList2.getPageSize());

        assertEquals(0, stemmerOverrideFile.selectList(5, 5).size());
        assertEquals(0, stemmerOverrideFile.selectList(-1, 5).size());
    }

}
