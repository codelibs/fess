<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.search_list_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="fads-admin-layout">
<div class="fads-layout-wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="log"/>
        <jsp:param name="menuType" value="searchList"/>
    </jsp:include>
    <main class="fads-main-content">
        <div class="fads-page-header">
            <div >
                <div class="fads-d-flex fads-align-center" style="flex-wrap:wrap;gap:var(--ds-space-100)">
                    <div class="fads-col-sm-6">
                        <h1>
                            <la:message key="labels.search_list_configuration"/>
                        </h1>
                    </div>
                    <div class="fads-col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item active"><la:link href="/admin/searchlist">
                                <la:message key="labels.search_list_configuration"/>
                            </la:link></li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="fads-row">
                <div class="col-12">
                    <div class="fads-card">
                        <%-- Card Header --%>
                        <div class="fads-card-header">
                            <h3 class="fads-card-title">
                                <la:message key="labels.search_list_configuration"/>
                            </h3>
                            <div class="fads-card-tools">
                                <div class="btn-group">
                                    <la:link href="/admin/searchlist/createnew?q=${f:u(q)}"
                                             styleClass="fads-btn fads-btn-success fads-btn-compact ${f:h(editableClass)}">
                                        <i class="fa fa-plus" aria-hidden="true"></i>
                                        <la:message key="labels.crud_link_create"/>
                                    </la:link>
                                </div>
                            </div>
                        </div>
                        <%-- Card Body --%>
                        <div class="fads-card-body">
                            <%-- Message --%>
                            <div>
                                <la:info id="msg" message="true">
                                    <div class="fads-banner fads-banner-info">${msg}</div>
                                </la:info>
                                <la:errors/>
                            </div>
                            <la:form action="/admin/searchlist" styleClass="form-inline" method="GET">
                                <div class="form-group">
                                    <la:text styleClass="query fads-textfield" property="q"
                                             title="Search" size="50" maxlength="1000"
                                             placeholder="Type a search query"/>
                                </div>
                                <div class="form-group ml-sm-2">
                                    <button type="submit" class="fads-btn fads-btn-primary" id="submit"
                                            name="search" value="<la:message key="labels.search"/>">
                                        <i class="fa fa-search" aria-hidden="true"></i>
                                        <la:message key="labels.search"/>
                                    </button>
                                </div>
                            </la:form>
                            <%-- List --%>
                            <c:choose>
                                <c:when test="${allRecordCount == null}">
                                </c:when>
                                <c:when test="${f:h(allRecordCount) > 0}">
                                    <div id="subheader" class="fads-row top10">
                                        <div class="col-12">
                                            <c:if test="${allRecordCountRelation=='EQUAL_TO'}">
                                                <la:message key="labels.search_result_status"
                                                            arg0="${f:h(q)}" arg1="${f:h(allRecordCount)}"
                                                            arg2="${f:h(currentStartRecordNumber)}"
                                                            arg3="${f:h(currentEndRecordNumber)}"/>
                                            </c:if><c:if test="${allRecordCountRelation!='EQUAL_TO'}">
                                            <la:message key="labels.search_result_status_over"
                                                        arg0="${f:h(q)}" arg1="${f:h(allRecordCount)}"
                                                        arg2="${f:h(currentStartRecordNumber)}"
                                                        arg3="${f:h(currentEndRecordNumber)}"/>
                                        </c:if>
                                            <c:if test="${execTime!=null}">
                                                <la:message key="labels.search_result_time"
                                                            arg0="${f:h(execTime)}"/>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div id="result">
                                        <ol class="fads-row">
                                            <c:forEach var="doc" varStatus="s" items="${documentItems}">
                                                <li class="fads-col-sm-12">
                                                    <h3 class="title">
                                                        <a href="${doc.url_link}">${doc.content_title}</a>
                                                    </h3>
                                                    <div class="body col-sm-10">
                                                            ${doc.content_description}
                                                    </div>
                                                    <div class="body text-right">
                                                        <la:message key="labels.doc_score"/>${f:h(doc.score)}<br>
                                                        <c:if test="${editable}">
                                                            <la:link
                                                                    href="/admin/searchlist/edit?crudMode=2&amp;doc.doc_id=${f:u(doc.doc_id)}&amp;q=${f:u(q)}"
                                                                    styleClass="fads-btn fads-btn-primary fads-btn-compact">
                                                                <i class="fa fa-pencil-alt" aria-hidden="true"></i>
                                                                <la:message key="labels.crud_button_update"/>
                                                            </la:link>
                                                            <button type="button"
                                                                    class="fads-btn fads-btn-danger fads-btn-compact"
                                                                    data-fads-dialog="confirmToDelete"
                                                                    data-docid="${f:u(doc.doc_id)}"
                                                                    data-title="${fe:replace(doc.content_title, '<[^>]+>', '')}"
                                                                    data-url="${f:h(doc.url_link)}">
                                                                <i class="fa fa-trash" aria-hidden="true"></i>
                                                                <la:message key="labels.search_list_button_delete"/>
                                                            </button>
                                                        </c:if>
                                                    </div>
                                                </li>
                                            </c:forEach>
                                        </ol>
                                    </div>
                                    <div class="fads-row">
                                        <div class="col-sm-12 text-center">
                                            <ul class="pagination pagination-sm justify-content-center">
                                                <c:if test="${existPrevPage}">
                                                    <li class=""><la:link
                                                                                   styleClass=""
                                                                                   href="prev?q=${f:u(q)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}&labelTypeValue=${f:u(labelTypeValue)}">
                                                        <la:message key="labels.prev_page"/>
                                                    </la:link></li>
                                                </c:if>
                                                <c:if test="${!existPrevPage}">
                                                    <li class="disabled"><a class=""
                                                                                      href="#"><la:message
                                                            key="labels.prev_page"/></a></li>
                                                </c:if>
                                                <c:forEach var="pageNumber" varStatus="s"
                                                           items="${pageNumberList}">
                                                    <li
                                                            <c:if test="${pageNumber == currentPageNumber}">class="active"</c:if>>
                                                        <la:link
                                                                styleClass=""
                                                                href="move?q=${f:u(q)}&pn=${f:u(pageNumber)}&num=${f:u(pageSize)}&labelTypeValue=${f:u(labelTypeValue)}">${f:h(pageNumber)}</la:link>
                                                    </li>
                                                </c:forEach>
                                                <c:if test="${existNextPage}">
                                                    <li
                                                            class="page-item<c:if test="${!existNextPage}"> disabled</c:if>">
                                                        <la:link
                                                                styleClass=""
                                                                href="next?q=${f:u(q)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}&labelTypeValue=${f:u(labelTypeValue)}">
                                                            <la:message key="labels.next_page"/>
                                                        </la:link>
                                                    </li>
                                                </c:if>
                                                <c:if test="${!existNextPage}">
                                                    <li class="next disabled"><a
                                                            class=""
                                                            href="#"><la:message
                                                            key="labels.next_page"/></a></li>
                                                </c:if>
                                            </ul>
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div id="result" class="fads-row top10">
                                        <div class="fads-col-sm-12">
                                            <p class="callout callout-info">
                                                <la:message key="labels.did_not_match" arg0="${f:h(q)}"/>
                                            </p>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${editable}">
                                <div class="fads-dialog-overlay" id="confirmToDelete"
                                     tabindex="-1" role="dialog">
                                    <div class="fads-dialog">
                                        <div class="fads-dialog-danger">
                                            <div class="fads-dialog-header">
                                                <h4 class="">
                                                    <la:message key="labels.search_list_button_delete"/>
                                                </h4>
                                                <button type="button" class="close" data-fads-dialog-close
                                                        aria-label="Close">
                                                    <span aria-hidden="true">×</span>
                                                </button>
                                            </div>
                                            <div class="fads-dialog-body">
                                                <p>
                                                    <la:message key="labels.search_list_delete_confirmation"/>
                                                </p>
                                                <p>
                                                    <strong id="delete-doc-title"></strong><br/> <span
                                                        id="delete-doc-url"></span>
                                                </p>
                                            </div>
                                            <div class="fads-dialog-footer">
                                                <la:form action="/admin/searchlist/delete">
                                                    <button type="button" class="fads-btn fads-btn-outline-light"
                                                            data-fads-dialog-close>
                                                        <la:message key="labels.search_list_button_cancel"/>
                                                    </button>
                                                    <input type="hidden" name="docId" id="docId"/>
                                                    <button type="submit" class="fads-btn fads-btn-outline-light"
                                                            name="delete"
                                                            value="<la:message key="labels.search_list_button_delete" />">
                                                        <i class="fa fa-trash" aria-hidden="true"></i>
                                                        <la:message key="labels.search_list_button_delete"/>
                                                    </button>
                                                </la:form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                        <div class="fads-card-footer">
                            <c:if test="${f:h(allRecordCount) > 0}">
                                <div class="fads-row">
                                    <c:if test="${editable}">
                                        <div class="col-sm-12 center">
                                            <button type="button" class="fads-btn fads-btn-danger"
                                                    data-fads-dialog="confirmToDeleteAll">
                                                <i class="fa fa-trash" aria-hidden="true"></i>
                                                <la:message key="labels.search_list_button_delete_all"/>
                                            </button>
                                        </div>
                                        <div class="fads-dialog-overlay" id="confirmToDeleteAll"
                                             tabindex="-1" role="dialog">
                                            <div class="fads-dialog">
                                                <div class="fads-dialog-danger">
                                                    <div class="fads-dialog-header">
                                                        <h4 class="">
                                                            <la:message key="labels.search_list_button_delete_all"/>
                                                        </h4>
                                                        <button type="button" class="close" data-fads-dialog-close
                                                                aria-label="Close">
                                                            <span aria-hidden="true">×</span>
                                                        </button>
                                                    </div>
                                                    <div class="fads-dialog-body">
                                                        <p>
                                                            <la:message
                                                                    key="labels.search_list_delete_all_confirmation"/>
                                                        </p>
                                                    </div>
                                                    <la:form action="/admin/searchlist/deleteall">
                                                    <div class="fads-dialog-footer">
                                                            <la:hidden property="q"/>
                                                            <button type="button" class="fads-btn fads-btn-outline-light"
                                                                    data-fads-dialog-close>
                                                                <la:message key="labels.search_list_button_cancel"/>
                                                            </button>
                                                            <button type="submit" class="fads-btn fads-btn-outline-light"
                                                                    name="deleteall"
                                                                    value="<la:message key="labels.search_list_button_delete_all" />">
                                                                <i class="fa fa-trash" aria-hidden="true"></i>
                                                                <la:message key="labels.search_list_button_delete_all"/>
                                                            </button>
                                                    </div>
                                                    </la:form>
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
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
