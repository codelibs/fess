package org.codelibs.fess.dict;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.codelibs.core.io.FileUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.unit.UnitFessTestCase;

public class DictionaryManagerTest extends UnitFessTestCase {
    private File testDir;

    private File synonymFile1;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        testDir = File.createTempFile("synonymtest", "_dir");
        testDir.delete();
        testDir.mkdirs();
        synonymFile1 = new File(testDir, "synonym.txt");
        FileUtil.writeBytes(synonymFile1.getAbsolutePath(), "abc=>123\nxyz,890".getBytes(Constants.UTF_8));
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        FileUtils.deleteDirectory(testDir);
    }

    public void test_getSynonymFiles() throws Exception {
        /*
        final DictionaryManager dictionaryManager = new DictionaryManager();
        dictionaryManager.addCreator(new SynonymCreator(synonymFile1.getPath()));
        dictionaryManager.init();
        final DictionaryFile<? extends DictionaryItem>[] synonymFiles = dictionaryManager.getDictionaryFiles();
        assertEquals(1, synonymFiles.length);
        */
    }

}
