<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.search_list_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="searchList" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.search_list_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><la:link href="index">
							<la:message key="labels.search_list_configuration" />
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
									<la:message key="labels.search_list_configuration" />
								</h3>
								<s:form action="search" method="get">
									<div class="input">
										<html:text styleClass="query" property="query" title="Search" size="50" maxlength="1000" />
										<input class="btn" type="submit" value="<la:message key="labels.search"/>" name="search" />
									</div>
								</s:form>
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
								<c:choose>
									<c:when test="${allRecordCount == null}">
										<div id="subheader"></div>
										<div id="result">
											<p>
												<la:message key="labels.search_list_index_page" />
											</p>
										</div>
									</c:when>
									<c:when test="${f:h(allRecordCount) != 0}">
										<div id="subheader">
											<p>
												<la:message key="labels.search_result_status"
													arg0="${f:h(query)}" arg1="${f:h(allRecordCount)}"
													arg2="${f:h(currentStartRecordNumber)}"
													arg3="${f:h(currentEndRecordNumber)}" />
												<c:if test="${execTime!=null}">
													<la:message key="labels.search_result_time"
														arg0="${f:h(execTime)}" />
												</c:if>
											</p>
										</div>
						
										<div id="result">
											<div>
												<ol>
													<c:forEach var="doc" varStatus="s" items="${documentItems}">
														<li>
															<h3 class="title">
																<a href="${doc.urlLink}">${f:h(doc.contentTitle)}</a>
															</h3>
															<div class="body">
																${doc.contentDescription}
																<div style="text-align: right;">
																	<c:if test="${!solrProcessRunning}">
																		<html:link
																			href="confirmDelete?query=${f:u(query)}&docId=${f:u(doc.docId)}&url=${f:u(doc.url)}">
																			<la:message key="labels.search_list_delete_link" />
																		</html:link>
																	</c:if>
																	<c:if test="${solrProcessRunning}">
																		<la:message key="labels.search_list_delete_link" />
																	</c:if>
																</div>
															</div>
														</li>
													</c:forEach>
												</ol>
											</div>
										</div>
						
										<div class="row center">
											<div class="pagination">
												<ul>
													<c:if test="${existPrePage}">
														<li class="prev"><la:link
																href="prev?query=${f:u(query)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}&labelTypeValue=${f:u(labelTypeValue)}"><la:message
																	key="labels.prev_page" />
															</la:link></li>
													</c:if>
													<c:if test="${!existPrePage}">
														<li class="prev disabled"><a href="#"><la:message
																	key="labels.prev_page" /></a></li>
													</c:if>
													<c:forEach var="pageNumber" varStatus="s"
														items="${pageNumberList}">
														<li
															<c:if test="${pageNumber == currentPageNumber}">class="active"</c:if>>
															<la:link
																href="move?query=${f:u(query)}&pn=${f:u(pageNumber)}&num=${f:u(pageSize)}&labelTypeValue=${f:u(labelTypeValue)}">${f:h(pageNumber)}</la:link>
														</li>
													</c:forEach>
													<c:if test="${existNextPage}">
														<li class="next<c:if test="${!existNextPage}"> disabled</c:if>">
															<la:link
																href="next?query=${f:u(query)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}&labelTypeValue=${f:u(labelTypeValue)}">
																<la:message key="labels.next_page" />
														</la:link>
														</li>
													</c:if>
													<c:if test="${!existNextPage}">
														<li class="next disabled"><a href="#"><la:message
																	key="labels.next_page" /></a></li>
													</c:if>
												</ul>
											</div>
											<div>
												<span> ${currentPageNumber}/${allPageCount}
													(${allRecordCount}) </span>
											</div>
										</div>
									</c:when>
									<c:otherwise>
										<div id="subheader"></div>
										<div id="result">
											<p>
												<la:message key="labels.did_not_match"
													arg0="${f:h(query)}" />
											</p>
										</div>
									</c:otherwise>
								</c:choose>
							<%-- Box Footer --%>
							<div class="box-footer">

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

