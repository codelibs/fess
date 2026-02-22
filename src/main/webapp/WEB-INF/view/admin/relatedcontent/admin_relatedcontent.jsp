<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.related_content_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="relatedContent"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.related_content_configuration"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="fads-row">
                <div class="fads-col-md-12">
                    <div class="fads-card">
                        <div class="fads-card-header">
                            <jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
                        </div>
                        <div class="fads-card-body">
                            <%-- Message --%>
                            <div>
                                <la:info id="msg" message="true">
                                    <div class="fads-banner fads-banner-info">${msg}</div>
                                </la:info>
                                <la:errors/>
                            </div>
                            <a role="button" data-fads-collapse="#listSearchForm" aria-expanded="false"
                               aria-controls="listSearchForm"><i class="fas fa-search" aria-hidden="true"></i></a>
                            <div class="collapse <c:if test="${!empty term || !empty content}">show</c:if>" id="listSearchForm">
                                <la:form action="/admin/relatedcontent/">
                                    <div class="fads-form-field">
                                        <label for="term" class="col-sm-2 text-sm-right col-form-label"><la:message
                                                key="labels.related_content_term"/></label>
                                        <div class="col-sm-10">
                                            <la:text styleId="term" property="term" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="content" class="col-sm-2 text-sm-right col-form-label"><la:message
                                                key="labels.related_content_content"/></label>
                                        <div class="col-sm-10">
                                            <la:text styleId="content" property="content" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <div class="offset-sm-2 col-sm-10">
                                            <button type="submit" class="fads-btn fads-btn-primary" id="submit"
                                                    name="search"
                                                    value="<la:message key="labels.crud_button_search" />">
                                                <i class="fa fa-search" aria-hidden="true"></i>
                                                <la:message key="labels.crud_button_search"/>
                                            </button>
                                            <button type="submit" class="fads-btn fads-btn-default" name="reset"
                                                    value="<la:message key="labels.crud_button_reset" />">
                                                <la:message key="labels.crud_button_reset"/>
                                            </button>
                                        </div>
                                    </div>
                                </la:form>
                            </div>
                            <%-- List --%>
                            <c:if test="${relatedContentPager.allRecordCount == 0}">
                                <div class="fads-row top10">
                                    <div class="fads-col-sm-12">
                                        <i class="fa fa-info-circle text-primary" aria-hidden="true"></i>
                                        <la:message key="labels.list_could_not_find_crud_table"/>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${relatedContentPager.allRecordCount > 0}">
                                <div class="fads-row top10">
                                    <div class="fads-col-sm-12">
                                        <table class="fads-table" aria-label="<la:message key="labels.related_content_list" />">
                                            <thead>
                                            <tr>
                                                <th><la:message
                                                        key="labels.related_content_list_term"/></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="data" varStatus="s"
                                                       items="${relatedContentItems}">
                                                <tr
                                                        data-href="${contextPath}/admin/relatedcontent/details/4/${f:u(data.id)}" role="button" tabindex="0">
                                                    <td>${f:h(data.term)}</td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <c:set var="pager" value="${relatedContentPager}"
                                       scope="request"/>
                                <c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp"/>
                                <c:if test="${pager.currentPageNumber > pager.allPageCount}">
                                    <script>location.href = "${contextPath}/admin/relatedcontent/list/${pager.allPageCount}";</script>
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
