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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.codelibs.core.collection.LruHashMap;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.admin.badword.AdminBadwordAction;
import org.codelibs.fess.app.web.login.LoginAction;
import org.codelibs.fess.entity.FessUser.PermissionState;
import org.codelibs.fess.ldap.LdapUser;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.LogUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.core.message.UserMessage;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.login.redirect.LoginRedirectBean;
import org.lastaflute.web.token.DoubleSubmitTokenMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SessionDataSerializerTest extends UnitFessTestCase {

    private SessionDataSerializer serializer;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        LogUtil.resetWarnOnce();
        serializer = new SessionDataSerializer();
    }

    private Map<String, Object> roundTrip(final String name, final Object value) {
        final Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put(name, value);
        final Map<String, Object> restored = serializer.fromBytes(serializer.toBytes(attributes));
        assertNotNull(restored);
        return restored;
    }

    /**
     * Searches the bytes for the text without its last character: Kryo writes an ASCII string with the
     * high bit of the last character set, so the whole text is never found even when it is there.
     */
    private boolean contains(final byte[] bytes, final String text) {
        final byte[] needle = text.substring(0, text.length() - 1).getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i + needle.length <= bytes.length; i++) {
            if (Arrays.equals(Arrays.copyOfRange(bytes, i, i + needle.length), needle)) {
                return true;
            }
        }
        return false;
    }

    private User newUser() {
        final User user = new User();
        user.setName("alice");
        user.setPassword("{bcrypt}$2a$10$hashedPasswordShouldNeverBeStored");
        user.setOriginalPassword("plain-original-password-secret");
        user.setRoles(new String[] { "cm9sZTE=", "cm9sZTI=" });
        user.setGroups(new String[] { "Z3JvdXAx" });
        final Map<String, String> attributes = new LinkedHashMap<>();
        attributes.put("department", "sales");
        user.setAttributes(attributes);
        user.setPasswordChangedAt(1234L);
        return user;
    }

    // ===================================================================================
    //                                                                        Basic types
    //                                                                        ===========
    @Test
    public void test_roundTrip_basicTypes() {
        final Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("string", "token-日本語");
        attributes.put("integer", 1280);
        attributes.put("long", 123456789012L);
        attributes.put("boolean", true);
        attributes.put("strings", new String[] { "a", "b" });
        attributes.put("list", new ArrayList<>(List.of("x", "y")));
        final Map<String, Object> nested = new ConcurrentHashMap<>();
        nested.put("k", "v");
        attributes.put("concurrent", nested);

        final Map<String, Object> restored = serializer.fromBytes(serializer.toBytes(attributes));

        assertEquals(attributes.keySet(), restored.keySet());
        assertEquals("token-日本語", restored.get("string"));
        assertEquals(1280, restored.get("integer"));
        assertEquals(123456789012L, restored.get("long"));
        assertEquals(true, restored.get("boolean"));
        assertTrue(Arrays.equals(new String[] { "a", "b" }, (String[]) restored.get("strings")));
        assertEquals(List.of("x", "y"), restored.get("list"));
        assertEquals(ConcurrentHashMap.class, restored.get("concurrent").getClass());
        assertEquals("v", ((Map<?, ?>) restored.get("concurrent")).get("k"));
    }

    @Test
    public void test_roundTrip_locale() {
        assertEquals(Locale.JAPAN, roundTrip("locale", Locale.JAPAN).get("locale"));
        final Locale serbianLatin = Locale.forLanguageTag("sr-Latn-RS");
        assertEquals(serbianLatin, roundTrip("locale", serbianLatin).get("locale"));
        assertEquals(Locale.ROOT, roundTrip("locale", Locale.ROOT).get("locale"));
    }

    @Test
    public void test_roundTrip_lruHashMap() {
        final LruHashMap<String, String[]> cache = new LruHashMap<>(3);
        cache.put("q1", new String[] { "d1" });
        cache.put("q2", new String[] { "d2", "d3" });
        cache.put("q3", new String[] { "d4" });
        cache.get("q1"); // q1 is now the youngest, q2 the eldest

        @SuppressWarnings("unchecked")
        final LruHashMap<String, String[]> restored = (LruHashMap<String, String[]>) roundTrip("cache", cache).get("cache");

        assertEquals(3, restored.getLimitSize());
        assertEquals(List.of("q2", "q3", "q1"), new ArrayList<>(restored.keySet()));
        assertTrue(Arrays.equals(new String[] { "d2", "d3" }, restored.get("q2")));
        restored.put("q4", new String[0]); // the limit still works
        assertEquals(3, restored.size());
    }

    @Test
    public void test_roundTrip_lruHashMap_notChangedBySerialization() {
        final LruHashMap<String, String[]> cache = new LruHashMap<>(3);
        cache.put("q1", new String[] { "d1" });
        cache.put("q2", new String[] { "d2" });
        serializer.toBytes(Map.of("cache", cache));
        assertEquals(List.of("q1", "q2"), new ArrayList<>(cache.keySet()));
    }

    // ===================================================================================
    //                                                                           LastaFlute
    //                                                                           ==========
    @Test
    public void test_roundTrip_messages() {
        final FessMessages messages = new FessMessages();
        messages.add(UserMessages.GLOBAL, new UserMessage("success.crawling_info_delete_all", "arg", 3, new StringBuilder("sb")));
        messages.add("name", new UserMessage("errors.required", "{labels.name}"));
        messages.add("name", UserMessage.asDirectMessage("a direct message"));

        final Object restored = roundTrip("lastaflute.message.ACTION_INFO", messages).get("lastaflute.message.ACTION_INFO");

        assertEquals(FessMessages.class, restored.getClass());
        final FessMessages result = (FessMessages) restored;
        assertFalse(result.isAccessed());
        assertEquals(List.of(UserMessages.GLOBAL, "name"), new ArrayList<>(result.toPropertySet()));
        final UserMessage first = result.silentAccessByIteratorOf(UserMessages.GLOBAL).next();
        assertEquals("success.crawling_info_delete_all", first.getMessageKey());
        assertTrue(first.isResource());
        assertEquals(List.of("arg", 3, "sb"), Arrays.asList(first.getValues()));
        final Iterator<UserMessage> name = result.silentAccessByIteratorOf("name");
        assertEquals("errors.required", name.next().getMessageKey());
        final UserMessage direct = name.next();
        assertEquals("a direct message", direct.getMessageKey());
        assertFalse(direct.isResource());
        assertFalse(messages.isAccessed()); // the original is not marked as shown by the serialization
    }

    @Test
    public void test_roundTrip_plainUserMessages() {
        final UserMessages messages = new UserMessages();
        messages.add(UserMessages.GLOBAL, new UserMessage("errors.login"));
        final Object restored = roundTrip("m", messages).get("m");
        assertEquals(UserMessages.class, restored.getClass());
        assertEquals(1, ((UserMessages) restored).size());
    }

    @Test
    public void test_roundTrip_doubleSubmitTokenMap() {
        final DoubleSubmitTokenMap tokens = new DoubleSubmitTokenMap();
        tokens.put(AdminBadwordAction.class, "token-1");
        tokens.put(String.class, "not-an-action"); // only the classes of actions come back

        final DoubleSubmitTokenMap restored =
                (DoubleSubmitTokenMap) roundTrip("lastaflute.action.TRANSACTION_TOKEN", tokens).get("lastaflute.action.TRANSACTION_TOKEN");

        assertEquals("token-1", restored.get(AdminBadwordAction.class).get());
        assertFalse(restored.get(String.class).isPresent());
    }

    @Test
    public void test_roundTrip_loginRedirectBean() {
        final LoginRedirectBean restored = (LoginRedirectBean) roundTrip("k", new LoginRedirectBean("/admin/user/?q=1")).get("k");
        assertEquals("/admin/user/?q=1", restored.getRedirectPath());
    }

    // ===================================================================================
    //                                                                                User
    //                                                                                ====
    @Test
    public void test_roundTrip_userBean_localUser() {
        final FessUserBean bean = new FessUserBean(newUser());
        bean.setPasswordChangedAt(1234L);
        bean.setLoginTime(5678L);
        bean.setPasswordCheckTime(9999L);
        final LocalDateTime syncCheck = LocalDateTime.of(2026, 11, 10, 12, 34, 56, 789000000);
        bean.manageLastestSyncCheckTime(syncCheck);
        bean.manageUserLocale(Locale.GERMANY);
        bean.manageUserTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));

        final Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("lastaflute.action.USER_BEAN.FessUserBean", bean);
        final byte[] bytes = serializer.toBytes(attributes);
        final FessUserBean restored = (FessUserBean) serializer.fromBytes(bytes).get("lastaflute.action.USER_BEAN.FessUserBean");

        assertEquals("alice", restored.getUserId());
        assertEquals(Long.valueOf(1234L), restored.getPasswordChangedAt());
        assertEquals(Long.valueOf(5678L), restored.getLoginTime());
        assertEquals(Long.valueOf(9999L), restored.getPasswordCheckTime());
        assertEquals(syncCheck, restored.getLastestSyncCheckTime().get());
        assertEquals(Locale.GERMANY, restored.getUserLocale().get());
        assertEquals("Asia/Tokyo", restored.getUserTimeZone().get().getID());
        assertEquals(User.class, restored.getFessUser().getClass());
        final User user = (User) restored.getFessUser();
        assertEquals(List.of("role1", "role2"), Arrays.asList(user.getRoleNames()));
        assertEquals(List.of("group1"), Arrays.asList(user.getGroupNames()));
        assertEquals("sales", user.getAttributes().get("department"));
        assertEquals(Long.valueOf(1234L), user.getPasswordChangedAt());

        // the credentials are not in the bytes; the user name that is, which proves the search works
        assertTrue(contains(bytes, "alice"));
        assertTrue(contains(bytes, "department"));
        assertFalse(contains(bytes, "plain-original-password-secret"));
        assertFalse(contains(bytes, "hashedPasswordShouldNeverBeStored"));
        assertFalse(contains(bytes, "$2a$10$"));
        assertNull(user.getPassword());
        assertNull(user.getOriginalPassword());
    }

    @Test
    public void test_roundTrip_userBean_withoutOptionalFields() {
        final User user = new User();
        user.setName("bob");
        final FessUserBean restored = (FessUserBean) roundTrip("u", new FessUserBean(user)).get("u");
        assertEquals("bob", restored.getUserId());
        assertNull(restored.getPasswordChangedAt());
        assertFalse(restored.getLastestSyncCheckTime().isPresent());
        assertFalse(restored.getUserLocale().isPresent());
        assertFalse(restored.getUserTimeZone().isPresent());
        final User restoredUser = (User) restored.getFessUser();
        assertNull(restoredUser.getRoles());
        assertNull(restoredUser.getAttributes());
    }

    @Test
    public void test_roundTrip_userBean_empty() {
        final FessUserBean restored = (FessUserBean) roundTrip("u", FessUserBean.empty()).get("u");
        assertEquals(Constants.EMPTY_USER_ID, restored.getUserId());
        assertNull(restored.getFessUser());
        assertEquals(FessUserBean.empty().getClass(), restored.getClass());
    }

    @Test
    public void test_roundTrip_userBean_ldapUser() {
        final Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.security.principal", "uid=carol,ou=people,dc=example,dc=com");
        env.put("java.naming.security.credentials", "ldap-bind-secret-XYZ");
        // the permissions are resolved by the directory search, which is not available here
        final LdapUser resolved = new LdapUser("carol", true, new String[] { "1carol", "Rdev", "Gstaff" }, PermissionState.PENDING);
        assertEquals(0, resolved.getEnvironment().size());

        final byte[] bytes = serializer.toBytes(Map.of("u", new FessUserBean(resolved)));
        final FessUserBean restored = (FessUserBean) serializer.fromBytes(bytes).get("u");
        final LdapUser restoredUser = (LdapUser) restored.getFessUser();

        assertEquals("carol", restoredUser.getName());
        assertTrue(restoredUser.isNameFromProvider());
        assertEquals(List.of("1carol", "Rdev", "Gstaff"), Arrays.asList(restoredUser.getResolvedPermissions()));
        assertEquals(PermissionState.PENDING, restoredUser.getPermissionState());
        assertEquals(0, restoredUser.getEnvironment().size());

        assertFalse(contains(bytes, "ldap-bind-secret-XYZ"));
        assertFalse(contains(bytes, "uid=carol,ou=people"));
        assertTrue(contains(bytes, "carol"));
    }

    @Test
    public void test_ldapUser_withEnvironmentAndResolvedPermissions_noCredentialInBytes() throws Exception {
        final Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.security.credentials", "ldap-bind-secret-XYZ");
        final LdapUser ldapUser = new LdapUser(env, "dave");
        final Field permissions = LdapUser.class.getDeclaredField("permissions");
        permissions.setAccessible(true);
        permissions.set(ldapUser, new String[] { "Rdev" });

        final byte[] bytes = serializer.toBytes(Map.of("u", new FessUserBean(ldapUser)));

        assertFalse(contains(bytes, "ldap-bind-secret-XYZ"));
        assertTrue(contains(bytes, "dave"));
        final LdapUser restored = (LdapUser) ((FessUserBean) serializer.fromBytes(bytes).get("u")).getFessUser();
        assertEquals(List.of("Rdev"), Arrays.asList(restored.getResolvedPermissions()));
        assertEquals(PermissionState.RESOLVED, restored.getPermissionState());
        assertFalse(restored.isNameFromProvider());
    }

    @Test
    public void test_ldapUser_unresolvedPermissions_isSkipped() {
        final LdapUser ldapUser = new LdapUser(new Hashtable<>(), "erin");
        final Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("lastaflute.action.USER_BEAN.FessUserBean", new FessUserBean(ldapUser));
        attributes.put("other", "kept");

        final Map<String, Object> restored = serializer.fromBytes(serializer.toBytes(attributes));

        assertEquals(List.of("other"), new ArrayList<>(restored.keySet()));
    }

    // ===================================================================================
    //                                                                           Exclusion
    //                                                                           =========
    @Test
    public void test_invalidOldPassword_isNeverStored() throws Exception {
        final Field field = LoginAction.class.getDeclaredField("INVALID_OLD_PASSWORD");
        field.setAccessible(true);
        final String name = (String) field.get(null);
        assertEquals(name, SessionDataSerializer.INVALID_OLD_PASSWORD); // follows the name used by LoginAction

        final Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put(name, "admin-password-in-session-secret");
        attributes.put("keep", "kept-value");
        final byte[] bytes = serializer.toBytes(attributes);

        assertFalse(contains(bytes, "admin-password-in-session-secret"));
        assertTrue(contains(bytes, "kept-value"));
        assertEquals(List.of("keep"), new ArrayList<>(serializer.fromBytes(bytes).keySet()));
        assertTrue(SessionDataSerializer.isExcluded(name));
    }

    @Test
    public void test_skippedNames_areReportedAndDigestInputIsStable() {
        final Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put(SessionDataSerializer.INVALID_OLD_PASSWORD, "secret");
        attributes.put("unregistered", new AtomicInteger(3));
        attributes.put("keep", "kept-value");
        final java.util.Set<String> skipped = new java.util.TreeSet<>();
        final byte[] bytes = serializer.toBytes(attributes, skipped);

        assertEquals(java.util.Set.of(SessionDataSerializer.INVALID_OLD_PASSWORD, "unregistered"), skipped);
        // the same bytes as the one-argument form, and unchanged by the value of a skipped attribute
        assertTrue(java.util.Arrays.equals(bytes, serializer.toBytes(attributes)));
        attributes.put(SessionDataSerializer.INVALID_OLD_PASSWORD, "another");
        assertTrue(java.util.Arrays.equals(bytes, serializer.toBytes(attributes, null)));
    }

    // ===================================================================================
    //                                                                     Skipped attributes
    //                                                                     ==================
    @Test
    public void test_unregisteredAttribute_isSkipped() {
        final Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("before", "b");
        attributes.put("unregistered", new AtomicInteger(3));
        attributes.put("nested", new ArrayList<>(List.of(new AtomicInteger(1))));
        attributes.put("after", "a");

        final Map<String, Object> restored = serializer.fromBytes(serializer.toBytes(attributes));

        assertEquals(List.of("before", "after"), new ArrayList<>(restored.keySet()));
    }

    @Test
    public void test_unregisteredFessUser_skipsTheUserBeanOnly() {
        final FessUserBean bean = new FessUserBean(new org.codelibs.fess.entity.FessUser() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getName() {
                return "sso";
            }

            @Override
            public String[] getRoleNames() {
                return new String[0];
            }

            @Override
            public String[] getGroupNames() {
                return new String[0];
            }

            @Override
            public String[] getPermissions() {
                return new String[0];
            }
        });
        final Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("bean", bean);
        attributes.put("locale", Locale.FRANCE);

        final Map<String, Object> restored = serializer.fromBytes(serializer.toBytes(attributes));

        assertEquals(List.of("locale"), new ArrayList<>(restored.keySet()));
    }

    @Test
    public void test_failingSerializer_isSkipped() {
        serializer.register(AtomicInteger.class, new Serializer<AtomicInteger>() {
            @Override
            public void write(final Kryo kryo, final Output output, final AtomicInteger object) {
                output.writeInt(1);
                throw new IllegalStateException("cannot write");
            }

            @Override
            public AtomicInteger read(final Kryo kryo, final Input input, final Class<? extends AtomicInteger> type) {
                return new AtomicInteger(input.readInt());
            }
        });
        final Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("bad", new AtomicInteger(1));
        attributes.put("good", "g");

        final Map<String, Object> restored = serializer.fromBytes(serializer.toBytes(attributes));

        assertEquals(List.of("good"), new ArrayList<>(restored.keySet()));
    }

    @Test
    public void test_failingReader_dropsTheAttributeOnly() {
        final boolean[] failOnRead = { false };
        serializer.register(AtomicInteger.class, new Serializer<AtomicInteger>() {
            @Override
            public void write(final Kryo kryo, final Output output, final AtomicInteger object) {
                output.writeInt(object.get());
            }

            @Override
            public AtomicInteger read(final Kryo kryo, final Input input, final Class<? extends AtomicInteger> type) {
                if (failOnRead[0]) {
                    throw new IllegalStateException("cannot read");
                }
                return new AtomicInteger(input.readInt());
            }
        });
        final Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("a", "1");
        attributes.put("bad", new AtomicInteger(5));
        attributes.put("c", "3");
        final byte[] bytes = serializer.toBytes(attributes);

        failOnRead[0] = true;
        final Map<String, Object> restored = serializer.fromBytes(bytes);

        assertEquals(List.of("a", "c"), new ArrayList<>(restored.keySet()));
    }

    @Test
    public void test_nullAndEmpty() {
        assertNull(serializer.fromBytes(null));
        assertNull(serializer.fromBytes(new byte[0]));
        assertTrue(serializer.fromBytes(serializer.toBytes(null)).isEmpty());
        assertTrue(serializer.fromBytes(serializer.toBytes(new LinkedHashMap<>())).isEmpty());
        final Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("nullValue", null);
        assertTrue(serializer.fromBytes(serializer.toBytes(attributes)).isEmpty());
    }

    @Test
    public void test_corruptBytes_areIgnored() {
        final byte[] bytes = serializer.toBytes(Map.of("k", "value"));
        assertNull(serializer.fromBytes(Arrays.copyOf(bytes, bytes.length - 3)));
        assertNull(serializer.fromBytes(new byte[] { 1, 2, 3 }));
    }

    // ===================================================================================
    //                                                                      Format version
    //                                                                      ==============
    @Test
    public void test_formatVersion_mismatch_returnsNull() {
        final SessionDataSerializer other = new SessionDataSerializer();
        other.register(AtomicInteger.class, new AtomicIntegerSerializer());
        assertFalse(serializer.getFormatVersion().equals(other.getFormatVersion()));

        final byte[] bytes = serializer.toBytes(Map.of("k", "v"));

        assertNull(other.fromBytes(bytes));
        assertEquals("v", serializer.fromBytes(bytes).get("k"));
    }

    @Test
    public void test_formatVersion_followsTheNumber() {
        final byte[] bytes = serializer.toBytes(Map.of("k", "v"));
        bytes[0] = (byte) (SessionDataSerializer.FORMAT_NUMBER + 1);
        assertNull(serializer.fromBytes(bytes));
        assertTrue(serializer.getFormatVersion().startsWith(SessionDataSerializer.FORMAT_NUMBER + "-"));
    }

    @Test
    public void test_pluginIds_followClassNameOrder() {
        final SessionDataSerializer first = new SessionDataSerializer();
        first.register(AtomicInteger.class, new AtomicIntegerSerializer());
        first.register(StringBuilder.class, new StringBuilderSerializer());
        final SessionDataSerializer second = new SessionDataSerializer();
        second.register(StringBuilder.class, new StringBuilderSerializer());
        second.register(AtomicInteger.class, new AtomicIntegerSerializer());

        assertEquals(first.getFormatVersion(), second.getFormatVersion());
        final Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("a", new AtomicInteger(7));
        attributes.put("b", new StringBuilder("sb"));
        final Map<String, Object> restored = second.fromBytes(first.toBytes(attributes));
        assertEquals(7, ((AtomicInteger) restored.get("a")).get());
        assertEquals("sb", restored.get("b").toString());
        assertEquals(first.getFormatVersion(), first.getFormatVersion());
    }

    @Test
    public void test_register_afterFixed_isRefused() {
        serializer.getFormatVersion();
        try {
            serializer.register(AtomicInteger.class, new AtomicIntegerSerializer());
            fail();
        } catch (final IllegalStateException e) {
            // expected
        }
        final SessionDataSerializer other = new SessionDataSerializer();
        other.toBytes(Map.of("k", "v"));
        try {
            other.register(AtomicInteger.class, new AtomicIntegerSerializer());
            fail();
        } catch (final IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void test_register_invalid() {
        try {
            serializer.register(null, new AtomicIntegerSerializer());
            fail();
        } catch (final IllegalArgumentException e) {
            // expected
        }
        try {
            serializer.register(AtomicInteger.class, null);
            fail();
        } catch (final IllegalArgumentException e) {
            // expected
        }
        try {
            serializer.register(String.class, new AtomicIntegerSerializer());
            fail();
        } catch (final IllegalArgumentException e) {
            // expected: registered already
        }
        serializer.register(AtomicInteger.class, new AtomicIntegerSerializer());
        try {
            serializer.register(AtomicInteger.class, new AtomicIntegerSerializer());
            fail();
        } catch (final IllegalArgumentException e) {
            // expected: registered twice
        }
    }

    @Test
    public void test_register_pluginClass_roundTrip() {
        serializer.register(AtomicInteger.class, new AtomicIntegerSerializer());
        assertEquals(42, ((AtomicInteger) roundTrip("counter", new AtomicInteger(42)).get("counter")).get());
    }

    @Test
    public void test_toBytes_threadSafe() throws Exception {
        final byte[] expected = serializer.toBytes(Map.of("k", "v"));
        final List<Throwable> errors = new ArrayList<>();
        final List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final Thread thread = new Thread(() -> {
                try {
                    for (int j = 0; j < 50; j++) {
                        assertTrue(Arrays.equals(expected, serializer.toBytes(Map.of("k", "v"))));
                        assertEquals("v", serializer.fromBytes(expected).get("k"));
                    }
                } catch (final Throwable t) {
                    synchronized (errors) {
                        errors.add(t);
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (final Thread thread : threads) {
            thread.join();
        }
        assertTrue(errors.toString(), errors.isEmpty());
    }

    // ===================================================================================
    //                                                               Untrusted stored bytes
    //                                                               ======================
    private Kryo hostileKryo() {
        serializer.getFormatVersion();
        return serializer.createKryo();
    }

    /** The bytes of one attribute: the class, then what the serializer of the class reads. */
    private byte[] blob(final Class<?> type, final Consumer<Output> body) {
        final Output output = new Output(64, -1);
        hostileKryo().writeClass(output, type);
        body.accept(output);
        return output.toBytes();
    }

    /** The bytes of a session with the attributes as they are given, in the layout of the serializer. */
    private byte[] container(final Map<String, byte[]> blobs) {
        serializer.getFormatVersion();
        final Output output = new Output(64, -1);
        output.writeVarInt(SessionDataSerializer.FORMAT_NUMBER, true);
        output.writeLong(serializer.fingerprint);
        output.writeVarInt(blobs.size(), true);
        blobs.forEach((name, bytes) -> {
            output.writeString(name);
            output.writeVarInt(bytes.length, true);
            output.writeBytes(bytes);
        });
        return output.toBytes();
    }

    private Map<String, byte[]> hostile(final byte[] bad) {
        final Map<String, byte[]> blobs = new LinkedHashMap<>();
        blobs.put("bad", bad);
        blobs.put("good", blob(String.class, o -> o.writeString("ok")));
        return blobs;
    }

    /** Asserts that the attribute is dropped, the others are read, and that nothing was allocated from the length. */
    private void assertDropped(final String what, final byte[] bad) {
        final Map<String, Object> restored = serializer.fromBytes(container(hostile(bad)));
        if (restored == null || !"ok".equals(restored.get("good")) || restored.containsKey("bad")) {
            fail(what + ": " + restored);
        }
    }

    private Map<String, Consumer<Output>> countBodies(final int n) {
        final Map<String, Consumer<Output>> bodies = new LinkedHashMap<>();
        // a collection: the flag of the type and the count + 1
        final Consumer<Output> collection = o -> o.writeVarIntFlag(false, n, true);
        final Consumer<Output> counted = o -> o.writeVarInt(n, true);
        bodies.put("ArrayList", collection);
        bodies.put("LinkedList", collection);
        bodies.put("HashSet", collection);
        bodies.put("LinkedHashSet", collection);
        bodies.put("TreeSet", collection);
        bodies.put("Arrays.asList", collection);
        bodies.put("HashMap", counted);
        bodies.put("LinkedHashMap", counted);
        bodies.put("ConcurrentHashMap", counted);
        bodies.put("TreeMap", counted);
        bodies.put("String[]", counted);
        bodies.put("String", o -> o.writeVarIntFlag(true, n, true));
        bodies.put("int[]", counted);
        bodies.put("long[]", counted);
        bodies.put("double[]", counted);
        bodies.put("float[]", counted);
        bodies.put("short[]", counted);
        bodies.put("char[]", counted);
        bodies.put("boolean[]", counted);
        bodies.put("byte[]", counted);
        bodies.put("BigInteger", counted);
        bodies.put("LruHashMap.size", o -> {
            o.writeVarInt(3, true);
            o.writeVarInt(n, true);
        });
        bodies.put("UserMessages.properties", counted);
        bodies.put("UserMessages.messages", o -> {
            o.writeVarInt(1, true);
            o.writeString("p");
            o.writeVarInt(n, true);
        });
        bodies.put("UserMessages.values", o -> {
            o.writeVarInt(1, true);
            o.writeString("p");
            o.writeVarInt(1, true);
            o.writeString("key");
            o.writeBoolean(true);
            o.writeVarInt(n, true);
        });
        bodies.put("DoubleSubmitTokenMap", counted);
        bodies.put("User.roles", o -> o.writeString("alice"));
        bodies.put("User.groups", o -> o.writeString("alice"));
        bodies.put("User.attributes", o -> o.writeString("alice"));
        bodies.put("LdapUser.permissions", o -> {
            o.writeString("alice");
            o.writeBoolean(false);
        });
        return bodies;
    }

    private Class<?> typeOf(final String body) {
        return switch (body.split("\\.")[0]) {
        case "ArrayList" -> ArrayList.class;
        case "LinkedList" -> java.util.LinkedList.class;
        case "HashSet" -> java.util.HashSet.class;
        case "LinkedHashSet" -> java.util.LinkedHashSet.class;
        case "TreeSet" -> java.util.TreeSet.class;
        case "Arrays" -> Arrays.asList().getClass();
        case "HashMap" -> java.util.HashMap.class;
        case "LinkedHashMap" -> LinkedHashMap.class;
        case "ConcurrentHashMap" -> ConcurrentHashMap.class;
        case "TreeMap" -> java.util.TreeMap.class;
        case "String[]" -> String[].class;
        case "String" -> String.class;
        case "int[]" -> int[].class;
        case "long[]" -> long[].class;
        case "double[]" -> double[].class;
        case "float[]" -> float[].class;
        case "short[]" -> short[].class;
        case "char[]" -> char[].class;
        case "boolean[]" -> boolean[].class;
        case "byte[]" -> byte[].class;
        case "BigInteger" -> java.math.BigInteger.class;
        case "LruHashMap" -> LruHashMap.class;
        case "UserMessages" -> UserMessages.class;
        case "DoubleSubmitTokenMap" -> DoubleSubmitTokenMap.class;
        case "User" -> User.class;
        case "LdapUser" -> LdapUser.class;
        default -> throw new IllegalArgumentException(body);
        };
    }

    @Test
    public void test_hostileCounts_areDroppedWithoutAllocating() {
        for (final int n : new int[] { Integer.MAX_VALUE, 1_000_000_000, 100_000, 1025 }) {
            countBodies(n).forEach((name, body) -> {
                final Consumer<Output> full;
                if (name.startsWith("User.")) {
                    // the name, the count of the field, then nothing: roles, groups, attributes in turn
                    final int field = List.of("User.roles", "User.groups", "User.attributes").indexOf(name);
                    full = o -> {
                        o.writeString("alice");
                        for (int i = 0; i < field; i++) {
                            o.writeVarInt(0, true);
                        }
                        o.writeVarInt(n, true);
                    };
                } else if (name.equals("LdapUser.permissions")) {
                    full = o -> {
                        o.writeString("alice");
                        o.writeBoolean(false);
                        o.writeVarInt(n, true);
                    };
                } else {
                    full = body;
                }
                assertDropped(name + " " + n, blob(typeOf(name), full));
            });
        }
    }

    @Test
    public void test_hostileCounts_inTheMessagesOfFessMessages() {
        assertDropped("FessMessages", blob(FessMessages.class, o -> o.writeVarInt(Integer.MAX_VALUE, true)));
    }

    @Test
    public void test_hostileCount_aboveTheAbsoluteCap_evenWithBytesBehindIt() {
        // 2 MiB of bytes, so the count is no larger than what is left, but it is larger than the cap
        final byte[] bad = blob(ArrayList.class, o -> {
            o.writeVarIntFlag(false, SessionDataSerializer.MAX_ELEMENTS + 2, true);
            o.writeBytes(new byte[2 * 1024 * 1024]);
        });
        assertDropped("above the cap", bad);
    }

    @Test
    public void test_listOfNulls_isStillRead() {
        final List<Object> nulls = new ArrayList<>(java.util.Collections.nCopies(500, null));
        final List<?> restored = (List<?>) roundTrip("nulls", nulls).get("nulls");
        assertEquals(500, restored.size());
    }

    @Test
    public void test_lruHashMap_limitIsLowered_andInvalidLimitIsDropped() {
        final Map<String, Object> restored = serializer.fromBytes(container(hostile(blob(LruHashMap.class, o -> {
            o.writeVarInt(1_000_000_000, true);
            o.writeVarInt(0, true);
        }))));
        assertEquals(SessionDataSerializer.MAX_LRU_LIMIT, ((LruHashMap<?, ?>) restored.get("bad")).getLimitSize());

        assertDropped("zero limit", blob(LruHashMap.class, o -> {
            o.writeVarInt(0, true);
            o.writeVarInt(0, true);
        }));
    }

    @Test
    public void test_framing_hostileLengths_ignoreTheSession() {
        final byte[] valid = container(hostile(blob(String.class, o -> o.writeString("x"))));
        final int header = 1 + Long.BYTES;

        // the count of the attributes
        final Output count = new Output(64, -1);
        count.writeVarInt(SessionDataSerializer.FORMAT_NUMBER, true);
        count.writeLong(serializer.fingerprint);
        count.writeVarInt(Integer.MAX_VALUE, true);
        assertNull(serializer.fromBytes(count.toBytes()));

        // the length of the bytes of an attribute
        final Output length = new Output(64, -1);
        length.writeVarInt(SessionDataSerializer.FORMAT_NUMBER, true);
        length.writeLong(serializer.fingerprint);
        length.writeVarInt(1, true);
        length.writeString("bad");
        length.writeVarInt(Integer.MAX_VALUE, true);
        length.writeBytes(new byte[8]);
        assertNull(serializer.fromBytes(length.toBytes()));

        // the length of a name
        final Output name = new Output(64, -1);
        name.writeVarInt(SessionDataSerializer.FORMAT_NUMBER, true);
        name.writeLong(serializer.fingerprint);
        name.writeVarInt(1, true);
        name.writeVarIntFlag(true, Integer.MAX_VALUE, true);
        assertNull(serializer.fromBytes(name.toBytes()));

        assertNotNull(serializer.fromBytes(valid));
        assertTrue(valid.length > header);
    }

    @Test
    public void test_payloadSize_isCapped() {
        final byte[] tooLarge = new byte[SessionDataSerializer.MAX_PAYLOAD_BYTES + 1];
        assertNull(serializer.fromBytes(tooLarge));

        final Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("huge", "a".repeat(SessionDataSerializer.MAX_PAYLOAD_BYTES + 1));
        attributes.put("small", "ok");
        final java.util.Set<String> skipped = new java.util.TreeSet<>();
        final byte[] bytes = serializer.toBytes(attributes, skipped);

        assertEquals(java.util.Set.of("huge"), skipped);
        assertTrue(bytes.length <= SessionDataSerializer.MAX_PAYLOAD_BYTES);
        assertEquals("ok", serializer.fromBytes(bytes).get("small"));
    }

    @Test
    public void test_payloadSize_sumOfAttributesIsCapped() {
        final Map<String, Object> attributes = new LinkedHashMap<>();
        for (int i = 0; i < 6; i++) {
            attributes.put("a" + i, new byte[SessionDataSerializer.MAX_PAYLOAD_BYTES / 4]);
        }
        final java.util.Set<String> skipped = new java.util.TreeSet<>();
        final byte[] bytes = serializer.toBytes(attributes, skipped);

        assertFalse(skipped.isEmpty());
        assertTrue(bytes.length <= SessionDataSerializer.MAX_PAYLOAD_BYTES);
        assertEquals(attributes.size() - skipped.size(), serializer.fromBytes(bytes).size());
    }

    @Test
    public void test_nesting_isLimited_whenWrittenAndRead() {
        Object nested = new ArrayList<>();
        for (int i = 0; i < SessionDataSerializer.MAX_DEPTH * 2; i++) {
            final List<Object> outer = new ArrayList<>();
            outer.add(nested);
            nested = outer;
        }
        final java.util.Set<String> skipped = new java.util.TreeSet<>();
        serializer.toBytes(Map.of("deep", nested), skipped);
        assertEquals(java.util.Set.of("deep"), skipped);

        // written by someone else, 100000 levels deep: a stack overflow is not a RuntimeException
        final Kryo kryo = hostileKryo();
        assertDropped("nesting", blob(ArrayList.class, o -> {
            for (int i = 0; i < 100_000; i++) {
                o.writeVarIntFlag(false, 2, true);
                kryo.writeClass(o, ArrayList.class);
            }
            o.writeVarIntFlag(false, 1, true);
        }));
    }

    @Test
    public void test_pluginSerializer_canCheckItsLengths() {
        final Output output = new Output(16, -1);
        output.writeVarInt(5, true);
        output.writeBytes(new byte[3]);
        final Input input = new Input(output.toBytes());
        final int length = input.readVarInt(true);
        try {
            SessionDataSerializer.checkLength(input, length);
            fail();
        } catch (final KryoException e) {
            // 5 elements need 5 bytes, 3 are left
        }
        SessionDataSerializer.checkLength(input, 3);
        try {
            SessionDataSerializer.checkLength(input, -1);
            fail();
        } catch (final KryoException e) {
            // negative
        }
    }

    private Map<String, Object> richAttributes() {
        final Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("string", "token-日本語");
        attributes.put("strings", new String[] { "a", "b", "c" });
        attributes.put("ints", new int[] { 1, 2, 3, 1 << 20 });
        attributes.put("list", new ArrayList<>(List.of("x", "y", 1, 2L)));
        final LruHashMap<String, String[]> cache = new LruHashMap<>(5);
        cache.put("q1", new String[] { "d1", "d2" });
        attributes.put("cache", cache);
        final FessMessages messages = new FessMessages();
        messages.add("name", new UserMessage("errors.required", "{labels.name}"));
        attributes.put("messages", messages);
        attributes.put("user", new FessUserBean(newUser()));
        attributes.put("ldap", new LdapUser("bob", true, new String[] { "1bob", "Rdev" }, PermissionState.PENDING));
        return attributes;
    }

    @Test
    public void test_truncatedBytes_neverFail() {
        final byte[] bytes = serializer.toBytes(richAttributes());
        assertEquals(richAttributes().size(), serializer.fromBytes(bytes).size());
        for (int length = 0; length < bytes.length; length++) {
            serializer.fromBytes(Arrays.copyOf(bytes, length));
        }
    }

    @Test
    public void test_mutatedBytes_neverFail() {
        final byte[] bytes = serializer.toBytes(richAttributes());
        final java.util.Random random = new java.util.Random(20261111L);
        for (int i = 0; i < 20_000; i++) {
            final byte[] mutated = bytes.clone();
            final int changes = 1 + random.nextInt(4);
            for (int j = 0; j < changes; j++) {
                // keep the header, which is read before anything else, intact most of the time
                final int index = random.nextInt(8) == 0 ? random.nextInt(mutated.length) : 9 + random.nextInt(mutated.length - 9);
                mutated[index] = (byte) (random.nextInt(4) == 0 ? 0xff : random.nextInt(256));
            }
            serializer.fromBytes(mutated);
        }
    }

    @Test
    public void test_garbageBehindAValidHeader_neverFails() {
        serializer.getFormatVersion();
        final java.util.Random random = new java.util.Random(20261112L);
        for (int i = 0; i < 20_000; i++) {
            final Output output = new Output(64, -1);
            output.writeVarInt(SessionDataSerializer.FORMAT_NUMBER, true);
            output.writeLong(serializer.fingerprint);
            output.writeVarInt(1 + random.nextInt(3), true);
            final byte[] garbage = new byte[random.nextInt(64)];
            random.nextBytes(garbage);
            if (garbage.length > 0 && random.nextBoolean()) {
                // a name and a plausible length make the garbage reach the serializers
                output.writeString("n");
                output.writeVarInt(garbage.length, true);
            }
            output.writeBytes(garbage);
            serializer.fromBytes(output.toBytes());
        }
    }

    private static class AtomicIntegerSerializer extends Serializer<AtomicInteger> {
        @Override
        public void write(final Kryo kryo, final Output output, final AtomicInteger object) {
            output.writeInt(object.get());
        }

        @Override
        public AtomicInteger read(final Kryo kryo, final Input input, final Class<? extends AtomicInteger> type) {
            return new AtomicInteger(input.readInt());
        }
    }

    private static class StringBuilderSerializer extends Serializer<StringBuilder> {
        @Override
        public void write(final Kryo kryo, final Output output, final StringBuilder object) {
            output.writeString(object.toString());
        }

        @Override
        public StringBuilder read(final Kryo kryo, final Input input, final Class<? extends StringBuilder> type) {
            return new StringBuilder(input.readString());
        }
    }
}
