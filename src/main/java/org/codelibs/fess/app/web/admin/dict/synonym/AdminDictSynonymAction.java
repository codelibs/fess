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
package org.codelibs.fess.app.web.admin.dict.synonym;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.SynonymPager;
import org.codelibs.fess.app.service.SynonymService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.dict.AdminDictAction;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.dict.synonym.SynonymItem;
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
 * @author shinsuke
 * @author Keiichi Watanabe
 */
public class AdminDictSynonymAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private SynonymService synonymService;
    @Resource
    private SynonymPager synonymPager;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameDictSynonym()));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final SearchForm form) {
        validate(form, messages -> {}, () -> asDictIndexHtml());
        return asHtml(path_AdminDictSynonym_AdminDictSynonymJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        validate(form, messages -> {}, () -> asDictIndexHtml());
        pageNumber.ifPresent(num -> {
            synonymPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            synonymPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminDictSynonym_AdminDictSynonymJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        validate(form, messages -> {}, () -> asDictIndexHtml());
        copyBeanToBean(form, synonymPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminDictSynonym_AdminDictSynonymJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SearchForm form) {
        validate(form, messages -> {}, () -> asDictIndexHtml());
        synonymPager.clear();
        return asHtml(path_AdminDictSynonym_AdminDictSynonymJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        // page navi
        RenderDataUtil.register(data, "synonymItemItems", synonymService.getSynonymList(form.dictId, synonymPager));

        // restore from pager
        BeanUtil.copyBeanToBean(synonymPager, form, op -> {
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
        return asHtml(path_AdminDictSynonym_AdminDictSynonymEditJsp).useForm(CreateForm.class, op -> {
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
        synonymService
                .getSynonymItem(form.dictId, form.id)
                .ifPresent(entity -> {
                    form.inputs = entity.getInputsValue();
                    form.outputs = entity.getOutputsValue();
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
                        synonymService
                                .getSynonymItem(dictId, id)
                                .ifPresent(entity -> {
                                    form.inputs = entity.getInputsValue();
                                    form.outputs = entity.getOutputsValue();
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
        return asHtml(path_AdminDictSynonym_AdminDictSynonymDownloadJsp).useForm(DownloadForm.class, op -> {
            op.setup(form -> {
                form.dictId = dictId;
            });
        }).renderWith(data -> {
            synonymService.getSynonymFile(dictId).ifPresent(file -> {
                RenderDataUtil.register(data, "path", file.getPath());
            }).orElse(() -> {
                throwValidationError(messages -> messages.addErrorsFailedToDownloadSynonymFile(GLOBAL), () -> asDictIndexHtml());
            });
        });
    }

    @Execute
    public ActionResponse download(final DownloadForm form) {
        validate(form, messages -> {}, () -> downloadpage(form.dictId));
        verifyTokenKeep(() -> downloadpage(form.dictId));
        return synonymService.getSynonymFile(form.dictId).map(file -> {
            return asStream(new File(file.getPath()).getName()).contentTypeOctetStream().stream(out -> {
                try (InputStream inputStream = file.getInputStream()) {
                    out.write(inputStream);
                }
            });
        }).orElseGet(() -> {
            throwValidationError(messages -> messages.addErrorsFailedToDownloadSynonymFile(GLOBAL), () -> downloadpage(form.dictId));
            return null;
        });
    }

    // -----------------------------------------------------
    //                                                Upload
    //                                               -------
    @Execute
    public HtmlResponse uploadpage(final String dictId) {
        saveToken();
        return asHtml(path_AdminDictSynonym_AdminDictSynonymUploadJsp).useForm(UploadForm.class, op -> {
            op.setup(form -> {
                form.dictId = dictId;
            });
        }).renderWith(data -> {
            synonymService.getSynonymFile(dictId).ifPresent(file -> {
                RenderDataUtil.register(data, "path", file.getPath());
            }).orElse(() -> {
                throwValidationError(messages -> messages.addErrorsFailedToDownloadSynonymFile(GLOBAL), () -> asDictIndexHtml());
            });
        });
    }

    @Execute
    public HtmlResponse upload(final UploadForm form) {
        validate(form, messages -> {}, () -> uploadpage(form.dictId));
        verifyToken(() -> uploadpage(form.dictId));
        return synonymService.getSynonymFile(form.dictId).map(file -> {
            try (InputStream inputStream = form.synonymFile.getInputStream()) {
                file.update(inputStream);
            } catch (final IOException e) {
                throwValidationError(messages -> messages.addErrorsFailedToUploadSynonymFile(GLOBAL), () -> {
                    return redirectWith(getClass(), moreUrl("uploadpage/" + form.dictId));
                });
            }
            saveInfo(messages -> messages.addSuccessUploadSynonymFile(GLOBAL));
            return redirectWith(getClass(), moreUrl("uploadpage/" + form.dictId));
        }).orElseGet(() -> {
            throwValidationError(messages -> messages.addErrorsFailedToUploadSynonymFile(GLOBAL), () -> uploadpage(form.dictId));
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
        createSynonymItem(form, () -> asEditHtml()).ifPresent(
                entity -> {
                    try {
                        synonymService.store(form.dictId, entity);
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
        createSynonymItem(form, () -> asEditHtml()).ifPresent(
                entity -> {
                    try {
                        synonymService.store(form.dictId, entity);
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
        synonymService
                .getSynonymItem(form.dictId, form.id)
                .ifPresent(
                        entity -> {
                            try {
                                synonymService.delete(form.dictId, entity);
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

    private OptionalEntity<SynonymItem> getEntity(final CreateForm form) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            final SynonymItem entity = new SynonymItem(0, StringUtil.EMPTY_STRINGS, StringUtil.EMPTY_STRINGS);
            return OptionalEntity.of(entity);
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return synonymService.getSynonymItem(form.dictId, ((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    protected OptionalEntity<SynonymItem> createSynonymItem(final CreateForm form, final VaErrorHook hook) {
        return getEntity(form).map(entity -> {
            final String[] newInputs = splitLine(form.inputs);
            validateSynonymString(newInputs, "inputs", hook);
            entity.setNewInputs(newInputs);
            final String[] newOutputs = splitLine(form.outputs);
            validateSynonymString(newOutputs, "outputs", hook);
            entity.setNewOutputs(newOutputs);
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

    private void validateSynonymString(final String[] values, final String propertyName, final VaErrorHook hook) {
        if (values.length == 0) {
            return;
        }
        for (final String value : values) {
            if (value.indexOf(',') >= 0) {
                throwValidationError(messages -> {
                    messages.addErrorsInvalidStrIsIncluded(propertyName, value, ",");
                }, hook);
            }
            if (value.indexOf("=>") >= 0) {
                throwValidationError(messages -> {
                    messages.addErrorsInvalidStrIsIncluded(propertyName, value, "=>");
                }, hook);
            }
        }
    }

    private String[] splitLine(final String value) {
        if (StringUtil.isBlank(value)) {
            return StringUtil.EMPTY_STRINGS;
        }
        final String[] values = value.split("[\r\n]");
        final List<String> list = new ArrayList<>(values.length);
        for (final String line : values) {
            if (StringUtil.isNotBlank(line)) {
                list.add(line.trim());
            }
        }
        return list.toArray(new String[list.size()]);
    }

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========

    protected HtmlResponse asDictIndexHtml() {
        return redirect(AdminDictAction.class);
    }

    private HtmlResponse asListHtml(final String dictId) {
        return asHtml(path_AdminDictSynonym_AdminDictSynonymJsp).renderWith(data -> {
            RenderDataUtil.register(data, "synonymItemItems", synonymService.getSynonymList(dictId, synonymPager));
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(synonymPager, form, op -> op.include("id"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminDictSynonym_AdminDictSynonymEditJsp);
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminDictSynonym_AdminDictSynonymDetailsJsp);
    }

}
