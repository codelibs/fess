package jp.sf.fess.dict;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jp.sf.fess.util.ResourceUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;

public abstract class DictionaryLocator {
    protected List<String> searchPathList = new ArrayList<String>();

    public abstract Map<String, DictionaryFile> find();

    protected File[] findFiles(final String path, final String filenamePrefix,
            final List<String> excludedSet) {
        final Collection<File> files = FileUtils.listFiles(new File(path),
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

        return files.toArray(new File[files.size()]);
    }

    public void addSearchPath(final String path) {
        searchPathList.add(ResourceUtil.resolve(path));
    }
}
