<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.request_header_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="requestHeader" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.request_header_title_details" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="/admin/requestheader">
							<la:message key="labels.request_header_link_list" />
						</la:link></li>
					<c:if test="${crudMode == 1}">
						<li class="active"><a href="#"><la:message
									key="labels.request_header_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
						<li class="active"><a href="#"><la:message
									key="labels.request_header_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
						<li class="active"><a href="#"><la:message
									key="labels.request_header_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
						<li class="active"><a href="#"><la:message
									key="labels.request_header_link_confirm" /></a></li>
					</c:if>
				</ol>
			</section>

			<section class="content">

				<%-- Form --%>
				<la:form>
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
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<la:message key="labels.request_header_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.request_header_link_update" />
										</c:if>
										<c:if test="${crudMode == 3}">
											<la:message key="labels.request_header_link_delete" />
										</c:if>
										<c:if test="${crudMode == 4}">
											<la:message key="labels.request_header_link_confirm" />
										</c:if>
									</h3>
									<div class="btn-group pull-right">
										<la:link href="/admin/requestheader"
											styleClass="btn btn-primary btn-xs">
											<la:message key="labels.request_header_link_list" />
										</la:link>
									</div>
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
														key="labels.request_header_name" /></th>
												<td>${f:h(name)}<la:hidden property="name" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.request_header_value" /></th>
												<td>${f:h(value)}<la:hidden property="value" /></td>
											</tr>
											<tr>
												<th><la:message
														key="labels.request_header_web_crawling_config" /></th>
												<td><c:forEach var="item" items="${webConfigItems}">
														<c:if test="${webConfigId==item.value}">${f:h(item.label)}</c:if>
													</c:forEach> <la:hidden property="webConfigId" /></td>
											</tr>
										</tbody>
									</table>

								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 1}">
										<button type="submit" class="btn" name="createagain"
											value="<la:message key="labels.request_header_button_back" />">
											<la:message key="labels.request_header_button_back" />
										</button>
										<button type="submit" class="btn btn-success" name="create"
											value="<la:message key="labels.request_header_button_create" />">
											<la:message key="labels.request_header_button_create" />
										</button>
									</c:if>
									<c:if test="${crudMode == 2}">
										<button type="submit" class="btn" name="editagain"
											value="<la:message key="labels.request_header_button_back" />">
											<la:message key="labels.request_header_button_back" />
										</button>
										<button type="submit" class="btn btn-warning" name="update"
											value="<la:message key="labels.request_header_button_update" />">
											<la:message key="labels.request_header_button_update" />
										</button>
									</c:if>
									<c:if test="${crudMode == 3}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.request_header_button_back" />">
											<la:message key="labels.request_header_button_back" />
										</button>
										<button type="submit" class="btn btn-danger" name="delete"
											value="<la:message key="labels.request_header_button_delete" />">
											<la:message key="labels.request_header_button_delete" />
										</button>
									</c:if>
									<c:if test="${crudMode == 4}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.request_header_button_back" />">
											<la:message key="labels.request_header_button_back" />
										</button>
										<button type="submit" class="btn btn-warning"
											name="editfromconfirm"
											value="<la:message key="labels.request_header_button_edit" />">
											<la:message key="labels.request_header_button_edit" />
										</button>
										<button type="submit" class="btn btn-danger"
											name="deletefromconfirm"
											value="<la:message key="labels.request_header_button_delete" />">
											<la:message key="labels.request_header_button_delete" />
										</button>
									</c:if>
								</div>
							</div>
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
