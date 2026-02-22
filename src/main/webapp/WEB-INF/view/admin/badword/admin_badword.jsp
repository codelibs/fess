<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.bad_word_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="suggest"/>
        <jsp:param name="menuType" value="badWord"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.bad_word_configuration"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
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
            <div class="fads-row">
                <div class="fads-col-md-12">
                    <div class="fads-card">
                        <div class="fads-card-header">
                            <h3 class="fads-card-title">
                                <la:message key="labels.bad_word_link_list"/>
                            </h3>
                            <div class="fads-card-tools">
                                <div class="btn-group">
                                    <la:link href="/admin/badword"
                                             styleClass="fads-btn fads-btn-default fads-btn-compact ">
                                        <i class="fa fa-th-list" aria-hidden="true"></i>
                                        <la:message key="labels.bad_word_link_list"/>
                                    </la:link>
                                    <la:link href="createnew" styleClass="fads-btn fads-btn-success fads-btn-compact ${f:h(editableClass)}">
                                        <i class="fa fa-plus" aria-hidden="true"></i>
                                        <la:message key="labels.bad_word_link_create"/>
                                    </la:link>
                                    <la:link href="downloadpage"
                                             styleClass="fads-btn fads-btn-primary fads-btn-compact">
                                        <i class="fa fa-download" aria-hidden="true"></i>
                                        <la:message key="labels.bad_word_link_download"/>
                                    </la:link>
                                    <la:link href="uploadpage"
                                             styleClass="fads-btn fads-btn-success fads-btn-compact ${f:h(editableClass)}">
                                        <i class="fa fa-upload" aria-hidden="true"></i>
                                        <la:message key="labels.bad_word_link_upload"/>
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
                            <c:if test="${badWordPager.allRecordCount == 0}">
                                <div class="fads-row">
                                    <div class="fads-col-sm-12">
                                        <i class="fa fa-info-circle text-primary" aria-hidden="true"></i>
                                        <la:message key="labels.list_could_not_find_crud_table"/>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${badWordPager.allRecordCount > 0}">
                                <div class="fads-row">
                                    <div class="fads-col-sm-12">
                                        <table class="fads-table" aria-label="<la:message key="labels.bad_word_list" />">
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
    </main>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}

