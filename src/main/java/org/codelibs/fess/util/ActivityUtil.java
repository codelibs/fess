/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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

import javax.servlet.http.HttpServletRequest;

import org.codelibs.core.lang.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class ActivityUtil {
    private static Logger logger = LoggerFactory.getLogger("activity");

    public static void login(final String username, final HttpServletRequest request) {
        log(Action.LOGIN, username + ' ' + getRemoteAddr(request));
    }

    public static void logout(final String username, final HttpServletRequest request) {
        log(Action.LOGOUT, username + ' ' + getRemoteAddr(request));
    }

    public static void access(final String username, final HttpServletRequest request) {
        log(Action.ACCESS, username + ' ' + getRemoteAddr(request) + ' ' + request.getRequestURL());
    }

    protected static void log(final Action action, final String msg) {
        logger.info("[" + action + "] " + msg);
    }

    protected static String getRemoteAddr(final HttpServletRequest request) {
        final String clientIp = request.getHeader("x-forwarded-for");
        if (StringUtil.isNotBlank(clientIp)) {
            return clientIp;
        }
        return request.getRemoteAddr();
    }

    protected enum Action {
        LOGIN, LOGOUT, ACCESS;
    }
}
