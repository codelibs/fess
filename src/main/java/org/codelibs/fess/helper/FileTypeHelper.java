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
package org.codelibs.fess.helper;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.util.ComponentUtil;

public class FileTypeHelper {
    private static final Logger logger = LogManager.getLogger(FileTypeHelper.class);

    protected String defaultValue = "others";

    protected Map<String, String> mimetypeMap = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        StreamUtil.split(ComponentUtil.getFessConfig().getIndexFiletype(), "\n")
                .of(stream -> stream.filter(StringUtil::isNotBlank).forEach(s -> {
                    final String[] values = StringUtils.split(s, "=", 2);
                    if (values.length == 2) {
                        mimetypeMap.put(values[0], values[1]);
                    }
                }));
        if (logger.isDebugEnabled()) {
            logger.debug("loaded filetype: {}", mimetypeMap);
        }
    }

    public void add(final String mimetype, final String filetype) {
        mimetypeMap.put(mimetype, filetype);
    }

    public String get(final String mimetype) {
        final String filetype = mimetypeMap.get(mimetype);
        if (StringUtil.isBlank(filetype)) {
            return defaultValue;
        }
        return filetype;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String[] getTypes() {
        return mimetypeMap.values().stream().distinct().toArray(n -> new String[n]);
    }
}
