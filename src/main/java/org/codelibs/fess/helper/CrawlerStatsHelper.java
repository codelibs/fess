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
package org.codelibs.fess.helper;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
    private static final Logger logger = LogManager.getLogger(CrawlerStatsHelper.class);

    private static final String BEGIN_KEY = "begin";

    protected Logger statsLogger = null;

    protected String loggerName = "fess.log.crawler.stats";

    protected long maxCacheSize = 1000;

    protected long cacheExpireAfterWrite = 10 * 60 * 1000L;

    protected LoadingCache<String, StatsObject> statsCache;

    @PostConstruct
    public void init() {
        statsLogger = LogManager.getLogger(loggerName);
        statsCache = CacheBuilder.newBuilder().maximumSize(maxCacheSize).expireAfterWrite(cacheExpireAfterWrite, TimeUnit.MILLISECONDS)
                .build(new CacheLoader<String, StatsObject>() {
                    @Override
                    public StatsObject load(final String key) {
                        return new StatsObject();
                    }
                });
    }

    @PreDestroy
    public void destroy() {
        if (logger.isDebugEnabled()) {
            logger.debug("cache stats: {}", statsCache.stats());
        }
        statsCache.asMap().entrySet().stream().forEach(e -> {
            final StatsObject data = e.getValue();
            final Long begin = data.remove(BEGIN_KEY);
            if (begin != null) {
                printStats(e.getKey(), data, begin, false);
            }
        });

    }

    public void begin(final Object keyObj) {
        getCacheKey(keyObj).ifPresent(key -> {
            try {
                statsCache.get(key);
            } catch (final Exception e) {
                final StringBuilder buf = createStringBuffer(keyObj, System.currentTimeMillis());
                buf.append('\t').append("action:begin");
                buf.append('\t').append("error:").append(escapeValue(e.getLocalizedMessage()).replaceAll("\\s", " "));
                log(buf);
            }
        });
    }

    public void record(final Object keyObj, final StatsAction action) {
        record(keyObj, action.name().toLowerCase(Locale.ENGLISH));
    }

    public void record(final Object keyObj, final String action) {
        getCacheKey(keyObj).ifPresent(key -> {
            try {
                final StatsObject data = statsCache.getIfPresent(key);
                if (data != null) {
                    data.put(escapeValue(action), System.currentTimeMillis());
                }
            } catch (final Exception e) {
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
                final StatsObject data = statsCache.getIfPresent(key);
                if (data != null && data.decrement() <= 0) {
                    statsCache.invalidate(key);
                    final Long begin = data.remove(BEGIN_KEY);
                    if (begin != null) {
                        printStats(keyObj, data, begin, true);
                    }
                }
            } catch (final Exception e) {
                final StringBuilder buf = createStringBuffer(keyObj, System.currentTimeMillis());
                buf.append('\t').append("action:done");
                buf.append('\t').append("error:").append(escapeValue(e.getLocalizedMessage()).replaceAll("\\s", " "));
                log(buf);
            }
        });
    }

    public void discard(final Object keyObj) {
        getCacheKey(keyObj).ifPresent(key -> {
            try {
                final StatsObject data = statsCache.getIfPresent(key);
                if (data != null) {
                    statsCache.invalidate(key);
                }
            } catch (final Exception e) {
                final StringBuilder buf = createStringBuffer(keyObj, System.currentTimeMillis());
                buf.append('\t').append("action:done");
                buf.append('\t').append("error:").append(escapeValue(e.getLocalizedMessage()).replaceAll("\\s", " "));
                log(buf);
            }
        });
    }

    protected void printStats(final Object keyObj, final StatsObject data, final long begin, final boolean done) {
        final StringBuilder buf = createStringBuffer(keyObj, begin);
        if (done) {
            buf.append('\t').append("done:").append(System.currentTimeMillis() - begin);
        }
        data.entrySet().stream().map(e -> escapeValue(e.getKey()) + ":" + (e.getValue().longValue() - begin)).map(s -> "\t" + s)
                .forEach(s -> buf.append(s));
        log(buf);
    }

    public void runOnThread(final Object keyObj) {
        getCacheKey(keyObj).ifPresent(key -> {
            try {
                final StatsObject data = statsCache.getIfPresent(key);
                if (data != null) {
                    data.increment();
                }
            } catch (final Exception e) {
                final StringBuilder buf = createStringBuffer(keyObj, System.currentTimeMillis());
                buf.append('\t').append("action:record");
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
        if (keyObj instanceof final UrlQueue<?> urlQueue) {
            return escapeValue(urlQueue.getUrl());
        }
        if (keyObj instanceof final StatsKeyObject statsKey) {
            return escapeValue(statsKey.getUrl());
        }
        if (keyObj instanceof final String key) {
            return escapeValue(key);
        }
        if (keyObj instanceof final Number key) {
            return key.toString();
        }
        return "-";
    }

    protected OptionalThing<String> getCacheKey(final Object keyObj) {
        if (keyObj instanceof final UrlQueue<?> urlQueue) {
            return OptionalThing.of(urlQueue.getId().toString());
        }
        if (keyObj instanceof final StatsKeyObject statsKey) {
            return OptionalThing.of(statsKey.getId());
        }
        if (keyObj instanceof final String key) {
            return OptionalThing.of(key);
        }
        if (keyObj instanceof final Number key) {
            return OptionalThing.of(key.toString());
        }
        return OptionalThing.empty();
    }

    protected String escapeValue(final String action) {
        return action.replace('\t', ' ');
    }

    protected void log(final StringBuilder buf) {
        statsLogger.info(buf.toString());
    }

    public void setLoggerName(final String loggerName) {
        this.loggerName = loggerName;
    }

    public void setMaxCacheSize(final long maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    public void setCacheExpireAfterWrite(final long cacheExpireAfterWrite) {
        this.cacheExpireAfterWrite = cacheExpireAfterWrite;
    }

    public static class StatsKeyObject {

        private final String id;

        private String url;

        public StatsKeyObject(final String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setUrl(final String url) {
            this.url = url;
        }

        protected String getUrl() {
            if (url != null) {
                return url;
            }
            return id;
        }
    }

    public static class StatsObject extends LinkedHashMap<String, Long> {
        private static final long serialVersionUID = 1L;

        protected final AtomicInteger count;

        public StatsObject() {
            put(BEGIN_KEY, System.currentTimeMillis());
            count = new AtomicInteger(1);
        }

        public int increment() {
            return count.incrementAndGet();
        }

        public int decrement() {
            return count.decrementAndGet();
        }
    }

    public enum StatsAction {
        ACCESSED, //
        ACCESS_EXCEPTION, //
        CHILD_URL, //
        CHILD_URLS, //
        EVALUATED, //
        EXCEPTION, //
        FINISHED, //
        PARSED, //
        PREPARED, //
        REDIRECTED, //
        PROCESSED,//
    }
}
