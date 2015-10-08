<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.system_info_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="log" />
			<jsp:param name="menuType" value="systemInfo" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.system_info_configuration" />
				</h1>
			</section>

			<section class="content">

				<div class="row">
					<div class="col-md-12">
						<div class="box">
							<%-- Box Header --%>
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.system_info_env_title" />
								</h3>
							</div>
							<%-- Box Body --%>
							<div class="box-body">
							    <textarea id="envData" style="height: 300px;" class="form-control">
<c:forEach var="item" items="${envItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
							    </textarea>
							</div>
						</div>
						<div class="box">
							<%-- Box Header --%>
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.system_info_prop_title" />
								</h3>
							</div>
							<%-- Box Body --%>
							<div class="box-body">
								<textarea id="propData" style="height: 300px;" class="form-control">
<c:forEach var="item" items="${propItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
								</textarea>
							</div>
						</div>
						<div class="box">
							<%-- Box Header --%>
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.system_info_fess_prop_title" />
								</h3>
							</div>
							<%-- Box Body --%>
							<div class="box-body">
								<c:if test="${empty fessPropItems}">
									<textarea id="fessPropData" style="height: 300px;" class="form-control"><la:message key="labels.system_info_crawler_properties_does_not_exist" /></textarea>
								</c:if>
								<c:if test="${!empty fessPropItems}">
									<textarea id="fessPropData" style="height: 300px;" class="form-control">
<c:forEach var="item" items="${fessPropItems}">${f:h(item.label)}=${f:h(item.value)}
</c:forEach>
									</textarea>
								</c:if>
							</div>
						</div>
						<div class="box">
							<%-- Box Header --%>
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.system_info_bug_report_title" />
								</h3>
							</div>
							<%-- Box Body --%>
							<div class="box-body">
								<textarea id="bugReportData" style="height: 300px;" class="form-control">
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
