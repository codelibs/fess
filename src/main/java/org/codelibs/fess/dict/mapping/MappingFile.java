/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.io.BufferedInputStream;
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

import org.apache.commons.io.IOUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.dict.DictionaryException;
import org.codelibs.fess.dict.DictionaryFile;
import org.dbflute.optional.OptionalEntity;

public class MappingFile extends DictionaryFile<MappingItem> {
    private static final String MAPPING = "mapping";

    List<MappingItem> mappingItemList;

    public MappingFile(final String id, final String path, final Date timestamp) {
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
    public OptionalEntity<MappingItem> get(long id) {
        if (mappingItemList == null) {
            reload(null, null);
        }

        for (final MappingItem mappingItem : mappingItemList) {
            if (id == mappingItem.getId()) {
                return OptionalEntity.of(mappingItem);
            }
        }
        return OptionalEntity.empty();
    }

    @Override
    public synchronized PagingList<MappingItem> selectList(final int offset, final int size) {
        if (mappingItemList == null) {
            reload(null, null);
        }

        if (offset >= mappingItemList.size() || offset < 0) {
            return new PagingList<>(Collections.<MappingItem> emptyList(), offset, size, mappingItemList.size());
        }

        int toIndex = offset + size;
        if (toIndex > mappingItemList.size()) {
            toIndex = mappingItemList.size();
        }

        return new PagingList<>(mappingItemList.subList(offset, toIndex), offset, size, mappingItemList.size());
    }

    @Override
    public synchronized void insert(final MappingItem item) {
        try (MappingUpdater updater = new MappingUpdater(item)) {
            reload(updater, null);
        }
    }

    @Override
    public synchronized void update(final MappingItem item) {
        try (MappingUpdater updater = new MappingUpdater(item)) {
            reload(updater, null);
        }
    }

    @Override
    public synchronized void delete(final MappingItem item) {
        final MappingItem mappingItem = item;
        mappingItem.setNewInputs(StringUtil.EMPTY_STRINGS);
        mappingItem.setNewOutput(StringUtil.EMPTY);
        try (MappingUpdater updater = new MappingUpdater(item)) {
            reload(updater, null);
        }
    }

    protected void reload(final MappingUpdater updater, final InputStream in) {
        final Pattern parsePattern = Pattern.compile("(.*)\\s*=>\\s*(.*)\\s*$");
        final List<MappingItem> itemList = new ArrayList<>();
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(in != null ? in : dictionaryManager.getContentInputStream(this), Constants.UTF_8))) {
            long id = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                // Remove comments
                line = line.replaceAll("#.*$", StringUtil.EMPTY);

                // Skip empty lines or comment lines
                if (line.trim().length() == 0) {
                    if (updater != null) {
                        updater.write(line);
                    }
                    continue;
                }

                String[] inputs;
                String output;

                Matcher m = parsePattern.matcher(line.trim());

                if (!m.find()) {
                    throw new DictionaryException("Failed to parse " + path);
                }

                inputs = parseString(m.group(1).trim()).split(",");
                output = parseString(m.group(2).trim());

                if (inputs == null || output == null || inputs.length == 0) {
                    throw new DictionaryException("Failed to parse " + path);
                }

                id++;
                final MappingItem item = new MappingItem(id, inputs, output);

                if (updater != null) {
                    final MappingItem newItem = updater.write(item);
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
                final MappingItem item = updater.commit();
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

    public InputStream getInputStream() throws IOException {
        return new BufferedInputStream(dictionaryManager.getContentInputStream(this));
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

        protected MappingItem item;

        protected MappingUpdater(final MappingItem newItem) {
            try {
                newFile = File.createTempFile(MAPPING, ".txt");
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), Constants.UTF_8));
            } catch (final IOException e) {
                if (newFile != null) {
                    newFile.delete();
                }
                throw new DictionaryException("Failed to write a userDict file.", e);
            }
            item = newItem;
        }

        public MappingItem write(final MappingItem oldItem) {
            try {
                if (item != null && item.getId() == oldItem.getId() && item.isUpdated()) {
                    if (item.equals(oldItem)) {
                        try {
                            if (!item.isDeleted()) {
                                // update
                                writer.write(item.toLineString());
                                writer.write(Constants.LINE_SEPARATOR);
                                return new MappingItem(item.getId(), item.getNewInputs(), item.getNewOutput());
                            } else {
                                return null;
                            }
                        } finally {
                            item.setNewInputs(null);
                            item.setNewOutput(null);
                        }
                    } else {
                        throw new DictionaryException("Mapping file was updated: old=" + oldItem + " : new=" + item);
                    }
                } else {
                    writer.write(oldItem.toLineString());
                    writer.write(Constants.LINE_SEPARATOR);
                    return oldItem;
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

        public MappingItem commit() {
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
            IOUtils.closeQuietly(writer);

            if (isCommit) {
                try {
                    dictionaryManager.store(MappingFile.this, newFile);
                } finally {
                    newFile.delete();
                }
            } else {
                newFile.delete();
            }
        }
    }

    char[] out = new char[256];

    private String parseString(String s) {
        int readPos = 0;
        int len = s.length();
        int writePos = 0;
        while (readPos < len) {
            char c = s.charAt(readPos++);
            if (c == '\\') {
                if (readPos >= len)
                    throw new DictionaryException("Invalid escaped char in [" + s + "]");
                c = s.charAt(readPos++);
                switch (c) {
                case '\\':
                    c = '\\';
                    break;
                case 'n':
                    c = '\n';
                    break;
                case 't':
                    c = '\t';
                    break;
                case 'r':
                    c = '\r';
                    break;
                case 'b':
                    c = '\b';
                    break;
                case 'f':
                    c = '\f';
                    break;
                case 'u':
                    if (readPos + 3 >= len)
                        throw new DictionaryException("Invalid escaped char in [" + s + "]");
                    c = (char) Integer.parseInt(s.substring(readPos, readPos + 4), 16);
                    readPos += 4;
                    break;
                }
            }
            out[writePos++] = c;
        }
        return new String(out, 0, writePos);
    }
}
