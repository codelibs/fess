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
package org.codelibs.fess.dict.mapping;

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

public class CharMappingFile extends DictionaryFile<CharMappingItem> {
    private static final Logger logger = LogManager.getLogger(CharMappingFile.class);

    private static final String MAPPING = "mapping";

    List<CharMappingItem> mappingItemList;

    public CharMappingFile(final String id, final String path, final Date timestamp) {
        super(id, path, timestamp);
    }

    @Override
    public String getType() {
        return MAPPING;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public OptionalEntity<CharMappingItem> get(final long id) {
        if (mappingItemList == null) {
            reload(null);
        }

        for (final CharMappingItem mappingItem : mappingItemList) {
            if (id == mappingItem.getId()) {
                return OptionalEntity.of(mappingItem);
            }
        }
        return OptionalEntity.empty();
    }

    @Override
    public synchronized PagingList<CharMappingItem> selectList(final int offset, final int size) {
        if (mappingItemList == null) {
            reload(null);
        }

        if (offset >= mappingItemList.size() || offset < 0) {
            return new PagingList<>(Collections.<CharMappingItem> emptyList(), offset, size, mappingItemList.size());
        }

        int toIndex = offset + size;
        if (toIndex > mappingItemList.size()) {
            toIndex = mappingItemList.size();
        }

        return new PagingList<>(mappingItemList.subList(offset, toIndex), offset, size, mappingItemList.size());
    }

    @Override
    public synchronized void insert(final CharMappingItem item) {
        try (MappingUpdater updater = new MappingUpdater(item)) {
            reload(updater);
        }
    }

    @Override
    public synchronized void update(final CharMappingItem item) {
        try (MappingUpdater updater = new MappingUpdater(item)) {
            reload(updater);
        }
    }

    @Override
    public synchronized void delete(final CharMappingItem item) {
        final CharMappingItem mappingItem = item;
        mappingItem.setNewInputs(StringUtil.EMPTY_STRINGS);
        mappingItem.setNewOutput(StringUtil.EMPTY);
        try (MappingUpdater updater = new MappingUpdater(item)) {
            reload(updater);
        }
    }

    protected void reload(final MappingUpdater updater) {
        try (CurlResponse curlResponse = dictionaryManager.getContentResponse(this)) {
            reload(updater, curlResponse.getContentAsStream());
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse " + path, e);
        }
    }

    protected void reload(final MappingUpdater updater, final InputStream in) {
        final Pattern parsePattern = Pattern.compile("(.*)\\s*=>\\s*(.*)\\s*$");
        final List<CharMappingItem> itemList = new ArrayList<>();
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

                String[] inputs;
                String output;

                final Matcher m = parsePattern.matcher(replacedLine);

                if (!m.find()) {
                    logger.warn("Failed to parse {} in {}", line, path);
                    if (updater != null) {
                        updater.write("# " + line);
                    }
                    continue;
                }

                inputs = m.group(1).trim().split(",");
                output = m.group(2).trim();

                if (inputs == null || output == null || inputs.length == 0) {
                    logger.warn("Failed to parse {} in {}", line, path);
                    if (updater != null) {
                        updater.write("# " + line);
                    }
                    continue;
                }

                id++;
                final CharMappingItem item = new CharMappingItem(id, inputs, output);

                if (updater != null) {
                    final CharMappingItem newItem = updater.write(item);
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
                final CharMappingItem item = updater.commit();
                if (item != null) {
                    itemList.add(item);
                }
            }
            mappingItemList = itemList;
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse " + path, e);
        }
    }

    public String getSimpleName() {
        return new File(path).getName();
    }

    public synchronized void update(final InputStream in) throws IOException {
        try (MappingUpdater updater = new MappingUpdater(null)) {
            reload(updater, in);
        }
    }

    @Override
    public String toString() {
        return "MappingFile [path=" + path + ", mappingItemList=" + mappingItemList + ", id=" + id + "]";
    }

    protected class MappingUpdater implements Closeable {

        protected boolean isCommit = false;

        protected File newFile;

        protected Writer writer;

        protected CharMappingItem item;

        protected MappingUpdater(final CharMappingItem newItem) {
            try {
                newFile = ComponentUtil.getSystemHelper().createTempFile(MAPPING, ".txt");
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), Constants.UTF_8));
            } catch (final Exception e) {
                if (newFile != null) {
                    newFile.delete();
                }
                throw new DictionaryException("Failed to write a userDict file.", e);
            }
            item = newItem;
        }

        public CharMappingItem write(final CharMappingItem oldItem) {
            try {
                if ((item == null) || (item.getId() != oldItem.getId()) || !item.isUpdated()) {
                    writer.write(oldItem.toLineString());
                    writer.write(Constants.LINE_SEPARATOR);
                    return oldItem;
                }
                if (!item.equals(oldItem)) {
                    throw new DictionaryException("Mapping file was updated: old=" + oldItem + " : new=" + item);
                }
                try {
                    if (!item.isDeleted()) {
                        // update
                        writer.write(item.toLineString());
                        writer.write(Constants.LINE_SEPARATOR);
                        return new CharMappingItem(item.getId(), item.getNewInputs(), item.getNewOutput());
                    }
                    return null;
                } finally {
                    item.setNewInputs(null);
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

        public CharMappingItem commit() {
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
                    dictionaryManager.store(CharMappingFile.this, newFile);
                } finally {
                    newFile.delete();
                }
            } else {
                newFile.delete();
            }
        }
    }

}
