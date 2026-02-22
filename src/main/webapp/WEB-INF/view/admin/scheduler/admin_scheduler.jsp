<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.scheduledjob_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="scheduler"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.scheduledjob_configuration"/>
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
                            <%-- List --%>
                            <c:if test="${schedulerPager.allRecordCount == 0}">
                                <div class="fads-row top10">
                                    <div class="fads-col-sm-12">
                                        <i class="fa fa-info-circle text-primary" aria-hidden="true"></i>
                                        <la:message key="labels.list_could_not_find_crud_table"/>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${schedulerPager.allRecordCount > 0}">
                                <div class="fads-row">
                                    <div class="fads-col-sm-12">
                                        <table class="fads-table" aria-label="<la:message key="labels.scheduledjob_list" />">
                                            <thead>
                                            <tr>
                                                <th><la:message key="labels.scheduledjob_name"/></th>
                                                <th class="fads-text-center" style="width: 10%"><la:message
                                                        key="labels.scheduledjob_status"/></th>
                                                <th class="fads-text-center" style="width: 10%"><la:message
                                                        key="labels.scheduledjob_target"/></th>
                                                <th><la:message
                                                        key="labels.scheduledjob_cronExpression"/></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="data" varStatus="s"
                                                       items="${scheduledJobItems}">
                                                <tr
                                                        data-href="${contextPath}/admin/scheduler/details/4/${f:u(data.id)}" role="button" tabindex="0">
                                                    <td>${f:h(data.name)}</td>
                                                    <td class="fads-text-center"><c:if test="${data.running}">
																	<span class="fads-lozenge fads-lozenge-success"><la:message
                                                                            key="labels.scheduledjob_running"/></span>
                                                    </c:if> <c:if test="${!data.running}">
                                                        <c:if test="${data.available}">
																		<span class="fads-lozenge fads-lozenge-primary"><la:message
                                                                                key="labels.scheduledjob_active"/></span>
                                                        </c:if>
                                                        <c:if test="${!data.available}">
																		<span class="badge badge-secondary"><la:message
                                                                                key="labels.scheduledjob_nojob"/></span>
                                                        </c:if>
                                                    </c:if></td>
                                                    <td class="fads-text-center">${f:h(data.target)}</td>
                                                    <td>${f:h(data.cronExpression)}</td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <c:set var="pager" value="${schedulerPager}" scope="request"/>
                                <c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp"/>
                                <c:if test="${pager.currentPageNumber > pager.allPageCount}">
                                    <script>location.href = "${contextPath}/admin/scheduler/list/${pager.allPageCount}";</script>
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
