<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message key="labels.theme_title"/></title>
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
                        <h1><la:message key="labels.theme_title"/></h1>
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
                                    <la:link href="uploadpage"
                                             styleClass="btn btn-success btn-xs ${f:h(editableClass)}">
                                        <i class="fa fa-plus" aria-hidden="true"></i>
                                        <la:message key="labels.theme_upload"/>
                                    </la:link>
                                </div>
                            </div>
                        </div>
                        <div class="card-body">
                            <%-- Message --%>
                            <div>
                                <la:info id="msg" message="true">
                                    <div class="alert alert-success">${msg}</div>
                                </la:info>
                                <la:errors/>
                            </div>
                            <%-- Default theme selector --%>
                            <c:if test="${editable}">
                                <div class="row mb-3">
                                    <div class="col-md-8">
                                        <la:form action="/admin/theme/setdefault" styleId="setDefaultForm">
                                            <div class="form-group row">
                                                <label for="defaultTheme" class="col-md-3 text-sm-right col-form-label">
                                                    <la:message key="labels.theme_set_default"/>
                                                </label>
                                                <div class="col-md-6">
                                                    <la:select property="defaultTheme" styleId="defaultTheme" styleClass="form-control">
                                                        <la:option value=""><la:message key="labels.theme_default_none"/></la:option>
                                                        <c:forEach var="t" items="${themeItems}">
                                                            <la:option value="${f:h(t.name)}">${f:h(t.displayName)} (${f:h(t.name)})</la:option>
                                                        </c:forEach>
                                                    </la:select>
                                                </div>
                                                <div class="col-md-3">
                                                    <button type="submit" class="btn btn-primary" name="setdefault"
                                                            value="<la:message key="labels.theme_set_default"/>">
                                                        <i class="fa fa-check" aria-hidden="true"></i>
                                                        <la:message key="labels.theme_set_default"/>
                                                    </button>
                                                </div>
                                            </div>
                                        </la:form>
                                    </div>
                                    <div class="col-md-4 text-right">
                                        <la:form action="/admin/theme/reload">
                                            <button type="submit" class="btn btn-default" name="reload"
                                                    value="<la:message key="labels.theme_reload"/>">
                                                <i class="fa fa-sync-alt" aria-hidden="true"></i>
                                                <la:message key="labels.theme_reload"/>
                                            </button>
                                        </la:form>
                                    </div>
                                </div>
                            </c:if>
                            <%-- List --%>
                            <div class="data-wrapper">
                                <div class="row">
                                    <div class="col-sm-12">
                                        <table class="table table-bordered table-striped" aria-label="<la:message key="labels.theme_list_name"/>">
                                            <thead>
                                            <tr>
                                                <th><la:message key="labels.theme_thumbnail"/></th>
                                                <th><la:message key="labels.theme_type"/></th>
                                                <th><la:message key="labels.theme_name"/></th>
                                                <th><la:message key="labels.theme_display_name"/></th>
                                                <th><la:message key="labels.theme_version"/></th>
                                                <th><la:message key="labels.theme_is_default"/></th>
                                                <th></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="t" varStatus="s" items="${themeItems}">
                                                <tr>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${t.type=='Static'}">
                                                                <img src="${fe:url('/themes/')}${f:h(t.name)}/thumbnail.png"
                                                                     style="max-height:32px" onerror="this.style.display='none'"
                                                                     alt="${f:h(t.displayName)}"/>
                                                            </c:when>
                                                            <c:otherwise><i class="fa fa-palette" aria-hidden="true"></i></c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>${f:h(t.type)}</td>
                                                    <td><la:link href="details?name=${f:u(t.name)}">${f:h(t.name)}</la:link></td>
                                                    <td>${f:h(t.displayName)}</td>
                                                    <td>${f:h(t.version)}</td>
                                                    <td>
                                                        <c:if test="${t.isDefault}">
                                                            <i class="fa fa-check" aria-hidden="true"></i>
                                                        </c:if>
                                                    </td>
                                                    <td>
                                                        <c:if test="${editable && t.type=='Static' && !t.isDefault}">
                                                            <div class="text-center">
                                                                <button type="button" class="btn btn-danger btn-xs"
                                                                        name="delete" data-toggle="modal"
                                                                        data-target='#confirmToDelete-${f:h(t.name)}'
                                                                        value="<la:message key="labels.crud_button_delete"/>">
                                                                    <i class="fa fa-trash" aria-hidden="true"></i>
                                                                    <la:message key="labels.crud_button_delete"/>
                                                                </button>
                                                            </div>
                                                            <div class="modal fade"
                                                                 id='confirmToDelete-${f:h(t.name)}'
                                                                 tabindex="-1" role="dialog">
                                                                <div class="modal-dialog">
                                                                    <div class="modal-content bg-danger">
                                                                        <div class="modal-header">
                                                                            <h4 class="modal-title">
                                                                                <la:message key="labels.crud_title_delete"/>
                                                                            </h4>
                                                                            <button type="button" class="close"
                                                                                    data-dismiss="modal"
                                                                                    aria-label="Close">
                                                                                <span aria-hidden="true">&times;</span>
                                                                            </button>
                                                                        </div>
                                                                        <div class="modal-body">
                                                                            <p>
                                                                                <la:message key="labels.crud_delete_confirmation"/>
                                                                            </p>
                                                                        </div>
                                                                        <div class="modal-footer justify-content-between">
                                                                            <button type="button"
                                                                                    class="btn btn-outline-light"
                                                                                    data-dismiss="modal">
                                                                                <la:message key="labels.crud_button_cancel"/>
                                                                            </button>
                                                                            <la:form action="/admin/theme/delete">
                                                                                <input type="hidden" name="name"
                                                                                       value="${f:h(t.name)}">
                                                                                <button type="submit"
                                                                                        class="btn btn-outline-light"
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
