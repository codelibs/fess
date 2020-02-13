<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.dict_protwords_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="dict"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.dict_protwords_title"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><la:link href="/admin/dict">
                                <la:message key="labels.dict_list_link"/>
                            </la:link></li>
                            <li class="breadcrumb-item"><la:link href="../list/1/?dictId=${f:u(dictId)}">
                                <la:message key="labels.dict_protwords_list_link"/>
                            </la:link></li>
                            <c:if test="${crudMode == 1}">
                                <li class="breadcrumb-item active"><la:message
                                        key="labels.dict_protwords_link_create"/></li>
                            </c:if>
                            <c:if test="${crudMode == 2}">
                                <li class="breadcrumb-item active"><la:message
                                        key="labels.dict_protwords_link_edit"/></li>
                            </c:if>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/dict/protwords/">
                <la:hidden property="crudMode"/>
                <la:hidden property="dictId"/>
                <c:if test="${crudMode==2}">
                    <la:hidden property="id"/>
                </c:if>
                <div class="row">
                    <div class="col-md-12">
                        <div
                                class="card card-outline <c:if test="${crudMode == 1 || crudMode == 2}">card-success</c:if>">
                            <div class="card-header">
                                <h3 class="card-title">
                                    <c:if test="${crudMode == 1}">
                                        <la:message key="labels.dict_protwords_link_create"/>
                                    </c:if>
                                    <c:if test="${crudMode == 2}">
                                        <la:message key="labels.dict_protwords_link_edit"/>
                                    </c:if>
                                </h3>
                                <div class="card-tools">
                                    <div class="btn-group">
                                        <la:link href="/admin/dict"
                                                 styleClass="btn btn-default btn-xs">
                                            <em class="fa fa-book"></em>
                                            <la:message key="labels.dict_list_link"/>
                                        </la:link>
                                        <la:link href="../list/1?dictId=${f:u(dictId)}"
                                                 styleClass="btn btn-primary btn-xs">
                                            <em class="fa fa-th-list"></em>
                                            <la:message key="labels.dict_protwords_list_link"/>
                                        </la:link>
                                        <la:link href="../createnew/${f:u(dictId)}"
                                                 styleClass="btn btn-success btn-xs">
                                            <em class="fa fa-plus"></em>
                                            <la:message key="labels.dict_protwords_link_create"/>
                                        </la:link>
                                        <la:link href="../downloadpage/${f:u(dictId)}"
                                                 styleClass="btn btn-primary btn-xs">
                                            <em class="fa fa-download"></em>
                                            <la:message key="labels.dict_protwords_link_download"/>
                                        </la:link>
                                        <la:link href="../uploadpage/${f:u(dictId)}"
                                                 styleClass="btn btn-success btn-xs">
                                            <em class="fa fa-upload"></em>
                                            <la:message key="labels.dict_protwords_link_upload"/>
                                        </la:link>
                                    </div>
                                </div>
                            </div>
                            <div class="card-body">
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="alert alert-info">${msg}</div>
                                    </la:info>
                                    <la:errors property="_global"/>
                                </div>
                                <div class="form-group row">
                                    <label for="input" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.dict_protwords_source"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="input"/>
                                        <la:text styleId="input" property="input"
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
