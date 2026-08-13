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
package org.codelibs.fess.app.web.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.text.StringEscapeUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.net.URLUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.sso.SsoAction;
import org.codelibs.fess.chat.ChatClient;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.OsddHelper;
import org.codelibs.fess.helper.PopularWordHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.RoleQueryHelper;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.thumbnail.ThumbnailManager;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.message.UserMessage;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.login.LoginManager;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.next.HtmlNext;
import org.lastaflute.web.ruts.process.ActionRuntime;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Abstract base class for search-related actions in the Fess search application.
 * Provides common functionality for search operations, including search form handling,
 * label management, user authentication, and search result processing.
 *
 * This class extends FessBaseAction and serves as the foundation for all search-related
 * web actions in the application.
 */
public abstract class FessSearchAction extends FessBaseAction {

    /**
     * Default constructor.
     */
    public FessSearchAction() {
        super();
    }

    /** The field name used for label-based search filtering. */
    protected static final String LABEL_FIELD = "label";

    /** Helper for performing search operations and managing search requests. */
    @Resource
    protected SearchHelper searchHelper;

    /** Manager for handling thumbnail generation and display. */
    @Resource
    protected ThumbnailManager thumbnailManager;

    /** Helper for managing label types and label-based filtering. */
    @Resource
    protected LabelTypeHelper labelTypeHelper;

    /** Helper for query processing and transformation. */
    @Resource
    protected QueryHelper queryHelper;

    /** Configuration for query field mappings and processing. */
    @Resource
    protected QueryFieldConfig queryFieldConfig;

    /** Helper for role-based query filtering and security. */
    @Resource
    protected RoleQueryHelper roleQueryHelper;

    /** Helper for managing user information and authentication. */
    @Resource
    protected UserInfoHelper userInfoHelper;

    /** Helper for OpenSearch Description Document (OSDD) functionality. */
    @Resource
    protected OsddHelper osddHelper;

    /** Helper for managing popular search words and suggestions. */
    @Resource
    protected PopularWordHelper popularWordHelper;

    /** Client for RAG chat functionality. */
    @Resource
    protected ChatClient chatClient;

    /** The HTTP servlet request object for the current request. */
    @Resource
    protected HttpServletRequest request;

    /** Flag indicating whether search logging is enabled. */
    protected boolean searchLogSupport;

    /** Flag indicating whether favorite functionality is enabled. */
    protected boolean favoriteSupport;

    /** Flag indicating whether thumbnail generation is enabled. */
    protected boolean thumbnailSupport;

    /**
     * Hook method called before action execution. Sets up search-related flags and
     * registers popular words if enabled.
     *
     * @param runtime the action runtime context
     * @return the action response, or null to continue with normal processing
     */
    @Override
    public ActionResponse hookBefore(final ActionRuntime runtime) { // application may override
        searchLogSupport = fessConfig.isSearchLog();
        favoriteSupport = fessConfig.isUserFavorite();
        thumbnailSupport = fessConfig.isThumbnailEnabled();
        runtime.registerData("searchLogSupport", searchLogSupport);
        runtime.registerData("favoriteSupport", favoriteSupport);
        runtime.registerData("thumbnailSupport", thumbnailSupport);
        if (fessConfig.isWebApiPopularWord()) {
            final List<String> tagList = new ArrayList<>();
            final String key = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
            if (StringUtil.isNotBlank(key)) {
                tagList.add(key);
            }
            runtime.registerData("popularWords", popularWordHelper.getWordList(SearchRequestType.SEARCH, null,
                    tagList.toArray(new String[tagList.size()]), null, null, null));
        }
        fessLoginAssist.getSavedUserBean().ifPresent(userBean -> {
            // FessUserBean.empty() wraps a null FessUser, so the bean does not always carry one.
            final FessUser fessUser = userBean.getFessUser();
            final String messageKey = fessUser != null ? getPermissionStateMessageKey(fessUser) : null;
            if (messageKey != null) {
                carryOverSavedErrors();
                // Request scope and add, not sessionManager and saveMessages. Session scope would
                // leave the message behind on any page without <la:errors>, to surface later when
                // it is no longer true, and saveMessages overwrites -- it would discard a message
                // an earlier request saved before redirecting here.
                requestManager.errors().add(UserMessages.GLOBAL, messageKey);
            }
        });
        return super.hookBefore(runtime);
    }

