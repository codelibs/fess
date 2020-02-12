<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.scheduledjob_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="scheduler"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.scheduledjob_configuration"/>
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
                    <div class="card card-outline card-primary">
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
                            <%-- List --%>
                            <c:if test="${schedulerPager.allRecordCount == 0}">
                                <div class="row top10">
                                    <div class="col-sm-12">
                                        <em class="fa fa-info-circle text-primary"></em>
                                        <la:message key="labels.list_could_not_find_crud_table"/>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${schedulerPager.allRecordCount > 0}">
                                <div class="row">
                                    <div class="col-sm-12">
                                        <table class="table table-bordered table-striped">
                                            <thead>
                                            <tr>
                                                <th><la:message key="labels.scheduledjob_name"/></th>
                                                <th class="text-center" style="width: 10%"><la:message
                                                        key="labels.scheduledjob_status"/></th>
                                                <th class="text-center" style="width: 10%"><la:message
                                                        key="labels.scheduledjob_target"/></th>
                                                <th><la:message
                                                        key="labels.scheduledjob_cronExpression"/></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="data" varStatus="s"
                                                       items="${scheduledJobItems}">
                                                <tr
                                                        data-href="${contextPath}/admin/scheduler/details/4/${f:u(data.id)}">
                                                    <td>${f:h(data.name)}</td>
                                                    <td class="text-center"><c:if test="${data.running}">
																	<span class="badge bg-success"><la:message
                                                                            key="labels.scheduledjob_running"/></span>
                                                    </c:if> <c:if test="${!data.running}">
                                                        <c:if test="${data.available}">
																		<span class="badge bg-primary"><la:message
                                                                                key="labels.scheduledjob_active"/></span>
                                                        </c:if>
                                                        <c:if test="${!data.available}">
																		<span class="badge badge-secondary"><la:message
                                                                                key="labels.scheduledjob_nojob"/></span>
                                                        </c:if>
                                                    </c:if></td>
                                                    <td class="text-center">${f:h(data.target)}</td>
                                                    <td>${f:h(data.cronExpression)}</td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <c:set var="pager" value="${schedulerPager}" scope="request"/>
                                <c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp"/>
                                <c:if test="${pager.currentPageNumber > pager.allPageCount}">
                                    <script>location.href = "${contextPath}/admin/scheduler/list/${pager.allPageCount}";</script>
                                </c:if>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
