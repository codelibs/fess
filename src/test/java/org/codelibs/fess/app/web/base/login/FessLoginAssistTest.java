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
package org.codelibs.fess.app.web.base.login;

import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.PasswordHashHelper;
import org.codelibs.fess.ldap.LdapUser;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.cbean.UserCB;
import org.codelibs.fess.opensearch.user.exbhv.UserBhv;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.core.time.TimeManager;
import org.lastaflute.web.login.TypicalLoginAssist;

/**
 * Unit tests for {@link FessLoginAssist}, focusing on the BCrypt-aware local
 * authentication path and lazy password re-hashing.
 */
public class FessLoginAssistTest extends UnitFessTestCase {

    private FessLoginAssist loginAssist;

    private PasswordHashHelper passwordHashHelper;

    private TestFessConfig testConfig;

    private CountingPasswordHashHelper countingPm;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        testConfig = new TestFessConfig();
        ComponentUtil.setFessConfig(testConfig);

        passwordHashHelper = new PasswordHashHelper();
        countingPm = new CountingPasswordHashHelper(passwordHashHelper);
        // Register PasswordHashHelper and (by default) a no-op UserService.
        ComponentUtil.register(countingPm, "passwordHashHelper");
        ComponentUtil.register(new RecordingUserService(), UserService.class.getCanonicalName());

        loginAssist = new FessLoginAssist();
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    // ---------------------------------------------------------------------
    // encryptPassword
    // ---------------------------------------------------------------------

    @Test
    public void test_encryptPassword_delegatesToPasswordHashHelper() {
        final String encoded = loginAssist.encryptPassword("secret");
        assertNotNull(encoded);
        assertTrue(encoded.startsWith("{bcrypt}$2a$"));
        assertTrue(passwordHashHelper.matches("secret", encoded));
    }

    // ---------------------------------------------------------------------
    // doAuthenticateLocal
    // ---------------------------------------------------------------------

    @Test
    public void test_doAuthenticateLocal_validPassword_returnsUser() {
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword(passwordHashHelper.encode("wonderland"));
        installUserBhv(stored);

        final OptionalEntity<FessUser> result = loginAssist.doAuthenticateLocal("alice", "wonderland");
        assertTrue(result.isPresent());
        assertEquals("alice", ((User) result.get()).getName());
    }

    @Test
    public void test_doAuthenticateLocal_wrongPassword_returnsEmpty_noRehash() {
        final String originalHash = passwordHashHelper.encode("wonderland");
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword(originalHash);
        installUserBhv(stored);
        final RecordingUserService rec = installUserService();

        final OptionalEntity<FessUser> result = loginAssist.doAuthenticateLocal("alice", "WRONG");
        assertFalse(result.isPresent());
        assertEquals(0, rec.updateCalls.get());
        assertEquals(originalHash, stored.getPassword());
    }

    @Test
    public void test_doAuthenticateLocal_unknownUser_consumesDummyBcrypt() {
        installUserBhv(null);
        final int before = countingPm.paddingCalls.get();

        final OptionalEntity<FessUser> result = loginAssist.doAuthenticateLocal("ghost", "password");
        assertFalse(result.isPresent());

        // Exactly one applyTimingPadding() call should have occurred.
        assertEquals(1, countingPm.paddingCalls.get() - before);
    }

    // ---------------------------------------------------------------------
    // doAuthenticateLocal — timing-padding matrix (Blocking-1)
    // ---------------------------------------------------------------------

    @Test
    public void test_timing_failWithLegacyHash_paysBcryptCost() throws Exception {
        // Legacy (unprefixed) hex stored + wrong password -> matches() returns
        // false WITHOUT paying a BCrypt cost, so padding must be applied.
        testConfig.digestAlgorithm = "sha256";
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword(sha256Hex("hunter2"));
        installUserBhv(stored);
        final int before = countingPm.paddingCalls.get();

        final OptionalEntity<FessUser> result = loginAssist.doAuthenticateLocal("alice", "WRONG");

        assertFalse(result.isPresent());
        assertEquals("legacy-hash failure must pay exactly one padding bcrypt cost", 1, countingPm.paddingCalls.get() - before);
    }

