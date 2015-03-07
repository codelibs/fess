/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.dict.userdict;

import java.io.File;

import org.codelibs.fess.Constants;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.FileUtil;

public class UserDictFileTest extends S2TestCase {

    private File file1;

    @Override
    protected void setUp() throws Exception {
        file1 = File.createTempFile("userdict_", ".txt");
        FileUtil.write(file1.getAbsolutePath(),
                "token1,seg1,reading1,pos1\ntoken2,seg2,reading2,pos2\ntoken3,seg3,reading3,pos3".getBytes(Constants.UTF_8));
    }

    @Override
    protected void tearDown() throws Exception {
        file1.delete();
    }

    public void test_selectList() {
        final UserDictFile userDictFile = new UserDictFile(file1);
        final PagingList<UserDictItem> itemList1 = userDictFile.selectList(0, 20);
        assertEquals(3, itemList1.size());
        assertEquals(3, itemList1.getAllRecordCount());
        assertEquals(1, itemList1.getCurrentPageNumber());
        assertEquals(20, itemList1.getPageSize());

        final PagingList<UserDictItem> itemList2 = userDictFile.selectList(2, 2);
        assertEquals(1, itemList2.size());
        assertEquals(3, itemList2.getAllRecordCount());
        assertEquals(2, itemList2.getCurrentPageNumber());
        assertEquals(2, itemList2.getPageSize());

        assertEquals(0, userDictFile.selectList(5, 5).size());
        assertEquals(0, userDictFile.selectList(-1, 5).size());
    }

    public void test_selectList2() {
        final UserDictFile userDictFile = new UserDictFile(file1);
        final PagingList<UserDictItem> itemList = userDictFile.selectList(0, 5);
        for (int i = 0; i < itemList.size(); i++) {
            final UserDictItem userDictItem = itemList.get(i);
            assertEquals("token" + (i + 1), userDictItem.getToken());
            assertEquals("seg" + (i + 1), userDictItem.getSegmentation());
            assertEquals("reading" + (i + 1), userDictItem.getReading());
            assertEquals("pos" + (i + 1), userDictItem.getPos());
            assertFalse(userDictItem.isUpdated());
            assertFalse(userDictItem.isUpdated());
        }
    }

    public void test_insert() {
        final UserDictFile userDictFile = new UserDictFile(file1);
        final PagingList<UserDictItem> itemList1 = userDictFile.selectList(0, 20);
        assertEquals(3, itemList1.size());

        final UserDictItem userDictItem1 = new UserDictItem(0, "token4", "seg4", "reading4", "pos4");
        userDictFile.insert(userDictItem1);
        final PagingList<UserDictItem> itemList2 = userDictFile.selectList(0, 20);
        assertEquals(4, itemList2.size());
        assertEquals("token4", itemList2.get(3).getToken());
        assertEquals("seg4", itemList2.get(3).getSegmentation());
        assertEquals("reading4", itemList2.get(3).getReading());
        assertEquals("pos4", itemList2.get(3).getPos());

        final UserDictItem userDictItem2 = new UserDictItem(0, "token5", "seg5", "reading5", "pos5");
        userDictFile.insert(userDictItem2);
        final PagingList<UserDictItem> itemList3 = userDictFile.selectList(0, 20);
        assertEquals(5, itemList3.size());
        assertEquals("token5", itemList3.get(4).getToken());
        assertEquals("seg5", itemList3.get(4).getSegmentation());
        assertEquals("reading5", itemList3.get(4).getReading());
        assertEquals("pos5", itemList3.get(4).getPos());
    }

    public void test_update() {
        final UserDictFile userDictFile = new UserDictFile(file1);
        final PagingList<UserDictItem> itemList1 = userDictFile.selectList(0, 20);
        assertEquals(3, itemList1.size());

        final UserDictItem userDictItem1 = itemList1.get(0);
        userDictItem1.setNewToken("TOKEN1");
        userDictItem1.setNewSegmentation("SEG1");
        userDictItem1.setNewReading("READING1");
        userDictItem1.setNewPos("POS1");
        userDictFile.update(userDictItem1);
        final PagingList<UserDictItem> itemList2 = userDictFile.selectList(0, 20);
        assertEquals(3, itemList2.size());
        final UserDictItem userDictItem2 = itemList2.get(0);
        assertEquals("TOKEN1", userDictItem2.getToken());
        assertEquals("SEG1", userDictItem2.getSegmentation());
        assertEquals("READING1", userDictItem2.getReading());
        assertEquals("POS1", userDictItem2.getPos());
        assertFalse(userDictItem2.isUpdated());

        final UserDictItem userDictItem3 = itemList2.get(2);
        userDictItem3.setNewToken("TOKEN3");
        userDictItem3.setNewSegmentation("SEG3");
        userDictItem3.setNewReading("READING3");
        userDictItem3.setNewPos("POS3");
        userDictFile.update(userDictItem3);
        final PagingList<UserDictItem> itemList3 = userDictFile.selectList(0, 20);
        assertEquals(3, itemList3.size());
        final UserDictItem userDictItem4 = itemList3.get(2);
        assertEquals("TOKEN3", userDictItem4.getToken());
        assertEquals("SEG3", userDictItem4.getSegmentation());
        assertEquals("READING3", userDictItem4.getReading());
        assertEquals("POS3", userDictItem4.getPos());
        assertFalse(userDictItem4.isUpdated());
    }

    public void test_delete() throws Exception {
        final UserDictFile userDictFile = new UserDictFile(file1);
        final PagingList<UserDictItem> itemList1 = userDictFile.selectList(0, 20);
        assertEquals(3, itemList1.size());

        final UserDictItem userDictItem1 = itemList1.get(0);
        userDictFile.delete(userDictItem1);
        final PagingList<UserDictItem> itemList2 = userDictFile.selectList(0, 20);
        assertEquals(2, itemList2.size());

        final UserDictItem userDictItem2 = itemList2.get(1);
        userDictFile.delete(userDictItem2);
        final PagingList<UserDictItem> itemList3 = userDictFile.selectList(0, 20);
        assertEquals(1, itemList3.size());

        assertEquals("token2,seg2,reading2,pos2" + Constants.LINE_SEPARATOR, new String(FileUtil.getBytes(file1), Constants.UTF_8));

    }
}
