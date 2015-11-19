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
							<%-- Box Header --%>
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.failure_url_configuration" />
								</h3>
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
											<la:text property="errorCountMin" size="2"
												styleClass="form-control"></la:text>
										</div>
										<div class="pull-left">-</div>
										<div class="col-xs-2">
											<la:text property="errorCountMax" size="2"
												styleClass="form-control"></la:text>
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
											<button type="submit" class="btn btn-primary" name="search"
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
															<th><la:message
																	key="labels.failure_url_last_access_time" /></th>
														</tr>
													</thead>
													<tbody>
														<c:forEach var="data" varStatus="s"
															items="${failureUrlItems}">
															<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}"
																data-href="details/4/${f:u(data.id)}">
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
										<%-- Paging Info --%>
										<div class="row">
											<div class="col-sm-5">
												<span><la:message
														key="labels.pagination_page_guide_msg"
														arg0="${f:h(failureUrlPager.currentPageNumber)}"
														arg1="${f:h(failureUrlPager.allPageCount)}"
														arg2="${f:h(failureUrlPager.allRecordCount)}" /></span>
											</div>
											<div class="col-sm-7">
												<%-- Paging Navigation --%>
												<ul class="pagination pagination-sm no-margin pull-right">
													<c:if test="${failureUrlPager.existPrePage}">
														<li class="prev"><la:link
																href="list/${failureUrlPager.currentPageNumber - 1}">
																<la:message key="labels.crud_link_prev_page" />
															</la:link></li>
													</c:if>
													<c:if test="${!failureUrlPager.existPrePage}">
														<li class="prev disabled"><a href="#"><la:message
																	key="labels.crud_link_prev_page" /></a></li>
													</c:if>
													<c:forEach var="p" varStatus="s"
														items="${failureUrlPager.pageNumberList}">
														<li
															<c:if test="${p == failureUrlPager.currentPageNumber}">class="active"</c:if>><la:link
																href="list/${p}">${p}</la:link></li>
													</c:forEach>
													<c:if test="${failureUrlPager.existNextPage}">
														<li class="next"><la:link
																href="list/${failureUrlPager.currentPageNumber + 1}">
																<la:message key="labels.crud_link_next_page" />
															</la:link></li>
													</c:if>
													<c:if test="${!failureUrlPager.existNextPage}">
														<li class="next disabled"><a href="#"><la:message
																	key="labels.crud_link_next_page" /></a></li>
													</c:if>
												</ul>
											</div>
										</div>
										<div class="row">
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
															<la:link href="deleteall" styleClass="btn btn-outline"
																data-dismiss="modal">
																<la:message key="labels.failure_url_delete_all_link" />
															</la:link>
														</div>
													</div>
												</div>
											</div>
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

