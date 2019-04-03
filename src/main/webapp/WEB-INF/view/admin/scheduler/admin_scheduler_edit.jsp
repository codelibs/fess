<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.scheduledjob_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="scheduler" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.scheduledjob_title_details" />
				</h1>
				<jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
			</section>
			<section class="content">
				<la:form action="/admin/scheduler/" styleClass="form-horizontal">
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2}">
						<la:hidden property="id" />
						<la:hidden property="versionNo" />
					</c:if>
					<la:hidden property="createdBy" />
					<la:hidden property="createdTime" />
					<div class="row">
						<div class="col-md-12">
							<div
								class="box <c:if test="${crudMode == 1}">box-success</c:if><c:if test="${crudMode == 2}">box-warning</c:if>">
								<div class="box-header with-border">
									<jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
								</div>
								<!-- /.box-header -->
								<div class="box-body">
									<div>
										<la:info id="msg" message="true">
											<div class="alert alert-info">${msg}</div>
										</la:info>
										<la:errors property="_global" />
									</div>
									<div class="form-group">
										<label for="name" class="col-sm-3 control-label"><la:message
												key="labels.scheduledjob_name" /></label>
										<div class="col-sm-9">
											<la:errors property="name" />
											<la:text styleId="name" property="name" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="target" class="col-sm-3 control-label"><la:message
												key="labels.scheduledjob_target" /></label>
										<div class="col-sm-9">
											<la:errors property="target" />
											<la:text styleId="target" property="target" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="cronExpression" class="col-sm-3 control-label"><la:message
												key="labels.scheduledjob_cronExpression" /></label>
										<div class="col-sm-9">
											<la:errors property="cronExpression" />
											<la:text styleId="cronExpression" property="cronExpression" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="scriptType" class="col-sm-3 control-label"><la:message
												key="labels.scheduledjob_scriptType" /></label>
										<div class="col-sm-9">
											<la:errors property="scriptType" />
											<la:text styleId="scriptType" property="scriptType" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="scriptData" class="col-sm-3 control-label"><la:message
												key="labels.scheduledjob_scriptData" /></label>
										<div class="col-sm-9">
											<la:errors property="scriptData" />
											<la:textarea styleId="scriptData" property="scriptData" styleClass="form-control"
												rows="5" />
										</div>
									</div>
									<div class="form-group">
										<label for="jobLogging" class="col-sm-3 control-label"><la:message
												key="labels.scheduledjob_jobLogging" /></label>
										<div class="col-sm-9">
											<la:errors property="jobLogging" />
											<div class="checkbox">
												<label> <la:checkbox styleId="jobLogging" property="jobLogging" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="crawler" class="col-sm-3 control-label"><la:message
												key="labels.scheduledjob_crawler" /></label>
										<div class="col-sm-9">
											<la:errors property="crawler" />
											<div class="checkbox">
												<label> <la:checkbox styleId="crawler" property="crawler" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="available" class="col-sm-3 control-label"><la:message
												key="labels.scheduledjob_status" /></label>
										<div class="col-sm-9">
											<la:errors property="available" />
											<div class="checkbox">
												<label> <la:checkbox styleId="available" property="available" /> <la:message
														key="labels.enabled" />
												</label>
											</div>
										</div>
									</div>
									<div class="form-group">
										<label for="sortOrder" class="col-sm-3 control-label"><la:message
												key="labels.sortOrder" /></label>
										<div class="form-inline col-sm-9">
											<la:errors property="sortOrder" />
											<input type="number" name="sortOrder" id="sortOrder"
												value="${f:h(sortOrder)}" class="form-control"
												min="0" max="100000">
										</div>
									</div>
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
