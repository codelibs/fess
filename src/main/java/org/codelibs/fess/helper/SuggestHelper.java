/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.es.exbhv.SuggestBadWordBhv;
import org.codelibs.fess.es.exbhv.SuggestElevateWordBhv;
import org.codelibs.fess.es.exentity.SuggestBadWord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuggestHelper {
    private static final Logger logger = LoggerFactory.getLogger(SuggestHelper.class);

    @Resource
    protected SuggestElevateWordBhv suggestElevateWordBhv;

    @Resource
    protected SuggestBadWordBhv suggestBadWordBhv;

    public String badwordFileDir = "./solr/core1/conf/";

    public void init() {
        final Thread th = new Thread(() -> {
            // TODO replace with Elasticsearch
                /*
                while (true) {
                    final PingResponse response = searchService.ping();
                    final int status = response.getStatus();
                    if (status == 0) {
                        if (logger.isInfoEnabled()) {
                            logger.info("Solr server was availabled. Refresh suggest words.");
                        }
                        refreshWords();
                        break;
                    }
                    try {
                        Thread.sleep(1 * 1000);
                    } catch (final InterruptedException e) {
                        //ignore
                    }
                }
                */
            });
        th.start();
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
}
