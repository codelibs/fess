<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.pathmap_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="pathMapping"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.pathmap_title_details"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/pathmap/">
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
                                        <th style="width: 25%"><la:message key="labels.regex"/></th>
                                        <td>${f:h(regex)}<la:hidden property="regex"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.replacement"/></th>
                                        <td>${f:h(replacement)}<la:hidden
                                                property="replacement"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.processType"/></th>
                                        <td><c:if test="${processType=='C'}">
                                            <la:message key="labels.pathmap_pt_crawling"/>
                                        </c:if> <c:if test="${processType=='D'}">
                                            <la:message key="labels.pathmap_pt_displaying"/>
                                        </c:if> <c:if test="${processType=='B'}">
                                            <la:message key="labels.pathmap_pt_both"/>
                                        </c:if> <c:if test="${processType=='R'}">
                                            <la:message key="labels.pathmap_pt_stored"/>
                                        </c:if> <la:hidden property="processType"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.sortOrder"/></th>
                                        <td>${f:h(sortOrder)}<la:hidden property="sortOrder"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.userAgent"/></th>
                                        <td>${f:h(userAgent)}<la:hidden property="userAgent"/></td>
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
