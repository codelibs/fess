/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.dict.nori;

import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.Constants;
import org.codelibs.fess.dict.DictionaryException;
import org.codelibs.fess.dict.DictionaryFile;
import org.dbflute.optional.OptionalEntity;

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

public class NoriFile extends DictionaryFile<NoriItem> {
    private static final String NORI = "nori";

    List<NoriItem> noriItemList;

    public NoriFile(final String id, final String path, final Date timestamp) {
        super(id, path, timestamp);
    }

    @Override
    public String getType() {
        return NORI;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public synchronized OptionalEntity<NoriItem> get(final long id) {
        if (noriItemList == null) {
            reload(null);
        }

        for (final NoriItem noriItem : noriItemList) {
            if (id == noriItem.getId()) {
                return OptionalEntity.of(noriItem);
            }
        }
        return OptionalEntity.empty();
    }

    @Override
    public synchronized PagingList<NoriItem> selectList(final int offset, final int size) {
        if (noriItemList == null) {
            reload(null);
        }

        if (offset >= noriItemList.size() || offset < 0) {
            return new PagingList<>(Collections.<NoriItem> emptyList(), offset, size, noriItemList.size());
        }

        int toIndex = offset + size;
        if (toIndex > noriItemList.size()) {
            toIndex = noriItemList.size();
        }

        return new PagingList<>(noriItemList.subList(offset, toIndex), offset, size, noriItemList.size());
    }

    @Override
    public synchronized void insert(final NoriItem item) {
        try (NoriUpdater updater = new NoriUpdater(item)) {
            reload(updater);
        }
    }

    @Override
    public synchronized void update(final NoriItem item) {
        try (NoriUpdater updater = new NoriUpdater(item)) {
            reload(updater);
        }
    }

    @Override
    public synchronized void delete(final NoriItem item) {
        final NoriItem kuromojiItem = item;
        kuromojiItem.setNewToken(StringUtil.EMPTY);
        try (NoriUpdater updater = new NoriUpdater(item)) {
            reload(updater);
        }
    }

    protected void reload(final NoriUpdater updater) {
        try (CurlResponse curlResponse = dictionaryManager.getContentResponse(this)) {
            reload(updater, curlResponse.getContentAsStream());
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse " + path, e);
        }
    }

    protected void reload(final NoriUpdater updater, final InputStream in) {
        final List<NoriItem> itemList = new ArrayList<>();
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

                final String[] values = StringUtil.split(replacedLine, " ");
                String token = null;
                String segmentation = null;

                if (values != null) {
                    final StringBuilder sb = new StringBuilder();
                    for (int idx = 0; idx < values.length; idx++) {
                        if (idx == 0) {
                            token = values[idx];
                        } else {
                            sb.append(values[idx]);
                            if (idx < values.length - 1) {
                                sb.append(" ");
                            }
                        }
                    }
                    segmentation = sb.toString();
                }

                id++;

                final NoriItem item = new NoriItem(id, token, segmentation);
                if (updater != null) {
                    final NoriItem newItem = updater.write(item);
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
                final NoriItem item = updater.commit();
                if (item != null) {
                    itemList.add(item);
                }
            }
            noriItemList = itemList;
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse " + path, e);
        }
    }

    public String getSimpleName() {
        return new File(path).getName();
    }

    public synchronized void update(final InputStream in) throws IOException {
        try (NoriUpdater updater = new NoriUpdater(null)) {
            reload(updater, in);
        }
    }

    @Override
    public String toString() {
        return "NoriFile [path=" + path + ", noriItemList=" + noriItemList + ", id=" + id + "]";
    }

    protected class NoriUpdater implements Closeable {

        protected boolean isCommit = false;

        protected File newFile;

        protected Writer writer;

        protected NoriItem item;

        protected NoriUpdater(final NoriItem newItem) {
            try {
                newFile = File.createTempFile(NORI, ".txt");
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), Constants.UTF_8));
            } catch (final IOException e) {
                if (newFile != null) {
                    newFile.delete();
                }
                throw new DictionaryException("Failed to write a userDict file.", e);
            }
            item = newItem;
        }

        public NoriItem write(final NoriItem oldItem) {
            try {
                if (item != null && item.getId() == oldItem.getId() && item.isUpdated()) {
                    if (item.equals(oldItem)) {
                        try {
                            if (!item.isDeleted()) {
                                // update
                                writer.write(item.toLineString());
                                writer.write(Constants.LINE_SEPARATOR);
                                return new NoriItem(item.getId(), item.getNewToken(), item.getNewSegmentation());
                            } else {
                                return null;
                            }
                        } finally {
                            item.setNewToken(null);
                        }
                    } else {
                        throw new DictionaryException("Kuromoji file was updated: old=" + oldItem + " : new=" + item);
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

        public NoriItem commit() {
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
                    dictionaryManager.store(NoriFile.this, newFile);
                } finally {
                    newFile.delete();
                }
            } else {
                newFile.delete();
            }
        }
    }

}
