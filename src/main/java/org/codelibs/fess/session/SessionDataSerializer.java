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
package org.codelibs.fess.session;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.collection.LruHashMap;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.entity.FessUser.PermissionState;
import org.codelibs.fess.ldap.LdapUser;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.util.LogUtil;
import org.lastaflute.core.message.UserMessage;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.LastaAction;
import org.lastaflute.web.login.redirect.LoginRedirectBean;
import org.lastaflute.web.token.DoubleSubmitTokenMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.BooleanArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.ObjectArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.ArraysAsListSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.TreeMapSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.TreeSetSerializer;
import com.esotericsoftware.kryo.serializers.MapSerializer;

/**
 * Serializes the attributes of an HTTP session for the OpenSearch-backed session store.
 *
 * <p>Neither {@link java.io.Serializable} nor Java serialization is involved. Like
 * {@code org.codelibs.fess.crawler.serializer.DataSerializer} this class owns a Kryo that requires
 * registration, so nothing that was not registered here can be read back, which keeps a gadget chain out
 * of the session store. Unlike it, a class of this application is never written field by field: every
 * one of them has an explicit serializer below that names exactly what is stored.</p>
 *
 * <p>What is written is a header followed by one entry per attribute: the name, the length and the bytes
 * of the value. The header holds the format number and a fingerprint of the registered classes in the
 * order their ids were assigned. {@link #fromBytes(byte[])} answers {@code null} when the fingerprint
 * differs, so a session written by another build or with other plugins is treated as absent instead of
 * being misread. The entries are independent of each other: an attribute whose value cannot be written is
 * skipped, and one that cannot be read is dropped, and the others are still handled. Both are reported once
 * with {@link LogUtil#warnOnce}. A skipped attribute looks the same as a removed one to the reader of the
 * stored session.</p>
 *
 * <p>Credentials are never stored. {@link User#getOriginalPassword()} and the password hash of a
 * {@link User}, and the LDAP environment of an {@link LdapUser}, are not written at all, and the attributes
 * named by {@link #isExcluded(String)} are dropped by name because their values are plain strings that the
 * type cannot tell apart.</p>
 *
 * <p>The stored bytes are not trusted: anyone who can write the session index can write anything. Every
 * length read from them is checked before something is allocated or looped over: the payload is at most
 * {@link #MAX_PAYLOAD_BYTES}, a count cannot exceed the bytes that are left (see {@link #checkLength} and
 * {@link #checkElementCount}) nor the absolute {@link #MAX_ELEMENTS}, the arrays of Kryo are checked by
 * {@link BoundedInput}, the collections and maps by the serializers below, and the nesting is limited to
 * {@link #MAX_DEPTH}. A violation is a {@link KryoException}, so that only the attribute concerned is dropped.</p>
 *
 * <p>Plugins add classes with {@link #register(Class, Serializer)} while the DI container initializes. The
 * registrations are fixed as soon as the format version is asked for or the first Kryo is created.</p>
 */
public class SessionDataSerializer {

    private static final Logger logger = LogManager.getLogger(SessionDataSerializer.class);

    /**
     * The number of the layout of the bytes written. Increase it when the layout or a serializer below
     * changes in a way that older bytes cannot be read.
     */
    protected static final int FORMAT_NUMBER = 1;

    /**
     * The session attribute that {@code LoginAction} fills with the password a user logged in with when it
     * is on the list of passwords that must be changed. It is a plain string, so it is excluded by name.
     */
    public static final String INVALID_OLD_PASSWORD = "LoginAction.invalidOldPassword";

    /** The attributes that are never written. */
    protected static final Set<String> EXCLUDED_ATTRIBUTES = Set.of(INVALID_OLD_PASSWORD);

    /** The classes whose instances are kept as they are in the arguments of a message. */
    protected static final Set<Class<?>> MESSAGE_VALUE_TYPES = Set.of(String.class, Integer.class, Long.class, Short.class, Byte.class,
            Boolean.class, Double.class, Float.class, Character.class, BigDecimal.class, BigInteger.class, Date.class, Timestamp.class);

    /** The largest payload that is written or read. A session is a few kilobytes; this only stops a hostile length. */
    public static final int MAX_PAYLOAD_BYTES = 4 * 1024 * 1024;

    /** The largest number of elements of a collection, a map or an array that is read. */
    protected static final int MAX_ELEMENTS = 1_000_000;

    /**
     * The number of elements that is accepted even when fewer bytes are left, because elements such as the
     * nulls of a list of one type take no bytes at all.
     */
    protected static final int ZERO_BYTE_ELEMENTS = 1024;

    /** The largest limit of a {@link LruHashMap} that is restored; a larger one is lowered to it. */
    protected static final int MAX_LRU_LIMIT = 10_000;

    /** The deepest nesting of objects that is written or read; it keeps a hostile nesting from overflowing the stack. */
    protected static final int MAX_DEPTH = 64;

    /** How many times the entries of a map that another thread changes are copied before giving up. */
    protected static final int SNAPSHOT_RETRY = 3;