    @Test
    public void test_timing_failWithBcryptHash_noExtraPadding() {
        // {bcrypt} stored + wrong password -> matches() already paid a BCrypt
        // cost. Padding again would double the cost and leak info.
        final String bcryptHash = passwordHashHelper.encode("wonderland");
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword(bcryptHash);
        installUserBhv(stored);
        final int before = countingPm.paddingCalls.get();

        final OptionalEntity<FessUser> result = loginAssist.doAuthenticateLocal("alice", "WRONG");

        assertFalse(result.isPresent());
        assertEquals("bcrypt-hash failure must NOT pay an extra padding cost", 0, countingPm.paddingCalls.get() - before);
    }

    @Test
    public void test_timing_unknownUser_paysPadding() {
        installUserBhv(null);
        final int before = countingPm.paddingCalls.get();

        final OptionalEntity<FessUser> result = loginAssist.doAuthenticateLocal("ghost", "password");

        assertFalse(result.isPresent());
        assertEquals("unknown user must pay exactly one padding bcrypt cost", 1, countingPm.paddingCalls.get() - before);
    }

    @Test
    public void test_timing_unknownPrefix_paysPadding() {
        // matches() returns false without paying BCrypt cost (unknown id),
        // so padding must still be applied.
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword("{unknown}whatever");
        installUserBhv(stored);
        final int before = countingPm.paddingCalls.get();

        final OptionalEntity<FessUser> result = loginAssist.doAuthenticateLocal("alice", "WRONG");

        assertFalse(result.isPresent());
        assertEquals("unknown-prefix failure must pay exactly one padding bcrypt cost", 1, countingPm.paddingCalls.get() - before);
    }

    @Test
    public void test_timing_nullStoredPassword_paysPadding() {
        // Defensive: stored password is null (shouldn't happen in practice).
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword(null);
        installUserBhv(stored);
        final int before = countingPm.paddingCalls.get();

        final OptionalEntity<FessUser> result = loginAssist.doAuthenticateLocal("alice", "WRONG");

        assertFalse(result.isPresent());
        assertEquals("null stored-password failure must pay exactly one padding bcrypt cost", 1, countingPm.paddingCalls.get() - before);
    }

    @Test
    public void test_doAuthenticateLocal_legacyHash_triggersRehash() throws Exception {
        testConfig.digestAlgorithm = "sha256";
        final String legacyHash = sha256Hex("hunter2");
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword(legacyHash);
        installUserBhv(stored);
        final RecordingUserService rec = installUserService();

        final OptionalEntity<FessUser> result = loginAssist.doAuthenticateLocal("alice", "hunter2");
        assertTrue(result.isPresent());
        assertEquals(1, rec.updateCalls.get());
        assertEquals("alice", rec.lastUsername.get());
        assertNotNull(rec.lastHash.get());
        assertTrue(rec.lastHash.get().startsWith("{bcrypt}$2a$"));
    }

    @Test
    public void test_doAuthenticateLocal_rehashFailure_stillSucceeds() throws Exception {
        testConfig.digestAlgorithm = "sha256";
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword(sha256Hex("hunter2"));
        installUserBhv(stored);

        // UserService that reports failure (false).
        final RecordingUserService rec = new RecordingUserService() {
            @Override
            public boolean updateStoredPasswordHash(final String username, final String expectedCurrentHash,
                    final String newEncodedPassword) {
                super.updateStoredPasswordHash(username, expectedCurrentHash, newEncodedPassword);
                return false;
            }
        };
        ComponentUtil.register(rec, UserService.class.getCanonicalName());

        final OptionalEntity<FessUser> result = loginAssist.doAuthenticateLocal("alice", "hunter2");
        assertTrue(result.isPresent());
        assertEquals(1, rec.updateCalls.get());
    }

