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
package org.codelibs.fess.app.web.admin.scheduler;

import java.text.MessageFormat;
import java.util.Base64;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.pager.SchedulerPager;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.config.exentity.ScheduledJob;
import org.codelibs.fess.helper.ProcessHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.util.LaRequestUtil;

/**
 * @author shinsuke
 * @author Keiichi Watanabe
 */
public class AdminSchedulerAction extends FessAdminAction {

    public static final String ROLE = "admin-scheduler";

    private static final Logger logger = LogManager.getLogger(AdminSchedulerAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private ScheduledJobService scheduledJobService;
    @Resource
    private SchedulerPager schedulerPager;
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
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final SearchForm form) {
        return asListHtml();
    }

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

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, schedulerPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminScheduler_AdminSchedulerJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse reset(final SearchForm form) {
        schedulerPager.clear();
        return asHtml(path_AdminScheduler_AdminSchedulerJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

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
    protected void loadScheduledJob(final EditForm form, final ScheduledJob entity) {
        copyBeanToBean(entity, form, op -> op.exclude("crudMode").excludeNull());
        form.jobLogging = entity.isLoggingEnabled() ? Constants.ON : null;
        form.crawler = entity.isCrawlerJob() ? Constants.ON : null;
        form.available = entity.isEnabled() ? Constants.ON : null;
    }

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
