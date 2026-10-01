<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.searchlog_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="log"/>
        <jsp:param name="menuType" value="searchLog"/>
    </jsp:include>
    <main id="mainContent" class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.searchlog_configuration"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item active"><la:link href="/admin/searchlog">
                                <la:message key="labels.searchlog_title"/>
                            </la:link></li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <jsp:include page="/WEB-INF/view/admin/searchlog/admin_searchlog_tabs.jsp">
                <jsp:param name="tab" value="logs"/>
            </jsp:include>
            <div class="row">
                <div class="col-12">
                    <div class="card card-outline card-primary">
                        <div class="card-header">
                            <h3 class="card-title">
                                <la:message key="labels.searchlog_title"/>
                            </h3>
                        </div>
                        <div class="card-body">
                            <%-- Message --%>
                            <div>
                                <la:info id="msg" message="true">
                                    <div class="alert alert-success">${msg}</div>
                                </la:info>
                                <la:errors/>
                            </div>
                            <la:form action="/admin/searchlog/">
                                <div class="form-group row">
                                    <label for="logTypeSearch" class="col-sm-2 text-sm-right col-form-label"><la:message
                                            key="labels.searchlog_log_type"/></label>
                                    <div class="col-sm-4">
                                        <la:select styleId="logTypeSearch" property="logType"
                                                   styleClass="form-control">
                                            <la:option value="search"><la:message
                                                    key="labels.searchlog_log_type_search"/></la:option>
                                            <la:option value="click"><la:message key="labels.searchlog_log_type_click"/></la:option>
                                            <la:option value="user_info"><la:message
                                                    key="labels.searchlog_log_type_user_info"/></la:option>
                                            <la:option value="favorite"><la:message
                                                    key="labels.searchlog_log_type_favorite"/></la:option>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="queryIdSearch" class="col-sm-2 text-sm-right col-form-label"><la:message
                                            key="labels.searchlog_queryid"/></label>
                                    <div class="col-sm-4">
                                        <la:text styleId="queryIdSearch" property="queryId" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="userSessionIdSearch" class="col-sm-2 text-sm-right col-form-label"><la:message
                                            key="labels.searchlog_usersessionid"/></label>
                                    <div class="col-sm-4">
                                        <la:text styleId="userSessionIdSearch" property="userSessionId"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <c:if test="${logType == 'search'}">
                                    <div class="form-group row">
                                        <label for="accessTypeSearch" class="col-sm-2 text-sm-right col-form-label"><la:message
                                                key="labels.searchlog_accesstype"/></label>
                                        <div class="col-sm-4">
                                            <la:text styleId="accessTypeSearch" property="accessType"
                                                     styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="searchWordSearch" class="col-sm-2 text-sm-right col-form-label"><la:message
                                                key="labels.searchlog_searchword"/></label>
                                        <div class="col-sm-4">
                                            <la:text styleId="searchWordSearch" property="searchWord"
                                                     styleClass="form-control"/>
                                        </div>
                                    </div>
                                </c:if>
                                <div class="form-group row">
                                    <label for="requestedTimeRangeSearch" class="col-sm-2 text-sm-right col-form-label"><la:message
                                            key="labels.searchlog_requestedtime"/></label>
                                    <div class="col-sm-4">
                                        <la:text styleId="requestedTimeRangeSearch" property="requestedTimeRange"
                                                 styleClass="form-control datetimerange"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="logTypeSearch" class="col-sm-2 text-sm-right col-form-label"><la:message
                                            key="labels.searchlog_size"/></label>
                                    <div class="col-sm-4">
                                        <la:select styleId="size" property="size"
                                                   styleClass="form-control">
                                            <la:option value="25">25</la:option>
                                            <la:option value="50">50</la:option>
                                            <la:option value="100">100</la:option>
                                            <la:option value="500">500</la:option>
                                            <la:option value="1000">1000</la:option>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <div class="offset-sm-2 col-sm-10">
                                        <button type="submit" class="btn btn-primary" id="submit"
                                                name="search"
                                                value="<la:message key="labels.crud_button_search" />">
                                            <i class="fa fa-search" aria-hidden="true"></i>
                                            <la:message key="labels.crud_button_search"/>
                                        </button>
                                        <button type="submit" class="btn btn-default" name="reset"
                                                value="<la:message key="labels.crud_button_reset" />">
                                            <la:message key="labels.crud_button_reset"/>
                                        </button>
                                        <button type="submit" class="btn btn-default" name="download"
                                                value="<la:message key="labels.searchlog_download_csv" />">
                                            <i class="fa fa-download" aria-hidden="true"></i>
                                            <la:message key="labels.searchlog_download_csv"/>
                                        </button>
                                    </div>
                                </div>
                            </la:form>
                            <%-- List --%>
                            <c:if test="${searchLogPager.allRecordCount == 0}">
                                <div class="row top20">
                                    <div class="col-sm-12">
                                        <i class="fa fa-info-circle text-primary" aria-hidden="true"></i>
                                        <la:message key="labels.list_could_not_find_crud_table"/>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${searchLogPager.allRecordCount > 0}">
                                <div class="row top10">
                                    <div class="col-sm-12">
                                        <table class="table table-bordered table-striped" aria-label="<la:message key="labels.search_log_list" />">
                                            <thead>
                                            <tr>
                                                <th style="width: 25%"><la:message
                                                        key="labels.searchlog_requested_time"/></th>
                                                <th><la:message
                                                        key="labels.searchlog_log_message"/></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="data" varStatus="s"
                                                       items="${searchLogItems}">
                                                <tr
                                                        data-href="${contextPath}/admin/searchlog/details/4/${f:u(logType)}/${f:u(data.id)}">
                                                    <td>${f:h(data.requestedAt)}</td>
                                                    <td>${f:h(data.logMessage)}</td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <c:set var="pager" value="${searchLogPager}"
                                       scope="request"/>
                                <c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp"/>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </main>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}

