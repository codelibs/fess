/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;

public class SuggestResponse implements Map<String, List<String>> {
    protected String searchQuery;

    protected long execTime;

    private final Map<String, List<String>> parent = new LinkedHashMap<String, List<String>>();

    public SuggestResponse(final QueryResponse queryResponse) {
        if (queryResponse != null) {
            final SpellCheckResponse spellCheckResponse = queryResponse
                    .getSpellCheckResponse();
            for (final Map.Entry<String, Suggestion> entry : spellCheckResponse
                    .getSuggestionMap().entrySet()) {
                parent.put(entry.getKey(),
                        new SuggestResponseList(entry.getValue()));
            }
        }
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(final String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public long getExecTime() {
        return execTime;
    }

    public void setExecTime(final long execTime) {
        this.execTime = execTime;
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
    public boolean containsKey(final Object key) {
        return parent.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return parent.containsValue(value);
    }

    @Override
    public List<String> get(final Object key) {
        return parent.get(key);
    }

    @Override
    public List<String> put(final String key, final List<String> value) {
        return parent.put(key, value);
    }

    @Override
    public List<String> remove(final Object key) {
        return parent.remove(key);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends List<String>> m) {
        parent.putAll(m);
    }

    @Override
    public void clear() {
        parent.clear();
    }

    @Override
    public Set<String> keySet() {
        return parent.keySet();
    }

    @Override
    public Collection<List<String>> values() {
        return parent.values();
    }

    @Override
    public Set<java.util.Map.Entry<String, List<String>>> entrySet() {
        return parent.entrySet();
    }

    public static class SuggestResponseList implements List<String> {
        private List<String> parent;

        private final List<Integer> frequencies;

        private final int frequency;

        private final String token;

        private final int startOffset;

        private final int endOffset;

        private final int numFound;

        public SuggestResponseList(final Suggestion suggestion) {
            parent = suggestion.getAlternatives();
            if (parent == null) {
                parent = Collections.emptyList();
            }
            frequencies = suggestion.getAlternativeFrequencies();
            frequency = suggestion.getOriginalFrequency();
            token = suggestion.getToken();
            startOffset = suggestion.getStartOffset();
            endOffset = suggestion.getEndOffset();
            numFound = suggestion.getNumFound();
        }

        public List<Integer> getFrequencies() {
            return frequencies;
        }

        public int getFrequency() {
            return frequency;
        }

        public String getToken() {
            return token;
        }

        public int getStartOffset() {
            return startOffset;
        }

        public int getEndOffset() {
            return endOffset;
        }

        public int getNumFound() {
            return numFound;
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
        public Iterator<String> iterator() {
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
        public boolean add(final String e) {
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
        public boolean addAll(final Collection<? extends String> c) {
            return parent.addAll(c);
        }

        @Override
        public boolean addAll(final int index,
                final Collection<? extends String> c) {
            return parent.addAll(index, c);
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            return parent.removeAll(c);
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
        public String get(final int index) {
            return parent.get(index);
        }

        @Override
        public String set(final int index, final String element) {
            return parent.set(index, element);
        }

        @Override
        public void add(final int index, final String element) {
            parent.add(index, element);
        }

        @Override
        public String remove(final int index) {
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
        public ListIterator<String> listIterator() {
            return parent.listIterator();
        }

        @Override
        public ListIterator<String> listIterator(final int index) {
            return parent.listIterator(index);
        }

        @Override
        public List<String> subList(final int fromIndex, final int toIndex) {
            return parent.subList(fromIndex, toIndex);
        }

    }

}
