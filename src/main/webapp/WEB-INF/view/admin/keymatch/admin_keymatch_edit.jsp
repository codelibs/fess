<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.key_match_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="keyMatch"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.key_match_title_details"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/keymatch/">
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
                                    <label for="term" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.key_match_term"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="term"/>
                                        <la:text styleId="term" property="term" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="query" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.key_match_query"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="query"/>
                                        <la:text styleId="query" property="query" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="maxSize" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.key_match_size"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="maxSize"/>
                                        <input type="number" name="maxSize" id="maxSize"
                                               value="${f:h(maxSize)}" class="form-control"
                                               min="0" max="10000000">
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="boost" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.key_match_boost"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="boost"/>
                                        <la:text styleId="boost" property="boost" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="virtualHost" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.virtual_host"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="virtualHost"/>
                                        <la:text styleId="virtualHost" property="virtualHost"
                                                 styleClass="form-control"/>
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

