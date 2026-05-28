<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message key="labels.theme_details_title"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="theme"/>
    </jsp:include>
    <main class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1><la:message key="labels.theme_details_title"/></h1>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="row">
                <div class="col-md-12">
                    <la:info id="msg" message="true">
                        <div class="alert alert-success">${msg}</div>
                    </la:info>
                    <la:errors property="_global"/>
                </div>
                <div class="col-md-12">
                    <div class="card card-outline card-primary">
                        <div class="card-header">
                            <h3 class="card-title">
                                <la:message key="labels.theme_manifest"/>
                            </h3>
                        </div>
                        <div class="card-body">
                            <table class="table table-bordered" aria-label="<la:message key="labels.theme_manifest"/>">
                                <tbody>
                                <tr>
                                    <th><la:message key="labels.theme_name"/></th>
                                    <td>${f:h(theme.name)}</td>
                                </tr>
                                <tr>
                                    <th><la:message key="labels.theme_display_name"/></th>
                                    <td>${f:h(theme.displayName)}</td>
                                </tr>
                                <tr>
                                    <th><la:message key="labels.theme_version"/></th>
                                    <td>${f:h(theme.version)}</td>
                                </tr>
                                <tr>
                                    <th><la:message key="labels.theme_is_default"/></th>
                                    <td>
                                        <c:choose>
                                            <c:when test="${theme.isDefault}">
                                                <i class="fa fa-check" aria-hidden="true"></i>
                                            </c:when>
                                            <c:otherwise>&nbsp;</c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <th><la:message key="labels.theme_health"/></th>
                                    <td>${f:h(theme.health)}</td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        <div class="card-footer">
                            <la:form action="/admin/theme/">
                                <button type="submit" class="btn btn-default" name="back"
                                        value="<la:message key="labels.crud_button_back"/>">
                                    <i class="fa fa-arrow-circle-left" aria-hidden="true"></i>
                                    <la:message key="labels.crud_button_back"/>
                                </button>
                            </la:form>
                            <c:if test="${editable && theme.type=='Static' && !theme.isDefault}">
                                <button type="button" class="btn btn-danger"
                                        name="delete" data-toggle="modal"
                                        data-target="#confirmToDelete"
                                        value="<la:message key="labels.crud_button_delete"/>">
                                    <i class="fa fa-trash" aria-hidden="true"></i>
                                    <la:message key="labels.crud_button_delete"/>
                                </button>
                                <div class="modal fade" id="confirmToDelete" tabindex="-1" role="dialog">
                                    <div class="modal-dialog">
                                        <div class="modal-content bg-danger">
                                            <div class="modal-header">
                                                <h4 class="modal-title">
                                                    <la:message key="labels.crud_title_delete"/>
                                                </h4>
                                                <button type="button" class="close"
                                                        data-dismiss="modal" aria-label="Close">
                                                    <span aria-hidden="true">&times;</span>
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
                                                <la:form action="/admin/theme/delete">
                                                    <input type="hidden" name="name" value="${f:h(theme.name)}">
                                                    <button type="submit" class="btn btn-outline-light"
                                                            name="delete"
                                                            value="<la:message key="labels.crud_button_delete"/>">
                                                        <i class="fa fa-trash" aria-hidden="true"></i>
                                                        <la:message key="labels.crud_button_delete"/>
                                                    </button>
                                                </la:form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
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
