<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.dict_synonym_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="system" />
			<jsp:param name="menuType" value="dict" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.dict_synonym_title" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="list">
							<la:message key="labels.dict_list_link" />
						</la:link></li>
					<li><la:message key="labels.dict_synonym_list_link" /></li>
				</ol>
			</section>
			<section class="content">
				<div class="row">
					<div class="col-md-12">
						<div class="box box-primary">
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.dict_synonym_list_link" />
								</h3>
								<div class="btn-group pull-right">
									<la:link href="/admin/dict" styleClass="btn btn-default btn-xs">
										<i class="fa fa-book"></i>
										<la:message key="labels.dict_list_link" />
									</la:link>
									<la:link href="list/1?dictId=${f:u(dictId)}"
										styleClass="btn btn-primary btn-xs">
										<i class="fa fa-th-list"></i>
										<la:message key="labels.dict_synonym_list_link" />
									</la:link>
									<la:link href="createnew/${f:u(dictId)}"
										styleClass="btn btn-success btn-xs">
										<i class="fa fa-plus"></i>
										<la:message key="labels.dict_synonym_link_create" />
									</la:link>
									<la:link href="downloadpage/${f:u(dictId)}"
										styleClass="btn btn-primary btn-xs">
										<i class="fa fa-download"></i>
										<la:message key="labels.dict_synonym_link_download" />
									</la:link>
									<la:link href="uploadpage/${f:u(dictId)}"
										styleClass="btn btn-success btn-xs">
										<i class="fa fa-upload"></i>
										<la:message key="labels.dict_synonym_link_upload" />
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
								<%-- List --%>
								<c:if test="${synonymPager.allRecordCount == 0}">
									<div class="row top10">
										<div class="col-sm-12">
											<i class="fa fa-info-circle text-light-blue"></i>
											<la:message key="labels.list_could_not_find_crud_table" />
										</div>
									</div>
								</c:if>
								<c:if test="${synonymPager.allRecordCount > 0}">
									<div class="row">
										<div class="col-sm-12">
											<table class="table table-bordered table-striped">
												<thead>
													<tr>
														<th><la:message key="labels.dict_synonym_source" /></th>
														<th><la:message key="labels.dict_synonym_target" /></th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="data" varStatus="s"
														items="${synonymItemItems}">
														<tr
															data-href="${contextPath}/admin/dict/synonym/details/${f:u(dictId)}/4/${f:u(data.id)}">
															<td>${f:h(data.inputs)}</td>
															<td>${f:h(data.outputs)}</td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
									<c:set var="pager" value="${synonymPager}" scope="request" />
									<div class="row">
										<div class="col-sm-2">
											<la:message key="labels.pagination_page_guide_msg"
												arg0="${f:h(pager.currentPageNumber)}"
												arg1="${f:h(pager.allPageCount)}"
												arg2="${f:h(pager.allRecordCount)}" />
										</div>
										<div class="col-sm-10">
											<ul class="pagination pagination-sm no-margin pull-right">
												<c:if test="${pager.existPrePage}">
													<li class="prev"><la:link
															href="list/${pager.currentPageNumber - 1}?dictId=${f:u(dictId)}">
															<la:message key="labels.prev_page" />
														</la:link></li>
												</c:if>
												<c:if test="${!pager.existPrePage}">
													<li class="prev disabled"><a href="#"><la:message
																key="labels.prev_page" /></a></li>
												</c:if>
												<c:forEach var="p" varStatus="s"
													items="${pager.pageNumberList}">
													<li
														<c:if test="${p == pager.currentPageNumber}">class="active"</c:if>><la:link
															href="list/${p}?dictId=${f:u(dictId)}">${p}</la:link></li>
												</c:forEach>
												<c:if test="${pager.existNextPage}">
													<li class="next"><la:link
															href="list/${pager.currentPageNumber + 1}?dictId=${f:u(dictId)}">
															<la:message key="labels.next_page" />
														</la:link></li>
												</c:if>
												<c:if test="${!pager.existNextPage}">
													<li class="next disabled"><a href="#"><la:message
																key="labels.next_page" /></a></li>
												</c:if>
											</ul>
										</div>
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
