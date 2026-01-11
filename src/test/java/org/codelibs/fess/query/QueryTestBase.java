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

import java.math.BigDecimal;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.query.parser.QueryParser;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

/**
 * Base test class for query-related tests that properly initializes FessConfig
 */
public abstract class QueryTestBase extends UnitFessTestCase {

    protected QueryProcessor queryProcessor;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        // Setup FessConfig with proper initialization
        setupBaseFessConfig();

        // Initialize QueryFieldConfig
        QueryFieldConfig queryFieldConfig = new QueryFieldConfig();
        queryFieldConfig.init();
        ComponentUtil.register(queryFieldConfig, "queryFieldConfig");

        // Initialize QueryParser
        QueryParser queryParser = new QueryParser();
        queryParser.init();
        ComponentUtil.register(queryParser, "queryParser");

        // Initialize QueryProcessor if needed
        queryProcessor = new QueryProcessor();
        queryProcessor.init();
        ComponentUtil.register(queryProcessor, "queryProcessor");

        // Call child class specific setup
        setUpChild();
    }

    /**
     * Override this method in child classes to perform additional setup
     */
    protected void setUpChild() throws Exception {
        // Default implementation does nothing
    }

    /**
     * Creates and registers a properly initialized FessConfig
     */
    protected void setupBaseFessConfig() {
        ComponentUtil.setFessConfig(createBaseFessConfig());
    }

    /**
     * Creates a base FessConfig implementation with common fields
     */
    protected FessConfig.SimpleImpl createBaseFessConfig() {
        return new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String get(String key) {
                // Override to return empty string instead of null to avoid NullPointerException
                return "";
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
            public String getIndexFieldUrl() {
                return "url";
            }

            @Override
            public String getIndexFieldSite() {
                return "site";
            }

            @Override
            public String getIndexFieldTimestamp() {
                return "timestamp";
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
            public String getIndexFieldLastModified() {
                return "last_modified";
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
            public String getIndexFieldExpires() {
                return "expires";
            }

            @Override
            public String getIndexFieldParentId() {
                return "parent_id";
            }

            @Override
            public String getIndexFieldPrimaryTerm() {
                return "primary_term";
            }

            @Override
            public String getIndexFieldRole() {
                return "role";
            }

            @Override
            public String getIndexFieldSeqNo() {
                return "seq_no";
            }

            @Override
            public String getIndexFieldVersion() {
                return "version";
            }

            @Override
            public String[] getQueryAdditionalNotAnalyzedFields(String... fields) {
                return fields;
            }

            @Override
            public String getIndexFieldImportantContent() {
                return "important_content";
            }

            @Override
            public BigDecimal getQueryBoostImportantContentAsDecimal() {
                return new BigDecimal("-1.0");
            }

            @Override
            public String getQueryFuzzyPrefixLength() {
                return "0";
            }

            @Override
            public Integer getQueryFuzzyPrefixLengthAsInteger() {
                return 0;
            }

            @Override
            public String getQueryFuzzyTranspositions() {
                return "true";
            }

            @Override
            public String getResponseFieldContentDescription() {
                return "content_description";
            }

            @Override
            public String getResponseFieldContentTitle() {
                return "content_title";
            }

            @Override
            public String getResponseFieldSitePath() {
                return "site_path";
            }

            @Override
            public String getResponseFieldUrlLink() {
                return "url_link";
            }

            @Override
            public BigDecimal getQueryBoostImportantContentLangAsDecimal() {
                return new BigDecimal("-1.0");
            }

            @Override
            public String getQueryBoostImportantContentLang() {
                return "-1.0";
            }

            @Override
            public String getQueryBoostFuzzyMinLength() {
                return "4";
            }

            @Override
            public Integer getQueryBoostFuzzyMinLengthAsInteger() {
                return 4;
            }

            @Override
            public String getQueryBoostFuzzyTitlePrefixLength() {
                return "0";
            }

            @Override
            public Integer getQueryBoostFuzzyTitlePrefixLengthAsInteger() {
                return 0;
            }

            @Override
            public String getQueryBoostFuzzyTitleTranspositions() {
                return "true";
            }

            @Override
            public String getQueryBoostFuzzyContentPrefixLength() {
                return "0";
            }

            @Override
            public Integer getQueryBoostFuzzyContentPrefixLengthAsInteger() {
                return 0;
            }

            @Override
            public String getQueryBoostFuzzyContentTranspositions() {
                return "true";
            }

            @Override
            public String getQueryBoostFuzzyTitleFuzziness() {
                return "AUTO";
            }

            @Override
            public String getQueryBoostFuzzyContentFuzziness() {
                return "AUTO";
            }

            @Override
            public String getQueryBoostFuzzyTitleExpansions() {
                return "10";
            }

            @Override
            public Integer getQueryBoostFuzzyTitleExpansionsAsInteger() {
                return 10;
            }

            @Override
            public String getQueryBoostFuzzyContentExpansions() {
                return "10";
            }

            @Override
            public Integer getQueryBoostFuzzyContentExpansionsAsInteger() {
                return 10;
            }

            @Override
            public String getQueryDefaultLanguages() {
                return "en,ja";
            }

            @Override
            public String getQueryLanguageMapping() {
                return "";
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
            public String getQueryAdditionalFacetFields() {
                return "";
            }

            @Override
            public String getQueryAdditionalSortFields() {
                return "";
            }

            @Override
            public String getQueryAdditionalNotAnalyzedFields() {
                return "";
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
            public String getQueryBoostTitle() {
                return "0.5";
            }

            @Override
            public BigDecimal getQueryBoostTitleAsDecimal() {
                return new BigDecimal("0.5");
            }

            @Override
            public String getQueryBoostContent() {
                return "0.05";
            }

            @Override
            public BigDecimal getQueryBoostContentAsDecimal() {
                return new BigDecimal("0.05");
            }

            @Override
            public String getQueryBoostFuzzyTitle() {
                return "0.01";
            }

            @Override
            public BigDecimal getQueryBoostFuzzyTitleAsDecimal() {
                return new BigDecimal("0.01");
            }

            @Override
            public String getQueryBoostFuzzyContent() {
                return "0.005";
            }

            @Override
            public BigDecimal getQueryBoostFuzzyContentAsDecimal() {
                return new BigDecimal("0.005");
            }

            @Override
            public String getQueryDismaxTieBreaker() {
                return "0.1";
            }

            @Override
            public BigDecimal getQueryDismaxTieBreakerAsDecimal() {
                return new BigDecimal("0.1");
            }

            @Override
            public String getQueryDefaultQueryType() {
                return "bool";
            }

            @Override
            public String getQueryBoostTitleLang() {
                return "1.0";
            }

            @Override
            public BigDecimal getQueryBoostTitleLangAsDecimal() {
                return new BigDecimal("1.0");
            }

            @Override
            public String getQueryBoostContentLang() {
                return "0.5";
            }

            @Override
            public BigDecimal getQueryBoostContentLangAsDecimal() {
                return new BigDecimal("0.5");
            }

            @Override
            public String getQueryPrefixExpansions() {
                return "50";
            }

            @Override
            public Integer getQueryPrefixExpansionsAsInteger() {
                return 50;
            }

            @Override
            public String getQueryPrefixSlop() {
                return "0";
            }

            @Override
            public Integer getQueryPrefixSlopAsInteger() {
                return 0;
            }

            @Override
            public String getQueryFuzzyExpansions() {
                return "50";
            }

            @Override
            public Integer getQueryFuzzyExpansionsAsInteger() {
                return 50;
            }
        };
    }
}