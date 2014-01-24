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

package jp.sf.fess.crud.action.admin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.db.exentity.RoleType;
import jp.sf.fess.form.admin.RoleTypeForm;
import jp.sf.fess.pager.RoleTypePager;
import jp.sf.fess.service.RoleTypeService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.sastruts.core.annotation.Token;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

public class BsRoleTypeAction implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(BsRoleTypeAction.class);

    // for list

    public List<RoleType> roleTypeItems;

    // for edit/confirm/delete

    @ActionForm
    @Resource
    protected RoleTypeForm roleTypeForm;

    @Resource
    protected RoleTypeService roleTypeService;

    @Resource
    protected RoleTypePager roleTypePager;

    protected String displayList(final boolean redirect) {
        // page navi
        roleTypeItems = roleTypeService.getRoleTypeList(roleTypePager);

        // restore from pager
        Beans.copy(roleTypePager, roleTypeForm.searchParams)
                .excludes(CommonConstants.PAGER_CONVERSION_RULE)

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
        if (StringUtil.isNotBlank(roleTypeForm.pageNumber)) {
            try {
                roleTypePager.setCurrentPageNumber(Integer
                        .parseInt(roleTypeForm.pageNumber));
            } catch (final NumberFormatException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Invalid value: " + roleTypeForm.pageNumber, e);
                }
            }
        }

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String search() {
        Beans.copy(roleTypeForm.searchParams, roleTypePager)
                .excludes(CommonConstants.PAGER_CONVERSION_RULE)

                .execute();

        return displayList(false);
    }

    @Execute(validator = false, input = "error.jsp")
    public String reset() {
        roleTypePager.clear();

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
        if (roleTypeForm.crudMode != CommonConstants.CONFIRM_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.CONFIRM_MODE,
                            roleTypeForm.crudMode });
        }

        loadRoleType();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String createpage() {
        // page navi
        roleTypeForm.initialize();
        roleTypeForm.crudMode = CommonConstants.CREATE_MODE;

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp", urlPattern = "editpage/{crudMode}/{id}")
    public String editpage() {
        if (roleTypeForm.crudMode != CommonConstants.EDIT_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.EDIT_MODE,
                            roleTypeForm.crudMode });
        }

        loadRoleType();

        return "edit.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String editfromconfirm() {
        roleTypeForm.crudMode = CommonConstants.EDIT_MODE;

        loadRoleType();

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
        if (roleTypeForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            roleTypeForm.crudMode });
        }

        loadRoleType();

        return "confirm.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false, input = "error.jsp")
    public String deletefromconfirm() {
        roleTypeForm.crudMode = CommonConstants.DELETE_MODE;

        loadRoleType();

        return "confirm.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        try {
            final RoleType roleType = createRoleType();
            roleTypeService.store(roleType);
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
            throw new ActionMessagesException(
                    "errors.crud_failed_to_create_crud_table");
        }
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String update() {
        try {
            final RoleType roleType = createRoleType();
            roleTypeService.store(roleType);
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
            throw new ActionMessagesException(
                    "errors.crud_failed_to_update_crud_table");
        }
    }

    @Token(save = false, validate = true)
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (roleTypeForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new ActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            roleTypeForm.crudMode });
        }

        try {
            final RoleType roleType = roleTypeService
                    .getRoleType(createKeyMap());
            if (roleType == null) {
                // throw an exception
                throw new ActionMessagesException(
                        "errors.crud_could_not_find_crud_table",

                        new Object[] { roleTypeForm.id });

            }

            roleTypeService.delete(roleType);
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
            throw new ActionMessagesException(
                    "errors.crud_failed_to_delete_crud_table");
        }
    }

    protected void loadRoleType() {

        final RoleType roleType = roleTypeService.getRoleType(createKeyMap());
        if (roleType == null) {
            // throw an exception
            throw new ActionMessagesException(
                    "errors.crud_could_not_find_crud_table",

                    new Object[] { roleTypeForm.id });

        }

        Beans.copy(roleType, roleTypeForm).excludes("searchParams", "mode")

        .execute();
    }

    protected RoleType createRoleType() {
        RoleType roleType;
        if (roleTypeForm.crudMode == CommonConstants.EDIT_MODE) {
            roleType = roleTypeService.getRoleType(createKeyMap());
            if (roleType == null) {
                // throw an exception
                throw new ActionMessagesException(
                        "errors.crud_could_not_find_crud_table",

                        new Object[] { roleTypeForm.id });

            }
        } else {
            roleType = new RoleType();
        }
        Beans.copy(roleTypeForm, roleType).excludes("searchParams", "mode")

        .execute();

        return roleType;
    }

    protected Map<String, String> createKeyMap() {
        final Map<String, String> keys = new HashMap<String, String>();

        keys.put("id", roleTypeForm.id);

        return keys;
    }
}
