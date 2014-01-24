/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.action.admin;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import jp.sf.fess.crud.action.admin.BsCrawlingSessionAction;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.db.exentity.CrawlingSessionInfo;
import jp.sf.fess.helper.JobHelper;
import jp.sf.fess.helper.SystemHelper;

import org.seasar.struts.annotation.Execute;

public class CrawlingSessionAction extends BsCrawlingSessionAction {

    private static final long serialVersionUID = 1L;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected JobHelper jobHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("crawlingSession");
    }

    public List<CrawlingSessionInfo> getCrawlingSessionInfoItems() {
        if (crawlingSessionForm.id != null) {
            return crawlingSessionService.getCrawlingSessionInfoList(Long
                    .parseLong(crawlingSessionForm.id));
        }
        return Collections.emptyList();
    }

    @Execute(validator = false, input = "error.jsp")
    public String deleteall() {
        crawlingSessionService.deleteOldSessions(jobHelper
                .getRunningSessionIdSet());
        SAStrutsUtil.addSessionMessage("success.crawling_session_delete_all");
        return displayList(true);
    }
}
