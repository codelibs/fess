/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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

import java.io.File;

import org.codelibs.core.io.FileUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.unit.UnitFessTestCase;

public class SynonymFileTest extends UnitFessTestCase {
    private File file1;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        file1 = File.createTempFile("synonym", ".txt");
        FileUtil.writeBytes(file1.getAbsolutePath(), "a1=>A1\nb1,b2 => B1\nc1 => C1, C2\nx1,X1\ny1, Y1, y2".getBytes(Constants.UTF_8));
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        file1.delete();
    }

    public void test_create() {
        /*
        final SynonymCreator synonymCreator = new SynonymCreator();
        final SynonymFile synonymFile = (SynonymFile) synonymCreator.create(file1.getPath(), new Date());
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
        */
    }

}
