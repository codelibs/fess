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

package jp.sf.fess.action.admin;

import java.sql.Timestamp;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.crud.action.admin.BsOverlappingHostAction;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.db.exentity.OverlappingHost;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.util.FessBeans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

public class OverlappingHostAction extends BsOverlappingHostAction {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory
            .getLog(OverlappingHostAction.class);

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("overlappingHost");
    }

    @Override
    protected void loadOverlappingHost() {

        final OverlappingHost overlappingHost = overlappingHostService
                .getOverlappingHost(createKeyMap());
        if (overlappingHost == null) {
            // throw an exception
            throw new SSCActionMessagesException(
                    "errors.crud_could_not_find_crud_table",
                    new Object[] { overlappingHostForm.id });
        }

        FessBeans.copy(overlappingHost, overlappingHostForm)
                .commonColumnDateConverter().excludes("searchParams", "mode")
                .execute();
    }

    @Override
    protected OverlappingHost createOverlappingHost() {
        OverlappingHost overlappingHost;
        final String username = systemHelper.getUsername();
        final Timestamp timestamp = systemHelper.getCurrentTimestamp();
        if (overlappingHostForm.crudMode == CommonConstants.EDIT_MODE) {
            overlappingHost = overlappingHostService
                    .getOverlappingHost(createKeyMap());
            if (overlappingHost == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { overlappingHostForm.id });
            }
        } else {
            overlappingHost = new OverlappingHost();
            overlappingHost.setCreatedBy(username);
            overlappingHost.setCreatedTime(timestamp);
        }
        overlappingHost.setUpdatedBy(username);
        overlappingHost.setUpdatedTime(timestamp);
        FessBeans.copy(overlappingHostForm, overlappingHost)
                .excludesCommonColumns().execute();

        return overlappingHost;
    }

    @Override
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (overlappingHostForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            overlappingHostForm.crudMode });
        }

        try {
            final OverlappingHost overlappingHost = overlappingHostService
                    .getOverlappingHost(createKeyMap());
            if (overlappingHost == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { overlappingHostForm.id });
            }

            //            overlappingHostService.delete(overlappingHost);
            final String username = systemHelper.getUsername();
            final Timestamp timestamp = systemHelper.getCurrentTimestamp();
            overlappingHost.setDeletedBy(username);
            overlappingHost.setDeletedTime(timestamp);
            overlappingHostService.store(overlappingHost);
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
