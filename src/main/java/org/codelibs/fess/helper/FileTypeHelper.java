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
package org.codelibs.fess.helper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * Helper class for managing file type mappings based on MIME types.
 * This class provides functionality to map MIME types to file types and
 * retrieve appropriate file type classifications for documents during indexing.
 *
 * The mappings are loaded from configuration and can be dynamically modified
 * at runtime. When a MIME type is not found in the mapping, a default value
 * is returned.
 */
public class FileTypeHelper {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(FileTypeHelper.class);

    /** Default file type value returned when MIME type is not found in mappings */
    protected String defaultValue = "others";

    /** Map storing MIME type to file type mappings */
    protected Map<String, String> mimetypeMap = new LinkedHashMap<>();

    /**
     * Initializes the file type mappings by loading configuration from Fess settings.
     * This method is called automatically after dependency injection is complete.
     * The mappings are loaded from the index filetype configuration property,
     * where each line contains a MIME type to file type mapping in the format "mimetype=filetype".
     */
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

    /**
     * Adds or updates a MIME type to file type mapping.
     *
     * @param mimetype the MIME type to map (e.g., "application/pdf")
     * @param filetype the file type classification (e.g., "pdf")
     */
    public void add(final String mimetype, final String filetype) {
        mimetypeMap.put(mimetype, filetype);
    }

    /**
     * Retrieves the file type for a given MIME type.
     *
     * @param mimetype the MIME type to look up
     * @return the corresponding file type, or the default value if not found
     */
    public String get(final String mimetype) {
        final String filetype = mimetypeMap.get(mimetype);
        if (StringUtil.isBlank(filetype)) {
            return defaultValue;
        }
        return filetype;
    }

    /**
     * Gets the default file type value used when MIME type is not found.
     *
     * @return the default file type value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default file type value to use when MIME type is not found.
     *
     * @param defaultValue the new default file type value
     */
    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Gets all distinct file types currently configured in the mappings.
     *
     * @return an array of all unique file type values
     */
    public String[] getTypes() {
        return mimetypeMap.values().stream().distinct().toArray(n -> new String[n]);
    }
}
