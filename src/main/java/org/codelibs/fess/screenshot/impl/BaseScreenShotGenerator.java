/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.screenshot.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.codelibs.fess.screenshot.ScreenShotGenerator;

public abstract class BaseScreenShotGenerator implements ScreenShotGenerator {

    @Resource
    protected ServletContext application;

    protected final Map<String, String> conditionMap = new HashMap<String, String>();

    public int directoryNameLength = 5;

    public void addCondition(final String key, final String regex) {
        conditionMap.put(key, regex);
    }

    @Override
    public boolean isTarget(final Map<String, Object> docMap) {
        for (final Map.Entry<String, String> entry : conditionMap.entrySet()) {
            final Object value = docMap.get(entry.getKey());
            if (value instanceof String
                    && !((String) value).matches(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

}