package jp.sf.fess.dic.synonym;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.sf.fess.dic.DictionaryFile;
import jp.sf.fess.dic.DictionaryLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynonymLocator extends DictionaryLocator {
    private static final Logger logger = LoggerFactory
            .getLogger(SynonymLocator.class);

    public String synonymFilePrefix = "synonym";

    public List<String> excludedSynonymList;

    @Override
    public Map<String, DictionaryFile> find() {
        final Map<String, DictionaryFile> fileMap = new HashMap<String, DictionaryFile>();
        for (final String path : searchPathList) {
            final File[] files = findFiles(path, synonymFilePrefix,
                    excludedSynonymList);
            for (final File file : files) {
                if (logger.isInfoEnabled()) {
                    logger.info("Synonym File: " + file.getAbsolutePath());
                }
                fileMap.put(file.getAbsolutePath(), new SynonymFile(file));
            }
        }
        return fileMap;
    }

}
