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
package org.codelibs.fess.app.web.api.admin.documents;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.LanguageHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.opensearch.action.bulk.BulkItemResponse;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.index.IndexRequestBuilder;

public class ApiAdminDocumentsActionTest extends UnitFessTestCase {

    // ===================================================================================
    //                                        put$bulk system-managed field stripping (Fix 2)
    //                                        ================================================

    @Test
    public void test_put$bulk_stripsSystemManagedFieldsBeforeIndexing() throws Exception {
        final FessConfig testConfig = buildFullFessConfig();
        final CapturingSearchEngineClient client = new CapturingSearchEngineClient();
        final ApiAdminDocumentsAction action = createInjectedAction(client, testConfig);

        final Map<String, Object> doc = new HashMap<>();
        doc.put("url", "https://example.com/doc");
        doc.put("title", "Title");
        doc.put("role", "Rguest");
        doc.put("boost", "1.0");
        doc.put("content", "legitimate client content");
        // A bulk PUT attempting to smuggle system-managed chunk fields straight into the index --
        // exactly what only the ChunkVectorHelper CAS pipeline may ever write. Non-empty values are
        // used deliberately: convertToStorableDoc() drops empty values on its own, so an empty
        // smuggled value would be removed even without the fix, making the test vacuous.
        doc.put(Constants.CONTENT_CHUNK_STATUS_FIELD, "fail");
        doc.put(Constants.CONTENT_CHUNK_VECTOR_FIELD, List.of(Map.of("vector", List.of(0.1, 0.2))));

        final BulkBody body = new BulkBody();
        body.documents = new ArrayList<>();
        body.documents.add(doc);

        action.put$bulk(body);

        org.junit.jupiter.api.Assertions.assertEquals(1, client.capturedDocs.size(),
                "the single document must reach searchEngineClient.addAll");
        final Map<String, Object> indexed = client.capturedDocs.get(0);
        assertFalse(indexed.containsKey(Constants.CONTENT_CHUNK_STATUS_FIELD),
                "content_chunk_status must be stripped before the document reaches addAll: " + indexed);
        assertFalse(indexed.containsKey(Constants.CONTENT_CHUNK_VECTOR_FIELD),
                "content_chunk_vector must be stripped before the document reaches addAll: " + indexed);
        // The strip must be surgical: the client's own legitimate fields must still be indexed.
        org.junit.jupiter.api.Assertions.assertEquals("legitimate client content", indexed.get("content"),
                "the client's legitimate content must survive the strip");
        org.junit.jupiter.api.Assertions.assertEquals("Title", indexed.get("title"),
                "the client's legitimate title must survive the strip");
    }

    // ===================================================================================
    //                                                                           Helpers
    //                                                                           ========

