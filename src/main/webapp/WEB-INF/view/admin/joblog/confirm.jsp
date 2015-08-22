<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.joblog_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="jobLog" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.joblog_title_details" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="index">
							<la:message key="labels.joblog_link_list" />
						</la:link></li>
					<c:if test="${crudMode == 1}">
						<li class="active"><a href="#"><la:message key="labels.joblog_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
						<li class="active"><a href="#"><la:message key="labels.joblog_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
						<li class="active"><a href="#"><la:message key="labels.joblog_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
						<li class="active"><a href="#"><la:message key="labels.joblog_link_confirm" /></a></li>
					</c:if>
				</ol>
			</section>

			<section class="content">

				<%-- Form --%>
				<la:form>
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
						<la:hidden property="id" />
						<la:hidden property="versionNo" />
					</c:if>
					<la:hidden property="createdBy" />
					<la:hidden property="createdTime" />
					<div class="row">
						<div class="col-md-12">
							<div class="box">
								<%-- Box Header --%>
								<div class="box-header with-border">
									<h3 class="box-title">
										<c:if test="${crudMode == 1}">
											<la:message key="labels.joblog_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.joblog_link_update" />
										</c:if>
										<c:if test="${crudMode == 3}">
											<la:message key="labels.joblog_link_delete" />
										</c:if>
										<c:if test="${crudMode == 4}">
											<la:message key="labels.joblog_link_confirm" />
										</c:if>
									</h3>
									<div class="box-tools pull-right">
										<span class="label label-default"><la:link href="index">
												<la:message key="labels.joblog_link_list" />
											</la:link></span>
									</div>
								</div>
								<%-- Box Body --%>
								<div class="box-body">
									<%-- Message --%>
									<div>
										<la:info id="msg" message="true">
											<div class="alert-message info">
												<bean:write name="msg" ignore="true" />
											</div>
										</la:info>
										<la:errors />
									</div>

									<%-- Form Fields --%>
									<table class="table table-bordered">
										<tbody>
											<tr>
												<th class="col-xs-2"><la:message key="labels.joblog_jobName" /></th>
												<td>${f:h(jobName)}<la:hidden property="jobName" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.joblog_jobStatus" /></th>
												<td>${f:h(jobStatus)}<la:hidden property="jobStatus" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.joblog_target" /></th>
												<td>${f:h(target)}<la:hidden property="target" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.joblog_startTime" /></th>
												<td>${f:h(startTime)}<la:hidden property="startTime" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.joblog_endTime" /></th>
												<td>${f:h(endTime)}<la:hidden property="endTime" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.joblog_scriptType" /></th>
												<td>${f:h(scriptType)}<la:hidden property="scriptType" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.joblog_scriptData" /></th>
												<td>${f:br(f:h(scriptData))}<la:hidden property="scriptData" /></td>
											</tr>
											<tr>
												<th><la:message key="labels.joblog_scriptResult" /></th>
												<td>${f:br(f:h(scriptResult))}<la:hidden property="scriptResult" /></td>
											</tr>
										</tbody>
									</table>

								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 3}">
										<input type="submit" class="btn" name="delete" value="<la:message key="labels.joblog_button_delete"/>" />
										<input type="submit" class="btn" name="back" value="<la:message key="labels.joblog_button_back"/>" />
									</c:if>
									<c:if test="${crudMode == 4}">
										<input type="submit" class="btn" name="back" value="<la:message key="labels.joblog_button_back"/>" />
										<input type="submit" class="btn" name="deletefromconfirm" value="<la:message key="labels.joblog_button_delete"/>" />
									</c:if>
								</div>
							</div>
						</div>
					</div>
				</la:form>

			</section>
		</div>

		<jsp:include page="/WEB-INF/view/common/admin2/footer.jsp"></jsp:include>
	</div>
	<jsp:include page="/WEB-INF/view/common/admin2/foot.jsp"></jsp:include>
</body>
</html>

