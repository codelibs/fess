<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.dict_synonym_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="dict"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.dict_synonym_title"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
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
            <div class="fads-row">
                <div class="fads-col-md-12">
                    <div class="fads-card">
                        <div class="fads-card-header">
                            <h3 class="fads-card-title">
                                <la:message key="labels.dict_synonym_list_link"/>
                            </h3>
                            <div class="fads-card-tools">
                                <div class="btn-group">
                                    <la:link href="/admin/dict" styleClass="fads-btn fads-btn-default fads-btn-compact">
                                        <i class="fa fa-book" aria-hidden="true"></i>
                                        <la:message key="labels.dict_list_link"/>
                                    </la:link>
                                    <la:link href="list/1?dictId=${f:u(dictId)}"
                                             styleClass="fads-btn fads-btn-primary fads-btn-compact">
                                        <i class="fa fa-th-list" aria-hidden="true"></i>
                                        <la:message key="labels.dict_synonym_list_link"/>
                                    </la:link>
                                    <la:link href="createnew/${f:u(dictId)}"
                                             styleClass="fads-btn fads-btn-success fads-btn-compact ${f:h(editableClass)}">
                                        <i class="fa fa-plus" aria-hidden="true"></i>
                                        <la:message key="labels.dict_synonym_link_create"/>
                                    </la:link>
                                    <la:link href="downloadpage/${f:u(dictId)}"
                                             styleClass="fads-btn fads-btn-primary fads-btn-compact">
                                        <i class="fa fa-download" aria-hidden="true"></i>
                                        <la:message key="labels.dict_synonym_link_download"/>
                                    </la:link>
                                    <la:link href="uploadpage/${f:u(dictId)}"
                                             styleClass="fads-btn fads-btn-success fads-btn-compact ${f:h(editableClass)}">
                                        <i class="fa fa-upload" aria-hidden="true"></i>
                                        <la:message key="labels.dict_synonym_link_upload"/>
                                    </la:link>
                                </div>
                            </div>
                        </div>
                        <div class="fads-card-body">
                            <%-- Message --%>
                            <div>
                                <la:info id="msg" message="true">
                                    <div class="fads-banner fads-banner-info">${msg}</div>
                                </la:info>
                                <la:errors/>
                            </div>
                            <%-- List --%>
                            <c:if test="${synonymPager.allRecordCount == 0}">
                                <div class="fads-row top10">
                                    <div class="fads-col-sm-12">
                                        <i class="fa fa-info-circle text-primary" aria-hidden="true"></i>
                                        <la:message key="labels.list_could_not_find_crud_table"/>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${synonymPager.allRecordCount > 0}">
                                <div class="fads-row">
                                    <div class="fads-col-sm-12">
                                        <table class="fads-table" aria-label="<la:message key="labels.dict_synonym_list" />">
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
                                <div class="fads-row">
                                    <div class="col-sm-2">
                                        <la:message key="labels.pagination_page_guide_msg"
                                                    arg0="${f:h(pager.currentPageNumber)}"
                                                    arg1="${f:h(pager.allPageCount)}"
                                                    arg2="${f:h(pager.allRecordCount)}"/>
                                    </div>
                                    <div class="col-sm-10">
                                        <ul class="fads-pagination" style="margin-left:auto">
                                            <c:if test="${pager.existPrePage}">
                                                <li class=""><la:link
                                                        styleClass=""
                                                        href="list/${pager.currentPageNumber - 1}?dictId=${f:u(dictId)}">
                                                    <la:message key="labels.prev_page"/>
                                                </la:link></li>
                                            </c:if>
                                            <c:if test="${!pager.existPrePage}">
                                                <li class="disabled"><a
                                                        class="" href="#"><la:message
                                                        key="labels.prev_page"/></a></li>
                                            </c:if>
                                            <c:forEach var="p" varStatus="s"
                                                       items="${pager.pageNumberList}">
                                                <li
                                                        <c:if test="${p == pager.currentPageNumber}">class="active"</c:if>>
                                                    <la:link
                                                            styleClass=""
                                                            href="list/${p}?dictId=${f:u(dictId)}">${p}</la:link></li>
                                            </c:forEach>
                                            <c:if test="${pager.existNextPage}">
                                                <li class=""><la:link
                                                        styleClass=""
                                                        href="list/${pager.currentPageNumber + 1}?dictId=${f:u(dictId)}">
                                                    <la:message key="labels.next_page"/>
                                                </la:link></li>
                                            </c:if>
                                            <c:if test="${!pager.existNextPage}">
                                                <li class="disabled"><a
                                                        class="" href="#"><la:message
                                                        key="labels.next_page"/></a></li>
                                            </c:if>
                                        </ul>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </main>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}
