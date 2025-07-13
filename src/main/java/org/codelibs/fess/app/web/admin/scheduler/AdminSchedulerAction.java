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
package org.codelibs.fess.app.web.admin.scheduler;

import java.text.MessageFormat;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.SchedulerPager;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.ProcessHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.ScheduledJob;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.util.LaRequestUtil;

import jakarta.annotation.Resource;

/**
 * Admin action for Scheduler management.
 *
 */
public class AdminSchedulerAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminSchedulerAction() {
        // Default constructor
    }

    /** Role name for admin scheduler operations */
    public static final String ROLE = "admin-scheduler";

    private static final Logger logger = LogManager.getLogger(AdminSchedulerAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    /** Service for managing scheduled jobs */
    private ScheduledJobService scheduledJobService;
    @Resource
    /** Pager for paginating scheduled job results */
    private SchedulerPager schedulerPager;
    /** Helper for processing scheduled jobs. */
    @Resource
    protected ProcessHelper processHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameScheduler()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    /**
     * Displays the scheduler management index page.
     *
     * @param form the search form for filtering
     * @return HTML response for the scheduler list page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final SearchForm form) {
        return asListHtml();
    }

    /**
     * Displays a paginated list of scheduled jobs.
     *
     * @param pageNumber the page number to display (optional)
     * @param form the search form containing filter criteria
     * @return HTML response with the scheduled job list
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            schedulerPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            schedulerPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminScheduler_AdminSchedulerJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Searches for scheduled jobs based on the provided search criteria.
     *
     * @param form the search form containing search criteria
     * @return HTML response with filtered scheduled job results
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, schedulerPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminScheduler_AdminSchedulerJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Resets the search criteria and displays all scheduled jobs.
     *
     * @param form the search form to reset
     * @return HTML response with the reset scheduled job list
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        schedulerPager.clear();
        return asHtml(path_AdminScheduler_AdminSchedulerJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    /**
     * Sets up search paging data for rendering the scheduled job list.
     *
     * @param data the render data to populate
     * @param form the search form containing current search criteria
     */
    protected void searchPaging(final RenderData data, final SearchForm form) {
        RenderDataUtil.register(data, "scheduledJobItems", scheduledJobService.getScheduledJobList(schedulerPager)); // page navi

        // restore from pager
        copyBeanToBean(schedulerPager, form, op -> op.include("id"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------

    /**
     * Creates a new scheduled job from a crawler configuration.
     *
     * @param type the crawler type (web, file, or data)
     * @param id the crawler configuration ID
     * @param name the name for the new job (base64 encoded)
     * @return HTML response for the job creation form
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnewjob(final String type, final String id, final String name) {
        saveToken();
        return asHtml(path_AdminScheduler_AdminSchedulerEditJsp).useForm(CreateForm.class, op -> {
            op.setup(scheduledJobForm -> {
                scheduledJobForm.initialize();
                scheduledJobForm.crudMode = CrudMode.CREATE;
                scheduledJobForm.jobLogging = Constants.ON;
                scheduledJobForm.crawler = Constants.ON;
                scheduledJobForm.available = Constants.ON;
                scheduledJobForm.cronExpression = null;
                final String decodedName = new String(Base64.getUrlDecoder().decode(name), Constants.CHARSET_UTF_8);
                scheduledJobForm.name = MessageFormat.format(fessConfig.getJobTemplateTitle(type), decodedName);
                final String[] ids = { "", "", "" };
                if (Constants.WEB_CRAWLER_TYPE.equals(type)) {
                    ids[0] = "\"" + id + "\"";
                } else if (Constants.FILE_CRAWLER_TYPE.equals(type)) {
                    ids[1] = "\"" + id + "\"";
                } else if (Constants.DATA_CRAWLER_TYPE.equals(type)) {
                    ids[2] = "\"" + id + "\"";
                }
                scheduledJobForm.scriptData =
                        MessageFormat.format(fessConfig.getJobTemplateScript(), ids[0], ids[1], ids[2], id.replace('-', '_'));
            });
        });
    }

    /**
     * Displays the form for creating a new scheduled job.
     *
     * @return HTML response for the job creation form
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse createnew() {
        saveToken();
        return asHtml(path_AdminScheduler_AdminSchedulerEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        });
    }

    /**
     * Displays the form for editing an existing scheduled job.
     *
     * @param form the edit form containing the ID of the job to edit
     * @return HTML response for the job edit form
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, this::asListHtml);
        final String id = form.id;
        scheduledJobService.getScheduledJob(id).ifPresent(entity -> {
            loadScheduledJob(form, entity);
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), this::asListHtml);
        });
        saveToken();
        if (form.crudMode.intValue() == CrudMode.EDIT) {
            // back
            form.crudMode = CrudMode.DETAILS;
            return asDetailsHtml(id);
        }
        form.crudMode = CrudMode.EDIT;
        return asEditHtml();
    }

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    /**
     * Displays the details of a scheduled job.
     *
     * @param crudMode the CRUD mode for the operation
     * @param id the ID of the scheduled job to display
     * @return HTML response for the job details page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        saveToken();
        return asHtml(path_AdminScheduler_AdminSchedulerDetailsJsp).renderWith(data -> {
            RenderDataUtil.register(data, "systemJobId", fessConfig.isSystemJobId(id));
        }).useForm(EditForm.class, op -> {
            op.setup(form -> {
                scheduledJobService.getScheduledJob(id).ifPresent(entity -> {
                    loadScheduledJob(form, entity);
                    form.crudMode = crudMode;
                    LaRequestUtil.getOptionalRequest().ifPresent(request -> {
                        request.setAttribute("running", entity.isRunning());
                        request.setAttribute("enabled", entity.isEnabled());
                    });
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
     * Creates a new scheduled job.
     *
     * @param form the create form containing the new job data
     * @return HTML response redirecting to the list page after creation
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getScheduledJob(form).ifPresent(entity -> {
            try {
                scheduledJobService.store(entity);
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
     * Updates an existing scheduled job.
     *
     * @param form the edit form containing the updated job data
     * @return HTML response redirecting to the list page after update
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, this::asEditHtml);
        verifyToken(this::asEditHtml);
        getScheduledJob(form).ifPresent(entity -> {
            try {
                scheduledJobService.store(entity);
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
     * Deletes a scheduled job.
     *
     * @param form the edit form containing the ID of the job to delete
     * @return HTML response redirecting to the list page after deletion
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        final String id = form.id;
        validate(form, messages -> {}, () -> asDetailsHtml(id));
        verifyToken(() -> asDetailsHtml(id));
        scheduledJobService.getScheduledJob(id).ifPresent(entity -> {
            try {
                scheduledJobService.delete(entity);
                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)),
                        this::asEditHtml);
            }
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), () -> asDetailsHtml(id));
        });
        return redirect(getClass());
    }

    /**
     * Starts a scheduled job.
     *
     * @param form the edit form containing the ID of the job to start
     * @return HTML response redirecting to the list page after starting
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse start(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        final String id = form.id;
        validate(form, messages -> {}, () -> asDetailsHtml(id));
        verifyToken(() -> asDetailsHtml(id));
        scheduledJobService.getScheduledJob(id).ifPresent(entity -> {
            if (!entity.isEnabled() || entity.isRunning()) {
                throwValidationError(messages -> {
                    messages.addErrorsFailedToStartJob(GLOBAL, entity.getName());
                }, () -> asDetailsHtml(id));
            }
            try {
                entity.start();
                saveInfo(messages -> messages.addSuccessJobStarted(GLOBAL, entity.getName()));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> {
                    messages.addErrorsFailedToStartJob(GLOBAL, entity.getName());
                }, () -> asDetailsHtml(id));
            }
        }).orElse(() -> {
            throwValidationError(messages -> {
                messages.addErrorsFailedToStartJob(GLOBAL, id);
            }, () -> asDetailsHtml(id));
        });
        return redirect(getClass());
    }

    /**
     * Stops a running scheduled job.
     *
     * @param form the edit form containing the ID of the job to stop
     * @return HTML response redirecting to the list page after stopping
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse stop(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        final String id = form.id;
        validate(form, messages -> {}, () -> asDetailsHtml(id));
        verifyToken(() -> asDetailsHtml(id));
        scheduledJobService.getScheduledJob(id).ifPresent(entity -> {
            try {
                entity.stop();
                saveInfo(messages -> messages.addSuccessJobStopped(GLOBAL, entity.getName()));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationError(messages -> {
                    messages.addErrorsFailedToStopJob(GLOBAL, entity.getName());
                }, () -> asDetailsHtml(id));
            }
        }).orElse(() -> {
            throwValidationError(messages -> {
                messages.addErrorsFailedToStartJob(GLOBAL, id);
            }, () -> asDetailsHtml(id));
        });
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    /**
     * Loads scheduled job data into the edit form.
     *
     * @param form the edit form to populate
     * @param entity the scheduled job entity to load from
     */
    protected void loadScheduledJob(final EditForm form, final ScheduledJob entity) {
        copyBeanToBean(entity, form, op -> op.exclude("crudMode").excludeNull());
        form.jobLogging = entity.isLoggingEnabled() ? Constants.ON : null;
        form.crawler = entity.isCrawlerJob() ? Constants.ON : null;
        form.available = entity.isEnabled() ? Constants.ON : null;
    }

    /**
     * Creates a ScheduledJob entity from form data with user and timestamp information.
     *
     * @param form the form containing the scheduled job data
     * @param username the username of the user performing the operation
     * @param currentTime the current timestamp
     * @return optional entity containing the scheduled job data, or empty if creation fails
     */
    private static OptionalEntity<ScheduledJob> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new ScheduledJob()).map(entity -> {
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return ComponentUtil.getComponent(ScheduledJobService.class).getScheduledJob(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    /**
     * Creates a ScheduledJob entity from the provided form data.
     *
     * @param form the form containing the scheduled job data
     * @return optional entity containing the scheduled job data, or empty if creation fails
     */
    public static OptionalEntity<ScheduledJob> getScheduledJob(final CreateForm form) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        return getEntity(form, username, currentTime).map(entity -> {
            entity.setUpdatedBy(username);
            entity.setUpdatedTime(currentTime);
            copyBeanToBean(form, entity, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
            entity.setJobLogging(isCheckboxEnabled(form.jobLogging) ? Constants.T : Constants.F);
            entity.setCrawler(isCheckboxEnabled(form.crawler) ? Constants.T : Constants.F);
            entity.setAvailable(isCheckboxEnabled(form.available) ? Constants.T : Constants.F);
            return entity;
        });
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
        return asHtml(path_AdminScheduler_AdminSchedulerJsp).renderWith(data -> {
            RenderDataUtil.register(data, "scheduledJobItems", scheduledJobService.getScheduledJobList(schedulerPager)); // page navi
        }).useForm(SearchForm.class, setup -> {
            setup.setup(form -> {
                copyBeanToBean(schedulerPager, form, op -> op.include("id"));
            });
        });
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminScheduler_AdminSchedulerEditJsp);
    }

    private HtmlResponse asDetailsHtml(final String id) {
        return asHtml(path_AdminScheduler_AdminSchedulerDetailsJsp).renderWith(data -> {
            RenderDataUtil.register(data, "systemJobId", fessConfig.isSystemJobId(id));
        });
    }
}
