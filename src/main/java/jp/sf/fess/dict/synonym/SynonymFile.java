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

package jp.sf.fess.dict.synonym;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.sf.fess.Constants;
import jp.sf.fess.dict.DictionaryException;
import jp.sf.fess.dict.DictionaryFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codelibs.core.util.StringUtil;
import org.codelibs.robot.util.StreamUtil;

public class SynonymFile extends DictionaryFile<SynonymItem> {
    private static final String SYNONYM = "synonym";

    private final File file;

    List<SynonymItem> synonymItemList;

    public SynonymFile(final File file) {
        this.file = file;
    }

    @Override
    public String getType() {
        return SYNONYM;
    }

    @Override
    public String getName() {
        return file.getAbsolutePath();
    }

    @Override
    public synchronized SynonymItem get(final long id) {
        for (final SynonymItem synonymItem : synonymItemList) {
            if (id == synonymItem.getId()) {
                return synonymItem;
            }
        }
        return null;
    }

    @Override
    public synchronized PagingList<SynonymItem> selectList(final int offset,
            final int size) {
        if (synonymItemList == null) {
            reload(null);
        }

        if (offset >= synonymItemList.size() || offset < 0) {
            return new PagingList<SynonymItem>(
                    Collections.<SynonymItem> emptyList(), offset, size,
                    synonymItemList.size());
        }

        int toIndex = offset + size;
        if (toIndex > synonymItemList.size()) {
            toIndex = synonymItemList.size();
        }

        return new PagingList<SynonymItem>(synonymItemList.subList(offset,
                toIndex), offset, size, synonymItemList.size());
    }

    @Override
    public synchronized void insert(final SynonymItem item) {
        final SynonymItem synonymItem = item;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true), Constants.UTF_8));
            bw.newLine();
            bw.write(synonymItem.toLineString());
            bw.flush();

            long nextId = 1;
            if (!synonymItemList.isEmpty()) {
                final SynonymItem lastItem = synonymItemList
                        .get(synonymItemList.size() - 1);
                nextId = lastItem.getId() + 1;
            }
            synonymItemList.add(new SynonymItem(nextId, synonymItem
                    .getNewInputs(), synonymItem.getNewOutputs()));
        } catch (final IOException e) {
            throw new DictionaryException("Failed to write: " + item, e);
        } finally {
            IOUtils.closeQuietly(bw);
        }
    }

    @Override
    public synchronized void update(final SynonymItem item) {
        SynonymUpdater updater = null;
        try {
            updater = new SynonymUpdater(file, item);
            reload(updater);
        } finally {
            if (updater != null) {
                updater.close();
            }
        }
    }

    @Override
    public synchronized void delete(final SynonymItem item) {
        final SynonymItem synonymItem = item;
        synonymItem.setNewInputs(StringUtil.EMPTY_STRINGS);
        synonymItem.setNewOutputs(StringUtil.EMPTY_STRINGS);
        SynonymUpdater updater = null;
        try {
            updater = new SynonymUpdater(file, synonymItem);
            reload(updater);
        } finally {
            if (updater != null) {
                updater.close();
            }
        }
    }

    private void reload(final SynonymUpdater updater) {
        final List<SynonymItem> itemList = new ArrayList<SynonymItem>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), Constants.UTF_8));
            long id = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.length() == 0 || line.charAt(0) == '#') {
                    if (updater != null) {
                        updater.write(line);
                    }
                    continue; // ignore empty lines and comments
                }

                String inputs[];
                String outputs[];

                final List<String> sides = split(line, "=>");
                if (sides.size() > 1) { // explicit mapping
                    if (sides.size() != 2) {
                        throw new DictionaryException(
                                "more than one explicit mapping specified on the same line");
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
                        final SynonymItem item = new SynonymItem(id, inputs,
                                outputs);
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
                        final SynonymItem item = new SynonymItem(id, inputs,
                                inputs);
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
                updater.commit();
            }
            synonymItemList = itemList;
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse "
                    + file.getAbsolutePath(), e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private static List<String> split(final String s, final String separator) {
        final List<String> list = new ArrayList<String>(2);
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

            char ch = s.charAt(pos++);
            if (ch == '\\') {
                sb.append(ch);
                if (pos >= end) {
                    break; // ERROR, or let it go?
                }
                ch = s.charAt(pos++);
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
                    sb.append(s.charAt(++i));
                } else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }
        return s;
    }

    protected static class SynonymUpdater {

        protected boolean isCommit = false;

        protected File oldFile;

        protected File newFile;

        protected Writer writer;

        protected SynonymItem item;

        protected SynonymUpdater(final File file, final SynonymItem newItem) {
            try {
                newFile = File.createTempFile(SYNONYM, ".txt");
                writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(newFile), Constants.UTF_8));
            } catch (final IOException e) {
                if (newFile != null) {
                    newFile.delete();
                }
                throw new DictionaryException(
                        "Failed to write a synonym file.", e);
            }
            oldFile = file;
            item = newItem;
        }

        public SynonymItem write(final SynonymItem oldItem) {
            try {
                if (item.getId() == oldItem.getId() && item.isUpdated()) {
                    if (item.equals(oldItem)) {
                        try {
                            if (!item.isDeleted()) {
                                // update
                                writer.write(item.toLineString());
                                writer.write(Constants.LINE_SEPARATOR);
                                return new SynonymItem(item.getId(),
                                        item.getNewInputs(),
                                        item.getNewOutputs());
                            } else {
                                return null;
                            }
                        } finally {
                            item.setNewInputs(null);
                            item.setNewOutputs(null);
                        }
                    } else {
                        throw new DictionaryException(
                                "Synonym file was updated: old=" + oldItem
                                        + " : new=" + item);
                    }
                } else {
                    writer.write(oldItem.toLineString());
                    writer.write(Constants.LINE_SEPARATOR);
                    return oldItem;
                }
            } catch (final IOException e) {
                throw new DictionaryException("Failed to write: " + oldItem
                        + " -> " + item, e);
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

        public void commit() {
            isCommit = true;
        }

        public void close() {
            try {
                writer.flush();
            } catch (final IOException e) {
                // ignore
            }
            IOUtils.closeQuietly(writer);

            if (isCommit) {
                try {
                    FileUtils.copyFile(newFile, oldFile);
                    newFile.delete();
                } catch (final IOException e) {
                    throw new DictionaryException("Failed to replace "
                            + oldFile.getAbsolutePath() + " with "
                            + newFile.getAbsolutePath(), e);
                }
            } else {
                newFile.delete();
            }
        }
    }

    public String getSimpleName() {
        return file.getName();
    }

    public InputStream getInputStream() throws IOException {
        return new BufferedInputStream(new FileInputStream(file));
    }

    public synchronized void update(final InputStream in) throws IOException {
        StreamUtil.drain(in, file);
        reload(null);
    }

    @Override
    public String toString() {
        return "SynonymFile [file=" + file + ", synonymItemList="
                + synonymItemList + ", id=" + id + "]";
    }
}
