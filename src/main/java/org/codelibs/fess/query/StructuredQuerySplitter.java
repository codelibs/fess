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
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.query.parser.QueryParser;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fesen.opensearch.index.query.BoolQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;

/**
 * Splits an assembled query into the plain text to embed and the structured conditions
 * {@code QueryStringBuilder} folded into it.
 *
 * <p>{@code QueryStringBuilder} appends facet and label selections to the query string as
 * {@code label:"x"}, so a search that started as plain words stops being plain the moment the
 * user narrows it. A vector branch cannot embed that string as it stands - {@code label:"x"} is
 * noise to an embedding model, and dropping it would return documents the user had just
 * excluded. This splitter recovers the two halves, so the free text can be embedded on its own
 * and the conditions can be applied as real filters.</p>
 *
 * <p>The split runs on the parse tree, not on the query string: the string is handed to
 * {@link QueryParser} and every field-qualified clause is handed back to {@link QueryProcessor},
 * so a condition becomes exactly the filter the keyword branch would build for it - a prefix
 * query for {@code site:}, a wildcard on the url field for {@code inurl:}, a range for
 * {@code timestamp:[..]}, a term or a match phrase depending on whether the field is analyzed.
 * Pattern-matching the string instead would have to re-implement all of that, and would drift
 * the moment either side changed.</p>
 *
 * <p>Free text is a bare term, and also a wildcard, prefix or fuzzy term on the default field:
 * the question mark that closes a question, or an asterisk typed in a sentence, is read as query
 * syntax by the keyword branch, but a vector has no syntax to misread it with, so the characters
 * are embedded as typed. Syntax that restricts - a phrase, an excluded word, a boosted word, a
 * range - is not text.</p>
 *
 * <p>Whatever cannot be split this way yields {@code null}, which means the caller should leave
 * the query alone - for the vector branch that means skipping it. A condition this class can see
 * is never dropped to make a query splittable: searching without it would return documents the
 * user had excluded. What the parser itself discards - {@code label:""}, whose value analyzes to
 * no tokens, never reaches the tree - is discarded from the keyword branch too, so the two
 * branches still filter alike.</p>
 */
public class StructuredQuerySplitter {

    private static final Logger logger = LogManager.getLogger(StructuredQuerySplitter.class);

    /** The pseudo-field that carries an ordering, as {@code TermQueryCommand} spells it. */
    protected static final String SORT_FIELD = "sort";

    /** A letter or a digit: what a text needs to have something to embed. */
    private static final Pattern WORD_CHAR = Pattern.compile("[\\p{L}\\p{N}]");

    /** A backslash and the character it escapes. */
    private static final Pattern ESCAPE = Pattern.compile("\\\\(.)");

    /** How a parse-tree node relates to the text/condition split. */
    protected enum Kind {
        /** Free text on the default field. */
        TEXT,
        /** A field-qualified clause the {@link QueryProcessor} can turn into a filter. */
        CONDITION,
        /** A boolean node holding both. */
        MIXED,
        /** Not splittable. */
        REJECT;
    }

    /**
     * Constructs a new StructuredQuerySplitter instance.
     */
    public StructuredQuerySplitter() {
        // Default constructor
    }

    /** The free text and the conditions recovered from a query. */
    public static final class Split {

        /** The free text to embed. Never blank. */
        public final String text;

        /** The conditions as a filter clause, or {@code null} when the query carried none. */
        public final QueryBuilder conditionFilter;

        Split(final String text, final QueryBuilder conditionFilter) {
            this.text = text;
            this.conditionFilter = conditionFilter;
        }
    }

