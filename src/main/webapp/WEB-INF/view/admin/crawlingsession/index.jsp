<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.crawling_session_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="crawlingSession" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.crawling_session_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><la:link href="index">
							<la:message key="labels.crawling_session_title" />
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
									<la:message key="labels.crawling_session_title" />
								</h3>
								<la:form>
										<label for="sessionIdSearchBtn"><la:message
												key="labels.crawling_session_session_id_search" /></label>
										<la:text styleId="sessionIdSearchBtn"
											property="searchParams.sessionId"></la:text>
										<input type="submit" class="btn small primary" name="search"
											value="<la:message key="labels.crawling_session_search"/>" /> <input
											type="submit" class="btn small" name="reset"
											value="<la:message key="labels.crawling_session_reset"/>" />
								</la:form>
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
								<c:if test="${crawlingSessionPager.allRecordCount == 0}">
									<p class="alert-message warning">
										<la:message key="labels.list_could_not_find_crud_table" />
									</p>
								</c:if>
								<c:if test="${crawlingSessionPager.allRecordCount > 0}">
									<table class="table table-bordered table-striped">
										<thead>
											<tr>
												<th><la:message key="labels.crawling_session_session_id" /></th>
												<th><la:message key="labels.crawling_session_created_time" /></th>
												<th><la:message key="labels.crawling_session_expired_time" /></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="data" varStatus="s" items="${crawlingSessionItems}">
												<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}" data-href="confirmpage/4/${f:u(data.id)}">
													<td>${f:h(data.sessionId)}</td>
													<td>${f:h(data.createdTime)}</td>
													<td>
														<c:if test="${data.expiredTime==null}"><la:message key="labels.none" /></c:if>
														<c:if test="${data.expiredTime!=null}">${f:h(data.expiredTime)}</c:if>
													</td>
													<td style="overflow-x: auto;>
														<la:link href="confirmpage/4/${f:u(data.id)}">
															<la:message key="labels.crawling_session_link_details" />
														</la:link>
														<la:link href="deletepage/3/${f:u(data.id)}">
															<la:message key="labels.crawling_session_link_delete" />
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
									if (confirm('<la:message key="labels.crawling_session_delete_all_confirmation"/>')) {
										return true;
									} else {
										return false;
									}
								}
							// -->
							</script>
									<la:link href="deleteall" onclick="return confirmToDeleteAll();"
										styleClass="btn">
										<la:message key="labels.crawling_session_delete_all_link" />
									</la:link>
								</div>
								<%-- Paging Info --%>
								<span><la:message key="labels.pagination_page_guide_msg" arg0="${f:h(crawlingSessionPager.currentPageNumber)}"
										arg1="${f:h(crawlingSessionPager.allPageCount)}" arg2="${f:h(crawlingSessionPager.allRecordCount)}"
									/></span>

								<%-- Paging Navigation --%>
								<ul class="pagination pagination-sm no-margin pull-right">
									<c:if test="${crawlingSessionPager.existPrePage}">
										<li class="prev"><la:link href="list/${crawlingSessionPager.currentPageNumber - 1}">
												<la:message key="labels.crud_link_prev_page" />
											</la:link></li>
									</c:if>
									<c:if test="${!crawlingSessionPager.existPrePage}">
										<li class="prev disabled"><a href="#"><la:message key="labels.crud_link_prev_page" /></a></li>
									</c:if>
									<c:forEach var="p" varStatus="s" items="${crawlingSessionPager.pageNumberList}">
										<li <c:if test="${p == crawlingSessionPager.currentPageNumber}">class="active"</c:if>><la:link href="list/${p}">${p}</la:link>
										</li>
									</c:forEach>
									<c:if test="${crawlingSessionPager.existNextPage}">
										<li class="next"><la:link href="list/${crawlingSessionPager.currentPageNumber + 1}">
												<la:message key="labels.crud_link_next_page" />
											</la:link></li>
									</c:if>
									<c:if test="${!crawlingSessionPager.existNextPage}">
										<li class="next disabled"><a href="#"><la:message key="labels.crud_link_next_page" /></a></li>
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

