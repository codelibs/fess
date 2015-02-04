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
