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
package org.codelibs.fess.app.web.admin.dict.stopwords;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.StopwordsPager;
import org.codelibs.fess.app.service.StopwordsService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.dict.AdminDictAction;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.dict.stopwords.StopwordsItem;
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

import jakarta.annotation.Resource;

/**
 * Admin action for Stopwords management.
 *
 */
public class AdminDictStopwordsAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminDictStopwordsAction() {
        super();
    }

    /** The role for this action. */
    public static final String ROLE = "admin-dict";

    private static final Logger logger = LogManager.getLogger(AdminDictStopwordsAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private StopwordsService stopwordsService;
    @Resource
    private StopwordsPager stopwordsPager;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameDictStopwords()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Display the stopwords index page.
     *
     * @param form the search form
     * @return HTML response for the index page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final SearchForm form) {
        validate(form, messages -> {}, this::asDictIndexHtml);
        stopwordsPager.clear();
        return asHtml(path_AdminDictStopwords_AdminDictStopwordsJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Display the stopwords list with pagination.
     *
     * @param pageNumber the page number to display
     * @param form the search form
     * @return HTML response for the list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        validate(form, messages -> {}, this::asDictIndexHtml);
        pageNumber.ifPresent(num -> {
            stopwordsPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            stopwordsPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminDictStopwords_AdminDictStopwordsJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Perform search for stopwords.
     *
     * @param form the search form containing search criteria
     * @return HTML response with search results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        validate(form, messages -> {}, this::asDictIndexHtml);
        copyBeanToBean(form, stopwordsPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminDictStopwords_AdminDictStopwordsJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Reset search criteria and return to default view.
     *
     * @param form the search form to reset
     * @return HTML response for the reset page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        validate(form, messages -> {}, this::asDictIndexHtml);
        stopwordsPager.clear();
        return asHtml(path_AdminDictStopwords_AdminDictStopwordsJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Set up pagination data for search results.
     *
     * @param data the render data to populate
     * @param form the search form containing pagination parameters
     */
    protected void searchPaging(final RenderData data, final SearchForm form) {
        // page navi
        RenderDataUtil.register(data, "stopwordsItemItems", stopwordsService.getStopwordsList(form.dictId, stopwordsPager));

        // restore from pager
        BeanUtil.copyBeanToBean(stopwordsPager, form, op -> {
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
     * Display the form for creating a new stopwords entry.
     *
     * @param dictId the dictionary ID
     * @return HTML response for the create form
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnew(final String dictId) {
        saveToken();
        return asHtml(path_AdminDictStopwords_AdminDictStopwordsEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
                form.dictId = dictId;
            });
        });
    }

    /**
     * Display the edit form for an existing stopwords entry.
     *
     * @param form the edit form containing the entry ID and dictionary ID
     * @return HTML response for the edit form
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, () -> asListHtml(form.dictId));
        stopwordsService.getStopwordsItem(form.dictId, form.id).ifPresent(entity -> {
            form.input = entity.getInputValue();
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
     * Display details of a specific stopwords entry.
     *
     * @param dictId the dictionary ID
     * @param crudMode the CRUD mode for the operation
     * @param id the entry ID
     * @return HTML response for the details page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final String dictId, final int crudMode, final long id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS, dictId);
        saveToken();
        return asDetailsHtml().useForm(EditForm.class, op -> {
            op.setup(form -> {
                stopwordsService.getStopwordsItem(dictId, id).ifPresent(entity -> {
                    form.input = entity.getInputValue();
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
     * Display the download page for stopwords file.
     *
     * @param dictId the dictionary ID
     * @return HTML response for the download page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse downloadpage(final String dictId) {
        saveToken();
        return asHtml(path_AdminDictStopwords_AdminDictStopwordsDownloadJsp).useForm(DownloadForm.class, op -> {
            op.setup(form -> {
                form.dictId = dictId;
            });
        }).renderWith(data -> {
            stopwordsService.getStopwordsFile(dictId).ifPresent(file -> {
                RenderDataUtil.register(data, "path", file.getPath());
            }).orElse(() -> {
                throwValidationError(messages -> messages.addErrorsFailedToDownloadStopwordsFile(GLOBAL), this::asDictIndexHtml);
            });
        });
    }

    /**
     * Download the stopwords file.
     *
     * @param form the download form containing dictionary ID
     * @return action response with the file download stream
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public ActionResponse download(final DownloadForm form) {
        validate(form, messages -> {}, () -> downloadpage(form.dictId));
        verifyTokenKeep(() -> downloadpage(form.dictId));
        return stopwordsService.getStopwordsFile(form.dictId)
                .map(file -> asStream(new File(file.getPath()).getName()).contentTypeOctetStream().stream(out -> {
                    file.writeOut(out);
                })).orElseGet(() -> {
                    throwValidationError(messages -> messages.addErrorsFailedToDownloadStopwordsFile(GLOBAL),
                            () -> downloadpage(form.dictId));
                    return null;
                });
    }

    // -----------------------------------------------------
    //                                                Upload
    //                                               -------
    /**
     * Display the upload page for stopwords file.
     *
     * @param dictId the dictionary ID
     * @return HTML response for the upload page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse uploadpage(final String dictId) {
        saveToken();
        return asHtml(path_AdminDictStopwords_AdminDictStopwordsUploadJsp).useForm(UploadForm.class, op -> {
            op.setup(form -> {
                form.dictId = dictId;
            });
        }).renderWith(data -> {
            stopwordsService.getStopwordsFile(dictId).ifPresent(file -> {
                RenderDataUtil.register(data, "path", file.getPath());
            }).orElse(() -> {
                throwValidationError(messages -> messages.addErrorsFailedToDownloadStopwordsFile(GLOBAL), this::asDictIndexHtml);
            });
        });
    }

    /**
     * Upload a stopwords file.
     *
     * @param form the upload form containing the file and dictionary ID
     * @return HTML response redirecting to the list page after successful upload
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse upload(final UploadForm form) {
        validate(form, messages -> {}, () -> uploadpage(form.dictId));
        verifyToken(() -> uploadpage(form.dictId));
        return stopwordsService.getStopwordsFile(form.dictId).map(file -> {
            try (InputStream inputStream = form.stopwordsFile.getInputStream()) {
                file.update(inputStream);
            } catch (final IOException e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsFailedToUploadStopwordsFile(GLOBAL),
                        () -> redirectWith(getClass(), moreUrl("uploadpage/" + form.dictId)));
            }
            saveInfo(messages -> messages.addSuccessUploadStopwordsFile(GLOBAL));
            return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
        }).orElseGet(() -> {
            throwValidationError(messages -> messages.addErrorsFailedToUploadStopwordsFile(GLOBAL), () -> uploadpage(form.dictId));
            return null;
        });

    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    /**
     * Create a new stopwords entry.
     *
     * @param form the create form containing the new entry data
     * @return HTML response redirecting to the list page after successful creation
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE, form.dictId);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        createStopwordsItem(form, this::asEditHtml).ifPresent(entity -> {
            stopwordsService.store(form.dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        }).orElse(() -> throwValidationError(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL), this::asEditHtml));
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    /**
     * Update an existing stopwords entry.
     *
     * @param form the edit form containing the updated entry data
     * @return HTML response redirecting to the list page after successful update
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT, form.dictId);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        createStopwordsItem(form, this::asEditHtml).ifPresent(entity -> {
            stopwordsService.store(form.dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        }).orElse(() -> throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()),
                this::asEditHtml));
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    /**
     * Delete a stopwords entry.
     *
     * @param form the edit form containing the entry ID to delete
     * @return HTML response redirecting to the list page after successful deletion
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS, form.dictId);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        stopwordsService.getStopwordsItem(form.dictId, form.id).ifPresent(entity -> {
            stopwordsService.delete(form.dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.getDisplayId()), this::asDetailsHtml);
        });
        return redirectWith(getClass(), moreUrl("list/1").params("dictId", form.dictId));
    }

    //===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============

    private static OptionalEntity<StopwordsItem> getEntity(final CreateForm form) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            final StopwordsItem entity = new StopwordsItem(0, StringUtil.EMPTY);
            return OptionalEntity.of(entity);
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(StopwordsService.class).getStopwordsItem(form.dictId, ((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    /**
     * Create a StopwordsItem from the form data.
     *
     * @param form the create form containing the item data
     * @param hook the validation error hook
     * @return optional entity containing the created stopwords item
     */
    public static OptionalEntity<StopwordsItem> createStopwordsItem(final CreateForm form, final VaErrorHook hook) {
        return getEntity(form).map(entity -> {
            final String newInput = form.input;
            validateStopwordsString(newInput, "input", hook);
            entity.setNewInput(newInput);
            return entity;
        });
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    /**
     * Verify that the CRUD mode matches the expected mode.
     *
     * @param crudMode the actual CRUD mode
     * @param expectedMode the expected CRUD mode
     * @param dictId the dictionary ID for error redirection
     */
    protected void verifyCrudMode(final int crudMode, final int expectedMode, final String dictId) {
        if (crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(crudMode));
            }, () -> asListHtml(dictId));
        }
    }

    private static void validateStopwordsString(final String values, final String propertyName, final VaErrorHook hook) {
        // TODO validation
    }

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========

    /**
     * Redirect to the dictionary index page.
     *
     * @return HTML response redirecting to the dictionary index
     */
    protected HtmlResponse asDictIndexHtml() {
        return redirect(AdminDictAction.class);
    }

    private HtmlResponse asListHtml(final String dictId) {
        return asHtml(path_AdminDictStopwords_AdminDictStopwordsJsp).renderWith(data -> {
            RenderDataUtil.register(data, "stopwordsItemItems", stopwordsService.getStopwordsList(dictId, stopwordsPager));
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(stopwordsPager, form, op -> op.include("id"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminDictStopwords_AdminDictStopwordsEditJsp);
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminDictStopwords_AdminDictStopwordsDetailsJsp);
    }

}
