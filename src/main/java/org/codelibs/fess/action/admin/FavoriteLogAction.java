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
import java.io.Serializable;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.db.exentity.FavoriteLog;
import org.codelibs.fess.form.admin.FavoriteLogForm;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.pager.FavoriteLogPager;
import org.codelibs.fess.service.FavoriteLogService;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.SimpleDateFormat;

public class FavoriteLogAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteLogAction.class);
    
    // for list

    public List<FavoriteLog> favoriteLogItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected FavoriteLogForm favoriteLogForm;

    @Resource
    protected FavoriteLogService favoriteLogService;

    @Resource
    protected FavoriteLogPager favoriteLogPager;
    
    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("favoriteLog");
    }
    
    protected String displayList(final boolean redirect) {
        // page navi
        favoriteLogItems = favoriteLogService.getFavoriteLogList(favoriteLogPager);

        // restore from pager
        Beans.copy(favoriteLogPager, favoriteLogForm.searchParams).excludes(CommonConstants.PAGER_CONVERSION_RULE)

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
        if (StringUtil.isNotBlank(favoriteLogForm.pageNumber)) {
            try {
                favoriteLogPager.setCurrentPageNumber(Integer.parseInt(favoriteLogForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Invalid value: " + favoriteLogForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(favoriteLogForm.searchParams, favoriteLogPager).excludes(CommonConstants.PAGER_CONVERSION_RULE)

        .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        favoriteLogPager.clear();

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
        if (favoriteLogForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    favoriteLogForm.crudMode });
        }

        loadFavoriteLog();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        favoriteLogForm.initialize();
        favoriteLogForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (favoriteLogForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.EDIT_MODE,
                    favoriteLogForm.crudMode });
        }

        loadFavoriteLog();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        favoriteLogForm.crudMode = CommonConstants.EDIT_MODE;

        loadFavoriteLog();

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
        if (favoriteLogForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    favoriteLogForm.crudMode });
        }

        loadFavoriteLog();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        favoriteLogForm.crudMode = CommonConstants.DELETE_MODE;

        loadFavoriteLog();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final FavoriteLog favoriteLog = createFavoriteLog();
            favoriteLogService.store(favoriteLog);
            SAStrutsUtil.addSessionMessage("success.crud_create_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException("errors.crud_failed_to_create_crud_table");
        }
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String update() {
        try {
            final FavoriteLog favoriteLog = createFavoriteLog();
            favoriteLogService.store(favoriteLog);
            SAStrutsUtil.addSessionMessage("success.crud_update_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException("errors.crud_failed_to_update_crud_table");
        }
    }

    @Token(save = false, validate = true)
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (favoriteLogForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    favoriteLogForm.crudMode });
        }

        try {
            final FavoriteLog favoriteLog = favoriteLogService.getFavoriteLog(createKeyMap());
            if (favoriteLog == null) {
                // throw an exception
                throw new ActionMessagesException("errors.crud_could_not_find_crud_table",

                new Object[] { favoriteLogForm.id });

            }

            favoriteLogService.delete(favoriteLog);
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException(e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new ActionMessagesException("errors.crud_failed_to_delete_crud_table");
        }
    }

    protected void loadFavoriteLog() {

        final FavoriteLog favoriteLog = favoriteLogService.getFavoriteLog(createKeyMap());
        if (favoriteLog == null) {
            // throw an exception
            throw new ActionMessagesException("errors.crud_could_not_find_crud_table",

            new Object[] { favoriteLogForm.id });

        }

        Beans.copy(favoriteLog, favoriteLogForm).excludes("searchParams", "mode")

        .execute();
    }

    protected FavoriteLog createFavoriteLog() {
        FavoriteLog favoriteLog;
        if (favoriteLogForm.crudMode == CommonConstants.EDIT_MODE) {
            favoriteLog = favoriteLogService.getFavoriteLog(createKeyMap());
            if (favoriteLog == null) {
                // throw an exception
                throw new ActionMessagesException("errors.crud_could_not_find_crud_table",

                new Object[] { favoriteLogForm.id });

            }
        } else {
            favoriteLog = new FavoriteLog();
        }
        Beans.copy(favoriteLogForm, favoriteLog).excludes("searchParams", "mode")

        .execute();

        return favoriteLog;
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", favoriteLogForm.id);

        return keys;
    }
    

    @Execute(validator = false, input = "error.jsp")
    public String deleteall() {
        favoriteLogService.deleteAll(favoriteLogPager);
        SAStrutsUtil.addSessionMessage("success.favorite_log_delete_all");
        return displayList(true);
    }

    @Execute(validator = false, input = "error.jsp")
    public String download() {
        BufferedWriter writer = null;
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final String fileName = "FessFavoriteLog_" + sdf.format(new Date()) + ".csv";
        final HttpServletResponse response = ResponseUtil.getResponse();
        response.setContentType("text/csv");
        response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
        try {
            writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), getCsvEncoding()));
            favoriteLogService.dump(writer, favoriteLogPager);
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
