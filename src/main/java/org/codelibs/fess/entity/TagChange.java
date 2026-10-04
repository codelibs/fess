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
package org.codelibs.fess.entity;

/**
 * A change of user tags waiting to be applied to the documents of the search index.
 *
 * @param type the kind of change
 * @param value the tag value; the old value for {@link Type#RENAME}
 * @param newValue the new tag value of {@link Type#RENAME}, otherwise null
 * @param url the URL of the documents of {@link Type#ADD} and {@link Type#REMOVE}, otherwise null
 */
public record TagChange(Type type, String value, String newValue, String url) {

    /** The kind of a tag change. */
    public enum Type {
        /** Puts a tag on the documents of a URL. */
        ADD,
        /** Takes a tag off the documents of a URL. */
        REMOVE,
        /** Takes a tag off every document. */
        DELETE,
        /** Replaces a tag value with another on every document. */
        RENAME
    }

    /**
     * Creates a change that puts a tag on the documents of a URL.
     *
     * @param value the tag value
     * @param url the URL
     * @return the change
     */
    public static TagChange add(final String value, final String url) {
        return new TagChange(Type.ADD, value, null, url);
    }

    /**
     * Creates a change that takes a tag off the documents of a URL.
     *
     * @param value the tag value
     * @param url the URL
     * @return the change
     */
    public static TagChange remove(final String value, final String url) {
        return new TagChange(Type.REMOVE, value, null, url);
    }

    /**
     * Creates a change that takes a tag off every document.
     *
     * @param value the tag value
     * @return the change
     */
    public static TagChange delete(final String value) {
        return new TagChange(Type.DELETE, value, null, null);
    }

    /**
     * Creates a change that replaces a tag value with another on every document.
     *
     * @param oldValue the current tag value
     * @param newValue the new tag value
     * @return the change
     */
    public static TagChange rename(final String oldValue, final String newValue) {
        return new TagChange(Type.RENAME, oldValue, newValue, null);
    }
}
