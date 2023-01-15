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
package org.codelibs.fess.dict.synonym;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.unit.UnitFessTestCase;

public class SynonymFileTest extends UnitFessTestCase {
    private SynonymFile synonymFile;

    // TODO
    /*
    private File file1;
    @Override
    public void setUp() throws Exception {
        super.setUp();
        file1 = File.createTempFile("synonym", ".txt");
        FileUtil.writeBytes(file1.getAbsolutePath(),
                "a1=>A1\nb1,b2 => B1\nc1 => C1, C2\nx1,X1\ny1, Y1, y2"
                        .getBytes(Constants.UTF_8));
        // TODO set up opensearch and dictionaryManager
        synonymFile = new SynonymFile("1", file1.getAbsolutePath(), new Date());
    }

    @Override
    public void tearDown() throws Exception {
        file1.delete();
    }
    */

    @Override
    public void setUp() throws Exception {
        super.setUp();
        synonymFile = new SynonymFile("1", "dummy", new Date());
        List<SynonymItem> itemList = new ArrayList<>();
        itemList.add(new SynonymItem(1, new String[] { "a1" }, new String[] { "A1" }));
        itemList.add(new SynonymItem(2, new String[] { "b1", "b2" }, new String[] { "B1" }));
        itemList.add(new SynonymItem(3, new String[] { "c1" }, new String[] { "C1", "C2" }));
        itemList.add(new SynonymItem(4, new String[] { "x1", "X1" }, new String[] { "x1", "X1" }));
        itemList.add(new SynonymItem(5, new String[] { "y1", "Y1", "y2" }, new String[] { "y1", "Y1", "y2" }));
        synonymFile.synonymItemList = itemList;
    }

    public void test_selectList() {
        final PagingList<SynonymItem> itemList1 = synonymFile.selectList(0, 20); // error occurs
        assertEquals(5, itemList1.size());
        assertEquals(5, itemList1.getAllRecordCount());
        assertEquals(1, itemList1.getCurrentPageNumber());
        assertEquals(20, itemList1.getPageSize());

        final PagingList<SynonymItem> itemList2 = synonymFile.selectList(4, 2);
        assertEquals(1, itemList2.size());
        assertEquals(5, itemList2.getAllRecordCount());
        assertEquals(3, itemList2.getCurrentPageNumber());
        assertEquals(2, itemList2.getPageSize());

        assertEquals(0, synonymFile.selectList(5, 5).size());
        assertEquals(0, synonymFile.selectList(-1, 5).size());
    }

    public void test_selectList2() {
        final PagingList<SynonymItem> itemList = synonymFile.selectList(0, 5);
        assertEquals(1, itemList.get(0).getInputs().length);
        assertEquals(1, itemList.get(0).getOutputs().length);
        assertEquals("a1", itemList.get(0).getInputs()[0]);
        assertEquals("A1", itemList.get(0).getOutputs()[0]);
        assertFalse(itemList.get(0).isUpdated());

        assertEquals(2, itemList.get(1).getInputs().length);
        assertEquals(1, itemList.get(1).getOutputs().length);
        assertEquals("b1", itemList.get(1).getInputs()[0]);
        assertEquals("b2", itemList.get(1).getInputs()[1]);
        assertEquals("B1", itemList.get(1).getOutputs()[0]);
        assertFalse(itemList.get(1).isUpdated());

        assertEquals(1, itemList.get(2).getInputs().length);
        assertEquals(2, itemList.get(2).getOutputs().length);
        assertEquals("c1", itemList.get(2).getInputs()[0]);
        assertEquals("C1", itemList.get(2).getOutputs()[0]);
        assertEquals("C2", itemList.get(2).getOutputs()[1]);
        assertFalse(itemList.get(2).isUpdated());

        assertEquals(2, itemList.get(3).getInputs().length);
        assertEquals(2, itemList.get(3).getOutputs().length);
        assertEquals("x1", itemList.get(3).getInputs()[0]);
        assertEquals("X1", itemList.get(3).getInputs()[1]);
        assertEquals("x1", itemList.get(3).getOutputs()[0]);
        assertEquals("X1", itemList.get(3).getOutputs()[1]);
        assertFalse(itemList.get(3).isUpdated());

        assertEquals(3, itemList.get(4).getInputs().length);
        assertEquals(3, itemList.get(4).getOutputs().length);
        assertEquals("y1", itemList.get(4).getInputs()[0]);
        assertEquals("Y1", itemList.get(4).getInputs()[1]);
        assertEquals("y2", itemList.get(4).getInputs()[2]);
        assertEquals("y1", itemList.get(4).getOutputs()[0]);
        assertEquals("Y1", itemList.get(4).getOutputs()[1]);
        assertEquals("y2", itemList.get(4).getOutputs()[2]);
        assertFalse(itemList.get(4).isUpdated());
    }