    @Test
    public void test_doAuthenticateLocal_rehashException_stillSucceeds() throws Exception {
        testConfig.digestAlgorithm = "sha256";
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword(sha256Hex("hunter2"));
        installUserBhv(stored);

        final RecordingUserService rec = new RecordingUserService() {
            @Override
            public boolean updateStoredPasswordHash(final String username, final String expectedCurrentHash,
                    final String newEncodedPassword) {
                super.updateStoredPasswordHash(username, expectedCurrentHash, newEncodedPassword);
                throw new RuntimeException("simulated failure");
            }
        };
        ComponentUtil.register(rec, UserService.class.getCanonicalName());

        final OptionalEntity<FessUser> result = loginAssist.doAuthenticateLocal("alice", "hunter2");
        assertTrue(result.isPresent()); // login must not fail because of rehash
        assertEquals(1, rec.updateCalls.get());
    }

    @Test
    public void test_doAuthenticateLocal_upgradeDisabled_noRehash() throws Exception {
        testConfig.digestAlgorithm = "sha256";
        testConfig.upgradeEnabled = false;
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword(sha256Hex("hunter2"));
        installUserBhv(stored);
        final RecordingUserService rec = installUserService();

        final OptionalEntity<FessUser> result = loginAssist.doAuthenticateLocal("alice", "hunter2");
        assertTrue(result.isPresent());
        assertEquals(0, rec.updateCalls.get());
    }

    @Test
    public void test_doAuthenticateLocal_newFormatSameCost_noRehash() {
        final String bcryptHash = passwordHashHelper.encode("wonderland");
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword(bcryptHash);
        installUserBhv(stored);
        final RecordingUserService rec = installUserService();

        final OptionalEntity<FessUser> result = loginAssist.doAuthenticateLocal("alice", "wonderland");
        assertTrue(result.isPresent());
        assertEquals(0, rec.updateCalls.get());
    }

    // ---------------------------------------------------------------------
    // lazyUpgradePassword
    // ---------------------------------------------------------------------

    @Test
    public void test_lazyUpgrade_passesExpectedCurrentHashToThreeArgUpdate() throws Exception {
        testConfig.digestAlgorithm = "sha256";
        final String legacyHash = sha256Hex("hunter2");
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword(legacyHash);
        installUserBhv(stored);
        final RecordingUserService rec = installUserService();

        final OptionalEntity<FessUser> result = loginAssist.doAuthenticateLocal("alice", "hunter2");
        assertTrue(result.isPresent());
        assertEquals(1, rec.updateCalls.get());
        // The expected-current-hash passed through must be exactly the stored legacy
        // hash observed at match time (CAS guard for concurrent password changes).
        assertEquals(legacyHash, rec.lastExpectedHash.get());
        assertTrue(rec.lastHash.get().startsWith("{bcrypt}$2a$"));
    }

    @Test
    public void test_lazyUpgrade_upgradeDisabled_doesNotInvokeUpdateAtAll() throws Exception {
        testConfig.digestAlgorithm = "sha256";
        testConfig.upgradeEnabled = false;
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword(sha256Hex("hunter2"));
        installUserBhv(stored);
        final RecordingUserService rec = installUserService();

        final OptionalEntity<FessUser> result = loginAssist.doAuthenticateLocal("alice", "hunter2");
        assertTrue(result.isPresent());
        // Neither the 2-arg nor the 3-arg overload should be called.
        assertEquals(0, rec.updateCalls.get());
        assertNull(rec.lastExpectedHash.get());
        assertNull(rec.lastHash.get());
    }

    // ---------------------------------------------------------------------
    // needsLoginSessionSyncCheck
    // ---------------------------------------------------------------------

    @Test
    public void test_needsSyncCheck_samlUser_staleCheckTime_skipsCheck() {
        final FessUserBean bean = new FessUserBean(newSamlUser("saml-admin"));
        assertFalse(loginAssist.needsLoginSessionSyncCheck(bean, OptionalThing.of(STALE_CHECK_DT), NOW));
    }

