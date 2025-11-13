<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.dashboard_title_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="dashboard"/>
        <jsp:param name="menuType" value="dashboard"/>
    </jsp:include>

    <main class="content-wrapper position-relative">
        <iframe class="w-100 h-100 position-absolute" style="border: 0;"
                src="<%=request.getContextPath()%>${serverPath}<%= response.encodeURL("/_plugin/kopf/") %>"
                title="<la:message key="labels.dashboard_plugin" />"></iframe>
    </main>

    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}
