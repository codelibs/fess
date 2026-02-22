<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.elevate_word_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="suggest"/>
        <jsp:param name="menuType" value="elevateWord"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.elevate_word_title_details"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item active"><la:link href="/admin/elevateword">
                                <la:message key="labels.elevate_word_link_list"/>
                            </la:link></li>
                            <c:if test="${crudMode == 1}">
                                <li class="breadcrumb-item active"><la:message
                                        key="labels.elevate_word_link_create"/></li>
                            </c:if>
                            <c:if test="${crudMode == 2}">
                                <li class="breadcrumb-item active"><la:message
                                        key="labels.elevate_word_link_edit"/></li>
                            </c:if>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/elevateword/">
                <la:hidden property="crudMode"/>
                <c:if test="${crudMode==2}">
                    <la:hidden property="id"/>
                    <la:hidden property="versionNo"/>
                </c:if>
                <la:hidden property="createdBy"/>
                <la:hidden property="createdTime"/>
                <div class="fads-row">
                    <div class="fads-col-md-12">
                        <div
                                class="fads-card <c:if test="${crudMode == 1 || crudMode == 2}">card-success</c:if>">
                            <div class="fads-card-header">
                                <h3 class="fads-card-title">
                                    <c:if test="${crudMode == 1}">
                                        <la:message key="labels.elevate_word_link_create"/>
                                    </c:if>
                                    <c:if test="${crudMode == 2}">
                                        <la:message key="labels.elevate_word_link_edit"/>
                                    </c:if>
                                </h3>
                                <div class="fads-card-tools">
                                    <div class="btn-group">
                                        <la:link href="/admin/elevateword"
                                                 styleClass="fads-btn fads-btn-default fads-btn-compact">
                                            <i class="fa fa-th-list" aria-hidden="true"></i>
                                            <la:message key="labels.elevate_word_link_list"/>
                                        </la:link>
                                        <la:link href="../createnew"
                                                 styleClass="fads-btn fads-btn-success fads-btn-compact">
                                            <i class="fa fa-plus" aria-hidden="true"></i>
                                            <la:message key="labels.elevate_word_link_create"/>
                                        </la:link>
                                        <la:link href="../downloadpage"
                                                 styleClass="fads-btn fads-btn-primary fads-btn-compact">
                                            <i class="fa fa-download" aria-hidden="true"></i>
                                            <la:message key="labels.elevate_word_link_download"/>
                                        </la:link>
                                        <la:link href="../uploadpage"
                                                 styleClass="fads-btn fads-btn-success fads-btn-compact">
                                            <i class="fa fa-upload" aria-hidden="true"></i>
                                            <la:message key="labels.elevate_word_link_upload"/>
                                        </la:link>
                                    </div>
                                </div>
                            </div>
                            <div class="fads-card-body">
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="fads-banner fads-banner-info">${msg}</div>
                                    </la:info>
                                    <la:errors property="_global"/>
                                </div>
                                <div class="fads-form-field">
                                    <label for="suggestWord" class="fads-label"><la:message
                                            key="labels.elevate_word_suggest_word"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="suggestWord"/>
                                        <la:text styleId="suggestWord" property="suggestWord"
                                                 styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="reading" class="fads-label"><la:message
                                            key="labels.elevate_word_reading"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="reading"/>
                                        <la:text styleId="reading" property="reading" styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="permissions" class="fads-label"><la:message
                                            key="labels.elevate_word_permissions"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="permissions"/>
                                        <la:textarea styleId="permissions" property="permissions"
                                                     styleClass="fads-textfield" rows="5"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
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
                                    <label for="boost" class="fads-label"><la:message
                                            key="labels.elevate_word_boost"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="boost"/>
                                        <la:text styleId="boost" property="boost" styleClass="fads-textfield"/>
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

