<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.labeltype_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="labelType" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.labeltype_title_details" />
				</h1>
				<jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
			</section>
			<section class="content">
				<la:form action="/admin/labeltype/">
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
												<th class="col-xs-3"><la:message
														key="labels.labeltype_name" /></th>
												<td>${f:h(name)}<la:hidden property="name" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.labeltype_value" /></th>
												<td>${f:h(value)}<la:hidden property="value" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.labeltype_included_paths" /></th>
												<td>${f:br(f:h(includedPaths))}<la:hidden
														property="includedPaths" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.labeltype_excluded_paths" /></th>
												<td>${f:br(f:h(excludedPaths))}<la:hidden
														property="excludedPaths" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.role_type" /></th>
												<td><c:forEach var="rt" varStatus="s"
														items="${roleTypeItems}">
														<c:forEach var="rtid" varStatus="s" items="${roleTypeIds}">
															<c:if test="${rtid==rt.id}">
																${f:h(rt.name)}<br />
															</c:if>
														</c:forEach>
													</c:forEach></td>
											</tr>
											<tr>
												<th><la:message key="labels.sortOrder" /></th>
												<td>${f:h(sortOrder)}<la:hidden property="sortOrder" /></td>
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
