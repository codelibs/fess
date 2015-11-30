<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.crawling_info_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="log" />
			<jsp:param name="menuType" value="crawlingInfo" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.crawling_info_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><la:link href="/admin/crawlinginfo">
							<la:message key="labels.crawling_info_title" />
						</la:link></li>
				</ol>
			</section>
			<section class="content">
				<div class="row">
					<div class="col-xs-12">
						<div class="box box-primary">
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.crawling_info_title" />
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
								<div class="row">
									<div class="col-sm-12">
										<la:form styleClass="form-inline"
											action="/admin/crawlinginfo/">
											<div class="form-group">
												<c:set var="ph_session_id">
													<la:message key="labels.crawling_info_session_id_search" />
												</c:set>
												<la:text styleId="sessionIdSearchBtn" property="sessionId"
													styleClass="form-control" placeholder="${ph_session_id}"></la:text>
											</div>
											<div class="form-group">
												<button type="submit" class="btn btn-primary" id="submit"
													name="search"
													value="<la:message key="labels.crawling_info_search" />">
													<i class="fa fa-search"></i>
													<la:message key="labels.crawling_info_search" />
												</button>
												<button type="submit" class="btn btn-secondary" name="reset"
													value="<la:message key="labels.crawling_info_reset" />">
													<la:message key="labels.crawling_info_reset" />
												</button>
											</div>
										</la:form>
									</div>
								</div>
								<%-- List --%>
								<c:if test="${crawlingInfoPager.allRecordCount == 0}">
									<div class="row top20">
										<div class="col-sm-12">
											<i class="fa fa-info-circle text-light-blue"></i>
											<la:message key="labels.list_could_not_find_crud_table" />
										</div>
									</div>
								</c:if>
								<c:if test="${crawlingInfoPager.allRecordCount > 0}">
									<div class="row top10">
										<div class="col-sm-12">
											<table class="table table-bordered table-striped dataTable">
												<thead>
													<tr>
														<th><la:message
																key="labels.crawling_info_session_id" /></th>
														<th><la:message
																key="labels.crawling_info_created_time" /></th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="data" varStatus="s"
														items="${crawlingInfoItems}">
														<tr
															data-href="${contextPath}/admin/crawlinginfo/details/4/${f:u(data.id)}">
															<td>${f:h(data.sessionId)}</td>
															<td><fmt:formatDate
																	value="${fe:date(data.createdTime)}"
																	pattern="yyyy-MM-dd'T'HH:mm:ss" /></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
									<c:set var="pager" value="${crawlingInfoPager}"
										scope="request" />
									<c:import url="/WEB-INF/view/common/admin/crud/pagination.jsp" />
									<div class="row">
										<la:form action="/admin/crawlinginfo/">
											<div class="col-sm-12 center">
												<button type="button" class="btn btn-danger"
													data-toggle="modal" data-target="#confirmToDeleteAll">
													<i class="fa fa-trash"></i>
													<la:message key="labels.crawling_info_delete_all_link" />
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
																<la:message
																	key="labels.crawling_info_delete_all_link" />
															</h4>
														</div>
														<div class="modal-body">
															<p>
																<la:message
																	key="labels.crawling_info_delete_all_confirmation" />
															</p>
														</div>
														<div class="modal-footer">
															<button type="button" class="btn btn-outline pull-left"
																data-dismiss="modal">
																<la:message
																	key="labels.crawling_info_delete_all_cancel" />
															</button>
															<button type="submit" class="btn btn-outline btn-danger"
																name="deleteall"
																value="<la:message key="labels.crawling_info_delete_all_link" />">
																<i class="fa fa-trash"></i>
																<la:message
																	key="labels.crawling_info_delete_all_link" />
															</button>
														</div>
													</div>
												</div>
											</div>
										</la:form>
									</div>
								</c:if>
							</div>
							<!-- /.box-body -->
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