    /** The Kryo of each thread, as a Kryo is not thread safe. */
    protected final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(this::createKryo);

    /** The classes plugins have registered, in class name order so that the ids do not depend on the loading order. */
    protected final Map<Class<?>, Serializer<?>> pluginSerializerMap = new TreeMap<>(Comparator.comparing(Class::getName));

    /** The registrations in the order of their ids, fixed when {@link #locked}. */
    protected List<Registration> registrations;

    /** The format version, fixed when {@link #locked}. */
    protected String formatVersion;

    /** The fingerprint of the registered classes, fixed when {@link #locked}. */
    protected long fingerprint;

    /** Whether the registrations are fixed, after which no class can be registered. */
    protected boolean locked;

    /**
     * Default constructor.
     */
    public SessionDataSerializer() {
        // nothing to do
    }

    /**
     * A class registered with Kryo and how it is serialized.
     *
     * @param type the class
     * @param serializer creates the serializer, or null for the default of Kryo (used for JDK types only)
     */
    protected record Registration(Class<?> type, Supplier<Serializer<?>> serializer) {
    }

    // ===================================================================================
    //                                                                        Registration
    //                                                                        ============
    /**
     * Allows a plugin to add a class to the classes that can be stored in a session.
     *
     * <p>A serializer is required: a class is never opened to the field by field serialization of Kryo, so
     * the plugin decides exactly what is written, and keeps credentials out of it. The serializer is shared
     * by all threads and must not keep state. The ids follow the class name order, so plugins do not have
     * to agree on them, but every node must have the same plugins or the format versions differ.</p>
     *
     * @param type the class to register
     * @param serializer the serializer that writes and reads the class
     * @throws IllegalArgumentException if the type or the serializer is null, or the type is registered already
     * @throws IllegalStateException if the registrations are fixed already
     */
    public synchronized void register(final Class<?> type, final Serializer<?> serializer) {
        if (type == null || serializer == null) {
            throw new IllegalArgumentException("Both type and serializer are required. type: " + type + ", serializer: " + serializer);
        }
        if (locked) {
            throw new IllegalStateException("Cannot register " + type.getName() + " because the registrations are fixed already.");
        }
        if (pluginSerializerMap.containsKey(type) || coreRegistrations().stream().anyMatch(r -> r.type().equals(type))) {
            throw new IllegalArgumentException(type.getName() + " is already registered.");
        }
        pluginSerializerMap.put(type, serializer);
        if (logger.isDebugEnabled()) {
            logger.debug("Registered {} for the session store.", type.getName());
        }
    }

    /**
     * Returns the version of the format written, which changes with the format number and with the set of
     * registered classes. Asking for it fixes the registrations.
     *
     * @return the format version
     */
    public String getFormatVersion() {
        lock();
        return formatVersion;
    }

    /**
     * Fixes the registrations and computes the format version, once.
     */
    protected synchronized void lock() {
        if (locked) {
            return;
        }
        final List<Registration> list = new ArrayList<>(coreRegistrations());
        pluginSerializerMap.forEach((type, serializer) -> list.add(new Registration(type, () -> serializer)));
        // The ids are assigned in this order, so a change of the order is a change of the format
        // even when the same classes are registered.
        final StringBuilder sb = new StringBuilder().append(FORMAT_NUMBER).append('\n');
        list.forEach(r -> sb.append(r.type().getName()).append('\n'));
        try {
            final byte[] digest = MessageDigest.getInstance("SHA-256").digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            long value = 0;
            for (int i = 0; i < Long.BYTES; i++) {
                value = (value << 8) | (digest[i] & 0xff);
            }
            fingerprint = value;
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available.", e);
        }
        formatVersion = FORMAT_NUMBER + "-" + String.format("%016x", fingerprint);
        registrations = list;
        locked = true;
    }

    /**
     * Creates a Kryo that requires registration and knows the registered classes.
     *
     * @return the Kryo
     */
    protected Kryo createKryo() {
        lock();
        final Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(true);
        kryo.setMaxDepth(MAX_DEPTH);
        for (final Registration r : registrations) {
            if (r.serializer() == null) {
                kryo.register(r.type());
            } else {
                kryo.register(r.type(), r.serializer().get());
            }
        }
        return kryo;
    }

