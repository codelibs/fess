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

import java.util.Base64;
import java.util.Date;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;

public abstract class DictionaryCreator {

    protected Pattern pattern;

    @Resource
    protected DictionaryManager dictionaryManager;

    protected DictionaryCreator(final String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public DictionaryFile<? extends DictionaryItem> create(final String path, final Date timestamp) {
        if (!isTarget(path)) {
            return null;
        }

        return newDictionaryFile(encodePath(path), path, timestamp);
    }

    protected String encodePath(final String path) {
        return Base64.getUrlEncoder().encodeToString(path.getBytes(Constants.CHARSET_UTF_8));
    }

    protected boolean isTarget(final String path) {
        return pattern.matcher(path).find();
    }

    protected abstract DictionaryFile<? extends DictionaryItem> newDictionaryFile(String id, String path, Date timestamp);

    public void setDictionaryManager(final DictionaryManager dictionaryManager) {
        this.dictionaryManager = dictionaryManager;
    }
}
