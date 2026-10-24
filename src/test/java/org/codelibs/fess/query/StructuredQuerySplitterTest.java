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

import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.query.StructuredQuerySplitter.Split;
import org.junit.jupiter.api.Test;
import org.codelibs.fesen.opensearch.index.query.BoolQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;

public class StructuredQuerySplitterTest extends UnitFessTestCase {

    // ---- Accepted: the shapes QueryStringBuilder folds into the query string ----

    @Test
    public void test_plainText_hasNoConditions() {
        final StubProcessorSplitter splitter = new StubProcessorSplitter();
        final Split split = splitter.split("mountain sunset");
        assertNotNull(split);
        assertEquals("mountain sunset", split.text);
        assertNull(split.conditionFilter);
        assertTrue(splitter.converted.isEmpty());
    }

    @Test
    public void test_singleTerm() {
        final Split split = new StubProcessorSplitter().split("cat");
        assertNotNull(split);
        assertEquals("cat", split.text);
        assertNull(split.conditionFilter);
    }

    /** ` filetype:"jpeg"` -- what QueryStringBuilder appends for a single facet selection. */
    @Test
    public void test_singleValueCondition() {
        final StubProcessorSplitter splitter = new StubProcessorSplitter();
        final Split split = splitter.split("mountain sunset filetype:\"jpeg\"");
        assertNotNull(split);
        assertEquals("mountain sunset", split.text);
        assertEquals(1, splitter.converted.size());
        assertEquals("filetype:jpeg", splitter.converted.get(0));
        assertEquals(1, filterClauses(split).size());
    }

    /**
     * ` (label:"a" OR label:"b")` -- a multi-value facet selection. The whole OR group must reach
     * core as ONE sub-query: splitting it into two clauses would turn the user's OR into an AND
     * and return nothing.
     */
    @Test
    public void test_orGroupReachesCoreAsOneSubQuery() {
        final StubProcessorSplitter splitter = new StubProcessorSplitter();
        final Split split = splitter.split("cat (label:\"a\" OR label:\"b\")");
        assertNotNull(split);
        assertEquals("cat", split.text);
        assertEquals(1, splitter.converted.size());
        assertTrue(splitter.converted.get(0).contains("label:a"));
        assertTrue(splitter.converted.get(0).contains("label:b"));
        assertEquals(1, filterClauses(split).size());
    }

    @Test
    public void test_multipleFieldsBecomeMultipleClauses() {
        final StubProcessorSplitter splitter = new StubProcessorSplitter();
        final Split split = splitter.split("cat dog label:\"a\" filetype:\"b\"");
        assertNotNull(split);
        assertEquals("cat dog", split.text);
        assertEquals(2, filterClauses(split).size());
    }

    /** A negated condition is expressible as a filter, so it is kept rather than refused. */
    @Test
    public void test_negatedCondition_becomesMustNot() {
        final Split split = new StubProcessorSplitter().split("cat -label:\"x\"");
        assertNotNull(split);
        assertEquals("cat", split.text);
        final BoolQueryBuilder filter = (BoolQueryBuilder) split.conditionFilter;
        assertEquals(0, filter.filter().size());
        assertEquals(1, filter.mustNot().size());
    }

    /**
     * Optional text alongside a required condition. The classic parser leaves the two spellings
     * of this in different shapes -- a flat clause list here, a nested boolean for the
     * parenthesised form below -- and both must split the same way, or which queries keep the
     * vector branch would depend on punctuation the user did not mean to be significant.
     */
    @Test
    public void test_optionalTextWithRequiredCondition() {
        final StubProcessorSplitter splitter = new StubProcessorSplitter();
        final Split split = splitter.split("cat OR dog label:\"x\"");
        assertNotNull(split);
        assertEquals("cat dog", split.text);
        assertEquals(1, filterClauses(split).size());
    }

    @Test
    public void test_optionalTextWithRequiredCondition_parenthesised() {
        final StubProcessorSplitter splitter = new StubProcessorSplitter();
        final Split split = splitter.split("(cat OR dog) label:\"x\"");
        assertNotNull(split);
        assertEquals("cat dog", split.text);
        assertEquals(1, filterClauses(split).size());
    }

    @Test
    public void test_cjkText() {
        final Split split = new StubProcessorSplitter().split("赤い車 label:\"写真\"");
        assertNotNull(split);
        assertEquals("赤い車", split.text);
        assertNotNull(split.conditionFilter);
    }

    // ---- Accepted because core's QueryProcessor has a command for them. The previous
    // pattern-matching implementation refused all three: it recognised only an allowlist of
    // fields, and only bare or quoted values.

