/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import org.apache.http.auth.UsernamePasswordCredentials;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.client.http.HcHttpClient;
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

    Map<String, Object> initializeClientFactory(CrawlerClientFactory crawlerClientFactory);

    Map<String, String> getConfigParameterMap(ConfigName name);

    default void initializeDefaultHttpProxy(final Map<String, Object> paramMap) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String proxyHost = fessConfig.getHttpProxyHost();
        final String proxyPort = fessConfig.getHttpProxyPort();
        if (StringUtil.isNotBlank(proxyHost) && StringUtil.isNotBlank(proxyPort)) {
            paramMap.put(HcHttpClient.PROXY_HOST_PROPERTY, proxyHost);
            paramMap.put(HcHttpClient.PROXY_PORT_PROPERTY, proxyPort);
            final String proxyUsername = fessConfig.getHttpProxyUsername();
            final String proxyPassword = fessConfig.getHttpProxyPassword();
            if (proxyUsername != null && proxyPassword != null) {
                paramMap.put(HcHttpClient.PROXY_CREDENTIALS_PROPERTY, new UsernamePasswordCredentials(proxyUsername, proxyPassword));
            }

        }
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
}