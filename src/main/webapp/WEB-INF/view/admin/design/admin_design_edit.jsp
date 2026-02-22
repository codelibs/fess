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
                    <div>
                        <la:info id="msg" message="true">
                            <div class="fads-banner fads-banner-info">${msg}</div>
                        </la:info>
                        <la:errors property="_global"/>
                    </div>
                    <div class="fads-card fads-fads-card-success">
                        <c:if test="${editable}">
                            <la:form action="/admin/design/">
                                <div class="fads-card-header">
                                    <h3 class="fads-card-title">
                                        <la:message key="labels.design_title_edit_content"/>
                                    </h3>
                                </div>
                                <div class="fads-card-body">
                                    <h4>${f:h(displayFileName)}</h4>
                                    <div>
                                        <la:errors property="content"/>
                                        <la:textarea styleId="content" property="content" rows="20"
                                                     styleClass="fads-textfield"></la:textarea>
                                    </div>
                                </div>
                                <div class="fads-card-footer">
                                    <button type="submit" class="fads-btn fads-btn-default" name="back"
                                            value="<la:message key="labels.design_button_back" />">
                                        <i class="fa fa-arrow-circle-left" aria-hidden="true"></i>
                                        <la:message key="labels.design_button_back"/>
                                    </button>
                                    <button type="submit" class="fads-btn fads-btn-success" name="update"
                                            value="<la:message key="labels.design_button_update" />">
                                        <i class="fa fa-pencil-alt" aria-hidden="true"></i>
                                        <la:message key="labels.design_button_update"/>
                                    </button>
                                </div>
                                <la:hidden property="fileName"/>
                            </la:form>
                        </c:if>
                    </div>
                </div>
            </div>
        </section>
    </main>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}