    @Test
    public void test_needsSyncCheck_ldapUser_staleCheckTime_skipsCheck() {
        final FessUserBean bean = new FessUserBean(new LdapUser(new Hashtable<>(), "ldap-admin"));
        assertFalse(loginAssist.needsLoginSessionSyncCheck(bean, OptionalThing.of(STALE_CHECK_DT), NOW));
    }

    @Test
    public void test_needsSyncCheck_openIdUser_staleCheckTime_skipsCheck() {
        final FessUserBean bean = new FessUserBean(newOpenIdUser("oidc-admin"));
        assertFalse(loginAssist.needsLoginSessionSyncCheck(bean, OptionalThing.of(STALE_CHECK_DT), NOW));
    }

    @Test
    public void test_needsSyncCheck_externalUser_neverCheckedYet_skipsCheck() {
        // super returns true for "no check yet"; the external-user guard must run first.
        final FessUserBean bean = new FessUserBean(newSamlUser("saml-admin"));
        assertFalse(loginAssist.needsLoginSessionSyncCheck(bean, OptionalThing.empty(), NOW));
    }

    @Test
    public void test_needsSyncCheck_localUser_staleCheckTime_performsCheck() {
        final FessUserBean bean = new FessUserBean(newLocalUser("alice"));
        assertTrue(loginAssist.needsLoginSessionSyncCheck(bean, OptionalThing.of(STALE_CHECK_DT), NOW));
    }

    @Test
    public void test_needsSyncCheck_localUser_withinInterval_skipsCheck() {
        final FessUserBean bean = new FessUserBean(newLocalUser("alice"));
        assertFalse(loginAssist.needsLoginSessionSyncCheck(bean, OptionalThing.of(FRESH_CHECK_DT), NOW));
    }

    @Test
    public void test_needsSyncCheck_localUser_neverCheckedYet_performsCheck() {
        final FessUserBean bean = new FessUserBean(newLocalUser("alice"));
        assertTrue(loginAssist.needsLoginSessionSyncCheck(bean, OptionalThing.empty(), NOW));
    }

    @Test
    public void test_needsSyncCheck_emptyUserBean_doesNotThrow() {
        final FessUserBean bean = FessUserBean.empty(); // wraps a null FessUser
        assertNull(bean.getFessUser());
        assertFalse(loginAssist.needsLoginSessionSyncCheck(bean, OptionalThing.of(STALE_CHECK_DT), NOW));
    }

    @Test
    public void test_needsSyncCheck_nullBackedUserBean_doesNotThrow() {
        final FessUserBean bean = new FessUserBean(null);
        assertFalse(loginAssist.needsLoginSessionSyncCheck(bean, OptionalThing.empty(), NOW));
    }

    // ---------------------------------------------------------------------
    // syncCheckLoginSessionIfNeeds (end-to-end through TypicalLoginAssist)
    // ---------------------------------------------------------------------

    @Test
    public void test_syncCheckLoginSession_externalUser_keepsSessionWithoutQuery() {
        final ExposedLoginAssist assist = newExposedAssist();
        final CountingUserBhv bhv = installCountingUserBhv(assist, null);
        final FessUserBean bean = new FessUserBean(newSamlUser("saml-admin"));
        bean.manageLastestSyncCheckTime(STALE_CHECK_DT);

        assertTrue(assist.callSyncCheckLoginSession(bean));
        assertEquals(0, bhv.selectCalls.get());
        assertEquals(0, assist.logoutCalls.get());
    }

    @Test
    public void test_syncCheckLoginSession_localUserDeleted_logsOut() {
        final ExposedLoginAssist assist = newExposedAssist();
        final CountingUserBhv bhv = installCountingUserBhv(assist, null); // the document is gone
        final FessUserBean bean = new FessUserBean(newLocalUser("alice"));
        bean.manageLastestSyncCheckTime(STALE_CHECK_DT);

        assertFalse(assist.callSyncCheckLoginSession(bean));
        assertEquals(1, bhv.selectCalls.get());
        assertEquals(1, assist.logoutCalls.get());
    }

