/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.taglib.FessFunctions;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Helper class for managing crawler statistics and performance metrics.
 * This class provides functionality to track, record, and report statistics
 * about crawler operations including timing data, performance metrics, and
 * operational events. It uses an internal cache to maintain statistics
 * objects and provides methods to begin tracking, record events, and
 * finalize statistics collection.
 *
 * @author shinsuke
 */
public class CrawlerStatsHelper {
    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(CrawlerStatsHelper.class);

    /** Key used to store the begin timestamp in statistics objects. */
    private static final String BEGIN_KEY = "begin";

    /** Logger instance specifically for outputting crawler statistics. */
    protected Logger statsLogger = null;

    /** Name of the logger used for statistics output. */
    protected String loggerName = "fess.log.crawler.stats";

    /** Maximum number of statistics objects to cache. */
    protected long maxCacheSize = 1000;

    /** Time in milliseconds after which cache entries expire after write. */
    protected long cacheExpireAfterWrite = 10 * 60 * 1000L;

    /** Cache for storing statistics objects keyed by crawler object identifiers. */
    protected LoadingCache<String, StatsObject> statsCache;

    /**
     * Initializes the crawler statistics helper.
     * Sets up the statistics logger and creates the cache for storing
     * statistics objects with the configured size and expiration settings.
     */
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

    /**
     * Cleanup method called when the helper is being destroyed.
     * Logs cache statistics and processes any remaining statistics
     * objects in the cache before shutdown.
     */
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

    /**
     * Begins statistics tracking for the specified crawler object.
     * Creates a new statistics object in the cache and starts timing.
     *
     * @param keyObj the crawler object to track (UrlQueue, StatsKeyObject, String, or Number)
     */
    public void begin(final Object keyObj) {
        getCacheKey(keyObj).ifPresent(key -> {
            try {
                statsCache.get(key);
            } catch (final Exception e) {
                final StringBuilder buf = createStringBuffer(keyObj, getCurrentTimeMillis());
                buf.append('\t').append("action:begin");
                buf.append('\t').append("error:").append(escapeValue(e.getLocalizedMessage()).replaceAll("\\s", " "));
                log(buf);
            }
        });
    }

    /**
     * Records a statistics action for the specified crawler object.
     *
     * @param keyObj the crawler object being tracked
     * @param action the statistics action to record
     */
    public void record(final Object keyObj, final StatsAction action) {
        record(keyObj, action.name().toLowerCase(Locale.ENGLISH));
    }

    /**
     * Records a custom statistics action for the specified crawler object.
     *
     * @param keyObj the crawler object being tracked
     * @param action the custom action name to record
     */
    public void record(final Object keyObj, final String action) {
        getCacheKey(keyObj).ifPresent(key -> {
            try {
                final StatsObject data = statsCache.getIfPresent(key);
                if (data != null) {
                    data.put(escapeValue(action), getCurrentTimeMillis());
                }
            } catch (final Exception e) {
                final StringBuilder buf = createStringBuffer(keyObj, getCurrentTimeMillis());
                buf.append('\t').append("action:record");
                buf.append('\t').append("error:").append(escapeValue(e.getLocalizedMessage()).replaceAll("\\s", " "));
                log(buf);
            }
        });
    }

