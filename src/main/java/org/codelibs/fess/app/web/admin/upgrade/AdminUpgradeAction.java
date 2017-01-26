/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.upgrade;

import static org.codelibs.core.stream.StreamUtil.split;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.codelibs.core.exception.ResourceNotFoundRuntimeException;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.elasticsearch.runner.net.Curl;
import org.codelibs.elasticsearch.runner.net.CurlResponse;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.config.exbhv.DataConfigBhv;
import org.codelibs.fess.es.config.exbhv.DataConfigToRoleBhv;
import org.codelibs.fess.es.config.exbhv.ElevateWordBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigToRoleBhv;
import org.codelibs.fess.es.config.exbhv.LabelToRoleBhv;
import org.codelibs.fess.es.config.exbhv.LabelTypeBhv;
import org.codelibs.fess.es.config.exbhv.RoleTypeBhv;
import org.codelibs.fess.es.config.exbhv.WebConfigBhv;
import org.codelibs.fess.es.config.exbhv.WebConfigToRoleBhv;
import org.codelibs.fess.es.config.exentity.ElevateWord;
import org.codelibs.fess.es.user.exbhv.RoleBhv;
import org.codelibs.fess.es.user.exentity.Role;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsResponse.FieldMappingMetaData;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminUpgradeAction extends FessAdminAction {

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LoggerFactory.getLogger(AdminUpgradeAction.class);

    private static final String VERSION_10_2 = "10.2";

    private static final String VERSION_10_1 = "10.1";

    private static final String VERSION_10_0 = "10.0";

    // ===================================================================================
    //                                                                           Attribute
    //
    @Resource
    protected FessConfig fessConfig;

    @Resource
    protected RoleBhv roleBhv;

    @Resource
    protected RoleTypeBhv roleTypeBhv;

    @Resource
    protected LabelToRoleBhv labelToRoleBhv;

    @Resource
    protected LabelTypeBhv labelTypeBhv;

    @Resource
    protected WebConfigToRoleBhv webConfigToRoleBhv;

    @Resource
    protected WebConfigBhv webConfigBhv;

    @Resource
    protected FileConfigToRoleBhv fileConfigToRoleBhv;

    @Resource
    protected FileConfigBhv fileConfigBhv;

    @Resource
    protected DataConfigToRoleBhv dataConfigToRoleBhv;

    @Resource
    protected DataConfigBhv dataConfigBhv;

    @Resource
    protected ElevateWordBhv elevateWordBhv;

    @Resource
    protected FessEsClient fessEsClient;

    @Resource
    private ScheduledJobService scheduledJobService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameUpgrade()));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    @Execute
    public HtmlResponse index() {
        saveToken();
        return asIndexHtml();
    }

    private HtmlResponse asIndexHtml() {
        return asHtml(path_AdminUpgrade_AdminUpgradeJsp).useForm(UpgradeForm.class);
    }

    @Execute
    public HtmlResponse upgradeFrom(final UpgradeForm form) {
        validate(form, messages -> {}, () -> {
            return asIndexHtml();
        });
        verifyToken(() -> asIndexHtml());

        if (VERSION_10_2.equals(form.targetVersion)) {
            try {
                upgradeFrom10_2();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessUpgradeFrom(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_10_1, e.getLocalizedMessage()));
            }
        } else if (VERSION_10_1.equals(form.targetVersion)) {
            try {
                upgradeFrom10_1();
                upgradeFrom10_2();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessUpgradeFrom(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_10_1, e.getLocalizedMessage()));
            }
        } else if (VERSION_10_0.equals(form.targetVersion)) {
            try {
                upgradeFrom10_0();
                upgradeFrom10_1();
                upgradeFrom10_2();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessUpgradeFrom(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_10_0, e.getLocalizedMessage()));
            }
        } else {
            saveError(messages -> messages.addErrorsUnknownVersionForUpgrade(GLOBAL));
        }
        return redirect(getClass());
    }

    private void upgradeFromAll() {
        final IndicesAdminClient indicesClient = fessEsClient.admin().indices();
        final String crawlerIndex = fessConfig.getIndexDocumentCrawlerIndex();

        // .crawler
        if (existsIndex(indicesClient, crawlerIndex, IndicesOptions.fromOptions(false, true, true, true))) {
            deleteIndex(indicesClient, crawlerIndex, response -> {});
        }
    }

    private void upgradeFrom10_2() {
        final IndicesAdminClient indicesClient = fessEsClient.admin().indices();
        final String configIndex = ".fess_config";
        final String updateIndex = fessConfig.getIndexDocumentUpdateIndex();
        final String searchIndex = fessConfig.getIndexDocumentSearchIndex();

        // update mapping
        addMapping(indicesClient, updateIndex, "access_token", "fess_indices/.fess_config");
        addMapping(indicesClient, updateIndex, "thumbnail_queue", "fess_indices/.fess_config");

        addFieldMapping(indicesClient, updateIndex, "doc", "filename",
                "{\"properties\":{\"filename\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, searchIndex, "doc", "filename",
                "{\"properties\":{\"filename\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(
                indicesClient,
                updateIndex,
                "doc",
                "important_content",
                "{\"properties\":{\"important_content\":{\"type\":\"langstring\",\"lang_field\":\"lang\",\"lang_base_name\":\"content\",\"index\":\"no\"}}}");
        addFieldMapping(
                indicesClient,
                searchIndex,
                "doc",
                "important_content",
                "{\"properties\":{\"important_content\":{\"type\":\"langstring\",\"lang_field\":\"lang\",\"lang_base_name\":\"content\",\"index\":\"no\"}}}");
        addFieldMapping(indicesClient, configIndex, "job_log", "lastUpdated", "{\"properties\":{\"lastUpdated\":{\"type\":\"long\"}}}");

        // data migration
        addData(configIndex,
                "scheduled_job",
                "thumbnail_generate",
                "{\"name\":\"Thumbnail Generator\",\"target\":\"all\",\"cronExpression\":\"* * * * *\",\"scriptType\":\"groovy\",\"scriptData\":\"return container.getComponent(\\\"generateThumbnailJob\\\").execute();\",\"jobLogging\":false,\"crawler\":false,\"available\":true,\"sortOrder\":7,\"createdBy\":\"system\",\"createdTime\":0,\"updatedBy\":\"system\",\"updatedTime\":0}");
        addData(configIndex,
                "scheduled_job",
                "ping_es",
                "{\"name\":\"Ping Elasticsearch\",\"target\":\"all\",\"cronExpression\":\"* * * * *\",\"scriptType\":\"groovy\",\"scriptData\":\"return container.getComponent(\\\"pingEsJob\\\").execute();\",\"jobLogging\":false,\"crawler\":false,\"available\":true,\"sortOrder\":8,\"createdBy\":\"system\",\"createdTime\":0,\"updatedBy\":\"system\",\"updatedTime\":0}");

    }

    private void upgradeFrom10_1() {
        final IndicesAdminClient indicesClient = fessEsClient.admin().indices();
        final String indexConfigPath = "fess_indices";
        final String configIndex = ".fess_config";
        final String logIndex = "fess_log";
        final String updateIndex = fessConfig.getIndexDocumentUpdateIndex();
        final String searchIndex = fessConfig.getIndexDocumentSearchIndex();
        final String oldDocIndex = "fess";
        final String suggestAnalyzerIndex = ".suggest.analyzer";
        final String suggestIndex = fessConfig.getIndexDocumentSuggestIndex();

        // file
        uploadResource(indexConfigPath, oldDocIndex, "ja/mapping.txt");
        uploadResource(indexConfigPath, oldDocIndex, "ar/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "ca/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "cs/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "da/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "de/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "el/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "es/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "fa/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "fi/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "fr/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "hi/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "hu/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "id/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "it/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "lt/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "lv/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "nl/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "no/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "pt/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "ro/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "ru/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "sv/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "th/protwords.txt");
        uploadResource(indexConfigPath, oldDocIndex, "tr/protwords.txt");

        // update mapping
        addFieldMapping(indicesClient, configIndex, "path_mapping", "userAgent",
                "{\"properties\":{\"userAgent\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, logIndex, "search_log", "languages",
                "{\"properties\":{\"languages\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");

        // update settings
        // suggest
        indicesClient.close(new CloseIndexRequest(suggestAnalyzerIndex));
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_ja",
                "{\"type\":\"custom\",\"tokenizer\":\"fess_japanese_normal\",\"filter\":[\"reading_form\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_ja",
                "{\"type\":\"custom\",\"tokenizer\":\"fess_japanese_normal\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_ja",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_ja",
                "{\"type\":\"custom\",\"tokenizer\":\"fess_japanese_normal\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"pos_filter\",\"content_length_filter\",\"limit_token_count_filter\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_en",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_en",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_en",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_en",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"english_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_ar",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_ar",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_ar",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\",\"arabic_normalization\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_ar",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"arabic_stop\",\"arabic_normalization\",\"arabic_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_ca",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_ca",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_ca",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\",\"catalan_elision\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_ca",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"catalan_elision\",\"catalan_stop\",\"catalan_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_cs",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_cs",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_cs",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_cs",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"czech_stop\",\"czech_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_da",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_da",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_da",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_da",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"danish_stop\",\"danish_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_nl",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_nl",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_nl",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\",\"dutch_override\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_nl",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"dutch_stop\",\"dutch_keywords\",\"dutch_override\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_fi",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_fi",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_fi",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_fi",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"finnish_stop\",\"finnish_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_fr",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_fr",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_fr",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\",\"french_elision\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_fr",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"french_elision\",\"french_stop\",\"french_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_de",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_de",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_de",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\",\"german_normalization\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_de",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"german_stop\",\"german_keywords\",\"german_normalization\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_el",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_el",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_el",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"greek_lowercase\",\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_el",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"greek_stop\",\"greek_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_hu",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_hu",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_hu",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"hungarian_stop\",\"hungarian_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_id",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_id",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_id",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_id",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"indonesian_stop\",\"indonesian_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_it",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_it",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_it",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\",\"italian_elision\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_it",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"italian_elision\",\"italian_stop\",\"italian_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_lv",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_lv",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_lv",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_lv",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"latvian_stop\",\"latvian_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_lt",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_lt",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_lt",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_lt",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"lithuanian_stop\",\"lithuanian_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_no",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_no",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_no",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_no",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"norwegian_stop\",\"norwegian_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_fa",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_fa",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "normalize_analyzer_fa",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\",\"arabic_normalization\",\"persian_normalization\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_fa",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"arabic_normalization\",\"persian_normalization\",\"persian_stop\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_pt",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_pt",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_pt",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_pt",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"portuguese_stop\",\"portuguese_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_ro",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_ro",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_ro",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_ro",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"romanian_stop\",\"romanian_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_ru",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_ru",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_ru",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_ru",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"russian_stop\",\"russian_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_es",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_es",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_es",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_es",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"spanish_stop\",\"spanish_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_sv",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_sv",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_sv",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_sv",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"swedish_stop\",\"swedish_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_tr",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_tr",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\"}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "normalize_analyzer_tr",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\",\"apostrophe\",\"turkish_lowercase\",\"turkish_stemmer\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_tr",
                "{\"type\":\"custom\",\"tokenizer\":\"standard\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"apostrophe\",\"turkish_lowercase\",\"turkish_stop\",\"turkish_keywords\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_analyzer_th",
                "{\"type\":\"custom\",\"tokenizer\":\"thai\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "reading_term_analyzer_th",
                "{\"type\":\"custom\",\"tokenizer\":\"thai\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "analyzer", "normalize_analyzer_th",
                "{\"type\":\"custom\",\"tokenizer\":\"keyword\",\"char_filter\":[\"mapping_char\"],\"filter\":[\"lowercase\"]}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "analyzer",
                "contents_analyzer_th",
                "{\"type\":\"custom\",\"tokenizer\":\"thai\",\"filter\":[\"lowercase\",\"stopword_en_filter\",\"content_length_filter\",\"limit_token_count_filter\",\"thai_stop\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "stemmer_en_filter", "{\"type\":\"stemmer\",\"name\":\"english\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "arabic_stop", "{\"type\":\"stop\",\"stopwords\":\"_arabic_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "arabic_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}ar/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "arabic_stemmer", "{\"type\":\"stemmer\",\"language\":\"arabic\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "catalan_elision",
                "{\"type\":\"elision\",\"articles\":[\"d\",\"l\",\"m\",\"n\",\"s\",\"t\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "catalan_stop", "{\"type\":\"stop\",\"stopwords\":\"_catalan_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "catalan_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}ca/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "catalan_stemmer", "{\"type\":\"stemmer\",\"language\":\"catalan\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "czech_stop", "{\"type\":\"stop\",\"stopwords\":\"_czech_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "czech_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}cs/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "czech_stemmer", "{\"type\":\"stemmer\",\"language\":\"czech\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "danish_stop", "{\"type\":\"stop\",\"stopwords\":\"_danish_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "danish_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}da/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "danish_stemmer", "{\"type\":\"stemmer\",\"language\":\"danish\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "dutch_stop", "{\"type\":\"stop\",\"stopwords\":\"_dutch_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "dutch_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}nl/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "dutch_stemmer", "{\"type\":\"stemmer\",\"language\":\"dutch\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "dutch_override",
                "{\"type\":\"stemmer_override\",\"rules\":[\"fiets=>fiets\",\"bromfiets=>bromfiets\",\"ei=>eier\",\"kind=>kinder\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "english_keywords",
                "{\"type\":\"keyword_marker\",\"keywords\":[\"hello\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "finnish_stop", "{\"type\":\"stop\",\"stopwords\":\"_finnish_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "finnish_keywords",
                "{\"type\":\"keyword_marker\",\"keywords\":[\"Hei\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "finnish_stemmer", "{\"type\":\"stemmer\",\"language\":\"finnish\"}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "filter",
                "french_elision",
                "{\"type\":\"elision\",\"articles_case\":true,\"articles\":[\"l\",\"m\",\"t\",\"qu\",\"n\",\"s\",\"j\",\"d\",\"c\",\"jusqu\",\"quoiqu\",\"lorsqu\",\"puisqu\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "french_stop", "{\"type\":\"stop\",\"stopwords\":\"_french_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "french_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}fr/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "french_stemmer",
                "{\"type\":\"stemmer\",\"language\":\"light_french\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "german_stop", "{\"type\":\"stop\",\"stopwords\":\"_german_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "german_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}de/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "german_stemmer",
                "{\"type\":\"stemmer\",\"language\":\"light_german\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "greek_stop", "{\"type\":\"stop\",\"stopwords\":\"_greek_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "greek_lowercase", "{\"type\":\"lowercase\",\"language\":\"greek\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "greek_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}el/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "greek_stemmer", "{\"type\":\"stemmer\",\"language\":\"greek\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "hindi_stop", "{\"type\":\"stop\",\"stopwords\":\"_hindi_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "hungarian_stop", "{\"type\":\"stop\",\"stopwords\":\"_hungarian_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "hungarian_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}hu/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "hungarian_stemmer",
                "{\"type\":\"stemmer\",\"language\":\"hungarian\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "indonesian_stop",
                "{\"type\":\"stop\",\"stopwords\":\"_indonesian_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "indonesian_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}id/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "indonesian_stemmer",
                "{\"type\":\"stemmer\",\"language\":\"indonesian\"}");
        updateAnalysis(
                indicesClient,
                suggestAnalyzerIndex,
                "filter",
                "italian_elision",
                "{\"type\":\"elision\",\"articles\":[\"c\",\"l\",\"all\",\"dall\",\"dell\",\"nell\",\"sull\",\"coll\",\"pell\",\"gl\",\"agl\",\"dagl\",\"degl\",\"negl\",\"sugl\",\"un\",\"m\",\"t\",\"s\",\"v\",\"d\"]}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "italian_stop", "{\"type\":\"stop\",\"stopwords\":\"_italian_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "italian_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}it/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "italian_stemmer",
                "{\"type\":\"stemmer\",\"language\":\"light_italian\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "latvian_stop", "{\"type\":\"stop\",\"stopwords\":\"_latvian_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "latvian_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}lv/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "latvian_stemmer", "{\"type\":\"stemmer\",\"language\":\"latvian\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "lithuanian_stop",
                "{\"type\":\"stop\",\"stopwords\":\"_lithuanian_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "lithuanian_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}lt/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "lithuanian_stemmer",
                "{\"type\":\"stemmer\",\"language\":\"lithuanian\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "norwegian_stop", "{\"type\":\"stop\",\"stopwords\":\"_norwegian_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "norwegian_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}no/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "norwegian_stemmer",
                "{\"type\":\"stemmer\",\"language\":\"norwegian\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "persian_stop", "{\"type\":\"stop\",\"stopwords\":\"_persian_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "portuguese_stop",
                "{\"type\":\"stop\",\"stopwords\":\"_portuguese_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "portuguese_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}pt/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "portuguese_stemmer",
                "{\"type\":\"stemmer\",\"language\":\"light_portuguese\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "romanian_stop", "{\"type\":\"stop\",\"stopwords\":\"_romanian_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "romanian_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}ro/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "romanian_stemmer",
                "{\"type\":\"stemmer\",\"language\":\"romanian\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "russian_stop", "{\"type\":\"stop\",\"stopwords\":\"_russian_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "russian_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}ru/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "russian_stemmer", "{\"type\":\"stemmer\",\"language\":\"russian\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "spanish_stop", "{\"type\":\"stop\",\"stopwords\":\"_spanish_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "spanish_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}es/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "spanish_stemmer",
                "{\"type\":\"stemmer\",\"language\":\"light_spanish\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "swedish_stop", "{\"type\":\"stop\",\"stopwords\":\"_swedish_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "swedish_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}sv/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "swedish_stemmer", "{\"type\":\"stemmer\",\"language\":\"swedish\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "turkish_stop", "{\"type\":\"stop\",\"stopwords\":\"_turkish_\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "turkish_lowercase",
                "{\"type\":\"lowercase\",\"language\":\"turkish\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "turkish_keywords",
                "{\"type\":\"keyword_marker\",\"keywords_path\":\"${fess.dictionary.path}tr/protwords.txt\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "turkish_stemmer", "{\"type\":\"stemmer\",\"language\":\"turkish\"}");
        updateAnalysis(indicesClient, suggestAnalyzerIndex, "filter", "thai_stop", "{\"type\":\"stop\",\"stopwords\":\"_thai_\"}");
        indicesClient.open(new OpenIndexRequest(suggestAnalyzerIndex));

        // data migration
        addData(configIndex,
                "scheduled_job",
                "thumbnail_purger",
                "{\"name\":\"Thumbnail Purger\",\"target\":\"all\",\"cronExpression\":\"0 0 * * *\",\"scriptType\":\"groovy\",\"scriptData\":\"return container.getComponent(\\\"purgeThumbnailJob\\\").expiry(30 * 24 * 60 * 60 * 1000).execute();\",\"jobLogging\":true,\"crawler\":false,\"available\":true,\"sortOrder\":6,\"createdBy\":\"system\",\"createdTime\":0,\"updatedBy\":\"system\",\"updatedTime\":0}");

        // alias
        final IndicesOptions indexOrAliasOptions = IndicesOptions.fromOptions(false, false, true, true);
        if (!existsIndex(indicesClient, searchIndex, indexOrAliasOptions)) {
            try {
                final IndicesAliasesResponse response =
                        indicesClient.prepareAliases().addAlias(oldDocIndex, searchIndex).execute()
                                .actionGet(fessConfig.getIndexIndicesTimeout());
                if (response.isAcknowledged()) {
                    logger.info("Created " + searchIndex + " alias for fess.");
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Failed to create " + searchIndex + " alias for fess.");
                }
            } catch (final Exception e) {
                logger.warn("Failed to create " + searchIndex + " alias for fess.", e);
            }
        }
        if (!existsIndex(indicesClient, updateIndex, indexOrAliasOptions)) {
            try {
                final IndicesAliasesResponse response =
                        indicesClient.prepareAliases().addAlias(oldDocIndex, updateIndex).execute()
                                .actionGet(fessConfig.getIndexIndicesTimeout());
                if (response.isAcknowledged()) {
                    logger.info("Created " + updateIndex + " alias for fess.");
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Failed to create " + updateIndex + " alias for fess.");
                }
            } catch (final Exception e) {
                logger.warn("Failed to create " + updateIndex + " alias for fess.", e);
            }
        }

        // fess.suggest
        if (existsIndex(indicesClient, suggestIndex, IndicesOptions.fromOptions(false, true, true, true))) {
            deleteIndex(indicesClient, suggestIndex, response -> {
                scheduledJobService.getScheduledJob("suggest_indexer").ifPresent(entity -> {
                    if (!entity.isEnabled() || entity.isRunning()) {
                        logger.warn("suggest_indexer job is running.");
                        return;
                    }
                    try {
                        entity.start();
                    } catch (final Exception e) {
                        logger.warn("Failed to start suggest_indexer job.", e);
                    }
                }).orElse(() -> {
                    logger.warn("No suggest_indexer job.");
                });
            });
        }
    }

    private void upgradeFrom10_0() {

        final IndicesAdminClient indicesClient = fessEsClient.admin().indices();
        final String indexConfigPath = "fess_indices";
        final String configIndex = ".fess_config";
        final String userIndex = ".fess_user";
        final String docIndex = fessConfig.getIndexDocumentUpdateIndex();
        final String docType = fessConfig.getIndexDocumentType();

        // file
        uploadResource(indexConfigPath, docIndex, "ko/seunjeon.txt");

        // alias
        createAlias(indicesClient, indexConfigPath, configIndex, ".fess_basic_config");

        // update mapping
        addFieldMapping(indicesClient, configIndex, "label_type", "permissions",
                "{\"properties\":{\"permissions\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, configIndex, "web_config", "permissions",
                "{\"properties\":{\"permissions\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, configIndex, "file_config", "permissions",
                "{\"properties\":{\"permissions\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, configIndex, "data_config", "permissions",
                "{\"properties\":{\"permissions\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "group", "gidNumber", "{\"properties\":{\"gidNumber\":{\"type\":\"long\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "employeeNumber",
                "{\"properties\":{\"employeeNumber\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "mail",
                "{\"properties\":{\"mail\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "telephoneNumber",
                "{\"properties\":{\"telephoneNumber\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "homePhone",
                "{\"properties\":{\"homePhone\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "homePostalAddress",
                "{\"properties\":{\"homePostalAddress\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "labeledURI",
                "{\"properties\":{\"labeledURI\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "roomNumber",
                "{\"properties\":{\"roomNumber\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "description",
                "{\"properties\":{\"description\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "title",
                "{\"properties\":{\"title\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "pager",
                "{\"properties\":{\"pager\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "street",
                "{\"properties\":{\"street\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "postalCode",
                "{\"properties\":{\"postalCode\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "physicalDeliveryOfficeName",
                "{\"properties\":{\"physicalDeliveryOfficeName\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "destinationIndicator",
                "{\"properties\":{\"destinationIndicator\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "internationaliSDNNumber",
                "{\"properties\":{\"internationaliSDNNumber\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "state",
                "{\"properties\":{\"state\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "employeeType",
                "{\"properties\":{\"employeeType\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "facsimileTelephoneNumber",
                "{\"properties\":{\"facsimileTelephoneNumber\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "postOfficeBox",
                "{\"properties\":{\"postOfficeBox\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "initials",
                "{\"properties\":{\"initials\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "carLicense",
                "{\"properties\":{\"carLicense\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "mobile",
                "{\"properties\":{\"mobile\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "postalAddress",
                "{\"properties\":{\"postalAddress\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "city",
                "{\"properties\":{\"city\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "teletexTerminalIdentifier",
                "{\"properties\":{\"teletexTerminalIdentifier\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "x121Address",
                "{\"properties\":{\"x121Address\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "businessCategory",
                "{\"properties\":{\"businessCategory\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "registeredAddress",
                "{\"properties\":{\"registeredAddress\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "displayName",
                "{\"properties\":{\"displayName\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "preferredLanguage",
                "{\"properties\":{\"preferredLanguage\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "departmentNumber",
                "{\"properties\":{\"departmentNumber\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "uidNumber", "{\"properties\":{\"uidNumber\":{\"type\":\"long\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "gidNumber", "{\"properties\":{\"gidNumber\":{\"type\":\"long\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "homeDirectory",
                "{\"properties\":{\"homeDirectory\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, userIndex, "user", "groups",
                "{\"properties\":{\"groups\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}}");
        addFieldMapping(indicesClient, docIndex, docType, "location", "{\"properties\":{\"location\":{\"type\":\"geo_point\"}}}");

        // data migration
        final Map<String, List<String>> mapping = new HashMap<>();
        labelToRoleBhv.selectList(cb -> cb.query().addOrderBy_LabelTypeId_Asc()).forEach(e -> {
            List<String> list = mapping.get(e.getLabelTypeId());
            if (list == null) {
                list = new ArrayList<>();
                mapping.put(e.getLabelTypeId(), list);
            }
            list.add(e.getRoleTypeId());
        });
        mapping.entrySet().forEach(
                e -> {
                    final String labelTypeId = e.getKey();
                    final List<String> idList = e.getValue();
                    labelTypeBhv.selectEntity(cb -> cb.acceptPK(labelTypeId)).ifPresent(
                            entity -> {
                                final String[] permissions =
                                        roleTypeBhv.selectList(cb -> cb.query().setId_InScope(idList)).stream()
                                                .map(r -> fessConfig.getRoleSearchRolePrefix() + r.getValue()).toArray(n -> new String[n]);
                                entity.setPermissions(permissions);
                                labelTypeBhv.insertOrUpdate(entity);
                                labelToRoleBhv.queryDelete(cb -> cb.query().setLabelTypeId_Equal(labelTypeId));
                            });
                });

        mapping.clear();
        webConfigToRoleBhv.selectList(cb -> cb.query().addOrderBy_WebConfigId_Asc()).forEach(e -> {
            final String webConfigId = e.getWebConfigId();
            List<String> list = mapping.get(webConfigId);
            if (list == null) {
                list = new ArrayList<>();
                mapping.put(webConfigId, list);
            }
            list.add(e.getRoleTypeId());
        });
        mapping.entrySet().forEach(
                e -> {
                    final String webConfigTypeId = e.getKey();
                    final List<String> idList = e.getValue();
                    webConfigBhv.selectEntity(cb -> cb.acceptPK(webConfigTypeId)).ifPresent(
                            entity -> {
                                final String[] permissions =
                                        roleTypeBhv.selectList(cb -> cb.query().setId_InScope(idList)).stream()
                                                .map(r -> fessConfig.getRoleSearchRolePrefix() + r.getValue()).toArray(n -> new String[n]);
                                entity.setPermissions(permissions);
                                webConfigBhv.insertOrUpdate(entity);
                                webConfigToRoleBhv.queryDelete(cb -> cb.query().setWebConfigId_Equal(webConfigTypeId));
                            });
                });

        mapping.clear();
        fileConfigToRoleBhv.selectList(cb -> cb.query().addOrderBy_FileConfigId_Asc()).forEach(e -> {
            final String fileConfigId = e.getFileConfigId();
            List<String> list = mapping.get(fileConfigId);
            if (list == null) {
                list = new ArrayList<>();
                mapping.put(fileConfigId, list);
            }
            list.add(e.getRoleTypeId());
        });
        mapping.entrySet().forEach(
                e -> {
                    final String fileConfigTypeId = e.getKey();
                    final List<String> idList = e.getValue();
                    fileConfigBhv.selectEntity(cb -> cb.acceptPK(fileConfigTypeId)).ifPresent(
                            entity -> {
                                final String[] permissions =
                                        roleTypeBhv.selectList(cb -> cb.query().setId_InScope(idList)).stream()
                                                .map(r -> fessConfig.getRoleSearchRolePrefix() + r.getValue()).toArray(n -> new String[n]);
                                entity.setPermissions(permissions);
                                fileConfigBhv.insertOrUpdate(entity);
                                fileConfigToRoleBhv.queryDelete(cb -> cb.query().setFileConfigId_Equal(fileConfigTypeId));
                            });
                });

        mapping.clear();
        dataConfigToRoleBhv.selectList(cb -> cb.query().addOrderBy_DataConfigId_Asc()).forEach(e -> {
            final String dataConfigId = e.getDataConfigId();
            List<String> list = mapping.get(dataConfigId);
            if (list == null) {
                list = new ArrayList<>();
                mapping.put(dataConfigId, list);
            }
            list.add(e.getRoleTypeId());
        });
        mapping.entrySet().forEach(
                e -> {
                    final String dataConfigTypeId = e.getKey();
                    final List<String> idList = e.getValue();
                    dataConfigBhv.selectEntity(cb -> cb.acceptPK(dataConfigTypeId)).ifPresent(
                            entity -> {
                                final String[] permissions =
                                        roleTypeBhv.selectList(cb -> cb.query().setId_InScope(idList)).stream()
                                                .map(r -> fessConfig.getRoleSearchRolePrefix() + r.getValue()).toArray(n -> new String[n]);
                                entity.setPermissions(permissions);
                                dataConfigBhv.insertOrUpdate(entity);
                                dataConfigToRoleBhv.queryDelete(cb -> cb.query().setDataConfigId_Equal(dataConfigTypeId));
                            });
                });

        roleTypeBhv.queryDelete(cb -> {});

        roleBhv.selectEntity(cb -> cb.query().setName_Equal("guest")).orElseGet(() -> {
            final Role entity = new Role();
            entity.setName("guest");
            roleBhv.insert(entity);
            return entity;
        });

        final List<ElevateWord> elevateWordList =
                elevateWordBhv
                        .selectList(cb -> cb.query().addOrderBy_CreatedBy_Asc())
                        .stream()
                        .filter(e -> StringUtil.isNotBlank(e.getTargetRole()))
                        .map(e -> {
                            final String[] permissions =
                                    split(e.getTargetRole(), ",").get(
                                            stream -> stream.filter(StringUtil::isNotBlank).map(
                                                    s -> fessConfig.getRoleSearchRolePrefix() + s)).toArray(n -> new String[n]);
                            e.setPermissions(permissions);
                            e.setTargetRole(null);
                            return e;
                        }).collect(Collectors.toList());
        if (!elevateWordList.isEmpty()) {
            elevateWordBhv.batchUpdate(elevateWordList);
        }

    }

    private void uploadResource(final String indexConfigPath, final String indexName, final String path) {
        final String filePath = indexConfigPath + "/" + indexName + "/" + path;
        try {
            final String source = FileUtil.readUTF8(filePath);
            try (CurlResponse response =
                    Curl.post(org.codelibs.fess.util.ResourceUtil.getElasticsearchHttpUrl() + "/_configsync/file").param("path", path)
                            .body(source).execute()) {
                if (response.getHttpStatusCode() == 200) {
                    logger.info("Register " + path + " to " + indexName);
                } else {
                    logger.warn("Invalid request for " + path);
                }
            }
        } catch (final Exception e) {
            logger.warn("Failed to register " + filePath, e);
        }
    }

    private void createAlias(final IndicesAdminClient indicesClient, final String indexConfigPath, final String indexName,
            final String aliasName) {
        final String aliasConfigPath = indexConfigPath + "/" + indexName + "/alias/" + aliasName + ".json";
        try {
            final File aliasConfigFile = ResourceUtil.getResourceAsFile(aliasConfigPath);
            if (aliasConfigFile.exists()) {
                final String source = FileUtil.readUTF8(aliasConfigFile);
                final IndicesAliasesResponse response =
                        indicesClient.prepareAliases().addAlias(indexName, aliasName, source).execute()
                                .actionGet(fessConfig.getIndexIndicesTimeout());
                if (response.isAcknowledged()) {
                    logger.info("Created " + aliasName + " alias for " + indexName);
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Failed to create " + aliasName + " alias for " + indexName);
                }
            }
        } catch (final ResourceNotFoundRuntimeException e) {
            // ignore
        } catch (final Exception e) {
            logger.warn(aliasConfigPath + " is not found.", e);
        }
    }

    private void addMapping(final IndicesAdminClient indicesClient, final String index, final String type, final String indexResourcePath) {
        final GetMappingsResponse getMappingsResponse =
                indicesClient.prepareGetMappings(index).execute().actionGet(fessConfig.getIndexIndicesTimeout());
        final ImmutableOpenMap<String, MappingMetaData> indexMappings = getMappingsResponse.mappings().get(index);
        if (indexMappings == null || !indexMappings.containsKey(type)) {
            String source = null;
            final String mappingFile = indexResourcePath + "/" + type + ".json";
            try {
                source = FileUtil.readUTF8(mappingFile);
            } catch (final Exception e) {
                logger.warn(mappingFile + " is not found.", e);
            }
            try {
                final PutMappingResponse putMappingResponse =
                        indicesClient.preparePutMapping(index).setType(type).setSource(source).execute()
                                .actionGet(fessConfig.getIndexIndicesTimeout());
                if (putMappingResponse.isAcknowledged()) {
                    logger.info("Created " + index + "/" + type + " mapping.");
                } else {
                    logger.warn("Failed to create " + index + "/" + type + " mapping.");
                }
                // TODO bulk
            } catch (final Exception e) {
                logger.warn("Failed to create " + index + "/" + type + " mapping.", e);
            }
        }
    }

    private void addFieldMapping(final IndicesAdminClient indicesClient, final String index, final String type, final String field,
            final String source) {
        final GetFieldMappingsResponse gfmResponse =
                indicesClient.prepareGetFieldMappings(index).addTypes(type).setFields(field).execute().actionGet();
        final FieldMappingMetaData fieldMappings = gfmResponse.fieldMappings(index, type, field);
        if (fieldMappings == null || fieldMappings.isNull()) {
            try {
                final PutMappingResponse pmResponse =
                        indicesClient.preparePutMapping(index).setType(type).setSource(source).execute().actionGet();
                if (!pmResponse.isAcknowledged()) {
                    logger.warn("Failed to add " + field + " to " + index + "/" + type);
                }
            } catch (final Exception e) {
                logger.warn("Failed to add " + field + " to " + index + "/" + type, e);
            }
        }
    }

    private void updateAnalysis(final IndicesAdminClient indicesClient, final String index, final String type, final String name,
            final String source) {
        try {
            final String dictionaryPath = System.getProperty("fess.dictionary.path", StringUtil.EMPTY);
            final XContentParser contentParser =
                    XContentFactory.xContent(XContentType.JSON).createParser(
                            source.replaceAll(Pattern.quote("${fess.dictionary.path}"), dictionaryPath).getBytes());
            contentParser.close();
            final XContentBuilder builder =
                    jsonBuilder().startObject().startObject("analysis").startObject(type).field(name).copyCurrentStructure(contentParser)
                            .endObject().endObject().endObject();
            indicesClient.prepareUpdateSettings(index).setSettings(builder.string()).execute().actionGet();
        } catch (final Exception e) {
            logger.warn("Failed to set analyzer to " + index, e);
        }
    }

    private void addData(final String index, final String type, final String id, final String source) {
        try {
            final IndexRequest indexRequest = new IndexRequest(index, type, id).source(source);
            fessEsClient.index(indexRequest).actionGet();
        } catch (final Exception e) {
            logger.warn("Failed to add " + id + " to " + index + "/" + type, e);
        }
    }

    private boolean existsIndex(final IndicesAdminClient indicesClient, final String index, final IndicesOptions options) {
        try {
            final IndicesExistsResponse response =
                    indicesClient.prepareExists(index).setIndicesOptions(options).execute().actionGet(fessConfig.getIndexSearchTimeout());
            return response.isExists();
        } catch (final Exception e) {
            // ignore
        }
        return false;
    }

    private void deleteIndex(final IndicesAdminClient indicesClient, final String index, final Consumer<DeleteIndexResponse> comsumer) {
        indicesClient.prepareDelete(index).execute(new ActionListener<DeleteIndexResponse>() {

            @Override
            public void onResponse(final DeleteIndexResponse response) {
                logger.info("Deleted " + index + " index.");
                comsumer.accept(response);
            }

            @Override
            public void onFailure(final Exception e) {
                logger.warn("Failed to delete " + index + " index.", e);
            }
        });
    }

}