<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.key_match_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="keyMatch"/>
    </jsp:include>
    <div class="content-wrapper">

        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.key_match_configuration"/>
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
                                <la:form action="/admin/keymatch/">
                                    <div class="form-group row">
                                        <label for="term" class="col-sm-2 col-form-label"><la:message
                                                key="labels.key_match_term"/></label>
                                        <div class="col-sm-10">
                                            <la:text styleId="term" property="term" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="query" class="col-sm-2 col-form-label"><la:message
                                                key="labels.key_match_query"/></label>
                                        <div class="col-sm-10">
                                            <la:text styleId="query" property="query" styleClass="form-control"/>
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
                            <c:if test="${keyMatchPager.allRecordCount == 0}">
                                <div class="row top10">
                                    <div class="col-sm-12">
                                        <em class="fa fa-info-circle text-primary"></em>
                                        <la:message key="labels.list_could_not_find_crud_table"/>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${keyMatchPager.allRecordCount > 0}">
                                <div class="row top10">
                                    <div class="col-sm-12">
                                        <table class="table table-bordered table-striped">
                                            <thead>
                                            <tr>
                                                <th><la:message key="labels.key_match_list_term"/></th>
                                                <th><la:message key="labels.key_match_list_query"/></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="data" varStatus="s"
                                                       items="${keyMatchItems}">
                                                <tr
                                                        data-href="${contextPath}/admin/keymatch/details/4/${f:u(data.id)}">
                                                    <td>${f:h(data.term)}</td>
                                                    <td>${f:h(data.query)}</td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <div class="row">
                                        <%-- Paging Info --%>
                                    <div class="col-sm-2">
                                        <la:message key="labels.pagination_page_guide_msg"
                                                    arg0="${f:h(keyMatchPager.currentPageNumber)}"
                                                    arg1="${f:h(keyMatchPager.allPageCount)}"
                                                    arg2="${f:h(keyMatchPager.allRecordCount)}"/>
                                    </div>
                                        <%-- Paging Navigation --%>
                                    <div class="col-sm-10">
                                        <ul class="pagination pagination-sm m-0 float-right">
                                            <c:if test="${keyMatchPager.existPrePage}">
                                                <li class="page-item"><la:link
                                                        styleClass="page-link"
                                                        href="list/${keyMatchPager.currentPageNumber - 1}">
                                                    <la:message key="labels.prev_page"/>
                                                </la:link></li>
                                            </c:if>
                                            <c:if test="${!keyMatchPager.existPrePage}">
                                                <li class="page-item disabled"><a
                                                        class="page-link" href="#"><la:message
                                                        key="labels.prev_page"/></a></li>
                                            </c:if>
                                            <c:forEach var="p" varStatus="s"
                                                       items="${keyMatchPager.pageNumberList}">
                                                <li
                                                        <c:if test="${p == keyMatchPager.currentPageNumber}">class="page-item active"</c:if>>
                                                    <la:link
                                                            href="list/${p}">${p}</la:link></li>
                                            </c:forEach>
                                            <c:if test="${keyMatchPager.existNextPage}">
                                                <li class="page-item"><la:link
                                                        styleClass="page-link"
                                                        href="list/${keyMatchPager.currentPageNumber + 1}">
                                                    <la:message key="labels.next_page"/>
                                                </la:link></li>
                                            </c:if>
                                            <c:if test="${!keyMatchPager.existNextPage}">
                                                <li class="page-item disabled"><a
                                                        class="page-link" href="#"><la:message
                                                        key="labels.next_page"/></a></li>
                                            </c:if>
                                        </ul>
                                    </div>
                                </div>
                                <c:if test="${keyMatchPager.currentPageNumber > keyMatchPager.allPageCount}">
                                    <script>location.href = "${contextPath}/admin/keymatch/list/${keyMatchPager.allPageCount}";</script>
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

