<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.scheduledjob_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="scheduledJob" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.scheduledjob_title_details" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="index">
							<la:message key="labels.scheduledjob_link_list" />
						</la:link></li>
					<c:if test="${crudMode == 1}">
						<li class="active"><a href="#"><la:message key="labels.scheduledjob_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
						<li class="active"><a href="#"><la:message key="labels.scheduledjob_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
						<li class="active"><a href="#"><la:message key="labels.scheduledjob_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
						<li class="active"><a href="#"><la:message key="labels.scheduledjob_link_confirm" /></a></li>
					</c:if>
				</ol>
			</section>

			<section class="content">

				<%-- Form --%>
				<s:form>
					<html:hidden property="crudMode" />
					<c:if test="${crudMode==2}">
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
											<la:message key="labels.scheduledjob_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.scheduledjob_link_update" />
										</c:if>
									</h3>
									<div class="box-tools pull-right">
										<span class="label label-default"><la:link href="index">
												<la:message key="labels.scheduledjob_link_list" />
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
									<div class="form-group">
										<label for="name"><la:message key="labels.scheduledjob_name" /></label>
										<html:text property="name" styleClass="form-control" />
									</div>
									<div class="form-group">
											<label for="target"><la:message key="labels.scheduledjob_target" /></label>
											<html:text property="target" styleClass="form-control" />
									</div>
									<div class="form-group">
											<label for="cronExpression"><la:message key="labels.scheduledjob_cronExpression" /></label>
											<html:text property="cronExpression" styleClass="form-control" />
									</div>
									<div class="form-group">
											<label for="scriptType"><la:message key="labels.scheduledjob_scriptType" /></label>
											<html:text property="scriptType" styleClass="form-control" />
									</div>
									<div class="form-group">
											<label for="scriptData"><la:message key="labels.scheduledjob_scriptData" /></label>
											<html:textarea property="scriptData" styleClass="form-control" rows="5" />
									</div>
									<div class="form-group">
											<label for="jobLogging"><la:message key="labels.scheduledjob_jobLogging"/></label>
											<div styleClass="form-inline" >
													<html:checkbox property="jobLogging" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="crawler"><la:message key="labels.scheduledjob_crawler" /></label>
											<div styleClass="form-inline" >
													<html:checkbox property="crawler" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
											<label for="available"><la:message key="labels.scheduledjob_status" /></label>
											<div styleClass="form-inline" >
													<html:checkbox property="available" />
													<la:message key="labels.enabled"/>
											</div>
									</div>
									<div class="form-group">
										<label for="sortOrder"><la:message key="labels.sortOrder" /></label>
										<html:text property="sortOrder" styleClass="form-control" />
									</div>
									</div>

								</div>
								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 1}">
										<input type="submit" class="btn" name="back" value="<la:message key="labels.scheduledjob_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="confirmfromcreate"
											value="<la:message key="labels.scheduledjob_button_create"/>"
										/>
									</c:if>
									<c:if test="${crudMode == 2}">
										<input type="submit" class="btn" name="back" value="<la:message key="labels.scheduledjob_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="confirmfromupdate"
											value="<la:message key="labels.scheduledjob_button_confirm"/>"
										/>
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
