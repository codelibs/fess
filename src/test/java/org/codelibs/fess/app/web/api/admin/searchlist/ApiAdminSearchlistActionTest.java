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
package org.codelibs.fess.app.web.api.admin.searchlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;

/**
 * Contract tests for the raw-document admin REST API.
 *
 * <p>{@code stripSystemManagedFields} is shared with the HTML screen, where dropping a
 * {@code content} update against an already-chunked document is correct (the screen renders that
 * field read-only, so the value can only arrive by a hand-crafted POST). The API has no such
 * rendering pass: a caller can legitimately send {@code content} and, before this fix, got
 * {@code status: 0} back while the field was silently discarded. These tests pin that the API
 * reports the condition instead of no-op'ing.</p>
 */
public class ApiAdminSearchlistActionTest extends UnitFessTestCase {

    // ===================================================================================
    //                                          put$doc: chunked content must not no-op
    //                                                                           =========

    @Test
    public void test_put$doc_contentUpdateAgainstChunkedEntity_isRejectedNotSilentlyDropped() throws Exception {
        final FakeSearchEngineClient client = new FakeSearchEngineClient();
        client.documentToReturn = chunkedEntity();
        final ApiAdminSearchlistAction action = createInjectedAction(client, buildFullFessConfig());

        final EditBody body = newEditBody();
        body.doc.put("content", "a legitimate API-supplied replacement body");

        assertValidationError(() -> action.put$doc(body)).handle(data -> {
            data.requiredMessageOf("_global", "errors.crud_failed_to_update_crud_table");
        });
        assertNull(client.lastStoredDoc, "the update must not be silently stored with the content dropped");
    }

    @Test
    public void test_put$doc_withoutContentKey_updatesChunkedEntityNormally() throws Exception {
        // The guard must fire only when the caller actually sent a content key -- an ordinary
        // metadata-only update of a chunked document must keep working.
        final FakeSearchEngineClient client = new FakeSearchEngineClient();
        client.documentToReturn = chunkedEntity();
        final ApiAdminSearchlistAction action = createInjectedAction(client, buildFullFessConfig());

        final EditBody body = newEditBody();
        body.doc.put("title", "A new title");

        action.put$doc(body);

        assertNotNull(client.lastStoredDoc, "put$doc must have reached store()");
        assertEquals("the stored chunk array must be untouched", List.of("chunk-a", "chunk-b"), client.lastStoredDoc.get("content"));
        assertEquals("A new title", client.lastStoredDoc.get("title"));
    }

    @Test
    public void test_put$doc_contentUpdateAgainstUnchunkedEntity_stillSucceeds() throws Exception {
        // A plain-string (possibly very large) stored content stays editable through the API; only
        // the chunk-array case is refused.
        final FakeSearchEngineClient client = new FakeSearchEngineClient();
        final Map<String, Object> entity = chunkedEntity();
        entity.put("content", "a plain stored string");
        entity.remove(Constants.CONTENT_CHUNK_STATUS_FIELD);
        entity.remove(Constants.CONTENT_CHUNK_VECTOR_FIELD);
        client.documentToReturn = entity;
        final ApiAdminSearchlistAction action = createInjectedAction(client, buildFullFessConfig());

        final EditBody body = newEditBody();
        body.doc.put("content", "an API-supplied replacement body");

        action.put$doc(body);

        assertNotNull(client.lastStoredDoc, "put$doc must have reached store()");
        assertEquals("an API-supplied replacement body", client.lastStoredDoc.get("content"));
    }

