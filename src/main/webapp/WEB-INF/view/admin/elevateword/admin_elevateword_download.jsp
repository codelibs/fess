<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.elevate_word_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="suggest"/>
        <jsp:param name="menuType" value="elevateWord"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.elevate_word_configuration"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item active"><la:link href="/admin/elevateword">
                                <la:message key="labels.elevate_word_link_list"/>
                            </la:link></li>
                            <li class="breadcrumb-item active"><a href="#"><la:message
                                    key="labels.elevate_word_link_download"/></a></li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/elevateword/">
                <div class="row">
                    <div class="col-md-12">
                        <div class="card card-outline card-primary">
                                <%-- Card Header --%>
                            <div class="card-header">
                                <h3 class="card-title">
                                    <la:message key="labels.elevate_word_link_download"/>
                                </h3>
                                <div class="card-tools">
                                    <div class="btn-group">
                                        <la:link href="/admin/elevateword"
                                                 styleClass="btn btn-default btn-xs">
                                            <em class="fa fa-th-list"></em>
                                            <la:message key="labels.elevate_word_link_list"/>
                                        </la:link>
                                        <la:link href="../createnew"
                                                 styleClass="btn btn-success btn-xs ${f:h(editableClass)}">
                                            <em class="fa fa-plus"></em>
                                            <la:message key="labels.elevate_word_link_create"/>
                                        </la:link>
                                        <la:link href="../downloadpage"
                                                 styleClass="btn btn-primary btn-xs">
                                            <em class="fa fa-download"></em>
                                            <la:message key="labels.elevate_word_link_download"/>
                                        </la:link>
                                        <la:link href="../uploadpage"
                                                 styleClass="btn btn-success btn-xs ${f:h(editableClass)}">
                                            <em class="fa fa-upload"></em>
                                            <la:message key="labels.elevate_word_link_upload"/>
                                        </la:link>
                                    </div>
                                </div>
                            </div>
                            <div class="card-body">
                                    <%-- Message --%>
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="alert alert-info">${msg}</div>
                                    </la:info>
                                    <la:errors/>
                                </div>
                                <div class="form-group row">
                                    <label class="col-sm-12 col-form-label"><la:message
                                            key="labels.elevate_word_file"/></label>
                                </div>
                            </div>
                            <div class="card-footer">
                                <button type="submit" class="btn btn-primary" name="download"
                                        value="<la:message key="labels.elevate_word_button_download" />">
                                    <em class="fa fa-download"></em>
                                    <la:message key="labels.elevate_word_button_download"/>
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

