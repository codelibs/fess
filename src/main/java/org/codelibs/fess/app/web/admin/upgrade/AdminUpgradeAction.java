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
package org.codelibs.fess.app.web.admin.upgrade;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.config.exbhv.DataConfigBhv;
import org.codelibs.fess.opensearch.config.exbhv.ElevateWordBhv;
import org.codelibs.fess.opensearch.config.exbhv.FileConfigBhv;
import org.codelibs.fess.opensearch.config.exbhv.LabelTypeBhv;
import org.codelibs.fess.opensearch.config.exbhv.RoleTypeBhv;
import org.codelibs.fess.opensearch.config.exbhv.WebConfigBhv;
import org.codelibs.fess.opensearch.user.exbhv.RoleBhv;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.opensearch.runner.net.OpenSearchCurl;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

import jakarta.annotation.Resource;

public class AdminUpgradeAction extends FessAdminAction {

    public static final String ROLE = "admin-upgrade";

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LogManager.getLogger(AdminUpgradeAction.class);

    private static final String VERSION_14_18 = "14.18";

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

        if (VERSION_14_18.equals(form.targetVersion)) {
            try {
                upgradeFrom14_18();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_14_18, e.getLocalizedMessage()));
            }
        } else {
            saveError(messages -> messages.addErrorsUnknownVersionForUpgrade(GLOBAL));
        }
        return redirect(getClass());
    }

    private void upgradeFrom14_18() {
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