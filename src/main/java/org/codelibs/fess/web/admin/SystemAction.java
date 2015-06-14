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

package org.codelibs.fess.web.admin;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.client.SearchClient;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.db.exentity.ScheduledJob;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.WebManagementHelper;
import org.codelibs.fess.service.ScheduledJobService;
import org.codelibs.sastruts.core.annotation.Token;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.RequestUtil;

public class SystemAction implements Serializable {
    private static final String STARTING_CRAWL_PROCESS = "startingCrawlProcess";

    private static final long serialVersionUID = 1L;

    @ActionForm
    @Resource
    protected SystemForm systemForm;

    @Resource
    protected SearchClient searchClient;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected JobHelper jobHelper;

    @Resource
    protected ScheduledJobService scheduledJobService;

    public String clusterName;

    public String clusterStatus;

    public String getHelpLink() {
        return systemHelper.getHelpLink("system");
    }

    protected String showIndex(final boolean redirect) {

        if (redirect) {
            return "index?redirect=true";
        } else {
            return "index.jsp";
        }
    }

    @Token(save = true, validate = false)
    @Execute(validator = false)
    public String index() {
        return showIndex(false);
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index")
    public String start() {
        if (!jobHelper.isCrawlProcessRunning()) {
            final List<ScheduledJob> scheduledJobList = scheduledJobService.getCrawloerJobList();
            for (final ScheduledJob scheduledJob : scheduledJobList) {
                scheduledJob.start();
            }
            SAStrutsUtil.addSessionMessage("success.start_crawl_process");
            RequestUtil.getRequest().getSession().setAttribute(STARTING_CRAWL_PROCESS, Boolean.TRUE);
        } else {
            SAStrutsUtil.addSessionMessage("success.failed_to_start_crawl_process");
        }
        return showIndex(true);
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index")
    public String stop() {
        if (jobHelper.isCrawlProcessRunning()) {
            if (StringUtil.isNotBlank(systemForm.sessionId)) {
                jobHelper.destroyCrawlerProcess(systemForm.sessionId);
            } else {
                for (final String sessionId : jobHelper.getRunningSessionIdSet()) {
                    jobHelper.destroyCrawlerProcess(sessionId);
                }
            }
            SAStrutsUtil.addSessionMessage("success.stopping_crawl_process");
        } else {
            SAStrutsUtil.addSessionMessage("errors.no_running_crawl_process");
        }
        return showIndex(true);
    }

    public boolean isCrawlerRunning() {
        final HttpSession session = RequestUtil.getRequest().getSession(false);
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