    /**
     * Marks statistics tracking as complete for the specified crawler object.
     * Decrements the reference count and if it reaches zero, removes the
     * statistics object from cache and outputs the final statistics.
     *
     * @param keyObj the crawler object to complete tracking for
     */
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
                final StringBuilder buf = createStringBuffer(keyObj, getCurrentTimeMillis());
                buf.append('\t').append("action:done");
                buf.append('\t').append("error:").append(escapeValue(e.getLocalizedMessage()).replaceAll("\\s", " "));
                log(buf);
            }
        });
    }

    /**
     * Discards statistics tracking for the specified crawler object.
     * Removes the statistics object from cache without outputting statistics.
     *
     * @param keyObj the crawler object to discard tracking for
     */
    public void discard(final Object keyObj) {
        getCacheKey(keyObj).ifPresent(key -> {
            try {
                final StatsObject data = statsCache.getIfPresent(key);
                if (data != null) {
                    statsCache.invalidate(key);
                }
            } catch (final Exception e) {
                final StringBuilder buf = createStringBuffer(keyObj, getCurrentTimeMillis());
                buf.append('\t').append("action:done");
                buf.append('\t').append("error:").append(escapeValue(e.getLocalizedMessage()).replaceAll("\\s", " "));
                log(buf);
            }
        });
    }

    /**
     * Outputs statistics information for a crawler object.
     *
     * @param keyObj the crawler object the statistics relate to
     * @param data the statistics data to output
     * @param begin the timestamp when tracking began
     * @param done whether tracking was completed normally
     */
    protected void printStats(final Object keyObj, final StatsObject data, final long begin, final boolean done) {
        final StringBuilder buf = createStringBuffer(keyObj, begin);
        if (done) {
            buf.append('\t').append("done:").append(getCurrentTimeMillis() - begin);
        }
        data.entrySet().stream().map(e -> escapeValue(e.getKey()) + ":" + (e.getValue().longValue() - begin)).map(s -> "\t" + s)
                .forEach(s -> buf.append(s));
        log(buf);
    }

    /**
     * Increments the thread reference count for the specified crawler object.
     * Used when the same object is being processed on multiple threads.
     *
     * @param keyObj the crawler object running on an additional thread
     */
    public void runOnThread(final Object keyObj) {
        getCacheKey(keyObj).ifPresent(key -> {
            try {
                final StatsObject data = statsCache.getIfPresent(key);
                if (data != null) {
                    data.increment();
                }
            } catch (final Exception e) {
                final StringBuilder buf = createStringBuffer(keyObj, getCurrentTimeMillis());
                buf.append('\t').append("action:record");
                buf.append('\t').append("error:").append(escapeValue(e.getLocalizedMessage()).replaceAll("\\s", " "));
                log(buf);
            }
        });
    }

    /**
     * Gets the current system time in milliseconds.
     *
     * @return current time in milliseconds
     */
    protected long getCurrentTimeMillis() {
        return ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
    }

    /**
     * Creates a string buffer for logging statistics information.
     *
     * @param keyObj the crawler object
     * @param time the timestamp to include
     * @return a StringBuilder with basic log information
     */
    private StringBuilder createStringBuffer(final Object keyObj, final long time) {
        final StringBuilder buf = new StringBuilder(1000);
        buf.append("url:").append(getUrl(keyObj));
        buf.append('\t');
        buf.append("time:").append(FessFunctions.formatDate(new Date(time)));
        return buf;
    }

    /**
     * Extracts the URL string from a crawler object for logging purposes.
     *
     * @param keyObj the crawler object to extract URL from
     * @return the URL string or a default value if not extractable
     */
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

    /**
     * Generates a cache key from a crawler object.
     *
     * @param keyObj the crawler object to generate key for
     * @return Optional cache key string, empty if object type not supported
     */
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

    /**
     * Escapes special characters in a value string for safe logging.
     *
     * @param action the string value to escape
     * @return the escaped string with tabs replaced by spaces
     */
    protected String escapeValue(final String action) {
        return action.replace('\t', ' ');
    }

    /**
     * Outputs a log message using the statistics logger.
     *
     * @param buf the string buffer containing the log message
     */
    protected void log(final StringBuilder buf) {
        statsLogger.info(buf.toString());
    }

    /**
     * Sets the name of the logger used for statistics output.
     *
     * @param loggerName the logger name to use
     */
    public void setLoggerName(final String loggerName) {
        this.loggerName = loggerName;
    }

    /**
     * Sets the maximum number of statistics objects to cache.
     *
     * @param maxCacheSize the maximum cache size
     */
    public void setMaxCacheSize(final long maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    /**
     * Sets the cache expiration time after write in milliseconds.
     *
     * @param cacheExpireAfterWrite the expiration time in milliseconds
     */
    public void setCacheExpireAfterWrite(final long cacheExpireAfterWrite) {
        this.cacheExpireAfterWrite = cacheExpireAfterWrite;
    }

    /**
     * Key object for statistics tracking that contains an identifier and optional URL.
     * Used when tracking statistics for objects that don't have built-in URL extraction.
     */
    public static class StatsKeyObject {

        /** Unique identifier for this statistics key object. */
        private final String id;

        /** Optional URL associated with this statistics key object. */
        private String url;

        /**
         * Creates a new statistics key object with the specified identifier.
         *
         * @param id the unique identifier for this object
         */
        public StatsKeyObject(final String id) {
            this.id = id;
        }

        /**
         * Gets the unique identifier for this statistics key object.
         *
         * @return the identifier
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the URL associated with this statistics key object.
         *
         * @param url the URL to associate with this object
         */
        public void setUrl(final String url) {
            this.url = url;
        }

        /**
         * Gets the URL associated with this statistics key object.
         *
         * @return the URL if set, otherwise returns the identifier
         */
        protected String getUrl() {
            if (url != null) {
                return url;
            }
            return id;
        }
    }

    /**
     * Statistics data object that stores timestamped events and maintains reference counting.
     * Extends LinkedHashMap to store event names mapped to their timestamps.
     * Includes reference counting for multi-threaded access tracking.
     */
    public static class StatsObject extends LinkedHashMap<String, Long> {
        /** Serial version UID for serialization. */
        private static final long serialVersionUID = 1L;

        /** Atomic counter for tracking reference count across multiple threads. */
        protected final AtomicInteger count;

        /**
         * Creates a new statistics object with the current timestamp as the begin time.
         * Initializes the reference count to 1.
         */
        public StatsObject() {
            put(BEGIN_KEY, ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
            count = new AtomicInteger(1);
        }

        /**
         * Increments the reference count for this statistics object.
         *
         * @return the new reference count after incrementing
         */
        public int increment() {
            return count.incrementAndGet();
        }

        /**
         * Decrements the reference count for this statistics object.
         *
         * @return the new reference count after decrementing
         */
        public int decrement() {
            return count.decrementAndGet();
        }
    }

    /**
     * Enumeration of predefined statistics actions that can be recorded
     * during crawler operations. Each action represents a specific event
     * or milestone in the crawling process.
     */
    public enum StatsAction {
        /** Indicates that a URL was successfully accessed. */
        ACCESSED,
        /** Indicates that an exception occurred during URL access. */
        ACCESS_EXCEPTION,
        /** Indicates that a child URL was discovered. */
        CHILD_URL,
        /** Indicates that multiple child URLs were discovered. */
        CHILD_URLS,
        /** Indicates that a URL was evaluated for crawling eligibility. */
        EVALUATED,
        /** Indicates that a general exception occurred during processing. */
        EXCEPTION,
        /** Indicates that processing of a URL has finished. */
        FINISHED,
        /** Indicates that content was successfully parsed. */
        PARSED,
        /** Indicates that a URL was prepared for crawling. */
        PREPARED,
        /** Indicates that a URL redirect was encountered. */
        REDIRECTED,
        /** Indicates that a URL was processed completely. */
        PROCESSED
    }
}
