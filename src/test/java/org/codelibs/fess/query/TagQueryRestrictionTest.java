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
package org.codelibs.fess.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.TagTypeHelper;
import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.query.StructuredQuerySplitter.Split;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fesen.opensearch.index.query.BoolQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.MatchNoneQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.TermQueryBuilder;
import org.junit.jupiter.api.Test;

/**
 * A condition on the {@code tag} field matches only the tags that the caller can see.
 */
public class TagQueryRestrictionTest extends QueryTestBase {

    private static final String VISIBLE = TagType.toTagValue("report", "alice");

    private static final String HIDDEN = TagType.toTagValue("secret", "bob");

    private final List<Collection<String>> resolvedValues = new ArrayList<>();

    private SearchRequestType resolvedType;

    @Override
    protected void setUpChild() throws Exception {
        new TermQueryCommand().register();
        new PhraseQueryCommand().register();
        new WildcardQueryCommand().register();
        new PrefixQueryCommand().register();
        new FuzzyQueryCommand().register();
        new TermRangeQueryCommand().register();
        new BooleanQueryCommand().register();
        new BoostQueryCommand().register();
        resolvedValues.clear();
        resolvedType = null;
        ComponentUtil.register(new TagTypeHelper() {
            @Override
            public Map<String, TagType> getVisibleTagTypes(final Collection<String> values, final SearchRequestType type) {
                resolvedValues.add(new ArrayList<>(values));
                resolvedType = type;
                final Map<String, TagType> result = new HashMap<>();
                if (values.contains(VISIBLE)) {
                    final TagType tagType = new TagType();
                    tagType.setName("report");
                    tagType.setOwner("alice");
                    result.put(VISIBLE, tagType);
                }
                return result;
            }
        }, "tagTypeHelper");
    }

    private QueryBuilder build(final String query, final SearchRequestType searchRequestType) {
        final QueryContext context = new QueryContext(query, false);
        context.setSearchRequestType(searchRequestType);
        return queryProcessor.execute(context, ComponentUtil.getQueryParser().parse(query), 1.0f);
    }

    private QueryBuilder build(final String query) {
        return build(query, SearchRequestType.JSON);
    }

    private static String quoted(final String value) {
        return "tag:\"" + value + "\"";
    }

    private void assertTermQuery(final QueryBuilder builder, final String value) {
        assertTrue(builder instanceof TermQueryBuilder, "expected a term query: " + builder);
        final TermQueryBuilder termQuery = (TermQueryBuilder) builder;
        assertEquals("tag", termQuery.fieldName());
        assertEquals(value, termQuery.value());
    }

    private void assertMatchNone(final QueryBuilder builder) {
        assertTrue(builder instanceof MatchNoneQueryBuilder, "expected a query matching nothing: " + builder);
    }

    @Test
    public void test_fieldsTag_visible() {
        // fields.tag=<value> is turned into tag:"<value>"
        assertTermQuery(build(quoted(VISIBLE)), VISIBLE);
    }

    @Test
    public void test_fieldsTag_hidden() {
        assertMatchNone(build(quoted(HIDDEN)));
    }

    @Test
    public void test_queryTag_visible() {
        final String value = "abc";
        ComponentUtil.register(new TagTypeHelper() {
            @Override
            public Map<String, TagType> getVisibleTagTypes(final Collection<String> values, final SearchRequestType type) {
                return values.contains(value) ? Map.of(value, new TagType()) : Map.of();
            }
        }, "tagTypeHelper");
        assertTermQuery(build("tag:" + value), value);
    }

    @Test
    public void test_queryTag_hidden() {
        assertMatchNone(build("tag:hidden"));
    }

    @Test
    public void test_queryTag_hiddenWithKeyword() {
        final QueryBuilder builder = build("report " + quoted(HIDDEN));
        assertTrue(builder instanceof BoolQueryBuilder, builder.toString());
        assertTrue(builder.toString().contains("match_none"), builder.toString());
        assertFalse(builder.toString().contains(HIDDEN), builder.toString());
    }

    @Test
    public void test_queryTag_patterns() {
        assertMatchNone(build("tag:c2Vj*"));
        assertMatchNone(build("tag:c2Vj?mV0"));
        assertMatchNone(build("tag:c2VjcmV0~"));
        assertMatchNone(build("tag:[a TO z]"));
        assertMatchNone(build("tag:\"" + VISIBLE + " " + HIDDEN + "\""));
        // nothing was resolved: a pattern never becomes a condition on a tag
        assertTrue(resolvedValues.isEmpty(), resolvedValues.toString());
    }

