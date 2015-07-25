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

package org.codelibs.fess.app.web.admin;

import java.beans.Beans;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.es.exentity.CrawlingSession;
import org.codelibs.fess.es.exentity.CrawlingSessionInfo;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.pager.CrawlingSessionPager;
import org.codelibs.fess.service.CrawlingSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlingSessionAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(CrawlingSessionAction.class);

    //for list
    public List<CrawlingSession> crawlingSessionItems;

    // for edit/confirm/delete
    //@ActionForm
    @Resource
    protected CrawlingSessionForm crawlingSessionForm;

    @Resource
    protected CrawlingSessionService crawlingSessionService;

    @Resource
    protected CrawlingSessionPager crawlingSessionPager;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected JobHelper jobHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("crawlingSession");
    }

    protected String displayList(final boolean redirect) {
        // page navi
        crawlingSessionItems = crawlingSessionService.getCrawlingSessionList(crawlingSessionPager);

        // restore from pager
        BeanUtil.copyBeanToBean(crawlingSessionPager, crawlingSessionForm.searchParams,
                option -> option.exclude(CommonConstants.PAGER_CONVERSION_RULE));

        if (redirect) {
            return "index?redirect=true";
        } else {
            return "index.jsp";
        }
    }

    public List<CrawlingSessionInfo> getCrawlingSessionInfoItems() {
        if (crawlingSessionForm.id != null) {
            return crawlingSessionService.getCrawlingSessionInfoList(crawlingSessionForm.id);
        }
        return Collections.emptyList();
    }

    //@Execute(validator = false, input = "error.jsp")
    public String deleteall() {
        crawlingSessionService.deleteOldSessions(jobHelper.getRunningSessionIdSet());
        SAStrutsUtil.addSessionMessage("success.crawling_session_delete_all");
        return displayList(true);
    }

    //@Execute(validator = false, input = "error.jsp")
    public String index() {
        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp", urlPattern = "list/{pageNumber}")
    public String list() {
        // page navi
        if (StringUtil.isNotBlank(crawlingSessionForm.pageNumber)) {
            try {
                crawlingSessionPager.setCurrentPageNumber(Integer.parseInt(crawlingSessionForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid value: " + crawlingSessionForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp")
    public String search() {
        BeanUtil.copyBeanToBean(crawlingSessionForm.searchParams, crawlingSessionPager,
                option -> option.exclude(CommonConstants.PAGER_CONVERSION_RULE));

        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp")
    public String reset() {
        crawlingSessionPager.clear();

        return displayList(false);
    }

    //@Execute(validator = false, input = "error.jsp")
    public String back() {
        return displayList(false);
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String editagain() {
        return "edit.jsp";
    }

    //@Execute(validator = false, input = "error.jsp", urlPattern = "confirmpage/{crudMode}/{id}")
    public String confirmpage() {
        if (crawlingSessionForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    crawlingSessionForm.crudMode });
        }

        loadCrawlingSession();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        crawlingSessionForm.initialize();
        crawlingSessionForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (crawlingSessionForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.EDIT_MODE,
                    crawlingSessionForm.crudMode });
        }

        loadCrawlingSession();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        crawlingSessionForm.crudMode = CommonConstants.EDIT_MODE;

        loadCrawlingSession();

        return "edit.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    //@Execute(validator = true, input = "edit.jsp")
    public String confirmfromcreate() {
        return "confirm.jsp";
    }

    @Token(save = false, validate = true, keep = true)
    //@Execute(validator = true, input = "edit.jsp")
    public String confirmfromupdate() {
        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp", urlPattern = "deletepage/{crudMode}/{id}")
    public String deletepage() {
        if (crawlingSessionForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    crawlingSessionForm.crudMode });
        }

        loadCrawlingSession();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    //@Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        crawlingSessionForm.crudMode = CommonConstants.DELETE_MODE;

        loadCrawlingSession();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    //@Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final CrawlingSession crawlingSession = createCrawlingSession();
            crawlingSessionService.store(crawlingSession);
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
    //@Execute(validator = true, input = "edit.jsp")
    public String update() {
        try {
            final CrawlingSession crawlingSession = createCrawlingSession();
            crawlingSessionService.store(crawlingSession);
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
    //@Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (crawlingSessionForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    crawlingSessionForm.crudMode });
        }

        try {
            final CrawlingSession crawlingSession = crawlingSessionService.getCrawlingSession(createKeyMap());
            if (crawlingSession == null) {
                // throw an exception
                throw new ActionMessagesException("errors.crud_could_not_find_crud_table",

                new Object[] { crawlingSessionForm.id });

            }

            crawlingSessionService.delete(crawlingSession);
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

    protected void loadCrawlingSession() {

        final CrawlingSession crawlingSession = crawlingSessionService.getCrawlingSession(createKeyMap());
        if (crawlingSession == null) {
            // throw an exception
            throw new ActionMessagesException("errors.crud_could_not_find_crud_table",

            new Object[] { crawlingSessionForm.id });

        }

        BeanUtil.copyBeanToBean(crawlingSession, crawlingSessionForm, option -> option.exclude("searchParams", "mode"));
    }

    protected CrawlingSession createCrawlingSession() {
        CrawlingSession crawlingSession;
        if (crawlingSessionForm.crudMode == CommonConstants.EDIT_MODE) {
            crawlingSession = crawlingSessionService.getCrawlingSession(createKeyMap());
            if (crawlingSession == null) {
                // throw an exception
                throw new ActionMessagesException("errors.crud_could_not_find_crud_table",

                new Object[] { crawlingSessionForm.id });

            }
        } else {
            crawlingSession = new CrawlingSession();
        }
        BeanUtil.copyBeanToBean(crawlingSessionForm, crawlingSession, option -> option.exclude("searchParams", "mode"));

        return crawlingSession;
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", crawlingSessionForm.id);

        return keys;
    }

}
