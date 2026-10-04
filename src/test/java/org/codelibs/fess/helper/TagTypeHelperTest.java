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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.core.misc.Pair;
import org.codelibs.fesen.opensearch.action.search.SearchAction;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.search.builder.SearchSourceBuilder;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.cbean.TagTypeCB;
import org.codelibs.fess.opensearch.config.exbhv.TagTypeBhv;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class TagTypeHelperTest extends UnitFessTestCase {

    private TagTypeHelper tagTypeHelper;

    private MockTagTypeBhv tagTypeBhv;

    /** The logged-in user, or null for an anonymous caller. */
    private FessUserBean userBean;

    /** The roles the role query helper returns for the caller. */
    private Set<String> roleSet;

    private String virtualHostKey;

    private boolean userTagEnabled;

    private List<String> guestRoleList;

    private int visibleMaxSize;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        userBean = null;
        roleSet = new LinkedHashSet<>();
        virtualHostKey = "";
        userTagEnabled = true;
        guestRoleList = new ArrayList<>(List.of("Rguest", "1guest"));
        visibleMaxSize = 1000;
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isUserTagEnabled() {
                return userTagEnabled;
            }

            @Override
            public Integer getUserTagNameMaxLengthAsInteger() {
                return 50;
            }

            @Override
            public Integer getUserTagVisibleMaxSizeAsInteger() {
                return visibleMaxSize;
            }

            @Override
            public Integer getPageTagtypeMaxFetchSizeAsInteger() {
                return 1000;
            }

            @Override
            public String getIndexFieldTag() {
                return "tag";
            }

            @Override
            public String getIndexFieldUrl() {
                return "url";
            }

            @Override
            public String getRoleSearchUserPrefix() {
                return "1";
            }

            @Override
            public List<String> getSearchGuestRoleList() {
                return guestRoleList;
            }
        });
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        ComponentUtil.register(new VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return virtualHostKey;
            }
        }, "virtualHostHelper");
        ComponentUtil.register(new RoleQueryHelper() {
            @Override
            public Set<String> build(final SearchRequestType searchRequestType) {
                return roleSet;
            }
        }, "roleQueryHelper");
        tagTypeBhv = new MockTagTypeBhv();
        tagTypeHelper = new TagTypeHelper() {
            @Override
            protected OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.ofNullable(userBean, () -> {});
            }
        };
        tagTypeHelper.tagTypeBhv = tagTypeBhv;
    }

    // -----------------------------------------------------------------------
    // value, id and name
    // -----------------------------------------------------------------------

    @Test
    public void test_tagValue_roundTrip_specialChars() {
        for (final String name : List.of("a:b", "x/y+z", "日本語", "😀 tag")) {
            final String v = tagTypeHelper.toTagValue(name, "user:1");
            assertEquals(new Pair<>(name, "user:1"), tagTypeHelper.parseTagValue(v).get());
            assertFalse(v.contains("="));
            assertFalse(v.contains("/"));
            assertFalse(v.contains("+"));
        }
        assertFalse(tagTypeHelper.toTagValue("a:b", "c").equals(tagTypeHelper.toTagValue("a", "b:c")));
    }

    @Test
    public void test_tagValue_matchesEntity() {
        final TagType tagType = createTagType("foo", "alice");
        assertEquals(tagType.getTagValue(), tagTypeHelper.toTagValue("foo", "alice"));
    }

    @Test
    public void test_parseTagValue_malformed() {
        assertTrue(tagTypeHelper.parseTagValue(null).isEmpty());
        assertTrue(tagTypeHelper.parseTagValue("").isEmpty());
        assertTrue(tagTypeHelper.parseTagValue("Zm9v").isEmpty());
        assertTrue(tagTypeHelper.parseTagValue("Zm9v:").isEmpty());
        assertTrue(tagTypeHelper.parseTagValue(":Zm9v").isEmpty());
        assertTrue(tagTypeHelper.parseTagValue("Zm9v:Zm9v:Zm9v").isEmpty());
        assertTrue(tagTypeHelper.parseTagValue("Zm9v:Zm9v=").isEmpty());
        assertTrue(tagTypeHelper.parseTagValue("Zm9v:*").isEmpty());
        // not valid UTF-8 (0xff)
        assertTrue(tagTypeHelper.parseTagValue("_w:Zm9v").isEmpty());
    }

    @Test
    public void test_toId_isSha256Hex() {
        assertTrue(tagTypeHelper.toId("x").matches("[0-9a-f]{64}"));
        // sha256("x")
        assertEquals("2d711642b726b04401627ca9fbac32f5c8530fb1903cc4db02258717921a4881", tagTypeHelper.toId("x"));
    }

    @Test
    public void test_normalizeName() {
        assertEquals("ABC x", tagTypeHelper.normalizeName("ＡＢＣ  x ").get());
        assertEquals("a b", tagTypeHelper.normalizeName("\ta　\n b").get());
        assertTrue(tagTypeHelper.normalizeName(null).isEmpty());
        assertTrue(tagTypeHelper.normalizeName("   ").isEmpty());
        assertTrue(tagTypeHelper.normalizeName("a\u0000b").isEmpty());
        assertTrue(tagTypeHelper.normalizeName("a‎b").isEmpty());
        // a format character outside the BMP (TAG LATIN SMALL LETTER A)
        assertTrue(tagTypeHelper.normalizeName("a󠁡b").isEmpty());
        // an unpaired surrogate
        assertTrue(tagTypeHelper.normalizeName("a\uD800b").isEmpty());
        assertTrue(tagTypeHelper.normalizeName("a".repeat(51)).isEmpty());
        assertEquals("a".repeat(50), tagTypeHelper.normalizeName("a".repeat(50)).get());
        // counted in code points, not chars
        assertEquals("😀".repeat(50), tagTypeHelper.normalizeName("😀".repeat(50)).get());
    }

    // -----------------------------------------------------------------------
    // permissions
    // -----------------------------------------------------------------------

    @Test
    public void test_isShared() {
        assertTrue(tagTypeHelper.isShared(createTagType("foo", "alice", "1alice", "Rguest")));
        assertFalse(tagTypeHelper.isShared(createTagType("foo", "alice", "1alice")));
        final TagType noPermissions = createTagType("foo", "alice");
        noPermissions.setPermissions(null);
        assertFalse(tagTypeHelper.isShared(noPermissions));
    }

    @Test
    public void test_buildPermissions() {
        assertArrayEquals(new String[] { "1alice" }, tagTypeHelper.buildPermissions("alice", false));
        // the synthetic guest user entry of the guest role list is not stored
        assertArrayEquals(new String[] { "1alice", "Rguest" }, tagTypeHelper.buildPermissions("alice", true));
    }

    // -----------------------------------------------------------------------
    // visibility
    // -----------------------------------------------------------------------

    @Test
    public void test_visibility_ownerEvenWithoutUserPermission() {
        // An LDAP-style user whose roles do not carry 1alice still sees the tags they own.
        login("alice");
        roleSet.add("2group");
        final TagType tagType = store(createTagType("foo", "alice", "1alice"));

        final Map<String, TagType> visible = tagTypeHelper.getVisibleTagTypes(List.of(tagType.getTagValue()), SearchRequestType.JSON);
        assertEquals(1, visible.size());
        assertSame(tagType, visible.get(tagType.getTagValue()));
    }

    @Test
    public void test_visibility_sharedVisibleToOtherLoggedInUser() {
        login("bob");
        roleSet.add("1bob");
        final TagType tagType = store(createTagType("foo", "alice", "1alice", "Rguest"));

        final Map<String, TagType> visible = tagTypeHelper.getVisibleTagTypes(List.of(tagType.getTagValue()), SearchRequestType.JSON);
        assertTrue(visible.containsKey(tagType.getTagValue()));
    }

    @Test
    public void test_visibility_privateHiddenFromOthers() {
        login("bob");
        roleSet.add("1bob");
        final TagType tagType = store(createTagType("foo", "alice", "1alice"));

        assertTrue(tagTypeHelper.getVisibleTagTypes(List.of(tagType.getTagValue()), SearchRequestType.JSON).isEmpty());
    }

    @Test
    public void test_visibility_anonymousSeesNothing() {
        roleSet.add("Rguest");
        final TagType tagType = store(createTagType("foo", "alice", "1alice", "Rguest"));

        assertTrue(tagTypeHelper.getVisibleTagTypes(List.of(tagType.getTagValue()), SearchRequestType.JSON).isEmpty());
        assertTrue(tagTypeHelper.getVisibleTagValues(SearchRequestType.JSON).isEmpty());
        assertEquals(0, tagTypeBhv.selectListCount.get());
    }

    @Test
    public void test_visibility_virtualHostMismatchHidden() {
        login("alice");
        final TagType tagType = store(createTagType("foo", "alice", "1alice"));
        tagType.setVirtualHost("host1");

        virtualHostKey = "host2";
        assertTrue(tagTypeHelper.getVisibleTagTypes(List.of(tagType.getTagValue()), SearchRequestType.JSON).isEmpty());
        clearRequestCache();
        virtualHostKey = "host1";
        assertTrue(tagTypeHelper.getVisibleTagTypes(List.of(tagType.getTagValue()), SearchRequestType.JSON)
                .containsKey(tagType.getTagValue()));
        clearRequestCache();
        virtualHostKey = "";
        assertTrue(tagTypeHelper.getVisibleTagTypes(List.of(tagType.getTagValue()), SearchRequestType.JSON)
                .containsKey(tagType.getTagValue()));
    }

    @Test
    public void test_visibility_sharedFollowsGuestPermissionSetting() {
        // role.search.guest.permissions={role}everyone
        guestRoleList = new ArrayList<>(List.of("Reveryone", "1guest"));
        assertArrayEquals(new String[] { "1alice", "Reveryone" }, tagTypeHelper.buildPermissions("alice", true));

        login("bob");
        roleSet.add("1bob");
        final TagType oldShared = store(createTagType("old", "alice", "1alice", "Rguest"));
        final TagType newShared = store(createTagType("new", "alice", tagTypeHelper.buildPermissions("alice", true)));

        assertFalse(tagTypeHelper.isShared(oldShared));
        assertTrue(tagTypeHelper.isShared(newShared));
        final Map<String, TagType> visible =
                tagTypeHelper.getVisibleTagTypes(List.of(oldShared.getTagValue(), newShared.getTagValue()), SearchRequestType.JSON);
        assertEquals(Set.of(newShared.getTagValue()), visible.keySet());
    }

    @Test
    public void test_getVisibleTagTypes_cachedPerRequest() {
        login("alice");
        final TagType foo = store(createTagType("foo", "alice", "1alice"));
        final TagType bar = store(createTagType("bar", "alice", "1alice"));

        tagTypeHelper.getVisibleTagTypes(List.of(foo.getTagValue()), SearchRequestType.JSON);
        tagTypeHelper.getVisibleTagTypes(List.of(foo.getTagValue()), SearchRequestType.JSON);
        assertEquals(1, tagTypeBhv.selectListCount.get());

        final Map<String, TagType> visible =
                tagTypeHelper.getVisibleTagTypes(List.of(foo.getTagValue(), bar.getTagValue(), "unknown"), SearchRequestType.JSON);
        assertEquals(2, tagTypeBhv.selectListCount.get());
        assertEquals(Set.of(foo.getTagValue(), bar.getTagValue()), visible.keySet());
        // the second query asked only for the values not cached yet
        assertTrue(tagTypeBhv.lastQuery.contains(tagTypeHelper.toId(bar.getTagValue())));
        assertFalse(tagTypeBhv.lastQuery.contains(tagTypeHelper.toId(foo.getTagValue())));
        // paths are not fetched
        assertFalse(tagTypeBhv.lastSpecifiedColumns.contains("paths"));
    }

    @Test
    public void test_getVisibleTagValues() {
        login("alice");
        roleSet.add("1alice");
        roleSet.add("2dev");
        virtualHostKey = "host1";
        store(createTagType("foo", "alice", "1alice"));
        store(createTagType("bar", "bob", "1bob", "Rguest"));

        final Set<String> values = tagTypeHelper.getVisibleTagValues(SearchRequestType.JSON);
        assertEquals(Set.of(tagTypeHelper.toTagValue("foo", "alice"), tagTypeHelper.toTagValue("bar", "bob")), values);
        assertEquals(2, tagTypeBhv.queries.size());
        // own tags first
        final String ownQuery = tagTypeBhv.queries.get(0);
        assertTrue(ownQuery.contains("\"owner\""), ownQuery);
        assertTrue(ownQuery.contains("\"alice\""), ownQuery);
        assertFalse(ownQuery.contains("\"permissions\""), ownQuery);
        assertTrue(ownQuery.contains("\"host1\""), ownQuery);
        // then tags of others the caller holds a permission of, with the rest of the budget
        final String otherQuery = tagTypeBhv.queries.get(1);
        assertTrue(otherQuery.contains("\"permissions\""), otherQuery);
        assertTrue(otherQuery.contains("\"2dev\""), otherQuery);
        assertTrue(otherQuery.contains("\"Rguest\""), otherQuery);
        assertFalse(otherQuery.contains("\"1guest\""), otherQuery);
        assertTrue(otherQuery.contains("\"must_not\""), otherQuery);
        assertTrue(otherQuery.contains("\"alice\""), otherQuery);
        assertTrue(otherQuery.contains("\"virtualHost\""), otherQuery);
        assertTrue(otherQuery.contains("\"host1\""), otherQuery);
        // the mock answered both stored tags to the first query
        assertEquals(998, tagTypeBhv.lastFetchSize);
        assertFalse(tagTypeBhv.lastSpecifiedColumns.contains("paths"));
    }

    @Test
    public void test_getVisibleTagValues_ownTagsKeptWithinCap() {
        login("alice");
        roleSet.add("1alice");
        final List<TagType> own = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            own.add(store(createTagType("own" + i, "alice", "1alice")));
        }
        final List<TagType> others = new ArrayList<>();
        // sorted before the own tags by sortOrder
        for (int i = 0; i < 3; i++) {
            final TagType tagType = store(createTagType("shared" + i, "bob", "1bob", "Rguest"));
            tagType.setSortOrder(-1);
            others.add(tagType);
        }
        // the owner query answers own tags; the permission query (must_not owner) answers others
        tagTypeBhv.responder = query -> query.contains("must_not") ? others : own;

        visibleMaxSize = 3;
        assertEquals(Set.of(own.get(0).getTagValue(), own.get(1).getTagValue(), own.get(2).getTagValue()),
                tagTypeHelper.getVisibleTagValues(SearchRequestType.JSON));
        // the budget was spent on own tags, so the permission query is not sent
        assertEquals(1, tagTypeBhv.selectListCount.get());

        clearRequestCache();
        visibleMaxSize = 4;
        final Set<String> values = tagTypeHelper.getVisibleTagValues(SearchRequestType.JSON);
        assertEquals(4, values.size());
        for (final TagType tagType : own) {
            assertTrue(values.contains(tagType.getTagValue()));
        }
        assertTrue(values.contains(others.get(0).getTagValue()));
        assertEquals(1, tagTypeBhv.lastFetchSize);
    }

    @Test
    public void test_getVisibleTagValues_cachedPerRequest() {
        login("alice");
        roleSet.add("1alice");
        store(createTagType("foo", "alice", "1alice"));

        final Set<String> first = tagTypeHelper.getVisibleTagValues(SearchRequestType.JSON);
        final int count = tagTypeBhv.selectListCount.get();
        assertEquals(Set.of(tagTypeHelper.toTagValue("foo", "alice")), first);
        assertEquals(first, tagTypeHelper.getVisibleTagValues(SearchRequestType.JSON));
        assertEquals(count, tagTypeBhv.selectListCount.get());

        clearRequestCache();
        tagTypeHelper.getVisibleTagValues(SearchRequestType.JSON);
        assertTrue(tagTypeBhv.selectListCount.get() > count);
    }

    @Test
    public void test_guestNamedOwner_privateTagNotShared() {
        // a user named guest has the guest user role 1guest as their own permission
        final TagType tagType = store(createTagType("foo", "guest", tagTypeHelper.buildPermissions("guest", false)));
        assertArrayEquals(new String[] { "1guest" }, tagType.getPermissions());
        assertFalse(tagTypeHelper.isShared(tagType));

        // bob's roles may carry the guest roles, 1guest included
        login("bob");
        roleSet.addAll(List.of("1bob", "Rguest", "1guest"));
        assertTrue(tagTypeHelper.getVisibleTagTypes(List.of(tagType.getTagValue()), SearchRequestType.JSON).isEmpty());
        tagTypeHelper.getVisibleTagValues(SearchRequestType.JSON);
        assertFalse(tagTypeBhv.lastQuery.contains("\"1guest\""), tagTypeBhv.lastQuery);

        // the owner still sees it
        clearRequestCache();
        login("guest");
        assertTrue(tagTypeHelper.getVisibleTagTypes(List.of(tagType.getTagValue()), SearchRequestType.JSON)
                .containsKey(tagType.getTagValue()));
    }

    // -----------------------------------------------------------------------
    // URL lookup and indexing
    // -----------------------------------------------------------------------

    @Test
    public void test_findTagValuesByUrls_exactMatch() {
        final String url = "http://a/b c%20#x";
        store(createTagType("foo", "alice", "1alice")).setPaths(new String[] { url, "http://a/b" });
        store(createTagType("bar", "bob", "1bob")).setPaths(new String[] { "http://a/b c", "http://a/b c%20" });

        final Map<String, Set<String>> result = tagTypeHelper.findTagValuesByUrls(List.of(url));
        assertEquals(Map.of(url, Set.of(tagTypeHelper.toTagValue("foo", "alice"))), result);
        assertTrue(tagTypeBhv.lastQuery.contains("\"paths\""), tagTypeBhv.lastQuery);
    }

    @Test
    public void test_findTagValuesByUrls_moreThanOnePage() {
        final String url = "http://example.com/";
        final Set<String> expected = new LinkedHashSet<>();
        for (int i = 0; i < 25; i++) {
            store(createTagType("tag" + i, "alice", "1alice")).setPaths(new String[] { url });
            expected.add(tagTypeHelper.toTagValue("tag" + i, "alice"));
        }

        assertEquals(Map.of(url, expected), tagTypeHelper.findTagValuesByUrls(List.of(url)));
        assertTrue(tagTypeBhv.selectBulkPages > 1);
    }

    @Test
    public void test_findTagValuesByUrls_empty() {
        assertTrue(tagTypeHelper.findTagValuesByUrls(List.of()).isEmpty());
        assertEquals(0, tagTypeBhv.selectBulkPages);
    }

    @Test
    public void test_applyTags_setsAndRemovesField() {
        store(createTagType("foo", "alice", "1alice")).setPaths(new String[] { "http://a/1" });
        store(createTagType("bar", "bob", "1bob", "Rguest")).setPaths(new String[] { "http://a/1", "http://a/2" });

        final Map<String, Object> doc1 = new HashMap<>(Map.of("url", "http://a/1"));
        final Map<String, Object> doc2 = new HashMap<>(Map.of("url", "http://a/2", "tag", new String[] { "stale" }));
        final Map<String, Object> doc3 = new HashMap<>(Map.of("url", "http://a/3", "tag", new String[] { "stale" }));
        final Map<String, Object> doc4 = new HashMap<>(Map.of("title", "no url", "tag", "stale"));
        tagTypeHelper.applyTags(List.of(doc1, doc2, doc3, doc4));

        assertEquals(Set.of(tagTypeHelper.toTagValue("foo", "alice"), tagTypeHelper.toTagValue("bar", "bob")),
                Set.of((String[]) doc1.get("tag")));
        assertArrayEquals(new String[] { tagTypeHelper.toTagValue("bar", "bob") }, (String[]) doc2.get("tag"));
        assertFalse(doc3.containsKey("tag"));
        assertFalse(doc4.containsKey("tag"));
        assertEquals(1, tagTypeBhv.selectBulkCalls);
    }

    @Test
    public void test_applyTags_disabledLeavesDocumentsUntouched() {
        userTagEnabled = false;
        store(createTagType("foo", "alice", "1alice")).setPaths(new String[] { "http://a/1" });
        final Map<String, Object> doc1 = new HashMap<>(Map.of("url", "http://a/1"));
        final Map<String, Object> doc2 = new HashMap<>(Map.of("url", "http://a/2", "tag", "kept"));

        tagTypeHelper.applyTags(List.of(doc1, doc2));

        assertFalse(doc1.containsKey("tag"));
        assertEquals("kept", doc2.get("tag"));
        assertEquals(0, tagTypeBhv.selectBulkCalls);
    }

    @Test
    public void test_applyTags_lookupFailureLeavesDocumentsUntouched() {
        tagTypeBhv.failure = new IllegalStateException("search engine is down");
        final Map<String, Object> doc = new HashMap<>(Map.of("url", "http://a/1", "tag", "kept"));

        tagTypeHelper.applyTags(List.of(doc));

        assertEquals("kept", doc.get("tag"));
    }

    // -----------------------------------------------------------------------
    // helpers
    // -----------------------------------------------------------------------

    private static void assertArrayEquals(final Object[] expected, final Object[] actual) {
        org.junit.jupiter.api.Assertions.assertArrayEquals(expected, actual);
    }

    private void login(final String userId) {
        userBean = new FessUserBean(new StubFessUser(userId));
    }

    private void clearRequestCache() {
        for (final SearchRequestType type : SearchRequestType.values()) {
            getMockRequest().removeAttribute(TagTypeHelper.VISIBLE_TAG_TYPES_ATTRIBUTE + type.name());
            getMockRequest().removeAttribute(TagTypeHelper.VISIBLE_TAG_VALUES_ATTRIBUTE + type.name());
        }
    }

    private TagType createTagType(final String name, final String owner, final String... permissions) {
        final TagType tagType = new TagType();
        tagType.setName(name);
        tagType.setOwner(owner);
        tagType.setPermissions(permissions);
        tagType.setVirtualHost("");
        tagType.setSortOrder(0);
        tagType.setPaths(new String[0]);
        return tagType;
    }

    private TagType store(final TagType tagType) {
        tagType.setId(tagTypeHelper.toId(tagType.getTagValue()));
        tagTypeBhv.entities.add(tagType);
        return tagType;
    }

    /**
     * Returns every stored entity from a query, so the helper must itself pick the ones asked
     * for; records the query and the columns it fetched.
     */
    private static class MockTagTypeBhv extends TagTypeBhv {
        final List<TagType> entities = new ArrayList<>();

        final AtomicInteger selectListCount = new AtomicInteger();

        int selectBulkCalls;

        int selectBulkPages;

        String lastQuery;

        final List<String> queries = new ArrayList<>();

        Set<String> lastSpecifiedColumns;

        int lastFetchSize;

        RuntimeException failure;

        /** When set, answers selectList from the query, truncated to the fetch size. */
        java.util.function.Function<String, List<TagType>> responder;

        private TagTypeCB capture(final CBCall<TagTypeCB> cbLambda) {
            if (failure != null) {
                throw failure;
            }
            final TagTypeCB cb = new TagTypeCB();
            cbLambda.callback(cb);
            lastQuery = String.valueOf(cb.query().getQuery());
            queries.add(lastQuery);
            final SearchSourceBuilder source = cb.build(new SearchRequestBuilder(null, SearchAction.INSTANCE)).request().source();
            lastSpecifiedColumns = new LinkedHashSet<>();
            if (source != null && source.fetchSource() != null) {
                lastSpecifiedColumns.addAll(List.of(source.fetchSource().includes()));
            }
            lastFetchSize = cb.isFetchScopeEffective() ? cb.getFetchSize() : -1;
            return cb;
        }

        @Override
        public ListResultBean<TagType> selectList(final CBCall<TagTypeCB> cbLambda) {
            capture(cbLambda);
            selectListCount.incrementAndGet();
            final ListResultBean<TagType> list = new ListResultBean<>();
            if (responder != null) {
                final List<TagType> answer = responder.apply(lastQuery);
                list.setSelectedList(new ArrayList<>(answer.subList(0, Math.min(answer.size(), lastFetchSize))));
                return list;
            }
            list.setSelectedList(new ArrayList<>(entities));
            return list;
        }

        @Override
        public void selectBulk(final CBCall<TagTypeCB> cbLambda, final EntityRowHandler<List<TagType>> entityLambda) {
            selectBulkCalls++;
            capture(cbLambda);
            // hand the entities over in pages of 10, like a cursor over the search engine
            for (int i = 0; i < entities.size(); i += 10) {
                selectBulkPages++;
                entityLambda.handle(new ArrayList<>(entities.subList(i, Math.min(i + 10, entities.size()))));
            }
        }
    }

    private static class StubFessUser implements FessUser {
        private static final long serialVersionUID = 1L;

        private final String name;

        StubFessUser(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String[] getRoleNames() {
            return new String[0];
        }

        @Override
        public String[] getGroupNames() {
            return new String[0];
        }

        @Override
        public String[] getPermissions() {
            // an LDAP-style user whose permissions do not carry the user role
            return new String[] { "2group" };
        }
    }
}
