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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crypto.password.PasswordEncoder;
import org.codelibs.fess.exception.FessConfigurationException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * Facade for password hashing and verification.
 * <p>
 * Implements a prefix-based (<code>{id}hash</code>) delegating scheme that is
 * functionally equivalent to Spring Security's
 * {@code PasswordEncoderFactories.createDelegatingPasswordEncoder()} (v5.8
 * defaults), without pulling in any external dependency. The algorithms are
 * {@link PasswordEncoder} components named <code>&lt;id&gt;PasswordEncoder</code>
 * in the DI container; the default is BCrypt (<code>bcryptPasswordEncoder</code>,
 * cost 10, <code>$2a$</code>) and a plugin can add its own. Legacy pre-prefix
 * hashes are still verifiable via {@code app.digest.algorithm} (sha256/sha512/md5
 * hex lower-case, no salt).
 */
public class PasswordHashHelper {

    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(PasswordHashHelper.class);

    /** Prefix marker (start) of the encoder id. */
    protected static final String PREFIX = "{";

    /** Prefix marker (end) of the encoder id. */
    protected static final String SUFFIX = "}";

    /** Suffix of the DI component name of an encoder: the id <code>bcrypt</code> is <code>bcryptPasswordEncoder</code>. */
    protected static final String ENCODER_COMPONENT_SUFFIX = "PasswordEncoder";

    /**
     * Default constructor.
     */
    public PasswordHashHelper() {
        // no-op
    }

    /**
     * Validates {@code app.password.algorithm} and builds what the encoder keeps for timing padding.
     * Fess does not run this when it starts (the component is created on first use and fess.xml declares no
     * post-construct call for it), so a blank or unknown algorithm surfaces as a
     * {@link FessConfigurationException} at the first password hash or limit check, not at start-up.
     * Verifying a stored hash of another, resolvable id keeps working until then.
     */
    @PostConstruct
    public void init() {
        // Throws FessConfigurationException on a blank or unknown algorithm.
        final PasswordEncoder encoder = resolveEncoderForEncode();
        // Build whatever the encoder keeps for timing padding now, so that the first
        // failed login does not pay for it.
        if (encoder.isVerificationCostly()) {
            encoder.applyTimingPadding();
        }
    }

