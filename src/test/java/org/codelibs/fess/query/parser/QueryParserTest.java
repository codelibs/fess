/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.query.parser;

import java.util.List;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.codelibs.fess.unit.UnitFessTestCase;

public class QueryParserTest extends UnitFessTestCase {

    public void test_LuceneQueryParser() {
        QueryParser queryParser = new QueryParser();

        Query query = queryParser.createDefaultFilterChain().parse("fess");
        assertEquals(TermQuery.class, query.getClass());
        assertEquals("_default:fess", ((TermQuery) query).getTerm().toString());

        query = queryParser.createDefaultFilterChain().parse("title:fess");
        assertEquals(TermQuery.class, query.getClass());
        assertEquals("title:fess", ((TermQuery) query).getTerm().toString());

        query = queryParser.createDefaultFilterChain().parse("fess*");
        assertEquals(PrefixQuery.class, query.getClass());
        assertEquals("_default:fess", ((PrefixQuery) query).getPrefix().toString());

        query = queryParser.createDefaultFilterChain().parse("fe?s");
        assertEquals(WildcardQuery.class, query.getClass());
        assertEquals("_default:fe?s", ((WildcardQuery) query).getTerm().toString());

        query = queryParser.createDefaultFilterChain().parse("fess~");
        assertEquals(FuzzyQuery.class, query.getClass());
        assertEquals("_default:fess", ((FuzzyQuery) query).getTerm().toString());

        query = queryParser.createDefaultFilterChain().parse("fess^10");
        assertEquals(BoostQuery.class, query.getClass());
        assertEquals("_default:fess", ((BoostQuery) query).getQuery().toString());
        assertEquals(10.0f, ((BoostQuery) query).getBoost());

        query = queryParser.createDefaultFilterChain().parse("\"fess\"");
        assertEquals(PhraseQuery.class, query.getClass());
        assertEquals("_default:fess", ((PhraseQuery) query).getTerms()[0].toString());

        query = queryParser.createDefaultFilterChain().parse("\"fess codelibs\"");
        assertEquals(PhraseQuery.class, query.getClass());
        assertEquals("_default:fess", ((PhraseQuery) query).getTerms()[0].toString());
        assertEquals("_default:codelibs", ((PhraseQuery) query).getTerms()[1].toString());

        query = queryParser.createDefaultFilterChain().parse("fess codelibs");
        assertEquals(BooleanQuery.class, query.getClass());
        List<BooleanClause> clauses = ((BooleanQuery) query).clauses();
        assertEquals(TermQuery.class, clauses.get(0).getQuery().getClass());
        assertEquals("_default:fess", clauses.get(0).getQuery().toString());
        assertEquals(Occur.MUST, clauses.get(0).getOccur());
        assertEquals(TermQuery.class, clauses.get(1).getQuery().getClass());
        assertEquals("_default:codelibs", clauses.get(1).getQuery().toString());
        assertEquals(Occur.MUST, clauses.get(1).getOccur());

        query = queryParser.createDefaultFilterChain().parse("fess AND codelibs");
        assertEquals(BooleanQuery.class, query.getClass());
        clauses = ((BooleanQuery) query).clauses();
        assertEquals(TermQuery.class, clauses.get(0).getQuery().getClass());
        assertEquals("_default:fess", clauses.get(0).getQuery().toString());
        assertEquals(Occur.MUST, clauses.get(0).getOccur());
        assertEquals(TermQuery.class, clauses.get(1).getQuery().getClass());
        assertEquals("_default:codelibs", clauses.get(1).getQuery().toString());
        assertEquals(Occur.MUST, clauses.get(1).getOccur());

        query = queryParser.createDefaultFilterChain().parse("fess OR codelibs");
        assertEquals(BooleanQuery.class, query.getClass());
        clauses = ((BooleanQuery) query).clauses();
        assertEquals(TermQuery.class, clauses.get(0).getQuery().getClass());
        assertEquals("_default:fess", clauses.get(0).getQuery().toString());
        assertEquals(Occur.SHOULD, clauses.get(0).getOccur());
        assertEquals(TermQuery.class, clauses.get(1).getQuery().getClass());
        assertEquals("_default:codelibs", clauses.get(1).getQuery().toString());
        assertEquals(Occur.SHOULD, clauses.get(1).getOccur());

        query = queryParser.createDefaultFilterChain().parse("\"fess\" codelibs");
        assertEquals(BooleanQuery.class, query.getClass());
        clauses = ((BooleanQuery) query).clauses();
        assertEquals(PhraseQuery.class, clauses.get(0).getQuery().getClass());
        assertEquals("_default:fess", ((PhraseQuery) clauses.get(0).getQuery()).getTerms()[0].toString());
        assertEquals(Occur.MUST, clauses.get(0).getOccur());
        assertEquals(TermQuery.class, clauses.get(1).getQuery().getClass());
        assertEquals("_default:codelibs", clauses.get(1).getQuery().toString());
        assertEquals(Occur.MUST, clauses.get(1).getOccur());

    }

}
