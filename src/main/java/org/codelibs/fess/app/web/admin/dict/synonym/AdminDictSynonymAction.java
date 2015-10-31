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
package org.codelibs.fess.app.web.admin.dict.synonym;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.SynonymPager;
import org.codelibs.fess.app.service.SynonymService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.dict.AdminDictAction;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.dict.synonym.SynonymItem;
import org.codelibs.fess.helper.SystemHelper;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.token.TxToken;
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
        runtime.registerData("helpLink", systemHelper.getHelpLink("synonym"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final SearchForm form) {
        validate(form, messages -> {}, toIndexHtml());
        return asHtml(path_AdminDictSynonym_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final SearchForm form) {
        validate(form, messages -> {}, toIndexHtml());
        synonymPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminDictSynonym_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        validate(form, messages -> {}, toIndexHtml());
        copyBeanToBean(form, synonymPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminDictSynonym_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SearchForm form) {
        validate(form, messages -> {}, toIndexHtml());
        synonymPager.clear();
        return asHtml(path_AdminDictSynonym_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final SearchForm form) {
        validate(form, messages -> {}, toIndexHtml());
        return asHtml(path_AdminDictSynonym_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        // page navi
        data.register("synonymItemItems", synonymService.getSynonymList(form.dictId, synonymPager));

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
    @Execute(token = TxToken.SAVE)
    public HtmlResponse createpage(final String dictId) {
        return asHtml(path_AdminDictSynonym_EditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
                form.dictId = dictId;
            });
        });
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editpage(final String dictId, final int crudMode, final long id) {
        verifyCrudMode(crudMode, CrudMode.EDIT);
        return asHtml(path_AdminDictSynonym_EditJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                synonymService.getSynonymItem(dictId, id).ifPresent(entity -> {
                    form.inputs = entity.getInputsValue();
                    form.outputs = entity.getOutputsValue();
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, dictId + ":" + id), toEditHtml());
                });
                form.id = id;
                form.crudMode = crudMode;
                form.dictId = dictId;
            });
        });
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editagain(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminDictSynonym_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse editfromconfirm(final EditForm form) {
        validate(form, messages -> {}, toEditHtml());
        form.crudMode = CrudMode.EDIT;
        synonymService.getSynonymItem(form.dictId, form.id).ifPresent(entity -> {
            form.inputs = entity.getInputsValue();
            form.outputs = entity.getOutputsValue();
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()), toEditHtml());
        });
        return asHtml(path_AdminDictSynonym_EditJsp);
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse deletepage(final String dictId, final int crudMode, final long id) {
        verifyCrudMode(crudMode, CrudMode.DELETE);
        return asHtml(path_AdminDictSynonym_ConfirmJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                synonymService.getSynonymItem(dictId, id).ifPresent(entity -> {
                    form.inputs = entity.getInputsValue();
                    form.outputs = entity.getOutputsValue();
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, dictId + ":" + id), toEditHtml());
                });
                form.id = id;
                form.crudMode = crudMode;
                form.dictId = dictId;
            });
        });
    }

    @Execute(token = TxToken.SAVE)
    public HtmlResponse deletefromconfirm(final EditForm form) {
        validate(form, messages -> {}, toEditHtml());
        form.crudMode = CrudMode.DELETE;
        synonymService.getSynonymItem(form.dictId, form.id).ifPresent(entity -> {
            form.inputs = entity.getInputsValue();
            form.outputs = entity.getOutputsValue();
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()), toEditHtml());
        });
        return asHtml(path_AdminDictSynonym_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final String dictId, final int crudMode, final long id) {
        verifyCrudMode(crudMode, CrudMode.CONFIRM);
        return asHtml(path_AdminDictSynonym_ConfirmJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                synonymService.getSynonymItem(dictId, id).ifPresent(entity -> {
                    form.inputs = entity.getInputsValue();
                    form.outputs = entity.getOutputsValue();
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, dictId + ":" + id), toEditHtml());
                });
                form.id = id;
                form.crudMode = crudMode;
                form.dictId = dictId;
            });
        });
    }

    @Execute(token = TxToken.VALIDATE_KEEP)
    public HtmlResponse confirmfromcreate(final CreateForm form) {
        validate(form, messages -> {}, toEditHtml());
        form.crudMode = CrudMode.CREATE;
        final String[] newInputs = splitLine(form.inputs);
        validateSynonymString(newInputs, () -> createpage(form.dictId));
        final String[] newOutputs = splitLine(form.outputs);
        validateSynonymString(newOutputs, () -> createpage(form.dictId));
        return asHtml(path_AdminDictSynonym_ConfirmJsp);
    }

    @Execute(token = TxToken.VALIDATE_KEEP)
    public HtmlResponse confirmfromupdate(final EditForm form) {
        validate(form, messages -> {}, toEditHtml());
        form.crudMode = CrudMode.EDIT;
        final String[] newInputs = splitLine(form.inputs);
        validateSynonymString(newInputs, () -> editpage(form.dictId, form.crudMode, form.id));
        final String[] newOutputs = splitLine(form.outputs);
        validateSynonymString(newOutputs, () -> editpage(form.dictId, form.crudMode, form.id));
        return asHtml(path_AdminDictSynonym_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                              Download
    //                                               -------
    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse downloadpage(final String dictId) {
        return asHtml(path_AdminDictSynonym_DownloadJsp).useForm(DownloadForm.class, op -> {
            op.setup(form -> {
                form.dictId = dictId;
            });
        }).renderWith(data -> {
            synonymService.getSynonymFile(dictId).ifPresent(file -> {
                data.register("path", file.getPath());
            }).orElse(() -> {
                throwValidationError(messages -> messages.addErrorsFailedToDownloadSynonymFile(GLOBAL), toIndexHtml());
            });
        });
    }

    @Execute(token = TxToken.VALIDATE)
    public ActionResponse download(final DownloadForm form) {
        validate(form, messages -> {}, () -> downloadpage(form.dictId));
        return synonymService.getSynonymFile(form.dictId).map(file -> {
            return asStream(new File(file.getPath()).getName()).contentType("text/plain; charset=UTF-8").stream(out -> {
                out.write(file.getInputStream());
            });
        }).orElseGet(() -> {
            throwValidationError(messages -> messages.addErrorsFailedToDownloadSynonymFile(GLOBAL), () -> downloadpage(form.dictId));
            return null;
        });
    }

    // -----------------------------------------------------
    //                                                Upload
    //                                               -------
    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse uploadpage(final String dictId) {
        return asHtml(path_AdminDictSynonym_UploadJsp).useForm(UploadForm.class, op -> {
            op.setup(form -> {
                form.dictId = dictId;
            });
        }).renderWith(data -> {
            synonymService.getSynonymFile(dictId).ifPresent(file -> {
                data.register("path", file.getPath());
            }).orElse(() -> {
                throwValidationError(messages -> messages.addErrorsFailedToDownloadSynonymFile(GLOBAL), toIndexHtml());
            });
        });
    }

    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse upload(final UploadForm form) {
        validate(form, messages -> {}, () -> uploadpage(form.dictId));
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
    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, toEditHtml());
        createSynonymItem(form).ifPresent(entity -> {
            final String[] newInputs = splitLine(form.inputs);
            validateSynonymString(newInputs, () -> confirmfromcreate(form));
            entity.setNewInputs(newInputs);
            final String[] newOutputs = splitLine(form.outputs);
            validateSynonymString(newOutputs, () -> confirmfromcreate(form));
            entity.setNewOutputs(newOutputs);
            synonymService.store(form.dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL), toEditHtml());
        });
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    @Execute(token = TxToken.VALIDATE)
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, toEditHtml());
        createSynonymItem(form).ifPresent(entity -> {
            final String[] newInputs = splitLine(form.inputs);
            validateSynonymString(newInputs, () -> confirmfromupdate(form));
            entity.setNewInputs(newInputs);
            final String[] newOutputs = splitLine(form.outputs);
            validateSynonymString(newOutputs, () -> confirmfromupdate(form));
            entity.setNewOutputs(newOutputs);
            synonymService.store(form.dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()), toEditHtml());
        });
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    @Execute
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DELETE);
        validate(form, messages -> {}, toEditHtml());
        synonymService.getSynonymItem(form.dictId, form.id).ifPresent(entity -> {
            synonymService.delete(form.dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()), toEditHtml());
        });
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    //===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============

    protected OptionalEntity<SynonymItem> createSynonymItem(final CreateForm form) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            if (form instanceof CreateForm) {
                final SynonymItem entity = new SynonymItem(0, StringUtil.EMPTY_STRINGS, StringUtil.EMPTY_STRINGS);
                return OptionalEntity.of(entity);
            }
            break;
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

    protected VaErrorHook toIndexHtml() {
        return () -> {
            return redirect(AdminDictAction.class);
        };
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminDictSynonym_EditJsp);
        };
    }

    private void validateSynonymString(final String[] values, final VaErrorHook hook) {
        if (values.length == 0) {
            return;
        }
        for (final String value : values) {
            if (value.indexOf(',') >= 0) {
                throwValidationError(messages -> {
                    messages.addErrorsInvalidStrIsIncluded(GLOBAL, value, ",");
                }, hook);
            }
            if (value.indexOf("=>") >= 0) {
                throwValidationError(messages -> {
                    messages.addErrorsInvalidStrIsIncluded(GLOBAL, value, "=>");
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

}
