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

package org.codelibs.fess.app.web.admin;

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
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.exception.SSCActionMessagesException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.app.service.CrawlingSessionService;
import org.lastaflute.web.util.LaResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataAction implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(DataAction.class);

    @Resource
    //@ActionForm
    protected DataForm dataForm;

    @Resource
    protected CrawlingSessionService crawlingSessionService;

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("data");
    }

    //@Execute(validator = false)
    public String index() {
        // set a default value
        dataForm.overwrite = "on";
        return "index.jsp";
    }

    //@Execute(validator = false)
    public String downloadCrawlingSession() {
        final DateFormat df = new SimpleDateFormat(CoreLibConstants.DATE_FORMAT_DIGIT_ONLY);
        final StringBuilder buf = new StringBuilder();
        buf.append("backup-cs-");
        buf.append(df.format(new Date()));
        buf.append(".csv");

        final HttpServletResponse response = LaResponseUtil.getResponse();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + buf.toString() + "\"");

        Writer writer = null;
        try {
            writer =
                    new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), crawlerProperties.getProperty(
                            Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8)));
            crawlingSessionService.exportCsv(writer);
            writer.flush();
            return null;
        } catch (final Exception e) {
            logger.error("Failed to export data.", e);
            throw new SSCActionMessagesException(e, "errors.failed_to_export_data");
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    //@Execute(validator = true, input = "index")
    public String upload() {
        final String fileName = dataForm.uploadedFile.getFileName();
        if (fileName.endsWith(".csv")) {
            BufferedInputStream is = null;
            File tempFile = null;
            FileOutputStream fos = null;
            final byte[] b = new byte[20];
            try {
                tempFile = File.createTempFile("fess-import-", ".csv");
                is = new BufferedInputStream(dataForm.uploadedFile.getInputStream());
                is.mark(20);
                if (is.read(b, 0, 20) <= 0) {
                    throw new FessSystemException("no import data.");
                }
                is.reset();
                fos = new FileOutputStream(tempFile);
                CopyUtil.copy(is, fos);
            } catch (final Exception e) {
                if (tempFile != null && !tempFile.delete()) {
                    logger.warn("Could not delete " + tempFile.getAbsolutePath());
                }
                logger.error("Failed to import data.", e);
                throw new SSCActionMessagesException(e, "errors.failed_to_import_data");
            } finally {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(fos);
            }

            final File oFile = tempFile;
            try {
                final String head = new String(b, Constants.UTF_8);
                if (!head.startsWith("SessionId,")) {
                    logger.error("Unknown file: " + dataForm.uploadedFile);
                    throw new SSCActionMessagesException("errors.unknown_import_file");
                }
                final String enc = crawlerProperties.getProperty(Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8);
                new Thread(() -> {
                    Reader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(new FileInputStream(oFile), enc));
                        if (head.startsWith("SessionId,")) {
                            // Crawling Session
                        crawlingSessionService.importCsv(reader);
                    }
                } catch (final Exception e) {
                    logger.error("Failed to import data.", e);
                    throw new FessSystemException("Failed to import data.", e);
                } finally {
                    if (!oFile.delete()) {
                        logger.warn("Could not delete " + oFile.getAbsolutePath());
                    }
                    IOUtils.closeQuietly(reader);
                }
            }   ).start();
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
                throw new SSCActionMessagesException(e, "errors.failed_to_import_data");
            }
        }

        SAStrutsUtil.addSessionMessage("success.importing_data");
        return "index?redirect=true";
    }
}