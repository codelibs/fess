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

import java.util.Base64;
import java.util.Date;
import java.util.regex.Pattern;

import org.codelibs.fess.Constants;

import jakarta.annotation.Resource;

/**
 * Abstract base class for creating dictionary files from file paths.
 * Dictionary creators are responsible for recognizing specific file patterns
 * and creating appropriate DictionaryFile instances for them.
 */
public abstract class DictionaryCreator {

    /** Pattern used to match file paths that this creator can handle. */
    protected Pattern pattern;

    /** Manager for dictionary operations and lifecycle. */
    @Resource
    protected DictionaryManager dictionaryManager;

    /**
     * Creates a new DictionaryCreator with the specified pattern.
     *
     * @param pattern the regular expression pattern to match file paths
     */
    protected DictionaryCreator(final String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    /**
     * Creates a dictionary file for the given path and timestamp if it matches this creator's pattern.
     *
     * @param path the file path to create a dictionary file for
     * @param timestamp the timestamp of the dictionary file
     * @return a DictionaryFile instance if the path matches, null otherwise
     */
    public DictionaryFile<? extends DictionaryItem> create(final String path, final Date timestamp) {
        if (!isTarget(path)) {
            return null;
        }

        return newDictionaryFile(encodePath(path), path, timestamp);
    }

    /**
     * Encodes a file path using Base64 URL-safe encoding.
     *
     * @param path the file path to encode
     * @return the Base64 encoded path
     */
    protected String encodePath(final String path) {
        return Base64.getUrlEncoder().encodeToString(path.getBytes(Constants.CHARSET_UTF_8));
    }

    /**
     * Checks if the given path matches this creator's pattern.
     *
     * @param path the file path to check
     * @return true if the path matches the pattern, false otherwise
     */
    protected boolean isTarget(final String path) {
        return pattern.matcher(path).find();
    }

    /**
     * Creates a new dictionary file instance for the given parameters.
     *
     * @param id the encoded identifier for the dictionary file
     * @param path the file path of the dictionary
     * @param timestamp the timestamp of the dictionary file
     * @return a new DictionaryFile instance
     */
    protected abstract DictionaryFile<? extends DictionaryItem> newDictionaryFile(String id, String path, Date timestamp);

    /**
     * Sets the dictionary manager for this creator.
     *
     * @param dictionaryManager the dictionary manager to set
     */
    public void setDictionaryManager(final DictionaryManager dictionaryManager) {
        this.dictionaryManager = dictionaryManager;
    }
}
