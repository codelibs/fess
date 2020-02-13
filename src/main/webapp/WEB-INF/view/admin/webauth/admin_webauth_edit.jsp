<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.webauth_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="webAuthentication"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.webauth_title_details"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/webauth/">
                <la:hidden property="crudMode"/>
                <c:if test="${crudMode==2}">
                    <la:hidden property="id"/>
                    <la:hidden property="versionNo"/>
                </c:if>
                <la:hidden property="createdBy"/>
                <la:hidden property="createdTime"/>
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
                                    <label for="hostname" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.webauth_hostname"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="hostname"/>
                                        <la:text styleId="hostname" property="hostname" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="port" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.webauth_port"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="port"/>
                                        <la:text styleId="port" property="port" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="authRealm" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.webauth_realm"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="authRealm"/>
                                        <la:text styleId="authRealm" property="authRealm" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="protocolScheme" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.webauth_scheme"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="protocolScheme"/>
                                        <la:select styleId="protocolScheme" property="protocolScheme"
                                                   styleClass="form-control">
                                            <c:forEach var="item" items="${protocolSchemeItems}">
                                                <la:option value="${f:u(item.value)}">${f:h(item.label)}</la:option>
                                            </c:forEach>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="username" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.webauth_username"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="username"/>
                                        <la:text styleId="username" property="username" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="password" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.webauth_password"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="password"/>
                                        <la:password styleId="password" property="password" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="parameters" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.webauth_parameters"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="parameters"/>
                                        <la:textarea styleId="parameters" property="parameters"
                                                     styleClass="form-control"
                                                     rows="5"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="webConfigId" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.webauth_web_crawling_config"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="webConfigId"/>
                                        <la:select styleId="webConfigId" property="webConfigId"
                                                   styleClass="form-control">
                                            <c:forEach var="item" items="${webConfigItems}">
                                                <la:option value="${f:u(item.value)}">${f:h(item.label)}</la:option>
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
