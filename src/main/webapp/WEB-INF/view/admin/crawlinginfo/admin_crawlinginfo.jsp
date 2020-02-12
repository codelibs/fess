<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.crawling_info_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="log"/>
        <jsp:param name="menuType" value="crawlingInfo"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.crawling_info_configuration"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item active"><la:link href="/admin/crawlinginfo">
                                <la:message key="labels.crawling_info_title"/>
                            </la:link></li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="row">
                <div class="col-sm-12">
                    <div class="card card-outline card-primary">
                        <div class="card-header">
                            <h3 class="card-title">
                                <la:message key="labels.crawling_info_title"/>
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
                            <div class="row">
                                <div class="col-sm-12">
                                    <la:form styleClass="form-inline"
                                             action="/admin/crawlinginfo/">
                                        <div class="form-group">
                                            <c:set var="ph_session_id">
                                                <la:message key="labels.crawling_info_session_id_search"/>
                                            </c:set>
                                            <la:text styleId="sessionIdSearchBtn" property="sessionId"
                                                     styleClass="form-control" placeholder="${ph_session_id}"></la:text>
                                        </div>
                                        <div class="form-group ml-sm-2">
                                            <button type="submit" class="btn btn-primary" id="submit"
                                                    name="search"
                                                    value="<la:message key="labels.crawling_info_search" />">
                                                <em class="fa fa-search"></em>
                                                <la:message key="labels.crawling_info_search"/>
                                            </button>
                                        </div>
                                        <div class="form-group ml-sm-2">
                                            <button type="submit" class="btn btn-default" name="reset"
                                                    value="<la:message key="labels.crawling_info_reset" />">
                                                <la:message key="labels.crawling_info_reset"/>
                                            </button>
                                        </div>
                                    </la:form>
                                </div>
                            </div>
                            <%-- List --%>
                            <c:if test="${crawlingInfoPager.allRecordCount == 0}">
                                <div class="row top20">
                                    <div class="col-sm-12">
                                        <em class="fa fa-info-circle text-primary"></em>
                                        <la:message key="labels.list_could_not_find_crud_table"/>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${crawlingInfoPager.allRecordCount > 0}">
                                <div class="row top10">
                                    <div class="col-sm-12">
                                        <table class="table table-bordered table-striped">
                                            <thead>
                                            <tr>
                                                <th><la:message
                                                        key="labels.crawling_info_session_id"/></th>
                                                <th><la:message
                                                        key="labels.crawling_info_created_time"/></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="data" varStatus="s"
                                                       items="${crawlingInfoItems}">
                                                <tr
                                                        data-href="${contextPath}/admin/crawlinginfo/details/4/${f:u(data.id)}">
                                                    <td>${f:h(data.sessionId)}</td>
                                                    <td><fmt:formatDate
                                                            value="${fe:date(data.createdTime)}"
                                                            pattern="yyyy-MM-dd'T'HH:mm:ss"/></td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <c:set var="pager" value="${crawlingInfoPager}"
                                       scope="request"/>
                                <c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp"/>
                                <c:if test="${pager.currentPageNumber > pager.allPageCount}">
                                    <script>location.href = "${contextPath}/admin/crawlinginfo/list/${pager.allPageCount}";</script>
                                </c:if>
                                <div class="row">
                                    <c:if test="${editable}">
                                        <la:form action="/admin/crawlinginfo/">
                                            <div class="col-sm-12 center">
                                                <button type="button" class="btn btn-danger"
                                                        data-toggle="modal" data-target="#confirmToDeleteAll">
                                                    <em class="fa fa-trash"></em>
                                                    <la:message key="labels.crawling_info_delete_all_link"/>
                                                </button>
                                            </div>
                                            <div class="modal fade" id="confirmToDeleteAll"
                                                 tabindex="-1" role="dialog">
                                                <div class="modal-dialog">
                                                    <div class="modal-content bg-danger">
                                                        <div class="modal-header">
                                                            <h4 class="modal-title">
                                                                <la:message
                                                                        key="labels.crawling_info_delete_all_link"/>
                                                            </h4>
                                                            <button type="button" class="close" data-dismiss="modal"
                                                                    aria-label="Close">
                                                                <span aria-hidden="true">×</span>
                                                            </button>
                                                        </div>
                                                        <div class="modal-body">
                                                            <p>
                                                                <la:message
                                                                        key="labels.crawling_info_delete_all_confirmation"/>
                                                            </p>
                                                        </div>
                                                        <div class="modal-footer justify-content-between">
                                                            <button type="button" class="btn btn-outline-light"
                                                                    data-dismiss="modal">
                                                                <la:message
                                                                        key="labels.crawling_info_delete_all_cancel"/>
                                                            </button>
                                                            <button type="submit" class="btn btn-outline-light"
                                                                    name="deleteall"
                                                                    value="<la:message key="labels.crawling_info_delete_all_link" />">
                                                                <em class="fa fa-trash"></em>
                                                                <la:message
                                                                        key="labels.crawling_info_delete_all_link"/>
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
        </section>
    </div>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>