    @Test
    public void test_rangeCondition() {
        final StubProcessorSplitter splitter = new StubProcessorSplitter();
        final Split split = splitter.split("cat timestamp:[now/d-1d TO *]");
        assertNotNull(split);
        assertEquals("cat", split.text);
        assertEquals(1, splitter.converted.size());
        assertTrue(splitter.converted.get(0).startsWith("timestamp:"));
    }

    @Test
    public void test_inurlCondition() {
        final Split split = new StubProcessorSplitter().split("cat inurl:\"foo\"");
        assertNotNull(split);
        assertEquals("cat", split.text);
        assertNotNull(split.conditionFilter);
    }

    @Test
    public void test_boostedCondition() {
        final Split split = new StubProcessorSplitter().split("cat label:\"x\"^2");
        assertNotNull(split);
        assertEquals("cat", split.text);
        assertNotNull(split.conditionFilter);
    }

    // ---- Refused ----

    /** Nothing to embed: the vector branch has no query of its own. */
    @Test
    public void test_conditionsWithoutText() {
        assertNull(new StubProcessorSplitter().split("label:\"x\""));
    }

    /** Syntax on the text itself cannot survive being turned into a vector. */
    @Test
    public void test_phraseOnDefaultField() {
        assertNull(new StubProcessorSplitter().split("\"mountain sunset\""));
    }

    /**
     * A wildcard, prefix or fuzzy term on the default field is words the user typed, not a
     * restriction a vector could not keep: a sentence ending in a question mark reads as one in
     * the query syntax, and refusing it skipped the vector branch for exactly the questions it is
     * for. The characters are embedded as typed.
     */
    @Test
    public void test_wildcardOnDefaultField_isTextAsTyped() {
        final Split trailing = new StubProcessorSplitter().split("パスワードを忘れた場合はどうすればいいですか?");
        assertNotNull(trailing);
        assertEquals("パスワードを忘れた場合はどうすればいいですか?", trailing.text);
        assertNull(trailing.conditionFilter);

        final Split inside = new StubProcessorSplitter().split("reset pas?word");
        assertNotNull(inside);
        assertEquals("reset pas?word", inside.text);

        final Split single = new StubProcessorSplitter().split("password?");
        assertNotNull(single);
        assertEquals("password?", single.text);
    }

    @Test
    public void test_prefixOnDefaultField_isTextAsTyped() {
        final Split split = new StubProcessorSplitter().split("reset cat*");
        assertNotNull(split);
        assertEquals("reset cat*", split.text);
        assertNull(split.conditionFilter);
    }

    @Test
    public void test_fuzzyOnDefaultField_isTextAsTyped() {
        final Split split = new StubProcessorSplitter().split("reset~ password");
        assertNotNull(split);
        assertEquals("reset~ password", split.text);
    }

    /** The wildcard characters are kept, and the escape that spelled them is not. */
    @Test
    public void test_wildcardOnDefaultField_dropsTheEscapeBackslash() {
        final Split split = new StubProcessorSplitter().split("a\\*b* c");
        assertNotNull(split);
        assertEquals("a*b* c", split.text);
    }

    /** A wildcard beside a condition: the condition still becomes a filter, the wildcard words are embedded. */
    @Test
    public void test_wildcardAlongsideACondition() {
        final StubProcessorSplitter splitter = new StubProcessorSplitter();
        final Split split = splitter.split("how do I reset it? label:\"docs\" site:example.com");
        assertNotNull(split);
        assertEquals("how do I reset it?", split.text);
        assertEquals(2, filterClauses(split).size());
    }

    // ---- A field core does not know is words of the default field. Every command of core turns a
    // clause on a field outside the search fields into a search of the literal `field:text` on the
    // default field, so `Q: how do I reset` is a question that begins with "Q:how", not a condition
    // on a field called Q. Splitting it as a condition would drop "how" from the embedding and make
    // the vector branch require the literal as a filter.

    @Test
    public void test_unknownField_isWordsOfTheDefaultField() {
        final StubProcessorSplitter splitter = new StubProcessorSplitter();
        final Split split = splitter.split("Q: how do I reset");
        assertNotNull(split);
        assertEquals("Q:how do I reset", split.text);
        assertNull(split.conditionFilter);
        assertTrue(splitter.converted.isEmpty(), splitter.converted.toString());
    }

    @Test
    public void test_unknownField_cjk() {
        final Split split = new StubProcessorSplitter().split("重要:パスワードを忘れた場合は");
        assertNotNull(split);
        assertEquals("重要:パスワードを忘れた場合は", split.text);
        assertNull(split.conditionFilter);
    }

    @Test
    public void test_unknownField_besideAKnownCondition() {
        final StubProcessorSplitter splitter = new StubProcessorSplitter();
        final Split split = splitter.split("Q: how do I reset label:\"docs\"");
        assertNotNull(split);
        assertEquals("Q:how do I reset", split.text);
        assertEquals(1, filterClauses(split).size());
        assertEquals(List.of("label:docs"), splitter.converted);
    }

