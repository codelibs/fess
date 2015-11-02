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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.SuggestElevateWordPager;
import org.codelibs.fess.app.service.SuggestElevateWordService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.config.exentity.SuggestElevateWord;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.SystemHelper;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.next.HtmlNext;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.token.TxToken;
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
    public HtmlResponse index(final SearchForm form) {
        return asHtml(path_AdminSuggestelevateword_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            suggestElevateWordPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            suggestElevateWordPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminSuggestelevateword_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, suggestElevateWordPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminSuggestelevateword_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SearchForm form) {
        suggestElevateWordPager.clear();
        return asHtml(path_AdminSuggestelevateword_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        data.register("suggestElevateWordItems", suggestElevateWordService.getSuggestElevateWordList(suggestElevateWordPager)); // page navi

        // restore from pager
        copyBeanToBean(suggestElevateWordPager, form, op -> op.include("id"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Execute(token = TxToken.SAVE)
    public HtmlResponse createnew() {
        return asHtml(path_AdminSuggestelevateword_EditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        });
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, toEditHtml());
        HtmlNext next;
        switch (form.crudMode) {
        case CrudMode.EDIT: // back
            form.crudMode = CrudMode.DETAILS;
            next = path_AdminSuggestelevateword_DetailsJsp;
            break;
        default:
            form.crudMode = CrudMode.EDIT;
            next = path_AdminSuggestelevateword_EditJsp;
            break;
        }
        final String id = form.id;
        suggestElevateWordService.getSuggestElevateWord(id).ifPresent(entity -> {
            copyBeanToBean(entity, form, op -> {});
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), toEditHtml());
        });
        return asHtml(next);
    }

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    @Execute
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        return asHtml(path_AdminSuggestelevateword_DetailsJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                suggestElevateWordService.getSuggestElevateWord(id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                    });
                    form.crudMode = crudMode;
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), toEditHtml());
                });
            });
        });
    }

    // -----------------------------------------------------
    //                                              Download
    //                                               -------
    @Execute(token = TxToken.SAVE)
    public HtmlResponse downloadpage(final SearchForm form) {
        return asHtml(path_AdminSuggestelevateword_DownloadJsp);
    }

    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse download(final SearchForm form) {
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
    @Execute(token = TxToken.SAVE)
    public HtmlResponse uploadpage(final UploadForm form) {
        return asHtml(path_AdminSuggestelevateword_UploadJsp);
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, toEditHtml());
        createSuggestElevateWord(form).ifPresent(entity -> {
            copyBeanToBean(form, entity, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
            suggestElevateWordService.store(entity);
            saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL), toEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, toEditHtml());
        createSuggestElevateWord(form).ifPresent(entity -> {
            copyBeanToBean(form, entity, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
            suggestElevateWordService.store(entity);
            saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DELETE);
        validate(form, messages -> {}, toEditHtml());
        final String id = form.id;
        suggestElevateWordService.getSuggestElevateWord(id).ifPresent(entity -> {
            suggestElevateWordService.delete(entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), toEditHtml());
        });
        return redirect(getClass());
    }

    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse upload(final UploadForm form) {
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

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected OptionalEntity<SuggestElevateWord> createSuggestElevateWord(final CreateForm form) {
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        switch (form.crudMode) {
        case CrudMode.CREATE:
            if (form instanceof CreateForm) {
                final SuggestElevateWord entity = new SuggestElevateWord();
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                entity.setUpdatedBy(username);
                entity.setUpdatedTime(currentTime);
                return OptionalEntity.of(entity);
            }
            break;
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return suggestElevateWordService.getSuggestElevateWord(((EditForm) form).id).map(entity -> {
                    entity.setUpdatedBy(username);
                    entity.setUpdatedTime(currentTime);
                    return entity;
                });
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final int crudMode, final int expectedMode) {
        if (crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminSuggestelevateword_EditJsp);
        };
    }
}
