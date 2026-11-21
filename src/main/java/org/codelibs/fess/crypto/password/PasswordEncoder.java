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

/**
 * A password hashing algorithm that {@link org.codelibs.fess.helper.PasswordHashHelper} can delegate to.
 * <p>
 * A stored password is written as <code>{id}hash</code>. The helper takes the <code>id</code> (in a stored value,
 * and the value of {@code app.password.algorithm} for new passwords) and looks up the DI component named
 * <code>&lt;id&gt;PasswordEncoder</code>, the same way {@code <sso.type>Authenticator} and
 * {@code <storage.type>StorageClient} are resolved. The built-in algorithm is registered as
 * {@code bcryptPasswordEncoder}.
 * </p>
 * <p>
 * To add an algorithm, implement this interface and register it from a plugin with an additive DI file such as
 * {@code fess++.xml}:
 * </p>
 *
 * <pre>
 * &lt;component name="argon2PasswordEncoder" class="com.example.Argon2PasswordEncoder"/&gt;
 * </pre>
 * <p>
 * The plugin jar goes to the plugin directory (<code>WEB-INF/plugin</code>) and must carry the manifest attribute
 * {@code Fess-WebAppJar: true}; without it the jar is not put on the web application's class path and the encoder is
 * not found.
 * </p>
 * <p>
 * Then set {@code app.password.algorithm=argon2} to make it the algorithm for new passwords. The id must be lower
 * case and must not contain <code>}</code>, because the configured value is lower-cased and the stored prefix ends at
 * the first <code>}</code>. An implementation is a singleton shared by all threads, so it must be thread-safe, and
 * it must stay installed as long as a password written with its id is stored: without it those users cannot log in
 * (the helper does not accept a hash whose id it cannot resolve).
 * </p>
 */
public interface PasswordEncoder {

    /**
     * Hashes a password. The result is stored after the <code>{id}</code> prefix, which the helper adds.
     *
     * @param rawPassword the plain-text password (never {@code null})
     * @return the encoded hash, without the <code>{id}</code> prefix
     * @throws IllegalArgumentException if the password cannot be hashed faithfully, for example when it is longer
     *         than {@link #getMaxPasswordBytes()} (the message must not contain the password)
     */
    String encode(String rawPassword);

    /**
     * Verifies a password against a hash made by this encoder. Whether a password longer than
     * {@link #getMaxPasswordBytes()} may verify is the business of the algorithm: {@link #encode(String)} refuses
     * such a password, but a hash may already exist that was made from one before the limit was enforced. The
     * built-in BCrypt encoder stays lenient for those hashes (it compares the first 72 bytes, as the hash was
     * made), so that their owners keep logging in; an algorithm that reads every byte verifies every byte.
     *
     * @param rawPassword the plain-text password (never {@code null})
     * @param encodedPassword the stored hash, without the <code>{id}</code> prefix
     * @return {@code true} if matched; {@code false} if not or if the stored hash is malformed
     */
    boolean matches(String rawPassword, String encodedPassword);

    /**
     * Tells whether a stored hash is weaker than what {@link #encode(String)} would produce now, so that it should
     * be re-encoded when its owner logs in. It is consulted only when the hash carries this encoder's id; a hash
     * of another id is always re-encoded by the helper.
     *
     * @param encodedPassword the stored hash, without the <code>{id}</code> prefix
     * @return {@code true} if the hash should be re-encoded; the default is {@code false}
     */
    default boolean needsUpgrade(final String encodedPassword) {
        return false;
    }

    /**
     * Returns the longest password, in UTF-8 bytes, that {@link #encode(String)} accepts.
     *
     * @return the limit in bytes, or 0 or less if there is none (the default)
     */
    default int getMaxPasswordBytes() {
        return 0;
    }

    /**
     * Tells whether {@link #matches(String, String)} takes a deliberately large amount of time, so that the helper
     * does not need to add {@link #applyTimingPadding()} after a failed login against a hash of this algorithm.
     *
     * @return {@code true} for a slow, work-factor based algorithm; the default is {@code false}
     */
    default boolean isVerificationCostly() {
        return false;
    }

    /**
     * Spends about as much time as one {@link #matches(String, String)} of this algorithm, to hide whether a
     * login failed for an unknown user or a wrong password. It is called only for the algorithm of new passwords
     * when {@link #isVerificationCostly()} is {@code true}, and must not throw.
     */
    default void applyTimingPadding() {
        // nothing to pad for a cheap algorithm
    }
}
