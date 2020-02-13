<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.joblog_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="log"/>
        <jsp:param name="menuType" value="jobLog"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.joblog_configuration"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item active"><la:link href="/admin/joblog">
                                <la:message key="labels.joblog_link_list"/>
                            </la:link></li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="row">
                <div class="col-md-12">
                    <div class="card card-outline card-primary">
                        <div class="card-header">
                            <h3 class="card-title">
                                <la:message key="labels.joblog_link_list"/>
                            </h3>
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
                            <c:if test="${jobLogPager.allRecordCount == 0}">
                                <div class="row top10">
                                    <div class="col-sm-12">
                                        <em class="fa fa-info-circle text-primary"></em>
                                        <la:message key="labels.list_could_not_find_crud_table"/>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${jobLogPager.allRecordCount > 0}">
                                <div class="row">
                                    <div class="col-sm-12">
                                        <table class="table table-bordered table-striped">
                                            <thead>
                                            <tr>
                                                <th><la:message key="labels.joblog_jobName"/></th>
                                                <th class="text-center" style="width: 15%"><la:message
                                                        key="labels.joblog_jobStatus"/></th>
                                                <th style="width: 25%"><la:message
                                                        key="labels.joblog_startTime"/></th>
                                                <th style="width: 25%"><la:message
                                                        key="labels.joblog_endTime"/></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="data" varStatus="s" items="${jobLogItems}">
                                                <tr
                                                        data-href="${contextPath}/admin/joblog/details/4/${f:u(data.id)}">
                                                    <td>${f:h(data.jobName)}</td>
                                                    <td class="text-center"><c:choose>
                                                        <c:when test="${data.jobStatus == 'ok'}">
																		<span class="badge bg-primary"><la:message
                                                                                key="labels.joblog_status_ok"/></span>
                                                        </c:when>
                                                        <c:when test="${data.jobStatus == 'fail'}">
																		<span class="badge bg-danger"><la:message
                                                                                key="labels.joblog_status_fail"/></span>
                                                        </c:when>
                                                        <c:when test="${data.jobStatus == 'running'}">
																		<span class="badge bg-success"><la:message
                                                                                key="labels.joblog_status_running"/></span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-gray">${f:h(data.jobStatus)}</span>
                                                        </c:otherwise>
                                                    </c:choose></td>
                                                    <td><fmt:formatDate
                                                            value="${fe:date(data.startTime)}"
                                                            pattern="yyyy-MM-dd'T'HH:mm:ss"/></td>
                                                    <td><c:if test="${data.endTime!=null}">
                                                        <fmt:formatDate value="${fe:date(data.endTime)}"
                                                                        pattern="yyyy-MM-dd'T'HH:mm:ss"/>
                                                    </c:if> <c:if test="${data.endTime==null}">
                                                        <la:message key="labels.none"/>
                                                    </c:if></td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <c:set var="pager" value="${jobLogPager}" scope="request"/>
                                <c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp"/>
                                <c:if test="${pager.currentPageNumber > pager.allPageCount}">
                                    <script>location.href = "${contextPath}/admin/joblog/list/${pager.allPageCount}";</script>
                                </c:if>
                                <div class="row">
                                    <c:if test="${editable}">
                                        <la:form action="/admin/joblog/">
                                            <div class="col-sm-12 center">
                                                <button type="button" class="btn btn-danger"
                                                        data-toggle="modal" data-target="#confirmToDeleteAll">
                                                    <em class="fa fa-trash"></em>
                                                    <la:message key="labels.joblog_delete_all_link"/>
                                                </button>
                                            </div>
                                            <div class="modal fade" id="confirmToDeleteAll"
                                                 tabindex="-1" role="dialog">
                                                <div class="modal-dialog">
                                                    <div class="modal-content bg-danger">
                                                        <div class="modal-header">
                                                            <h4 class="modal-title">
                                                                <la:message
                                                                        key="labels.joblog_delete_all_link"/>
                                                            </h4>
                                                            <button type="button" class="close" data-dismiss="modal"
                                                                    aria-label="Close">
                                                                <span aria-hidden="true">×</span>
                                                            </button>
                                                        </div>
                                                        <div class="modal-body">
                                                            <p>
                                                                <la:message
                                                                        key="labels.joblog_delete_all_confirmation"/>
                                                            </p>
                                                        </div>
                                                        <div class="modal-footer justify-content-between">
                                                            <button type="button" class="btn btn-outline-light"
                                                                    data-dismiss="modal">
                                                                <la:message
                                                                        key="labels.joblog_delete_all_cancel"/>
                                                            </button>
                                                            <button type="submit" class="btn btn-outline-light"
                                                                    name="deleteall"
                                                                    value="<la:message key="labels.joblog_delete_all_link" />">
                                                                <em class="fa fa-trash"></em>
                                                                <la:message
                                                                        key="labels.joblog_delete_all_link"/>
                                                            </button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </la:form></c:if>
                                </div>
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

