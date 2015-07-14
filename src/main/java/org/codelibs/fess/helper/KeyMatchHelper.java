/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.client.FessEsClient;
import org.codelibs.fess.client.FessEsClient.SearchConditionBuilder;
import org.codelibs.fess.es.exentity.KeyMatch;
import org.codelibs.fess.service.KeyMatchService;
import org.codelibs.fess.util.ComponentUtil;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.annotation.tiger.InitMethod;

public class KeyMatchHelper {
    protected volatile Map<String, String[]> keyMatchQueryMap = Collections.emptyMap();

    protected ThreadLocal<List<String>> searchWordList = new ThreadLocal<>();

    protected long reloadInterval = 1000L;

    @InitMethod
    public void init() {
        reload(0);
    }

    public void update() {
        new Thread(() -> reload(reloadInterval)).start();
    }

    protected void reload(final long interval) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        final KeyMatchService keyMatchService = SingletonS2Container.getComponent(KeyMatchService.class);
        final List<KeyMatch> list = keyMatchService.getAvailableKeyMatchList();
        final Map<String, String[]> keyMatchQueryMap = new HashMap<String, String[]>(list.size());
        for (final KeyMatch keyMatch : list) {
            final List<Map<String, Object>> documentList = getDocumentList(keyMatch);
            final List<String> docIdList = new ArrayList<String>();
            for (final Map<String, Object> map : documentList) {
                final String docId = (String) map.get(fieldHelper.docIdField);
                if (StringUtil.isNotBlank(docId)) {
                    docIdList.add(fieldHelper.docIdField + ":" + docId + "^" + keyMatch.getBoost());
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
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        final List<Map<String, Object>> documentList =
                fessEsClient.getDocumentList(fieldHelper.docIndex, fieldHelper.docType,
                        searchRequestBuilder -> {
                            return SearchConditionBuilder.builder(searchRequestBuilder).administrativeAccess().size(keyMatch.getMaxSize())
                                    .query(keyMatch.getQuery()).responseFields(new String[] { fieldHelper.docIdField }).build();
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
