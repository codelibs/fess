/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.SynonymPager;
import org.codelibs.fess.app.service.SynonymService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.dict.AdminDictAction;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.dict.synonym.SynonymItem;
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
public class AdminDictSynonymAction extends FessAdminAction {

    public static final String ROLE = "admin-dict";

    private static final Logger logger = LogManager.getLogger(AdminDictSynonymAction.class);

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

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final SearchForm form) {
        validate(form, messages -> {}, this::asDictIndexHtml);
        synonymPager.clear();
        return asHtml(path_AdminDictSynonym_AdminDictSynonymJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        validate(form, messages -> {}, this::asDictIndexHtml);
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
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        validate(form, messages -> {}, this::asDictIndexHtml);
        copyBeanToBean(form, synonymPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminDictSynonym_AdminDictSynonymJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        validate(form, messages -> {}, this::asDictIndexHtml);
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
    @Secured({ ROLE })
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
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, () -> asListHtml(form.dictId));
        synonymService.getSynonymItem(form.dictId, form.id).ifPresent(entity -> {
            form.inputs = entity.getInputsValue();
            form.outputs = entity.getOutputsValue();
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()),
                    () -> asListHtml(form.dictId));
        });
        saveToken();
        if (form.crudMode.intValue() == CrudMode.EDIT) {
            // back
            form.crudMode = CrudMode.DETAILS;
            return asDetailsHtml();
        }
        form.crudMode = CrudMode.EDIT;
        return asEditHtml();
    }

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final String dictId, final int crudMode, final long id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS, dictId);
        saveToken();
        return asDetailsHtml().useForm(EditForm.class, op -> {
            op.setup(form -> {
                synonymService.getSynonymItem(dictId, id).ifPresent(entity -> {
                    form.inputs = entity.getInputsValue();
                    form.outputs = entity.getOutputsValue();
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, dictId + ":" + id),
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
    @Secured({ ROLE, ROLE + VIEW })
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
                throwValidationError(messages -> messages.addErrorsFailedToDownloadSynonymFile(GLOBAL), this::asDictIndexHtml);
            });
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public ActionResponse download(final DownloadForm form) {
        validate(form, messages -> {}, () -> downloadpage(form.dictId));
        verifyTokenKeep(() -> downloadpage(form.dictId));
        return synonymService.getSynonymFile(form.dictId)
                .map(file -> asStream(new File(file.getPath()).getName()).contentTypeOctetStream().stream(out -> {
                    file.writeOut(out);
                })).orElseGet(() -> {
                    throwValidationError(messages -> messages.addErrorsFailedToDownloadSynonymFile(GLOBAL),
                            () -> downloadpage(form.dictId));
                    return null;
                });
    }

    // -----------------------------------------------------
    //                                                Upload
    //                                               -------
    @Execute
    @Secured({ ROLE })
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
                throwValidationError(messages -> messages.addErrorsFailedToDownloadSynonymFile(GLOBAL), this::asDictIndexHtml);
            });
        });
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse upload(final UploadForm form) {
        validate(form, messages -> {}, () -> uploadpage(form.dictId));
        verifyToken(() -> uploadpage(form.dictId));
        return synonymService.getSynonymFile(form.dictId).map(file -> {
            try (InputStream inputStream = form.synonymFile.getInputStream()) {
                file.update(inputStream);
            } catch (final IOException e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsFailedToUploadSynonymFile(GLOBAL),
                        () -> redirectWith(getClass(), moreUrl("uploadpage/" + form.dictId)));
            }
            saveInfo(messages -> messages.addSuccessUploadSynonymFile(GLOBAL));
            return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
        }).orElseGet(() -> {
            throwValidationError(messages -> messages.addErrorsFailedToUploadSynonymFile(GLOBAL), () -> uploadpage(form.dictId));
            return null;
        });

    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE, form.dictId);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        createSynonymItem(form, this::asEditHtml).ifPresent(entity -> {
            try {
                synonymService.store(form.dictId, entity);
                saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL), this::asEditHtml);
        });
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT, form.dictId);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        createSynonymItem(form, this::asEditHtml).ifPresent(entity -> {
            try {
                synonymService.store(form.dictId, entity);
                saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            saveToken();
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()), this::asEditHtml);
        });
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS, form.dictId);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        synonymService.getSynonymItem(form.dictId, form.id).ifPresent(entity -> {
            try {
                synonymService.delete(form.dictId, entity);
                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()), this::asDetailsHtml);
        });
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    //===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============

    private static OptionalEntity<SynonymItem> getEntity(final CreateForm form) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            final SynonymItem entity = new SynonymItem(0, StringUtil.EMPTY_STRINGS, StringUtil.EMPTY_STRINGS);
            return OptionalEntity.of(entity);
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(SynonymService.class).getSynonymItem(form.dictId, ((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    protected OptionalEntity<SynonymItem> createSynonymItem(final CreateForm form, final VaErrorHook hook) {
        try {
            return createSynonymItem(this, form, hook);
        } catch (final ValidationErrorException e) {
            saveToken();
            throw e;
        }
    }

    public static OptionalEntity<SynonymItem> createSynonymItem(final FessBaseAction action, final CreateForm form,
            final VaErrorHook hook) {
        return getEntity(form).map(entity -> {
            final String[] newInputs = splitLine(form.inputs);
            validateSynonymString(action, newInputs, "inputs", hook);
            entity.setNewInputs(newInputs);
            final String[] newOutputs = splitLine(form.outputs);
            validateSynonymString(action, newOutputs, "outputs", hook);
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

    private static void validateSynonymString(final FessBaseAction action, final String[] values, final String propertyName,
            final VaErrorHook hook) {
        if (values.length == 0) {
            return;
        }
        for (final String value : values) {
            if (value.indexOf(',') >= 0) {
                action.throwValidationError(messages -> {
                    messages.addErrorsInvalidStrIsIncluded(propertyName, value, ",");
                }, hook);
            }
            if (value.indexOf("=>") >= 0) {
                action.throwValidationError(messages -> {
                    messages.addErrorsInvalidStrIsIncluded(propertyName, value, "=>");
                }, hook);
            }
        }
    }

    private static String[] splitLine(final String value) {
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
