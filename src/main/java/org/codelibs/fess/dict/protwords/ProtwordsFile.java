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
package org.codelibs.fess.dict.protwords;

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

import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.Constants;
import org.codelibs.fess.dict.DictionaryException;
import org.codelibs.fess.dict.DictionaryFile;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;

public class ProtwordsFile extends DictionaryFile<ProtwordsItem> {
    private static final String PROTWORDS = "protwords";

    List<ProtwordsItem> protwordsItemList;

    public ProtwordsFile(final String id, final String path, final Date timestamp) {
        super(id, path, timestamp);
    }

    @Override
    public String getType() {
        return PROTWORDS;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public synchronized OptionalEntity<ProtwordsItem> get(final long id) {
        if (protwordsItemList == null) {
            reload(null);
        }

        for (final ProtwordsItem ProtwordsItem : protwordsItemList) {
            if (id == ProtwordsItem.getId()) {
                return OptionalEntity.of(ProtwordsItem);
            }
        }
        return OptionalEntity.empty();
    }

    @Override
    public synchronized PagingList<ProtwordsItem> selectList(final int offset, final int size) {
        if (protwordsItemList == null) {
            reload(null);
        }

        if (offset >= protwordsItemList.size() || offset < 0) {
            return new PagingList<>(Collections.<ProtwordsItem> emptyList(), offset, size, protwordsItemList.size());
        }

        int toIndex = offset + size;
        if (toIndex > protwordsItemList.size()) {
            toIndex = protwordsItemList.size();
        }

        return new PagingList<>(protwordsItemList.subList(offset, toIndex), offset, size, protwordsItemList.size());
    }

    @Override
    public synchronized void insert(final ProtwordsItem item) {
        try (ProtwordsUpdater updater = new ProtwordsUpdater(item)) {
            reload(updater);
        }
    }

    @Override
    public synchronized void update(final ProtwordsItem item) {
        try (ProtwordsUpdater updater = new ProtwordsUpdater(item)) {
            reload(updater);
        }
    }

    @Override
    public synchronized void delete(final ProtwordsItem item) {
        final ProtwordsItem ProtwordsItem = item;
        ProtwordsItem.setNewInput(StringUtil.EMPTY);
        try (ProtwordsUpdater updater = new ProtwordsUpdater(item)) {
            reload(updater);
        }
    }

    protected void reload(final ProtwordsUpdater updater) {
        try (CurlResponse curlResponse = dictionaryManager.getContentResponse(this)) {
            reload(updater, curlResponse.getContentAsStream());
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse " + path, e);
        }
    }

    protected void reload(final ProtwordsUpdater updater, final InputStream in) {
        final List<ProtwordsItem> itemList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, Constants.UTF_8))) {
            long id = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.length() == 0 || line.charAt(0) == '#') {
                    if (updater != null) {
                        updater.write(line);
                    }
                    continue; // ignore empty lines and comments
                }

                final String inputStrings = line;
                final String input = unescape(inputStrings);

                if (input.length() > 0) {
                    id++;
                    final ProtwordsItem item = new ProtwordsItem(id, input);
                    if (updater != null) {
                        final ProtwordsItem newItem = updater.write(item);
                        if (newItem != null) {
                            itemList.add(newItem);
                        } else {
                            id--;
                        }
                    } else {
                        itemList.add(item);
                    }
                }
            }
            if (updater != null) {
                final ProtwordsItem item = updater.commit();
                if (item != null) {
                    itemList.add(item);
                }
            }
            protwordsItemList = itemList;
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse " + path, e);
        }
    }

    private String unescape(final String s) {
        if (s.indexOf('\\') >= 0) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                final char ch = s.charAt(i);
                if (ch == '\\' && i < s.length() - 1) {
                    i++;
                    sb.append(s.charAt(i));
                } else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }
        return s;
    }

    public String getSimpleName() {
        return new File(path).getName();
    }

    public synchronized void update(final InputStream in) throws IOException {
        try (ProtwordsUpdater updater = new ProtwordsUpdater(null)) {
            reload(updater, in);
        }
    }

    @Override
    public String toString() {
        return "ProtwordsFile [path=" + path + ", protwordsItemList=" + protwordsItemList + ", id=" + id + "]";
    }

    protected class ProtwordsUpdater implements Closeable {

        protected boolean isCommit = false;

        protected File newFile;

        protected Writer writer;

        protected ProtwordsItem item;

        protected ProtwordsUpdater(final ProtwordsItem newItem) {
            try {
                newFile = ComponentUtil.getSystemHelper().createTempFile(PROTWORDS, ".txt");
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), Constants.UTF_8));
            } catch (final Exception e) {
                if (newFile != null) {
                    newFile.delete();
                }
                throw new DictionaryException("Failed to write a userDict file.", e);
            }
            item = newItem;
        }

        public ProtwordsItem write(final ProtwordsItem oldItem) {
            try {
                if ((item == null) || (item.getId() != oldItem.getId()) || !item.isUpdated()) {
                    writer.write(oldItem.toLineString());
                    writer.write(Constants.LINE_SEPARATOR);
                    return oldItem;
                }
                if (!item.equals(oldItem)) {
                    throw new DictionaryException("Protwords file was updated: old=" + oldItem + " : new=" + item);
                }
                try {
                    if (!item.isDeleted()) {
                        // update
                        writer.write(item.toLineString());
                        writer.write(Constants.LINE_SEPARATOR);
                        return new ProtwordsItem(item.getId(), item.getNewInput());
                    }
                    return null;
                } finally {
                    item.setNewInput(null);
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

        public ProtwordsItem commit() {
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
                    dictionaryManager.store(ProtwordsFile.this, newFile);
                } finally {
                    newFile.delete();
                }
            } else {
                newFile.delete();
            }
        }
    }
}
