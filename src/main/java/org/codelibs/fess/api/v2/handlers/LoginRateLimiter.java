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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.LongSupplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * In-memory, per-instance rate limiter for /api/v2/auth/login (and /auth/password).
 *
 * <p>Maintains a sliding window of attempt timestamps per (scope, key) pair plus
 * an optional lockout-until timestamp. Three scopes are recognized: IP, USER
 * and CHAT. {@link Scope#CHAT} is used by the v2 chat endpoints to throttle
 * per-user chat invocations independently of the login buckets.
 * Thresholds are passed in per call so the manager can read FessConfig once and
 * forward the values; this keeps the helper test-friendly (no static config).</p>
 *
 * <p>Eviction: each {@link #allow} call drops timestamps older than the window
 * from the head of the queue. Idle keys remain in the map until the next call;
 * {@link #sweep} can be invoked periodically to evict empty entries.</p>
 *
 * <p>Memory cap: the entries map is bounded by {@link #effectiveCap}. When the cap
 * is reached, eviction picks the oldest <em>idle</em> entry (no in-window hits AND
 * no active lockout) — M-4: locked-out entries are never evicted, otherwise an
 * attacker could pump bogus usernames to silently release a victim's lockout. If
 * every entry is either locked or active the map is allowed to grow above cap by
 * one and a WARN is logged (best-effort policy: prefer correctness over a strict
 * cap). The cap defaults to {@link #DEFAULT_MAX_ENTRIES} (100,000) and can be
 * tuned via the {@code theme.api.login.rate.limit.max.entries} property.</p>
 *
 * <p>Sweep schedule: on DI init a {@link TimeoutManager} periodic task calls
 * {@link #sweep()} every {@link #SWEEP_INTERVAL_SECONDS} seconds (default 300).
 * This is the same background-task pattern used by CoordinatorHelper and
 * LogNotificationHelper in this codebase.</p>
 *
 * <p>MJ-30 i18n note: error.message is developer-facing English. Clients MUST use
 * error.code (the V2ErrorCode token) for user-facing i18n.</p>
 *
 * <p>Multi-node deployments require LB-level rate limiting; see Plan 3 risks.</p>
 */
public class LoginRateLimiter {

    private static final Logger logger = LogManager.getLogger(LoginRateLimiter.class);

    /** Default sweep interval in seconds; matches a 5-minute schedule. */
    static final int SWEEP_INTERVAL_SECONDS = 300;

    /** Default maximum number of distinct (scope, key) entries in the map. */
    static final int DEFAULT_MAX_ENTRIES = 100_000;

    /**
     * Rate-limit scope: distinguishes the bucket namespace so the same
     * {@code key} value (e.g. {@code "alice"}) can be tracked independently
     * for login throttling and chat throttling.
     */
    public enum Scope {
        /** Per-client-IP login bucket. */
        IP,
        /** Per-username login bucket. */
        USER,
        /** Per-user chat-invocation bucket used by the v2 chat endpoints. */
        CHAT
    }

    private static final class Entry {
        final Deque<Long> hits = new ArrayDeque<>();
        long lockUntilEpochMs = 0L;
    }

    private final LongSupplier clock;

    /**
     * Effective cap — initialised to {@link #DEFAULT_MAX_ENTRIES} and updated from
     * FessConfig in {@link #init()} if the property is configured. Using a volatile
     * int rather than a final int allows the DI lifecycle to override the cap from
     * config without requiring a per-test constructor parameter.
     */
    private volatile int effectiveCap;

    /** Set to true once the map reaches effectiveCap to avoid flooding the log. */
    private volatile boolean capWarnLogged = false;

    /**
     * Insertion-ordered map capped at {@link #effectiveCap}. Access-order=false so the
     * eldest-inserted entry is the natural FIFO candidate. Eviction is deliberately NOT
     * delegated to {@link LinkedHashMap#removeEldestEntry} because that would happily
     * evict a locked-out entry — letting an attacker pump bogus usernames to silently
     * release a victim's lockout. Instead, all insertions go through
     * {@link #insertWithEviction} which scans for an idle (no hits, no active lockout)
     * eldest entry; if none exists the map grows by one and a WARN is logged. All
     * mutations and removals are guarded by synchronized(entries).
     */
    private final Map<String, Entry> entries;

    private org.codelibs.core.timer.TimeoutTask sweepTask;

    /**
     * Default constructor used by the DI container. Uses
     * {@link System#currentTimeMillis} as the clock and {@link #DEFAULT_MAX_ENTRIES}
     * as the initial cap; {@link #init()} may override the cap from
     * {@code FessConfig} once DI is fully wired.
     */
    public LoginRateLimiter() {
        this(System::currentTimeMillis, DEFAULT_MAX_ENTRIES);
    }

    LoginRateLimiter(final LongSupplier clock) {
        this(clock, DEFAULT_MAX_ENTRIES);
    }

    LoginRateLimiter(final LongSupplier clock, final int maxEntries) {
        this.clock = clock;
        this.effectiveCap = maxEntries;
        // accessOrder=false: iteration order is insertion order, used by insertWithEviction
        // to find the oldest idle entry. We do NOT override removeEldestEntry — eviction is
        // performed explicitly in insertWithEviction so locked-out entries are never evicted.
        this.entries = new LinkedHashMap<>(16, 0.75f, false);
    }

    /**
     * Inserts a fresh Entry for {@code mapKey}, performing best-effort eviction to honour
     * {@link #effectiveCap}. Locked-out entries (lockUntilEpochMs >= now) are NEVER evicted
     * — M-4: otherwise an attacker could pump bogus usernames to silently release a victim's
     * lockout. The eldest entry whose lockout has expired AND whose hit-deque contains no
     * timestamp newer than the {@code idleWindowMs} threshold is chosen as the victim; if
     * no such entry exists the map is allowed to grow above cap by one and a WARN is
     * logged (best-effort policy: prefer correctness over a strict cap).
     *
     * <p>The {@code idleWindowMs} bound is the largest of any sliding window we care about
     * — for the login limiter the call sites use 60s so a hit older than 60s is
     * effectively stale. We use {@link #SWEEP_INTERVAL_SECONDS} * 1000 (i.e. 5min) as the
     * conservative idle threshold so that an entry with very-old stale hits is still
     * considered evictable even when called outside the normal sweep cadence.</p>
     *
     * <p>Must be called while holding {@code synchronized(entries)}.</p>
     *
     * @return the Entry now associated with {@code mapKey}
     */
    private Entry insertWithEviction(final String mapKey, final long now) {
        if (entries.size() >= effectiveCap) {
            // Conservative idle threshold: any hit older than the sweep interval is stale.
            // (The handler's sliding windows are bounded by 60s; we use 5min as a defensive
            // upper bound so this remains safe even if future callers raise window sizes.)
            final long idleWindowMs = (long) SWEEP_INTERVAL_SECONDS * 1_000L;
            String victim = null;
            for (final Map.Entry<String, Entry> en : entries.entrySet()) {
                final Entry candidate = en.getValue();
                synchronized (candidate) {
                    // M-4: skip locked-out entries entirely. A locked-out entry must
                    // survive cap pressure — otherwise an attacker could overflow the
                    // map with bogus keys to release a victim's lockout.
                    if (candidate.lockUntilEpochMs >= now) {
                        continue;
                    }
                    // Idle = no hit within the idle window. We compare against peekLast()
                    // because hits are appended in chronological order, so the newest hit
                    // is at the tail. If even the newest hit is older than idleWindowMs, the
                    // entry is stale and safe to evict.
                    if (candidate.hits.isEmpty() || candidate.hits.peekLast() < now - idleWindowMs) {
                        victim = en.getKey();
                        break;
                    }
                }
            }
            if (victim != null) {
                entries.remove(victim);
                if (capWarnLogged && entries.size() < effectiveCap / 2) {
                    capWarnLogged = false;
                }
            } else {
                // All entries are either locked-out or have a recent hit. Best-effort policy:
                // allow the map to grow by one rather than evicting a locked entry, and log
                // WARN so operators see the cap-pressure signal.
                if (!capWarnLogged) {
                    logger.warn(
                            "LoginRateLimiter entries map at cap={} but no idle entry to evict "
                                    + "(all locked or active); growing temporarily. "
                                    + "Consider increasing theme.api.login.rate.limit.max.entries or investigating traffic anomaly.",
                            effectiveCap);
                    capWarnLogged = true;
                }
            }
        }
        final Entry e = new Entry();
        entries.put(mapKey, e);
        return e;
    }

    /**
     * Called by Lasta Di after the component is bound. Reads the
     * {@code theme.api.login.rate.limit.max.entries} property (if present) to
     * override the default cap, then registers a periodic sweep task via
     * {@link TimeoutManager} — the same pattern used by CoordinatorHelper and
     * LogNotificationHelper. Sweep runs every {@link #SWEEP_INTERVAL_SECONDS}
     * seconds to evict entries whose windows have expired.
     */
    @PostConstruct
    public void init() {
        try {
            final Integer configCap = ComponentUtil.getFessConfig().getThemeApiLoginRateLimitMaxEntriesAsInteger();
            if (configCap != null && configCap > 0) {
                effectiveCap = configCap;
            }
        } catch (final Exception e) {
            // FessConfig not available in slim test harnesses — keep DEFAULT_MAX_ENTRIES.
            logger.debug("LoginRateLimiter: could not read max entries from FessConfig; using default={}", effectiveCap);
        }
        try {
            sweepTask = TimeoutManager.getInstance().addTimeoutTarget(this::sweep, SWEEP_INTERVAL_SECONDS, true);
            if (logger.isInfoEnabled()) {
                logger.info("LoginRateLimiter sweep scheduled: intervalSeconds={}, maxEntries={}", SWEEP_INTERVAL_SECONDS, effectiveCap);
            }
        } catch (final Exception e) {
            // TimeoutManager may be absent in slim test DI graphs — degrade gracefully.
            logger.warn("LoginRateLimiter could not register sweep task; map will not be automatically evicted.", e);
        }
    }

    /** Stops the background sweep task on shutdown. */
    @PreDestroy
    public void destroy() {
        if (sweepTask != null) {
            sweepTask.stop();
        }
    }

    /**
     * Attempts to consume one slot in the (scope, key) bucket within a sliding
     * window of {@code windowSeconds}. Expired timestamps at the head of the
     * queue are evicted first; if the bucket is currently locked out or already
     * at {@code maxPerWindow} the call returns {@code false} without recording
     * a hit.
     *
     * <p>MJ-6: empty or null {@code key} is denied rather than silently
     * allowed; an empty key usually means the caller failed to resolve the
     * client identity, and granting access would bypass the gate entirely.</p>
     *
     * @param scope rate-limit scope (e.g. IP, USER, CHAT)
     * @param key bucket key (e.g. IP address or username)
     * @param maxPerWindow upper bound of attempts permitted within {@code windowSeconds};
     *                     values {@code <= 0} disable the gate and return {@code true}
     * @param windowSeconds sliding-window width in seconds
     * @return {@code true} if the slot was consumed, {@code false} if the bucket is full or locked out
     */
    public boolean allow(final Scope scope, final String key, final int maxPerWindow, final int windowSeconds) {
        if (maxPerWindow <= 0) {
            return true;
        }
        // MJ-6: empty or null key is denied rather than silently allowed.
        // Defense-in-depth: an empty key usually means the caller failed to resolve
        // the client IP or username; granting access would bypass the gate entirely.
        if (key == null || key.isEmpty()) {
            return false;
        }
        final String mapKey = scope.name() + ":" + key;
        final long now = clock.getAsLong();
        final long windowMs = (long) windowSeconds * 1_000L;
        final Entry e;
        synchronized (entries) {
            Entry existing = entries.get(mapKey);
            if (existing == null) {
                existing = insertWithEviction(mapKey, now);
            }
            e = existing;
        }
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

    /**
     * Returns true when a fresh attempt against (scope, key) would currently be admitted by
     * {@link #allow}, but without consuming a bucket slot. Use this to short-circuit work
     * (e.g. credential verification) when the bucket is already exhausted, then call
     * {@link #allow} only on the failure path so success and system-error paths do not
     * count against the legitimate user's quota.
     *
     * <p>MJ-6: returns {@code false} (deny) when key is null or empty — defense in depth.
     * Empty key means the caller could not resolve the identity; passing rather than denying
     * would silently bypass the gate.</p>
     *
     * @param scope rate-limit scope (e.g. IP, USER, CHAT)
     * @param key bucket key (e.g. IP address or username)
     * @param maxPerWindow upper bound of attempts permitted within {@code windowSeconds}
     * @param windowSeconds sliding-window width
     * @return true if a subsequent {@link #allow} would currently succeed
     */
    public boolean peek(final Scope scope, final String key, final int maxPerWindow, final int windowSeconds) {
        if (maxPerWindow <= 0) {
            return true;
        }
        // MJ-6: empty/null key → deny (same rationale as allow()).
        if (key == null || key.isEmpty()) {
            return false;
        }
        final String mapKey = scope.name() + ":" + key;
        final long now = clock.getAsLong();
        final long windowMs = (long) windowSeconds * 1_000L;
        final Entry e;
        synchronized (entries) {
            e = entries.get(mapKey);
        }
        if (e == null) {
            return true;
        }
        synchronized (e) {
            if (now < e.lockUntilEpochMs) {
                return false;
            }
            // Note: we intentionally do not evict expired hits here — peek is a side-effect-free
            // check; the next allow() will evict them. This keeps peek() cheap and predictable.
            int active = 0;
            for (final long t : e.hits) {
                if (t >= now - windowMs) {
                    active++;
                }
            }
            return active < maxPerWindow;
        }
    }

    /**
     * m-21: uses Math.max so that a shorter lockoutSeconds value supplied in a second
     * call never shrinks an existing longer lockout — the stricter deadline always wins.
     *
     * @param scope rate-limit scope (e.g. IP, USER, CHAT) that the lockout applies to
     * @param key bucket key being locked out (e.g. IP address or username); ignored when {@code null} or empty
     * @param lockoutSeconds duration of the lockout in seconds; ignored when {@code <= 0}
     */
    public void lockOut(final Scope scope, final String key, final int lockoutSeconds) {
        if (key == null || key.isEmpty() || lockoutSeconds <= 0) {
            return;
        }
        final String mapKey = scope.name() + ":" + key;
        final long now = clock.getAsLong();
        final Entry e;
        synchronized (entries) {
            Entry existing = entries.get(mapKey);
            if (existing == null) {
                existing = insertWithEviction(mapKey, now);
            }
            e = existing;
        }
        synchronized (e) {
            e.lockUntilEpochMs = Math.max(e.lockUntilEpochMs, now + (long) lockoutSeconds * 1_000L);
        }
    }

    /**
     * Clears the rate-limit bucket for (scope, key) after a successful login so that
     * the user is not penalized for earlier failed attempts in the same window.
     *
     * <p>MJ-5: called by LoginHandler on the successful-login path for both Scope.USER
     * (by username) and Scope.IP (by client IP). This prevents a legitimate user who
     * previously exceeded the window from being locked out on their next login after
     * the correct password is supplied.</p>
     *
     * @param scope the rate-limit scope
     * @param key the bucket key (username or IP address)
     */
    public void clear(final Scope scope, final String key) {
        if (key == null || key.isEmpty()) {
            return;
        }
        final String mapKey = scope.name() + ":" + key;
        synchronized (entries) {
            entries.remove(mapKey);
        }
    }

    /** Removes empty entries; safe to call from any thread. */
    public void sweep() {
        final long now = clock.getAsLong();
        synchronized (entries) {
            final Iterator<Map.Entry<String, Entry>> it = entries.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<String, Entry> en = it.next();
                final Entry existing = en.getValue();
                synchronized (existing) {
                    if (existing.hits.isEmpty() && existing.lockUntilEpochMs < now) {
                        it.remove();
                    }
                }
            }
        }
        if (logger.isDebugEnabled()) {
            synchronized (entries) {
                logger.debug("LoginRateLimiter.sweep completed: remaining entries={}", entries.size());
            }
        }
    }
}
