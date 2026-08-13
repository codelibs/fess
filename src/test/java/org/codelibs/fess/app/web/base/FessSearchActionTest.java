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
package org.codelibs.fess.app.web.base;

import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.lastaflute.core.message.UserMessage;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.ruts.process.ActionRuntime;

public class FessSearchActionTest extends UnitFessTestCase {

    /** A FessUser that is not an EntraIdUser: the surfacing must not know what authenticated it. */
    private static FessUser userWith(final FessUser.PermissionState state) {
        return new FessUser() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getName() {
                return "taro";
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
            public PermissionState getPermissionState() {
                return state;
            }
        };
    }

    private final FessSearchAction action = new FessSearchAction() {
    };

    @Test
    public void test_getPermissionStateMessageKey_saysNothingWhenResolved() {
        assertNull(action.getPermissionStateMessageKey(userWith(FessUser.PermissionState.RESOLVED)));
    }

    @Test
    public void test_getPermissionStateMessageKey_reportsALoadingUser() {
        assertEquals(FessMessages.ERRORS_user_permissions_loading,
                action.getPermissionStateMessageKey(userWith(FessUser.PermissionState.PENDING)));
    }

    @Test
    public void test_getPermissionStateMessageKey_reportsAFailedUser() {
        assertEquals(FessMessages.ERRORS_user_permissions_unavailable,
                action.getPermissionStateMessageKey(userWith(FessUser.PermissionState.FAILED)));
    }

    @Test
    public void test_getPermissionStateMessageKey_toleratesAMissingState() {
        // FessUser#getPermissionState() is documented as never null, but nothing enforces that on
        // an implementation, and this method is a documented extension point. A switch that threw
        // here would 500 every front-end page.
        assertNull(action.getPermissionStateMessageKey(userWith(null)), "an unknown state has nothing to say");
    }

    // ===================================================================================
    //                                                             hookBefore Wiring
    //                                                             ==================
    // The tests above pin only the pure decision function. These pin the glue in hookBefore
    // itself -- the fessLoginAssist.getSavedUserBean().ifPresent(...) block and the
    // requestManager.errors().add(...) call -- since a regression that deleted that block, or
    // swapped requestManager for sessionManager, would ship undetected otherwise.

    @Test
    public void test_hookBefore_reportsALoadingUserInRequestScopeOnly() {
        final FessSearchAction searchAction = createAction(savedUserBean(FessUser.PermissionState.PENDING));

        searchAction.hookBefore(new TestActionRuntime("/"));

        assertRequestHasMessage(searchAction, FessMessages.ERRORS_user_permissions_loading);
        assertSessionHasNoMessage(searchAction);
    }

    @Test
    public void test_hookBefore_reportsAFailedUserInRequestScopeOnly() {
        final FessSearchAction searchAction = createAction(savedUserBean(FessUser.PermissionState.FAILED));

        searchAction.hookBefore(new TestActionRuntime("/"));

        assertRequestHasMessage(searchAction, FessMessages.ERRORS_user_permissions_unavailable);
        assertSessionHasNoMessage(searchAction);
    }

    @Test
    public void test_hookBefore_addsNothingForAResolvedUser() {
        final FessSearchAction searchAction = createAction(savedUserBean(FessUser.PermissionState.RESOLVED));

        searchAction.hookBefore(new TestActionRuntime("/"));

        assertFalse(searchAction.requestManager.errors().get().isPresent(), "a resolved user has nothing to report");
        assertSessionHasNoMessage(searchAction);
    }

    @Test
    public void test_hookBefore_addsNothingWhenTheBeanCarriesNoUser() {
        // FessUserBean.empty() wraps a null FessUser and does not override getFessUser().
        final FessSearchAction searchAction = createAction(OptionalThing.of(FessUserBean.empty()));

        searchAction.hookBefore(new TestActionRuntime("/"));

        assertFalse(searchAction.requestManager.errors().get().isPresent(), "a bean with no user has no state to report on");
        assertSessionHasNoMessage(searchAction);
    }

    @Test
    public void test_hookBefore_addsNothingForAnAnonymousCaller() {
        final FessSearchAction searchAction = createAction(OptionalThing.empty());

        // Must not throw for the no-saved-user-bean case either.
        searchAction.hookBefore(new TestActionRuntime("/"));

        assertFalse(searchAction.requestManager.errors().get().isPresent(), "an anonymous caller has no user to report on");
        assertSessionHasNoMessage(searchAction);
    }