    /**
     * Splits an assembled query.
     *
     * @param query the assembled query string
     * @param searchRequestType the type of the search request the query belongs to, so that a
     *        condition matches what it matches in the keyword branch of that request, e.g. a
     *        condition on the tag field matches only the tags that type can see (all of them for
     *        the admin search)
     * @return the split, or {@code null} when the query is not safe to split
     */
    public Split split(final String query, final SearchRequestType searchRequestType) {
        if (StringUtil.isBlank(query)) {
            return null;
        }
        final QueryContext context;
        final Query parsed;
        try {
            context = createQueryContext(query);
            context.setSearchRequestType(searchRequestType);
            if (context.getDefaultField() != null) {
                // `allintitle:` / `allinurl:` narrow the search to one field. The vector branch
                // has no way to honour that -- a chunk vector covers the document's text, not a
                // field -- and running without the narrowing would return the content matches the
                // user excluded by asking for titles only.
                return null;
            }
            // The context's string rather than the raw one, because its constructor is what
            // strips those two prefixes. This is not quite the string the keyword branch parses:
            // QueryHelper appends its configured additionalQuery before building its own
            // context, and the vector branch never sees that -- the same blind spot it has
            // had all along, embedding the query text and applying no additionalQuery either.
            parsed = getQueryParser().parse(context.getQueryString());
        } catch (final RuntimeException e) {
            // A query that cannot be parsed fails the keyword branch too, so there is nothing
            // useful the vector branch could do with it. Logged because everything else that can
            // go wrong here -- an unavailable component, a broken FessConfig -- arrives as the
            // same silent "not splittable", and a vector branch that stops firing is otherwise
            // undiagnosable.
            if (logger.isDebugEnabled()) {
                logger.debug("Not splittable, failed to parse: {}", query, e);
            }
            return null;
        }
        if (parsed == null) {
            return null;
        }
        final List<String> texts = new ArrayList<>();
        final BoolQueryBuilder conditionFilter = QueryBuilders.boolQuery();
        if (!walk(parsed, Occur.MUST, context, texts, conditionFilter)) {
            return null;
        }
        final String text = String.join(" ", texts).trim();
        if (StringUtil.isBlank(text)) {
            // Conditions with no text of their own: there is nothing to embed.
            return null;
        }
        return new Split(text, conditionFilter.hasClauses() ? conditionFilter : null);
    }

    /**
     * Walks one parse-tree node, appending its text to {@code texts} and its conditions to
     * {@code conditionFilter}.
     *
     * @param query the node
     * @param occur how the node is combined into its parent
     * @param context the throwaway context conditions are converted against
     * @param texts collects the free text
     * @param conditionFilter collects the conditions
     * @return false when the node is not splittable
     */
    protected boolean walk(final Query query, final Occur occur, final QueryContext context, final List<String> texts,
            final BoolQueryBuilder conditionFilter) {
        switch (classify(query)) {
        case TEXT:
            // A negated term cannot be expressed in an embedding, so it is not splittable.
            return occur != Occur.MUST_NOT && collectText(query, texts);
        case CONDITION:
            return addCondition(query, occur, context, conditionFilter);
        case MIXED:
            // A node holding both can only be pulled apart where it is required: in
            // `(cat label:x) OR dog` the conditions are not a filter over anything.
            if (occur != Occur.MUST) {
                return false;
            }
            for (final BooleanClause clause : ((BooleanQuery) query).clauses()) {
                if (!walk(clause.query(), clause.occur(), context, texts, conditionFilter)) {
                    return false;
                }
            }
            return true;
        default:
            return false;
        }
    }

