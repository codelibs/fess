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
package org.codelibs.fess.dict.kuromoji;

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
import org.codelibs.fess.util.KuromojiCSVUtil;
import org.dbflute.optional.OptionalEntity;

public class KuromojiFile extends DictionaryFile<KuromojiItem> {
    private static final String KUROMOJI = "kuromoji";

    List<KuromojiItem> kuromojiItemList;

    public KuromojiFile(final String id, final String path, final Date timestamp) {
        super(id, path, timestamp);
    }

    @Override
    public String getType() {
        return KUROMOJI;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public synchronized OptionalEntity<KuromojiItem> get(final long id) {
        if (kuromojiItemList == null) {
            reload(null);
        }

        for (final KuromojiItem kuromojiItem : kuromojiItemList) {
            if (id == kuromojiItem.getId()) {
                return OptionalEntity.of(kuromojiItem);
            }
        }
        return OptionalEntity.empty();
    }

    @Override
    public synchronized PagingList<KuromojiItem> selectList(final int offset, final int size) {
        if (kuromojiItemList == null) {
            reload(null);
        }

        if (offset >= kuromojiItemList.size() || offset < 0) {
            return new PagingList<>(Collections.<KuromojiItem> emptyList(), offset, size, kuromojiItemList.size());
        }

        int toIndex = offset + size;
        if (toIndex > kuromojiItemList.size()) {
            toIndex = kuromojiItemList.size();
        }

        return new PagingList<>(kuromojiItemList.subList(offset, toIndex), offset, size, kuromojiItemList.size());
    }

    @Override
    public synchronized void insert(final KuromojiItem item) {
        try (KuromojiUpdater updater = new KuromojiUpdater(item)) {
            reload(updater);
        }
    }

    @Override
    public synchronized void update(final KuromojiItem item) {
        try (KuromojiUpdater updater = new KuromojiUpdater(item)) {
            reload(updater);
        }
    }

    @Override
    public synchronized void delete(final KuromojiItem item) {
        final KuromojiItem kuromojiItem = item;
        kuromojiItem.setNewToken(StringUtil.EMPTY);
        try (KuromojiUpdater updater = new KuromojiUpdater(item)) {
            reload(updater);
        }
    }

    protected void reload(final KuromojiUpdater updater) {
        try (CurlResponse curlResponse = dictionaryManager.getContentResponse(this)) {
            reload(updater, curlResponse.getContentAsStream());
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse " + path, e);
        }
    }

    protected void reload(final KuromojiUpdater updater, final InputStream in) {
        final List<KuromojiItem> itemList = new ArrayList<>();
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

                final String[] values = KuromojiCSVUtil.parse(replacedLine);
                String token = null;
                String segmentation = null;
                String reading = null;
                String pos = null;
                switch (values.length) {
                case 4:
                    pos = values[3];
                case 3:
                    reading = values[2];
                case 2:
                    segmentation = values[1];
                case 1:
                    token = values[0];
                default:
                    break;
                }

                id++;
                final KuromojiItem item = new KuromojiItem(id, token, segmentation, reading, pos);
                if (updater != null) {
                    final KuromojiItem newItem = updater.write(item);
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
                final KuromojiItem item = updater.commit();
                if (item != null) {
                    itemList.add(item);
                }
            }
            kuromojiItemList = itemList;
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse " + path, e);
        }
    }

    public String getSimpleName() {
        return new File(path).getName();
    }

    public synchronized void update(final InputStream in) throws IOException {
        try (KuromojiUpdater updater = new KuromojiUpdater(null)) {
            reload(updater, in);
        }
    }

    @Override
    public String toString() {
        return "KuromojiFile [path=" + path + ", kuromojiItemList=" + kuromojiItemList + ", id=" + id + "]";
    }

    protected class KuromojiUpdater implements Closeable {

        protected boolean isCommit = false;

        protected File newFile;

        protected Writer writer;

        protected KuromojiItem item;

        protected KuromojiUpdater(final KuromojiItem newItem) {
            try {
                newFile = ComponentUtil.getSystemHelper().createTempFile(KUROMOJI, ".txt");
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), Constants.UTF_8));
            } catch (final Exception e) {
                if (newFile != null) {
                    newFile.delete();
                }
                throw new DictionaryException("Failed to write a userDict file.", e);
            }
            item = newItem;
        }

        public KuromojiItem write(final KuromojiItem oldItem) {
            try {
                if ((item == null) || (item.getId() != oldItem.getId()) || !item.isUpdated()) {
                    writer.write(oldItem.toLineString());
                    writer.write(Constants.LINE_SEPARATOR);
                    return oldItem;
                }
                if (!item.equals(oldItem)) {
                    throw new DictionaryException("Kuromoji file was updated: old=" + oldItem + " : new=" + item);
                }
                try {
                    if (!item.isDeleted()) {
                        // update
                        writer.write(item.toLineString());
                        writer.write(Constants.LINE_SEPARATOR);
                        return new KuromojiItem(item.getId(), item.getNewToken(), item.getNewSegmentation(), item.getNewReading(),
                                item.getNewPos());
                    }
                    return null;
                } finally {
                    item.setNewToken(null);
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

        public KuromojiItem commit() {
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
                    dictionaryManager.store(KuromojiFile.this, newFile);
                } finally {
                    newFile.delete();
                }
            } else {
                newFile.delete();
            }
        }
    }

}
