<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.elevate_word_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="suggest" />
			<jsp:param name="menuType" value="elevateWord" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.elevate_word_title_details" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="list">
							<la:message key="labels.elevate_word_link_list" />
						</la:link></li>
					<li class="active"><la:message
							key="labels.elevate_word_link_details" /></li>
				</ol>
			</section>
			<section class="content">
				<la:form action="/admin/elevateword/">
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
						<la:hidden property="id" />
						<la:hidden property="versionNo" />
					</c:if>
					<la:hidden property="createdBy" />
					<la:hidden property="createdTime" />
					<div class="row">
						<div class="col-md-12">
							<div
								class="box <c:if test="${crudMode == 1}">box-success</c:if><c:if test="${crudMode == 2}">box-warning</c:if><c:if test="${crudMode == 3}">box-danger</c:if><c:if test="${crudMode == 4}">box-primary</c:if>">
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<la:message key="labels.elevate_word_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.elevate_word_link_edit" />
										</c:if>
										<c:if test="${crudMode == 3}">
											<la:message key="labels.elevate_word_link_delete" />
										</c:if>
										<c:if test="${crudMode == 4}">
											<la:message key="labels.elevate_word_link_details" />
										</c:if>
									</h3>
									<div class="btn-group pull-right">
										<la:link href="/admin/elevateword"
											styleClass="btn btn-default btn-xs">
											<i class="fa fa-th-list"></i>
											<la:message key="labels.elevate_word_link_list" />
										</la:link>
										<la:link href="../createnew"
											styleClass="btn btn-success btn-xs">
											<i class="fa fa-plus"></i>
											<la:message key="labels.elevate_word_link_create" />
										</la:link>
										<la:link href="../downloadpage"
											styleClass="btn btn-primary btn-xs">
											<i class="fa fa-download"></i>
											<la:message key="labels.elevate_word_link_download" />
										</la:link>
										<la:link href="../uploadpage"
											styleClass="btn btn-success btn-xs">
											<i class="fa fa-upload"></i>
											<la:message key="labels.elevate_word_link_upload" />
										</la:link>
									</div>
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
									<%-- Form Fields --%>
									<table class="table table-bordered">
										<tbody>
											<tr>
												<th class="col-xs-2"><la:message
														key="labels.elevate_word_suggest_word" /></th>
												<td>${f:h(suggestWord)}<la:hidden
														property="suggestWord" /></td>
											</tr>
											<tr>
												<th><la:message
														key="labels.elevate_word_reading" /></th>
												<td>${f:h(reading)}<la:hidden property="reading" /></td>
											</tr>
											<tr>
												<th><la:message
														key="labels.elevate_word_target_role" /></th>
												<td>${f:h(targetRole)}<la:hidden property="targetRole" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.label_type" /></th>
												<td><c:forEach var="l" varStatus="s"
														items="${labelTypeItems}">
														<c:forEach var="ltid" varStatus="s"
															items="${labelTypeIds}">
															<c:if test="${ltid==l.id}">
																${f:h(l.name)}<br />
															</c:if>
														</c:forEach>
													</c:forEach></td>
											</tr>
											<tr>
												<th><la:message key="labels.elevate_word_boost" /></th>
												<td>${f:h(boost)}<la:hidden property="boost" /></td>
											</tr>
										</tbody>
									</table>
								</div>
								<!-- /.box-body -->
								<div class="box-footer">
									<jsp:include page="/WEB-INF/view/common/admin/crud/buttons.jsp"></jsp:include>
								</div>
								<!-- /.box-footer -->
							</div>
							<!-- /.box -->
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