    /**
     * Classifies a parse-tree node as text, condition, both or neither.
     *
     * @param query the node
     * @return the kind
     */
    protected Kind classify(final Query query) {
        if (query instanceof final BooleanQuery booleanQuery) {
            boolean text = false;
            boolean condition = false;
            for (final BooleanClause clause : booleanQuery.clauses()) {
                switch (classify(clause.query())) {
                case TEXT:
                    text = true;
                    break;
                case CONDITION:
                    condition = true;
                    break;
                case MIXED:
                    text = true;
                    condition = true;
                    break;
                default:
                    return Kind.REJECT;
                }
            }
            if (text) {
                return condition ? Kind.MIXED : Kind.TEXT;
            }
            return condition ? Kind.CONDITION : Kind.REJECT;
        }
        if (query instanceof final BoostQuery boostQuery) {
            // `label:x^2` is a condition that can be boosted; `cat^2` is syntax on the text itself,
            // and the boost would be lost the moment the text became a vector.
            return classify(boostQuery.getQuery()) == Kind.CONDITION ? Kind.CONDITION : Kind.REJECT;
        }
        final String field = extractField(query);
        if (field == null) {
            return Kind.REJECT;
        }
        if (Constants.DEFAULT_FIELD.equals(field)) {
            // A bare term is text, and so is a wildcard, prefix or fuzzy term: the characters that
            // make it one -- the question mark ending a question, an asterisk -- are what a person
            // types in a sentence, and a vector has no syntax to misread them with. A phrase or a
            // range is query syntax, and dropping it would search for something the user did not
            // ask for.
            return wordsOf(query) != null ? Kind.TEXT : Kind.REJECT;
        }
        if (SORT_FIELD.equals(field)) {
            // The vector branch cannot apply an ordering, and interleaving score-ordered hits
            // into an already-sorted result set makes the order meaningless. The
            // TermQueryCommand converts this clause to null, which addCondition would refuse
            // anyway; stating the rule here keeps it true regardless of what that returns, and
            // it matters because QueryStringBuilder appends `sort:` to every query on a
            // deployment with a default sort configured.
            return Kind.REJECT;
        }
        if (!isSearchField(field)) {
            // Core's commands read a clause on a field outside the search fields as words of the
            // default field, `field:text` and all -- `Q: how do I reset` is a question, and `Q:how`
            // is its first word. Treating it as a condition would drop that word from the
            // embedding and make the vector branch demand the literal as a filter.
            return wordsOf(query) != null ? Kind.TEXT : Kind.REJECT;
        }
        return Kind.CONDITION;
    }

    /**
     * Returns whether core's commands treat the field as a field of its own, as they decide it.
     *
     * <p>The commands single out three fields before they look at the search fields: the tag field
     * (restricted to the tags the caller can see), {@code site} and {@code inurl}. They stay
     * conditions here whatever the configured search fields are, so that a configuration which
     * leaves one out cannot turn a restriction the keyword branch applies into words.</p>
     *
     * @param field the field name
     * @return true when the field is one of the search fields, or one the commands handle first
     */
    protected boolean isSearchField(final String field) {
        if (QueryFieldConfig.SITE_FIELD.equals(field) || QueryFieldConfig.INURL_FIELD.equals(field)
                || ComponentUtil.getFessConfig().getIndexFieldTag().equals(field)) {
            return true;
        }
        final QueryFieldConfig config = getQueryFieldConfig();
        return config.searchFieldSet != null && config.searchFieldSet.contains(field);
    }

    /**
     * Reads the field a leaf node queries.
     *
     * @param query the node
     * @return the field name, or {@code null} when the node has none
     */
    protected String extractField(final Query query) {
        if (query instanceof final TermQuery termQuery) {
            return termQuery.getTerm().field();
        }
        if (query instanceof final PhraseQuery phraseQuery) {
            final Term[] terms = phraseQuery.getTerms();
            return terms.length == 0 ? null : terms[0].field();
        }
        if (query instanceof final MultiTermQuery multiTermQuery) {
            // Covers the prefix, wildcard, regexp, fuzzy and range queries the classic parser
            // emits. Whether there is a command for one of them is not decided here: a regexp
            // value has none, and addCondition below refuses the query when the conversion
            // throws.
            return multiTermQuery.getField();
        }
        return null;
    }

    /**
     * Collects the text of a node already classified as {@link Kind#TEXT}.
     *
     * @param query the node
     * @param texts collects the free text
     * @return false when the node is not splittable
     */
    protected boolean collectText(final Query query, final List<String> texts) {
        if (query instanceof final BooleanQuery booleanQuery) {
            for (final BooleanClause clause : booleanQuery.clauses()) {
                if (clause.occur() != Occur.MUST && clause.occur() != Occur.SHOULD) {
                    return false;
                }
                if (!collectText(clause.query(), texts)) {
                    return false;
                }
            }
            return true;
        }
        final String words = wordsOf(query);
        if (words == null) {
            // Restates the rule classify() applies to a leaf on the default field, for the node
            // shapes it would already have refused. Nothing else can become text.
            return false;
        }
        if (StringUtil.isNotBlank(words)) {
            texts.add(words);
        }
        return true;
    }

