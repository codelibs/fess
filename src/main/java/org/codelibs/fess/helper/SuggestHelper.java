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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.config.exbhv.SuggestBadWordBhv;
import org.codelibs.fess.es.config.exbhv.SuggestElevateWordBhv;
import org.codelibs.fess.es.config.exentity.SuggestBadWord;
import org.codelibs.fess.es.log.exentity.SearchFieldLog;
import org.codelibs.fess.es.log.exentity.SearchLog;
import org.codelibs.fess.suggest.Suggester;
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

    @Resource
    protected FieldHelper fieldHelper;

    public String[] contentFieldNames = { "_default" };

    public String[] tagFieldNames = { "label" };

    public String[] roleFieldNames = { "role" };

    private static final String TEXT_SEP = " ";

    protected Suggester suggester;

    @PostConstruct
    public void init() {
        final Thread th = new Thread(() -> {
            fessEsClient.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
            suggester = Suggester.builder().build(fessEsClient, fieldHelper.docIndex);
            suggester.createIndexIfNothing();
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

    public void refreshWords() {
        deleteAllBadWord();
        storeAllElevateWords();
    }

    public void storeAllElevateWords() {
        // TODO
        //        suggestService.deleteAllElevateWords();
        //
        //        final List<SuggestElevateWord> list = suggestElevateWordBhv.selectList(cb -> {
        //            cb.query().setDeletedBy_IsNull();
        //        });
        //        for (final SuggestElevateWord suggestElevateWord : list) {
        //            final String word = suggestElevateWord.getSuggestWord();
        //            final String reading = suggestElevateWord.getReading();
        //            final String labelStr = suggestElevateWord.getTargetLabel();
        //            final String roleStr = suggestElevateWord.getTargetRole();
        //            final long boost = suggestElevateWord.getBoost().longValue();
        //
        //            addElevateWord(word, reading, labelStr, roleStr, boost, false);
        //        }
        //        suggestService.commit();
    }

    public void addElevateWord(final String word, final String reading, final String labels, final String roles, final long boost) {
        addElevateWord(word, reading, labels, roles, boost, true);
    }

    public void addElevateWord(final String word, final String reading, final String labels, final String roles, final long boost,
            final boolean commit) {
        final List<SuggestBadWord> badWordList = suggestBadWordBhv.selectList(badWordCB -> {
            badWordCB.query().setSuggestWord_Equal(word);
        });
        if (badWordList.size() > 0) {
            return;
        }

        final List<String> labelList = new ArrayList<String>();
        if (StringUtil.isNotBlank(labels)) {
            final String[] array = labels.trim().split(",");
            for (final String label : array) {
                labelList.add(label);
            }
        }
        final List<String> roleList = new ArrayList<String>();
        if (StringUtil.isNotBlank(roles)) {
            final String[] array = roles.trim().split(",");
            for (final String role : array) {
                roleList.add(role);
            }
        }

        // TODO
        //        suggestService.addElevateWord(word, reading, labelList, roleList, boost);
        //
        //        if (commit) {
        //            suggestService.commit();
        //        }
    }

    public void deleteAllBadWord() {
        final List<SuggestBadWord> list = suggestBadWordBhv.selectList(cb -> {
            cb.query().matchAll();
        });
        final Set<String> badWords = new HashSet<String>();
        for (final SuggestBadWord suggestBadWord : list) {
            final String word = suggestBadWord.getSuggestWord();
            badWords.add(word);
        }
        // TODO
        //        suggestService.updateBadWords(badWords);
        //        suggestService.deleteBadWords();
        //        suggestService.commit();
    }

    /*
    public void updateSolrBadwordFile() {
        suggestBadWordBhv.selectList(cb -> {
            cb.query().matchAll();
        });

        final File dir = new File(System.getProperty("catalina.home").replace("¥", "/") + "/" + badwordFileDir);
        if (!dir.exists() || !dir.isDirectory()) {
            logger.warn(dir.getAbsolutePath() + " does not exist.");
            return;
        }

        // TODO
        //        final File file = new File(dir, SuggestConstants.BADWORD_FILENAME);
        //        BufferedWriter bw = null;
        //        try {
        //            if (!file.exists()) {
        //                file.createNewFile();
        //            }
        //            bw = new BufferedWriter(new FileWriter(file, false));
        //            for (final SuggestBadWord suggestBadWord : list) {
        //                bw.write(suggestBadWord.getSuggestWord());
        //                bw.newLine();
        //            }
        //            bw.close();
        //        } catch (final IOException e) {
        //            logger.warn("Failed to update badword file.", e);
        //        } finally {
        //            if (bw != null) {
        //                try {
        //                    bw.close();
        //                } catch (final Exception e2) {
        //                    //ignore
        //                }
        //            }
        //        }
    }
    */

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
