/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.dict.protwords;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.ProtwordsPager;
import org.codelibs.fess.app.service.ProtwordsService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.dict.AdminDictAction;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.dict.protwords.ProtwordsItem;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author ma2tani
 */
public class AdminDictProtwordsAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private ProtwordsService protwordsService;
    @Resource
    private ProtwordsPager protwordsPager;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameDictProtwords()));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final SearchForm form) {
        validate(form, messages -> {}, () -> asDictIndexHtml());
        return asHtml(path_AdminDictProtwords_AdminDictProtwordsJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        validate(form, messages -> {}, () -> asDictIndexHtml());
        pageNumber.ifPresent(num -> {
            protwordsPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            protwordsPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminDictProtwords_AdminDictProtwordsJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        validate(form, messages -> {}, () -> asDictIndexHtml());
        copyBeanToBean(form, protwordsPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminDictProtwords_AdminDictProtwordsJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SearchForm form) {
        validate(form, messages -> {}, () -> asDictIndexHtml());
        protwordsPager.clear();
        return asHtml(path_AdminDictProtwords_AdminDictProtwordsJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        // page navi
        RenderDataUtil.register(data, "protwordsItemItems", protwordsService.getProtwordsList(form.dictId, protwordsPager));

        // restore from pager
        BeanUtil.copyBeanToBean(protwordsPager, form, op -> {
            op.exclude(Constants.PAGER_CONVERSION_RULE);
        });
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Execute
    public HtmlResponse createnew(final String dictId) {
        saveToken();
        return asHtml(path_AdminDictProtwords_AdminDictProtwordsEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
                form.dictId = dictId;
            });
        });
    }

    @Execute
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, () -> asListHtml(form.dictId));
        protwordsService
                .getProtwordsItem(form.dictId, form.id)
                .ifPresent(entity -> {
                    form.input = entity.getInputValue();
                })
                .orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()),
                            () -> asListHtml(form.dictId));
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
    public HtmlResponse details(final String dictId, final int crudMode, final long id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS, dictId);
        saveToken();
        return asDetailsHtml().useForm(
                EditForm.class,
                op -> {
                    op.setup(form -> {
                        protwordsService
                                .getProtwordsItem(dictId, id)
                                .ifPresent(entity -> {
                                    form.input = entity.getInputValue();
                                })
                                .orElse(() -> {
                                    throwValidationError(
                                            messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, dictId + ":" + id),
                                            () -> asListHtml(dictId));
                                });
                        form.id = id;
                        form.crudMode = crudMode;
                        form.dictId = dictId;
                    });
                });
    }

    // -----------------------------------------------------
    //                                              Download
    //                                               -------
    @Execute
    public HtmlResponse downloadpage(final String dictId) {
        saveToken();
        return asHtml(path_AdminDictProtwords_AdminDictProtwordsDownloadJsp).useForm(DownloadForm.class, op -> {
            op.setup(form -> {
                form.dictId = dictId;
            });
        }).renderWith(data -> {
            protwordsService.getProtwordsFile(dictId).ifPresent(file -> {
                RenderDataUtil.register(data, "path", file.getPath());
            }).orElse(() -> {
                throwValidationError(messages -> messages.addErrorsFailedToDownloadProtwordsFile(GLOBAL), () -> asDictIndexHtml());
            });
        });
    }

    @Execute
    public ActionResponse download(final DownloadForm form) {
        validate(form, messages -> {}, () -> downloadpage(form.dictId));
        verifyTokenKeep(() -> downloadpage(form.dictId));
        return protwordsService.getProtwordsFile(form.dictId).map(file -> {
            return asStream(new File(file.getPath()).getName()).contentTypeOctetStream().stream(out -> {
                try (InputStream inputStream = file.getInputStream()) {
                    out.write(inputStream);
                }
            });
        }).orElseGet(() -> {
            throwValidationError(messages -> messages.addErrorsFailedToDownloadProtwordsFile(GLOBAL), () -> downloadpage(form.dictId));
            return null;
        });
    }

    // -----------------------------------------------------
    //                                                Upload
    //                                               -------
    @Execute
    public HtmlResponse uploadpage(final String dictId) {
        saveToken();
        return asHtml(path_AdminDictProtwords_AdminDictProtwordsUploadJsp).useForm(UploadForm.class, op -> {
            op.setup(form -> {
                form.dictId = dictId;
            });
        }).renderWith(data -> {
            protwordsService.getProtwordsFile(dictId).ifPresent(file -> {
                RenderDataUtil.register(data, "path", file.getPath());
            }).orElse(() -> {
                throwValidationError(messages -> messages.addErrorsFailedToDownloadProtwordsFile(GLOBAL), () -> asDictIndexHtml());
            });
        });
    }

    @Execute
    public HtmlResponse upload(final UploadForm form) {
        validate(form, messages -> {}, () -> uploadpage(form.dictId));
        verifyToken(() -> uploadpage(form.dictId));
        return protwordsService.getProtwordsFile(form.dictId).map(file -> {
            try (InputStream inputStream = form.protwordsFile.getInputStream()) {
                file.update(inputStream);
            } catch (final IOException e) {
                throwValidationError(messages -> messages.addErrorsFailedToUploadProtwordsFile(GLOBAL), () -> {
                    return redirectWith(getClass(), moreUrl("uploadpage/" + form.dictId));
                });
            }
            saveInfo(messages -> messages.addSuccessUploadSynonymFile(GLOBAL));
            return redirectWith(getClass(), moreUrl("uploadpage/" + form.dictId));
        }).orElseGet(() -> {
            throwValidationError(messages -> messages.addErrorsFailedToUploadProtwordsFile(GLOBAL), () -> uploadpage(form.dictId));
            return null;
        });

    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE, form.dictId);
        validate(form, messages -> {}, () -> asEditHtml());
        verifyToken(() -> asEditHtml());
        createProtwordsItem(form, () -> asEditHtml()).ifPresent(entity -> {
            protwordsService.store(form.dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        }).orElse(() -> throwValidationError(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL), () -> asEditHtml()));
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    @Execute
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT, form.dictId);
        validate(form, messages -> {}, () -> asEditHtml());
        verifyToken(() -> asEditHtml());
        createProtwordsItem(form, () -> asEditHtml()).ifPresent(entity -> {
            protwordsService.store(form.dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        }).orElse(
                () -> throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()),
                        () -> asEditHtml()));
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    @Execute
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS, form.dictId);
        validate(form, messages -> {}, () -> asDetailsHtml());
        verifyToken(() -> asDetailsHtml());
        protwordsService
                .getProtwordsItem(form.dictId, form.id)
                .ifPresent(entity -> {
                    protwordsService.delete(form.dictId, entity);
                    saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
                })
                .orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()),
                            () -> asDetailsHtml());
                });
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    //===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============

    private OptionalEntity<ProtwordsItem> getEntity(final CreateForm form) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            final ProtwordsItem entity = new ProtwordsItem(0, StringUtil.EMPTY);
            return OptionalEntity.of(entity);
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return protwordsService.getProtwordsItem(form.dictId, ((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    protected OptionalEntity<ProtwordsItem> createProtwordsItem(final CreateForm form, final VaErrorHook hook) {
        return getEntity(form).map(entity -> {
            final String newInput = form.input;
            validateProtwordsString(newInput, "input", hook);
            entity.setNewInput(newInput);
            return entity;
        });
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final int crudMode, final int expectedMode, final String dictId) {
        if (crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(crudMode));
            }, () -> asListHtml(dictId));
        }
    }

    private void validateProtwordsString(final String values, final String propertyName, final VaErrorHook hook) {
        if (values.length() == 0) {
            return;
        }
        // TODO validation
    }

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========

    protected HtmlResponse asDictIndexHtml() {
        return redirect(AdminDictAction.class);
    }

    private HtmlResponse asListHtml(final String dictId) {
        return asHtml(path_AdminDictProtwords_AdminDictProtwordsJsp).renderWith(data -> {
            RenderDataUtil.register(data, "protwordsItemItems", protwordsService.getProtwordsList(dictId, protwordsPager));
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(protwordsPager, form, op -> op.include("id"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminDictProtwords_AdminDictProtwordsEditJsp);
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminDictProtwords_AdminDictProtwordsDetailsJsp);
    }

}
