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

import java.time.LocalDateTime;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.action.admin.BsKeyMatchAction;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.db.exentity.KeyMatch;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.FessBeans;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

public class KeyMatchAction extends BsKeyMatchAction {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(KeyMatchAction.class);

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("keyMatch");
    }

    @Override
    protected void loadKeyMatch() {

        final KeyMatch keyMatch = keyMatchService.getKeyMatch(createKeyMap());
        if (keyMatch == null) {
            // throw an exception
            throw new SSCActionMessagesException(
                    "errors.crud_could_not_find_crud_table",
                    new Object[] { keyMatchForm.id });
        }

        FessBeans.copy(keyMatch, keyMatchForm).commonColumnDateConverter()
                .excludes("searchParams", "mode").execute();
    }

    @Override
    protected KeyMatch createKeyMatch() {
        KeyMatch keyMatch;
        final String username = systemHelper.getUsername();
        final LocalDateTime currentTime = systemHelper.getCurrentTime();
        if (keyMatchForm.crudMode == CommonConstants.EDIT_MODE) {
            keyMatch = keyMatchService.getKeyMatch(createKeyMap());
            if (keyMatch == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { keyMatchForm.id });
            }
        } else {
            keyMatch = new KeyMatch();
            keyMatch.setCreatedBy(username);
            keyMatch.setCreatedTime(currentTime);
        }
        keyMatch.setUpdatedBy(username);
        keyMatch.setUpdatedTime(currentTime);
        FessBeans.copy(keyMatchForm, keyMatch).excludesCommonColumns()
                .execute();

        return keyMatch;
    }

    @Override
    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String create() {
        final String result = super.create();
        ComponentUtil.getKeyMatchHelper().update();
        return result;
    }

    @Override
    @Token(save = false, validate = true)
    @Execute(validator = true, input = "edit.jsp")
    public String update() {
        final String result = super.update();
        ComponentUtil.getKeyMatchHelper().update();
        return result;
    }

    @Override
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (keyMatchForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            keyMatchForm.crudMode });
        }

        try {
            final KeyMatch keyMatch = keyMatchService
                    .getKeyMatch(createKeyMap());
            if (keyMatch == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { keyMatchForm.id });
            }

            //           keyMatchService.delete(keyMatch);
            final String username = systemHelper.getUsername();
            final LocalDateTime currentTime = systemHelper.getCurrentTime();
            keyMatch.setDeletedBy(username);
            keyMatch.setDeletedTime(currentTime);
            keyMatchService.store(keyMatch);
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            ComponentUtil.getKeyMatchHelper().update();

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