    /**
     * Lists the classes this application registers, in the order of their ids.
     *
     * @return the registrations
     */
    protected List<Registration> coreRegistrations() {
        final List<Registration> list = new ArrayList<>();
        for (final Class<?> type : new Class<?>[] { String.class, String[].class, Integer.class, int[].class, Long.class, long[].class,
                Double.class, double[].class, Float.class, float[].class, Boolean.class, boolean[].class, Byte.class, byte[].class,
                Short.class, short[].class, Character.class, char[].class, Date.class, Timestamp.class, BigDecimal.class, BigInteger.class,
                TreeSet.class, TreeMap.class }) {
            // The default serializers of Kryo for these allocate from the length they read, except those of the
            // primitive arrays, which BoundedInput checks.
            final Supplier<Serializer<?>> bounded = type == String[].class ? BoundedStringArraySerializer::new
                    : type == boolean[].class ? BoundedBooleanArraySerializer::new
                            : type == TreeSet.class ? BoundedTreeSetSerializer::new
                                    : type == TreeMap.class ? BoundedTreeMapSerializer::new : null;
            list.add(new Registration(type, bounded));
        }
        list.add(new Registration(ArrayList.class, BoundedCollectionSerializer::new));
        list.add(new Registration(LinkedList.class, BoundedCollectionSerializer::new));
        list.add(new Registration(HashSet.class, BoundedCollectionSerializer::new));
        list.add(new Registration(LinkedHashSet.class, BoundedCollectionSerializer::new));
        list.add(new Registration(HashMap.class, BoundedMapSerializer::new));
        list.add(new Registration(LinkedHashMap.class, BoundedMapSerializer::new));
        list.add(new Registration(ConcurrentHashMap.class, BoundedMapSerializer::new));
        list.add(new Registration(Collections.emptyList().getClass(), null));
        list.add(new Registration(Collections.emptySet().getClass(), null));
        list.add(new Registration(Collections.emptyMap().getClass(), null));
        list.add(new Registration(Collections.singletonList(null).getClass(), null));
        list.add(new Registration(Collections.singleton(null).getClass(), null));
        list.add(new Registration(Collections.singletonMap(null, null).getClass(), null));
        list.add(new Registration(Arrays.asList().getClass(), BoundedArraysAsListSerializer::new));

        list.add(new Registration(Locale.class, LocaleSerializer::new));
        list.add(new Registration(LruHashMap.class, LruHashMapSerializer::new));

        list.add(new Registration(UserMessages.class, () -> new UserMessagesSerializer(UserMessages::new)));
        list.add(new Registration(FessMessages.class, () -> new UserMessagesSerializer(FessMessages::new)));
        list.add(new Registration(DoubleSubmitTokenMap.class, DoubleSubmitTokenMapSerializer::new));
        list.add(new Registration(LoginRedirectBean.class, LoginRedirectBeanSerializer::new));

        list.add(new Registration(FessUserBean.class, FessUserBeanSerializer::new));
        list.add(new Registration(FessUserBean.empty().getClass(), EmptyFessUserBeanSerializer::new));
        list.add(new Registration(User.class, UserSerializer::new));
        list.add(new Registration(LdapUser.class, LdapUserSerializer::new));
        return list;
    }

    // ===================================================================================
    //                                                                       Serialization
    //                                                                       =============
    /**
     * Tests whether an attribute is never stored, whatever its value.
     *
     * @param name the name of the attribute
     * @return true if it is not stored
     */
    public static boolean isExcluded(final String name) {
        return EXCLUDED_ATTRIBUTES.contains(name);
    }

    /**
     * Serializes the attributes of a session. An attribute that is excluded, whose class is not registered
     * or whose serializer fails is left out and the others are written.
     *
     * @param attributes the attributes by name
     * @return the bytes
     */
    public byte[] toBytes(final Map<String, Object> attributes) {
        return toBytes(attributes, null);
    }

    /**
     * Serializes the attributes of a session, and reports the names that were left out, so that the reader of
     * the stored session can tell a skipped attribute from a removed one. Null names and values are not
     * attributes and are not reported.
     *
     * @param attributes the attributes by name
     * @param skippedNames receives the names of the attributes that were not written, or null if not needed
     * @return the bytes
     */
    public byte[] toBytes(final Map<String, Object> attributes, final Set<String> skippedNames) {
        final Kryo kryo = kryoThreadLocal.get();
        final Map<String, byte[]> entries = new LinkedHashMap<>();
        if (attributes != null) {
            final Output scratch = new Output(256, MAX_PAYLOAD_BYTES);
            int total = 0;
            for (final Map.Entry<String, Object> entry : new ArrayList<>(attributes.entrySet())) {
                final String name = entry.getKey();
                final Object value = entry.getValue();
                if (name == null || value == null) {
                    continue;
                }
                if (isExcluded(name)) {
                    if (skippedNames != null) {
                        skippedNames.add(name);
                    }
                    continue;
                }
                try {
                    scratch.reset();
                    kryo.writeClassAndObject(scratch, value);
                    final byte[] bytes = scratch.toBytes();
                    total += bytes.length + name.length() * 3 + 10;
                    if (total > MAX_PAYLOAD_BYTES - 64) {
                        throw new KryoException("The stored session would exceed " + MAX_PAYLOAD_BYTES + " bytes.");
                    }
                    entries.put(name, bytes);
                } catch (final RuntimeException e) {
                    kryo.reset();
                    if (skippedNames != null) {
                        skippedNames.add(name);
                    }
                    LogUtil.warnOnce(logger, "write:" + name + ":" + value.getClass().getName(),
                            "The session attribute {} ({}) is not shared between nodes because it cannot be serialized: {}."
                                    + " Register its class with SessionDataSerializer#register to share it.",
                            name, value.getClass().getName(), e.getMessage());
                }
            }
        }
        final Output output = new Output(256, -1);
        output.writeVarInt(FORMAT_NUMBER, true);
        output.writeLong(fingerprint);
        output.writeVarInt(entries.size(), true);
        entries.forEach((name, bytes) -> {
            output.writeString(name);
            output.writeVarInt(bytes.length, true);
            output.writeBytes(bytes);
        });
        return output.toBytes();
    }

