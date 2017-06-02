/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import java.util.function.Consumer;

import javax.annotation.Resource;

import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.config.exbhv.DataConfigBhv;
import org.codelibs.fess.es.config.exbhv.DataConfigToRoleBhv;
import org.codelibs.fess.es.config.exbhv.ElevateWordBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigToRoleBhv;
import org.codelibs.fess.es.config.exbhv.LabelToRoleBhv;
import org.codelibs.fess.es.config.exbhv.LabelTypeBhv;
import org.codelibs.fess.es.config.exbhv.RoleTypeBhv;
import org.codelibs.fess.es.config.exbhv.WebConfigBhv;
import org.codelibs.fess.es.config.exbhv.WebConfigToRoleBhv;
import org.codelibs.fess.es.user.exbhv.RoleBhv;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsResponse.FieldMappingMetaData;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.IndicesAdminClient;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminUpgradeAction extends FessAdminAction {

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LoggerFactory.getLogger(AdminUpgradeAction.class);

    private static final String VERSION_11_0 = "11.0";

    private static final String VERSION_11_1 = "11.1";

    // ===================================================================================
    //                                                                           Attribute
    //
    @Resource
    protected FessConfig fessConfig;

    @Resource
    protected RoleBhv roleBhv;

    @Resource
    protected RoleTypeBhv roleTypeBhv;

    @Resource
    protected LabelToRoleBhv labelToRoleBhv;

    @Resource
    protected LabelTypeBhv labelTypeBhv;

    @Resource
    protected WebConfigToRoleBhv webConfigToRoleBhv;

    @Resource
    protected WebConfigBhv webConfigBhv;

    @Resource
    protected FileConfigToRoleBhv fileConfigToRoleBhv;

    @Resource
    protected FileConfigBhv fileConfigBhv;

    @Resource
    protected DataConfigToRoleBhv dataConfigToRoleBhv;

    @Resource
    protected DataConfigBhv dataConfigBhv;

    @Resource
    protected ElevateWordBhv elevateWordBhv;

    @Resource
    protected FessEsClient fessEsClient;

    @Resource
    private ScheduledJobService scheduledJobService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameUpgrade()));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    @Execute
    public HtmlResponse index() {
        saveToken();
        return asIndexHtml();
    }

    private HtmlResponse asIndexHtml() {
        return asHtml(path_AdminUpgrade_AdminUpgradeJsp).useForm(UpgradeForm.class);
    }

    @Execute
    public HtmlResponse upgradeFrom(final UpgradeForm form) {
        validate(form, messages -> {}, () -> {
            return asIndexHtml();
        });
        verifyToken(() -> asIndexHtml());

        if (VERSION_11_0.equals(form.targetVersion)) {
            try {
                upgradeFrom11_0();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessUpgradeFrom(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_11_0, e.getLocalizedMessage()));
            }
        } else if (VERSION_11_1.equals(form.targetVersion)) {
            try {
                upgradeFrom11_0();
                upgradeFrom11_1();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessUpgradeFrom(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_11_1, e.getLocalizedMessage()));
            }
        } else {
            saveError(messages -> messages.addErrorsUnknownVersionForUpgrade(GLOBAL));
        }
        return redirect(getClass());
    }

    private void upgradeFrom11_0() {
    }

    private void upgradeFrom11_1() {
        final IndicesAdminClient indicesClient = fessEsClient.admin().indices();
        final String configIndex = ".fess_config";

        // update mapping
        addFieldMapping(indicesClient, configIndex, "thumbnail_queue", "thumbnail_id",
                "{\"properties\":{\"thumbnail_id\":{\"type\":\"keyword\"}}}");
    }

    private void upgradeFromAll() {
        final IndicesAdminClient indicesClient = fessEsClient.admin().indices();
        final String crawlerIndex = fessConfig.getIndexDocumentCrawlerIndex();

        // .crawler
        if (existsIndex(indicesClient, crawlerIndex, IndicesOptions.fromOptions(false, true, true, true))) {
            deleteIndex(indicesClient, crawlerIndex, response -> {});
        }
    }

    private void addFieldMapping(final IndicesAdminClient indicesClient, final String index, final String type, final String field,
            final String source) {
        final GetFieldMappingsResponse gfmResponse =
                indicesClient.prepareGetFieldMappings(index).addTypes(type).setFields(field).execute().actionGet();
        final FieldMappingMetaData fieldMappings = gfmResponse.fieldMappings(index, type, field);
        if (fieldMappings == null || fieldMappings.isNull()) {
            try {
                final PutMappingResponse pmResponse =
                        indicesClient.preparePutMapping(index).setType(type).setSource(source).execute().actionGet();
                if (!pmResponse.isAcknowledged()) {
                    logger.warn("Failed to add " + field + " to " + index + "/" + type);
                }
            } catch (final Exception e) {
                logger.warn("Failed to add " + field + " to " + index + "/" + type, e);
            }
        }
    }

    private boolean existsIndex(final IndicesAdminClient indicesClient, final String index, final IndicesOptions options) {
        try {
            final IndicesExistsResponse response =
                    indicesClient.prepareExists(index).setIndicesOptions(options).execute().actionGet(fessConfig.getIndexSearchTimeout());
            return response.isExists();
        } catch (final Exception e) {
            // ignore
        }
        return false;
    }

    private void deleteIndex(final IndicesAdminClient indicesClient, final String index, final Consumer<DeleteIndexResponse> comsumer) {
        indicesClient.prepareDelete(index).execute(new ActionListener<DeleteIndexResponse>() {

            @Override
            public void onResponse(final DeleteIndexResponse response) {
                logger.info("Deleted " + index + " index.");
                comsumer.accept(response);
            }

            @Override
            public void onFailure(final Exception e) {
                logger.warn("Failed to delete " + index + " index.", e);
            }
        });
    }
}