/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.taglib.FessFunctions;
import org.dbflute.optional.OptionalThing;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author shinsuke
 *
 */
public class CrawlerStatsHelper {
    private static final String BEGIN_KEY = "begin";

    protected Logger logger = null;

    protected String loggerName = "fess.log.crawler.stats";

    protected long maxCacheSize = 1000;

    protected long cacheExpireAfterWrite = 10 * 60 * 1000L;

    protected LoadingCache<String, Map<String, Long>> statsCache;

    @PostConstruct
    public void init() {
        logger = LogManager.getLogger(loggerName);
        statsCache = CacheBuilder.newBuilder().maximumSize(maxCacheSize).expireAfterWrite(cacheExpireAfterWrite, TimeUnit.MILLISECONDS)
                .build(new CacheLoader<String, Map<String, Long>>() {
                    @Override
                    public Map<String, Long> load(String key) {
                        Map<String, Long> map = new LinkedHashMap<>();
                        map.put(BEGIN_KEY, System.currentTimeMillis());
                        return map;
                    }
                });
    }

    public void begin(final Object keyObj) {
        getCacheKey(keyObj).ifPresent(key -> {
            try {
                statsCache.get(key);
            } catch (Exception e) {
                final StringBuilder buf = createStringBuffer(keyObj, System.currentTimeMillis());
                buf.append('\t').append("action:begin");
                buf.append('\t').append("error:").append(escapeValue(e.getLocalizedMessage()).replaceAll("\\s", " "));
                log(buf);
            }
        });
    }

    public void record(final Object keyObj, final String action) {
        getCacheKey(keyObj).ifPresent(key -> {
            try {
                Map<String, Long> data = statsCache.getIfPresent(key);
                if (data != null) {
                    data.put(escapeValue(action), System.currentTimeMillis());
                }
            } catch (Exception e) {
                final StringBuilder buf = createStringBuffer(keyObj, System.currentTimeMillis());
                buf.append('\t').append("action:record");
                buf.append('\t').append("error:").append(escapeValue(e.getLocalizedMessage()).replaceAll("\\s", " "));
                log(buf);
            }
        });
    }

    public void done(final Object keyObj) {
        getCacheKey(keyObj).ifPresent(key -> {
            try {
                final Map<String, Long> data = statsCache.getIfPresent(key);
                if (data != null) {
                    statsCache.invalidate(key);
                    final Long begin = data.remove(BEGIN_KEY);
                    if (begin != null) {
                        final StringBuilder buf = createStringBuffer(keyObj, begin.longValue());
                        buf.append('\t').append("done:").append(System.currentTimeMillis() - begin.longValue());
                        data.entrySet().stream().map(e -> escapeValue(e.getKey()) + ":" + (e.getValue().longValue() - begin.longValue()))
                                .map(s -> "\t" + s).forEach(s -> buf.append(s));
                        log(buf);
                    }
                }
            } catch (Exception e) {
                final StringBuilder buf = createStringBuffer(keyObj, System.currentTimeMillis());
                buf.append('\t').append("action:done");
                buf.append('\t').append("error:").append(escapeValue(e.getLocalizedMessage()).replaceAll("\\s", " "));
                log(buf);
            }
        });
    }

    private StringBuilder createStringBuffer(final Object keyObj, final long time) {
        final StringBuilder buf = new StringBuilder(1000);
        buf.append("url:").append(getUrl(keyObj));
        buf.append('\t');
        buf.append("time:").append(FessFunctions.formatDate(new Date(time)));
        return buf;
    }

    protected String getUrl(final Object keyObj) {
        if (keyObj instanceof UrlQueue<?> urlQueue) {
            return escapeValue(urlQueue.getUrl());
        } else if (keyObj instanceof String key) {
            return escapeValue(key);
        } else if (keyObj instanceof Number key) {
            return key.toString();
        }
        return "-";
    }

    protected OptionalThing<String> getCacheKey(final Object keyObj) {
        if (keyObj instanceof UrlQueue<?> urlQueue) {
            return OptionalThing.of(urlQueue.getId().toString());
        } else if (keyObj instanceof String key) {
            return OptionalThing.of(key);
        } else if (keyObj instanceof Number key) {
            return OptionalThing.of(key.toString());
        }
        return OptionalThing.empty();
    }

    protected String escapeValue(String action) {
        return action.replace('\t', ' ');
    }

    protected void log(final StringBuilder buf) {
        logger.info(buf.toString());
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public void setMaxCacheSize(long maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    public void setCacheExpireAfterWrite(long cacheExpireAfterWrite) {
        this.cacheExpireAfterWrite = cacheExpireAfterWrite;
    }

}
