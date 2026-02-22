<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.design_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="design"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.design_configuration"/>
                        </h1>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="fads-row">
                <div class="fads-col-md-12">
                    <la:info id="msg" message="true">
                        <div class="fads-banner fads-banner-info">${msg}</div>
                    </la:info>
                    <la:errors property="_global"/>
                </div>
                <div class="fads-col-md-6">
                    <div class="fads-card">
                        <la:form action="/admin/design/">
                            <div class="fads-card-header">
                                <h3 class="fads-card-title">
                                    <la:message key="labels.design_title_file"/>
                                </h3>
                            </div>
                            <div class="fads-card-body">
                                <div class="fads-form-field">
                                    <la:errors property="fileName"/>
                                    <la:select styleId="fileName" property="fileName" styleClass="fads-textfield">
                                        <c:forEach var="item" varStatus="s" items="${fileNameItems}">
                                            <la:option value="${item}">${f:h(item)}</la:option>
                                        </c:forEach>
                                    </la:select>
                                </div>
                            </div>
                            <div class="fads-card-footer">
                                <button type="submit" class="fads-btn fads-btn-primary" name="download"
                                        value="<la:message key="labels.design_download_button" />">
                                    <i class="fa fa-download" aria-hidden="true"></i>
                                    <la:message key="labels.design_download_button"/>
                                </button>
                                <c:if test="${editable}">
                                    <button type="button" class="fads-btn fads-btn-danger" name="delete"
                                            data-fads-dialog="confirmToDelete"
                                            value="<la:message key="labels.design_delete_button" />">
                                        <i class="fa fa-trash" aria-hidden="true"></i>
                                        <la:message key="labels.design_delete_button"/>
                                    </button>
                                    <div class="fads-dialog-overlay" id="confirmToDelete" tabindex="-1"
                                         role="dialog">
                                        <div class="fads-dialog">
                                            <div class="fads-dialog-danger">
                                                <div class="fads-dialog-header">
                                                    <h4 class="">
                                                        <la:message key="labels.crud_title_delete"/>
                                                    </h4>
                                                    <button type="button" class="close" data-fads-dialog-close
                                                            aria-label="Close">
                                                        <span aria-hidden="true">×</span>
                                                    </button>
                                                </div>
                                                <div class="fads-dialog-body">
                                                    <p>
                                                        <la:message key="labels.crud_delete_confirmation"/>
                                                    </p>
                                                </div>
                                                <div class="fads-dialog-footer">
                                                    <button type="button" class="fads-btn fads-btn-outline-light"
                                                            data-fads-dialog-close>
                                                        <la:message key="labels.crud_button_cancel"/>
                                                    </button>
                                                    <button type="submit" class="fads-btn fads-btn-outline-light"
                                                            name="delete"
                                                            value="<la:message key="labels.crud_button_delete" />">
                                                        <i class="fa fa-trash" aria-hidden="true"></i>
                                                        <la:message key="labels.crud_button_delete"/>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </la:form>
                    </div>
                </div>
                <div class="fads-col-md-6">
                    <c:if test="${editable}">
                        <div class="fads-card">
                            <la:form action="/admin/design/">
                                <div class="fads-card-header">
                                    <h3 class="fads-card-title">
                                        <la:message key="labels.design_file_title_edit"/>
                                    </h3>
                                </div>
                                <div class="fads-card-body">
                                    <div class="fads-form-field">
                                        <la:errors property="fileName"/>
                                        <la:select styleId="fileName" property="fileName" styleClass="fads-textfield">
                                            <c:forEach var="item" items="${jspFileNameItems}">
                                                <la:option value="${f:u(item.first)}">${f:h(item.second)}</la:option>
                                            </c:forEach>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="fads-card-footer">
                                    <button type="submit" class="fads-btn fads-btn-primary" name="edit"
                                            value="<la:message key="labels.design_edit_button" />">
                                        <i class="fa fa-pencil-alt" aria-hidden="true"></i>
                                        <la:message key="labels.design_edit_button"/>
                                    </button>
                                    <button type="submit" class="fads-btn fads-btn-warning"
                                            name="editAsUseDefault"
                                            value="<la:message key="labels.design_use_default_button" />">
                                        <i class="fa fa-recycle" aria-hidden="true"></i>
                                        <la:message key="labels.design_use_default_button"/>
                                    </button>
                                </div>
                            </la:form>
                        </div>
                    </c:if>
                </div>
                <div class="fads-col-md-12">
                    <c:if test="${editable}">
                        <div class="fads-card fads-fads-card-success">
                            <la:form action="/admin/design/upload/"
                                     enctype="multipart/form-data">
                                <div class="fads-card-header">
                                    <h3 class="fads-card-title">
                                        <la:message key="labels.design_title_file_upload"/>
                                    </h3>
                                </div>
                                <div class="fads-card-body">
                                    <div class="fads-form-field">
                                        <label class="fads-label"><la:message
                                                key="labels.design_file"/></label>
                                        <div class="form-inline col-sm-9">
                                            <la:errors property="designFile"/>
                                            <input type="file" name="designFile" class="fads-textfield"/>
                                        </div>
                                    </div>
                                    <div class="fads-form-field">
                                        <label class="fads-label"><la:message
                                                key="labels.design_file_name"/></label>
                                        <div class="fads-col-sm-9">
                                            <la:errors property="designFileName"/>
                                            <la:text styleId="designFileName" property="designFileName"
                                                     styleClass="fads-textfield"/>
                                        </div>
                                    </div>
                                </div>
                                <div class="fads-card-footer">
                                    <button type="submit" class="fads-btn fads-btn-success" name="upload"
                                            value="<la:message key="labels.design_button_upload" />">
                                        <i class="fa fa-upload" aria-hidden="true"></i>
                                        <la:message key="labels.design_button_upload"/>
                                    </button>
                                </div>
                            </la:form>
                        </div>
                    </c:if>
                </div>
            </div>
        </section>
    </main>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}
