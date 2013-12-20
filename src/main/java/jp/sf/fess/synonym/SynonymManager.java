package jp.sf.fess.synonym;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.sf.fess.util.ResourceUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.seasar.extension.timer.TimeoutManager;
import org.seasar.extension.timer.TimeoutTarget;
import org.seasar.extension.timer.TimeoutTask;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynonymManager {
    private static final Logger logger = LoggerFactory
            .getLogger(SynonymManager.class);

    protected List<String> searchPathList = new ArrayList<String>();

    protected Map<String, SynonymFile> synonymFileMap;

    public String synonymFilePrefix = "synonym";

    //    public String[] excludedSynonymDirs = new String[] { "data", "txlog",
    //            "lib", "bin", "contrib" };

    public Set<String> excludedSynonymSet;

    public long keepAlive = 5 * 60 * 1000; // 5min

    public int watcherTimeout = 60; // 1min

    protected volatile long lifetime = 0;

    protected TimeoutTask watcherTargetTask;

    @InitMethod
    public void init() {
        // start
        final WatcherTarget watcherTarget = new WatcherTarget();
        watcherTargetTask = TimeoutManager.getInstance().addTimeoutTarget(
                watcherTarget, watcherTimeout, true);
    }

    @DestroyMethod
    public void destroy() {
        if (watcherTargetTask != null && !watcherTargetTask.isStopped()) {
            watcherTargetTask.stop();
        }
    }

    public SynonymFile[] getSynonymFiles() {
        final Map<String, SynonymFile> fileMap = getSynonymFileMap();

        final Collection<SynonymFile> values = fileMap.values();
        return values.toArray(new SynonymFile[values.size()]);
    }

    public SynonymFile getSynonymFile(final String uri) {
        final Map<String, SynonymFile> fileMap = getSynonymFileMap();

        return fileMap.get(uri);
    }

    protected Map<String, SynonymFile> getSynonymFileMap() {
        synchronized (this) {
            if (lifetime > System.currentTimeMillis() && synonymFileMap != null) {
                lifetime = System.currentTimeMillis() + keepAlive;
                return synonymFileMap;
            }

            final Map<String, SynonymFile> newFileMap = new LinkedHashMap<String, SynonymFile>();
            for (final String path : searchPathList) {
                final File[] files = findFiles(path);
                for (final File file : files) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Synonym File: " + file.getAbsolutePath());
                    }
                    newFileMap.put(file.getAbsolutePath(),
                            new SynonymFile(file));
                }
            }
            synonymFileMap = newFileMap;
            lifetime = System.currentTimeMillis() + keepAlive;
            return synonymFileMap;
        }
    }

    protected File[] findFiles(final String path) {
        final Collection<File> files = FileUtils.listFiles(new File(path),
                new AbstractFileFilter() {
                    @Override
                    public boolean accept(final File dir, final String name) {
                        return name.startsWith(synonymFilePrefix);
                    }
                }, new AbstractFileFilter() {
                    @Override
                    public boolean accept(final File dir, final String name) {
                        return excludedSynonymSet == null
                                || !excludedSynonymSet.contains(name);
                    }
                });

        return files.toArray(new File[files.size()]);
    }

    public void addSearchPath(final String path) {
        searchPathList.add(ResourceUtil.resolve(path));
    }

    protected class WatcherTarget implements TimeoutTarget {

        @Override
        public void expired() {
            synchronized (SynonymManager.this) {
                if (lifetime <= System.currentTimeMillis()
                        && synonymFileMap != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Cleaning synonym files: "
                                + synonymFileMap);
                    }
                    synonymFileMap = null;
                }
            }
        }
    }
}
