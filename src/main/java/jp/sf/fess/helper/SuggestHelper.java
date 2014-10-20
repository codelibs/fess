package jp.sf.fess.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import jp.sf.fess.db.cbean.SuggestBadWordCB;
import jp.sf.fess.db.cbean.SuggestElevateWordCB;
import jp.sf.fess.db.exbhv.SuggestBadWordBhv;
import jp.sf.fess.db.exbhv.SuggestElevateWordBhv;
import jp.sf.fess.db.exentity.SuggestBadWord;
import jp.sf.fess.db.exentity.SuggestElevateWord;
import jp.sf.fess.suggest.SuggestConstants;
import jp.sf.fess.suggest.service.SuggestService;

import org.codelibs.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuggestHelper {
    private static final Logger logger = LoggerFactory
            .getLogger(SuggestHelper.class);

    @Resource
    protected SuggestService suggestService;

    @Resource
    protected SuggestElevateWordBhv suggestElevateWordBhv;

    @Resource
    protected SuggestBadWordBhv suggestBadWordBhv;

    public String badwordFileDir = "./solr/core1/conf/";

    public void storeAllElevateWords() {
        suggestService.deleteAllElevateWords();

        final SuggestElevateWordCB cb = new SuggestElevateWordCB();
        cb.query().setDeletedBy_IsNull();
        final List<SuggestElevateWord> list = suggestElevateWordBhv
                .selectList(cb);
        for (final SuggestElevateWord suggestElevateWord : list) {
            final String word = suggestElevateWord.getSuggestWord();
            final String reading = suggestElevateWord.getReading();
            final String labelStr = suggestElevateWord.getTargetLabel();
            final String roleStr = suggestElevateWord.getTargetRole();
            final long boost = suggestElevateWord.getBoost().longValue();

            addElevateWord(word, reading, labelStr, roleStr, boost, false);
        }
        suggestService.commit();
    }

    public void addElevateWord(final String word, final String reading,
            final String labels, final String roles, final long boost) {
        addElevateWord(word, reading, labels, roles, boost, true);
    }

    public void addElevateWord(final String word, final String reading,
            final String labels, final String roles, final long boost,
            final boolean commit) {
        final SuggestBadWordCB badWordCB = new SuggestBadWordCB();
        badWordCB.query().setSuggestWord_Equal(word);
        final List<SuggestBadWord> badWordList = suggestBadWordBhv
                .selectList(badWordCB);
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

        suggestService
        .addElevateWord(word, reading, labelList, roleList, boost);

        if (commit) {
            suggestService.commit();
        }
    }

    public void deleteAllBadWord() {
        final SuggestBadWordCB cb = new SuggestBadWordCB();
        cb.query().setDeletedBy_IsNull();
        final List<SuggestBadWord> list = suggestBadWordBhv.selectList(cb);
        for (final SuggestBadWord suggestBadWord : list) {
            final String word = suggestBadWord.getSuggestWord();
            suggestService.deleteBadWord(word);
        }
        suggestService.commit();
    }

    public void updateSolrBadwordFile() {
        final SuggestBadWordCB cb = new SuggestBadWordCB();
        cb.query().setDeletedBy_IsNull();
        final List<SuggestBadWord> list = suggestBadWordBhv.selectList(cb);

        final File dir = new File(System.getProperty("catalina.home").replace(
                "¥", "/")
                + "/" + badwordFileDir);
        if (!dir.exists() || !dir.isDirectory()) {
            logger.warn(dir.getAbsolutePath() + " does not exist.");
            return;
        }

        final File file = new File(dir, SuggestConstants.BADWORD_FILENAME);
        BufferedWriter bw = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(file, false));
            for (final SuggestBadWord suggestBadWord : list) {
                bw.write(suggestBadWord.getSuggestWord());
                bw.newLine();
            }
            bw.close();
        } catch (final IOException e) {
            logger.warn("Failed to update badword file.", e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (final Exception e2) {
                    //ignore
                }
            }
        }
    }
}
