<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message
            key="labels.elevate_word_configuration"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="suggest"/>
        <jsp:param name="menuType" value="elevateWord"/>
    </jsp:include>
    <div class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>
                            <la:message key="labels.elevate_word_title_details"/>
                        </h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><la:link href="list">
                                <la:message key="labels.elevate_word_link_list"/>
                            </la:link></li>
                            <li class="breadcrumb-item active"><la:message
                                    key="labels.elevate_word_link_details"/></li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <la:form action="/admin/elevateword/">
                <la:hidden property="crudMode"/>
                <c:if test="${crudMode==2 || crudMode==3 || crudMode==4}">
                    <la:hidden property="id"/>
                    <la:hidden property="versionNo"/>
                </c:if>
                <la:hidden property="createdBy"/>
                <la:hidden property="createdTime"/>
                <div class="row">
                    <div class="col-md-12">
                        <div
                                class="card card-outline <c:if test="${crudMode == 1 || crudMode == 2}">card-success</c:if><c:if test="${crudMode == 3}">card-danger</c:if><c:if test="${crudMode == 4}">card-primary</c:if>">
                            <div class="card-header">
                                <h3 class="card-title">
                                    <c:if test="${crudMode == 1}">
                                        <la:message key="labels.elevate_word_link_create"/>
                                    </c:if>
                                    <c:if test="${crudMode == 2}">
                                        <la:message key="labels.elevate_word_link_edit"/>
                                    </c:if>
                                    <c:if test="${crudMode == 3}">
                                        <la:message key="labels.elevate_word_link_delete"/>
                                    </c:if>
                                    <c:if test="${crudMode == 4}">
                                        <la:message key="labels.elevate_word_link_details"/>
                                    </c:if>
                                </h3>
                                <div class="card-tools">
                                    <div class="btn-group">
                                        <la:link href="/admin/elevateword"
                                                 styleClass="btn btn-default btn-xs">
                                            <em class="fa fa-th-list"></em>
                                            <la:message key="labels.elevate_word_link_list"/>
                                        </la:link>
                                        <la:link href="../createnew"
                                                 styleClass="btn btn-success btn-xs ${f:h(editableClass)}">
                                            <em class="fa fa-plus"></em>
                                            <la:message key="labels.elevate_word_link_create"/>
                                        </la:link>
                                        <la:link href="../downloadpage"
                                                 styleClass="btn btn-primary btn-xs">
                                            <em class="fa fa-download"></em>
                                            <la:message key="labels.elevate_word_link_download"/>
                                        </la:link>
                                        <la:link href="../uploadpage"
                                                 styleClass="btn btn-success btn-xs ${f:h(editableClass)}">
                                            <em class="fa fa-upload"></em>
                                            <la:message key="labels.elevate_word_link_upload"/>
                                        </la:link>
                                    </div>
                                </div>
                            </div>
                            <div class="card-body">
                                    <%-- Message --%>
                                <div>
                                    <la:info id="msg" message="true">
                                        <div class="alert alert-info">${msg}</div>
                                    </la:info>
                                    <la:errors/>
                                </div>
                                    <%-- Form Fields --%>
                                <table class="table table-bordered">
                                    <tbody>
                                    <tr>
                                        <th style="width: 25%"><la:message
                                                key="labels.elevate_word_suggest_word"/></th>
                                        <td>${f:h(suggestWord)}<la:hidden
                                                property="suggestWord"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.elevate_word_reading"/></th>
                                        <td>${f:h(reading)}<la:hidden property="reading"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.elevate_word_permissions"/></th>
                                        <td>${f:br(f:h(permissions))}<la:hidden
                                                property="permissions"/></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.label_type"/></th>
                                        <td><c:forEach var="l" varStatus="s"
                                                       items="${labelTypeItems}">
                                            <c:forEach var="ltid" varStatus="s"
                                                       items="${labelTypeIds}">
                                                <c:if test="${ltid==l.id}">
                                                    ${f:h(l.name)}<br/>
                                                </c:if>
                                            </c:forEach>
                                        </c:forEach></td>
                                    </tr>
                                    <tr>
                                        <th><la:message key="labels.elevate_word_boost"/></th>
                                        <td>${f:h(boost)}<la:hidden property="boost"/></td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="card-footer">
                                <jsp:include page="/WEB-INF/view/common/admin/crud/buttons.jsp"></jsp:include>
                            </div>
                        </div>
                    </div>
                </div>
            </la:form>
        </section>
    </div>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
</body>
</html>

