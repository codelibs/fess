<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.crawler_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="general"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.crawler_title_edit"/>
                        </h1>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/general/">
                <div class="fads-row">
                    <div class="fads-col-md-12">
                        <div class="fads-card fads-fads-card-success">
                            <div class="fads-card-body">
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="fads-banner fads-banner-info">${msg}</div>
                                    </la:info>
                                    <la:errors property="_global"/>
                                </div>
                                    <%-- System --%>
                                <h4><la:message key="labels.general_menu_system"/></h4>
                                <div class="fads-form-field">
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
                                <div class="fads-form-field">
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
                                <div class="fads-form-field">
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
                                <div class="fads-form-field">
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
                                <div class="fads-form-field">
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
                                <div class="fads-form-field">
                                    <label for="appValue" class="fads-label"><la:message
                                            key="labels.app_value"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="appValue"/>
                                        <la:textarea styleId="appValue" property="appValue"
                                                     styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="defaultLabelValue" class="fads-label"><la:message
                                            key="labels.default_label_value"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="defaultLabelValue"/>
                                        <la:textarea styleId="defaultLabelValue" property="defaultLabelValue"
                                                     styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="defaultSortValue" class="fads-label"><la:message
                                            key="labels.default_sort_value"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="defaultSortValue"/>
                                        <la:textarea styleId="defaultSortValue" property="defaultSortValue"
                                                     styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="virtualHostValue" class="fads-label"><la:message
                                            key="labels.virtual_host_value"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="virtualHostValue"/>
                                        <la:textarea styleId="virtualHostValue" property="virtualHostValue"
                                                     styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
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
                                <div class="fads-form-field">
                                    <label for="csvFileEncoding" class="fads-label"><la:message
                                            key="labels.csv_file_encoding"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="csvFileEncoding"/>
                                        <la:text styleId="csvFileEncoding" property="csvFileEncoding"
                                                 styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
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
                                <div class="fads-form-field">
                                    <label for="notificationTo" class="fads-label"><la:message
                                            key="labels.notification_to"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="notificationTo"/>
                                        <div class="fads-input-group">
                                            <la:text styleId="notificationTo" property="notificationTo"
                                                     styleClass="fads-textfield"/>
                                            <div class="">
                                                <button type="submit" class="fads-btn fads-btn-default ${f:h(editableClass)}"
                                                        name="sendmail" value="test">
                                                    <la:message key="labels.send_testmail"/>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                    <%-- Crawler --%>
                                <h4><la:message key="labels.general_menu_crawler"/></h4>
                                <div class="fads-form-field">
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
                                <div class="fads-form-field">
                                    <label for="crawlingThreadCount"
                                           class="fads-label"><la:message
                                            key="labels.crawling_thread_count"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="crawlingThreadCount"/>
                                        <input type="number" name="crawlingThreadCount" id="crawlingThreadCount"
                                               value="${f:h(crawlingThreadCount)}" class="fads-textfield"
                                               min="1" max="1000">
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="dayForCleanup" class="fads-label"><la:message
                                            key="labels.day_for_cleanup"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="dayForCleanup"/>
                                        <input type="number" name="dayForCleanup" id="dayForCleanup"
                                               value="${f:h(dayForCleanup)}" class="fads-textfield"
                                               min="-1" max="3650">
                                        <la:message key="labels.day"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="ignoreFailureType" class="fads-label"><la:message
                                            key="labels.ignore_failure_type"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="ignoreFailureType"/>
                                        <la:text styleId="ignoreFailureType" property="ignoreFailureType"
                                                 styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="failureCountThreshold"
                                           class="fads-label"><la:message
                                            key="labels.failure_count_threshold"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="failureCountThreshold"/>
                                        <input type="number" name="failureCountThreshold" id="failureCountThreshold"
                                               value="${f:h(failureCountThreshold)}" class="fads-textfield"
                                               min="-1" max="1000">
                                    </div>
                                </div>
                                    <%-- Logging --%>
                                <h4><la:message key="labels.general_menu_logging"/></h4>
                                <div class="fads-form-field">
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
                                <div class="fads-form-field">
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
                                <div class="fads-form-field">
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
                                <div class="fads-form-field">
                                    <label for="purgeSearchLogDay" class="fads-label"><la:message
                                            key="labels.purge_search_log_day"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="purgeSearchLogDay"/>
                                        <input type="number" name="purgeSearchLogDay" id="purgeSearchLogDay"
                                               value="${f:h(purgeSearchLogDay)}" class="fads-textfield"
                                               min="-1" max="3650">
                                        <la:message key="labels.day"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="purgeJobLogDay" class="fads-label"><la:message
                                            key="labels.purge_job_log_day"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="purgeJobLogDay"/>
                                        <input type="number" name="purgeJobLogDay" id="purgeJobLogDay"
                                               value="${f:h(purgeJobLogDay)}" class="fads-textfield"
                                               min="-1" max="3650">
                                        <la:message key="labels.day"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="purgeUserInfoDay" class="fads-label"><la:message
                                            key="labels.purge_user_info_day"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="purgeUserInfoDay"/>
                                        <input type="number" name="purgeUserInfoDay" id="purgeUserInfoDay"
                                               value="${f:h(purgeUserInfoDay)}" class="fads-textfield"
                                               min="-1" max="3650">
                                        <la:message key="labels.day"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="purgeByBots" class="fads-label"><la:message
                                            key="labels.purge_by_bots"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="purgeByBots"/>
                                        <la:text styleId="purgeByBots" property="purgeByBots"
                                                 styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="logLevel" class="fads-label"><la:message
                                            key="labels.log_level"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="logLevel"/>
                                        <la:select styleId="logLevel" property="logLevel" styleClass="fads-textfield">
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
                                <div class="fads-form-field">
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
                                <div class="fads-form-field">
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
                                <div class="fads-form-field">
                                    <label for="purgeSuggestSearchLogDay"
                                           class="fads-label"><la:message
                                            key="labels.purge_suggest_search_log_day"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="purgeSuggestSearchLogDay"/>
                                        <input type="number" name="purgeSuggestSearchLogDay"
                                               id="purgeSuggestSearchLogDay"
                                               value="${f:h(purgeSuggestSearchLogDay)}" class="fads-textfield"
                                               min="-1" max="3650">
                                        <la:message key="labels.day"/>
                                    </div>
                                </div>
                                    <%-- LDAP --%>
                                <h4><la:message key="labels.general_menu_ldap"/></h4>
                                <div class="fads-form-field">
                                    <label for="ldapProviderUrl"
                                           class="fads-label"><la:message
                                            key="labels.ldap_provider_url"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="ldapProviderUrl"/>
                                        <la:text styleId="ldapProviderUrl" property="ldapProviderUrl"
                                                 styleClass="fads-textfield" autocomplete="off"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="ldapBaseDn"
                                           class="fads-label"><la:message
                                            key="labels.ldap_base_dn"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="ldapBaseDn"/>
                                        <la:text styleId="ldapBaseDn" property="ldapBaseDn"
                                                 styleClass="fads-textfield" autocomplete="off"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="ldapAdminSecurityPrincipal"
                                           class="fads-label"><la:message
                                            key="labels.ldap_admin_security_principal"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="ldapAdminSecurityPrincipal"/>
                                        <la:text styleId="ldapAdminSecurityPrincipal"
                                                 property="ldapAdminSecurityPrincipal"
                                                 styleClass="fads-textfield" autocomplete="off"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="ldapAdminSecurityCredentials"
                                           class="fads-label"><la:message
                                            key="labels.ldap_admin_security_credentials"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="ldapAdminSecurityCredentials"/>
                                        <la:password styleId="ldapAdminSecurityCredentials"
                                                     property="ldapAdminSecurityCredentials"
                                                     styleClass="fads-textfield" autocomplete="new-password"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="ldapSecurityPrincipal"
                                           class="fads-label"><la:message
                                            key="labels.ldap_security_principal"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="ldapSecurityPrincipal"/>
                                        <la:text styleId="ldapSecurityPrincipal" property="ldapSecurityPrincipal"
                                                 styleClass="fads-textfield" autocomplete="off"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="ldapAccountFilter"
                                           class="fads-label"><la:message
                                            key="labels.ldap_account_filter"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="ldapAccountFilter"/>
                                        <la:text styleId="ldapAccountFilter" property="ldapAccountFilter"
                                                 styleClass="fads-textfield" autocomplete="off"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="ldapGroupFilter"
                                           class="fads-label"><la:message
                                            key="labels.ldap_group_filter"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="ldapGroupFilter"/>
                                        <la:text styleId="ldapGroupFilter" property="ldapGroupFilter"
                                                 styleClass="fads-textfield" autocomplete="off"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="ldapMemberofAttribute"
                                           class="fads-label"><la:message
                                            key="labels.ldap_memberof_attribute"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="ldapMemberofAttribute"/>
                                        <la:text styleId="ldapMemberofAttribute" property="ldapMemberofAttribute"
                                                 styleClass="fads-textfield" autocomplete="off"/>
                                    </div>
                                </div>
                                    <%-- Nortification --%>
                                <h4><la:message key="labels.general_menu_notification"/></h4>
                                <div class="fads-form-field">
                                    <label for="notificationLogin"
                                           class="fads-label"><la:message
                                            key="labels.notification_login"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="notificationLogin"/>
                                        <la:textarea styleId="notificationLogin" property="notificationLogin"
                                                     styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="notificationSearchTop"
                                           class="fads-label"><la:message
                                            key="labels.notification_search_top"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="notificationSearchTop"/>
                                        <la:textarea styleId="notificationSearchTop" property="notificationSearchTop"
                                                     styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                    <%-- Storage --%>
                                <h4><la:message key="labels.general_storage"/></h4>
                                <div class="fads-form-field">
                                    <label for="storageType"
                                           class="fads-label"><la:message
                                            key="labels.storage_type"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="storageType"/>
                                        <la:select styleId="storageType" property="storageType"
                                                   styleClass="fads-textfield">
                                            <la:option value="auto"><la:message key="labels.storage_type_auto"/></la:option>
                                            <la:option value="s3"><la:message key="labels.storage_type_s3"/></la:option>
                                            <la:option value="gcs"><la:message key="labels.storage_type_gcs"/></la:option>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="storageBucket"
                                           class="fads-label"><la:message
                                            key="labels.storage_bucket"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="storageBucket"/>
                                        <la:text styleId="storageBucket" property="storageBucket"
                                                 styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="storageEndpoint"
                                           class="fads-label"><la:message
                                            key="labels.storage_endpoint"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="storageEndpoint"/>
                                        <la:text styleId="storageEndpoint" property="storageEndpoint"
                                                 styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="storageAccessKey"
                                           class="fads-label"><la:message
                                            key="labels.storage_access_key"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="storageAccessKey"/>
                                        <la:password styleId="storageAccessKey" property="storageAccessKey"
                                                     styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="storageSecretKey"
                                           class="fads-label"><la:message
                                            key="labels.storage_secret_key"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="storageSecretKey"/>
                                        <la:password styleId="storageSecretKey" property="storageSecretKey"
                                                     styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="storageRegion"
                                           class="fads-label"><la:message
                                            key="labels.storage_region"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="storageRegion"/>
                                        <la:text styleId="storageRegion" property="storageRegion"
                                                 styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="storageProjectId"
                                           class="fads-label"><la:message
                                            key="labels.storage_project_id"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="storageProjectId"/>
                                        <la:text styleId="storageProjectId" property="storageProjectId"
                                                 styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="storageCredentialsPath"
                                           class="fads-label"><la:message
                                            key="labels.storage_credentials_path"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="storageCredentialsPath"/>
                                        <la:text styleId="storageCredentialsPath" property="storageCredentialsPath"
                                                 styleClass="fads-textfield"/>
                                    </div>
                                </div>
                            </div>
                            <div class="fads-card-footer">
                                <c:if test="${editable}">
                                    <button type="submit" class="fads-btn fads-btn-success" name="update"
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