    // ===================================================================================
    //                                                        Shadowing Another Message
    //                                                        =========================
    // saveError(...) writes under LastaWebKey.ACTION_ERRORS_KEY in *session* scope; hookBefore
    // writes under the same key in *request* scope. <la:errors> resolves via
    // pageContext.findAttribute -- page, then request, then session, stopping at the first hit --
    // so a request-scoped notice added on its own hides the session-scoped one entirely, and the
    // tag then clears the session copy it never rendered.

    @Test
    public void test_hookBefore_doesNotHideAMessageSavedBeforeTheRedirect() {
        // What SearchAction does for a malformed query: saveError(...), then redirectToRoot().
        // The user lands on index.jsp, whose <la:errors> must show the invalid-query error.
        final FessSearchAction searchAction = createAction(savedUserBean(FessUser.PermissionState.FAILED));
        savedInSessionBeforeTheRedirect(searchAction, FessMessages.ERRORS_invalid_query_unknown);

        searchAction.hookBefore(new TestActionRuntime("/"));

        final UserMessages rendered = resolveAsErrorsTagWould(searchAction);
        assertTrue(rendered.hasMessageOf(UserMessages.GLOBAL, FessMessages.ERRORS_invalid_query_unknown),
                "the invalid-query error must still reach the page; the permission notice may accompany it, not replace it");
        assertTrue(rendered.hasMessageOf(UserMessages.GLOBAL, FessMessages.ERRORS_user_permissions_unavailable),
                "the permission notice must still be reported");
    }

    @Test
    public void test_hookBefore_leavesASavedMessageAloneForAResolvedUser() {
        // Nothing to report, so nothing may be written to request scope: findAttribute has to fall
        // through to the session copy on its own, exactly as it did before this branch.
        final FessSearchAction searchAction = createAction(savedUserBean(FessUser.PermissionState.RESOLVED));
        savedInSessionBeforeTheRedirect(searchAction, FessMessages.ERRORS_invalid_query_unknown);

        searchAction.hookBefore(new TestActionRuntime("/"));

        assertFalse(searchAction.requestManager.errors().get().isPresent(), "a resolved user has nothing to add to request scope");
        assertTrue(resolveAsErrorsTagWould(searchAction).hasMessageOf(UserMessages.GLOBAL, FessMessages.ERRORS_invalid_query_unknown),
                "the saved message must still reach the page");
    }

    // ===================================================================================
    //                                                        Carrying Over, Not Consuming
    //                                                        ============================
    // UserMessages#add(UserMessages) -- what addMessages(saved) ends up calling -- reads its source
    // through accessByIteratorOf, which marks that source accessed. ActionCoinsHelper then deletes
    // any accessed session errors at the start of the next request. Handing the session copy
    // straight to request scope would therefore destroy it, including on the responses that render
    // no <la:errors> at all (error/notFound.jsp, help.jsp, the streaming and redirecting actions),
    // where it was displayed zero times.

    @Test
    public void test_carryOverSavedErrors_doesNotConsumeTheSessionCopy() {
        final FessSearchAction searchAction = createAction(savedUserBean(FessUser.PermissionState.RESOLVED));
        final UserMessages saved = new UserMessages();
        saved.add(UserMessages.GLOBAL, new UserMessage(FessMessages.ERRORS_invalid_query_unknown, "sea"));
        searchAction.sessionManager.errors().saveMessages(saved);

        searchAction.carryOverSavedErrors();

        assertFalse(saved.isAccessed(),
                "the session copy must not be marked accessed; ActionCoinsHelper deletes accessed session errors next request");
        assertTrue(saved.hasMessageOf(UserMessages.GLOBAL, FessMessages.ERRORS_invalid_query_unknown),
                "the session copy must still hold its message");

        assertTrue(searchAction.requestManager.errors().get().isPresent(), "the saved error must reach request scope");
        final UserMessages carried = searchAction.requestManager.errors().get().get();
        final UserMessage sessionMessage = saved.silentAccessByIteratorOf(UserMessages.GLOBAL).next();
        final UserMessage requestMessage = carried.silentAccessByIteratorOf(UserMessages.GLOBAL).next();
        assertEquals(sessionMessage.getMessageKey(), requestMessage.getMessageKey());
        assertEquals("sea", requestMessage.getValues()[0]);
        assertTrue(sessionMessage != requestMessage, "the request copy must be a rebuilt message, not the session instance");
        assertTrue(sessionMessage.getValues() != requestMessage.getValues(),
                "the values array must not be shared: TaglibEnhanceLogic escapes message arguments in place");
    }

