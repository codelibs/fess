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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.base.login.FessCredential;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.util.LaRequestUtil;

import jakarta.annotation.PostConstruct;

/**
 * The helper for user activities.
 * This class provides methods to log user actions such as login, logout, and access.
 * It supports both LTSV and ECS log formats.
 *
 * @author shinsuke
 */
public class ActivityHelper {

    /**
     * The logger.
     */
    protected Logger logger = null;

    /**
     * The logger name.
     */
    protected String loggerName = "fess.log.audit";

    /**
     * The permission separator.
     */
    protected String permissionSeparator = "|";

    /**
     * The flag to use ECS format.
     */
    protected boolean useEcsFormat = false;

    /**
     * The ECS version.
     */
    protected String ecsVersion = "1.2.0";

    /**
     * The ECS service name.
     */
    protected String ecsServiceName = "fess";

    /**
     * The ECS event dataset.
     */
    protected String ecsEventDataset = "app";

    /**
     * The environment map.
     */
    protected Map<String, String> envMap;

    /**
     * Initialize the helper.
     */
    @PostConstruct
    public void init() {
        logger = LogManager.getLogger(loggerName);
        final String logFormat = ComponentUtil.getFessConfig().getAppAuditLogFormat();
        if (StringUtil.isBlank(logFormat)) {
            useEcsFormat = "docker".equals(getEnvMap().get("FESS_APP_TYPE"));
        } else if ("ecs".equals(logFormat)) {
            useEcsFormat = true;
        }
    }

    /**
     * Get the environment map.
     * @return The environment map.
     */
    protected Map<String, String> getEnvMap() {
        if (envMap != null) {
            return envMap;
        }
        return System.getenv();
    }

    /**
     * Set the environment map.
     * @param envMap The environment map.
     */
    public void setEnvMap(final Map<String, String> envMap) {
        this.envMap = envMap;
    }

    /**
     * Log the login activity.
     * @param user The user.
     */
    public void login(final OptionalThing<FessUserBean> user) {
        final Map<String, String> valueMap = new LinkedHashMap<>();
        valueMap.put("action", Action.LOGIN.name());
        valueMap.put("user", user.map(FessUserBean::getUserId).orElse("-"));
        valueMap.put("permissions",
                user.map(u -> stream(u.getPermissions()).get(stream -> stream.collect(Collectors.joining(permissionSeparator))))
                        .filter(StringUtil::isNotBlank).orElse("-"));
        log(valueMap);
    }

    /**
     * Log the login failure activity.
     * @param credential The credential.
     */
    public void loginFailure(final OptionalThing<LoginCredential> credential) {
        final Map<String, String> valueMap = new LinkedHashMap<>();
        valueMap.put("action", Action.LOGIN_FAILURE.name());
        credential.ifPresent(c -> {
            valueMap.put("class", c.getClass().getSimpleName());
            if (c instanceof final FessCredential fessCredential) {
                valueMap.put("user", fessCredential.getUserId());
            }
        });
        log(valueMap);
    }

    /**
     * Log the logout activity.
     * @param user The user.
     */
    public void logout(final OptionalThing<FessUserBean> user) {
        final Map<String, String> valueMap = new LinkedHashMap<>();
        valueMap.put("action", Action.LOGOUT.name());
        valueMap.put("user", user.map(FessUserBean::getUserId).orElse("-"));
        valueMap.put("permissions",
                user.map(u -> stream(u.getPermissions()).get(stream -> stream.collect(Collectors.joining(permissionSeparator))))
                        .filter(StringUtil::isNotBlank).orElse("-"));
        log(valueMap);
    }

    /**
     * Log the access activity.
     * @param user The user.
     * @param path The path.
     * @param execute The execute.
     */
    public void access(final OptionalThing<FessUserBean> user, final String path, final String execute) {
        final Map<String, String> valueMap = new LinkedHashMap<>();
        valueMap.put("action", Action.ACCESS.name());
        valueMap.put("user", user.map(FessUserBean::getUserId).orElse("-"));
        valueMap.put("path", path);
        valueMap.put("execute", execute);
        log(valueMap);
    }

    /**
     * Log the permission changed activity.
     * @param user The user.
     */
    public void permissionChanged(final OptionalThing<FessUserBean> user) {
        final Map<String, String> valueMap = new LinkedHashMap<>();
        valueMap.put("action", Action.UPDATE_PERMISSION.name());
        valueMap.put("user", user.map(FessUserBean::getUserId).orElse("-"));
        valueMap.put("permissions",
                user.map(u -> stream(u.getPermissions()).get(stream -> stream.collect(Collectors.joining(permissionSeparator))))
                        .filter(StringUtil::isNotBlank).orElse("-"));
        log(valueMap);
    }