    /**
     * Deserializes the attributes of a session.
     *
     * @param bytes the bytes written by {@link #toBytes(Map)}
     * @return the attributes by name, or null when the bytes were written with another format version or
     *         cannot be read
     */
    public Map<String, Object> fromBytes(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        if (bytes.length > MAX_PAYLOAD_BYTES) {
            LogUtil.warnOnce(logger, "read:size", "The stored session is ignored because it is larger than {} bytes.", MAX_PAYLOAD_BYTES);
            return null;
        }
        final Kryo kryo = kryoThreadLocal.get();
        final Map<String, Object> attributes = new LinkedHashMap<>();
        try {
            final Input input = new BoundedInput(bytes, 0, bytes.length);
            if (input.readVarInt(true) != FORMAT_NUMBER || input.readLong() != fingerprint) {
                return null;
            }
            final int count = input.readVarInt(true);
            // an entry takes more than a byte: the name and the length
            checkLength(input, count);
            for (int i = 0; i < count; i++) {
                final String name = input.readString();
                final int length = input.readVarInt(true);
                checkBytes(input, length, 1);
                // the attribute is read from its own bytes, so that it cannot read into the next one
                final Input value = new BoundedInput(bytes, input.position(), length);
                input.skip(length);
                try {
                    final Object object = kryo.readClassAndObject(value);
                    if (object != null) {
                        attributes.put(name, object);
                    }
                } catch (final RuntimeException e) {
                    kryo.reset();
                    LogUtil.warnOnce(logger, "read:" + name, "The session attribute {} is dropped because it cannot be deserialized.", name,
                            e);
                }
            }
            return attributes;
        } catch (final RuntimeException e) {
            kryo.reset();
            LogUtil.warnOnce(logger, "read", "The stored session is ignored because it cannot be read.", e);
            return null;
        }
    }

    // ===================================================================================
    //                                                                          Serializers
    //                                                                          ===========
    /**
     * Writes a string array that may be null.
     *
     * @param output the output
     * @param values the values
     */
    protected static void writeStrings(final Output output, final String[] values) {
        output.writeVarInt(values == null ? 0 : values.length + 1, true);
        if (values != null) {
            for (final String value : values) {
                output.writeString(value);
            }
        }
    }

    /**
     * Reads a string array written by {@link #writeStrings(Output, String[])}.
     *
     * @param input the input
     * @return the values, or null
     */
    protected static String[] readStrings(final Input input) {
        final int size = input.readVarInt(true);
        if (size == 0) {
            return null;
        }
        checkLength(input, size - 1);
        final String[] values = new String[size - 1];
        for (int i = 0; i < values.length; i++) {
            values[i] = input.readString();
        }
        return values;
    }

    /**
     * Checks a count read from the stored bytes where each of the elements takes at least a byte, so that a
     * count larger than the bytes that are left, which cannot be true, is refused before anything is
     * allocated for it. Serializers of plugins use it for the counts they read.
     *
     * @param input the input the count was read from
     * @param length the count
     * @throws KryoException if the count is negative, larger than {@link #MAX_ELEMENTS} or than the bytes left
     */
    public static void checkLength(final Input input, final int length) {
        final int remaining = input.limit() - input.position();
        if (length < 0 || length > MAX_ELEMENTS || length > remaining) {
            throw new KryoException("Invalid length " + length + " with " + remaining + " bytes left.");
        }
    }

    /**
     * Checks a count of the elements of a collection or a map. It is the same as {@link #checkLength} except
     * that up to {@link #ZERO_BYTE_ELEMENTS} elements are accepted without bytes, as a list of nulls takes none.
     *
     * @param input the input the count was read from
     * @param length the count
     * @throws KryoException if the count is not plausible
     */
    public static void checkElementCount(final Input input, final int length) {
        if (length > ZERO_BYTE_ELEMENTS) {
            checkLength(input, length);
        } else if (length < 0) {
            throw new KryoException("Invalid length " + length);
        }
    }

    /**
     * Checks the length of an array in bytes of the bytes that are left.
     */
    private static void checkBytes(final Input input, final int length, final int bytesPerElement) {
        final long remaining = input.limit() - input.position();
        if (length < 0 || (long) length * bytesPerElement > remaining) {
            throw new KryoException("Invalid length " + length + " with " + remaining + " bytes left.");
        }
    }

    /**
     * An input that checks the length of what it reads in bulk against the bytes that are left before it
     * allocates, which Kryo does not: an array of 2 billion elements is allocated before the first byte is read.
     * It reads from a byte array only.
     */
    protected static class BoundedInput extends Input {

