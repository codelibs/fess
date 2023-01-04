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

import org.lastaflute.web.util.LaRequestUtil;

public class UserAgentHelper {
    protected static final String USER_AGENT = "user-agent";

    protected static final String USER_AGENT_TYPE = "ViewHelper.UserAgent";

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

    public enum UserAgentType {
        IE, FIREFOX, CHROME, SAFARI, OPERA, OTHER;
    }

}
