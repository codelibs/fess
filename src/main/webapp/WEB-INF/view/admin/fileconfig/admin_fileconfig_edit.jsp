<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.file_crawling_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="crawl"/>
        <jsp:param name="menuType" value="fileConfig"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.file_crawling_title_details"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/fileconfig/">
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
                                    <label for="paths" class="fads-label"><la:message
                                            key="labels.paths"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="paths"/>
                                        <la:textarea styleId="paths" property="paths" styleClass="fads-textfield"
                                                     rows="5"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="includedPaths" class="fads-label"><la:message
                                            key="labels.included_paths"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="includedPaths"/>
                                        <la:textarea styleId="includedPaths" property="includedPaths"
                                                     styleClass="fads-textfield" rows="5"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="excludedPaths" class="fads-label"><la:message
                                            key="labels.excluded_paths"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="excludedPaths"/>
                                        <la:textarea styleId="excludedPaths" property="excludedPaths"
                                                     styleClass="fads-textfield" rows="5"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="includedDocPaths" class="fads-label"><la:message
                                            key="labels.included_doc_paths"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="includedDocPaths"/>
                                        <la:textarea styleId="includedDocPaths" property="includedDocPaths"
                                                     styleClass="fads-textfield" rows="5"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="excludedDocPaths" class="fads-label"><la:message
                                            key="labels.excluded_doc_paths"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="excludedDocPaths"/>
                                        <la:textarea styleId="excludedDocPaths" property="excludedDocPaths"
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
