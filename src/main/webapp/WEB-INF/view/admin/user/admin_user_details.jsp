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
				<la:form action="/admin/user/">
					<la:hidden property="crudMode" />
					<c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
						<la:hidden property="id" />
						<la:hidden property="versionNo" />
					</c:if>
					<div class="row">
						<div class="col-md-12">
							<div
								class="box <c:if test="${crudMode == 1}">box-success</c:if><c:if test="${crudMode == 2}">box-warning</c:if><c:if test="${crudMode == 3}">box-danger</c:if><c:if test="${crudMode == 4}">box-primary</c:if>">
								<div class="box-header with-border">
									<jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
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
												<th class="col-xs-2"><la:message key="labels.user_name" /></th>
												<td>${f:h(name)}<la:hidden property="name" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_surname" /></th>
												<td>${f:h(surname)}<la:hidden property="surname" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_given_name" /></th>
												<td>${f:h(givenName)}<la:hidden property="givenName" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_mail" /></th>
												<td>${f:h(mail)}<la:hidden property="mail" /></td>
											</tr>
											<c:if test="${ldapAdminEnabled}">
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_employeeNumber" /></th>
												<td>${f:h(employeeNumber)}<la:hidden property="employeeNumber" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_telephoneNumber" /></th>
												<td>${f:h(telephoneNumber)}<la:hidden property="telephoneNumber" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_homePhone" /></th>
												<td>${f:h(homePhone)}<la:hidden property="homePhone" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_homePostalAddress" /></th>
												<td>${f:h(homePostalAddress)}<la:hidden property="homePostalAddress" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_labeledURI" /></th>
												<td>${f:h(labeledURI)}<la:hidden property="labeledURI" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_roomNumber" /></th>
												<td>${f:h(roomNumber)}<la:hidden property="roomNumber" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_description" /></th>
												<td>${f:h(description)}<la:hidden property="description" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_title" /></th>
												<td>${f:h(title)}<la:hidden property="title" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_pager" /></th>
												<td>${f:h(pager)}<la:hidden property="pager" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_street" /></th>
												<td>${f:h(street)}<la:hidden property="street" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_postalCode" /></th>
												<td>${f:h(postalCode)}<la:hidden property="postalCode" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_physicalDeliveryOfficeName" /></th>
												<td>${f:h(physicalDeliveryOfficeName)}<la:hidden property="physicalDeliveryOfficeName" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_destinationIndicator" /></th>
												<td>${f:h(destinationIndicator)}<la:hidden property="destinationIndicator" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_internationaliSDNNumber" /></th>
												<td>${f:h(internationaliSDNNumber)}<la:hidden property="internationaliSDNNumber" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_state" /></th>
												<td>${f:h(state)}<la:hidden property="state" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_employeeType" /></th>
												<td>${f:h(facsimileTelephoneNumber)}<la:hidden property="employeeType" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_facsimileTelephoneNumber" /></th>
												<td>${f:h(facsimileTelephoneNumber)}<la:hidden property="facsimileTelephoneNumber" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_postOfficeBox" /></th>
												<td>${f:h(postOfficeBox)}<la:hidden property="postOfficeBox" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_initials" /></th>
												<td>${f:h(initials)}<la:hidden property="initials" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_carLicense" /></th>
												<td>${f:h(carLicense)}<la:hidden property="carLicense" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_mobile" /></th>
												<td>${f:h(mobile)}<la:hidden property="mobile" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_postalAddress" /></th>
												<td>${f:h(postalAddress)}<la:hidden property="postalAddress" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_city" /></th>
												<td>${f:h(city)}<la:hidden property="city" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_teletexTerminalIdentifier" /></th>
												<td>${f:h(teletexTerminalIdentifier)}<la:hidden property="teletexTerminalIdentifier" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_x121Address" /></th>
												<td>${f:h(x121Address)}<la:hidden property="x121Address" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_businessCategory" /></th>
												<td>${f:h(businessCategory)}<la:hidden property="businessCategory" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_registeredAddress" /></th>
												<td>${f:h(registeredAddress)}<la:hidden property="registeredAddress" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_displayName" /></th>
												<td>${f:h(displayName)}<la:hidden property="displayName" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_preferredLanguage" /></th>
												<td>${f:h(preferredLanguage)}<la:hidden property="preferredLanguage" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_departmentNumber" /></th>
												<td>${f:h(departmentNumber)}<la:hidden property="departmentNumber" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_uidNumber" /></th>
												<td>${f:h(uidNumber)}<la:hidden property="uidNumber" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_gidNumber" /></th>
												<td>${f:h(gidNumber)}<la:hidden property="gidNumber" /></td>
											</tr>
											<tr>
												<th class="col-xs-2"><la:message key="labels.user_homeDirectory" /></th>
												<td>${f:h(homeDirectory)}<la:hidden property="homeDirectory" /></td>
											</tr>
											</c:if>
											<tr>
												<th><la:message key="labels.roles" /></th>
												<td><c:forEach var="rt" varStatus="s"
														items="${roleItems}">
														<c:forEach var="rtid" varStatus="s" items="${roles}">
															<c:if test="${rtid==rt.id}">
																${f:h(rt.name)}<br />
															</c:if>
														</c:forEach>
													</c:forEach></td>
											</tr>
											<tr>
												<th><la:message key="labels.groups" /></th>
												<td><c:forEach var="rt" varStatus="s"
														items="${groupItems}">
														<c:forEach var="rtid" varStatus="s" items="${groups}">
															<c:if test="${rtid==rt.id}">
																${f:h(rt.name)}<br />
															</c:if>
														</c:forEach>
													</c:forEach></td>
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
