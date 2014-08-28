/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.dict.userdict;

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
import org.apache.lucene.analysis.ja.util.CSVUtil;
import org.codelibs.core.util.StringUtil;
import org.codelibs.robot.util.StreamUtil;

public class UserDictFile extends DictionaryFile<UserDictItem> {
    private static final String USERDICT = "userDict";

    private final File file;

    List<UserDictItem> userDictItemList;

    public UserDictFile(final File file) {
        this.file = file;
    }

    @Override
    public String getType() {
        return USERDICT;
    }

    @Override
    public String getName() {
        return file.getAbsolutePath();
    }

    @Override
    public UserDictItem get(final long id) {
        for (final UserDictItem userDictItem : userDictItemList) {
            if (id == userDictItem.getId()) {
                return userDictItem;
            }
        }
        return null;
    }

    @Override
    public synchronized PagingList<UserDictItem> selectList(final int offset,
            final int size) {
        if (userDictItemList == null) {
            reload(null);
        }

        if (offset >= userDictItemList.size() || offset < 0) {
            return new PagingList<UserDictItem>(
                    Collections.<UserDictItem> emptyList(), offset, size,
                    userDictItemList.size());
        }

        int toIndex = offset + size;
        if (toIndex > userDictItemList.size()) {
            toIndex = userDictItemList.size();
        }

        return new PagingList<UserDictItem>(userDictItemList.subList(offset,
                toIndex), offset, size, userDictItemList.size());
    }

    @Override
    public synchronized void insert(final UserDictItem item) {
        final UserDictItem userDictItem = item;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true), Constants.UTF_8));
            bw.newLine();
            bw.write(userDictItem.toLineString());
            bw.flush();

            long nextId = 1;
            if (!userDictItemList.isEmpty()) {
                final UserDictItem lastItem = userDictItemList
                        .get(userDictItemList.size() - 1);
                nextId = lastItem.getId() + 1;
            }
            userDictItemList.add(new UserDictItem(nextId, userDictItem
                    .getNewToken(), userDictItem.getNewSegmentation(),
                    userDictItem.getNewReading(), userDictItem.getNewPos()));
        } catch (final IOException e) {
            throw new DictionaryException("Failed to write: " + item, e);
        } finally {
            IOUtils.closeQuietly(bw);
        }
    }

    @Override
    public synchronized void update(final UserDictItem item) {
        UserDictUpdater updater = null;
        try {
            updater = new UserDictUpdater(file, item);
            reload(updater);
        } finally {
            if (updater != null) {
                updater.close();
            }
        }
    }

    @Override
    public synchronized void delete(final UserDictItem item) {
        final UserDictItem userDictItem = item;
        userDictItem.setNewToken(StringUtil.EMPTY);
        UserDictUpdater updater = null;
        try {
            updater = new UserDictUpdater(file, userDictItem);
            reload(updater);
        } finally {
            if (updater != null) {
                updater.close();
            }
        }
    }

    protected void reload(final UserDictUpdater updater) {
        final List<UserDictItem> itemList = new ArrayList<UserDictItem>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), Constants.UTF_8));
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

                final String[] values = CSVUtil.parse(line);
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
                final UserDictItem item = new UserDictItem(id, token,
                        segmentation, reading, pos);
                if (updater != null) {
                    final UserDictItem newItem = updater.write(item);
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
                updater.commit();
            }
            userDictItemList = itemList;
        } catch (final IOException e) {
            throw new DictionaryException("Failed to parse "
                    + file.getAbsolutePath(), e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    protected static class UserDictUpdater {

        protected boolean isCommit = false;

        protected File oldFile;

        protected File newFile;

        protected Writer writer;

        protected UserDictItem item;

        protected UserDictUpdater(final File file, final UserDictItem newItem) {
            try {
                newFile = File.createTempFile(USERDICT, ".txt");
                writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(newFile), Constants.UTF_8));
            } catch (final IOException e) {
                if (newFile != null) {
                    newFile.delete();
                }
                throw new DictionaryException(
                        "Failed to write a userDict file.", e);
            }
            oldFile = file;
            item = newItem;
        }

        public UserDictItem write(final UserDictItem oldItem) {
            try {
                if (item.getId() == oldItem.getId() && item.isUpdated()) {
                    if (item.equals(oldItem)) {
                        try {
                            if (!item.isDeleted()) {
                                // update
                                writer.write(item.toLineString());
                                writer.write(Constants.LINE_SEPARATOR);
                                return new UserDictItem(item.getId(),
                                        item.getNewToken(),
                                        item.getNewSegmentation(),
                                        item.getNewReading(), item.getNewPos());
                            } else {
                                return null;
                            }
                        } finally {
                            item.setNewToken(null);
                        }
                    } else {
                        throw new DictionaryException(
                                "UserDict file was updated: old=" + oldItem
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

    public void update(InputStream in) throws IOException {
        StreamUtil.drain(in, file);
        reload(null);
    }
}
