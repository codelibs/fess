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
package org.codelibs.fess.es.config.exentity;

import java.util.Map;
import java.util.function.Supplier;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.client.ftp.FtpClient;
import org.codelibs.fess.crawler.client.http.HcHttpClient;
import org.codelibs.fess.crawler.client.smb.SmbClient;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

public interface CrawlingConfig {

    String getId();

    String getName();

    String[] getPermissions();

    String[] getVirtualHosts();

    String getDocumentBoost();

    String getIndexingTarget(String input);

    String getConfigId();

    Integer getTimeToLive();

    CrawlerClientFactory initializeClientFactory(Supplier<CrawlerClientFactory> creator);

    Map<String, String> getConfigParameterMap(ConfigName name);

    default void initializeDefaultHttpProxy(final Map<String, Object> paramMap) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String proxyHost = fessConfig.getHttpProxyHost();
        final String proxyPort = fessConfig.getHttpProxyPort();
        if (StringUtil.isNotBlank(proxyHost) && StringUtil.isNotBlank(proxyPort)) {
            paramMap.put(Param.Client.PROXY_HOST, proxyHost);
            paramMap.put(Param.Client.PROXY_PORT, proxyPort);
            final String proxyUsername = fessConfig.getHttpProxyUsername();
            final String proxyPassword = fessConfig.getHttpProxyPassword();
            if (proxyUsername != null && proxyPassword != null) {
                paramMap.put(HcHttpClient.PROXY_CREDENTIALS_PROPERTY, new UsernamePasswordCredentials(proxyUsername, proxyPassword));
            }

        }
    }

    default String getScriptType() {
        final String scriptType = getConfigParameterMap(ConfigName.CONFIG).get(Param.Config.SCRIPT_TYPE);
        if (StringUtil.isNotBlank(scriptType)) {
            return scriptType;
        }
        return Constants.DEFAULT_SCRIPT;
    }

    public enum ConfigType {
        WEB("W"), FILE("F"), DATA("D");

        private final String typePrefix;

        ConfigType(final String typePrefix) {
            this.typePrefix = typePrefix;
        }

        public String getTypePrefix() {
            return typePrefix;
        }

        String getConfigId(final String id) {
            if (id == null) {
                return null;
            }
            return typePrefix + id.toString();
        }
    }

    public enum ConfigName {
        CLIENT, XPATH, META, VALUE, SCRIPT, FIELD, CONFIG;
    }

    public static class Param {
        // client.*
        public static class Client {
            public static final String SMB_AUTHENTICATIONS = SmbClient.SMB_AUTHENTICATIONS_PROPERTY;
            public static final String SMB1_AUTHENTICATIONS = org.codelibs.fess.crawler.client.smb1.SmbClient.SMB_AUTHENTICATIONS_PROPERTY;
            public static final String FTP_AUTHENTICATIONS = FtpClient.FTP_AUTHENTICATIONS_PROPERTY;
            public static final String ROBOTS_TXT_ENABLED = HcHttpClient.ROBOTS_TXT_ENABLED_PROPERTY;
            public static final String PROXY_PASSWORD = "proxyPassword";
            public static final String PROXY_USERNAME = "proxyUsername";
            public static final String PROXY_PORT = HcHttpClient.PROXY_PORT_PROPERTY;
            public static final String PROXY_HOST = HcHttpClient.PROXY_HOST_PROPERTY;
            public static final String USER_AGENT = HcHttpClient.USER_AGENT_PROPERTY;
        }

        // xpath.*
        public static class XPath {
            public static final String DEFAULT_LANG = "default.lang";
            public static final String DEFAULT_CONTENT = "default.content";
            public static final String DEFAULT_DIGEST = "default.digest";
            // xapth.<field>=<value>
        }

        // config.*
        public static class Config {
            public static final String KEEP_ORIGINAL_BODY = "keep.original.body";
            public static final String CLEANUP_ALL = "cleanup.all";
            public static final String CLEANUP_URL_FILTERS = "cleanup.urlFilters";
            public static final String JCIFS_PREFIX = "jcifs.";
            public static final String HTML_CANONICAL_XPATH = "html.canonical.xpath";
            public static final String HTML_PRUNED_TAGS = "html.pruned.tags";
            public static final String PIPELINE = "pipeline";
            public static final String IGNORE_ROBOTS_TAGS = "ignore.robots.tags";
            public static final String SCRIPT_TYPE = "script.type";
        }

        // meta.*
        // meta.<field>=<value>

        // value.*
        // value.<field>=<value>

        // script.*
        // script.<field>=<value>

        // field.*
        // field.<field>=<value>
    }
}