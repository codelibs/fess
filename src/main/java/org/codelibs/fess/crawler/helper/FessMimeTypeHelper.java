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
package org.codelibs.fess.crawler.helper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * FessMimeTypeHelper extends MimeTypeHelperImpl to provide Fess-specific
 * MIME type detection configuration. It reads extension-to-MIME-type override
 * mappings from FessConfig to handle cases where content-based detection
 * produces incorrect results (e.g., SQL files starting with REM comments
 * being misdetected as batch files).
 */
public class FessMimeTypeHelper extends MimeTypeHelperImpl {

    private static final Logger logger = LogManager.getLogger(FessMimeTypeHelper.class);

    /**
     * Default constructor for FessMimeTypeHelper.
     */
    public FessMimeTypeHelper() {
        // Default constructor
    }

    /**
     * Initializes the extension override map by loading configuration from FessConfig.
     * The mappings are loaded from the crawler.document.mimetype.extension.overrides property,
     * where each line contains a mapping in the format ".ext=mime/type".
     */
    @PostConstruct
    public void init() {
        final String overrides = ComponentUtil.getFessConfig().getCrawlerDocumentMimetypeExtensionOverrides();
        if (StringUtil.isNotBlank(overrides)) {
            final Map<String, String> map = new LinkedHashMap<>();
            StreamUtil.split(overrides, "\n").of(stream -> stream.filter(StringUtil::isNotBlank).forEach(s -> {
                final String[] values = StringUtils.split(s, "=", 2);
                if (values.length == 2) {
                    map.put(values[0].trim(), values[1].trim());
                }
            }));
            setExtensionMimeTypeMap(map);
            if (logger.isDebugEnabled()) {
                logger.debug("loaded mimetype extension overrides: {}", map);
            }
        }
    }
}
