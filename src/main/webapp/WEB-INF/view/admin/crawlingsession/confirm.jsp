<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.crawling_session_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="log" />
			<jsp:param name="menuType" value="crawlingSession" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.crawling_session_title_confirm" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="/admin/crawlingsession">
							<la:message key="labels.crawling_session_link_list" />
						</la:link></li>
					<c:if test="${crudMode == 1}">
						<li class="active"><a href="#"><la:message
									key="labels.crawling_session_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
						<li class="active"><a href="#"><la:message
									key="labels.crawling_session_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
						<li class="active"><a href="#"><la:message
									key="labels.crawling_session_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
						<li class="active"><a href="#"><la:message
									key="labels.crawling_session_link_details" /></a></li>
					</c:if>
				</ol>
			</section>

			<section class="content">

				<%-- Form --%>
				<la:form>
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
						<la:hidden property="id" />
					</c:if>
					<div class="row">
						<div class="col-md-12">
							<div
								class="box <c:if test="${crudMode == 1}">box-success</c:if><c:if test="${crudMode == 2}">box-warning</c:if><c:if test="${crudMode == 3}">box-danger</c:if><c:if test="${crudMode == 4}">box-primary</c:if>">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<la:message key="labels.crawling_session_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.crawling_session_link_update" />
										</c:if>
										<c:if test="${crudMode == 3}">
											<la:message key="labels.crawling_session_link_delete" />
										</c:if>
										<c:if test="${crudMode == 4}">
											<la:message key="labels.crawling_session_link_details" />
										</c:if>
									</h3>
									<div class="box-tools pull-right">
										<span class="label label-default"><la:link
												href="/admin/crawlingsession">
												<la:message key="labels.crawling_session_link_list" />
											</la:link></span>
									</div>
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

									<%-- Form Fields --%>
									<table class="table table-bordered">
										<tbody>
											<tr>
												<th><la:message
														key="labels.crawling_session_session_id" /></th>
												<td><a href="${f:url('/admin/searchlist/search')}?query=segment:${f:u(sessionId)}">${f:h(sessionId)}</a>
													<la:hidden property="sessionId" /></td>
											</tr>
											<c:forEach var="info" items="${crawlingSessionInfoItems}">
												<tr>
													<th>${f:h(info.keyMsg)}</th>
													<td>${f:h(info.value)}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>

								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 1}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.crawling_session_button_back" />">
											<la:message key="labels.crawling_session_button_back" />
										</button>
									</c:if>
									<c:if test="${crudMode == 2}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.crawling_session_button_back" />">
											<la:message key="labels.crawling_session_button_back" />
										</button>
									</c:if>
									<c:if test="${crudMode == 3}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.crawling_session_button_back" />">
											<la:message key="labels.crawling_session_button_back" />
										</button>
										<button type="submit" class="btn btn-danger" name="delete"
											value="<la:message key="labels.crawling_session_button_delete" />">
											<la:message key="labels.crawling_session_button_delete" />
										</button>
									</c:if>
									<c:if test="${crudMode == 4}">
										<button type="submit" class="btn" name="back"
											value="<la:message key="labels.crawling_session_button_back" />">
											<la:message key="labels.crawling_session_button_back" />
										</button>
										<button type="submit" class="btn btn-danger" name="deletefromconfirm"
											value="<la:message key="labels.crawling_session_button_delete" />">
											<la:message key="labels.crawling_session_button_delete" />
										</button>
									</c:if>
								</div>
							</div>
						</div>
					</div>
				</la:form>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>