        /**
         * Creates the input.
         *
         * @param bytes the bytes
         * @param offset the first byte to read
         * @param length the number of bytes to read
         */
        protected BoundedInput(final byte[] bytes, final int offset, final int length) {
            super(bytes, offset, length);
        }

        @Override
        public byte[] readBytes(final int length) {
            checkBytes(this, length, 1);
            return super.readBytes(length);
        }

        @Override
        public String readString() {
            // A UTF-8 string allocates its characters from the length before it reads them, each takes a byte.
            if (readVarIntFlag()) {
                final int start = position();
                final int charCount = readVarIntFlag(true);
                if (charCount > 1) {
                    checkBytes(this, charCount - 1, 1);
                }
                setPosition(start);
            }
            return super.readString();
        }

        @Override
        public int[] readInts(final int length) {
            checkBytes(this, length, 4);
            return super.readInts(length);
        }

        @Override
        public int[] readInts(final int length, final boolean optimizePositive) {
            checkBytes(this, length, 1);
            return super.readInts(length, optimizePositive);
        }

        @Override
        public long[] readLongs(final int length) {
            checkBytes(this, length, 8);
            return super.readLongs(length);
        }

        @Override
        public long[] readLongs(final int length, final boolean optimizePositive) {
            checkBytes(this, length, 1);
            return super.readLongs(length, optimizePositive);
        }

        @Override
        public float[] readFloats(final int length) {
            checkBytes(this, length, 4);
            return super.readFloats(length);
        }

        @Override
        public double[] readDoubles(final int length) {
            checkBytes(this, length, 8);
            return super.readDoubles(length);
        }

        @Override
        public short[] readShorts(final int length) {
            checkBytes(this, length, 2);
            return super.readShorts(length);
        }

        @Override
        public char[] readChars(final int length) {
            checkBytes(this, length, 2);
            return super.readChars(length);
        }

        @Override
        public boolean[] readBooleans(final int length) {
            checkBytes(this, length, 1);
            return super.readBooleans(length);
        }
    }

    /**
     * A {@link CollectionSerializer} that checks the count before it creates the collection with it.
     */
    @SuppressWarnings("rawtypes")
    protected static class BoundedCollectionSerializer extends CollectionSerializer<Collection> {

        /**
         * Default constructor.
         */
        public BoundedCollectionSerializer() {
            // nothing to do
        }

        @Override
        protected Collection create(final Kryo kryo, final Input input, final Class<? extends Collection> type, final int size) {
            checkElementCount(input, size);
            return super.create(kryo, input, type, size);
        }
    }

    /**
     * A {@link MapSerializer} that checks the count before it creates the map with it.
     */
    @SuppressWarnings("rawtypes")
    protected static class BoundedMapSerializer extends MapSerializer<Map> {

        /**
         * Default constructor.
         */
        public BoundedMapSerializer() {
            // nothing to do
        }

        @Override
        protected Map create(final Kryo kryo, final Input input, final Class<? extends Map> type, final int size) {
            checkElementCount(input, size);
            return super.create(kryo, input, type, size);
        }
    }

    /**
     * The serializer of {@link TreeSet} of Kryo, with the count checked.
     */
    @SuppressWarnings("rawtypes")
    protected static class BoundedTreeSetSerializer extends TreeSetSerializer {

        /**
         * Default constructor.
         */
        public BoundedTreeSetSerializer() {
            // nothing to do
        }

        @Override
        protected TreeSet create(final Kryo kryo, final Input input, final Class<? extends TreeSet> type, final int size) {
            checkElementCount(input, size);
            return super.create(kryo, input, type, size);
        }
    }

    /**
     * The serializer of {@link TreeMap} of Kryo, with the count checked.
     */
    @SuppressWarnings("rawtypes")
    protected static class BoundedTreeMapSerializer extends TreeMapSerializer {

        /**
         * Default constructor.
         */
        public BoundedTreeMapSerializer() {
            // nothing to do
        }

        @Override
        protected TreeMap create(final Kryo kryo, final Input input, final Class<? extends TreeMap> type, final int size) {
            checkElementCount(input, size);
            return super.create(kryo, input, type, size);
        }
    }

    /**
     * The serializer of the list of {@link Arrays#asList(Object[])} of Kryo, with the count checked.
     */
    @SuppressWarnings("rawtypes")
    protected static class BoundedArraysAsListSerializer extends ArraysAsListSerializer {

        /**
         * Default constructor.
         */
        public BoundedArraysAsListSerializer() {
            // nothing to do
        }

        @Override
        protected List create(final Kryo kryo, final Input input, final Class type, final int size) {
            checkElementCount(input, size);
            return super.create(kryo, input, type, size);
        }
    }

    /**
     * The serializer of {@code String[]} of Kryo, which allocates the array from the length it reads, with the
     * length checked. The bytes written are the same.
     */
    protected static class BoundedStringArraySerializer extends ObjectArraySerializer {

        /** Creates the serializer. */
        protected BoundedStringArraySerializer() {
            super(null, String[].class);
        }

