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
package org.codelibs.fess.app.web.admin.reqheader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.ReqHeaderPager;
import org.codelibs.fess.app.service.RequestHeaderService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.RequestHeader;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;

import jakarta.annotation.Resource;

/**
 * Admin action for Request Header management.
 *
 */
public class AdminReqheaderAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminReqheaderAction() {
        // Default constructor
    }

    /** Role name for admin request header operations */
    public static final String ROLE = "admin-reqheader";

    private static final Logger logger = LogManager.getLogger(AdminReqheaderAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private RequestHeaderService requestHeaderService;
    @Resource
    private ReqHeaderPager reqHeaderPager;
    /** Web configuration service for managing web crawl configurations. */
    @Resource
    protected WebConfigService webConfigService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameReqheader()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Displays the request header management index page.
     *
     * @param form the search form for filtering
     * @return HTML response for the request header list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final SearchForm form) {
        return asListHtml();
    }

    /**
     * Displays a paginated list of request header items.
     *
     * @param pageNumber the page number to display (optional)
     * @param form the search form containing filter criteria
     * @return HTML response with the request header list
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            reqHeaderPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            reqHeaderPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminReqheader_AdminReqheaderJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Searches for request header items based on the provided search criteria.
     *
     * @param form the search form containing search criteria
     * @return HTML response with filtered request header results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, reqHeaderPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminReqheader_AdminReqheaderJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Resets the search criteria and displays all request header items.
     *
     * @param form the search form to reset
     * @return HTML response with the reset request header list
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        reqHeaderPager.clear();
        return asHtml(path_AdminReqheader_AdminReqheaderJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Sets up search paging data for rendering the request header list.
     *
     * @param data the render data to populate
     * @param form the search form containing current search criteria
     */
    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "requestHeaderItems", requestHeaderService.getRequestHeaderList(reqHeaderPager)); // page navi
        RenderDataUtil.register(data, "displayCreateLink", !crawlingConfigHelper.getAllWebConfigList(false, false, false, null).isEmpty());

        // restore from pager
        copyBeanToBean(reqHeaderPager, form, op -> op.include("id"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    /**
     * Displays the form for creating a new request header item.
     *
     * @return HTML response for the create form
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnew() {
        saveToken();
        return asHtml(path_AdminReqheader_AdminReqheaderEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        }).renderWith(data -> {
            registerWebConfigItems(data);
        });
    }

    /**
     * Displays the form for editing an existing request header item.
     *
     * @param form the edit form containing the ID of the item to edit
     * @return HTML response for the edit form
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, this::asListHtml);
        final String id = form.id;
        requestHeaderService.getRequestHeader(id).ifPresent(entity -> {
            copyBeanToBean(entity, form, op -> {});
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml);
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
     * Displays the details of a request header item.
     *
     * @param crudMode the CRUD mode for the operation
     * @param id the ID of the request header item to display
     * @return HTML response for the details page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        return asDetailsHtml().useForm(EditForm.class, op -> {
            op.setup(form -> {
                requestHeaderService.getRequestHeader(id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                    });
                    form.crudMode = crudMode;
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml);
                });
            });
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    /**
     * Creates a new request header item.
     *
     * @param form the create form containing the new item data
     * @return HTML response redirecting to the list page after creation
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getRequestHeader(form).ifPresent(entity -> {
            try {
                requestHeaderService.store(entity);
                saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL), this::asEditHtml);
        });
        return redirect(getClass());
    }

    /**
     * Updates an existing request header item.
     *
     * @param form the edit form containing the updated item data
     * @return HTML response redirecting to the list page after update
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getRequestHeader(form).ifPresent(entity -> {
            try {
                requestHeaderService.store(entity);
                saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), this::asEditHtml);
        });
        return redirect(getClass());
    }

    /**
     * Deletes a request header item.
     *
     * @param form the edit form containing the ID of the item to delete
     * @return HTML response redirecting to the list page after deletion
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        final String id = form.id;
        requestHeaderService.getRequestHeader(id).ifPresent(entity -> {
            try {
                requestHeaderService.delete(entity);
                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asDetailsHtml);
        });
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    /**
     * Creates a RequestHeader entity from form data with user and timestamp information.
     *
     * @param form the form containing the request header data
     * @param username the username of the user performing the operation
     * @param currentTime the current timestamp
     * @return optional entity containing the request header data, or empty if creation fails
     */
    public static OptionalEntity<RequestHeader> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new RequestHeader()).map(entity -> {
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(RequestHeaderService.class).getRequestHeader(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    /**
     * Creates a RequestHeader entity from the provided form data.
     *
     * @param form the form containing the request header data
     * @return optional entity containing the request header data, or empty if creation fails
     */
    public static OptionalEntity<RequestHeader> getRequestHeader(final CreateForm form) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        return getEntity(form, username, currentTime).map(entity -> {
            entity.setUpdatedBy(username);
            entity.setUpdatedTime(currentTime);
            copyBeanToBean(form, entity, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
            return entity;
        });
    }

    /**
     * Registers web configuration items for rendering in the UI.
     *
     * @param data the render data to populate with web config items
     */
    protected void registerWebConfigItems(final RenderData data) {
        final List<Map<String, String>> itemList = new ArrayList<>();
        final List<WebConfig> webConfigList = crawlingConfigHelper.getAllWebConfigList(false, false, false, null);
        for (final WebConfig webConfig : webConfigList) {
            itemList.add(createItem(webConfig.getName(), webConfig.getId().toString()));
        }
        RenderDataUtil.register(data, "webConfigItems", itemList);
    }

    /**
     * Creates a map item for UI rendering with label and value.
     *
     * @param label the display label for the item
     * @param value the value for the item
     * @return map containing the label and value
     */
    protected Map<String, String> createItem(final String label, final String value) {
        final Map<String, String> map = new HashMap<>(2);
        map.put(Constants.ITEM_LABEL, label);
        map.put(Constants.ITEM_VALUE, value);
        return map;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    /**
     * Verifies that the CRUD mode matches the expected mode.
     *
     * @param crudMode the actual CRUD mode
     * @param expectedMode the expected CRUD mode
     */
    protected void verifyCrudMode(final int crudMode, final int expectedMode) {
        if (crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(crudMode));
            }, this::asListHtml);
        }
    }

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminReqheader_AdminReqheaderJsp).renderWith(data -> {
            RenderDataUtil.register(data, "requestHeaderItems", requestHeaderService.getRequestHeaderList(reqHeaderPager)); // page navi
            RenderDataUtil.register(data, "displayCreateLink",
                    !crawlingConfigHelper.getAllWebConfigList(false, false, false, null).isEmpty());
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(reqHeaderPager, form, op -> op.include("id"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminReqheader_AdminReqheaderEditJsp).renderWith(data -> {
            registerWebConfigItems(data);
        });
    }

    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminReqheader_AdminReqheaderDetailsJsp).renderWith(data -> {
            registerWebConfigItems(data);
        });
    }
}
