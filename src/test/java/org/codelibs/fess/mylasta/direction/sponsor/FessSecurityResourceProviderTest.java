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
package org.codelibs.fess.mylasta.direction.sponsor;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.lastaflute.core.security.InvertibleCryptographer;
import org.lastaflute.core.security.OneWayCryptographer;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessSecurityResourceProviderTest extends UnitFessTestCase {

    private FessSecurityResourceProvider securityResourceProvider;
    private InvertibleCryptographer invertibleCryptographer;
    private OneWayCryptographer oneWayCryptographer;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        // Create InvertibleCryptographer with AES
        invertibleCryptographer = InvertibleCryptographer.createAesCipher("1234567890123456");

        // Create OneWayCryptographer with SHA256
        oneWayCryptographer = OneWayCryptographer.createSha256Cryptographer();

        securityResourceProvider = new FessSecurityResourceProvider(invertibleCryptographer, oneWayCryptographer);
    }

    // Test constructor
    @Test
    public void test_constructor_withValidParameters() {
        // Test normal construction
        assertNotNull(securityResourceProvider);

        // Verify fields are set correctly
        InvertibleCryptographer providedInvertible = securityResourceProvider.providePrimaryInvertibleCryptographer();
        OneWayCryptographer providedOneWay = securityResourceProvider.providePrimaryOneWayCryptographer();

        assertNotNull(providedInvertible);
        assertNotNull(providedOneWay);
        assertEquals(invertibleCryptographer, providedInvertible);
        assertEquals(oneWayCryptographer, providedOneWay);
    }

    @Test
    public void test_constructor_withNullInvertibleCryptographer() {
        // Test with null InvertibleCryptographer
        FessSecurityResourceProvider provider = new FessSecurityResourceProvider(null, oneWayCryptographer);
        assertNotNull(provider);
        assertNull(provider.providePrimaryInvertibleCryptographer());
        assertNotNull(provider.providePrimaryOneWayCryptographer());
        assertEquals(oneWayCryptographer, provider.providePrimaryOneWayCryptographer());
    }

    @Test
    public void test_constructor_withNullOneWayCryptographer() {
        // Test with null OneWayCryptographer
        FessSecurityResourceProvider provider = new FessSecurityResourceProvider(invertibleCryptographer, null);
        assertNotNull(provider);
        assertNotNull(provider.providePrimaryInvertibleCryptographer());
        assertEquals(invertibleCryptographer, provider.providePrimaryInvertibleCryptographer());
        assertNull(provider.providePrimaryOneWayCryptographer());
    }

    @Test
    public void test_constructor_withBothNull() {
        // Test with both cryptographers null
        FessSecurityResourceProvider provider = new FessSecurityResourceProvider(null, null);
        assertNotNull(provider);
        assertNull(provider.providePrimaryInvertibleCryptographer());
        assertNull(provider.providePrimaryOneWayCryptographer());
    }

    // Test providePrimaryInvertibleCryptographer
    @Test
    public void test_providePrimaryInvertibleCryptographer() {
        // Test that the method returns the same instance
        InvertibleCryptographer first = securityResourceProvider.providePrimaryInvertibleCryptographer();
        InvertibleCryptographer second = securityResourceProvider.providePrimaryInvertibleCryptographer();

        assertNotNull(first);
        assertNotNull(second);
        assertSame(first, second);
        assertSame(invertibleCryptographer, first);
    }

    @Test
    public void test_providePrimaryInvertibleCryptographer_consistency() {
        // Test multiple calls return consistent results
        for (int i = 0; i < 10; i++) {
            InvertibleCryptographer cryptographer = securityResourceProvider.providePrimaryInvertibleCryptographer();
            assertNotNull(cryptographer);
            assertSame(invertibleCryptographer, cryptographer);
        }
    }

    // Test providePrimaryOneWayCryptographer
    @Test
    public void test_providePrimaryOneWayCryptographer() {
        // Test that the method returns the same instance
        OneWayCryptographer first = securityResourceProvider.providePrimaryOneWayCryptographer();
        OneWayCryptographer second = securityResourceProvider.providePrimaryOneWayCryptographer();

        assertNotNull(first);
        assertNotNull(second);
        assertSame(first, second);
        assertSame(oneWayCryptographer, first);
    }

    @Test
    public void test_providePrimaryOneWayCryptographer_consistency() {
        // Test multiple calls return consistent results
        for (int i = 0; i < 10; i++) {
            OneWayCryptographer cryptographer = securityResourceProvider.providePrimaryOneWayCryptographer();
            assertNotNull(cryptographer);
            assertSame(oneWayCryptographer, cryptographer);
        }
    }

    // Test with different cipher types
    @Test
    public void test_withBlowfishCipher() {
        // Test with Blowfish cipher
        InvertibleCryptographer blowfish = InvertibleCryptographer.createBlowfishCipher("secretkey");
        FessSecurityResourceProvider provider = new FessSecurityResourceProvider(blowfish, oneWayCryptographer);

        assertNotNull(provider);
        assertEquals(blowfish, provider.providePrimaryInvertibleCryptographer());
        assertEquals(oneWayCryptographer, provider.providePrimaryOneWayCryptographer());
    }

    @Test
    public void test_withDesCipher() {
        // Test with DES cipher
        InvertibleCryptographer des = InvertibleCryptographer.createDesCipher("12345678");
        FessSecurityResourceProvider provider = new FessSecurityResourceProvider(des, oneWayCryptographer);

        assertNotNull(provider);
        assertEquals(des, provider.providePrimaryInvertibleCryptographer());
        assertEquals(oneWayCryptographer, provider.providePrimaryOneWayCryptographer());
    }

    // Test with different hash algorithms
    @Test
    public void test_withSha512Cryptographer() {
        // Test with SHA512
        OneWayCryptographer sha512 = OneWayCryptographer.createSha512Cryptographer();
        FessSecurityResourceProvider provider = new FessSecurityResourceProvider(invertibleCryptographer, sha512);

        assertNotNull(provider);
        assertEquals(invertibleCryptographer, provider.providePrimaryInvertibleCryptographer());
        assertEquals(sha512, provider.providePrimaryOneWayCryptographer());
    }

    @Test
    public void test_withMd5Cryptographer() {
        // Test with MD5
        OneWayCryptographer md5 = new OneWayCryptographer("MD5", OneWayCryptographer.ENCODING_UTF8);
        FessSecurityResourceProvider provider = new FessSecurityResourceProvider(invertibleCryptographer, md5);

        assertNotNull(provider);
        assertEquals(invertibleCryptographer, provider.providePrimaryInvertibleCryptographer());
        assertEquals(md5, provider.providePrimaryOneWayCryptographer());
    }

    // Test encryption and decryption functionality
    @Test
    public void test_invertibleCryptography() {
        // Test that invertible cryptography works correctly
        InvertibleCryptographer cryptographer = securityResourceProvider.providePrimaryInvertibleCryptographer();
        assertNotNull(cryptographer);

        String plainText = "Hello, World!";
        String encrypted = cryptographer.encrypt(plainText);
        assertNotNull(encrypted);
        assertFalse(plainText.equals(encrypted));

        String decrypted = cryptographer.decrypt(encrypted);
        assertEquals(plainText, decrypted);
    }

    @Test
    public void test_invertibleCryptography_withEmptyString() {
        // Test with empty string
        InvertibleCryptographer cryptographer = securityResourceProvider.providePrimaryInvertibleCryptographer();
        assertNotNull(cryptographer);

        String plainText = "";
        String encrypted = cryptographer.encrypt(plainText);
        assertNotNull(encrypted);

        String decrypted = cryptographer.decrypt(encrypted);
        assertEquals(plainText, decrypted);
    }

    @Test
    public void test_invertibleCryptography_withSpecialCharacters() {
        // Test with special characters
        InvertibleCryptographer cryptographer = securityResourceProvider.providePrimaryInvertibleCryptographer();
        assertNotNull(cryptographer);

        String plainText = "!@#$%^&*(){}[]|\\:;\"'<>,.?/~`";
        String encrypted = cryptographer.encrypt(plainText);
        assertNotNull(encrypted);
        assertFalse(plainText.equals(encrypted));

        String decrypted = cryptographer.decrypt(encrypted);
        assertEquals(plainText, decrypted);
    }

    @Test
    public void test_oneWayCryptography() {
        // Test that one-way cryptographer is available
        OneWayCryptographer cryptographer = securityResourceProvider.providePrimaryOneWayCryptographer();
        assertNotNull(cryptographer);
    }

    @Test
    public void test_oneWayCryptography_consistency() {
        // Test that same cryptographer instance is returned
        OneWayCryptographer cryptographer1 = securityResourceProvider.providePrimaryOneWayCryptographer();
        OneWayCryptographer cryptographer2 = securityResourceProvider.providePrimaryOneWayCryptographer();

        assertNotNull(cryptographer1);
        assertNotNull(cryptographer2);
        assertSame(cryptographer1, cryptographer2);
    }

    @Test
    public void test_oneWayCryptography_withDifferentProviders() {
        // Test with different providers
        OneWayCryptographer sha256 = OneWayCryptographer.createSha256Cryptographer();
        FessSecurityResourceProvider provider1 = new FessSecurityResourceProvider(invertibleCryptographer, sha256);

        OneWayCryptographer sha512 = OneWayCryptographer.createSha512Cryptographer();
        FessSecurityResourceProvider provider2 = new FessSecurityResourceProvider(invertibleCryptographer, sha512);

        assertNotNull(provider1.providePrimaryOneWayCryptographer());
        assertNotNull(provider2.providePrimaryOneWayCryptographer());
        assertNotSame(provider1.providePrimaryOneWayCryptographer(), provider2.providePrimaryOneWayCryptographer());
    }

    // Test thread safety
    @Test
    public void test_threadSafety() {
        // Test that the provider returns the same instances in multi-threaded environment
        final int threadCount = 10;
        final Thread[] threads = new Thread[threadCount];
        final InvertibleCryptographer[] invertibles = new InvertibleCryptographer[threadCount];
        final OneWayCryptographer[] oneWays = new OneWayCryptographer[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                invertibles[index] = securityResourceProvider.providePrimaryInvertibleCryptographer();
                oneWays[index] = securityResourceProvider.providePrimaryOneWayCryptographer();
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                fail("Thread interrupted: " + e.getMessage());
            }
        }

        // Verify all threads got the same instances
        for (int i = 0; i < threadCount; i++) {
            assertSame(invertibleCryptographer, invertibles[i]);
            assertSame(oneWayCryptographer, oneWays[i]);
        }
    }

    // Test with multiple providers
    @Test
    public void test_multipleProviders() {
        // Test that multiple providers can coexist independently
        InvertibleCryptographer inver1 = InvertibleCryptographer.createAesCipher("key1key1key1key1");
        InvertibleCryptographer inver2 = InvertibleCryptographer.createAesCipher("key2key2key2key2");
        OneWayCryptographer oneWay1 = OneWayCryptographer.createSha256Cryptographer();
        OneWayCryptographer oneWay2 = OneWayCryptographer.createSha512Cryptographer();

        FessSecurityResourceProvider provider1 = new FessSecurityResourceProvider(inver1, oneWay1);
        FessSecurityResourceProvider provider2 = new FessSecurityResourceProvider(inver2, oneWay2);

        assertNotSame(provider1, provider2);
        assertEquals(inver1, provider1.providePrimaryInvertibleCryptographer());
        assertEquals(inver2, provider2.providePrimaryInvertibleCryptographer());
        assertEquals(oneWay1, provider1.providePrimaryOneWayCryptographer());
        assertEquals(oneWay2, provider2.providePrimaryOneWayCryptographer());
    }
}