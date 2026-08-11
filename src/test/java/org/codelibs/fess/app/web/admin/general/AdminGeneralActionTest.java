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
package org.codelibs.fess.app.web.admin.general;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.ldap.LdapManager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class AdminGeneralActionTest extends UnitFessTestCase {

    /**
     * updateConfig writes every general setting to the shared system properties, so this test
     * needs its own container to keep those values from leaking into other test classes.
     *
     * @return true to create the container for each test
     */
    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Override
    public void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new LdapManager(), "ldapManager");
        // updateConfig refreshes design files and re-reads app values, which are unrelated to
        // the stored properties under test and pull in further components.
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

    @Test
    public void test_updateConfig_resultCollapsed_cloud_keepsStoredValueWhenAbsent() {
        assertResultCollapsedAfterUpdate(Constants.FESEN_TYPE_CLOUD, Constants.TRUE, null, Constants.TRUE);
    }

    @Test
    public void test_updateConfig_resultCollapsed_cloud_keepsStoredValueWhenFalse() {
        assertResultCollapsedAfterUpdate(Constants.FESEN_TYPE_CLOUD, Constants.TRUE, Constants.FALSE, Constants.TRUE);
    }

    @Test
    public void test_updateConfig_resultCollapsed_aws_keepsStoredValueWhenAbsent() {
        assertResultCollapsedAfterUpdate(Constants.FESEN_TYPE_AWS, Constants.TRUE, null, Constants.TRUE);
    }

    @Test
    public void test_updateConfig_resultCollapsed_default_appliesUncheckedValue() {
        assertResultCollapsedAfterUpdate("default", Constants.TRUE, null, Constants.FALSE);
    }

    @Test
    public void test_updateConfig_resultCollapsed_default_appliesCheckedValue() {
        assertResultCollapsedAfterUpdate("default", Constants.FALSE, Constants.TRUE, Constants.TRUE);
    }

    @Test
    public void test_updateConfig_resultCollapsed_unknownType_appliesUncheckedValue() {
        assertResultCollapsedAfterUpdate("unknown", Constants.TRUE, null, Constants.FALSE);
    }

    @Test
    public void test_updateConfig_spnegoAllowedRealms_roundTrip() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final EditForm form = createEditForm();
        form.spnegoAllowedRealms = "TRUSTED.EXAMPLE,PARTNER.EXAMPLE";
        AdminGeneralAction.updateConfig(fessConfig, form);
        assertEquals("TRUSTED.EXAMPLE,PARTNER.EXAMPLE", ComponentUtil.getSystemProperties().getProperty("spnego.allowed.realms"));

        // The value must come back into the form, otherwise the next save would silently clear it.
        final EditForm reloaded = new EditForm();
        AdminGeneralAction.updateForm(fessConfig, reloaded);
        assertEquals("TRUSTED.EXAMPLE,PARTNER.EXAMPLE", reloaded.spnegoAllowedRealms);
    }

    @Test
    public void test_updateConfig_spnegoPreauthPassword_canBeCleared() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final EditForm form = createEditForm();
        form.spnegoPreauthPassword = "secret";
        AdminGeneralAction.updateConfig(fessConfig, form);
        assertEquals("secret", ComponentUtil.getSystemProperties().getProperty("spnego.preauth.password"));

        // Clearing the field must remove the key. The SPNEGO library only uses a keytab when both
        // the pre-authentication user name and password are empty, so a password that cannot be
        // cleared leaves a keytab configuration unreachable from this screen.
        form.spnegoPreauthPassword = null;
        AdminGeneralAction.updateConfig(fessConfig, form);
        assertNull(ComponentUtil.getSystemProperties().getProperty("spnego.preauth.password"));

        // An empty string reaches updateConfig through the API, which does not map "" to null.
        form.spnegoPreauthPassword = "secret";
        AdminGeneralAction.updateConfig(fessConfig, form);
        form.spnegoPreauthPassword = "";
        AdminGeneralAction.updateConfig(fessConfig, form);
        assertNull(ComponentUtil.getSystemProperties().getProperty("spnego.preauth.password"));
    }

    @Test
    public void test_updateConfig_spnegoPreauthPassword_maskKeepsStoredValue() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final EditForm form = createEditForm();
        form.spnegoPreauthPassword = "secret";
        AdminGeneralAction.updateConfig(fessConfig, form);

        // updateForm renders a mask instead of the stored password, and submitting it back must
        // leave the stored value alone rather than overwrite it with the mask.
        final EditForm reloaded = createEditForm();
        AdminGeneralAction.updateForm(fessConfig, reloaded);
        AdminGeneralAction.updateConfig(fessConfig, reloaded);
        assertEquals("secret", ComponentUtil.getSystemProperties().getProperty("spnego.preauth.password"));
    }

    @Test
    public void test_updateForm_legacyEntraIdSettingsSurviveAnUpdate() {
        // A deployment configured only through the legacy aad.* keys still works: both
        // EntraIdAuthenticator and FessProp fall back to them and no migration exists. updateForm
        // read only the entraid.* keys, so it rendered the shipped defaults for the four settings
        // that have a non-empty one, and updateConfig then wrote those defaults to the entraid.*
        // keys -- which every getter prefers. Opening this screen for an unrelated change and
        // pressing Update therefore moved a sovereign-cloud tenant onto the commercial cloud,
        // reset a tuned state TTL, put the permission field back to mail and re-enabled domain
        // services, all without a log line.
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        fessConfig.setSystemProperty("aad.authority", "https://login.microsoftonline.us/");
        fessConfig.setSystemProperty("aad.state.ttl", "600");
        fessConfig.setSystemProperty("aad.permission.fields", "userPrincipalName");
        fessConfig.setSystemProperty("aad.use.ds", Constants.FALSE);
        fessConfig.setSystemProperty("aad.tenant", "legacy-tenant");
        fessConfig.setSystemProperty("aad.client.id", "legacy-client-id");
        fessConfig.setSystemProperty("aad.client.secret", "legacy-client-secret");

        final EditForm form = createEditForm();
        AdminGeneralAction.updateForm(fessConfig, form);

        // The screen must show what is actually in effect, not the shipped default.
        assertEquals("https://login.microsoftonline.us/", form.entraidAuthority);
        assertEquals("600", form.entraidStateTtl);
        assertEquals("userPrincipalName", form.entraidPermissionFields);
        assertEquals(Constants.FALSE, form.entraidUseDs);
        assertEquals("legacy-tenant", form.entraidTenant);
        // A legacy-only deployment does have credentials; an empty box would say otherwise.
        assertTrue("entraidClientId was rendered empty", StringUtil.isNotBlank(form.entraidClientId));
        assertTrue("entraidClientSecret was rendered empty", StringUtil.isNotBlank(form.entraidClientSecret));

        AdminGeneralAction.updateConfig(fessConfig, form);

        // Saving is now a migration of the legacy values into the new keys, not a clobber.
        assertEquals("https://login.microsoftonline.us/", ComponentUtil.getSystemProperties().getProperty("entraid.authority"));
        assertEquals("600", ComponentUtil.getSystemProperties().getProperty("entraid.state.ttl"));
        assertEquals("userPrincipalName", ComponentUtil.getSystemProperties().getProperty("entraid.permission.fields"));
        assertEquals(Constants.FALSE, ComponentUtil.getSystemProperties().getProperty("entraid.use.ds"));

        // What the consumers see is unchanged by the round trip.
        assertEquals("userPrincipalName", fessConfig.getEntraIdPermissionFields()[0]);
        assertFalse(fessConfig.isEntraIdUseDomainServices());

        // The masked credential fields go back as the mask, which updateConfig skips, so the
        // legacy secrets stay where they are instead of being replaced by "**********".
        assertEquals("legacy-client-id", ComponentUtil.getSystemProperties().getProperty("aad.client.id"));
        assertEquals("legacy-client-secret", ComponentUtil.getSystemProperties().getProperty("aad.client.secret"));
        assertNull(ComponentUtil.getSystemProperties().getProperty("entraid.client.id"));
        assertNull(ComponentUtil.getSystemProperties().getProperty("entraid.client.secret"));
    }

    @Test
    public void test_isSpnegoNtlmPromptUnsupported_spnego_rejectsNtlmPromptWithoutBasic() {
        // The library requires Basic auth to be available before it can downgrade an NTLM token.
        assertTrue(AdminGeneralAction.isSpnegoNtlmPromptUnsupported(createSpnegoForm("spnego", Constants.FALSE, Constants.TRUE)));
    }

    @Test
    public void test_isSpnegoNtlmPromptUnsupported_spnego_allowsNtlmPromptWithBasic() {
        assertFalse(AdminGeneralAction.isSpnegoNtlmPromptUnsupported(createSpnegoForm("spnego", Constants.TRUE, Constants.TRUE)));
    }

    @Test
    public void test_isSpnegoNtlmPromptUnsupported_spnego_allowsNtlmPromptDisabled() {
        assertFalse(AdminGeneralAction.isSpnegoNtlmPromptUnsupported(createSpnegoForm("spnego", Constants.FALSE, Constants.FALSE)));
    }

    @Test
    public void test_isSpnegoNtlmPromptUnsupported_otherSsoType_leavesDormantSettingsAlone() {
        // sso.type=none is the default, so a stored combination that no authenticator ever reads
        // must not block saving an unrelated setting on this screen.
        assertFalse(AdminGeneralAction.isSpnegoNtlmPromptUnsupported(createSpnegoForm(Constants.NONE, Constants.FALSE, Constants.TRUE)));
        assertFalse(AdminGeneralAction.isSpnegoNtlmPromptUnsupported(createSpnegoForm("saml", Constants.FALSE, Constants.TRUE)));
    }

    @Test
    public void test_isSpnegoNtlmPromptUnsupported_missingSsoType_isNotSpnego() {
        assertFalse(AdminGeneralAction.isSpnegoNtlmPromptUnsupported(createSpnegoForm(null, Constants.FALSE, Constants.TRUE)));
        assertFalse(AdminGeneralAction.isSpnegoNtlmPromptUnsupported(createSpnegoForm(StringUtil.EMPTY, Constants.FALSE, Constants.TRUE)));
        assertFalse(AdminGeneralAction.isSpnegoNtlmPromptUnsupported(createSpnegoForm(" ", Constants.FALSE, Constants.TRUE)));
    }

    @Test
    public void test_isSpnegoNtlmPromptUnsupported_switchingToSpnegoIsStillRejected() {
        // The gate reads the submitted type, so turning SPNEGO on while the stored checkboxes hold
        // the unsupported combination is rejected instead of being saved into a broken login.
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final EditForm stored = createEditForm();
        stored.ssoType = Constants.NONE;
        stored.spnegoAllowBasic = Constants.FALSE;
        stored.spnegoPromptNtlm = Constants.TRUE;
        AdminGeneralAction.updateConfig(fessConfig, stored);

        final EditForm form = createEditForm();
        AdminGeneralAction.updateForm(fessConfig, form);
        assertFalse(AdminGeneralAction.isSpnegoNtlmPromptUnsupported(form));

        form.ssoType = "spnego";
        assertTrue(AdminGeneralAction.isSpnegoNtlmPromptUnsupported(form));
    }

    /**
     * Runs updateConfig for the given search engine type and asserts the stored property value.
     * The stored property is read back directly because isResultCollapsed() forces false for
     * cloud and aws and therefore cannot observe what was actually written.
     *
     * @param fesenType the search engine type
     * @param storedValue the property value before updateConfig
     * @param formValue the resultCollapsed request parameter (null when absent)
     * @param expectedValue the expected property value after updateConfig
     */
    private void assertResultCollapsedAfterUpdate(final String fesenType, final String storedValue, final String formValue,
            final String expectedValue) {
        final FessConfig fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getSearchEngineType() {
                return fesenType;
            }
        };
        ComponentUtil.getSystemProperties().setProperty(Constants.RESULT_COLLAPSED_PROPERTY, storedValue);

        final EditForm form = createEditForm();
        form.resultCollapsed = formValue;
        AdminGeneralAction.updateConfig(fessConfig, form);

        assertEquals("fesenType=" + fesenType + ", resultCollapsed=" + formValue, expectedValue,
                ComponentUtil.getSystemProperties().getProperty(Constants.RESULT_COLLAPSED_PROPERTY));
    }

    /**
     * Creates a form carrying the three fields the SPNEGO NTLM prompt rule correlates. The
     * checkbox fields are always set explicitly, because isCheckboxEnabled reads an absent field
     * as disabled and a fixture that leaves one out could not tell the two apart.
     *
     * @param ssoType the submitted sso.type value
     * @param spnegoAllowBasic the submitted spnegoAllowBasic checkbox value
     * @param spnegoPromptNtlm the submitted spnegoPromptNtlm checkbox value
     * @return the form to pass to isSpnegoNtlmPromptUnsupported
     */
    private EditForm createSpnegoForm(final String ssoType, final String spnegoAllowBasic, final String spnegoPromptNtlm) {
        final EditForm form = createEditForm();
        form.ssoType = ssoType;
        form.spnegoAllowBasic = spnegoAllowBasic;
        form.spnegoPromptNtlm = spnegoPromptNtlm;
        return form;
    }

    /**
     * Creates a form with the numeric fields filled in, since updateConfig unboxes them.
     *
     * @return the form to pass to updateConfig
     */
    private EditForm createEditForm() {
        final EditForm form = new EditForm();
        form.dayForCleanup = 0;
        form.crawlingThreadCount = 1;
        form.failureCountThreshold = 0;
        form.purgeSearchLogDay = 0;
        form.purgeJobLogDay = 0;
        form.purgeUserInfoDay = 0;
        form.purgeSuggestSearchLogDay = 0;
        return form;
    }
}
