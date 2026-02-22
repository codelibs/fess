<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.dict_mapping_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="dict"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.dict_mapping_title"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><la:link href="/admin/dict">
                                <la:message key="labels.dict_list_link"/>
                            </la:link></li>
                            <li class="breadcrumb-item"><la:link href="../list/1/?dictId=${f:u(dictId)}">
                                <la:message key="labels.dict_mapping_list_link"/>
                            </la:link></li>
                            <c:if test="${crudMode == 1}">
                                <li class="breadcrumb-item active"><la:message
                                        key="labels.dict_mapping_link_create"/></li>
                            </c:if>
                            <c:if test="${crudMode == 2}">
                                <li class="breadcrumb-item active"><la:message
                                        key="labels.dict_mapping_link_edit"/></li>
                            </c:if>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/dict/mapping/">
                <la:hidden property="crudMode"/>
                <la:hidden property="dictId"/>
                <c:if test="${crudMode==2}">
                    <la:hidden property="id"/>
                </c:if>
                <div class="fads-row">
                    <div class="fads-col-md-12">
                        <div
                                class="fads-card <c:if test="${crudMode == 1 || crudMode == 2}">card-success</c:if>">
                            <div class="fads-card-header">
                                <h3 class="fads-card-title">
                                    <c:if test="${crudMode == 1}">
                                        <la:message key="labels.dict_mapping_link_create"/>
                                    </c:if>
                                    <c:if test="${crudMode == 2}">
                                        <la:message key="labels.dict_mapping_link_edit"/>
                                    </c:if>
                                </h3>
                                <div class="fads-card-tools">
                                    <div class="btn-group">
                                        <la:link href="/admin/dict"
                                                 styleClass="fads-btn fads-btn-default fads-btn-compact">
                                            <i class="fa fa-book" aria-hidden="true"></i>
                                            <la:message key="labels.dict_list_link"/>
                                        </la:link>
                                        <la:link href="../list/1?dictId=${f:u(dictId)}"
                                                 styleClass="fads-btn fads-btn-primary fads-btn-compact">
                                            <i class="fa fa-th-list" aria-hidden="true"></i>
                                            <la:message key="labels.dict_mapping_list_link"/>
                                        </la:link>
                                        <la:link href="../createnew/${f:u(dictId)}"
                                                 styleClass="fads-btn fads-btn-success fads-btn-compact">
                                            <i class="fa fa-plus" aria-hidden="true"></i>
                                            <la:message key="labels.dict_mapping_link_create"/>
                                        </la:link>
                                        <la:link href="../downloadpage/${f:u(dictId)}"
                                                 styleClass="fads-btn fads-btn-primary fads-btn-compact">
                                            <i class="fa fa-download" aria-hidden="true"></i>
                                            <la:message key="labels.dict_mapping_link_download"/>
                                        </la:link>
                                        <la:link href="../uploadpage/${f:u(dictId)}"
                                                 styleClass="fads-btn fads-btn-success fads-btn-compact">
                                            <i class="fa fa-upload" aria-hidden="true"></i>
                                            <la:message key="labels.dict_mapping_link_upload"/>
                                        </la:link>
                                    </div>
                                </div>
                            </div>
                            <div class="fads-card-body">
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="fads-banner fads-banner-info">${msg}</div>
                                    </la:info>
                                    <la:errors property="_global"/>
                                </div>
                                <div class="fads-form-field">
                                    <label for="inputs" class="fads-label"><la:message
                                            key="labels.dict_mapping_source"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="inputs"/>
                                        <la:textarea styleId="inputs" property="inputs" rows="5"
                                                     styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="output" class="fads-label"><la:message
                                            key="labels.dict_mapping_target"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="output"/>
                                        <la:text styleId="output" property="output" styleClass="fads-textfield"/>
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