    /**
     * Reads the words a leaf node on the default field was typed as.
     *
     * <p>A term is its text. A wildcard keeps the characters that made it one, minus the
     * backslashes that spelled an escape; a prefix gets its asterisk back, and a fuzzy term its
     * tilde. A wildcard, prefix or fuzzy term with no letter or digit in it -- {@code ?},
     * {@code *} -- says nothing a vector could be asked about.</p>
     *
     * @param query the leaf node
     * @return the words, or {@code null} when the node is not text
     */
    protected String wordsOf(final Query query) {
        if (query instanceof final TermQuery termQuery) {
            return qualified(termQuery.getTerm(), termQuery.getTerm().text());
        }
        final Term term;
        final String words;
        if (query instanceof final WildcardQuery wildcardQuery) {
            term = wildcardQuery.getTerm();
            words = ESCAPE.matcher(term.text()).replaceAll("$1");
        } else if (query instanceof final PrefixQuery prefixQuery) {
            term = prefixQuery.getPrefix();
            words = term.text() + "*";
        } else if (query instanceof final FuzzyQuery fuzzyQuery) {
            term = fuzzyQuery.getTerm();
            words = term.text() + "~";
        } else {
            return null;
        }
        return WORD_CHAR.matcher(words).find() ? qualified(term, words) : null;
    }

    /**
     * Puts back the field name core's commands keep in the words of a field they do not know.
     *
     * @param term the term the words belong to
     * @param words the words
     * @return the words, as {@code field:words} for a field other than the default one
     */
    private String qualified(final Term term, final String words) {
        return Constants.DEFAULT_FIELD.equals(term.field()) ? words : term.field() + ":" + words;
    }

    /**
     * Converts a node already classified as {@link Kind#CONDITION} and adds it to the filter.
     *
     * @param query the node
     * @param occur how the node is combined into its parent
     * @param context the throwaway context the conversion runs against
     * @param conditionFilter collects the conditions
     * @return false when the node is not splittable
     */
    protected boolean addCondition(final Query query, final Occur occur, final QueryContext context,
            final BoolQueryBuilder conditionFilter) {
        final QueryBuilder builder;
        try {
            builder = getQueryProcessor().execute(context, query, 1.0f);
        } catch (final RuntimeException e) {
            // An unsupported or invalid clause -- a regexp value, for which there is no command --
            // which the keyword branch turns into an error page, so the vector branch has
            // nothing to add. Logged for the same reason as the parse failure above.
            if (logger.isDebugEnabled()) {
                logger.debug("Not splittable, failed to convert: {}", query, e);
            }
            return false;
        }
        if (builder == null) {
            // Core returns null for a clause that constrains nothing it can express as a query.
            // Only `sort:` and an empty boolean do so today, and classify() refuses both before
            // they reach here; this guards the same rule at the seam, since a null would
            // otherwise go straight into the filter.
            return false;
        }
        if (occur == Occur.MUST) {
            conditionFilter.filter(builder);
            return true;
        }
        if (occur == Occur.MUST_NOT) {
            conditionFilter.mustNot(builder);
            return true;
        }
        // An optional condition -- `+cat dog OR label:x` -- is not a filter: applying it would
        // exclude documents the query says are merely less preferred.
        return false;
    }

    /**
     * Creates the context conditions are converted against. It is a throwaway: it is built with
     * {@code isQuery=false} so the conversion's field logs and highlight terms are discarded
     * rather than written onto the live request, where they would show up as highlights for a
     * query the keyword branch never ran.
     *
     * @param query the assembled query string
     * @return the context
     */
    protected QueryContext createQueryContext(final String query) {
        return new QueryContext(query, false);
    }

    /**
     * Returns the query parser.
     *
     * @return the parser
     */
    protected QueryParser getQueryParser() {
        return ComponentUtil.getQueryParser();
    }

    /**
     * Returns the field configuration.
     *
     * @return the configuration
     */
    protected QueryFieldConfig getQueryFieldConfig() {
        return ComponentUtil.getQueryFieldConfig();
    }

    /**
     * Returns the query processor.
     *
     * @return the processor
     */
    protected QueryProcessor getQueryProcessor() {
        return ComponentUtil.getQueryProcessor();
    }
}
