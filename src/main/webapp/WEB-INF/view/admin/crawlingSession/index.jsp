<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <bean:message key="labels.crawling_session_configuration" /></title>
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
					<bean:message key="labels.crawling_session_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><s:link href="index">
							<bean:message key="labels.crawling_session_title" />
						</s:link></li>
				</ol>
			</section>

			<section class="content">

				<div class="row">
					<div class="col-md-12">
						<div class="box">
							<%-- Box Header --%>
							<div class="box-header with-border">
								<h3 class="box-title">
									<bean:message key="labels.crawling_session_title" />
								</h3>
							</div>
							<s:form>
								<div>
									<label for="sessionIdSearchBtn"><bean:message
											key="labels.crawling_session_session_id_search" /></label>
									<html:text styleId="sessionIdSearchBtn"
										property="searchParams.sessionId"></html:text>
									<input type="submit" class="btn small primary" name="search"
										value="<bean:message key="labels.crawling_session_search"/>" /> <input
										type="submit" class="btn small" name="reset"
										value="<bean:message key="labels.crawling_session_reset"/>" />
								</div>
							</s:form>
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
										<bean:message key="labels.list_could_not_find_crud_table" />
									</p>
								</c:if>
								<c:if test="${crawlingSessionPager.allRecordCount > 0}">
									<table class="table table-bordered table-striped">
										<thead>
											<tr>
												<th><bean:message key="labels.crawling_session_session_id" /></th>
												<th><bean:message key="labels.crawling_session_created_time" /></th>
												<th><bean:message key="labels.crawling_session_expired_time" /></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="data" varStatus="s" items="${crawlingSessionItems}">
												<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}" data-href="confirmpage/4/${f:u(data.id)}">
													<td>${f:h(data.sessionId)}</td>
													<td>${f:h(data.createdTime)}</td>
													<td>
														<c:if test="${data.expiredTime==null}"><bean:message key="labels.none" /></c:if>
														<c:if test="${data.expiredTime!=null}">${f:h(data.expiredTime)}</c:if>
													</td>
													<td style="overflow-x: auto;>
														<s:link href="confirmpage/4/${f:u(data.id)}">
															<bean:message key="labels.crawling_session_link_details" />
														</s:link>
														<s:link href="deletepage/3/${f:u(data.id)}">
															<bean:message key="labels.crawling_session_link_delete" />
														</s:link>
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
									if (confirm('<bean:message key="labels.crawling_session_delete_all_confirmation"/>')) {
										return true;
									} else {
										return false;
									}
								}
							// -->
							</script>
									<s:link href="deleteall" onclick="return confirmToDeleteAll();"
										styleClass="btn">
										<bean:message key="labels.crawling_session_delete_all_link" />
									</s:link>
								</div>
								<%-- Paging Info --%>
								<span><bean:message key="labels.pagination_page_guide_msg" arg0="${f:h(crawlingSessionPager.currentPageNumber)}"
										arg1="${f:h(crawlingSessionPager.allPageCount)}" arg2="${f:h(crawlingSessionPager.allRecordCount)}"
									/></span>

								<%-- Paging Navigation --%>
								<ul class="pagination pagination-sm no-margin pull-right">
									<c:if test="${crawlingSessionPager.existPrePage}">
										<li class="prev"><s:link href="list/${crawlingSessionPager.currentPageNumber - 1}">
												<bean:message key="labels.crud_link_prev_page" />
											</s:link></li>
									</c:if>
									<c:if test="${!crawlingSessionPager.existPrePage}">
										<li class="prev disabled"><a href="#"><bean:message key="labels.crud_link_prev_page" /></a></li>
									</c:if>
									<c:forEach var="p" varStatus="s" items="${crawlingSessionPager.pageNumberList}">
										<li <c:if test="${p == crawlingSessionPager.currentPageNumber}">class="active"</c:if>><s:link href="list/${p}">${p}</s:link>
										</li>
									</c:forEach>
									<c:if test="${crawlingSessionPager.existNextPage}">
										<li class="next"><s:link href="list/${crawlingSessionPager.currentPageNumber + 1}">
												<bean:message key="labels.crud_link_next_page" />
											</s:link></li>
									</c:if>
									<c:if test="${!crawlingSessionPager.existNextPage}">
										<li class="next disabled"><a href="#"><bean:message key="labels.crud_link_next_page" /></a></li>
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

