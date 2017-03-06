/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import org.codelibs.fess.crawler.client.CrawlerClientFactory;

public interface CrawlingConfig {

    String getId();

    String getName();

    String[] getPermissions();

    String[] getLabelTypeValues();

    String getDocumentBoost();

    String getIndexingTarget(String input);

    String getConfigId();

    Integer getTimeToLive();

    Map<String, Object> initializeClientFactory(CrawlerClientFactory crawlerClientFactory);

    Map<String, String> getConfigParameterMap(ConfigName name);

    public enum ConfigType {
        WEB("W"), FILE("F"), DATA("D");

        private final String typePrefix;

        ConfigType(final String typePrefix) {
            this.typePrefix = typePrefix;
        }

        public String getTypePrefix() {
            return typePrefix;
        }

        public String getConfigId(final String id) {
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