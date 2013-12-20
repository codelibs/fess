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

package jp.sf.fess.synonym;

import java.io.File;
import java.util.HashSet;

import jp.sf.fess.Constants;

import org.apache.commons.io.FileUtils;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.FileUtil;

public class SynonymManagerTest extends S2TestCase {

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

    public void test_findFiles() {
        final SynonymManager synonymManager = new SynonymManager();
        synonymManager.excludedSynonymSet = new HashSet<String>();
        synonymManager.excludedSynonymSet.add("data");
        final File[] files = synonymManager
                .findFiles(testDir.getAbsolutePath());
        assertEquals(2, files.length);
        assertEquals(synonymFile1.getAbsolutePath(), files[0].getAbsolutePath());
        assertEquals(synonymFile3.getAbsolutePath(), files[1].getAbsolutePath());
    }

    public void test_getSynonymFiles() throws Exception {
        final SynonymManager synonymManager = new SynonymManager();
        synonymManager.keepAlive = 1000;
        synonymManager.watcherTimeout = 1;
        synonymManager.excludedSynonymSet = new HashSet<String>();
        synonymManager.excludedSynonymSet.add("data");
        synonymManager.addSearchPath(testDir.getAbsolutePath());
        synonymManager.init();
        final SynonymFile[] synonymFiles = synonymManager.getSynonymFiles();
        assertEquals(2, synonymFiles.length);

        assertNotNull(synonymManager.synonymFileMap);
        Thread.sleep(2000);
        assertNull(synonymManager.synonymFileMap);

        final SynonymFile[] synonymFiles2 = synonymManager.getSynonymFiles();
        assertEquals(2, synonymFiles2.length);

        assertNotNull(synonymManager.synonymFileMap);
        Thread.sleep(2000);
        assertNull(synonymManager.synonymFileMap);
    }
}
