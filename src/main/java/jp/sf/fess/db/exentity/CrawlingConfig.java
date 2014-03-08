/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.db.exentity;

import java.util.Map;

import org.seasar.robot.client.S2RobotClientFactory;

public interface CrawlingConfig {

    Long getId();

    String getName();

    String[] getRoleTypeValues();

    String[] getLabelTypeValues();

    String getDocumentBoost();

    String getIndexingTarget(String input);

    String getConfigId();

    void initializeClientFactory(S2RobotClientFactory s2RobotClientFactory);

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

        public String getConfigId(final Long id) {
            if (id == null) {
                return null;
            }
            return typePrefix + id.toString();
        }
    }

    public enum ConfigName {
        CLIENT, XPATH, META, VALUE, SCRIPT, FIELD;
    }
}