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
package org.codelibs.fess.setup;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sets the two values of {@code fess_config.properties} that an installation should not keep at
 * the shipped defaults: the initial password of the {@code admin} user and the cipher key.
 *
 * <ul>
 * <li>{@code index.user.initial_password} is read when Fess creates the user index, which is its
 * first start. Setting it later changes nothing.</li>
 * <li>{@code app.cipher.key} encrypts the passwords kept in the crawl and data store
 * configurations. A value saved under one key cannot be read under another, so the key is
 * replaced only while it still has the shipped value, unless the caller forces it.</li>
 * </ul>
 */
public final class ConfigInitializer {

    /** The key of the initial password of the {@code admin} user. */
    static final String PASSWORD_KEY = "index.user.initial_password";

    /** The key of the cipher key. */
    static final String CIPHER_KEY = "app.cipher.key";

    /** The key of the cipher algorithm. */
    private static final String ALGORITHM_KEY = "app.cipher.algorithm";

    /** The cipher key shipped in {@code fess_config.properties}. */
    static final String DEFAULT_CIPHER_KEY = "___change__me___";

    /** The initial password shipped in {@code fess_config.properties}. */
    static final String DEFAULT_PASSWORD = "admin";

    /** The length of a generated cipher key. */
    private static final int GENERATED_KEY_LENGTH = 32;

    /** The length of a generated admin password. */
    private static final int GENERATED_PASSWORD_LENGTH = 16;

    private static final String KEY_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private ConfigInitializer() {
    }

    /**
     * Returns where a Fess distribution keeps {@code fess_config.properties}.
     *
     * @param fessHome the Fess home directory
     * @return the path of the file in a ZIP or tar.gz installation
     */
    public static Path defaultConfig(final Path fessHome) {
        return fessHome.resolve("app").resolve("WEB-INF").resolve("classes").resolve("fess_config.properties");
    }

    /**
     * Generates a cipher key.
     *
     * @return a random key of the length AES-256 takes
     */
    public static String generateCipherKey() {
        return randomString(GENERATED_KEY_LENGTH);
    }

    /**
     * Generates an initial password for the {@code admin} user.
     *
     * @return a random alphanumeric password
     */
    public static String generatePassword() {
        return randomString(GENERATED_PASSWORD_LENGTH);
    }

    private static String randomString(final int length) {
        final SecureRandom random = new SecureRandom();
        final StringBuilder value = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            value.append(KEY_CHARS.charAt(random.nextInt(KEY_CHARS.length())));
        }
        return value.toString();
    }

    /**
     * Writes the initial password and the cipher key to {@code fess_config.properties}, replacing
     * the line of each key or appending one when the file has none.
     *
     * @param config the {@code fess_config.properties} to change
     * @param adminPassword the initial password of the {@code admin} user
     * @param cipherKey the cipher key
     * @param force whether to replace a cipher key that is no longer the shipped value
     * @throws SetupException if a value is not acceptable, the file cannot be used, or the cipher key was already changed
     */
    public static void initialize(final Path config, final String adminPassword, final String cipherKey, final boolean force)
            throws SetupException {
        checkPassword(adminPassword);
        checkCipherKey(cipherKey);
        if (!Files.isRegularFile(config)) {
            throw new SetupException(config + " was not found. Name the file with --config.");
        }
        try {
            final List<String> lines = new ArrayList<>(Files.readAllLines(config, StandardCharsets.UTF_8));
            final String algorithm = valueOf(lines, ALGORITHM_KEY);
            if (algorithm != null && !"aes".equalsIgnoreCase(algorithm)) {
                throw new SetupException(ALGORITHM_KEY + " is " + algorithm + ". init sets a key for aes only.");
            }
            final String current = valueOf(lines, CIPHER_KEY);
            if (!force && current != null && !current.isEmpty() && !DEFAULT_CIPHER_KEY.equals(current)) {
                throw new SetupException(CIPHER_KEY + " in " + config + " has already been changed. Values Fess saved with it could not"
                        + " be read under a new key. Pass --force to replace it anyway.");
            }
            set(lines, PASSWORD_KEY, adminPassword, "Initial password of the admin user, read when Fess first starts.");
            set(lines, CIPHER_KEY, cipherKey, "Secret key for encryption.");
            Files.write(config, lines, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new SetupException("Failed to update " + config, e);
        }
    }

    /**
     * Checks a password before it goes into a properties file.
     *
     * @param password the password
     * @throws SetupException if it is blank, has surrounding whitespace or a control character, or is the shipped one
     */
    static void checkPassword(final String password) throws SetupException {
        checkPropertyValue(PASSWORD_KEY, password);
        if (DEFAULT_PASSWORD.equals(password)) {
            throw new SetupException("The password must not be the shipped " + DEFAULT_PASSWORD + ".");
        }
    }

    /**
     * Checks a cipher key. AES takes a key of 16, 24 or 32 bytes.
     *
     * @param key the cipher key
     * @throws SetupException if it is not a printable ASCII string of that length, or is the shipped one
     */
    static void checkCipherKey(final String key) throws SetupException {
        checkPropertyValue(CIPHER_KEY, key);
        if (DEFAULT_CIPHER_KEY.equals(key)) {
            throw new SetupException("The cipher key must not be the shipped " + DEFAULT_CIPHER_KEY + ".");
        }
        if (!key.chars().allMatch(c -> c >= 0x21 && c <= 0x7e)) {
            throw new SetupException(CIPHER_KEY + " must be printable ASCII without spaces.");
        }
        if (key.length() != 16 && key.length() != 24 && key.length() != 32) {
            throw new SetupException(CIPHER_KEY + " must be 16, 24 or 32 characters for aes, not " + key.length() + ".");
        }
    }

    private static void checkPropertyValue(final String name, final String value) throws SetupException {
        if (value == null || value.isEmpty()) {
            throw new SetupException(name + " must not be empty.");
        }
        if (!value.equals(value.strip())) {
            throw new SetupException(name + " must not start or end with whitespace.");
        }
        if (value.chars().anyMatch(Character::isISOControl)) {
            throw new SetupException(name + " must not contain a control character.");
        }
    }

    private static Pattern linePattern(final String key) {
        return Pattern.compile("^\\s*" + Pattern.quote(key) + "\\s*[=:]\\s*(.*?)\\s*$");
    }

    /** The value of the first active line of a key, or null when the file has none. */
    private static String valueOf(final List<String> lines, final String key) {
        final Pattern pattern = linePattern(key);
        for (final String line : lines) {
            final Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private static void set(final List<String> lines, final String key, final String value, final String comment) {
        final Pattern pattern = linePattern(key);
        final String setting = key + "=" + value.replace("\\", "\\\\");
        for (int i = 0; i < lines.size(); i++) {
            if (pattern.matcher(lines.get(i)).matches()) {
                lines.set(i, setting);
                return;
            }
        }
        lines.add("# Added by fess-setup: " + comment);
        lines.add(setting);
    }
}
