<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.suggest_word_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="suggest"/>
        <jsp:param name="menuType" value="suggestWord"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.suggest_word_title_details"/>
                        </h1>
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
                            <div class="row">
                                <div class="col-sm-12">
                                    <la:form action="/admin/suggest/">
                                        <table class="table table-bordered table-striped">
                                            <thead>
                                            <tr>
                                                <th style="width: 15%"><la:message key="labels.suggest_word_type"/></th>
                                                <th class="text-center" style="width: 10%"><la:message
                                                        key="labels.suggest_word_number"/></th>
                                                <th class="text-center" style="width: 20%"></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <tr>
                                                <td><la:message key="labels.suggest_word_type_all"/></td>
                                                <td class="text-right">${f:h(totalWordsNum)}</td>
                                                <td class="text-center">
                                                    <c:if test="${editable}">
                                                        <button type="button" class="btn btn-danger btn-xs"
                                                                name="deleteAllWords"
                                                                data-toggle="modal" data-target="#confirmToAllDelete"
                                                                value="<la:message key="labels.design_delete_button" />">
                                                            <em class="fa fa-trash"></em>
                                                            <la:message key="labels.design_delete_button"/>
                                                        </button>
                                                        <div class="modal fade" id="confirmToAllDelete"
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
                                                                        <button type="submit"
                                                                                class="btn btn-outline-light"
                                                                                name="deleteAllWords"
                                                                                value="<la:message key="labels.crud_button_delete" />">
                                                                            <em class="fa fa-trash"></em>
                                                                            <la:message
                                                                                    key="labels.crud_button_delete"/>
                                                                        </button>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:if>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td><la:message key="labels.suggest_word_type_document"/></td>
                                                <td class="text-right">${f:h(documentWordsNum)}</td>
                                                <td class="text-center">
                                                    <c:if test="${editable}">
                                                        <button type="button" class="btn btn-danger btn-xs"
                                                                name="deleteDocumentWords"
                                                                data-toggle="modal"
                                                                data-target="#confirmToDocumentDelete"
                                                                value="<la:message key="labels.design_delete_button" />">
                                                            <em class="fa fa-trash"></em>
                                                            <la:message key="labels.design_delete_button"/>
                                                        </button>
                                                        <div class="modal fade"
                                                             id="confirmToDocumentDelete"
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
                                                                        <button type="submit"
                                                                                class="btn btn-outline-light"
                                                                                name="deleteDocumentWords"
                                                                                value="<la:message key="labels.crud_button_delete" />">
                                                                            <em class="fa fa-trash"></em>
                                                                            <la:message
                                                                                    key="labels.crud_button_delete"/>
                                                                        </button>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:if>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td><la:message key="labels.suggest_word_type_query"/></td>
                                                <td class="text-right">${f:h(queryWordsNum)}</td>
                                                <td class="text-center">
                                                    <c:if test="${editable}">
                                                        <button type="button" class="btn btn-danger btn-xs"
                                                                name="deleteQueryWords"
                                                                data-toggle="modal" data-target="#confirmToQueryDelete"
                                                                value="<la:message key="labels.design_delete_button" />">
                                                            <em class="fa fa-trash"></em>
                                                            <la:message key="labels.design_delete_button"/>
                                                        </button>
                                                        <div class="modal fade" id="confirmToQueryDelete"
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
                                                                        <button type="submit"
                                                                                class="btn btn-outline-light"
                                                                                name="deleteQueryWords"
                                                                                value="<la:message key="labels.crud_button_delete" />">
                                                                            <em class="fa fa-trash"></em>
                                                                            <la:message
                                                                                    key="labels.crud_button_delete"/>
                                                                        </button>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:if>
                                                </td>
                                            </tr>
                                            </tbody>
                                        </table>
                                    </la:form>
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
