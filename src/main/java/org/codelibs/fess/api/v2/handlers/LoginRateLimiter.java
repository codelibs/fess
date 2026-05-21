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
package org.codelibs.fess.api.v2.handlers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * In-memory, per-instance rate limiter for /api/v2/auth/login (and /auth/password).
 *
 * <p>Maintains a sliding window of attempt timestamps per (scope, key) pair plus
 * an optional lockout-until timestamp. Two scopes are recognized: IP and USER.
 * Thresholds are passed in per call so the manager can read FessConfig once and
 * forward the values; this keeps the helper test-friendly (no static config).</p>
 *
 * <p>Eviction: each {@link #allow} call drops timestamps older than the window
 * from the head of the queue. Idle keys remain in the map until the next call;
 * {@link #sweep} can be invoked periodically to evict empty entries.</p>
 *
 * <p>Multi-node deployments require LB-level rate limiting; see Plan 3 risks.</p>
 */
public class LoginRateLimiter {

    public enum Scope {
        IP, USER
    }

    private static final class Entry {
        final Deque<Long> hits = new ArrayDeque<>();
        long lockUntilEpochMs = 0L;
    }

    private final LongSupplier clock;
    private final Map<String, Entry> entries = new ConcurrentHashMap<>();

    public LoginRateLimiter() {
        this(System::currentTimeMillis);
    }

    LoginRateLimiter(final LongSupplier clock) {
        this.clock = clock;
    }

    public boolean allow(final Scope scope, final String key, final int maxPerWindow, final int windowSeconds) {
        if (maxPerWindow <= 0) {
            return true;
        }
        if (key == null || key.isEmpty()) {
            return true;
        }
        final String mapKey = scope.name() + ":" + key;
        final long now = clock.getAsLong();
        final long windowMs = windowSeconds * 1_000L;
        final Entry e = entries.computeIfAbsent(mapKey, k -> new Entry());
        synchronized (e) {
            if (now < e.lockUntilEpochMs) {
                return false;
            }
            while (!e.hits.isEmpty() && e.hits.peekFirst() < now - windowMs) {
                e.hits.pollFirst();
            }
            if (e.hits.size() >= maxPerWindow) {
                return false;
            }
            e.hits.addLast(now);
            return true;
        }
    }

    public void lockOut(final Scope scope, final String key, final int lockoutSeconds) {
        if (key == null || key.isEmpty() || lockoutSeconds <= 0) {
            return;
        }
        final String mapKey = scope.name() + ":" + key;
        final Entry e = entries.computeIfAbsent(mapKey, k -> new Entry());
        synchronized (e) {
            e.lockUntilEpochMs = clock.getAsLong() + lockoutSeconds * 1_000L;
        }
    }

    /** Removes empty entries; safe to call from any thread. */
    public void sweep() {
        entries.entrySet().removeIf(en -> {
            synchronized (en.getValue()) {
                final Entry e = en.getValue();
                return e.hits.isEmpty() && e.lockUntilEpochMs < clock.getAsLong();
            }
        });
    }
}
