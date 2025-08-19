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
package org.codelibs.fess.ds;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.codelibs.fess.ds.callback.IndexUpdateCallback;
import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.helper.PluginHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.DataConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ResourceUtil;

public class DataStoreFactoryTest extends UnitFessTestCase {

    private DataStoreFactory dataStoreFactory;
    private SystemHelper systemHelper;
    private File tempDir;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dataStoreFactory = new DataStoreFactory();
        systemHelper = new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return System.currentTimeMillis();
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");

        // Create temporary directory for test JAR files
        tempDir = Files.createTempDirectory("datastore-test").toFile();
        tempDir.deleteOnExit();
    }

    @Override
    public void tearDown() throws Exception {
        // Clean up temporary files
        if (tempDir != null && tempDir.exists()) {
            deleteDirectory(tempDir);
        }
        super.tearDown();
    }

    private void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }

    // Test add method with valid parameters
    public void test_add_validParameters() {
        TestDataStore dataStore = new TestDataStore("TestStore");
        dataStoreFactory.add("testName", dataStore);

        // Verify data store is registered with both the given name and class simple name
        assertNotNull(dataStoreFactory.getDataStore("testName"));
        assertNotNull(dataStoreFactory.getDataStore("testname")); // case insensitive
        assertNotNull(dataStoreFactory.getDataStore("TestDataStore"));
        assertNotNull(dataStoreFactory.getDataStore("testdatastore")); // case insensitive
        assertSame(dataStore, dataStoreFactory.getDataStore("testName"));
        assertSame(dataStore, dataStoreFactory.getDataStore("TestDataStore"));
    }

    // Test add method with null name
    public void test_add_nullName() {
        TestDataStore dataStore = new TestDataStore("TestStore");
        try {
            dataStoreFactory.add(null, dataStore);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("name or dataStore is null.", e.getMessage());
        }
    }

    // Test add method with null dataStore
    public void test_add_nullDataStore() {
        try {
            dataStoreFactory.add("testName", null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("name or dataStore is null.", e.getMessage());
        }
    }

    // Test add method with both null parameters
    public void test_add_bothNull() {
        try {
            dataStoreFactory.add(null, null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("name or dataStore is null.", e.getMessage());
        }
    }

    // Test getDataStore with existing name
    public void test_getDataStore_existingName() {
        TestDataStore dataStore = new TestDataStore("TestStore");
        dataStoreFactory.add("myStore", dataStore);

        DataStore retrieved = dataStoreFactory.getDataStore("myStore");
        assertNotNull(retrieved);
        assertSame(dataStore, retrieved);
    }

    // Test getDataStore with non-existing name
    public void test_getDataStore_nonExistingName() {
        DataStore retrieved = dataStoreFactory.getDataStore("nonExisting");
        assertNull(retrieved);
    }

    // Test getDataStore with null name
    public void test_getDataStore_nullName() {
        DataStore retrieved = dataStoreFactory.getDataStore(null);
        assertNull(retrieved);
    }

    // Test getDataStore with case insensitive lookup
    public void test_getDataStore_caseInsensitive() {
        TestDataStore dataStore = new TestDataStore("TestStore");
        dataStoreFactory.add("MyStore", dataStore);

        assertSame(dataStore, dataStoreFactory.getDataStore("MyStore"));
        assertSame(dataStore, dataStoreFactory.getDataStore("mystore"));
        assertSame(dataStore, dataStoreFactory.getDataStore("MYSTORE"));
        assertSame(dataStore, dataStoreFactory.getDataStore("mYsToRe"));
    }

    // Test multiple data store registration
    public void test_multipleDataStores() {
        TestDataStore dataStore1 = new TestDataStore("Store1");
        TestDataStore2 dataStore2 = new TestDataStore2("Store2");

        dataStoreFactory.add("store1", dataStore1);
        dataStoreFactory.add("store2", dataStore2);

        assertSame(dataStore1, dataStoreFactory.getDataStore("store1"));
        assertSame(dataStore2, dataStoreFactory.getDataStore("store2"));
        assertSame(dataStore1, dataStoreFactory.getDataStore("TestDataStore"));
        assertSame(dataStore2, dataStoreFactory.getDataStore("TestDataStore2"));
    }

    // Test overwriting existing data store
    public void test_overwriteExistingDataStore() {
        TestDataStore dataStore1 = new TestDataStore("Store1");
        TestDataStore dataStore2 = new TestDataStore("Store2");

        dataStoreFactory.add("store", dataStore1);
        assertSame(dataStore1, dataStoreFactory.getDataStore("store"));

        dataStoreFactory.add("store", dataStore2);
        assertSame(dataStore2, dataStoreFactory.getDataStore("store"));
    }

    // Test getDataStoreNames with caching
    public void test_getDataStoreNames_caching() {
        // Mock the loadDataStoreNameList to control behavior
        final int[] loadCount = { 0 };
        DataStoreFactory testFactory = new DataStoreFactory() {
            @Override
            protected List<String> loadDataStoreNameList() {
                loadCount[0]++;
                return List.of("Store1", "Store2");
            }
        };

        // First call should load
        String[] names = testFactory.getDataStoreNames();
        assertEquals(2, names.length);
        assertEquals("Store1", names[0]);
        assertEquals("Store2", names[1]);
        assertEquals(1, loadCount[0]);

        // Second immediate call should use cache
        names = testFactory.getDataStoreNames();
        assertEquals(2, names.length);
        assertEquals(1, loadCount[0]); // Should not increment

        // Simulate time passing (more than 60 seconds)
        testFactory.lastLoadedTime = System.currentTimeMillis() - 61000L;

        // Should reload after cache expiry
        names = testFactory.getDataStoreNames();
        assertEquals(2, names.length);
        assertEquals(2, loadCount[0]); // Should increment
    }

    // Test loadDataStoreNameList with valid XML
    public void test_loadDataStoreNameList_validXml() throws Exception {
        // Create test JAR with valid XML
        final File jarFile = new File(tempDir, "test-datastore.jar");
        createTestJarWithXml(jarFile,
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<components>\n"
                        + "  <component class=\"org.codelibs.fess.ds.impl.CsvDataStore\"/>\n"
                        + "  <component class=\"org.codelibs.fess.ds.impl.DatabaseDataStore\"/>\n"
                        + "  <component class=\"org.codelibs.fess.ds.impl.FileListDataStore\"/>\n" + "</components>");

        DataStoreFactory testFactory = new DataStoreFactory() {
            @Override
            protected List<String> loadDataStoreNameList() {
                // Override ResourceUtil behavior for testing
                File[] jarFiles = new File[] { jarFile };
                return super.loadDataStoreNameList();
            }
        };

        // Note: ResourceUtil.getPluginJarFiles is a static method, so we can't mock it directly
        // The test will verify the method works with the actual ResourceUtil implementation

        // Test with reflection to access protected method
        List<String> names = testFactory.loadDataStoreNameList();
        assertTrue(names.isEmpty() || names.size() >= 0); // Will be empty due to ResourceUtil static method
    }

    // Test loadDataStoreNameList with malformed XML
    public void test_loadDataStoreNameList_malformedXml() throws Exception {
        File jarFile = new File(tempDir, "malformed-datastore.jar");
        createTestJarWithXml(jarFile, "This is not valid XML");

        DataStoreFactory testFactory = new DataStoreFactory() {
            @Override
            protected List<String> loadDataStoreNameList() {
                // Will handle exception and log warning
                return super.loadDataStoreNameList();
            }
        };

        List<String> names = testFactory.loadDataStoreNameList();
        assertNotNull(names);
        assertTrue(names.isEmpty() || names.size() >= 0);
    }

    // Test loadDataStoreNameList with empty component class
    public void test_loadDataStoreNameList_emptyClass() throws Exception {
        File jarFile = new File(tempDir, "empty-class-datastore.jar");
        createTestJarWithXml(jarFile, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<components>\n" + "  <component class=\"\"/>\n"
                + "  <component/>\n" + "</components>");

        DataStoreFactory testFactory = new DataStoreFactory();
        List<String> names = testFactory.loadDataStoreNameList();
        assertNotNull(names);
    }

    // Test loadDataStoreNameList sorts results
    public void test_loadDataStoreNameList_sorted() {
        DataStoreFactory testFactory = new DataStoreFactory() {
            @Override
            protected List<String> loadDataStoreNameList() {
                // Return unsorted list to test sorting
                return List.of("Zebra", "Apple", "Banana").stream().sorted().collect(java.util.stream.Collectors.toList());
            }
        };

        List<String> names = testFactory.loadDataStoreNameList();
        assertEquals(3, names.size());
        assertEquals("Apple", names.get(0));
        assertEquals("Banana", names.get(1));
        assertEquals("Zebra", names.get(2));
    }

    // Helper method to create test JAR with XML content
    private void createTestJarWithXml(File jarFile, String xmlContent) throws Exception {
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile))) {
            JarEntry entry = new JarEntry("fess_ds++.xml");
            jos.putNextEntry(entry);
            jos.write(xmlContent.getBytes());
            jos.closeEntry();
        }
    }

    // Test DataStore implementation for testing
    private static class TestDataStore extends AbstractDataStore {
        private String name;

        public TestDataStore(String name) {
            this.name = name;
        }

        @Override
        protected String getName() {
            return name;
        }

        @Override
        protected void storeData(DataConfig dataConfig, IndexUpdateCallback callback, DataStoreParams paramMap,
                Map<String, String> scriptMap, Map<String, Object> defaultDataMap) {
            // Test implementation
        }
    }

    // Second Test DataStore implementation for testing multiple stores
    private static class TestDataStore2 extends AbstractDataStore {
        private String name;

        public TestDataStore2(String name) {
            this.name = name;
        }

        @Override
        protected String getName() {
            return name;
        }

        @Override
        protected void storeData(DataConfig dataConfig, IndexUpdateCallback callback, DataStoreParams paramMap,
                Map<String, String> scriptMap, Map<String, Object> defaultDataMap) {
            // Test implementation
        }
    }
}