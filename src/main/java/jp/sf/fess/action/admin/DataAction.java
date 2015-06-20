/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package jp.sf.fess.action.admin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import jp.sf.fess.Constants;
import jp.sf.fess.FessSystemException;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.form.admin.DataForm;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.service.ClickLogService;
import jp.sf.fess.service.CrawlingSessionService;
import jp.sf.fess.service.DatabaseService;
import jp.sf.fess.service.SearchLogService;

import org.apache.commons.io.IOUtils;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.robot.util.StreamUtil;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.seasar.struts.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataAction implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory
            .getLogger(DataAction.class);

    @Resource
    @ActionForm
    protected DataForm dataForm;

    @Resource
    protected DatabaseService databaseService;

    @Resource
    protected CrawlingSessionService crawlingSessionService;

    @Resource
    protected SearchLogService searchLogService;

    @Resource
    protected ClickLogService clickLogService;

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("data");
    }

    @Execute(validator = false)
    public String index() {
        // set a default value
        dataForm.overwrite = "on";
        return "index.jsp";
    }

    @Execute(validator = false)
    public String download() {
        final DateFormat df = new SimpleDateFormat(
                CoreLibConstants.DATE_FORMAT_DIGIT_ONLY);
        final StringBuilder buf = new StringBuilder();
        buf.append("backup-");
        buf.append(df.format(new Date()));
        buf.append(".xml");

        final HttpServletResponse response = ResponseUtil.getResponse();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\""
                + buf.toString() + "\"");

        try {
            final ServletOutputStream sos = response.getOutputStream();
            try {
                databaseService.exportData(sos);
                sos.flush();
            } finally {
                sos.close();
            }
            return null;
        } catch (final Exception e) {
            logger.error("Failed to export data.", e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_export_data");
        }
    }

    @Execute(validator = false)
    public String downloadCrawlingSession() {
        final DateFormat df = new SimpleDateFormat(
                CoreLibConstants.DATE_FORMAT_DIGIT_ONLY);
        final StringBuilder buf = new StringBuilder();
        buf.append("backup-cs-");
        buf.append(df.format(new Date()));
        buf.append(".csv");

        final HttpServletResponse response = ResponseUtil.getResponse();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\""
                + buf.toString() + "\"");

        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    response.getOutputStream(), crawlerProperties.getProperty(
                            Constants.CSV_FILE_ENCODING_PROPERTY,
                            Constants.UTF_8)));
            crawlingSessionService.exportCsv(writer);
            writer.flush();
            return null;
        } catch (final Exception e) {
            logger.error("Failed to export data.", e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_export_data");
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    @Execute(validator = false)
    public String downloadSearchLog() {
        final DateFormat df = new SimpleDateFormat(
                CoreLibConstants.DATE_FORMAT_DIGIT_ONLY);
        final StringBuilder buf = new StringBuilder();
        buf.append("backup-sl-");
        buf.append(df.format(new Date()));
        buf.append(".csv");

        final HttpServletResponse response = ResponseUtil.getResponse();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\""
                + buf.toString() + "\"");

        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    response.getOutputStream(), crawlerProperties.getProperty(
                            Constants.CSV_FILE_ENCODING_PROPERTY,
                            Constants.UTF_8)));
            searchLogService.exportCsv(writer);
            writer.flush();
            return null;
        } catch (final Exception e) {
            logger.error("Failed to export data.", e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_export_data");
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    @Execute(validator = false)
    public String downloadClickLog() {
        final DateFormat df = new SimpleDateFormat(
                CoreLibConstants.DATE_FORMAT_DIGIT_ONLY);
        final StringBuilder buf = new StringBuilder();
        buf.append("backup-cl-");
        buf.append(df.format(new Date()));
        buf.append(".csv");

        final HttpServletResponse response = ResponseUtil.getResponse();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\""
                + buf.toString() + "\"");

        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    response.getOutputStream(), crawlerProperties.getProperty(
                            Constants.CSV_FILE_ENCODING_PROPERTY,
                            Constants.UTF_8)));
            clickLogService.exportCsv(writer);
            writer.flush();
            return null;
        } catch (final Exception e) {
            logger.error("Failed to export data.", e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_export_data");
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    @Execute(validator = true, input = "index")
    public String upload() {
        final String fileName = dataForm.uploadedFile.getFileName();
        if (fileName.endsWith(".xml")) {
            try {
                databaseService.importData(
                        dataForm.uploadedFile.getInputStream(),
                        dataForm.overwrite != null
                                && "on".equalsIgnoreCase(dataForm.overwrite));
                SAStrutsUtil.addSessionMessage("success.importing_data");
                return "index?redirect=true";
            } catch (final Exception e) {
                logger.error("Failed to import data.", e);
                throw new SSCActionMessagesException(e,
                        "errors.failed_to_import_data");
            }
        } else if (fileName.endsWith(".csv")) {
            BufferedInputStream is = null;
            File tempFile = null;
            FileOutputStream fos = null;
            final byte[] b = new byte[20];
            try {
                tempFile = File.createTempFile("fess-import-", ".csv");
                is = new BufferedInputStream(
                        dataForm.uploadedFile.getInputStream());
                is.mark(20);
                if (is.read(b, 0, 20) <= 0) {
                    throw new FessSystemException("no import data.");
                }
                is.reset();
                fos = new FileOutputStream(tempFile);
                StreamUtil.drain(is, fos);
            } catch (final Exception e) {
                if (tempFile != null && !tempFile.delete()) {
                    logger.warn("Could not delete "
                            + tempFile.getAbsolutePath());
                }
                logger.error("Failed to import data.", e);
                throw new SSCActionMessagesException(e,
                        "errors.failed_to_import_data");
            } finally {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(fos);
            }

            final File oFile = tempFile;
            try {
                final String head = new String(b, Constants.UTF_8).replace("\"", "");
                if (!head.startsWith("SessionId,")
                        && !head.startsWith("SearchWord,")
                        && !head.startsWith("SearchId,")) {
                    logger.error("Unknown file: " + dataForm.uploadedFile);
                    throw new SSCActionMessagesException(
                            "errors.unknown_import_file");
                }
                final String enc = crawlerProperties.getProperty(
                        Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Reader reader = null;
                        try {
                            reader = new BufferedReader(new InputStreamReader(
                                    new FileInputStream(oFile), enc));
                            if (head.startsWith("SessionId,")) {
                                // Crawling Session
                                crawlingSessionService.importCsv(reader);
                            } else if (head.startsWith("SearchWord,")) {
                                // Search Log
                                searchLogService.importCsv(reader);
                            } else if (head.startsWith("SearchId,")) {
                                // Click Log
                                clickLogService.importCsv(reader);
                            }
                        } catch (final Exception e) {
                            logger.error("Failed to import data.", e);
                            throw new FessSystemException(
                                    "Failed to import data.", e);
                        } finally {
                            if (!oFile.delete()) {
                                logger.warn("Could not delete "
                                        + oFile.getAbsolutePath());
                            }
                            IOUtils.closeQuietly(reader);
                        }
                    }
                }).start();
            } catch (final ActionMessagesException e) {
                if (!oFile.delete()) {
                    logger.warn("Could not delete " + oFile.getAbsolutePath());
                }
                throw e;
            } catch (final Exception e) {
                if (!oFile.delete()) {
                    logger.warn("Could not delete " + oFile.getAbsolutePath());
                }
                logger.error("Failed to import data.", e);
                throw new SSCActionMessagesException(e,
                        "errors.failed_to_import_data");
            }
        }

        SAStrutsUtil.addSessionMessage("success.importing_data");
        return "index?redirect=true";
    }
}