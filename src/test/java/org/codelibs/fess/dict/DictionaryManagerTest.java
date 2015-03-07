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

package org.codelibs.fess.dict;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.codelibs.fess.Constants;
import org.codelibs.fess.dict.synonym.SynonymLocator;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.FileUtil;

public class DictionaryManagerTest extends S2TestCase {

    private File testDir;

    private File synonymFile1;

    @Override
    protected void setUp() throws Exception {
        testDir = File.createTempFile("synonymtest", "_dir");
        testDir.delete();
        testDir.mkdirs();
        synonymFile1 = new File(testDir, "synonym.txt");
        FileUtil.write(synonymFile1.getAbsolutePath(), "abc=>123\nxyz,890".getBytes(Constants.UTF_8));
    }

    @Override
    protected void tearDown() throws Exception {
        FileUtils.deleteDirectory(testDir);
    }

    public void test_getSynonymFiles() throws Exception {
        final DictionaryManager dictionaryManager = new DictionaryManager();
        dictionaryManager.keepAlive = 1000;
        dictionaryManager.watcherTimeout = 1;
        final SynonymLocator synonymLocator = new SynonymLocator();
        synonymLocator.excludedSynonymList = new ArrayList<String>();
        synonymLocator.excludedSynonymList.add("data");
        synonymLocator.addSearchPath(testDir.getAbsolutePath());
        dictionaryManager.addLocator(synonymLocator);
        dictionaryManager.init();
        final DictionaryFile<? extends DictionaryItem>[] synonymFiles = dictionaryManager.getDictionaryFiles();
        assertEquals(1, synonymFiles.length);

        assertNotNull(dictionaryManager.dicFileMap);
        Thread.sleep(2000);
        assertNull(dictionaryManager.dicFileMap);

        final DictionaryFile<? extends DictionaryItem>[] synonymFiles2 = dictionaryManager.getDictionaryFiles();
        assertEquals(1, synonymFiles2.length);

        assertNotNull(dictionaryManager.dicFileMap);
        Thread.sleep(2000);
        assertNull(dictionaryManager.dicFileMap);
    }
}
