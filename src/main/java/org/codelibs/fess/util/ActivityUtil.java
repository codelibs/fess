/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

import org.codelibs.core.lang.StringUtil;
import org.lastaflute.web.util.LaRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class ActivityUtil {
    private static Logger logger = LoggerFactory.getLogger("fess.log.audit");

    public static void login(final String username) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("action:");
        buf.append(Action.LOGIN);
        buf.append('\t');
        buf.append("user:");
        buf.append(username);
        log(buf);
    }

    public static void logout(final String username) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("action:");
        buf.append(Action.LOGOUT);
        buf.append('\t');
        buf.append("user:");
        buf.append(username);
        log(buf);
    }

    public static void access(final String username, final String path, final String execute) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("action:");
        buf.append(Action.ACCESS);
        buf.append('\t');
        buf.append("user:");
        buf.append(username);
        buf.append('\t');
        buf.append("path:");
        buf.append(path);
        buf.append('\t');
        buf.append("execute:");
        buf.append(execute);
        log(buf);
    }

    private static void log(final StringBuilder buf) {
        buf.append('\t');
        buf.append("ip:");
        buf.append(getClientIp());
        buf.append('\t');
        buf.append("time:");
        buf.append(DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.now()));
        logger.info(buf.toString());
    }

    protected static String getClientIp() {
        return LaRequestUtil.getOptionalRequest().map(req -> {
            final String value = req.getHeader("x-forwarded-for");
            if (StringUtil.isNotBlank(value)) {
                return value;
            } else {
                return req.getRemoteAddr();
            }
        }).orElse("-");
    }

    protected enum Action {
        LOGIN, LOGOUT, ACCESS;
    }
}
