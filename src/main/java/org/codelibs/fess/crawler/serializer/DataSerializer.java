/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.core.io.SerializeUtil;
import org.codelibs.fess.util.ComponentUtil;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class DataSerializer {

    private static final Logger logger = LogManager.getLogger(DataSerializer.class);

    protected static final String JAVABIN = "javabin";

    protected static final String KRYO = "kryo";

    protected final ThreadLocal<Kryo> kryoThreadLocal;

    public DataSerializer() {
        kryoThreadLocal = ThreadLocal.withInitial(() -> {
            final Kryo kryo = new Kryo();
            // TODO use kryo.register
            kryo.setRegistrationRequired(false);
            if (logger.isDebugEnabled()) {
                kryo.setWarnUnregisteredClasses(true);
            }
            return kryo;
        });
    }

    protected String getSerializerType() {
        return ComponentUtil.getFessConfig().getCrawlerDataSerializer();
    }

    public byte[] fromObjectToBinary(final Object obj) {
        final String serializer = getSerializerType();
        return switch (serializer) {
        case KRYO -> serializeWithKryo(obj);
        case JAVABIN -> SerializeUtil.fromObjectToBinary(obj);
        default -> throw new IllegalArgumentException("Unexpected value: " + serializer);
        };
    }

    public Object fromBinaryToObject(final byte[] bytes) {
        final String serializer = getSerializerType();
        return switch (serializer) {
        case KRYO -> deserializeWithKryo(bytes);
        case JAVABIN -> SerializeUtil.fromBinaryToObject(bytes);
        default -> throw new IllegalArgumentException("Unexpected value: " + serializer);
        };
    }

    protected byte[] serializeWithKryo(final Object obj) {
        final Kryo kryo = kryoThreadLocal.get();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); final Output output = new Output(baos)) {
            kryo.writeClassAndObject(output, obj);
            output.flush();
            return baos.toByteArray();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    protected Object deserializeWithKryo(final byte[] bytes) {
        final Kryo kryo = kryoThreadLocal.get();
        try (final Input input = new Input(new ByteArrayInputStream(bytes))) {
            return kryo.readClassAndObject(input);
        }
    }
}
