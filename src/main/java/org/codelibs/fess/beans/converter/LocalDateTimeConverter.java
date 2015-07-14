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
package org.codelibs.fess.beans.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.seasar.framework.beans.Converter;
import org.seasar.framework.exception.EmptyRuntimeException;
import org.seasar.framework.util.StringUtil;

public class LocalDateTimeConverter implements Converter {

    protected String pattern;

    public LocalDateTimeConverter(String pattern) {
        if (StringUtil.isEmpty(pattern)) {
            throw new EmptyRuntimeException("pattern");
        }
        this.pattern = pattern;
    }

    @Override
    public Object getAsObject(String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern));
    }

    @Override
    public String getAsString(Object value) {
        return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(pattern));
    }

    @Override
    public boolean isTarget(Class clazz) {
        return clazz == LocalDateTime.class;
    }

}
