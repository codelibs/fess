/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;

import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.mylasta.direction.FessConfig.SimpleImpl;
import org.codelibs.fess.util.ComponentUtil;
import org.elasticsearch.action.ActionListener;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminMaintenanceAction extends FessAdminAction {

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LoggerFactory.getLogger(AdminMaintenanceAction.class);

    private static final String[] ES_CAT_NAMES = new String[] { "aliases", "allocation", "count", "fielddata", "health", "indices",
            "master", "nodeattrs", "nodes", "pending_tasks", "plugins", "recovery", "repositories", "thread_pool", "shards", "segments",
            "snapshots", "templates" };

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
        return asHtml(path_AdminMaintenance_AdminMaintenanceJsp).useForm(ActionForm.class, op -> op.setup(f -> {
            f.replaceAliases = Constants.ON;
            f.resetDictionaries = Constants.ON;
        }));
    }

    @Execute
    public HtmlResponse reindexOnly(final ActionForm form) {
        validate(form, messages -> {}, this::asIndexHtml);
        verifyToken(this::asIndexHtml);
        if (startReindex(isCheckboxEnabled(form.replaceAliases), isCheckboxEnabled(form.resetDictionaries), form.numberOfShardsForDoc,
                form.autoExpandReplicasForDoc)) {
            saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));
        }
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse reloadDocIndex(final ActionForm form) {
        validate(form, messages -> {}, this::asIndexHtml);
        verifyToken(this::asIndexHtml);
        final String docIndex = fessConfig.getIndexDocumentUpdateIndex();
        fessEsClient
                .admin()
                .indices()
                .prepareClose(docIndex)
                .execute(
                        ActionListener.wrap(
                                res -> {
                                    logger.info("Close " + docIndex);
                                    fessEsClient
                                            .admin()
                                            .indices()
                                            .prepareOpen(docIndex)
                                            .execute(
                                                    ActionListener.wrap(res2 -> logger.info("Open " + docIndex),
                                                            e -> logger.warn("Failed to open " + docIndex, e)));
                                }, e -> logger.warn("Failed to close " + docIndex, e)));
        saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse clearCrawlerIndex(final ActionForm form) {
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

    @Execute
    public ActionResponse downloadLogs(final ActionForm form) {
        validate(form, messages -> {}, this::asIndexHtml);
        verifyTokenKeep(this::asIndexHtml);

        final String diagnosticId = "log" + new SimpleDateFormat("yyyyMMddHHmm").format(ComponentUtil.getSystemHelper().getCurrentTime());
        return asStream(diagnosticId + ".zip").contentTypeOctetStream().stream(out -> {
            try (ZipOutputStream zos = new ZipOutputStream(out.stream())) {
                writeLogFiles(zos, diagnosticId);
                writeSystemProperties(zos, diagnosticId);
                writeFessBasicConfig(zos, diagnosticId);
                writeFessConfig(zos, diagnosticId);
                writeElasticsearchCat(zos, diagnosticId);
                writeElasticsearchJson(zos, diagnosticId);
            }
        });
    }

    protected void writeElasticsearchJson(final ZipOutputStream zos, final String id) {
        writeElastisearchJsonApi(zos, id, "cluster", "health");
        writeElastisearchJsonApi(zos, id, "cluster", "state");
        writeElastisearchJsonApi(zos, id, "cluster", "stats");
        writeElastisearchJsonApi(zos, id, "cluster", "pending_tasks");
        writeElastisearchJsonApi(zos, id, "nodes", "stats");
        writeElastisearchJsonApi(zos, id, "nodes", "_all");
        writeElastisearchJsonApi(zos, id, "nodes", "usage");
        writeElastisearchJsonApi(zos, id, "remote", "info");
        writeElastisearchJsonApi(zos, id, "tasks", "");
        writeElastisearchJsonApi(zos, id, "nodes", "hot_threads");
    }

    protected void writeElastisearchJsonApi(final ZipOutputStream zos, final String id, final String v1, final String v2) {
        final ZipEntry entry = new ZipEntry(id + "/es_" + v1 + "_" + v2 + ".json");
        try {
            zos.putNextEntry(entry);
            try (CurlResponse response = ComponentUtil.getCurlHelper().get("/_" + v1 + "/" + v2).execute()) {
                CopyUtil.copy(response.getContentAsStream(), zos);
            }
        } catch (final Exception e) {
            logger.warn("Failed to access /_" + v1 + "/" + v2, e);
        }
    }

    protected void writeElasticsearchCat(final ZipOutputStream zos, final String id) {
        Arrays.stream(ES_CAT_NAMES).forEach(name -> {
            final ZipEntry entry = new ZipEntry(id + "/es_cat_" + name + ".txt");
            try {
                zos.putNextEntry(entry);
                try (CurlResponse response = ComponentUtil.getCurlHelper().get("/_cat/" + name).param("v", "").execute()) {
                    CopyUtil.copy(response.getContentAsStream(), zos);
                }
            } catch (final Exception e) {
                logger.warn("Failed to access /_cat/" + name, e);
            }
        });
    }

    protected void writeFessConfig(final ZipOutputStream zos, final String id) {
        if (fessConfig instanceof SimpleImpl) {
            final Properties prop = new Properties();
            ((SimpleImpl) fessConfig).keySet().stream().forEach(k -> prop.setProperty(k, fessConfig.get(k)));

            final ZipEntry entry = new ZipEntry(id + "/fess_config.properties");
            try {
                zos.putNextEntry(entry);
                prop.store(zos, getHostInfo());
            } catch (final IOException e) {
                logger.warn("Failed to access system.properties.", e);
            }
        }
    }

    protected void writeFessBasicConfig(final ZipOutputStream zos, final String id) {
        final ZipEntry entry = new ZipEntry(id + "/fess_basic_config.bulk");
        try {
            zos.putNextEntry(entry);
            try (CurlResponse response =
                    ComponentUtil.getCurlHelper().get("/.fess_basic_config/_data").param("format", "json")
                            .param("scroll", fessConfig.getIndexScrollSearchTimeout()).execute()) {
                CopyUtil.copy(response.getContentAsStream(), zos);
            }
        } catch (final IOException e) {
            logger.warn("Failed to access system.properties.", e);
        }
    }

    protected void writeSystemProperties(final ZipOutputStream zos, final String id) {
        final ZipEntry entry = new ZipEntry(id + "/system.properties");
        try {
            zos.putNextEntry(entry);
            ComponentUtil.getSystemProperties().store(zos, getHostInfo());
        } catch (final IOException e) {
            logger.warn("Failed to access system.properties.", e);
        }
    }

    protected void writeLogFiles(final ZipOutputStream zos, final String id) {
        final String logFilePath = systemHelper.getLogFilePath();
        if (StringUtil.isNotBlank(logFilePath)) {
            final Path logDirPath = Paths.get(logFilePath);
            try (Stream<Path> stream = Files.list(logDirPath)) {
                stream.filter(entry -> isLogFilename(entry.getFileName().toString())).forEach(filePath -> {
                    final ZipEntry entry = new ZipEntry(id + "/" + filePath.getFileName().toString());
                    try {
                        zos.putNextEntry(entry);
                        final long len = Files.copy(filePath, zos);
                        if (logger.isDebugEnabled()) {
                            logger.debug(filePath.getFileName() + ": " + len);
                        }
                    } catch (final IOException e) {
                        logger.warn("Failed to access " + filePath, e);
                    }
                });
            } catch (final Exception e) {
                logger.warn("Failed to access log files.", e);
            }
        }
    }

    protected String getHostInfo() {
        final StringBuilder buf = new StringBuilder();
        try {
            final InetAddress ia = InetAddress.getLocalHost();
            final String hostname = ia.getHostName();
            if (StringUtil.isNotBlank(hostname)) {
                buf.append(hostname);
            }
            final String ip = ia.getHostAddress();
            if (StringUtil.isNotBlank(ip)) {
                if (buf.length() > 0) {
                    buf.append(" : ");
                }
                buf.append(ip);
            }
        } catch (final Exception e) {
            // ignore
        }
        return buf.toString();
    }

    protected boolean isLogFilename(final String name) {
        return name.endsWith(".log") || name.endsWith(".log.gz");
    }

    protected boolean startReindex(final boolean replaceAliases, final boolean resetDictionaries, final String numberOfShards,
            final String autoExpandReplicas) {
        final String docIndex = "fess";
        final String fromIndex = fessConfig.getIndexDocumentUpdateIndex();
        final String toIndex = docIndex + "." + new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        if (fessEsClient.createIndex(docIndex, toIndex, numberOfShards, autoExpandReplicas, resetDictionaries)) {
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