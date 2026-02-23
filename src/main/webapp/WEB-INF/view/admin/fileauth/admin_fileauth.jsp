<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.file_auth_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="fileAuthentication"/>
    </jsp:include>
    <main class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.file_auth_configuration"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="row">
                <div class="col-md-12">
                    <c:if test="${!displayCreateLink}">
                        <la:link href="/admin/fileconfig/" styleClass="btn btn-primary ${f:h(editableClass)}">
                            <i class="fa fa-arrow-circle-right" aria-hidden="true"></i>
                            <la:message key="labels.file_auth_create_file_config"/>
                        </la:link>
                    </c:if>
                    <c:if test="${displayCreateLink}">
                        <div class="card card-outline card-primary">
                            <div class="card-header">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
                            </div>
                            <div class="card-body">
                                    <%-- Message --%>
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="alert alert-success">${msg}</div>
                                    </la:info>
                                    <la:errors/>
                                </div>
                                    <%-- List --%>
                                <c:if test="${fileAuthPager.allRecordCount == 0}">
                                    <div class="row top10"><div class="col-sm-12 empty-list-placeholder"><i class="fa fa-info-circle text-primary" aria-hidden="true"></i>
                                            <la:message key="labels.list_could_not_find_crud_table"/>
                                        </div>
                                    </div>
                                </c:if>
                                <c:if test="${fileAuthPager.allRecordCount > 0}">
                                    <div class="row top10">
                                        <div class="col-sm-12">
                                            <table class="table table-bordered table-striped" aria-label="<la:message key="labels.file_auth_list" />">
                                                <thead>
                                                <tr>
                                                    <th><la:message key="labels.file_auth_list_hostname"/></th>
                                                    <th><la:message
                                                            key="labels.file_auth_list_file_crawling_config"/></th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <c:forEach var="data" varStatus="s"
                                                           items="${fileAuthenticationItems}">
                                                    <tr
                                                            data-href="${contextPath}/admin/fileauth/details/4/${f:u(data.id)}">
                                                        <td><c:if
                                                                test="${data.hostname==null||data.hostname==''}">
                                                            <la:message key="labels.file_auth_any"/>
                                                        </c:if> ${f:h(data.hostname)}: <c:if
                                                                test="${data.port==null||data.port==''}">
                                                            <la:message key="labels.file_auth_any"/>
                                                        </c:if> ${f:h(data.port)}</td>
                                                        <td>${f:h(data.fileConfig.name)}</td>
                                                    </tr>
                                                </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                    <c:set var="pager" value="${fileAuthPager}" scope="request"/>
                                    <c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp"/>
                                    <c:if test="${pager.currentPageNumber > pager.allPageCount}">
                                        <script>location.href = "${contextPath}/admin/fileauth/list/${pager.allPageCount}";</script>
                                    </c:if>
                                </c:if>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </section>
    </main>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}
