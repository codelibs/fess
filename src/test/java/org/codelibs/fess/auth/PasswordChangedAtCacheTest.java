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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.google.common.base.Ticker;

/**
 * Tests the cache of password change times: what it holds, for how long, and that a lookup that
 * fails or races with a change never makes a session look older than it is.
 */
public class PasswordChangedAtCacheTest extends UnitFessTestCase {

    private final Map<String, Long> stored = new HashMap<>();

    private final AtomicInteger reads = new AtomicInteger();

    private RuntimeException failure;

    private int expire = 10;

    private long nanos;

    private PasswordChangedAtCache cache;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getPasswordChangedCacheExpireAsInteger() {
                return Integer.valueOf(expire);
            }
        });
        ComponentUtil.register(new UserService() {
            @Override
            public long getPasswordChangedAt(final String username) {
                reads.incrementAndGet();
                if (failure != null) {
                    throw failure;
                }
                return stored.getOrDefault(username, 0L);
            }
        }, UserService.class.getCanonicalName());
        cache = new PasswordChangedAtCache() {
            @Override
            protected Ticker getTicker() {
                return new Ticker() {
                    @Override
                    public long read() {
                        return nanos;
                    }
                };
            }
        };
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    @Test
    public void test_get_readsOnceWithinTheTime_andAgainAfterIt() {
        stored.put("alice", 100L);

        assertEquals(100L, cache.get("alice"));
        assertEquals(100L, cache.get("alice"));
        assertEquals(1, reads.get());

        stored.put("alice", 200L);
        nanos += TimeUnit.SECONDS.toNanos(9);
        assertEquals(100L, cache.get("alice"));

        nanos += TimeUnit.SECONDS.toNanos(2);
        assertEquals(200L, cache.get("alice"));
        assertEquals(2, reads.get());
    }

    @Test
    public void test_get_keepsEachUserApart() {
        stored.put("alice", 100L);
        stored.put("bob", 300L);

        assertEquals(100L, cache.get("alice"));
        assertEquals(300L, cache.get("bob"));
        assertEquals(2, reads.get());
    }

    @Test
    public void test_get_withoutExpire_readsEveryTime() {
        expire = 0;
        stored.put("alice", 100L);

        assertEquals(100L, cache.get("alice"));
        assertEquals(100L, cache.get("alice"));
        assertEquals(2, reads.get());
        cache.put("alice", 500L); // nothing to keep it in
        assertEquals(100L, cache.get("alice"));
    }

    @Test
    public void test_get_expireThatIsNotANumber_usesTheDefault() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getPasswordChangedCacheExpireAsInteger() {
                throw new NumberFormatException("x");
            }
        });
        stored.put("alice", 100L);

        assertEquals(100L, cache.get("alice"));
        assertEquals(100L, cache.get("alice"));
        assertEquals(1, reads.get());
    }

    @Test
    public void test_put_isSeenByGet_withoutReading() {
        cache.put("alice", 500L);

        assertEquals(500L, cache.get("alice"));
        assertEquals(0, reads.get());
    }

    @Test
    public void test_put_neverGoesBackwards() {
        cache.put("alice", 500L);
        cache.put("alice", 400L);

        assertEquals(500L, cache.get("alice"));
    }

    @Test
    public void test_get_readThatRacesWithAChange_doesNotOverwriteIt() {
        // the change is kept after the read began and before it returned the older time
        ComponentUtil.register(new UserService() {
            @Override
            public long getPasswordChangedAt(final String username) {
                cache.put(username, 500L);
                return 100L;
            }
        }, UserService.class.getCanonicalName());

        assertEquals(500L, cache.get("alice"));
        assertEquals(500L, cache.get("alice"));
    }

    @Test
    public void test_get_failure_answersZero_andIsNotKept() {
        failure = new IllegalStateException("search engine is down");
        assertEquals(0L, cache.get("alice"));

        failure = null;
        stored.put("alice", 100L);
        assertEquals(100L, cache.get("alice"));
    }
}
