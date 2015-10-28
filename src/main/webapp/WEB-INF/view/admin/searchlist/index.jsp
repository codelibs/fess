<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.search_list_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="log" />
			<jsp:param name="menuType" value="searchList" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.search_list_configuration" />
				</h1>
				<ol class="breadcrumb">
					<li class="active"><la:link href="/admin/searchlist">
							<la:message key="labels.search_list_configuration" />
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
									<la:message key="labels.search_list_configuration" />
								</h3>
								<la:form styleClass="form-inline">
									<div class="form-group">
										<label class="sr-only" for="sessionIdSearchBtn"></label>
										<la:text styleClass="query form-control" property="query" title="Search"
											size="50" maxlength="1000"
											placeholder="Type a search query" />
									</div>
									<div class="form-group">
										<button type="submit" class="btn btn-primary" name="search"
											value="<la:message key="labels.search"/>">
											<la:message key="labels.search" />
										</button>
									</div>
								</la:form>
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

								<%-- List --%>
								<c:choose>
									<c:when test="${allRecordCount == null}">
										<div id="subheader"></div>
										<div id="result">
											<%--
											<p>
												<la:message key="labels.search_list_index_page" />
											</p>
											--%>
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
																	<c:if test="${!crawlerProcessRunning}">
																		<la:link
																			href="confirmDelete?query=${f:u(query)}&docId=${f:u(doc.docId)}&url=${f:u(doc.url)}">
																			<la:message key="labels.search_list_delete_link" />
																		</la:link>
																	</c:if>
																	<c:if test="${crawlerProcessRunning}">
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
																href="prev?query=${f:u(query)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}&labelTypeValue=${f:u(labelTypeValue)}">
																<la:message key="labels.prev_page" />
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
														<li
															class="next<c:if test="${!existNextPage}"> disabled</c:if>">
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
												<la:message key="labels.did_not_match" arg0="${f:h(query)}" />
											</p>
										</div>
									</c:otherwise>
								</c:choose>
								<%-- Box Footer --%>
								<div class="box-footer"></div>
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
