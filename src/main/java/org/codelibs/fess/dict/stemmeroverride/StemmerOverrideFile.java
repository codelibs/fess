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

/**
 * Manages a dictionary file for stemmer overrides.
 * This class handles reading, parsing, and updating files that contain
 * stemmer override rules, where each rule maps an input word to an
 * output stem. The file format is expected to be `input => output`.
 *
 * The class provides methods for retrieving, adding, updating, and
 * deleting stemmer override items, as well as reloading the dictionary
 * from its source file.
 */
public class StemmerOverrideFile extends DictionaryFile<StemmerOverrideItem> {
    private static final Logger logger = LogManager.getLogger(StemmerOverrideFile.class);

    private static final String STEMMER_OVERRIDE = "stemmeroverride";

    /** The list of stemmer override items loaded from the dictionary file. */
    List<StemmerOverrideItem> stemmerOverrideItemList;

    /**
     * Constructs a new stemmer override file.
     *
     * @param id        The unique identifier for this dictionary file.
     * @param path      The path to the dictionary file.
     * @param timestamp The last modified timestamp of the file.
     */
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

    /**
     * Reloads the stemmer override dictionary from its source file.
     *
     * @param updater An optional updater to apply changes during reload.
     * @throws DictionaryException if the dictionary file cannot be read.
     */
    protected void reload(final StemmerOverrideUpdater updater) {
        try (CurlResponse curlResponse = dictionaryManager.getContentResponse(this)) {
            reload(updater, curlResponse.getContentAsStream());
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse " + path, e);
        }
    }

    /**
     * Reloads the stemmer override dictionary from an input stream.
     *
     * @param updater An optional updater to apply changes.
     * @param in      The input stream to read the dictionary from.
     * @throws DictionaryException if the input stream cannot be parsed.
     */
    protected void reload(final StemmerOverrideUpdater updater, final InputStream in) {
        final Pattern parsePattern = Pattern.compile("(.*?)\\s*+=>\\s*+(.*?)\\s*+$");
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
                    logger.warn("Failed to parse stemmer override: line={}, path={}", line, path);
                    if (updater != null) {
                        updater.write("# " + line);
                    }
                    continue;
                }

                final String input = m.group(1).trim();
                final String output = m.group(2).trim();

                if (input == null || output == null) {
                    logger.warn("Failed to parse stemmer override: line={}, path={}", line, path);
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
        try (StemmerOverrideUpdater updater = new StemmerOverrideUpdater(null)) {
            reload(updater, in);
        }
    }

    @Override
    public String toString() {
        return "StemmerOverrideFile [path=" + path + ", stemmerOverrideItemList=" + stemmerOverrideItemList + ", id=" + id + "]";
    }

    /**
     * An inner class for updating the stemmer override file.
     * This class handles the process of writing changes to a temporary file
     * and then replacing the original file upon successful commit.
     */
    protected class StemmerOverrideUpdater implements Closeable {

        /** A flag indicating whether the changes have been committed. */
        protected boolean isCommit = false;

        /** The temporary file to write changes to. */
        protected File newFile;

        /** The writer for the temporary file. */
        protected Writer writer;

        /** The stemmer override item being added or updated. */
        protected StemmerOverrideItem item;

        /**
         * Constructs a new updater for a stemmer override item.
         *
         * @param newItem The item to be added or updated.
         * @throws DictionaryException if the temporary file cannot be created.
         */
        protected StemmerOverrideUpdater(final StemmerOverrideItem newItem) {
            FileOutputStream fos = null;
            try {
                newFile = ComponentUtil.getSystemHelper().createTempFile(STEMMER_OVERRIDE, ".txt");
                fos = new FileOutputStream(newFile);
                writer = new BufferedWriter(new OutputStreamWriter(fos, Constants.UTF_8));
                fos = null; // Successfully wrapped, no need to close explicitly
            } catch (final Exception e) {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (final IOException ioe) {
                        // Ignore close exception
                    }
                }
                if (newFile != null) {
                    newFile.delete();
                }
                throw new DictionaryException("Failed to write a userDict file.", e);
            }
            item = newItem;
        }

        /**
         * Writes a stemmer override item to the temporary file.
         * If the item is being updated, it writes the new version.
         *
         * @param oldItem The original item from the dictionary.
         * @return The written item, or null if the item was deleted.
         * @throws DictionaryException if the file was updated concurrently.
         */
        public StemmerOverrideItem write(final StemmerOverrideItem oldItem) {
            try {
                if (item == null || item.getId() != oldItem.getId() || !item.isUpdated()) {
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