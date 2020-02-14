<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.user_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="user"/>
        <jsp:param name="menuType" value="user"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.user_title_details"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/user/">
                <la:hidden property="crudMode"/>
                <c:if test="${crudMode==2}">
                    <la:hidden property="id"/>
                    <la:hidden property="versionNo"/>
                </c:if>
                <div class="row">
                    <div class="col-md-12">
                        <div
                                class="card card-outline <c:if test="${crudMode == 1 || crudMode == 2}">card-success</c:if>">
                            <div class="card-header">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
                            </div>
                            <div class="card-body">
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="alert alert-info">${msg}</div>
                                    </la:info>
                                    <la:errors property="_global"/>
                                </div>
                                <div class="form-group row">
                                    <label for="name" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.user_name"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="name"/>
                                        <c:if test="${crudMode==1}">
                                            <la:text styleId="name" property="name" styleClass="form-control"/>
                                        </c:if>
                                        <c:if test="${crudMode==2}">
                                            ${f:h(name)}
                                            <la:hidden property="name" styleClass="form-control"/>
                                        </c:if>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="password" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.user_password"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="password"/>
                                        <la:password styleId="password" property="password" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="confirmPassword" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.user_confirm_password"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="confirmPassword"/>
                                        <la:password styleId="confirmPassword" property="confirmPassword"
                                                     styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="attributes.surname" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.user_surname"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="attributes.surname"/>
                                        <la:text styleId="attributes.surname" property="attributes.surname" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="attributes.givenName" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.user_given_name"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="attributes.givenName"/>
                                        <la:text styleId="attributes.givenName" property="attributes.givenName" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="attributes.mail" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.user_mail"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="attributes.mail"/>
                                        <la:text styleId="attributes.mail" property="attributes.mail" styleClass="form-control"/>
                                    </div>
                                </div>
                                <c:if test="${ldapAdminEnabled}">
                                    <div class="form-group row">
                                        <label for="attributes.employeeNumber" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_employeeNumber"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.employeeNumber"/>
                                            <la:text styleId="attributes.employeeNumber" property="attributes.employeeNumber" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.telephoneNumber" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_telephoneNumber"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.telephoneNumber"/>
                                            <la:text styleId="attributes.telephoneNumber" property="attributes.telephoneNumber" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.homePhone" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_homePhone"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.homePhone"/>
                                            <la:text styleId="attributes.homePhone" property="attributes.homePhone" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.homePostalAddress" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_homePostalAddress"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.homePostalAddress"/>
                                            <la:text styleId="attributes.homePostalAddress" property="attributes.homePostalAddress" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.labeledURI" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_labeledURI"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.labeledURI"/>
                                            <la:text styleId="attributes.labeledURI" property="attributes.labeledURI" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.roomNumber" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_roomNumber"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.roomNumber"/>
                                            <la:text styleId="attributes.roomNumber" property="attributes.roomNumber" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.description" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_description"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.description"/>
                                            <la:text styleId="attributes.description" property="attributes.description" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.title" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_title"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.title"/>
                                            <la:text styleId="attributes.title" property="attributes.title" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.pager" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_pager"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.pager"/>
                                            <la:text styleId="attributes.pager" property="attributes.pager" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.street" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_street"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.street"/>
                                            <la:text styleId="attributes.street"  property="attributes.street" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.postalCode" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_postalCode"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.postalCode"/>
                                            <la:text styleId="attributes.postalCode" property="attributes.postalCode" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.physicalDeliveryOfficeName"
                                               class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_physicalDeliveryOfficeName"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.physicalDeliveryOfficeName"/>
                                            <la:text styleId="attributes.physicalDeliveryOfficeName" property="attributes.physicalDeliveryOfficeName"
                                                     styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.destinationIndicator" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_destinationIndicator"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.destinationIndicator"/>
                                            <la:text styleId="attributes.destinationIndicator" property="attributes.destinationIndicator"
                                                     styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.internationaliSDNNumber" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_internationaliSDNNumber"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.internationaliSDNNumber"/>
                                            <la:text styleId="attributes.internationaliSDNNumber" property="attributes.internationaliSDNNumber"
                                                     styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.state" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_state"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.state"/>
                                            <la:text styleId="attributes.state" property="attributes.state" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.employeeType" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_employeeType"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.employeeType"/>
                                            <la:text styleId="attributes.employeeType" property="attributes.employeeType" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.facsimileTelephoneNumber"
                                               class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_facsimileTelephoneNumber"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.facsimileTelephoneNumber"/>
                                            <la:text styleId="attributes.facsimileTelephoneNumber" property="attributes.facsimileTelephoneNumber"
                                                     styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.postOfficeBox" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_postOfficeBox"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.postOfficeBox"/>
                                            <la:text styleId="attributes.postOfficeBox" property="attributes.postOfficeBox" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.initials" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_initials"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.initials"/>
                                            <la:text styleId="attributes.initials" property="attributes.initials" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.carLicense" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_carLicense"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.carLicense"/>
                                            <la:text styleId="attributes.carLicense" property="attributes.carLicense" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.mobile" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_mobile"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.mobile"/>
                                            <la:text styleId="attributes.mobile" property="attributes.mobile" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.postalAddress" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_postalAddress"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.postalAddress"/>
                                            <la:text styleId="attributes.postalAddress" property="attributes.postalAddress" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.city" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_city"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.city"/>
                                            <la:text styleId="attributes.city" property="attributes.city" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.teletexTerminalIdentifier"
                                               class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_teletexTerminalIdentifier"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.teletexTerminalIdentifier"/>
                                            <la:text styleId="attributes.teletexTerminalIdentifier" property="attributes.teletexTerminalIdentifier"
                                                     styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.x121Address" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_x121Address"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.x121Address"/>
                                            <la:text styleId="attributes.x121Address" property="attributes.x121Address" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.businessCategory" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_businessCategory"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.businessCategory"/>
                                            <la:text styleId="attributes.businessCategory" property="attributes.businessCategory" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.registeredAddress" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_registeredAddress"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.registeredAddress"/>
                                            <la:text styleId="attributes.registeredAddress" property="attributes.registeredAddress" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.displayName" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_displayName"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.displayName"/>
                                            <la:text styleId="attributes.displayName" property="attributes.displayName" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.preferredLanguage" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_preferredLanguage"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.preferredLanguage"/>
                                            <la:text styleId="attributes.preferredLanguage" property="attributes.preferredLanguage" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.departmentNumber" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_departmentNumber"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.departmentNumber"/>
                                            <la:text styleId="attributes.departmentNumber" property="attributes.departmentNumber" styleClass="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.uidNumber" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_uidNumber"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.uidNumber"/>
                                            <input type="number" id="attributes.uidNumber" name="attributes.uidNumber" class="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.gidNumber" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_gidNumber"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.gidNumber"/>
                                            <input type="number" id="attributes.gidNumber" name="attributes.gidNumber" class="form-control"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label for="attributes.homeDirectory" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.user_homeDirectory"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="attributes.homeDirectory"/>
                                            <la:text styleId="attributes.homeDirectory" property="attributes.homeDirectory" styleClass="form-control"/>
                                        </div>
                                    </div>
                                </c:if>
                                <div class="form-group row">
                                    <label for="roles" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.roles"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="roles"/>
                                        <la:select styleId="roles" property="roles" multiple="true"
                                                   styleClass="form-control">
                                            <c:forEach var="l" varStatus="s" items="${roleItems}">
                                                <la:option value="${l.id}">${f:h(l.name)}</la:option>
                                            </c:forEach>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="groups" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.groups"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="groups"/>
                                        <la:select styleId="groups" property="groups" multiple="true"
                                                   styleClass="form-control">
                                            <c:forEach var="l" varStatus="s" items="${groupItems}">
                                                <la:option value="${l.id}">${f:h(l.name)}</la:option>
                                            </c:forEach>
                                        </la:select>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/buttons.jsp"></jsp:include>
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

