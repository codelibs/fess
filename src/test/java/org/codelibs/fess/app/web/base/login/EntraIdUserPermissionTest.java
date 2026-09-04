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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.app.web.base.login.EntraIdCredential.EntraIdUser;
import org.codelibs.fess.helper.ActivityHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.sso.entraid.EntraIdAuthenticator;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;

import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.ITenantProfile;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;

public class EntraIdUserPermissionTest extends UnitFessTestCase {

    /** The user's object id in the tenant, as the ID token's {@code oid} claim carries it. */
    private static final String OBJECT_ID = "3f7a1c9e-0b52-4d18-9a6c-2e5b8d41f0aa";

    /** An ID token carrying {@link #OBJECT_ID}, in the shape MSAL4J hands back. */
    private static final String ID_TOKEN = new PlainJWT(new JWTClaimsSet.Builder().claim("oid", OBJECT_ID).build()).serialize();

    private static IAuthenticationResult authResult() {
        return authResult(new Date(Long.MAX_VALUE), "access-token");
    }

    private static IAuthenticationResult authResult(final Date expiresOn, final String accessToken) {
        return authResult(expiresOn, accessToken, ID_TOKEN);
    }

    private static IAuthenticationResult authResult(final Date expiresOn, final String accessToken, final String idToken) {
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
                return accessToken;
            }

