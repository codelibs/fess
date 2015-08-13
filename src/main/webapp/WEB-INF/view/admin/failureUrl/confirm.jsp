<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.failure_url_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="failureUrl" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.failure_url_title_details" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="index">
							<la:message key="labels.failure_url_link_list" />
						</la:link></li>
					<c:if test="${crudMode == 1}">
						<li class="active"><a href="#"><la:message key="labels.failure_url_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
						<li class="active"><a href="#"><la:message key="labels.failure_url_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
						<li class="active"><a href="#"><la:message key="labels.failure_url_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
						<li class="active"><a href="#"><la:message key="labels.failure_url_link_confirm" /></a></li>
					</c:if>
				</ol>
			</section>

			<section class="content">

				<%-- Form --%>
				<s:form>
					<html:hidden property="crudMode" />
					<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
						<html:hidden property="id" />
						<html:hidden property="versionNo" />
					</c:if>
					<html:hidden property="createdBy" />
					<html:hidden property="createdTime" />
					<div class="row">
						<div class="col-md-12">
							<div class="box">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<la:message key="labels.failure_url_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.failure_url_link_update" />
										</c:if>
										<c:if test="${crudMode == 3}">
											<la:message key="labels.failure_url_link_delete" />
										</c:if>
										<c:if test="${crudMode == 4}">
											<la:message key="labels.failure_url_link_confirm" />
										</c:if>
									</h3>
									<div class="box-tools pull-right">
										<span class="label label-default"><la:link href="index">
												<la:message key="labels.failure_url_link_list" />
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

									<%-- Form Fields --%>
									<table class="table table-bordered">
										<tbody>
											<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
												<tr>
													<th style="width: 150px;"><la:message
															key="labels.failure_url_id" /></th>
													<td style="width: 350px;">${f:h(id)}<html:hidden
															property="id" /></td>
												</tr>
											</c:if>
											<tr>
												<th><la:message key="labels.failure_url_url" /></th>
												<td><div style="width: 350px; overflow-x: auto;">
														${f:h(url)}
														<html:hidden property="url" />
													</div></td>
											</tr>
											<tr>
												<th><la:message key="labels.failure_url_thread_name" /></th>
												<td>${f:h(threadName)}<html:hidden property="threadName" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.failure_url_error_name" /></th>
												<td>${f:h(errorName)}<html:hidden property="errorName" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.failure_url_error_log" /></th>
												<td><div style="width: 350px; overflow-x: auto;">
														${f:br(f:nbsp(f:h(errorLog)))}
														<html:hidden property="errorLog" />
													</div></td>
											</tr>
											<tr>
												<th><la:message key="labels.failure_url_error_count" /></th>
												<td>${f:h(errorCount)}<html:hidden property="errorCount" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.failure_url_last_access_time" /></th>
												<td>${f:h(lastAccessTime)}<html:hidden
														property="lastAccessTime" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.failure_url_web_config_name" /></th>
												<td>${f:h(webConfigName)}</td>
											</tr>
											<tr>
												<th><la:message key="labels.failure_url_file_config_name" /></th>
												<td>${f:h(fileConfigName)}</td>
											</tr>
										</tbody>
									</table>

								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 3}">
										<input type="submit" class="btn small" name="delete" value="<la:message key="labels.crud_button_delete"/>" />
										<input type="submit" class="btn small" name="back" value="<la:message key="labels.crud_button_back"/>" />
									</c:if>
									<c:if test="${crudMode == 4}">
										<input type="submit" class="btn small" name="back" value="<la:message key="labels.crud_button_back"/>" />
										<input type="submit" class="btn small" name="deletefromconfirm" value="<la:message key="labels.crud_button_delete"/>" />
									</c:if>
								</div>
							</div>
						</div>
					</div>
				</s:form>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin2/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin2/foot.jsp"></jsp:include>
</body>
</html>

