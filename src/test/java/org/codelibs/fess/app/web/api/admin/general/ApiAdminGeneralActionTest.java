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
package org.codelibs.fess.app.web.api.admin.general;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.ldap.LdapManager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.validation.Required;

/**
 * Contract tests for {@code PUT /api/admin/general}.
 *
 * <p>The endpoint is not a plain "store what was sent" update. It first rebuilds the whole settings
 * body out of what is stored ({@code AdminGeneralAction.updateForm}), then overlays the request with
 * {@code BeanUtil.copyBeanToBean(body, newBody, CopyOptions::excludeNull)}, and only the merged
 * result reaches {@code AdminGeneralAction.updateConfig}. The validation that runs in between is
 * deliberately split: the bean constraints are evaluated against the raw request body, while the
 * correlated SPNEGO rule is evaluated against the merged result.</p>
 *
 * <p>That merge-then-validate glue is what these tests pin, because nothing else exercises it: the
 * two shared halves have their own tests in {@code AdminGeneralActionTest}, and the integration
 * suite's {@code GeneralTests} only reads. Each test drives the real {@code put$index} execute
 * method, so the merge, the bean constraints and the correlated rule run in the order the action
 * uses them. The one thing left outside is LastaFlute's own translation of the thrown
 * {@code ValidationErrorException} into an HTTP 400 body, which happens in the API failure hook
 * after the execute method has already returned.</p>
 */
public class ApiAdminGeneralActionTest extends UnitFessTestCase {

    private static final String PREAUTH_PASSWORD_KEY = "spnego.preauth.password";

    private static final String ALLOW_BASIC_KEY = "spnego.allow.basic";

    private static final String PROMPT_NTLM_KEY = "spnego.prompt.ntlm";

    private static final String ALLOWED_REALMS_KEY = "spnego.allowed.realms";

    private static final String SSO_TYPE_SPNEGO = "spnego";

