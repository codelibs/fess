<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.crawler_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="general"/>
    </jsp:include>
    <main class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.crawler_title_edit"/>
                        </h1>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/general/">
                <div class="row">
                    <div class="col-md-12">
                        <div class="card card-outline card-success">
                            <div class="card-body">
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="alert alert-success">${msg}</div>
                                    </la:info>
                                    <la:errors property="_global"/>
                                </div>
                                    <%-- System --%>
                                <h4><la:message key="labels.general_menu_system"/></h4>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.web_api_json_enabled"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="webApiJson"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="webApiJson" styleClass="form-check-input" property="webApiJson"/>
                                            <label for="webApiJson" class="form-check-label">
                                                 <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.login_required"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="loginRequired"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="loginRequired" styleClass="form-check-input" property="loginRequired"/>
                                            <label for="loginRequired" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.login_link"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="loginLink"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="loginLink" styleClass="form-check-input" property="loginLink"/>
                                            <label for="loginLink" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <c:if test="${fesenType!='cloud' and fesenType!='aws'}">
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.result_collapsed"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="resultCollapsed"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="resultCollapsed" styleClass="form-check-input" property="resultCollapsed" disabled="${fesenType=='cloud' or fesenType=='aws'}"/>
                                            <label for="resultCollapsed" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                </c:if>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.thumbnail"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="thumbnail"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="thumbnail" styleClass="form-check-input" property="thumbnail"/>
                                            <label for="thumbnail" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="appValue" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.app_value"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="appValue"/>
                                        <la:textarea styleId="appValue" property="appValue"
                                                     styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="defaultLabelValue" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.default_label_value"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="defaultLabelValue"/>
                                        <la:textarea styleId="defaultLabelValue" property="defaultLabelValue"
                                                     styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="defaultSortValue" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.default_sort_value"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="defaultSortValue"/>
                                        <la:textarea styleId="defaultSortValue" property="defaultSortValue"
                                                     styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="virtualHostValue" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.virtual_host_value"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="virtualHostValue"/>
                                        <la:textarea styleId="virtualHostValue" property="virtualHostValue"
                                                     styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.popular_word_word_enabled"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="popularWord"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="popularWord" styleClass="form-check-input" property="popularWord"/>
                                            <label for="popularWord" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="csvFileEncoding" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.csv_file_encoding"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="csvFileEncoding"/>
                                        <la:text styleId="csvFileEncoding" property="csvFileEncoding"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.append_query_param_enabled"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="appendQueryParameter"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="appendQueryParameter" styleClass="form-check-input"
                                                                 property="appendQueryParameter"/>
                                            <label for="appendQueryParameter" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <%-- Search File Proxy --%>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.search_file_proxy_enabled"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="searchFileProxy"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="searchFileProxy" styleClass="form-check-input" property="searchFileProxy"/>
                                            <label for="searchFileProxy" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <%-- Browser Locale --%>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.search_use_browser_locale"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="searchUseBrowserLocale"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="searchUseBrowserLocale" styleClass="form-check-input" property="searchUseBrowserLocale"/>
                                            <label for="searchUseBrowserLocale" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <%-- SSO Type --%>
                                <div class="form-group row">
                                    <label for="ssoType" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.sso_type"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="ssoType"/>
                                        <la:select styleId="ssoType" property="ssoType" styleClass="form-control">
                                            <la:option value="none">None</la:option>
                                            <la:option value="oic">OpenID Connect</la:option>
                                            <la:option value="saml">SAML</la:option>
                                            <la:option value="spnego">SPNEGO</la:option>
                                            <la:option value="entraid">Entra ID</la:option>
                                        </la:select>
                                    </div>
                                </div>
                                    <%-- Crawler --%>
                                <h4><la:message key="labels.general_menu_crawler"/></h4>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.incremental_crawling"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="incrementalCrawling"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="incrementalCrawling" styleClass="form-check-input"
                                                                 property="incrementalCrawling"/>
                                            <label for="incrementalCrawling" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="crawlingThreadCount"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.crawling_thread_count"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="crawlingThreadCount"/>
                                        <input type="number" name="crawlingThreadCount" id="crawlingThreadCount"
                                               value="${f:h(crawlingThreadCount)}" class="form-control"
                                               min="1" max="1000">
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="crawlingUserAgent" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.crawling_user_agent"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="crawlingUserAgent"/>
                                        <la:text styleId="crawlingUserAgent" property="crawlingUserAgent"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="dayForCleanup" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.day_for_cleanup"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="dayForCleanup"/>
                                        <input type="number" name="dayForCleanup" id="dayForCleanup"
                                               value="${f:h(dayForCleanup)}" class="form-control"
                                               min="-1" max="3650">
                                        <la:message key="labels.day"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="ignoreFailureType" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.ignore_failure_type"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="ignoreFailureType"/>
                                        <la:text styleId="ignoreFailureType" property="ignoreFailureType"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="failureCountThreshold"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.failure_count_threshold"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="failureCountThreshold"/>
                                        <input type="number" name="failureCountThreshold" id="failureCountThreshold"
                                               value="${f:h(failureCountThreshold)}" class="form-control"
                                               min="-1" max="1000">
                                    </div>
                                </div>
                                    <%-- Logging --%>
                                <h4><la:message key="labels.general_menu_logging"/></h4>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.search_log_enabled"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="searchLog"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="searchLog" styleClass="form-check-input" property="searchLog"/>
                                            <label for="searchLog" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.user_info_enabled"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="userInfo"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="userInfo" styleClass="form-check-input" property="userInfo"/>
                                            <label for="userInfo" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.user_favorite_enabled"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="userFavorite"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="userFavorite" styleClass="form-check-input" property="userFavorite"/>
                                            <label for="userFavorite" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="purgeSearchLogDay" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.purge_search_log_day"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="purgeSearchLogDay"/>
                                        <input type="number" name="purgeSearchLogDay" id="purgeSearchLogDay"
                                               value="${f:h(purgeSearchLogDay)}" class="form-control"
                                               min="-1" max="3650">
                                        <la:message key="labels.day"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="purgeJobLogDay" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.purge_job_log_day"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="purgeJobLogDay"/>
                                        <input type="number" name="purgeJobLogDay" id="purgeJobLogDay"
                                               value="${f:h(purgeJobLogDay)}" class="form-control"
                                               min="-1" max="3650">
                                        <la:message key="labels.day"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="purgeUserInfoDay" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.purge_user_info_day"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="purgeUserInfoDay"/>
                                        <input type="number" name="purgeUserInfoDay" id="purgeUserInfoDay"
                                               value="${f:h(purgeUserInfoDay)}" class="form-control"
                                               min="-1" max="3650">
                                        <la:message key="labels.day"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="purgeByBots" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.purge_by_bots"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="purgeByBots"/>
                                        <la:text styleId="purgeByBots" property="purgeByBots"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="logLevel" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.log_level"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="logLevel"/>
                                        <la:select styleId="logLevel" property="logLevel" styleClass="form-control">
                                            <la:option value="OFF">OFF</la:option>
                                            <la:option value="FATAL">FATAL</la:option>
                                            <la:option value="ERROR">ERROR</la:option>
                                            <la:option value="WARN">WARN</la:option>
                                            <la:option value="INFO">INFO</la:option>
                                            <la:option value="DEBUG">DEBUG</la:option>
                                            <la:option value="TRACE">TRACE</la:option>
                                            <la:option value="ALL">ALL</la:option>
                                        </la:select>
                                    </div>
                                </div>
                                    <%-- Suggest --%>
                                <h4><la:message key="labels.general_menu_suggest"/></h4>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.suggest_search_log_enabled"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="suggestSearchLog"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="suggestSearchLog" styleClass="form-check-input" property="suggestSearchLog"/>
                                            <label for="suggestSearchLog" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.suggest_documents_enabled"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="suggestDocuments"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="suggestDocuments" styleClass="form-check-input" property="suggestDocuments"/>
                                            <label for="suggestDocuments" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="purgeSuggestSearchLogDay"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.purge_suggest_search_log_day"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="purgeSuggestSearchLogDay"/>
                                        <input type="number" name="purgeSuggestSearchLogDay"
                                               id="purgeSuggestSearchLogDay"
                                               value="${f:h(purgeSuggestSearchLogDay)}" class="form-control"
                                               min="-1" max="3650">
                                        <la:message key="labels.day"/>
                                    </div>
                                </div>
                                    <%-- LDAP --%>
                                <h4><la:message key="labels.general_menu_ldap"/></h4>
                                <div class="form-group row">
                                    <label for="ldapProviderUrl"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.ldap_provider_url"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="ldapProviderUrl"/>
                                        <la:text styleId="ldapProviderUrl" property="ldapProviderUrl"
                                                 styleClass="form-control" autocomplete="off"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="ldapBaseDn"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.ldap_base_dn"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="ldapBaseDn"/>
                                        <la:text styleId="ldapBaseDn" property="ldapBaseDn"
                                                 styleClass="form-control" autocomplete="off"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="ldapAdminSecurityPrincipal"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.ldap_admin_security_principal"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="ldapAdminSecurityPrincipal"/>
                                        <la:text styleId="ldapAdminSecurityPrincipal"
                                                 property="ldapAdminSecurityPrincipal"
                                                 styleClass="form-control" autocomplete="off"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="ldapAdminSecurityCredentials"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.ldap_admin_security_credentials"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="ldapAdminSecurityCredentials"/>
                                        <la:password styleId="ldapAdminSecurityCredentials"
                                                     property="ldapAdminSecurityCredentials"
                                                     styleClass="form-control" autocomplete="new-password"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="ldapSecurityPrincipal"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.ldap_security_principal"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="ldapSecurityPrincipal"/>
                                        <la:text styleId="ldapSecurityPrincipal" property="ldapSecurityPrincipal"
                                                 styleClass="form-control" autocomplete="off"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="ldapAccountFilter"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.ldap_account_filter"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="ldapAccountFilter"/>
                                        <la:text styleId="ldapAccountFilter" property="ldapAccountFilter"
                                                 styleClass="form-control" autocomplete="off"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="ldapGroupFilter"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.ldap_group_filter"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="ldapGroupFilter"/>
                                        <la:text styleId="ldapGroupFilter" property="ldapGroupFilter"
                                                 styleClass="form-control" autocomplete="off"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="ldapMemberofAttribute"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.ldap_memberof_attribute"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="ldapMemberofAttribute"/>
                                        <la:text styleId="ldapMemberofAttribute" property="ldapMemberofAttribute"
                                                 styleClass="form-control" autocomplete="off"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="ldapSecurityAuthentication"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.ldap_security_authentication"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="ldapSecurityAuthentication"/>
                                        <la:text styleId="ldapSecurityAuthentication" property="ldapSecurityAuthentication"
                                                 styleClass="form-control" autocomplete="off"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="ldapInitialContextFactory"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.ldap_initial_context_factory"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="ldapInitialContextFactory"/>
                                        <la:text styleId="ldapInitialContextFactory" property="ldapInitialContextFactory"
                                                 styleClass="form-control" autocomplete="off"/>
                                    </div>
                                </div>
                                    <%-- OpenID Connect --%>
                                <h4><la:message key="labels.general_menu_oic"/></h4>
                                <div class="form-group row">
                                    <label for="oicClientId"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.oic_client_id"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="oicClientId"/>
                                        <la:password styleId="oicClientId" property="oicClientId"
                                                     styleClass="form-control" autocomplete="new-password"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="oicClientSecret"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.oic_client_secret"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="oicClientSecret"/>
                                        <la:password styleId="oicClientSecret" property="oicClientSecret"
                                                     styleClass="form-control" autocomplete="new-password"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="oicAuthServerUrl"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.oic_auth_server_url"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="oicAuthServerUrl"/>
                                        <la:text styleId="oicAuthServerUrl" property="oicAuthServerUrl"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="oicTokenServerUrl"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.oic_token_server_url"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="oicTokenServerUrl"/>
                                        <la:text styleId="oicTokenServerUrl" property="oicTokenServerUrl"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="oicRedirectUrl"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.oic_redirect_url"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="oicRedirectUrl"/>
                                        <la:text styleId="oicRedirectUrl" property="oicRedirectUrl"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="oicScope"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.oic_scope"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="oicScope"/>
                                        <la:text styleId="oicScope" property="oicScope"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="oicBaseUrl"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.oic_base_url"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="oicBaseUrl"/>
                                        <la:text styleId="oicBaseUrl" property="oicBaseUrl"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="oicDefaultGroups"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.oic_default_groups"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="oicDefaultGroups"/>
                                        <la:text styleId="oicDefaultGroups" property="oicDefaultGroups"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="oicDefaultRoles"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.oic_default_roles"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="oicDefaultRoles"/>
                                        <la:text styleId="oicDefaultRoles" property="oicDefaultRoles"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                    <%-- SAML --%>
                                <h4><la:message key="labels.general_menu_saml"/></h4>
                                <div class="form-group row">
                                    <label for="samlSpBaseUrl"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.saml_sp_base_url"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="samlSpBaseUrl"/>
                                        <la:text styleId="samlSpBaseUrl" property="samlSpBaseUrl"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="samlAttributeGroupName"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.saml_attribute_group_name"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="samlAttributeGroupName"/>
                                        <la:text styleId="samlAttributeGroupName" property="samlAttributeGroupName"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="samlAttributeRoleName"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.saml_attribute_role_name"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="samlAttributeRoleName"/>
                                        <la:text styleId="samlAttributeRoleName" property="samlAttributeRoleName"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="samlDefaultGroups"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.saml_default_groups"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="samlDefaultGroups"/>
                                        <la:text styleId="samlDefaultGroups" property="samlDefaultGroups"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="samlDefaultRoles"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.saml_default_roles"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="samlDefaultRoles"/>
                                        <la:text styleId="samlDefaultRoles" property="samlDefaultRoles"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                    <%-- SPNEGO --%>
                                <h4><la:message key="labels.general_menu_spnego"/></h4>
                                <div class="form-group row">
                                    <label for="spnegoKrb5Conf"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.spnego_krb5_conf"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="spnegoKrb5Conf"/>
                                        <la:text styleId="spnegoKrb5Conf" property="spnegoKrb5Conf"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="spnegoLoginConf"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.spnego_login_conf"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="spnegoLoginConf"/>
                                        <la:text styleId="spnegoLoginConf" property="spnegoLoginConf"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="spnegoLoginClientModule"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.spnego_login_client_module"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="spnegoLoginClientModule"/>
                                        <la:text styleId="spnegoLoginClientModule" property="spnegoLoginClientModule"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="spnegoLoginServerModule"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.spnego_login_server_module"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="spnegoLoginServerModule"/>
                                        <la:text styleId="spnegoLoginServerModule" property="spnegoLoginServerModule"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="spnegoPreauthUsername"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.spnego_preauth_username"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="spnegoPreauthUsername"/>
                                        <la:text styleId="spnegoPreauthUsername" property="spnegoPreauthUsername"
                                                 styleClass="form-control" autocomplete="off"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="spnegoPreauthPassword"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.spnego_preauth_password"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="spnegoPreauthPassword"/>
                                        <la:password styleId="spnegoPreauthPassword" property="spnegoPreauthPassword"
                                                     styleClass="form-control" autocomplete="new-password"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.spnego_allow_basic"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="spnegoAllowBasic"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="spnegoAllowBasic" styleClass="form-check-input" property="spnegoAllowBasic"/>
                                            <label for="spnegoAllowBasic" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.spnego_allow_unsecure_basic"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="spnegoAllowUnsecureBasic"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="spnegoAllowUnsecureBasic" styleClass="form-check-input" property="spnegoAllowUnsecureBasic"/>
                                            <label for="spnegoAllowUnsecureBasic" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.spnego_prompt_ntlm"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="spnegoPromptNtlm"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="spnegoPromptNtlm" styleClass="form-check-input" property="spnegoPromptNtlm"/>
                                            <label for="spnegoPromptNtlm" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.spnego_allow_localhost"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="spnegoAllowLocalhost"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="spnegoAllowLocalhost" styleClass="form-check-input" property="spnegoAllowLocalhost"/>
                                            <label for="spnegoAllowLocalhost" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.spnego_allow_delegation"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="spnegoAllowDelegation"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="spnegoAllowDelegation" styleClass="form-check-input" property="spnegoAllowDelegation"/>
                                            <label for="spnegoAllowDelegation" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="spnegoExcludeDirs"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.spnego_exclude_dirs"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="spnegoExcludeDirs"/>
                                        <la:text styleId="spnegoExcludeDirs" property="spnegoExcludeDirs"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                    <%-- Entra ID --%>
                                <h4><la:message key="labels.general_menu_entraid"/></h4>
                                <div class="form-group row">
                                    <label for="entraidClientId"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.entraid_client_id"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="entraidClientId"/>
                                        <la:password styleId="entraidClientId" property="entraidClientId"
                                                     styleClass="form-control" autocomplete="new-password"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="entraidClientSecret"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.entraid_client_secret"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="entraidClientSecret"/>
                                        <la:password styleId="entraidClientSecret" property="entraidClientSecret"
                                                     styleClass="form-control" autocomplete="new-password"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="entraidTenant"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.entraid_tenant"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="entraidTenant"/>
                                        <la:text styleId="entraidTenant" property="entraidTenant"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="entraidAuthority"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.entraid_authority"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="entraidAuthority"/>
                                        <la:text styleId="entraidAuthority" property="entraidAuthority"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="entraidReplyUrl"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.entraid_reply_url"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="entraidReplyUrl"/>
                                        <la:text styleId="entraidReplyUrl" property="entraidReplyUrl"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="entraidStateTtl"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.entraid_state_ttl"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="entraidStateTtl"/>
                                        <la:text styleId="entraidStateTtl" property="entraidStateTtl"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="entraidDefaultGroups"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.entraid_default_groups"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="entraidDefaultGroups"/>
                                        <la:text styleId="entraidDefaultGroups" property="entraidDefaultGroups"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="entraidDefaultRoles"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.entraid_default_roles"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="entraidDefaultRoles"/>
                                        <la:text styleId="entraidDefaultRoles" property="entraidDefaultRoles"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="entraidPermissionFields"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.entraid_permission_fields"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="entraidPermissionFields"/>
                                        <la:text styleId="entraidPermissionFields" property="entraidPermissionFields"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <span class="font-weight-bold col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.entraid_use_ds"/></span>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="entraidUseDs"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="entraidUseDs" styleClass="form-check-input" property="entraidUseDs"/>
                                            <label for="entraidUseDs" class="form-check-label">
                                                <la:message key="labels.enabled"/>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                                    <%-- Notice --%>
                                <h4><la:message key="labels.general_menu_notice"/></h4>
                                <div class="form-group row">
                                    <label for="notificationLogin"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.notification_login"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="notificationLogin"/>
                                        <la:textarea styleId="notificationLogin" property="notificationLogin"
                                                     styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="notificationSearchTop"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.notification_search_top"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="notificationSearchTop"/>
                                        <la:textarea styleId="notificationSearchTop" property="notificationSearchTop"
                                                     styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="notificationAdvanceSearch"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.notification_advance_search"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="notificationAdvanceSearch"/>
                                        <la:textarea styleId="notificationAdvanceSearch" property="notificationAdvanceSearch"
                                                     styleClass="form-control"/>
                                    </div>
                                </div>
                                    <%-- Notify --%>
                                <h4><la:message key="labels.general_menu_notify"/></h4>
                                <div class="form-group row">
                                    <label for="notificationTo" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.notification_to"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="notificationTo"/>
                                        <div class="input-group">
                                            <la:text styleId="notificationTo" property="notificationTo"
                                                     styleClass="form-control"/>
                                            <div class="input-group-append">
                                                <button type="submit" class="btn btn-default ${f:h(editableClass)}"
                                                        name="sendmail" value="test">
                                                    <la:message key="labels.send_testmail"/>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="slackWebhookUrls"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.slack_webhook_urls"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="slackWebhookUrls"/>
                                        <la:textarea styleId="slackWebhookUrls" property="slackWebhookUrls"
                                                     styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="googleChatWebhookUrls"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.google_chat_webhook_urls"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="googleChatWebhookUrls"/>
                                        <la:textarea styleId="googleChatWebhookUrls" property="googleChatWebhookUrls"
                                                     styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="logNotificationEnabled"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.log_notification_enabled"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="logNotificationEnabled"/>
                                        <div class="form-check">
                                            <la:checkbox styleId="logNotificationEnabled"
                                                         property="logNotificationEnabled"
                                                         styleClass="form-check-input"/>
                                        </div>
                                    </div>
                                </div>
                                    <%-- Storage --%>
                                <h4><la:message key="labels.general_storage"/></h4>
                                <div class="form-group row">
                                    <label for="storageType"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.storage_type"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="storageType"/>
                                        <la:select styleId="storageType" property="storageType"
                                                   styleClass="form-control">
                                            <la:option value="auto"><la:message key="labels.storage_type_auto"/></la:option>
                                            <la:option value="s3"><la:message key="labels.storage_type_s3"/></la:option>
                                            <la:option value="gcs"><la:message key="labels.storage_type_gcs"/></la:option>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="storageBucket"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.storage_bucket"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="storageBucket"/>
                                        <la:text styleId="storageBucket" property="storageBucket"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="storageEndpoint"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.storage_endpoint"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="storageEndpoint"/>
                                        <la:text styleId="storageEndpoint" property="storageEndpoint"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="storageAccessKey"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.storage_access_key"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="storageAccessKey"/>
                                        <la:password styleId="storageAccessKey" property="storageAccessKey"
                                                     styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="storageSecretKey"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.storage_secret_key"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="storageSecretKey"/>
                                        <la:password styleId="storageSecretKey" property="storageSecretKey"
                                                     styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="storageRegion"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.storage_region"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="storageRegion"/>
                                        <la:text styleId="storageRegion" property="storageRegion"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="storageProjectId"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.storage_project_id"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="storageProjectId"/>
                                        <la:text styleId="storageProjectId" property="storageProjectId"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="storageCredentialsPath"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.storage_credentials_path"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="storageCredentialsPath"/>
                                        <la:text styleId="storageCredentialsPath" property="storageCredentialsPath"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                    <%-- RAG --%>
                                <c:if test="${ragEnabled}">
                                <h4><la:message key="labels.general_rag"/></h4>
                                <div class="form-group row">
                                    <label for="ragLlmName"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.rag_llm_name"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="ragLlmName"/>
                                        <la:select styleId="ragLlmName" property="ragLlmName"
                                                   styleClass="form-control">
                                            <c:forEach var="item" items="${ragLlmNameItems}">
                                                <la:option value="${f:u(item.value)}">${f:h(item.label)}</la:option>
                                            </c:forEach>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="llmLogLevel"
                                           class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.llm_log_level"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="llmLogLevel"/>
                                        <la:select styleId="llmLogLevel" property="llmLogLevel"
                                                   styleClass="form-control">
                                            <la:option value="OFF">OFF</la:option>
                                            <la:option value="ERROR">ERROR</la:option>
                                            <la:option value="WARN">WARN</la:option>
                                            <la:option value="INFO">INFO</la:option>
                                            <la:option value="DEBUG">DEBUG</la:option>
                                            <la:option value="TRACE">TRACE</la:option>
                                        </la:select>
                                    </div>
                                </div>
                                </c:if>
                            </div>
                            <div class="card-footer">
                                <c:if test="${editable}">
                                    <button type="submit" class="btn btn-success" name="update"
                                            value="<la:message key="labels.crawl_button_update" />">
                                        <i class="fa fa-pencil-alt" aria-hidden="true"></i>
                                        <la:message key="labels.crawl_button_update"/>
                                    </button>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </la:form>
        </section>
    </main>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}
