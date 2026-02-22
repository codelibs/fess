<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><<la:message ke="labels.admin_brand_title"/> | <<la:message ke="labels.error_title"/></title>
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
                            <la:message key="labels.error_title"/>
                        </h1>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="callout callout-danger lead">
                <p>
                    <la:errors/>
                </p>
            </div>

        </section>
    </main>

    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>

</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}
