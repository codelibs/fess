/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import jakarta.annotation.Resource;

/**
 * Admin action for Synonym management.
 *
 * @author shinsuke
 * @author Keiichi Watanabe
 */
public class AdminDictSynonymAction extends FessAdminAction {

    /**
     * Role name required for accessing synonym dictionary administration features.
     */
    public static final String ROLE = "admin-dict";

    private static final Logger logger = LogManager.getLogger(AdminDictSynonymAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private SynonymService synonymService;
    @Resource
    private SynonymPager synonymPager;

    /**
     * Default constructor.
     */
    public AdminDictSynonymAction() {
        // Default constructor
    }

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
    /**
     * Displays the main synonym dictionary index page.
     *
     * @param form the search form containing search criteria
     * @return HTML response for the synonym dictionary index page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final SearchForm form) {
        validate(form, messages -> {}, this::asDictIndexHtml);
        synonymPager.clear();
        return asHtml(path_AdminDictSynonym_AdminDictSynonymJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Displays a paginated list of synonym items.
     *
     * @param pageNumber the optional page number for pagination
     * @param form the search form containing search criteria
     * @return HTML response with the synonym items list
     */
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

    /**
     * Performs a search for synonym items based on the provided criteria.
     *
     * @param form the search form containing search criteria
     * @return HTML response with the search results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        validate(form, messages -> {}, this::asDictIndexHtml);
        copyBeanToBean(form, synonymPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminDictSynonym_AdminDictSynonymJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Resets the search criteria and returns to the default view.
     *
     * @param form the search form to reset
     * @return HTML response with reset search criteria
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        validate(form, messages -> {}, this::asDictIndexHtml);
        synonymPager.clear();
        return asHtml(path_AdminDictSynonym_AdminDictSynonymJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Sets up pagination data for search results.
     *
     * @param data the render data to populate
     * @param form the search form containing criteria
     */
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
    /**
     * Displays the form for creating a new synonym item.
     *
     * @param dictId the dictionary ID
     * @return HTML response for the create new synonym form
     */
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

    /**
     * Displays the form for editing an existing synonym item.
     *
     * @param form the edit form containing synonym item data
     * @return HTML response for the edit synonym form
     */
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
    /**
     * Displays the details view for a specific synonym item.
     *
     * @param dictId the dictionary ID
     * @param crudMode the CRUD operation mode
     * @param id the synonym item ID
     * @return HTML response for the synonym item details
     */
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
    /**
     * Displays the download page for synonym dictionary files.
     *
     * @param dictId the dictionary ID
     * @return HTML response for the download page
     */
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

    /**
     * Downloads the synonym dictionary file.
     *
     * @param form the download form containing download parameters
     * @return ActionResponse with the file download stream
     */
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
    /**
     * Displays the upload page for synonym dictionary files.
     *
     * @param dictId the dictionary ID
     * @return HTML response for the upload page
     */
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

    /**
     * Handles the upload of synonym dictionary files.
     *
     * @param form the upload form containing the file to upload
     * @return HTML response after processing the upload
     */
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
    /**
     * Creates a new synonym item.
     *
     * @param form the create form containing synonym item data
     * @return HTML response after creating the synonym item
     */
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

    /**
     * Updates an existing synonym item.
     *
     * @param form the edit form containing updated synonym item data
     * @return HTML response after updating the synonym item
     */
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

    /**
     * Deletes an existing synonym item.
     *
     * @param form the edit form containing the synonym item to delete
     * @return HTML response after deleting the synonym item
     */
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

    /**
     * Creates a synonym item from the provided form data with validation.
     *
     * @param form the create form containing synonym data
     * @param hook the validation error hook for handling errors
     * @return OptionalEntity containing the created synonym item or empty if creation failed
     */
    protected OptionalEntity<SynonymItem> createSynonymItem(final CreateForm form, final VaErrorHook hook) {
        try {
            return createSynonymItem(this, form, hook);
        } catch (final ValidationErrorException e) {
            saveToken();
            throw e;
        }
    }

    /**
     * Static method to create a synonym item from form data with validation.
     *
     * @param action the base action for validation operations
     * @param form the create form containing synonym data
     * @param hook the validation error hook for handling errors
     * @return OptionalEntity containing the created synonym item or empty if creation failed
     */
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
    /**
     * Verifies that the CRUD mode matches the expected mode.
     *
     * @param crudMode the current CRUD mode
     * @param expectedMode the expected CRUD mode
     * @param dictId the dictionary ID for error handling
     */
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

    /**
     * Redirects to the dictionary index page.
     *
     * @return HTML response redirecting to the dictionary index
     */
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
