<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.pathmap_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="pathMapping"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.pathmap_title_details"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/pathmap/">
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
                                    <label for="regex" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.regex"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="regex"/>
                                        <la:text styleId="regex" property="regex" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="replacement" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.replacement"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="replacement"/>
                                        <la:text styleId="replacement" property="replacement"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="processType" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.processType"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="processType"/>
                                        <la:select styleId="processType" property="processType"
                                                   styleClass="form-control">
                                            <la:option value="C">
                                                <la:message key="labels.pathmap_pt_crawling"/>
                                            </la:option>
                                            <la:option value="D">
                                                <la:message key="labels.pathmap_pt_displaying"/>
                                            </la:option>
                                            <la:option value="B">
                                                <la:message key="labels.pathmap_pt_both"/>
                                            </la:option>
                                            <la:option value="R">
                                                <la:message key="labels.pathmap_pt_stored"/>
                                            </la:option>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="sortOrder" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.sortOrder"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="sortOrder"/>
                                        <input type="number" name="sortOrder" id="sortOrder"
                                               value="${f:h(sortOrder)}" class="form-control"
                                               min="0" max="100000">
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="userAgent" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.userAgent"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="userAgent"/>
                                        <la:text styleId="userAgent" property="userAgent" styleClass="form-control"/>
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
