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

import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.RecordingActivityHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Tests that a login session ends once the user says that its login has ended, and that every other session is left alone.
 */
public class FessLoginAssistExpiryTest extends UnitFessTestCase {

    private RecordingActivityHelper audit;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        audit = new RecordingActivityHelper();
        ComponentUtil.register(audit, "activityHelper");
    }

    @Test
    public void test_session_endsWhenTheLoginHasEnded() {
        final SessionAssist assist = new SessionAssist();
        assist.session = new FessUserBean(new StubUser("alice", true));

        assertFalse(assist.getSavedUserBean().isPresent());
        assertEquals(1, assist.logouts.get());
        assertEquals(1, audit.sessionEnds.size());
        assertEquals("alice:idp_session_ended", audit.sessionEnds.get(0));
        assertNull(assist.session);
    }

    @Test
    public void test_session_keptWhileTheLoginHasNotEnded() {
        final SessionAssist assist = new SessionAssist();
        assist.session = new FessUserBean(new StubUser("alice", false));

        assertTrue(assist.getSavedUserBean().isPresent());
        assertEquals(0, assist.logouts.get());
        assertTrue(audit.sessionEnds.isEmpty());
    }

    @Test
    public void test_session_ofAUserWithNoEndOfItsOwnIsNeverEnded() {
        final SessionAssist assist = new SessionAssist();
        assist.session = new FessUserBean(new FessUser() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getName() {
                return "bob";
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

        assertTrue(assist.getSavedUserBean().isPresent());
        assertEquals(0, assist.logouts.get());
        assertTrue(audit.sessionEnds.isEmpty());
    }

    @Test
    public void test_noSession_isNotAnError() {
        final SessionAssist assist = new SessionAssist();

        assertFalse(assist.getSavedUserBean().isPresent());
        assertEquals(0, assist.logouts.get());
        assertTrue(audit.sessionEnds.isEmpty());
    }

    /** A login assist whose session is a field, so that it needs no servlet container. */
    private static class SessionAssist extends FessLoginAssist {
        FessUserBean session;

        final AtomicInteger logouts = new AtomicInteger();

        @Override
        protected OptionalThing<FessUserBean> getSessionUserBeanUnchecked() {
            return OptionalThing.ofNullable(session, () -> {
                throw new IllegalStateException("no session");
            });
        }

        @Override
        public void logout() {
            logouts.incrementAndGet();
            session = null;
        }
    }

    private static class StubUser implements FessUser {
        private static final long serialVersionUID = 1L;

        private final String name;

        private final boolean expired;

        StubUser(final String name, final boolean expired) {
            this.name = name;
            this.expired = expired;
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

        @Override
        public boolean isExpired() {
            return expired;
        }
    }
}
