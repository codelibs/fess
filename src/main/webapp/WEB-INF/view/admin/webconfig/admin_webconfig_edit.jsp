<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.web_crawling_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="webConfig"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.web_crawling_title_details"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/webconfig/">
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
                                <div class="fads-form-field">
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
                                    <label for="urls" class="fads-label"><la:message
                                            key="labels.urls"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="urls"/>
                                        <la:textarea styleId="urls" property="urls" styleClass="fads-textfield"
                                                     rows="5"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="includedUrls" class="fads-label"><la:message
                                            key="labels.included_urls"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="includedUrls"/>
                                        <la:textarea styleId="includedUrls" property="includedUrls"
                                                     styleClass="fads-textfield" rows="5"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="excludedUrls" class="fads-label"><la:message
                                            key="labels.excluded_urls"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="excludedUrls"/>
                                        <la:textarea styleId="excludedUrls" property="excludedUrls"
                                                     styleClass="fads-textfield" rows="5"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="includedDocUrls" class="fads-label"><la:message
                                            key="labels.included_doc_urls"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="includedDocUrls"/>
                                        <la:textarea styleId="includedDocUrls" property="includedDocUrls"
                                                     styleClass="fads-textfield" rows="5"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="excludedDocUrls" class="fads-label"><la:message
                                            key="labels.excluded_doc_urls"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="excludedDocUrls"/>
                                        <la:textarea styleId="excludedDocUrls" property="excludedDocUrls"
                                                     styleClass="fads-textfield" rows="5"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="configParameter" class="fads-label"><la:message
                                            key="labels.config_parameter"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="configParameter"/>
                                        <la:textarea styleId="configParameter" property="configParameter"
                                                     styleClass="fads-textfield" rows="5"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="depth" class="fads-label"><la:message
                                            key="labels.depth"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="depth"/>
                                        <input type="number" name="depth" id="depth"
                                               value="${f:h(depth)}" class="fads-textfield"
                                               min="0" max="10000">
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="maxAccessCount" class="fads-label"><la:message
                                            key="labels.max_access_count"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="maxAccessCount"/>
                                        <input type="number" name="maxAccessCount" id="maxAccessCount"
                                               value="${f:h(maxAccessCount)}" class="fads-textfield"
                                               min="0" max="1000000000">
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="userAgent" class="fads-label"><la:message
                                            key="labels.user_agent"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="userAgent"/>
                                        <la:text styleId="userAgent" property="userAgent" styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="numOfThread" class="fads-label"><la:message
                                            key="labels.number_of_thread"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="numOfThread"/>
                                        <input type="number" name="numOfThread" id="numOfThread"
                                               value="${f:h(numOfThread)}" class="fads-textfield"
                                               min="1" max="1000">
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="intervalTime" class="fads-label"><la:message
                                            key="labels.interval_time"/></label>
                                    <div class="form-inline col-sm-9">
                                        <la:errors property="intervalTime"/>
                                        <input type="number" name="intervalTime" id="intervalTime"
                                               value="${f:h(intervalTime)}" class="fads-textfield"
                                               min="0">
                                        <la:message key="labels.millisec"/>
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
                                <div class="fads-form-field"<c:if
                                        test="${!labelSettingEnabled}"> style="display:none"</c:if>>
                                    <label for="labelTypeIds" class="fads-label"><la:message
                                            key="labels.label_type"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="labelTypeIds"/>
                                        <la:select styleId="labelTypeIds" property="labelTypeIds" multiple="true"
                                                   styleClass="fads-textfield">
                                            <c:forEach var="l" varStatus="s" items="${labelTypeItems}">
                                                <la:option value="${f:u(l.id)}">${f:h(l.name)}</la:option>
                                            </c:forEach>
                                        </la:select>
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
