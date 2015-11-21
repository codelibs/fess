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
package org.codelibs.fess.app.web.admin.badword;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.Resource;

import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.SuggestBadWordPager;
import org.codelibs.fess.app.service.SuggestBadWordService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.config.exentity.SuggestBadWord;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;

/**
 * @author Keiichi Watanabe
 */
public class AdminBadwordAction extends FessAdminAction {

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
    @Resource
    protected SuggestHelper suggestHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameBadword()));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index() {
        return asListHtml();
    }

    @Execute
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            suggestBadWordPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            suggestBadWordPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminBadword_AdminBadwordJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, suggestBadWordPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminBadword_AdminBadwordJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SearchForm form) {
        suggestBadWordPager.clear();
        return asHtml(path_AdminBadword_AdminBadwordJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        data.register("suggestBadWordItems", suggestBadWordService.getSuggestBadWordList(suggestBadWordPager)); // page navi

        // restore from pager
        copyBeanToBean(suggestBadWordPager, form, op -> op.include("id"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Execute
    public HtmlResponse createnew() {
        saveToken();
        return asEditHtml().useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        });
    }

    @Execute
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, () -> asListHtml());
        final String id = form.id;
        suggestBadWordService.getSuggestBadWord(id).ifPresent(entity -> {
            copyBeanToBean(entity, form, op -> {});
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), () -> asListHtml());
        });
        saveToken();
        if (form.crudMode.intValue() == CrudMode.EDIT) {
            // back
            form.crudMode = CrudMode.DETAILS;
            return asDetailsHtml();
        } else {
            form.crudMode = CrudMode.EDIT;
            return asEditHtml();
        }
    }

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    @Execute
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        return asDetailsHtml().useForm(EditForm.class, op -> {
            op.setup(form -> {
                suggestBadWordService.getSuggestBadWord(id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                    });
                    form.crudMode = crudMode;
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), () -> asListHtml());
                });
            });
        });
    }

    // -----------------------------------------------------
    //                                              Download
    //                                               -------
    @Execute
    public HtmlResponse downloadpage() {
        saveToken();
        return asDownloadHtml();
    }

    @Execute
    public ActionResponse download(final DownloadForm form) {
        verifyToken(() -> asDownloadHtml());

        return asStream("badword.csv").stream(out -> {
            Path tempFile = Files.createTempFile(null, null);
            try {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(tempFile), getCsvEncoding()))) {
                    suggestBadWordService.exportCsv(writer);
                } catch (final Exception e) {
                    throwValidationError(messages -> messages.addErrorsFailedToDownloadBadwordFile(GLOBAL), () -> asDownloadHtml());
                }
                try (InputStream in = Files.newInputStream(tempFile)) {
                    out.write(in);
                }
            } finally {
                Files.delete(tempFile);
            }
        });
    }

    // -----------------------------------------------------
    //                                                Upload
    //                                               -------
    @Execute
    public HtmlResponse uploadpage() {
        saveToken();
        return asUploadHtml();
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, () -> asEditHtml());
        verifyToken(() -> asEditHtml());
        getSuggestBadWord(form).ifPresent(entity -> {
            suggestBadWordService.store(entity);
            suggestHelper.addBadWord(entity.getSuggestWord());
            saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL), () -> asEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, () -> asEditHtml());
        verifyToken(() -> asEditHtml());
        getSuggestBadWord(form).ifPresent(entity -> {
            suggestBadWordService.store(entity);
            suggestHelper.storeAllBadWords();
            saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), () -> asEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, () -> asDetailsHtml());
        verifyToken(() -> asDetailsHtml());
        final String id = form.id;
        suggestBadWordService.getSuggestBadWord(id).ifPresent(entity -> {
            suggestBadWordService.delete(entity);
            suggestHelper.deleteBadWord(entity.getSuggestWord());
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), () -> asDetailsHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse upload(final UploadForm form) {
        validate(form, messages -> {}, () -> asUploadHtml());
        verifyToken(() -> asUploadHtml());
        new Thread(() -> {
            try (Reader reader = new BufferedReader(new InputStreamReader(form.suggestBadWordFile.getInputStream(), getCsvEncoding()))) {
                suggestBadWordService.importCsv(reader);
                suggestHelper.storeAllBadWords();
            } catch (final Exception e) {
                throw new FessSystemException("Failed to import data.", e);
            }
        }).start();
        saveInfo(messages -> messages.addSuccessUploadSuggestBadWord(GLOBAL));
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private OptionalEntity<SuggestBadWord> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            if (form instanceof CreateForm) {
                return OptionalEntity.of(new SuggestBadWord()).map(entity -> {
                    entity.setCreatedBy(username);
                    entity.setCreatedTime(currentTime);
                    return entity;
                });
            }
            break;
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return suggestBadWordService.getSuggestBadWord(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    protected OptionalEntity<SuggestBadWord> getSuggestBadWord(final CreateForm form) {
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        return getEntity(form, username, currentTime).map(entity -> {
            entity.setUpdatedBy(username);
            entity.setUpdatedTime(currentTime);
            copyBeanToBean(form, entity, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
            return entity;
        });
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final int crudMode, final int expectedMode) {
        if (crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(crudMode));
            }, () -> asListHtml());
        }
    }

    private String getCsvEncoding() {
        return crawlerProperties.getProperty(Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8);
    }

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========
    private HtmlResponse asListHtml() {
        return asHtml(path_AdminBadword_AdminBadwordJsp).renderWith(data -> {
            data.register("suggestBadWordItems", suggestBadWordService.getSuggestBadWordList(suggestBadWordPager));
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(suggestBadWordPager, form, op -> op.include("id"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminBadword_AdminBadwordEditJsp);
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminBadword_AdminBadwordDetailsJsp);
    }

    private HtmlResponse asUploadHtml() {
        return asHtml(path_AdminBadword_AdminBadwordUploadJsp).useForm(UploadForm.class);
    }

    private HtmlResponse asDownloadHtml() {
        return asHtml(path_AdminBadword_AdminBadwordDownloadJsp).useForm(DownloadForm.class);
    }

}
