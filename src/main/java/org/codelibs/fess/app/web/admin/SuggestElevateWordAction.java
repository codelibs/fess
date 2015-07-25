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

import java.beans.Beans;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.beans.FessBeans;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.es.exentity.SuggestElevateWord;
import org.codelibs.fess.exception.SSCActionMessagesException;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.pager.SuggestElevateWordPager;
import org.codelibs.fess.service.SuggestElevateWordService;
import org.lastaflute.web.util.LaResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuggestElevateWordAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(SuggestElevateWordAction.class);

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected SuggestHelper suggestHelper;

    @Resource
    protected DynamicProperties crawlerProperties;

    // for list

    public List<SuggestElevateWord> suggestElevateWordItems;

    // for edit/confirm/delete

    //@ActionForm
    @Resource
    protected SuggestElevateWordForm suggestElevateWordForm;

    @Resource
    protected SuggestElevateWordService suggestElevateWordService;

    @Resource
    protected SuggestElevateWordPager suggestElevateWordPager;

    protected String displayList(final boolean redirect) {
        // page navi
        suggestElevateWordItems = suggestElevateWordService.getSuggestElevateWordList(suggestElevateWordPager);

        // restore from pager
        BeanUtil.copyBeanToBean(suggestElevateWordPager, suggestElevateWordForm.searchParams,
                option -> option.exclude(CommonConstants.PAGER_CONVERSION_RULE));

        if (redirect) {
            return "index?redirect=true";
        } else {
            return "index.jsp";
        }
    }

    //@Execute(validator = false, input = "error.jsp")
    public String index() {
        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp", urlPattern = "list/{pageNumber}")
    public String list() {
        // page navi
        if (StringUtil.isNotBlank(suggestElevateWordForm.pageNumber)) {
            try {
                suggestElevateWordPager.setCurrentPageNumber(Integer.parseInt(suggestElevateWordForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid value: " + suggestElevateWordForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp")
    public String search() {
        BeanUtil.copyBeanToBean(suggestElevateWordForm.searchParams, suggestElevateWordPager,
                option -> option.exclude(CommonConstants.PAGER_CONVERSION_RULE));

        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp")
    public String reset() {
        suggestElevateWordPager.clear();

        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp")
    public String back() {
        return displayList(false);
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String editagain() {
        return "edit.jsp";
    }

    //@Execute(validator = false, input = "error.jsp", urlPattern = "confirmpage/{crudMode}/{id}")
    public String confirmpage() {
        if (suggestElevateWordForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    suggestElevateWordForm.crudMode });
        }

        loadSuggestElevateWord();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        suggestElevateWordForm.initialize();
        suggestElevateWordForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (suggestElevateWordForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.EDIT_MODE,
                    suggestElevateWordForm.crudMode });
        }

        loadSuggestElevateWord();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        suggestElevateWordForm.crudMode = CommonConstants.EDIT_MODE;

        loadSuggestElevateWord();

        return "edit.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    //@Execute(validator = true, input = "edit.jsp")
    public String confirmfromcreate() {
        return "confirm.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    //@Execute(validator = true, input = "edit.jsp")
    public String confirmfromupdate() {
        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp", urlPattern = "deletepage/{crudMode}/{id}")
    public String deletepage() {
        if (suggestElevateWordForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    suggestElevateWordForm.crudMode });
        }

        loadSuggestElevateWord();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        suggestElevateWordForm.crudMode = CommonConstants.DELETE_MODE;

        loadSuggestElevateWord();

        return "confirm.jsp";
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", suggestElevateWordForm.id);

        return keys;
    }

    public String getHelpLink() {
        return systemHelper.getHelpLink("suggestElevateWord");
    }

    protected void loadSuggestElevateWord() {

        final SuggestElevateWord suggestElevateWord = suggestElevateWordService.getSuggestElevateWord(createKeyMap());
        if (suggestElevateWord == null) {
            // throw an exception
            throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { suggestElevateWordForm.id });
        }

        BeanUtil.copyBeanToBean(suggestElevateWord, suggestElevateWordForm, option -> option.excludes("searchParams", "mode"));
    }

    protected SuggestElevateWord createSuggestElevateWord() {
        SuggestElevateWord suggestElevateWord;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (suggestElevateWordForm.crudMode == CommonConstants.EDIT_MODE) {
            suggestElevateWord = suggestElevateWordService.getSuggestElevateWord(createKeyMap());
            if (suggestElevateWord == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { suggestElevateWordForm.id });
            }
        } else {
            suggestElevateWord = new SuggestElevateWord();
            suggestElevateWord.setCreatedBy(username);
            suggestElevateWord.setCreatedTime(currentTime);
        }
        suggestElevateWord.setUpdatedBy(username);
        suggestElevateWord.setUpdatedTime(currentTime);
        BeanUtil.copyBeanToBean(suggestElevateWordForm, suggestElevateWord,
                option -> option.exclude(CommonConstants.COMMON_CONVERSION_RULE));

        return suggestElevateWord;
    }

    @Token(save = false, validate = true)
    //@Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final SuggestElevateWord suggestElevateWord = createSuggestElevateWord();
            suggestElevateWordService.store(suggestElevateWord);
            suggestHelper.storeAllElevateWords();
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
    //@Execute(validator = true, input = "edit.jsp")
    public String update() {
        try {
            final SuggestElevateWord suggestElevateWord = createSuggestElevateWord();
            suggestElevateWordService.store(suggestElevateWord);
            suggestHelper.storeAllElevateWords();
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

    //@Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (suggestElevateWordForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    suggestElevateWordForm.crudMode });
        }

        try {
            final SuggestElevateWord suggestElevateWord = suggestElevateWordService.getSuggestElevateWord(createKeyMap());
            if (suggestElevateWord == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { suggestElevateWordForm.id });
            }

            suggestElevateWordService.delete(suggestElevateWord);
            suggestHelper.storeAllElevateWords();
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

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "downloadpage")
    public String downloadpage() {
        return "download.jsp";
    }

    @Token(save = false, validate = true)
    //@Execute(validator = false, input = "downloadpage")
    public String download() {

        final HttpServletResponse response = LaResponseUtil.getResponse();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "elevateword.csv" + "\"");

        try (Writer writer =
                new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), crawlerProperties.getProperty(
                        Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8)))) {
            suggestElevateWordService.exportCsv(writer);
        } catch (final Exception e) {
            logger.error("Failed to export data.", e);
            throw new SSCActionMessagesException(e, "errors.failed_to_export_data");
        }
        return null;
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "uploadpage")
    public String uploadpage() {
        return "upload.jsp";
    }

    @Token(save = false, validate = true)
    //@Execute(validator = true, input = "uploadpage")
    public String upload() {
        BufferedInputStream is = null;
        File tempFile = null;
        FileOutputStream fos = null;
        final byte[] b = new byte[20];
        try {
            tempFile = File.createTempFile("suggestelevateword-import-", ".csv");
            is = new BufferedInputStream(suggestElevateWordForm.suggestElevateWordFile.getInputStream());
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
            if (!(head.startsWith("\"SuggestWord\"") || head.startsWith("SuggestWord"))) {
                logger.error("Unknown file: " + suggestElevateWordForm.suggestElevateWordFile);
                throw new SSCActionMessagesException("errors.unknown_import_file");
            }
            final String enc = crawlerProperties.getProperty(Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8);
            new Thread(() -> {
                Reader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(oFile), enc));
                    suggestElevateWordService.importCsv(reader);
                } catch (final Exception e) {
                    logger.error("Failed to import data.", e);
                    throw new FessSystemException("Failed to import data.", e);
                } finally {
                    if (!oFile.delete()) {
                        logger.warn("Could not delete " + oFile.getAbsolutePath());
                    }
                    IOUtils.closeQuietly(reader);
                    suggestHelper.storeAllElevateWords();
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
        SAStrutsUtil.addSessionMessage("success.upload_suggest_elevate_word");

        return "uploadpage?redirect=true";
    }
}
