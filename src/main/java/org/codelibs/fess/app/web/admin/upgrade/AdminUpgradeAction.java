/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.upgrade;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.client.SearchEngineClient;
import org.codelibs.fess.es.config.exbhv.DataConfigBhv;
import org.codelibs.fess.es.config.exbhv.ElevateWordBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigBhv;
import org.codelibs.fess.es.config.exbhv.LabelTypeBhv;
import org.codelibs.fess.es.config.exbhv.RoleTypeBhv;
import org.codelibs.fess.es.config.exbhv.WebConfigBhv;
import org.codelibs.fess.es.user.exbhv.RoleBhv;
import org.codelibs.fess.util.UpgradeUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

public class AdminUpgradeAction extends FessAdminAction {

    public static final String ROLE = "admin-upgrade";

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LogManager.getLogger(AdminUpgradeAction.class);

    private static final String VERSION_13_0 = "13.0";

    private static final String VERSION_13_1 = "13.1";

    private static final String VERSION_13_2 = "13.2";

    private static final String VERSION_13_3 = "13.3";

    private static final String VERSION_13_4 = "13.4";

    private static final String VERSION_13_5 = "13.5";

    private static final String VERSION_13_6 = "13.6";

    private static final String VERSION_13_7 = "13.7";

    private static final String VERSION_13_8 = "13.8";

    private static final String VERSION_13_9 = "13.9";

    private static final String VERSION_13_10 = "13.10";

    private static final String VERSION_13_11 = "13.11";

    private static final String VERSION_13_12 = "13.12";

    private static final String VERSION_13_13 = "13.13";

    private static final String VERSION_13_14 = "13.14";

    private static final String VERSION_13_15 = "13.15";

    // ===================================================================================
    //                                                                           Attribute
    //
    @Resource
    protected RoleBhv roleBhv;

    @Resource
    protected RoleTypeBhv roleTypeBhv;

    @Resource
    protected LabelTypeBhv labelTypeBhv;

    @Resource
    protected WebConfigBhv webConfigBhv;

    @Resource
    protected FileConfigBhv fileConfigBhv;

    @Resource
    protected DataConfigBhv dataConfigBhv;

    @Resource
    protected ElevateWordBhv elevateWordBhv;

    @Resource
    protected SearchEngineClient searchEngineClient;

