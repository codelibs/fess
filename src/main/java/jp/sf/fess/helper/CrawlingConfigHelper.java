/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jp.sf.fess.db.exentity.CrawlingConfig;

public class CrawlingConfigHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    protected final Map<String, CrawlingConfig> crawlingConfigMap = new ConcurrentHashMap<String, CrawlingConfig>();

    protected int count = 1;

    protected String configIdField = "cid_s_s";

    public synchronized String store(final String sessionId,
            final CrawlingConfig crawlingConfig) {
        final String sessionCountId = sessionId + "-" + count;
        crawlingConfigMap.put(sessionCountId, crawlingConfig);
        count++;
        return sessionCountId;
    }

    public void remove(final String sessionId) {
        crawlingConfigMap.remove(sessionId);
    }

    public CrawlingConfig get(final String sessionId) {
        return crawlingConfigMap.get(sessionId);
    }

    public String getConfigIdField() {
        return configIdField;
    }

    public void setConfigIdField(final String configIdField) {
        this.configIdField = configIdField;
    }

}
