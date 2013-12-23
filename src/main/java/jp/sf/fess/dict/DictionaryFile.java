package jp.sf.fess.dict;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class DictionaryFile {
    public abstract String getType();

    public abstract String getName();

    public abstract PagingList<DictionaryItem> selectList(int offset, int size);

    public abstract void insert(DictionaryItem item);

    public abstract void update(DictionaryItem item);

    public abstract void delete(DictionaryItem item);

    public static class PagingList<E> implements List<E> {
        private final List<E> parent;

        protected int allRecordCount;

        protected int pageSize;

        protected int currentPageNumber;

        public PagingList(final List<E> list, final int offset, final int size,
                final int allRecordCount) {
            this.parent = list;
            this.allRecordCount = allRecordCount;
            pageSize = size;
            currentPageNumber = offset / size + 1;
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

    }
}