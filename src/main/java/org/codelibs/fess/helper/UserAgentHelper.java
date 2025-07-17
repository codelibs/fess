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

import org.lastaflute.web.util.LaRequestUtil;

/**
 * Helper class for detecting and categorizing user agent types from HTTP requests.
 * This class analyzes the User-Agent header to determine which browser type is being used
 * and caches the result in the request attribute for performance optimization.
 *
 * @author FessProject
 */
public class UserAgentHelper {

    /**
     * Default constructor for UserAgentHelper.
     */
    public UserAgentHelper() {
        // Default constructor
    }

    /** The HTTP header name for User-Agent */
    protected static final String USER_AGENT = "user-agent";

    /** The request attribute key for storing cached user agent type */
    protected static final String USER_AGENT_TYPE = "ViewHelper.UserAgent";

    /**
     * Determines the user agent type from the current HTTP request.
     * The method analyzes the User-Agent header to categorize the browser type
     * and caches the result in the request attribute for subsequent calls.
     *
     * @return the detected user agent type, or OTHER if no specific type is detected
     */
    public UserAgentType getUserAgentType() {
        return LaRequestUtil.getOptionalRequest().map(request -> {
            UserAgentType uaType = (UserAgentType) request.getAttribute(USER_AGENT_TYPE);
            if (uaType == null) {
                final String userAgent = request.getHeader(USER_AGENT);
                if (userAgent != null) {
                    if (userAgent.indexOf("MSIE") >= 0 || userAgent.indexOf("Trident") >= 0) {
                        uaType = UserAgentType.IE;
                    } else if (userAgent.indexOf("Firefox") >= 0) {
                        uaType = UserAgentType.FIREFOX;
                    } else if (userAgent.indexOf("Chrome") >= 0) {
                        uaType = UserAgentType.CHROME;
                    } else if (userAgent.indexOf("Safari") >= 0) {
                        uaType = UserAgentType.SAFARI;
                    } else if (userAgent.indexOf("Opera") >= 0) {
                        uaType = UserAgentType.OPERA;
                    }
                }
                if (uaType == null) {
                    uaType = UserAgentType.OTHER;
                }
                request.setAttribute(USER_AGENT_TYPE, uaType);
            }
            return uaType;
        }).orElse(UserAgentType.OTHER);
    }

    /**
     * Enumeration of supported browser types for user agent detection.
     * Each type represents a major browser family that can be detected
     * from the User-Agent header string.
     */
    public enum UserAgentType {
        /** Internet Explorer and Trident-based browsers */
        IE,
        /** Mozilla Firefox browser */
        FIREFOX,
        /** Google Chrome browser */
        CHROME,
        /** Apple Safari browser */
        SAFARI,
        /** Opera browser */
        OPERA,
        /** Other or unrecognized browser types */
        OTHER;
    }

}
