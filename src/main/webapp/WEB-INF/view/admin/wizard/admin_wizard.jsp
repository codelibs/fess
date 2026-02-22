<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.wizard_title_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="wizard"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.wizard_title_configuration"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item active"><la:link href="/admin/wizard/">
                                <la:message key="labels.wizard_start_title"/>
                            </la:link></li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/wizard/">
                <div class="fads-row">
                    <div class="fads-col-md-12">
                        <div class="fads-card">
                            <div class="fads-card-header">
                                <h3 class="fads-card-title">
                                    <la:message key="labels.wizard_start_title"/>
                                </h3>
                            </div>
                            <div class="fads-card-body">
                                    <%-- Message: BEGIN --%>
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="fads-banner fads-banner-info">${msg}</div>
                                    </la:info>
                                    <la:errors/>
                                </div>
                                    <%-- Message: END --%>
                                <p>
                                    <la:message key="labels.wizard_start_desc"/>
                                </p>
                            </div>
                            <div class="fads-card-footer">
                                <c:if test="${editable}">
                                    <button type="submit" class="fads-btn fads-btn-primary"
                                            name="crawlingConfigForm"
                                            value="<la:message key="labels.wizard_start_button"/>">
                                        <i class="fa fa-arrow-circle-right" aria-hidden="true"></i>
                                        <la:message key="labels.wizard_start_button"/>
                                    </button>
                                </c:if>
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
