/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.thumbnail.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.codelibs.fess.thumbnail.ThumbnailGenerator;

public abstract class BaseThumbnailGenerator implements ThumbnailGenerator {

    @Resource
    protected ServletContext application;

    protected final Map<String, String> conditionMap = new HashMap<>();

    protected int directoryNameLength = 5;

    public List<String> generatorList;

    public void addCondition(final String key, final String regex) {
        conditionMap.put(key, regex);
    }

    @Override
    public boolean isTarget(final Map<String, Object> docMap) {
        for (final Map.Entry<String, String> entry : conditionMap.entrySet()) {
            final Object value = docMap.get(entry.getKey());
            if (value instanceof String && !((String) value).matches(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isAvailable() {
        if (generatorList != null && !generatorList.isEmpty()) {
            return generatorList.stream().allMatch(s -> new File(s).isFile());
        }
        return true;
    }

    public void setDirectoryNameLength(int directoryNameLength) {
        this.directoryNameLength = directoryNameLength;
    }

}