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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.codelibs.fess.app.web.base.login.EntraIdCredential.EntraIdUser;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.sso.entraid.EntraIdAuthenticator;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.ITenantProfile;

public class EntraIdUserPermissionTest extends UnitFessTestCase {

    private static IAuthenticationResult authResult() {
        final IAccount account = new IAccount() {
            private static final long serialVersionUID = 1L;

            @Override
            public String homeAccountId() {
                return "home-account-id";
            }

            @Override
            public String environment() {
                return "login.microsoftonline.com";
            }

            @Override
            public String username() {
                return "taro@contoso.onmicrosoft.com";
            }

            @Override
            public Map<String, ITenantProfile> getTenantProfiles() {
                return Collections.emptyMap();
            }
        };
        return new IAuthenticationResult() {
            private static final long serialVersionUID = 1L;

            @Override
            public String accessToken() {
                return "access-token";
            }

            @Override
            public String idToken() {
                return "id-token";
            }

            @Override
            public IAccount account() {
                return account;
            }

            @Override
            public ITenantProfile tenantProfile() {
                return null;
            }

            @Override
            public String environment() {
                return "login.microsoftonline.com";
            }

            @Override
            public String scopes() {
                return "https://graph.microsoft.com/.default";
            }

            @Override
            public Date expiresOnDate() {
                return new Date(Long.MAX_VALUE);
            }
        };
    }

    /**
     * Builds an EntraIdUser without letting its constructor talk to Microsoft Graph.
     */
    private EntraIdUser newUser() {
        ComponentUtil.register(new EntraIdAuthenticator() {
            @Override
            public void updateMemberOf(final EntraIdUser user) {
                // the test drives setGroups/setRoles itself
            }
        }, EntraIdAuthenticator.class.getCanonicalName());
        return new EntraIdUser(authResult());
    }

    @Test
    public void test_getPermissions_doesNotPinAStaleValueWhenTheAsyncLookupLands() throws Exception {
        // scheduleParentGroupLookup runs on a TimeoutManager thread while the user is already
        // logged in and searching. getPermissions() is a check-then-act -- read `permissions ==
        // null`, read `groups`, write `permissions` -- so a reader that started before the async
        // task can finish after it and overwrite the fresh value with one computed from the
        // direct groups alone. Nothing sets `permissions` back to null after that, so the parent
        // group permissions stay missing for the rest of the session.
        final CountDownLatch readerIsInside = new CountDownLatch(1);
        final CountDownLatch asyncTaskIsDone = new CountDownLatch(1);
        ComponentUtil.register(new SystemHelper() {
            @Override
            public String getSearchRoleByGroup(final String name) {
                if ("direct-group".equals(name)) {
                    // The reader has read `groups` and is now mid-computation.
                    readerIsInside.countDown();
                    try {
                        asyncTaskIsDone.await(10L, TimeUnit.SECONDS);
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                return super.getSearchRoleByGroup(name);
            }
        }, "systemHelper");

        final EntraIdUser user = newUser();
        user.setGroups(new String[] { "direct-group" });
        user.setRoles(new String[0]);

        final Thread reader = new Thread(() -> user.getPermissions());
        reader.start();
        assertTrue(readerIsInside.await(10L, TimeUnit.SECONDS));

        // What scheduleParentGroupLookup does once the parent groups arrive, on its own thread so
        // that it can be made to wait for the reader rather than deadlocking with it.
        final Thread asyncLookup = new Thread(() -> {
            user.setGroups(new String[] { "direct-group", "parent-group" });
            user.setRoles(new String[0]);
            user.resetPermissions();
        });
        asyncLookup.start();
        // Give the async task time to get as far as it is able to before the reader finishes.
        Thread.sleep(200L);

        asyncTaskIsDone.countDown();
        reader.join(10000L);
        asyncLookup.join(10000L);

        final String[] permissions = user.getPermissions();
        assertTrue("parent-group missing from " + Arrays.toString(permissions),
                Arrays.stream(permissions).anyMatch(p -> p.contains("parent-group")));
    }
}
