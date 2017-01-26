/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.thumbnail.ThumbnailGenerator;

public abstract class BaseThumbnailGenerator implements ThumbnailGenerator {

    protected final Map<String, String> conditionMap = new HashMap<>();

    protected int directoryNameLength = 5;

    protected List<String> generatorList;

    protected Map<String, String> filePathMap = new HashMap<>();

    protected String name;

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
            String path = System.getenv("PATH");
            if (path == null) {
                path = System.getenv("Path");
            }
            if (path == null) {
                path = System.getenv("path");
            }
            final List<String> pathList = new ArrayList<>();
            pathList.add("/usr/share/fess/bin");
            if (path != null) {
                stream(path.split(File.pathSeparator)).of(stream -> stream.map(s -> s.trim()).forEach(s -> pathList.add(s)));
            }
            return generatorList.stream().map(s -> {
                if (s.startsWith("${path}")) {
                    for (final String p : pathList) {
                        final File f = new File(s.replace("${path}", p));
                        if (f.exists()) {
                            final String filePath = f.getAbsolutePath();
                            filePathMap.put(s, filePath);
                            return filePath;
                        }
                    }
                }
                return s;
            }).allMatch(s -> new File(s).isFile());
        }
        return true;
    }

    public void setDirectoryNameLength(final int directoryNameLength) {
        this.directoryNameLength = directoryNameLength;
    }

    protected String expandPath(final String value) {
        if (value != null && filePathMap.containsKey(value)) {
            return filePathMap.get(value);
        }
        return value;
    }

    public void setGeneratorList(final List<String> generatorList) {
        this.generatorList = generatorList;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}