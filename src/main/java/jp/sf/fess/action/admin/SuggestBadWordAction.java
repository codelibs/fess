/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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
import java.io.Writer;
import java.sql.Timestamp;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import jp.sf.fess.Constants;
import jp.sf.fess.FessSystemException;
import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.crud.action.admin.BsSuggestBadWordAction;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.db.exentity.SuggestBadWord;
import jp.sf.fess.helper.SuggestHelper;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.util.FessBeans;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.robot.util.StreamUtil;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.seasar.struts.util.ResponseUtil;

public class SuggestBadWordAction extends BsSuggestBadWordAction {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory
            .getLog(SuggestBadWordAction.class);

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected SuggestHelper suggestHelper;

    @Resource
    protected DynamicProperties crawlerProperties;

    public String getHelpLink() {
        return systemHelper.getHelpLink("suggestBadWord");
    }

    @Override
    protected void loadSuggestBadWord() {

        final SuggestBadWord suggestBadWord = suggestBadWordService
                .getSuggestBadWord(createKeyMap());
        if (suggestBadWord == null) {
            // throw an exception
            throw new SSCActionMessagesException(
                    "errors.crud_could_not_find_crud_table",
                    new Object[] { suggestBadWordForm.id });
        }

        FessBeans.copy(suggestBadWord, suggestBadWordForm)
                .commonColumnDateConverter().excludes("searchParams", "mode")
                .execute();
    }

    @Override
    protected SuggestBadWord createSuggestBadWord() {
        SuggestBadWord suggestBadWord;
        final String username = systemHelper.getUsername();
        final Timestamp timestamp = systemHelper.getCurrentTimestamp();
        if (suggestBadWordForm.crudMode == CommonConstants.EDIT_MODE) {
            suggestBadWord = suggestBadWordService
                    .getSuggestBadWord(createKeyMap());
            if (suggestBadWord == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { suggestBadWordForm.id });
            }
        } else {
            suggestBadWord = new SuggestBadWord();
            suggestBadWord.setCreatedBy(username);
            suggestBadWord.setCreatedTime(timestamp);
        }
        suggestBadWord.setUpdatedBy(username);
        suggestBadWord.setUpdatedTime(timestamp);
        FessBeans.copy(suggestBadWordForm, suggestBadWord)
                .excludesCommonColumns().execute();

        return suggestBadWord;
    }

    @Override
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (suggestBadWordForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            suggestBadWordForm.crudMode });
        }

        try {
            final SuggestBadWord suggestBadWord = suggestBadWordService
                    .getSuggestBadWord(createKeyMap());
            if (suggestBadWord == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { suggestBadWordForm.id });
            }

            //           suggestBadWordService.delete(suggestBadWord);
            final String username = systemHelper.getUsername();
            final Timestamp timestamp = systemHelper.getCurrentTimestamp();
            suggestBadWord.setDeletedBy(username);
            suggestBadWord.setDeletedTime(timestamp);
            suggestBadWordService.store(suggestBadWord);
            suggestHelper.deleteAllBadWord();
            suggestHelper.updateSolrBadwordFile();
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, e.getMessageId(),
                    e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e,
                    "errors.crud_failed_to_delete_crud_table");
        }
    }

    @Override
    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final SuggestBadWord suggestBadWord = createSuggestBadWord();
            suggestBadWordService.store(suggestBadWord);
            suggestHelper.deleteAllBadWord();
            suggestHelper.updateSolrBadwordFile();
            SAStrutsUtil.addSessionMessage("success.crud_create_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(
                    "errors.crud_failed_to_create_crud_table");
        }
    }

    @Override
    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String update() {
        try {
            final SuggestBadWord suggestBadWord = createSuggestBadWord();
            suggestBadWordService.store(suggestBadWord);
            suggestHelper.deleteAllBadWord();
            suggestHelper.updateSolrBadwordFile();
            SAStrutsUtil.addSessionMessage("success.crud_update_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(
                    "errors.crud_failed_to_update_crud_table");
        }
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "downloadpage")
    public String downloadpage() {
        return "download.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = false, input = "downloadpage")
    public String download() {

        final HttpServletResponse response = ResponseUtil.getResponse();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\""
                + "suggestbadword.csv" + "\"");

        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    response.getOutputStream(), crawlerProperties.getProperty(
                            Constants.CSV_FILE_ENCODING_PROPERTY,
                            Constants.UTF_8)));
            suggestBadWordService.exportCsv(writer);
            writer.flush();
            return null;
        } catch (final Exception e) {
            log.error("Failed to export data.", e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_export_data");
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "uploadpage")
    public String uploadpage() {
        return "upload.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "uploadpage")
    public String upload() {
        BufferedInputStream is = null;
        File tempFile = null;
        FileOutputStream fos = null;
        final byte[] b = new byte[20];
        try {
            tempFile = File.createTempFile("suggestbadword-import-", ".csv");
            is = new BufferedInputStream(
                    suggestBadWordForm.suggestBadWordFile.getInputStream());
            is.mark(20);
            if (is.read(b, 0, 20) <= 0) {
                throw new FessSystemException("no import data.");
            }
            is.reset();
            fos = new FileOutputStream(tempFile);
            StreamUtil.drain(is, fos);
        } catch (final Exception e) {
            if (tempFile != null && !tempFile.delete()) {
                log.warn("Could not delete " + tempFile.getAbsolutePath());
            }
            log.error("Failed to import data.", e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_import_data");
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(fos);
        }

        final File oFile = tempFile;
        try {
            final String head = new String(b, Constants.UTF_8);
            if (!head.startsWith("\"SuggestWord\",")) {
                log.error("Unknown file: "
                        + suggestBadWordForm.suggestBadWordFile);
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
                        suggestBadWordService.importCsv(reader);
                    } catch (final Exception e) {
                        log.error("Failed to import data.", e);
                        throw new FessSystemException("Failed to import data.",
                                e);
                    } finally {
                        if (!oFile.delete()) {
                            log.warn("Could not delete "
                                    + oFile.getAbsolutePath());
                        }
                        IOUtils.closeQuietly(reader);
                        suggestHelper.deleteAllBadWord();
                        suggestHelper.updateSolrBadwordFile();
                    }
                }
            }).start();
        } catch (final ActionMessagesException e) {
            if (!oFile.delete()) {
                log.warn("Could not delete " + oFile.getAbsolutePath());
            }
            throw e;
        } catch (final Exception e) {
            if (!oFile.delete()) {
                log.warn("Could not delete " + oFile.getAbsolutePath());
            }
            log.error("Failed to import data.", e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_import_data");
        }
        SAStrutsUtil.addSessionMessage("success.upload_suggest_bad_word");

        return "uploadpage?redirect=true";
    }
}
