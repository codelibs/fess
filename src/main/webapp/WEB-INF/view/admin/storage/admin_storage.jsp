<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.storage_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="storage"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.storage_configuration"/>
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
                                <a aria-hidden="true" href="${contextPath}/admin/storage/">
                                    <i class="fas fa-database fa-fw"
                                       aria-hidden="true"></i>${f:h(bucket)}
                                </a>
                                <c:forEach var="item" varStatus="s" items="${pathItems}">
                                    / <span><a
                                        href="${contextPath}/admin/storage/list/${f:u(item.id)}/">${f:h(item.name)}</a></span>
                                </c:forEach>
                                / <c:if test="${editable}"><a data-toggle="modal" data-target="#createDir" href="#"><i
                                    class="fas fa-folder fa-fw" aria-hidden="true"></i></a></c:if>
                            </h3>
                            <c:if test="${editable}">
								<div class="card-tools">
                                    <a class="btn btn-success btn-xs" data-toggle="modal" data-target="#uploadeFile" href="#">
                                        <em class="fa fa-upload" aria-hidden="true"></em>
                                        <la:message key="labels.storage_button_upload"/>
                                    </a>
                                </div>
                            </c:if>
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

                                <c:if test="${editable}">
                                    <div class="modal" id="createDir" tabindex="-1" role="dialog">
                                        <div class="modal-dialog">
                                            <la:form action="/admin/storage/createDir/"
                                                     enctype="multipart/form-data" styleClass="modal-content">
                                                <input type="hidden" name="path" value="${f:h(path)}"/>
                                                <div class="modal-header">
                                                    <h4 class="modal-title">
                                                        <la:message key="labels.crud_title_create"/>
                                                    </h4>
                                                    <button type="button" class="close" data-dismiss="modal"
                                                            aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="form-group row">
                                                        <label for="name" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                                key="labels.storage_folder_name"/></label>
                                                        <div class="form-inline col-sm-9">
                                                           <input id="name" type="text" name="name" class="form-control"/>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="modal-footer justify-content-between">
                                                    <button type="button" class="btn btn-outline-secondary"
                                                            data-dismiss="modal">
                                                        <la:message key="labels.crud_button_cancel"/>
                                                    </button>
                                                    <button type="submit" class="btn btn-success" name="createDir">
                                                        <em class="fa fa-make"></em>
                                                        <la:message key="labels.crud_button_create"/>
                                                    </button>
                                                </div>
                                            </la:form>
                                        </div>
                                    </div>

                                    <div class="modal" id="uploadeFile" tabindex="-1" role="dialog">
                                        <div class="modal-dialog">
                                            <la:form action="/admin/storage/upload/" enctype="multipart/form-data"
                                                     styleClass="modal-content">
                                                <input type="hidden" name="path" value="${f:h(path)}"/>
                                                <div class="modal-header">
                                                    <h4 class="modal-title">
                                                        <la:message key="labels.storage_upload_file"/>
                                                    </h4>
                                                    <button type="button" class="close" data-dismiss="modal"
                                                            aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="form-group row">
                                                        <label for="uploadFile" class="col-sm-3 text-sm-right col-form-label"><la:message
                                                                key="labels.storage_file"/></label>
                                                        <div class="form-inline col-sm-9">
                                                            <input type="file" name="uploadFile" id="uploadFile"  class="form-control-file"/>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="modal-footer justify-content-between">
                                                    <button type="button" class="btn btn-outline-secondary"
                                                            data-dismiss="modal">
                                                        <la:message key="labels.crud_button_cancel"/>
                                                    </button>
                                                    <button type="submit" class="btn btn-success" name="upload">
                                                        <em class="fa fa-upload"></em>
                                                        <la:message key="labels.storage_button_upload"/>
                                                    </button>
                                                </div>
                                            </la:form>
                                        </div>
                                    </div>
                                </c:if>

                                <div class="row">
                                    <div class="col-sm-12">
                                        <table class="table table-bordered table-striped">
                                            <thead>
                                            <tr>
                                                <th><la:message key="labels.storage_name"/></th>
                                                <th style="width: 10%"><la:message key="labels.storage_size"/></th>
                                                <th style="width: 15%"><la:message
                                                        key="labels.storage_last_modified"/></th>
                                                <th style="width: 20%"></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:if test="${not empty path and not empty parentId}">
                                                <tr data-href="${contextPath}/admin/storage/list/${f:u(data.parentId)}/">
                                                    <td>..</td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                </tr>
                                            </c:if>
                                            <c:if test="${not empty path and empty parentId}">
                                                <tr data-href="${contextPath}/admin/storage/">
                                                    <td>..</td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                </tr>
                                            </c:if>
                                            <c:forEach var="data" varStatus="s" items="${fileItems}">
                                                <c:if test="${not data.directory}">
                                                <tr>
                                                    <td>
                                                        <em class="far fa-file"></em>
                                                            ${f:h(data.name)}
                                                    </td>
                                                    <td>${f:h(data.size)}</td>
                                                    <td>${fe:formatDate(data.lastModified, 'yyyy-MM-dd HH:mm:ss')}</td>
                                                </c:if>
                                                <c:if test="${data.directory.booleanValue()}">
                                                <tr data-href="${contextPath}/admin/storage/list/${f:h(data.id)}/">
                                                    <td>
                                                        <em class="fa fa-folder-open" style="color:#F7C502;"></em>
                                                            ${f:h(data.name)}
                                                    </td>
                                                    <td></td>
                                                    <td></td>
                                                </c:if>
                                                <td>
                                                    <c:if test="${not data.directory}">
                                                        <a class="btn btn-primary btn-xs" role="button" name="download"
                                                           href="${contextPath}/admin/storage/download/${f:h(data.id)}/"
                                                           value="<la:message key="labels.storage_button_download" />"
                                                        >
                                                            <em class="fa fa-download"></em>
                                                            <la:message key="labels.storage_button_download"/>
                                                        </a>
                                                        <c:if test="${editable}">
	                                                        <a class="btn btn-primary btn-xs" role="button" name="editTags"
	                                                           href="${contextPath}/admin/storage/editTags?path=${f:u(data.path)}&name=${f:u(data.name)}"
	                                                           value="<la:message key="labels.storage_button_tags" />"
	                                                        >
	                                                            <em class="fa fa-tags"></em>
	                                                            <la:message key="labels.storage_button_tags"/>
	                                                        </a>
                                                            <button type="button" class="btn btn-danger btn-xs"
                                                                    name="delete" data-toggle="modal"
                                                                    data-target="#confirmToDelete-${f:h(data.hashCode)}"
                                                                    value="<la:message key="labels.crud_button_delete" />"
                                                            >
                                                                <em class="fa fa-times"></em>
                                                                <la:message key="labels.crud_button_delete"/>
                                                            </button>
                                                            <div class="modal fade"
                                                                 id="confirmToDelete-${f:h(data.hashCode)}"
                                                                 tabindex="-1" role="dialog"
                                                            >
                                                                <div class="modal-dialog">
                                                                    <div class="modal-content bg-danger">
                                                                        <div class="modal-header">
                                                                            <h4 class="modal-title">
                                                                                <la:message
                                                                                        key="labels.crud_title_delete"/>
                                                                                : ${f:h(data.name)}
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
                                                                            <la:form
                                                                                    action="${contextPath}/admin/storage/delete/${f:h(data.id)}/">
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