        @Override
        public Object[] read(final Kryo kryo, final Input input, final Class type) {
            final int start = input.position();
            final int size = input.readVarInt(true);
            if (size > 0) {
                checkLength(input, size - 1);
            }
            input.setPosition(start);
            return super.read(kryo, input, type);
        }
    }

    /**
     * The serializer of {@code boolean[]} of Kryo, which allocates the array from the length it reads and
     * reads the elements one by one, with the length checked. The bytes written are the same.
     */
    protected static class BoundedBooleanArraySerializer extends BooleanArraySerializer {

        /**
         * Default constructor.
         */
        public BoundedBooleanArraySerializer() {
            // nothing to do
        }

        @Override
        public boolean[] read(final Kryo kryo, final Input input, final Class type) {
            final int start = input.position();
            final int size = input.readVarInt(true);
            if (size > 0) {
                checkLength(input, size - 1);
            }
            input.setPosition(start);
            return super.read(kryo, input, type);
        }
    }

    /**
     * Writes a long that may be null.
     *
     * @param output the output
     * @param value the value
     */
    protected static void writeLong(final Output output, final Long value) {
        output.writeBoolean(value != null);
        if (value != null) {
            output.writeLong(value);
        }
    }

    /**
     * Reads a long written by {@link #writeLong(Output, Long)}.
     *
     * @param input the input
     * @return the value, or null
     */
    protected static Long readLong(final Input input) {
        return input.readBoolean() ? Long.valueOf(input.readLong()) : null;
    }

    /**
     * Writes a {@link Locale} as a language tag, which keeps the script that the fields of the locale of Kryo drop.
     */
    protected static class LocaleSerializer extends Serializer<Locale> {

        /**
         * Default constructor.
         */
        public LocaleSerializer() {
            // nothing to do
        }

        @Override
        public void write(final Kryo kryo, final Output output, final Locale locale) {
            output.writeString(locale.toLanguageTag());
        }

        @Override
        public Locale read(final Kryo kryo, final Input input, final Class<? extends Locale> type) {
            return Locale.forLanguageTag(input.readString());
        }
    }

    /**
     * Writes a {@link LruHashMap} as its limit and its entries from the eldest to the youngest.
     */
    protected static class LruHashMapSerializer extends Serializer<LruHashMap<Object, Object>> {

        /**
         * Default constructor.
         */
        public LruHashMapSerializer() {
            // nothing to do
        }

        @Override
        public void write(final Kryo kryo, final Output output, final LruHashMap<Object, Object> map) {
            final List<Object> keys = new ArrayList<>();
            final List<Object> values = new ArrayList<>();
            // The map is not thread safe and a request of the same session may change it meanwhile.
            // Entries are visited, not looked up, because a lookup of this map moves the entry.
            for (int attempt = 1;; attempt++) {
                keys.clear();
                values.clear();
                try {
                    for (final Map.Entry<Object, Object> entry : map.entrySet()) {
                        keys.add(entry.getKey());
                        values.add(entry.getValue());
                    }
                    break;
                } catch (final ConcurrentModificationException e) {
                    if (attempt >= SNAPSHOT_RETRY) {
                        throw e;
                    }
                }
            }
            output.writeVarInt(map.getLimitSize(), true);
            output.writeVarInt(keys.size(), true);
            for (int i = 0; i < keys.size(); i++) {
                kryo.writeClassAndObject(output, keys.get(i));
                kryo.writeClassAndObject(output, values.get(i));
            }
        }

        @Override
        public LruHashMap<Object, Object> read(final Kryo kryo, final Input input, final Class<? extends LruHashMap<Object, Object>> type) {
            // The limit is a capacity that the map allocates on its first entry, so a hostile one is lowered.
            final int limit = input.readVarInt(true);
            if (limit < 1) {
                throw new KryoException("Invalid limit of the map: " + limit);
            }
            final LruHashMap<Object, Object> map = new LruHashMap<>(Math.min(limit, MAX_LRU_LIMIT));
            final int size = input.readVarInt(true);
            // a key and a value take two bytes
            checkLength(input, size);
            for (int i = 0; i < size; i++) {
                final Object key = kryo.readClassAndObject(input);
                map.put(key, kryo.readClassAndObject(input));
            }
            return map;
        }
    }

    /**
     * Writes {@link UserMessages} as the messages by property, flattened to keys and arguments. The
     * arguments that are not of a basic type are written as the text of {@link String#valueOf(Object)}, and
     * the information about the validator annotation is dropped; neither is needed to show the message.
     */
    protected static class UserMessagesSerializer extends Serializer<UserMessages> {

        private final Supplier<? extends UserMessages> factory;

        /**
         * Creates the serializer.
         *
         * @param factory creates the messages that are read
         */
        protected UserMessagesSerializer(final Supplier<? extends UserMessages> factory) {
            this.factory = factory;
        }

