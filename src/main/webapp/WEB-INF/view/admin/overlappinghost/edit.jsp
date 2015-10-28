<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.overlapping_host_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="overlappintHost" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.overlapping_host_title_details" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="/admin/overlappinghost">
							<la:message key="labels.overlapping_host_link_list" />
						</la:link></li>
					<c:if test="${crudMode == 1}">
						<li class="active"><a href="#"><la:message
									key="labels.overlapping_host_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
						<li class="active"><a href="#"><la:message
									key="labels.overlapping_host_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
						<li class="active"><a href="#"><la:message
									key="labels.overlapping_host_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
						<li class="active"><a href="#"><la:message
									key="labels.overlapping_host_link_confirm" /></a></li>
					</c:if>
				</ol>
			</section>

			<section class="content">

				<%-- Form --%>
				<la:form>
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2}">
						<la:hidden property="id" />
						<la:hidden property="versionNo" />
					</c:if>
					<la:hidden property="createdBy" />
					<la:hidden property="createdTime" />
					<la:hidden property="sortOrder" />
					<div class="row">
						<div class="col-md-12">
							<div class="box box-primary">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<la:message key="labels.overlapping_host_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.overlapping_host_link_update" />
										</c:if>
									</h3>
									<div class="box-tools pull-right">
										<span class="label label-default"><la:link
												href="/admin/overlappinghost">
												<la:message key="labels.overlapping_host_link_list" />
											</la:link></span>
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
									<div class="form-group">
										<label for="regularName"><la:message
												key="labels.regular_name" /></label>
										<la:text property="regularName" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="overlappingName"><la:message
												key="labels.overlapping_name" /></label>
										<la:text property="overlappingName" styleClass="form-control" />
									</div>

									<%-- Box Footer --%>
									<div class="box-footer">
										<c:if test="${crudMode == 1}">
											<button type="submit" class="btn" name="back"
												value="<la:message key="labels.overlapping_host_button_back" />">
												<la:message key="labels.overlapping_host_button_back" />
											</button>
											<button type="submit" class="btn btn-success"
												name="confirmfromcreate"
												value="<la:message key="labels.overlapping_host_button_confirm" />">
												<la:message key="labels.overlapping_host_button_confirm" />
											</button>
										</c:if>
										<c:if test="${crudMode == 2}">
											<button type="submit" class="btn" name="back"
												value="<la:message key="labels.overlapping_host_button_back" />">
												<la:message key="labels.overlapping_host_button_back" />
											</button>
											<button type="submit" class="btn btn-warning"
												name="confirmfromupdate"
												value="<la:message key="labels.overlapping_host_button_confirm" />">
												<la:message key="labels.overlapping_host_button_confirm" />
											</button>
										</c:if>
									</div>
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