    /**
     * Print the log.
     * @param action The action.
     * @param user The user.
     * @param params The parameters.
     */
    public void print(final String action, final OptionalThing<FessUserBean> user, final Map<String, String> params) {
        final Map<String, String> valueMap = new LinkedHashMap<>();
        valueMap.put("action", action.replace('\t', '_').toUpperCase(Locale.ENGLISH));
        valueMap.put("user", user.map(FessUserBean::getUserId).orElse("-"));
        final Comparator<Map.Entry<String, String>> c = Comparator.comparing(Map.Entry::getKey);
        params.entrySet().stream().sorted(c).forEach(e -> {
            valueMap.put(e.getKey(), e.getValue().replace('\t', '_'));
        });
        log(valueMap);
    }

    /**
     * Log the value map.
     * @param valueMap The value map.
     */
    protected void log(final Map<String, String> valueMap) {
        valueMap.put("ip", getClientIp());
        valueMap.put("time", DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.now()));
        if (useEcsFormat) {
            printByEcs(valueMap);
        } else {
            printByLtsv(valueMap);
        }
    }

    /**
     * Print the log by LTSV.
     * @param valueMap The value map.
     */
    protected void printByLtsv(final Map<String, String> valueMap) {
        printLog(valueMap.entrySet().stream().map(e -> e.getKey() + ":" + e.getValue()).collect(Collectors.joining("\t")));
    }

    /**
     * Print the log by ECS.
     * @param valueMap The value map.
     */
    protected void printByEcs(final Map<String, String> valueMap) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("{\"@timestamp\":\"").append(valueMap.remove("time")).append('"');
        buf.append(",\"log.level\":\"INFO\"");
        buf.append(",\"ecs.version\":\"").append(ecsVersion).append('"');
        buf.append(",\"service.name\":\"").append(ecsServiceName).append('"');
        buf.append(",\"event.dataset\":\"").append(ecsEventDataset).append('"');
        buf.append(",\"process.thread.name\":\"").append(StringEscapeUtils.escapeJson(Thread.currentThread().getName())).append('"');
        buf.append(",\"log.logger\":\"").append(StringEscapeUtils.escapeJson(this.getClass().getName())).append('"');
        valueMap.entrySet().stream().forEach(e -> buf.append(",\"labels.").append(e.getKey()).append("\":\"")
                .append(StringEscapeUtils.escapeJson(e.getValue())).append('"'));
        buf.append('}');
        printLog(buf.toString());
    }

    /**
     * Print the log.
     * @param message The message.
     */
    protected void printLog(final String message) {
        logger.info(message);
    }

    /**
     * Get the client IP.
     * @return The client IP.
     */
    protected String getClientIp() {
        return LaRequestUtil.getOptionalRequest().map(req -> ComponentUtil.getViewHelper().getClientIp(req)).orElse("-");
    }

    /**
     * The action.
     */
    protected enum Action {
        /**
         * The login action.
         */
        LOGIN,
        /**
         * The logout action.
         */
        LOGOUT,
        /**
         * The access action.
         */
        ACCESS,
        /**
         * The login failure action.
         */
        LOGIN_FAILURE,
        /**
         * The update permission action.
         */
        UPDATE_PERMISSION;
    }

    /**
     * Set the logger name.
     * @param loggerName The logger name.
     */
    public void setLoggerName(final String loggerName) {
        this.loggerName = loggerName;
    }

    /**
     * Set the permission separator.
     * @param permissionSeparator The permission separator.
     */
    public void setPermissionSeparator(final String permissionSeparator) {
        this.permissionSeparator = permissionSeparator;
    }

    /**
     * Set the ECS version.
     * @param ecsVersion The ECS version.
     */
    public void setEcsVersion(final String ecsVersion) {
        this.ecsVersion = ecsVersion;
    }

    /**
     * Set the ECS service name.
     * @param ecsServiceName The ECS service name.
     */
    public void setEcsServiceName(final String ecsServiceName) {
        this.ecsServiceName = ecsServiceName;
    }

    /**
     * Set the ECS event dataset.
     * @param ecsEventDataset The ECS event dataset.
     */
    public void setEcsEventDataset(final String ecsEventDataset) {
        this.ecsEventDataset = ecsEventDataset;
    }
}
