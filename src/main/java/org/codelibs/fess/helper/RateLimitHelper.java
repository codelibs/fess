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

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Helper class for rate limiting functionality.
 * Implements a sliding window algorithm for request counting
 * and manages IP-based blocking using Guava Cache for automatic expiration.
 */
public class RateLimitHelper {

    private static final Logger logger = LogManager.getLogger(RateLimitHelper.class);

    /**
     * Request counters per IP address.
     * Entries automatically expire after the configured window period.
     */
    protected Cache<String, AtomicLong> requestCounters;

    /**
     * Blocked IPs with automatic expiration.
     * Entries automatically expire after the configured block duration.
     */
    protected Cache<String, Boolean> blockedIps;

    /**
     * Default constructor.
     */
    public RateLimitHelper() {
        // nothing
    }

    /**
     * Initialize caches with configuration values.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing {}", this.getClass().getSimpleName());
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final long windowMs = fessConfig.getRateLimitWindowMsAsInteger().longValue();
        final long blockDurationMs = fessConfig.getRateLimitBlockDurationMsAsInteger().longValue();

        requestCounters = CacheBuilder.newBuilder().expireAfterWrite(windowMs, TimeUnit.MILLISECONDS).maximumSize(10000).build();

        blockedIps = CacheBuilder.newBuilder().expireAfterWrite(blockDurationMs, TimeUnit.MILLISECONDS).maximumSize(10000).build();

        if (logger.isInfoEnabled()) {
            logger.info("RateLimitHelper initialized: windowMs={}, blockDurationMs={}", windowMs, blockDurationMs);
        }
    }

    /**
     * Check if rate limiting is enabled.
     * @return true if rate limiting is enabled
     */
    public boolean isEnabled() {
        return ComponentUtil.getFessConfig().isRateLimitEnabled();
    }

    /**
     * Get the client IP address from the request, considering proxy headers.
     * Only trusts X-Forwarded-For/X-Real-IP headers when the request comes from a trusted proxy.
     * @param request the HTTP request
     * @return the client IP address
     */
    public String getClientIp(final HttpServletRequest request) {
        final String remoteAddr = request.getRemoteAddr();

        // Only trust proxy headers if the request comes from a trusted proxy
        if (isTrustedProxy(remoteAddr)) {
            final String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (StringUtil.isNotBlank(xForwardedFor)) {
                final String clientIp = xForwardedFor.split(",")[0].trim();
                if (logger.isDebugEnabled()) {
                    logger.debug("Client IP from X-Forwarded-For: clientIp={}, remoteAddr={}", clientIp, remoteAddr);
                }
                return clientIp;
            }
            final String xRealIp = request.getHeader("X-Real-IP");
            if (StringUtil.isNotBlank(xRealIp)) {
                final String clientIp = xRealIp.trim();
                if (logger.isDebugEnabled()) {
                    logger.debug("Client IP from X-Real-IP: clientIp={}, remoteAddr={}", clientIp, remoteAddr);
                }
                return clientIp;
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Client IP from remoteAddr: ip={}", remoteAddr);
        }
        return remoteAddr;
    }

    /**
     * Check if the IP is a trusted proxy.
     * @param ip the IP address to check
     * @return true if the IP is a trusted proxy
     */
    protected boolean isTrustedProxy(final String ip) {
        final Set<String> trustedProxies = ComponentUtil.getFessConfig().getRateLimitTrustedProxiesAsSet();
        final boolean trusted = trustedProxies.contains(ip);
        if (logger.isDebugEnabled() && trusted) {
            logger.debug("Trusted proxy detected: ip={}", ip);
        }
        return trusted;
    }

    /**
     * Check if the IP is in the whitelist.
     * @param ip the IP address to check
     * @return true if whitelisted
     */
    public boolean isWhitelisted(final String ip) {
        final Set<String> whitelist = ComponentUtil.getFessConfig().getRateLimitWhitelistIpsAsSet();
        final boolean whitelisted = whitelist.contains(ip);
        if (logger.isDebugEnabled() && whitelisted) {
            logger.debug("Whitelisted IP: ip={}", ip);
        }
        return whitelisted;
    }

    /**
     * Check if the IP is blocked.
     * @param ip the IP address to check
     * @return true if blocked
     */
    public boolean isBlocked(final String ip) {
        if (isWhitelisted(ip)) {
            return false;
        }

        // Check statically configured blocked IPs
        final Set<String> blockedIpSet = ComponentUtil.getFessConfig().getRateLimitBlockedIpsAsSet();
        if (blockedIpSet.contains(ip)) {
            if (logger.isDebugEnabled()) {
                logger.debug("IP in static block list: ip={}", ip);
            }
            return true;
        }

        // Check dynamically blocked IPs (Cache handles expiration automatically)
        final Boolean blocked = blockedIps.getIfPresent(ip);
        if (blocked != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("IP dynamically blocked: ip={}", ip);
            }
            return true;
        }

        return false;
    }

    /**
     * Check if the request is allowed under rate limiting rules.
     * @param ip the client IP address
     * @return true if the request is allowed
     */
    public boolean allowRequest(final String ip) {
        if (isWhitelisted(ip)) {
            return true;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int maxRequests = fessConfig.getRateLimitRequestsPerWindowAsInteger();

        AtomicLong counter = requestCounters.getIfPresent(ip);
        if (counter == null) {
            counter = new AtomicLong(0);
            requestCounters.put(ip, counter);
        }

        final long count = counter.incrementAndGet();

        if (logger.isDebugEnabled()) {
            logger.debug("Request count: ip={}, count={}, max={}", ip, count, maxRequests);
        }

        if (count > maxRequests) {
            blockedIps.put(ip, Boolean.TRUE);
            logger.info("Rate limit exceeded, IP blocked: ip={}, requestCount={}", ip, count);
            return false;
        }

        return true;
    }

    /**
     * Get the Retry-After header value in seconds.
     * @return the retry after seconds
     */
    public int getRetryAfterSeconds() {
        return ComponentUtil.getFessConfig().getRateLimitRetryAfterSecondsAsInteger();
    }

    /**
     * Block an IP address for the specified duration.
     * Note: The duration is ignored as the cache uses the configured block duration.
     * @param ip the IP address to block
     * @param durationMs the duration in milliseconds (ignored, uses configured value)
     */
    public void blockIp(final String ip, final long durationMs) {
        blockedIps.put(ip, Boolean.TRUE);
        logger.info("IP manually blocked: ip={}", ip);
    }

    /**
     * Unblock an IP address.
     * @param ip the IP address to unblock
     */
    public void unblockIp(final String ip) {
        blockedIps.invalidate(ip);
        logger.info("IP unblocked: ip={}", ip);
    }

    /**
     * Get the number of currently blocked IPs.
     * Note: This may include expired entries not yet evicted.
     * @return the count of blocked IPs
     */
    public int getBlockedIpCount() {
        blockedIps.cleanUp();
        return (int) blockedIps.size();
    }

    /**
     * Get the number of tracked IP counters.
     * Note: This may include expired entries not yet evicted.
     * @return the count of tracked IPs
     */
    public int getTrackedIpCount() {
        requestCounters.cleanUp();
        return (int) requestCounters.size();
    }

    /**
     * Clean up expired entries.
     * Note: Guava Cache handles expiration automatically, but this method
     * can be called to force immediate cleanup.
     */
    public void cleanup() {
        requestCounters.cleanUp();
        blockedIps.cleanUp();
        if (logger.isDebugEnabled()) {
            logger.debug("Cache cleanup completed: trackedIps={}, blockedIps={}", requestCounters.size(), blockedIps.size());
        }
    }
}
