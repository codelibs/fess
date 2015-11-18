/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.config.exbhv.SuggestBadWordBhv;
import org.codelibs.fess.es.config.exbhv.SuggestElevateWordBhv;
import org.codelibs.fess.es.config.exentity.SuggestBadWord;
import org.codelibs.fess.es.config.exentity.SuggestElevateWord;
import org.codelibs.fess.es.log.exentity.SearchFieldLog;
import org.codelibs.fess.es.log.exentity.SearchLog;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.suggest.Suggester;
import org.codelibs.fess.suggest.constants.FieldNames;
import org.codelibs.fess.suggest.entity.ElevateWord;
import org.codelibs.fess.suggest.entity.SuggestItem;
import org.codelibs.fess.suggest.index.contents.document.DocumentReader;
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

    @Resource
    protected SuggestElevateWordBhv suggestElevateWordBhv;

    @Resource
    protected SuggestBadWordBhv suggestBadWordBhv;

    @Resource
    protected FessEsClient fessEsClient;

    public String[] contentFieldNames = { "_default" };

    public String[] tagFieldNames = { "label" };

    public String[] roleFieldNames = { "role" };

    public String[] contentsIndexFieldNames = { "content", "title" };

    private static final String TEXT_SEP = " ";

    protected Suggester suggester;

    protected final AtomicBoolean initialized = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        final Thread th = new Thread(() -> {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            fessEsClient.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
            suggester = Suggester.builder().build(fessEsClient, fessConfig.getIndexDocumentIndex());
            suggester.settings().array().delete(SuggestSettings.DefaultKeys.SUPPORTED_FIELDS);
            for (final String field : contentsIndexFieldNames) {
                suggester.settings().array().add(SuggestSettings.DefaultKeys.SUPPORTED_FIELDS, field);
            }
            suggester.createIndexIfNothing();
            initialized.set(true);
        });
        th.start();
    }

    public Suggester suggester() {
        return suggester;
    }

    public void indexFromSearchLog(final List<SearchLog> searchLogList) {
        for (final SearchLog searchLog : searchLogList) {
            // TODO if(getHitCount == 0) continue;

            final StringBuilder sb = new StringBuilder();
            final List<String> fields = new ArrayList<>();
            final List<String> tags = new ArrayList<>();
            final List<String> roles = new ArrayList<>();

            for (final SearchFieldLog searchFieldLog : searchLog.getSearchFieldLogList()) {
                final String name = searchFieldLog.getName();
                if (isContentField(name)) {
                    if (sb.length() > 0) {
                        sb.append(TEXT_SEP);
                    }
                    sb.append(searchFieldLog.getValue());
                    fields.add(name);
                } else if (isTagField(name)) {
                    tags.add(searchFieldLog.getValue());
                } else if (isRoleField(name)) {
                    roles.add(searchFieldLog.getValue());
                }
            }

            if (sb.length() > 0) {
                suggester.indexer().indexFromSearchWord(sb.toString(), fields.toArray(new String[fields.size()]),
                        tags.toArray(new String[tags.size()]), roles.toArray(new String[roles.size()]), 1);
            }
        }
        suggester.refresh();
    }

    public void indexFromDocuments(final Consumer<Boolean> success, final Consumer<Throwable> error) {
        while (!initialized.get()) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                error.accept(e);
                return;
            }
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        DocumentReader reader =
                new ESSourceReader(fessEsClient, suggester.settings(), fessConfig.getIndexDocumentIndex(),
                        fessConfig.getIndexDocumentType());

        suggester.indexer().indexFromDocument(reader, 2, 100).done(response -> {
            suggester.refresh();

            //TODO delete old doc

                success.accept(true);
            }).error(t -> error.accept(t));
    }

    public void purge(LocalDateTime time) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.rangeQuery(FieldNames.TIMESTAMP).lt(time.format(DateTimeFormatter.BASIC_ISO_DATE)));

        boolQueryBuilder.must(QueryBuilders.termQuery(FieldNames.KINDS, SuggestItem.Kind.DOCUMENT.toString()));
        boolQueryBuilder.mustNot(QueryBuilders.termQuery(FieldNames.KINDS, SuggestItem.Kind.QUERY.toString()));
        boolQueryBuilder.must(QueryBuilders.termQuery(FieldNames.KINDS, SuggestItem.Kind.USER.toString()));

        SuggestUtil.deleteByQuery(fessEsClient, suggester.getIndex(), suggester.getType(), boolQueryBuilder);
    }

    public void refreshWords() {
        deleteAllBadWords();
        storeAllElevateWords();
    }

    public void storeAllElevateWords() {
        deleteAllBadWords();

        final List<SuggestElevateWord> list = suggestElevateWordBhv.selectList(cb -> {
            cb.query().matchAll();
        });

        for (final SuggestElevateWord elevateWord : list) {
            addElevateWord(elevateWord.getSuggestWord(), elevateWord.getReading(), elevateWord.getLabelTypeValues(),
                    elevateWord.getTargetRole(), elevateWord.getBoost(), false);
        }
        suggester.refresh();
    }

    public void deleteAllElevateWord() {
        final List<SuggestElevateWord> list = suggestElevateWordBhv.selectList(cb -> {
            cb.query().matchAll();
        });

        for (final SuggestElevateWord elevateWord : list) {
            suggester.indexer().deleteElevateWord(elevateWord.getSuggestWord());
        }
        suggester.refresh();
    }

    public void deleteElevateWord(final String word) {
        suggester.indexer().deleteElevateWord(word);
        suggester.refresh();
    }

    public void addElevateWord(final String word, final String reading, final String[] tags, final String roles, final float boost) {
        addElevateWord(word, reading, tags, roles, boost, true);
    }

    public void addElevateWord(final String word, final String reading, final String[] tags, final String roles, final float boost,
            final boolean commit) {
        final List<String> labelList = new ArrayList<String>();
        for (final String label : tags) {
            labelList.add(label);
        }
        final List<String> roleList = new ArrayList<String>();
        if (StringUtil.isNotBlank(roles)) {
            final String[] array = roles.trim().split(",");
            for (final String role : array) {
                roleList.add(role);
            }
        }

        suggester.indexer().addElevateWord(
                new ElevateWord(word, boost, Collections.singletonList(reading), Arrays.asList(contentFieldNames), labelList, roleList));
    }

    public void deleteAllBadWords() {
        suggester.settings().badword().deleteAll();
    }

    public void storeAllBadWords() {
        deleteAllBadWords();
        final List<SuggestBadWord> list = suggestBadWordBhv.selectList(cb -> {
            cb.query().matchAll();
        });
        for (final SuggestBadWord suggestBadWord : list) {
            final String word = suggestBadWord.getSuggestWord();
            suggester.indexer().addBadWord(word);
        }
    }

    public void addBadWord(final String badWord) {
        suggester.indexer().addBadWord(badWord);
    }

    public void deleteBadWord(final String badWord) {
        suggester.indexer().deleteBadWord(badWord);
    }

    protected boolean isContentField(final String field) {
        for (final String contentField : contentFieldNames) {
            if (contentField.equals(field)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isTagField(final String field) {
        for (final String tagField : tagFieldNames) {
            if (tagField.equals(field)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isRoleField(final String field) {
        for (final String roleField : roleFieldNames) {
            if (roleField.equals(field)) {
                return true;
            }
        }
        return false;
    }
}
