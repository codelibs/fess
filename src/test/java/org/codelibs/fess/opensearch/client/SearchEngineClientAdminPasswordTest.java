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
package org.codelibs.fess.opensearch.client;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.lucene.search.TotalHits;
import org.codelibs.fesen.opensearch.action.ActionRequest;
import org.codelibs.fesen.opensearch.action.ActionType;
import org.codelibs.fesen.opensearch.action.admin.indices.refresh.RefreshAction;
import org.codelibs.fesen.opensearch.action.admin.indices.refresh.RefreshRequest;
import org.codelibs.fesen.opensearch.action.admin.indices.refresh.RefreshRequestBuilder;
import org.codelibs.fesen.opensearch.action.search.SearchAction;
import org.codelibs.fesen.opensearch.action.search.SearchRequest;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.action.search.SearchResponse;
import org.codelibs.fesen.opensearch.action.search.ShardSearchFailure;
import org.codelibs.fesen.opensearch.action.support.PlainActionFuture;
import org.codelibs.fesen.opensearch.common.action.ActionFuture;
import org.codelibs.fesen.opensearch.core.action.ActionResponse;
import org.codelibs.fesen.opensearch.core.common.bytes.BytesArray;
import org.codelibs.fesen.opensearch.index.query.TermQueryBuilder;
import org.codelibs.fesen.opensearch.search.SearchHit;
import org.codelibs.fesen.opensearch.search.SearchHits;
import org.codelibs.fesen.opensearch.search.internal.InternalSearchResponse;
import org.codelibs.fesen.opensearch.transport.client.AdminClient;
import org.codelibs.fesen.opensearch.transport.client.ClusterAdminClient;
import org.codelibs.fesen.opensearch.transport.client.IndicesAdminClient;
import org.codelibs.fess.crypto.bcrypt.BCrypt;
import org.codelibs.fess.helper.PasswordHashHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Tests the start-up warning about an {@code admin} user whose stored password is one that
 * {@code password.invalid.admin.passwords} lists.
 *
 * <p>The stored hash comes from an override of the reader, so the tests need no cluster. The log is
 * read on the test's own thread, because other classes log through the same logger at the same
 * time.</p>
 */
