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
package org.codelibs.fess.dict.kuromoji;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.unit.UnitFessTestCase;

public class KuromojiFileTest extends UnitFessTestCase {
    private KuromojiFile kuromojiFile;

    /*
    // TODO
    private File file1;

    @Override
    protected void setUp() throws Exception {
        file1 = File.createTempFile("kuromoji_", ".txt");
        FileUtil.write(
                file1.getAbsolutePath(),
                "token1,seg1,reading1,pos1\ntoken2,seg2,reading2,pos2\ntoken3,seg3,reading3,pos3"
                        .getBytes(Constants.UTF_8));
    }

    @Override
    protected void tearDown() throws Exception {
        file1.delete();
    }
    */

    @Override
    public void setUp() throws Exception {
        super.setUp();
        kuromojiFile = new KuromojiFile("1", "dummy", new Date());
        List<KuromojiItem> itemList = new ArrayList<>();
        itemList.add(new KuromojiItem(1, "token1", "seg1", "reading1", "pos1"));
        itemList.add(new KuromojiItem(2, "token2", "seg2", "reading2", "pos2"));
        itemList.add(new KuromojiItem(3, "token3", "seg3", "reading3", "pos3"));
        kuromojiFile.kuromojiItemList = itemList;
    }

    public void test_selectList() {
        final PagingList<KuromojiItem> itemList1 = kuromojiFile.selectList(0, 20);
        assertEquals(3, itemList1.size());
        assertEquals(3, itemList1.getAllRecordCount());
        assertEquals(1, itemList1.getCurrentPageNumber());
        assertEquals(20, itemList1.getPageSize());

        final PagingList<KuromojiItem> itemList2 = kuromojiFile.selectList(2, 2);
        assertEquals(1, itemList2.size());
        assertEquals(3, itemList2.getAllRecordCount());
        assertEquals(2, itemList2.getCurrentPageNumber());
        assertEquals(2, itemList2.getPageSize());

        assertEquals(0, kuromojiFile.selectList(5, 5).size());
        assertEquals(0, kuromojiFile.selectList(-1, 5).size());
    }

    public void test_selectList2() {
        final PagingList<KuromojiItem> itemList = kuromojiFile.selectList(0, 5);
        for (int i = 0; i < itemList.size(); i++) {
            final KuromojiItem kuromojiItem = itemList.get(i);
            assertEquals("token" + (i + 1), kuromojiItem.getToken());
            assertEquals("seg" + (i + 1), kuromojiItem.getSegmentation());
            assertEquals("reading" + (i + 1), kuromojiItem.getReading());
            assertEquals("pos" + (i + 1), kuromojiItem.getPos());
            assertFalse(kuromojiItem.isUpdated());
            assertFalse(kuromojiItem.isUpdated());
        }
    }

    /*
    // TODO
    public void test_insert() {
        final KuromojiFile kuromojiFile = new KuromojiFile(file1);
        final PagingList<KuromojiItem> itemList1 = kuromojiFile.selectList(0,
                20);
        assertEquals(3, itemList1.size());

        final KuromojiItem kuromojiItem1 = new KuromojiItem(0, "token4",
                "seg4", "reading4", "pos4");
        kuromojiFile.insert(kuromojiItem1);
        final PagingList<KuromojiItem> itemList2 = kuromojiFile.selectList(0,
                20);
        assertEquals(4, itemList2.size());
        assertEquals("token4", itemList2.get(3).getToken());
        assertEquals("seg4", itemList2.get(3).getSegmentation());
        assertEquals("reading4", itemList2.get(3).getReading());
        assertEquals("pos4", itemList2.get(3).getPos());

        final KuromojiItem kuromojiItem2 = new KuromojiItem(0, "token5",
                "seg5", "reading5", "pos5");
        kuromojiFile.insert(kuromojiItem2);
        final PagingList<KuromojiItem> itemList3 = kuromojiFile.selectList(0,
                20);
        assertEquals(5, itemList3.size());
        assertEquals("token5", itemList3.get(4).getToken());
        assertEquals("seg5", itemList3.get(4).getSegmentation());
        assertEquals("reading5", itemList3.get(4).getReading());
        assertEquals("pos5", itemList3.get(4).getPos());
    }

    public void test_update() {
        final KuromojiFile kuromojiFile = new KuromojiFile(file1);
        final PagingList<KuromojiItem> itemList1 = kuromojiFile.selectList(0,
                20);
        assertEquals(3, itemList1.size());

        final KuromojiItem kuromojiItem1 = itemList1.get(0);
        kuromojiItem1.setNewToken("TOKEN1");
        kuromojiItem1.setNewSegmentation("SEG1");
        kuromojiItem1.setNewReading("READING1");
        kuromojiItem1.setNewPos("POS1");
        kuromojiFile.update(kuromojiItem1);
        final PagingList<KuromojiItem> itemList2 = kuromojiFile.selectList(0,
                20);
        assertEquals(3, itemList2.size());
        final KuromojiItem kuromojiItem2 = itemList2.get(0);
        assertEquals("TOKEN1", kuromojiItem2.getToken());
        assertEquals("SEG1", kuromojiItem2.getSegmentation());
        assertEquals("READING1", kuromojiItem2.getReading());
        assertEquals("POS1", kuromojiItem2.getPos());
        assertFalse(kuromojiItem2.isUpdated());

        final KuromojiItem kuromojiItem3 = itemList2.get(2);
        kuromojiItem3.setNewToken("TOKEN3");
        kuromojiItem3.setNewSegmentation("SEG3");
        kuromojiItem3.setNewReading("READING3");
        kuromojiItem3.setNewPos("POS3");
        kuromojiFile.update(kuromojiItem3);
        final PagingList<KuromojiItem> itemList3 = kuromojiFile.selectList(0,
                20);
        assertEquals(3, itemList3.size());
        final KuromojiItem kuromojiItem4 = itemList3.get(2);
        assertEquals("TOKEN3", kuromojiItem4.getToken());
        assertEquals("SEG3", kuromojiItem4.getSegmentation());
        assertEquals("READING3", kuromojiItem4.getReading());
        assertEquals("POS3", kuromojiItem4.getPos());
        assertFalse(kuromojiItem4.isUpdated());
    }

    public void test_delete() throws Exception {
        final KuromojiFile kuromojiFile = new KuromojiFile(file1);
        final PagingList<KuromojiItem> itemList1 = kuromojiFile.selectList(0,
                20);
        assertEquals(3, itemList1.size());

        final KuromojiItem kuromojiItem1 = itemList1.get(0);
        kuromojiFile.delete(kuromojiItem1);
        final PagingList<KuromojiItem> itemList2 = kuromojiFile.selectList(0,
                20);
        assertEquals(2, itemList2.size());

        final KuromojiItem kuromojiItem2 = itemList2.get(1);
        kuromojiFile.delete(kuromojiItem2);
        final PagingList<KuromojiItem> itemList3 = kuromojiFile.selectList(0,
                20);
        assertEquals(1, itemList3.size());

        assertEquals("token2,seg2,reading2,pos2" + Constants.LINE_SEPARATOR,
                new String(FileUtil.getBytes(file1), Constants.UTF_8));

    }
    */

}