    /** Mirrors FessBaseAction#saveError: session scope, under LastaWebKey.ACTION_ERRORS_KEY. */
    private void savedInSessionBeforeTheRedirect(final FessSearchAction searchAction, final String messageKey) {
        final UserMessages saved = new UserMessages();
        saved.add(UserMessages.GLOBAL, new UserMessage(messageKey));
        searchAction.sessionManager.errors().saveMessages(saved);
    }

    /**
     * What {@code <la:errors>} sees: TaglibEnhanceLogic#findUserMessages calls
     * pageContext.findAttribute(ACTION_ERRORS_KEY), which searches page, then request, then
     * session, and stops at the first scope that answers. Neither of these paths writes page
     * scope, so request wins over session whenever it holds anything at all.
     */
    private UserMessages resolveAsErrorsTagWould(final FessSearchAction searchAction) {
        return searchAction.requestManager.errors()
                .get()
                .orElseGet(() -> searchAction.sessionManager.errors().get().orElseGet(UserMessages::new));
    }

    private static OptionalThing<FessUserBean> savedUserBean(final FessUser.PermissionState state) {
        return OptionalThing.of(new FessUserBean(userWith(state)));
    }

    private void assertRequestHasMessage(final FessSearchAction searchAction, final String messageKey) {
        final boolean hasMessage = searchAction.requestManager.errors()
                .get()
                .map(messages -> messages.hasMessageOf(UserMessages.GLOBAL, messageKey))
                .orElse(false);
        assertTrue(hasMessage, "expected " + messageKey + " to be present in request scope");
    }

    private void assertSessionHasNoMessage(final FessSearchAction searchAction) {
        // The whole point of requestManager.errors().add(...) over sessionManager: a message left
        // in session scope would resurface later on a page with no <la:errors> tag, well after the
        // state that caused it stopped being true.
        assertFalse(searchAction.sessionManager.errors().get().isPresent(),
                "the notice must live in request scope only, not session scope");
    }

    /**
     * Wires a {@link FessSearchAction} with a login assist that returns the given saved user bean
     * and a fessConfig that turns off hookBefore's (unrelated) popular-word branch, then injects
     * the standard LastaFlute framework fields -- requestManager, sessionManager, etc. -- that
     * hookBefore's message-scope wiring depends on. Mirrors FessAdminActionTest's
     * createGodHandAction pattern.
     */
    private FessSearchAction createAction(final OptionalThing<FessUserBean> userBean) {
        final FessSearchAction searchAction = new FessSearchAction() {
        };
        // Set before inject() so the framework binder -- which never overwrites an already
        // non-null field -- leaves these test doubles alone.
        searchAction.fessLoginAssist = new FessLoginAssist() {
            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                return userBean;
            }
        };
        searchAction.fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public boolean isWebApiPopularWord() {
                return false;
            }
        };
        searchAction.viewHelper = new ViewHelper();
        inject(searchAction);
        return searchAction;
    }

    private static class TestActionRuntime extends ActionRuntime {

        TestActionRuntime(final String requestPath) {
            super(requestPath, null, null);
        }
    }

    @Test
    public void test_getPermissionStateMessageKey_toleratesANullState() {
        // FessUser is Serializable and its implementations live in the session, so one that gains a
        // permission-state field without changing serialVersionUID deserializes an older session
        // with it null -- field initializers do not run during deserialization. A bare switch on a
        // null selector throws, and this runs in hookBefore on every front page, so that would be a
        // 500 on every request until the session is dropped.
        final FessUser user = new FessUser() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getName() {
                return "olduser";
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
            public FessUser.PermissionState getPermissionState() {
                return null;
            }
        };
        assertNull(new FessSearchAction() {
        }.getPermissionStateMessageKey(user));
    }
}
