<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message key="labels.plugin_install_title"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="plugin"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.plugin_install_title"/>
                        </h1>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="fads-row">
                <div class="fads-col-md-12">
                    <la:info id="msg" message="true">
                        <div class="fads-banner fads-banner-info">${msg}</div>
                    </la:info>
                    <la:errors property="_global"/>
                </div>
                <div class="fads-col-md-12">
                    <div class="fads-card fads-fads-card-success">
                        <div class="fads-card-header">
                            <h3 class="fads-card-title">
                                <la:message key="labels.plugin_install"/>
                            </h3>
                        </div>
                        <la:form action="/admin/plugin/install" enctype="multipart/form-data">
                        <div class="fads-card-body">
                            <ul class="fads-tabs" role="tablist">
                                <li role="presentation" class="fads-tab-item"><a href="#remote" aria-controls="remote" class="fads-tab-link active"
                                                                          role="tab"
                                                                          data-fads-tab="true"
                                ><la:message key="labels.plugin_remote_install"/></a></li>
                                <li role="presentation" class="fads-tab-item"><a href="#local" aria-controls="local" class="fads-tab-link" role="tab"
                                                           data-fads-tab="true"><la:message
                                        key="labels.plugin_local_install"
                                /></a></li>
                            </ul>
                            <div class="fads-tab-content">
                                <div role="tabpanel" class="fads-tab-pane active" id="remote">
                                    <div class="fads-card-body">
                                        <div class="fads-form-field">
                                            <la:errors property="selectedArtifact"/>
                                            <la:select styleId="artifacts" property="id" styleClass="fads-textfield">
                                                <c:forEach var="item" varStatus="s"
                                                           items="${availableArtifactItems}">
                                                    <la:option
                                                            value="${f:h(item.id)}">${f:h(item.name)}-${f:h(item.version)}</la:option>
                                                </c:forEach>
                                            </la:select>
                                        </div>
                                    </div>
                                </div>
                                <div role="tabpanel" class="fads-tab-pane" id="local">
                                    <div class="fads-card-body">
                                        <div class="fads-form-field">
                                            <label for="jarFile" class="col-md-3 text-sm-right col-form-label"><la:message
                                                    key="labels.plugin_jar_file"/></label>
                                            <div class="col-md-9 text-sm-right col-form-label">
                                                <input id="jarFile" type="file" name="jarFile" class="fads-textfield"/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <button type="submit" class="fads-btn fads-btn-success" name="install"
                                    value="<la:message key="labels.crud_button_install" />"
                            >
                                <i class="fa fa-plus" aria-hidden="true"></i>
                                <la:message key="labels.crud_button_install"/>
                            </button>
                        </div>
                        </la:form>
                        <div class="fads-card-footer">
                            <la:form action="/admin/plugin/">
                            <button type="submit" class="fads-btn fads-btn-default" name="back"
                                    value="<la:message key="labels.crud_button_back" />">
                                <i class="fa fa-arrow-circle-left" aria-hidden="true"></i>
                                <la:message key="labels.crud_button_back"/>
                            </button>
                            </la:form>
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
