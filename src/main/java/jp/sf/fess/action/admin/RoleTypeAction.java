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

import java.sql.Timestamp;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.crud.action.admin.BsRoleTypeAction;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.db.exentity.RoleType;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.util.FessBeans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

public class RoleTypeAction extends BsRoleTypeAction {
    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(RoleTypeAction.class);

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("roleType");
    }

    @Override
    protected void loadRoleType() {

        final RoleType roleType = roleTypeService.getRoleType(createKeyMap());
        if (roleType == null) {
            // throw an exception
            throw new SSCActionMessagesException(
                    "errors.crud_could_not_find_crud_table",
                    new Object[] { roleTypeForm.id });
        }

        FessBeans.copy(roleType, roleTypeForm).commonColumnDateConverter()
                .excludes("searchParams", "mode").execute();
    }

    @Override
    protected RoleType createRoleType() {
        RoleType roleType;
        final String username = systemHelper.getUsername();
        final Timestamp timestamp = systemHelper.getCurrentTimestamp();
        if (roleTypeForm.crudMode == CommonConstants.EDIT_MODE) {
            roleType = roleTypeService.getRoleType(createKeyMap());
            if (roleType == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { roleTypeForm.id });
            }
        } else {
            roleType = new RoleType();
            roleType.setCreatedBy(username);
            roleType.setCreatedTime(timestamp);
        }
        roleType.setUpdatedBy(username);
        roleType.setUpdatedTime(timestamp);
        FessBeans.copy(roleTypeForm, roleType).excludesCommonColumns()
                .execute();

        return roleType;
    }

    @Override
    @Token(save = false, validate = true)
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (roleTypeForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            roleTypeForm.crudMode });
        }

        try {
            final RoleType roleType = roleTypeService
                    .getRoleType(createKeyMap());
            if (roleType == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { roleTypeForm.id });
            }

            //           roleTypeService.delete(roleType);
            final String username = systemHelper.getUsername();
            final Timestamp timestamp = systemHelper.getCurrentTimestamp();
            roleType.setDeletedBy(username);
            roleType.setDeletedTime(timestamp);
            roleTypeService.store(roleType);
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, e.getMessageId(),
                    e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e,
                    "errors.crud_failed_to_delete_crud_table");
        }
    }
}
