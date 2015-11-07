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
package org.codelibs.fess.app.web.admin.scheduledjob;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.ScheduledJobPager;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.config.exentity.ScheduledJob;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.job.JobExecutor;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.next.HtmlNext;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
 * @author Keiichi Watanabe
 */
public class AdminScheduledjobAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private ScheduledJobService scheduledJobService;
    @Resource
    private ScheduledJobPager scheduledJobPager;
    @Resource
    private SystemHelper systemHelper;
    @Resource
    protected JobHelper jobHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("scheduledJob"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final SearchForm form) {
        return asHtml(path_AdminScheduledjob_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            scheduledJobPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            scheduledJobPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminScheduledjob_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, scheduledJobPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminScheduledjob_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SearchForm form) {
        scheduledJobPager.clear();
        return asHtml(path_AdminScheduledjob_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        data.register("scheduledJobItems", scheduledJobService.getScheduledJobList(scheduledJobPager)); // page navi

        // restore from pager
        copyBeanToBean(scheduledJobPager, form, op -> op.include("id"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Execute
    //(token = TxToken.SAVE)
    public HtmlResponse createnew() {
        return asHtml(path_AdminScheduledjob_EditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        });
    }

    @Execute
    //(token = TxToken.SAVE)
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, toEditHtml());
        HtmlNext next;
        switch (form.crudMode) {
        case CrudMode.EDIT: // back
            form.crudMode = CrudMode.DETAILS;
            next = path_AdminScheduledjob_DetailsJsp;
            break;
        default:
            form.crudMode = CrudMode.EDIT;
            next = path_AdminScheduledjob_EditJsp;
            break;
        }
        final String id = form.id;
        scheduledJobService.getScheduledJob(id).ifPresent(entity -> {
            loadScheduledJob(form, entity);
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), toEditHtml());
        });
        return asHtml(next);
    }

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    @Execute
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        return asHtml(path_AdminScheduledjob_DetailsJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                scheduledJobService.getScheduledJob(id).ifPresent(entity -> {
                    loadScheduledJob(form, entity);
                    form.crudMode = crudMode;
                    LaRequestUtil.getOptionalRequest().ifPresent(request -> {
                        request.setAttribute("running", entity.isRunning());
                    });
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), toEditHtml());
                });
            });
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, toEditHtml());
        createScheduledJob(form).ifPresent(entity -> {
            scheduledJobService.store(entity);
            saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL), toEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, toEditHtml());
        createScheduledJob(form).ifPresent(entity -> {
            scheduledJobService.store(entity);
            saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, toEditHtml());
        final String id = form.id;
        scheduledJobService.getScheduledJob(id).ifPresent(entity -> {
            scheduledJobService.delete(entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), toEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse start(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, toEditHtml());
        final String id = form.id;
        scheduledJobService.getScheduledJob(id).ifPresent(entity -> {
            try {
                entity.start();
                saveInfo(messages -> messages.addSuccessJobStarted(GLOBAL, entity.getName()));
            } catch (final Exception e) {
                throwValidationError(messages -> {
                    messages.addErrorsFailedToStartJob(GLOBAL, entity.getName());
                }, toEditHtml());
            }
        }).orElse(() -> {
            throwValidationError(messages -> {
                messages.addErrorsFailedToStartJob(GLOBAL, id);
            }, toEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse stop(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, toEditHtml());
        final String id = form.id;
        scheduledJobService.getScheduledJob(id).ifPresent(entity -> {
            try {
                final JobExecutor jobExecutoer = jobHelper.getJobExecutoer(entity.getId());
                jobExecutoer.shutdown();
                saveInfo(messages -> messages.addSuccessJobStopped(GLOBAL, entity.getName()));
            } catch (final Exception e) {
                throwValidationError(messages -> {
                    messages.addErrorsFailedToStopJob(GLOBAL, entity.getName());
                }, toEditHtml());
            }
        }).orElse(() -> {
            throwValidationError(messages -> {
                messages.addErrorsFailedToStartJob(GLOBAL, id);
            }, toEditHtml());
        });
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadScheduledJob(final EditForm form, ScheduledJob entity) {
        copyBeanToBean(entity, form, op -> op.exclude("crudMode").excludeNull());
        form.jobLogging = entity.isLoggingEnabled() ? Constants.ON : null;
        form.crawler = entity.isCrawlerJob() ? Constants.ON : null;
        form.available = entity.isEnabled() ? Constants.ON : null;
    }

    protected OptionalEntity<ScheduledJob> createScheduledJob(final CreateForm form) {
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        switch (form.crudMode) {
        case CrudMode.CREATE:
            if (form instanceof CreateForm) {
                final ScheduledJob entity = new ScheduledJob();
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                entity.setUpdatedBy(username);
                entity.setUpdatedTime(currentTime);
                copyBeanToBean(form, entity, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
                entity.setJobLogging(Constants.ON.equals(form.jobLogging) ? Constants.T : Constants.F);
                entity.setCrawler(Constants.ON.equals(form.crawler) ? Constants.T : Constants.F);
                entity.setAvailable(Constants.ON.equals(form.available) ? Constants.T : Constants.F);
                return OptionalEntity.of(entity);
            }
            break;
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return scheduledJobService.getScheduledJob(((EditForm) form).id).map(entity -> {
                    entity.setUpdatedBy(username);
                    entity.setUpdatedTime(currentTime);
                    copyBeanToBean(form, entity, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
                    entity.setJobLogging(Constants.ON.equals(form.jobLogging) ? Constants.T : Constants.F);
                    entity.setCrawler(Constants.ON.equals(form.crawler) ? Constants.T : Constants.F);
                    entity.setAvailable(Constants.ON.equals(form.available) ? Constants.T : Constants.F);
                    return entity;
                });
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

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminScheduledjob_EditJsp);
        };
    }
}
