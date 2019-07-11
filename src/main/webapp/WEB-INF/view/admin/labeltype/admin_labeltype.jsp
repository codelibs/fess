<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.labeltype_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="labelType" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.labeltype_configuration" />
				</h1>
				<jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
			</section>
			<section class="content">
				<div class="row">
					<div class="col-md-12">
						<div class="box box-primary">
							<div class="box-header with-border">
								<jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
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
								<c:if test="${labelTypePager.allRecordCount == 0}">
									<div class="row top10">
										<div class="col-sm-12">
											<em class="fa fa-info-circle text-light-blue"></em>
											<la:message key="labels.list_could_not_find_crud_table" />
										</div>
									</div>
								</c:if>
								<c:if test="${labelTypePager.allRecordCount > 0}">
									<div class="row">
										<div class="col-sm-12">
											<table class="table table-bordered table-striped">
												<thead>
													<tr>
														<th><la:message key="labels.labeltype_name" /></th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="data" varStatus="s"
														items="${labelTypeItems}">
														<tr
															data-href="${contextPath}/admin/labeltype/details/4/${f:u(data.id)}">
															<td>${f:h(data.name)}</td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
									<div class="row">
										<%-- Paging Info --%>
										<div class="col-sm-2">
											<la:message key="labels.pagination_page_guide_msg"
												arg0="${f:h(labelTypePager.currentPageNumber)}"
												arg1="${f:h(labelTypePager.allPageCount)}"
												arg2="${f:h(labelTypePager.allRecordCount)}" />
										</div>
										<%-- Paging Navigation --%>
										<div class="col-sm-10">
											<ul class="pagination pagination-sm no-margin pull-right">
												<c:if test="${labelTypePager.existPrePage}">
													<li class="prev"><la:link
															href="list/${labelTypePager.currentPageNumber - 1}">
															<la:message key="labels.prev_page" />
														</la:link></li>
												</c:if>
												<c:if test="${!labelTypePager.existPrePage}">
													<li class="prev disabled"><a href="#"><la:message
																key="labels.prev_page" /></a></li>
												</c:if>
												<c:forEach var="p" varStatus="s"
													items="${labelTypePager.pageNumberList}">
													<li
														<c:if test="${p == labelTypePager.currentPageNumber}">class="active"</c:if>><la:link
															href="list/${p}">${p}</la:link></li>
												</c:forEach>
												<c:if test="${labelTypePager.existNextPage}">
													<li class="next"><la:link
															href="list/${labelTypePager.currentPageNumber + 1}">
															<la:message key="labels.next_page" />
														</la:link></li>
												</c:if>
												<c:if test="${!labelTypePager.existNextPage}">
													<li class="next disabled"><a href="#"><la:message
																key="labels.next_page" /></a></li>
												</c:if>
											</ul>
										</div>
									</div>
									<c:if test="${labelTypePager.currentPageNumber > labelTypePager.allPageCount}">
										<script>location.href = "${contextPath}/admin/labeltype/list/${labelTypePager.allPageCount}";</script>
									</c:if>
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
