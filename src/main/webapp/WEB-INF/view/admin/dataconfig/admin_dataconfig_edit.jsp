<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.data_crawling_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="dataConfig"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.data_crawling_title_details"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/dataconfig/">
                <la:hidden property="crudMode"/>
                <c:if test="${crudMode==2}">
                    <la:hidden property="id"/>
                    <la:hidden property="versionNo"/>
                </c:if>
                <la:hidden property="createdBy"/>
                <la:hidden property="createdTime"/>
                <la:hidden property="sortOrder"/>
                <div class="fads-row">
                    <div class="fads-col-md-12">
                        <div
                                class="fads-card <c:if test="${crudMode == 1 || crudMode == 2}">card-success</c:if>">
                            <div class="fads-card-header">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/header.jsp"></jsp:include>
                            </div>
                            <div class="fads-card-body">
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="fads-banner fads-banner-info">${msg}</div>
                                    </la:info>
                                    <la:errors property="_global"/>
                                </div>
                                <div class="fads-form-field">
                                    <label for="name" class="fads-label"><la:message
                                            key="labels.name"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="name"/>
                                        <la:text styleId="name" property="name" styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="handlerName" class="fads-label"><la:message
                                            key="labels.handler_name"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="handlerName"/>
                                        <la:select styleId="handlerName" property="handlerName"
                                                   styleClass="fads-textfield">
                                            <c:forEach var="hn" varStatus="s"
                                                       items="${handlerNameItems}">
                                                <la:option value="${f:u(hn.value)}">${f:h(hn.label)}</la:option>
                                            </c:forEach>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="handlerParameter" class="fads-label"><la:message
                                            key="labels.handler_parameter"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="handlerParameter"/>
                                        <la:textarea styleId="handlerParameter" property="handlerParameter"
                                                     styleClass="fads-textfield" rows="5"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="handlerScript" class="fads-label"><la:message
                                            key="labels.handler_script"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="handlerScript"/>
                                        <la:textarea styleId="handlerScript" property="handlerScript"
                                                     styleClass="fads-textfield" rows="5"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="boost" class="fads-label"><la:message
                                            key="labels.boost"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="boost"/>
                                        <la:text styleId="boost" property="boost" styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="permissions" class="fads-label"><la:message
                                            key="labels.permissions"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="permissions"/>
                                        <la:textarea styleId="permissions" property="permissions"
                                                     styleClass="fads-textfield"
                                                     rows="5"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="virtualHosts" class="fads-label"><la:message
                                            key="labels.virtual_hosts"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="virtualHosts"/>
                                        <la:textarea styleId="virtualHosts" property="virtualHosts"
                                                     styleClass="fads-textfield"
                                                     rows="5"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="available" class="fads-label"><la:message
                                            key="labels.available"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="available"/>
                                        <la:select styleId="available" property="available" styleClass="fads-textfield">
                                            <la:option value="true">
                                                <la:message key="labels.enabled"/>
                                            </la:option>
                                            <la:option value="false">
                                                <la:message key="labels.disabled"/>
                                            </la:option>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="permissions" class="fads-label"><la:message
                                            key="labels.description"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="description"/>
                                        <la:textarea styleId="description" property="description"
                                                     styleClass="fads-textfield"
                                                     rows="5"/>
                                    </div>
                                </div>
                            </div>
                            <div class="fads-card-footer">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/buttons.jsp"></jsp:include>
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
