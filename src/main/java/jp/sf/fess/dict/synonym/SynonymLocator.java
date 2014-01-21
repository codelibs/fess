package jp.sf.fess.dict.synonym;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.sf.fess.dict.DictionaryFile;
import jp.sf.fess.dict.DictionaryItem;
import jp.sf.fess.dict.DictionaryLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynonymLocator extends DictionaryLocator {
    private static final Logger logger = LoggerFactory
            .getLogger(SynonymLocator.class);

    public String synonymFilePrefix = "synonym";

    public List<String> excludedSynonymList;

    @Override
    public List<DictionaryFile<? extends DictionaryItem>> find() {
        final List<DictionaryFile<? extends DictionaryItem>> fileList = new ArrayList<DictionaryFile<? extends DictionaryItem>>();
        for (final String path : searchPathList) {
            if (logger.isInfoEnabled()) {
                logger.info("Synonym Files from " + path);
            }
            final File[] files = findFiles(path, synonymFilePrefix,
                    excludedSynonymList);
            for (final File file : files) {
                if (logger.isInfoEnabled()) {
                    logger.info("Synonym File: " + file.getAbsolutePath());
                }
                fileList.add(new SynonymFile(file));
            }
        }
        Collections.sort(fileList,
                new Comparator<DictionaryFile<? extends DictionaryItem>>() {
                    @Override
                    public int compare(
                            final DictionaryFile<? extends DictionaryItem> o1,
                            final DictionaryFile<? extends DictionaryItem> o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
        return fileList;
    }

}
