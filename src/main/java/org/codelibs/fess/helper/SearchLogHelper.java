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

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Resource;

import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.es.exentity.ClickLog;
import org.codelibs.fess.es.exentity.SearchLog;
import org.codelibs.robot.util.LruHashMap;
import org.seasar.framework.container.annotation.tiger.InitMethod;

public abstract class SearchLogHelper {

    @Resource
    protected DynamicProperties crawlerProperties;

    public long userCheckInterval = 5 * 60 * 1000;// 5 min

    public int userInfoCacheSize = 1000;

    protected volatile Queue<SearchLog> searchLogQueue = new ConcurrentLinkedQueue<SearchLog>();

    protected volatile Queue<ClickLog> clickLogQueue = new ConcurrentLinkedQueue<ClickLog>();

    protected Map<String, Long> userInfoCache;

    @InitMethod
    public void init() {
        userInfoCache = new LruHashMap<String, Long>(userInfoCacheSize);
    }

    public abstract void updateUserInfo(final String userCode);

    public void addSearchLog(final SearchLog searchLog) {
        searchLogQueue.add(searchLog);
    }

    public void addClickLog(final ClickLog clickLog) {
        clickLogQueue.add(clickLog);
    }

    public void storeSearchLog() {
        if (!searchLogQueue.isEmpty()) {
            final Queue<SearchLog> queue = searchLogQueue;
            searchLogQueue = new ConcurrentLinkedQueue<SearchLog>();
            processSearchLogQueue(queue);
        }

        if (!clickLogQueue.isEmpty()) {
            final Queue<ClickLog> queue = clickLogQueue;
            clickLogQueue = new ConcurrentLinkedQueue<ClickLog>();
            processClickLogQueue(queue);
        }
    }

    public int getClickCount(final String url) {
        // TODO
        return 0;
    }

    public long getFavoriteCount(final String url) {
        // TODO
        return 0;
    }

    protected abstract void processSearchLogQueue(Queue<SearchLog> queue);

    protected abstract void processClickLogQueue(Queue<ClickLog> queue);

    public boolean addfavoriteLog(String userCode, String favoriteUrl) {
        // TODO Auto-generated method stub
        return false;
    }
}
