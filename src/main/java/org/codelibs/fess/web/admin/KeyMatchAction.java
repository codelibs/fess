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
import org.codelibs.fess.es.exentity.KeyMatch;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.pager.KeyMatchPager;
import org.codelibs.fess.service.KeyMatchService;
import org.codelibs.fess.util.ComponentUtil;
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

public class KeyMatchAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(KeyMatchAction.class);

    // for list

    public List<KeyMatch> keyMatchItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected KeyMatchForm keyMatchForm;

    @Resource
    protected KeyMatchService keyMatchService;

    @Resource
    protected KeyMatchPager keyMatchPager;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("keyMatch");
    }

    protected String displayList(final boolean redirect) {
        // page navi
        keyMatchItems = keyMatchService.getKeyMatchList(keyMatchPager);

        // restore from pager
        Beans.copy(keyMatchPager, keyMatchForm.searchParams).excludes(CommonConstants.PAGER_CONVERSION_RULE)

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
        if (StringUtil.isNotBlank(keyMatchForm.pageNumber)) {
            try {
                keyMatchPager.setCurrentPageNumber(Integer.parseInt(keyMatchForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid value: " + keyMatchForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(keyMatchForm.searchParams, keyMatchPager).excludes(CommonConstants.PAGER_CONVERSION_RULE)

        .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        keyMatchPager.clear();

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
        if (keyMatchForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.CONFIRM_MODE,
                    keyMatchForm.crudMode });
        }

        loadKeyMatch();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        keyMatchForm.initialize();
        keyMatchForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (keyMatchForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.EDIT_MODE, keyMatchForm.crudMode });
        }

        loadKeyMatch();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        keyMatchForm.crudMode = CommonConstants.EDIT_MODE;

        loadKeyMatch();

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
        if (keyMatchForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE, keyMatchForm.crudMode });
        }

        loadKeyMatch();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        keyMatchForm.crudMode = CommonConstants.DELETE_MODE;

        loadKeyMatch();

        return "confirm.jsp";
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", keyMatchForm.id);

        return keys;
    }

    protected void loadKeyMatch() {

        final KeyMatch keyMatch = keyMatchService.getKeyMatch(createKeyMap());
        if (keyMatch == null) {
            // throw an exception
            throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { keyMatchForm.id });
        }

        FessBeans.copy(keyMatch, keyMatchForm).commonColumnDateConverter().excludes("searchParams", "mode").execute();
    }

    protected KeyMatch createKeyMatch() {
        KeyMatch keyMatch;
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        if (keyMatchForm.crudMode == CommonConstants.EDIT_MODE) {
            keyMatch = keyMatchService.getKeyMatch(createKeyMap());
            if (keyMatch == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { keyMatchForm.id });
            }
        } else {
            keyMatch = new KeyMatch();
            keyMatch.setCreatedBy(username);
            keyMatch.setCreatedTime(currentTime);
        }
        keyMatch.setUpdatedBy(username);
        keyMatch.setUpdatedTime(currentTime);
        FessBeans.copy(keyMatchForm, keyMatch).excludesCommonColumns().execute();

        return keyMatch;
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        ComponentUtil.getKeyMatchHelper().update();
        try {
            final KeyMatch keyMatch = createKeyMatch();
            keyMatchService.store(keyMatch);
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
            final KeyMatch keyMatch = createKeyMatch();
            keyMatchService.store(keyMatch);
            SAStrutsUtil.addSessionMessage("success.crud_update_crud_table");

            final String result = displayList(true);
            ComponentUtil.getKeyMatchHelper().update();
            return result;
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

    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (keyMatchForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode", new Object[] { CommonConstants.DELETE_MODE,
                    keyMatchForm.crudMode });
        }

        try {
            final KeyMatch keyMatch = keyMatchService.getKeyMatch(createKeyMap());
            if (keyMatch == null) {
                // throw an exception
                throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { keyMatchForm.id });
            }

            keyMatchService.delete(keyMatch);
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            ComponentUtil.getKeyMatchHelper().update();

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
