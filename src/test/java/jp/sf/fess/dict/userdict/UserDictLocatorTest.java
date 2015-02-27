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

package jp.sf.fess.dict.userdict;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.sf.fess.Constants;
import jp.sf.fess.dict.DictionaryFile;
import jp.sf.fess.dict.DictionaryItem;

import org.apache.commons.io.FileUtils;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.FileUtil;

public class UserDictLocatorTest extends S2TestCase {

    private File testDir;

    private File testDataDir;

    private File testCoreDir;

    private File userDictFile1;

    private File userDictFile2;

    private File userDictFile3;

    private File dummyFile1;

    private File dummyFile2;

    @Override
    protected void setUp() throws Exception {
        testDir = File.createTempFile("userdicttest", "_dir");
        testDir.delete();
        testDir.mkdirs();
        testDataDir = new File(testDir, "data");
        testDataDir.mkdirs();
        testCoreDir = new File(testDir, "core");
        testCoreDir.mkdirs();
        userDictFile1 = new File(testDir, "userdict.txt");
        FileUtil.write(userDictFile1.getAbsolutePath(),
                "abc=>123\nxyz,890".getBytes(Constants.UTF_8));
        userDictFile2 = new File(testDataDir, "userdict_data.txt");
        FileUtil.write(userDictFile2.getAbsolutePath(),
                "abc=>123\nxyz,890".getBytes(Constants.UTF_8));
        userDictFile3 = new File(testCoreDir, "userdict_core.txt");
        FileUtil.write(userDictFile3.getAbsolutePath(),
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
        final UserDictLocator userDictLocator = new UserDictLocator();
        userDictLocator.excludedUserDictList = new ArrayList<String>();
        userDictLocator.excludedUserDictList.add("data");
        userDictLocator.addSearchPath(testDir.getAbsolutePath());
        final List<DictionaryFile<? extends DictionaryItem>> list = userDictLocator
                .find();
        assertEquals(2, list.size());
        final DictionaryFile<? extends DictionaryItem> dicFile1 = list.get(0);
        final DictionaryFile<? extends DictionaryItem> dicFile2 = list.get(1);
        assertEquals(userDictFile3.getAbsolutePath(), dicFile1.getName());
        assertEquals(userDictFile1.getAbsolutePath(), dicFile2.getName());
    }

}
