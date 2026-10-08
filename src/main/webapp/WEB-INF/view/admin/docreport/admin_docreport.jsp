<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.docreport_title"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="log"/>
        <jsp:param name="menuType" value="docReport"/>
    </jsp:include>
    <main id="mainContent" class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.docreport_title"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item active"><la:link href="/admin/docreport">
                                <la:message key="labels.docreport_title"/>
                            </la:link></li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <ul class="nav nav-tabs docreport-tabs mb-3">
                <c:if test="${not fesenPluginless}">
                <li class="nav-item"><a class="nav-link ${tab == 'duplicate' ? 'active' : ''}"
                                        href="${fe:url('/admin/docreport/duplicate')}${empty url ? '' : '?url='}${f:u(url)}"><la:message
                        key="labels.docreport_tab_duplicate"/></a></li>
                </c:if>
                <li class="nav-item"><a class="nav-link ${tab == 'dormant' ? 'active' : ''}"
                                        href="${fe:url('/admin/docreport/dormant')}${empty url ? '' : '?url='}${f:u(url)}"><la:message
                        key="labels.docreport_tab_dormant"/></a></li>
            </ul>
            <c:choose>
                <c:when test="${tab == 'dormant'}">
                    <jsp:include page="/WEB-INF/view/admin/docreport/admin_docreport_dormant.jsp"/>
                </c:when>
                <c:otherwise>
                    <jsp:include page="/WEB-INF/view/admin/docreport/admin_docreport_duplicate.jsp"/>
                </c:otherwise>
            </c:choose>
        </section>
    </main>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}
