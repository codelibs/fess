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
package org.codelibs.fess.it.admin;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.it.CrudTestBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

/**
 * Integration Tests for /api/admin/documents/bulk
 */
@Tag("it")
public class DocumentsTests extends CrudTestBase {
    private static final Logger logger = LogManager.getLogger(DocumentsTests.class);

    private static final String NAME_PREFIX = "documentsTest_";
    private static final String API_PATH = "/api/admin/documents";
    private static final String BULK_ENDPOINT = "bulk";
    private static final String SEARCHLIST_API_PATH = "/api/admin/searchlist";

    private static final String KEY_PROPERTY = "title";

    @BeforeAll
    protected static void initAll() {
        RestAssured.baseURI = getFessUrl();
        settingTestToken();
    }

    @AfterAll
    protected static void tearDownAll() {
        // Clean up test documents
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", 1000);
        searchBody.put("q", NAME_PREFIX + "*");

        String response = checkMethodBase(searchBody).get(SEARCHLIST_API_PATH + "/docs").asString();
        List<Map<String, Object>> docs = JsonPath.from(response).getList("response.docs");

        for (Map<String, Object> doc : docs) {
            String docId = doc.get("doc_id").toString();
            deleteMethod(SEARCHLIST_API_PATH + "/doc/" + docId);
        }
        refresh();

        deleteTestToken();
    }

    @Override
    protected String getNamePrefix() {
        return NAME_PREFIX;
    }

    @Override
    protected String getApiPath() {
        return API_PATH;
    }

    @Override
    protected String getKeyProperty() {
        return KEY_PROPERTY;
    }

    @Override
    protected String getListEndpointSuffix() {
        return "";
    }

    @Override
    protected String getItemEndpointSuffix() {
        return "";
    }

    @Override
    protected Map<String, Object> createTestParam(int id) {
        // Not used for this test
        return new HashMap<>();
    }

    @Override
    protected Map<String, Object> getUpdateMap() {
        // Not used for this test
        return new HashMap<>();
    }

    @Override
    protected void tearDown() {
        // Custom teardown in tearDownAll
    }

    @Test
    void bulkOperationsTest() {
        testBulkCreate();
        testBulkUpdate();
    }

    /**
     * Test: Bulk create documents
     */
    private void testBulkCreate() {
        logger.info("[BEGIN] testBulkCreate");

        // Create multiple documents in bulk
        final Map<String, Object> requestBody = new HashMap<>();
        final List<Map<String, Object>> documents = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            final Map<String, Object> doc = new HashMap<>();
            doc.put("title", NAME_PREFIX + i);
            doc.put("url", "http://example.com/bulk/" + i);
            doc.put("content", "This is test content for bulk document " + i);
            doc.put("boost", 1.0f);
            doc.put("role", "Rguest");
            documents.add(doc);
        }

        requestBody.put("documents", documents);

        // Execute bulk create
        checkMethodBase(requestBody).put(API_PATH + "/" + BULK_ENDPOINT).then()
                .body("response.status", equalTo(0));
        refresh();

        // Verify documents were created
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", 100);
        searchBody.put("q", NAME_PREFIX + "*");

        String response = checkMethodBase(searchBody).get(SEARCHLIST_API_PATH + "/docs").asString();
        List<Map<String, Object>> docs = JsonPath.from(response).getList("response.docs");

        logger.info("Created documents: {}", docs);
        assertEquals(5, docs.size(), "Should have created 5 documents");

        // Verify document content
        for (int i = 0; i < 5; i++) {
            final String expectedTitle = NAME_PREFIX + i;
            boolean found = docs.stream().anyMatch(doc -> expectedTitle.equals(doc.get("title")));
            assertTrue(found, "Document with title " + expectedTitle + " should exist");
        }

