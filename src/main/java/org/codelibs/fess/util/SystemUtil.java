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
package org.codelibs.fess.util;

import org.codelibs.fess.Constants;

/**
 * This class provides system-related utility methods.
 * It extends {@link org.codelibs.core.lang.SystemUtil} and adds
 * methods specific to the Fess application.
 */
public class SystemUtil extends org.codelibs.core.lang.SystemUtil {
    /**
     * Private constructor to prevent instantiation.
     */
    private SystemUtil() {
    }

    /**
     * Gets the HTTP address of the search engine.
     *
     * @return The search engine HTTP address.
     */
    public static String getSearchEngineHttpAddress() {
        return System.getProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
    }
}