        @Override
        public void write(final Kryo kryo, final Output output, final UserMessages messages) {
            final Set<String> properties = messages.toPropertySet();
            output.writeVarInt(properties.size(), true);
            for (final String property : properties) {
                // the silent access: the other one marks the messages as shown, which is state of the session
                final List<UserMessage> list = new ArrayList<>();
                for (final Iterator<UserMessage> ite = messages.silentAccessByIteratorOf(property); ite.hasNext();) {
                    list.add(ite.next());
                }
                output.writeString(property);
                output.writeVarInt(list.size(), true);
                for (final UserMessage message : list) {
                    output.writeString(message.getMessageKey());
                    output.writeBoolean(message.isResource());
                    final Object[] values = message.getValues();
                    output.writeVarInt(values.length, true);
                    for (final Object value : values) {
                        kryo.writeClassAndObject(output,
                                value == null || MESSAGE_VALUE_TYPES.contains(value.getClass()) ? value : String.valueOf(value));
                    }
                }
            }
        }

        @Override
        public UserMessages read(final Kryo kryo, final Input input, final Class<? extends UserMessages> type) {
            final UserMessages messages = factory.get();
            final int propertyCount = input.readVarInt(true);
            checkLength(input, propertyCount);
            for (int i = 0; i < propertyCount; i++) {
                final String property = input.readString();
                final int messageCount = input.readVarInt(true);
                checkLength(input, messageCount);
                for (int j = 0; j < messageCount; j++) {
                    final String key = input.readString();
                    final boolean resource = input.readBoolean();
                    final int valueCount = input.readVarInt(true);
                    checkLength(input, valueCount);
                    final Object[] values = new Object[valueCount];
                    for (int k = 0; k < values.length; k++) {
                        values[k] = kryo.readClassAndObject(input);
                    }
                    messages.add(property, resource ? new UserMessage(key, values) : UserMessage.asDirectMessage(key));
                }
            }
            return messages;
        }
    }

    /**
     * Writes the transaction tokens of the forms by the name of the action class that issued them. An entry
     * whose class cannot be loaded, or is not an action, is dropped when it is read.
     */
    protected static class DoubleSubmitTokenMapSerializer extends Serializer<DoubleSubmitTokenMap> {

        /**
         * Default constructor.
         */
        public DoubleSubmitTokenMapSerializer() {
            // nothing to do
        }

        /** The map of the tokens, which has no accessor to iterate. */
        private static final Field TOKEN_MAP_FIELD = findTokenMapField();