    @Test
    public void test_unknownField_wildcardPrefixAndFuzzyAreTextAsTyped() {
        assertEquals("Q:ba*", new StubProcessorSplitter().split("Q:ba*").text);
        assertEquals("Q:b?d", new StubProcessorSplitter().split("Q:b?d").text);
        assertEquals("Q:reset~ now", new StubProcessorSplitter().split("Q:reset~ now").text);
    }

    /** What restricts is refused on an unknown field as on the default one. */
    @Test
    public void test_unknownField_restrictiveSyntaxIsRefused() {
        assertNull(new StubProcessorSplitter().split("Q:\"how do\""));
        assertNull(new StubProcessorSplitter().split("foo:[a TO c]"));
        assertNull(new StubProcessorSplitter().split("Q:how^2"));
        assertNull(new StubProcessorSplitter().split("cat -Q:how"));
        assertNull(new StubProcessorSplitter().split("Q:?"));
    }

    /** The fields core searches keep being conditions, including the ones its commands single out. */
    @Test
    public void test_knownFieldsStayConditions() {
        for (final String field : new String[] { "label", "site", "filetype", "inurl", "title", "url", "tag", "timestamp" }) {
            final StubProcessorSplitter splitter = new StubProcessorSplitter();
            final Split split = splitter.split("cat " + field + ":\"x\"");
            assertNotNull(split, field);
            assertEquals(field, "cat", split.text);
            assertEquals(1, filterClauses(split).size(), field);
        }
    }

    /**
     * The set of fields the splitter treats as conditions is core's own, not a copy: every field of
     * the search fields a real configuration declares is split off as a filter, and a field it does
     * not declare is words. The configuration is the real one, initialised from the unit test's
     * FessConfig, so a field added to core's list is covered without touching this test.
     */
    @Test
    public void test_everySearchFieldOfTheRealConfigIsACondition() {
        final QueryFieldConfig realConfig = new QueryFieldConfig();
        realConfig.init();
        assertTrue(realConfig.getSearchFields().length > 10);
        final StubProcessorSplitter splitter = new StubProcessorSplitter() {
            @Override
            protected QueryFieldConfig getQueryFieldConfig() {
                return realConfig;
            }
        };
        for (final String field : realConfig.getSearchFields()) {
            final Split split = splitter.split("cat " + field + ":\"x\"");
            assertNotNull(split, field);
            assertEquals(field, "cat", split.text);
            assertEquals(1, filterClauses(split).size(), field);
        }
        final Split unknown = splitter.split("cat nosuchfield:\"x\"");
        assertNotNull(unknown);
        assertEquals("cat nosuchfield:x", unknown.text);
        assertNull(unknown.conditionFilter);
    }

    /**
     * The commands handle the tag field, {@code site} and {@code inurl} before they look at the
     * search fields, so a configuration that leaves them out of the search fields still has them
     * restrict the keyword branch. The vector branch must restrict by them as well.
     */
    @Test
    public void test_tagSiteAndInurlStayConditionsWhateverTheSearchFields() {
        final QueryFieldConfig bare = new QueryFieldConfig();
        bare.setSearchFields(new String[] { "title", "content" });
        for (final String field : new String[] { "site", "inurl", "tag" }) {
            final StubProcessorSplitter splitter = new StubProcessorSplitter() {
                @Override
                protected QueryFieldConfig getQueryFieldConfig() {
                    return bare;
                }
            };
            final Split split = splitter.split("cat " + field + ":\"x\"");
            assertNotNull(split, field);
            assertEquals(field, "cat", split.text);
            assertEquals(1, filterClauses(split).size(), field);
            // and as a prefix or a wildcard of that field
            final Split prefix = new StubProcessorSplitter() {
                @Override
                protected QueryFieldConfig getQueryFieldConfig() {
                    return bare;
                }
            }.split("cat " + field + ":ab*");
            assertNotNull(prefix, field);
            assertEquals(field, "cat", prefix.text);
            assertEquals(1, filterClauses(prefix).size(), field);
        }
        // a field the configuration does not name and the commands do not single out is words
        final Split other = new StubProcessorSplitter() {
            @Override
            protected QueryFieldConfig getQueryFieldConfig() {
                return bare;
            }
        }.split("cat label:\"x\"");
        assertNotNull(other);
        assertEquals("cat label:x", other.text);
    }

    /** A field name is matched as it is spelled: `Label:` is not the field `label` for core's commands either. */
    @Test
    public void test_fieldNamesAreCaseSensitive() {
        final Split split = new StubProcessorSplitter().split("cat Label:docs");
        assertNotNull(split);
        assertEquals("cat Label:docs", split.text);
        assertNull(split.conditionFilter);
    }