    @Resource
    protected ScheduledJobService scheduledJobService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameUpgrade()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        saveToken();
        return asIndexHtml();
    }

    private HtmlResponse asIndexHtml() {
        return asHtml(path_AdminUpgrade_AdminUpgradeJsp).useForm(UpgradeForm.class);
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse upgradeFrom(final UpgradeForm form) {
        validate(form, messages -> {}, this::asIndexHtml);
        verifyToken(this::asIndexHtml);

        if (VERSION_13_0.equals(form.targetVersion)) {
            try {
                upgradeFrom13_0();
                upgradeFrom13_1();
                upgradeFrom13_2();
                upgradeFrom13_3();
                upgradeFrom13_4();
                upgradeFrom13_5();
                upgradeFrom13_6();
                upgradeFrom13_7();
                upgradeFrom13_8();
                upgradeFrom13_10();
                upgradeFrom13_11();
                upgradeFrom13_12();
                upgradeFrom13_13();
                upgradeFrom13_14();
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_0, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_1.equals(form.targetVersion)) {
            try {
                upgradeFrom13_1();
                upgradeFrom13_2();
                upgradeFrom13_3();
                upgradeFrom13_4();
                upgradeFrom13_5();
                upgradeFrom13_6();
                upgradeFrom13_7();
                upgradeFrom13_8();
                upgradeFrom13_9();
                upgradeFrom13_10();
                upgradeFrom13_11();
                upgradeFrom13_12();
                upgradeFrom13_13();
                upgradeFrom13_14();
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_1, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_2.equals(form.targetVersion)) {
            try {
                upgradeFrom13_2();
                upgradeFrom13_3();
                upgradeFrom13_4();
                upgradeFrom13_5();
                upgradeFrom13_6();
                upgradeFrom13_7();
                upgradeFrom13_8();
                upgradeFrom13_9();
                upgradeFrom13_10();
                upgradeFrom13_11();
                upgradeFrom13_12();
                upgradeFrom13_13();
                upgradeFrom13_14();
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_2, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_3.equals(form.targetVersion)) {
            try {
                upgradeFrom13_3();
                upgradeFrom13_4();
                upgradeFrom13_5();
                upgradeFrom13_6();
                upgradeFrom13_7();
                upgradeFrom13_8();
                upgradeFrom13_9();
                upgradeFrom13_10();
                upgradeFrom13_11();
                upgradeFrom13_12();
                upgradeFrom13_13();
                upgradeFrom13_14();
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_3, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_4.equals(form.targetVersion)) {
            try {
                upgradeFrom13_4();
                upgradeFrom13_5();
                upgradeFrom13_6();
                upgradeFrom13_7();
                upgradeFrom13_8();
                upgradeFrom13_9();
                upgradeFrom13_10();
                upgradeFrom13_11();
                upgradeFrom13_12();
                upgradeFrom13_13();
                upgradeFrom13_14();
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_4, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_5.equals(form.targetVersion)) {
            try {
                upgradeFrom13_5();
                upgradeFrom13_6();
                upgradeFrom13_7();
                upgradeFrom13_8();
                upgradeFrom13_9();
                upgradeFrom13_10();
                upgradeFrom13_11();
                upgradeFrom13_12();
                upgradeFrom13_13();
                upgradeFrom13_14();
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_5, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_6.equals(form.targetVersion)) {
            try {
                upgradeFrom13_6();
                upgradeFrom13_7();
                upgradeFrom13_8();
                upgradeFrom13_9();
                upgradeFrom13_10();
                upgradeFrom13_11();
                upgradeFrom13_12();
                upgradeFrom13_13();
                upgradeFrom13_14();
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_6, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_7.equals(form.targetVersion)) {
            try {
                upgradeFrom13_7();
                upgradeFrom13_8();
                upgradeFrom13_9();
                upgradeFrom13_10();
                upgradeFrom13_11();
                upgradeFrom13_12();
                upgradeFrom13_13();
                upgradeFrom13_14();
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_7, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_8.equals(form.targetVersion)) {
            try {
                upgradeFrom13_8();
                upgradeFrom13_9();
                upgradeFrom13_10();
                upgradeFrom13_11();
                upgradeFrom13_12();
                upgradeFrom13_13();
                upgradeFrom13_14();
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_8, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_9.equals(form.targetVersion)) {
            try {
                upgradeFrom13_9();
                upgradeFrom13_10();
                upgradeFrom13_11();
                upgradeFrom13_12();
                upgradeFrom13_13();
                upgradeFrom13_14();
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_9, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_10.equals(form.targetVersion)) {
            try {
                upgradeFrom13_10();
                upgradeFrom13_11();
                upgradeFrom13_12();
                upgradeFrom13_13();
                upgradeFrom13_14();
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_10, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_11.equals(form.targetVersion)) {
            try {
                upgradeFrom13_11();
                upgradeFrom13_12();
                upgradeFrom13_13();
                upgradeFrom13_14();
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_11, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_12.equals(form.targetVersion)) {
            try {
                upgradeFrom13_12();
                upgradeFrom13_13();
                upgradeFrom13_14();
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_12, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_13.equals(form.targetVersion)) {
            try {
                upgradeFrom13_13();
                upgradeFrom13_14();
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_13, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_14.equals(form.targetVersion)) {
            try {
                upgradeFrom13_14();
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_14, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_15.equals(form.targetVersion)) {
            try {
                upgradeFrom13_15();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_15, e.getLocalizedMessage()));
            }
        } else {
            saveError(messages -> messages.addErrorsUnknownVersionForUpgrade(GLOBAL));
        }
        return redirect(getClass());
    }

    private void upgradeFrom13_0() {
        UpgradeUtil.addData(searchEngineClient, ".fess_config.scheduled_job", "label_updater",
                "{\"name\":\"Label Updater\",\"target\":\"all\",\"cronExpression\":\"\",\"scriptType\":\"groovy\",\"scriptData\":\"return container.getComponent(\\\"updateLabelJob\\\").execute();\",\"jobLogging\":false,\"crawler\":false,\"available\":true,\"sortOrder\":11,\"createdBy\":\"system\",\"createdTime\":0,\"updatedBy\":\"system\",\"updatedTime\":0}");
    }

    private void upgradeFrom13_1() {
        // nothing
    }

    private void upgradeFrom13_2() {
        // nothing
    }

    private void upgradeFrom13_3() {
        // nothing
    }

    private void upgradeFrom13_4() {
        // nothing
    }

    private void upgradeFrom13_5() {
        // nothing
    }

    private void upgradeFrom13_6() {
        // nothing
    }

    private void upgradeFrom13_7() {
        // nothing
    }

    private void upgradeFrom13_8() {
        // nothing
    }

    private void upgradeFrom13_9() {
        // nothing
    }

    private void upgradeFrom13_10() {
        // nothing
    }

    private void upgradeFrom13_11() {
        // nothing
    }

    private void upgradeFrom13_12() {
        // nothing
    }

    private void upgradeFrom13_13() {
        // nothing
    }

    private void upgradeFrom13_14() {
        // nothing
    }

    private void upgradeFrom13_15() {
        // nothing
    }

    private void upgradeFromAll() {
        // nothing
    }

}