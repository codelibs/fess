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

package org.codelibs.fess.web.admin;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.beans.FessBeans;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.db.exentity.SuggestBadWord;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.pager.SuggestBadWordPager;
import org.codelibs.fess.service.SuggestBadWordService;
import org.codelibs.fess.web.base.FessAdminAction;
import org.codelibs.robot.util.StreamUtil;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.seasar.struts.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuggestBadWordAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(SuggestBadWordAction.class);

    // for list

    public List<SuggestBadWord> suggestBadWordItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected SuggestBadWordForm suggestBadWordForm;

    @Resource
    protected SuggestBadWordService suggestBadWordService;

    @Resource
    protected SuggestBadWordPager suggestBadWordPager;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected SuggestHelper suggestHelper;

    @Resource
    protected DynamicProperties crawlerProperties;

    protected String displayList(final boolean redirect) {
        // page navi
        suggestBadWordItems = suggestBadWordService.getSuggestBadWordList(suggestBadWordPager);

        // restore from pager
        Beans.copy(suggestBadWordPager, suggestBadWordForm.searchParams).excludes(CommonConstants.PAGER_CONVERSION_RULE)

        .execute();

        if (redirect) {
            return "index?redirect=true";
        } else {
            return "index.jsp";
        }
    }

    @Execute(validator = false, input = "error.jsp")
    public String index() {
        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp", urlPattern = "list/{pageNumber}")
    public String list() {
        // page navi
        if (StringUtil.isNotBlank(suggestBadWordForm.pageNumber)) {
            try {
                suggestBadWordPager.setCurrentPageNumber(Integer.parseInt(suggestBadWordForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid value: " + suggestBadWordForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(suggestBadWordForm.searchParams, suggestBadWordPager).excludes(CommonConstants.PAGER_CONVERSION_RULE)

        .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        suggestBadWordPager.clear();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String back() {
        return displayList(false);
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editagain() {
        return "edit.jsp";
    }

    @Execute(validator = false, input = "error.jsp", urlPattern = "confirmpage/{crudMode}/{id}")
    public String confirmpage() {
        if (suggestBadWordForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    suggestBadWordForm.crudMode });
        }

        loadSuggestBadWord();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        suggestBadWordForm.initialize();
        suggestBadWordForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (suggestBadWordForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.EDIT_MODE,
                    suggestBadWordForm.crudMode });
        }

        loadSuggestBadWord();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        suggestBadWordForm.crudMode = CommonConstants.EDIT_MODE;

        loadSuggestBadWord();

        return "edit.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    @Execute(validator = true, input = "edit.jsp")
    public String confirmfromcreate() {
        return "confirm.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    @Execute(validator = true, input = "edit.jsp")
    public String confirmfromupdate() {
        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "deletepage/{crudMode}/{id}")
    public String deletepage() {
        if (suggestBadWordForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    suggestBadWordForm.crudMode });
        }

        loadSuggestBadWord();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        suggestBadWordForm.crudMode = CommonConstants.DELETE_MODE;

        loadSuggestBadWord();

        return "confirm.jsp";
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", suggestBadWordForm.id);

        return keys;
    }

    public String getHelpLink() {
        return systemHelper.getHelpLink("suggestBadWord");
    }

    protected void loadSuggestBadWord() {

        final SuggestBadWord suggestBadWord = suggestBadWordService.getSuggestBadWord(createKeyMap());
        if (suggestBadWord == null) {
            // throw an exception
            throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { suggestBadWordForm.id });
        }

        FessBeans.copy(suggestBadWord, suggestBadWordForm).commonColumnDateConverter().excludes("searchParams", "mode").execute();
    }

    protected SuggestBadWord createSuggestBadWord() {
        SuggestBadWord suggestBadWord;
        final String username = systemHelper.getUsername();
        final LocalDateTime currentTime = systemHelper.getCurrentTime();
        if (suggestBadWordForm.crudMode == CommonConstants.EDIT_MODE) {
            suggestBadWord = suggestBadWordService.getSuggestBadWord(createKeyMap());
            if (suggestBadWord == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { suggestBadWordForm.id });
            }
        } else {
            suggestBadWord = new SuggestBadWord();
            suggestBadWord.setCreatedBy(username);
            suggestBadWord.setCreatedTime(currentTime);
        }
        suggestBadWord.setUpdatedBy(username);
        suggestBadWord.setUpdatedTime(currentTime);
        FessBeans.copy(suggestBadWordForm, suggestBadWord).excludesCommonColumns().execute();

        return suggestBadWord;
    }

    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (suggestBadWordForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    suggestBadWordForm.crudMode });
        }

        try {
            final SuggestBadWord suggestBadWord = suggestBadWordService.getSuggestBadWord(createKeyMap());
            if (suggestBadWord == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { suggestBadWordForm.id });
            }

            //           suggestBadWordService.delete(suggestBadWord);
            final String username = systemHelper.getUsername();
            final LocalDateTime currentTime = systemHelper.getCurrentTime();
            suggestBadWord.setDeletedBy(username);
            suggestBadWord.setDeletedTime(currentTime);
            suggestBadWordService.store(suggestBadWord);
            suggestHelper.deleteAllBadWord();
            suggestHelper.updateSolrBadwordFile();
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            logger.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, "errors.crud_failed_to_delete_crud_table");
        }
    }

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
            logger.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException("errors.crud_failed_to_create_crud_table");
        }
    }

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
            logger.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException("errors.crud_failed_to_update_crud_table");
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
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "badword.csv" + "\"");

        try (Writer writer =
                new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), crawlerProperties.getProperty(
                        Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8)))) {
            suggestBadWordService.exportCsv(writer);
        } catch (final Exception e) {
            logger.error("Failed to export data.", e);
            throw new SSCActionMessagesException(e, "errors.failed_to_export_data");
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
            tempFile = File.createTempFile("suggestbadword-import-", ".csv");
            is = new BufferedInputStream(suggestBadWordForm.suggestBadWordFile.getInputStream());
            is.mark(20);
            if (is.read(b, 0, 20) <= 0) {
                throw new FessSystemException("no import data.");
            }
            is.reset();
            fos = new FileOutputStream(tempFile);
            StreamUtil.drain(is, fos);
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
            if (!(head.startsWith("\"BadWord\"") || head.startsWith("BadWord"))) {
                logger.error("Unknown file: " + suggestBadWordForm.suggestBadWordFile);
                throw new SSCActionMessagesException("errors.unknown_import_file");
            }
            final String enc = crawlerProperties.getProperty(Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8);
            new Thread(new Runnable() {
                public void run() {
                    Reader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(new FileInputStream(oFile), enc));
                        suggestBadWordService.importCsv(reader);
                    } catch (final Exception e) {
                        logger.error("Failed to import data.", e);
                        throw new FessSystemException("Failed to import data.", e);
                    } finally {
                        if (!oFile.delete()) {
                            logger.warn("Could not delete " + oFile.getAbsolutePath());
                        }
                        IOUtils.closeQuietly(reader);
                        suggestHelper.deleteAllBadWord();
                        suggestHelper.updateSolrBadwordFile();
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
            throw new SSCActionMessagesException(e, "errors.failed_to_import_data");
        }
        SAStrutsUtil.addSessionMessage("success.upload_suggest_bad_word");

        return "uploadpage?redirect=true";
    }
}
