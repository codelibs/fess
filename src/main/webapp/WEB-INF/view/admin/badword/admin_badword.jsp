<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.bad_word_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="suggest"/>
        <jsp:param name="menuType" value="badWord"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.bad_word_configuration"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item active"><la:link href="/admin/badword">
                                <la:message key="labels.bad_word_link_list"/>
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
                                <la:message key="labels.bad_word_link_list"/>
                            </h3>
                            <div class="card-tools">
                                <div class="btn-group">
                                    <la:link href="/admin/badword"
                                             styleClass="btn btn-default btn-xs ">
                                        <em class="fa fa-th-list"></em>
                                        <la:message key="labels.bad_word_link_list"/>
                                    </la:link>
                                    <la:link href="createnew" styleClass="btn btn-success btn-xs ${f:h(editableClass)}">
                                        <em class="fa fa-plus"></em>
                                        <la:message key="labels.bad_word_link_create"/>
                                    </la:link>
                                    <la:link href="downloadpage"
                                             styleClass="btn btn-primary btn-xs">
                                        <em class="fa fa-download"></em>
                                        <la:message key="labels.bad_word_link_download"/>
                                    </la:link>
                                    <la:link href="uploadpage"
                                             styleClass="btn btn-success btn-xs ${f:h(editableClass)}">
                                        <em class="fa fa-upload"></em>
                                        <la:message key="labels.bad_word_link_upload"/>
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
                            <%-- List --%>
                            <c:if test="${badWordPager.allRecordCount == 0}">
                                <div class="row">
                                    <div class="col-sm-12">
                                        <em class="fa fa-info-circle text-primary"></em>
                                        <la:message key="labels.list_could_not_find_crud_table"/>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${badWordPager.allRecordCount > 0}">
                                <div class="row">
                                    <div class="col-sm-12">
                                        <table class="table table-bordered table-striped">
                                            <thead>
                                            <tr>
                                                <th><la:message
                                                        key="labels.bad_word_list_suggest_word"/></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="data" varStatus="s"
                                                       items="${badWordItems}">
                                                <tr
                                                        data-href="${contextPath}/admin/badword/details/4/${f:u(data.id)}">
                                                    <td>${f:h(data.suggestWord)}</td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <c:set var="pager" value="${badWordPager}"
                                       scope="request"/>
                                <c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp"/>
                                <c:if test="${pager.currentPageNumber > pager.allPageCount}">
                                    <script>location.href = "${contextPath}/admin/badword/list/${pager.allPageCount}";</script>
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

