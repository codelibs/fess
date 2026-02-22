<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.reqheader_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="requestHeader"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.reqheader_configuration"/>
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
                    <c:if test="${!displayCreateLink}">
                        <la:link href="../webconfig/" styleClass="fads-btn fads-btn-primary ${f:h(editableClass)}">
                            <la:message key="labels.reqheader_create_web_config"/>
                        </la:link>
                    </c:if>
                    <c:if test="${displayCreateLink}">
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
                                    <%-- List --%>
                                <c:if test="${reqHeaderPager.allRecordCount == 0}">
                                    <div class="fads-row top10">
                                        <div class="fads-col-sm-12">
                                            <i class="fa fa-info-circle text-primary" aria-hidden="true"></i>
                                            <la:message key="labels.list_could_not_find_crud_table"/>
                                        </div>
                                    </div>
                                </c:if>
                                <c:if test="${reqHeaderPager.allRecordCount > 0}">
                                    <div class="fads-row top10">
                                        <div class="fads-col-sm-12">
                                            <table class="fads-table" aria-label="<la:message key="labels.request_header_list" />">
                                                <thead>
                                                <tr>
                                                    <th><la:message key="labels.reqheader_list_name"/></th>
                                                    <th><la:message
                                                            key="labels.reqheader_list_web_crawling_config"/></th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <c:forEach var="data" varStatus="s"
                                                           items="${requestHeaderItems}">
                                                    <tr
                                                            data-href="${contextPath}/admin/reqheader/details/4/${f:u(data.id)}">
                                                        <td>${f:h(data.name)}</td>
                                                        <td>${f:h(data.webConfig.name)}</td>
                                                    </tr>
                                                </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>

                                    <c:set var="pager" value="${reqHeaderPager}"
                                           scope="request"/>
                                    <c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp"/>
                                    <c:if test="${pager.currentPageNumber > pager.allPageCount}">
                                        <script>location.href = "${contextPath}/admin/reqheader/list/${pager.allPageCount}";</script>
                                    </c:if>
                                </c:if>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </section>
    </main>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}
