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

import org.apache.lucene.search.Query;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.query.parser.QueryParser;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.MatchPhraseQueryBuilder;
import org.opensearch.index.query.PrefixQueryBuilder;
import org.opensearch.index.query.QueryBuilder;

public class QueryCommandTest extends UnitFessTestCase {
    private QueryCommand queryCommand;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Setup FessConfig with proper initialization
        FessConfig.SimpleImpl fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            {
                // Force initialize the ObjectiveConfig's prop field using reflection
                try {
                    java.lang.reflect.Field propField = org.lastaflute.core.direction.ObjectiveConfig.class.getDeclaredField("prop");
                    propField.setAccessible(true);
                    propField.set(this, new org.dbflute.helper.jprop.ObjectiveProperties("test"));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to initialize prop field", e);
                }
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
            public String getIndexFieldId() {
                return "_id";
            }

            @Override
            public String getIndexFieldDocId() {
                return "doc_id";
            }

            @Override
            public String getIndexFieldBoost() {
                return "boost";
            }

            @Override
            public String getIndexFieldContentLength() {
                return "content_length";
            }

            @Override
            public String getIndexFieldHost() {
                return "host";
            }

            @Override
            public String getIndexFieldSite() {
                return "site";
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
            public String getIndexFieldMimetype() {
                return "mimetype";
            }

            @Override
            public String getIndexFieldFiletype() {
                return "filetype";
            }

            @Override
            public String getIndexFieldFilename() {
                return "filename";
            }

            @Override
            public String getIndexFieldCreated() {
                return "created";
            }

            @Override
            public String getIndexFieldDigest() {
                return "digest";
            }

            @Override
            public String getIndexFieldUrl() {
                return "url";
            }

            @Override
            public String getIndexFieldThumbnail() {
                return "thumbnail";
            }

            @Override
            public String getIndexFieldClickCount() {
                return "click_count";
            }

            @Override
            public String getIndexFieldFavoriteCount() {
                return "favorite_count";
            }

            @Override
            public String getIndexFieldConfigId() {
                return "config_id";
            }

            @Override
            public String getIndexFieldLang() {
                return "lang";
            }

            @Override
            public String getIndexFieldHasCache() {
                return "has_cache";
            }

            @Override
            public String getQueryAdditionalDefaultFields() {
                return "";
            }

            @Override
            public String getQueryAdditionalSearchFields() {
                return "";
            }

            @Override
            public String getQueryLanguageMapping() {
                return "en:en,ja:ja";
            }

            @Override
            public String getQueryDefaultLanguages() {
                return "en,ja";
            }

            @Override
            public String[] getQueryAdditionalResponseFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalScrollResponseFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalCacheResponseFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalHighlightedFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalSearchFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalFacetFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalSortFields(String... fields) {
                return fields;
            }

            @Override
            public String[] getQueryAdditionalApiResponseFields(String... fields) {
                return fields;
            }

            @Override
            public String getIndexFieldCache() {
                return "cache";
            }

            @Override
            public String getIndexFieldLabel() {
                return "label";
            }

            @Override
            public String getIndexFieldSegment() {
                return "segment";
            }

            @Override
            public String get(String key) {
                // Return empty string instead of null
                return "";
            }
        };

        ComponentUtil.setFessConfig(fessConfig);

        // Initialize QueryFieldConfig
        QueryFieldConfig queryFieldConfig = new QueryFieldConfig();
        queryFieldConfig.init();
        ComponentUtil.register(queryFieldConfig, "queryFieldConfig");

        // Initialize QueryParser
        QueryParser queryParser = new QueryParser();
        queryParser.init();
        ComponentUtil.register(queryParser, "queryParser");

        // Initialize QueryProcessor
        QueryProcessor queryProcessor = new QueryProcessor();
        queryProcessor.init();
        ComponentUtil.register(queryProcessor, "queryProcessor");

        queryCommand = new QueryCommand() {
            @Override
            public QueryBuilder execute(QueryContext context, Query query, float boost) {
                return null;
            }

            @Override
            protected String getQueryClassName() {
                return null;
            }
        };
    }

    public void test_buildMatchPhraseQuery() {
        assertQueryBuilder("test", "", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("test", "test", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("test", "a", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("test", "あ", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("test", "ア", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("test", "亜", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("test", "아", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("title", "test", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("title", "a", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("title", "あ", PrefixQueryBuilder.class);
        assertQueryBuilder("title", "ああ", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("title", "ア", PrefixQueryBuilder.class);
        assertQueryBuilder("title", "アア", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("title", "亜", PrefixQueryBuilder.class);
        assertQueryBuilder("title", "亜亜", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("title", "아", PrefixQueryBuilder.class);
        assertQueryBuilder("title", "아아", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("content", "test", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("content", "a", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("content", "あ", PrefixQueryBuilder.class);
        assertQueryBuilder("content", "ああ", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("content", "ア", PrefixQueryBuilder.class);
        assertQueryBuilder("content", "アア", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("content", "亜", PrefixQueryBuilder.class);
        assertQueryBuilder("content", "亜亜", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("content", "아", PrefixQueryBuilder.class);
        assertQueryBuilder("content", "아아", MatchPhraseQueryBuilder.class);
    }

    private void assertQueryBuilder(String field, String value, Class<?> clazz) {
        QueryBuilder queryBuilder = queryCommand.buildMatchPhraseQuery(field, value);
        assertEquals(clazz, queryBuilder.getClass());
    }

}
