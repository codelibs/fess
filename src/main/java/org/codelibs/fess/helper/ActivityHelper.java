/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.base.login.FessCredential;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.util.LaRequestUtil;

/**
 * @author shinsuke
 *
 */
public class ActivityHelper {
    protected Logger logger = null;

    protected String loggerName = "fess.log.audit";

    protected String permissionSeparator = "|";

    @PostConstruct
    public void init() {
        logger = LogManager.getLogger(loggerName);
    }

    public void login(final OptionalThing<FessUserBean> user) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("action:");
        buf.append(Action.LOGIN);
        buf.append('\t');
        buf.append("user:");
        buf.append(user.map(FessUserBean::getUserId).orElse("-"));
        buf.append('\t');
        buf.append("permissions:");
        buf.append(user.map(u -> stream(u.getPermissions()).get(stream -> stream.collect(Collectors.joining(permissionSeparator))))
                .filter(StringUtil::isNotBlank).orElse("-"));
        log(buf);
    }

    public void loginFailure(final OptionalThing<LoginCredential> credential) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("action:");
        buf.append(Action.LOGIN_FAILURE);
        credential.map(c -> {
            final StringBuilder buffer = new StringBuilder(100);
            buffer.append('\t');
            buffer.append("class:");
            buffer.append(c.getClass().getSimpleName());
            if (c instanceof FessCredential) {
                buffer.append('\t');
                buffer.append("user:");
                buffer.append(((FessCredential) c).getUserId());
            }
            return buffer.toString();
        }).ifPresent(buf::append);
        log(buf);
    }

    public void logout(final OptionalThing<FessUserBean> user) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("action:");
        buf.append(Action.LOGOUT);
        buf.append('\t');
        buf.append("user:");
        buf.append(user.map(FessUserBean::getUserId).orElse("-"));
        buf.append('\t');
        buf.append("permissions:");
        buf.append(user.map(u -> stream(u.getPermissions()).get(stream -> stream.collect(Collectors.joining(permissionSeparator))))
                .filter(StringUtil::isNotBlank).orElse("-"));
        log(buf);
    }

    public void access(final OptionalThing<FessUserBean> user, final String path, final String execute) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("action:");
        buf.append(Action.ACCESS);
        buf.append('\t');
        buf.append("user:");
        buf.append(user.map(FessUserBean::getUserId).orElse("-"));
        buf.append('\t');
        buf.append("path:");
        buf.append(path);
        buf.append('\t');
        buf.append("execute:");
        buf.append(execute);
        log(buf);
    }

    public void permissionChanged(final OptionalThing<FessUserBean> user) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("action:");
        buf.append(Action.UPDATE_PERMISSION);
        buf.append('\t');
        buf.append("user:");
        buf.append(user.map(FessUserBean::getUserId).orElse("-"));
        buf.append('\t');
        buf.append("permissions:");
        buf.append(user.map(u -> stream(u.getPermissions()).get(stream -> stream.collect(Collectors.joining(permissionSeparator))))
                .filter(StringUtil::isNotBlank).orElse("-"));
        log(buf);
    }

    public void print(final String action, final OptionalThing<FessUserBean> user, final Map<String, String> params) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("action:");
        buf.append(action.replace('\t', '_').toUpperCase(Locale.ENGLISH));
        buf.append('\t');
        buf.append("user:");
        buf.append(user.map(FessUserBean::getUserId).orElse("-"));
        params.entrySet().stream().map(e -> e.getKey() + ":" + e.getValue()).map(s -> s.replace('\t', '_')).sorted().forEach(s -> {
            buf.append('\t');
            buf.append(s);
        });
        log(buf);
    }

    protected void log(final StringBuilder buf) {
        buf.append('\t');
        buf.append("ip:");
        buf.append(getClientIp());
        buf.append('\t');
        buf.append("time:");
        buf.append(DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.now()));
        logger.info(buf.toString());
    }

    protected static String getClientIp() {
        return LaRequestUtil.getOptionalRequest().map(req -> ComponentUtil.getViewHelper().getClientIp(req)).orElse("-");
    }

    protected enum Action {
        LOGIN, LOGOUT, ACCESS, LOGIN_FAILURE, UPDATE_PERMISSION;
    }

    public void setLoggerName(final String loggerName) {
        this.loggerName = loggerName;
    }

    public void setPermissionSeparator(final String permissionSeparator) {
        this.permissionSeparator = permissionSeparator;
    }
}
