<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.scheduledjob_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="scheduledJob" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.scheduledjob_title_details" />
				</h1>
				<jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
			</section>
			<section class="content">
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
														key="labels.scheduledjob_name" /></th>
												<td>${f:h(name)}<la:hidden property="name" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.scheduledjob_target" /></th>
												<td>${f:h(target)}<la:hidden property="target" /></td>
											</tr>
											<tr>
												<th><la:message
														key="labels.scheduledjob_cronExpression" /></th>
												<td>${f:h(cronExpression)}<la:hidden
														property="cronExpression" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.scheduledjob_scriptType" /></th>
												<td>${f:h(scriptType)}<la:hidden property="scriptType" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.scheduledjob_scriptData" /></th>
												<td>${f:br(f:h(scriptData))}<la:hidden
														property="scriptData" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.scheduledjob_jobLogging" /></th>
												<td><c:if test="${jobLogging=='on'}">
														<la:message key="labels.enabled" />
													</c:if> <c:if test="${jobLogging!='on'}">
														<la:message key="labels.disabled" />
													</c:if> <la:hidden property="jobLogging" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.scheduledjob_crawler" /></th>
												<td><c:if test="${crawler=='on'}">
														<la:message key="labels.enabled" />
													</c:if> <c:if test="${crawler!='on'}">
														<la:message key="labels.disabled" />
													</c:if> <la:hidden property="crawler" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.scheduledjob_status" /></th>
												<td><c:if test="${available=='on'}">
														<la:message key="labels.enabled" />
													</c:if> <c:if test="${available!='on'}">
														<la:message key="labels.disabled" />
													</c:if> <la:hidden property="available" /></td>
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
									<c:if test="${running}">
										<button type="submit" class="btn btn-danger" name="stop"
											value="<la:message key="labels.scheduledjob_button_stop" />">
											<i class="fa fa-stop"></i>
											<la:message key="labels.scheduledjob_button_stop" />
										</button>
									</c:if>
									<c:if test="${!running}">
										<button type="submit" class="btn btn-success" name="start"
											value="<la:message key="labels.scheduledjob_button_start" />">
											<i class="fa fa-play-circle"></i>
											<la:message key="labels.scheduledjob_button_start" />
										</button>
									</c:if>
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
