<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
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
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.file_auth_title_details"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/fileauth/">
                <la:hidden property="crudMode"/>
                <c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
                    <la:hidden property="id"/>
                    <la:hidden property="versionNo"/>
                </c:if>
                <la:hidden property="createdBy"/>
                <la:hidden property="createdTime"/>
                <div class="row">
                    <div class="col-md-12">
                        <div
                                class="card card-outline <c:if test="${crudMode == 1 || crudMode == 2}">card-success</c:if><c:if test="${crudMode == 3}">card-danger</c:if><c:if test="${crudMode == 4}">card-primary</c:if>">
                                <%-- Card Header --%>
                            <div class="card-header">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
                            </div>
                                <%-- Card Body --%>
                            <div class="card-body">
                                    <%-- Message --%>
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="alert alert-info">${msg}</div>
                                    </la:info>
                                    <la:errors/>
                                </div>
                                    <%-- Form Fields --%>
                                <table class="table table-bordered">
                                    <tbody>
                                    <tr>
                                        <th style="width: 25%"><la:message
                                                key="labels.file_auth_hostname"/></th>
                                        <td>${f:h(hostname)}<la:hidden property="hostname"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.file_auth_port"/></th>
                                        <td>${f:h(port)}<la:hidden property="port"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.file_auth_scheme"/></th>
                                        <td><c:forEach var="item"
                                                       items="${protocolSchemeItems}">
                                            <c:if test="${protocolScheme==item.value}">${f:h(item.label)}</c:if>
                                        </c:forEach> <la:hidden property="protocolScheme"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message
                                                key="labels.file_auth_username"/></th>
                                        <td>${f:h(username)}<la:hidden property="username"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message
                                                key="labels.file_auth_password"/></th>
                                        <td><c:if test="${password!=''}">******</c:if> <la:hidden
                                                property="password"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message
                                                key="labels.file_auth_parameters"/></th>
                                        <td>${f:br(f:h(parameters))}<la:hidden
                                                property="parameters"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message
                                                key="labels.file_auth_file_crawling_config"/></th>
                                        <td><c:forEach var="item" items="${fileConfigItems}">
                                            <c:if test="${fileConfigId==item.value}">${f:h(item.label)}</c:if>
                                        </c:forEach> <la:hidden property="fileConfigId"/></td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="card-footer">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/buttons.jsp"></jsp:include>
                            </div>
                        </div>
                    </div>
                </div>
            </la:form>
        </section>
    </div>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
