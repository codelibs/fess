/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.dict.stopwords;

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

/**
 * Manages a dictionary file for stopwords.
 * This class handles reading, parsing, and updating files that contain
 * a list of stopwords. Each line in the file represents a single stopword.
 *
 * The class provides methods for retrieving, adding, updating, and
 * deleting stopword items, as well as reloading the dictionary
 * from its source file.
 */
public class StopwordsFile extends DictionaryFile<StopwordsItem> {
    private static final String STOPWORDS = "stopwords";

    /** The list of stopword items loaded from the dictionary file. */
    List<StopwordsItem> stopwordsItemList;

    /**
     * Constructs a new stopwords file.
     *
     * @param id        The unique identifier for this dictionary file.
     * @param path      The path to the dictionary file.
     * @param timestamp The last modified timestamp of the file.
     */
    public StopwordsFile(final String id, final String path, final Date timestamp) {
        super(id, path, timestamp);
    }

    @Override
    public String getType() {
        return STOPWORDS;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public synchronized OptionalEntity<StopwordsItem> get(final long id) {
        if (stopwordsItemList == null) {
            reload(null);
        }

        for (final StopwordsItem StopwordsItem : stopwordsItemList) {
            if (id == StopwordsItem.getId()) {
                return OptionalEntity.of(StopwordsItem);
            }
        }
        return OptionalEntity.empty();
    }

    @Override
    public synchronized PagingList<StopwordsItem> selectList(final int offset, final int size) {
        if (stopwordsItemList == null) {
            reload(null);
        }

        if (offset >= stopwordsItemList.size() || offset < 0) {
            return new PagingList<>(Collections.<StopwordsItem> emptyList(), offset, size, stopwordsItemList.size());
        }

        int toIndex = offset + size;
        if (toIndex > stopwordsItemList.size()) {
            toIndex = stopwordsItemList.size();
        }

        return new PagingList<>(stopwordsItemList.subList(offset, toIndex), offset, size, stopwordsItemList.size());
    }

    @Override
    public synchronized void insert(final StopwordsItem item) {
        try (StopwordsUpdater updater = new StopwordsUpdater(item)) {
            reload(updater);
        }
    }

    @Override
    public synchronized void update(final StopwordsItem item) {
        try (StopwordsUpdater updater = new StopwordsUpdater(item)) {
            reload(updater);
        }
    }

    @Override
    public synchronized void delete(final StopwordsItem item) {
        final StopwordsItem stopwordsItem = item;
        stopwordsItem.setNewInput(StringUtil.EMPTY);
        try (StopwordsUpdater updater = new StopwordsUpdater(item)) {
            reload(updater);
        }
    }

    /**
     * Reloads the stopwords dictionary from its source file.
     *
     * @param updater An optional updater to apply changes during reload.
     * @throws DictionaryException if the dictionary file cannot be read.
     */
    protected void reload(final StopwordsUpdater updater) {
        try (CurlResponse curlResponse = dictionaryManager.getContentResponse(this)) {
            reload(updater, curlResponse.getContentAsStream());
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse " + path, e);
        }
    }

    /**
     * Reloads the stopwords dictionary from an input stream.
     *
     * @param updater An optional updater to apply changes.
     * @param in      The input stream to read the dictionary from.
     * @throws DictionaryException if the input stream cannot be parsed.
     */
    protected void reload(final StopwordsUpdater updater, final InputStream in) {
        final List<StopwordsItem> itemList = new ArrayList<>();
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
                    final StopwordsItem item = new StopwordsItem(id, input);
                    if (updater != null) {
                        final StopwordsItem newItem = updater.write(item);
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
                final StopwordsItem item = updater.commit();
                if (item != null) {
                    itemList.add(item);
                }
            }
            stopwordsItemList = itemList;
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

    /**
     * Returns the simple name of the dictionary file.
     *
     * @return The file name without the path.
     */
    public String getSimpleName() {
        return new File(path).getName();
    }

    /**
     * Updates the dictionary file with content from an input stream.
     *
     * @param in The input stream containing the new dictionary content.
     * @throws IOException if an I/O error occurs.
     */
    public synchronized void update(final InputStream in) throws IOException {
        try (StopwordsUpdater updater = new StopwordsUpdater(null)) {
            reload(updater, in);
        }
    }

    @Override
    public String toString() {
        return "StopwordsFile [path=" + path + ", stopwordsItemList=" + stopwordsItemList + ", id=" + id + "]";
    }

    /**
     * An inner class for updating the stopwords file.
     * This class handles the process of writing changes to a temporary file
     * and then replacing the original file upon successful commit.
     */
    protected class StopwordsUpdater implements Closeable {

        /** A flag indicating whether the changes have been committed. */
        protected boolean isCommit = false;

        /** The temporary file to write changes to. */
        protected File newFile;

        /** The writer for the temporary file. */
        protected Writer writer;

        /** The stopword item being added or updated. */
        protected StopwordsItem item;

        /**
         * Constructs a new updater for a stopword item.
         *
         * @param newItem The item to be added or updated.
         * @throws DictionaryException if the temporary file cannot be created.
         */
        protected StopwordsUpdater(final StopwordsItem newItem) {
            try {
                newFile = ComponentUtil.getSystemHelper().createTempFile(STOPWORDS, ".txt");
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), Constants.UTF_8));
            } catch (final Exception e) {
                if (newFile != null) {
                    newFile.delete();
                }
                throw new DictionaryException("Failed to write a userDict file.", e);
            }
            item = newItem;
        }

        /**
         * Writes a stopword item to the temporary file.
         * If the item is being updated, it writes the new version.
         *
         * @param oldItem The original item from the dictionary.
         * @return The written item, or null if the item was deleted.
         * @throws DictionaryException if the file was updated concurrently.
         */
        public StopwordsItem write(final StopwordsItem oldItem) {
            try {
                if (item == null || item.getId() != oldItem.getId() || !item.isUpdated()) {
                    writer.write(oldItem.toLineString());
                    writer.write(Constants.LINE_SEPARATOR);
                    return oldItem;
                }
                if (!item.equals(oldItem)) {
                    throw new DictionaryException("Stopwords file was updated: old=" + oldItem + " : new=" + item);
                }
                try {
                    if (!item.isDeleted()) {
                        // update
                        writer.write(item.toLineString());
                        writer.write(Constants.LINE_SEPARATOR);
                        return new StopwordsItem(item.getId(), item.getNewInput());
                    }
                    return null;
                } finally {
                    item.setNewInput(null);
                }
            } catch (final IOException e) {
                throw new DictionaryException("Failed to write: " + oldItem + " -> " + item, e);
            }
        }

        /**
         * Writes a raw line to the temporary file.
         *
         * @param line The line to write.
         * @throws DictionaryException if an I/O error occurs.
         */
        public void write(final String line) {
            try {
                writer.write(line);
                writer.write(Constants.LINE_SEPARATOR);
            } catch (final IOException e) {
                throw new DictionaryException("Failed to write: " + line, e);
            }
        }

        /**
         * Commits the changes to the dictionary file.
         * If there is a pending new item, it is written to the file.
         *
         * @return The committed item, or null if no item was committed.
         * @throws DictionaryException if an I/O error occurs.
         */
        public StopwordsItem commit() {
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
                    dictionaryManager.store(StopwordsFile.this, newFile);
                } finally {
                    newFile.delete();
                }
            } else {
                newFile.delete();
            }
        }
    }
}