public class SearchEngineClientAdminPasswordTest extends UnitFessTestCase {

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new PasswordHashHelper(), "passwordHashHelper");
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    /** A client that acts as the webapp or a job process and reads the admin user's hash from a field. */
    private static class StoredPasswordClient extends SearchEngineClient {
        private final boolean webapp;

        private final String stored;

        private final RuntimeException failure;

        final List<String> readNames = new ArrayList<>();

        StoredPasswordClient(final boolean webapp, final String stored, final RuntimeException failure) {
            this.webapp = webapp;
            this.stored = stored;
            this.failure = failure;
        }

        @Override
        protected boolean isWebappProcess() {
            return webapp;
        }

        @Override
        protected String getStoredUserPassword(final FessConfig fessConfig, final String name) {
            readNames.add(name);
            if (failure != null) {
                throw failure;
            }
            return stored;
        }
    }

    private static FessConfig listing(final String passwords) {
        return new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getPasswordInvalidAdminPasswords() {
                return passwords;
            }

            @Override
            public String getAppDigestAlgorithm() {
                return "sha256";
            }
        };
    }

    private static String bcrypt(final String password) {
        // the lowest cost keeps the test fast; the cost is read from the hash when it is verified
        return "{bcrypt}" + BCrypt.hashpw(password, BCrypt.gensalt(4));
    }

    private List<String> warnings(final StoredPasswordClient client, final FessConfig fessConfig, final boolean expected) {
        // the hash helper reads app.digest.algorithm for a legacy hash from the registered configuration
        ComponentUtil.setFessConfig(fessConfig);
        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            assertEquals(expected, client.reportListedAdminPassword(fessConfig));
            return capture.messagesOnThisThreadAt(Level.WARN);
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_warnsWhenTheStoredPasswordIsListed() {
        final StoredPasswordClient client = new StoredPasswordClient(true, bcrypt("Listed-Secret1"), null);

        final List<String> warnings = warnings(client, listing("admin\n  Listed-Secret1  \n"), true);

        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0),
                warnings.get(0).contains("The admin user still has a password listed in password.invalid.admin.passwords"));
        assertTrue(warnings.get(0), warnings.get(0).contains("Sign in as admin and set a new password when asked"));
        assertTrue(warnings.get(0), warnings.get(0).contains("from another admin account on the Users page"));
        assertTrue(warnings.get(0), warnings.get(0).contains("index.user.initial_password"));
        assertFalse(warnings.get(0), warnings.get(0).contains("Listed-Secret1"));
        assertEquals(List.of("admin"), client.readNames);
    }

    @Test
    public void test_warnsForTheShippedDefault() {
        final StoredPasswordClient client = new StoredPasswordClient(true, bcrypt("admin"), null);

        assertEquals(1, warnings(client, listing("admin"), true).size());
    }

    @Test
    public void test_warnsForALegacyDigest() {
        // sha256("admin"), as a pre-BCrypt release stored it (app.digest.algorithm=sha256)
        final StoredPasswordClient client =
                new StoredPasswordClient(true, "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918", null);

        assertEquals(1, warnings(client, listing("admin"), true).size());
    }

    @Test
    public void test_silentWhenThePasswordWasChanged() {
        final StoredPasswordClient client = new StoredPasswordClient(true, bcrypt("A-new-password-9"), null);

        assertEquals(List.of(), warnings(client, listing("admin\nchangeme"), false));
        assertEquals(List.of("admin"), client.readNames);
    }

    @Test
    public void test_silentWithoutAnAdminUser() {
        assertEquals(List.of(), warnings(new StoredPasswordClient(true, null, null), listing("admin"), false));
        assertEquals(List.of(), warnings(new StoredPasswordClient(true, "", null), listing("admin"), false));
    }

    @Test
    public void test_skippedWhenTheListIsEmpty() {
        for (final String passwords : new String[] { "", "  \n \n", null }) {
            final StoredPasswordClient client = new StoredPasswordClient(true, bcrypt("admin"), null);

            assertEquals(List.of(), warnings(client, listing(passwords), false));
            assertEquals("the user must not be read: " + passwords, List.of(), client.readNames);
        }
    }

    @Test
    public void test_skippedInJobProcesses() {
        final StoredPasswordClient client = new StoredPasswordClient(false, bcrypt("admin"), null);

        assertEquals(List.of(), warnings(client, listing("admin"), false));
        assertEquals(List.of(), client.readNames);
    }

    /**
     * Plays the engine for {@link SearchEngineClient#getStoredUserPassword}: it records the refresh and
     * the search in the order they arrive and answers the search with the given sources.
     */
    private static class UserIndexAnswers extends SearchEngineClient {
        final List<String> calls = new ArrayList<>();

        private final String[] sources;

        UserIndexAnswers(final String... sources) {
            this.sources = sources;
        }

        private final IndicesAdminClient indices = (IndicesAdminClient) Proxy.newProxyInstance(IndicesAdminClient.class.getClassLoader(),
                new Class<?>[] { IndicesAdminClient.class }, (proxy, method, args) -> {
                    if ("prepareRefresh".equals(method.getName())) {
                        return new RefreshRequestBuilder(this, RefreshAction.INSTANCE).setIndices((String[]) args[0]);
                    }
                    throw new UnsupportedOperationException(method.getName());
                });

        @Override
        public AdminClient admin() {
            return new AdminClient() {
                @Override
                public ClusterAdminClient cluster() {
                    throw new UnsupportedOperationException("cluster");
                }

                @Override
                public IndicesAdminClient indices() {
                    return indices;
                }
            };
        }

        @Override
        public SearchRequestBuilder prepareSearch(final String... indices) {
            return new SearchRequestBuilder(this, SearchAction.INSTANCE).setIndices(indices);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <Request extends ActionRequest, Response extends ActionResponse> ActionFuture<Response> execute(
                final ActionType<Response> action, final Request request) {
            final PlainActionFuture<Response> future = PlainActionFuture.newFuture();
            if (RefreshAction.INSTANCE.equals(action)) {
                calls.add("refresh:" + String.join(",", ((RefreshRequest) request).indices()));
                future.onResponse(null);
            } else if (SearchAction.INSTANCE.equals(action)) {
                final SearchRequest search = (SearchRequest) request;
                final TermQueryBuilder query = (TermQueryBuilder) search.source().query();
                calls.add("search:" + String.join(",", search.indices()) + ":" + query.fieldName() + "=" + query.value());
                final SearchHit[] hits = new SearchHit[sources.length];
                for (int i = 0; i < sources.length; i++) {
                    hits[i] = new SearchHit(i, "id" + i, null, null);
                    hits[i].sourceRef(new BytesArray(sources[i]));
                }
                final SearchHits searchHits = new SearchHits(hits, new TotalHits(hits.length, TotalHits.Relation.EQUAL_TO), 1.0f);
                future.onResponse((Response) new SearchResponse(new InternalSearchResponse(searchHits, null, null, null, false, null, 1),
                        null, 1, 1, 0, 1L, ShardSearchFailure.EMPTY_ARRAY, SearchResponse.Clusters.EMPTY));
            } else {
                throw new UnsupportedOperationException(action.name());
            }
            return future;
        }
    }

    private static FessConfig userIndexConfig() {
        return new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexUserIndex() {
                return "fess_user";
            }

            @Override
            public String getIndexIndicesTimeout() {
                return "1m";
            }

            @Override
            public String getIndexSearchTimeout() {
                return "1m";
            }
        };
    }

    /** The bulk load of a first start does not refresh, so the search would miss the admin user without the refresh before it. */
    @Test
    public void test_getStoredUserPassword_refreshesBeforeItSearchesByName() {
        final UserIndexAnswers engine = new UserIndexAnswers("{\"password\":\"{bcrypt}$2a$04$hash\"}");
        final SearchEngineClient client = new SearchEngineClient();
        client.client = engine;

        assertEquals("{bcrypt}$2a$04$hash", client.getStoredUserPassword(userIndexConfig(), "admin"));
        assertEquals(List.of("refresh:fess_user.user", "search:fess_user.user:name=admin"), engine.calls);
    }

    @Test
    public void test_getStoredUserPassword_noUserIsNull() {
        final SearchEngineClient client = new SearchEngineClient();
        client.client = new UserIndexAnswers();
        assertNull(client.getStoredUserPassword(userIndexConfig(), "admin"));

        client.client = new UserIndexAnswers("{\"name\":\"admin\"}");
        assertNull(client.getStoredUserPassword(userIndexConfig(), "admin"));
    }

    @Test
    public void test_aReadFailureDoesNotStopTheStart() {
        final StoredPasswordClient client = new StoredPasswordClient(true, null, new IllegalStateException("index_not_found"));

        final List<String> warnings = warnings(client, listing("admin"), false);

        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0), warnings.get(0).contains("Could not check the password of the admin user"));
    }
}
