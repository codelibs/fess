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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.service.KeyMatchService;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.client.FessEsClient.SearchConditionBuilder;
import org.codelibs.fess.es.config.exentity.KeyMatch;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.core.SingletonLaContainer;

public class KeyMatchHelper {
    protected volatile Map<String, String[]> keyMatchQueryMap = Collections.emptyMap();

    protected ThreadLocal<List<String>> searchWordList = new ThreadLocal<>();

    protected long reloadInterval = 1000L;

    @PostConstruct
    public void init() {
        reload(0);
    }

    public void update() {
        new Thread(() -> reload(reloadInterval)).start();
    }

    protected void reload(final long interval) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final KeyMatchService keyMatchService = SingletonLaContainer.getComponent(KeyMatchService.class);
        final List<KeyMatch> list = keyMatchService.getAvailableKeyMatchList();
        final Map<String, String[]> keyMatchQueryMap = new HashMap<String, String[]>(list.size());
        for (final KeyMatch keyMatch : list) {
            final List<Map<String, Object>> documentList = getDocumentList(keyMatch);
            final List<String> docIdList = new ArrayList<String>();
            for (final Map<String, Object> map : documentList) {
                final String docId = (String) map.get(fessConfig.getIndexFieldDocId());
                if (StringUtil.isNotBlank(docId)) {
                    docIdList.add(fessConfig.getIndexFieldDocId() + ":" + docId + "^" + keyMatch.getBoost());
                }
            }
            if (!docIdList.isEmpty()) {
                keyMatchQueryMap.put(keyMatch.getTerm(), docIdList.toArray(new String[docIdList.size()]));
            }

            if (reloadInterval > 0) {
                try {
                    Thread.sleep(reloadInterval);
                } catch (final InterruptedException e) {
                    // ignore
                }
            }
        }
        this.keyMatchQueryMap = keyMatchQueryMap;
    }

    protected List<Map<String, Object>> getDocumentList(final KeyMatch keyMatch) {
        final FessEsClient fessEsClient = ComponentUtil.getElasticsearchClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final List<Map<String, Object>> documentList =
                fessEsClient.getDocumentList(fessConfig.getIndexDocumentIndex(), fessConfig.getIndexDocumentType(),
                        searchRequestBuilder -> {
                            return SearchConditionBuilder.builder(searchRequestBuilder).administrativeAccess().size(keyMatch.getMaxSize())
                                    .query(keyMatch.getQuery()).responseFields(new String[] { fessConfig.getIndexFieldDocId() }).build();
                        });
        return documentList;
    }

    public void clear() {
        searchWordList.remove();
    }

    public void addSearchWord(final String word) {
        final String[] values = keyMatchQueryMap.get(word);
        if (values != null) {
            List<String> list = searchWordList.get();
            if (list == null) {
                list = new ArrayList<>();
                searchWordList.set(list);
            }
            for (final String value : values) {
                list.add(value);
            }
        }
    }

    public List<String> getDocIdQueryList() {
        return searchWordList.get();
    }

    public long getReloadInterval() {
        return reloadInterval;
    }

    public void setReloadInterval(final long reloadInterval) {
        this.reloadInterval = reloadInterval;
    }
}