        logger.info("[END] testBulkCreate");
    }

    /**
     * Test: Bulk update documents
     */
    private void testBulkUpdate() {
        logger.info("[BEGIN] testBulkUpdate");

        // Get existing documents
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", 100);
        searchBody.put("q", NAME_PREFIX + "*");

        String response = checkMethodBase(searchBody).get(SEARCHLIST_API_PATH + "/docs").asString();
        List<Map<String, Object>> existingDocs = JsonPath.from(response).getList("response.docs");

        if (existingDocs.isEmpty()) {
            logger.warn("No documents found for bulk update test");
            return;
        }

        // Update documents in bulk
        final Map<String, Object> requestBody = new HashMap<>();
        final List<Map<String, Object>> documents = new ArrayList<>();

        for (Map<String, Object> existingDoc : existingDocs) {
            final Map<String, Object> doc = new HashMap<>();
            doc.put("doc_id", existingDoc.get("doc_id"));
            doc.put("title", existingDoc.get("title") + "_updated");
            doc.put("url", existingDoc.get("url_link"));
            doc.put("content", "Updated content");
            doc.put("boost", 2.0f);
            doc.put("role", "Rguest");
            documents.add(doc);
        }

        requestBody.put("documents", documents);

        // Execute bulk update
        checkMethodBase(requestBody).put(API_PATH + "/" + BULK_ENDPOINT).then()
                .body("response.status", equalTo(0));
        refresh();

        // Verify documents were updated
        response = checkMethodBase(searchBody).get(SEARCHLIST_API_PATH + "/docs").asString();
        List<Map<String, Object>> updatedDocs = JsonPath.from(response).getList("response.docs");

        logger.info("Updated documents: {}", updatedDocs);

        // Verify updated content
        for (Map<String, Object> doc : updatedDocs) {
            String title = doc.get("title").toString();
            assertTrue(title.endsWith("_updated"), "Document title should be updated");
        }

        logger.info("[END] testBulkUpdate");
    }

    @Test
    void bulkCreateEmptyListTest() {
        logger.info("[BEGIN] bulkCreateEmptyListTest");

        // Test with empty document list
        final Map<String, Object> requestBody = new HashMap<>();
        final List<Map<String, Object>> documents = new ArrayList<>();
        requestBody.put("documents", documents);

        // Execute bulk create with empty list
        checkMethodBase(requestBody).put(API_PATH + "/" + BULK_ENDPOINT).then()
                .body("response.status", equalTo(0));

        logger.info("[END] bulkCreateEmptyListTest");
    }

    @Test
    void bulkCreateLargeSetTest() {
        logger.info("[BEGIN] bulkCreateLargeSetTest");

        // Create a larger set of documents
        final Map<String, Object> requestBody = new HashMap<>();
        final List<Map<String, Object>> documents = new ArrayList<>();

        for (int i = 100; i < 120; i++) {
            final Map<String, Object> doc = new HashMap<>();
            doc.put("title", NAME_PREFIX + "large_" + i);
            doc.put("url", "http://example.com/bulk/large/" + i);
            doc.put("content", "This is test content for large bulk document " + i);
            doc.put("boost", 1.0f);
            doc.put("role", "Rguest");
            documents.add(doc);
        }

        requestBody.put("documents", documents);

        // Execute bulk create
        checkMethodBase(requestBody).put(API_PATH + "/" + BULK_ENDPOINT).then()
                .body("response.status", equalTo(0));
        refresh();

        // Verify documents were created
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", 100);
        searchBody.put("q", NAME_PREFIX + "large_*");

        String response = checkMethodBase(searchBody).get(SEARCHLIST_API_PATH + "/docs").asString();
        List<Map<String, Object>> docs = JsonPath.from(response).getList("response.docs");

        logger.info("Created large set of documents: {} documents", docs.size());
        assertEquals(20, docs.size(), "Should have created 20 documents");

        // Clean up large set
        for (Map<String, Object> doc : docs) {
            String docId = doc.get("doc_id").toString();
            deleteMethod(SEARCHLIST_API_PATH + "/doc/" + docId);
        }
        refresh();

        logger.info("[END] bulkCreateLargeSetTest");
    }
}