    @Test
    public void test_put$doc_systemManagedFieldsAreStillStripped() throws Exception {
        // The strip of content_chunk_vector/content_chunk_status is unconditional and unchanged;
        // it stays silent because those keys are never a legitimate client-supplied update.
        final FakeSearchEngineClient client = new FakeSearchEngineClient();
        client.documentToReturn = chunkedEntity();
        final ApiAdminSearchlistAction action = createInjectedAction(client, buildFullFessConfig());

        final EditBody body = newEditBody();
        body.doc.put(Constants.CONTENT_CHUNK_STATUS_FIELD, "fail");
        body.doc.put(Constants.CONTENT_CHUNK_VECTOR_FIELD, new ArrayList<>());

        action.put$doc(body);

        assertNotNull(client.lastStoredDoc, "put$doc must have reached store()");
        assertEquals(Constants.DONE, client.lastStoredDoc.get(Constants.CONTENT_CHUNK_STATUS_FIELD));
        @SuppressWarnings("unchecked")
        final List<Object> storedVector = (List<Object>) client.lastStoredDoc.get(Constants.CONTENT_CHUNK_VECTOR_FIELD);
        assertFalse(storedVector.isEmpty(), "the client's smuggled empty vector list must be dropped");
    }

    // ===================================================================================
    //                                                              post$doc regression
    //                                                                           =========

    @Test
    public void test_post$doc_freshEntityKeepsClientContent() throws Exception {
        // getDoc() builds a brand-new empty entity for CREATE, so its content is never a List and
        // the guard must never fire on the create path.
        final FakeSearchEngineClient client = new FakeSearchEngineClient();
        final ApiAdminSearchlistAction action = createInjectedAction(client, buildFullFessConfig());

        final CreateBody body = new CreateBody();
        body.doc = new HashMap<>();
        body.doc.put("url", "https://example.com/new");
        body.doc.put("title", "Title");
        body.doc.put("role", "Rguest");
        body.doc.put("boost", "1.0");
        body.doc.put("content", "a brand-new document's own content");

        action.post$doc(body);

        assertNotNull(client.lastStoredDoc, "post$doc must have reached store()");
        assertEquals("a brand-new document's own content", client.lastStoredDoc.get("content"));
    }

    // ===================================================================================
    //                                                                            Helpers
    //                                                                           =========

    private static Map<String, Object> chunkedEntity() {
        final Map<String, Object> entity = new HashMap<>();
        entity.put("doc_id", "chunked-doc-1");
        entity.put("_id", "chunked-doc-1-original-id");
        entity.put("url", "https://example.com/chunked");
        entity.put("content", new ArrayList<Object>(List.of("chunk-a", "chunk-b")));
        entity.put(Constants.CONTENT_CHUNK_STATUS_FIELD, Constants.DONE);
        entity.put(Constants.CONTENT_CHUNK_VECTOR_FIELD, List.of(Map.of("vector", List.of(0.1, 0.2))));
        entity.put("_seq_no", 5L);
        entity.put("_primary_term", 1L);
        return entity;
    }

    private static EditBody newEditBody() {
        final EditBody body = new EditBody();
        body.doc = new HashMap<>();
        body.doc.put("doc_id", "chunked-doc-1");
        body.doc.put("url", "https://example.com/chunked");
        body.doc.put("title", "Title");
        body.doc.put("role", "Rguest");
        body.doc.put("boost", "1.0");
        return body;
    }

