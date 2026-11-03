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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.lastaflute.core.security.InvertibleCryptographer;

public class ConfigInitializerTest {

    private static final String SHIPPED = "# Cipher algorithm used for encryption.\napp.cipher.algorithm=aes\n"
            + "# Secret key for encryption (change this value for production).\napp.cipher.key=___change__me___\n"
            + "index.user.initial_password=admin\nother.key=keep\n";

    private static final String KEY = "0123456789abcdef0123456789abcdef";

    @TempDir
    Path tempDir;

    private Path config(final String content) throws Exception {
        final Path file = tempDir.resolve("fess_config.properties");
        Files.writeString(file, content);
        return file;
    }

    @Test
    public void test_replacesTheShippedValues() throws Exception {
        final Path file = config(SHIPPED);
        ConfigInitializer.initialize(file, "s3cret-pass", KEY, false);
        final String text = Files.readString(file);
        assertTrue(text.contains("index.user.initial_password=s3cret-pass\n"));
        assertTrue(text.contains("app.cipher.key=" + KEY + "\n"));
        assertFalse(text.contains("___change__me___"));
        assertTrue(text.contains("other.key=keep\n"));
        assertTrue(text.contains("# Secret key for encryption (change this value for production).\n"));
    }

    @Test
    public void test_appendsAKeyTheFileLacks() throws Exception {
        final Path file = config("app.cipher.algorithm=aes\n");
        ConfigInitializer.initialize(file, "s3cret-pass", KEY, false);
        final String text = Files.readString(file);
        assertTrue(text.contains("index.user.initial_password=s3cret-pass\n"));
        assertTrue(text.contains("app.cipher.key=" + KEY + "\n"));
    }

    @Test
    public void test_aCommentedKeyIsNotTheSetting() throws Exception {
        final Path file = config("#app.cipher.key=old\napp.cipher.key=___change__me___\n");
        ConfigInitializer.initialize(file, "s3cret-pass", KEY, false);
        final String text = Files.readString(file);
        assertTrue(text.contains("#app.cipher.key=old\n"));
        assertEquals(1, text.lines().filter(line -> line.startsWith("app.cipher.key=")).count());
    }

    @Test
    public void test_aBackslashIsEscaped() throws Exception {
        final Path file = config(SHIPPED);
        ConfigInitializer.initialize(file, "pa\\ss-word", KEY, false);
        assertTrue(Files.readString(file).contains("index.user.initial_password=pa\\\\ss-word\n"));
        final java.util.Properties properties = new java.util.Properties();
        try (var reader = Files.newBufferedReader(file)) {
            properties.load(reader);
        }
        assertEquals("pa\\ss-word", properties.getProperty("index.user.initial_password"));
    }

    @Test
    public void test_aChangedCipherKeyIsKeptWithoutForce() throws Exception {
        final Path file = config(SHIPPED.replace("___change__me___", "AlreadyChanged1234"));
        final SetupException e = assertThrows(SetupException.class, () -> ConfigInitializer.initialize(file, "s3cret-pass", KEY, false));
        assertTrue(e.getMessage().contains("--force"));
        assertTrue(Files.readString(file).contains("index.user.initial_password=admin\n"));

        ConfigInitializer.initialize(file, "s3cret-pass", KEY, true);
        assertTrue(Files.readString(file).contains("app.cipher.key=" + KEY + "\n"));
    }

    @Test
    public void test_onlyAesIsSupported() throws Exception {
        final Path file = config(SHIPPED.replace("=aes", "=blowfish"));
        assertThrows(SetupException.class, () -> ConfigInitializer.initialize(file, "s3cret-pass", KEY, false));
    }

    @Test
    public void test_refusesValuesThatWouldNotWork() throws Exception {
        final Path file = config(SHIPPED);
        assertThrows(SetupException.class, () -> ConfigInitializer.initialize(file, "admin", KEY, false));
        assertThrows(SetupException.class, () -> ConfigInitializer.initialize(file, "", KEY, false));
        assertThrows(SetupException.class, () -> ConfigInitializer.initialize(file, " padded ", KEY, false));
        assertThrows(SetupException.class, () -> ConfigInitializer.initialize(file, "two\nlines", KEY, false));
        assertThrows(SetupException.class, () -> ConfigInitializer.initialize(file, "s3cret-pass", "short", false));
        assertThrows(SetupException.class, () -> ConfigInitializer.initialize(file, "s3cret-pass", "___change__me___", false));
        assertThrows(SetupException.class, () -> ConfigInitializer.initialize(file, "s3cret-pass", "has space 0123456789abcdef", false));
        assertEquals(SHIPPED, Files.readString(file));
    }

    @Test
    public void test_aMissingFileIsReported() {
        final SetupException e = assertThrows(SetupException.class,
                () -> ConfigInitializer.initialize(tempDir.resolve("none.properties"), "s3cret-pass", KEY, false));
        assertTrue(e.getMessage().contains("--config"));
    }

    @Test
    public void test_aGeneratedKeyIsUsable() {
        final String first = ConfigInitializer.generateCipherKey();
        assertNotEquals(first, ConfigInitializer.generateCipherKey());
        assertEquals(32, first.length());
        // The key goes to the cipher Fess builds from app.cipher.key.
        final InvertibleCryptographer cipher = InvertibleCryptographer.createAesCipher(first);
        assertEquals("secret", cipher.decrypt(cipher.encrypt("secret")));
    }

    @Test
    public void test_defaultConfigIsUnderTheWebapp() {
        assertEquals(Path.of("/opt/fess/app/WEB-INF/classes/fess_config.properties"),
                ConfigInitializer.defaultConfig(Path.of("/opt/fess")));
    }

    @Test
    public void test_initCommand() throws Exception {
        final Path file = config(SHIPPED);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ByteArrayOutputStream err = new ByteArrayOutputStream();
        final PrintStream o = new PrintStream(out, true, StandardCharsets.UTF_8);
        final PrintStream e = new PrintStream(err, true, StandardCharsets.UTF_8);

        assertEquals(0, FessSetup.run(new String[] { "init", "--admin-password", "s3cret-pass", "--config", file.toString() }, o, e));
        final String text = Files.readString(file);
        assertTrue(text.contains("index.user.initial_password=s3cret-pass\n"));
        assertFalse(text.contains("___change__me___"));
        assertTrue(out.toString(StandardCharsets.UTF_8).contains("(generated)"));
        assertFalse(out.toString(StandardCharsets.UTF_8).contains("s3cret-pass"));
        assertFalse(out.toString(StandardCharsets.UTF_8)
                .contains(text.lines().filter(l -> l.startsWith("app.cipher.key=")).findFirst().get().substring(15)));

        // A second run would replace a key that is no longer the shipped one.
        assertEquals(1, FessSetup.run(new String[] { "init", "--admin-password", "other-pass1", "--config", file.toString() }, o, e));
        assertTrue(Files.readString(file).contains("index.user.initial_password=s3cret-pass\n"));
    }

    @Test
    public void test_initCommandUsageErrors() throws Exception {
        final Path file = config(SHIPPED);
        final PrintStream o = new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8);
        final PrintStream e = new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8);
        // A flag with no value must not become the password.
        assertEquals(2, FessSetup.run(new String[] { "init", "--config", file.toString(), "--admin-password" }, o, e));
        assertEquals(2, FessSetup.run(new String[] { "init", "--unknown" }, o, e));
        assertEquals(SHIPPED, Files.readString(file));
    }
}
