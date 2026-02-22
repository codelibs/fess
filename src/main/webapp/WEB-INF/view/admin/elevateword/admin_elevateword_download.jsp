<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.elevate_word_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="suggest"/>
        <jsp:param name="menuType" value="elevateWord"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.elevate_word_configuration"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item active"><la:link href="/admin/elevateword">
                                <la:message key="labels.elevate_word_link_list"/>
                            </la:link></li>
                            <li class="breadcrumb-item active"><a href="#"><la:message
                                    key="labels.elevate_word_link_download"/></a></li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/elevateword/">
                <div class="fads-row">
                    <div class="fads-col-md-12">
                        <div class="fads-card">
                                <%-- Card Header --%>
                            <div class="fads-card-header">
                                <h3 class="fads-card-title">
                                    <la:message key="labels.elevate_word_link_download"/>
                                </h3>
                                <div class="fads-card-tools">
                                    <div class="btn-group">
                                        <la:link href="/admin/elevateword"
                                                 styleClass="fads-btn fads-btn-default fads-btn-compact">
                                            <i class="fa fa-th-list" aria-hidden="true"></i>
                                            <la:message key="labels.elevate_word_link_list"/>
                                        </la:link>
                                        <la:link href="../createnew"
                                                 styleClass="fads-btn fads-btn-success fads-btn-compact ${f:h(editableClass)}">
                                            <i class="fa fa-plus" aria-hidden="true"></i>
                                            <la:message key="labels.elevate_word_link_create"/>
                                        </la:link>
                                        <la:link href="../downloadpage"
                                                 styleClass="fads-btn fads-btn-primary fads-btn-compact">
                                            <i class="fa fa-download" aria-hidden="true"></i>
                                            <la:message key="labels.elevate_word_link_download"/>
                                        </la:link>
                                        <la:link href="../uploadpage"
                                                 styleClass="fads-btn fads-btn-success fads-btn-compact ${f:h(editableClass)}">
                                            <i class="fa fa-upload" aria-hidden="true"></i>
                                            <la:message key="labels.elevate_word_link_upload"/>
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
                                    <label class="col-sm-12 col-form-label"><la:message
                                            key="labels.elevate_word_file"/></label>
                                </div>
                            </div>
                            <div class="fads-card-footer">
                                <button type="submit" class="fads-btn fads-btn-primary" name="download"
                                        value="<la:message key="labels.elevate_word_button_download" />">
                                    <i class="fa fa-download" aria-hidden="true"></i>
                                    <la:message key="labels.elevate_word_button_download"/>
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