    /**
     * Consumes approximately one verification worth of CPU of the algorithm used for
     * new passwords to equalise authentication latency across success and failure
     * paths. Callers should invoke this on the failure branch when the normal
     * {@link #matches} invocation did not already pay that cost (for example: user
     * not found, legacy hex-digest stored value, unknown-id prefix). It does nothing
     * when that algorithm is not costly ({@link PasswordEncoder#isVerificationCostly()}).
     *
     * <p>This method never throws: if the padding cannot be performed for
     * any reason, it returns silently rather than leaking a distinguishable
     * exception to the caller.</p>
     */
    public void applyTimingPadding() {
        try {
            final PasswordEncoder encoder = resolveEncoderForEncode();
            if (encoder.isVerificationCostly()) {
                encoder.applyTimingPadding();
            }
        } catch (final RuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("timing-padding verification failed", e);
            }
        }
    }

    /**
     * Indicates whether the supplied stored hash is in a format whose
     * verification via {@link #matches(String, String)} already pays a significant
     * cost, meaning the caller does <em>not</em> need to add
     * {@link #applyTimingPadding()} on the failure branch.
     *
     * @param storedPassword the stored (hashed) password value
     * @return {@code true} if the id prefix denotes an encoder whose verification is
     *         costly ({@link PasswordEncoder#isVerificationCostly()}, true for BCrypt);
     *         {@code false} for legacy unprefixed values and unknown prefixes
     */
    public boolean isTimingSafeHash(final String storedPassword) {
        if (storedPassword == null || storedPassword.isEmpty()) {
            return false;
        }
        final PasswordEncoder encoder = findEncoder(extractId(storedPassword));
        return encoder != null && encoder.isVerificationCostly();
    }

    /**
     * Encodes the raw password using the configured default algorithm.
     *
     * @param rawPassword the plain-text password (must not be {@code null})
     * @return the encoded password prefixed with <code>{id}</code>
     * @throws NullPointerException if {@code rawPassword} is null
     * @throws IllegalArgumentException if the password is longer than {@link #getMaxPasswordBytes()}
     * @throws FessConfigurationException if the configured algorithm is unknown
     */
    public String encode(final String rawPassword) {
        if (rawPassword == null) {
            throw new NullPointerException("rawPassword must not be null");
        }
        final String idForEncode = resolveIdForEncode();
        return PREFIX + idForEncode + SUFFIX + resolveEncoder(idForEncode).encode(rawPassword);
    }

    /**
     * Returns the longest password, in UTF-8 bytes, that the encoder used for new passwords accepts.
     * The limit belongs to the algorithm (BCrypt reads only the first 72 bytes), unlike the
     * algorithm-independent {@code password.max.length}.
     *
     * @return the limit in bytes, or 0 or less if the encoder has none
     */
    public int getMaxPasswordBytes() {
        return resolveEncoderForEncode().getMaxPasswordBytes();
    }

    /**
     * Checks whether a password is longer than the encoder used for new passwords accepts.
     *
     * @param password the plain-text password
     * @return {@code true} if {@link #encode(String)} would refuse it; {@code false} for {@code null}
     *         or when the encoder has no limit
     */
    public boolean exceedsMaxPasswordBytes(final String password) {
        final int max = getMaxPasswordBytes();
        return max > 0 && password != null && password.getBytes(StandardCharsets.UTF_8).length > max;
    }

    /**
     * Verifies that the raw password matches the stored representation.
     * Falls back to legacy hex digest verification when the stored value
     * carries no <code>{id}</code> prefix.
     *
     * <p>The helper applies no length limit when verifying: the encoder of the stored hash decides. The built-in
     * BCrypt encoder is lenient, because a password set over its limit before 15.10 (the
     * {@code password.max.length} field allowed it, since BCrypt came in 15.6.0) was hashed from its first bytes only,
     * and its owner must keep logging in. A hash of an algorithm that reads every byte (the legacy SHA digests,
     * for one) is verified in full.</p>
     *
     * @param rawPassword the plain-text password being verified
     * @param storedPassword the stored (hashed) password
     * @return {@code true} if the password matches, {@code false} otherwise
     *         (including null/empty inputs and unknown prefixes)
     */
    public boolean matches(final String rawPassword, final String storedPassword) {
        if (rawPassword == null || storedPassword == null || storedPassword.isEmpty()) {
            return false;
        }
        final String id = extractId(storedPassword);
        if (id == null) {
            // Legacy: unprefixed hex digest governed by app.digest.algorithm.
            return matchesLegacy(rawPassword, storedPassword);
        }
        final PasswordEncoder encoder = findEncoder(id);
        if (encoder == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("unknown password prefix id={}", id);
            }
            return false;
        }
        final String encodedPayload = storedPassword.substring(id.length() + 2);
        try {
            return encoder.matches(rawPassword, encodedPayload);
        } catch (final RuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("failed to verify password id={}", id, e);
            }
            return false;
        }
    }

    /**
     * Determines whether the stored password should be re-encoded using the
     * currently configured algorithm and parameters.
     *
     * @param storedPassword the stored (hashed) password
     * @return {@code true} if re-encoding is recommended
     */
    public boolean upgradeEncoding(final String storedPassword) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (!fessConfig.isAppPasswordUpgradeEnabled() || storedPassword == null || storedPassword.isEmpty()) {
            return false;
        }
        final String idForEncode = resolveIdForEncode();
        final String id = extractId(storedPassword);
        if (id == null) {
            // Legacy hashes should always be upgraded.
            return true;
        }
        final PasswordEncoder encoder = findEncoder(id);
        if (encoder == null) {
            // Unknown prefix: matches() would have returned false, so there is
            // nothing sensible to re-encode from. Decline the upgrade
            // defensively instead of silently re-hashing on an unverified path.
            if (logger.isDebugEnabled()) {
                logger.debug("declining upgrade for unknown password prefix id={}", id);
            }
            return false;
        }
        if (!id.equals(idForEncode)) {
            return true;
        }
        return encoder.needsUpgrade(storedPassword.substring(id.length() + 2));
    }

    /**
     * Resolves the encoder id used for new passwords.
     *
     * @return the configured encoder id (lower-cased)
     */
    protected String resolveIdForEncode() {
        final String configured = ComponentUtil.getFessConfig().getAppPasswordAlgorithm();
        if (StringUtil.isBlank(configured)) {
            throw new FessConfigurationException("app.password.algorithm is not configured");
        }
        final String id = configured.toLowerCase(Locale.ROOT);
        if (findEncoder(id) == null) {
            throw new FessConfigurationException("Unsupported password algorithm: " + id + " (app.password.algorithm). No component " + id
                    + ENCODER_COMPONENT_SUFFIX + " is registered; is the plugin that supplies it installed?");
        }
        return id;
    }

    /**
     * Resolves the encoder used for new passwords.
     *
     * @return the encoder of {@code app.password.algorithm}
     * @throws FessConfigurationException if the algorithm is blank or unknown
     */
    protected PasswordEncoder resolveEncoderForEncode() {
        return resolveEncoder(resolveIdForEncode());
    }

    private PasswordEncoder resolveEncoder(final String id) {
        final PasswordEncoder encoder = findEncoder(id);
        if (encoder == null) {
            throw new FessConfigurationException("Unknown password algorithm: " + id);
        }
        return encoder;
    }

    /**
     * Looks up the encoder component <code>&lt;id&gt;PasswordEncoder</code>. The lookup is not cached:
     * the component is a container singleton, so it is a map access, and a cache would outlive a container
     * that is rebuilt (as the tests do).
     *
     * @param id the encoder id, or {@code null}
     * @return the encoder, or {@code null} if the id is blank or no such component is registered
     */
    protected PasswordEncoder findEncoder(final String id) {
        if (StringUtil.isBlank(id)) {
            return null;
        }
        final String name = id + ENCODER_COMPONENT_SUFFIX;
        if (!ComponentUtil.hasComponent(name)) {
            return null;
        }
        final Object component = ComponentUtil.getComponent(name);
        return component instanceof final PasswordEncoder encoder ? encoder : null;
    }

    /**
     * Extracts the <code>{id}</code> from a stored password value, or
     * {@code null} if it does not carry a prefix.
     *
     * @param storedPassword the stored password value (possibly prefixed)
     * @return the id inside <code>{...}</code>, or {@code null} if not prefixed
     */
    protected String extractId(final String storedPassword) {
        if (storedPassword == null || !storedPassword.startsWith(PREFIX)) {
            return null;
        }
        final int end = storedPassword.indexOf(SUFFIX, PREFIX.length());
        if (end < 0) {
            return null;
        }
        return storedPassword.substring(PREFIX.length(), end);
    }

    /**
     * Verifies a legacy (unprefixed) hex digest using
     * {@code app.digest.algorithm} with a constant-time comparison.
     *
     * @param rawPassword the plain-text password
     * @param storedPassword the stored legacy hex digest
     * @return {@code true} if matched
     */
    protected boolean matchesLegacy(final String rawPassword, final String storedPassword) {
        final String algorithm = ComponentUtil.getFessConfig().getAppDigestAlgorithm();
        if (StringUtil.isBlank(algorithm)) {
            return false;
        }
        final String jcaName = toJcaDigestName(algorithm);
        if (jcaName == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("unsupported legacy digest algorithm={}", algorithm);
            }
            return false;
        }
        final byte[] computed;
        try {
            final MessageDigest md = MessageDigest.getInstance(jcaName);
            computed = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
        } catch (final NoSuchAlgorithmException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("legacy digest algorithm not available={}", algorithm, e);
            }
            return false;
        }
        final byte[] expected = hexDecodeLowerCase(storedPassword);
        if (expected == null) {
            return false;
        }
        final boolean matched = MessageDigest.isEqual(computed, expected);
        if (matched && "md5".equals(algorithm.toLowerCase(Locale.ROOT)) && logger.isWarnEnabled()) {
            logger.warn("Insecure legacy MD5 password matched. "
                    + "This user will be re-hashed on next login if app.password.upgrade is enabled.");
        }
        return matched;
    }

    /**
     * Translates the short algorithm id (sha256/sha512/md5) recorded in
     * {@code app.digest.algorithm} into the JCA standard name.
     *
     * @param algorithm the short algorithm id
     * @return the JCA digest name, or {@code null} if unsupported
     */
    protected String toJcaDigestName(final String algorithm) {
        final String lower = algorithm.toLowerCase(Locale.ROOT);
        return switch (lower) {
        case "sha256", "sha-256" -> "SHA-256";
        case "sha512", "sha-512" -> "SHA-512";
        case "md5" -> "MD5";
        default -> null;
        };
    }

    /**
     * Decodes a lower-case hex string into bytes. Upper-case input is also
     * accepted for robustness — constant-time comparison still succeeds.
     *
     * @param hex the hex-encoded input
     * @return the decoded bytes, or {@code null} if the input is not a valid even-length hex string
     */
    protected byte[] hexDecodeLowerCase(final String hex) {
        if (hex == null) {
            return null;
        }
        final int len = hex.length();
        if ((len & 1) != 0) {
            return null;
        }
        final byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            final int hi = Character.digit(hex.charAt(i), 16);
            final int lo = Character.digit(hex.charAt(i + 1), 16);
            if (hi < 0 || lo < 0) {
                return null;
            }
            out[i / 2] = (byte) ((hi << 4) | lo);
        }
        return out;
    }
}
