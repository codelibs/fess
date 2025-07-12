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
package org.codelibs.fess.app.web.admin.dict.stemmeroverride;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.StemmerOverridePager;
import org.codelibs.fess.app.service.StemmerOverrideService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.dict.AdminDictAction;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.dict.stemmeroverride.StemmerOverrideItem;
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
 * Admin action for Stemmer Override management.
 *
 * @author shinsuke
 */
public class AdminDictStemmeroverrideAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminDictStemmeroverrideAction() {
        // nothing
    }

    /**
     * The role for this action.
     */
    /** The role for this action. */
    public static final String ROLE = "admin-dict";

    private static final Logger logger = LogManager.getLogger(AdminDictStemmeroverrideAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private StemmerOverrideService stemmerOverrideService;
    @Resource
    private StemmerOverridePager stemmerOverridePager;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameDictStemmeroverride()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Display the main index page for stemmer override dictionary management.
     * Clears the pager and shows the initial search form.
     *
     * @param form The search form containing filter criteria
     * @return HTML response for the stemmer override index page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final SearchForm form) {
        validate(form, messages -> {}, this::asDictIndexHtml);
        stemmerOverridePager.clear();
        return asHtml(path_AdminDictStemmeroverride_AdminDictStemmeroverrideJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Display a paginated list of stemmer override items.
     * Sets the current page number and shows the list with pagination.
     *
     * @param pageNumber Optional page number to display (0-based)
     * @param form The search form containing filter criteria
     * @return HTML response showing the stemmer override list
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        validate(form, messages -> {}, this::asDictIndexHtml);
        pageNumber.ifPresent(num -> {
            stemmerOverridePager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            stemmerOverridePager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminDictStemmeroverride_AdminDictStemmeroverrideJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Perform a search operation for stemmer override items.
     * Updates the pager with search criteria and displays filtered results.
     *
     * @param form The search form containing search criteria
     * @return HTML response showing search results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        validate(form, messages -> {}, this::asDictIndexHtml);
        copyBeanToBean(form, stemmerOverridePager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminDictStemmeroverride_AdminDictStemmeroverrideJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Reset the search criteria and pager to default state.
     * Clears all filters and returns to the initial page view.
     *
     * @param form The search form to reset
     * @return HTML response showing the reset stemmer override list
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        validate(form, messages -> {}, this::asDictIndexHtml);
        stemmerOverridePager.clear();
        return asHtml(path_AdminDictStemmeroverride_AdminDictStemmeroverrideJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Populate render data with stemmer override items for pagination display.
     * Retrieves items based on search criteria and restores form data from pager.
     *
     * @param data The render data to populate
     * @param form The search form containing criteria
     */
    protected void searchPaging(final RenderData data, final SearchForm form) {
        // page navi
        RenderDataUtil.register(data, "stemmerOverrideItemItems",
                stemmerOverrideService.getStemmerOverrideList(form.dictId, stemmerOverridePager));

        // restore from pager
        BeanUtil.copyBeanToBean(stemmerOverridePager, form, op -> {
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
     * Show the create new page.
     * @param dictId The dictionary ID.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnew(final String dictId) {
        saveToken();
        return asHtml(path_AdminDictStemmeroverride_AdminDictStemmeroverrideEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
                form.dictId = dictId;
            });
        });
    }

    /**
     * Display the edit form for an existing stemmer override item.
     * Loads the item data and switches to edit mode or details view based on current state.
     *
     * @param form The edit form containing item ID and CRUD mode
     * @return HTML response for the edit page or details page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, () -> asListHtml(form.dictId));
        stemmerOverrideService.getStemmerOverrideItem(form.dictId, form.id).ifPresent(entity -> {
            form.input = entity.getInput();
            form.output = entity.getOutput();
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
     * Display detailed view of a specific stemmer override item.
     * Shows read-only details of the selected item.
     *
     * @param dictId The dictionary ID
     * @param crudMode The CRUD mode (should be DETAILS)
     * @param id The ID of the stemmer override item to display
     * @return HTML response showing item details
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final String dictId, final int crudMode, final long id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS, dictId);
        saveToken();
        return asDetailsHtml().useForm(EditForm.class, op -> {
            op.setup(form -> {
                stemmerOverrideService.getStemmerOverrideItem(dictId, id).ifPresent(entity -> {
                    form.input = entity.getInput();
                    form.output = entity.getOutput();
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
     * Display the download page for stemmer override dictionary file.
     * Shows the file path and provides download interface.
     *
     * @param dictId The dictionary ID to download
     * @return HTML response for the download page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse downloadpage(final String dictId) {
        saveToken();
        return asHtml(path_AdminDictStemmeroverride_AdminDictStemmeroverrideDownloadJsp).useForm(DownloadForm.class, op -> {
            op.setup(form -> {
                form.dictId = dictId;
            });
        }).renderWith(data -> {
            stemmerOverrideService.getStemmerOverrideFile(dictId).ifPresent(file -> {
                RenderDataUtil.register(data, "path", file.getPath());
            }).orElse(() -> {
                throwValidationError(messages -> messages.addErrorsFailedToDownloadStemmeroverrideFile(GLOBAL), this::asDictIndexHtml);
            });
        });
    }

    /**
     * Download the stemmer override dictionary file.
     * Streams the dictionary file as an octet-stream download.
     *
     * @param form The download form containing dictionary ID
     * @return Action response with file stream for download
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public ActionResponse download(final DownloadForm form) {
        validate(form, messages -> {}, () -> downloadpage(form.dictId));
        verifyTokenKeep(() -> downloadpage(form.dictId));
        return stemmerOverrideService.getStemmerOverrideFile(form.dictId)
                .map(file -> asStream(new File(file.getPath()).getName()).contentTypeOctetStream().stream(out -> {
                    file.writeOut(out);
                })).orElseGet(() -> {
                    throwValidationError(messages -> messages.addErrorsFailedToDownloadStemmeroverrideFile(GLOBAL),
                            () -> downloadpage(form.dictId));
                    return null;
                });
    }

    // -----------------------------------------------------
    //                                                Upload
    //                                               -------
    /**
     * Display the upload page for stemmer override dictionary file.
     * Shows the current file path and provides upload interface.
     *
     * @param dictId The dictionary ID to upload to
     * @return HTML response for the upload page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse uploadpage(final String dictId) {
        saveToken();
        return asHtml(path_AdminDictStemmeroverride_AdminDictStemmeroverrideUploadJsp).useForm(UploadForm.class, op -> {
            op.setup(form -> {
                form.dictId = dictId;
            });
        }).renderWith(data -> {
            stemmerOverrideService.getStemmerOverrideFile(dictId).ifPresent(file -> {
                RenderDataUtil.register(data, "path", file.getPath());
            }).orElse(() -> {
                throwValidationError(messages -> messages.addErrorsFailedToDownloadStemmeroverrideFile(GLOBAL), this::asDictIndexHtml);
            });
        });
    }

    /**
     * Upload a new stemmer override dictionary file.
     * Processes the uploaded file and updates the dictionary.
     *
     * @param form The upload form containing the file and dictionary ID
     * @return HTML response redirecting to the list page on success
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse upload(final UploadForm form) {
        validate(form, messages -> {}, () -> uploadpage(form.dictId));
        verifyToken(() -> uploadpage(form.dictId));
        return stemmerOverrideService.getStemmerOverrideFile(form.dictId).map(file -> {
            try (InputStream inputStream = form.stemmerOverrideFile.getInputStream()) {
                file.update(inputStream);
            } catch (final IOException e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsFailedToUploadStemmeroverrideFile(GLOBAL),
                        () -> redirectWith(getClass(), moreUrl("uploadpage/" + form.dictId)));
            }
            saveInfo(messages -> messages.addSuccessUploadStemmeroverrideFile(GLOBAL));
            return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
        }).orElseGet(() -> {
            throwValidationError(messages -> messages.addErrorsFailedToUploadStemmeroverrideFile(GLOBAL), () -> uploadpage(form.dictId));
            return null;
        });

    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    /**
     * Create a stemmer override item.
     * @param form The create form.
     * @return The HTML response.
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE, form.dictId);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        createStemmerOverrideItem(form, this::asEditHtml).ifPresent(entity -> {
            try {
                stemmerOverrideService.store(form.dictId, entity);
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
     * Update an existing stemmer override item.
     * Validates the form data and updates the item in the dictionary.
     *
     * @param form The edit form containing updated item data
     * @return HTML response redirecting to the list page on success
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT, form.dictId);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        createStemmerOverrideItem(form, this::asEditHtml).ifPresent(entity -> {
            try {
                stemmerOverrideService.store(form.dictId, entity);
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
     * Delete a stemmer override item from the dictionary.
     * Removes the specified item and redirects to the list page.
     *
     * @param form The edit form containing the item ID to delete
     * @return HTML response redirecting to the list page on success
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS, form.dictId);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        stemmerOverrideService.getStemmerOverrideItem(form.dictId, form.id).ifPresent(entity -> {
            try {
                stemmerOverrideService.delete(form.dictId, entity);
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

    private static OptionalEntity<StemmerOverrideItem> getEntity(final CreateForm form) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            final StemmerOverrideItem entity = new StemmerOverrideItem(0, StringUtil.EMPTY, StringUtil.EMPTY);
            return OptionalEntity.of(entity);
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(StemmerOverrideService.class).getStemmerOverrideItem(form.dictId, ((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    /**
     * Create a stemmer override item.
     * @param form The create form.
     * @param hook The error hook.
     * @return An optional entity of a stemmer override item.
     */
    protected OptionalEntity<StemmerOverrideItem> createStemmerOverrideItem(final CreateForm form, final VaErrorHook hook) {
        try {
            return createStemmerOverrideItem(this, form, hook);
        } catch (final ValidationErrorException e) {
            saveToken();
            throw e;
        }
    }

    /**
     * Get the stemmer override item.
     * @param action The action.
     * @param form The create form.
     * @param hook The error hook.
     * @return The stemmer override item.
     */
    public static OptionalEntity<StemmerOverrideItem> createStemmerOverrideItem(final FessBaseAction action, final CreateForm form,
            final VaErrorHook hook) {
        return getEntity(form).map(entity -> {
            entity.setNewInput(form.input);
            entity.setNewOutput(form.output);
            return entity;
        });
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    /**
     * Verify that the CRUD mode matches the expected mode.
     * Throws validation error if modes don't match.
     *
     * @param crudMode The current CRUD mode
     * @param expectedMode The expected CRUD mode
     * @param dictId The dictionary ID for error context
     */
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

    /**
     * Get the HTML response for the dictionary index page.
     * @return The HTML response.
     */
    protected HtmlResponse asDictIndexHtml() {
        return redirect(AdminDictAction.class);
    }

    private HtmlResponse asListHtml(final String dictId) {
        return asHtml(path_AdminDictStemmeroverride_AdminDictStemmeroverrideJsp).renderWith(data -> {
            RenderDataUtil.register(data, "stemmerOverrideItemItems",
                    stemmerOverrideService.getStemmerOverrideList(dictId, stemmerOverridePager));
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(stemmerOverridePager, form, op -> op.include("id"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminDictStemmeroverride_AdminDictStemmeroverrideEditJsp);
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminDictStemmeroverride_AdminDictStemmeroverrideDetailsJsp);
    }

}
