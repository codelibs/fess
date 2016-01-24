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
					<la:message key="labels.failure_url_configuration" />
				</h1>
				<jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
			</section>
			<section class="content">
				<div class="row">
					<div class="col-xs-12">
						<div class="box box-primary">
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.failure_url_configuration" />
								</h3>
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
								<la:form action="/admin/failureurl/"
									styleClass="form-horizontal">
									<div class="form-group">
										<label for="url" class="col-sm-2 control-label"><la:message
												key="labels.failure_url_search_url" /></label>
										<div class="col-sm-10">
											<la:text property="url" styleClass="form-control"></la:text>
										</div>
									</div>
									<div class="form-group row">
										<label for="errorCountMin" class="col-sm-2 control-label"><la:message
												key="labels.failure_url_search_error_count" /></label>
										<div class="col-xs-2">
											<la:errors property="errorCountMin" />
											<input type="number" name="errorCountMin"
												value="${f:h(errorCountMin)}" class="form-control"
												min="0" max="100000">
										</div>
										<div class="pull-left">-</div>
										<div class="col-xs-2">
											<la:errors property="errorCountMax" />
											<input type="number" name="errorCountMax"
												value="${f:h(errorCountMax)}" class="form-control"
												min="0" max="100000">
										</div>
									</div>
									<div class="form-group">
										<label for="errorName" class="col-sm-2 control-label"><la:message
												key="labels.failure_url_search_error_name" /></label>
										<div class="col-sm-10">
											<la:text property="errorName" styleClass="form-control"></la:text>
										</div>
									</div>
									<div class="form-group">
										<div class="col-sm-offset-2 col-sm-10">
											<button type="submit" class="btn btn-primary" id="submit"
												name="search"
												value="<la:message key="labels.crud_button_search" />">
												<i class="fa fa-search"></i>
												<la:message key="labels.crud_button_search" />
											</button>
											<button type="submit" class="btn btn-secondary" name="reset"
												value="<la:message key="labels.crud_button_reset" />">
												<la:message key="labels.crud_button_reset" />
											</button>
										</div>
									</div>
								</la:form>
								<div class="data-wrapper">
									<%-- List --%>
									<c:if test="${failureUrlPager.allRecordCount == 0}">
										<div class="row top20">
											<div class="col-sm-12">
												<i class="fa fa-info-circle text-light-blue"></i>
												<la:message key="labels.list_could_not_find_crud_table" />
											</div>
										</div>
									</c:if>
									<c:if test="${failureUrlPager.allRecordCount > 0}">
										<div class="row">
											<div class="col-sm-12">
												<table class="table table-bordered table-striped dataTable">
													<thead>
														<tr>
															<th><la:message key="labels.failure_url_url" /></th>
															<th><la:message
																	key="labels.failure_url_search_error_name" /></th>
															<th class="col-md-2"><la:message
																	key="labels.failure_url_last_access_time" /></th>
														</tr>
													</thead>
													<tbody>
														<c:forEach var="data" varStatus="s"
															items="${failureUrlItems}">
															<tr data-href="${contextPath}/admin/failureurl/details/4/${f:u(data.id)}">
																<td>${f:h(data.url)}</td>
																<td>${f:h(data.errorName)}</td>
																<td><fmt:formatDate
																		value="${fe:date(data.lastAccessTime)}"
																		pattern="yyyy-MM-dd'T'HH:mm:ss" /></td>
															</tr>
														</c:forEach>
													</tbody>
												</table>
											</div>
										</div>
										<c:set var="pager" value="${failureUrlPager}" scope="request" />
										<c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp" />
										<div class="row">
											<la:form action="/admin/failureurl/">
												<div class="col-sm-12 center">
													<button type="button" class="btn btn-danger"
														data-toggle="modal" data-target="#confirmToDeleteAll">
														<i class="fa fa-trash"></i>
														<la:message key="labels.failure_url_delete_all_link" />
													</button>
												</div>
												<div class="modal modal-danger fade" id="confirmToDeleteAll"
													tabindex="-1" role="dialog">
													<div class="modal-dialog">
														<div class="modal-content">
															<div class="modal-header">
																<button type="button" class="close" data-dismiss="modal"
																	aria-label="Close">
																	<span aria-hidden="true">×</span>
																</button>
																<h4 class="modal-title">
																	<la:message key="labels.failure_url_delete_all_link" />
																</h4>
															</div>
															<div class="modal-body">
																<p>
																	<la:message
																		key="labels.failure_url_delete_all_confirmation" />
																</p>
															</div>
															<div class="modal-footer">
																<button type="button" class="btn btn-outline pull-left"
																	data-dismiss="modal">
																	<la:message key="labels.failure_url_delete_all_cancel" />
																</button>
																<button type="submit" class="btn btn-outline btn-danger"
																	name="deleteall"
																	value="<la:message key="labels.failure_url_delete_all_link" />">
																	<i class="fa fa-trash"></i>
																	<la:message key="labels.failure_url_delete_all_link" />
																</button>
															</div>
														</div>
													</div>
												</div>
											</la:form>
										</div>
									</c:if>
								</div>
								<!-- /.data-wrapper -->
							</div>
							<!-- ./box-body -->
						</div>
						<!-- /.box -->
					</div>
				</div>
			</section>
		</div>
		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>

