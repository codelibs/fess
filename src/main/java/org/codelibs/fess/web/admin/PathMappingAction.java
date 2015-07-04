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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.beans.FessBeans;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.es.exentity.PathMapping;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.pager.PathMappingPager;
import org.codelibs.fess.service.PathMappingService;
import org.codelibs.fess.web.base.FessAdminAction;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathMappingAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(PathMappingAction.class);

    // for list

    public List<PathMapping> pathMappingItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected PathMappingForm pathMappingForm;

    @Resource
    protected PathMappingService pathMappingService;

    @Resource
    protected PathMappingPager pathMappingPager;

    @Resource
    protected PathMappingHelper pathMappingHelper;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("pathMapping");
    }

    protected String displayList(final boolean redirect) {
        if (redirect) {
            pathMappingHelper.init();
        }

        // page navi
        pathMappingItems = pathMappingService.getPathMappingList(pathMappingPager);

        // restore from pager
        Beans.copy(pathMappingPager, pathMappingForm.searchParams).excludes(CommonConstants.PAGER_CONVERSION_RULE)

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
        if (StringUtil.isNotBlank(pathMappingForm.pageNumber)) {
            try {
                pathMappingPager.setCurrentPageNumber(Integer.parseInt(pathMappingForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid value: " + pathMappingForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(pathMappingForm.searchParams, pathMappingPager).excludes(CommonConstants.PAGER_CONVERSION_RULE)

        .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        pathMappingPager.clear();

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
        if (pathMappingForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    pathMappingForm.crudMode });
        }

        loadPathMapping();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        pathMappingForm.initialize();
        pathMappingForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (pathMappingForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.EDIT_MODE,
                    pathMappingForm.crudMode });
        }

        loadPathMapping();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        pathMappingForm.crudMode = CommonConstants.EDIT_MODE;

        loadPathMapping();

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
        if (pathMappingForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    pathMappingForm.crudMode });
        }

        loadPathMapping();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        pathMappingForm.crudMode = CommonConstants.DELETE_MODE;

        loadPathMapping();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final PathMapping pathMapping = createPathMapping();
            pathMappingService.store(pathMapping);
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
            final PathMapping pathMapping = createPathMapping();
            pathMappingService.store(pathMapping);
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

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", pathMappingForm.id);

        return keys;
    }

    protected void loadPathMapping() {

        final PathMapping pathMapping = pathMappingService.getPathMapping(createKeyMap());
        if (pathMapping == null) {
            // throw an exception
            throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { pathMappingForm.id });
        }

        FessBeans.copy(pathMapping, pathMappingForm).commonColumnDateConverter().excludes("searchParams", "mode").execute();
    }

    protected PathMapping createPathMapping() {
        PathMapping pathMapping;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (pathMappingForm.crudMode == CommonConstants.EDIT_MODE) {
            pathMapping = pathMappingService.getPathMapping(createKeyMap());
            if (pathMapping == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { pathMappingForm.id });
            }
        } else {
            pathMapping = new PathMapping();
            pathMapping.setCreatedBy(username);
            pathMapping.setCreatedTime(currentTime);
        }
        pathMapping.setUpdatedBy(username);
        pathMapping.setUpdatedTime(currentTime);
        FessBeans.copy(pathMappingForm, pathMapping).excludesCommonColumns().execute();

        return pathMapping;
    }

    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (pathMappingForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    pathMappingForm.crudMode });
        }

        try {
            final PathMapping pathMapping = pathMappingService.getPathMapping(createKeyMap());
            if (pathMapping == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { pathMappingForm.id });
            }

            pathMappingService.delete(pathMapping);
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            logger.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, "errors.crud_failed_to_delete_crud_table");
        }
    }

}
