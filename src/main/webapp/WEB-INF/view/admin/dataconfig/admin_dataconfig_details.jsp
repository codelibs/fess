<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.data_crawling_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="dataConfig"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.data_crawling_title_details"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/dataconfig/">
                <la:hidden property="crudMode"/>
                <c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
                    <la:hidden property="id"/>
                    <la:hidden property="versionNo"/>
                </c:if>
                <la:hidden property="createdBy"/>
                <la:hidden property="createdTime"/>
                <la:hidden property="sortOrder"/>
                <div class="row">
                    <div class="col-md-12">
                        <div
                                class="card card-outline <c:if test="${crudMode == 1 || crudMode == 2}">card-success</c:if><c:if test="${crudMode == 3}">card-danger</c:if><c:if test="${crudMode == 4}">card-primary</c:if>">
                            <div class="card-header">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
                            </div>
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
                                    <c:if test="${id != null}">
                                        <tr>
                                            <th style="width: 25%"><la:message key="labels.id"/></th>
                                            <td>${f:h(id)}</td>
                                        </tr>
                                    </c:if>
                                    <tr>
                                        <th style="width: 25%"><la:message key="labels.name"/></th>
                                        <td>${f:h(name)}<la:hidden property="name"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.handler_name"/></th>
                                        <td><c:forEach var="hn" varStatus="s"
                                                       items="${handlerNameItems}">
                                            <c:if test="${handlerName==f:u(hn.value)}">
                                                ${f:h(hn.label)}<br/>
                                            </c:if>
                                        </c:forEach> <la:hidden property="handlerName"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.handler_parameter"/></th>
                                        <td>${f:br(f:h(handlerParameter))}<la:hidden
                                                property="handlerParameter"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.handler_script"/></th>
                                        <td>${f:br(f:h(handlerScript))}<la:hidden
                                                property="handlerScript"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.boost"/></th>
                                        <td>${f:h(boost)}<la:hidden property="boost"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.permissions"/></th>
                                        <td>${f:br(f:h(permissions))}<la:hidden
                                                property="permissions"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.virtual_hosts"/></th>
                                        <td>${f:br(f:h(virtualHosts))}<la:hidden
                                                property="virtualHosts"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.available"/></th>
                                        <td><la:hidden property="available"/> <c:if
                                                test="${available=='true'}">
                                            <la:message key="labels.enabled"/>
                                        </c:if> <c:if test="${available=='false'}">
                                            <la:message key="labels.disabled"/>
                                        </c:if></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.description"/></th>
                                        <td>${f:br(f:h(description))}<la:hidden
                                                property="description"/></td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="card-footer">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/buttons.jsp"></jsp:include>
                                <c:if test="${fe:permission('admin-scheduler')}">
                                    <la:link styleClass="btn btn-success"
                                             href="/admin/scheduler/createnewjob/data_crawling/${f:u(id)}/${fe:base64(name)}">
                                        <em class="fa fa-plus-circle"></em>
                                        <la:message key="labels.data_crawling_button_create_job"/>
                                    </la:link>
                                </c:if>
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
