<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.related_query_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="relatedQuery"/>
    </jsp:include>
    <div class="content-wrapper">

        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.related_query_configuration"/>
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
                        <!-- /.card-header -->
                        <div class="card-body">
                            <%-- Message --%>
                            <div>
                                <la:info id="msg" message="true">
                                    <div class="alert alert-info">${msg}</div>
                                </la:info>
                                <la:errors/>
                            </div>
                            <a role="button" data-toggle="collapse" href="#listSearchForm" aria-expanded="false"
                               aria-controls="listSearchForm"><i class="fas fa-search" aria-hidden="true"></i></a>
                            <div class="collapse" id="listSearchForm">
                                <la:form action="/admin/relatedquery/">
                                    <div class="form-group row">
                                        <label for="term" class="col-sm-2 text-sm-right col-form-label"><la:message
                                                key="labels.related_query_term"/></label>
                                        <div class="col-sm-10">
                                            <la:text styleId="term" property="term" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="queries" class="col-sm-2 text-sm-right col-form-label"><la:message
                                                key="labels.related_query_queries"/></label>
                                        <div class="col-sm-10">
                                            <la:text styleId="queries" property="queries" styleClass="form-control"/>
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
                            </div>
                            <%-- List --%>
                            <c:if test="${relatedQueryPager.allRecordCount == 0}">
                                <div class="row top10">
                                    <div class="col-sm-12">
                                        <em class="fa fa-info-circle text-primary"></em>
                                        <la:message key="labels.list_could_not_find_crud_table"/>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${relatedQueryPager.allRecordCount > 0}">
                                <div class="row top10">
                                    <div class="col-sm-12">
                                        <table class="table table-bordered table-striped">
                                            <thead>
                                            <tr>
                                                <th><la:message
                                                        key="labels.related_query_list_term"/></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="data" varStatus="s"
                                                       items="${relatedQueryItems}">
                                                <tr
                                                        data-href="${contextPath}/admin/relatedquery/details/4/${f:u(data.id)}">
                                                    <td>${f:h(data.term)}</td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <c:set var="pager" value="${relatedQueryPager}"
                                       scope="request"/>
                                <c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp"/>
                                <c:if test="${pager.currentPageNumber > pager.allPageCount}">
                                    <script>location.href = "${contextPath}/admin/relatedquery/list/${pager.allPageCount}";</script>
                                </c:if>
                            </c:if>
                        </div>
                        <!-- /.card-body -->
                    </div>
                    <!-- /.card -->
                </div>
            </div>
        </section>
    </div>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
