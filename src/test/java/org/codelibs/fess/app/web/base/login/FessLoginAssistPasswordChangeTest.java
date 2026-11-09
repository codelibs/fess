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

import java.util.Hashtable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.auth.PasswordChangedAtCache;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.ldap.LdapUser;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.google.common.base.Ticker;

/**
 * Tests that a login session of a local user ends once the password of that user is changed, and
 * that no other kind of login session is looked at.
 */
public class FessLoginAssistPasswordChangeTest extends UnitFessTestCase {

    private TestConfig config;

    private StampService service;

    private SessionAssist assist;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        config = new TestConfig();
        ComponentUtil.setFessConfig(config);
        service = new StampService();
        ComponentUtil.register(service, UserService.class.getCanonicalName());
        assist = new SessionAssist();
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    // ---------------------------------------------------------------------
    // the session of the user whose password changed
    // ---------------------------------------------------------------------

    @Test
    public void test_otherSession_endsAfterPasswordChange() {
        assist.session = assist.createUserBean(localUser("alice", null));
        service.stamps.put("alice", 1000L);

        assertFalse(assist.getSavedUserBean().isPresent());
        assertEquals(1, assist.logouts.get());
        assertNull(assist.session);
    }

    @Test
    public void test_session_endsAfterAnotherPasswordChange() {
        assist.session = assist.createUserBean(localUser("alice", 500L));
        service.stamps.put("alice", 1000L);

        assertFalse(assist.getSavedUserBean().isPresent());
        assertEquals(1, assist.logouts.get());
    }

    @Test
    public void test_session_keptWhilePasswordUnchanged() {
        assist.session = assist.createUserBean(localUser("alice", 500L));
        service.stamps.put("alice", 500L);

        assertTrue(assist.getSavedUserBean().isPresent());
        assertEquals(0, assist.logouts.get());
    }

    @Test
    public void test_session_keptWhenUserWithoutFieldNeverChangedPassword() {
        assist.session = assist.createUserBean(localUser("alice", null));

        assertTrue(assist.getSavedUserBean().isPresent());
        assertEquals(0, assist.logouts.get());
    }

    @Test
    public void test_session_keptWhenDocumentLostTheField() {
        // e.g. the document was rewritten by a node that does not know the field
        assist.session = assist.createUserBean(localUser("alice", 500L));
        service.stamps.put("alice", 0L);

        assertTrue(assist.getSavedUserBean().isPresent());
    }

    @Test
    public void test_session_keptWhenLookupFails() {
        assist.session = assist.createUserBean(localUser("alice", null));
        service.failure = new IllegalStateException("search engine is down");

        assertTrue(assist.getSavedUserBean().isPresent());
        assertEquals(0, assist.logouts.get());
    }

    // ---------------------------------------------------------------------
    // sessions that are not looked at
    // ---------------------------------------------------------------------

    @Test
    public void test_sessionOpenedBeforeTheFieldExisted_notChecked() {
        final FessUserBean bean = new FessUserBean(localUser("alice", null)); // no snapshot: a pre-upgrade session
        assertNull(bean.getPasswordChangedAt());
        assist.session = bean;
        service.stamps.put("alice", 1000L);

        assertTrue(assist.getSavedUserBean().isPresent());
        assertEquals(0, service.reads.get());
    }

    @Test
    public void test_ldapUser_notChecked() {
        assist.session = assist.createUserBean(new LdapUser(new Hashtable<>(), "bob"));
        service.stamps.put("bob", 1000L);

        assertNull(assist.session.getPasswordChangedAt());
        assertTrue(assist.getSavedUserBean().isPresent());
        assertEquals(0, service.reads.get());
    }

    @Test
    public void test_ssoUser_notChecked() {
        assist.session = assist.createUserBean(new SsoUser("carol"));
        service.stamps.put("carol", 1000L);

        assertNull(assist.session.getPasswordChangedAt());
        assertTrue(assist.getSavedUserBean().isPresent());
        assertEquals(0, service.reads.get());
    }

    @Test
    public void test_noSession_returnsEmptyWithoutLookup() {
        assertFalse(assist.getSavedUserBean().isPresent());
        assertEquals(0, service.reads.get());
        assertEquals(0, assist.logouts.get());
    }

    // ---------------------------------------------------------------------
    // what the session remembers
    // ---------------------------------------------------------------------

    @Test
    public void test_createUserBean_localUser_remembersTheTimeOfTheDocument() {
        assertEquals(Long.valueOf(700L), assist.createUserBean(localUser("alice", 700L)).getPasswordChangedAt());
    }

    @Test
    public void test_createUserBean_localUserWithoutField_remembersZero() {
        assertEquals(Long.valueOf(0L), assist.createUserBean(localUser("alice", null)).getPasswordChangedAt());
    }

    @Test
    public void test_createUserBean_configuredAdminUser_isCheckedLikeAnyLocalUser() {
        // the admin account is a document of the user index and logs in with its password
        assertEquals(Long.valueOf(0L), assist.createUserBean(localUser("admin", null)).getPasswordChangedAt());
    }

    // ---------------------------------------------------------------------
    // the cache
    // ---------------------------------------------------------------------

