<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.dict_synonym_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="dict"/>
    </jsp:include>
    <div class="content-wrapper">

        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.dict_synonym_title"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><la:link href="/admin/dict">
                                <la:message key="labels.dict_list_link"/>
                            </la:link></li>
                            <li class="breadcrumb-item"><la:message key="labels.dict_synonym_list_link"/></li>
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
                                <la:message key="labels.dict_synonym_list_link"/>
                            </h3>
                            <div class="card-tools">
                                <div class="btn-group">
                                    <la:link href="/admin/dict" styleClass="btn btn-default btn-xs">
                                        <em class="fa fa-book"></em>
                                        <la:message key="labels.dict_list_link"/>
                                    </la:link>
                                    <la:link href="list/1?dictId=${f:u(dictId)}"
                                             styleClass="btn btn-primary btn-xs">
                                        <em class="fa fa-th-list"></em>
                                        <la:message key="labels.dict_synonym_list_link"/>
                                    </la:link>
                                    <la:link href="createnew/${f:u(dictId)}"
                                             styleClass="btn btn-success btn-xs ${f:h(editableClass)}">
                                        <em class="fa fa-plus"></em>
                                        <la:message key="labels.dict_synonym_link_create"/>
                                    </la:link>
                                    <la:link href="downloadpage/${f:u(dictId)}"
                                             styleClass="btn btn-primary btn-xs">
                                        <em class="fa fa-download"></em>
                                        <la:message key="labels.dict_synonym_link_download"/>
                                    </la:link>
                                    <la:link href="uploadpage/${f:u(dictId)}"
                                             styleClass="btn btn-success btn-xs ${f:h(editableClass)}">
                                        <em class="fa fa-upload"></em>
                                        <la:message key="labels.dict_synonym_link_upload"/>
                                    </la:link>
                                </div>
                            </div>
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
                            <%-- List --%>
                            <c:if test="${synonymPager.allRecordCount == 0}">
                                <div class="row top10">
                                    <div class="col-sm-12">
                                        <em class="fa fa-info-circle text-primary"></em>
                                        <la:message key="labels.list_could_not_find_crud_table"/>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${synonymPager.allRecordCount > 0}">
                                <div class="row">
                                    <div class="col-sm-12">
                                        <table class="table table-bordered table-striped">
                                            <thead>
                                            <tr>
                                                <th><la:message key="labels.dict_synonym_source"/></th>
                                                <th><la:message key="labels.dict_synonym_target"/></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="data" varStatus="s"
                                                       items="${synonymItemItems}">
                                                <tr
                                                        data-href="${contextPath}/admin/dict/synonym/details/${f:u(dictId)}/4/${f:u(data.id)}">
                                                    <td>${f:h(data.inputs)}</td>
                                                    <td>${f:h(data.outputs)}</td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <c:set var="pager" value="${synonymPager}" scope="request"/>
                                <div class="row">
                                    <div class="col-sm-2">
                                        <la:message key="labels.pagination_page_guide_msg"
                                                    arg0="${f:h(pager.currentPageNumber)}"
                                                    arg1="${f:h(pager.allPageCount)}"
                                                    arg2="${f:h(pager.allRecordCount)}"/>
                                    </div>
                                    <div class="col-sm-10">
                                        <ul class="pagination pagination-sm m-0 float-right">
                                            <c:if test="${pager.existPrePage}">
                                                <li class="page-item"><la:link
                                                        styleClass="page-link"
                                                        href="list/${pager.currentPageNumber - 1}?dictId=${f:u(dictId)}">
                                                    <la:message key="labels.prev_page"/>
                                                </la:link></li>
                                            </c:if>
                                            <c:if test="${!pager.existPrePage}">
                                                <li class="page-item disabled"><a
                                                        class="page-link" href="#"><la:message
                                                        key="labels.prev_page"/></a></li>
                                            </c:if>
                                            <c:forEach var="p" varStatus="s"
                                                       items="${pager.pageNumberList}">
                                                <li
                                                        <c:if test="${p == pager.currentPageNumber}">class="active"</c:if>>
                                                    <la:link
                                                            styleClass="page-link"
                                                            href="list/${p}?dictId=${f:u(dictId)}">${p}</la:link></li>
                                            </c:forEach>
                                            <c:if test="${pager.existNextPage}">
                                                <li class="page-item"><la:link
                                                        styleClass="page-link"
                                                        href="list/${pager.currentPageNumber + 1}?dictId=${f:u(dictId)}">
                                                    <la:message key="labels.next_page"/>
                                                </la:link></li>
                                            </c:if>
                                            <c:if test="${!pager.existNextPage}">
                                                <li class="page-item disabled"><a
                                                        class="page-link" href="#"><la:message
                                                        key="labels.next_page"/></a></li>
                                            </c:if>
                                        </ul>
                                    </div>
                                </div>
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
