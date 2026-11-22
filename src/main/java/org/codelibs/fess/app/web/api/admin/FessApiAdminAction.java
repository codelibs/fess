/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.api.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.api.ApiResult.ApiErrorResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.FessApiAction;
import org.codelibs.fess.exception.InvalidAccessTokenException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Abstract base class for admin API actions in Fess.
 * This class extends FessApiAction to provide admin-specific functionality
 * including enhanced access control for administrative operations.
 *
 * <p>Admin API actions require special permissions and access tokens
 * that are validated against the admin role configuration.</p>
 */
public abstract class FessApiAdminAction extends FessApiAction {

    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(FessApiAdminAction.class);

    /**
     * Default constructor.
     */
    public FessApiAdminAction() {
    }

    /**
     * Determines whether the current request is authorized to access admin API endpoints.
     * This method validates the access token and checks if the associated permissions
     * allow admin access according to the Fess configuration.
     *
     * <p>Only the permissions the token was issued with are consulted. A token may also name a
     * request parameter through which the caller supplies further permissions, and those are what
     * a search is filtered by -- but they are chosen by the caller, so admitting them here let any
     * token whatsoever reach the administration API by naming
     * {@code api.admin.access.permissions} in that parameter. Granting a token administrative
     * access is the issuing screen's decision, and it is recorded on the token.
     *
     * @return true if admin access is allowed, false otherwise
     */
    @Override
    protected boolean isAccessAllowed() {
        try {
            return accessTokenService.getTokenPermissions(request)
                    .map(permissions -> fessConfig.isApiAdminAccessAllowed(permissions))
                    .orElse(false);
        } catch (final InvalidAccessTokenException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid access token.", e);
            }
            return false;
        }
    }

    /**
     * Gives a crawl setting that is being created the default permission when the request names none.
     * <p>
     * The create screens prefill the permission field with {@code role.search.default.display.permissions}
     * ({@code {role}guest}). A request of this API never runs that prefill, and a setting without a permission
     * has documents that no search shows, administrators included, with nothing in the response or the logs to say so.
     * A permission that the caller sent is kept as is.
     * </p>
     * <p>
     * Only a POST calls this. A PUT stores what it carries, like the edit screens, because the body of a GET
     * (which has an empty {@code permissions} for a setting without one) has to be sendable back unchanged: a PUT that
     * omits {@code permissions} (null) keeps the permission of the stored setting, and an empty one stores none.
     * </p>
     *
     * @param permissions the permissions of the request, one per line, may be null or blank
     * @return the permissions of the request, or the default one when it has none
     */
    protected String permissionsOrDefault(final String permissions) {
        return StringUtil.isBlank(permissions) ? fessConfig.getSearchDefaultDisplayPermission() : permissions;
    }

    /**
     * Checks whether this action works only with the CodeLibs plugins of the search engine.
     * <p>
     * When it does and the search engine runs without them ({@code FessConfig#isFesenPluginless()}),
     * {@link #hookBefore(ActionRuntime)} answers with HTTP 404 instead of running the action.
     * </p>
     *
     * @return true if the action needs the CodeLibs plugins; false by default
     */
    protected boolean requiresEnginePlugins() {
        return false;
    }

    /**
     * Records a request that the access token check turned away as {@code ACCESS_DENIED}.
     * <p>
     * The user is the name of the access token when the request carries a registered one (an expired
     * token, or one that lacks the administrative permission) and {@code -} otherwise, and only the
     * request path is written: neither the access token nor its parameter ever reaches the audit log.
     * </p>
     *
     * @param runtime the action runtime context of the rejected request
     */
    @Override
    protected void onAccessDenied(final ActionRuntime runtime) {
        activityHelper.accessDenied(getAuditUser(), runtime.getRequestPath());
    }

    /**
     * Gets the name that the audit log records as the user of this request.
     * <p>
     * It is the logged-in user when the request carries a session. A request that carries only an
     * access token has no user, so the name of the token says whose call it was; the token itself is
     * never used. It is {@code -} when neither is known.
     * </p>
     *
     * @return the user id, the access token name, or {@code -}
     */
    protected String getAuditUser() {
        return getUserBean().map(FessUserBean::getUserId).orElseGet(() -> {
            try {
                return accessTokenService.getTokenName(request).filter(StringUtil::isNotBlank).orElse("-");
            } catch (final RuntimeException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to resolve the access token name for the audit log.", e);
                }
                return "-";
            }
        });
    }

    /**
     * Records the request in the audit log before the action runs, as the admin screens do.
     * <p>
     * This runs only after {@link #godHandPrologue(ActionRuntime)} has accepted the access token,
     * so every administrative change made through this API leaves an {@code ACCESS} record. The
     * user is the logged-in user when the request carries a session, the name of the access token
     * when it carries only a token, and {@code -} otherwise; the access token itself is never written. An action that {@link #requiresEnginePlugins() requires
     * the plugins} is then turned away with HTTP 404 when the search engine runs without them.
     * </p>
     *
     * @param runtime the action runtime context
     * @return the action response from the parent hook
     */
    @Override
    public ActionResponse hookBefore(final ActionRuntime runtime) {
        activityHelper.access(getAuditUser(), runtime.getRequestPath(), runtime.getExecuteMethod().getName());
        if (requiresEnginePlugins() && fessConfig.isFesenPluginless()) {
            return asJson(
                    new ApiErrorResponse().message("This API is not available when the search engine runs without the CodeLibs plugins.")
                            .status(Status.FAILED)
                            .result()).httpStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return super.hookBefore(runtime);
    }

    /**
     * Tells whether a stored document was changed since the {@code version_no} of a request was read.
     *
     * @param stored the version of the stored document, or null if it is not known
     * @param requested the {@code version_no} the request carries
     * @return true if both are known and differ. A version that was not read (null or negative) cannot be
     *         compared, and the request is not judged stale.
     */
    public static boolean isStaleVersion(final Long stored, final Number requested) {
        return stored != null && stored.longValue() >= 0L && requested != null && stored.longValue() != requested.longValue();
    }

    /**
     * Refuses an update whose {@code version_no} is not the version of the stored document, so that a change
     * made since the document was read is not overwritten. Call it before the body is applied.
     *
     * @param id the ID of the document to update, for the log
     * @param requested the {@code version_no} the request carries
     * @param stored the version of the stored document, or null if there is no such document (it is
     *        reported as not found by the caller)
     */
    protected void verifyVersionNo(final String id, final Number requested, final Long stored) {
        if (isStaleVersion(stored, requested)) {
            if (logger.isInfoEnabled()) {
                logger.info("The version_no of an update is stale: id={}, requestedVersion={}, storedVersion={}", id, requested, stored);
            }
            throwValidationErrorApi(messages -> messages.addErrorsSettingChangedConcurrently(GLOBAL));
        }
    }
}
