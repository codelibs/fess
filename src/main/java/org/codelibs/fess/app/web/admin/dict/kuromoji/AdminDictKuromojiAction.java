/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.dict.kuromoji;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.KuromojiPager;
import org.codelibs.fess.app.service.KuromojiService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.dict.AdminDictAction;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.dict.kuromoji.KuromojiItem;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.validation.VaErrorHook;
import org.lastaflute.web.validation.exception.ValidationErrorException;

/**
 * @author shinsuke
 * @author Keiichi Watanabe
 */
public class AdminDictKuromojiAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private KuromojiService kuromojiService;
    @Resource
    private KuromojiPager kuromojiPager;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameDictKuromoji()));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final SearchForm form) {
        validate(form, messages -> {}, () -> asDictIndexHtml());
        kuromojiPager.clear();
        return asHtml(path_AdminDictKuromoji_AdminDictKuromojiJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        validate(form, messages -> {}, () -> asDictIndexHtml());
        pageNumber.ifPresent(num -> {
            kuromojiPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            kuromojiPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminDictKuromoji_AdminDictKuromojiJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        validate(form, messages -> {}, () -> asDictIndexHtml());
        copyBeanToBean(form, kuromojiPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminDictKuromoji_AdminDictKuromojiJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SearchForm form) {
        validate(form, messages -> {}, () -> asDictIndexHtml());
        kuromojiPager.clear();
        return asHtml(path_AdminDictKuromoji_AdminDictKuromojiJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        // page navi
        RenderDataUtil.register(data, "kuromojiItemItems", kuromojiService.getKuromojiList(form.dictId, kuromojiPager));

        // restore from pager
        BeanUtil.copyBeanToBean(kuromojiPager, form, op -> {
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
        return asHtml(path_AdminDictKuromoji_AdminDictKuromojiEditJsp).useForm(CreateForm.class, op -> {
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
        kuromojiService
                .getKuromojiItem(form.dictId, form.id)
                .ifPresent(entity -> {
                    copyBeanToBean(entity, form, op -> {});
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
                        kuromojiService
                                .getKuromojiItem(dictId, id)
                                .ifPresent(entity -> {
                                    copyBeanToBean(entity, form, copyOp -> {
                                        copyOp.excludeNull();
                                    });
                                    form.crudMode = crudMode;
                                })
                                .orElse(() -> {
                                    throwValidationError(
                                            messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, dictId + ":" + id),
                                            () -> asListHtml(dictId));
                                });
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
        return asHtml(path_AdminDictKuromoji_AdminDictKuromojiDownloadJsp).useForm(DownloadForm.class, op -> {
            op.setup(form -> {
                form.dictId = dictId;
            });
        }).renderWith(data -> {
            kuromojiService.getKuromojiFile(dictId).ifPresent(file -> {
                RenderDataUtil.register(data, "path", file.getPath());
            }).orElse(() -> {
                throwValidationError(messages -> messages.addErrorsFailedToDownloadKuromojiFile(GLOBAL), () -> asDictIndexHtml());
            });
        });
    }

    @Execute
    public ActionResponse download(final DownloadForm form) {
        validate(form, messages -> {}, () -> downloadpage(form.dictId));
        verifyTokenKeep(() -> downloadpage(form.dictId));
        return kuromojiService.getKuromojiFile(form.dictId).map(file -> {
            return asStream(new File(file.getPath()).getName()).contentTypeOctetStream().stream(out -> {
                file.writeOut(out);
            });
        }).orElseGet(() -> {
            throwValidationError(messages -> messages.addErrorsFailedToDownloadKuromojiFile(GLOBAL), () -> downloadpage(form.dictId));
            return null;
        });
    }

    // -----------------------------------------------------
    //                                                Upload
    //                                               -------
    @Execute
    public HtmlResponse uploadpage(final String dictId) {
        saveToken();
        return asHtml(path_AdminDictKuromoji_AdminDictKuromojiUploadJsp).useForm(UploadForm.class, op -> {
            op.setup(form -> {
                form.dictId = dictId;
            });
        }).renderWith(data -> {
            kuromojiService.getKuromojiFile(dictId).ifPresent(file -> {
                RenderDataUtil.register(data, "path", file.getPath());
            }).orElse(() -> {
                throwValidationError(messages -> messages.addErrorsFailedToDownloadKuromojiFile(GLOBAL), () -> asDictIndexHtml());
            });
        });
    }

    @Execute
    public HtmlResponse upload(final UploadForm form) {
        validate(form, messages -> {}, () -> uploadpage(form.dictId));
        verifyToken(() -> uploadpage(form.dictId));
        return kuromojiService.getKuromojiFile(form.dictId).map(file -> {
            try (InputStream inputStream = form.kuromojiFile.getInputStream()) {
                file.update(inputStream);
            } catch (final IOException e) {
                throwValidationError(messages -> messages.addErrorsFailedToUploadKuromojiFile(GLOBAL), () -> {
                    return redirectWith(getClass(), moreUrl("uploadpage/" + form.dictId));
                });
            }
            saveInfo(messages -> messages.addSuccessUploadKuromojiFile(GLOBAL));
            return redirectWith(getClass(), moreUrl("uploadpage/" + form.dictId));
        }).orElseGet(() -> {
            throwValidationError(messages -> messages.addErrorsFailedToUploadKuromojiFile(GLOBAL), () -> uploadpage(form.dictId));
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
        createKuromojiItem(form, () -> asEditHtml()).ifPresent(
                entity -> {
                    try {
                        kuromojiService.store(form.dictId, entity);
                        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
                    } catch (final Exception e) {
                        throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)),
                                () -> asEditHtml());
                    }
                }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL), () -> asEditHtml());
        });
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    @Execute
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT, form.dictId);
        validate(form, messages -> {}, () -> asEditHtml());
        verifyToken(() -> asEditHtml());
        createKuromojiItem(form, () -> asEditHtml()).ifPresent(
                entity -> {
                    try {
                        kuromojiService.store(form.dictId, entity);
                        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
                    } catch (final Exception e) {
                        throwValidationError(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)),
                                () -> asEditHtml());
                    }
                }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()), () -> asEditHtml());
        });
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    @Execute
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS, form.dictId);
        validate(form, messages -> {}, () -> asDetailsHtml());
        verifyToken(() -> asDetailsHtml());
        kuromojiService
                .getKuromojiItem(form.dictId, form.id)
                .ifPresent(
                        entity -> {
                            try {
                                kuromojiService.delete(form.dictId, entity);
                                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
                            } catch (final Exception e) {
                                throwValidationError(
                                        messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)),
                                        () -> asEditHtml());
                            }
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

    private static OptionalEntity<KuromojiItem> getEntity(final CreateForm form) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            final KuromojiItem entity = new KuromojiItem(0, StringUtil.EMPTY, StringUtil.EMPTY, StringUtil.EMPTY, StringUtil.EMPTY);
            return OptionalEntity.of(entity);
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(KuromojiService.class).getKuromojiItem(form.dictId, ((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    protected OptionalEntity<KuromojiItem> createKuromojiItem(final CreateForm form, final VaErrorHook hook) {
        try {
            return createKuromojiItem(this, form, hook);
        } catch (final ValidationErrorException e) {
            saveToken();
            throw e;
        }
    }

    public static OptionalEntity<KuromojiItem> createKuromojiItem(final FessBaseAction action, final CreateForm form, final VaErrorHook hook) {
        return getEntity(form).map(entity -> {
            entity.setNewToken(form.token);
            entity.setNewSegmentation(form.segmentation);
            entity.setNewReading(form.reading);
            entity.setNewPos(form.pos);
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

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========

    protected HtmlResponse asDictIndexHtml() {
        return redirect(AdminDictAction.class);
    }

    private HtmlResponse asListHtml(final String dictId) {
        return asHtml(path_AdminDictKuromoji_AdminDictKuromojiJsp).renderWith(data -> {
            RenderDataUtil.register(data, "kuromojiItemItems", kuromojiService.getKuromojiList(dictId, kuromojiPager));
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(kuromojiPager, form, op -> op.include("id"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminDictKuromoji_AdminDictKuromojiEditJsp);
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminDictKuromoji_AdminDictKuromojiDetailsJsp);
    }

}
