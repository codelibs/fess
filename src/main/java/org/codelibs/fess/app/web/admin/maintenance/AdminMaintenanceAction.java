/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.maintenance;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.client.FessEsClient;
import org.elasticsearch.action.ActionListener;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminMaintenanceAction extends FessAdminAction {

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LoggerFactory.getLogger(AdminMaintenanceAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //

    @Resource
    protected FessEsClient fessEsClient;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameMaintenance()));
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
        return asHtml(path_AdminMaintenance_AdminMaintenanceJsp).useForm(UpgradeForm.class);
    }

    @Execute
    public HtmlResponse reindexOnly(final UpgradeForm form) {
        validate(form, messages -> {}, this::asIndexHtml);
        verifyToken(this::asIndexHtml);
        if (startReindex(isCheckboxEnabled(form.replaceAliases), form.numberOfShardsForDoc, form.autoExpandReplicasForDoc)) {
            saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));
        }
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse clearCrawlerIndex(final UpgradeForm form) {
        validate(form, messages -> {}, this::asIndexHtml);
        verifyToken(this::asIndexHtml);
        fessEsClient
                .admin()
                .indices()
                .prepareDelete(//
                        fessConfig.getIndexDocumentCrawlerIndex() + ".queue", //
                        fessConfig.getIndexDocumentCrawlerIndex() + ".data", //
                        fessConfig.getIndexDocumentCrawlerIndex() + ".filter")
                .execute(
                        ActionListener.wrap(res -> logger.info("Deleted .crawler indices."),
                                e -> logger.warn("Failed to delete .crawler.* indices.", e)));
        saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));
        return redirect(getClass());
    }

    private boolean startReindex(final boolean replaceAliases, final String numberOfShards, final String autoExpandReplicas) {
        final String docIndex = "fess";
        final String fromIndex = fessConfig.getIndexDocumentUpdateIndex();
        final String toIndex = docIndex + "." + new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        if (fessEsClient.createIndex(docIndex, toIndex, numberOfShards, autoExpandReplicas)) {
            fessEsClient.admin().cluster().prepareHealth(toIndex).setWaitForYellowStatus().execute(ActionListener.wrap(response -> {
                fessEsClient.addMapping(docIndex, "doc", toIndex);
                fessEsClient.reindex(fromIndex, toIndex, replaceAliases);
                if (replaceAliases && !fessEsClient.updateAlias(toIndex)) {
                    logger.warn("Failed to update aliases for " + fromIndex + " and " + toIndex);
                }
            }, e -> logger.warn("Failed to reindex from " + fromIndex + " to " + toIndex, e)));
            return true;
        }
        saveError(messages -> messages.addErrorsFailedToReindex(GLOBAL, fromIndex, toIndex));
        return false;
    }

}