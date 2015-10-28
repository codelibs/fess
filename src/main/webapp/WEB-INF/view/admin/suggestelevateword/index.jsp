<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title"/> | <la:message key="labels.suggest_elevate_word_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="suggest" />
			<jsp:param name="menuType" value="suggestElevateWord" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.suggest_elevate_word_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><la:link href="/admin/suggestelevateword">
							<la:message key="labels.suggest_elevate_word_link_list" />
						</la:link></li>
				</ol>
			</section>

			<section class="content">

				<div class="row">
					<div class="col-md-12">
						<div class="box box-primary">
							<%-- Box Header --%>
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.suggest_elevate_word_link_list" />
								</h3>
								<div class="box-tools pull-right">
									<span class="label label-default"><la:link href="/admin/suggestelevateword">
										<la:message key="labels.suggest_elevate_word_link_list" />
										</la:link></span>
									<span class="label label-default"><la:link href="createpage">
										<la:message key="labels.suggest_elevate_word_link_create_new" />
										</la:link></span>
									<span class="label label-default"><la:link href="downloadpage">
										<la:message key="labels.suggest_elevate_word_link_download" />
										</la:link></span>
									<span class="label label-default"><la:link href="uploadpage">
										<la:message key="labels.suggest_elevate_word_link_upload" />
										</la:link></span>
								</div>
							</div>
							<%-- Box Body --%>
							<div class="box-body">
								<%-- Message --%>
								<div>
									<la:info id="msg" message="true">
										<div class="alert alert-info">
											${msg}
										</div>
									</la:info>
									<la:errors />
								</div>

								<%-- List --%>
								<c:if test="${suggestElevateWordPager.allRecordCount == 0}">
									<p class="callout callout-info">
										<la:message key="labels.list_could_not_find_crud_table" />
									</p>
								</c:if>
								<c:if test="${suggestElevateWordPager.allRecordCount > 0}">
									<table class="table table-bordered table-striped">
										<thead>
											<tr>
												<th><la:message key="labels.suggest_elevate_word_list_suggest_word" /></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="data" varStatus="s" items="${suggestElevateWordItems}">
												<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}" data-href="${contextPath}/admin/suggestelevateword/confirmpage/4/${f:u(data.id)}">
													<td style="overflow-x: auto;">${f:h(data.suggestWord)}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</c:if>

							</div>
							<%-- Box Footer --%>
							<div class="box-footer">
								<%-- Paging Info --%>
								<span><la:message key="labels.pagination_page_guide_msg" arg0="${f:h(suggestElevateWordPager.currentPageNumber)}"
										arg1="${f:h(suggestElevateWordPager.allPageCount)}" arg2="${f:h(suggestElevateWordPager.allRecordCount)}"
									/></span>

								<%-- Paging Navigation --%>
								<ul class="pagination pagination-sm no-margin pull-right">
									<c:if test="${suggestElevateWordPager.existPrePage}">
										<li class="prev"><la:link href="list/${suggestElevateWordPager.currentPageNumber - 1}">
												<la:message key="labels.suggest_elevate_word_link_prev_page" />
											</la:link></li>
									</c:if>
									<c:if test="${!suggestElevateWordPager.existPrePage}">
										<li class="prev disabled"><a href="#"><la:message key="labels.suggest_elevate_word_link_prev_page" /></a></li>
									</c:if>
									<c:forEach var="p" varStatus="s" items="${suggestElevateWordPager.pageNumberList}">
										<li <c:if test="${p == suggestElevateWordPager.currentPageNumber}">class="active"</c:if>><la:link href="list/${p}">${p}</la:link>
										</li>
									</c:forEach>
									<c:if test="${suggestElevateWordPager.existNextPage}">
										<li class="next"><la:link href="list/${suggestElevateWordPager.currentPageNumber + 1}">
												<la:message key="labels.suggest_elevate_word_link_next_page" />
											</la:link></li>
									</c:if>
									<c:if test="${!suggestElevateWordPager.existNextPage}">
										<li class="next disabled"><a href="#"><la:message key="labels.suggest_elevate_word_link_next_page" /></a></li>
									</c:if>
								</ul>

							</div>
						</div>
					</div>
				</div>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>