    /**
     * Moves any error messages an earlier request saved into request scope, so that the permission
     * notice added next accompanies them instead of hiding them.
     *
     * <p>{@code saveError(...)} writes under {@link org.lastaflute.web.LastaWebKey#ACTION_ERRORS_KEY}
     * in session scope, and the notice is written under that same key in request scope.
     * {@code <la:errors>} resolves the key with {@code pageContext.findAttribute}, which searches
     * page, then request, then session and stops at the first scope that answers -- so a
     * request-scoped notice on its own wins outright, and the tag then clears the session copy it
     * never rendered. That is how "invalid query" would go missing for a user whose permissions
     * are still loading: {@code SearchAction} saves it and redirects to the root, and the root's
     * own hookBefore would shadow it.
     *
     * <p>The carry-over is deliberately non-consuming: the messages are rebuilt into a fresh
     * {@link UserMessages} and only that new instance is handed to request scope, so the session
     * copy is never marked accessed and {@code ActionCoinsHelper#removeCachedMessages} leaves it
     * in place at the start of the next request. Reading the session copy through
     * {@code UserMessages#accessByIteratorOf} instead -- which is what handing it straight to
     * {@code addMessages(saved)} does -- would mark it accessed and so destroy it even for a
     * response that renders no {@code <la:errors>} at all and therefore displayed it zero times.
     * Several of the responses reachable from here are exactly that: {@code error/notFound.jsp},
     * {@code help.jsp}, {@code chat/chat.jsp}, and the streaming or redirecting actions that
     * extend this class. A page that does render the tag still clears the session copy itself,
     * once it has written the merged request copy.
     *
     * <p>Resource messages are rebuilt from their key and values rather than shared, because
     * {@code TaglibEnhanceLogic#htmlEscapeMessageArgs} escapes the value array in place: one
     * array shared by both copies would be escaped twice if the session copy were rendered on a
     * later request.
     */
    protected void carryOverSavedErrors() {
        sessionManager.errors().get().ifPresent(saved -> {
            final UserMessages carried = new UserMessages();
            for (final String property : saved.toPropertySet()) {
                // silentAccessByIteratorOf, not accessByIteratorOf: reading must not mark the
                // session copy accessed.
                for (final Iterator<UserMessage> ite = saved.silentAccessByIteratorOf(property); ite.hasNext();) {
                    final UserMessage message = ite.next();
                    if (message.isResource()) {
                        // The constructor copies the values array, so the two copies share nothing.
                        carried.add(property, new UserMessage(message.getMessageKey(), message.getValues()));
                    } else {
                        // A direct message has no values to share -- UserMessage's direct
                        // constructor stores an empty array -- and no public way to rebuild it.
                        carried.add(property, message);
                    }
                }
            }
            requestManager.errors().addMessages(carried);
        });
    }

    /**
     * Returns the message telling the user that their group and role permissions are not all in
     * place, or null when they are.
     *
     * <p>Takes a {@link FessUser} rather than reading a specific authenticator's user type: the
     * state is on the interface so that any authenticator resolving memberships after login can
     * report it.
     *
     * <p>An unrecognized state says nothing, and so does null: {@link FessUser#getPermissionState()}
     * is documented as never null but nothing enforces that on an implementation, and this runs on
     * every front-end page, where throwing would turn a missing state into a 500.
     *
     * @param user The logged-in user.
     * @return The message key, or null when there is nothing to say.
     */
    protected String getPermissionStateMessageKey(final FessUser user) {
        // "case null" as well as default: FessUser is Serializable and its implementations live in
        // the session, so one that gains this field without changing serialVersionUID deserializes an
        // older session with it null. A bare switch on a null selector throws, and this runs in
        // hookBefore on every front page, so that would be a 500 on every request until the session
        // is dropped.
        return switch (user.getPermissionState()) {
        case PENDING -> FessMessages.ERRORS_user_permissions_loading;
        case FAILED -> FessMessages.ERRORS_user_permissions_unavailable;
        case null, default -> null;
        };
    }

