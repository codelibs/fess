/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.dict.synonym;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.sf.fess.Constants;
import jp.sf.fess.dict.DictionaryFile;
import jp.sf.fess.dict.DictionaryItem;

import org.apache.commons.io.FileUtils;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.FileUtil;

public class SynonymLocatorTest extends S2TestCase {

    private File testDir;

    private File testDataDir;

    private File testCoreDir;

    private File synonymFile1;

    private File synonymFile2;

    private File synonymFile3;

    private File dummyFile1;

    private File dummyFile2;

    @Override
    protected void setUp() throws Exception {
        testDir = File.createTempFile("synonymtest", "_dir");
        testDir.delete();
        testDir.mkdirs();
        testDataDir = new File(testDir, "data");
        testDataDir.mkdirs();
        testCoreDir = new File(testDir, "core");
        testCoreDir.mkdirs();
        synonymFile1 = new File(testDir, "synonym.txt");
        FileUtil.write(synonymFile1.getAbsolutePath(),
                "abc=>123\nxyz,890".getBytes(Constants.UTF_8));
        synonymFile2 = new File(testDataDir, "synonym_data.txt");
        FileUtil.write(synonymFile2.getAbsolutePath(),
                "abc=>123\nxyz,890".getBytes(Constants.UTF_8));
        synonymFile3 = new File(testCoreDir, "synonym_core.txt");
        FileUtil.write(synonymFile3.getAbsolutePath(),
                "abc=>123\nxyz,890".getBytes(Constants.UTF_8));
        dummyFile1 = new File(testDir, "dummy.txt");
        FileUtil.write(dummyFile1.getAbsolutePath(),
                "dummy 1".getBytes(Constants.UTF_8));
        dummyFile2 = new File(testCoreDir, "dummy.txt");
        FileUtil.write(dummyFile2.getAbsolutePath(),
                "dummy 2".getBytes(Constants.UTF_8));

    }

    @Override
    protected void tearDown() throws Exception {
        FileUtils.deleteDirectory(testDir);
    }

    public void test_find() {
        final SynonymLocator synonymLocator = new SynonymLocator();
        synonymLocator.excludedSynonymList = new ArrayList<String>();
        synonymLocator.excludedSynonymList.add("data");
        synonymLocator.addSearchPath(testDir.getAbsolutePath());
        final List<DictionaryFile<? extends DictionaryItem>> list = synonymLocator
                .find();
        assertEquals(2, list.size());
        final DictionaryFile<? extends DictionaryItem> dicFile1 = list.get(0);
        final DictionaryFile<? extends DictionaryItem> dicFile2 = list.get(1);
        assertEquals(synonymFile1.getAbsolutePath(), dicFile1.getName());
        assertEquals(synonymFile3.getAbsolutePath(), dicFile2.getName());
    }

}