            @Override
            public String idToken() {
                return idToken;
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
                return expiresOn;
            }
        };
    }

    /**
     * Builds an EntraIdUser without letting its constructor talk to Microsoft Graph.
     */
    private EntraIdUser newUser() {
        return newUser(authResult());
    }

    private EntraIdUser newUser(final IAuthenticationResult authResult) {
        ComponentUtil.register(new EntraIdAuthenticator() {
            @Override
            public void scheduleUpdateMemberOf(final EntraIdUser user) {
                // the test drives setGroups/setRoles itself
            }
        }, EntraIdAuthenticator.class.getCanonicalName());
        return new EntraIdUser(authResult);
    }

    @Test
    public void test_getPermissions_doesNotCollapseAGroupNameOnABackslash() {
        // getCanonicalLdapName drops everything up to the first backslash, because a name a user
        // types at login may be NetBIOS-qualified as DOMAIN\name. An identity provider's group
        // name is not, so running it through that truncation let one group's name produce another
        // group's permission: with entraid.permission.fields=displayName, a tenant user who can
        // create a security group -- the Entra ID default -- names it "x\finance" and receives
        // the permission of the unrelated group "finance", and with it every document that group
        // can read. Verified end to end against a live tenant before this fix.
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final EntraIdUser user = newUser();
        user.setGroups(new String[] { "x\\finance" });
        user.setRoles(new String[] { "y\\admin" });

        final List<String> permissions = Arrays.asList(user.getPermissions());

        assertTrue(permissions.toString(), permissions.contains("2x\\finance"));
        assertFalse(permissions.toString(), permissions.contains("2finance"));
        assertTrue(permissions.toString(), permissions.contains("Ry\\admin"));
        assertFalse(permissions.toString(), permissions.contains("Radmin"));
    }

    @Test
    public void test_getPermissions_doesNotCollapseAUserNameOnABackslash() {
        // The same truncation applied to the name the provider asserted for the user itself.
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final EntraIdUser user = newUser();
        user.setGroups(new String[0]);
        user.setRoles(new String[0]);

        final List<String> permissions = Arrays.asList(user.getPermissions());

        // The account username is taro@contoso.onmicrosoft.com, so nothing is truncated here; the
        // assertion that matters is that the value arrives whole and prefixed.
        assertTrue(permissions.toString(), permissions.contains("1taro@contoso.onmicrosoft.com"));
        assertTrue(permissions.toString(), permissions.contains("1" + OBJECT_ID));
    }

    @Test
    public void test_getPermissions_namesTheUserByTheObjectIdInTheIdToken() {
        // Microsoft Graph names a user by the object id, so that is the value a crawler writes
        // into the role field of a document the user owns. homeAccountId() is MSAL4J's own
        // account key -- "<object id>.<tenant id>" -- and a permission built from it matches no
        // such role, so the user never saw a document granted to them by object id.
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final EntraIdUser user = newUser();
        user.setGroups(new String[0]);
        user.setRoles(new String[0]);

        final List<String> permissions = Arrays.asList(user.getPermissions());

        assertTrue(permissions.toString(), permissions.contains("1" + OBJECT_ID));
        assertFalse(permissions.toString(), permissions.contains("1home-account-id"));
    }

    @Test
    public void test_getPermissions_grantsNoObjectIdPermissionWhenTheIdTokenCarriesNone() {
        // A missing or unreadable oid claim must drop the permission, not encode a literal "null"
        // -- which is not blank, so the trailing filter would keep it, and a document whose role
        // field held it would be readable by every such session.
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final EntraIdUser user = newUser(authResult(new Date(Long.MAX_VALUE), "access-token", "not-a-jwt"));
        user.setGroups(new String[0]);
        user.setRoles(new String[0]);

        final List<String> permissions = Arrays.asList(user.getPermissions());

        assertFalse(permissions.toString(), permissions.stream().anyMatch(p -> p.contains("null")));
        assertTrue(permissions.toString(), permissions.contains("1taro@contoso.onmicrosoft.com"));
    }

    @Test
    public void test_getPermissions_doesNotPinAStaleValueWhenTheAsyncLookupLands() throws Exception {
        // The membership resolution scheduled at login runs on a TimeoutManager thread while the
        // user is already logged in and searching. getPermissions() is a check-then-act -- read
        // `permissions == null`, read `groups`, write `permissions` -- so a reader that started
        // before that task lands can finish after it and overwrite the fresh value with one
        // computed from the direct groups alone. Nothing sets `permissions` back to null after
        // that, so the parent group permissions stay missing for the rest of the session.
        final CountDownLatch readerIsInside = new CountDownLatch(1);
        final CountDownLatch asyncTaskIsDone = new CountDownLatch(1);
        ComponentUtil.register(new SystemHelper() {
            @Override
            public String getSearchRoleByDirectoryGroup(final String name) {
                if ("direct-group".equals(name)) {
                    // The reader has read `groups` and is now mid-computation.
                    readerIsInside.countDown();
                    try {
                        asyncTaskIsDone.await(10L, TimeUnit.SECONDS);
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                return super.getSearchRoleByDirectoryGroup(name);
            }
        }, "systemHelper");

        final EntraIdUser user = newUser();
        user.setGroups(new String[] { "direct-group" });
        user.setRoles(new String[0]);

        final Thread reader = new Thread(() -> user.getPermissions());
        reader.start();
        assertTrue(readerIsInside.await(10L, TimeUnit.SECONDS));

        // What the scheduled updateMemberOf task does once the parent groups arrive, on its own
        // thread so that it can be made to wait for the reader rather than deadlocking with it.
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

    @Test
    public void test_refresh_renewsOnceWhenConcurrentRequestsShareTheUser() throws Exception {
        // Lastaflute keeps the FessUserBean -- and therefore one EntraIdUser -- as a session
        // attribute, and FessBaseAction.godHandPrologue calls refresh() on every action request,
        // so all the requests a session has in flight arrive in the REFRESH_MARGIN window
        // together. Each of them used to see a renewed access token and run updateMemberOf, which
        // is a synchronous Microsoft Graph GET /me/memberOf on a request thread plus another
        // scheduled parent group lookup. updateMemberOf itself now runs off the request thread,
        // but scheduling it twice per rollover would still double the eventual Graph traffic --
        // exactly what the per-request guard was added to remove. scheduleUpdateMemberOf is the
        // seam refresh() now calls, so it is what proves the guard suppressed the second call.
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        // Inside REFRESH_MARGIN, so refresh() really attempts the silent acquisition.
        final IAuthenticationResult initial = authResult(new Date(now + 30 * 1000L), "access-token");

        final AtomicInteger scheduleCalls = new AtomicInteger();
        final CountDownLatch winnerIsAcquiring = new CountDownLatch(1);
        final CountDownLatch loserIsDone = new CountDownLatch(1);
        ComponentUtil.register(new EntraIdAuthenticator() {
            @Override
            public void scheduleUpdateMemberOf(final EntraIdUser user) {
                scheduleCalls.incrementAndGet();
            }

            @Override
            public IAuthenticationResult refreshTokenSilently(final EntraIdUser user) {
                // Hold the acquisition open the way a real MSAL4J round trip does, so the second
                // request reaches refresh() while this one is still inside it.
                winnerIsAcquiring.countDown();
                try {
                    loserIsDone.await(10L, TimeUnit.SECONDS);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return authResult(new Date(now + 30 * 1000L), "renewed-access-token");
            }
        }, EntraIdAuthenticator.class.getCanonicalName());

        final EntraIdUser user = new EntraIdUser(initial);
        // The constructor schedules its own resolution once; only what refresh() adds is under test.
        scheduleCalls.set(0);

        final AtomicBoolean winnerResult = new AtomicBoolean();
        final Thread winner = new Thread(() -> winnerResult.set(user.refresh()));
        winner.start();
        assertTrue(winnerIsAcquiring.await(10L, TimeUnit.SECONDS));

        // The session's second concurrent request. Its token has not expired, so it must be let
        // through rather than blocked behind the acquisition, and it must not renew again. If the
        // refreshing CAS guard in refresh() were removed, this second call would reach
        // refreshTokenSilently (and, since the stub always answers "renewed", scheduleUpdateMemberOf)
        // concurrently with the winner instead of returning immediately, taking the count below to 2.
        assertTrue(user.refresh());
        loserIsDone.countDown();
        winner.join(10000L);

        assertTrue(winnerResult.get());
        assertEquals(1, scheduleCalls.get(), "a concurrent refresh must not schedule a second Microsoft Graph round trip");
        // Last-writer-wins used to be able to leave the older of the two results in place.
        assertEquals("renewed-access-token", user.getAuthenticationResult().accessToken());
    }

    @Test
    public void test_refresh_stillRenewsOnEveryRollover() throws Exception {
        // The counterpart of the test above: the guard must only suppress a *concurrent* renewal.
        // A sequential refresh has to keep re-reading the directory, otherwise a session would
        // never pick up a group change again, and the flag has to be released on the way out.
        // scheduleUpdateMemberOf is the seam refresh() now calls per rollover.
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        final IAuthenticationResult initial = authResult(new Date(now + 30 * 1000L), "access-token");

        final AtomicInteger scheduleCalls = new AtomicInteger();
        final AtomicReference<IAuthenticationResult> next = new AtomicReference<>();
        ComponentUtil.register(new EntraIdAuthenticator() {
            @Override
            public void scheduleUpdateMemberOf(final EntraIdUser user) {
                scheduleCalls.incrementAndGet();
            }

            @Override
            public IAuthenticationResult refreshTokenSilently(final EntraIdUser user) {
                return next.get();
            }
        }, EntraIdAuthenticator.class.getCanonicalName());

        final EntraIdUser user = new EntraIdUser(initial);
        scheduleCalls.set(0);

        next.set(authResult(new Date(now + 30 * 1000L), "second-access-token"));
        assertTrue(user.refresh());
        assertEquals(1, scheduleCalls.get(), "the first rollover must re-read the directory");

        next.set(authResult(new Date(now + 30 * 1000L), "third-access-token"));
        assertTrue(user.refresh());
        assertEquals(2, scheduleCalls.get(), "the guard must be released once the acquisition is over");
        assertEquals("third-access-token", user.getAuthenticationResult().accessToken());
    }

    @Test
    public void test_updateMemberOf_resetsThePermissionsCacheOnceGroupsResolve() throws Exception {
        // Under the new PENDING window, getPermissions() is very likely to be computed once before
        // updateMemberOf lands -- groups is still null, so only the user-scoped permission gets
        // cached. resetPermissions() inside updateMemberOf is now the only thing that clears that
        // cache once the real groups arrive; refresh() no longer calls it separately. If it
        // silently stopped firing, this stale, user-scoped-only array would pin for the rest of
        // the session.
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        // updateMemberOf calls permissionChanged() at the end, and test_app.xml does not register
        // a real activityHelper (production's app.xml does).
        ComponentUtil.register(new ActivityHelper() {
            @Override
            public void permissionChanged(final OptionalThing<FessUserBean> user) {
                // no-op
            }
        }, "activityHelper");
        ComponentUtil.register(new EntraIdAuthenticator() {
            @Override
            public void scheduleUpdateMemberOf(final EntraIdUser user) {
                // keep the constructor off Graph; this test drives updateMemberOf itself
            }
        }, EntraIdAuthenticator.class.getCanonicalName());
        final EntraIdUser user = new EntraIdUser(authResult());

        final String[] beforePermissions = user.getPermissions();
        assertFalse("resolved-group must not be present before updateMemberOf runs: " + Arrays.toString(beforePermissions),
                Arrays.stream(beforePermissions).anyMatch(p -> p.contains("resolved-group")));

        final EntraIdAuthenticator resolvingAuthenticator = new EntraIdAuthenticator() {
            @Override
            protected boolean processDirectMemberOf(final EntraIdUser user, final List<String> groupList, final List<String> roleList,
                    final List<String> groupIdsForParentLookup, final String url) {
                groupList.add("resolved-group");
                return true;
            }
        };
        resolvingAuthenticator.updateMemberOf(user);

        final String[] afterPermissions = user.getPermissions();
        assertTrue("resolved-group missing from " + Arrays.toString(afterPermissions),
                Arrays.stream(afterPermissions).anyMatch(p -> p.contains("resolved-group")));
    }

    /**
     * Registers a SystemHelper whose clock the test drives, the way EntraIdAuthenticatorTest does.
     */
    private void registerClock(final AtomicLong clock) {
        ComponentUtil.register(new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return clock.get();
            }
        }, "systemHelper");
    }

    @Test
    public void test_refresh_attemptsARenewalWhenTheTokenHasExpired() {
        // FessBaseAction.godHandPrologue discards this result, so returning false without asking
        // MSAL4J for anything never ended the session: it left it holding a dead access token and
        // taking the same early exit on every later request, which is what stopped its group
        // memberships from ever being re-read again. MSAL4J's silent flow spends the cached
        // refresh token, which outlives the access token by hours, so an expired access token is
        // precisely the case worth one attempt.
        final AtomicLong clock = new AtomicLong(1_700_000_000_000L);
        registerClock(clock);
        final AtomicInteger acquisitions = new AtomicInteger();
        ComponentUtil.register(new EntraIdAuthenticator() {
            @Override
            public void scheduleUpdateMemberOf(final EntraIdUser user) {
                // the constructor must not reach Microsoft Graph. Overriding the scheduling and
                // not updateMemberOf: the base implementation hands a real task to TimeoutManager,
                // so overriding only the body still leaves a timer thread racing this test.
            }

            @Override
            public IAuthenticationResult refreshTokenSilently(final EntraIdUser user) {
                acquisitions.incrementAndGet();
                return null;
            }
        }, EntraIdAuthenticator.class.getCanonicalName());

        final EntraIdUser user = new EntraIdUser(authResult(new Date(clock.get() - 1L), "expired-access-token"));

        // The acquisition failed, so the token really is dead and refresh() says so.
        assertFalse(user.refresh());
        assertEquals(1, acquisitions.get(), "an expired access token must not be given up on without asking MSAL4J");
    }

    @Test
    public void test_refresh_recoversASessionWhoseTokenExpired() {
        // The user was idle across the expiry -- with REFRESH_MARGIN in place their last request
        // can easily have fallen before the renewal window -- and comes back. The cached refresh
        // token is still good, so the session carries on with a live token and re-read groups.
        final AtomicLong clock = new AtomicLong(1_700_000_000_000L);
        registerClock(clock);
        final AtomicInteger memberOfCalls = new AtomicInteger();
        ComponentUtil.register(new EntraIdAuthenticator() {
            @Override
            public void scheduleUpdateMemberOf(final EntraIdUser user) {
                // The renewal schedules the re-resolution rather than running it: refresh() is on
                // a request thread and updateMemberOf reaches Microsoft Graph. Counting the
                // scheduling is what pins that the re-read is requested at all.
                memberOfCalls.incrementAndGet();
            }

            @Override
            public IAuthenticationResult refreshTokenSilently(final EntraIdUser user) {
                return authResult(new Date(clock.get() + 60 * 60 * 1000L), "renewed-access-token");
            }
        }, EntraIdAuthenticator.class.getCanonicalName());

        final EntraIdUser user = new EntraIdUser(authResult(new Date(clock.get() - 1L), "expired-access-token"));
        // The constructor schedules the first resolution; only what refresh() adds is under test.
        memberOfCalls.set(0);

        assertTrue(user.refresh());
        assertEquals("renewed-access-token", user.getAuthenticationResult().accessToken());
        assertEquals(1, memberOfCalls.get(), "a recovered session must re-read its group memberships");
    }

    @Test
    public void test_refresh_holdsOffAFailingRenewalUntilTheThrottleLapses() {
        // A revoked refresh token, a disabled account, and an account a logout on another session
        // evicted from the shared MSAL4J cache all fail for good, and refresh() runs on every
        // action request. Retrying unconditionally would put back exactly the per-request round
        // trip REFRESH_MARGIN was introduced to remove.
        final AtomicLong clock = new AtomicLong(1_700_000_000_000L);
        registerClock(clock);
        final AtomicInteger acquisitions = new AtomicInteger();
        ComponentUtil.register(new EntraIdAuthenticator() {
            @Override
            public void scheduleUpdateMemberOf(final EntraIdUser user) {
                // the constructor must not reach Microsoft Graph. Overriding the scheduling and
                // not updateMemberOf: the base implementation hands a real task to TimeoutManager,
                // so overriding only the body still leaves a timer thread racing this test.
            }

            @Override
            public IAuthenticationResult refreshTokenSilently(final EntraIdUser user) {
                acquisitions.incrementAndGet();
                return null;
            }
        }, EntraIdAuthenticator.class.getCanonicalName());

        final EntraIdUser user = new EntraIdUser(authResult(new Date(clock.get() - 1L), "expired-access-token"));

        assertFalse(user.refresh());
        assertEquals(1, acquisitions.get(), "the first request after the expiry must attempt a renewal");

        // The rest of the requests this session makes inside the interval.
        assertFalse(user.refresh());
        clock.addAndGet(EntraIdUser.RENEWAL_THROTTLE_INTERVAL - 1L);
        assertFalse(user.refresh());
        assertEquals(1, acquisitions.get(), "a renewal that failed must not be retried on every request");

        // ... and the first one after it.
        clock.addAndGet(1L);
        assertFalse(user.refresh());
        assertEquals(2, acquisitions.get(), "the throttle must lapse rather than give up for good");
    }

    @Test
    public void test_refresh_doesNotStampedeWhenConcurrentRequestsFindAnExpiredToken() throws Exception {
        // The concurrency guard has to cover the expired token as well, not just the renewal
        // window: godHandPrologue calls refresh() on every action request, so the requests a
        // session has in flight when it comes back after the expiry arrive here together, and
        // each of them would otherwise run its own acquisition and its own synchronous Microsoft
        // Graph call behind updateMemberOf.
        final AtomicLong clock = new AtomicLong(1_700_000_000_000L);
        registerClock(clock);
        final AtomicInteger acquisitions = new AtomicInteger();
        final AtomicInteger memberOfCalls = new AtomicInteger();
        final CountDownLatch winnerIsAcquiring = new CountDownLatch(1);
        final CountDownLatch loserIsDone = new CountDownLatch(1);
        ComponentUtil.register(new EntraIdAuthenticator() {
            @Override
            public void scheduleUpdateMemberOf(final EntraIdUser user) {
                // The scheduling is what refresh() does -- it runs on a request thread and
                // updateMemberOf reaches Microsoft Graph. Counting the base implementation's
                // TimeoutManager task instead would race this assertion.
                memberOfCalls.incrementAndGet();
            }

            @Override
            public IAuthenticationResult refreshTokenSilently(final EntraIdUser user) {
                acquisitions.incrementAndGet();
                // Hold the acquisition open the way a real MSAL4J round trip does, so the second
                // request reaches refresh() while this one is still inside it.
                winnerIsAcquiring.countDown();
                try {
                    loserIsDone.await(10L, TimeUnit.SECONDS);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return authResult(new Date(clock.get() + 60 * 60 * 1000L), "renewed-access-token");
            }
        }, EntraIdAuthenticator.class.getCanonicalName());

        final EntraIdUser user = new EntraIdUser(authResult(new Date(clock.get() - 1L), "expired-access-token"));
        memberOfCalls.set(0);

        final AtomicBoolean winnerResult = new AtomicBoolean();
        final Thread winner = new Thread(() -> winnerResult.set(user.refresh()));
        winner.start();
        assertTrue(winnerIsAcquiring.await(10L, TimeUnit.SECONDS));

        // The session's second concurrent request. It holds nothing valid, so it reports that,
        // but it must not start a second acquisition of its own.
        assertFalse(user.refresh());
        assertEquals(1, acquisitions.get(), "a concurrent refresh must not start a second silent acquisition");
        loserIsDone.countDown();
        winner.join(10000L);

        assertTrue(winnerResult.get());
        assertEquals(1, memberOfCalls.get(), "a concurrent refresh must not make a second Microsoft Graph round trip");
        assertEquals("renewed-access-token", user.getAuthenticationResult().accessToken());
    }

    @Test
    public void test_refresh_holdsOffAfterAnExceptionToo() {
        // The exception path has to back off as well, otherwise the failure it now reports at
        // WARN -- refreshTokenSilently swallows its own, so in production this is updateMemberOf
        // or the component lookup throwing -- is written once per request rather than once per
        // interval, which is exactly the noise the throttle is there to prevent.
        final AtomicLong clock = new AtomicLong(1_700_000_000_000L);
        registerClock(clock);
        final AtomicInteger acquisitions = new AtomicInteger();
        ComponentUtil.register(new EntraIdAuthenticator() {
            @Override
            public void scheduleUpdateMemberOf(final EntraIdUser user) {
                // the constructor must not reach Microsoft Graph. Overriding the scheduling and
                // not updateMemberOf: the base implementation hands a real task to TimeoutManager,
                // so overriding only the body still leaves a timer thread racing this test.
            }

            @Override
            public IAuthenticationResult refreshTokenSilently(final EntraIdUser user) {
                acquisitions.incrementAndGet();
                throw new IllegalStateException("the directory could not be reached");
            }
        }, EntraIdAuthenticator.class.getCanonicalName());

        final EntraIdUser user = new EntraIdUser(authResult(new Date(clock.get() - 1L), "expired-access-token"));

        assertFalse(user.refresh());
        assertFalse(user.refresh());
        assertEquals(1, acquisitions.get(), "a renewal that threw must not be retried on every request");

        clock.addAndGet(EntraIdUser.RENEWAL_THROTTLE_INTERVAL);
        assertFalse(user.refresh());
        assertEquals(2, acquisitions.get(), "the throttle must lapse rather than give up for good");
    }
}
