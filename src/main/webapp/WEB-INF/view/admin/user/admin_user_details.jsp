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
                <c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
                    <la:hidden property="id"/>
                    <la:hidden property="versionNo"/>
                </c:if>
                <div class="row">
                    <div class="col-md-12">
                        <div
                                class="card card-outline <c:if test="${crudMode == 1 || crudMode == 2}">card-success</c:if><c:if test="${crudMode == 3}">card-danger</c:if><c:if test="${crudMode == 4}">card-primary</c:if>">
                            <div class="card-header">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
                            </div>
                            <div class="card-body">
                                    <%-- Message --%>
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="alert alert-info">${msg}</div>
                                    </la:info>
                                    <la:errors/>
                                </div>
                                    <%-- Form Fields --%>
                                <table class="table table-bordered">
                                    <tbody>
                                    <tr>
                                        <th style="width: 25%"><la:message key="labels.user_name"/></th>
                                        <td>${f:h(name)}<la:hidden property="name"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.user_surname"/></th>
                                        <td>${f:h(attributes.surname)}<la:hidden property="attributes.surname"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.user_given_name"/></th>
                                        <td>${f:h(attributes.givenName)}<la:hidden
                                                property="attributes.givenName"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.user_mail"/></th>
                                        <td>${f:h(attributes.mail)}<la:hidden property="attributes.mail"/></td>
                                    </tr>
                                    <c:if test="${ldapAdminEnabled}">
                                        <tr>
                                            <th><la:message key="labels.user_employeeNumber"/></th>
                                            <td>${f:h(attributes.employeeNumber)}<la:hidden
                                                    property="attributes.employeeNumber"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_telephoneNumber"/></th>
                                            <td>${f:h(attributes.telephoneNumber)}<la:hidden
                                                    property="attributes.telephoneNumber"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_homePhone"/></th>
                                            <td>${f:h(attributes.homePhone)}<la:hidden
                                                    property="attributes.homePhone"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_homePostalAddress"/></th>
                                            <td>${f:h(attributes.homePostalAddress)}<la:hidden
                                                    property="attributes.homePostalAddress"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_labeledURI"/></th>
                                            <td>${f:h(attributes.labeledURI)}<la:hidden
                                                    property="attributes.labeledURI"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_roomNumber"/></th>
                                            <td>${f:h(attributes.roomNumber)}<la:hidden
                                                    property="attributes.roomNumber"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_description"/></th>
                                            <td>${f:h(attributes.description)}<la:hidden
                                                    property="attributes.description"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_title"/></th>
                                            <td>${f:h(attributes.title)}<la:hidden property="attributes.title"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_pager"/></th>
                                            <td>${f:h(attributes.pager)}<la:hidden property="attributes.pager"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_street"/></th>
                                            <td>${f:h(attributes.street)}<la:hidden property="attributes.street"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_postalCode"/></th>
                                            <td>${f:h(attributes.postalCode)}<la:hidden
                                                    property="attributes.postalCode"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_physicalDeliveryOfficeName"/></th>
                                            <td>${f:h(attributes.physicalDeliveryOfficeName)}<la:hidden
                                                    property="attributes.physicalDeliveryOfficeName"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_destinationIndicator"/></th>
                                            <td>${f:h(attributes.destinationIndicator)}<la:hidden
                                                    property="attributes.destinationIndicator"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_internationaliSDNNumber"/></th>
                                            <td>${f:h(attributes.internationaliSDNNumber)}<la:hidden
                                                    property="attributes.internationaliSDNNumber"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_state"/></th>
                                            <td>${f:h(attributes.state)}<la:hidden property="attributes.state"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_employeeType"/></th>
                                            <td>${f:h(attributes.facsimileTelephoneNumber)}<la:hidden
                                                    property="attributes.employeeType"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_facsimileTelephoneNumber"/></th>
                                            <td>${f:h(attributes.facsimileTelephoneNumber)}<la:hidden
                                                    property="attributes.facsimileTelephoneNumber"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_postOfficeBox"/></th>
                                            <td>${f:h(attributes.postOfficeBox)}<la:hidden
                                                    property="attributes.postOfficeBox"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_initials"/></th>
                                            <td>${f:h(attributes.initials)}<la:hidden
                                                    property="attributes.initials"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_carLicense"/></th>
                                            <td>${f:h(attributes.carLicense)}<la:hidden
                                                    property="attributes.carLicense"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_mobile"/></th>
                                            <td>${f:h(attributes.mobile)}<la:hidden property="attributes.mobile"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_postalAddress"/></th>
                                            <td>${f:h(attributes.postalAddress)}<la:hidden
                                                    property="attributes.postalAddress"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_city"/></th>
                                            <td>${f:h(attributes.city)}<la:hidden property="attributes.city"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_teletexTerminalIdentifier"/></th>
                                            <td>${f:h(attributes.teletexTerminalIdentifier)}<la:hidden
                                                    property="attributes.teletexTerminalIdentifier"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_x121Address"/></th>
                                            <td>${f:h(attributes.x121Address)}<la:hidden
                                                    property="attributes.x121Address"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_businessCategory"/></th>
                                            <td>${f:h(attributes.businessCategory)}<la:hidden
                                                    property="attributes.businessCategory"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_registeredAddress"/></th>
                                            <td>${f:h(attributes.registeredAddress)}<la:hidden
                                                    property="attributes.registeredAddress"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_displayName"/></th>
                                            <td>${f:h(attributes.displayName)}<la:hidden
                                                    property="attributes.displayName"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_preferredLanguage"/></th>
                                            <td>${f:h(attributes.preferredLanguage)}<la:hidden
                                                    property="attributes.preferredLanguage"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_departmentNumber"/></th>
                                            <td>${f:h(attributes.departmentNumber)}<la:hidden
                                                    property="attributes.departmentNumber"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_uidNumber"/></th>
                                            <td>${f:h(attributes.uidNumber)}<la:hidden
                                                    property="attributes.uidNumber"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_gidNumber"/></th>
                                            <td>${f:h(attributes.gidNumber)}<la:hidden
                                                    property="attributes.gidNumber"/></td>
                                        </tr>
                                        <tr>
                                            <th><la:message key="labels.user_homeDirectory"/></th>
                                            <td>${f:h(attributes.homeDirectory)}<la:hidden
                                                    property="attributes.homeDirectory"/></td>
                                        </tr>
                                    </c:if>
                                    <tr>
                                        <th><la:message key="labels.roles"/></th>
                                        <td><c:forEach var="rt" varStatus="s"
                                                       items="${roleItems}">
                                            <c:forEach var="rtid" varStatus="s" items="${roles}">
                                                <c:if test="${rtid==rt.id}">
                                                    ${f:h(rt.name)}<br/>
                                                </c:if>
                                            </c:forEach>
                                        </c:forEach></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.groups"/></th>
                                        <td><c:forEach var="rt" varStatus="s"
                                                       items="${groupItems}">
                                            <c:forEach var="rtid" varStatus="s" items="${groups}">
                                                <c:if test="${rtid==rt.id}">
                                                    ${f:h(rt.name)}<br/>
                                                </c:if>
                                            </c:forEach>
                                        </c:forEach></td>
                                    </tr>
                                    </tbody>
                                </table>
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
