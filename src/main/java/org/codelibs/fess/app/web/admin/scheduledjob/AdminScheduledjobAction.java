/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.pager.ScheduledJobPager;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.es.exentity.ScheduledJob;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.job.JobExecutor;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
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
    protected SystemHelper systemHelper;

    @Resource
    protected JobHelper jobHelper;

    private boolean running = false;

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
    public HtmlResponse index(final ScheduledjobSearchForm form) {
        return asHtml(path_AdminScheduledjob_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final Integer pageNumber, final ScheduledjobSearchForm form) {
        scheduledJobPager.setCurrentPageNumber(pageNumber);
        return asHtml(path_AdminScheduledjob_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final ScheduledjobSearchForm form) {
        copyBeanToBean(form.searchParams, scheduledJobPager, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminScheduledjob_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final ScheduledjobSearchForm form) {
        scheduledJobPager.clear();
        return asHtml(path_AdminScheduledjob_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse back(final ScheduledjobSearchForm form) {
        return asHtml(path_AdminScheduledjob_IndexJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final ScheduledjobSearchForm form) {
        data.register("scheduledJobItems", scheduledJobService.getScheduledJobList(scheduledJobPager)); // page navi

        // restore from pager
        copyBeanToBean(scheduledJobPager, form.searchParams, op -> op.exclude(CommonConstants.PAGER_CONVERSION_RULE));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse createpage(final ScheduledjobEditForm form) {
        form.initialize();
        form.crudMode = CommonConstants.CREATE_MODE;
        return asHtml(path_AdminScheduledjob_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editpage(final int crudMode, final String id, final ScheduledjobEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.EDIT_MODE);
        loadScheduledJob(form);
        return asHtml(path_AdminScheduledjob_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editagain(final ScheduledjobEditForm form) {
        return asHtml(path_AdminScheduledjob_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse editfromconfirm(final ScheduledjobEditForm form) {
        form.crudMode = CommonConstants.EDIT_MODE;
        loadScheduledJob(form);
        return asHtml(path_AdminScheduledjob_EditJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletepage(final int crudMode, final String id, final ScheduledjobEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        loadScheduledJob(form);
        return asHtml(path_AdminScheduledjob_ConfirmJsp);
    }

    @Token(save = true, validate = false)
    @Execute
    public HtmlResponse deletefromconfirm(final ScheduledjobEditForm form) {
        form.crudMode = CommonConstants.DELETE_MODE;
        loadScheduledJob(form);
        return asHtml(path_AdminScheduledjob_ConfirmJsp);
    }

    // -----------------------------------------------------
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmpage(final int crudMode, final String id, final ScheduledjobEditForm form) {
        form.crudMode = crudMode;
        form.id = id;
        verifyCrudMode(form, CommonConstants.CONFIRM_MODE);
        loadScheduledJob(form);
        return asHtml(path_AdminScheduledjob_ConfirmJsp).renderWith(data -> {
            data.register("running", running);
        });
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromcreate(final ScheduledjobEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminScheduledjob_ConfirmJsp).renderWith(data -> {
            data.register("running", running);
        });
    }

    @Token(save = false, validate = true, keep = true)
    @Execute
    public HtmlResponse confirmfromupdate(final ScheduledjobEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        return asHtml(path_AdminScheduledjob_ConfirmJsp).renderWith(data -> {
            data.register("running", running);
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse create(final ScheduledjobEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        scheduledJobService.store(createScheduledJob(form));
        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    @Execute
    public HtmlResponse update(final ScheduledjobEditForm form) {
        validate(form, messages -> {}, toEditHtml());
        scheduledJobService.store(createScheduledJob(form));
        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final ScheduledjobEditForm form) {
        verifyCrudMode(form, CommonConstants.DELETE_MODE);
        scheduledJobService.delete(getScheduledJob(form));
        saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse start(final ScheduledjobEditForm form) {
        verifyCrudMode(form, CommonConstants.CONFIRM_MODE);
        final ScheduledJob scheduledJob = getScheduledJob(form);
        try {
            scheduledJob.start();
            saveInfo(messages -> messages.addSuccessJobStarted(GLOBAL, scheduledJob.getName()));
        } catch (final Exception e) {
            throwValidationError(messages -> {
                messages.addErrorsFailedToStartJob(GLOBAL, scheduledJob.getName());
            }, toEditHtml());
        }
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse stop(final ScheduledjobEditForm form) {
        verifyCrudMode(form, CommonConstants.CONFIRM_MODE);
        final ScheduledJob scheduledJob = getScheduledJob(form);
        try {
            final JobExecutor jobExecutoer = jobHelper.getJobExecutoer(scheduledJob.getId());
            jobExecutoer.shutdown();
            saveInfo(messages -> messages.addSuccessJobStopped(GLOBAL, scheduledJob.getName()));
        } catch (final Exception e) {
            throwValidationError(messages -> {
                messages.addErrorsFailedToStopJob(GLOBAL, scheduledJob.getName());
            }, toEditHtml());
        }
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected void loadScheduledJob(final ScheduledjobEditForm form) {
        final ScheduledJob scheduledJob = getScheduledJob(form);
        copyBeanToBean(scheduledJob, form, op -> op.exclude("crudMode"));
        form.jobLogging = scheduledJob.isLoggingEnabled() ? Constants.ON : null;
        form.crawler = scheduledJob.isCrawlerJob() ? Constants.ON : null;
        form.available = scheduledJob.isEnabled() ? Constants.ON : null;
        running = scheduledJob.isRunning();
    }

    protected ScheduledJob getScheduledJob(final ScheduledjobEditForm form) {
        final ScheduledJob scheduledJob = scheduledJobService.getScheduledJob(createKeyMap(form));
        if (scheduledJob == null) {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        }
        return scheduledJob;
    }

    protected ScheduledJob createScheduledJob(final ScheduledjobEditForm form) {
        ScheduledJob scheduledJob;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (form.crudMode == CommonConstants.EDIT_MODE) {
            scheduledJob = getScheduledJob(form);
        } else {
            scheduledJob = new ScheduledJob();
            scheduledJob.setCreatedBy(username);
            scheduledJob.setCreatedTime(currentTime);
        }
        scheduledJob.setUpdatedBy(username);
        scheduledJob.setUpdatedTime(currentTime);
        copyBeanToBean(form, scheduledJob, op -> op.exclude(CommonConstants.COMMON_CONVERSION_RULE));
        scheduledJob.setJobLogging(Constants.ON.equals(form.jobLogging) ? Constants.T : Constants.F);
        scheduledJob.setCrawler(Constants.ON.equals(form.crawler) ? Constants.T : Constants.F);
        scheduledJob.setAvailable(Constants.ON.equals(form.available) ? Constants.T : Constants.F);
        return scheduledJob;
    }

    protected Map<String, String> createKeyMap(final ScheduledjobEditForm form) {
        final Map<String, String> keys = new HashMap<String, String>();
        keys.put("id", form.id);
        return keys;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final ScheduledjobEditForm form, final int expectedMode) {
        if (form.crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(form.crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminScheduledjob_EditJsp);
        };
    }
}
