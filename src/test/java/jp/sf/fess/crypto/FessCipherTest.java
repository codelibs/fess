/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.crypto;

import org.seasar.extension.unit.S2TestCase;

public class FessCipherTest extends S2TestCase {
    public FessCipher fessCipher;

    @Override
    protected String getRootDicon() throws Throwable {
        return "jp/sf/fess/crypto/cipher.dicon";
    }

    public void test_encypto() throws Exception {
        final byte[] value = "test".getBytes(FessCipher.UTF_8);
        final byte[] result = new byte[] { -71, 94, 118, -115, -62, -28, -92,
                -29 };
        final byte[] data = fessCipher.encrypto(value);
        assertEquals(8, data.length);
        for (int i = 0; i < 8; i++) {
            assertEquals(result[i], data[i]);
        }
        assertEquals(1, fessCipher.encryptoQueue.size());
        assertEquals(0, fessCipher.decryptoQueue.size());
    }

    public void test_decypto() throws Exception {
        final byte[] value = new byte[] { -71, 94, 118, -115, -62, -28, -92,
                -29 };
        final byte[] result = "test".getBytes(FessCipher.UTF_8);
        final byte[] data = fessCipher.decrypto(value);
        assertEquals(4, data.length);
        for (int i = 0; i < 4; i++) {
            assertEquals(result[i], data[i]);
        }
        assertEquals(0, fessCipher.encryptoQueue.size());
        assertEquals(1, fessCipher.decryptoQueue.size());
    }

    public void test_encyptoText() {
        final String value = "test";
        final String result = "uV52jcLkpOM=";
        final String data = fessCipher.encryptoText(value);
        assertEquals(result, data);
        assertEquals(1, fessCipher.encryptoQueue.size());
        assertEquals(0, fessCipher.decryptoQueue.size());
    }

    public void test_decyptoText() {
        final String value = "uV52jcLkpOM=";
        final String result = "test";
        final String data = fessCipher.decryptoText(value);
        assertEquals(result, data);
        assertEquals(0, fessCipher.encryptoQueue.size());
        assertEquals(1, fessCipher.decryptoQueue.size());
    }
}
