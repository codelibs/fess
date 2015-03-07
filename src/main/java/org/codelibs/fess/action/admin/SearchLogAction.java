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

package org.codelibs.fess.action.admin;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.action.admin.BsSearchLogAction;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.framework.beans.util.Beans;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ResponseUtil;

import com.ibm.icu.text.SimpleDateFormat;

public class SearchLogAction extends BsSearchLogAction {
    private static final Log log = LogFactory.getLog(SearchLogAction.class);

    private static final long serialVersionUID = 1L;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("searchLog");
    }

    @Override
    @Execute(validator = false, input = "error.jsp")
    public String search() {
        final Map<String, String> searchParams = searchLogForm.searchParams;
        if (!searchParams.containsKey("startPage")) {
            searchParams.put("startPage", StringUtil.EMPTY);
        }
        Beans.copy(searchParams, searchLogPager).excludes(CommonConstants.PAGER_CONVERSION_RULE).execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String deleteall() {
        searchLogService.deleteAll(searchLogPager);
        SAStrutsUtil.addSessionMessage("success.search_log_delete_all");
        return displayList(true);
    }

    @Execute(validator = false, input = "error.jsp")
    public String download() {
        BufferedWriter writer = null;
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final String fileName = "FessSearchLog_" + sdf.format(new Date()) + ".csv";
        final HttpServletResponse response = ResponseUtil.getResponse();
        response.setContentType("text/csv");
        response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
        try {
            writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), getCsvEncoding()));
            searchLogService.dump(writer, searchLogPager);
            writer.flush();
        } catch (final Exception e) {
            log.error("Could not create FessSearchLog.csv.", e);
            throw new SSCActionMessagesException(e, "errors.could_not_create_search_log_csv");
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return null;
    }

    private String getCsvEncoding() {
        if (StringUtil.isNotBlank(searchLogForm.csvEncoding)) {
            return searchLogForm.csvEncoding;
        }
        final Locale locale = RequestUtil.getRequest().getLocale();
        if ("ja".equals(locale.getLanguage())) {
            return Constants.MS932;
        }
        return Constants.UTF_8;
    }

    public boolean isHasClickLog() {
        return searchLogForm.clickLogList != null && !searchLogForm.clickLogList.isEmpty();
    }
}
