<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.failure_url_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="log"/>
        <jsp:param name="menuType" value="failureUrl"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.failure_url_title_details"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/failureurl/">
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
                                    <c:if test="${crudMode == 1}">
                                        <la:message key="labels.failure_url_link_create"/>
                                    </c:if>
                                    <c:if test="${crudMode == 2}">
                                        <la:message key="labels.failure_url_link_update"/>
                                    </c:if>
                                    <c:if test="${crudMode == 3}">
                                        <la:message key="labels.failure_url_link_delete"/>
                                    </c:if>
                                    <c:if test="${crudMode == 4}">
                                        <la:message key="labels.failure_url_link_details"/>
                                    </c:if>
                                </h3>
                                <div class="card-tools">
                                    <div class="btn-group">
                                        <la:link href="/admin/failureurl"
                                                 styleClass="btn btn-primary btn-xs">
                                            <em class="fa fa-th-list"></em>
                                            <la:message key="labels.failure_url_link_list"/>
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
                                <div class="table-responsive">
                                    <table class="table table-bordered">
                                        <tbody>
                                        <c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
                                            <tr>
                                                <th style="width: 25%"><la:message key="labels.failure_url_id"/></th>
                                                <td>${f:h(id)}<la:hidden property="id"/></td>
                                            </tr>
                                        </c:if>
                                        <tr>
                                            <th style="width: 25%"><la:message key="labels.failure_url_url"/></th>
                                            <td>${f:h(url)}
                                                <la:hidden property="url"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.failure_url_thread_name"/></th>
                                            <td>${f:h(threadName)}<la:hidden property="threadName"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.failure_url_error_name"/></th>
                                            <td>${f:h(errorName)}<la:hidden property="errorName"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.failure_url_error_log"/></th>
                                            <td>${f:br(f:nbsp(f:h(errorLog)))}
                                                <la:hidden
                                                        property="errorLog"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.failure_url_error_count"/></th>
                                            <td>${f:h(errorCount)}<la:hidden property="errorCount"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message
                                                    key="labels.failure_url_last_access_time"/></th>
                                            <td>${f:h(lastAccessTime)}<la:hidden
                                                    property="lastAccessTime"/></td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="card-footer">
                                <c:if test="${crudMode == 4}">
                                    <button type="submit" class="btn btn-default" name="back"
                                            value="<la:message key="labels.crud_button_back" />">
                                        <em class="fa fa-arrow-circle-left"></em>
                                        <la:message key="labels.crud_button_back"/>
                                    </button>
                                    <c:if test="${editable}">
                                        <button type="button" class="btn btn-danger" name="delete"
                                                data-toggle="modal" data-target="#confirmToDelete"
                                                value="<la:message key="labels.crud_button_delete" />">
                                            <em class="fa fa-trash"></em>
                                            <la:message key="labels.crud_button_delete"/>
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