    /*
    public void test_insert() {
         final PagingList<SynonymItem> itemList1 = synonymFile.selectList(0, 20);
         assertEquals(5, itemList1.size());

         final SynonymItem synonymItem1 = new SynonymItem(0, new String[] { "z1", "z2" }, new String[] { "Z1", "Z2" });
         synonymFile.insert(synonymItem1);
         final PagingList<SynonymItem> itemList2 = synonymFile.selectList(0, 20);
         assertEquals(6, itemList2.size());
         assertEquals("z1", itemList2.get(5).getInputs()[0]);
         assertEquals("z2", itemList2.get(5).getInputs()[1]);
         assertEquals("Z1", itemList2.get(5).getOutputs()[0]);
         assertEquals("Z2", itemList2.get(5).getOutputs()[1]);

         final SynonymItem synonymItem2 = new SynonymItem(0, new String[] {"z1", "z2" }, new String[] { "z1", "z2" });
         synonymFile.insert(synonymItem2);
         final PagingList<SynonymItem> itemList3 = synonymFile.selectList(0, 20);
         assertEquals(7, itemList3.size());
         assertEquals("z1", itemList3.get(6).getInputs()[0]);
         assertEquals("z2", itemList3.get(6).getInputs()[1]);
         assertEquals("z1", itemList3.get(6).getOutputs()[0]);
         assertEquals("z2", itemList3.get(6).getOutputs()[1]);
     }

     public void test_update() {
         final SynonymFile synonymFile = new SynonymFile(file1);
         final PagingList<SynonymItem> itemList1 = synonymFile.selectList(0, 20);
         assertEquals(5, itemList1.size());

         final SynonymItem synonymItem1 = itemList1.get(0);
         synonymItem1.setNewInputs(new String[] { "a1", "a2" });
         synonymItem1.setNewOutputs(new String[] { "A1", "A2" });
         synonymFile.update(synonymItem1);
         final PagingList<SynonymItem> itemList2 = synonymFile.selectList(0, 20);
         assertEquals(5, itemList2.size());
         final SynonymItem synonymItem2 = itemList2.get(0);
         assertEquals(2, synonymItem2.getInputs().length);
         assertEquals(2, synonymItem2.getOutputs().length);
         assertEquals("a1", synonymItem2.getInputs()[0]);
         assertEquals("a2", synonymItem2.getInputs()[1]);
         assertEquals("A1", synonymItem2.getOutputs()[0]);
         assertEquals("A2", synonymItem2.getOutputs()[1]);
         assertFalse(synonymItem2.isUpdated());

         final SynonymItem synonymItem3 = itemList2.get(2);
         synonymItem3.setNewInputs(new String[] { "c1", "c2" });
         synonymItem3.setNewOutputs(new String[] { "c1", "c2" });
         synonymFile.update(synonymItem3);
         final PagingList<SynonymItem> itemList3 = synonymFile.selectList(0, 20);
         assertEquals(5, itemList3.size());
         final SynonymItem synonymItem4 = itemList3.get(2);
         assertEquals(2, synonymItem4.getInputs().length);
         assertEquals(2, synonymItem4.getOutputs().length);
         assertEquals("c1", synonymItem4.getInputs()[0]);
         assertEquals("c2", synonymItem4.getInputs()[1]);
         assertEquals("c1", synonymItem4.getOutputs()[0]);
         assertEquals("c2", synonymItem4.getOutputs()[1]);
         assertFalse(synonymItem2.isUpdated());
     }

     public void test_delete() throws Exception {
         final SynonymFile synonymFile = new SynonymFile(file1);
         final PagingList<SynonymItem> itemList1 = synonymFile.selectList(0, 20);
         assertEquals(5, itemList1.size());

         final SynonymItem synonymItem1 = itemList1.get(0);
         synonymFile.delete(synonymItem1);
         final PagingList<SynonymItem> itemList2 = synonymFile.selectList(0, 20);
         assertEquals(4, itemList2.size());

         final SynonymItem synonymItem2 = itemList2.get(3);
         synonymFile.delete(synonymItem2);
         final PagingList<SynonymItem> itemList3 = synonymFile.selectList(0, 20);
         assertEquals(3, itemList3.size());

         assertEquals(
                 "b1,b2=>B1" + Constants.LINE_SEPARATOR + "c1=>C1,C2"
                         + Constants.LINE_SEPARATOR + "X1,x1"
                         + Constants.LINE_SEPARATOR,
                 new String(FileUtil.getBytes(file1), Constants.UTF_8));
     }
     */
}
