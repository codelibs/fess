/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.Constants;
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
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.UpgradeUtil;
import org.codelibs.opensearch.runner.net.OpenSearchCurl;
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

    private static final String VERSION_13_16 = "13.16";

    private static final String VERSION_13_17 = "13.17";

    private static final String VERSION_14_0 = "14.0";

    private static final String VERSION_14_1 = "14.1";

    private static final String VERSION_14_2 = "14.2";

    private static final String VERSION_14_3 = "14.3";

    private static final String VERSION_14_4 = "14.4";

    private static final String VERSION_14_5 = "14.5";

    private static final String VERSION_14_6 = "14.6";

    private static final String VERSION_14_7 = "14.7";

    private static final String VERSION_14_8 = "14.8";

    private static final String VERSION_14_9 = "14.9";

    private static final String VERSION_14_10 = "14.10";

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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
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
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_15, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_16.equals(form.targetVersion)) {
            try {
                upgradeFrom13_16();
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_16, e.getLocalizedMessage()));
            }
        } else if (VERSION_13_17.equals(form.targetVersion)) {
            try {
                upgradeFrom13_17();
                upgradeFrom14_0();
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_13_17, e.getLocalizedMessage()));
            }
        } else if (VERSION_14_0.equals(form.targetVersion)) {
            try {
                upgradeFrom14_0();
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_14_0, e.getLocalizedMessage()));
            }
        } else if (VERSION_14_1.equals(form.targetVersion)) {
            try {
                upgradeFrom14_1();
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_14_1, e.getLocalizedMessage()));
            }
        } else if (VERSION_14_2.equals(form.targetVersion)) {
            try {
                upgradeFrom14_2();
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_14_2, e.getLocalizedMessage()));
            }
        } else if (VERSION_14_3.equals(form.targetVersion)) {
            try {
                upgradeFrom14_3();
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_14_3, e.getLocalizedMessage()));
            }
        } else if (VERSION_14_4.equals(form.targetVersion)) {
            try {
                upgradeFrom14_4();
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_14_4, e.getLocalizedMessage()));
            }
        } else if (VERSION_14_5.equals(form.targetVersion)) {
            try {
                upgradeFrom14_5();
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_14_5, e.getLocalizedMessage()));
            }
        } else if (VERSION_14_6.equals(form.targetVersion)) {
            try {
                upgradeFrom14_6();
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_14_6, e.getLocalizedMessage()));
            }
        } else if (VERSION_14_7.equals(form.targetVersion)) {
            try {
                upgradeFrom14_7();
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_14_7, e.getLocalizedMessage()));
            }
        } else if (VERSION_14_8.equals(form.targetVersion)) {
            try {
                upgradeFrom14_8();
                upgradeFrom14_9();
                upgradeFrom14_10();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_14_8, e.getLocalizedMessage()));
            }
        } else if (VERSION_14_9.equals(form.targetVersion)) {
            try {
                upgradeFrom14_9();
                upgradeFrom14_10();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_14_9, e.getLocalizedMessage()));
            }
        } else if (VERSION_14_10.equals(form.targetVersion)) {
            try {
                upgradeFrom14_10();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_14_10, e.getLocalizedMessage()));
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

    private void upgradeFrom13_16() {
        // nothing
    }

    private void upgradeFrom13_17() {
        final String[] configIndices = { //
                "fess_config.access_token", //
                "fess_config.bad_word", //
                "fess_config.boost_document_rule", //
                "fess_config.crawling_info", //
                "fess_config.crawling_info_param", //
                "fess_config.data_config", //
                "fess_config.duplicate_host", //
                "fess_config.elevate_word", //
                "fess_config.elevate_word_to_label", //
                "fess_config.failure_url", //
                "fess_config.file_authentication", //
                "fess_config.file_config", //
                "fess_config.job_log", //
                "fess_config.key_match", //
                "fess_config.label_type", //
                "fess_config.path_mapping", //
                "fess_config.related_content", //
                "fess_config.related_query", //
                "fess_config.request_header", //
                "fess_config.role_type", //
                "fess_config.scheduled_job", //
                "fess_config.thumbnail_queue", //
                "fess_config.web_authentication", //
                "fess_config.web_config", //
                "fess_user.group", //
                "fess_user.role", //
                "fess_user.user", //
                "configsync", //
        };
        final String[] crawlerIndices = { ".crawler.data", //
                ".crawler.filter", //
                ".crawler.queue", //
        };
        final SearchEngineClient client = ComponentUtil.getSearchEngineClient();
        for (final String index : configIndices) {
            final String oldIndex = "." + index;
            if (client.existsIndex(oldIndex) && client.existsIndex(index)) {
                logger.info("Copying from {} to {}", oldIndex, index);
                if (!client.reindex(oldIndex, index, false)) {
                    logger.warn("Failed to copy from {} to {}", oldIndex, index);
                }
            } else if (logger.isDebugEnabled()) {
                if (!client.existsIndex(oldIndex)) {
                    logger.debug("{} does not exist.", oldIndex);
                }
                if (!client.existsIndex(index)) {
                    logger.debug("{} does not exist.", index);
                }
            }
        }
        for (final String index : crawlerIndices) {
            if (client.existsIndex(index)) {
                if (client.deleteIndex(index)) {
                    logger.warn("Deleted {}.", index);
                } else {
                    logger.warn("Failed to delete {}.", index);
                }
            } else if (logger.isDebugEnabled()) {
                logger.debug("{} does not exist.", index);
            }
        }
    }

    private void upgradeFrom14_0() {
        // nothing
    }

    private void upgradeFrom14_1() {
        // nothing
    }

    private void upgradeFrom14_2() {
        // nothing
    }

    private void upgradeFrom14_3() {
        // nothing
    }

    private void upgradeFrom14_4() {
        // nothing
    }

    private void upgradeFrom14_5() {
        // nothing
    }

    private void upgradeFrom14_6() {
        // nothing
    }

    private void upgradeFrom14_7() {
        // nothing
    }

    private void upgradeFrom14_8() {
        // nothing
    }

    private void upgradeFrom14_9() {
        // update mapping text files
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final String fesenType = fessConfig.getFesenType();
        switch (fesenType) {
        case Constants.FESEN_TYPE_CLOUD:
        case Constants.FESEN_TYPE_AWS:
            // nothing
            break;
        default:
            for (final String path : getDictionaryPaths()) {
                if (path.endsWith("mapping.txt")) {
                    logger.debug("Updating {}", path);
                    final String content = getDictionaryContent(path);
                    if (content != null) {
                        sendDictionaryContent(path, StreamUtil.split(content, "\n")
                                .get(stream -> stream.map(s -> s.replaceFirst("#.*", StringUtil.EMPTY)).collect(Collectors.joining("\n"))));
                    }
                }
            }
            try (CurlResponse response = ComponentUtil.getCurlHelper().post("/_configsync/flush").execute()) {
                if (response.getHttpStatusCode() == 200) {
                    logger.info("Flushed config files.");
                } else {
                    logger.warn("Failed to flush config files.");
                }
            } catch (final Exception e) {
                logger.warn("Failed to flush config files.", e);
            }
            break;
        }

    }

    private void upgradeFrom14_10() {
        // nothing
    }

    private String[] getDictionaryPaths() {
        try (CurlResponse response = ComponentUtil.getCurlHelper().get("/_configsync/file").param("size", "1000").execute()) {
            if (response.getHttpStatusCode() == 200) {
                final Map<String, Object> contentMap = response.getContent(OpenSearchCurl.jsonParser());
                if (contentMap.get("path") instanceof final List<?> pathList) {
                    return pathList.stream().map(Object::toString).toArray(n -> new String[n]);
                }
            } else if (response.getContentException() != null) {
                logger.warn("ConfigSync request failed.", response.getContentException());
            } else {
                logger.warn("ConfigSync request failed. The response is {}", response.getContentAsString());
            }
        } catch (final IOException e) {
            logger.warn("ConfigSync request failed.", e);
        }
        return new String[0];
    }

    private String getDictionaryContent(final String path) {
        try (CurlResponse response = ComponentUtil.getCurlHelper().get("/_configsync/file").param("path", path).execute()) {
            if (response.getHttpStatusCode() == 200) {
                return response.getContentAsString();
            }
            if (response.getContentException() != null) {
                logger.warn("{} is invalid path.", path, response.getContentException());
            } else {
                logger.warn("{} is invalid path. The response is {}", path, response.getContentAsString());
            }
        } catch (final IOException e) {
            logger.warn("{} is invalid path.", path, e);
        }
        return null;
    }

    private void sendDictionaryContent(final String path, final String content) {
        try (CurlResponse response = ComponentUtil.getCurlHelper().post("/_configsync/file").param("path", path).body(content).execute()) {
            if (response.getHttpStatusCode() == 200) {
                logger.info("Updated {}", path);
            } else if (response.getContentException() != null) {
                logger.warn("{} is invalid path.", path, response.getContentException());
            } else {
                logger.warn("{} is invalid path. The response is {}", path, response.getContentAsString());
            }
        } catch (final IOException e) {
            logger.warn("{} is invalid path.", path, e);
        }
    }

    private void upgradeFromAll() {
        // nothing
    }

}