    @Test
    public void test_lookup_cachedWithinTtl_andRepeatedAfterIt() {
        config.expire = 10;
        assist.session = assist.createUserBean(localUser("alice", null));

        assertTrue(assist.getSavedUserBean().isPresent());
        assertTrue(assist.getSavedUserBean().isPresent());
        assertEquals(1, service.reads.get());

        assist.ticker.advance(9, TimeUnit.SECONDS);
        assertTrue(assist.getSavedUserBean().isPresent());
        assertEquals(1, service.reads.get());

        assist.ticker.advance(2, TimeUnit.SECONDS);
        service.stamps.put("alice", 1000L);
        assertFalse(assist.getSavedUserBean().isPresent()); // the change shows once the entry expired
        assertEquals(2, service.reads.get());
    }

    @Test
    public void test_lookup_notCachedWhenTtlIsZero() {
        config.expire = 0;
        assist.session = assist.createUserBean(localUser("alice", null));

        assertTrue(assist.getSavedUserBean().isPresent());
        assertTrue(assist.getSavedUserBean().isPresent());
        assertEquals(2, service.reads.get());
    }

    @Test
    public void test_lookup_cachedPerUser() {
        assist.session = assist.createUserBean(localUser("alice", null));
        assertTrue(assist.getSavedUserBean().isPresent());
        assist.session = assist.createUserBean(localUser("bob", null));
        assertTrue(assist.getSavedUserBean().isPresent());
        assertEquals(2, service.reads.get());
    }

    // ---------------------------------------------------------------------
    // acceptPasswordChange
    // ---------------------------------------------------------------------

    @Test
    public void test_acceptPasswordChange_keepsTheSessionThatChangedIt() {
        assist.session = assist.createUserBean(localUser("alice", null));
        assertTrue(assist.getSavedUserBean().isPresent()); // caches the old time
        service.stamps.put("alice", 2000L);

        assist.acceptPasswordChange("alice", 2000L);

        assertEquals(Long.valueOf(2000L), assist.session.getPasswordChangedAt());
        assertEquals(1, assist.saved.get()); // stored again, so a replicated session notices
        assertTrue(assist.getSavedUserBean().isPresent());
        assertEquals(0, assist.logouts.get());
    }

    @Test
    public void test_acceptPasswordChange_endsOtherSessionsOfThatUserOnThisNodeAtOnce() {
        // an administrator changes the password of bob; the cache already holds bob's old time
        assist.session = assist.createUserBean(localUser("bob", null));
        assertTrue(assist.getSavedUserBean().isPresent());
        assertEquals(1, service.reads.get());

        assist.session = assist.createUserBean(localUser("admin", null));
        assist.acceptPasswordChange("bob", 3000L);
        assertEquals(Long.valueOf(0L), assist.session.getPasswordChangedAt()); // not the session of bob
        assertEquals(0, assist.saved.get());

        assist.session = assist.createUserBean(localUser("bob", null));
        assertFalse(assist.getSavedUserBean().isPresent());
        assertEquals(1, service.reads.get()); // answered from the cache the change wrote
    }

    @Test
    public void test_acceptPasswordChange_withoutSession_doesNotFail() {
        assist.acceptPasswordChange("alice", 2000L);
        assertEquals(0, assist.saved.get());
    }

    // ---------------------------------------------------------------------
    // Fakes
    // ---------------------------------------------------------------------

    private static User localUser(final String name, final Long passwordChangedAt) {
        final User user = new User();
        user.setName(name);
        user.setPasswordChangedAt(passwordChangedAt);
        return user;
    }

    private static class SsoUser implements FessUser {
        private static final long serialVersionUID = 1L;

        private final String name;

        SsoUser(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
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
    }

    private static class FakeTicker extends Ticker {
        private long nanos;

        void advance(final long time, final TimeUnit unit) {
            nanos += unit.toNanos(time);
        }

        @Override
        public long read() {
            return nanos;
        }
    }

    /** A login assist whose session is a field, so that it needs no servlet container. */
    private static class SessionAssist extends FessLoginAssist {
        FessUserBean session;
        final AtomicInteger logouts = new AtomicInteger();
        final AtomicInteger saved = new AtomicInteger();
        final FakeTicker ticker = new FakeTicker();
        final PasswordChangedAtCache cache = new PasswordChangedAtCache() {
            @Override
            protected Ticker getTicker() {
                return ticker;
            }
        };

        @Override
        protected OptionalThing<FessUserBean> getSessionUserBeanUnchecked() {
            return OptionalThing.ofNullable(session, () -> {
                throw new IllegalStateException("no session");
            });
        }

        @Override
        protected void saveSessionUserBean(final FessUserBean userBean) {
            saved.incrementAndGet();
        }

        @Override
        public void logout() {
            logouts.incrementAndGet();
            session = null;
        }

        @Override
        protected PasswordChangedAtCache getPasswordChangedAtCache() {
            return cache;
        }

        @Override
        public FessUserBean createUserBean(final FessUser user) {
            return super.createUserBean(user);
        }
    }

    private static class StampService extends UserService {
        final Map<String, Long> stamps = new HashMap<>();
        final AtomicInteger reads = new AtomicInteger();
        RuntimeException failure;

        @Override
        public long getPasswordChangedAt(final String username) {
            reads.incrementAndGet();
            if (failure != null) {
                throw failure;
            }
            return stamps.getOrDefault(username, 0L);
        }
    }

    private static class TestConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        int expire = 10;

        @Override
        public Integer getPasswordChangedCacheExpireAsInteger() {
            return Integer.valueOf(expire);
        }
    }
}
