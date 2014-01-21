/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

import java.nio.charset.Charset;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import jp.sf.fess.FessSystemException;

import org.apache.commons.codec.binary.Base64;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FessCipher {
    private static final Logger logger = LoggerFactory
            .getLogger(FessCipher.class);

    protected static final Charset UTF_8 = Charset.forName("UTF-8");

    public String algorithm = "Blowfish";

    @Binding(bindingType = BindingType.MUST)
    public String key;

    public Charset charset = UTF_8;

    protected Queue<Cipher> encryptoQueue = new ConcurrentLinkedQueue<Cipher>();

    protected Queue<Cipher> decryptoQueue = new ConcurrentLinkedQueue<Cipher>();

    public byte[] encrypto(final byte[] data) {
        final Cipher cipher = pollEncryptoCipher();
        byte[] encrypted;
        try {
            encrypted = cipher.doFinal(data);
        } catch (final Exception e) {
            throw new FessSystemException(
                    "Could not create a new cipher for encrypto.", e);
        } finally {
            offerEncryptoCipher(cipher);
        }
        return encrypted;
    }

    public String encryptoText(final String text) {
        return new String(
                Base64.encodeBase64(encrypto(text.getBytes(charset))), UTF_8);
    }

    public byte[] decrypto(final byte[] data) {
        final Cipher cipher = pollDecryptoCipher();
        byte[] decrypted;
        try {
            decrypted = cipher.doFinal(data);
        } catch (final Exception e) {
            throw new FessSystemException(
                    "Could not create a new cipher for decrypto.", e);
        } finally {
            offerDecryptoCipher(cipher);
        }
        return decrypted;
    }

    public String decryptoText(final String text) {
        return new String(decrypto(Base64.decodeBase64(text.getBytes(UTF_8))),
                charset);
    }

    protected Cipher pollEncryptoCipher() {
        Cipher cipher = encryptoQueue.poll();
        if (cipher == null) {
            if (logger.isInfoEnabled()) {
                logger.info("Initializing a cipher for an encryption.");
            }
            final SecretKeySpec sksSpec = new SecretKeySpec(
                    key.getBytes(UTF_8), algorithm);
            try {
                cipher = Cipher.getInstance(algorithm);
                cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, sksSpec);
            } catch (final Exception e) {
                throw new FessSystemException(
                        "Could not create a new cipher for encrypto.", e);
            }
        }
        return cipher;
    }

    protected void offerEncryptoCipher(final Cipher cipher) {
        encryptoQueue.offer(cipher);
    }

    protected Cipher pollDecryptoCipher() {
        Cipher cipher = decryptoQueue.poll();
        if (cipher == null) {
            if (logger.isInfoEnabled()) {
                logger.info("Initializing a cipher for an decryption.");
            }
            final SecretKeySpec sksSpec = new SecretKeySpec(
                    key.getBytes(UTF_8), algorithm);
            try {
                cipher = Cipher.getInstance(algorithm);
                cipher.init(javax.crypto.Cipher.DECRYPT_MODE, sksSpec);
            } catch (final Exception e) {
                throw new FessSystemException(
                        "Could not create a new cipher for decrypto.", e);
            }
        }
        return cipher;
    }

    protected void offerDecryptoCipher(final Cipher cipher) {
        decryptoQueue.offer(cipher);
    }
}
