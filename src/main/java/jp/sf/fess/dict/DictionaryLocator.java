package jp.sf.fess.dict;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.sf.fess.util.ResourceUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DictionaryLocator {
    private static final Logger logger = LoggerFactory
            .getLogger(DictionaryLocator.class);

    protected List<String> searchPathList = new ArrayList<String>();

    public abstract List<DictionaryFile<? extends DictionaryItem>> find();

    protected File[] findFiles(final String path, final String filenamePrefix,
            final List<String> excludedSet) {

        final File directory = new File(path);
        if (logger.isDebugEnabled()) {
            logger.debug("Load files from " + directory.getAbsolutePath());
        }
        final Collection<File> files = FileUtils.listFiles(directory,
                new AbstractFileFilter() {
                    @Override
                    public boolean accept(final File dir, final String name) {
                        return name.startsWith(filenamePrefix);
                    }
                }, new AbstractFileFilter() {
                    @Override
                    public boolean accept(final File dir, final String name) {
                        return excludedSet == null
                                || !excludedSet.contains(name);
                    }
                });

        if (logger.isDebugEnabled()) {
            logger.debug("Dictionary files: " + files);
        }
        return files.toArray(new File[files.size()]);
    }

    public void addSearchPath(final String path) {
        searchPathList.add(ResourceUtil.resolve(path));
    }
}
