<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.wizard_title_configuration"/></title>
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
                            <la:message key="labels.wizard_crawling_config_title"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><la:link href="/admin/wizard/">
                                <la:message key="labels.wizard_start_title"/>
                            </la:link></li>
                            <li class="breadcrumb-item active"><la:message
                                    key="labels.wizard_crawling_config_title"/></li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/wizard/">
                <div class="fads-row">
                    <div class="fads-col-md-12">
                        <div class="fads-card fads-fads-card-success">
                            <div class="fads-card-header">
                                <h3 class="fads-card-title">
                                    <la:message key="labels.wizard_crawling_setting_title"/>
                                </h3>
                            </div>
                            <div class="fads-card-body">
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="fads-banner fads-banner-info">${msg}</div>
                                    </la:info>
                                    <la:errors property="_global"/>
                                </div>
                                <div class="fads-form-field">
                                    <label for="crawlingConfigName" class="fads-label"><la:message
                                            key="labels.wizard_crawling_config_name"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="crawlingConfigName"/>
                                        <la:text styleId="crawlingConfigName" property="crawlingConfigName"
                                                 styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="crawlingConfigPath" class="fads-label"><la:message
                                            key="labels.wizard_crawling_config_path"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="crawlingConfigPath"/>
                                        <la:text styleId="crawlingConfigPath" property="crawlingConfigPath"
                                                 styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="maxAccessCount" class="fads-label"><la:message
                                            key="labels.maxAccessCount"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="maxAccessCount"/>
                                        <la:text styleId="maxAccessCount" property="maxAccessCount"
                                                 styleClass="fads-textfield"/>
                                    </div>
                                </div>
                                <div class="fads-form-field">
                                    <label for="depth" class="fads-label"><la:message
                                            key="labels.depth"/></label>
                                    <div class="fads-col-sm-9">
                                        <la:errors property="depth"/>
                                        <la:text styleId="depth" property="depth" styleClass="fads-textfield"/>
                                    </div>
                                </div>
                            </div>
                            <div class="fads-card-footer">
                                <button type="submit" class="fads-btn fads-btn-subtle" name="index"
                                        value="<la:message key="labels.wizard_button_cancel"/>">
                                    <la:message key="labels.wizard_button_cancel"/>
                                </button>
                                <button type="submit" class="fads-btn fads-btn-primary"
                                        name="crawlingConfig"
                                        value="<la:message key="labels.wizard_button_register_again"/>">
                                    <i class="fa fa-redo-alt" aria-hidden="true"></i>
                                    <la:message key="labels.wizard_button_register_again"/>
                                </button>
                                <button type="submit" class="fads-btn fads-btn-success"
                                        name="crawlingConfigNext"
                                        value="<la:message key="labels.wizard_button_register_next"/>">
                                    <i class="fa fa-arrow-circle-right" aria-hidden="true"></i>
                                    <la:message key="labels.wizard_button_register_next"/>
                                </button>
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