    @Test
    public void test_queryTag_negatedHidden() {
        final QueryBuilder builder = build("-" + quoted(HIDDEN));
        assertTrue(builder instanceof BoolQueryBuilder, builder.toString());
        final BoolQueryBuilder boolQuery = (BoolQueryBuilder) builder;
        assertEquals(1, boolQuery.mustNot().size());
        assertMatchNone(boolQuery.mustNot().get(0));
    }

    @Test
    public void test_queryTag_resolvedOncePerValue() {
        build(quoted(VISIBLE) + " OR " + quoted(HIDDEN) + " OR " + quoted(VISIBLE) + " OR " + quoted(HIDDEN));
        assertEquals(2, resolvedValues.size(), resolvedValues.toString());
        assertEquals(List.of(VISIBLE), resolvedValues.get(0));
        assertEquals(List.of(HIDDEN), resolvedValues.get(1));
        assertEquals(SearchRequestType.JSON, resolvedType);
    }

    @Test
    public void test_queryTag_unknownRequestType() {
        assertMatchNone(build(quoted(HIDDEN), null));
        assertEquals(SearchRequestType.JSON, resolvedType);
    }

    @Test
    public void test_queryTag_adminSearch() {
        assertTermQuery(build(quoted(HIDDEN), SearchRequestType.ADMIN_SEARCH), HIDDEN);
        assertTrue(resolvedValues.isEmpty());
    }

    @Test
    public void test_queryTag_adminSearch_patternsUnrestricted() {
        assertFalse(build("tag:c2Vj*", SearchRequestType.ADMIN_SEARCH) instanceof MatchNoneQueryBuilder);
    }

    @Test
    public void test_queryTag_disabled_matchesNothing() {
        userTagEnabled = false;
        assertMatchNone(build(quoted(VISIBLE)));
        assertTrue(resolvedValues.isEmpty(), resolvedValues.toString());
        // the admin search is not restricted, enabled or not
        assertTermQuery(build(quoted(HIDDEN), SearchRequestType.ADMIN_SEARCH), HIDDEN);
    }

    @Test
    public void test_otherField_notResolved() {
        final QueryBuilder builder = build("title:" + "hidden");
        assertFalse(builder instanceof MatchNoneQueryBuilder, builder.toString());
        assertTrue(resolvedValues.isEmpty());
    }

    @Test
    public void test_queryTag_resolutionFails() {
        ComponentUtil.register(new TagTypeHelper() {
            @Override
            public Map<String, TagType> getVisibleTagTypes(final Collection<String> values, final SearchRequestType type) {
                throw new IllegalStateException("not available");
            }
        }, "tagTypeHelper");
        assertMatchNone(build(quoted(VISIBLE)));
    }

    // -------------------------------------------------------------------------------------
    //                                              visibility from the real TagTypeHelper
    //                                              --------------------------------------

