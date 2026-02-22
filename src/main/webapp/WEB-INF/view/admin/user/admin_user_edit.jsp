<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.user_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="user"/>
        <jsp:param name="menuType" value="user"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.user_title_details"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
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
                <div class="fads-row">
                    <div class="fads-col-md-12">
                        <div
                                class="fads-card <c:if test="${crudMode == 1 || crudMode == 2}">card-success</c:if>">
                            <div class="fads-card-header">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
                            </div>
                            <div class="fads-card-body">
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="fads-banner fads-banner-info">${msg}</div>
                                    </la:info>
                                    <la:errors property="_global"/>
                                </div>
                                <div class="fads-form-field">
                                    <label for="name" class="fads-label"><la:message
                                            key="labels.user_name"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="name"/>
                                        <c:if test="${crudMode==1}">
                                            <la:text styleId="name" property="name" styleClass="fads-textfield"/>
                                        </c:if>
                                        <c:if test="${crudMode==2}">
                                            ${f:h(name)}
                                            <la:hidden property="name" styleClass="fads-textfield"/>
                                        </c:if>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="password" class="fads-label"><la:message
                                            key="labels.user_password"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="password"/>
                                        <la:password styleId="password" property="password" styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="confirmPassword" class="fads-label"><la:message
                                            key="labels.user_confirm_password"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="confirmPassword"/>
                                        <la:password styleId="confirmPassword" property="confirmPassword"
                                                     styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="attributes.surname" class="fads-label"><la:message
                                            key="labels.user_surname"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="attributes.surname"/>
                                        <la:text styleId="attributes.surname" property="attributes.surname" styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="attributes.givenName" class="fads-label"><la:message
                                            key="labels.user_given_name"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="attributes.givenName"/>
                                        <la:text styleId="attributes.givenName" property="attributes.givenName" styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="attributes.mail" class="fads-label"><la:message
                                            key="labels.user_mail"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="attributes.mail"/>
                                        <la:text styleId="attributes.mail" property="attributes.mail" styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <c:if test="${ldapAdminEnabled}">
                                    <div class="fads-form-field">
                                        <label for="attributes.employeeNumber" class="fads-label"><la:message
                                                key="labels.user_employeeNumber"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.employeeNumber"/>
                                            <la:text styleId="attributes.employeeNumber" property="attributes.employeeNumber" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.telephoneNumber" class="fads-label"><la:message
                                                key="labels.user_telephoneNumber"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.telephoneNumber"/>
                                            <la:text styleId="attributes.telephoneNumber" property="attributes.telephoneNumber" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.homePhone" class="fads-label"><la:message
                                                key="labels.user_homePhone"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.homePhone"/>
                                            <la:text styleId="attributes.homePhone" property="attributes.homePhone" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.homePostalAddress" class="fads-label"><la:message
                                                key="labels.user_homePostalAddress"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.homePostalAddress"/>
                                            <la:text styleId="attributes.homePostalAddress" property="attributes.homePostalAddress" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.labeledURI" class="fads-label"><la:message
                                                key="labels.user_labeledURI"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.labeledURI"/>
                                            <la:text styleId="attributes.labeledURI" property="attributes.labeledURI" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.roomNumber" class="fads-label"><la:message
                                                key="labels.user_roomNumber"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.roomNumber"/>
                                            <la:text styleId="attributes.roomNumber" property="attributes.roomNumber" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.description" class="fads-label"><la:message
                                                key="labels.user_description"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.description"/>
                                            <la:text styleId="attributes.description" property="attributes.description" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.title" class="fads-label"><la:message
                                                key="labels.user_title"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.title"/>
                                            <la:text styleId="attributes.title" property="attributes.title" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.pager" class="fads-label"><la:message
                                                key="labels.user_pager"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.pager"/>
                                            <la:text styleId="attributes.pager" property="attributes.pager" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.street" class="fads-label"><la:message
                                                key="labels.user_street"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.street"/>
                                            <la:text styleId="attributes.street"  property="attributes.street" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.postalCode" class="fads-label"><la:message
                                                key="labels.user_postalCode"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.postalCode"/>
                                            <la:text styleId="attributes.postalCode" property="attributes.postalCode" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.physicalDeliveryOfficeName"
                                               class="fads-label"><la:message
                                                key="labels.user_physicalDeliveryOfficeName"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.physicalDeliveryOfficeName"/>
                                            <la:text styleId="attributes.physicalDeliveryOfficeName" property="attributes.physicalDeliveryOfficeName"
                                                     styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.destinationIndicator" class="fads-label"><la:message
                                                key="labels.user_destinationIndicator"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.destinationIndicator"/>
                                            <la:text styleId="attributes.destinationIndicator" property="attributes.destinationIndicator"
                                                     styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.internationaliSDNNumber" class="fads-label"><la:message
                                                key="labels.user_internationaliSDNNumber"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.internationaliSDNNumber"/>
                                            <la:text styleId="attributes.internationaliSDNNumber" property="attributes.internationaliSDNNumber"
                                                     styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.state" class="fads-label"><la:message
                                                key="labels.user_state"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.state"/>
                                            <la:text styleId="attributes.state" property="attributes.state" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.employeeType" class="fads-label"><la:message
                                                key="labels.user_employeeType"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.employeeType"/>
                                            <la:text styleId="attributes.employeeType" property="attributes.employeeType" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.facsimileTelephoneNumber"
                                               class="fads-label"><la:message
                                                key="labels.user_facsimileTelephoneNumber"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.facsimileTelephoneNumber"/>
                                            <la:text styleId="attributes.facsimileTelephoneNumber" property="attributes.facsimileTelephoneNumber"
                                                     styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.postOfficeBox" class="fads-label"><la:message
                                                key="labels.user_postOfficeBox"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.postOfficeBox"/>
                                            <la:text styleId="attributes.postOfficeBox" property="attributes.postOfficeBox" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.initials" class="fads-label"><la:message
                                                key="labels.user_initials"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.initials"/>
                                            <la:text styleId="attributes.initials" property="attributes.initials" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.carLicense" class="fads-label"><la:message
                                                key="labels.user_carLicense"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.carLicense"/>
                                            <la:text styleId="attributes.carLicense" property="attributes.carLicense" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.mobile" class="fads-label"><la:message
                                                key="labels.user_mobile"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.mobile"/>
                                            <la:text styleId="attributes.mobile" property="attributes.mobile" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.postalAddress" class="fads-label"><la:message
                                                key="labels.user_postalAddress"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.postalAddress"/>
                                            <la:text styleId="attributes.postalAddress" property="attributes.postalAddress" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.city" class="fads-label"><la:message
                                                key="labels.user_city"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.city"/>
                                            <la:text styleId="attributes.city" property="attributes.city" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.teletexTerminalIdentifier"
                                               class="fads-label"><la:message
                                                key="labels.user_teletexTerminalIdentifier"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.teletexTerminalIdentifier"/>
                                            <la:text styleId="attributes.teletexTerminalIdentifier" property="attributes.teletexTerminalIdentifier"
                                                     styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.x121Address" class="fads-label"><la:message
                                                key="labels.user_x121Address"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.x121Address"/>
                                            <la:text styleId="attributes.x121Address" property="attributes.x121Address" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.businessCategory" class="fads-label"><la:message
                                                key="labels.user_businessCategory"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.businessCategory"/>
                                            <la:text styleId="attributes.businessCategory" property="attributes.businessCategory" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.registeredAddress" class="fads-label"><la:message
                                                key="labels.user_registeredAddress"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.registeredAddress"/>
                                            <la:text styleId="attributes.registeredAddress" property="attributes.registeredAddress" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.displayName" class="fads-label"><la:message
                                                key="labels.user_displayName"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.displayName"/>
                                            <la:text styleId="attributes.displayName" property="attributes.displayName" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.preferredLanguage" class="fads-label"><la:message
                                                key="labels.user_preferredLanguage"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.preferredLanguage"/>
                                            <la:text styleId="attributes.preferredLanguage" property="attributes.preferredLanguage" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.departmentNumber" class="fads-label"><la:message
                                                key="labels.user_departmentNumber"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.departmentNumber"/>
                                            <la:text styleId="attributes.departmentNumber" property="attributes.departmentNumber" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.uidNumber" class="fads-label"><la:message
                                                key="labels.user_uidNumber"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.uidNumber"/>
                                            <input type="number" id="attributes.uidNumber" name="attributes.uidNumber" class="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.gidNumber" class="fads-label"><la:message
                                                key="labels.user_gidNumber"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.gidNumber"/>
                                            <input type="number" id="attributes.gidNumber" name="attributes.gidNumber" class="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label for="attributes.homeDirectory" class="fads-label"><la:message
                                                key="labels.user_homeDirectory"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="attributes.homeDirectory"/>
                                            <la:text styleId="attributes.homeDirectory" property="attributes.homeDirectory" styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                </c:if>
                                <div class="fads-form-field">
                                    <label for="roles" class="fads-label"><la:message
                                            key="labels.roles"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="roles"/>
                                        <la:select styleId="roles" property="roles" multiple="true"
                                                   styleClass="fads-textfield">
                                            <c:forEach var="l" varStatus="s" items="${roleItems}">
                                                <la:option value="${l.id}">${f:h(l.name)}</la:option>
                                            </c:forEach>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="groups" class="fads-label"><la:message
                                            key="labels.groups"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="groups"/>
                                        <la:select styleId="groups" property="groups" multiple="true"
                                                   styleClass="fads-textfield">
                                            <c:forEach var="l" varStatus="s" items="${groupItems}">
                                                <la:option value="${l.id}">${f:h(l.name)}</la:option>
                                            </c:forEach>
                                        </la:select>
                                    </div>
                                </div>
                            </div>
                            <div class="fads-card-footer">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/buttons.jsp"></jsp:include>
                            </div>
                        </div>
                    </div>
                </div>
            </la:form>
        </section>
    </main>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}

