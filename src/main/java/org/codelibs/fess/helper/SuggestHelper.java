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
package org.codelibs.fess.helper;

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.config.exbhv.BadWordBhv;
import org.codelibs.fess.es.config.exbhv.ElevateWordBhv;
import org.codelibs.fess.es.config.exentity.BadWord;
import org.codelibs.fess.es.config.exentity.ElevateWord;
import org.codelibs.fess.es.log.exentity.SearchFieldLog;
import org.codelibs.fess.es.log.exentity.SearchLog;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.suggest.Suggester;
import org.codelibs.fess.suggest.constants.FieldNames;
import org.codelibs.fess.suggest.entity.SuggestItem;
import org.codelibs.fess.suggest.index.SuggestDeleteResponse;
import org.codelibs.fess.suggest.index.contents.document.ESSourceReader;
import org.codelibs.fess.suggest.settings.SuggestSettings;
import org.codelibs.fess.suggest.util.SuggestUtil;
import org.codelibs.fess.util.ComponentUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuggestHelper {
    private static final Logger logger = LoggerFactory.getLogger(SuggestHelper.class);

    private static final String TEXT_SEP = " ";

    @Resource
    protected ElevateWordBhv elevateWordBhv;

    @Resource
    protected BadWordBhv badWordBhv;

    @Resource
    protected FessEsClient fessEsClient;

    protected Suggester suggester;

    private FessConfig fessConfig;

    private final Set<String> contentFieldNameSet = new HashSet<>();

    private final Set<String> tagFieldNameSet = new HashSet<>();

    private final Set<String> roleFieldNameSet = new HashSet<>();

    private List<String> contentFieldList;

    @PostConstruct
    public void init() {
        fessConfig = ComponentUtil.getFessConfig();
        split(fessConfig.getSuggestFieldContents(), ",").of(
                stream -> stream.filter(StringUtil::isNotBlank).forEach(contentFieldNameSet::add));
        split(fessConfig.getSuggestFieldTags(), ",").of(stream -> stream.filter(StringUtil::isNotBlank).forEach(tagFieldNameSet::add));
        split(fessConfig.getSuggestFieldRoles(), ",").of(stream -> stream.filter(StringUtil::isNotBlank).forEach(roleFieldNameSet::add));
        contentFieldList = Arrays.asList(stream(fessConfig.getSuggestFieldContents()).get(stream -> stream.toArray(n -> new String[n])));

        fessEsClient.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet(fessConfig.getIndexHealthTimeout());

        suggester = Suggester.builder().build(fessEsClient, fessConfig.getIndexDocumentSuggestIndex());
        suggester.settings().array().delete(SuggestSettings.DefaultKeys.SUPPORTED_FIELDS);
        split(fessConfig.getSuggestFieldIndexContents(), ",").of(
                stream -> stream.filter(StringUtil::isNotBlank).forEach(
                        field -> suggester.settings().array().add(SuggestSettings.DefaultKeys.SUPPORTED_FIELDS, field)));
        suggester.createIndexIfNothing();
    }

    public Suggester suggester() {
        return suggester;
    }

    public void indexFromSearchLog(final List<SearchLog> searchLogList) {
        searchLogList.stream().forEach(
                searchLog -> {
                    if (searchLog.getHitCount() == null
                            || searchLog.getHitCount().longValue() < fessConfig.getSuggestMinHitCountAsInteger().longValue()) {
                        return;
                    }

                    final StringBuilder sb = new StringBuilder(100);
                    final List<String> fields = new ArrayList<>();
                    final List<String> tags = new ArrayList<>();
                    final List<String> roles = new ArrayList<>();

                    for (final SearchFieldLog searchFieldLog : searchLog.getSearchFieldLogList()) {
                        final String name = searchFieldLog.getName();
                        if (contentFieldNameSet.contains(name)) {
                            if (sb.length() > 0) {
                                sb.append(TEXT_SEP);
                            }
                            sb.append(searchFieldLog.getValue());
                            fields.add(name);
                        } else if (tagFieldNameSet.contains(name)) {
                            tags.add(searchFieldLog.getValue());
                        } else if (roleFieldNameSet.contains(name)) {
                            roles.add(searchFieldLog.getValue());
                        }
                    }

                    if (sb.length() > 0) {
                        final String[] langs = searchLog.getLanguages() == null ? new String[] {} : searchLog.getLanguages().split(",");
                        stream(searchLog.getRoles()).of(stream -> stream.forEach(role -> roles.add(role)));
                        if (fessConfig.isValidSearchLogPermissions(roles.toArray(new String[roles.size()]))) {
                            suggester.indexer().indexFromSearchWord(sb.toString(), fields.toArray(new String[fields.size()]),
                                    tags.toArray(new String[tags.size()]), roles.toArray(new String[roles.size()]), 1, langs);
                        }
                    }
                });
        suggester.refresh();
    }

    public void indexFromDocuments(final Consumer<Boolean> success, final Consumer<Throwable> error) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        suggester
                .indexer()
                .indexFromDocument(
                        () -> {
                            final ESSourceReader reader =
                                    new ESSourceReader(fessEsClient, suggester.settings(), fessConfig.getIndexDocumentSearchIndex(),
                                            fessConfig.getIndexDocumentType());
                            reader.setScrollSize(fessConfig.getSuggestSourceReaderScrollSizeAsInteger().intValue());
                            return reader;
                        }, 2, fessConfig.getSuggestUpdateRequestIntervalAsInteger().longValue()).then(response -> {
                    suggester.refresh();
                    success.accept(true);
                }).error(t -> error.accept(t));
    }

    public void purgeDocumentSuggest(final LocalDateTime time) {
        final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.rangeQuery(FieldNames.TIMESTAMP).lt(
                time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

        boolQueryBuilder.must(QueryBuilders.termQuery(FieldNames.KINDS, SuggestItem.Kind.DOCUMENT.toString()));
        boolQueryBuilder.mustNot(QueryBuilders.termQuery(FieldNames.KINDS, SuggestItem.Kind.QUERY.toString()));
        boolQueryBuilder.mustNot(QueryBuilders.termQuery(FieldNames.KINDS, SuggestItem.Kind.USER.toString()));

        SuggestUtil.deleteByQuery(fessEsClient, suggester.getIndex(), suggester.getType(), boolQueryBuilder);
    }

    public void purgeSearchlogSuggest(final LocalDateTime time) {
        final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.rangeQuery(FieldNames.TIMESTAMP).lt(
                time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

        boolQueryBuilder.mustNot(QueryBuilders.termQuery(FieldNames.KINDS, SuggestItem.Kind.DOCUMENT.toString()));
        boolQueryBuilder.must(QueryBuilders.termQuery(FieldNames.KINDS, SuggestItem.Kind.QUERY.toString()));
        boolQueryBuilder.mustNot(QueryBuilders.termQuery(FieldNames.KINDS, SuggestItem.Kind.USER.toString()));

        SuggestUtil.deleteByQuery(fessEsClient, suggester.getIndex(), suggester.getType(), boolQueryBuilder);
    }

    public long getAllWordsNum() {
        return suggester.getAllWordsNum();
    }

    public long getDocumentWordsNum() {
        return suggester.getDocumentWordsNum();
    }

    public long getQueryWordsNum() {
        return suggester.getQueryWordsNum();
    }

    public boolean deleteAllWords() {
        final SuggestDeleteResponse response = suggester.indexer().deleteAll();
        if (response.hasError()) {
            logger.warn("Failed to delete all words.", response.getErrors().get(0));
            return false;
        }
        suggester.refresh();
        return true;
    }

    public boolean deleteDocumentWords() {
        final SuggestDeleteResponse response = suggester.indexer().deleteDocumentWords();
        if (response.hasError()) {
            logger.warn("Failed to delete document words.", response.getErrors().get(0));
            return false;
        }
        suggester.refresh();
        return true;
    }

    public boolean deleteQueryWords() {
        final SuggestDeleteResponse response = suggester.indexer().deleteQueryWords();
        if (response.hasError()) {
            logger.warn("Failed to delete query words.", response.getErrors().get(0));
            return false;
        }
        suggester.refresh();
        return true;
    }

    public void refreshWords() {
        deleteAllBadWords();
        storeAllElevateWords();
    }

    public void storeAllElevateWords() {
        deleteAllBadWords();

        final List<ElevateWord> list = elevateWordBhv.selectList(cb -> {
            cb.query().matchAll();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageElevateWordMaxFetchSizeAsInteger());
        });

        for (final ElevateWord elevateWord : list) {
            addElevateWord(elevateWord.getSuggestWord(), elevateWord.getReading(), elevateWord.getLabelTypeValues(),
                    elevateWord.getPermissions(), elevateWord.getBoost(), false);
        }
        suggester.refresh();
    }

    public void deleteAllElevateWord() {
        final List<ElevateWord> list = elevateWordBhv.selectList(cb -> {
            cb.query().matchAll();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageElevateWordMaxFetchSizeAsInteger());
        });

        for (final ElevateWord elevateWord : list) {
            suggester.indexer().deleteElevateWord(elevateWord.getSuggestWord());
        }
        suggester.refresh();
    }

    public void deleteElevateWord(final String word) {
        suggester.indexer().deleteElevateWord(word);
        suggester.refresh();
    }

    public void addElevateWord(final String word, final String reading, final String[] tags, final String[] permissions, final Float boost) {
        addElevateWord(word, reading, tags, permissions, boost, true);
    }

    public void addElevateWord(final String word, final String reading, final String[] tags, final String[] permissions, final float boost,
            final boolean commit) {
        final List<String> labelList = new ArrayList<>();
        for (final String label : tags) {
            labelList.add(label);
        }
        final List<String> roleList = new ArrayList<>();
        for (final String permission : permissions) {
            roleList.add(permission);
        }

        suggester.indexer().addElevateWord(
                new org.codelibs.fess.suggest.entity.ElevateWord(word, boost, Collections.singletonList(reading), contentFieldList,
                        labelList, roleList));
    }

    public void deleteAllBadWords() {
        suggester.settings().badword().deleteAll();
    }

    public void storeAllBadWords() {
        deleteAllBadWords();
        final List<BadWord> list = badWordBhv.selectList(cb -> {
            cb.query().matchAll();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageBadWordMaxFetchSizeAsInteger());
        });
        for (final BadWord badWord : list) {
            final String word = badWord.getSuggestWord();
            suggester.indexer().addBadWord(word);
        }
    }

    public void addBadWord(final String badWord) {
        suggester.indexer().addBadWord(badWord);
    }

    public void deleteBadWord(final String badWord) {
        suggester.indexer().deleteBadWord(badWord);
    }

}
