<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.system_info_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="log"/>
        <jsp:param name="menuType" value="systemInfo"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.system_info_configuration"/>
                        </h1>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="fads-row">
                <div class="fads-col-md-6">
                    <div class="fads-card">
                        <div class="fads-card-header">
                            <h3 class="fads-card-title">
                                <la:message key="labels.system_info_env_title"/>
                            </h3>
                        </div>
                        <%-- Card Body --%>
                        <div class="fads-card-body">
								<textarea id="envData" class="systemInfoData fads-textarea"
                                          readonly>
<c:forEach var="item" items="${envItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
								</textarea>
                        </div>
                    </div>
                </div>
                <div class="fads-col-md-6">
                    <div class="fads-card fads-fads-card-success">
                        <div class="fads-card-header">
                            <h3 class="fads-card-title">
                                <la:message key="labels.system_info_prop_title"/>
                            </h3>
                        </div>
                        <div class="fads-card-body">
								<textarea id="propData" class="systemInfoData fads-textarea"
                                          readonly>
<c:forEach var="item" items="${propItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
								</textarea>
                        </div>
                    </div>
                </div>
            </div>
            <div class="fads-row">
                <div class="fads-col-md-6">
                    <div class="fads-card fads-card-warning">
                        <div class="fads-card-header">
                            <h3 class="fads-card-title">
                                <la:message key="labels.system_info_fess_prop_title"/>
                            </h3>
                        </div>
                        <div class="fads-card-body">
                            <c:if test="${empty fessPropItems}">
									<textarea id="fessPropData" class="systemInfoData fads-textarea"
                                              readonly><la:message
                                            key="labels.system_info_system_properties_does_not_exist"/></textarea>
                            </c:if>
                            <c:if test="${!empty fessPropItems}">
									<textarea id="fessPropData" class="systemInfoData fads-textarea"
                                              readonly>
<c:forEach var="item" items="${fessPropItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
									</textarea>
                            </c:if>
                        </div>
                    </div>
                </div>
                <div class="fads-col-md-6">
                    <div class="fads-card fads-card-danger">
                        <div class="fads-card-header">
                            <h3 class="fads-card-title">
                                <la:message key="labels.system_info_bug_report_title"/>
                            </h3>
                        </div>
                        <div class="fads-card-body">
								<textarea id="bugReportData" class="systemInfoData fads-textarea"
                                          readonly>
<c:forEach var="item" items="${bugReportItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
								</textarea>
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
