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

package jp.sf.fess.helper;

import javax.servlet.http.HttpServletRequest;

import org.seasar.struts.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAgentHelper {
    private static final Logger logger = LoggerFactory
            .getLogger(UserAgentHelper.class);

    private static final String USER_AGENT = "user-agent";

    private static final String USER_AGENT_TYPE = "ViewHelper.UserAgent";

    public UserAgentType getUserAgentType() {
        final HttpServletRequest request = RequestUtil.getRequest();
        return getUserAgentType(request);
    }

    public UserAgentType getUserAgentType(final HttpServletRequest request) {
        UserAgentType uaType = (UserAgentType) request
                .getAttribute(USER_AGENT_TYPE);
        if (uaType == null) {
            final String userAgent = request.getHeader(USER_AGENT);
            if (userAgent != null) {
                if (userAgent.indexOf("MSIE") >= 0
                        || userAgent.indexOf("Trident") >= 0) {
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
    }

    public int getIEMajorVersion(final HttpServletRequest request) {
        UserAgentType userAgentType = getUserAgentType(request);
        if (userAgentType == UserAgentType.IE) {
            final String userAgent = request.getHeader(USER_AGENT);
            try {
                if (userAgent.contains("MSIE")) {
                    String substring = userAgent.substring(
                            userAgent.indexOf("MSIE")).split(";")[0];
                    return Integer.parseInt(substring.split("[ \\.]")[1]);
                } else if (userAgent.contains("rv")) {
                    String substring = userAgent.substring(
                            userAgent.indexOf("rv"), userAgent.indexOf(")"));
                    return Integer.parseInt(substring.split("[ :\\.]")[1]);
                }
            } catch (Exception e) {
                logger.debug("Invalid request header: " + userAgent, e);
            }
        }
        return 0;
    }

    public enum UserAgentType {
        IE, FIREFOX, CHROME, SAFARI, OPERA, OTHER;
    }

}
