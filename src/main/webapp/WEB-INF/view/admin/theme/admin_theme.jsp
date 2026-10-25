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
    <main id="mainContent" class="content-wrapper">
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
                                    <div class="alert alert-success" role="status">${msg}</div>
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
                                                    <la:message key="labels.theme_default"/>
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
                                                        <img src="${fe:url('/themes/')}${f:h(t.name)}/thumbnail.png"
                                                             style="max-height:32px" onerror="this.style.display='none'"
                                                             alt="${f:h(t.displayName)}"/>
                                                    </td>
                                                    <td><la:link href="details?name=${f:u(t.name)}">${f:h(t.name)}</la:link></td>
                                                    <td>${f:h(t.displayName)}</td>
                                                    <td>${f:h(t.version)}</td>
                                                    <td>
                                                        <c:if test="${t.isDefault}">
                                                            <i class="fa fa-check" aria-hidden="true"></i>
                                                        </c:if>
                                                    </td>
                                                    <td>
                                                        <%-- The bundled theme (name "bootstrap") is the last-resort
                                                             fallback ThemeRegistry falls back to; it stays listed
                                                             but is never deletable. --%>
                                                        <c:if test="${editable && !t.isDefault && t.name != 'bootstrap'}">
                                                            <div class="text-center">
                                                                <button type="button" class="btn btn-danger btn-xs"
                                                                        name="delete" data-toggle="modal"
                                                                        data-target='#confirmToDelete-${s.index}'
                                                                        value="<la:message key="labels.crud_button_delete"/>">
                                                                    <i class="fa fa-trash" aria-hidden="true"></i>
                                                                    <la:message key="labels.crud_button_delete"/>
                                                                </button>
                                                            </div>
                                                            <div class="modal fade"
                                                                 id='confirmToDelete-${s.index}'
                                                                 tabindex="-1" role="dialog">
                                                                <div class="modal-dialog">
                                                                    <div class="modal-content bg-danger">
                                                                        <div class="modal-header">
                                                                            <h4 class="modal-title">
                                                                                <la:message key="labels.crud_title_delete"/>
                                                                            </h4>
                                                                            <button type="button" class="close"
                                                                                    data-dismiss="modal"
                                                                                    aria-label="<la:message key="labels.crud_button_close"/>">
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

                    <div class="card card-outline card-primary">
                        <div class="card-header">
                            <h3 class="card-title">
                                <la:message key="labels.theme_install_title"/>
                            </h3>
                        </div>
                        <div class="card-body">
                            <c:if test="${empty availableThemeNames}">
                                <%-- Not labels.list_could_not_find_crud_table ("not found"):
                                     an empty catalogue means the repository's index could
                                     not be read, not that no themes are published, and the
                                     operator can act on the difference. A theme can still be
                                     installed from the upload page. --%>
                                <p><la:message key="labels.theme_available_empty"/></p>
                            </c:if>
                            <c:if test="${not empty availableThemeNames}">
                                <%-- Step 1: pick a theme. A page transition, not script: the form
                                     reloads this page with ?installName=, and the server renders
                                     step 2 for that theme. A plain form rather than la:form,
                                     which would put its double-submit token in the query string. --%>
                                <form method="get" action="${fe:url('/admin/theme/')}">
                                    <div class="form-group row">
                                        <label for="installName" class="col-sm-3 col-form-label">
                                            <la:message key="labels.theme_name"/>
                                        </label>
                                        <div class="col-sm-6">
                                            <select id="installName" name="installName" class="form-control">
                                                <option value=""><la:message key="labels.theme_install_select_name"/></option>
                                                <c:forEach var="n" items="${availableThemeNames}">
                                                    <option value="${f:h(n)}" ${n == pickedThemeName ? 'selected' : ''}>${f:h(n)}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="col-sm-3">
                                            <button type="submit" class="btn btn-default">
                                                <la:message key="labels.theme_install_select"/>
                                            </button>
                                        </div>
                                    </div>
                                </form>
                                <%-- Step 2: pick a version of the theme picked above. pickedThemeName
                                     is registered only when the catalogue lists it, so an unknown
                                     name stays at step 1. The picked name is shown as text, not
                                     as a control: the step 1 select above stays editable, and the
                                     name posted here must be the one the operator can read.
                                     Plain inputs, not la:text/la:select: this page renders with
                                     ThemeListForm, which has no name/version property, so a
                                     bound tag would fail the whole page. The install action
                                     binds them to ThemeInstallForm. --%>
                                <c:if test="${not empty pickedThemeName}">
                                    <la:form action="/admin/theme/install">
                                        <input type="hidden" name="name" value="${f:h(pickedThemeName)}"/>
                                        <div class="form-group row">
                                            <div class="col-sm-3 col-form-label">
                                                <la:message key="labels.theme_name"/>
                                            </div>
                                            <div class="col-sm-9">
                                                <div class="form-control-plaintext">${f:h(pickedThemeName)}</div>
                                            </div>
                                        </div>
                                        <div class="form-group row">
                                            <label for="installVersion" class="col-sm-3 col-form-label">
                                                <la:message key="labels.theme_version"/>
                                            </label>
                                            <div class="col-sm-6">
                                                <select id="installVersion" name="version" class="form-control">
                                                    <c:forEach var="v" items="${availableVersions}">
                                                        <option value="${f:h(v)}" ${v == defaultVersion ? 'selected' : ''}>${f:h(v)}</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                            <div class="col-sm-3">
                                                <button type="submit" class="btn btn-primary ${f:h(editableClass)}">
                                                    <i class="fa fa-download" aria-hidden="true"></i>
                                                    <la:message key="labels.theme_install"/>
                                                </button>
                                            </div>
                                        </div>
                                    </la:form>
                                </c:if>
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
