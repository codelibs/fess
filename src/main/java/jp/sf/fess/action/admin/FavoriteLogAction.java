/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import jp.sf.fess.Constants;
import jp.sf.fess.crud.action.admin.BsFavoriteLogAction;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.helper.SystemHelper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ResponseUtil;

import com.ibm.icu.text.SimpleDateFormat;

public class FavoriteLogAction extends BsFavoriteLogAction {
    private static final Log log = LogFactory.getLog(FavoriteLogAction.class);

    private static final long serialVersionUID = 1L;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("favoriteLog");
    }

    @Execute(validator = false, input = "error.jsp")
    public String deleteall() {
        favoriteLogService.deleteAll(favoriteLogPager);
        SAStrutsUtil.addSessionMessage("success.search_log_delete_all");
        return displayList(true);
    }

    @Execute(validator = false, input = "error.jsp")
    public String download() {
        BufferedWriter writer = null;
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final String fileName = "FessFavoriteLog_" + sdf.format(new Date())
                + ".csv";
        final HttpServletResponse response = ResponseUtil.getResponse();
        response.setContentType("text/csv");
        response.setHeader("Content-disposition", "attachment; filename=\""
                + fileName + "\"");
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    response.getOutputStream(), getCsvEncoding()));
            favoriteLogService.dump(writer, favoriteLogPager);
            writer.flush();
        } catch (final Exception e) {
            log.error("Could not create FessSearchLog.csv.", e);
            throw new SSCActionMessagesException(e,
                    "errors.could_not_create_search_log_csv");
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return null;
    }

    private String getCsvEncoding() {
        if (StringUtil.isNotBlank(favoriteLogForm.csvEncoding)) {
            return favoriteLogForm.csvEncoding;
        }
        final Locale locale = RequestUtil.getRequest().getLocale();
        if ("ja".equals(locale.getLanguage())) {
            return Constants.MS932;
        }
        return Constants.UTF_8;
    }

}
