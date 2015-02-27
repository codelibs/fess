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

package jp.sf.fess.action.admin;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.form.admin.StatsForm;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.pager.StatsPager;
import jp.sf.fess.service.SearchFieldLogService;
import jp.sf.fess.service.StatsService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.util.StringUtil;
import org.seasar.framework.beans.util.Beans;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;

public class StatsAction implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(StatsAction.class);

    public List<Map<String, Object>> statsItems;

    @ActionForm
    @Resource
    protected StatsForm statsForm;

    @Resource
    protected StatsService statsService;

    @Resource
    protected SearchFieldLogService searchFieldLogService;

    @Resource
    protected StatsPager statsPager;

    @Resource
    protected SystemHelper systemHelper;

    public String[] reportTypeItems;

    public String getHelpLink() {
        return systemHelper.getHelpLink("stats");
    }

    protected String displayList(final boolean redirect) {
        // page navi
        statsItems = statsService.getStatsList(statsPager);
        reportTypeItems = searchFieldLogService.getGroupedFieldNames();

        // restore from pager
        Beans.copy(statsPager, statsForm.searchParams)
                .excludes(CommonConstants.PAGER_CONVERSION_RULE).execute();

        if (redirect) {
            return "index?redirect=true";
        } else {
            return "index.jsp";
        }
    }

    @Execute(validator = false, input = "error.jsp")
    public String index() {
        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp", urlPattern = "list/{pageNumber}")
    public String list() {
        // page navi
        if (StringUtil.isNotBlank(statsForm.pageNumber)) {
            try {
                statsPager.setCurrentPageNumber(Integer
                        .parseInt(statsForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Invalid value: " + statsForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(statsForm.searchParams, statsPager)
                .excludes(CommonConstants.PAGER_CONVERSION_RULE).execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        statsPager.clear();

        return displayList(false);
    }

}
