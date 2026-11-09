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
package org.codelibs.fess.auth;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.LogUtil;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Holds, for a short time, when the password of each local user was last changed, as it is read
 * from the user index. A login session is checked against it on every request, which would
 * otherwise search the user index on every request. The component is a singleton, so all sessions
 * of a node share one cache.
 *
 * <p>The entries expire after {@code password.changed.cache.expire} seconds; with 0 or less
 * nothing is kept and every read goes to the user index.</p>
 */
public class PasswordChangedAtCache {

    private static final Logger logger = LogManager.getLogger(PasswordChangedAtCache.class);

    /** The most entries held. */
    protected static final long MAX_SIZE = 10000L;

    /** The seconds used when {@code password.changed.cache.expire} is not a number. */
    protected static final int DEFAULT_EXPIRE = 10;

    private volatile Cache<String, Long> cache;

    /**
     * Default constructor for the DI container.
     */
    public PasswordChangedAtCache() {
        // default constructor
    }

    /**
     * Gets the time the password of a local user was last changed.
     *
     * <p>When the time cannot be read, the answer is 0, which is never later than what a session
     * remembers, and it is not kept: a login session is not ended because the search engine is
     * unreachable.</p>
     *
     * @param username the name of the user
     * @return epoch milliseconds, or 0 when there is none
     */
    public long get(final String username) {
        final Cache<String, Long> c = getCache();
        final Long cached = c != null ? c.getIfPresent(username) : null;
        if (cached != null) {
            return cached.longValue();
        }
        final long changedAt;
        try {
            changedAt = ComponentUtil.getComponent(UserService.class).getPasswordChangedAt(username);
        } catch (final RuntimeException e) {
            LogUtil.warnOnce(logger, "read",
                    "Could not read the time of the last password change, so login sessions are not checked against it until it can be read.",
                    e);
            return 0L;
        }
        return c != null ? keep(c, username, changedAt) : changedAt;
    }

    /**
     * Keeps the time a password was changed at, on this node.
     *
     * @param username the name of the user
     * @param changedAt epoch milliseconds
     */
    public void put(final String username, final long changedAt) {
        final Cache<String, Long> c = getCache();
        if (c != null) {
            keep(c, username, changedAt);
        }
    }

    /**
     * Keeps the later of the held and the given time. Times only increase, and a read of the user
     * index that began before a change must not put an older time over the one the change kept.
     */
    private static long keep(final Cache<String, Long> cache, final String username, final long changedAt) {
        return cache.asMap().merge(username, changedAt, Math::max).longValue();
    }

    /**
     * Gets the clock of the cache.
     *
     * @return the ticker
     */
    protected Ticker getTicker() {
        return Ticker.systemTicker();
    }

    private Cache<String, Long> getCache() {
        final int expire = getExpire();
        if (expire <= 0) {
            return null;
        }
        Cache<String, Long> c = cache;
        if (c == null) {
            synchronized (this) {
                c = cache;
                if (c == null) {
                    c = CacheBuilder.newBuilder()
                            .maximumSize(MAX_SIZE)
                            .expireAfterWrite(expire, TimeUnit.SECONDS)
                            .ticker(getTicker())
                            .build();
                    cache = c;
                }
            }
        }
        return c;
    }

    private int getExpire() {
        try {
            return ComponentUtil.getFessConfig().getPasswordChangedCacheExpireAsInteger().intValue();
        } catch (final RuntimeException e) {
            LogUtil.warnOnce(logger, "expire", "password.changed.cache.expire is not a number, so {} seconds is used.", DEFAULT_EXPIRE, e);
            return DEFAULT_EXPIRE;
        }
    }
}
