<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.data_crawling_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="dataConfig" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.data_crawling_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><la:link href="index">
							<la:message key="labels.data_crawling_link_list" />
						</la:link></li>
				</ol>
			</section>

			<section class="content">

				<div class="row">
					<div class="col-md-12">
						<div class="box">
							<%-- Box Header --%>
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.data_crawling_link_list" />
								</h3>
								<div class="box-tools pull-right">
									<span class="label label-default"><la:link href="createpage">
											<la:message key="labels.data_crawling_link_create_new" />
										</la:link></span>
								</div>
							</div>
							<%-- Box Body --%>
							<div class="box-body">
								<%-- Message --%>
								<div>
									<html:messages id="msg" message="true">
										<div class="alert-message info">
											<bean:write name="msg" ignore="true" />
										</div>
									</html:messages>
									<html:errors />
								</div>

								<%-- List --%>
								<c:if test="${dataConfigPager.allRecordCount == 0}">
									<p class="alert-message warning">
										<la:message key="labels.list_could_not_find_crud_table" />
									</p>
								</c:if>
								<c:if test="${dataConfigPager.allRecordCount > 0}">
									<table class="table table-bordered table-striped">
										<thead>
											<tr>
												<th><la:message key="labels.name" /></th>
												<th><la:message key="labels.available" /></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="data" varStatus="s" items="${dataConfigItems}">
												<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}" data-href="confirmpage/4/${f:u(data.id)}">
													<td>${f:h(data.name)}</td>
													<td style="text-align: center;">
														<c:if test="${data.available=='true'}">
															<la:message key="labels.enabled" />
														</c:if> <c:if test="${data.available=='false'}">
															<la:message key="labels.disabled" />
														</c:if>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</c:if>

							</div>
							<%-- Box Footer --%>
							<div class="box-footer">
								<%-- Paging Info --%>
								<span><la:message key="labels.pagination_page_guide_msg" arg0="${f:h(dataConfigPager.currentPageNumber)}"
										arg1="${f:h(dataConfigPager.allPageCount)}" arg2="${f:h(dataConfigPager.allRecordCount)}"
									/></span>

								<%-- Paging Navigation --%>
								<ul class="pagination pagination-sm no-margin pull-right">
									<c:if test="${dataConfigPager.existPrePage}">
										<li class="prev"><la:link href="list/${dataConfigPager.currentPageNumber - 1}">
												<la:message key="labels.data_crawling_link_prev_page" />
											</la:link></li>
									</c:if>
									<c:if test="${!dataConfigPager.existPrePage}">
										<li class="prev disabled"><a href="#"><la:message key="labels.data_crawling_link_prev_page" /></a></li>
									</c:if>
									<c:forEach var="p" varStatus="s" items="${dataConfigPager.pageNumberList}">
										<li <c:if test="${p == dataConfigPager.currentPageNumber}">class="active"</c:if>><la:link href="list/${p}">${p}</la:link>
										</li>
									</c:forEach>
									<c:if test="${dataConfigPager.existNextPage}">
										<li class="next"><la:link href="list/${dataConfigPager.currentPageNumber + 1}">
												<la:message key="labels.data_crawling_link_next_page" />
											</la:link></li>
									</c:if>
									<c:if test="${!dataConfigPager.existNextPage}">
										<li class="next disabled"><a href="#"><la:message key="labels.data_crawling_link_next_page" /></a></li>
									</c:if>
								</ul>

							</div>
						</div>
					</div>
				</div>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin2/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin2/foot.jsp"></jsp:include>
</body>
</html>

