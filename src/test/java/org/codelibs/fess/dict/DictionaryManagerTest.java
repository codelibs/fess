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
package org.codelibs.fess.dict;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.codelibs.core.io.FileUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.dict.mapping.CharMappingCreator;
import org.codelibs.fess.unit.UnitFessTestCase;

public class DictionaryManagerTest extends UnitFessTestCase {
    private File testDir;

    private File file1;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        testDir = File.createTempFile("synonymtest", "_dir");
        testDir.delete();
        testDir.mkdirs();
        file1 = new File(testDir, "synonym.txt");
        FileUtil.writeBytes(file1.getAbsolutePath(), "abc=>123\nxyz,890".getBytes(Constants.UTF_8));
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        FileUtils.deleteDirectory(testDir);
    }

    public void test_init() {
        final DictionaryManager dictionaryManager = new DictionaryManager();
        dictionaryManager.init();
        assertEquals(0, dictionaryManager.creatorList.size());

        dictionaryManager.addCreator(new CharMappingCreator());
        dictionaryManager.init();
        assertEquals(1, dictionaryManager.creatorList.size());
    }

    /*
    public void test_storeSynonymFiles() throws Exception {
        final DictionaryManager dictionaryManager = new DictionaryManager();
        final SynonymCreator synonymCreator = new SynonymCreator();
        final SynonymFile synonymFile = (SynonymFile) synonymCreator.create(file1.getPath(), new Date());
        dictionaryManager.store(synonymFile, file1);
        final DictionaryFile<? extends DictionaryItem>[] synonymFiles = dictionaryManager.getDictionaryFiles();
        assertEquals(1, synonymFiles.length);
    }
    */

}
