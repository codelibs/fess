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

public abstract class DictionaryFile<T extends DictionaryItem> {
    protected DictionaryManager dictionaryManager;

    protected String id;

    protected String path;

    protected Date timestamp;

    protected DictionaryFile(final String id, final String path, final Date timestamp) {
        this.id = id;
        this.path = path;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public DictionaryFile<T> manager(final DictionaryManager dictionaryManager) {
        this.dictionaryManager = dictionaryManager;
        return this;
    }

    public void writeOut(final WrittenStreamOut out) throws IOException {
        try (final CurlResponse curlResponse = dictionaryManager.getContentResponse(this);
                final InputStream inputStream = new BufferedInputStream(curlResponse.getContentAsStream())) {
            out.write(inputStream);
        }
    }

    public abstract String getType();

    public abstract PagingList<T> selectList(int offset, int size);

    public abstract OptionalEntity<T> get(long id);

    public abstract void insert(T item);

    public abstract void update(T item);

    public abstract void delete(T item);

    public static class PagingList<E> implements List<E> {
        private final List<E> parent;

        protected int allPageCount;

        protected int allRecordCount;

        protected int pageSize;

        protected int currentPageNumber;

        protected int pageRangeSize;

        public PagingList(final List<E> list, final int offset, final int size, final int allRecordCount) {
            this.parent = list;
            this.allRecordCount = allRecordCount;
            pageSize = size;
            currentPageNumber = offset / size + 1;
            allPageCount = (allRecordCount - 1) / size + 1;
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

        public int getAllRecordCount() {
            return allRecordCount;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getCurrentPageNumber() {
            return currentPageNumber;
        }

        public int getAllPageCount() {
            return allPageCount;
        }

        public boolean isExistPrePage() {
            return currentPageNumber != 1;
        }

        public boolean isExistNextPage() {
            return currentPageNumber != allPageCount;
        }

        public void setPageRangeSize(final int pageRangeSize) {
            this.pageRangeSize = pageRangeSize;
        }

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