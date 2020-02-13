<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message key="labels.plugin_title"/></title>
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
                            <la:message key="labels.plugin_title"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="row">
                <div class="col-md-12">
                    <div class="card card-outline card-primary">
                        <div class="card-header">
                            <h3 class="card-title">
                                <la:message key="labels.crud_title_list"/>
                            </h3>
                            <div class="card-tools">
                                <div class="btn-group">
                                    <la:link href="installplugin"
                                             styleClass="btn btn-success btn-xs ${f:h(editableClass)}">
                                        <em class="fa fa-plus"></em>
                                        <la:message key="labels.plugin_install"/>
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
                            <%-- List --%>
                            <div class="data-wrapper">
                                <div class="row">
                                    <div class="col-sm-12">
                                        <table class="table table-bordered table-striped">
                                            <thead>
                                            <tr>
                                                <th><la:message key="labels.plugin_type"/></th>
                                                <th><la:message key="labels.plugin_name"/></th>
                                                <th><la:message key="labels.plugin_version"/></th>
                                                <th></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="artifact" varStatus="s" items="${installedArtifactItems}">
                                                <tr>
                                                    <td>${f:h(artifact.type)}</td>
                                                    <td>${f:h(artifact.name)}</td>
                                                    <td>${f:h(artifact.version)}</td>
                                                    <td>
                                                        <c:if test="${editable}">
                                                            <div class="text-center">
                                                                <button type="button" class="btn btn-danger btn-xs"
                                                                        name="delete" data-toggle="modal"
                                                                        data-target='#confirmToDelete-${f:h(artifact.name)}-${f:h(artifact.version).replace(".", "\\.")}'
                                                                        value="<la:message key="labels.crud_button_delete" />"
                                                                >
                                                                    <em class="fa fa-trash"></em>
                                                                    <la:message key="labels.crud_button_delete"/>
                                                                </button>
                                                            </div>
                                                            <div class="modal fade"
                                                                 id='confirmToDelete-${f:h(artifact.name)}-${f:h(artifact.version)}'
                                                                 tabindex="-1" role="dialog"
                                                            >
                                                                <div class="modal-dialog">
                                                                    <div class="modal-content bg-danger">
                                                                        <div class="modal-header">
                                                                            <h4 class="modal-title">
                                                                                <la:message
                                                                                        key="labels.crud_title_delete"/>
                                                                            </h4>
                                                                            <button type="button" class="close"
                                                                                    data-dismiss="modal"
                                                                                    aria-label="Close">
                                                                                <span aria-hidden="true">×</span>
                                                                            </button>
                                                                        </div>
                                                                        <div class="modal-body">
                                                                            <p>
                                                                                <la:message
                                                                                        key="labels.crud_delete_confirmation"/>
                                                                            </p>
                                                                        </div>
                                                                        <div class="modal-footer justify-content-between">
                                                                            <button type="button"
                                                                                    class="btn btn-outline-light"
                                                                                    data-dismiss="modal">
                                                                                <la:message
                                                                                        key="labels.crud_button_cancel"/>
                                                                            </button>
                                                                            <la:form action="/admin/plugin/">
                                                                                <input type="hidden" name="name"
                                                                                       value="${f:h(artifact.name)}">
                                                                                <input type="hidden" name="version"
                                                                                       value="${f:h(artifact.version)}">
                                                                                <button type="submit"
                                                                                        class="btn btn-outline-light"
                                                                                        name="delete"
                                                                                        value="<la:message key="labels.crud_button_delete" />"
                                                                                >
                                                                                    <em class="fa fa-trash"></em>
                                                                                    <la:message
                                                                                            key="labels.crud_button_delete"/>
                                                                                </button>
                                                                            </la:form>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
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

