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

import java.time.LocalDateTime;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codelibs.fess.beans.FessBeans;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.crud.action.admin.BsPathMappingAction;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.db.exentity.PathMapping;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.SystemHelper;
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

        final PathMapping pathMapping = pathMappingService.getPathMapping(createKeyMap());
        if (pathMapping == null) {
            // throw an exception
            throw new SSCActionMessagesException("errors.crud_could_not_find_crud_table", new Object[] { pathMappingForm.id });
        }

        FessBeans.copy(pathMapping, pathMappingForm).commonColumnDateConverter().excludes("searchParams", "mode").execute();
    }

    @Override
    protected PathMapping createPathMapping() {
        PathMapping pathMapping;
        final String username = systemHelper.getUsername();
        final LocalDateTime currentTime = systemHelper.getCurrentTime();
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

    @Override
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

            //            pathMappingService.delete(pathMapping);
            final String username = systemHelper.getUsername();
            final LocalDateTime currentTime = systemHelper.getCurrentTime();
            pathMapping.setDeletedBy(username);
            pathMapping.setDeletedTime(currentTime);
            pathMappingService.store(pathMapping);
            SAStrutsUtil.addSessionMessage("success.crud_delete_crud_table");

            return displayList(true);
        } catch (final ActionMessagesException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final CrudMessageException e) {
            log.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, e.getMessageId(), e.getArgs());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new SSCActionMessagesException(e, "errors.crud_failed_to_delete_crud_table");
        }
    }

}
