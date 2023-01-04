/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.api.admin.systeminfo;

import static org.codelibs.fess.app.web.admin.systeminfo.AdminSysteminfoAction.getBugReportItems;
import static org.codelibs.fess.app.web.admin.systeminfo.AdminSysteminfoAction.getEnvItems;
import static org.codelibs.fess.app.web.admin.systeminfo.AdminSysteminfoAction.getFessPropItems;
import static org.codelibs.fess.app.web.admin.systeminfo.AdminSysteminfoAction.getPropItems;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

/**
 * @author Keiichi Watanabe
 */
public class ApiAdminSysteminfoAction extends FessApiAdminAction {

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/systeminfo
    @Execute
    public JsonResponse<ApiResult> get$index() {
        final List<Map<String, String>> bugReportItems = getBugReportItems();
        final List<Map<String, String>> envItems = getEnvItems();
        final List<Map<String, String>> fessPropItems = getFessPropItems(fessConfig);
        final List<Map<String, String>> propItems = getPropItems();
        return asJson(new ApiResult.ApiSystemInfoResponse().bugReportProps(bugReportItems).envProps(envItems).fessProps(fessPropItems)
                .systemProps(propItems).status(ApiResult.Status.OK).result());
    }

}
