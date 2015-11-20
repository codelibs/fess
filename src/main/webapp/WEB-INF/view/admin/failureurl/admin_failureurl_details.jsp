<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.failure_url_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="log" />
			<jsp:param name="menuType" value="failureUrl" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.failure_url_title_details" />
				</h1>
				<jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
			</section>
			<section class="content">
				<la:form action="/admin/failureurl/">
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
										<c:if test="${crudMode == 1}">
											<la:message key="labels.failure_url_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.failure_url_link_update" />
										</c:if>
										<c:if test="${crudMode == 3}">
											<la:message key="labels.failure_url_link_delete" />
										</c:if>
										<c:if test="${crudMode == 4}">
											<la:message key="labels.failure_url_link_details" />
										</c:if>
									</h3>
									<div class="btn-group pull-right">
										<la:link href="/admin/failureurl"
											styleClass="btn btn-primary btn-xs">
											<i class="fa fa-th-list"></i>
											<la:message key="labels.failure_url_link_list" />
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
									<div class="table-responsive">
										<table class="table table-bordered">
											<tbody>
												<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
													<tr>
														<th><la:message key="labels.failure_url_id" /></th>
														<td>${f:h(id)}<la:hidden property="id" /></td>
													</tr>
												</c:if>
												<tr>
													<th><la:message key="labels.failure_url_url" /></th>
													<td>${f:h(url)}<la:hidden property="url" />
													</td>
												</tr>
												<tr>
													<th><la:message key="labels.failure_url_thread_name" /></th>
													<td>${f:h(threadName)}<la:hidden property="threadName" /></td>
												</tr>
												<tr>
													<th><la:message key="labels.failure_url_error_name" /></th>
													<td>${f:h(errorName)}<la:hidden property="errorName" /></td>
												</tr>
												<tr>
													<th><la:message key="labels.failure_url_error_log" /></th>
													<td>${f:br(f:nbsp(f:h(errorLog)))}<la:hidden
															property="errorLog" />
													</td>
												</tr>
												<tr>
													<th><la:message key="labels.failure_url_error_count" /></th>
													<td>${f:h(errorCount)}<la:hidden property="errorCount" /></td>
												</tr>
												<tr>
													<th><la:message
															key="labels.failure_url_last_access_time" /></th>
													<td>${f:h(lastAccessTime)}<la:hidden
															property="lastAccessTime" /></td>
												</tr>
												<tr>
													<th><la:message
															key="labels.failure_url_web_config_name" /></th>
													<td>${f:h(webConfigName)}</td>
												</tr>
												<tr>
													<th><la:message
															key="labels.failure_url_file_config_name" /></th>
													<td>${f:h(fileConfigName)}</td>
												</tr>
											</tbody>
										</table>
									</div>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<c:if test="${crudMode == 4}">
										<button type="submit" class="btn btn-default" name="back"
											value="<la:message key="labels.crud_button_back" />">
											<i class="fa fa-arrow-circle-left"></i>
											<la:message key="labels.crud_button_back" />
										</button>
										<button type="button" class="btn btn-danger" name="delete"
											data-toggle="modal" data-target="#confirmToDelete"
											value="<la:message key="labels.crud_button_delete" />">
											<i class="fa fa-trash"></i>
											<la:message key="labels.crud_button_delete" />
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

