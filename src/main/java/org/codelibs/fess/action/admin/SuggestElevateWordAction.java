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

package org.codelibs.fess.action.admin;

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
import java.time.LocalDateTime;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.action.admin.BsSuggestElevateWordAction;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.db.exentity.SuggestElevateWord;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.FessBeans;
import org.codelibs.robot.util.StreamUtil;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.seasar.struts.util.ResponseUtil;

public class SuggestElevateWordAction extends BsSuggestElevateWordAction {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory
            .getLog(SuggestElevateWordAction.class);

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected SuggestHelper suggestHelper;

    @Resource
    protected DynamicProperties crawlerProperties;

    public String getHelpLink() {
        return systemHelper.getHelpLink("suggestElevateWord");
    }

    @Override
    protected void loadSuggestElevateWord() {

        final SuggestElevateWord suggestElevateWord = suggestElevateWordService
                .getSuggestElevateWord(createKeyMap());
        if (suggestElevateWord == null) {
            // throw an exception
            throw new SSCActionMessagesException(
                    "errors.crud_could_not_find_crud_table",
                    new Object[] { suggestElevateWordForm.id });
        }

        FessBeans.copy(suggestElevateWord, suggestElevateWordForm)
                .commonColumnDateConverter().excludes("searchParams", "mode")
                .execute();
    }

    @Override
    protected SuggestElevateWord createSuggestElevateWord() {
        SuggestElevateWord suggestElevateWord;
        final String username = systemHelper.getUsername();
        final LocalDateTime currentTime = systemHelper.getCurrentTime();
        if (suggestElevateWordForm.crudMode == CommonConstants.EDIT_MODE) {
            suggestElevateWord = suggestElevateWordService
                    .getSuggestElevateWord(createKeyMap());
            if (suggestElevateWord == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { suggestElevateWordForm.id });
            }
        } else {
            suggestElevateWord = new SuggestElevateWord();
            suggestElevateWord.setCreatedBy(username);
            suggestElevateWord.setCreatedTime(currentTime);
        }
        suggestElevateWord.setUpdatedBy(username);
        suggestElevateWord.setUpdatedTime(currentTime);
        FessBeans.copy(suggestElevateWordForm, suggestElevateWord)
                .excludesCommonColumns().execute();

        return suggestElevateWord;
    }

    @Override
    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final SuggestElevateWord suggestElevateWord = createSuggestElevateWord();
            suggestElevateWordService.store(suggestElevateWord);
            suggestHelper.storeAllElevateWords();
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
            final SuggestElevateWord suggestElevateWord = createSuggestElevateWord();
            suggestElevateWordService.store(suggestElevateWord);
            suggestHelper.storeAllElevateWords();
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

    @Override
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (suggestElevateWordForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            suggestElevateWordForm.crudMode });
        }

        try {
            final SuggestElevateWord suggestElevateWord = suggestElevateWordService
                    .getSuggestElevateWord(createKeyMap());
            if (suggestElevateWord == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { suggestElevateWordForm.id });
            }

            //           suggestElevateWordService.delete(suggestElevateWord);
            final String username = systemHelper.getUsername();
            final LocalDateTime currentTime = systemHelper.getCurrentTime();
            suggestElevateWord.setDeletedBy(username);
            suggestElevateWord.setDeletedTime(currentTime);
            suggestElevateWordService.store(suggestElevateWord);
            suggestHelper.storeAllElevateWords();
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
                + "elevateword.csv" + "\"");

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                response.getOutputStream(), crawlerProperties.getProperty(
                        Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8)))) {
            suggestElevateWordService.exportCsv(writer);
        } catch (final Exception e) {
            log.error("Failed to export data.", e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_export_data");
        }
        return null;
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
            tempFile = File
                    .createTempFile("suggestelevateword-import-", ".csv");
            is = new BufferedInputStream(
                    suggestElevateWordForm.suggestElevateWordFile
                            .getInputStream());
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
            if (!(head.startsWith("\"SuggestWord\"") || head
                    .startsWith("SuggestWord"))) {
                log.error("Unknown file: "
                        + suggestElevateWordForm.suggestElevateWordFile);
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
                        suggestElevateWordService.importCsv(reader);
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
                        suggestHelper.storeAllElevateWords();
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
        SAStrutsUtil.addSessionMessage("success.upload_suggest_elevate_word");

        return "uploadpage?redirect=true";
    }
}
