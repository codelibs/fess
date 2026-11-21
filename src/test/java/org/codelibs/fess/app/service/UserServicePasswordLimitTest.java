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
package org.codelibs.fess.app.service;

import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.auth.AuthenticationManager;
import org.codelibs.fess.helper.PasswordHashHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.opensearch.user.cbean.UserCB;
import org.codelibs.fess.opensearch.user.exbhv.UserBhv;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fesen.opensearch.action.index.IndexRequestBuilder;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * A password the hash algorithm refuses must be found before {@code AuthenticationManager#changePassword},
 * which may already have changed the password in LDAP.
 */
public class UserServicePasswordLimitTest extends UnitFessTestCase {

    private UserService userService;

    private final List<String> authenticationManagerCalls = new ArrayList<>();

    private final List<User> written = new ArrayList<>();

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getAppPasswordAlgorithm() {
                return "bcrypt";
            }

            @Override
            public Integer getAppPasswordBcryptCostAsInteger() {
                return Integer.valueOf(4);
            }
        });
        userService = new UserService();
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword("{bcrypt}old");
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(stored);
            }

            @Override
            public void insert(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                written.add(entity);
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                written.add(entity);
            }
        };
        ComponentUtil.register(new PasswordHashHelper(), "passwordHashHelper");
        ComponentUtil.register(new AuthenticationManager() {
            @Override
            public void insert(final User user) {
            }

            @Override
            public boolean changePassword(final String username, final String password) {
                authenticationManagerCalls.add(username);
                return true;
            }
        }, "authenticationManager");
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    @Test
    public void test_changePassword_overlongPassword_isRefusedBeforeTheAuthenticationManager() {
        try {
            userService.changePassword("alice", "a".repeat(73));
            fail();
        } catch (final IllegalArgumentException e) {
            assertFalse(e.getMessage(), e.getMessage().contains("aaaa"));
        }
        assertTrue("LDAP must not have been touched", authenticationManagerCalls.isEmpty());
        assertTrue(written.isEmpty());
    }

    @Test
    public void test_changePassword_passwordAtTheLimit_isStored() {
        userService.changePassword("alice", "a".repeat(72));

        assertEquals(List.of("alice"), authenticationManagerCalls);
        assertEquals(1, written.size());
        assertTrue(written.get(0).getPassword().startsWith("{bcrypt}$2a$04$"));
    }
}
