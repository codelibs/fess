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
package org.codelibs.fess.dict;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.codelibs.curl.CurlResponse;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.servlet.request.stream.WrittenStreamOut;

/**
 * Abstract base class for dictionary files that manage dictionary items.
 * A dictionary file represents a collection of dictionary items with
 * CRUD operations and pagination support.
 *
 * @param <T> the type of dictionary items managed by this file
 */
public abstract class DictionaryFile<T extends DictionaryItem> {
    /** The dictionary manager responsible for this file. */
    protected DictionaryManager dictionaryManager;

    /** The unique identifier for this dictionary file. */
    protected String id;

    /** The file path of this dictionary. */
    protected String path;

    /** The timestamp when this dictionary file was created or last modified. */
    protected Date timestamp;

    /**
     * Creates a new DictionaryFile with the specified parameters.
     *
     * @param id the unique identifier for this dictionary file
     * @param path the file path of this dictionary
     * @param timestamp the timestamp of the dictionary file
     */
    protected DictionaryFile(final String id, final String path, final Date timestamp) {
        this.id = id;
        this.path = path;
        this.timestamp = timestamp;
    }

    /**
     * Returns the unique identifier of this dictionary file.
     *
     * @return the dictionary file ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the file path of this dictionary.
     *
     * @return the file path
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the timestamp of this dictionary file.
     *
     * @return the timestamp when this dictionary was created or last modified
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the dictionary manager for this file and returns this instance.
     *
     * @param dictionaryManager the dictionary manager to set
     * @return this dictionary file instance for method chaining
     */
    public DictionaryFile<T> manager(final DictionaryManager dictionaryManager) {
        this.dictionaryManager = dictionaryManager;
        return this;
    }

    /**
     * Writes the content of this dictionary file to the provided output stream.
     *
     * @param out the output stream to write to
     * @throws IOException if an I/O error occurs during writing
     */
    public void writeOut(final WrittenStreamOut out) throws IOException {
        try (final CurlResponse curlResponse = dictionaryManager.getContentResponse(this);
                final InputStream inputStream = new BufferedInputStream(curlResponse.getContentAsStream())) {
            out.write(inputStream);
        }
    }

    /**
     * Gets the type identifier for this dictionary file.
     *
     * @return the dictionary type
     */
    public abstract String getType();

    /**
     * Retrieves a paginated list of dictionary items.
     *
     * @param offset the starting offset for pagination
     * @param size the number of items to retrieve
     * @return a paginated list of dictionary items
     */
    public abstract PagingList<T> selectList(int offset, int size);

    /**
     * Retrieves a dictionary item by its ID.
     *
     * @param id the item ID
     * @return an optional containing the item if found
     */
    public abstract OptionalEntity<T> get(long id);

    /**
     * Inserts a new dictionary item.
     *
     * @param item the item to insert
     */
    public abstract void insert(T item);

    /**
     * Updates an existing dictionary item.
     *
     * @param item the item to update
     */
    public abstract void update(T item);

    /**
     * Deletes a dictionary item.
     *
     * @param item the item to delete
     */
    public abstract void delete(T item);

    /**
     * A paginated list implementation that wraps another list and provides
     * pagination metadata and functionality.
     *
     * @param <E> the type of elements in this list
     */
    public static class PagingList<E> implements List<E> {
        /** The underlying list containing the actual data. */
        private final List<E> parent;

        /** The total number of pages available. */
        protected int allPageCount;

        /** The total number of records across all pages. */
        protected int allRecordCount;

        /** The number of records per page. */
        protected int pageSize;

        /** The current page number (1-based). */
        protected int currentPageNumber;

        /** The size of the page range for navigation. */
        protected int pageRangeSize;

        /**
         * Creates a new PagingList with the specified parameters.
         *
         * @param list the underlying list of items for this page
         * @param offset the starting offset for this page
         * @param size the page size
         * @param allRecordCount the total number of records across all pages
         */
        public PagingList(final List<E> list, final int offset, final int size, final int allRecordCount) {
            parent = list;
            this.allRecordCount = allRecordCount;
            pageSize = size;
            currentPageNumber = offset / size + 1;
            allPageCount = allRecordCount == 0 ? 0 : (allRecordCount - 1) / size + 1;
        }

