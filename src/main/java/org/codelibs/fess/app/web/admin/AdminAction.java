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
package org.codelibs.fess.app.web.admin;

import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.web.admin.accesstoken.AdminAccesstokenAction;
import org.codelibs.fess.app.web.admin.backup.AdminBackupAction;
import org.codelibs.fess.app.web.admin.badword.AdminBadwordAction;
import org.codelibs.fess.app.web.admin.boostdoc.AdminBoostdocAction;
import org.codelibs.fess.app.web.admin.crawlinginfo.AdminCrawlinginfoAction;
import org.codelibs.fess.app.web.admin.dashboard.AdminDashboardAction;
import org.codelibs.fess.app.web.admin.dataconfig.AdminDataconfigAction;
import org.codelibs.fess.app.web.admin.design.AdminDesignAction;
import org.codelibs.fess.app.web.admin.dict.AdminDictAction;
import org.codelibs.fess.app.web.admin.duplicatehost.AdminDuplicatehostAction;
import org.codelibs.fess.app.web.admin.elevateword.AdminElevatewordAction;
import org.codelibs.fess.app.web.admin.esreq.AdminEsreqAction;
import org.codelibs.fess.app.web.admin.failureurl.AdminFailureurlAction;
import org.codelibs.fess.app.web.admin.fileauth.AdminFileauthAction;
import org.codelibs.fess.app.web.admin.fileconfig.AdminFileconfigAction;
import org.codelibs.fess.app.web.admin.general.AdminGeneralAction;
import org.codelibs.fess.app.web.admin.group.AdminGroupAction;
import org.codelibs.fess.app.web.admin.joblog.AdminJoblogAction;
import org.codelibs.fess.app.web.admin.keymatch.AdminKeymatchAction;
import org.codelibs.fess.app.web.admin.labeltype.AdminLabeltypeAction;
import org.codelibs.fess.app.web.admin.log.AdminLogAction;
import org.codelibs.fess.app.web.admin.maintenance.AdminMaintenanceAction;
import org.codelibs.fess.app.web.admin.pathmap.AdminPathmapAction;
import org.codelibs.fess.app.web.admin.plugin.AdminPluginAction;
import org.codelibs.fess.app.web.admin.relatedcontent.AdminRelatedcontentAction;
import org.codelibs.fess.app.web.admin.relatedquery.AdminRelatedqueryAction;
import org.codelibs.fess.app.web.admin.reqheader.AdminReqheaderAction;
import org.codelibs.fess.app.web.admin.role.AdminRoleAction;
import org.codelibs.fess.app.web.admin.scheduler.AdminSchedulerAction;
import org.codelibs.fess.app.web.admin.searchlog.AdminSearchlogAction;
import org.codelibs.fess.app.web.admin.storage.AdminStorageAction;
import org.codelibs.fess.app.web.admin.suggest.AdminSuggestAction;
import org.codelibs.fess.app.web.admin.systeminfo.AdminSysteminfoAction;
import org.codelibs.fess.app.web.admin.user.AdminUserAction;
import org.codelibs.fess.app.web.admin.webauth.AdminWebauthAction;
import org.codelibs.fess.app.web.admin.webconfig.AdminWebconfigAction;
import org.codelibs.fess.app.web.admin.wizard.AdminWizardAction;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

public class AdminAction extends FessAdminAction {