    /**
     * Registers a TagTypeHelper that decides visibility itself from the given tags, as the caller
     * {@code userId} (null when not logged in) holding the given roles.
     */
    private void registerRealVisibility(final String userId, final Set<String> roles, final TagType... tagTypes) {
        ComponentUtil.register(new VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return "";
            }
        }, "virtualHostHelper");
        ComponentUtil.register(new TagTypeHelper() {
            // the mock request lives across the callers of a test: keep each caller's cache apart
            private final Map<String, Optional<TagType>> cache = new HashMap<>();

            @Override
            protected Map<String, Optional<TagType>> getVisibleTagTypeCache(final SearchRequestType type) {
                return cache;
            }

            @Override
            protected String getLoggedInUserId() {
                return userId;
            }

            @Override
            protected Set<String> getViewerRoles(final SearchRequestType type) {
                return roles;
            }

            @Override
            protected Map<String, TagType> fetchTagTypes(final Collection<String> values) {
                final Map<String, TagType> result = new LinkedHashMap<>();
                for (final TagType tagType : tagTypes) {
                    if (values.contains(tagType.getTagValue())) {
                        result.put(tagType.getTagValue(), tagType);
                    }
                }
                return result;
            }
        }, "tagTypeHelper");
    }

    private static TagType tagType(final String name, final String owner, final String... permissions) {
        final TagType tagType = new TagType();
        tagType.setName(name);
        tagType.setOwner(owner);
        tagType.setPermissions(permissions);
        tagType.setVirtualHost("");
        return tagType;
    }

    @Test
    public void test_sameNameDifferentOwner_isolated() {
        // alice and bob each have a tag named foo; only bob's is shared
        final TagType aliceFoo = tagType("foo", "alice", "1alice");
        final TagType bobFoo = tagType("foo", "bob", "1bob", "Rguest");
        final TagType carolFoo = tagType("foo", "carol", "1carol");
        assertFalse(aliceFoo.getTagValue().equals(bobFoo.getTagValue()));

        // alice sees her own foo and bob's shared foo as two separate conditions, never carol's
        registerRealVisibility("alice", Set.of("1alice", "Rguest"), aliceFoo, bobFoo, carolFoo);
        assertTermQuery(build(quoted(aliceFoo.getTagValue())), aliceFoo.getTagValue());
        assertTermQuery(build(quoted(bobFoo.getTagValue())), bobFoo.getTagValue());
        assertMatchNone(build(quoted(carolFoo.getTagValue())));

        // bob cannot reach alice's private foo through his own tag of the same name
        registerRealVisibility("bob", Set.of("1bob", "Rguest"), aliceFoo, bobFoo, carolFoo);
        assertTermQuery(build(quoted(bobFoo.getTagValue())), bobFoo.getTagValue());
        assertMatchNone(build(quoted(aliceFoo.getTagValue())));
    }

    @Test
    public void test_anonymous_tagConditionMatchesNothing() {
        final TagType shared = tagType("foo", "bob", "1bob", "Rguest");
        registerRealVisibility(null, Set.of("Rguest"), shared);
        // fields.tag with a valid value of a shared tag: no error, no match
        assertMatchNone(build(quoted(shared.getTagValue())));
        assertMatchNone(build(quoted(shared.getTagValue()), SearchRequestType.SEARCH));
        final QueryBuilder withKeyword = build("report " + quoted(shared.getTagValue()));
        assertTrue(withKeyword.toString().contains("match_none"), withKeyword.toString());
    }

    // -------------------------------------------------------------------------------------
    //                                                                          vector leg
    //                                                                          ----------

    /**
     * The vector leg builds the conditions of the query with the same commands as the keyword leg,
     * so a condition on the tag field has to see the request type of the search that is running.
     */
    private QueryBuilder splitFilter(final String query, final SearchRequestType searchRequestType) {
        final Split split = new StructuredQuerySplitter().split(query, searchRequestType);
        assertNotNull(split);
        assertEquals("report", split.text);
        assertNotNull(split.conditionFilter);
        return split.conditionFilter;
    }

    @Test
    public void test_vectorLeg_adminSearch_keepsTheTagOfAnyValue() {
        // Like the keyword leg of an admin search, the vector leg is not limited to the visible tags.
        final String filter = splitFilter("report " + quoted(HIDDEN), SearchRequestType.ADMIN_SEARCH).toString();
        assertTrue(filter.contains(HIDDEN), filter);
        assertFalse(filter.contains("match_none"), filter);
        assertTrue(resolvedValues.isEmpty());
    }

    @Test
    public void test_vectorLeg_jsonSearch_stillLimitedToVisibleTags() {
        final String filter = splitFilter("report " + quoted(HIDDEN), SearchRequestType.JSON).toString();
        assertTrue(filter.contains("match_none"), filter);
        assertFalse(filter.contains(HIDDEN), filter);
        assertEquals(SearchRequestType.JSON, resolvedType);
        assertTrue(splitFilter("report " + quoted(VISIBLE), SearchRequestType.JSON).toString().contains(VISIBLE));
    }

    @Test
    public void test_vectorLeg_htmlSearch_limitedToTheTagsOfThatRequestType() {
        final String filter = splitFilter("report " + quoted(HIDDEN), SearchRequestType.SEARCH).toString();
        assertTrue(filter.contains("match_none"), filter);
        assertFalse(filter.contains(HIDDEN), filter);
        // the visible tags are resolved for the HTML search, not for the JSON API
        assertEquals(SearchRequestType.SEARCH, resolvedType);
    }

    @Test
    public void test_vectorLeg_unknownRequestType_stillLimitedToVisibleTags() {
        final String filter = splitFilter("report " + quoted(HIDDEN), null).toString();
        assertTrue(filter.contains("match_none"), filter);
        assertEquals(SearchRequestType.JSON, resolvedType);
    }
}