    /**
     * Mirrors {@code AdminSearchlistActionTest#createInjectedAction}: UTFlute {@code inject()} for
     * the framework fields that {@code validateApi()}/{@code throwValidationErrorApi()}/
     * {@code saveInfo()} need, then the fess-specific collaborators that {@code fess.xml} (not
     * loaded by the unit-test container) would otherwise supply -- {@code systemHelper}, a single
     * {@code fessConfig} instance (set both on the action field and via
     * {@link ComponentUtil#setFessConfig}, since {@code getDoc()}/{@code validateFields()} read it
     * statically) and the fake {@code searchEngineClient} (likewise resolved both ways).
     */
    private ApiAdminSearchlistAction createInjectedAction(final FakeSearchEngineClient client, final FessConfig testConfig)
            throws Exception {
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        // FessApiAction declares @Resource AccessTokenService, whose own AccessTokenBhv @Resource
        // cannot be assembled in the unit container; only isAccessAllowed() (never reached when the
        // execute method is called directly) uses it.
        suppressBindingOf(org.codelibs.fess.app.service.AccessTokenService.class);
        final ApiAdminSearchlistAction action = new ApiAdminSearchlistAction();
        inject(action);

        final org.codelibs.fess.helper.SystemHelper systemHelperInstance = new org.codelibs.fess.helper.SystemHelper();
        final java.lang.reflect.Field sysField = org.codelibs.fess.app.web.base.FessBaseAction.class.getDeclaredField("systemHelper");
        sysField.setAccessible(true);
        if (sysField.get(action) == null) {
            sysField.set(action, systemHelperInstance);
        }
        ComponentUtil.register(systemHelperInstance, "systemHelper");

        final java.lang.reflect.Field fessConfigField = org.codelibs.fess.app.web.base.FessBaseAction.class.getDeclaredField("fessConfig");
        fessConfigField.setAccessible(true);
        fessConfigField.set(action, testConfig);
        ComponentUtil.setFessConfig(testConfig);

        final java.lang.reflect.Field clientField = ApiAdminSearchlistAction.class.getDeclaredField("searchEngineClient");
        clientField.setAccessible(true);
        clientField.set(action, client);
        ComponentUtil.register(client, "searchEngineClient");

        ComponentUtil.register(new org.codelibs.fess.helper.CrawlingInfoHelper(), "crawlingInfoHelper");

        return action;
    }

    /**
     * A {@link FessConfig.SimpleImpl} with every getter the exercised paths (post$doc/put$doc,
     * getDoc(), validateFields(), convertToStorableDoc(), generateId()) touch explicitly
     * overridden -- an unoverridden {@code SimpleImpl} getter NPEs on its null backing properties.
     */
    private FessConfig buildFullFessConfig() {
        return new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexAdminRequiredFields() {
                return "url,title,role,boost";
            }

            @Override
            public String getIndexAdminArrayFields() {
                return "lang,role,label,anchor,virtual_host";
            }

            @Override
            public String getIndexAdminDateFields() {
                return "expires,created,timestamp,last_modified";
            }

            @Override
            public String getIndexAdminIntegerFields() {
                return "";
            }

            @Override
            public String getIndexAdminLongFields() {
                return "content_length,favorite_count,click_count";
            }

            @Override
            public String getIndexAdminFloatFields() {
                return "boost";
            }

            @Override
            public String getIndexAdminDoubleFields() {
                return "";
            }

            @Override
            public String getIndexFieldId() {
                return "_id";
            }

            @Override
            public String getIndexFieldVersion() {
                return "_version";
            }

            @Override
            public String getIndexFieldSeqNo() {
                return "_seq_no";
            }

            @Override
            public String getIndexFieldPrimaryTerm() {
                return "_primary_term";
            }

            @Override
            public String getIndexFieldDocId() {
                return "doc_id";
            }

            @Override
            public String getIndexFieldUrl() {
                return "url";
            }

            @Override
            public String getIndexFieldRole() {
                return "role";
            }

            @Override
            public String getIndexFieldVirtualHost() {
                return "virtual_host";
            }

            @Override
            public String getIndexDocumentUpdateIndex() {
                return "fess.update";
            }

            @Override
            public String getIndexIdDigestAlgorithm() {
                return "SHA-512";
            }
        };
    }

    /** A capturing {@link SearchEngineClient} fake for driving post$doc/put$doc end to end. */
    private static final class FakeSearchEngineClient extends SearchEngineClient {
        Map<String, Object> documentToReturn;
        Map<String, Object> lastStoredDoc;

        @Override
        public OptionalEntity<Map<String, Object>> getDocument(final String index,
                final org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition<org.opensearch.action.search.SearchRequestBuilder> condition) {
            return documentToReturn == null ? OptionalEntity.empty() : OptionalEntity.of(new HashMap<>(documentToReturn));
        }

        @Override
        public boolean store(final String index, final Object obj) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> doc = (Map<String, Object>) obj;
            lastStoredDoc = new HashMap<>(doc);
            return true;
        }

        @Override
        public boolean delete(final String index, final String id, final Number seqNo, final Number primaryTerm) {
            return true;
        }
    }
}