    /**
     * Returns the login manager for this action. Search actions do not require
     * a login manager as they handle authentication differently.
     *
     * @return an empty OptionalThing as search actions don't use login managers
     */
    @Override
    protected OptionalThing<LoginManager> myLoginManager() {
        return OptionalThing.empty();
    }

    /**
     * Sets up HTML data for rendering search-related pages. This includes
     * label types, language items, user information, and various UI flags.
     *
     * @param runtime the action runtime context
     */
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        systemHelper.setupSearchHtmlData(this, runtime);

        runtime.registerData("osddLink", osddHelper.hasOpenSearchFile());
        runtime.registerData("clipboardCopyIcon", fessConfig.isClipboardCopyIconEnabled());

        final List<Map<String, String>> labelTypeItems = labelTypeHelper.getLabelTypeItemList(SearchRequestType.SEARCH,
                request.getLocale() == null ? Locale.ROOT : request.getLocale());
        runtime.registerData("labelTypeItems", labelTypeItems);
        runtime.registerData("displayLabelTypeItems", labelTypeItems != null && !labelTypeItems.isEmpty());

        Locale locale = ComponentUtil.getRequestManager().getUserLocale();
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        runtime.registerData("langItems", systemHelper.getLanguageItems(locale));
        final String username = systemHelper.getUsername();
        runtime.registerData("username", username);
        runtime.registerData("editableUser", fessLoginAssist.getSavedUserBean().map(FessUserBean::isEditable).orElse(false));
        runtime.registerData("adminUser",
                fessConfig.isAdminUser(username) || fessLoginAssist.getSavedUserBean()
                        .map(user -> user.hasRoles(fessConfig.getAuthenticationAdminRolesAsArray()))
                        .orElse(false));

