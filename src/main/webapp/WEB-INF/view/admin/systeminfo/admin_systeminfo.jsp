<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.system_info_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="log"/>
        <jsp:param name="menuType" value="systemInfo"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.system_info_configuration"/>
                        </h1>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="row">
                <div class="col-md-6">
                    <div class="card card-outline card-primary">
                        <div class="card-header">
                            <h3 class="card-title">
                                <la:message key="labels.system_info_env_title"/>
                            </h3>
                        </div>
                        <%-- Card Body --%>
                        <div class="card-body">
								<textarea id="envData" class="systemInfoData form-control"
                                          readonly>
<c:forEach var="item" items="${envItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
								</textarea>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card card-outline card-success">
                        <div class="card-header">
                            <h3 class="card-title">
                                <la:message key="labels.system_info_prop_title"/>
                            </h3>
                        </div>
                        <div class="card-body">
								<textarea id="propData" class="systemInfoData form-control"
                                          readonly>
<c:forEach var="item" items="${propItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
								</textarea>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-6">
                    <div class="card card-outline card-warning">
                        <div class="card-header">
                            <h3 class="card-title">
                                <la:message key="labels.system_info_fess_prop_title"/>
                            </h3>
                        </div>
                        <div class="card-body">
                            <c:if test="${empty fessPropItems}">
									<textarea id="fessPropData" class="systemInfoData form-control"
                                              readonly><la:message
                                            key="labels.system_info_system_properties_does_not_exist"/></textarea>
                            </c:if>
                            <c:if test="${!empty fessPropItems}">
									<textarea id="fessPropData" class="systemInfoData form-control"
                                              readonly>
<c:forEach var="item" items="${fessPropItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
									</textarea>
                            </c:if>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card card-outline card-danger">
                        <div class="card-header">
                            <h3 class="card-title">
                                <la:message key="labels.system_info_bug_report_title"/>
                            </h3>
                        </div>
                        <div class="card-body">
								<textarea id="bugReportData" class="systemInfoData form-control"
                                          readonly>
<c:forEach var="item" items="${bugReportItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
								</textarea>
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
