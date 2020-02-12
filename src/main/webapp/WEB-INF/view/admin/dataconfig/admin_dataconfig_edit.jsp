<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.data_crawling_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="dataConfig"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.data_crawling_title_details"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/dataconfig/">
                <la:hidden property="crudMode"/>
                <c:if test="${crudMode==2}">
                    <la:hidden property="id"/>
                    <la:hidden property="versionNo"/>
                </c:if>
                <la:hidden property="createdBy"/>
                <la:hidden property="createdTime"/>
                <la:hidden property="sortOrder"/>
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
                                            key="labels.name"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="name"/>
                                        <la:text styleId="name" property="name" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="handlerName" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.handler_name"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="handlerName"/>
                                        <la:select styleId="handlerName" property="handlerName"
                                                   styleClass="form-control">
                                            <c:forEach var="hn" varStatus="s"
                                                       items="${handlerNameItems}">
                                                <la:option value="${f:u(hn.value)}">${f:h(hn.label)}</la:option>
                                            </c:forEach>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="handlerParameter" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.handler_parameter"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="handlerParameter"/>
                                        <la:textarea styleId="handlerParameter" property="handlerParameter"
                                                     styleClass="form-control" rows="5"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="handlerScript" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.handler_script"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="handlerScript"/>
                                        <la:textarea styleId="handlerScript" property="handlerScript"
                                                     styleClass="form-control" rows="5"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="boost" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.boost"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="boost"/>
                                        <la:text styleId="boost" property="boost" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="permissions" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.permissions"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="permissions"/>
                                        <la:textarea styleId="permissions" property="permissions"
                                                     styleClass="form-control"
                                                     rows="5"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="virtualHosts" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.virtual_hosts"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="virtualHosts"/>
                                        <la:textarea styleId="virtualHosts" property="virtualHosts"
                                                     styleClass="form-control"
                                                     rows="5"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="available" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.available"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="available"/>
                                        <la:select styleId="available" property="available" styleClass="form-control">
                                            <la:option value="true">
                                                <la:message key="labels.enabled"/>
                                            </la:option>
                                            <la:option value="false">
                                                <la:message key="labels.disabled"/>
                                            </la:option>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="permissions" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.description"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="description"/>
                                        <la:textarea styleId="description" property="description"
                                                     styleClass="form-control"
                                                     rows="5"/>
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
