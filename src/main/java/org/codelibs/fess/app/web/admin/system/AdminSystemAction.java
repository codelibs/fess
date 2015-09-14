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

package org.codelibs.fess.app.web.admin.system;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.exentity.ScheduledJob;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.util.LaRequestUtil;

/**
 * @author Keiichi Watanabe
 */
public class AdminSystemAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private static final String STARTING_CRAWL_PROCESS = "startingCrawlProcess";

    @Resource
    private SystemHelper systemHelper;
    @Resource
    protected DynamicProperties crawlerProperties;
    @Resource
    protected JobHelper jobHelper;
    @Resource
    protected ScheduledJobService scheduledJobService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("system"));
    }

    // ===================================================================================
    //                                                                              Index
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final SystemForm form) {
        return asHtml(path_AdminSystem_IndexJsp).renderWith(data -> {
            // TODO
            // data.register("clusterName", );
            // data.register("clusterStatus", );
            data.register("crawlerRunning", isCrawlerRunning());
            data.register("runningSessionIds", getRunningSessionIds());
        });
    }

    @Token(save = false, validate = true)
    // @Execute(validator = true, input = "index")
    public HtmlResponse start(final SystemForm form) {
        if (!jobHelper.isCrawlProcessRunning()) {
            final List<ScheduledJob> scheduledJobList = scheduledJobService.getCrawloerJobList();
            for (final ScheduledJob scheduledJob : scheduledJobList) {
                scheduledJob.start();
            }
            saveInfo(messages -> messages.addSuccessStartCrawlProcess(GLOBAL));
            LaRequestUtil.getRequest().getSession().setAttribute(STARTING_CRAWL_PROCESS, Boolean.TRUE);
        } else {
            saveInfo(messages -> messages.addErrorsFailedToStartCrawlProcess(GLOBAL));
        }

        return redirect(getClass());
    }

    @Token(save = false, validate = true)
    //@Execute(validator = true, input = "index")
    public HtmlResponse stop(final SystemForm form) {
        if (jobHelper.isCrawlProcessRunning()) {
            if (StringUtil.isNotBlank(form.sessionId)) {
                jobHelper.destroyCrawlerProcess(form.sessionId);
            } else {
                for (final String sessionId : jobHelper.getRunningSessionIdSet()) {
                    jobHelper.destroyCrawlerProcess(sessionId);
                }
            }
            saveInfo(messages -> messages.addSuccessStoppingCrawlProcess(GLOBAL));
        } else {
            saveInfo(messages -> messages.addErrorsNoRunningCrawlProcess(GLOBAL));
        }
        return redirect(getClass());
    }

    public boolean isCrawlerRunning() {
        final HttpSession session = LaRequestUtil.getRequest().getSession(false);
        if (session != null) {
            final Object obj = session.getAttribute(STARTING_CRAWL_PROCESS);
            if (obj != null) {
                session.removeAttribute(STARTING_CRAWL_PROCESS);
                return true;
            }
        }
        return jobHelper.isCrawlProcessRunning();
    }

    public String[] getRunningSessionIds() {
        final Set<String> idSet = jobHelper.getRunningSessionIdSet();
        return idSet.toArray(new String[idSet.size()]);
    }

}