    /**
     * Wires an {@link ApiAdminDocumentsAction} through UTFlute's {@code inject()} (framework fields
     * needed by {@code validateApi()}/{@code asJson()}), then directly wires the fess-specific
     * collaborators that {@code fess.xml} (not loaded by the unit test container) would otherwise
     * provide: {@code systemHelper}, a single consistent {@code fessConfig} instance (used both via
     * this reflection-set field AND {@link ComponentUtil#setFessConfig}, since {@code validateFields()}/
     * {@code convertToStorableDoc()} read the latter statically), the capturing
     * {@code searchEngineClient}, and the {@code crawlingInfoHelper}/{@code languageHelper}/
     * {@code crawlingConfigHelper} that {@code put$bulk} resolves statically. Mirrors
     * {@code AdminSearchlistActionTest#createInjectedAction}.
     */
    private ApiAdminDocumentsAction createInjectedAction(final SearchEngineClient client, final FessConfig testConfig) throws Exception {
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        // The API action declares an @Resource AccessTokenService whose own AccessTokenBhv field
        // cannot bind without a live datastore; put$bulk never uses it, so suppress its binding.
        suppressBindingOf(org.codelibs.fess.app.service.AccessTokenService.class);
        final ApiAdminDocumentsAction action = new ApiAdminDocumentsAction();
        inject(action);

        final SystemHelper systemHelperInstance = new SystemHelper();
        final Field sysField = FessBaseAction.class.getDeclaredField("systemHelper");
        sysField.setAccessible(true);
        if (sysField.get(action) == null) {
            sysField.set(action, systemHelperInstance);
        }
        ComponentUtil.register(systemHelperInstance, "systemHelper");

        final Field fessConfigField = FessBaseAction.class.getDeclaredField("fessConfig");
        fessConfigField.setAccessible(true);
        fessConfigField.set(action, testConfig);
        ComponentUtil.setFessConfig(testConfig);

        final Field clientField = ApiAdminDocumentsAction.class.getDeclaredField("searchEngineClient");
        clientField.setAccessible(true);
        clientField.set(action, client);
        ComponentUtil.register(client, "searchEngineClient");

        // put$bulk resolves these statically (ComponentUtil.getXxx). generateId()/generateDocId()
        // need only FessConfig; languageHelper.updateDocument() and crawlingConfigHelper.getPipeline()
        // are not reached by this test's payload (no lang field, no config_id, and the capturing
        // addAll never invokes the per-document options callback), so plain instances suffice.
        ComponentUtil.register(new CrawlingInfoHelper(), "crawlingInfoHelper");
        ComponentUtil.register(new LanguageHelper(), "languageHelper");
        ComponentUtil.register(new CrawlingConfigHelper(), "crawlingConfigHelper");

        return action;
    }

    /**
     * A capturing {@link SearchEngineClient} fake that records the document maps handed to
     * {@link #addAll} (defensively copied, since the real implementation mutates them) instead of
     * indexing them, and returns an empty {@link BulkResponse} so {@code put$bulk} can build its
     * summary response without a live cluster.
     */
    private static final class CapturingSearchEngineClient extends SearchEngineClient {
        final List<Map<String, Object>> capturedDocs = new ArrayList<>();

        @Override
        public BulkResponse addAll(final String index, final List<Map<String, Object>> docList,
                final BiConsumer<Map<String, Object>, IndexRequestBuilder> options) {
            for (final Map<String, Object> doc : docList) {
                capturedDocs.add(new HashMap<>(doc));
            }
            return new BulkResponse(new BulkItemResponse[0], 0L);
        }
    }

    /**
     * A {@link FessConfig.SimpleImpl} with every getter {@code put$bulk} touches
     * (validateFields()/convertToStorableDoc()/generateId()/generateDocId() plus the per-field
     * default-value assignments) explicitly overridden -- an unoverridden {@code SimpleImpl} getter
     * NPEs (it reads a {@code null} backing {@code Properties} object). Mirrors
     * {@code AdminSearchlistActionTest#buildFullFessConfig}, extended with the additional field
     * getters {@code put$bulk} reads.
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
            public String getIndexFieldDocId() {
                return "doc_id";
            }

            @Override
            public String getIndexFieldContentLength() {
                return "content_length";
            }

            @Override
            public String getIndexFieldTitle() {
                return "title";
            }

            @Override
            public String getIndexFieldContent() {
                return "content";
            }

            @Override
            public String getIndexFieldFavoriteCount() {
                return "favorite_count";
            }

            @Override
            public String getIndexFieldClickCount() {
                return "click_count";
            }

            @Override
            public String getIndexFieldBoost() {
                return "boost";
            }

            @Override
            public String getIndexFieldRole() {
                return "role";
            }

            @Override
            public String getIndexFieldLastModified() {
                return "last_modified";
            }

            @Override
            public String getIndexFieldTimestamp() {
                return "timestamp";
            }

            @Override
            public String getIndexFieldLang() {
                return "lang";
            }

            @Override
            public String getIndexFieldUrl() {
                return "url";
            }

            @Override
            public String getIndexFieldVirtualHost() {
                return "virtual_host";
            }

            @Override
            public List<String> getSearchGuestRoleList() {
                return List.of("Rguest");
            }

            @Override
            public boolean isThumbnailCrawlerEnabled() {
                return false;
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
}
