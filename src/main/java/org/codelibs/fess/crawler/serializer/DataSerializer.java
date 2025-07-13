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

/**
 * A serializer class for handling object serialization and deserialization.
 * <p>
 * This class provides serialization capabilities using different serializers,
 * currently supporting Kryo and JavaBin serialization formats. The serializer
 * type is determined by the crawler data serializer configuration.
 * </p>
 * <p>
 * The class is thread-safe and uses ThreadLocal to maintain Kryo instances
 * per thread to avoid synchronization overhead.
 * </p>
 *
 * @author CodeLibs Project
 */
public class DataSerializer {

    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(DataSerializer.class);

    /** Constant for JavaBin serializer type. */
    protected static final String JAVABIN = "javabin";

    /** Constant for Kryo serializer type. */
    protected static final String KRYO = "kryo";

    /** ThreadLocal container for Kryo instances to ensure thread safety. */
    protected final ThreadLocal<Kryo> kryoThreadLocal;

    /**
     * Constructs a new DataSerializer.
     * <p>
     * Initializes the ThreadLocal Kryo instances with appropriate configuration.
     * The Kryo instances are configured to not require class registration for
     * flexibility, and debug mode warnings are enabled when debug logging is active.
     * </p>
     */
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

    /**
     * Gets the configured serializer type from the Fess configuration.
     *
     * @return the serializer type (either "kryo" or "javabin")
     */
    protected String getSerializerType() {
        return ComponentUtil.getFessConfig().getCrawlerDataSerializer();
    }

    /**
     * Serializes an object to a byte array.
     * <p>
     * The serialization method used depends on the configured serializer type.
     * Supported types are Kryo and JavaBin serialization.
     * </p>
     *
     * @param obj the object to serialize
     * @return the serialized object as a byte array
     * @throws IllegalArgumentException if an unsupported serializer type is configured
     * @throws IORuntimeException if an I/O error occurs during serialization
     */
    public byte[] fromObjectToBinary(final Object obj) {
        final String serializer = getSerializerType();
        return switch (serializer) {
        case KRYO -> serializeWithKryo(obj);
        case JAVABIN -> SerializeUtil.fromObjectToBinary(obj);
        default -> throw new IllegalArgumentException("Unexpected value: " + serializer);
        };
    }

    /**
     * Deserializes a byte array back to an object.
     * <p>
     * The deserialization method used depends on the configured serializer type.
     * Supported types are Kryo and JavaBin deserialization.
     * </p>
     *
     * @param bytes the byte array to deserialize
     * @return the deserialized object
     * @throws IllegalArgumentException if an unsupported serializer type is configured
     * @throws IORuntimeException if an I/O error occurs during deserialization
     */
    public Object fromBinaryToObject(final byte[] bytes) {
        final String serializer = getSerializerType();
        return switch (serializer) {
        case KRYO -> deserializeWithKryo(bytes);
        case JAVABIN -> SerializeUtil.fromBinaryToObject(bytes);
        default -> throw new IllegalArgumentException("Unexpected value: " + serializer);
        };
    }

    /**
     * Serializes an object using Kryo serialization.
     * <p>
     * Uses the thread-local Kryo instance to serialize the object along with
     * its class information. The serialized data is written to a byte array
     * output stream.
     * </p>
     *
     * @param obj the object to serialize
     * @return the serialized object as a byte array
     * @throws IORuntimeException if an I/O error occurs during serialization
     */
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

    /**
     * Deserializes a byte array using Kryo deserialization.
     * <p>
     * Uses the thread-local Kryo instance to read both the class information
     * and object data from the byte array input stream.
     * </p>
     *
     * @param bytes the byte array to deserialize
     * @return the deserialized object
     */
    protected Object deserializeWithKryo(final byte[] bytes) {
        final Kryo kryo = kryoThreadLocal.get();
        try (final Input input = new Input(new ByteArrayInputStream(bytes))) {
            return kryo.readClassAndObject(input);
        }
    }
}
