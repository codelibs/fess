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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crypto.bcrypt.BCrypt;
import org.codelibs.fess.mylasta.direction.FessConfig;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * Facade for password hashing and verification.
 * <p>
 * Implements a prefix-based (<code>{id}hash</code>) delegating scheme that is
 * functionally equivalent to Spring Security's
 * {@code PasswordEncoderFactories.createDelegatingPasswordEncoder()} (v5.8
 * defaults), without pulling in any external dependency. The default encoder
 * is BCrypt (cost 10, <code>$2a$</code>) and legacy pre-prefix hashes are
 * still verifiable via {@code app.digest.algorithm} (sha256/sha512/md5 hex
 * lower-case, no salt).
 */
public class PasswordHelper {

    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(PasswordHelper.class);

    /** Prefix marker (start) of the encoder id. */
    protected static final String PREFIX = "{";

    /** Prefix marker (end) of the encoder id. */
    protected static final String SUFFIX = "}";

    /** Encoder id for BCrypt. */
    protected static final String ID_BCRYPT = "bcrypt";

    /** Full id prefix token for BCrypt (<code>{bcrypt}</code>). */
    public static final String BCRYPT_PREFIX = PREFIX + ID_BCRYPT + SUFFIX;

    /**
     * Plaintext seed used to generate the dummy BCrypt hash consumed by
     * {@link #applyTimingPadding()}. The value is not secret and is not
     * user-supplied; it exists only to produce a well-formed hash for
     * timing-attack equalisation.
     */
    protected static final String DUMMY_BCRYPT_SEED = "__fess_timing_guard__";

    /** Injected Fess configuration. */
    @Resource
    protected FessConfig fessConfig;

    /** Registered encoders keyed by id. Populated lazily and read-only after init. */
    protected volatile Map<String, PasswordEncoder> encoders;

    /**
     * Cached dummy BCrypt hash used by {@link #applyTimingPadding()} as a
     * timing-attack countermeasure. Built at container start-up (see
     * {@link #init()}) using the currently configured
     * {@code app.password.bcrypt.cost}, so that the dummy path uses the same
     * BCrypt cost as real user records. Stored in a {@code volatile} field
     * with double-checked locking so a runtime cost bump (via config reload)
     * can still trigger a one-time regeneration without synchronisation on
     * the hot path.
     */
    protected volatile String dummyBcryptHash;

    /**
     * Default constructor.
     */
    public PasswordHelper() {
        // no-op
    }

    /**
     * Validates the password configuration eagerly at container start-up so
     * that a misconfigured {@code app.password.algorithm} fails fast instead of
     * surfacing on the first user login. Also forces the encoder map to build
     * so its construction cost is not paid on the login critical path.
     */
    @PostConstruct
    public void init() {
        // resolveIdForEncode() internally validates against getEncoders(),
        // which throws IllegalStateException on unknown algorithms.
        resolveIdForEncode();
        // Eagerly build the dummy BCrypt hash so that (a) any misconfiguration
        // of the BCrypt cost surfaces at start-up (fail-fast), and (b) the
        // first timing-padding call on the login hot path does not pay the
        // cost of generating it.
        getDummyBcryptHash();
    }

