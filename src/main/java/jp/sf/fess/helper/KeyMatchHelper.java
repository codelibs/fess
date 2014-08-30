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

package jp.sf.fess.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.sf.fess.Constants;
import jp.sf.fess.db.exentity.KeyMatch;
import jp.sf.fess.service.KeyMatchService;
import jp.sf.fess.service.SearchService;
import jp.sf.fess.util.ComponentUtil;

import org.codelibs.core.util.StringUtil;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.annotation.tiger.InitMethod;

public class KeyMatchHelper {
    protected volatile Map<String, String[]> keyMatchQueryMap = Collections
            .emptyMap();

    protected ThreadLocal<List<String>> searchWordList = new ThreadLocal<>();

    protected long reloadInterval = 1000L;

    @InitMethod
    public void init() {
        reload(0);
    }

    public void update() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                reload(reloadInterval);
            }
        }).start();
    }

    protected void reload(final long interval) {
        final KeyMatchService keyMatchService = SingletonS2Container
                .getComponent(KeyMatchService.class);
        final List<KeyMatch> list = keyMatchService.getAvailableKeyMatchList();
        final Map<String, String[]> keyMatchQueryMap = new HashMap<String, String[]>(
                list.size());
        for (final KeyMatch keyMatch : list) {
            final List<Map<String, Object>> documentList = getDocumentList(keyMatch);
            final List<String> docIdList = new ArrayList<String>();
            for (final Map<String, Object> map : documentList) {
                final String docId = (String) map.get(Constants.DOC_ID);
                if (StringUtil.isNotBlank(docId)) {
                    docIdList.add(Constants.DOC_ID + ":" + docId + "^"
                            + keyMatch.getBoost());
                }
            }
            if (!docIdList.isEmpty()) {
                keyMatchQueryMap.put(keyMatch.getTerm(),
                        docIdList.toArray(new String[docIdList.size()]));
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
        final SearchService searchService = ComponentUtil.getSearchService();
        final List<Map<String, Object>> documentList = searchService
                .getDocumentList(keyMatch.getQuery(), 0, keyMatch.getMaxSize(),
                        null, null, null, new String[] { Constants.DOC_ID },
                        null, false);
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
