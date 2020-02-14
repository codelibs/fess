<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message key="labels.plugin_install_title"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="plugin"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.plugin_install_title"/>
                        </h1>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="row">
                <div class="col-md-12">
                    <la:info id="msg" message="true">
                        <div class="alert alert-info">${msg}</div>
                    </la:info>
                    <la:errors property="_global"/>
                </div>
                <div class="col-md-12">
                    <div class="card card-outline card-success">
                        <div class="card-header">
                            <h3 class="card-title">
                                <la:message key="labels.plugin_install"/>
                            </h3>
                        </div>
                        <la:form action="/admin/plugin/install" enctype="multipart/form-data">
                        <div class="card-body">
                            <ul class="nav nav-tabs" role="tablist">
                                <li role="presentation" class="nav-item"><a href="#remote" aria-controls="remote" class="nav-link active"
                                                                          role="tab"
                                                                          data-toggle="tab"
                                ><la:message key="labels.plugin_remote_install"/></a></li>
                                <li role="presentation" class="nav-item"><a href="#local" aria-controls="local" class="nav-link" role="tab"
                                                           data-toggle="tab"><la:message
                                        key="labels.plugin_local_install"
                                /></a></li>
                            </ul>
                            <div class="tab-content">
                                <div role="tabpanel" class="tab-pane active" id="remote">
                                    <div class="card-body">
                                        <div class="form-group row">
                                            <la:errors property="selectedArtifact"/>
                                            <la:select styleId="artifacts" property="id" styleClass="form-control">
                                                <c:forEach var="item" varStatus="s"
                                                           items="${availableArtifactItems}">
                                                    <la:option
                                                            value="${f:h(item.id)}">${f:h(item.name)}-${f:h(item.version)}</la:option>
                                                </c:forEach>
                                            </la:select>
                                        </div>
                                    </div>
                                </div>
                                <div role="tabpanel" class="tab-pane" id="local">
                                    <div class="card-body">
                                        <div class="form-group row">
                                            <label for="jarFile" class="col-md-3 text-sm-right col-form-label"><la:message
                                                    key="labels.plugin_jar_file"/></label>
                                            <div class="col-md-9 text-sm-right col-form-label">
                                                <input id="jarFile" type="file" name="jarFile" class="form-control-file"/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <button type="submit" class="btn btn-success" name="install"
                                    value="<la:message key="labels.crud_button_install" />"
                            >
                                <em class="fa fa-plus"></em>
                                <la:message key="labels.crud_button_install"/>
                            </button>
                        </div>
                        </la:form>
                        <div class="card-footer">
                            <la:form action="/admin/plugin/">
                            <button type="submit" class="btn btn-default" name="back"
                                    value="<la:message key="labels.crud_button_back" />">
                                <em class="fa fa-arrow-circle-left"></em>
                                <la:message key="labels.crud_button_back"/>
                            </button>
                            </la:form>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
