<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.failure_url_configuration" /></title>
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

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.failure_url_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><la:link href="index">
							<la:message key="labels.failure_url_configuration" />
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
									<la:message key="labels.failure_url_configuration" />
								</h3>
								<la:form>
									<table class="bordered-table zebra-striped">
										<tbody>
											<tr>
												<th><la:message key="labels.failure_url_search_url" /></th>
												<td><la:text property="searchParams.url"></la:text></td>
											</tr>
											<tr>
												<th><la:message
														key="labels.failure_url_search_error_count" /></th>
												<td><la:text property="searchParams.errorCountMin"
														size="2" styleClass="mini"></la:text> - <la:text
														property="searchParams.errorCountMax" size="2"
														styleClass="mini"></la:text></td>
											<tr>
												<th><la:message key="labels.failure_url_search_error_name" /></th>
												<td><la:text property="searchParams.errorName"></la:text></td>
											</tr>
										</tbody>
									</table>
									<div class="row">
										<div class="span8 offset4">
											<input type="submit" class="btn small primary" name="search"
												value="<la:message key="labels.crud_button_search"/>" /> <input
												type="submit" class="btn small" name="reset"
												value="<la:message key="labels.crud_button_reset"/>" />
										</div>
									</div>
								</la:form>
							</div>
							<%-- Box Body --%>
							<div class="box-body">
								<%-- Message --%>
								<div>
									<la:info id="msg" message="true">
										<div class="alert-message info">
											${msg}
										</div>
									</la:info>
									<la:errors />
								</div>

								<%-- List --%>
								<c:if test="${failureUrlPager.allRecordCount == 0}">
									<p class="alert-message warning">
										<la:message key="labels.list_could_not_find_crud_table" />
									</p>
								</c:if>
								<c:if test="${failureUrlPager.allRecordCount > 0}">
									<table class="bordered-table zebra-striped">
										<thead>
											<tr>
												<th><la:message key="labels.failure_url_url" /></th>
												<th><la:message key="labels.failure_url_last_access_time" /></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="data" varStatus="s" items="${failureUrlItems}">
												<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}">
													<td><div style="width: 380px; overflow-x: auto;">${f:h(data.url)}</div></td>
													<td>${f:h(data.lastAccessTimeForList)}</td>
													<td style="overflow-x: auto;">
														<la:link	href="confirmpage/4/${f:u(data.id)}">
															<la:message key="labels.failure_url_link_confirm" />
														</la:link>
														<la:link href="deletepage/3/${f:u(data.id)}">
															<la:message key="labels.crud_link_delete" />
														</la:link>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</c:if>

							</div>
							<%-- Box Footer --%>
							<div class="box-footer">
								<div class="span12 center">
									<script>
							<!--
								function confirmToDeleteAll() {
									if (confirm('<la:message key="labels.failure_url_delete_all_confirmation"/>')) {
										return true;
									} else {
										return false;
									}
								}
							// -->
							</script>
									<la:link href="deleteall" onclick="return confirmToDeleteAll();"
										styleClass="btn">
										<la:message key="labels.search_log_delete_all_link" />
									</la:link>
								</div>
								<%-- Paging Info --%>
								<span><la:message key="labels.pagination_page_guide_msg" arg0="${f:h(failureUrlPager.currentPageNumber)}"
										arg1="${f:h(failureUrlPager.allPageCount)}" arg2="${f:h(failureUrlPager.allRecordCount)}"
									/></span>

								<%-- Paging Navigation --%>
								<ul class="pagination pagination-sm no-margin pull-right">
									<c:if test="${failureUrlPager.existPrePage}">
										<li class="prev"><la:link href="list/${failureUrlPager.currentPageNumber - 1}">
												<la:message key="labels.crud_link_prev_page" />
											</la:link></li>
									</c:if>
									<c:if test="${!failureUrlPager.existPrePage}">
										<li class="prev disabled"><a href="#"><la:message key="labels.crud_link_prev_page" /></a></li>
									</c:if>
									<c:forEach var="p" varStatus="s" items="${failureUrlPager.pageNumberList}">
										<li <c:if test="${p == failureUrlPager.currentPageNumber}">class="active"</c:if>><la:link href="list/${p}">${p}</la:link>
										</li>
									</c:forEach>
									<c:if test="${failureUrlPager.existNextPage}">
										<li class="next"><la:link href="list/${failureUrlPager.currentPageNumber + 1}">
												<la:message key="labels.crud_link_next_page" />
											</la:link></li>
									</c:if>
									<c:if test="${!failureUrlPager.existNextPage}">
										<li class="next disabled"><a href="#"><la:message key="labels.crud_link_next_page" /></a></li>
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

