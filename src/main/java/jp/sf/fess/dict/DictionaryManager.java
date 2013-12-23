package jp.sf.fess.dict;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.sf.fess.dict.synonym.SynonymFile;

import org.seasar.extension.timer.TimeoutManager;
import org.seasar.extension.timer.TimeoutTarget;
import org.seasar.extension.timer.TimeoutTask;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DictionaryManager {
    private static final Logger logger = LoggerFactory
            .getLogger(DictionaryManager.class);

    protected Map<String, DictionaryFile> dicFileMap;

    public long keepAlive = 5 * 60 * 1000; // 5min

    public int watcherTimeout = 60; // 1min

    protected volatile long lifetime = 0;

    protected TimeoutTask watcherTargetTask;

    protected List<DictionaryLocator> locatorList = new ArrayList<DictionaryLocator>();

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

    public DictionaryFile[] getDictionaryFiles() {
        final Map<String, DictionaryFile> fileMap = getDictionaryFileMap();

        final Collection<DictionaryFile> values = fileMap.values();
        return values.toArray(new SynonymFile[values.size()]);
    }

    public DictionaryFile getDictionaryFile(final String uri) {
        final Map<String, DictionaryFile> fileMap = getDictionaryFileMap();

        return fileMap.get(uri);
    }

    protected Map<String, DictionaryFile> getDictionaryFileMap() {
        synchronized (this) {
            if (lifetime > System.currentTimeMillis() && dicFileMap != null) {
                lifetime = System.currentTimeMillis() + keepAlive;
                return dicFileMap;
            }

            final Map<String, DictionaryFile> newFileMap = new TreeMap<String, DictionaryFile>();
            for (final DictionaryLocator locator : locatorList) {
                newFileMap.putAll(locator.find());
            }
            dicFileMap = newFileMap;
            lifetime = System.currentTimeMillis() + keepAlive;
            return dicFileMap;
        }
    }

    public void addLocator(final DictionaryLocator locator) {
        locatorList.add(locator);
    }

    protected class WatcherTarget implements TimeoutTarget {
        @Override
        public void expired() {
            synchronized (DictionaryManager.this) {
                if (lifetime <= System.currentTimeMillis()
                        && dicFileMap != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Cleaning synonym files: " + dicFileMap);
                    }
                    dicFileMap = null;
                }
            }
        }
    }
}
