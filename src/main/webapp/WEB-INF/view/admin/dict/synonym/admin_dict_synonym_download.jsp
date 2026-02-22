<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.dict_synonym_configuration"/></title>
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
                            <la:message key="labels.dict_synonym_title"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><la:link href="/admin/dict">
                                <la:message key="labels.dict_list_link"/>
                            </la:link></li>
                            <li class="breadcrumb-item"><la:link href="../list/1/?dictId=${f:u(dictId)}">
                                <la:message key="labels.dict_synonym_list_link"/>
                            </la:link></li>
                            <li class="breadcrumb-item active"><la:message
                                    key="labels.dict_synonym_link_download"/></li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/dict/synonym/">
                <la:hidden property="dictId"/>
                <div class="fads-row">
                    <div class="fads-col-md-12">
                        <div class="fads-card">
                            <div class="fads-card-header">
                                <h3 class="fads-card-title">
                                    <la:message key="labels.dict_synonym_link_download"/>
                                </h3>
                                <div class="fads-card-tools">
                                    <div class="btn-group">
                                        <la:link href="/admin/dict"
                                                 styleClass="fads-btn fads-btn-default fads-btn-compact">
                                            <i class="fa fa-book" aria-hidden="true"></i>
                                            <la:message key="labels.dict_list_link"/>
                                        </la:link>
                                        <la:link href="../list/0/?dictId=${f:u(dictId)}"
                                                 styleClass="fads-btn fads-btn-primary fads-btn-compact">
                                            <i class="fa fa-th-list" aria-hidden="true"></i>
                                            <la:message key="labels.dict_synonym_list_link"/>
                                        </la:link>
                                        <la:link href="../createnew/${f:u(dictId)}"
                                                 styleClass="fads-btn fads-btn-success fads-btn-compact ${f:h(editableClass)}">
                                            <i class="fa fa-plus" aria-hidden="true"></i>
                                            <la:message key="labels.dict_synonym_link_create"/>
                                        </la:link>
                                        <la:link href="../downloadpage/${f:u(dictId)}"
                                                 styleClass="fads-btn fads-btn-primary fads-btn-compact">
                                            <i class="fa fa-download" aria-hidden="true"></i>
                                            <la:message key="labels.dict_synonym_link_download"/>
                                        </la:link>
                                        <la:link href="../uploadpage/${f:u(dictId)}"
                                                 styleClass="fads-btn fads-btn-success fads-btn-compact ${f:h(editableClass)}">
                                            <i class="fa fa-upload" aria-hidden="true"></i>
                                            <la:message key="labels.dict_synonym_link_upload"/>
                                        </la:link>
                                    </div>
                                </div>
                            </div>
                            <div class="fads-card-body">
                                    <%-- Message --%>
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="fads-banner fads-banner-info">${msg}</div>
                                    </la:info>
                                    <la:errors/>
                                </div>
                                <div class="fads-form-field">
                                    <label class="col-sm-12 text-sm-right col-form-label">${f:h(path)}</label>
                                </div>
                            </div>
                            <div class="fads-card-footer">
                                <button type="submit" class="fads-btn fads-btn-primary" name="download"
                                        value="<la:message key="labels.dict_synonym_button_download" />">
                                    <i class="fa fa-download" aria-hidden="true"></i>
                                    <la:message key="labels.dict_synonym_button_download"/>
                                </button>
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
