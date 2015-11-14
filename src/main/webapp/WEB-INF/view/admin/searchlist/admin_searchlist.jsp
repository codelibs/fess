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
					<div class="col-xs-12">
						<div class="box box-primary">
							<%-- Box Header --%>
							<div class="box-header with-border">
								<h3 class="box-title">
									<la:message key="labels.search_list_configuration" />
								</h3>
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
								<la:form action="/admin/searchlist" styleClass="form-inline">
									<div class="form-group">
										<label class="sr-only" for="sessionIdSearchBtn"></label>
										<la:text styleClass="query form-control" property="query"
											title="Search" size="50" maxlength="1000"
											placeholder="Type a search query" />
									</div>
									<div class="form-group">
										<button type="submit" class="btn btn-primary" name="search"
											value="<la:message key="labels.search"/>">
											<i class="fa fa-search"></i>
											<la:message key="labels.search" />
										</button>
									</div>
								</la:form>
								<%-- List --%>
								<c:choose>
									<c:when test="${allRecordCount == null}">
									</c:when>
									<c:when test="${f:h(allRecordCount) > 0}">
										<div id="subheader" class="row top10">
											<div class="col-xs-12">
												<la:message key="labels.search_result_status"
													arg0="${f:h(query)}" arg1="${f:h(allRecordCount)}"
													arg2="${f:h(currentStartRecordNumber)}"
													arg3="${f:h(currentEndRecordNumber)}" />
												<c:if test="${execTime!=null}">
													<la:message key="labels.search_result_time"
														arg0="${f:h(execTime)}" />
												</c:if>
											</div>
										</div>
										<div id="result">
											<ol class="row">
												<c:forEach var="doc" varStatus="s" items="${documentItems}">
													<li class="col-sm-12">
														<h3 class="title">
															<a href="${doc.urlLink}">${f:h(doc.contentTitle)}</a>
														</h3>
														<div class="body col-sm-11">
															${doc.contentDescription}</div> <c:if
															test="${!crawlerProcessRunning}">
															<button type="button"
																class="btn btn-xs btn-danger col-sm-1"
																data-toggle="modal" data-target="#confirmToDelete">
																<i class="fa fa-trash"></i>
																<la:message key="labels.search_list_delete_link" />
															</button>
															<div class="modal modal-danger fade" id="confirmToDelete"
																tabindex="-1" role="dialog">
																<div class="modal-dialog">
																	<div class="modal-content">
																		<div class="modal-header">
																			<button type="button" class="close"
																				data-dismiss="modal" aria-label="Close">
																				<span aria-hidden="true">×</span>
																			</button>
																			<h4 class="modal-title">
																				<la:message key="labels.search_list_delete_link" />
																			</h4>
																		</div>
																		<div class="modal-body">
																			<p>
																				<la:message
																					key="labels.search_list_delete_confirmation"
																					arg0="${f:h(doc.urlLink)}" />
																			</p>
																		</div>
																		<div class="modal-footer">
																			<button type="button"
																				class="btn btn-outline pull-left"
																				data-dismiss="modal">
																				<la:message key="labels.search_list_delete_cancel" />
																			</button>
																			<la:form action="delete">
																				<%-- TODO: doc_id --%>
																				<la:hidden property="docId"
																					value="${f:u(doc.doc_id)}" />
																				<la:hidden property="query" value="${f:u(query)}" />
																				<button type="submit"
																					class="btn btn-outline btn-danger">
																					<i class="fa fa-trash"></i>
																					<la:message key="labels.search_list_delete_link" />
																				</button>
																			</la:form>
																		</div>
																	</div>
																</div>
															</div>
														</c:if> <c:if test="${crawlerProcessRunning}">
															<div class="col-sm-1">
																<la:message key="labels.search_list_delete_link" />
															</div>
														</c:if>
													</li>
												</c:forEach>
											</ol>
										</div>
										<div class="row">
											<div class="col-sm-12 text-center">
												<ul class="pagination pagination-sm">
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
										</div>
									</c:when>
									<c:otherwise>
										<div id="result" class="row top10">
											<div class="col-sm-12">
												<p class="callout callout-info">
													<la:message key="labels.did_not_match" arg0="${f:h(query)}" />
												</p>
											</div>
										</div>
									</c:otherwise>
								</c:choose>
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
