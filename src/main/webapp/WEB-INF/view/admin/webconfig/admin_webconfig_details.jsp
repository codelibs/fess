<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.web_crawling_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="webConfig"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.web_crawling_title_details"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/webconfig/">
                <la:hidden property="crudMode"/>
                <c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
                    <la:hidden property="id"/>
                    <la:hidden property="versionNo"/>
                </c:if>
                <la:hidden property="createdBy"/>
                <la:hidden property="createdTime"/>
                <la:hidden property="sortOrder"/>
                <div class="row">
                    <div class="col-md-12">
                        <div
                                class="card card-outline <c:if test="${crudMode == 1 || crudMode == 2}">card-success</c:if><c:if test="${crudMode == 3}">card-danger</c:if><c:if test="${crudMode == 4}">card-primary</c:if>">
                            <div class="card-header">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
                            </div>
                            <div class="card-body">
                                    <%-- Message --%>
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="alert alert-info">${msg}</div>
                                    </la:info>
                                    <la:errors/>
                                </div>
                                    <%-- Form Fields --%>
                                <table class="table table-bordered">
                                    <tbody>
                                    <c:if test="${id != null}">
                                        <tr>
                                            <th style="width: 25%"><la:message key="labels.id"/></th>
                                            <td>${f:h(id)}</td>
                                        </tr>
                                    </c:if>
                                    <tr>
                                        <th style="width: 25%"><la:message key="labels.name"/></th>
                                        <td>${f:h(name)}<la:hidden property="name"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.urls"/></th>
                                        <td>${f:br(f:h(urls))}<la:hidden property="urls"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.included_urls"/></th>
                                        <td>${f:br(f:h(includedUrls))}<la:hidden
                                                property="includedUrls"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.excluded_urls"/></th>
                                        <td>${f:br(f:h(excludedUrls))}<la:hidden
                                                property="excludedUrls"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.included_doc_urls"/></th>
                                        <td>${f:br(f:h(includedDocUrls))}<la:hidden
                                                property="includedDocUrls"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.excluded_doc_urls"/></th>
                                        <td>${f:br(f:h(excludedDocUrls))}<la:hidden
                                                property="excludedDocUrls"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.config_parameter"/></th>
                                        <td>${f:br(f:h(configParameter))}<la:hidden
                                                property="configParameter"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.depth"/></th>
                                        <td>${f:h(depth)}<la:hidden property="depth"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.max_access_count"/></th>
                                        <td>${f:h(maxAccessCount)}<la:hidden
                                                property="maxAccessCount"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.user_agent"/></th>
                                        <td>${f:h(userAgent)}<la:hidden property="userAgent"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.number_of_thread"/></th>
                                        <td>${f:h(numOfThread)}<la:hidden
                                                property="numOfThread"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.interval_time"/></th>
                                        <td>${f:h(intervalTime)}
                                            <la:hidden
                                                    property="intervalTime"/> <la:message
                                                    key="labels.millisec"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.boost"/></th>
                                        <td>${f:h(boost)}<la:hidden property="boost"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.permissions"/></th>
                                        <td>${f:br(f:h(permissions))}<la:hidden
                                                property="permissions"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.virtual_hosts"/></th>
                                        <td>${f:br(f:h(virtualHosts))}<la:hidden
                                                property="virtualHosts"/></td>
                                    </tr>
                                    <tr<c:if test="${!labelSettingEnabled}"> style="display:none"</c:if>>
                                        <th><la:message key="labels.label_type"/></th>
                                        <td><c:forEach var="l" varStatus="s"
                                                       items="${labelTypeItems}">
                                            <c:forEach var="ltid" varStatus="s"
                                                       items="${labelTypeIds}">
                                                <c:if test="${ltid==l.id}">
                                                    ${f:h(l.name)}<br/>
                                                </c:if>
                                            </c:forEach>
                                        </c:forEach></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.available"/></th>
                                        <td><la:hidden property="available"/> <c:if
                                                test="${available=='true'}">
                                            <la:message key="labels.enabled"/>
                                        </c:if> <c:if test="${available=='false'}">
                                            <la:message key="labels.disabled"/>
                                        </c:if></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.description"/></th>
                                        <td>${f:br(f:h(description))}<la:hidden
                                                property="description"/></td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="card-footer">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/buttons.jsp"></jsp:include>
                                <c:if test="${editable}"><la:link styleClass="btn btn-success"
                                                                  href="/admin/scheduler/createnewjob/web_crawling/${f:u(id)}/${fe:base64(name)}">
                                    <em class="fa fa-plus-circle"></em>
                                    <la:message key="labels.web_crawling_button_create_job"/>
                                </la:link></c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </la:form>
        </section>
    </div>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
