<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Fess | <la:message key="labels.web_authentication_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin2/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin2/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin2/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="crawl" />
			<jsp:param name="menuType" value="webAuthentication" />
		</jsp:include>

		<div class="content-wrapper">

			<%-- Content Header --%>
			<section class="content-header">
				<h1>
					<la:message key="labels.web_authentication_title_details" />
				</h1>
				<ol class="breadcrumb">
					<li><la:link href="index">
							<la:message key="labels.web_authentication_link_list" />
						</la:link></li>
					<c:if test="${crudMode == 1}">
						<li class="active"><a href="#"><la:message key="labels.web_authentication_link_create" /></a></li>
					</c:if>
					<c:if test="${crudMode == 2}">
						<li class="active"><a href="#"><la:message key="labels.web_authentication_link_update" /></a></li>
					</c:if>
					<c:if test="${crudMode == 3}">
						<li class="active"><a href="#"><la:message key="labels.web_authentication_link_delete" /></a></li>
					</c:if>
					<c:if test="${crudMode == 4}">
						<li class="active"><a href="#"><la:message key="labels.web_authentication_link_confirm" /></a></li>
					</c:if>
				</ol>
			</section>

			<section class="content">

				<%-- Form --%>
				<la:form>
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2}">
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
											<la:message key="labels.web_authentication_link_create" />
										</c:if>
										<c:if test="${crudMode == 2}">
											<la:message key="labels.web_authentication_link_update" />
										</c:if>
									</h3>
									<div class="box-tools pull-right">
										<span class="label label-default"><la:link href="index">
												<la:message key="labels.web_authentication_link_list" />
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
										<label for="hostname"><la:message key="labels.web_authentication_hostname" /></label>
										<la:text property="hostname" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="port"><la:message key="labels.web_authentication_port" /></label>
										<la:text property="port" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="authRealm"><la:message key="labels.web_authentication_realm" /></label>
										<la:text property="authRealm" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="protocolScheme"><la:message key="labels.web_authentication_scheme" /></label>
										<html:select property="protocolScheme" styleClass="form-control">
												<c:forEach var="item" items="${protocolSchemeItems}">
														<html:option value="${f:u(item.value)}">${f:h(item.label)}</html:option>
												</c:forEach>
										</html:select>
									</div>
									<div class="form-group">
										<label for="username"><la:message key="labels.web_authentication_username" /></label>
										<la:text property="username" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="password"><la:message key="labels.web_authentication_password" /></label>
										<html:password property="password" styleClass="form-control" />
									</div>
									<div class="form-group">
										<label for="parameters"><la:message key="labels.web_authentication_parameters" /></label>
										<la:textarea property="parameters" styleClass="form-control" rows="5" />
									</div>
									<div class="form-group">
										<label for="webConfigId"><la:message key="labels.web_authentication_web_crawling_config" /></label>
										<html:select property="webConfigId" styleClass="form-control">
												<c:forEach var="item" items="${webConfigItems}">
														<html:option value="${f:u(item.value)}">${f:h(item.label)}</html:option>
												</c:forEach>
										</html:select>
									</div>
								</div>

								<%-- Box Footer --%>
								<div class="box-footer">
									<c:if test="${crudMode == 1}">
										<input type="submit" class="btn" name="back" value="<la:message key="labels.web_authentication_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="confirmfromcreate"
											value="<la:message key="labels.web_authentication_button_create"/>"
										/>
									</c:if>
									<c:if test="${crudMode == 2}">
										<input type="submit" class="btn" name="back" value="<la:message key="labels.web_authentication_button_back"/>" />
										<input type="submit" class="btn btn-primary" name="confirmfromupdate"
											value="<la:message key="labels.web_authentication_button_confirm"/>"
										/>
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
