/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.suggestbadword;

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
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.SuggestBadWordPager;
import org.codelibs.fess.app.service.SuggestBadWordService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.config.exentity.SuggestBadWord;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.token.TxToken;
import org.lastaflute.web.util.LaResponseUtil;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
 */
public class AdminSuggestbadwordAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private SuggestBadWordService suggestBadWordService;
    @Resource
    private SuggestBadWordPager suggestBadWordPager;
    @Resource
    private SystemHelper systemHelper;
    @Resource
    protected DynamicProperties crawlerProperties;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("suggestBadWord"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final SuggestBadWordSearchForm form) {
        return asHtml(path_AdminSuggestbadword_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final SuggestBadWordSearchForm form) {
        suggestBadWordPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminSuggestbadword_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SuggestBadWordSearchForm form) {
        copyBeanToBean(form.searchParams, suggestBadWordPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminSuggestbadword_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SuggestBadWordSearchForm form) {
        suggestBadWordPager.clear();
        return asHtml(path_AdminSuggestbadword_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final SuggestBadWordSearchForm form) {
        return asHtml(path_AdminSuggestbadword_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SuggestBadWordSearchForm form) {
        data.register("suggestBadWordItems", suggestBadWordService.getSuggestBadWordList(suggestBadWordPager)); // page navi
        // restore from pager
        copyBeanToBean(suggestBadWordPager, form.searchParams, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Execute(token = TxToken.SAVE)
    public HtmlResponse createpage(final SuggestBadWordEditForm form) {
        form.initialize();
        form.crudMode = CrudMode.CREATE;
        return asHtml(path_AdminSuggestbadword_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editpage(final int crudMode, final String id, final SuggestBadWordEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.EDIT);
        loadSuggestBadWord(form);
        return asHtml(path_AdminSuggestbadword_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editagain(final SuggestBadWordEditForm form) {
        return asHtml(path_AdminSuggestbadword_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editfromconfirm(final SuggestBadWordEditForm form) {
        form.crudMode = CrudMode.EDIT;
        loadSuggestBadWord(form);
        return asHtml(path_AdminSuggestbadword_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse deletepage(final int crudMode, final String id, final SuggestBadWordEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.DELETE);
        loadSuggestBadWord(form);
        return asHtml(path_AdminSuggestbadword_ConfirmJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse deletefromconfirm(final SuggestBadWordEditForm form) {
        form.crudMode = CrudMode.DELETE;
        loadSuggestBadWord(form);
        return asHtml(path_AdminSuggestbadword_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final SuggestBadWordEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CrudMode.CONFIRM);
        loadSuggestBadWord(form);
        return asHtml(path_AdminSuggestbadword_ConfirmJsp);
    }

    @Execute(token = TxToken.VALIDATE_KEEP)
    public HtmlResponse confirmfromcreate(final SuggestBadWordEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminSuggestbadword_ConfirmJsp);
    }

    @Execute(token = TxToken.VALIDATE_KEEP)
    public HtmlResponse confirmfromupdate(final SuggestBadWordEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminSuggestbadword_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                              Download
    //                                               -------
    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse downloadpage(final SuggestBadWordSearchForm form) {
        return asHtml(path_AdminSuggestbadword_DownloadJsp);
    }

    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse download(final SuggestBadWordSearchForm form) {
        final HttpServletResponse response = LaResponseUtil.getResponse();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "badword.csv" + "\"");
        try (Writer writer =
                new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), crawlerProperties.getProperty(
                        Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8)))) {
            suggestBadWordService.exportCsv(writer);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return asHtml(path_AdminSuggestbadword_DownloadJsp);
    }

    // -----------------------------------------------------
    //                                                Upload
    //                                               -------
    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse uploadpage(final SuggestBadWordUploadForm form) {
        return asHtml(path_AdminSuggestbadword_UploadJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse create(final SuggestBadWordEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        suggestBadWordService.store(createSuggestBadWord(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse update(final SuggestBadWordEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        suggestBadWordService.store(createSuggestBadWord(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final SuggestBadWordEditForm form) {
        verifyCrudMode(form, CrudMode.DELETE);
        suggestBadWordService.delete(getSuggestBadWord(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse upload(final SuggestBadWordUploadForm form) {
        BufferedInputStream is = null;
        File tempFile = null;
        FileOutputStream fos = null;
        final byte[] b = new byte[20];
        try {
            tempFile = File.createTempFile("suggestbadword-import-", ".csv");
            is = new BufferedInputStream(form.suggestBadWordFile.getInputStream());
            is.mark(20);
            if (is.read(b, 0, 20) <= 0) {
                // TODO
            }
            is.reset();
            fos = new FileOutputStream(tempFile);
            CopyUtil.copy(is, fos);
        } catch (final Exception e) {
            if (tempFile != null && !tempFile.delete()) {
                // TODO
            }
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(fos);
        }

        final File oFile = tempFile;
        try {
            final String head = new String(b, Constants.UTF_8);
            if (!(head.startsWith("\"SuggestWord\"") || head.startsWith("SuggestWord"))) {
                // TODO
            }
            final String enc = crawlerProperties.getProperty(Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8);
            new Thread(() -> {
                Reader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(oFile), enc));
                    suggestBadWordService.importCsv(reader);
                } catch (final Exception e) {
                    throw new FessSystemException("Failed to import data.", e);
                } finally {
                    if (!oFile.delete()) {
                        // TODO
                }
                IOUtils.closeQuietly(reader);
            }
        }   ).start();
        } catch (final Exception e) {
            if (!oFile.delete()) {
                // TODO
            }
        }
        saveInfo(messages -> messages.addSuccessUploadSuggestBadWord(GLOBAL));
        return redirect(getClass());
    }

    //===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadSuggestBadWord(final SuggestBadWordEditForm form) {
        copyBeanToBean(getSuggestBadWord(form), form, op -> op.exclude("crudMode"));
    }

    protected SuggestBadWord getSuggestBadWord(final SuggestBadWordEditForm form) {
        final SuggestBadWord suggestBadWord = suggestBadWordService.getSuggestBadWord(createKeyMap(form));
        if (suggestBadWord == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return suggestBadWord;
    }

    protected SuggestBadWord createSuggestBadWord(final SuggestBadWordEditForm form) {
        SuggestBadWord suggestBadWord;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CrudMode.EDIT) {
            suggestBadWord = getSuggestBadWord(form);
        } else {
            suggestBadWord = new SuggestBadWord();
            suggestBadWord.setCreatedBy(username);
            suggestBadWord.setCreatedTime(currentTime);
        }
        suggestBadWord.setUpdatedBy(username);
        suggestBadWord.setUpdatedTime(currentTime);
        copyBeanToBean(form, suggestBadWord, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
        return suggestBadWord;
    }

    protected Map<String, String> createKeyMap(final SuggestBadWordEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    protected Map<String, String> createItem(final String label, final String value) {
        final Map<String, String> map = new HashMap<String, String>(2);
        map.put(Constants.ITEM_LABEL, label);
        map.put(Constants.ITEM_VALUE, value);
        return map;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final SuggestBadWordEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminSuggestbadword_EditJsp);
        };
    }
}
