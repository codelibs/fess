<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.webauth_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="webAuthentication" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.webauth_title_details" />
				</h1>
				<jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
			</section>
			<section class="content">
				<la:form action="/admin/webauth/">
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
						<la:hidden property="id" />
						<la:hidden property="versionNo" />
					</c:if>
					<la:hidden property="createdBy" />
					<la:hidden property="createdTime" />
					<div class="row">
						<div class="col-md-12">
							<div
								class="box <c:if test="${crudMode == 1}">box-success</c:if><c:if test="${crudMode == 2}">box-warning</c:if><c:if test="${crudMode == 3}">box-danger</c:if><c:if test="${crudMode == 4}">box-primary</c:if>">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
									<%-- Message --%>
									<div>
										<la:info id="msg" message="true">
											<div class="alert alert-info">${msg}</div>
										</la:info>
										<la:errors />
									</div>
									<%-- Form Fields --%>
									<table class="table table-bordered">
										<tbody>
											<tr>
												<th class="col-xs-2"><la:message
														key="labels.webauth_hostname" /></th>
												<td>${f:h(hostname)}<la:hidden property="hostname" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.webauth_port" /></th>
												<td>${f:h(port)}<la:hidden property="port" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.webauth_realm" /></th>
												<td>${f:h(authRealm)}<la:hidden property="authRealm" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.webauth_scheme" /></th>
												<td><c:forEach var="item"
														items="${protocolSchemeItems}">
														<c:if test="${protocolScheme==item.value}">${f:h(item.label)}</c:if>
													</c:forEach> <la:hidden property="protocolScheme" /></td>
											</tr>
											<tr>
												<th><la:message
														key="labels.webauth_username" /></th>
												<td>${f:h(username)}<la:hidden property="username" /></td>
											</tr>
											<tr>
												<th><la:message
														key="labels.webauth_password" /></th>
												<td><c:if test="${password!=''}">******</c:if> <la:hidden
														property="password" /></td>
											</tr>
											<tr>
												<th><la:message
														key="labels.webauth_parameters" /></th>
												<td>${f:br(f:h(parameters))}<la:hidden
														property="parameters" /></td>
											</tr>
											<tr>
												<th><la:message
														key="labels.webauth_web_crawling_config" /></th>
												<td><c:forEach var="item" items="${webConfigItems}">
														<c:if test="${webConfigId==item.value}">${f:h(item.label)}</c:if>
													</c:forEach> <la:hidden property="webConfigId" /></td>
											</tr>
										</tbody>
									</table>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<jsp:include page="/WEB-INF/view/common/admin/crud/buttons.jsp"></jsp:include>
								</div>
								<!-- /.box-footer -->
							</div>
							<!-- /.box -->
						</div>
					</div>
				</la:form>
			</section>
		</div>
		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
