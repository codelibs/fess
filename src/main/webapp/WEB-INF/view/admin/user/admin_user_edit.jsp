<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><la:message key="labels.admin_brand_title" /> | <la:message
		key="labels.user_configuration" /></title>
<jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
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
											<la:errors property="attributes.surname" />
											<la:text property="attributes.surname" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="givenName" class="col-sm-3 control-label"><la:message
												key="labels.user_given_name" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.givenName" />
											<la:text property="attributes.givenName" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="mail" class="col-sm-3 control-label"><la:message
												key="labels.user_mail" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.mail" />
											<la:text property="attributes.mail" styleClass="form-control" />
										</div>
									</div>
									<c:if test="${ldapAdminEnabled}">
									<div class="form-group">
										<label for="employeeNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_employeeNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.employeeNumber" />
											<la:text property="attributes.employeeNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="telephoneNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_telephoneNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.telephoneNumber" />
											<la:text property="attributes.telephoneNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="homePhone" class="col-sm-3 control-label"><la:message
												key="labels.user_homePhone" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.homePhone" />
											<la:text property="attributes.homePhone" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="homePostalAddress" class="col-sm-3 control-label"><la:message
												key="labels.user_homePostalAddress" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.homePostalAddress" />
											<la:text property="attributes.homePostalAddress" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="labeledURI" class="col-sm-3 control-label"><la:message
												key="labels.user_labeledURI" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.labeledURI" />
											<la:text property="attributes.labeledURI" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="roomNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_roomNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.roomNumber" />
											<la:text property="attributes.roomNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="description" class="col-sm-3 control-label"><la:message
												key="labels.user_description" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.description" />
											<la:text property="attributes.description" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="title" class="col-sm-3 control-label"><la:message
												key="labels.user_title" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.title" />
											<la:text property="attributes.title" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="pager" class="col-sm-3 control-label"><la:message
												key="labels.user_pager" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.pager" />
											<la:text property="attributes.pager" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="street" class="col-sm-3 control-label"><la:message
												key="labels.user_street" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.street" />
											<la:text property="attributes.street" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="postalCode" class="col-sm-3 control-label"><la:message
												key="labels.user_postalCode" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.postalCode" />
											<la:text property="attributes.postalCode" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="physicalDeliveryOfficeName" class="col-sm-3 control-label"><la:message
												key="labels.user_physicalDeliveryOfficeName" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.physicalDeliveryOfficeName" />
											<la:text property="attributes.physicalDeliveryOfficeName" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="destinationIndicator" class="col-sm-3 control-label"><la:message
												key="labels.user_destinationIndicator" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.destinationIndicator" />
											<la:text property="attributes.destinationIndicator" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="internationaliSDNNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_internationaliSDNNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.internationaliSDNNumber" />
											<la:text property="attributes.internationaliSDNNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="state" class="col-sm-3 control-label"><la:message
												key="labels.user_state" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.state" />
											<la:text property="attributes.state" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="employeeType" class="col-sm-3 control-label"><la:message
												key="labels.user_employeeType" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.employeeType" />
											<la:text property="attributes.employeeType" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="facsimileTelephoneNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_facsimileTelephoneNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.facsimileTelephoneNumber" />
											<la:text property="attributes.facsimileTelephoneNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="postOfficeBox" class="col-sm-3 control-label"><la:message
												key="labels.user_postOfficeBox" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.postOfficeBox" />
											<la:text property="attributes.postOfficeBox" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="initials" class="col-sm-3 control-label"><la:message
												key="labels.user_initials" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.initials" />
											<la:text property="attributes.initials" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="carLicense" class="col-sm-3 control-label"><la:message
												key="labels.user_carLicense" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.carLicense" />
											<la:text property="attributes.carLicense" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="mobile" class="col-sm-3 control-label"><la:message
												key="labels.user_mobile" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.mobile" />
											<la:text property="attributes.mobile" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="postalAddress" class="col-sm-3 control-label"><la:message
												key="labels.user_postalAddress" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.postalAddress" />
											<la:text property="attributes.postalAddress" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="city" class="col-sm-3 control-label"><la:message
												key="labels.user_city" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.city" />
											<la:text property="attributes.city" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="teletexTerminalIdentifier" class="col-sm-3 control-label"><la:message
												key="labels.user_teletexTerminalIdentifier" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.teletexTerminalIdentifier" />
											<la:text property="attributes.teletexTerminalIdentifier" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="x121Address" class="col-sm-3 control-label"><la:message
												key="labels.user_x121Address" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.x121Address" />
											<la:text property="attributes.x121Address" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="businessCategory" class="col-sm-3 control-label"><la:message
												key="labels.user_businessCategory" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.businessCategory" />
											<la:text property="attributes.businessCategory" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="registeredAddress" class="col-sm-3 control-label"><la:message
												key="labels.user_registeredAddress" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.registeredAddress" />
											<la:text property="attributes.registeredAddress" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="displayName" class="col-sm-3 control-label"><la:message
												key="labels.user_displayName" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.displayName" />
											<la:text property="attributes.displayName" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="preferredLanguage" class="col-sm-3 control-label"><la:message
												key="labels.user_preferredLanguage" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.preferredLanguage" />
											<la:text property="attributes.preferredLanguage" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="departmentNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_departmentNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.departmentNumber" />
											<la:text property="attributes.departmentNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="uidNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_uidNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.uidNumber" />
											<la:text property="attributes.uidNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="gidNumber" class="col-sm-3 control-label"><la:message
												key="labels.user_gidNumber" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.gidNumber" />
											<la:text property="attributes.gidNumber" styleClass="form-control" />
										</div>
									</div>
									<div class="form-group">
										<label for="homeDirectory" class="col-sm-3 control-label"><la:message
												key="labels.user_homeDirectory" /></label>
										<div class="col-sm-9">
											<la:errors property="attributes.homeDirectory" />
											<la:text property="attributes.homeDirectory" styleClass="form-control" />
										</div>
									</div>
									</c:if>
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