        @Override
        public int size() {
            return parent.size();
        }

        @Override
        public boolean isEmpty() {
            return parent.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return parent.contains(o);
        }

        @Override
        public Iterator<E> iterator() {
            return parent.iterator();
        }

        @Override
        public Object[] toArray() {
            return parent.toArray();
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            return parent.toArray(a);
        }

        @Override
        public boolean add(final E e) {
            return parent.add(e);
        }

        @Override
        public boolean remove(final Object o) {
            return parent.remove(o);
        }

        @Override
        public boolean containsAll(final Collection<?> c) {
            return parent.containsAll(c);
        }

        @Override
        public boolean addAll(final Collection<? extends E> c) {
            return parent.addAll(c);
        }

        @Override
        public boolean addAll(final int index, final Collection<? extends E> c) {
            return parent.addAll(index, c);
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            return parent.retainAll(c);
        }

        @Override
        public boolean retainAll(final Collection<?> c) {
            return parent.retainAll(c);
        }

        @Override
        public void clear() {
            parent.clear();
        }

        @Override
        public E get(final int index) {
            return parent.get(index);
        }

        @Override
        public E set(final int index, final E element) {
            return parent.set(index, element);
        }

        @Override
        public void add(final int index, final E element) {
            parent.add(index, element);
        }

        @Override
        public E remove(final int index) {
            return parent.remove(index);
        }

        @Override
        public int indexOf(final Object o) {
            return parent.indexOf(o);
        }

        @Override
        public int lastIndexOf(final Object o) {
            return parent.lastIndexOf(o);
        }

        @Override
        public ListIterator<E> listIterator() {
            return parent.listIterator();
        }

        @Override
        public ListIterator<E> listIterator(final int index) {
            return parent.listIterator(index);
        }

        @Override
        public List<E> subList(final int fromIndex, final int toIndex) {
            return parent.subList(fromIndex, toIndex);
        }

        /**
         * Returns the total number of records across all pages.
         *
         * @return the total record count
         */
        public int getAllRecordCount() {
            return allRecordCount;
        }

        /**
         * Returns the page size (number of records per page).
         *
         * @return the page size
         */
        public int getPageSize() {
            return pageSize;
        }

        /**
         * Returns the current page number (1-based).
         *
         * @return the current page number
         */
        public int getCurrentPageNumber() {
            return currentPageNumber;
        }

        /**
         * Returns the total number of pages.
         *
         * @return the total page count
         */
        public int getAllPageCount() {
            return allPageCount;
        }

        /**
         * Checks if a previous page exists.
         *
         * @return true if there is a previous page, false otherwise
         */
        public boolean isExistPrePage() {
            return currentPageNumber != 1;
        }

        /**
         * Checks if a next page exists.
         *
         * @return true if there is a next page, false otherwise
         */
        public boolean isExistNextPage() {
            return currentPageNumber != allPageCount;
        }

        /**
         * Sets the page range size for navigation.
         *
         * @param pageRangeSize the page range size to set
         */
        public void setPageRangeSize(final int pageRangeSize) {
            this.pageRangeSize = pageRangeSize;
        }

        /**
         * Creates a list of page numbers for navigation.
         *
         * @return a list of page numbers within the page range
         */
        public List<Integer> createPageNumberList() {
            int startPage = currentPageNumber - pageRangeSize;
            if (startPage < 1) {
                startPage = 1;
            }
            int endPage = currentPageNumber + pageRangeSize;
            if (endPage > allPageCount) {
                endPage = allPageCount;
            }
            final List<Integer> pageNumberList = new ArrayList<>();
            for (int i = startPage; i <= endPage; i++) {
                pageNumberList.add(i);
            }
            return pageNumberList;
        }

    }

}