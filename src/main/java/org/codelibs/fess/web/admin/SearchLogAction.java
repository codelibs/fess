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
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.db.exentity.SearchLog;
import org.codelibs.fess.pager.SearchLogPager;
import org.codelibs.fess.service.SearchLogService;
import org.codelibs.fess.web.base.FessAdminAction;
import org.codelibs.sastruts.core.annotation.Token;
import org.seasar.framework.beans.util.Beans;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ResponseUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.exception.ActionMessagesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.List;

public class SearchLogAction extends FessAdminAction {
    private static final Logger logger = LoggerFactory.getLogger(SearchLogAction.class);

    // for list

    public List<SearchLog> searchLogItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected SearchLogForm searchLogForm;

    @Resource
    protected SearchLogService searchLogService;

    @Resource
    protected SearchLogPager searchLogPager;

    @Resource
    protected SystemHelper systemHelper;

    protected String displayList(final boolean redirect) {
        // page navi
        searchLogItems = searchLogService.getSearchLogList(searchLogPager);

        // restore from pager
        Beans.copy(searchLogPager, searchLogForm.searchParams).excludes(CommonConstants.PAGER_CONVERSION_RULE)

        .execute();

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
        if (StringUtil.isNotBlank(searchLogForm.pageNumber)) {
            try {
                searchLogPager.setCurrentPageNumber(Integer.parseInt(searchLogForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid value: " + searchLogForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        searchLogPager.clear();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String back() {
        return displayList(false);
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editagain() {
        return "edit.jsp";
    }

    @Execute(validator = false, input = "error.jsp", urlPattern = "confirmpage/{crudMode}/{id}")
    public String confirmpage() {
        if (searchLogForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    searchLogForm.crudMode });
        }

        loadSearchLog();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        searchLogForm.initialize();
        searchLogForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (searchLogForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.EDIT_MODE, searchLogForm.crudMode });
        }

        loadSearchLog();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        searchLogForm.crudMode = CommonConstants.EDIT_MODE;

        loadSearchLog();

        return "edit.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    @Execute(validator = true, input = "edit.jsp")
    public String confirmfromcreate() {
        return "confirm.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    @Execute(validator = true, input = "edit.jsp")
    public String confirmfromupdate() {
        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "deletepage/{crudMode}/{id}")
    public String deletepage() {
        if (searchLogForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    searchLogForm.crudMode });
        }

        loadSearchLog();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        searchLogForm.crudMode = CommonConstants.DELETE_MODE;

        loadSearchLog();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final SearchLog searchLog = createSearchLog();
            searchLogService.store(searchLog);
            SAStrutsUtil.addSessionMessage("success.crud_create_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException("errors.crud_failed_to_create_crud_table");
        }
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String update() {
        try {
            final SearchLog searchLog = createSearchLog();
            searchLogService.store(searchLog);
            SAStrutsUtil.addSessionMessage("success.crud_update_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException("errors.crud_failed_to_update_crud_table");
        }
    }

    @Token(save = false, validate = true)
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (searchLogForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    searchLogForm.crudMode });
        }

        try {
            final SearchLog searchLog = searchLogService.getSearchLog(createKeyMap());
            if (searchLog == null) {
                // throw an exception
                throw new ActionMessagesException("errors.crud_could_not_find_crud_table",

                new Object[] { searchLogForm.id });

            }

            searchLogService.delete(searchLog);
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new ActionMessagesException("errors.crud_failed_to_delete_crud_table");
        }
    }

    protected void loadSearchLog() {

        final SearchLog searchLog = searchLogService.getSearchLog(createKeyMap());
        if (searchLog == null) {
            // throw an exception
            throw new ActionMessagesException("errors.crud_could_not_find_crud_table",

            new Object[] { searchLogForm.id });

        }

        Beans.copy(searchLog, searchLogForm).excludes("searchParams", "mode")

        .execute();
    }

    protected SearchLog createSearchLog() {
        SearchLog searchLog;
        if (searchLogForm.crudMode == CommonConstants.EDIT_MODE) {
            searchLog = searchLogService.getSearchLog(createKeyMap());
            if (searchLog == null) {
                // throw an exception
                throw new ActionMessagesException("errors.crud_could_not_find_crud_table",

                new Object[] { searchLogForm.id });

            }
        } else {
            searchLog = new SearchLog();
        }
        Beans.copy(searchLogForm, searchLog).excludes("searchParams", "mode")

        .execute();

        return searchLog;
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", searchLogForm.id);

        return keys;
    }

    public String getHelpLink() {
        return systemHelper.getHelpLink("searchLog");
    }

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
            logger.error("Could not create FessSearchLog.csv.", e);
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
