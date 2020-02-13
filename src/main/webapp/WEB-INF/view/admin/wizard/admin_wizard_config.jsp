<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.wizard_title_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="wizard"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.wizard_crawling_config_title"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
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
                <div class="row">
                    <div class="col-md-12">
                        <div class="card card-outline card-success">
                            <div class="card-header">
                                <h3 class="card-title">
                                    <la:message key="labels.wizard_crawling_setting_title"/>
                                </h3>
                            </div>
                            <div class="card-body">
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="alert alert-info">${msg}</div>
                                    </la:info>
                                    <la:errors property="_global"/>
                                </div>
                                <div class="form-group row">
                                    <label for="crawlingConfigName" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.wizard_crawling_config_name"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="crawlingConfigName"/>
                                        <la:text styleId="crawlingConfigName" property="crawlingConfigName"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="crawlingConfigPath" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.wizard_crawling_config_path"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="crawlingConfigPath"/>
                                        <la:text styleId="crawlingConfigPath" property="crawlingConfigPath"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="maxAccessCount" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.maxAccessCount"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="maxAccessCount"/>
                                        <la:text styleId="maxAccessCount" property="maxAccessCount"
                                                 styleClass="form-control"/>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="depth" class="col-sm-3 text-sm-right col-form-label"><la:message
                                            key="labels.depth"/></label>
                                    <div class="col-sm-9">
                                        <la:errors property="depth"/>
                                        <la:text styleId="depth" property="depth" styleClass="form-control"/>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer">
                                <button type="submit" class="btn btn-outline-secondary" name="index"
                                        value="<la:message key="labels.wizard_button_cancel"/>">
                                    <la:message key="labels.wizard_button_cancel"/>
                                </button>
                                <button type="submit" class="btn btn-primary"
                                        name="crawlingConfig"
                                        value="<la:message key="labels.wizard_button_register_again"/>">
                                    <em class="fa fa-redo-alt"></em>
                                    <la:message key="labels.wizard_button_register_again"/>
                                </button>
                                <button type="submit" class="btn btn-success"
                                        name="crawlingConfigNext"
                                        value="<la:message key="labels.wizard_button_register_next"/>">
                                    <em class="fa fa-arrow-circle-right"></em>
                                    <la:message key="labels.wizard_button_register_next"/>
                                </button>
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
