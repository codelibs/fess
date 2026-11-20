<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
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
    <main id="mainContent" class="content-wrapper">
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
            <div class="card card-outline card-primary">
                <div class="card-body">
                    <ul class="nav nav-tabs" role="tablist">
                        <li class="nav-item">
                            <a class="nav-link active" id="systeminfo-env-tab" href="#systeminfo-env"
                               data-toggle="tab" role="tab" aria-controls="systeminfo-env" aria-selected="true">
                                <la:message key="labels.system_info_env_title"/>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" id="systeminfo-prop-tab" href="#systeminfo-prop"
                               data-toggle="tab" role="tab" aria-controls="systeminfo-prop" aria-selected="false">
                                <la:message key="labels.system_info_prop_title"/>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" id="systeminfo-fessprop-tab" href="#systeminfo-fessprop"
                               data-toggle="tab" role="tab" aria-controls="systeminfo-fessprop" aria-selected="false">
                                <la:message key="labels.system_info_fess_prop_title"/>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" id="systeminfo-bug-tab" href="#systeminfo-bug"
                               data-toggle="tab" role="tab" aria-controls="systeminfo-bug" aria-selected="false">
                                <la:message key="labels.system_info_bug_report_title"/>
                            </a>
                        </li>
                    </ul>
                    <div class="tab-content">
                        <div class="tab-pane active" id="systeminfo-env" role="tabpanel"
                             aria-labelledby="systeminfo-env-tab">
								<textarea id="envData" class="systemInfoData form-control"
                                          readonly>
<c:forEach var="item" items="${envItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
								</textarea>
                        </div>
                        <div class="tab-pane" id="systeminfo-prop" role="tabpanel"
                             aria-labelledby="systeminfo-prop-tab">
								<textarea id="propData" class="systemInfoData form-control"
                                          readonly>
<c:forEach var="item" items="${propItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
								</textarea>
                        </div>
                        <div class="tab-pane" id="systeminfo-fessprop" role="tabpanel"
                             aria-labelledby="systeminfo-fessprop-tab">
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
                        <div class="tab-pane" id="systeminfo-bug" role="tabpanel"
                             aria-labelledby="systeminfo-bug-tab">
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
    </main>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}
