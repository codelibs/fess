<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.storage_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="storage"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.storage_configuration"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
                        <jsp:include page="/WEB-INF/view/common/admin/crud/breadcrumb.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="fads-row">
                <div class="fads-col-md-12">
                    <div class="fads-card">
                        <div class="fads-card-header">
                            <h3 class="fads-card-title">
                                <a aria-hidden="true" href="${contextPath}/admin/storage/">
                                    <i class="fas fa-database fa-fw"
                                       aria-hidden="true"></i>${f:h(bucket)}
                                </a>
                                <c:forEach var="item" varStatus="s" items="${pathItems}">
                                    / <span><a
                                        href="${contextPath}/admin/storage/list/${f:u(item.id)}/">${f:h(item.name)}</a></span>
                                </c:forEach>
                                / <c:if test="${editable}"><a data-fads-dialog="createDir" href="#"><i
                                    class="fas fa-folder fa-fw" aria-hidden="true"></i></a></c:if>
                            </h3>
                            <c:if test="${editable}">
								<div class="fads-card-tools">
                                    <a class="fads-btn fads-btn-success fads-btn-compact" data-fads-dialog="uploadeFile" href="#">
                                        <i class="fa fa-upload" aria-hidden="true"></i>
                                        <la:message key="labels.storage_button_upload"/>
                                    </a>
                                </div>
                            </c:if>
                        </div>
                        <div class="fads-card-body">
                            <%-- Message --%>
                            <div>
                                <la:info id="msg" message="true">
                                    <div class="fads-banner fads-banner-info">${msg}</div>
                                </la:info>
                                <la:errors/>
                            </div>
                            <%-- List --%>
                            <div class="data-wrapper">

                                <c:if test="${editable}">
                                    <div class="modal" id="createDir" tabindex="-1" role="dialog">
                                        <div class="fads-dialog">
                                            <la:form action="/admin/storage/createDir/"
                                                     enctype="multipart/form-data" styleClass="modal-content">
                                                <input type="hidden" name="path" value="${f:h(path)}"/>
                                                <div class="fads-dialog-header">
                                                    <h4 class="">
                                                        <la:message key="labels.crud_title_create"/>
                                                    </h4>
                                                    <button type="button" class="close" data-fads-dialog-close
                                                            aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                                <div class="fads-dialog-body">
                                                    <div class="fads-form-field">
                                                        <label for="name" class="fads-label"><la:message
                                                                key="labels.storage_folder_name"/></label>
                                                        <div class="form-inline col-sm-9">
                                                           <input id="name" type="text" name="name" class="fads-textfield"/>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="fads-dialog-footer">
                                                    <button type="button" class="fads-btn fads-btn-subtle"
                                                            data-fads-dialog-close>
                                                        <la:message key="labels.crud_button_cancel"/>
                                                    </button>
                                                    <button type="submit" class="fads-btn fads-btn-success" name="createDir">
                                                        <i class="fa fa-make" aria-hidden="true"></i>
                                                        <la:message key="labels.crud_button_create"/>
                                                    </button>
                                                </div>
                                            </la:form>
                                        </div>
                                    </div>

                                    <div class="modal" id="uploadeFile" tabindex="-1" role="dialog">
                                        <div class="fads-dialog">
                                            <la:form action="/admin/storage/upload/" enctype="multipart/form-data"
                                                     styleClass="modal-content">
                                                <input type="hidden" name="path" value="${f:h(path)}"/>
                                                <div class="fads-dialog-header">
                                                    <h4 class="">
                                                        <la:message key="labels.storage_upload_file"/>
                                                    </h4>
                                                    <button type="button" class="close" data-fads-dialog-close
                                                            aria-label="Close">
                                                        <span aria-hidden="true">&times;</span>
                                                    </button>
                                                </div>
                                                <div class="fads-dialog-body">
                                                    <div class="fads-form-field">
                                                        <label for="uploadFile" class="fads-label"><la:message
                                                                key="labels.storage_file"/></label>
                                                        <div class="form-inline col-sm-9">
                                                            <input type="file" name="uploadFile" id="uploadFile"  class="fads-textfield"/>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="fads-dialog-footer">
                                                    <button type="button" class="fads-btn fads-btn-subtle"
                                                            data-fads-dialog-close>
                                                        <la:message key="labels.crud_button_cancel"/>
                                                    </button>
                                                    <button type="submit" class="fads-btn fads-btn-success" name="upload">
                                                        <i class="fa fa-upload" aria-hidden="true"></i>
                                                        <la:message key="labels.storage_button_upload"/>
                                                    </button>
                                                </div>
                                            </la:form>
                                        </div>
                                    </div>
                                </c:if>

                                <div class="fads-row">
                                    <div class="fads-col-sm-12">
                                        <table class="fads-table" aria-label="<la:message key="labels.storage_list" />">
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
                                                <tr data-href="${contextPath}/admin/storage/list/${f:u(data.parentId)}/" role="button" tabindex="0">
                                                    <td>..</td>
                                                    <td></td>
                                                    <td></td>
                                                    <td></td>
                                                </tr>
                                            </c:if>
                                            <c:if test="${not empty path and empty parentId}">
                                                <tr data-href="${contextPath}/admin/storage/" role="button" tabindex="0">
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
                                                        <i class="far fa-file" aria-hidden="true"></i>
                                                            ${f:h(data.name)}
                                                    </td>
                                                    <td>${f:h(data.size)}</td>
                                                    <td>${fe:formatDate(data.lastModified, 'yyyy-MM-dd HH:mm:ss')}</td>
                                                </c:if>
                                                <c:if test="${data.directory.booleanValue()}">
                                                <tr data-href="${contextPath}/admin/storage/list/${f:h(data.id)}/" role="button" tabindex="0">
                                                    <td>
                                                        <i class="fa fa-folder-open" aria-hidden="true" style="color:#F7C502;"></em>
                                                            ${f:h(data.name)}
                                                    </td>
                                                    <td></td>
                                                    <td></td>
                                                </c:if>
                                                <td>
                                                    <c:if test="${not data.directory}">
                                                        <a class="fads-btn fads-btn-primary fads-btn-compact" role="button" name="download"
                                                           href="${contextPath}/admin/storage/download/${f:h(data.id)}/"
                                                           value="<la:message key="labels.storage_button_download" />"
                                                        >
                                                            <i class="fa fa-download" aria-hidden="true"></i>
                                                            <la:message key="labels.storage_button_download"/>
                                                        </a>
                                                        <c:if test="${editable}">
	                                                        <a class="fads-btn fads-btn-primary fads-btn-compact" role="button" name="editTags"
	                                                           href="${contextPath}/admin/storage/editTags?path=${f:u(data.path)}&name=${f:u(data.name)}"
	                                                           value="<la:message key="labels.storage_button_tags" />"
	                                                        >
	                                                            <i class="fa fa-tags" aria-hidden="true"></i>
	                                                            <la:message key="labels.storage_button_tags"/>
	                                                        </a>
                                                            <button type="button" class="fads-btn fads-btn-danger fads-btn-compact"
                                                                    name="delete" 
                                                                    data-fads-dialog="confirmToDelete-${f:h(data.hashCode)}"
                                                                    value="<la:message key="labels.crud_button_delete" />"
                                                            >
                                                                <i class="fa fa-times" aria-hidden="true"></i>
                                                                <la:message key="labels.crud_button_delete"/>
                                                            </button>
                                                            <div class="fads-dialog-overlay"
                                                                 id="confirmToDelete-${f:h(data.hashCode)}"
                                                                 tabindex="-1" role="dialog"
                                                            >
                                                                <div class="fads-dialog">
                                                                    <div class="fads-dialog-danger">
                                                                        <div class="fads-dialog-header">
                                                                            <h4 class="">
                                                                                <la:message
                                                                                        key="labels.crud_title_delete"/>
                                                                                : ${f:h(data.name)}
                                                                            </h4>
                                                                            <button type="button" class="close"
                                                                                    data-fads-dialog-close
                                                                                    aria-label="Close">
                                                                                <span aria-hidden="true">×</span>
                                                                            </button>
                                                                        </div>
                                                                        <div class="fads-dialog-body">
                                                                            <p>
                                                                                <la:message
                                                                                        key="labels.crud_delete_confirmation"/>
                                                                            </p>
                                                                        </div>
                                                                        <div class="fads-dialog-footer">
                                                                            <button type="button"
                                                                                    class="fads-btn fads-btn-outline-light"
                                                                                    data-fads-dialog-close>
                                                                                <la:message
                                                                                        key="labels.crud_button_cancel"/>
                                                                            </button>
                                                                            <la:form
                                                                                    action="${contextPath}/admin/storage/delete/${f:h(data.id)}/">
                                                                                <button type="submit"
                                                                                        class="fads-btn fads-btn-outline-light"
                                                                                        name="delete"
                                                                                        value="<la:message key="labels.crud_button_delete" />"
                                                                                >
                                                                                    <i class="fa fa-trash" aria-hidden="true"></i>
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
    </main>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}