    @Override
    protected String getActionRole() {
        return "admin";
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    @Secured({ //
            AdminDashboardAction.ROLE, //
            AdminDashboardAction.ROLE + VIEW, //
            AdminWizardAction.ROLE, //
            AdminWizardAction.ROLE + VIEW, //
            AdminGeneralAction.ROLE, //
            AdminGeneralAction.ROLE + VIEW, //
            AdminSchedulerAction.ROLE, //
            AdminSchedulerAction.ROLE + VIEW, //
            AdminDesignAction.ROLE, //
            AdminDesignAction.ROLE + VIEW, //
            AdminDictAction.ROLE, //
            AdminDictAction.ROLE + VIEW, //
            AdminAccesstokenAction.ROLE, //
            AdminAccesstokenAction.ROLE + VIEW, //
            AdminPluginAction.ROLE, //
            AdminPluginAction.ROLE + VIEW, //
            AdminStorageAction.ROLE, //
            AdminStorageAction.ROLE + VIEW, //
            AdminWebconfigAction.ROLE, //
            AdminWebconfigAction.ROLE + VIEW, //
            AdminFileconfigAction.ROLE, //
            AdminFileconfigAction.ROLE + VIEW, //
            AdminDataconfigAction.ROLE, //
            AdminDataconfigAction.ROLE + VIEW, //
            AdminLabeltypeAction.ROLE, //
            AdminLabeltypeAction.ROLE + VIEW, //
            AdminKeymatchAction.ROLE, //
            AdminKeymatchAction.ROLE + VIEW, //
            AdminBoostdocAction.ROLE, //
            AdminBoostdocAction.ROLE + VIEW, //
            AdminRelatedcontentAction.ROLE, //
            AdminRelatedcontentAction.ROLE + VIEW, //
            AdminRelatedqueryAction.ROLE, //
            AdminRelatedqueryAction.ROLE + VIEW, //
            AdminPathmapAction.ROLE, //
            AdminPathmapAction.ROLE + VIEW, //
            AdminWebauthAction.ROLE, //
            AdminWebauthAction.ROLE + VIEW, //
            AdminFileauthAction.ROLE, //
            AdminFileauthAction.ROLE + VIEW, //
            AdminReqheaderAction.ROLE, //
            AdminReqheaderAction.ROLE + VIEW, //
            AdminDuplicatehostAction.ROLE, //
            AdminDuplicatehostAction.ROLE + VIEW, //
            AdminUserAction.ROLE, //
            AdminUserAction.ROLE + VIEW, //
            AdminRoleAction.ROLE, //
            AdminRoleAction.ROLE + VIEW, //
            AdminGroupAction.ROLE, //
            AdminGroupAction.ROLE + VIEW, //
            AdminSuggestAction.ROLE, //
            AdminSuggestAction.ROLE + VIEW, //
            AdminElevatewordAction.ROLE, //
            AdminElevatewordAction.ROLE + VIEW, //
            AdminBadwordAction.ROLE, //
            AdminBadwordAction.ROLE + VIEW, //
            AdminSysteminfoAction.ROLE, //
            AdminSysteminfoAction.ROLE + VIEW, //
            AdminSearchlogAction.ROLE, //
            AdminSearchlogAction.ROLE + VIEW, //
            AdminJoblogAction.ROLE, //
            AdminJoblogAction.ROLE + VIEW, //
            AdminCrawlinginfoAction.ROLE, //
            AdminCrawlinginfoAction.ROLE + VIEW, //
            AdminLogAction.ROLE, //
            AdminLogAction.ROLE + VIEW, //
            AdminFailureurlAction.ROLE, //
            AdminFailureurlAction.ROLE + VIEW, //
            AdminBackupAction.ROLE, //
            AdminBackupAction.ROLE + VIEW, //
            AdminMaintenanceAction.ROLE, //
            AdminMaintenanceAction.ROLE + VIEW, //
            AdminEsreqAction.ROLE, //
            AdminEsreqAction.ROLE + VIEW, //
            AdminDashboardAction.ROLE, //
            AdminDashboardAction.ROLE + VIEW, //
            AdminWizardAction.ROLE, //
            AdminWizardAction.ROLE + VIEW, //
            AdminGeneralAction.ROLE, //
            AdminGeneralAction.ROLE + VIEW, //
            AdminSchedulerAction.ROLE, //
            AdminSchedulerAction.ROLE + VIEW, //
            AdminDesignAction.ROLE, //
            AdminDesignAction.ROLE + VIEW, //
            AdminDictAction.ROLE, //
            AdminDictAction.ROLE + VIEW, //
            AdminAccesstokenAction.ROLE, //
            AdminAccesstokenAction.ROLE + VIEW, //
            AdminPluginAction.ROLE, //
            AdminPluginAction.ROLE + VIEW, //
            AdminStorageAction.ROLE, //
            AdminStorageAction.ROLE + VIEW, //
            AdminWebconfigAction.ROLE, //
            AdminWebconfigAction.ROLE + VIEW, //
            AdminFileconfigAction.ROLE, //
            AdminFileconfigAction.ROLE + VIEW, //
            AdminDataconfigAction.ROLE, //
            AdminDataconfigAction.ROLE + VIEW, //
            AdminLabeltypeAction.ROLE, //
            AdminLabeltypeAction.ROLE + VIEW, //
            AdminKeymatchAction.ROLE, //
            AdminKeymatchAction.ROLE + VIEW, //
            AdminBoostdocAction.ROLE, //
            AdminBoostdocAction.ROLE + VIEW, //
            AdminRelatedcontentAction.ROLE, //
            AdminRelatedcontentAction.ROLE + VIEW, //
            AdminRelatedqueryAction.ROLE, //
            AdminRelatedqueryAction.ROLE + VIEW, //
            AdminPathmapAction.ROLE, //
            AdminPathmapAction.ROLE + VIEW, //
            AdminWebauthAction.ROLE, //
            AdminWebauthAction.ROLE + VIEW, //
            AdminFileauthAction.ROLE, //
            AdminFileauthAction.ROLE + VIEW, //
            AdminReqheaderAction.ROLE, //
            AdminReqheaderAction.ROLE + VIEW, //
            AdminDuplicatehostAction.ROLE, //
            AdminDuplicatehostAction.ROLE + VIEW, //
            AdminUserAction.ROLE, //
            AdminUserAction.ROLE + VIEW, //
            AdminRoleAction.ROLE, //
            AdminRoleAction.ROLE + VIEW, //
            AdminGroupAction.ROLE, //
            AdminGroupAction.ROLE + VIEW, //
            AdminSuggestAction.ROLE, //
            AdminSuggestAction.ROLE + VIEW, //
            AdminElevatewordAction.ROLE, //
            AdminElevatewordAction.ROLE + VIEW, //
            AdminBadwordAction.ROLE, //
            AdminBadwordAction.ROLE + VIEW, //
            AdminSysteminfoAction.ROLE, //
            AdminSysteminfoAction.ROLE + VIEW, //
            AdminSearchlogAction.ROLE, //
            AdminSearchlogAction.ROLE + VIEW, //
            AdminJoblogAction.ROLE, //
            AdminJoblogAction.ROLE + VIEW, //
            AdminCrawlinginfoAction.ROLE, //
            AdminCrawlinginfoAction.ROLE + VIEW, //
            AdminLogAction.ROLE, //
            AdminLogAction.ROLE + VIEW, //
            AdminFailureurlAction.ROLE, //
            AdminFailureurlAction.ROLE + VIEW, //
            AdminBackupAction.ROLE, //
            AdminBackupAction.ROLE + VIEW, //
            AdminMaintenanceAction.ROLE, //
            AdminMaintenanceAction.ROLE + VIEW, //
            AdminEsreqAction.ROLE, //
            AdminEsreqAction.ROLE + VIEW, //
            AdminDashboardAction.ROLE, //
            AdminDashboardAction.ROLE + VIEW, //
            AdminWizardAction.ROLE, //
            AdminWizardAction.ROLE + VIEW, //
            AdminGeneralAction.ROLE, //
            AdminGeneralAction.ROLE + VIEW, //
            AdminSchedulerAction.ROLE, //
            AdminSchedulerAction.ROLE + VIEW, //
            AdminDesignAction.ROLE, //
            AdminDesignAction.ROLE + VIEW, //
            AdminDictAction.ROLE, //
            AdminDictAction.ROLE + VIEW, //
            AdminAccesstokenAction.ROLE, //
            AdminAccesstokenAction.ROLE + VIEW, //
            AdminPluginAction.ROLE, //
            AdminPluginAction.ROLE + VIEW, //
            AdminStorageAction.ROLE, //
            AdminStorageAction.ROLE + VIEW, //
            AdminWebconfigAction.ROLE, //
            AdminWebconfigAction.ROLE + VIEW, //
            AdminFileconfigAction.ROLE, //
            AdminFileconfigAction.ROLE + VIEW, //
            AdminDataconfigAction.ROLE, //
            AdminDataconfigAction.ROLE + VIEW, //
            AdminLabeltypeAction.ROLE, //
            AdminLabeltypeAction.ROLE + VIEW, //
            AdminKeymatchAction.ROLE, //
            AdminKeymatchAction.ROLE + VIEW, //
            AdminBoostdocAction.ROLE, //
            AdminBoostdocAction.ROLE + VIEW, //
            AdminRelatedcontentAction.ROLE, //
            AdminRelatedcontentAction.ROLE + VIEW, //
            AdminRelatedqueryAction.ROLE, //
            AdminRelatedqueryAction.ROLE + VIEW, //
            AdminPathmapAction.ROLE, //
            AdminPathmapAction.ROLE + VIEW, //
            AdminWebauthAction.ROLE, //
            AdminWebauthAction.ROLE + VIEW, //
            AdminFileauthAction.ROLE, //
            AdminFileauthAction.ROLE + VIEW, //
            AdminReqheaderAction.ROLE, //
            AdminReqheaderAction.ROLE + VIEW, //
            AdminDuplicatehostAction.ROLE, //
            AdminDuplicatehostAction.ROLE + VIEW, //
            AdminUserAction.ROLE, //
            AdminUserAction.ROLE + VIEW, //
            AdminRoleAction.ROLE, //
            AdminRoleAction.ROLE + VIEW, //
            AdminGroupAction.ROLE, //
            AdminGroupAction.ROLE + VIEW, //
            AdminSuggestAction.ROLE, //
            AdminSuggestAction.ROLE + VIEW, //
            AdminElevatewordAction.ROLE, //
            AdminElevatewordAction.ROLE + VIEW, //
            AdminBadwordAction.ROLE, //
            AdminBadwordAction.ROLE + VIEW, //
            AdminSysteminfoAction.ROLE, //
            AdminSysteminfoAction.ROLE + VIEW, //
            AdminSearchlogAction.ROLE, //
            AdminSearchlogAction.ROLE + VIEW, //
            AdminJoblogAction.ROLE, //
            AdminJoblogAction.ROLE + VIEW, //
            AdminCrawlinginfoAction.ROLE, //
            AdminCrawlinginfoAction.ROLE + VIEW, //
            AdminLogAction.ROLE, //
            AdminLogAction.ROLE + VIEW, //
            AdminFailureurlAction.ROLE, //
            AdminFailureurlAction.ROLE + VIEW, //
            AdminBackupAction.ROLE, //
            AdminBackupAction.ROLE + VIEW, //
            AdminMaintenanceAction.ROLE, //
            AdminMaintenanceAction.ROLE + VIEW, //
            AdminEsreqAction.ROLE, //
            AdminEsreqAction.ROLE + VIEW,//

    })
    public HtmlResponse index() {
        return redirect(getUserBean().map(user -> {
            final Class<? extends FessAdminAction> actionClass = getAdminActionClass(user);
            if (actionClass != null) {
                return actionClass;
            }
            return AdminDashboardAction.class;
        }).orElse(AdminDashboardAction.class));
    }

    public static Class<? extends FessAdminAction> getAdminActionClass(final FessUserBean user) {
        if (user.hasRoles(getActionRoles(AdminDashboardAction.ROLE))) {
            return AdminDashboardAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminWizardAction.ROLE))) {
            return AdminWizardAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminGeneralAction.ROLE))) {
            return AdminGeneralAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminSchedulerAction.ROLE))) {
            return AdminSchedulerAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminDesignAction.ROLE))) {
            return AdminDesignAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminDictAction.ROLE))) {
            return AdminDictAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminAccesstokenAction.ROLE))) {
            return AdminAccesstokenAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminPluginAction.ROLE))) {
            return AdminPluginAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminStorageAction.ROLE))) {
            return AdminStorageAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminWebconfigAction.ROLE))) {
            return AdminWebconfigAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminFileconfigAction.ROLE))) {
            return AdminFileconfigAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminDataconfigAction.ROLE))) {
            return AdminDataconfigAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminLabeltypeAction.ROLE))) {
            return AdminLabeltypeAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminKeymatchAction.ROLE))) {
            return AdminKeymatchAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminBoostdocAction.ROLE))) {
            return AdminBoostdocAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminRelatedcontentAction.ROLE))) {
            return AdminRelatedcontentAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminRelatedqueryAction.ROLE))) {
            return AdminRelatedqueryAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminPathmapAction.ROLE))) {
            return AdminPathmapAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminWebauthAction.ROLE))) {
            return AdminWebauthAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminFileauthAction.ROLE))) {
            return AdminFileauthAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminReqheaderAction.ROLE))) {
            return AdminReqheaderAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminDuplicatehostAction.ROLE))) {
            return AdminDuplicatehostAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminUserAction.ROLE))) {
            return AdminUserAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminRoleAction.ROLE))) {
            return AdminRoleAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminGroupAction.ROLE))) {
            return AdminGroupAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminSuggestAction.ROLE))) {
            return AdminSuggestAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminElevatewordAction.ROLE))) {
            return AdminElevatewordAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminBadwordAction.ROLE))) {
            return AdminBadwordAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminSysteminfoAction.ROLE))) {
            return AdminSysteminfoAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminSearchlogAction.ROLE))) {
            return AdminSearchlogAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminJoblogAction.ROLE))) {
            return AdminJoblogAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminCrawlinginfoAction.ROLE))) {
            return AdminCrawlinginfoAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminLogAction.ROLE))) {
            return AdminLogAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminFailureurlAction.ROLE))) {
            return AdminFailureurlAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminBackupAction.ROLE))) {
            return AdminBackupAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminMaintenanceAction.ROLE))) {
            return AdminMaintenanceAction.class;
        }
        if (user.hasRoles(getActionRoles(AdminEsreqAction.ROLE))) {
            return AdminEsreqAction.class;
        }
        return null;
    }

    private static String[] getActionRoles(final String role) {
        return new String[] { role, role + VIEW };
    }

}