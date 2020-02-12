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
                            <la:message key="labels.scheduledjob_title_details"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/scheduler/">
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
                                        <th style="width: 25%"><la:message key="labels.scheduledjob_name"/></th>
                                        <td>${f:h(name)}<la:hidden property="name"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.scheduledjob_target"/></th>
                                        <td>${f:h(target)}<la:hidden property="target"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message
                                                key="labels.scheduledjob_cronExpression"/></th>
                                        <td>${f:h(cronExpression)}<la:hidden
                                                property="cronExpression"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.scheduledjob_scriptType"/></th>
                                        <td>${f:h(scriptType)}<la:hidden property="scriptType"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.scheduledjob_scriptData"/></th>
                                        <td>${f:br(f:h(scriptData))}<la:hidden
                                                property="scriptData"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.scheduledjob_jobLogging"/></th>
                                        <td><c:if test="${jobLogging=='on'}">
                                            <la:message key="labels.enabled"/>
                                        </c:if> <c:if test="${jobLogging!='on'}">
                                            <la:message key="labels.disabled"/>
                                        </c:if> <la:hidden property="jobLogging"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.scheduledjob_crawler"/></th>
                                        <td><c:if test="${crawler=='on'}">
                                            <la:message key="labels.enabled"/>
                                        </c:if> <c:if test="${crawler!='on'}">
                                            <la:message key="labels.disabled"/>
                                        </c:if> <la:hidden property="crawler"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.scheduledjob_status"/></th>
                                        <td><c:if test="${available=='on'}">
                                            <la:message key="labels.enabled"/>
                                        </c:if> <c:if test="${available!='on'}">
                                            <la:message key="labels.disabled"/>
                                        </c:if> <la:hidden property="available"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.sortOrder"/></th>
                                        <td>${f:h(sortOrder)}<la:hidden property="sortOrder"/></td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="card-footer">
                                <button type="submit" class="btn btn-default" name="list" value="back">
                                    <em class="fa fa-arrow-circle-left"></em>
                                    <la:message key="labels.crud_button_back"/>
                                </button>
                                <c:if test="${editable}">
                                    <button type="submit" class="btn btn-primary" name="edit"
                                            value="<la:message key="labels.crud_button_edit" />">
                                        <em class="fa fa-pencil-alt"></em>
                                        <la:message key="labels.crud_button_edit"/>
                                    </button>
                                    <c:if test="${!running and !systemJobId}">
                                        <button type="button" class="btn btn-danger" name="delete"
                                                data-toggle="modal" data-target="#confirmToDelete"
                                                value="<la:message key="labels.crud_button_delete" />">
                                            <em class="fa fa-trash"></em>
                                            <la:message key="labels.crud_button_delete"/>
                                        </button>
                                        <div class="modal fade" id="confirmToDelete" tabindex="-1"
                                             role="dialog">
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
                                    <c:if test="${running}">
                                        <button type="submit" class="btn btn-danger" name="stop"
                                                value="<la:message key="labels.scheduledjob_button_stop" />">
                                            <em class="fa fa-stop"></em>
                                            <la:message key="labels.scheduledjob_button_stop"/>
                                        </button>
                                    </c:if>
                                    <c:if test="${!running && enabled}">
                                        <button type="submit" class="btn btn-success" name="start"
                                                value="<la:message key="labels.scheduledjob_button_start" />">
                                            <em class="fa fa-play-circle"></em>
                                            <la:message key="labels.scheduledjob_button_start"/>
                                        </button>
                                    </c:if>
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
