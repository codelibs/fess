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
    public void test_isSpnegoNtlmPromptUnsupported() {
        final EditForm form = createEditForm();

        // The library requires Basic auth to be available before it can downgrade an NTLM token.
        form.spnegoAllowBasic = null;
        form.spnegoPromptNtlm = Constants.TRUE;
        assertTrue(AdminGeneralAction.isSpnegoNtlmPromptUnsupported(form));

        form.spnegoAllowBasic = Constants.TRUE;
        assertFalse(AdminGeneralAction.isSpnegoNtlmPromptUnsupported(form));

        form.spnegoAllowBasic = null;
        form.spnegoPromptNtlm = null;
        assertFalse(AdminGeneralAction.isSpnegoNtlmPromptUnsupported(form));
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