    /** Wildcard characters alone say nothing to embed. */
    @Test
    public void test_wildcardWithoutAWord_isRefused() {
        assertNull(new StubProcessorSplitter().split("?"));
        assertNull(new StubProcessorSplitter().split("*"));
        assertNull(new StubProcessorSplitter().split("??? label:\"x\""));
    }

    /** An excluded wildcard term is as inexpressible as an excluded plain term. */
    @Test
    public void test_negatedWildcard_isRefused() {
        assertNull(new StubProcessorSplitter().split("cat -do?"));
        assertNull(new StubProcessorSplitter().split("cat -do*"));
    }

    /** A boost is syntax on the term, wildcard or not. */
    @Test
    public void test_boostedWildcard_isRefused() {
        assertNull(new StubProcessorSplitter().split("cat*^2"));
    }

    /** A regular expression is not a sentence. */
    @Test
    public void test_regexpOnDefaultField_isRefused() {
        assertNull(new StubProcessorSplitter().split("/ca[tr]/"));
    }

    /** A range on the default field is syntax on the text, as before. */
    @Test
    public void test_rangeOnDefaultField_isRefused() {
        assertNull(new StubProcessorSplitter().split("[a TO c]"));
    }

    @Test
    public void test_boostOnDefaultField() {
        assertNull(new StubProcessorSplitter().split("cat^2"));
    }

    /** A term the user excluded cannot be expressed in an embedding. */
    @Test
    public void test_negatedText() {
        assertNull(new StubProcessorSplitter().split("cat -dog"));
    }

    /**
     * The same, alongside a condition. Worth its own case: this is the shape where an excluded
     * term would otherwise be collected into the text and embedded as something to look for.
     */
    @Test
    public void test_negatedTextAlongsideACondition() {
        assertNull(new StubProcessorSplitter().split("cat -dog label:\"x\""));
    }

    /** `cat OR label:x` has no reading in which the condition is a filter over the text. */
    @Test
    public void test_textOrCondition() {
        assertNull(new StubProcessorSplitter().split("cat OR label:\"x\""));
    }

    /**
     * An optional condition is not a filter: in `+cat dog OR label:x` the label is what the query
     * calls preferable, and filtering on it would drop documents the user asked to keep.
     */
    @Test
    public void test_optionalCondition() {
        assertNull(new StubProcessorSplitter().split("+cat dog OR label:\"x\""));
    }

    /** The conditions in `(cat label:x) OR dog` are not a filter over anything. */
    @Test
    public void test_conditionInsideAnOptionalGroup() {
        assertNull(new StubProcessorSplitter().split("(cat label:\"x\") OR dog"));
    }

    /**
     * An ordering cannot be applied to a score-ordered branch. This matters in practice: a
     * deployment with a default sort configured has `sort:` on every single query.
     */
    @Test
    public void test_sort() {
        assertNull(new StubProcessorSplitter().split("cat sort:filename.asc"));
        assertNull(new StubProcessorSplitter().split("cat label:\"a\" sort:filename.asc"));
    }

    /**
     * `allintitle:` narrows the search to one field, which a whole-document chunk vector cannot
     * express. Running without the narrowing would return exactly the content matches the user
     * asked to exclude.
     */
    @Test
    public void test_allInTitle() {
        // QueryContext resolves the prefix against the configured title field, so this is the one
        // case that reads FessConfig at all.
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexFieldTitle() {
                return "title";
            }
        });
        try {
            assertNull(new StubProcessorSplitter().split("allintitle:cat"));
            assertNull(new StubProcessorSplitter().split("allintitle:cat label:\"x\""));
        } finally {
            ComponentUtil.setFessConfig(null);
        }
    }

    /** A query core itself cannot parse fails core's keyword branch too. */
    @Test
    public void test_unparseable() {
        assertNull(new StubProcessorSplitter().split("cat ("));
    }

    /**
     * A condition core converts to null carries a constraint that would be lost. Searching
     * without it would return documents the user had excluded, so the whole query is refused.
     */
    @Test
    public void test_conditionCoreCannotConvert() {
        final StubProcessorSplitter splitter = new StubProcessorSplitter();
        splitter.convertToNull = true;
        assertNull(splitter.split("cat filetype:\"jpeg\""));
    }

    @Test
    public void test_conditionCoreRejects() {
        final StubProcessorSplitter splitter = new StubProcessorSplitter();
        splitter.convertThrows = true;
        assertNull(splitter.split("cat filetype:\"jpeg\""));
    }

    @Test
    public void test_blank() {
        assertNull(new StubProcessorSplitter().split(null));
        assertNull(new StubProcessorSplitter().split(""));
        assertNull(new StubProcessorSplitter().split("   "));
    }

    private List<QueryBuilder> filterClauses(final Split split) {
        assertNotNull(split.conditionFilter);
        return ((BoolQueryBuilder) split.conditionFilter).filter();
    }
}
