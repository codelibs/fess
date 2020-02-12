<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.design_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="design"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.design_configuration"/>
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
                <div class="col-md-6">
                    <div class="card card-outline card-primary">
                        <la:form action="/admin/design/">
                            <div class="card-header">
                                <h3 class="card-title">
                                    <la:message key="labels.design_title_file"/>
                                </h3>
                            </div>
                            <div class="card-body">
                                <div class="form-group row">
                                    <la:errors property="fileName"/>
                                    <la:select styleId="fileName" property="fileName" styleClass="form-control">
                                        <c:forEach var="item" varStatus="s" items="${fileNameItems}">
                                            <la:option value="${item}">${f:h(item)}</la:option>
                                        </c:forEach>
                                    </la:select>
                                </div>
                            </div>
                            <div class="card-footer">
                                <button type="submit" class="btn btn-primary" name="download"
                                        value="<la:message key="labels.design_download_button" />">
                                    <em class="fa fa-download"></em>
                                    <la:message key="labels.design_download_button"/>
                                </button>
                                <c:if test="${editable}">
                                    <button type="button" class="btn btn-danger" name="delete"
                                            data-toggle="modal" data-target="#confirmToDelete"
                                            value="<la:message key="labels.design_delete_button" />">
                                        <em class="fa fa-trash"></em>
                                        <la:message key="labels.design_delete_button"/>
                                    </button>
                                    <div class="modal fade" id="confirmToDelete" tabindex="-1"
                                         role="dialog">
                                        <div class="modal-dialog">
                                            <div class="modal-content bg-danger">
                                                <div class="modal-header">
                                                    <h4 class="modal-title">
                                                        <la:message key="labels.crud_title_delete"/>
                                                    </h4>
                                                    <button type="button" class="close" data-dismiss="modal"
                                                            aria-label="Close">
                                                        <span aria-hidden="true">×</span>
                                                    </button>
                                                </div>
                                                <div class="modal-body">
                                                    <p>
                                                        <la:message key="labels.crud_delete_confirmation"/>
                                                    </p>
                                                </div>
                                                <div class="modal-footer justify-content-between">
                                                    <button type="button" class="btn btn-outline-light"
                                                            data-dismiss="modal">
                                                        <la:message key="labels.crud_button_cancel"/>
                                                    </button>
                                                    <button type="submit" class="btn btn-outline-light"
                                                            name="delete"
                                                            value="<la:message key="labels.crud_button_delete" />">
                                                        <em class="fa fa-trash"></em>
                                                        <la:message key="labels.crud_button_delete"/>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </la:form>
                    </div>
                </div>
                <div class="col-md-6">
                    <c:if test="${editable}">
                        <div class="card card-outline card-primary">
                            <la:form action="/admin/design/">
                                <div class="card-header">
                                    <h3 class="card-title">
                                        <la:message key="labels.design_file_title_edit"/>
                                    </h3>
                                </div>
                                <div class="card-body">
                                    <div class="form-group row">
                                        <la:errors property="fileName"/>
                                        <la:select styleId="fileName" property="fileName" styleClass="form-control">
                                            <c:forEach var="item" items="${jspFileNameItems}">
                                                <la:option value="${f:u(item.first)}">${f:h(item.second)}</la:option>
                                            </c:forEach>
                                        </la:select>
                                    </div>
                                </div>
                                <div class="card-footer">
                                    <button type="submit" class="btn btn-primary" name="edit"
                                            value="<la:message key="labels.design_edit_button" />">
                                        <em class="fa fa-pencil-alt"></em>
                                        <la:message key="labels.design_edit_button"/>
                                    </button>
                                    <button type="submit" class="btn btn-warning"
                                            name="editAsUseDefault"
                                            value="<la:message key="labels.design_use_default_button" />">
                                        <em class="fa fa-recycle"></em>
                                        <la:message key="labels.design_use_default_button"/>
                                    </button>
                                </div>
                            </la:form>
                        </div>
                    </c:if>
                </div>
                <div class="col-md-12">
                    <c:if test="${editable}">
                        <div class="card card-outline card-success">
                            <la:form action="/admin/design/upload/"
                                     enctype="multipart/form-data">
                                <div class="card-header">
                                    <h3 class="card-title">
                                        <la:message key="labels.design_title_file_upload"/>
                                    </h3>
                                </div>
                                <div class="card-body">
                                    <div class="form-group row">
                                        <label class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.design_file"/></label>
                                        <div class="form-inline col-sm-9">
                                            <la:errors property="designFile"/>
                                            <input type="file" name="designFile" class="form-control-file"/>
                                        </div>
                                    </div>
                                    <div class="form-group row">
                                        <label class="col-sm-3 text-sm-right col-form-label"><la:message
                                                key="labels.design_file_name"/></label>
                                        <div class="col-sm-9">
                                            <la:errors property="designFileName"/>
                                            <la:text styleId="designFileName" property="designFileName"
                                                     styleClass="form-control"/>
                                        </div>
                                    </div>
                                </div>
                                <div class="card-footer">
                                    <button type="submit" class="btn btn-success" name="upload"
                                            value="<la:message key="labels.design_button_upload" />">
                                        <em class="fa fa-upload"></em>
                                        <la:message key="labels.design_button_upload"/>
                                    </button>
                                </div>
                            </la:form>
                        </div>
                    </c:if>
                </div>
            </div>
        </section>
    </div>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>
