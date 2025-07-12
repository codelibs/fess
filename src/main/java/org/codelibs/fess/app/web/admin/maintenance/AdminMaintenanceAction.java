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

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.mylasta.direction.FessConfig.SimpleImpl;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.SearchEngineUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.opensearch.core.action.ActionListener;

import jakarta.annotation.Resource;

/**
 * Admin action for maintenance operations including reindexing, log management,
 * and system diagnostics.
 */
public class AdminMaintenanceAction extends FessAdminAction {

    /**
     * Default constructor for AdminMaintenanceAction.
     */
    public AdminMaintenanceAction() {
        super();
    }

    /**
     * Role identifier for admin maintenance operations.
     */
    public static final String ROLE = "admin-maintenance";

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LogManager.getLogger(AdminMaintenanceAction.class);

    private static final String[] CAT_NAMES =
            { "aliases", "allocation", "count", "fielddata", "health", "indices", "master", "nodeattrs", "nodes", "pending_tasks",
                    "plugins", "recovery", "repositories", "thread_pool", "shards", "segments", "snapshots", "templates" };

    // ===================================================================================
    //                                                                           Attribute
    //

    /**
     * Search engine client for performing maintenance operations on indices.
     */
    @Resource
    protected SearchEngineClient searchEngineClient;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameMaintenance()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    /**
     * Displays the main maintenance page.
     *
     * @return HTML response for the maintenance index page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        saveToken();
        return asIndexHtml();
    }

    private HtmlResponse asIndexHtml() {
        return asHtml(path_AdminMaintenance_AdminMaintenanceJsp).useForm(ActionForm.class, op -> op.setup(f -> {
            f.replaceAliases = Constants.ON;
            f.resetDictionaries = null;
        }));
    }

    /**
     * Starts a reindex operation based on the provided form parameters.
     *
     * @param form the action form containing reindex configuration
     * @return HTML response redirecting to the maintenance page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse reindexOnly(final ActionForm form) {
        validate(form, messages -> {}, this::asIndexHtml);
        verifyToken(this::asIndexHtml);
        if (startReindex(isCheckboxEnabled(form.replaceAliases), isCheckboxEnabled(form.resetDictionaries), form.numberOfShardsForDoc,
                form.autoExpandReplicasForDoc)) {
            saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));
        }
        return redirect(getClass());
    }

    /**
     * Reloads the document index by closing and reopening it.
     *
     * @param form the action form (validated but not used for configuration)
     * @return HTML response redirecting to the maintenance page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse reloadDocIndex(final ActionForm form) {
        validate(form, messages -> {}, this::asIndexHtml);
        verifyToken(this::asIndexHtml);
        searchEngineClient.flushConfigFiles(() -> {
            final String docIndex = fessConfig.getIndexDocumentUpdateIndex();
            searchEngineClient.admin().indices().prepareClose(docIndex).execute(ActionListener.wrap(res -> {
                logger.info("Close {}", docIndex);
                searchEngineClient.admin().indices().prepareOpen(docIndex).execute(
                        ActionListener.wrap(res2 -> logger.info("Open {}", docIndex), e -> logger.warn("Failed to open {}", docIndex, e)));
            }, e -> logger.warn("Failed to close {}", docIndex, e)));
        });
        saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));
        return redirect(getClass());
    }

    /**
     * Clears all crawler indices including queue, data, and filter indices.
     *
     * @param form the action form (validated but not used for configuration)
     * @return HTML response redirecting to the maintenance page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse clearCrawlerIndex(final ActionForm form) {
        validate(form, messages -> {}, this::asIndexHtml);
        verifyToken(this::asIndexHtml);
        searchEngineClient.admin().indices().prepareDelete(//
                fessConfig.getIndexDocumentCrawlerIndex() + ".queue", //
                fessConfig.getIndexDocumentCrawlerIndex() + ".data", //
                fessConfig.getIndexDocumentCrawlerIndex() + ".filter")
                .execute(ActionListener.wrap(res -> logger.info("Deleted .crawler indices."),
                        e -> logger.warn("Failed to delete .crawler.* indices.", e)));
        saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));
        return redirect(getClass());
    }

    /**
     * Downloads diagnostic logs and system information as a ZIP file.
     *
     * @param form the action form (validated but not used for configuration)
     * @return streaming response containing the diagnostic ZIP file
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
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
                writeFesenCat(zos, diagnosticId);
                writeFesenJson(zos, diagnosticId);
            }
        });
    }

    /**
     * Writes OpenSearch/Elasticsearch JSON API responses to the ZIP output stream.
     *
     * @param zos the ZIP output stream to write to
     * @param id the diagnostic ID for organizing files in the ZIP
     */
    protected void writeFesenJson(final ZipOutputStream zos, final String id) {
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

    /**
     * Writes a specific OpenSearch/Elasticsearch API response to the ZIP output stream.
     *
     * @param zos the ZIP output stream to write to
     * @param id the diagnostic ID for organizing files in the ZIP
     * @param v1 the first part of the API path (e.g., "cluster", "nodes")
     * @param v2 the second part of the API path (e.g., "health", "stats")
     */
    protected void writeElastisearchJsonApi(final ZipOutputStream zos, final String id, final String v1, final String v2) {
        final ZipEntry entry = new ZipEntry(id + "/es_" + v1 + "_" + v2 + ".json");
        try {
            zos.putNextEntry(entry);
            try (CurlResponse response = ComponentUtil.getCurlHelper().get("/_" + v1 + "/" + v2).execute()) {
                CopyUtil.copy(response.getContentAsStream(), zos);
            }
        } catch (final Exception e) {
            logger.warn("Failed to access /_{}/{}", v1, v2, e);
        }
    }

    /**
     * Writes OpenSearch/Elasticsearch CAT API responses to the ZIP output stream.
     *
     * @param zos the ZIP output stream to write to
     * @param id the diagnostic ID for organizing files in the ZIP
     */
    protected void writeFesenCat(final ZipOutputStream zos, final String id) {
        Arrays.stream(CAT_NAMES).forEach(name -> {
            final ZipEntry entry = new ZipEntry(id + "/es_cat_" + name + ".txt");
            try {
                zos.putNextEntry(entry);
                try (CurlResponse response = ComponentUtil.getCurlHelper().get("/_cat/" + name).param("v", "").execute()) {
                    CopyUtil.copy(response.getContentAsStream(), zos);
                }
            } catch (final Exception e) {
                logger.warn("Failed to access /_cat/{}", name, e);
            }
        });
    }

    /**
     * Writes Fess configuration properties to the ZIP output stream.
     *
     * @param zos the ZIP output stream to write to
     * @param id the diagnostic ID for organizing files in the ZIP
     */
    protected void writeFessConfig(final ZipOutputStream zos, final String id) {
        if (fessConfig instanceof SimpleImpl) {
            final Properties prop = new Properties();
            ((SimpleImpl) fessConfig).keySet().stream().forEach(k -> prop.setProperty(k, fessConfig.get(k)));

            final ZipEntry entry = new ZipEntry(id + "/fess_config.properties");
            try {
                zos.putNextEntry(entry);
                prop.store(zos, getHostInfo());
            } catch (final IOException e) {
                logger.warn("Failed to access fess_config.properties.", e);
            }
        }
    }

    /**
     * Writes Fess basic configuration data in bulk format to the ZIP output stream.
     *
     * @param zos the ZIP output stream to write to
     * @param id the diagnostic ID for organizing files in the ZIP
     */
    protected void writeFessBasicConfig(final ZipOutputStream zos, final String id) {
        final String index = "fess_basic_config";
        final ZipEntry entry = new ZipEntry(id + "/fess_basic_config.bulk");
        try {
            zos.putNextEntry(entry);
            SearchEngineUtil.scroll(index, hit -> {
                final String data = "{\"index\":{\"_index\":\"" + index + "\",\"_id\":\"" + StringEscapeUtils.escapeJson(hit.getId())
                        + "\"}}\n" + hit.getSourceAsString() + "\n";
                try {
                    zos.write(data.getBytes(Constants.CHARSET_UTF_8));
                } catch (final IOException e) {
                    logger.warn("Failed to access /{}/{}.", index, hit.getId(), e);
                }
                return true;
            });
        } catch (final IOException e) {
            logger.warn("Failed to access /{}.", index, e);
        }
    }

    /**
     * Writes system properties to the ZIP output stream.
     *
     * @param zos the ZIP output stream to write to
     * @param id the diagnostic ID for organizing files in the ZIP
     */
    protected void writeSystemProperties(final ZipOutputStream zos, final String id) {
        final ZipEntry entry = new ZipEntry(id + "/system.properties");
        try {
            zos.putNextEntry(entry);
            ComponentUtil.getSystemProperties().store(zos, getHostInfo());
        } catch (final IOException e) {
            logger.warn("Failed to access system.properties.", e);
        }
    }

    /**
     * Writes log files from the log directory to the ZIP output stream.
     *
     * @param zos the ZIP output stream to write to
     * @param id the diagnostic ID for organizing files in the ZIP
     */
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
                            logger.debug("{}: {}", filePath.getFileName(), len);
                        }
                    } catch (final IOException e) {
                        logger.warn("Failed to access {}", filePath, e);
                    }
                });
            } catch (final Exception e) {
                logger.warn("Failed to access log files.", e);
            }
        }
    }

    /**
     * Gets host information including hostname and IP address.
     *
     * @return formatted string containing hostname and IP address
     */
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

    /**
     * Checks if the given filename is a log file based on its extension.
     *
     * @param name the filename to check
     * @return true if the file is a log file (.log or .log.gz), false otherwise
     */
    protected boolean isLogFilename(final String name) {
        return name.endsWith(".log") || name.endsWith(".log.gz");
    }

    /**
     * Starts a reindex operation with the specified parameters.
     *
     * @param replaceAliases whether to replace aliases after reindexing
     * @param resetDictionaries whether to reset dictionaries during reindexing
     * @param numberOfShards the number of shards for the new index
     * @param autoExpandReplicas the auto expand replicas setting for the new index
     * @return true if the reindex operation started successfully, false otherwise
     */
    protected boolean startReindex(final boolean replaceAliases, final boolean resetDictionaries, final String numberOfShards,
            final String autoExpandReplicas) {
        final String docIndex = "fess";
        final String fromIndex = fessConfig.getIndexDocumentUpdateIndex();
        final String toIndex = docIndex + "." + new SimpleDateFormat(Constants.DOCUMENT_INDEX_SUFFIX_PATTERN).format(new Date());
        if (searchEngineClient.createIndex(docIndex, toIndex, numberOfShards, autoExpandReplicas, resetDictionaries)) {
            searchEngineClient.admin().cluster().prepareHealth(toIndex).setWaitForYellowStatus().execute(ActionListener.wrap(response -> {
                searchEngineClient.addMapping(docIndex, "doc", toIndex);
                if (searchEngineClient.copyDocIndex(fromIndex, toIndex, replaceAliases) && replaceAliases
                        && !searchEngineClient.updateAlias(toIndex)) {
                    logger.warn("Failed to update aliases for {} and {}", fromIndex, toIndex);
                }
            }, e -> logger.warn("Failed to reindex from {} to {}", fromIndex, toIndex, e)));
            return true;
        }
        saveError(messages -> messages.addErrorsFailedToReindex(GLOBAL, fromIndex, toIndex));
        return false;
    }

}
