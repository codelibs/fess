<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.backup_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="log"/>
        <jsp:param name="menuType" value="backup"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.backup_configuration"/>
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
                                <la:message key="labels.backup_configuration"/>
                            </h3>
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
                                        <c:if test="${editable}">
                                            <la:form action="/admin/backup/upload/" enctype="multipart/form-data"
                                                     styleClass="form-inline">
                                                <label for="bulkFile" class="mb-2 mr-2"><la:message
                                                        key="labels.backup_bulk_file"/></label>
                                                <div class="mb-2 mr-sm-2"><input type="file" id="bulkFile"
                                                                                 name="bulkFile"
                                                                                 class="form-control-file"/></div>
                                                <button type="submit" class="btn btn-success mb-2" name="upload">
                                                    <em class="fa fa-upload"></em>
                                                    <la:message key="labels.backup_button_upload"/>
                                                </button>
                                            </la:form>
                                        </c:if>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-12">
                                        <table class="table table-bordered table-striped">
                                            <thead>
                                            <tr>
                                                <th><la:message key="labels.backup_name"/></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="data" varStatus="s"
                                                       items="${backupItems}">
                                                <tr
                                                        data-href="${contextPath}/admin/backup/download/${f:u(data.id)}/">
                                                    <td>${f:h(data.name)}</td>
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

