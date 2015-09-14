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

package org.codelibs.fess.app.web.admin.suggestelevateword;

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
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.SuggestElevateWordPager;
import org.codelibs.fess.app.service.SuggestElevateWordService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.es.exentity.SuggestElevateWord;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.util.LaResponseUtil;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author Keiichi Watanabe
 */
public class AdminSuggestelevatewordAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private SuggestElevateWordService suggestElevateWordService;
    @Resource
    private SuggestElevateWordPager suggestElevateWordPager;
    @Resource
    private SystemHelper systemHelper;
    @Resource
    protected SuggestHelper suggestHelper;
    @Resource
    protected DynamicProperties crawlerProperties;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("suggestElevateWord"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final SuggestElevateWordSearchForm form) {
        return asHtml(path_AdminSuggestelevateword_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final SuggestElevateWordSearchForm form) {
        suggestElevateWordPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminSuggestelevateword_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SuggestElevateWordSearchForm form) {
        copyBeanToBean(form.searchParams, suggestElevateWordPager, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminSuggestelevateword_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SuggestElevateWordSearchForm form) {
        suggestElevateWordPager.clear();
        return asHtml(path_AdminSuggestelevateword_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final SuggestElevateWordSearchForm form) {
        return asHtml(path_AdminSuggestelevateword_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SuggestElevateWordSearchForm form) {
        data.register("suggestElevateWordItems", suggestElevateWordService.getSuggestElevateWordList(suggestElevateWordPager)); // page navi
        // restore from pager
        copyBeanToBean(suggestElevateWordPager, form.searchParams, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(final SuggestElevateWordEditForm form) {
        form.initialize();
        form.crudMode = CommonConstants.CREATE_MODE;
        return asHtml(path_AdminSuggestelevateword_EditJsp).renderWith(data -> {
            // TODO
            });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(final int crudMode, final String id, final SuggestElevateWordEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.EDIT_MODE);
        loadSuggestElevateWord(form);
        return asHtml(path_AdminSuggestelevateword_EditJsp).renderWith(data -> {
            // TODO
            });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(final SuggestElevateWordEditForm form) {
        return asHtml(path_AdminSuggestelevateword_EditJsp).renderWith(data -> {
            // TODO
            });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(final SuggestElevateWordEditForm form) {
        form.crudMode = CommonConstants.EDIT_MODE;
        loadSuggestElevateWord(form);
        return asHtml(path_AdminSuggestelevateword_EditJsp).renderWith(data -> {
            // TODO
            });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final SuggestElevateWordEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        loadSuggestElevateWord(form);
        return asHtml(path_AdminSuggestelevateword_ConfirmJsp).renderWith(data -> {
            // TODO
            });
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final SuggestElevateWordEditForm form) {
        form.crudMode = CommonConstants.DELETE_MODE;
        loadSuggestElevateWord(form);
        return asHtml(path_AdminSuggestelevateword_ConfirmJsp).renderWith(data -> {
            // TODO
            });
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final SuggestElevateWordEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.CONFIRM_MODE);
        loadSuggestElevateWord(form);
        return asHtml(path_AdminSuggestelevateword_ConfirmJsp).renderWith(data -> {
            // TODO
            });
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(final SuggestElevateWordEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminSuggestelevateword_ConfirmJsp).renderWith(data -> {
            // TODO
            });
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(final SuggestElevateWordEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminSuggestelevateword_ConfirmJsp).renderWith(data -> {
            // TODO
            });
    }

    // -----------------------------------------------------
    //                                              Download
    //                                               -------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse downloadpage(final SuggestElevateWordSearchForm form) {
        return asHtml(path_AdminSuggestelevateword_DownloadJsp);
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse download(final SuggestElevateWordSearchForm form) {
        final HttpServletResponse response = LaResponseUtil.getResponse();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "elevateword.csv" + "\"");
        try (Writer writer =
                new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), crawlerProperties.getProperty(
                        Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8)))) {
            suggestElevateWordService.exportCsv(writer);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return asHtml(path_AdminSuggestelevateword_DownloadJsp);
    }

    // -----------------------------------------------------
    //                                                Upload
    //                                               -------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse uploadpage(final SuggestElevateWordUploadForm form) {
        return asHtml(path_AdminSuggestelevateword_UploadJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(final SuggestElevateWordEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        suggestElevateWordService.store(createSuggestElevateWord(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(final SuggestElevateWordEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        suggestElevateWordService.store(createSuggestElevateWord(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final SuggestElevateWordEditForm form) {
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        suggestElevateWordService.delete(getSuggestElevateWord(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse upload(final SuggestElevateWordUploadForm form) {
        BufferedInputStream is = null;
        File tempFile = null;
        FileOutputStream fos = null;
        final byte[] b = new byte[20];
        try {
            tempFile = File.createTempFile("suggestelevateword-import-", ".csv");
            is = new BufferedInputStream(form.suggestElevateWordFile.getInputStream());
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
                    suggestElevateWordService.importCsv(reader);
                } catch (final Exception e) {
                    throw new FessSystemException("Failed to import data.", e);
                } finally {
                    if (!oFile.delete()) {
                        // TODO
                }
                IOUtils.closeQuietly(reader);
                suggestHelper.storeAllElevateWords();
            }
        }   ).start();
        } catch (final Exception e) {
            if (!oFile.delete()) {
                // TODO
            }
        }
        saveInfo(messages -> messages.addSuccessUploadSuggestElevateWord(GLOBAL));
        return redirect(getClass());
    }

    //===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadSuggestElevateWord(final SuggestElevateWordEditForm form) {
        copyBeanToBean(getSuggestElevateWord(form), form, op -> op.exclude("crudMode"));
    }

    protected SuggestElevateWord getSuggestElevateWord(final SuggestElevateWordEditForm form) {
        final SuggestElevateWord suggestElevateWord = suggestElevateWordService.getSuggestElevateWord(createKeyMap(form));
        if (suggestElevateWord == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return suggestElevateWord;
    }

    protected SuggestElevateWord createSuggestElevateWord(final SuggestElevateWordEditForm form) {
        SuggestElevateWord suggestElevateWord;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CommonConstants.EDIT_MODE) {
            suggestElevateWord = getSuggestElevateWord(form);
        } else {
            suggestElevateWord = new SuggestElevateWord();
            suggestElevateWord.setCreatedBy(username);
            suggestElevateWord.setCreatedTime(currentTime);
        }
        suggestElevateWord.setUpdatedBy(username);
        suggestElevateWord.setUpdatedTime(currentTime);
        copyBeanToBean(form, suggestElevateWord, op -> op.exclude(CommonConstants.COMMON_CONVERSION_RULE));
        return suggestElevateWord;
    }

    protected Map<String, String> createKeyMap(final SuggestElevateWordEditForm form) {
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
    protected void verifyCrudMode(final SuggestElevateWordEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminSuggestelevateword_EditJsp);
        };
    }
}
