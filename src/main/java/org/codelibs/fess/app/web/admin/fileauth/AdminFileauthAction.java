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
package org.codelibs.fess.app.web.admin.fileauth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.FileAuthPager;
import org.codelibs.fess.app.service.FileAuthenticationService;
import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.FileAuthentication;
import org.codelibs.fess.opensearch.config.exentity.FileConfig;
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
 * Admin action for File Authentication management.
 *
 * @author shinsuke
 */
public class AdminFileauthAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminFileauthAction() {
        // Default constructor
    }

    /** The role name for file authentication administration. */
    public static final String ROLE = "admin-fileauth";

    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(AdminFileauthAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Service for file authentication operations. */
    @Resource
    private FileAuthenticationService fileAuthenticationService;

    /** Pager for file authentication list pagination. */
    @Resource
    private FileAuthPager fileAuthenticationPager;

    /** Service for file configuration operations. */
    @Resource
    protected FileConfigService fileConfigService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    /**
     * Sets up HTML data for rendering, including help link.
     *
     * @param runtime the action runtime
     */
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameFileauth()));
    }

    /**
     * Returns the action role for this admin action.
     *
     * @return the role name
     */
    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Displays the file authentication list page.
     *
     * @return HTML response for the list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        return asListHtml();
    }

    /**
     * Displays the file authentication list with pagination.
     *
     * @param pageNumber the page number
     * @param form the search form
     * @return HTML response for the list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            fileAuthenticationPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            fileAuthenticationPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminFileauth_AdminFileauthJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Searches file authentications based on the form criteria.
     *
     * @param form the search form
     * @return HTML response for the search results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, fileAuthenticationPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminFileauth_AdminFileauthJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Resets the search criteria and displays the default list.
     *
     * @param form the search form
     * @return HTML response for the reset list
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        fileAuthenticationPager.clear();
        return asHtml(path_AdminFileauth_AdminFileauthJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Sets up data for search result pagination.
     *
     * @param data the render data
     * @param form the search form
     */
    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "fileAuthenticationItems",
                fileAuthenticationService.getFileAuthenticationList(fileAuthenticationPager)); // page navi
        RenderDataUtil.register(data, "displayCreateLink", !crawlingConfigHelper.getAllFileConfigList(false, false, false, null).isEmpty());
        // restore from pager
        copyBeanToBean(fileAuthenticationPager, form, op -> op.include("id"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    /**
     * Displays the create new file authentication page.
     *
     * @return HTML response for the create page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnew() {
        saveToken();
        return asHtml(path_AdminFileauth_AdminFileauthEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        }).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerFileConfigItems(data);
        });
    }

    /**
     * Displays the edit file authentication page.
     *
     * @param form the edit form
     * @return HTML response for the edit page
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, this::asListHtml);
        final String id = form.id;
        fileAuthenticationService.getFileAuthentication(id).ifPresent(entity -> {
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
     * Displays the file authentication details page.
     *
     * @param crudMode the CRUD mode
     * @param id the file authentication ID
     * @return HTML response for the details page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        return asDetailsHtml().useForm(EditForm.class, op -> {
            op.setup(form -> {
                fileAuthenticationService.getFileAuthentication(id).ifPresent(entity -> {
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
     * Creates a new file authentication.
     *
     * @param form the create form
     * @return HTML response after creation
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getFileAuthentication(form).ifPresent(entity -> {
            try {
                fileAuthenticationService.store(entity);
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
     * Updates an existing file authentication.
     *
     * @param form the edit form
     * @return HTML response after update
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getFileAuthentication(form).ifPresent(entity -> {
            try {
                fileAuthenticationService.store(entity);
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
     * Deletes a file authentication.
     *
     * @param form the edit form
     * @return HTML response after deletion
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, this::asDetailsHtml);
        verifyToken(this::asDetailsHtml);
        final String id = form.id;
        fileAuthenticationService.getFileAuthentication(id).ifPresent(entity -> {
            try {
                fileAuthenticationService.delete(entity);
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

    //===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    /**
     * Gets a file authentication entity based on the form and current user info.
     *
     * @param form the create form
     * @param username the current username
     * @param currentTime the current time
     * @return optional file authentication entity
     */
    public static OptionalEntity<FileAuthentication> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new FileAuthentication()).map(entity -> {
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(FileAuthenticationService.class).getFileAuthentication(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    /**
     * Gets a file authentication entity from the form with system info.
     *
     * @param form the create form
     * @return optional file authentication entity
     */
    public static OptionalEntity<FileAuthentication> getFileAuthentication(final CreateForm form) {
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
     * Registers protocol scheme items for the dropdown list.
     *
     * @param data the render data
     */
    protected void registerProtocolSchemeItems(final RenderData data) {
        final List<Map<String, String>> itemList = new ArrayList<>();
        final Locale locale = ComponentUtil.getRequestManager().getUserLocale();
        itemList.add(createItem(ComponentUtil.getMessageManager().getMessage(locale, "labels.file_auth_scheme_samba"), Constants.SAMBA));
        itemList.add(createItem(ComponentUtil.getMessageManager().getMessage(locale, "labels.file_auth_scheme_ftp"), Constants.FTP));
        RenderDataUtil.register(data, "protocolSchemeItems", itemList);
    }

    /**
     * Registers file configuration items for the dropdown list.
     *
     * @param data the render data
     */
    protected void registerFileConfigItems(final RenderData data) {
        final List<Map<String, String>> itemList = new ArrayList<>();
        final List<FileConfig> fileConfigList = crawlingConfigHelper.getAllFileConfigList(false, false, false, null);
        for (final FileConfig fileConfig : fileConfigList) {
            itemList.add(createItem(fileConfig.getName(), fileConfig.getId().toString()));
        }
        RenderDataUtil.register(data, "fileConfigItems", itemList);
    }

    /**
     * Creates a dropdown item with label and value.
     *
     * @param label the item label
     * @param value the item value
     * @return map containing the item
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

    /**
     * Returns HTML response for the list page.
     *
     * @return HTML response for the list page
     */
    private HtmlResponse asListHtml() {
        return asHtml(path_AdminFileauth_AdminFileauthJsp).renderWith(data -> {
            RenderDataUtil.register(data, "fileAuthenticationItems",
                    fileAuthenticationService.getFileAuthenticationList(fileAuthenticationPager)); // page navi
            RenderDataUtil.register(data, "displayCreateLink",
                    !crawlingConfigHelper.getAllFileConfigList(false, false, false, null).isEmpty());
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(fileAuthenticationPager, form, op -> op.include("id"));
            });
        });
    }

    /**
     * Returns HTML response for the edit page.
     *
     * @return HTML response for the edit page
     */
    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminFileauth_AdminFileauthEditJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerFileConfigItems(data);
        });
    }

    /**
     * Returns HTML response for the details page.
     *
     * @return HTML response for the details page
     */
    private HtmlResponse asDetailsHtml() {
        return asHtml(path_AdminFileauth_AdminFileauthDetailsJsp).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerFileConfigItems(data);
        });
    }
}