    @Test
    public void test_syncCheckLoginSession_localUserStillPresent_keepsSession() {
        final ExposedLoginAssist assist = newExposedAssist();
        final CountingUserBhv bhv = installCountingUserBhv(assist, newLocalUser("alice"));
        final FessUserBean bean = new FessUserBean(newLocalUser("alice"));
        bean.manageLastestSyncCheckTime(STALE_CHECK_DT);

        assertTrue(assist.callSyncCheckLoginSession(bean));
        assertEquals(1, bhv.selectCalls.get());
        assertEquals(0, assist.logoutCalls.get());
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    /** Fixed "now" used by the sync-check tests. */
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 1, 2, 3, 4, 5);

    /** Older than the 300-second default sync-check interval. */
    private static final LocalDateTime STALE_CHECK_DT = NOW.minusSeconds(310);

    /** Well within the 300-second default sync-check interval. */
    private static final LocalDateTime FRESH_CHECK_DT = NOW.minusSeconds(10);

    private static SamlCredential.SamlUser newSamlUser(final String nameId) {
        return new SamlCredential.SamlUser(nameId, "session-index", null, null, null, new String[0], new String[] { "admin" });
    }

    private static OpenIdConnectCredential.OpenIdUser newOpenIdUser(final String name) {
        return new OpenIdConnectCredential.OpenIdUser(name, new String[0], new String[] { "admin" });
    }

    private static User newLocalUser(final String name) {
        final User user = new User();
        user.setName(name);
        return user;
    }

    private ExposedLoginAssist newExposedAssist() {
        final ExposedLoginAssist assist = new ExposedLoginAssist();
        setField(TypicalLoginAssist.class, assist, "timeManager", newFixedTimeManager(NOW));
        return assist;
    }

    private static TimeManager newFixedTimeManager(final LocalDateTime fixed) {
        return (TimeManager) Proxy.newProxyInstance(TimeManager.class.getClassLoader(), new Class<?>[] { TimeManager.class },
                (proxy, method, args) -> {
                    final String name = method.getName();
                    if ("currentDateTime".equals(name)) {
                        return fixed;
                    }
                    if ("toString".equals(name)) {
                        return "fixedTimeManager:" + fixed;
                    }
                    if ("hashCode".equals(name)) {
                        return Integer.valueOf(System.identityHashCode(proxy));
                    }
                    if ("equals".equals(name)) {
                        return Boolean.valueOf(proxy == args[0]);
                    }
                    throw new UnsupportedOperationException(name);
                });
    }

    private void installUserBhv(final User stored) {
        installCountingUserBhv(loginAssist, stored);
    }

    private CountingUserBhv installCountingUserBhv(final FessLoginAssist target, final User stored) {
        final CountingUserBhv bhv = new CountingUserBhv(stored);
        setField(FessLoginAssist.class, target, "userBhv", bhv);
        return bhv;
    }

