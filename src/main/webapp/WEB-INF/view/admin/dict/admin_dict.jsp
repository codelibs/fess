<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.dict_configuration"/></title>
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
                            <la:message key="labels.dict_list_title"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li><la:message key="labels.dict_list_link"/></li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="fads-row">
                <div class="fads-col-md-12">
                    <div class="fads-card">
                        <div class="fads-card-header">
                            <h3 class="fads-card-title">
                                <la:message key="labels.dict_list_link"/>
                            </h3>
                        </div>
                        <div class="fads-card-body">
                            <%-- Message --%>
                            <div>
                                <la:info id="msg" message="true">
                                    <div class="fads-banner fads-banner-info">${msg}</div>
                                </la:info>
                                <la:errors/>
                            </div>
                            <%-- List --%>
                            <c:if test="${fn:length(dictFiles) == 0}">
                                <div class="fads-row top10">
                                    <div class="fads-col-sm-12">
                                        <i class="fa fa-info-circle text-primary" aria-hidden="true"></i>
                                        <la:message key="labels.list_could_not_find_crud_table"/>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${fn:length(dictFiles) > 0}">
                                <table class="fads-table" aria-label="<la:message key="labels.dict_list_title" />">
                                    <thead>
                                    <tr>
                                        <th><la:message key="labels.dictionary_name"/></th>
                                        <th><la:message key="labels.dictionary_type"/></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="data" varStatus="s" items="${dictFiles}">
                                        <tr data-href="${f:h(data.type)}/?dictId=${f:h(data.id)}" role="button" tabindex="0">
                                            <td>${f:h(data.path)}</td>
                                            <td>${f:h(data.type)}</td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:if>
                        </div>
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
