<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.suggest_word_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="suggest"/>
        <jsp:param name="menuType" value="suggestWord"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.suggest_word_title_details"/>
                        </h1>
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
                                <la:message key="labels.crud_title_list"/>
                            </h3>
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
                            <div class="fads-row">
                                <div class="fads-col-sm-12">
                                    <la:form action="/admin/suggest/">
                                        <table class="fads-table" aria-label="<la:message key="labels.suggest_word_list" />">
                                            <thead>
                                            <tr>
                                                <th style="width: 15%"><la:message key="labels.suggest_word_type"/></th>
                                                <th class="fads-text-center" style="width: 10%"><la:message
                                                        key="labels.suggest_word_number"/></th>
                                                <th class="fads-text-center" style="width: 20%"></th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <tr>
                                                <td><la:message key="labels.suggest_word_type_all"/></td>
                                                <td class="fads-text-right">${f:h(totalWordsNum)}</td>
                                                <td class="fads-text-center">
                                                    <c:if test="${editable}">
                                                        <button type="button" class="fads-btn fads-btn-danger fads-btn-compact"
                                                                name="deleteAllWords"
                                                                data-fads-dialog="confirmToAllDelete"
                                                                value="<la:message key="labels.design_delete_button" />">
                                                            <i class="fa fa-trash" aria-hidden="true"></i>
                                                            <la:message key="labels.design_delete_button"/>
                                                        </button>
                                                        <div class="fads-dialog-overlay" id="confirmToAllDelete"
                                                             tabindex="-1" role="dialog">
                                                            <div class="fads-dialog">
                                                                <div class="fads-dialog-danger">
                                                                    <div class="fads-dialog-header">
                                                                        <h4 class="">
                                                                            <la:message key="labels.crud_title_delete"/>
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
                                                                        <button type="submit"
                                                                                class="fads-btn fads-btn-outline-light"
                                                                                name="deleteAllWords"
                                                                                value="<la:message key="labels.crud_button_delete" />">
                                                                            <i class="fa fa-trash" aria-hidden="true"></i>
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
                                                <td class="fads-text-right">${f:h(documentWordsNum)}</td>
                                                <td class="fads-text-center">
                                                    <c:if test="${editable}">
                                                        <button type="button" class="fads-btn fads-btn-danger fads-btn-compact"
                                                                name="deleteDocumentWords"
                                                                
                                                                data-fads-dialog="confirmToDocumentDelete"
                                                                value="<la:message key="labels.design_delete_button" />">
                                                            <i class="fa fa-trash" aria-hidden="true"></i>
                                                            <la:message key="labels.design_delete_button"/>
                                                        </button>
                                                        <div class="fads-dialog-overlay"
                                                             id="confirmToDocumentDelete"
                                                             tabindex="-1" role="dialog">
                                                            <div class="fads-dialog">
                                                                <div class="fads-dialog-danger">
                                                                    <div class="fads-dialog-header">
                                                                        <h4 class="">
                                                                            <la:message key="labels.crud_title_delete"/>
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
                                                                        <button type="submit"
                                                                                class="fads-btn fads-btn-outline-light"
                                                                                name="deleteDocumentWords"
                                                                                value="<la:message key="labels.crud_button_delete" />">
                                                                            <i class="fa fa-trash" aria-hidden="true"></i>
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
                                                <td class="fads-text-right">${f:h(queryWordsNum)}</td>
                                                <td class="fads-text-center">
                                                    <c:if test="${editable}">
                                                        <button type="button" class="fads-btn fads-btn-danger fads-btn-compact"
                                                                name="deleteQueryWords"
                                                                data-fads-dialog="confirmToQueryDelete"
                                                                value="<la:message key="labels.design_delete_button" />">
                                                            <i class="fa fa-trash" aria-hidden="true"></i>
                                                            <la:message key="labels.design_delete_button"/>
                                                        </button>
                                                        <div class="fads-dialog-overlay" id="confirmToQueryDelete"
                                                             tabindex="-1" role="dialog">
                                                            <div class="fads-dialog">
                                                                <div class="fads-dialog-danger">
                                                                    <div class="fads-dialog-header">
                                                                        <h4 class="">
                                                                            <la:message key="labels.crud_title_delete"/>
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
                                                                        <button type="submit"
                                                                                class="fads-btn fads-btn-outline-light"
                                                                                name="deleteQueryWords"
                                                                                value="<la:message key="labels.crud_button_delete" />">
                                                                            <i class="fa fa-trash" aria-hidden="true"></i>
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
    </main>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
${fe:html(false)}