    private static void setField(final Class<?> declaringType, final Object target, final String name, final Object value) {
        try {
            final java.lang.reflect.Field f = declaringType.getDeclaredField(name);
            f.setAccessible(true);
            f.set(target, value);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private RecordingUserService installUserService() {
        final RecordingUserService rec = new RecordingUserService();
        ComponentUtil.register(rec, UserService.class.getCanonicalName());
        return rec;
    }

    private static String sha256Hex(final String input) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        final StringBuilder sb = new StringBuilder(digest.length * 2);
        for (final byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------------
    // Fakes
    // ---------------------------------------------------------------------

    /** Counts the {@code .fess_user} lookups issued by the login-session sync check. */
    private static class CountingUserBhv extends UserBhv {
        final AtomicInteger selectCalls = new AtomicInteger(0);
        private final User stored;

        CountingUserBhv(final User stored) {
            this.stored = stored;
        }

        @Override
        public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
            selectCalls.incrementAndGet();
            return stored == null ? OptionalEntity.empty() : OptionalEntity.of(stored);
        }
    }

    /**
     * Exposes the protected sync-check entry point of {@code TypicalLoginAssist} and replaces
     * {@code logout()} with a counter, so the whole flow can run without a servlet session.
     */
    private static class ExposedLoginAssist extends FessLoginAssist {
        final AtomicInteger logoutCalls = new AtomicInteger(0);

        @Override
        public void logout() {
            logoutCalls.incrementAndGet();
        }

        boolean callSyncCheckLoginSession(final FessUserBean userBean) {
            return syncCheckLoginSessionIfNeeds(userBean);
        }
    }

    private static class RecordingUserService extends UserService {
        final AtomicInteger updateCalls = new AtomicInteger(0);
        final AtomicReference<String> lastUsername = new AtomicReference<>();
        final AtomicReference<String> lastHash = new AtomicReference<>();
        final AtomicReference<String> lastExpectedHash = new AtomicReference<>();

        @Override
        public boolean updateStoredPasswordHash(final String username, final String encodedPassword) {
            return updateStoredPasswordHash(username, null, encodedPassword);
        }

        @Override
        public boolean updateStoredPasswordHash(final String username, final String expectedCurrentHash, final String newEncodedPassword) {
            updateCalls.incrementAndGet();
            lastUsername.set(username);
            lastHash.set(newEncodedPassword);
            lastExpectedHash.set(expectedCurrentHash);
            return true;
        }
    }

    /**
     * Wraps a real {@link PasswordHashHelper} and counts {@code matches} calls so
     * tests can assert the dummy-bcrypt timing-countermeasure behaviour.
     */
    private static class CountingPasswordHashHelper extends PasswordHashHelper {
        private final PasswordHashHelper delegate;
        final AtomicInteger matchesCalls = new AtomicInteger(0);
        final AtomicInteger paddingCalls = new AtomicInteger(0);
        final AtomicReference<String> lastStored = new AtomicReference<>();

        CountingPasswordHashHelper(final PasswordHashHelper delegate) {
            this.delegate = delegate;
        }

        @Override
        public String encode(final String rawPassword) {
            return delegate.encode(rawPassword);
        }

        @Override
        public boolean matches(final String rawPassword, final String storedPassword) {
            matchesCalls.incrementAndGet();
            lastStored.set(storedPassword);
            return delegate.matches(rawPassword, storedPassword);
        }

        @Override
        public boolean upgradeEncoding(final String storedPassword) {
            return delegate.upgradeEncoding(storedPassword);
        }

        @Override
        public void applyTimingPadding() {
            paddingCalls.incrementAndGet();
            // Do not actually consume a BCrypt round in unit tests — it would
            // add tens of milliseconds per test with no observable behaviour
            // to assert beyond call counting.
        }

        @Override
        public boolean isTimingSafeHash(final String storedPassword) {
            return delegate.isTimingSafeHash(storedPassword);
        }
    }

    /** Minimal FessConfig override for this test. */
    private static class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        String passwordAlgorithm = "bcrypt";
        int bcryptCost = 10;
        boolean upgradeEnabled = true;
        String digestAlgorithm = "sha256";

        @Override
        public String getAppPasswordAlgorithm() {
            return passwordAlgorithm;
        }

        @Override
        public Integer getAppPasswordBcryptCostAsInteger() {
            return Integer.valueOf(bcryptCost);
        }

        @Override
        public String getAppPasswordBcryptCost() {
            return Integer.toString(bcryptCost);
        }

        @Override
        public boolean isAppPasswordUpgradeEnabled() {
            return upgradeEnabled;
        }

        @Override
        public String getAppPasswordUpgradeEnabled() {
            return Boolean.toString(upgradeEnabled);
        }

        @Override
        public String getAppDigestAlgorithm() {
            return digestAlgorithm;
        }
    }
}
