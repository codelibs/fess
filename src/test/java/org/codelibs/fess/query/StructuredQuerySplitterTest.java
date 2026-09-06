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
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;

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

    @Test
    public void test_prefixOnDefaultField() {
        assertNull(new StubProcessorSplitter().split("cat*"));
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
