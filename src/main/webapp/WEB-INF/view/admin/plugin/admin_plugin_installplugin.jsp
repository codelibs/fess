<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title" /> | <la:message
            key="labels.plugin_install_title" /></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system" />
        <jsp:param name="menuType" value="plugin" />
    </jsp:include>
    <div class="content-wrapper">
        <section class="content-header">
            <h1>
                <la:message key="labels.plugin_install_title" />
            </h1>
            <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
        </section>
        <section class="content">
            <la:form action="/admin/plugin/" styleClass="form-horizontal">
                <div class="row">
                    <div class="col-md-12">
                            <!-- /.box-header -->
                            <div class="box-body">
                                    <%-- Message --%>
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="alert alert-info">${msg}</div>
                                    </la:info>
                                    <la:errors />
                                </div>
                                    <%-- List --%>
                                <div class="data-wrapper">
                                    <div class="row">
                                        <div class="col-sm-12">
                                            <table class="table table-bordered table-striped dataTable">
                                                <tbody>
                                                <tr>
                                                    <th><la:message key="labels.plugin_type" /></th>
                                                    <th><la:message key="labels.plugin_name" /></th>
                                                    <th><la:message key="labels.plugin_version" /></th>
                                                    <th></th>
                                                </tr>
                                                <c:forEach var="artifact" varStatus="s"
                                                           items="${availableArtifactItems}">
                                                    <tr>
                                                        <td>${f:h(artifact.type)}</td>
                                                        <td>${f:h(artifact.name)}</td>
                                                        <td>${f:h(artifact.version)}</td>
                                                        <td>
                                                            <la:form action="/admin/plugin/" styleClass="form-horizontal">
                                                                <input type="hidden" name="name" value="${f:h(artifact.name)}">
                                                                <input type="hidden" name="version" value="${f:h(artifact.version)}">
                                                                <input type="hidden" name="url" value="${f:h(artifact.url)}">
                                                                <button type="submit" class="btn btn-warning"
                                                                        name="install"
                                                                        value="<la:message key="labels.crud_button_install" />">
                                                                    <em class="fa fa-plus"></em>
                                                                    <la:message key="labels.crud_button_install" />
                                                                </button>
                                                            </la:form>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                                <!-- /.data-wrapper -->
                            </div>
                            <!-- /.box-body -->
                            <!-- /.box-footer -->
                        </div>
                        <!-- /.box -->
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