    /**
     * {@code updateConfig} writes every general setting into the shared system properties, so this
     * test class needs its own container to keep those values out of the other test classes.
     *
     * @return true to create the container for each test
     */
    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Override
    public void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new LdapManager(), "ldapManager");
        // updateConfig refreshes design files and re-reads app values, which are unrelated to the
        // stored properties under test and pull in further components.
        ComponentUtil.register(new SystemHelper() {
            @Override
            public List<Path> refreshDesignJspFiles() {
                return Collections.emptyList();
            }

            @Override
            public void updateSystemProperties() {
                // nothing
            }
        }, "systemHelper");
    }

    // ===================================================================================
    //                                                    Masked secret: omitted vs emptied
    //                                                    ==================================

    @Test
    public void test_put$index_omittedPreauthPassword_keepsStoredSecret() throws Exception {
        // updateForm renders a stored spnego.preauth.password as the "**********" mask, excludeNull
        // leaves that mask in place when the request omits the field, and updateConfig skips any
        // value made only of mask characters. Break any one of those three -- drop excludeNull, or
        // store the raw body instead of the merged one -- and a request that never mentioned the
        // password silently deletes it.
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        fessConfig.setSystemProperty(PREAUTH_PASSWORD_KEY, "stored-secret");

        final ApiAdminGeneralAction action = createInjectedAction();
        final EditBody body = newStorableBody();
        assertNull(body.spnegoPreauthPassword, "the request must omit the password for this test to mean anything");

        assertOkResponse(action.put$index(body));

        assertEquals("stored-secret", ComponentUtil.getSystemProperties().getProperty(PREAUTH_PASSWORD_KEY));
    }

    @Test
    public void test_put$index_emptyPreauthPassword_clearsStoredSecret() throws Exception {
        // The asymmetry that makes the test above worth having: an explicitly empty password is a
        // meaningful setting rather than "no opinion", because the SPNEGO library only falls back to
        // a keytab when both the pre-authentication user name and the password are empty.
        // excludeNull excludes null, not "", so the empty string survives the merge and updateConfig
        // removes the stored key.
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        fessConfig.setSystemProperty(PREAUTH_PASSWORD_KEY, "stored-secret");

        final ApiAdminGeneralAction action = createInjectedAction();
        final EditBody body = newStorableBody();
        body.spnegoPreauthPassword = "";

        assertOkResponse(action.put$index(body));

        assertNull(ComponentUtil.getSystemProperties().getProperty(PREAUTH_PASSWORD_KEY));
    }

    @Test
    public void test_put$index_suppliedPreauthPassword_replacesStoredSecret() throws Exception {
        // The mask guard must not degenerate into "never write the password".
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        fessConfig.setSystemProperty(PREAUTH_PASSWORD_KEY, "stored-secret");

        final ApiAdminGeneralAction action = createInjectedAction();
        final EditBody body = newStorableBody();
        body.spnegoPreauthPassword = "rotated-secret";

        assertOkResponse(action.put$index(body));

        assertEquals("rotated-secret", ComponentUtil.getSystemProperties().getProperty(PREAUTH_PASSWORD_KEY));
    }

    // ===================================================================================
    //                                                       The merge fills in the request
    //                                                       ===============================

    @Test
    public void test_put$index_omittedPlainField_keepsStoredValue() throws Exception {
        // The same merge protects the ordinary (unmasked) settings: a request must not blank out
        // everything it did not mention.
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        fessConfig.setSystemProperty(ALLOWED_REALMS_KEY, "TRUSTED.EXAMPLE,PARTNER.EXAMPLE");

        final ApiAdminGeneralAction action = createInjectedAction();
        final EditBody body = newStorableBody();
        assertNull(body.spnegoAllowedRealms, "the request must omit the realms for this test to mean anything");

        assertOkResponse(action.put$index(body));

        assertEquals("TRUSTED.EXAMPLE,PARTNER.EXAMPLE", ComponentUtil.getSystemProperties().getProperty(ALLOWED_REALMS_KEY));
    }

    @Test
    public void test_put$index_bodyWithOnlyRequiredFields_isStorable() throws Exception {
        // updateConfig unboxes seven Integer fields but only three of them are @Required, so a body
        // carrying exactly what the constraints demand can only be stored because the merge supplied
        // the other four from what updateForm read back.
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        fessConfig.setPurgeSearchLogDay(45);

        final ApiAdminGeneralAction action = createInjectedAction();
        final EditBody body = newRequiredOnlyBody();
        assertNull(body.purgeSearchLogDay, "the request must omit the purge day for this test to mean anything");

        assertOkResponse(action.put$index(body));

        assertEquals(45, ComponentUtil.getFessConfig().getPurgeSearchLogDay());
    }

    // ===================================================================================
    //                                        Correlated rule runs against the merged result
    //                                        ===============================================

    @Test
    public void test_put$index_ntlmPromptRule_readsStoredValuesForOmittedFields() throws Exception {
        // sso.type, spnego.allow.basic and spnego.prompt.ntlm are all stored and none of them is in
        // the request, yet the rule must still fire: it is evaluated against what is about to be
        // stored, not against what the request happened to carry.
        storeUnsupportedSpnegoCombination();

        final ApiAdminGeneralAction action = createInjectedAction();
        final EditBody body = newStorableBody();
        assertNull(body.ssoType, "the request must omit ssoType for this test to mean anything");
        assertNull(body.spnegoAllowBasic, "the request must omit spnegoAllowBasic for this test to mean anything");
        assertNull(body.spnegoPromptNtlm, "the request must omit spnegoPromptNtlm for this test to mean anything");

        assertValidationError(() -> action.put$index(body)).handle(data -> {
            data.requiredMessageOf("spnegoPromptNtlm", "errors.spnego_prompt_ntlm_requires_basic");
        });
    }

    @Test
    public void test_put$index_ntlmPromptRule_acceptsSupportedStoredCombination() throws Exception {
        // Negative control for the test above: the very same omitted-everything request is accepted
        // when the stored combination is a supported one, so the rejection there comes from the
        // stored values and not merely from the request being partial.
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        fessConfig.setSsoType(SSO_TYPE_SPNEGO);
        fessConfig.setSystemProperty(ALLOW_BASIC_KEY, Constants.TRUE);
        fessConfig.setSystemProperty(PROMPT_NTLM_KEY, Constants.TRUE);

        final ApiAdminGeneralAction action = createInjectedAction();

        assertOkResponse(action.put$index(newStorableBody()));

        assertEquals(Constants.TRUE, ComponentUtil.getSystemProperties().getProperty(PROMPT_NTLM_KEY));
    }

    @Test
    public void test_put$index_ntlmPromptRule_acceptsRequestThatFixesStoredCombination() throws Exception {
        // The other half of the merge: the request supplies the missing half of the correlated pair,
        // so what is about to be stored is a supported combination and the save goes through.
        storeUnsupportedSpnegoCombination();

        final ApiAdminGeneralAction action = createInjectedAction();
        final EditBody body = newStorableBody();
        body.spnegoAllowBasic = Constants.TRUE;

        assertOkResponse(action.put$index(body));

        assertEquals(Constants.TRUE, ComponentUtil.getSystemProperties().getProperty(ALLOW_BASIC_KEY));
        assertEquals(Constants.TRUE, ComponentUtil.getSystemProperties().getProperty(PROMPT_NTLM_KEY));
    }

    // ===================================================================================
    //                                                  @Required is checked on the request
    //                                                  ==================================

    @Test
    public void test_put$index_partialBodyWithoutRequiredFields_isRejected() throws Exception {
        // The bean constraints are evaluated against the raw request body, not against the merged
        // result, so a genuinely partial PUT is refused even though the merge could have supplied
        // every missing value from what is stored. That is deliberate, but it is easy to miss in the
        // code -- validateApi takes "body" while the rule nested inside it takes "newBody" -- and it
        // would invert without a sound if the two arguments were ever unified.
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        fessConfig.setSystemProperty(PREAUTH_PASSWORD_KEY, "stored-secret");

        final ApiAdminGeneralAction action = createInjectedAction();
        final EditBody body = new EditBody();
        body.spnegoAllowedRealms = "TRUSTED.EXAMPLE";

        assertValidationError(() -> action.put$index(body)).handle(data -> {
            data.requiredMessageOf("dayForCleanup", Required.class);
            data.requiredMessageOf("crawlingThreadCount", Required.class);
            data.requiredMessageOf("failureCountThreshold", Required.class);
            data.requiredMessageOf("csvFileEncoding", Required.class);
        });

        // Nothing may have been written: updateConfig runs only after validateApi returns normally.
        assertEquals("stored-secret", ComponentUtil.getSystemProperties().getProperty(PREAUTH_PASSWORD_KEY));
        assertNull(ComponentUtil.getSystemProperties().getProperty(ALLOWED_REALMS_KEY));
    }

    @Test
    public void test_put$index_partialBodyReportsRequiredAndCorrelationTogether() throws Exception {
        // The correlated rule is not gated behind the bean constraints: ActionValidator collects the
        // hibernate violations first, then runs the extra validation lambda into the same message
        // set, and throws once at the end. A partial PUT that also lands on the unsupported SPNEGO
        // combination therefore reports both problems in one response instead of one per round trip.
        storeUnsupportedSpnegoCombination();

        final ApiAdminGeneralAction action = createInjectedAction();
        final EditBody body = new EditBody();

        assertValidationError(() -> action.put$index(body)).handle(data -> {
            data.requiredMessageOf("dayForCleanup", Required.class);
            data.requiredMessageOf("spnegoPromptNtlm", "errors.spnego_prompt_ntlm_requires_basic");
        });
    }

    // ===================================================================================
    //                                                                             Helpers
    //                                                                             =======

    /**
     * Stores the SPNEGO settings that the correlated rule must reject: the SPNEGO provider is
     * selected, the NTLM prompt is on, and Basic authentication -- which the library needs before it
     * can downgrade an NTLM token -- is off.
     */
    private void storeUnsupportedSpnegoCombination() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        fessConfig.setSsoType(SSO_TYPE_SPNEGO);
        fessConfig.setSystemProperty(ALLOW_BASIC_KEY, Constants.FALSE);
        fessConfig.setSystemProperty(PROMPT_NTLM_KEY, Constants.TRUE);
    }

    /**
     * Creates the smallest request body that satisfies the bean constraints: the four fields
     * {@code EditForm} marks {@code @Required}, and nothing else.
     *
     * @return the request body to hand to {@code put$index}
     */
    private EditBody newRequiredOnlyBody() {
        final EditBody body = new EditBody();
        body.dayForCleanup = 20;
        body.crawlingThreadCount = 5;
        body.failureCountThreshold = 10;
        body.csvFileEncoding = "UTF-8";
        return body;
    }

    /**
     * Creates a request body carrying every {@code Integer} field {@code updateConfig} unboxes, on
     * top of the {@code @Required} ones. The tests that assert on what was stored use this so that a
     * broken merge shows up as the wrong stored value rather than as an unboxing
     * {@code NullPointerException} on an unrelated field.
     *
     * @return the request body to hand to {@code put$index}
     */
    private EditBody newStorableBody() {
        final EditBody body = newRequiredOnlyBody();
        body.purgeSearchLogDay = 30;
        body.purgeJobLogDay = 30;
        body.purgeUserInfoDay = 30;
        body.purgeSuggestSearchLogDay = 30;
        return body;
    }

    /**
     * Wires an {@link ApiAdminGeneralAction} through UTFlute's {@code inject()} for the framework
     * fields that {@code validateApi()}/{@code asJson()} need, then fills in the fess-specific
     * collaborators that {@code fess.xml} -- not loaded by the unit-test container -- would
     * otherwise supply. Mirrors {@code ApiAdminSearchlistActionTest#createInjectedAction}.
     *
     * @return the action, ready to have its execute method called
     * @throws Exception if the reflective wiring fails
     */
    private ApiAdminGeneralAction createInjectedAction() throws Exception {
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        // FessApiAction declares an @Resource AccessTokenService whose own AccessTokenBhv @Resource
        // cannot be assembled in the unit container; only isAccessAllowed() uses it, and that is
        // never reached when the execute method is called directly.
        suppressBindingOf(org.codelibs.fess.app.service.AccessTokenService.class);
        final ApiAdminGeneralAction action = new ApiAdminGeneralAction();
        inject(action);

        final Field systemHelperField = FessBaseAction.class.getDeclaredField("systemHelper");
        systemHelperField.setAccessible(true);
        if (systemHelperField.get(action) == null) {
            systemHelperField.set(action, ComponentUtil.getSystemHelper());
        }

        final Field fessConfigField = FessBaseAction.class.getDeclaredField("fessConfig");
        fessConfigField.setAccessible(true);
        if (fessConfigField.get(action) == null) {
            fessConfigField.set(action, ComponentUtil.getFessConfig());
        }

        return action;
    }

    /**
     * Asserts the endpoint answered with the OK status. The status is read reflectively because
     * {@code ApiResponse} exposes no getter for it.
     *
     * @param response the response returned by {@code put$index}
     * @throws Exception if the reflective read fails
     */
    private void assertOkResponse(final JsonResponse<ApiResult> response) throws Exception {
        assertNotNull(response, "put$index must return a response");
        final ApiResult result = response.getJsonResult();
        assertNotNull(result, "put$index must return a JSON result");

        final Field responseField = ApiResult.class.getDeclaredField("response");
        responseField.setAccessible(true);
        final Object apiResponse = responseField.get(result);
        assertNotNull(apiResponse, "the JSON result must carry a response");

        final Field statusField = ApiResult.ApiResponse.class.getDeclaredField("status");
        statusField.setAccessible(true);
        assertEquals(ApiResult.Status.OK.getId(), ((Integer) statusField.get(apiResponse)).intValue());
    }
}
