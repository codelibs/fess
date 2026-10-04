<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.tagtype_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="tagType"/>
    </jsp:include>
    <main id="mainContent" class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.tagtype_title_details"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/tagtype/">
                <la:hidden property="crudMode"/>
                <c:if test="${crudMode==2}">
                    <la:hidden property="id"/>
                    <la:hidden property="seqNo"/>
                    <la:hidden property="primaryTerm"/>
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
                                        <div class="alert alert-success">${msg}</div>
                                    </la:info>
                                    <la:errors property="_global"/>
                                </div>
                                <div class="form-group row">
                                    <label for="name" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.tagtype_name"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="name"/>
                                        <la:text styleId="name" property="name" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="owner" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.tagtype_owner"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="owner"/>
                                        <la:text styleId="owner" property="owner" styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="paths" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.tagtype_paths"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="paths"/>
                                        <la:textarea styleId="paths" property="paths"
                                                     styleClass="form-control" rows="10"/>
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
                                    <label for="virtualHost" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.virtual_host"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="virtualHost"/>
                                        <la:text styleId="virtualHost" property="virtualHost"
                                                 styleClass="form-control"/>
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
                            </div>
                            <div class="card-footer">
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
