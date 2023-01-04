/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.dict.stemmeroverride;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.Constants;
import org.codelibs.fess.dict.DictionaryException;
import org.codelibs.fess.dict.DictionaryFile;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;

public class StemmerOverrideFile extends DictionaryFile<StemmerOverrideItem> {
    private static final Logger logger = LogManager.getLogger(StemmerOverrideFile.class);

    private static final String STEMMER_OVERRIDE = "stemmeroverride";

    List<StemmerOverrideItem> stemmerOverrideItemList;

    public StemmerOverrideFile(final String id, final String path, final Date timestamp) {
        super(id, path, timestamp);
    }

    @Override
    public String getType() {
        return STEMMER_OVERRIDE;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public synchronized OptionalEntity<StemmerOverrideItem> get(final long id) {
        if (stemmerOverrideItemList == null) {
            reload(null);
        }

        for (final StemmerOverrideItem stemmerOverrideItem : stemmerOverrideItemList) {
            if (id == stemmerOverrideItem.getId()) {
                return OptionalEntity.of(stemmerOverrideItem);
            }
        }
        return OptionalEntity.empty();
    }

    @Override
    public synchronized PagingList<StemmerOverrideItem> selectList(final int offset, final int size) {
        if (stemmerOverrideItemList == null) {
            reload(null);
        }

        if (offset >= stemmerOverrideItemList.size() || offset < 0) {
            return new PagingList<>(Collections.<StemmerOverrideItem> emptyList(), offset, size, stemmerOverrideItemList.size());
        }

        int toIndex = offset + size;
        if (toIndex > stemmerOverrideItemList.size()) {
            toIndex = stemmerOverrideItemList.size();
        }

        return new PagingList<>(stemmerOverrideItemList.subList(offset, toIndex), offset, size, stemmerOverrideItemList.size());
    }

    @Override
    public synchronized void insert(final StemmerOverrideItem item) {
        try (StemmerOverrideUpdater updater = new StemmerOverrideUpdater(item)) {
            reload(updater);
        }
    }

    @Override
    public synchronized void update(final StemmerOverrideItem item) {
        try (StemmerOverrideUpdater updater = new StemmerOverrideUpdater(item)) {
            reload(updater);
        }
    }

    @Override
    public synchronized void delete(final StemmerOverrideItem item) {
        final StemmerOverrideItem stemmerOverrideItem = item;
        stemmerOverrideItem.setNewInput(StringUtil.EMPTY);
        stemmerOverrideItem.setNewOutput(StringUtil.EMPTY);
        try (StemmerOverrideUpdater updater = new StemmerOverrideUpdater(item)) {
            reload(updater);
        }
    }

    protected void reload(final StemmerOverrideUpdater updater) {
        try (CurlResponse curlResponse = dictionaryManager.getContentResponse(this)) {
            reload(updater, curlResponse.getContentAsStream());
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse " + path, e);
        }
    }

    protected void reload(final StemmerOverrideUpdater updater, final InputStream in) {
        final Pattern parsePattern = Pattern.compile("(.*)\\s*=>\\s*(.*)\\s*$");
        final List<StemmerOverrideItem> itemList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, Constants.UTF_8))) {
            long id = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                // Remove comments
                final String replacedLine = line.replaceAll("#.*$", StringUtil.EMPTY).trim();

                // Skip empty lines or comment lines
                if (replacedLine.length() == 0) {
                    if (updater != null) {
                        updater.write(line);
                    }
                    continue;
                }

                final Matcher m = parsePattern.matcher(replacedLine);

                if (!m.find()) {
                    logger.warn("Failed to parse {} in {}", line, path);
                    if (updater != null) {
                        updater.write("# " + line);
                    }
                    continue;
                }

                final String input = m.group(1).trim();
                final String output = m.group(2).trim();

                if (input == null || output == null) {
                    logger.warn("Failed to parse {} to {}", line, path);
                    if (updater != null) {
                        updater.write("# " + line);
                    }
                    continue;
                }

                id++;
                final StemmerOverrideItem item = new StemmerOverrideItem(id, input, output);

                if (updater != null) {
                    final StemmerOverrideItem newItem = updater.write(item);
                    if (newItem != null) {
                        itemList.add(newItem);
                    } else {
                        id--;
                    }
                } else {
                    itemList.add(item);
                }
            }
            if (updater != null) {
                final StemmerOverrideItem item = updater.commit();
                if (item != null) {
                    itemList.add(item);
                }
            }
            stemmerOverrideItemList = itemList;
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse " + path, e);
        }
    }

    public String getSimpleName() {
        return new File(path).getName();
    }

    public synchronized void update(final InputStream in) throws IOException {
        try (StemmerOverrideUpdater updater = new StemmerOverrideUpdater(null)) {
            reload(updater, in);
        }
    }

    @Override
    public String toString() {
        return "StemmerOverrideFile [path=" + path + ", stemmerOverrideItemList=" + stemmerOverrideItemList + ", id=" + id + "]";
    }

    protected class StemmerOverrideUpdater implements Closeable {

        protected boolean isCommit = false;

        protected File newFile;

        protected Writer writer;

        protected StemmerOverrideItem item;

        protected StemmerOverrideUpdater(final StemmerOverrideItem newItem) {
            try {
                newFile = ComponentUtil.getSystemHelper().createTempFile(STEMMER_OVERRIDE, ".txt");
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), Constants.UTF_8));
            } catch (final Exception e) {
                if (newFile != null) {
                    newFile.delete();
                }
                throw new DictionaryException("Failed to write a userDict file.", e);
            }
            item = newItem;
        }

        public StemmerOverrideItem write(final StemmerOverrideItem oldItem) {
            try {
                if ((item == null) || (item.getId() != oldItem.getId()) || !item.isUpdated()) {
                    writer.write(oldItem.toLineString());
                    writer.write(Constants.LINE_SEPARATOR);
                    return oldItem;
                }
                if (!item.equals(oldItem)) {
                    throw new DictionaryException("StemmerOverride file was updated: old=" + oldItem + " : new=" + item);
                }
                try {
                    if (!item.isDeleted()) {
                        // update
                        writer.write(item.toLineString());
                        writer.write(Constants.LINE_SEPARATOR);
                        return new StemmerOverrideItem(item.getId(), item.getNewInput(), item.getNewOutput());
                    }
                    return null;
                } finally {
                    item.setNewInput(null);
                    item.setNewOutput(null);
                }
            } catch (final IOException e) {
                throw new DictionaryException("Failed to write: " + oldItem + " -> " + item, e);
            }
        }

        public void write(final String line) {
            try {
                writer.write(line);
                writer.write(Constants.LINE_SEPARATOR);
            } catch (final IOException e) {
                throw new DictionaryException("Failed to write: " + line, e);
            }
        }

        public StemmerOverrideItem commit() {
            isCommit = true;
            if (item != null && item.isUpdated()) {
                try {
                    writer.write(item.toLineString());
                    writer.write(Constants.LINE_SEPARATOR);
                    return item;
                } catch (final IOException e) {
                    throw new DictionaryException("Failed to write: " + item, e);
                }
            }
            return null;
        }

        @Override
        public void close() {
            try {
                writer.flush();
            } catch (final IOException e) {
                // ignore
            }
            CloseableUtil.closeQuietly(writer);

            if (isCommit) {
                try {
                    dictionaryManager.store(StemmerOverrideFile.this, newFile);
                } finally {
                    newFile.delete();
                }
            } else {
                newFile.delete();
            }
        }
    }

}
