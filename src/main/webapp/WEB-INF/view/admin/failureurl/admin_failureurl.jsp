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
                            <la:message key="labels.failure_url_configuration"/>
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
                <div class="col-12">
                    <div class="card card-outline card-primary">
                        <div class="card-header">
                            <h3 class="card-title">
                                <la:message key="labels.failure_url_configuration"/>
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
                            <la:form action="/admin/failureurl/">
                                <div class="form-group row">
                                    <label for="url" class="col-sm-2 text-sm-right col-form-label"><la:message
                                            key="labels.failure_url_search_url"/></label>
                                    <div class="col-sm-10">
                                        <la:text styleId="url" property="url" styleClass="form-control"></la:text>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="errorCountMin" class="col-sm-2 text-sm-right col-form-label"><la:message
                                            key="labels.failure_url_search_error_count"/></label>
                                    <div class="form-inline col-sm-10">
                                        <div>
                                            <la:errors property="errorCountMin"/>
                                            <input type="number" name="errorCountMin" id="errorCountMin"
                                                   value="${f:h(errorCountMin)}" class="form-control"
                                                   min="0" max="100000">
                                        </div>
                                        <div class="mx-sm-2">-</div>
                                        <div>
                                            <la:errors property="errorCountMax"/>
                                            <input type="number" name="errorCountMax" id="errorCountMax"
                                                   value="${f:h(errorCountMax)}" class="form-control"
                                                   min="0" max="100000">
                                        </div>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="errorName" class="col-sm-2 text-sm-right col-form-label"><la:message
                                            key="labels.failure_url_search_error_name"/></label>
                                    <div class="col-sm-10">
                                        <la:text styleId="errorName" property="errorName"
                                                 styleClass="form-control"></la:text>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <div class="offset-sm-2 col-sm-10">
                                        <button type="submit" class="btn btn-primary" id="submit"
                                                name="search"
                                                value="<la:message key="labels.crud_button_search" />">
                                            <em class="fa fa-search"></em>
                                            <la:message key="labels.crud_button_search"/>
                                        </button>
                                        <button type="submit" class="btn btn-default" name="reset"
                                                value="<la:message key="labels.crud_button_reset" />">
                                            <la:message key="labels.crud_button_reset"/>
                                        </button>
                                    </div>
                                </div>
                            </la:form>
                            <div class="data-wrapper">
                                <%-- List --%>
                                <c:if test="${failureUrlPager.allRecordCount == 0}">
                                    <div class="row top20">
                                        <div class="col-sm-12">
                                            <em class="fa fa-info-circle text-primary"></em>
                                            <la:message key="labels.list_could_not_find_crud_table"/>
                                        </div>
                                    </div>
                                </c:if>
                                <c:if test="${failureUrlPager.allRecordCount > 0}">
                                    <div class="row">
                                        <div class="col-sm-12">
                                            <table class="table table-bordered table-striped">
                                                <thead>
                                                <tr>
                                                    <th><la:message key="labels.failure_url_url"/></th>
                                                    <th><la:message
                                                            key="labels.failure_url_search_error_name"/></th>
                                                    <th style="width: 15%"><la:message
                                                            key="labels.failure_url_last_access_time"/></th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <c:forEach var="data" varStatus="s"
                                                           items="${failureUrlItems}">
                                                    <tr data-href="${contextPath}/admin/failureurl/details/4/${f:u(data.id)}">
                                                        <td>${f:h(data.url)}</td>
                                                        <td>${f:h(data.errorName)}</td>
                                                        <td><fmt:formatDate
                                                                value="${fe:date(data.lastAccessTime)}"
                                                                pattern="yyyy-MM-dd'T'HH:mm:ss"/></td>
                                                    </tr>
                                                </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                    <c:set var="pager" value="${failureUrlPager}" scope="request"/>
                                    <c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp"/>
                                    <c:if test="${pager.currentPageNumber > pager.allPageCount}">
                                        <script>location.href = "${contextPath}/admin/failureurl/list/${pager.allPageCount}";</script>
                                    </c:if>
                                    <div class="row">
                                        <c:if test="${editable}">
                                            <la:form action="/admin/failureurl/">
                                                <div class="col-sm-12 center">
                                                    <button type="button" class="btn btn-danger"
                                                            data-toggle="modal" data-target="#confirmToDeleteAll">
                                                        <em class="fa fa-trash"></em>
                                                        <la:message key="labels.failure_url_delete_all_link"/>
                                                    </button>
                                                </div>
                                                <div class="modal fade" id="confirmToDeleteAll"
                                                     tabindex="-1" role="dialog">
                                                    <div class="modal-dialog">
                                                        <div class="modal-content bg-danger">
                                                            <div class="modal-header">
                                                                <h4 class="modal-title">
                                                                    <la:message
                                                                            key="labels.failure_url_delete_all_link"/>
                                                                </h4>
                                                                <button type="button" class="close" data-dismiss="modal"
                                                                        aria-label="Close">
                                                                    <span aria-hidden="true">×</span>
                                                                </button>
                                                            </div>
                                                            <div class="modal-body">
                                                                <p>
                                                                    <la:message
                                                                            key="labels.failure_url_delete_all_confirmation"/>
                                                                </p>
                                                            </div>
                                                            <div class="modal-footer justify-content-between">
                                                                <button type="button" class="btn btn-outline-light"
                                                                        data-dismiss="modal">
                                                                    <la:message
                                                                            key="labels.failure_url_delete_all_cancel"/>
                                                                </button>
                                                                <button type="submit" class="btn btn-outline-light"
                                                                        name="deleteall"
                                                                        value="<la:message key="labels.failure_url_delete_all_link" />">
                                                                    <em class="fa fa-trash"></em>
                                                                    <la:message
                                                                            key="labels.failure_url_delete_all_link"/>
                                                                </button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </la:form>
                                        </c:if>
                                    </div>
                                </c:if>
                            </div>
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

