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
package org.codelibs.fess.crypto.password;

import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crypto.bcrypt.BCrypt;
import org.codelibs.fess.util.ComponentUtil;

/**
 * BCrypt encoder backed by the vendored jBCrypt implementation. Registered as the DI component
 * {@code bcryptPasswordEncoder}; the cost is {@code app.password.bcrypt.cost}.
 */
public class BcryptPasswordEncoder implements PasswordEncoder {

    private static final Logger logger = LogManager.getLogger(BcryptPasswordEncoder.class);

    /**
     * The number of bytes of a password that BCrypt takes into account. Anything after this many UTF-8 bytes
     * does not change the hash, so {@link #encode(String)} refuses a longer password.
     */
    public static final int MAX_PASSWORD_BYTES = 72;

    /**
     * Plaintext seed used to generate the dummy hash consumed by {@link #applyTimingPadding()}. The value is not
     * secret and is not user-supplied; it exists only to produce a well-formed hash for timing-attack equalisation.
     */
    protected static final String DUMMY_SEED = "__fess_timing_guard__";

    /** The dummy hash (without any id prefix) used by {@link #applyTimingPadding()}, built at {@link #dummyCost}. */
    protected volatile String dummyHash;

    /** The cost {@link #dummyHash} was built at, so that a changed {@code app.password.bcrypt.cost} rebuilds it. */
    protected volatile int dummyCost;

    /**
     * Default constructor.
     */
    public BcryptPasswordEncoder() {
        // no-op
    }

    @Override
    public int getMaxPasswordBytes() {
        return MAX_PASSWORD_BYTES;
    }

    @Override
    public boolean isVerificationCostly() {
        return true;
    }

    @Override
    public String encode(final String rawPassword) {
        final int length = rawPassword.getBytes(StandardCharsets.UTF_8).length;
        if (length > MAX_PASSWORD_BYTES) {
            // BCrypt would silently ignore the end. The message carries no part of the password.
            throw new IllegalArgumentException(
                    "The password is " + length + " bytes long, but BCrypt accepts at most " + MAX_PASSWORD_BYTES + " bytes.");
        }
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(resolveCost()));
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

    @Override
    public boolean needsUpgrade(final String encodedPassword) {
        final int currentCost = parseCost(encodedPassword);
        final Integer targetCost = ComponentUtil.getFessConfig().getAppPasswordBcryptCostAsInteger();
        return currentCost >= 0 && targetCost != null && currentCost < targetCost.intValue();
    }

    @Override
    public void applyTimingPadding() {
        try {
            BCrypt.checkpw(DUMMY_SEED, getDummyHash());
        } catch (final RuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("timing-padding verification failed", e);
            }
        }
    }

    /**
     * Returns the dummy hash used by {@link #applyTimingPadding()}, generating it on first use and again when the
     * configured cost has changed, so that the dummy path costs the same as real user records.
     *
     * @return a well-formed BCrypt hash that never matches a real user-supplied plaintext
     */
    protected String getDummyHash() {
        final int cost = resolveCost();
        String v = dummyHash;
        if (v == null || dummyCost != cost) {
            synchronized (this) {
                v = dummyHash;
                if (v == null || dummyCost != cost) {
                    v = BCrypt.hashpw(DUMMY_SEED, BCrypt.gensalt(cost));
                    dummyCost = cost;
                    dummyHash = v;
                }
            }
        }
        return v;
    }

    /**
     * Resolves the effective BCrypt cost, clamped into the jBCrypt-valid range [4, 30].
     *
     * @return the effective BCrypt cost in the range [4, 30]
     */
    protected int resolveCost() {
        final Integer cost = ComponentUtil.getFessConfig().getAppPasswordBcryptCostAsInteger();
        if (cost == null) {
            return 10;
        }
        final int c = cost;
        if (c < 4) {
            return 4;
        }
        if (c > 30) {
            return 30;
        }
        return c;
    }

    /**
     * Parses the cost (log rounds) component of a BCrypt hash (<code>$2a$NN$...</code>).
     *
     * @param bcryptHash the BCrypt hash string (without any <code>{id}</code> prefix)
     * @return the cost value, or {@code -1} if parsing fails
     */
    protected int parseCost(final String bcryptHash) {
        if (bcryptHash == null || bcryptHash.length() < 7 || bcryptHash.charAt(0) != '$') {
            return -1;
        }
        final int secondDollar = bcryptHash.indexOf('$', 1);
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
}