    /**
     * Consumes approximately one BCrypt verification worth of CPU to equalise
     * authentication latency across success and failure paths. Callers should
     * invoke this on the failure branch when the normal {@link #matches}
     * invocation did not already pay a BCrypt cost (for example: user not
     * found, legacy hex-digest stored value, unknown-id prefix).
     *
     * <p>This method never throws: if the dummy hash cannot be produced for
     * any reason, it returns silently rather than leaking a distinguishable
     * exception to the caller.</p>
     */
    public void applyTimingPadding() {
        final String dummy = getDummyBcryptHash();
        if (dummy == null) {
            return;
        }
        try {
            BCrypt.checkpw(DUMMY_BCRYPT_SEED, dummy.substring(BCRYPT_PREFIX.length()));
        } catch (final RuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("timing-padding verification failed", e);
            }
        }
    }

    /**
     * Indicates whether the supplied stored hash is in a format whose
     * verification via {@link #matches(String, String)} already pays a BCrypt
     * cost (or equivalent), meaning the caller does <em>not</em> need to add
     * {@link #applyTimingPadding()} on the failure branch.
     *
     * @param storedPassword the stored (hashed) password value
     * @return {@code true} if the id prefix denotes a timing-safe encoder
     *         (currently only {@code {bcrypt}}); {@code false} for legacy
     *         unprefixed values and unknown prefixes
     */
    public boolean isTimingSafeHash(final String storedPassword) {
        if (storedPassword == null || storedPassword.isEmpty()) {
            return false;
        }
        return ID_BCRYPT.equals(extractId(storedPassword));
    }

    /**
     * Returns the dummy BCrypt hash used by {@link #applyTimingPadding()},
     * generating it lazily on first access using the configured BCrypt cost.
     * Uses double-checked locking so subsequent calls are lock-free.
     *
     * @return a valid {@code {bcrypt}$2a$...} string that will never match any
     *         real user-supplied plaintext, or {@code null} if generation
     *         failed (logged only)
     */
    protected String getDummyBcryptHash() {
        String v = dummyBcryptHash;
        if (v == null) {
            synchronized (this) {
                v = dummyBcryptHash;
                if (v == null) {
                    try {
                        final int cost = resolveBcryptCost();
                        v = BCRYPT_PREFIX + BCrypt.hashpw(DUMMY_BCRYPT_SEED, BCrypt.gensalt(cost));
                        dummyBcryptHash = v;
                    } catch (final RuntimeException e) {
                        logger.warn("Failed to build dummy BCrypt hash for timing padding", e);
                        return null;
                    }
                }
            }
        }
        return v;
    }

    /**
     * Resolves the effective BCrypt cost, clamped into the jBCrypt-valid
     * range [4, 30]. Extracted from {@link BcryptPasswordEncoder} so the
     * dummy-hash path uses the exact same value as real user records.
     *
     * @return the effective BCrypt cost in the range [4, 30]
     */
    protected int resolveBcryptCost() {
        final Integer cost = fessConfig.getAppPasswordBcryptCostAsInteger();
        if (cost == null) {
            return 10;
        }
        final int c = cost.intValue();
        if (c < 4) {
            return 4;
        }
        if (c > 30) {
            return 30;
        }
        return c;
    }

    /**
     * Encodes the raw password using the configured default algorithm.
     *
     * @param rawPassword the plain-text password (must not be {@code null})
     * @return the encoded password prefixed with <code>{id}</code>
     * @throws NullPointerException if {@code rawPassword} is null
     * @throws IllegalStateException if the configured algorithm is unknown
     */
    public String encode(final String rawPassword) {
        if (rawPassword == null) {
            throw new NullPointerException("rawPassword must not be null");
        }
        final String idForEncode = resolveIdForEncode();
        final PasswordEncoder encoder = getEncoders().get(idForEncode);
        if (encoder == null) {
            throw new IllegalStateException("Unknown password algorithm: " + idForEncode);
        }
        return PREFIX + idForEncode + SUFFIX + encoder.encode(rawPassword);
    }

    /**
     * Verifies that the raw password matches the stored representation.
     * Falls back to legacy hex digest verification when the stored value
     * carries no <code>{id}</code> prefix.
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
        final PasswordEncoder encoder = getEncoders().get(id);
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
        if (!fessConfig.isAppPasswordUpgradeEnabled()) {
            return false;
        }
        if (storedPassword == null || storedPassword.isEmpty()) {
            return false;
        }
        final String idForEncode = resolveIdForEncode();
        final String id = extractId(storedPassword);
        if (id == null) {
            // Legacy hashes should always be upgraded.
            return true;
        }
        if (!getEncoders().containsKey(id)) {
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
        if (ID_BCRYPT.equals(id)) {
            final int currentCost = parseBcryptCost(storedPassword.substring(id.length() + 2));
            final Integer targetCost = fessConfig.getAppPasswordBcryptCostAsInteger();
            if (currentCost >= 0 && targetCost != null && currentCost < targetCost.intValue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Resolves the encoder id used for new passwords.
     *
     * @return the configured encoder id (lower-cased)
     */
    protected String resolveIdForEncode() {
        final String configured = fessConfig.getAppPasswordAlgorithm();
        if (StringUtil.isBlank(configured)) {
            throw new IllegalStateException("app.password.algorithm is not configured");
        }
        final String id = configured.toLowerCase(Locale.ROOT);
        if (!getEncoders().containsKey(id)) {
            throw new IllegalStateException("Unsupported password algorithm: " + id);
        }
        return id;
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
     * Parses the cost (log rounds) component of a BCrypt hash
     * (<code>$2a$NN$...</code>).
     *
     * @param bcryptHash the BCrypt hash string (without any <code>{id}</code> prefix)
     * @return the cost value, or {@code -1} if parsing fails
     */
    protected int parseBcryptCost(final String bcryptHash) {
        if (bcryptHash == null || bcryptHash.length() < 7 || bcryptHash.charAt(0) != '$') {
            return -1;
        }
        final int firstDollar = 0;
        final int secondDollar = bcryptHash.indexOf('$', firstDollar + 1);
        if (secondDollar < 0) {
            return -1;
        }
        final int thirdDollar = bcryptHash.indexOf('$', secondDollar + 1);
        if (thirdDollar < 0) {
            return -1;
        }
        try {
            return Integer.parseInt(bcryptHash.substring(secondDollar + 1, thirdDollar));
        } catch (final NumberFormatException e) {
            return -1;
        }
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
        final String algorithm = fessConfig.getAppDigestAlgorithm();
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
        switch (lower) {
        case "sha256":
        case "sha-256":
            return "SHA-256";
        case "sha512":
        case "sha-512":
            return "SHA-512";
        case "md5":
            return "MD5";
        default:
            return null;
        }
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

    /**
     * Returns the encoder map, initializing it lazily on first access.
     *
     * @return an immutable map of encoder id to encoder
     */
    protected Map<String, PasswordEncoder> getEncoders() {
        Map<String, PasswordEncoder> local = encoders;
        if (local == null) {
            synchronized (this) {
                local = encoders;
                if (local == null) {
                    final Map<String, PasswordEncoder> map = new LinkedHashMap<>();
                    map.put(ID_BCRYPT, new BcryptPasswordEncoder());
                    local = Collections.unmodifiableMap(map);
                    encoders = local;
                }
            }
        }
        return local;
    }

    /**
     * Internal encoder abstraction. Kept package-private/inner to avoid
     * leaking public API surface.
     */
    protected interface PasswordEncoder {
        /**
         * Encodes the raw password.
         *
         * @param rawPassword the plain-text password
         * @return the encoded hash (without any <code>{id}</code> prefix)
         */
        String encode(String rawPassword);

        /**
         * Verifies the raw password against the encoded hash.
         *
         * @param rawPassword the plain-text password
         * @param encodedPassword the encoded hash (without <code>{id}</code> prefix)
         * @return {@code true} if matched
         */
        boolean matches(String rawPassword, String encodedPassword);
    }

    /**
     * BCrypt encoder backed by the vendored jBCrypt implementation.
     */
    protected class BcryptPasswordEncoder implements PasswordEncoder {

        /**
         * Default constructor.
         */
        protected BcryptPasswordEncoder() {
            // no-op
        }

        @Override
        public String encode(final String rawPassword) {
            return BCrypt.hashpw(rawPassword, BCrypt.gensalt(resolveBcryptCost()));
        }

        @Override
        public boolean matches(final String rawPassword, final String encodedPassword) {
            if (encodedPassword == null || encodedPassword.isEmpty()) {
                return false;
            }
            try {
                return BCrypt.checkpw(rawPassword, encodedPassword);
            } catch (final IllegalArgumentException e) {
                // Malformed stored hash.
                if (logger.isDebugEnabled()) {
                    logger.debug("malformed bcrypt hash", e);
                }
                return false;
            }
        }

    }
}
