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
package org.codelibs.fess.dict.synonym;

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

public class SynonymFile extends DictionaryFile<SynonymItem> {
    private static final String SYNONYM = "synonym";

    List<SynonymItem> synonymItemList;

    public SynonymFile(final String id, final String path, final Date timestamp) {
        super(id, path, timestamp);
    }

    @Override
    public String getType() {
        return SYNONYM;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public synchronized OptionalEntity<SynonymItem> get(final long id) {
        if (synonymItemList == null) {
            reload(null);
        }

        for (final SynonymItem synonymItem : synonymItemList) {
            if (id == synonymItem.getId()) {
                return OptionalEntity.of(synonymItem);
            }
        }
        return OptionalEntity.empty();
    }

    @Override
    public synchronized PagingList<SynonymItem> selectList(final int offset, final int size) {
        if (synonymItemList == null) {
            reload(null);
        }

        if (offset >= synonymItemList.size() || offset < 0) {
            return new PagingList<>(Collections.<SynonymItem> emptyList(), offset, size, synonymItemList.size());
        }

        int toIndex = offset + size;
        if (toIndex > synonymItemList.size()) {
            toIndex = synonymItemList.size();
        }

        return new PagingList<>(synonymItemList.subList(offset, toIndex), offset, size, synonymItemList.size());
    }

    @Override
    public synchronized void insert(final SynonymItem item) {
        try (SynonymUpdater updater = new SynonymUpdater(item)) {
            reload(updater);
        }
    }

    @Override
    public synchronized void update(final SynonymItem item) {
        try (SynonymUpdater updater = new SynonymUpdater(item)) {
            reload(updater);
        }
    }

    @Override
    public synchronized void delete(final SynonymItem item) {
        final SynonymItem synonymItem = item;
        synonymItem.setNewInputs(StringUtil.EMPTY_STRINGS);
        synonymItem.setNewOutputs(StringUtil.EMPTY_STRINGS);
        try (SynonymUpdater updater = new SynonymUpdater(item)) {
            reload(updater);
        }
    }

    protected void reload(final SynonymUpdater updater) {
        try (CurlResponse curlResponse = dictionaryManager.getContentResponse(this)) {
            reload(updater, curlResponse.getContentAsStream());
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse " + path, e);
        }
    }

    protected void reload(final SynonymUpdater updater, final InputStream in) {
        final List<SynonymItem> itemList = new ArrayList<>();
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

                String[] inputs;
                String[] outputs;

                final List<String> sides = split(line, "=>");
                if (sides.size() > 1) { // explicit mapping
                    if (sides.size() != 2) {
                        throw new DictionaryException("more than one explicit mapping specified on the same line");
                    }
                    final List<String> inputStrings = split(sides.get(0), ",");
                    inputs = new String[inputStrings.size()];
                    for (int i = 0; i < inputs.length; i++) {
                        inputs[i] = unescape(inputStrings.get(i)).trim();
                    }

                    final List<String> outputStrings = split(sides.get(1), ",");
                    outputs = new String[outputStrings.size()];
                    for (int i = 0; i < outputs.length; i++) {
                        outputs[i] = unescape(outputStrings.get(i)).trim();
                    }

                    if (inputs.length > 0 && outputs.length > 0) {
                        id++;
                        final SynonymItem item = new SynonymItem(id, inputs, outputs);
                        if (updater != null) {
                            final SynonymItem newItem = updater.write(item);
                            if (newItem != null) {
                                itemList.add(newItem);
                            } else {
                                id--;
                            }
                        } else {
                            itemList.add(item);
                        }
                    }
                } else {
                    final List<String> inputStrings = split(line, ",");
                    inputs = new String[inputStrings.size()];
                    for (int i = 0; i < inputs.length; i++) {
                        inputs[i] = unescape(inputStrings.get(i)).trim();
                    }

                    if (inputs.length > 0) {
                        id++;
                        final SynonymItem item = new SynonymItem(id, inputs, inputs);
                        if (updater != null) {
                            final SynonymItem newItem = updater.write(item);
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
            }
            if (updater != null) {
                final SynonymItem item = updater.commit();
                if (item != null) {
                    itemList.add(item);
                }
            }
            synonymItemList = itemList;
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse " + path, e);
        }
    }

    private static List<String> split(final String s, final String separator) {
        final List<String> list = new ArrayList<>(2);
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        final int end = s.length();
        while (pos < end) {
            if (s.startsWith(separator, pos)) {
                if (sb.length() > 0) {
                    list.add(sb.toString());
                    sb = new StringBuilder();
                }
                pos += separator.length();
                continue;
            }

            char ch = s.charAt(pos);
            pos++;
            if (ch == '\\') {
                sb.append(ch);
                if (pos >= end) {
                    break; // ERROR, or let it go?
                }
                ch = s.charAt(pos);
                pos++;
            }

            sb.append(ch);
        }

        if (sb.length() > 0) {
            list.add(sb.toString());
        }

        return list;
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
        try (SynonymUpdater updater = new SynonymUpdater(null)) {
            reload(updater, in);
        }
    }

    @Override
    public String toString() {
        return "SynonymFile [path=" + path + ", synonymItemList=" + synonymItemList + ", id=" + id + "]";
    }

    protected class SynonymUpdater implements Closeable {

        protected boolean isCommit = false;

        protected File newFile;

        protected Writer writer;

        protected SynonymItem item;

        protected SynonymUpdater(final SynonymItem newItem) {
            try {
                newFile = ComponentUtil.getSystemHelper().createTempFile(SYNONYM, ".txt");
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), Constants.UTF_8));
            } catch (final Exception e) {
                if (newFile != null) {
                    newFile.delete();
                }
                throw new DictionaryException("Failed to write a userDict file.", e);
            }
            item = newItem;
        }

        public SynonymItem write(final SynonymItem oldItem) {
            try {
                if ((item == null) || (item.getId() != oldItem.getId()) || !item.isUpdated()) {
                    writer.write(oldItem.toLineString());
                    writer.write(Constants.LINE_SEPARATOR);
                    return oldItem;
                }
                if (!item.equals(oldItem)) {
                    throw new DictionaryException("Synonym file was updated: old=" + oldItem + " : new=" + item);
                }
                try {
                    if (!item.isDeleted()) {
                        // update
                        writer.write(item.toLineString());
                        writer.write(Constants.LINE_SEPARATOR);
                        return new SynonymItem(item.getId(), item.getNewInputs(), item.getNewOutputs());
                    }
                    return null;
                } finally {
                    item.setNewInputs(null);
                    item.setNewOutputs(null);
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

        public SynonymItem commit() {
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
                    dictionaryManager.store(SynonymFile.this, newFile);
                } finally {
                    newFile.delete();
                }
            } else {
                newFile.delete();
            }
        }
    }

}
