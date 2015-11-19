<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.joblog_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="log" />
			<jsp:param name="menuType" value="jobLog" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.joblog_title_details" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="/admin/crawlinginfo">
							<la:message key="labels.joblog_link_list" />
						</la:link></li>
					<li class="active"><la:message
							key="labels.joblog_link_details" /></li>
				</ol>
			</section>
			<section class="content">
				<la:form action="/admin/joblog/">
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
						<la:hidden property="id" />
					</c:if>
					<div class="row">
						<div class="col-md-12">
							<div
								class="box <c:if test="${crudMode == 1}">box-success</c:if><c:if test="${crudMode == 2}">box-warning</c:if><c:if test="${crudMode == 3}">box-danger</c:if><c:if test="${crudMode == 4}">box-primary</c:if>">
								<div class="box-header with-border">
									<h3 class="box-title">
										<la:message key="labels.joblog_link_details" />
									</h3>
									<div class="btn-group pull-right">
										<la:link href="/admin/joblog"
											styleClass="btn btn-primary btn-xs">
											<i class="fa fa-th-list"></i>
											<la:message key="labels.joblog_link_list" />
										</la:link>
									</div>
								</div>
								<!-- /.box-header -->
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
														key="labels.joblog_jobName" /></th>
												<td>${f:h(jobName)}<la:hidden property="jobName" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.joblog_jobStatus" /></th>
												<td>${f:h(jobStatus)}<la:hidden property="jobStatus" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.joblog_target" /></th>
												<td>${f:h(target)}<la:hidden property="target" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.joblog_startTime" /></th>
												<td>${f:h(startTime)}<la:hidden property="startTime" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.joblog_endTime" /></th>
												<td>${f:h(endTime)}<la:hidden property="endTime" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.joblog_scriptType" /></th>
												<td>${f:h(scriptType)}<la:hidden property="scriptType" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.joblog_scriptData" /></th>
												<td>${f:br(f:h(scriptData))}<la:hidden
														property="scriptData" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.joblog_scriptResult" /></th>
												<td>${f:br(f:h(scriptResult))}<la:hidden
														property="scriptResult" /></td>
											</tr>
										</tbody>
									</table>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<button type="submit" class="btn btn-default" name="back"
										value="<la:message key="labels.joblog_button_back" />">
										<i class="fa fa-arrow-circle-left"></i>
										<la:message key="labels.joblog_button_back" />
									</button>
									<button type="button" class="btn btn-danger" name="delete"
										data-toggle="modal" data-target="#confirmToDelete"
										value="<la:message key="labels.joblog_button_delete" />">
										<i class="fa fa-trash"></i>
										<la:message key="labels.joblog_button_delete" />
									</button>
									<div class="modal modal-danger fade" id="confirmToDelete"
										tabindex="-1" role="dialog">
										<div class="modal-dialog">
											<div class="modal-content">
												<div class="modal-header">
													<button type="button" class="close" data-dismiss="modal"
														aria-label="Close">
														<span aria-hidden="true">×</span>
													</button>
													<h4 class="modal-title">
														<la:message key="labels.crud_title_delete" />
													</h4>
												</div>
												<div class="modal-body">
													<p>
														<la:message key="labels.crud_delete_confirmation" />
													</p>
												</div>
												<div class="modal-footer">
													<button type="button" class="btn btn-outline pull-left"
														data-dismiss="modal">
														<la:message key="labels.crud_button_cancel" />
													</button>
													<button type="submit" class="btn btn-outline btn-danger"
														name="delete"
														value="<la:message key="labels.crud_button_delete" />">
														<i class="fa fa-trash"></i>
														<la:message key="labels.crud_button_delete" />
													</button>
												</div>
											</div>
										</div>
									</div>
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

