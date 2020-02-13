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
                            <la:message key="labels.joblog_title_details"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><la:link href="/admin/crawlinginfo">
                                <la:message key="labels.joblog_link_list"/>
                            </la:link></li>
                            <li class="breadcrumb-item active"><la:message
                                    key="labels.joblog_link_details"/></li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/joblog/">
                <la:hidden property="crudMode"/>
                <c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
                    <la:hidden property="id"/>
                </c:if>
                <div class="row">
                    <div class="col-md-12">
                        <div
                                class="card card-outline <c:if test="${crudMode == 1 || crudMode == 2}">card-success</c:if><c:if test="${crudMode == 3}">card-danger</c:if><c:if test="${crudMode == 4}">card-primary</c:if>">
                            <div class="card-header">
                                <h3 class="card-title">
                                    <la:message key="labels.joblog_link_details"/>
                                </h3>
                                <div class="card-tools">
                                    <div class="btn-group">
                                        <la:link href="/admin/joblog"
                                                 styleClass="btn btn-primary btn-xs">
                                            <em class="fa fa-th-list"></em>
                                            <la:message key="labels.joblog_link_list"/>
                                        </la:link>
                                    </div>
                                </div>
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
                                    <tr>
                                        <th style="width: 25%"><la:message
                                                key="labels.joblog_jobName"/></th>
                                        <td>${f:h(jobName)}<la:hidden property="jobName"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.joblog_jobStatus"/></th>
                                        <td>${f:h(jobStatus)}<la:hidden property="jobStatus"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.joblog_target"/></th>
                                        <td>${f:h(target)}<la:hidden property="target"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.joblog_startTime"/></th>
                                        <td>${f:h(startTime)}<la:hidden property="startTime"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.joblog_endTime"/></th>
                                        <td>${f:h(endTime)}<la:hidden property="endTime"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.joblog_scriptType"/></th>
                                        <td>${f:h(scriptType)}<la:hidden property="scriptType"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.joblog_scriptData"/></th>
                                        <td>${f:br(f:h(scriptData))}<la:hidden
                                                property="scriptData"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.joblog_scriptResult"/></th>
                                        <td>${f:br(f:h(scriptResult))}<la:hidden
                                                property="scriptResult"/></td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="card-footer">
                                <button type="submit" class="btn btn-default" name="back"
                                        value="<la:message key="labels.joblog_button_back" />">
                                    <em class="fa fa-arrow-circle-left"></em>
                                    <la:message key="labels.joblog_button_back"/>
                                </button>
                                <c:if test="${editable}">
                                    <button type="button" class="btn btn-danger" name="delete"
                                            data-toggle="modal" data-target="#confirmToDelete"
                                            value="<la:message key="labels.joblog_button_delete" />">
                                        <em class="fa fa-trash"></em>
                                        <la:message key="labels.joblog_button_delete"/>
                                    </button>
                                    <div class="modal fade" id="confirmToDelete"
                                         tabindex="-1" role="dialog">
                                        <div class="modal-dialog">
                                            <div class="modal-content bg-danger">
                                                <div class="modal-header">
                                                    <h4 class="modal-title">
                                                        <la:message key="labels.crud_title_delete"/>
                                                    </h4>
                                                    <button type="button" class="close" data-dismiss="modal"
                                                            aria-label="Close">
                                                        <span aria-hidden="true">×</span>
                                                    </button>
                                                </div>
                                                <div class="modal-body">
                                                    <p>
                                                        <la:message key="labels.crud_delete_confirmation"/>
                                                    </p>
                                                </div>
                                                <div class="modal-footer justify-content-between">
                                                    <button type="button" class="btn btn-outline-light"
                                                            data-dismiss="modal">
                                                        <la:message key="labels.crud_button_cancel"/>
                                                    </button>
                                                    <button type="submit" class="btn btn-outline-light"
                                                            name="delete"
                                                            value="<la:message key="labels.crud_button_delete" />">
                                                        <em class="fa fa-trash"></em>
                                                        <la:message key="labels.crud_button_delete"/>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
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

