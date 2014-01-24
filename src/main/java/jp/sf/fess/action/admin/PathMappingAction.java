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
import jp.sf.fess.crud.action.admin.BsPathMappingAction;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.db.exentity.PathMapping;
import jp.sf.fess.helper.PathMappingHelper;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.util.FessBeans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.exception.ActionMessagesException;

public class PathMappingAction extends BsPathMappingAction {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(PathMappingAction.class);

    @Resource
    protected PathMappingHelper pathMappingHelper;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("pathMapping");
    }

    @Override
    protected String displayList(final boolean redirect) {
        if (redirect) {
            pathMappingHelper.init();
        }
        return super.displayList(redirect);
    }

    @Override
    protected void loadPathMapping() {

        final PathMapping pathMapping = pathMappingService
                .getPathMapping(createKeyMap());
        if (pathMapping == null) {
            // throw an exception
            throw new SSCActionMessagesException(
                    "errors.crud_could_not_find_crud_table",
                    new Object[] { pathMappingForm.id });
        }

        FessBeans.copy(pathMapping, pathMappingForm)
                .commonColumnDateConverter().excludes("searchParams", "mode")
                .execute();
    }

    @Override
    protected PathMapping createPathMapping() {
        PathMapping pathMapping;
        final String username = systemHelper.getUsername();
        final Timestamp timestamp = systemHelper.getCurrentTimestamp();
        if (pathMappingForm.crudMode == CommonConstants.EDIT_MODE) {
            pathMapping = pathMappingService.getPathMapping(createKeyMap());
            if (pathMapping == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { pathMappingForm.id });
            }
        } else {
            pathMapping = new PathMapping();
            pathMapping.setCreatedBy(username);
            pathMapping.setCreatedTime(timestamp);
        }
        pathMapping.setUpdatedBy(username);
        pathMapping.setUpdatedTime(timestamp);
        FessBeans.copy(pathMappingForm, pathMapping).excludesCommonColumns()
                .execute();

        return pathMapping;
    }

    @Override
    @Execute(validator = false, input = "error.jsp")
    public String delete() {
        if (pathMappingForm.crudMode != CommonConstants.DELETE_MODE) {
            throw new SSCActionMessagesException("errors.crud_invalid_mode",
                    new Object[] { CommonConstants.DELETE_MODE,
                            pathMappingForm.crudMode });
        }

        try {
            final PathMapping pathMapping = pathMappingService
                    .getPathMapping(createKeyMap());
            if (pathMapping == null) {
                // throw an exception
                throw new SSCActionMessagesException(
                        "errors.crud_could_not_find_crud_table",
                        new Object[] { pathMappingForm.id });
            }

            //            pathMappingService.delete(pathMapping);
            final String username = systemHelper.getUsername();
            final Timestamp timestamp = systemHelper.getCurrentTimestamp();
            pathMapping.setDeletedBy(username);
            pathMapping.setDeletedTime(timestamp);
            pathMappingService.store(pathMapping);
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
