<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.user_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="skin-blue sidebar-mini">
	<div class="wrapper">
		<jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
		<jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
			<jsp:param name="menuCategoryType" value="user" />
			<jsp:param name="menuType" value="user" />
		</jsp:include>
		<div class="content-wrapper">
			<section class="content-header">
				<h1>
					<la:message key="labels.user_title_details" />
				</h1>
				<jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
			</section>
			<section class="content">
				<la:form action="/admin/user/" styleClass="form-horizontal">
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2}">
						<la:hidden property="id" />
						<la:hidden property="versionNo" />
					</c:if>
					<div class="row">
						<div class="col-md-12">
							<div
								class="box <c:if test="${crudMode == 1}">box-success</c:if><c:if test="${crudMode == 2}">box-warning</c:if>">
								<div class="box-header with-border">
									<jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
								</div>
								<!-- /.box-header -->
								<div class="box-body">
									<div>
										<la:info id="msg" message="true">
											<div class="alert alert-info">${msg}</div>
										</la:info>
										<la:errors property="_global" />
									</div>
									<div class="form-group">
										<label for="name" class="col-sm-3 control-label"><la:message
												key="labels.user_name" /></label>
										<div class="col-sm-9">
											<la:errors property="name" />
											<c:if test="${crudMode==1}">
												<la:text property="name" styleClass="form-control" />
											</c:if>
											<c:if test="${crudMode==2}">
											${f:h(name)}
											<la:hidden property="name" styleClass="form-control" />
											</c:if>
										</div>
									</div>
									<div class="form-group">
										<label for="password" class="col-sm-3 control-label"><la:message
												key="labels.user_password" /></label>
										<div class="col-sm-9">
											<la:errors property="password" />
											<la:password property="password" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="confirm_password" class="col-sm-3 control-label"><la:message
												key="labels.user_confirm_password" /></label>
										<div class="col-sm-9">
											<la:errors property="confirmPassword" />
											<la:password property="confirmPassword"
												styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="surname" class="col-sm-3 control-label"><la:message
												key="labels.user_surname" /></label>
										<div class="col-sm-9">
											<la:errors property="surname" />
											<la:text property="surname" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="givenName" class="col-sm-3 control-label"><la:message
												key="labels.user_given_name" /></label>
										<div class="col-sm-9">
											<la:errors property="givenName" />
											<la:text property="givenName" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="mail" class="col-sm-3 control-label"><la:message
												key="labels.user_mail" /></label>
										<div class="col-sm-9">
											<la:errors property="mail" />
											<la:text property="mail" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="employeeNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_employeeNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="employeeNumber" />
											<la:text property="employeeNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="telephoneNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_telephoneNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="telephoneNumber" />
											<la:text property="telephoneNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="homePhone" class="col-sm-3 control-label"><la:message
												key="labels.user_homePhone" /></label>
										<div class="col-sm-9">
											<la:errors property="homePhone" />
											<la:text property="homePhone" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="homePostalAddress" class="col-sm-3 control-label"><la:message
												key="labels.user_homePostalAddress" /></label>
										<div class="col-sm-9">
											<la:errors property="homePostalAddress" />
											<la:text property="homePostalAddress" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="labeledURI" class="col-sm-3 control-label"><la:message
												key="labels.user_labeledURI" /></label>
										<div class="col-sm-9">
											<la:errors property="labeledURI" />
											<la:text property="labeledURI" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="roomNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_roomNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="roomNumber" />
											<la:text property="roomNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="description" class="col-sm-3 control-label"><la:message
												key="labels.user_description" /></label>
										<div class="col-sm-9">
											<la:errors property="description" />
											<la:text property="description" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="title" class="col-sm-3 control-label"><la:message
												key="labels.user_title" /></label>
										<div class="col-sm-9">
											<la:errors property="title" />
											<la:text property="title" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="pager" class="col-sm-3 control-label"><la:message
												key="labels.user_pager" /></label>
										<div class="col-sm-9">
											<la:errors property="pager" />
											<la:text property="pager" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="street" class="col-sm-3 control-label"><la:message
												key="labels.user_street" /></label>
										<div class="col-sm-9">
											<la:errors property="street" />
											<la:text property="street" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="postalCode" class="col-sm-3 control-label"><la:message
												key="labels.user_postalCode" /></label>
										<div class="col-sm-9">
											<la:errors property="postalCode" />
											<la:text property="postalCode" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="physicalDeliveryOfficeName" class="col-sm-3 control-label"><la:message
												key="labels.user_physicalDeliveryOfficeName" /></label>
										<div class="col-sm-9">
											<la:errors property="physicalDeliveryOfficeName" />
											<la:text property="physicalDeliveryOfficeName" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="destinationIndicator" class="col-sm-3 control-label"><la:message
												key="labels.user_destinationIndicator" /></label>
										<div class="col-sm-9">
											<la:errors property="destinationIndicator" />
											<la:text property="destinationIndicator" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="internationaliSDNNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_internationaliSDNNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="internationaliSDNNumber" />
											<la:text property="internationaliSDNNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="state" class="col-sm-3 control-label"><la:message
												key="labels.user_state" /></label>
										<div class="col-sm-9">
											<la:errors property="state" />
											<la:text property="state" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="employeeType" class="col-sm-3 control-label"><la:message
												key="labels.user_employeeType" /></label>
										<div class="col-sm-9">
											<la:errors property="employeeType" />
											<la:text property="employeeType" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="facsimileTelephoneNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_facsimileTelephoneNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="facsimileTelephoneNumber" />
											<la:text property="facsimileTelephoneNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="postOfficeBox" class="col-sm-3 control-label"><la:message
												key="labels.user_postOfficeBox" /></label>
										<div class="col-sm-9">
											<la:errors property="postOfficeBox" />
											<la:text property="postOfficeBox" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="initials" class="col-sm-3 control-label"><la:message
												key="labels.user_initials" /></label>
										<div class="col-sm-9">
											<la:errors property="initials" />
											<la:text property="initials" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="carLicense" class="col-sm-3 control-label"><la:message
												key="labels.user_carLicense" /></label>
										<div class="col-sm-9">
											<la:errors property="carLicense" />
											<la:text property="carLicense" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="mobile" class="col-sm-3 control-label"><la:message
												key="labels.user_mobile" /></label>
										<div class="col-sm-9">
											<la:errors property="mobile" />
											<la:text property="mobile" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="postalAddress" class="col-sm-3 control-label"><la:message
												key="labels.user_postalAddress" /></label>
										<div class="col-sm-9">
											<la:errors property="postalAddress" />
											<la:text property="postalAddress" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="city" class="col-sm-3 control-label"><la:message
												key="labels.user_city" /></label>
										<div class="col-sm-9">
											<la:errors property="city" />
											<la:text property="city" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="teletexTerminalIdentifier" class="col-sm-3 control-label"><la:message
												key="labels.user_teletexTerminalIdentifier" /></label>
										<div class="col-sm-9">
											<la:errors property="teletexTerminalIdentifier" />
											<la:text property="teletexTerminalIdentifier" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="x121Address" class="col-sm-3 control-label"><la:message
												key="labels.user_x121Address" /></label>
										<div class="col-sm-9">
											<la:errors property="x121Address" />
											<la:text property="x121Address" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="businessCategory" class="col-sm-3 control-label"><la:message
												key="labels.user_businessCategory" /></label>
										<div class="col-sm-9">
											<la:errors property="businessCategory" />
											<la:text property="businessCategory" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="registeredAddress" class="col-sm-3 control-label"><la:message
												key="labels.user_registeredAddress" /></label>
										<div class="col-sm-9">
											<la:errors property="registeredAddress" />
											<la:text property="registeredAddress" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="displayName" class="col-sm-3 control-label"><la:message
												key="labels.user_displayName" /></label>
										<div class="col-sm-9">
											<la:errors property="displayName" />
											<la:text property="displayName" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="preferredLanguage" class="col-sm-3 control-label"><la:message
												key="labels.user_preferredLanguage" /></label>
										<div class="col-sm-9">
											<la:errors property="preferredLanguage" />
											<la:text property="preferredLanguage" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="departmentNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_departmentNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="departmentNumber" />
											<la:text property="departmentNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="uidNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_uidNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="uidNumber" />
											<la:text property="uidNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="gidNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_gidNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="gidNumber" />
											<la:text property="gidNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="homeDirectory" class="col-sm-3 control-label"><la:message
												key="labels.user_homeDirectory" /></label>
										<div class="col-sm-9">
											<la:errors property="homeDirectory" />
											<la:text property="homeDirectory" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="roles" class="col-sm-3 control-label"><la:message
												key="labels.roles" /></label>
										<div class="col-sm-9">
											<la:errors property="roles" />
											<la:select property="roles" multiple="true"
												styleClass="form-control">
												<c:forEach var="l" varStatus="s" items="${roleItems}">
													<la:option value="${l.id}">${f:h(l.name)}</la:option>
												</c:forEach>
											</la:select>
										</div>
									</div>
									<div class="form-group">
										<label for="groups" class="col-sm-3 control-label"><la:message
												key="labels.groups" /></label>
										<div class="col-sm-9">
											<la:errors property="groups" />
											<la:select property="groups" multiple="true"
												styleClass="form-control">
												<c:forEach var="l" varStatus="s" items="${groupItems}">
													<la:option value="${l.id}">${f:h(l.name)}</la:option>
												</c:forEach>
											</la:select>
										</div>
									</div>
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