        runtime.registerData("pageLoginLink", fessConfig.isLoginLinkEnabled());
        runtime.registerData("chatEnabled", chatClient.isAvailable());
    }

    // ===================================================================================
    //                                                                             Helpers
    //                                                                           =========

    /**
     * Checks if login is required for the current request based on configuration
     * and user authentication status.
     *
     * @return true if login is required, false otherwise
     */
    protected boolean isLoginRequired() {
        if (fessConfig.isLoginRequired() && !fessLoginAssist.getSavedUserBean().isPresent()) {
            return true;
        }
        return false;
    }

    /**
     * Builds and populates search form parameters including results per page,
     * label filtering, and sort order based on user preferences and configuration.
     *
     * @param form the search form to populate with parameters
     */
    protected void buildFormParams(final SearchForm form) {

        final HttpSession session = request.getSession(false);
        if (session != null) {
            final Object resultsPerPage = session.getAttribute(Constants.RESULTS_PER_PAGE);
            if (resultsPerPage instanceof Integer) {
                form.num = (Integer) resultsPerPage;
            }
        }

        // label
        final List<Map<String, String>> labelTypeItems = labelTypeHelper.getLabelTypeItemList(SearchRequestType.SEARCH,
                request.getLocale() == null ? Locale.ROOT : request.getLocale());

        if (!labelTypeItems.isEmpty() && !form.fields.containsKey(FessSearchAction.LABEL_FIELD)) {
            final String[] defaultLabelValues = fessConfig.getDefaultLabelValues(getUserBean());
            if (defaultLabelValues.length > 0) {
                form.fields.put(FessSearchAction.LABEL_FIELD, defaultLabelValues);
            }
        }

        final Map<String, String> labelMap = new LinkedHashMap<>();
        if (!labelTypeItems.isEmpty()) {
            for (final Map<String, String> map : labelTypeItems) {
                labelMap.put(map.get(Constants.ITEM_VALUE), map.get(Constants.ITEM_LABEL));
            }
        }
        request.setAttribute(Constants.LABEL_VALUE_MAP, labelMap);

        // sort
        if (StringUtil.isBlank(form.sort)) {
            final String[] defaultSortValues = fessConfig.getDefaultSortValues(getUserBean());
            if (defaultSortValues.length == 1) {
                form.sort = defaultSortValues[0];
            } else if (defaultSortValues.length >= 2) {
                final StringBuilder sortValueSb = new StringBuilder();
                final Set<String> sortFieldNames = new HashSet<>();
                for (final String defaultSortValue : defaultSortValues) {
                    for (final String singleValue : defaultSortValue.split(",")) {
                        final String sortFieldName = singleValue.split("\\.")[0];
                        if (!sortFieldNames.contains(sortFieldName)) {
                            sortFieldNames.add(sortFieldName);
                            if (sortValueSb.length() > 0) {
                                sortValueSb.append(",");
                            }
                            sortValueSb.append(singleValue);
                        }
                    }
                }
                form.sort = sortValueSb.toString();
            }
        }
    }

    /**
     * Builds initial parameters for facet and geo search functionality
     * by calling buildInitParamMap for both parameter types.
     */
    protected void buildInitParams() {
        buildInitParamMap(viewHelper.getInitFacetParamMap(), Constants.FACET_QUERY, Constants.FACET_FORM);
        buildInitParamMap(viewHelper.getInitGeoParamMap(), Constants.GEO_QUERY, Constants.GEO_FORM);
    }

    /**
     * Builds parameter maps for search initialization, creating both query strings
     * and form inputs for the given parameters.
     *
     * @param paramMap the parameter map to process
     * @param queryKey the key for storing query string parameters
     * @param formKey the key for storing form input parameters
     */
    protected void buildInitParamMap(final Map<String, String> paramMap, final String queryKey, final String formKey) {
        if (!paramMap.isEmpty()) {
            final StringBuilder queryBuf = new StringBuilder(100);
            final StringBuilder formBuf = new StringBuilder(100);
            for (final Map.Entry<String, String> entry : paramMap.entrySet()) {
                queryBuf.append('&');
                queryBuf.append(URLUtil.encode(entry.getValue(), Constants.UTF_8));
                queryBuf.append('=');
                queryBuf.append(URLUtil.encode(entry.getKey(), Constants.UTF_8));
                formBuf.append("<input type=\"hidden\" name=\"");
                formBuf.append(StringEscapeUtils.escapeHtml4(entry.getValue()));
                formBuf.append("\" value=\"");
                formBuf.append(StringEscapeUtils.escapeHtml4(entry.getKey()));
                formBuf.append("\"/>");
            }
            request.setAttribute(queryKey, queryBuf.toString());
            request.setAttribute(formKey, formBuf.toString());
        }
    }

    /**
     * Redirects the user to the login page after storing current search parameters
     * for restoration after successful authentication.
     *
     * @return HTML response that redirects to the login page
     */
    protected HtmlResponse redirectToLogin() {
        searchHelper.storeSearchParameters();
        return systemHelper.getRedirectResponseToLogin(redirect(SsoAction.class));
    }

    /**
     * Redirects the user to the root path of the application.
     *
     * @return HTML response that redirects to the root page
     */
    protected HtmlResponse redirectToRoot() {
        return systemHelper.getRedirectResponseToRoot(newHtmlResponseAsRedirect("/"));
    }

    /**
     * Processes the given path through the virtual host helper to handle
     * virtual host configurations and path modifications.
     *
     * @param path the HTML path to process
     * @return the processed path with virtual host handling applied
     */
    protected HtmlNext virtualHost(final HtmlNext path) {
        return ComponentUtil.getVirtualHostHelper().getVirtualHostPath(path);
    }
}