        private static Field findTokenMapField() {
            try {
                final Field field = DoubleSubmitTokenMap.class.getDeclaredField("tokenMap");
                field.setAccessible(true);
                return field;
            } catch (final ReflectiveOperationException | RuntimeException e) {
                LogUtil.warnOnce(logger, "tokenMap", "The transaction tokens of forms are not shared between nodes.", e);
                return null;
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public void write(final Kryo kryo, final Output output, final DoubleSubmitTokenMap tokens) {
            if (TOKEN_MAP_FIELD == null) {
                throw new IllegalStateException("The token map of DoubleSubmitTokenMap is not accessible.");
            }
            final Map<Class<?>, String> map;
            try {
                map = new HashMap<>((Map<Class<?>, String>) TOKEN_MAP_FIELD.get(tokens));
            } catch (final IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
            output.writeVarInt(map.size(), true);
            map.forEach((groupType, token) -> {
                output.writeString(groupType.getName());
                output.writeString(token);
            });
        }

        @Override
        public DoubleSubmitTokenMap read(final Kryo kryo, final Input input, final Class<? extends DoubleSubmitTokenMap> type) {
            final DoubleSubmitTokenMap tokens = new DoubleSubmitTokenMap();
            final int size = input.readVarInt(true);
            checkLength(input, size);
            for (int i = 0; i < size; i++) {
                final String className = input.readString();
                final String token = input.readString();
                try {
                    // not initialized: loading must not run code of a class that is not an action
                    final Class<?> groupType = Class.forName(className, false, DoubleSubmitTokenMap.class.getClassLoader());
                    if (LastaAction.class.isAssignableFrom(groupType)) {
                        tokens.put(groupType, token);
                    }
                } catch (final ClassNotFoundException | LinkageError e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("A transaction token is dropped because {} cannot be loaded.", LogUtil.sanitize(className), e);
                    }
                }
            }
            return tokens;
        }
    }

    /**
     * Writes the path that a user is sent back to after a login.
     */
    protected static class LoginRedirectBeanSerializer extends Serializer<LoginRedirectBean> {

        /**
         * Default constructor.
         */
        public LoginRedirectBeanSerializer() {
            // nothing to do
        }

        @Override
        public void write(final Kryo kryo, final Output output, final LoginRedirectBean bean) {
            output.writeString(bean.getRedirectPath());
        }

        @Override
        public LoginRedirectBean read(final Kryo kryo, final Input input, final Class<? extends LoginRedirectBean> type) {
            return new LoginRedirectBean(input.readString());
        }
    }

    /**
     * Writes the login of a user: the {@link FessUser} and what the session remembers of it. The permissions
     * are not written for a local user, they are derived from the roles and groups.
     */
    protected static class FessUserBeanSerializer extends Serializer<FessUserBean> {

        /**
         * Default constructor.
         */
        public FessUserBeanSerializer() {
            // nothing to do
        }

        @Override
        public void write(final Kryo kryo, final Output output, final FessUserBean bean) {
            kryo.writeClassAndObject(output, bean.getFessUser());
            writeLong(output, bean.getPasswordChangedAt());
            writeLong(output, bean.getLoginTime());
            writeLong(output, bean.getPasswordCheckTime());
            output.writeString(bean.getLastestSyncCheckTime().map(LocalDateTime::toString).orElse(null));
            output.writeString(bean.getUserLocale().map(Locale::toLanguageTag).orElse(null));
            output.writeString(bean.getUserTimeZone().map(TimeZone::getID).orElse(null));
        }

        @Override
        public FessUserBean read(final Kryo kryo, final Input input, final Class<? extends FessUserBean> type) {
            final FessUserBean bean = new FessUserBean((FessUser) kryo.readClassAndObject(input));
            bean.setPasswordChangedAt(readLong(input));
            bean.setLoginTime(readLong(input));
            bean.setPasswordCheckTime(readLong(input));
            final String syncCheckTime = input.readString();
            if (syncCheckTime != null) {
                bean.manageLastestSyncCheckTime(LocalDateTime.parse(syncCheckTime));
            }
            final String locale = input.readString();
            if (locale != null) {
                bean.manageUserLocale(Locale.forLanguageTag(locale));
            }
            final String timeZone = input.readString();
            if (timeZone != null) {
                bean.manageUserTimeZone(TimeZone.getTimeZone(timeZone));
            }
            return bean;
        }
    }

    /**
     * Writes nothing for the user bean of {@link FessUserBean#empty()}, which wraps no user.
     */
    protected static class EmptyFessUserBeanSerializer extends Serializer<FessUserBean> {

        /**
         * Default constructor.
         */
        public EmptyFessUserBeanSerializer() {
            // nothing to do
        }

        @Override
        public void write(final Kryo kryo, final Output output, final FessUserBean bean) {
            // a marker only
        }

        @Override
        public FessUserBean read(final Kryo kryo, final Input input, final Class<? extends FessUserBean> type) {
            return FessUserBean.empty();
        }
    }

    /**
     * Writes a local user as what authorization and the session checks read: the name, the roles and groups
     * as stored, the attributes and the time of the last password change. The password hash and the
     * original password are not written.
     */
    protected static class UserSerializer extends Serializer<User> {

        /**
         * Default constructor.
         */
        public UserSerializer() {
            // nothing to do
        }

        @Override
        public void write(final Kryo kryo, final Output output, final User user) {
            output.writeString(user.getName());
            writeStrings(output, user.getRoles());
            writeStrings(output, user.getGroups());
            final Map<String, String> attributes = user.getAttributes();
            output.writeVarInt(attributes == null ? 0 : attributes.size() + 1, true);
            if (attributes != null) {
                attributes.forEach((key, value) -> {
                    output.writeString(key);
                    output.writeString(value);
                });
            }
            writeLong(output, user.getPasswordChangedAt());
        }

        @Override
        public User read(final Kryo kryo, final Input input, final Class<? extends User> type) {
            final User user = new User();
            user.setName(input.readString());
            user.setRoles(readStrings(input));
            user.setGroups(readStrings(input));
            final int size = input.readVarInt(true);
            if (size > 0) {
                checkLength(input, size - 1);
                final Map<String, String> attributes = new LinkedHashMap<>();
                for (int i = 1; i < size; i++) {
                    attributes.put(input.readString(), input.readString());
                }
                user.setAttributes(attributes);
            }
            user.setPasswordChangedAt(readLong(input));
            return user;
        }
    }

    /**
     * Writes an LDAP user as the name and the permissions it has resolved. Its environment is not written:
     * it holds the bind credentials, the password of the user after a login form and those of the
     * administrator after an SSO login, and only the search of the permissions uses it. The user is read
     * back with an empty environment, which is why the permissions must have been resolved. When they have
     * not, the attribute is left out and written by a later request.
     */
    protected static class LdapUserSerializer extends Serializer<LdapUser> {

        /**
         * Default constructor.
         */
        public LdapUserSerializer() {
            // nothing to do
        }

        @Override
        public void write(final Kryo kryo, final Output output, final LdapUser user) {
            final String[] permissions = user.getResolvedPermissions();
            if (permissions == null) {
                // getPermissions() would search the directory in the thread that closes a request
                throw new IllegalStateException("The permissions of the LDAP user are not resolved yet.");
            }
            output.writeString(user.getName());
            output.writeBoolean(user.isNameFromProvider());
            writeStrings(output, permissions);
            output.writeString(user.getPermissionState().name());
        }

        @Override
        public LdapUser read(final Kryo kryo, final Input input, final Class<? extends LdapUser> type) {
            final String name = input.readString();
            final boolean nameFromProvider = input.readBoolean();
            final String[] permissions = readStrings(input);
            return new LdapUser(name, nameFromProvider, permissions != null ? permissions : new String[0],
                    PermissionState.valueOf(input.readString()));
        }
    }
}
