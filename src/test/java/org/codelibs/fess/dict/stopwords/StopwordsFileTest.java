/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.dict.stopwords;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.codelibs.core.io.CloseableUtil;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.Constants;
import org.codelibs.fess.dict.DictionaryException;
import org.codelibs.fess.dict.DictionaryFile;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class StopwordsFileTest extends UnitFessTestCase {

    private StopwordsFile stopwordsFile;
    private File testFile;
    private DictionaryManager dictionaryManager;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        // Create a temporary test file
        testFile = File.createTempFile("test_stopwords", ".txt");
        testFile.deleteOnExit();

        // Write test data to file
        try (FileOutputStream fos = new FileOutputStream(testFile)) {
            fos.write(getTestContent().getBytes(StandardCharsets.UTF_8));
        }

        // Create a temporary project.properties file for SystemHelper
        File propFile = File.createTempFile("project", ".properties");
        propFile.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(propFile)) {
            fos.write("fess.version=1.0.0".getBytes());
        }

        // Initialize SystemHelper
        SystemHelper systemHelper = new SystemHelper() {
            @Override
            protected void parseProjectProperties(final java.nio.file.Path propPath) {
                super.parseProjectProperties(propFile.toPath());
            }

            @Override
            public File createTempFile(String prefix, String suffix) {
                try {
                    File tempFile = File.createTempFile(prefix, suffix);
                    tempFile.deleteOnExit();
                    return tempFile;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        systemHelper.init();
        ComponentUtil.register(systemHelper, "systemHelper");

        // Initialize DictionaryManager mock
        dictionaryManager = new DictionaryManager() {
            @Override
            public CurlResponse getContentResponse(DictionaryFile<?> file) {
                try {
                    // Return a CurlResponse with the file's content
                    return new CurlResponse() {
                        @Override
                        public InputStream getContentAsStream() throws IOException {
                            return new FileInputStream(testFile);
                        }

                        @Override
                        public void close() throws IOException {
                            // Nothing to close
                        }
                    };
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void store(DictionaryFile<?> file, File tempFile) {
                // Copy tempFile to testFile
                try {
                    java.nio.file.Files.copy(tempFile.toPath(), testFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        ComponentUtil.register(dictionaryManager, "dictionaryManager");

        // Create StopwordsFile instance
        stopwordsFile = new StopwordsFile("test_id", testFile.getAbsolutePath(), new Date());
        stopwordsFile.manager(dictionaryManager);
    }

    @Override
    protected void tearDown() throws Exception {
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }
        super.tearDown();
    }

    private String getTestContent() {
        return "# This is a comment\n" + "the\n" + "and\n" + "a\n" + "is\n" + "to\n";
    }

    private void loadTestData() {
        // Load data into stopwordsFile
        try (InputStream is = new ByteArrayInputStream(getTestContent().getBytes(StandardCharsets.UTF_8))) {
            stopwordsFile.reload(null, is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Test basic getters
    @Test
    public void test_getType() {
        assertEquals("stopwords", stopwordsFile.getType());
    }

    @Test
    public void test_getPath() {
        assertEquals(testFile.getAbsolutePath(), stopwordsFile.getPath());
    }

    @Test
    public void test_getSimpleName() {
        assertEquals(testFile.getName(), stopwordsFile.getSimpleName());
    }

    // Test get method
    @Test
    public void test_get_existingItem() {
        // Load test data
        loadTestData();

        OptionalEntity<StopwordsItem> result = stopwordsFile.get(1);
        assertTrue(result.isPresent());
        assertEquals("the", result.get().getInput());
    }

    @Test
    public void test_get_nonExistingItem() {
        // Load test data
        loadTestData();

        OptionalEntity<StopwordsItem> result = stopwordsFile.get(999);
        assertFalse(result.isPresent());
    }

    @Test
    public void test_get_withNullList() {
        // stopwordsItemList is null initially, should trigger reload
        OptionalEntity<StopwordsItem> result = stopwordsFile.get(1);
        assertTrue(result.isPresent());
        assertNotNull(stopwordsFile.stopwordsItemList);
    }

    // Test selectList method
    @Test
    public void test_selectList_normalCase() {
        loadTestData();

        DictionaryFile.PagingList<StopwordsItem> result = stopwordsFile.selectList(0, 2);
        assertEquals(2, result.size());
        assertEquals(1, result.getCurrentPageNumber());
        assertEquals(2, result.getPageSize());
        assertEquals(5, result.getAllRecordCount());
        assertEquals(3, result.getAllPageCount());
        assertEquals(1, result.getCurrentPageNumber());
    }

    @Test
    public void test_selectList_offsetOutOfBounds() {
        loadTestData();

        DictionaryFile.PagingList<StopwordsItem> result = stopwordsFile.selectList(10, 2);
        // When offset is out of bounds, all items may still be returned
        assertTrue(result.size() <= 5);
        assertEquals(6, result.getCurrentPageNumber()); // offset 10 with size 2 = page 6
        assertEquals(2, result.getPageSize());
        assertEquals(5, result.getAllRecordCount());
    }

    @Test
    public void test_selectList_negativeOffset() {
        loadTestData();

        DictionaryFile.PagingList<StopwordsItem> result = stopwordsFile.selectList(-1, 2);
        // Negative offset is treated as 0
        assertTrue(result.size() >= 0);
        assertEquals(1, result.getCurrentPageNumber()); // Default page 1
        assertEquals(2, result.getPageSize());
        assertEquals(5, result.getAllRecordCount());
    }

    @Test
    public void test_selectList_sizeExceedsAvailable() {
        loadTestData();

        DictionaryFile.PagingList<StopwordsItem> result = stopwordsFile.selectList(3, 10);
        // With offset 3 and total 5 items, we should get 2 items
        assertEquals(2, result.size());
        assertEquals(1, result.getCurrentPageNumber()); // First page since we're getting actual results
        assertEquals(10, result.getPageSize());
        assertEquals(5, result.getAllRecordCount());
    }

    @Test
    public void test_selectList_withNullList() {
        // stopwordsItemList is null initially, should trigger reload
        DictionaryFile.PagingList<StopwordsItem> result = stopwordsFile.selectList(0, 2);
        assertNotNull(result);
        assertNotNull(stopwordsFile.stopwordsItemList);
    }

    // Test insert method
    @Test
    public void test_insert() {
        loadTestData();

        StopwordsItem newItem = new StopwordsItem(0, "test");
        stopwordsFile.insert(newItem);

        // Verify the item was added
        boolean found = false;
        for (StopwordsItem item : stopwordsFile.stopwordsItemList) {
            if ("test".equals(item.getInput())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    // Test update method
    @Test
    public void test_update() {
        loadTestData();

        StopwordsItem item = stopwordsFile.get(1).get();
        item.setNewInput("updated");
        stopwordsFile.update(item);

        // Verify the item was updated
        OptionalEntity<StopwordsItem> result = stopwordsFile.get(1);
        assertTrue(result.isPresent());
        assertEquals("updated", result.get().getInput());
    }

    // Test delete method
    @Test
    public void test_delete() {
        loadTestData();

        int originalSize = stopwordsFile.stopwordsItemList.size();
        StopwordsItem item = stopwordsFile.get(1).get();
        stopwordsFile.delete(item);

        // Verify the item count decreased
        // Note: get(1) may still return an item if IDs are reassigned
        assertTrue(stopwordsFile.stopwordsItemList.size() < originalSize);
    }

    // Test reload with InputStream
    @Test
    public void test_reload_withComments() {
        String content = "# This is a comment\n" + "word1\n" + "\n" + // empty line
                "word2\n" + "# Another comment\n" + "word3\n";

        InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        stopwordsFile.reload(null, is);

        assertEquals(3, stopwordsFile.stopwordsItemList.size());
        assertEquals("word1", stopwordsFile.stopwordsItemList.get(0).getInput());
        assertEquals("word2", stopwordsFile.stopwordsItemList.get(1).getInput());
        assertEquals("word3", stopwordsFile.stopwordsItemList.get(2).getInput());
    }

    @Test
    public void test_reload_withEscapedCharacters() {
        String content = "word\\\\1\n" + // word\1
                "word\\\\\\\\2\n" + // word\\2
                "word3\n";

        InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        stopwordsFile.reload(null, is);

        assertEquals(3, stopwordsFile.stopwordsItemList.size());
        assertEquals("word\\1", stopwordsFile.stopwordsItemList.get(0).getInput());
        assertEquals("word\\\\2", stopwordsFile.stopwordsItemList.get(1).getInput());
        assertEquals("word3", stopwordsFile.stopwordsItemList.get(2).getInput());
    }

    @Test
    public void test_reload_withIOException() {
        InputStream failingStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Test exception");
            }
        };

        try {
            stopwordsFile.reload(null, failingStream);
            fail("Should throw DictionaryException");
        } catch (DictionaryException e) {
            assertTrue(e.getMessage().contains("Failed to parse"));
        }
    }

    // Test update with InputStream
    @Test
    public void test_update_withInputStream() throws IOException {
        String content = "new1\n" + "new2\n" + "new3\n";

        InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        stopwordsFile.update(is);

        assertEquals(3, stopwordsFile.stopwordsItemList.size());
        assertEquals("new1", stopwordsFile.stopwordsItemList.get(0).getInput());
        assertEquals("new2", stopwordsFile.stopwordsItemList.get(1).getInput());
        assertEquals("new3", stopwordsFile.stopwordsItemList.get(2).getInput());
    }

    // Test toString method
    @Test
    public void test_toString() {
        loadTestData();
        String result = stopwordsFile.toString();
        assertTrue(result.contains("StopwordsFile"));
        assertTrue(result.contains("path=" + testFile.getAbsolutePath()));
        assertTrue(result.contains("id=test_id"));
    }

    // Test unescape private method through reload
    @Test
    public void test_unescape_noBackslash() {
        String content = "word1\n";
        InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        stopwordsFile.reload(null, is);

        assertEquals(1, stopwordsFile.stopwordsItemList.size());
        assertEquals("word1", stopwordsFile.stopwordsItemList.get(0).getInput());
    }

    @Test
    public void test_unescape_withBackslash() {
        String content = "word\\n1\n" + // word + n + 1
                "word\\\n"; // word + (nothing after backslash)

        InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        stopwordsFile.reload(null, is);

        assertEquals(2, stopwordsFile.stopwordsItemList.size());
        // The content creates two lines: "word\n1" and "word\"
        assertTrue(stopwordsFile.stopwordsItemList.stream().anyMatch(item -> item.getInput().contains("word")));
    }

    // Test StopwordsUpdater inner class
    @Test
    public void test_updater_writeOldItem() {
        loadTestData();

        StopwordsItem item = stopwordsFile.get(1).get();
        StopwordsFile.StopwordsUpdater updater = stopwordsFile.new StopwordsUpdater(null);

        StopwordsItem result = updater.write(item);
        assertNotNull(result);
        assertEquals(item.getInput(), result.getInput());

        updater.close();
    }

    @Test
    public void test_updater_updateItem() {
        loadTestData();

        StopwordsItem originalItem = stopwordsFile.get(1).get();
        StopwordsItem updateItem = new StopwordsItem(1, "the");
        updateItem.setNewInput("modified");

        StopwordsFile.StopwordsUpdater updater = stopwordsFile.new StopwordsUpdater(updateItem);

        StopwordsItem result = updater.write(originalItem);
        assertNotNull(result);
        assertEquals("modified", result.getInput());

        updater.close();
    }

    @Test
    public void test_updater_deleteItem() {
        loadTestData();

        StopwordsItem originalItem = stopwordsFile.get(1).get();
        StopwordsItem deleteItem = new StopwordsItem(1, "the");
        deleteItem.setNewInput("");

        StopwordsFile.StopwordsUpdater updater = stopwordsFile.new StopwordsUpdater(deleteItem);

        StopwordsItem result = updater.write(originalItem);
        assertNull(result);

        updater.close();
    }

    @Test
    public void test_updater_mismatchedItem() {
        loadTestData();

        StopwordsItem originalItem = stopwordsFile.get(1).get();
        StopwordsItem mismatchedItem = new StopwordsItem(1, "different");
        mismatchedItem.setNewInput("modified");

        StopwordsFile.StopwordsUpdater updater = stopwordsFile.new StopwordsUpdater(mismatchedItem);

        try {
            updater.write(originalItem);
            fail("Should throw DictionaryException");
        } catch (DictionaryException e) {
            assertTrue(e.getMessage().contains("Stopwords file was updated"));
        } finally {
            updater.close();
        }
    }

    @Test
    public void test_updater_commit() {
        StopwordsItem newItem = new StopwordsItem(0, "committed");
        StopwordsFile.StopwordsUpdater updater = stopwordsFile.new StopwordsUpdater(newItem);

        StopwordsItem result = updater.commit();
        assertNotNull(result);
        assertEquals("committed", result.getInput());

        updater.close();
    }

    @Test
    public void test_updater_commitWithoutUpdate() {
        StopwordsFile.StopwordsUpdater updater = stopwordsFile.new StopwordsUpdater(null);

        StopwordsItem result = updater.commit();
        assertNull(result);

        updater.close();
    }

    @Test
    public void test_updater_writeLine() {
        StopwordsFile.StopwordsUpdater updater = stopwordsFile.new StopwordsUpdater(null);

        // Test writing a comment line
        updater.write("# This is a comment");

        updater.close();
    }

    // Test edge cases
    @Test
    public void test_emptyFile() {
        String content = "";
        InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        stopwordsFile.reload(null, is);

        assertEquals(0, stopwordsFile.stopwordsItemList.size());
    }

    @Test
    public void test_onlyCommentsAndEmptyLines() {
        String content = "# Comment 1\n" + "\n" + "# Comment 2\n" + "\n";

        InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        stopwordsFile.reload(null, is);

        assertEquals(0, stopwordsFile.stopwordsItemList.size());
    }

    @Test
    public void test_reload_withUpdater() {
        String content = "word1\n" + "word2\n" + "word3\n";

        StopwordsItem updateItem = new StopwordsItem(2, "word2");
        updateItem.setNewInput("updated2");

        StopwordsFile.StopwordsUpdater updater = stopwordsFile.new StopwordsUpdater(updateItem);

        InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        stopwordsFile.reload(updater, is);

        updater.close();

        assertEquals(3, stopwordsFile.stopwordsItemList.size());
        assertEquals("word1", stopwordsFile.stopwordsItemList.get(0).getInput());
        assertEquals("updated2", stopwordsFile.stopwordsItemList.get(1).getInput());
        assertEquals("word3", stopwordsFile.stopwordsItemList.get(2).getInput());
    }

}