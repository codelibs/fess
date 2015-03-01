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

package org.codelibs.fess.dict;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codelibs.core.util.StringUtil;
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

    protected Map<String, DictionaryFile<? extends DictionaryItem>> dicFileMap;

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

    public DictionaryFile<? extends DictionaryItem>[] getDictionaryFiles() {
        final Map<String, DictionaryFile<? extends DictionaryItem>> fileMap = getDictionaryFileMap();

        final Collection<DictionaryFile<? extends DictionaryItem>> values = fileMap
                .values();
        @SuppressWarnings("unchecked")
        final DictionaryFile<? extends DictionaryItem>[] list = new DictionaryFile[values
                .size()];
        return values.toArray(list);
    }

    public DictionaryFile<? extends DictionaryItem> getDictionaryFile(
            final String id) {
        if (StringUtil.isBlank(id)) {
            return null;
        }

        final Map<String, DictionaryFile<? extends DictionaryItem>> fileMap = getDictionaryFileMap();
        return fileMap.get(id);
    }

    protected Map<String, DictionaryFile<? extends DictionaryItem>> getDictionaryFileMap() {
        synchronized (this) {
            if (lifetime > System.currentTimeMillis() && dicFileMap != null) {
                lifetime = System.currentTimeMillis() + keepAlive;
                return dicFileMap;
            }

            final Map<String, DictionaryFile<? extends DictionaryItem>> newFileMap = new LinkedHashMap<>();
            for (final DictionaryLocator locator : locatorList) {
                for (final DictionaryFile<? extends DictionaryItem> dictFile : locator
                        .find()) {
                    final String id = UUID.randomUUID().toString();
                    dictFile.setId(id);
                    newFileMap.put(id, dictFile);
                }
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
                    dicFileMap = null;
                    if (logger.isDebugEnabled()) {
                        logger.debug("Clear dictionary cache: " + dicFileMap);
                    }
                }
            }
        }
    }
}
